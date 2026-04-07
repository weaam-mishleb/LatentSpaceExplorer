package view;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import model.DTO.ProjectionResult;
import model.VectorSpace;
import model.WordEmbedding;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SceneRenderer3D implements IRenderer {
    private final Group mainSceneRoot = new Group();
    private final Group worldRoot = new Group();
    private final Group linesGroup = new Group();
    private PerspectiveCamera camera;
    private final Group labelsGroup = new Group();
    private final Map<Sphere, WordEmbedding> sphereToWordMap = new HashMap<>();
    private final Map<String, Sphere> wordToSphereMap = new HashMap<>();

    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

    private static final double SCALE = 200.0;
    private boolean is3DMode = true;

    private final Label floatingLabel;
    private Consumer<String> onWordClicked;

    public SceneRenderer3D(Label floatingLabel) {
        this.floatingLabel = floatingLabel;
        worldRoot.getChildren().add(linesGroup);
        worldRoot.getChildren().add(labelsGroup);

        mainSceneRoot.getChildren().add(worldRoot);
        setupLighting();
    }

    public void setOnWordClicked(Consumer<String> onWordClicked) {
        this.onWordClicked = onWordClicked;
    }

    public SubScene createSubScene(double width, double height) {
        SubScene subScene = new SubScene(mainSceneRoot, width, height, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.WHITE);
        setupCamera(subScene);
        return subScene;
    }

    public void initializeSpace(VectorSpace space) {
        if (space != null) {
            for (WordEmbedding word : space.getAllEmbeddings()) {
                createSphereForWord(word);
            }
        }
    }

    private void setupLighting() {
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(-1000); light.setTranslateY(-1000); light.setTranslateZ(-2000);
        mainSceneRoot.getChildren().addAll(light, new AmbientLight(Color.rgb(150, 150, 150)));
    }

    private void setupCamera(SubScene scene) {
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1); camera.setFarClip(10000.0);
        resetCamera();
        Group cameraGroup = new Group(camera);

        mainSceneRoot.getChildren().add(cameraGroup);

        scene.setCamera(camera);

        MouseInteractionHandler3D interactionHandler = new MouseInteractionHandler3D(camera, worldRoot, rotateX, rotateY);
        interactionHandler.attachToScene(scene);
    }

    @Override
    public void resetCamera() {
        rotateX.setAngle(0);
        rotateY.setAngle(0);
        worldRoot.getTransforms().setAll(rotateX, rotateY);

        camera.setTranslateX(0);
        camera.setTranslateY(0);
        camera.setTranslateZ(-3500);

        clearLines();
        resetSphereColors();
    }

    private void createSphereForWord(WordEmbedding word) {
        Sphere sphere = new Sphere(4, 8);
        PhongMaterial material = new PhongMaterial(Color.web("#004488"));
        material.setSpecularColor(Color.LIGHTBLUE);
        sphere.setMaterial(material);

        sphereToWordMap.put(sphere, word);
        wordToSphereMap.put(word.getWord(), sphere);

        sphere.setTranslateX(word.getVector().getValueAt(0) * SCALE);
        sphere.setTranslateY(word.getVector().getValueAt(1) * SCALE);
        sphere.setTranslateZ(word.getVector().getValueAt(2) * SCALE);

        sphere.setOnMouseEntered(e -> {
            if (sphere.getRadius() < 5) highlightSphere(sphere, Color.ORANGE, 6);
            floatingLabel.setText(word.getWord());
            floatingLabel.setLayoutX(e.getSceneX() + 15);
            floatingLabel.setLayoutY(e.getSceneY() - 15);
            floatingLabel.setVisible(true);
            floatingLabel.toFront();
        });

        sphere.setOnMouseExited(e -> {
            if (sphere.getRadius() < 7) highlightSphere(sphere, Color.web("#004488"), 4);
            floatingLabel.setVisible(false);
        });

        sphere.setOnMouseClicked(e -> {
            if (e.isStillSincePress() && onWordClicked != null) {
                onWordClicked.accept(word.getWord());
                e.consume();
            }
        });

        worldRoot.getChildren().add(sphere);
    }

    public void updateAllPositions(int x, int y, int z, boolean is3DMode) {
        this.is3DMode = is3DMode;
        clearLines();
        for (Map.Entry<Sphere, WordEmbedding> entry : sphereToWordMap.entrySet()) {
            WordEmbedding word = entry.getValue();
            Sphere sphere = entry.getKey();

            sphere.setTranslateX(word.getVector().getValueAt(x) * SCALE);
            sphere.setTranslateY(word.getVector().getValueAt(y) * SCALE);
            sphere.setTranslateZ(is3DMode ? word.getVector().getValueAt(z) * SCALE : 0);
        }
        if (!is3DMode) {
            rotateX.setAngle(0); rotateY.setAngle(0);
        }
    }

    @Override
    public void zoomToWord(String word) {
        Sphere s = wordToSphereMap.get(word.trim());
        if (s != null) {
            camera.setTranslateX(s.getTranslateX());
            camera.setTranslateY(s.getTranslateY());
            camera.setTranslateZ(s.getTranslateZ() - 800);
        }
    }

    @Override
    public void focusOnTwoPoints(String word1, String word2) {
        Sphere s1 = wordToSphereMap.get(word1.trim());
        Sphere s2 = wordToSphereMap.get(word2.trim());
        if (s1 != null && s2 != null) {
            Point3D p1 = toPoint(s1);
            Point3D p2 = toPoint(s2);
            Point3D mid = p1.midpoint(p2);

            camera.setTranslateX(mid.getX());
            camera.setTranslateY(mid.getY());
            camera.setTranslateZ(mid.getZ() - 1000);
        }
    }

    @Override
    public void highlightWord(String word, Color c, double radius) {
        Sphere s = wordToSphereMap.get(word.trim());
        if (s != null) {
            highlightSphere(s, c, radius);
            javafx.scene.text.Text textLabel = new javafx.scene.text.Text(" " + word.trim() + " ");
            textLabel.setFill(Color.BLACK);
            textLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

            javafx.scene.shape.Rectangle background = new javafx.scene.shape.Rectangle();
            background.setFill(Color.rgb(255, 255, 255, 0.8));
            background.setStroke(c);
            background.setStrokeWidth(2);
            background.setArcWidth(10);
            background.setArcHeight(10);
            background.widthProperty().bind(textLabel.layoutBoundsProperty().map(b -> b.getWidth() + 8));
            background.heightProperty().bind(textLabel.layoutBoundsProperty().map(b -> b.getHeight() + 4));

            javafx.scene.layout.StackPane labelContainer = new javafx.scene.layout.StackPane(background, textLabel);
            labelContainer.setTranslateX(0);
            labelContainer.setTranslateY(0);
            labelContainer.setTranslateZ(0);

            javafx.scene.transform.Rotate invRotX = new javafx.scene.transform.Rotate(0, javafx.scene.transform.Rotate.X_AXIS);
            invRotX.angleProperty().bind(rotateX.angleProperty().multiply(-1));
            javafx.scene.transform.Rotate invRotY = new javafx.scene.transform.Rotate(0, javafx.scene.transform.Rotate.Y_AXIS);
            invRotY.angleProperty().bind(rotateY.angleProperty().multiply(-1));

            labelContainer.getTransforms().add(new javafx.scene.transform.Translate(s.getTranslateX(), s.getTranslateY(), s.getTranslateZ()));
            labelContainer.getTransforms().addAll(invRotY, invRotX);
            labelContainer.getTransforms().add(new javafx.scene.transform.Translate(radius + 10, -radius - 15, -1));

            labelsGroup.getChildren().add(labelContainer);
        }
    }

    private void highlightSphere(Sphere s, Color c, double r) {
        if (s == null) return;
        s.setRadius(r);
        s.setMaterial(new PhongMaterial(c));
    }
    @Override
    public void resetSphereColors() {
        PhongMaterial defaultMat = new PhongMaterial(Color.web("#004488"));
        defaultMat.setSpecularColor(Color.LIGHTBLUE);

        for (Sphere s : sphereToWordMap.keySet()) {
            s.setRadius(4);
            s.setMaterial(defaultMat);
        }
    }

    @Override
    public void dimAllExcept(List<String> activeWords) {
        PhongMaterial dimMaterial = new PhongMaterial(Color.web("#004488", 0.17));

        for (Map.Entry<Sphere, WordEmbedding> entry : sphereToWordMap.entrySet()) {
            Sphere currentSphere = entry.getKey();
            if (!activeWords.contains(entry.getValue().getWord())) {
                currentSphere.setMaterial(dimMaterial);
                currentSphere.setRadius(4);
            }
        }
    }

    @Override
    public void applyProjectionGradient(List<ProjectionResult> projection) {
        if (projection == null || projection.isEmpty()) return;

        int n = projection.size();
        for (int i = 0; i < n; i++) {
            ProjectionResult entry = projection.get(i);
            Sphere s = wordToSphereMap.get(entry.getWord());

            if (s != null) {
                double rankNormalized = (double) i / Math.max(1, (n - 1));

                double hue = 240.0 * (1.0 - rankNormalized);
                Color scaleColor = Color.hsb(hue, 1.0, 1.0);

                s.setMaterial(new PhongMaterial(scaleColor));
            }
        }
    }

    @Override
    public void drawLineBetweenWords(String w1, String w2) {
        Sphere s1 = wordToSphereMap.get(w1.trim());
        Sphere s2 = wordToSphereMap.get(w2.trim());
        if (s1 != null && s2 != null) {
            Cylinder line = createConnection(toPoint(s1), toPoint(s2));
            linesGroup.getChildren().add(line);
        }
    }

    @Override
    public void clearLines() {
        linesGroup.getChildren().clear();
        labelsGroup.getChildren().clear();
    }

    private Cylinder createConnection(Point3D origin, Point3D target) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();
        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        Cylinder line = new Cylinder(0.5, height);
        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);
        PhongMaterial mat = new PhongMaterial(Color.ORANGE);
        line.setMaterial(mat);
        return line;
    }

    private Point3D toPoint(Sphere s) {
        return new Point3D(s.getTranslateX(), s.getTranslateY(), s.getTranslateZ());
    }
}