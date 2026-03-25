package math;

import model.WordEmbedding;
import java.util.List;

/**
 * this class provides static mathematical helper methods for vector operations.
 */
public class VectorMath {
    /**
     * calculates the expected vector for a word analogy task ("a is to b as c is to ___").
     * The mathematical formula applied is: vector(a) - vector(b) + vector(c).
     */
    public static double[] calculateAnalogyVector(WordEmbedding a, WordEmbedding b, WordEmbedding c) {
        int dim = a.getDimension();
        double[] result = new double[dim];
        for (int i = 0; i < dim; i++) {
            result[i] = a.getCoordinate(i) - b.getCoordinate(i) + c.getCoordinate(i);
        }
        return result;
    }
    /**
     * calculates the average (centroid) vector from a list of word embeddings.
     */
    public static double[] calculateAverageVector(List<WordEmbedding> embeddings) {
        if (embeddings == null || embeddings.isEmpty()) return null;
        int dim = embeddings.get(0).getDimension();
        double[] sumVector = new double[dim];
        for (WordEmbedding emb : embeddings) {
            for (int i = 0; i < dim; i++) {
                sumVector[i] += emb.getCoordinate(i);
            }
        }
        for (int i = 0; i < dim; i++) {
            sumVector[i] /= embeddings.size();
        }

        return sumVector;
    }
}