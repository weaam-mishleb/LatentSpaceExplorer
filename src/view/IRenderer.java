package view;

import javafx.scene.paint.Color;
import model.ProjectionResult;
import java.util.List;
//20
/**
 *this interface defines the rendering capabilities required for the 3D visualization.
 * it decouples the visual logic from the specific JavaFX implementation.
 */
public interface IRenderer {
    /** clears all drawn lines (relationships) from the 3D scene. */
    void clearLines();

    /** resets all sphere (word) colors to their default state. */
    void resetSphereColors();

    /** returns the 3D camera to its original starting position and orientation. */
    void resetCamera();

    /** highlights a specific word by changing its color and size in the 3D space. */
    void highlightWord(String word, Color c, double radius);

    /** moves the camera to focus closely on a specific word's vector position. */
    void zoomToWord(String word);

    /** draws a physical line between two word vectors to represent a relationship. */
    void drawLineBetweenWords(String word1, String word2);

    /** adjusts the camera to fit two specific points/words into the view. */
    void focusOnTwoPoints(String w1, String w2);

    /** applies a color gradient to spheres based on their position on a projection axis. */
    void applyProjectionGradient(List<ProjectionResult> projection);
}