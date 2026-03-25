package view;

import controller.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.VectorSpace;

public class JavaFX3DVisualizer extends Application implements Visualizer {
    private static VectorSpace fullSpace;
    private static VectorSpace pcaSpace;
    private SceneRenderer renderer;
    private MainController controller;

    private Label floatingLabel;
    private Label resultLabel;
    private ComboBox<Integer> xCombo, yCombo, zCombo;
    private ToggleButton toggle2D3D;
    private ToggleGroup metricGroup;

    @Override
    public void display(VectorSpace fullSpace, VectorSpace pcaSpace) {
        JavaFX3DVisualizer.fullSpace = fullSpace;
        JavaFX3DVisualizer.pcaSpace = pcaSpace;
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        AnchorPane mainLayout = new AnchorPane();

        createFloatingLabel(mainLayout);
        createResultOverlay(mainLayout);
        renderer = new SceneRenderer(floatingLabel);
        renderer.initializeSpace(pcaSpace);
        metricGroup = new ToggleGroup();
        controller = new MainController(fullSpace, renderer, resultLabel, metricGroup);
        renderer.setOnWordClicked(clickedWord -> {
            controller.executeWithHistory(() -> controller.showNearestNeighbors(clickedWord), clickedWord);
        });

        createUndoOverlay(mainLayout);

        SubScene subScene = renderer.createSubScene(1000, 800);
        Pane d3Container = new Pane(subScene);
        mainLayout.getChildren().add(d3Container);

        d3Container.toBack();
        subScene.widthProperty().bind(mainLayout.widthProperty());
        subScene.heightProperty().bind(mainLayout.heightProperty());

        TabPane bottomTabs = createBottomTabs();
        mainLayout.getChildren().add(bottomTabs);
        AnchorPane.setBottomAnchor(bottomTabs, 0.0);
        AnchorPane.setLeftAnchor(bottomTabs, 0.0);
        AnchorPane.setRightAnchor(bottomTabs, 0.0);

        Scene mainScene = new Scene(mainLayout, 1100, 750);
        primaryStage.setTitle("Latent Space Explorer");
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
                () -> controller.executeWithHistory(() -> controller.zoomToWordAction(txtSearch.getText()), txtSearch.getText()),
                () -> controller.executeWithHistory(() -> controller.showNearestNeighbors(txtSearch.getText()), txtSearch.getText()),
                () -> renderer.resetCamera(),
                toggle2D3D,
                this::updateAllPositions,
                xCombo, yCombo, zCombo,
                txtSearch
        );

        Tab mathTab = TabFactory.createMathTab(
                (w1, w2) -> controller.executeWithHistory(() -> controller.calculateAndShowDistance(w1, w2), w1),
                (a, b, c) -> controller.executeWithHistory(() -> controller.computeAnalogy(a, b, c), "Analogy"),
                (grp, k) -> controller.executeWithHistory(() -> controller.computeGroupCentroid(grp, k), "Centroid"),                (s, e) -> controller.executeWithHistory(() -> controller.showProjection(s, e), "Projection"),
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

    private void toggleDimensionMode() {
        boolean is3D = toggle2D3D.isSelected();
        toggle2D3D.setText(is3D ? "Mode: 3D" : "Mode: 2D");
        resultLabel.setText(is3D ? "Switched to 3D Mode" : "Switched to 2D Mode");
        renderer.updateAllPositions(xCombo.getValue(), yCombo.getValue(), zCombo.getValue(), is3D);
    }

    private void updateAllPositions() {
        renderer.updateAllPositions(xCombo.getValue(), yCombo.getValue(), zCombo.getValue(), toggle2D3D.isSelected());
    }

    @Override public void highlight(String word) { controller.zoomToWordAction(word); }
     public void updateView() {}
}