package commands;

import javafx.scene.control.Label;
import config.AppConstants;
import view.IRenderer;
import logic.AnalysisService;
import exceptions.WordNotFoundException;

//16
/**
 * this command handles spatial navigation within the 3D vector space.
 * it locates a specific word, highlights it, and moves the camera to focus on it.
 */
public class ZoomToWordCommand extends AbstractVisualCommand {
    private final String searchWord;
    private final AnalysisService service; // הוספנו את הסרוויס כדי שנוכל לבדוק נתונים

    /**
     * constructs the command to find and zoom into a specific word.
     */
    public ZoomToWordCommand(AnalysisService service, IRenderer renderer, Label resultLabel, String prevWord, String searchWord) {
        super(renderer, resultLabel, prevWord);
        this.service = service; // שומרים את הסרוויס
        this.searchWord = searchWord.trim();
        this.targetWord = this.searchWord;
    }

    /**
     * the specific execution hook called by the Template Method in AbstractVisualCommand.
     */
    @Override
    protected void doExecute() {
        try {
            service.findNearestNeighbors(searchWord, 1);
            renderer.highlightWord(searchWord, AppConstants.COLOR_TARGET, AppConstants.RADIUS_TARGET);
            renderer.zoomToWord(searchWord);
            resultLabel.setText("Found: " + searchWord);

        } catch (WordNotFoundException e) {
            resultLabel.setText("Error: Word '" + searchWord + "' not found.");
        }
    }
}