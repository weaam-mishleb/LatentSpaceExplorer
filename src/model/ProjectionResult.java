package model;

/**
 * this class encapsulates the result of projecting a word's vector onto a specific axis or direction.
 */
public class ProjectionResult {
    private final String word;
    private final double projectionValue;

    /**
     * Constructs a new ProjectionResult.
     */
    public ProjectionResult(String word, double projectionValue) {
        this.word = word;
        this.projectionValue = projectionValue;
    }
    /**
     * return The word string.
     */
    public String getWord() {
        return word;
    }

    /**
     *return The scalar projection value.
     */
    public double getProjectionValue() {
        return projectionValue;
    }
}