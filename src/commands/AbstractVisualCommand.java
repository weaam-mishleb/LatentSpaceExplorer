package commands;

import javafx.scene.control.Label;
import config.AppConstants;
import view.IRenderer;
//10
/**
 * this abstract class serves as the foundation for all visual actions in the system.
 * it centralizes common logic (like preparing the screen and undoing actions) to prevent
 */
public abstract class AbstractVisualCommand implements Command {
    protected final IRenderer renderer;
    protected final Label resultLabel;
    private final String prevWord;
    protected String targetWord;

    /**
     * constructs the base visual command.
     */
    public AbstractVisualCommand(IRenderer renderer, Label resultLabel, String prevWord) {
        this.renderer = renderer;
        this.resultLabel = resultLabel;
        this.prevWord = prevWord;
    }
    @Override
    public void execute() {
        renderer.clearLines();
        renderer.resetSphereColors();
        doExecute();
    }
    protected abstract void doExecute();
    @Override
    public void undo() {
        renderer.clearLines();
        renderer.resetSphereColors();
        if (prevWord != null) {
            renderer.highlightWord(prevWord, AppConstants.COLOR_TARGET, AppConstants.RADIUS_TARGET);
            renderer.zoomToWord(prevWord);
            resultLabel.setText("Undo: Back to " + prevWord);
        } else {
            renderer.resetCamera();
            resultLabel.setText("Undo: Reset view");
        }
    }
    public String getTargetWord() {
        return targetWord;
    }
}