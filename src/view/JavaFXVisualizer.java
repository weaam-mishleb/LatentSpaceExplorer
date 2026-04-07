package view;

import controller.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.VectorSpace;

import java.util.function.Consumer;

public class JavaFXVisualizer extends Application implements Visualizer {
    private static VectorSpace fullSpace;
    private static VectorSpace pcaSpace;
    private SceneRenderer3D renderer3D;
    private SceneRenderer2D renderer2D;
    private SubScene subScene3D;
    private SubScene subScene2D;
    private Pane viewContainer;
    private MainController controller;

    private Label floatingLabel;
    private Label resultLabel;
    private ComboBox<Integer> xCombo, yCombo, zCombo;
    private ToggleButton toggle2D3D;
    private ToggleGroup metricGroup;

    @Override
    public void display(VectorSpace fullSpace, VectorSpace pcaSpace) {
        JavaFXVisualizer.fullSpace = fullSpace;
        JavaFXVisualizer.pcaSpace = pcaSpace;
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        AnchorPane mainLayout = new AnchorPane();

        createFloatingLabel(mainLayout);
        createResultOverlay(mainLayout);
        metricGroup = new ToggleGroup();
        renderer3D = new SceneRenderer3D(floatingLabel);
        renderer3D.initializeSpace(pcaSpace);
        subScene3D = renderer3D.createSubScene(1000, 800);

        renderer2D = new SceneRenderer2D(floatingLabel);
        renderer2D.initializeSpace(pcaSpace);
        subScene2D = renderer2D.createSubScene(1000, 800);
        controller = new MainController(fullSpace, renderer3D, resultLabel, metricGroup);
        Consumer<String> onWordClicked = clickedWord -> controller.showNearestNeighbors(clickedWord);
        renderer3D.setOnWordClicked(onWordClicked);
        renderer2D.setOnWordClicked(onWordClicked);

        createUndoOverlay(mainLayout);
        viewContainer = new Pane(subScene3D); // Start with 3D
        mainLayout.getChildren().add(viewContainer);
        viewContainer.toBack();
        subScene3D.widthProperty().bind(mainLayout.widthProperty());
        subScene3D.heightProperty().bind(mainLayout.heightProperty());
        subScene2D.widthProperty().bind(mainLayout.widthProperty());
        subScene2D.heightProperty().bind(mainLayout.heightProperty());

        TabPane bottomTabs = createBottomTabs();
        mainLayout.getChildren().add(bottomTabs);
        AnchorPane.setBottomAnchor(bottomTabs, 0.0);
        AnchorPane.setLeftAnchor(bottomTabs, 0.0);
        AnchorPane.setRightAnchor(bottomTabs, 0.0);

        Scene mainScene = new Scene(mainLayout, 1100, 750);
        primaryStage.setTitle("Latent Space Explorer - 2D/3D Engine");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void createUndoOverlay(AnchorPane layout) {
        Button btnUndo = new Button("↩ Undo");
        btnUndo.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnUndo.setOnAction(e -> controller.undo());

        Button btnRedo = new Button("Redo ↪");
        btnRedo.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnRedo.setOnAction(e -> controller.redo());

        HBox undoRedoBox = new HBox(10, btnUndo, btnRedo);
        StackPane pane = new StackPane(undoRedoBox);
        AnchorPane.setTopAnchor(pane, 10.0);
        AnchorPane.setRightAnchor(pane, 10.0);
        layout.getChildren().add(pane);
    }

    private TabPane createBottomTabs() {
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: rgba(240, 240, 240, 0.95); -fx-background-radius: 5; -fx-tab-max-height: 25px;");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setMaxHeight(140);

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("Enter word...");
        toggle2D3D = new ToggleButton("Mode: 3D");
        toggle2D3D.setSelected(true);
        toggle2D3D.setStyle("-fx-base: #9C27B0; -fx-text-fill: white; -fx-font-weight: bold;");
        toggle2D3D.setOnAction(e -> toggleDimensionMode());

        xCombo = createCombo(0); yCombo = createCombo(1); zCombo = createCombo(2);
        Tab exploreTab = TabFactory.createExploreTab(
                () -> controller.zoomToWordAction(txtSearch.getText()),
                () -> controller.showNearestNeighbors(txtSearch.getText()),
                () -> {
                    renderer3D.resetCamera();
                    renderer2D.resetCamera();
                },
                toggle2D3D,
                this::updateAllPositions,
                xCombo, yCombo, zCombo,
                txtSearch
        );

        Tab mathTab = TabFactory.createMathTab(
                (w1, w2) -> controller.calculateAndShowDistance(w1, w2),
                (a, b, c) -> controller.computeAnalogy(a, b, c),
                (grp, k) -> controller.computeGroupCentroid(grp, k),
                (s, e) -> controller.showProjection(s, e),
                controller::updateMetricStrategy,
                metricGroup
        );

        tabPane.getTabs().addAll(exploreTab, mathTab);
        return tabPane;
    }

    private void createFloatingLabel(AnchorPane layout) {
        floatingLabel = new Label("");
        floatingLabel.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-padding: 5px; -fx-border-color: #333; -fx-border-radius: 5px;");
        floatingLabel.setVisible(false);
        layout.getChildren().add(floatingLabel);
    }

    private void createResultOverlay(AnchorPane layout) {
        resultLabel = new Label("Ready");
        resultLabel.setStyle("-fx-text-fill: white; -fx-background-color: rgba(0,0,0,0.7); -fx-padding: 10px; -fx-font-size: 16px;");
        StackPane topPane = new StackPane(resultLabel); topPane.setMaxHeight(40);
        AnchorPane.setTopAnchor(topPane, 10.0); AnchorPane.setLeftAnchor(topPane, 0.0); AnchorPane.setRightAnchor(topPane, 0.0);
        layout.getChildren().add(topPane);
    }

    private ComboBox<Integer> createCombo(int val) {
        ComboBox<Integer> box = new ComboBox<>();
        for (int i = 0; i < 50; i++) box.getItems().add(i);
        box.setValue(val);
        return box;
    }

    // THE MAGIC SWAP: Change view and inject new renderer to controller!
    private void toggleDimensionMode() {
        boolean is3D = toggle2D3D.isSelected();
        toggle2D3D.setText(is3D ? "Mode: 3D" : "Mode: 2D");
        resultLabel.setText(is3D ? "Switched to 3D Mode" : "Switched to 2D Mode");

        viewContainer.getChildren().clear();

        if (is3D) {
            controller.setRenderer(renderer3D);
            viewContainer.getChildren().add(subScene3D);
        } else {
            controller.setRenderer(renderer2D);
            viewContainer.getChildren().add(subScene2D);
        }

        updateAllPositions();
    }

    private void updateAllPositions() {
        boolean is3D = toggle2D3D.isSelected();
        if (is3D) {
            renderer3D.updateAllPositions(xCombo.getValue(), yCombo.getValue(), zCombo.getValue(), true);
        } else {
            renderer2D.updateAllPositions(xCombo.getValue(), yCombo.getValue(), zCombo.getValue(), false);
        }
    }

    @Override public void highlight(String word) { controller.zoomToWordAction(word); }
}