package commands;

import javafx.scene.control.Label;
import logic.AnalysisService;
import model.SearchResult;
import config.AppConstants;
import view.IRenderer;
//12
/**
 * It solves the classic vector math problem: A is to B as C is to X (A - B + C = X).
 */
public class ComputeAnalogyCommand extends AbstractVisualCommand {
    private final AnalysisService service;
    private final String wordA, wordB, wordC;
    public ComputeAnalogyCommand(AnalysisService service, IRenderer renderer, Label resultLabel, String prevWord, String a, String b, String c) {
        super(renderer, resultLabel, prevWord);
        this.service = service;
        this.wordA = a.trim();
        this.wordB = b.trim();
        this.wordC = c.trim();
    }

    /**
     * The specific execution hook called by the Template Method in AbstractVisualCommand.
     * Calculates the target vector, finds the closest word, and updates the 3D scene.
     */
    @Override
    protected void doExecute() {
        SearchResult result = service.computeAnalogy(wordA, wordB, wordC);
        if (result == null) {
            resultLabel.setText("Error computing analogy.");
            return;
        }

        String resultWord = result.getWord();
        this.targetWord = resultWord;

        renderer.highlightWord(wordA, AppConstants.COLOR_TARGET, AppConstants.RADIUS_CENTROID);
        renderer.highlightWord(wordB, AppConstants.COLOR_TARGET, AppConstants.RADIUS_CENTROID);
        renderer.highlightWord(wordC, AppConstants.COLOR_TARGET, AppConstants.RADIUS_CENTROID);
        renderer.highlightWord(resultWord, AppConstants.COLOR_ANALOGY_RESULT, AppConstants.RADIUS_ANALOGY_RESULT);

        renderer.drawLineBetweenWords(wordA, resultWord);
        renderer.drawLineBetweenWords(wordC, resultWord);

        renderer.zoomToWord(resultWord);

        resultLabel.setText(String.format("%s - %s + %s = %s", wordA, wordB, wordC, resultWord));
    }
}