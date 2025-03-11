package com.risonna.stuff.figurecubestuff.controller;

import com.risonna.stuff.figurecubestuff.model.*;
import com.risonna.stuff.figurecubestuff.util.Notification;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.stage.DirectoryChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.io.*;
import java.util.Map;

public class MainController {

    @FXML
    private ComboBox<String> figureComboBox;

    @FXML
    private ComboBox<String> materialComboBox;

    @FXML
    private TextField heightField;

    @FXML
    private TextField baseField;

    @FXML
    private Canvas canvas;
    @FXML
    private Label deformedHeightLabel;
    @FXML
    private Label deformedBaseLabel;

    @FXML private VBox calculatorView;
    @FXML
    private Button calculateButton; // Add this field at the top with other FXML injected fields
    @FXML private VBox analysisView;
    @FXML private GridPane chartsGrid;
    private LineChart[] charts;
    private Session currentSession;

    @FXML
    public void initialize() {
        setupTransitions();
        setupCharts();
        setupInputHandlers();
    }


    private void setupInputHandlers() {
        // Only allow numbers and decimal point in text fields
        heightField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                heightField.setText(oldValue);
            }
        });

        baseField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                baseField.setText(oldValue);
            }
        });

        // Add enter key handler
        heightField.setOnAction(e -> baseField.requestFocus());
        baseField.setOnAction(e -> onCalculate());
    }

    private AbstractFigure createFigure(String figureType, double height, double base, TypeOfMetal material) {
        return switch (figureType) {
            case "Цилиндр" -> new Cylinder(height, base, material);
            case "Параллелепипед" -> new Parallelepiped(height, base, material);
            case "Конус" -> new Cone(height, base, material);
            case "Треугольник" -> new Triangle(height, base, material);
            default -> throw new IllegalArgumentException("Неизвестный тип фигуры: " + figureType);
        };
    }
    private void clearFields() {
        figureComboBox.setValue(null);
        materialComboBox.setValue(null);
        heightField.clear();
        baseField.clear();
        deformedHeightLabel.setText("Деформированная высота: ");
        deformedBaseLabel.setText("Деформированное основание: ");

        // Clear canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Clear charts if they're initialized
        if (charts != null) {
            for (LineChart<Number, Number> chart : charts) {
                chart.getData().clear();
            }
        }
    }
    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (figureComboBox.getValue() == null) {
            errorMessage.append("- Выберите фигуру\n");
        }

        if (materialComboBox.getValue() == null) {
            errorMessage.append("- Выберите материал\n");
        }

        try {
            if (heightField.getText().isEmpty()) {
                errorMessage.append("- Введите высоту\n");
            } else {
                double height = Double.parseDouble(heightField.getText());
                if (height <= 0) {
                    errorMessage.append("- Высота должна быть положительным числом\n");
                }
            }
        } catch (NumberFormatException e) {
            errorMessage.append("- Высота должна быть числом\n");
        }

        try {
            if (baseField.getText().isEmpty()) {
                errorMessage.append("- Введите основание\n");
            } else {
                double base = Double.parseDouble(baseField.getText());
                if (base <= 0) {
                    errorMessage.append("- Основание должно быть положительным числом\n");
                }
            }
        } catch (NumberFormatException e) {
            errorMessage.append("- Основание должно быть числом\n");
        }

        if (errorMessage.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка ввода");
            alert.setHeaderText("Исправьте следующие ошибки:");
            alert.setContentText(errorMessage.toString());
            alert.showAndWait();
            return false;
        }

        return true;
    }

    private void setupTransitions() {
        // Add hover effect for buttons
        calculateButton.setOnMouseEntered(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(200), calculateButton);
            ft.setFromValue(1.0);
            ft.setToValue(0.8);
            ft.play();
        });
        calculateButton.setOnMouseExited(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(200), calculateButton);
            ft.setFromValue(0.8);
            ft.setToValue(1.0);
            ft.play();
        });
    }

    private void setupCharts() {
        charts = new LineChart[4];
        String[] chartTitles = {
                "Деформация по высоте",
                "Деформация по основанию",
                "Зависимость от материала",
                "Сравнительный анализ"
        };

        for (int i = 0; i < 4; i++) {
            NumberAxis xAxis = new NumberAxis();
            NumberAxis yAxis = new NumberAxis();
            charts[i] = new LineChart<>(xAxis, yAxis);
            charts[i].setTitle(chartTitles[i]);
            charts[i].setAnimated(true);
            charts[i].getStyleClass().add("custom-chart");

            // Add to the grid
            chartsGrid.add(charts[i], i % 2, i / 2);
        }
    }

    // Menu handlers
    @FXML
    private void onCreateProject() {
        currentSession = null;
        clearFields();
        showNotification("Новый проект создан");
    }

    @FXML
    private void onOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть проект");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Session Files", "*.session"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                currentSession = (Session) ois.readObject();
                updateUIFromSession();
                showNotification("Проект загружен: " + file.getName());
            } catch (IOException | ClassNotFoundException e) {
                showNotification("Ошибка при загрузке проекта: " + e.getMessage());
            }
        }
    }
    private void saveCalculationsToDb() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            if (currentSession == null || currentSession.getCalculations().isEmpty()) {
                showNotification("Нет данных для сохранения в базу данных");
                return;
            }

            String url = "jdbc:mysql://localhost:3306/figure_calculations";
            String user = "root";
            String password = "gekz007";

            String query = "INSERT INTO calculations (figure_type, material, height, base, deformed_height, deformed_base) VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                for (Calculation calculation : currentSession.getCalculations()) {
                    pstmt.setString(1, calculation.getFigureType());
                    pstmt.setString(2, calculation.getMaterial().toString());
                    pstmt.setDouble(3, calculation.getHeight());
                    pstmt.setDouble(4, calculation.getBase());
                    pstmt.setDouble(5, calculation.getDeformedHeight());
                    pstmt.setDouble(6, calculation.getDeformedBase());
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
                showNotification("Данные сохранены в базу данных");
            } catch (SQLException e) {
                showNotification("Ошибка при сохранении в базу данных: " + e.getMessage());
            }
        }
        catch (ClassNotFoundException e) {
            showNotification("MySQL JDBC Driver not found: " + e.getMessage());
        }
    }

    private List<Calculation> fetchCalculationsFromDb() {
        List<Calculation> calculations = new ArrayList<>();
        String url = "jdbc:mysql://localhost:3306/figure_calculations";
        String user = "root";
        String password = "gekz007";

        String query = "SELECT figure_type, material, height, base, deformed_height, deformed_base, calculation_time FROM calculations";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String figureType = rs.getString("figure_type");
                TypeOfMetal material = TypeOfMetal.valueOf(rs.getString("material"));
                double height = rs.getDouble("height");
                double base = rs.getDouble("base");
                double deformedHeight = rs.getDouble("deformed_height");
                double deformedBase = rs.getDouble("deformed_base");

                calculations.add(new Calculation(figureType, material, height, base, deformedHeight, deformedBase));
            }
        } catch (SQLException e) {
            showNotification("Ошибка при загрузке данных из базы данных: " + e.getMessage());
        }

        return calculations;
    }

    private void updateUIFromSession() {
        if (currentSession != null) {
            var calculations = currentSession.getCalculations().getFirst();
            figureComboBox.setValue(calculations.getFigureType());
            materialComboBox.setValue(calculations.getMaterial().toString());
            heightField.setText(String.valueOf(calculations.getHeight()));
            baseField.setText(String.valueOf(calculations.getBase()));
            deformedHeightLabel.setText(String.format("Деформированная высота: %.2f", calculations.getDeformedHeight()));
            deformedBaseLabel.setText(String.format("Деформированное основание: %.2f", calculations.getDeformedBase()));

            // Redraw the figure
            onCalculate();
        }
    }

    @FXML
    private void onSaveProject() {
        if (currentSession == null) {
            showNotification("Нет данных для сохранения");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить проект");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Session Files", "*.session"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(currentSession);
                showNotification("Проект сохранен: " + file.getName());
            } catch (IOException e) {
                showNotification("Ошибка при сохранении проекта: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onSaveToDb() {
        try {
            System.out.println("Попытка сохранить в базу данных..."); // Console debug
            if (currentSession == null || currentSession.getCalculations().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Нет данных для сохранения");
                alert.setContentText("Нет данных для сохранения в базу данных.");
                alert.show();
                return;
            }
            saveCalculationsToDb();
        } catch (Exception e) {
            e.printStackTrace(); // Console stack trace
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка базы данных");
            alert.setHeaderText("Не удалось сохранить в базу данных");
            alert.setContentText("Ошибка: " + e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void onExit() {
        // Add exit confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Выход из программы");
        alert.setContentText("Вы уверены, что хотите выйти?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Platform.exit();
            }
        });
    }

    // In MainController.java
    @FXML
    private void onCalculate() {
        if (!validateInput()) {
            return;
        }

        try {
            String figureType = figureComboBox.getValue();
            TypeOfMetal material = TypeOfMetal.valueOf(materialComboBox.getValue());
            double height = Double.parseDouble(heightField.getText());
            double base = Double.parseDouble(baseField.getText());

            AbstractFigure figure = createFigure(figureType, height, base, material);
            figure.calc();
            figure.draw(canvas.getGraphicsContext2D(), 1);

            // Update labels with formatted numbers
            deformedHeightLabel.setText(String.format("Деформированная высота: %.2f", figure.getDeformHeight()));
            deformedBaseLabel.setText(String.format("Деформированное основание: %.2f", figure.getDeformOsnov()));

            // Add the current calculation to the session
            if (currentSession == null) {
                currentSession = new Session();
            }
            currentSession.addCalculation(figureType, material, height, base, figure.getDeformHeight(), figure.getDeformOsnov());

            showNotification("Расчет выполнен успешно");
        } catch (Exception e) {
            System.out.println("Ошибка при расчете " + e.getMessage());
        }
    }
    @FXML
    private void showCalculationView() {
        analysisView.setVisible(false);
        calculatorView.setVisible(true);
    }

    @FXML
    private void onAnalysisView() {
        try {
            System.out.println("Попытка показать окно анализа..."); // Console debug
            List<Calculation> calculations = fetchCalculationsFromDb();
            System.out.println("Получено " + calculations.size() + " расчетов"); // Debug count

            if (calculations.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Нет данных");
                alert.setHeaderText("Нет данных для анализа");
                alert.setContentText("В базе данных нет данных для анализа.");
                alert.show();
                return;
            }

            // Update charts with data
            updateCharts(calculations);

            // Show analysis view
            calculatorView.setVisible(false);
            analysisView.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace(); // Console stack trace
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка анализа");
            alert.setHeaderText("Не получилось отобразить анализ");
            alert.setContentText("Ошибка: " + e.getMessage());
            alert.show();
        }
    }

    private void updateCharts(List<Calculation> calculations) {
        // Clear all charts
        for (LineChart<Number, Number> chart : charts) {
            chart.getData().clear();
        }

        // Chart 1: Height deformation by material
        XYChart.Series<Number, Number> heightSeries = new XYChart.Series<>();
        heightSeries.setName("Деформация высоты");
        for (int i = 0; i < calculations.size(); i++) {
            Calculation calc = calculations.get(i);
            heightSeries.getData().add(new XYChart.Data<>(i, calc.getDeformedHeight()));
        }
        charts[0].getData().add(heightSeries);

        // Chart 2: Base deformation by material
        XYChart.Series<Number, Number> baseSeries = new XYChart.Series<>();
        baseSeries.setName("Деформация основания");
        for (int i = 0; i < calculations.size(); i++) {
            Calculation calc = calculations.get(i);
            baseSeries.getData().add(new XYChart.Data<>(i, calc.getDeformedBase()));
        }
        charts[1].getData().add(baseSeries);

        // Chart 3: Original vs Deformed Height
        XYChart.Series<Number, Number> originalVsDeformedSeries = new XYChart.Series<>();
        originalVsDeformedSeries.setName("Высота до/после");
        for (int i = 0; i < calculations.size(); i++) {
            Calculation calc = calculations.get(i);
            originalVsDeformedSeries.getData().add(new XYChart.Data<>(calc.getHeight(), calc.getDeformedHeight()));
        }
        charts[2].getData().add(originalVsDeformedSeries);

        // Chart 4: Material comparison
        XYChart.Series<Number, Number> materialSeries = new XYChart.Series<>();
        materialSeries.setName("По материалам");
        Map<TypeOfMetal, Double> avgDeformation = new HashMap<>();
        for (Calculation calc : calculations) {
            avgDeformation.merge(calc.getMaterial(),
                    (calc.getHeight() - calc.getDeformedHeight()) / calc.getHeight(),
                    Double::sum);
        }
        int i = 0;
        for (Map.Entry<TypeOfMetal, Double> entry : avgDeformation.entrySet()) {
            materialSeries.getData().add(new XYChart.Data<>(i++, entry.getValue()));
        }
        charts[3].getData().add(materialSeries);
    }

    private void showNotification(String message) {
        // Add this alert-based notification alongside or instead of the custom notification
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();

        // Keep the original notification if you want both
        Notification notification = new Notification(message);
        notification.show();
    }

    @FXML
    private void exportToExcel() {
        try {
            List<Calculation> calculations = fetchCalculationsFromDb();

            if (calculations.isEmpty()) {
                showNotification("Нет данных для выгрузки");
                return;
            }

            // Create directory chooser dialog
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Сохранить в Excel");
            File directory = directoryChooser.showDialog(null);

            if (directory == null) {
                return; // User canceled
            }

            // Create a new Excel workbook
            Workbook workbook = new XSSFWorkbook();

            // Main data sheet
            Sheet dataSheet = workbook.createSheet("Данные расчетов");

            // Create header row
            Row headerRow = dataSheet.createRow(0);
            String[] columns = {"Тип фигуры", "Материал", "Коэффициент материала", "Высота", "Основание",
                    "Деформированная высота", "Деформированное основание", "Коэффициент деформации высоты",
                    "Коэффициент деформации основания", "Дата"};

            // Style for headers
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Create headers
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Prepare arrays for correlation calculation
            double[] origHeights = new double[calculations.size()];
            double[] deformedHeights = new double[calculations.size()];
            double[] origBases = new double[calculations.size()];
            double[] deformedBases = new double[calculations.size()];
            double[] materialFactors = new double[calculations.size()];
            double[] heightDeformRatios = new double[calculations.size()];
            double[] baseDeformRatios = new double[calculations.size()];

            // Fill data rows
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < calculations.size(); i++) {
                Calculation calc = calculations.get(i);
                Row row = dataSheet.createRow(i + 1);

                // Store values for correlation
                origHeights[i] = calc.getHeight();
                deformedHeights[i] = calc.getDeformedHeight();
                origBases[i] = calc.getBase();
                deformedBases[i] = calc.getDeformedBase();
                materialFactors[i] = calc.getMaterial().getFactor();
                heightDeformRatios[i] = (calc.getHeight() - calc.getDeformedHeight()) / calc.getHeight();
                baseDeformRatios[i] = (calc.getBase() - calc.getDeformedBase()) / calc.getBase();

                // Set cell values
                row.createCell(0).setCellValue(calc.getFigureType());
                row.createCell(1).setCellValue(calc.getMaterial().toString());
                row.createCell(2).setCellValue(calc.getMaterial().getFactor());
                row.createCell(3).setCellValue(calc.getHeight());
                row.createCell(4).setCellValue(calc.getBase());
                row.createCell(5).setCellValue(calc.getDeformedHeight());
                row.createCell(6).setCellValue(calc.getDeformedBase());
                row.createCell(7).setCellValue(heightDeformRatios[i]);
                row.createCell(8).setCellValue(baseDeformRatios[i]);

                // Add calculation time if it's not null
                if (calc.getCalculationTime() != null) {
                    row.createCell(9).setCellValue(calc.getCalculationTime().format(formatter));
                }
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                dataSheet.autoSizeColumn(i);
            }

            // Create correlation sheet
            Sheet correlationSheet = workbook.createSheet("Корреляционный анализ");

            // Create correlation headers
            String[] correlationPairs = {
                    "Исходная высота vs Деформированная высота",
                    "Исходное основание vs Деформированное основание",
                    "Коэффициент материала vs Коэффициент деформации высоты",
                    "Коэффициент материала vs Коэффициент деформации основания",
                    "Исходная высота vs Коэффициент деформации высоты",
                    "Исходное основание vs Коэффициент деформации основания"
            };

            Row correlationHeaderRow = correlationSheet.createRow(0);
            Cell headerCell = correlationHeaderRow.createCell(0);
            headerCell.setCellValue("Корреляционные пары");
            headerCell.setCellStyle(headerStyle);

            Cell valueHeaderCell = correlationHeaderRow.createCell(1);
            valueHeaderCell.setCellValue("Коэффициент Корреляции");
            valueHeaderCell.setCellStyle(headerStyle);

            Cell interpretationHeaderCell = correlationHeaderRow.createCell(2);
            interpretationHeaderCell.setCellValue("Интерпретация");
            interpretationHeaderCell.setCellStyle(headerStyle);

            // Calculate correlations
            double[] correlations = {
                    calculateCorrelation(origHeights, deformedHeights),
                    calculateCorrelation(origBases, deformedBases),
                    calculateCorrelation(materialFactors, heightDeformRatios),
                    calculateCorrelation(materialFactors, baseDeformRatios),
                    calculateCorrelation(origHeights, heightDeformRatios),
                    calculateCorrelation(origBases, baseDeformRatios)
            };

            // Create styles for different correlation strengths
            CellStyle strongPosStyle = workbook.createCellStyle();
            strongPosStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            strongPosStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle strongNegStyle = workbook.createCellStyle();
            strongNegStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
            strongNegStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle moderateStyle = workbook.createCellStyle();
            moderateStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            moderateStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle weakStyle = workbook.createCellStyle();
            weakStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            weakStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Fill correlation data
            for (int i = 0; i < correlationPairs.length; i++) {
                Row row = correlationSheet.createRow(i + 1);
                row.createCell(0).setCellValue(correlationPairs[i]);

                Cell valueCell = row.createCell(1);
                valueCell.setCellValue(correlations[i]);

                Cell interpretationCell = row.createCell(2);
                double corrValue = Math.abs(correlations[i]);

                if (corrValue >= 0.7) {
                    interpretationCell.setCellValue("Сильная корреляция");
                    valueCell.setCellStyle(correlations[i] >= 0 ? strongPosStyle : strongNegStyle);
                } else if (corrValue >= 0.5) {
                    interpretationCell.setCellValue("Средняя корреляция");
                    valueCell.setCellStyle(moderateStyle);
                } else if (corrValue >= 0.3) {
                    interpretationCell.setCellValue("Слабая корреляция");
                    valueCell.setCellStyle(weakStyle);
                } else {
                    interpretationCell.setCellValue("Очень слабая или отсутствующая корреляция");
                }
            }

            // Add explanation row
            Row explanationRow = correlationSheet.createRow(correlationPairs.length + 2);
            Cell explanationCell = explanationRow.createCell(0);
            explanationCell.setCellValue("Пояснение: Коэффициент корреляции измеряет силу и направление линейной связи между двумя переменными.");

            Row explanationRow2 = correlationSheet.createRow(correlationPairs.length + 3);
            Cell explanationCell2 = explanationRow2.createCell(0);
            explanationCell2.setCellValue("Значения варьируются от -1 до 1. Значение близкое к 1 означает сильную положительную корреляцию, к -1 – сильную отрицательную, к 0 – отсутствие корреляции.");

            // Auto-size correlation sheet columns
            for (int i = 0; i < 3; i++) {
                correlationSheet.autoSizeColumn(i);
            }

            // Write the file
            String fileName = "deformation_data_" + java.time.LocalDate.now().toString() + ".xlsx";
            String filePath = Paths.get(directory.getAbsolutePath(), fileName).toString();

            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
                showNotification("Данные выгружены в " + filePath);
            }

            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
            showNotification("Ошибка выгрузки данных: " + e.getMessage());
        }
    }

    /**
     * Calculate Pearson correlation coefficient between two arrays
     */
    private double calculateCorrelation(double[] x, double[] y) {
        if (x.length != y.length || x.length < 2) {
            return 0; // Cannot calculate correlation with insufficient data
        }

        // Calculate means
        double meanX = 0;
        double meanY = 0;
        for (int i = 0; i < x.length; i++) {
            meanX += x[i];
            meanY += y[i];
        }
        meanX /= x.length;
        meanY /= y.length;

        // Calculate correlation
        double numerator = 0;
        double denominatorX = 0;
        double denominatorY = 0;

        for (int i = 0; i < x.length; i++) {
            double xDiff = x[i] - meanX;
            double yDiff = y[i] - meanY;
            numerator += xDiff * yDiff;
            denominatorX += xDiff * xDiff;
            denominatorY += yDiff * yDiff;
        }

        if (denominatorX == 0 || denominatorY == 0) {
            return 0; // Avoid division by zero
        }

        return numerator / (Math.sqrt(denominatorX) * Math.sqrt(denominatorY));
    }
}