package commands;

import config.AppConstants;
import javafx.scene.control.Label;
import logic.AnalysisService;
import view.IRenderer;
//11
/**
 * This class encapsulates the specific action of calculating the distance
 * between two words and visualizing that relationship in the 3D space.
 */
public class CalculateDistanceCommand extends AbstractVisualCommand {
    private final AnalysisService service;
    private final String word1;
    private final String word2;

    public CalculateDistanceCommand(AnalysisService service, IRenderer renderer, Label resultLabel, String prevWord, String w1, String w2) {
        super(renderer, resultLabel, prevWord);
        this.service = service;
        this.word1 = w1.trim();
        this.word2 = w2.trim();
        this.targetWord = this.word1;
    }

    @Override
    protected void doExecute() {
        try {
            double dist = service.getDistance(word1, word2);
            renderer.highlightWord(word1, AppConstants.COLOR_CENTROID, AppConstants.RADIUS_NEIGHBOR);
            renderer.highlightWord(word2, AppConstants.COLOR_CENTROID, AppConstants.RADIUS_NEIGHBOR);
            renderer.drawLineBetweenWords(word1, word2);
            resultLabel.setText(String.format("Distance: %.4f", dist));
            renderer.focusOnTwoPoints(word1, word2);

        } catch (exceptions.WordNotFoundException e) {
            resultLabel.setText("Error: Words not found.");
        }
    }
    }