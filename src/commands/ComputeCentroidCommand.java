package commands;

import javafx.scene.control.Label;
import logic.AnalysisService;
import model.SearchResult;
import model.WordEmbedding;
import config.AppConstants;
import view.IRenderer;

import java.util.Arrays;
import java.util.List;
//13
/**
 * this class calculates the geometric center (centroid) of a group of words,
 * and then visually finds and highlights the closest existing words to that theoretical point.
 */
public class ComputeCentroidCommand extends AbstractVisualCommand {
    private final AnalysisService service;
    private final String inputWords;
    private final int k;
    public ComputeCentroidCommand(AnalysisService service, IRenderer renderer, Label resultLabel, String prevWord, String input, int k) {
        super(renderer, resultLabel, prevWord);
        this.service = service;
        this.inputWords = input;
        this.k = k;
    }
    @Override
    protected void doExecute() {
        List<String> words = Arrays.asList(inputWords.split(","));
        WordEmbedding centroidVec = service.getCentroidVector(words);

        if (centroidVec != null) {
            for (String w : words) {
                renderer.highlightWord(w.trim(), AppConstants.COLOR_CENTROID, AppConstants.RADIUS_CENTROID);
            }
            List<SearchResult> nearest = service.getKNearestToVector(centroidVec, k, words);

            if (!nearest.isEmpty()) {
                String topClosest = nearest.get(0).getWord();
                this.targetWord = topClosest;
                renderer.highlightWord(topClosest, AppConstants.COLOR_TARGET, AppConstants.RADIUS_TARGET);
                renderer.zoomToWord(topClosest);
                for (SearchResult entry : nearest) {
                    String nWord = entry.getWord();
                    if (!nWord.equals(topClosest)) {
                        renderer.highlightWord(nWord, AppConstants.COLOR_NEIGHBOR, AppConstants.RADIUS_NEIGHBOR);
                    }
                    renderer.drawLineBetweenWords(topClosest, nWord);
                }
                resultLabel.setText(String.format("Centroid computed. Showing %d nearest neighbors.", k));
            } else {
                resultLabel.setText("Error: Centroid computed but no neighbors found.");
            }
        } else {
            resultLabel.setText("Error: Could not calculate centroid.");
        }
    }
}