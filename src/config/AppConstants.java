package config;

import javafx.scene.paint.Color;

/**
 * This class acts as the Single Source of Truth for visual constants in the application.
 * By centralizing colors and sizes, it ensures UI consistency and makes future
 * theme changes incredibly easy to implement without modifying core logic.
 */
public class AppConstants {

    /** Color used for the primary searched/focused word. */
    public static final Color COLOR_TARGET = Color.CYAN;

    /** Color used for words that are near the target (KNN results). */
    public static final Color COLOR_NEIGHBOR = Color.LIMEGREEN;

    /** Highlight color for the final computed answer in an analogy (A - B + C = X). */
    public static final Color COLOR_ANALOGY_RESULT = Color.GOLD;

    /** Color for the theoretical center point or the input words of a centroid calculation. */
    public static final Color COLOR_CENTROID = Color.ORANGE;

    /** Color for the anchor words defining a projection axis. */
    public static final Color COLOR_AXIS = Color.MAGENTA;


    /** The size of the primary target word (large to draw focus). */
    public static final int RADIUS_TARGET = 16;

    /** The size of neighboring words in a cluster. */
    public static final int RADIUS_NEIGHBOR = 12;

    /** The size of words used to calculate a mathematical centroid. */
    public static final int RADIUS_CENTROID = 10;

    /** The size of the analogy result (largest, to stand out completely). */
    public static final int RADIUS_ANALOGY_RESULT = 18;

    /** The size of the anchor words defining the ends of a projection axis. */
    public static final int RADIUS_AXIS = 14;
}