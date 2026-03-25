package view;

import model.VectorSpace;

/**
 * Design Pattern:Facade
 * this interface defines the entry point and core interactions for the visualization layer.
 * it abstracts the display logic from the underlying data structures.
 */
public interface Visualizer {
    /**
     * initializes and starts the visualization environment.
     */
    void display(VectorSpace fullSpace, VectorSpace pcaSpace);

    /**
     * commands the UI to visually emphasize a specific word.
     * usually involves zooming the camera or changing the word's color/size.
     */
    void highlight(String word);
}