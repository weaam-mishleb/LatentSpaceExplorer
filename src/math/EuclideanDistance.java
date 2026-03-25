package math;

import exceptions.DimensionMismatchException;
import model.WordEmbedding;
import java.util.Comparator;
import java.util.Map;
//4
/**
 * Design Pattern: Strategy (Concrete Implementation)
 * this class implements the Euclidean Distance metric to measure the straight-line
 */
public class EuclideanDistance implements Distance {
    /**
     * calculates the Euclidean distance between two word embeddings.
     * The formula computes the square root of the sum of the squared differences
     * between corresponding coordinates.
     */
    @Override
    public double calculate(WordEmbedding v1, WordEmbedding v2) {
        if (v1.getDimension() != v2.getDimension()) {
            throw new DimensionMismatchException(v1.getDimension(), v2.getDimension());
        }
        double sum = 0.0;
        for (int i = 0; i < v1.getDimension(); i++) {
            // accumulates the squared difference for each dimension
            sum += Math.pow(v1.getCoordinate(i) - v2.getCoordinate(i), 2);
        }
        return Math.sqrt(sum);
    }
    /**
     *return The string "Euclidean Distance".
     */
    @Override
    public String getName() {
        return "Euclidean Distance";
    }
    /**
     * return a comparator sorting the entries.
     */
    @Override
    public Comparator<Map.Entry<WordEmbedding, Double>> getResultComparator() {
        return Map.Entry.comparingByValue();
    }
}