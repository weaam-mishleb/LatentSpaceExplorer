package view;

import javafx.scene.SubScene;
import javafx.scene.layout.Pane;

/**
 * Handles 2D navigation by translating mouse drag gestures into panning movements,
 * and scroll gestures into zooming.
 */
public class MouseInteractionHandler2D {
    private final Pane mainPane;
    private double mouseAnchorX;
    private double mouseAnchorY;
    private double translateAnchorX;
    private double translateAnchorY;

    public MouseInteractionHandler2D(Pane mainPane) {
        this.mainPane = mainPane;
    }

    public void attachToScene(SubScene scene) {
        scene.setOnMousePressed(event -> {
            mouseAnchorX = event.getSceneX();
            mouseAnchorY = event.getSceneY();
            translateAnchorX = mainPane.getTranslateX();
            translateAnchorY = mainPane.getTranslateY();
        });
        scene.setOnMouseDragged(event -> {
            mainPane.setTranslateX(translateAnchorX + event.getSceneX() - mouseAnchorX);
            mainPane.setTranslateY(translateAnchorY + event.getSceneY() - mouseAnchorY);
        });

        scene.setOnScroll(event -> {
            double zoomFactor = 1.05;
            double deltaY = event.getDeltaY();
            if (deltaY < 0) {
                zoomFactor = 1 / zoomFactor;
            }

            double newScaleX = mainPane.getScaleX() * zoomFactor;
            double newScaleY = mainPane.getScaleY() * zoomFactor;
            if (newScaleX >= 0.1 && newScaleX <= 15.0) {
                mainPane.setScaleX(newScaleX);
                mainPane.setScaleY(newScaleY);
            }
        });
    }
}