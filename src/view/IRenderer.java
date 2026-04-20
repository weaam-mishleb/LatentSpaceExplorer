package view;

import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import model.DTO.ProjectionResult;
import model.VectorSpace;
import java.util.List;
import java.util.function.Consumer;

/**
 * This interface defines the rendering capabilities required for visual representation.
 * It decouples the visual logic from the specific JavaFX implementation (2D or 3D).
 */
public interface IRenderer {

    /** sets the callback for when a word node is clicked. */
    void setOnWordClicked(Consumer<String> onWordClicked);

    /** creates and returns the JavaFX SubScene containing the visualization. */
    SubScene createSubScene(double width, double height);

    /** initializes the visual space with nodes representing the word embeddings. */
    void initializeSpace(VectorSpace space);

    /** updates the coordinates of all nodes, allowing transitions between dimensions. */
    void updateAllPositions(int x, int y, int z, boolean is3DMode);

    /** returns the viewpoint/camera to its original starting position. */
    void resetCamera();

    /** moves the viewpoint to focus closely on a specific word's position. */
    void zoomToWord(String word);

    /** adjusts the viewpoint to fit two specific points/words into the view. */
    void focusOnTwoPoints(String w1, String w2);

    /** clears all drawn lines (relationships) from the scene. */
    void clearLines();

    /** resets all node (word) colors to their default state. */
    void resetSphereColors();

    /** highlights a specific word by changing its color and size. */
    void highlightWord(String word, Color c, double radius);

    /** draws a physical line between two word vectors to represent a relationship. */
    void drawLineBetweenWords(String word1, String word2);

    /** applies a color gradient to nodes based on their position on a projection axis. */
    void applyProjectionGradient(List<ProjectionResult> projection);
    /** dims all nodes except the provided list of active words to create a focus effect. */
    void dimAllExcept(List<String> activeWords);
}