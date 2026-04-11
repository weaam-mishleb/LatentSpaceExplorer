package controller;
import commands.*;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import logic.AnalysisService;
import math.CosineSimilarity;
import math.Distance;
import math.EuclideanDistance;
import model.DTO.SearchResult;
import model.VectorSpace;
import view.IRenderer;
import java.util.HashMap;
import java.util.Map;
/**
 * this class acts as the mediator between the UI (View) and the Business Logic (Model/Service).
 * ut translates user interactions into concrete Commands and manages the application state.
 */
public class MainController {
    private final AnalysisService service;
    private final CommandManager commandManager = new CommandManager();
    private IRenderer renderer;
    private final Label resultLabel;
    private final ToggleGroup metricGroup;
    private final Map<String, Distance> distanceMetrics = new HashMap<>();
    private String currentFocusWord = null;
    private ListView<String> neighborsListView = new ListView<>();

    public MainController(VectorSpace space, IRenderer renderer, Label resultLabel, ToggleGroup metricGroup) {
        distanceMetrics.put("cosine", new CosineSimilarity());
        distanceMetrics.put("euclidean", new EuclideanDistance());
        this.service = new AnalysisService(space, distanceMetrics.get("cosine"));
        this.renderer = renderer;
        this.resultLabel = resultLabel;
        this.metricGroup = metricGroup;
    }

    public void setRenderer(IRenderer newRenderer) {
        this.renderer = newRenderer;
    }

    public void undo() { commandManager.undo(); }
    public void redo() { commandManager.redo(); }

    public void zoomToWordAction(String word) {
        ZoomToWordCommand cmd = new ZoomToWordCommand(service, renderer, resultLabel, currentFocusWord, word);
        commandManager.executeCommand(cmd);
        currentFocusWord = cmd.getTargetWord();
    }

    public void computeGroupCentroid(String input, int k) {
        ComputeCentroidCommand cmd = new ComputeCentroidCommand(service, renderer, resultLabel, currentFocusWord, input, k);
        commandManager.executeCommand(cmd);
        currentFocusWord = cmd.getTargetWord();
    }

    public void showProjection(String start, String end) {
        ShowProjectionCommand cmd = new ShowProjectionCommand(service, renderer, resultLabel, currentFocusWord, start, end);
        commandManager.executeCommand(cmd);
        currentFocusWord = cmd.getTargetWord();
    }

    public void calculateAndShowDistance(String w1, String w2) {
        CalculateDistanceCommand cmd = new CalculateDistanceCommand(service, renderer, resultLabel, currentFocusWord, w1, w2);
        commandManager.executeCommand(cmd);
        currentFocusWord = cmd.getTargetWord();
    }

    public void updateMetricStrategy() {
        RadioButton selected = (RadioButton) metricGroup.getSelectedToggle();
        if (selected == null) return;
        String type = (String) selected.getUserData();
        Distance selectedMetric = distanceMetrics.get(type);

        if (selectedMetric != null) {
            service.setMetric(selectedMetric);
            resultLabel.setText("Strategy: " + selectedMetric.getName());
        }
        renderer.clearLines();
        renderer.resetSphereColors();
    }

    public void showNearestNeighbors(String targetWord) {
        FindNeighborsCommand cmd = new FindNeighborsCommand(service, renderer, resultLabel, currentFocusWord, targetWord, 5);
        commandManager.executeCommand(cmd);
        currentFocusWord = cmd.getTargetWord();
        java.util.List<SearchResult> results = cmd.getResults();
        if (results != null && !results.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (SearchResult result : results) {
                sb.append(String.format("%s -> %.3f\n", result.getWord(), result.getScore()));
            }
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Search Results");
            alert.setHeaderText("Nearest 5 Neighbors for: " + targetWord);
            alert.setContentText(sb.toString());
            alert.initModality(javafx.stage.Modality.NONE);
            alert.show();
        }
    }

    public void computeAnalogy(String a, String b, String c) {
        ComputeAnalogyCommand cmd = new ComputeAnalogyCommand(service, renderer, resultLabel, currentFocusWord, a, b, c);
        commandManager.executeCommand(cmd);
        currentFocusWord = cmd.getTargetWord();
    }
}