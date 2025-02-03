package com.risonna.stuff.figurecubestuff.controller;

import com.risonna.stuff.figurecubestuff.model.*;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.canvas.GraphicsContext;

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

    @FXML
    private void onCalculate() {
        try {
            // Получаем выбранную фигуру
            String figureType = figureComboBox.getValue();

            // Получаем выбранный материал
            TypeOfMetal material = TypeOfMetal.valueOf(materialComboBox.getValue());

            // Получаем введенные пользователем значения
            double height = Double.parseDouble(heightField.getText());
            double base = Double.parseDouble(baseField.getText());

            // Создаем фигуру
            AbstractFigure figure = createFigure(figureType, height, base, material);

            // Вычисляем деформацию
            figure.calc();

            // Очищаем canvas перед отрисовкой
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            // Отрисовываем фигуру
            figure.draw(gc, 1);

            // Выводим результаты
            System.out.println("Деформированная высота: " + figure.getDeformHeight());
            System.out.println("Деформированное основание: " + figure.getDeformOsnov());
            deformedHeightLabel.setText("Деформированная высота: " + figure.getDeformHeight());
            deformedBaseLabel.setText("Деформированное основание: " + figure.getDeformOsnov());

        } catch (Exception e) {
            e.printStackTrace();
            // В реальном приложении здесь можно вывести сообщение об ошибке
        }
    }

    private AbstractFigure createFigure(String figureType, double height, double base, TypeOfMetal material) {
        switch (figureType) {
            case "Цилиндр":
                return new Cylinder(height, base, material);
            case "Параллелепипед":
                return new Parallelepiped(height, base, material);
            case "Конус":
                return new Cone(height, base, material);
            case "Треугольник":
                return new Triangle(height, base, material);
            default:
                throw new IllegalArgumentException("Неизвестный тип фигуры: " + figureType);
        }
    }
}