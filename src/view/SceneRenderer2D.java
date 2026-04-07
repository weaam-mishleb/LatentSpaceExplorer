package view;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import model.VectorSpace;
import model.WordEmbedding;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SceneRenderer2D implements IRenderer {
    private final Pane mainPane = new Pane();
    private final Group linesGroup = new Group();
    private final Group nodesGroup = new Group();
    private final Group labelsGroup = new Group();

    private final Map<Circle, WordEmbedding> circleToWordMap = new HashMap<>();
    private final Map<String, Circle> wordToCircleMap = new HashMap<>();

    private static final double SCALE = 200.0;
    private final Label floatingLabel;
    private Consumer<String> onWordClicked;

    private double sceneWidth = 800;
    private double sceneHeight = 600;

    public SceneRenderer2D(Label floatingLabel) {
        this.floatingLabel = floatingLabel;
        mainPane.getChildren().addAll(linesGroup, nodesGroup, labelsGroup);
    }

    @Override
    public void setOnWordClicked(Consumer<String> onWordClicked) {
        this.onWordClicked = onWordClicked;
    }

    @Override
    public SubScene createSubScene(double width, double height) {
        this.sceneWidth = width;
        this.sceneHeight = height;
        mainPane.setTranslateX(width / 2);
        mainPane.setTranslateY(height / 2);

        SubScene subScene = new SubScene(mainPane, width, height);
        MouseInteractionHandler2D interactionHandler = new MouseInteractionHandler2D(mainPane);
        interactionHandler.attachToScene(subScene);

        return subScene;
    }

    @Override
    public void initializeSpace(VectorSpace space) {
        if (space != null) {
            for (WordEmbedding word : space.getAllEmbeddings()) {
                createCircleForWord(word);
            }
        }
    }

    private void createCircleForWord(WordEmbedding word) {
        Circle circle = new Circle(4, Color.web("#004488"));
        circleToWordMap.put(circle, word);
        wordToCircleMap.put(word.getWord(), circle);

        circle.setCenterX(word.getVector().getValueAt(0) * SCALE);
        circle.setCenterY(word.getVector().getValueAt(1) * SCALE);

        circle.setOnMouseEntered(e -> {
            if (circle.getRadius() < 5) {
                circle.setRadius(6);
                circle.setFill(Color.ORANGE);
            }
            floatingLabel.setText(word.getWord());
            floatingLabel.setLayoutX(e.getSceneX() + 15);
            floatingLabel.setLayoutY(e.getSceneY() - 15);
            floatingLabel.setVisible(true);
            floatingLabel.toFront();
        });

        circle.setOnMouseExited(e -> {
            if (circle.getRadius() < 7) {
                circle.setRadius(4);
                circle.setFill(Color.web("#004488"));
            }
            floatingLabel.setVisible(false);
        });

        circle.setOnMouseClicked(e -> {
            if (e.isStillSincePress() && onWordClicked != null) {
                onWordClicked.accept(word.getWord());
                e.consume();
            }
        });

        nodesGroup.getChildren().add(circle);
    }

    @Override
    public void resetCamera() {
        mainPane.setTranslateX(sceneWidth / 2);
        mainPane.setTranslateY(sceneHeight / 2);
        mainPane.setScaleX(1.0);
        mainPane.setScaleY(1.0);
        clearLines();
        resetSphereColors();
    }

    @Override
    public void updateAllPositions(int x, int y, int z, boolean is3DMode) {
        clearLines();
        for (Map.Entry<Circle, WordEmbedding> entry : circleToWordMap.entrySet()) {
            WordEmbedding word = entry.getValue();
            Circle circle = entry.getKey();
            circle.setCenterX(word.getVector().getValueAt(x) * SCALE);
            circle.setCenterY(word.getVector().getValueAt(y) * SCALE);
        }
    }

    @Override
    public void zoomToWord(String word) {
        Circle c = wordToCircleMap.get(word.trim());
        if (c != null) {
            mainPane.setTranslateX(-c.getCenterX() + (sceneWidth / 2));
            mainPane.setTranslateY(-c.getCenterY() + (sceneHeight / 2));
            mainPane.setScaleX(2.0);
            mainPane.setScaleY(2.0);
        }
    }

    @Override
    public void focusOnTwoPoints(String word1, String word2) {
        Circle c1 = wordToCircleMap.get(word1.trim());
        Circle c2 = wordToCircleMap.get(word2.trim());
        if (c1 != null && c2 != null) {
            double midX = (c1.getCenterX() + c2.getCenterX()) / 2;
            double midY = (c1.getCenterY() + c2.getCenterY()) / 2;
            mainPane.setTranslateX(-midX + (sceneWidth / 2));
            mainPane.setTranslateY(-midY + (sceneHeight / 2));
            mainPane.setScaleX(1.5);
            mainPane.setScaleY(1.5);
        }
    }

    @Override
    public void highlightWord(String word, Color color, double radius) {
        Circle c = wordToCircleMap.get(word.trim());
        if (c != null) {
            c.setRadius(radius);
            c.setFill(color);

            Text textLabel = new Text(" " + word.trim() + " ");
            textLabel.setFill(Color.BLACK);
            textLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            Rectangle bg = new Rectangle();
            bg.setFill(Color.rgb(255, 255, 255, 0.8));
            bg.setStroke(color);
            bg.widthProperty().bind(textLabel.layoutBoundsProperty().map(b -> b.getWidth() + 4));
            bg.heightProperty().bind(textLabel.layoutBoundsProperty().map(b -> b.getHeight() + 2));

            StackPane container = new StackPane(bg, textLabel);
            container.setLayoutX(c.getCenterX() + radius + 5);
            container.setLayoutY(c.getCenterY() - radius - 15);
            labelsGroup.getChildren().add(container);
        }
    }

    @Override
    public void resetSphereColors() {
        for (Circle c : circleToWordMap.keySet()) {
            c.setRadius(4);
            c.setFill(Color.web("#004488"));
            c.setOpacity(1.0);
        }
    }

    @Override
    public void dimAllExcept(List<String> activeWords) {
        for (Map.Entry<Circle, WordEmbedding> entry : circleToWordMap.entrySet()) {
            if (!activeWords.contains(entry.getValue().getWord())) {
                entry.getKey().setOpacity(0.08);
            } else {
                entry.getKey().setOpacity(1.0);
            }
        }
    }

    @Override
    public void applyProjectionGradient(List<model.ProjectionResult> projection) {
        if (projection == null || projection.isEmpty()) return;

        int n = projection.size();
        for (int i = 0; i < n; i++) {
            model.ProjectionResult entry = projection.get(i);
            Circle c = wordToCircleMap.get(entry.getWord());

            if (c != null) {
                double rankNormalized = (double) i / Math.max(1, (n - 1));

                double hue = 240.0 * (1.0 - rankNormalized);
                c.setFill(Color.hsb(hue, 1.0, 1.0));
            }
        }
    }

    @Override
    public void drawLineBetweenWords(String w1, String w2) {
        Circle c1 = wordToCircleMap.get(w1.trim());
        Circle c2 = wordToCircleMap.get(w2.trim());
        if (c1 != null && c2 != null) {
            Line line = new Line(c1.getCenterX(), c1.getCenterY(), c2.getCenterX(), c2.getCenterY());
            line.setStroke(Color.ORANGE);
            line.setStrokeWidth(2);
            linesGroup.getChildren().add(line);
        }
    }

    @Override
    public void clearLines() {
        linesGroup.getChildren().clear();
        labelsGroup.getChildren().clear();
    }
}