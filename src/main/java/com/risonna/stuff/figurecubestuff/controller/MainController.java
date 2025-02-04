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
            System.out.println("Attempting to save to database..."); // Console debug
            if (currentSession == null || currentSession.getCalculations().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No Data to Save");
                alert.setContentText("There are no calculations to save to the database.");
                alert.show();
                return;
            }
            saveCalculationsToDb();
        } catch (Exception e) {
            e.printStackTrace(); // Console stack trace
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to Save to Database");
            alert.setContentText("Error: " + e.getMessage());
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
            System.out.println("Attempting to show analysis view..."); // Console debug
            List<Calculation> calculations = fetchCalculationsFromDb();
            System.out.println("Fetched " + calculations.size() + " calculations"); // Debug count

            if (calculations.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Data");
                alert.setHeaderText("No Data for Analysis");
                alert.setContentText("There are no calculations available in the database.");
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
            alert.setTitle("Analysis Error");
            alert.setHeaderText("Failed to Show Analysis");
            alert.setContentText("Error: " + e.getMessage());
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
}