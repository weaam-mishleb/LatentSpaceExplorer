package view;

import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.transform.Rotate;
/**
 * this class handles 3D navigation by translating mouse gestures into camera
 * movements (panning and zooming) and world transformations (rotation).
 */
public class MouseInteractionHandler {
    private final PerspectiveCamera camera;
    private final Group worldRoot;
    private final Rotate rotateX;
    private final Rotate rotateY;
    private double mouseOldX, mouseOldY;
    /**
     * constructs the handler with references to the 3D scene elements.
     */
    public MouseInteractionHandler(PerspectiveCamera camera, Group worldRoot, Rotate rotateX, Rotate rotateY) {
        this.camera = camera;
        this.worldRoot = worldRoot;
        this.rotateX = rotateX;
        this.rotateY = rotateY;
    }

    /**
     * attaches mouse and scroll event listeners to the given SubScene.
     */
    public void attachToScene(SubScene scene) {
        scene.setOnMousePressed(event -> {
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });

        scene.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - mouseOldX;
            double deltaY = event.getSceneY() - mouseOldY;
            if (event.isSecondaryButtonDown() || event.isMiddleButtonDown()) {
                camera.setTranslateX(camera.getTranslateX() - deltaX * 3.0);
                camera.setTranslateY(camera.getTranslateY() - deltaY * 3.0);
            }
            else {
                rotateY.setAngle(rotateY.getAngle() + deltaX * 0.4);
                rotateX.setAngle(rotateX.getAngle() - deltaY * 0.4);
                worldRoot.getTransforms().setAll(rotateX, rotateY);
            }
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });
        scene.setOnScroll(event -> {
            camera.setTranslateZ(camera.getTranslateZ() + event.getDeltaY() * 5);
        });
    }
}