package controller;

import commands.*;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import logic.AnalysisService;
import math.CosineSimilarity;
import math.Distance;
import math.EuclideanDistance;
import model.VectorSpace;
import view.IRenderer;

import java.util.HashMap;
import java.util.Map;
//21
/**
 * this class acts as the mediator between the UI (View) and the Business Logic (Model/Service).
 * it translates user interactions into concrete Commands and manages the application state.
 */
public class MainController {
    private final AnalysisService service;
    private final CommandManager commandManager = new CommandManager();
    private final IRenderer renderer;
    private final Label resultLabel;
    private final ToggleGroup metricGroup;
    private final Map<String, Distance> distanceMetrics = new HashMap<>();
    private String currentFocusWord = null;
    private ListView<String> neighborsListView = new ListView<>();

    /**
     * Initializes the controller, injecting dependencies and setting up the Strategy registry.
     */
    public MainController(VectorSpace space, IRenderer renderer, Label resultLabel, ToggleGroup metricGroup) {
        // Strategy Registry: Stores available distance calculation algorithms
        distanceMetrics.put("cosine", new CosineSimilarity());
        distanceMetrics.put("euclidean", new EuclideanDistance());
        this.service = new AnalysisService(space, distanceMetrics.get("cosine"));
        this.renderer = renderer;
        this.resultLabel = resultLabel;
        this.metricGroup = metricGroup;
    }

    /** triggers the Undo operation via the CommandManager. */
    public void undo() {
        commandManager.undo();
    }

    /** triggers the Redo operation via the CommandManager. */
    public void redo() {
        commandManager.redo();
    }

    /**
     * creates and executes an anonymous command for lightweight actions.
     * this is useful for UI-only actions that don't require a dedicated Command class.
     */
    public void executeWithHistory(Runnable action, String wordToFocus) {
        String prevWord = currentFocusWord;
        Command cmd = new Command() {
            @Override
            public void execute() {
                action.run();
                currentFocusWord = wordToFocus;
            }

            @Override
            public void undo() {
                renderer.clearLines();
                renderer.resetSphereColors();
                if (prevWord != null) {
                    renderer.highlightWord(prevWord, Color.CYAN, 12);
                    renderer.zoomToWord(prevWord);
                    resultLabel.setText("Undo: Back to " + prevWord);
                } else {
                    renderer.resetCamera();
                    resultLabel.setText("Undo: Reset view");
                }
                currentFocusWord = prevWord;
            }
        };
        commandManager.executeCommand(cmd);
    }

    /** executes a dedicated command to zoom to a specific word. */
    public void zoomToWordAction(String word) {
        ZoomToWordCommand cmd = new ZoomToWordCommand(service, renderer, resultLabel, currentFocusWord, word);
        commandManager.executeCommand(cmd);
        currentFocusWord = cmd.getTargetWord();
    }

    /** executes a dedicated command to compute and display a group centroid. */
    public void computeGroupCentroid(String input, int k) {
        ComputeCentroidCommand cmd = new ComputeCentroidCommand(service, renderer, resultLabel, currentFocusWord, input, k);
        commandManager.executeCommand(cmd);
        currentFocusWord = cmd.getTargetWord();
    }

    /** executes a dedicated command to show word projections along a custom axis. */
    public void showProjection(String start, String end) {
        ShowProjectionCommand cmd = new ShowProjectionCommand(service, renderer, resultLabel, currentFocusWord, start, end);
        commandManager.executeCommand(cmd);
        currentFocusWord = cmd.getTargetWord();
    }

    /** executes a dedicated command to calculate the distance between two words. */
    public void calculateAndShowDistance(String w1, String w2) {
        CalculateDistanceCommand cmd = new CalculateDistanceCommand(service, renderer, resultLabel, currentFocusWord, w1, w2);
        commandManager.executeCommand(cmd);
        currentFocusWord = cmd.getTargetWord();
    }

    /**
     * updates the active distance calculation strategy dynamically based on user UI selection.
     */
    public void updateMetricStrategy() {
        RadioButton selected = (RadioButton) metricGroup.getSelectedToggle();
        if (selected == null) return;

        String type = (String) selected.getUserData();
        Distance selectedMetric = distanceMetrics.get(type);

        if (selectedMetric != null) {
            // Strategy Pattern: Swapping the algorithm at runtime
            service.setMetric(selectedMetric);
            resultLabel.setText("Strategy: " + selectedMetric.getName());
        }

        renderer.clearLines();
        renderer.resetSphereColors();
    }

    /** executes a dedicated command to find and display the nearest neighbors of a word. */
    public void showNearestNeighbors(String targetWord) {
        FindNeighborsCommand cmd = new FindNeighborsCommand(
                service, renderer, resultLabel, currentFocusWord, targetWord, 5
        );
        commandManager.executeCommand(cmd);
        currentFocusWord = cmd.getTargetWord();

        java.util.List<model.SearchResult> results = cmd.getResults();

        if (results != null && !results.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (model.SearchResult result : results) {
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
    /** executes a dedicated command to compute and display a word analogy. */
    public void computeAnalogy(String a, String b, String c) {
        ComputeAnalogyCommand cmd = new ComputeAnalogyCommand(
                service, renderer, resultLabel, currentFocusWord, a, b, c
        );
        commandManager.executeCommand(cmd);
        currentFocusWord = cmd.getTargetWord();
    }

}