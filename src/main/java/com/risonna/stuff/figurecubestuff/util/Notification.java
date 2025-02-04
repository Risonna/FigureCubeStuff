package com.risonna.stuff.figurecubestuff.util;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Notification {
    private final Popup popup;
    private final Timeline timeline;

    public Notification(String message) {
        popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(true);

        Label label = new Label(message);
        label.getStyleClass().add("notification");
        StackPane pane = new StackPane(label);
        pane.getStyleClass().add("notification-pane");
        popup.getContent().add(pane);

        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(popup.opacityProperty(), 0.0)),
                new KeyFrame(Duration.millis(500), new KeyValue(popup.opacityProperty(), 1.0)),
                new KeyFrame(Duration.millis(2500), new KeyValue(popup.opacityProperty(), 1.0)),
                new KeyFrame(Duration.millis(3000), new KeyValue(popup.opacityProperty(), 0.0))
        );
    }

    public void show() {
        Stage stage = (Stage) popup.getOwnerWindow();
        if (stage != null) {
            popup.setX(stage.getX() + stage.getWidth()/2 - popup.getWidth()/2);
            popup.setY(stage.getY() + stage.getHeight() - 100);
            popup.show(stage);
            timeline.play();
            timeline.setOnFinished(e -> popup.hide());
        }
    }
}