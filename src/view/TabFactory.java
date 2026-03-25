package view;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
/**
 * Design Pattern:Factory Method
 * This class centralizes the creation of UI components (Tabs).
 * It uses functional interfaces to decouple the view from the controller logic.
 */
public class TabFactory {
    public interface SearchAction { void execute(String query); }
    public interface AnalogyAction { void execute(String a, String b, String c); }
    public interface DistanceAction { void execute(String w1, String w2); }
    public interface GroupAction { void execute(String wordsList, int k); }
    public interface ProjectionAction { void execute(String start, String end); }
    /**
     * Creates the "Explore" tab which focuses on navigation, searching, and 3D axis control.
     * return A configured Tab for spatial exploration.
     */
    public static Tab createExploreTab(
            Runnable onFind,
            Runnable onNeighbors,
            Runnable onReset,
            ToggleButton toggle2D3D,
            Runnable onUpdateAxes,
            ComboBox<Integer> x, ComboBox<Integer> y, ComboBox<Integer> z,
            TextField txtSearchReference) {
        Tab tab = new Tab("Explore");
        VBox container = new VBox(5);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new javafx.geometry.Insets(5));
        // Row 1: Word Search and Navigation
        HBox searchRow = new HBox(8);
        searchRow.setAlignment(Pos.CENTER);
        txtSearchReference.setPrefWidth(120);
        Button btnFind = new Button("Find & Zoom");
        Button btnNeighbors = new Button("Show Neighbors");
        Button btnReset = new Button("Reset View");
        // Event Binding: UI buttons trigger logic via provided Runnables
        btnFind.setOnAction(e -> onFind.run());
        btnNeighbors.setOnAction(e -> onNeighbors.run());
        btnReset.setOnAction(e -> onReset.run());
        searchRow.getChildren().addAll(new Label("Search:"), txtSearchReference, btnFind, btnNeighbors, btnReset, new Separator(javafx.geometry.Orientation.VERTICAL), toggle2D3D);
        // Row 2: Vector Dimension Control (Axes Selection)
        HBox axesRow = new HBox(10);
        axesRow.setAlignment(Pos.CENTER);
        Button btnUpdate = new Button("Update Axes");
        btnUpdate.setOnAction(e -> onUpdateAxes.run());
        axesRow.getChildren().addAll(new Label("X:"), x, new Label("Y:"), y, new Label("Z:"), z, btnUpdate);
        container.getChildren().addAll(searchRow, axesRow);
        tab.setContent(container);
        return tab;
    }

    /**
     * Creates the "Math & Analysis" tab for complex operations like analogies and projections.
     * return A configured Tab for mathematical vector analysis.
     */
    public static Tab createMathTab(
            DistanceAction onDist,
            AnalogyAction onAnalogy,
            GroupAction onGroup,
            ProjectionAction onProject,
            Runnable onMetricChange,
            ToggleGroup metricGroup
    ) {
        Tab tab = new Tab("Math & Analysis");
        VBox container = new VBox(5);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new javafx.geometry.Insets(5));

        // Section 1: Distance Calculation & Strategy Toggle
        HBox row1 = new HBox(8);
        row1.setAlignment(Pos.CENTER);
        RadioButton rbCosine = new RadioButton("Cos"); rbCosine.setUserData("cosine"); rbCosine.setToggleGroup(metricGroup); rbCosine.setSelected(true);
        RadioButton rbEuc = new RadioButton("Euc"); rbEuc.setUserData("euclidean"); rbEuc.setToggleGroup(metricGroup);

        // Strategy Pattern in action: Swapping algorithms via RadioButtons
        rbCosine.setOnAction(e -> onMetricChange.run());
        rbEuc.setOnAction(e -> onMetricChange.run());

        TextField w1 = new TextField(); w1.setPromptText("word 1"); w1.setPrefWidth(70);
        TextField w2 = new TextField(); w2.setPromptText("word 2"); w2.setPrefWidth(70);
        Button btnDist = new Button("Calc Distance");
        btnDist.setOnAction(e -> onDist.execute(w1.getText(), w2.getText()));
        row1.getChildren().addAll(rbCosine, rbEuc, new Separator(javafx.geometry.Orientation.VERTICAL), w1, w2, btnDist);

        // Section 2: Vector Analogies and Centroid Calculation
        HBox row2 = new HBox(8);
        row2.setAlignment(Pos.CENTER);

        TextField a = new TextField(); a.setPromptText("word 1"); a.setPrefWidth(60);
        TextField b = new TextField(); b.setPromptText("word 2"); b.setPrefWidth(60);
        TextField c = new TextField(); c.setPromptText("word 3"); c.setPrefWidth(60);
        Button btnAnalogy = new Button("Compute Analogy");
        btnAnalogy.setOnAction(e -> onAnalogy.execute(a.getText(), b.getText(), c.getText()));

        TextField txtGroup = new TextField(); txtGroup.setPromptText("word1,word2,..."); txtGroup.setPrefWidth(95);
        TextField txtK = new TextField(); txtK.setPromptText("K=3"); txtK.setPrefWidth(45);
        Button btnGroup = new Button("Centroid");

        btnGroup.setOnAction(e -> {
            int k = 5; // Default K value
            try { if (!txtK.getText().isEmpty()) k = Integer.parseInt(txtK.getText()); }
            catch(NumberFormatException ex) { /* Fallback to default */ }
            onGroup.execute(txtGroup.getText(), k);
        });

        row2.getChildren().addAll(a, new Label("-"), b, new Label("+"), c, btnAnalogy, new Separator(javafx.geometry.Orientation.VERTICAL), txtGroup, txtK, btnGroup);

        // Section 3: Custom Axis Projection
        HBox row3 = new HBox(8);
        row3.setAlignment(Pos.CENTER);
        TextField pStart = new TextField(); pStart.setPromptText("start"); pStart.setPrefWidth(80);
        TextField pEnd = new TextField(); pEnd.setPromptText("end"); pEnd.setPrefWidth(80);
        Button btnProject = new Button("Project Axis");
        btnProject.setOnAction(e -> onProject.execute(pStart.getText(), pEnd.getText()));
        row3.getChildren().addAll(new Label("Custom Axis:"), pStart, new Label("->"), pEnd, btnProject);

        container.getChildren().addAll(row1, new Separator(), row2, new Separator(), row3);
        tab.setContent(container);
        return tab;
    }
}