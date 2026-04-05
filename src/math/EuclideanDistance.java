package math;

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
    public double calculate(WordEmbedding w1, WordEmbedding w2) {
        double[] v1 = w1.getVectorValues();
        double[] v2 = w2.getVectorValues();

        if (v1.length != v2.length) {
            throw new IllegalArgumentException("Vector dimensions must match for Euclidean Distance");
        }

        double sum = 0.0;
        for (int i = 0; i < v1.length; i++) {
            double diff = v1[i] - v2[i];
            sum += diff * diff;
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