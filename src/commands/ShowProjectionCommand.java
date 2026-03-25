package commands;

import exceptions.WordNotFoundException;
import javafx.scene.control.Label;
import logic.AnalysisService;
import model.ProjectionResult;
import config.AppConstants;
import view.IRenderer;
import java.util.List;
//15
/**
 * Design Pattern: Command
 * This class handles the mathematical projection of the vector space onto a specific semantic axis.
 * it visualizes how words distribute along a spectrum defined by a start word and an end word.
 */
public class ShowProjectionCommand extends AbstractVisualCommand {
    private final AnalysisService service;
    private final String startWord, endWord;
    public ShowProjectionCommand(AnalysisService service, IRenderer renderer, Label resultLabel, String prevWord, String start, String end) {
        super(renderer, resultLabel, prevWord);
        this.service = service;
        this.startWord = start.trim();
        this.endWord = end.trim();

        this.targetWord = startWord;
    }
    @Override
    protected void doExecute() {
        renderer.highlightWord(startWord, AppConstants.COLOR_AXIS, AppConstants.RADIUS_AXIS);
        renderer.highlightWord(endWord, AppConstants.COLOR_AXIS, AppConstants.RADIUS_AXIS);
        renderer.drawLineBetweenWords(startWord, endWord);
        try {
            List<ProjectionResult> projection = service.projectWordsOnAxis(startWord, endWord, 5000);
            if(!projection.isEmpty()) {
                renderer.applyProjectionGradient(projection);
                resultLabel.setText("Projection: " + startWord + " -> " + endWord);
                renderer.focusOnTwoPoints(startWord, endWord);
            }
        } catch (WordNotFoundException ex) {
            resultLabel.setText("Error: " + ex.getMessage());
        }
    }
}