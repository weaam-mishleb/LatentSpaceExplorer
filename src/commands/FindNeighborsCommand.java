package commands;

import exceptions.WordNotFoundException;
import javafx.scene.control.Label;
import logic.AnalysisService;
import model.SearchResult;
import config.AppConstants;
import view.IRenderer;

import java.util.List;

/**
 * this command executes a K-Nearest Neighbors (KNN) search for a specific word
 * and visualizes the results by drawing a local cluster/graph in the 3D space.
 */
public class FindNeighborsCommand extends AbstractVisualCommand {
    private final AnalysisService service;
    private final String searchWord;
    private final int k;

    // ADDED: Class field to store the results
    private List<SearchResult> results;

    public FindNeighborsCommand(AnalysisService service, IRenderer renderer, Label resultLabel, String prevWord, String searchWord, int k) {
        super(renderer, resultLabel, prevWord);
        this.service = service;
        this.searchWord = searchWord.trim();

        this.targetWord = this.searchWord;
        this.k = k;
    }

    @Override
    protected void doExecute() {
        try {
            // CHANGED: Saving the results directly to the class field
            this.results = service.findNearestNeighbors(searchWord, k);

            if (this.results.isEmpty()) {
                resultLabel.setText("Word not found.");
                return;
            }

            renderer.highlightWord(searchWord, AppConstants.COLOR_ANALOGY_RESULT, AppConstants.RADIUS_TARGET);

            for (SearchResult entry : this.results) {
                String neighborWord = entry.getWord();
                renderer.highlightWord(neighborWord, AppConstants.COLOR_NEIGHBOR, AppConstants.RADIUS_NEIGHBOR);
                renderer.drawLineBetweenWords(searchWord, neighborWord);
            }
            renderer.zoomToWord(searchWord);
            resultLabel.setText("Showing " + k + " neighbors for: " + searchWord);

        } catch (WordNotFoundException ex) {
            resultLabel.setText(ex.getMessage());
        }
    }

    // ADDED: Getter so the Controller can pull the data for the Alert popup
    public List<SearchResult> getResults() {
        return this.results;
    }
}