package math;

import model.WordEmbedding;

import java.util.Comparator;
import java.util.Map;
//5
/**4
 * Design Pattern: Strategy (Concrete Implementation)
 * this class implements the Cosine Similarity metric to measure the semantic similarity
 * between two word vectors. It evaluates the angle between vectors, ignoring their magnitude.
 */
public class CosineSimilarity implements Distance {
    /**
     * calculates the cosine similarity between two word embeddings.
     * the formula is: DotProduct(A, B) / (Norm(A) * Norm(B))
     * the difference between the distance is between (-1 , 1)
     */
    @Override
    public double calculate(WordEmbedding w1, WordEmbedding w2) {
        double[] v1 = w1.getVectorValues();
        double[] v2 = w2.getVectorValues();

        if (v1.length != v2.length) {
            throw new IllegalArgumentException("Vector dimensions must match for Cosine Similarity");
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < v1.length; i++) {
            dotProduct += v1[i] * v2[i];
            norm1 += v1[i] * v1[i];
            norm2 += v2[i] * v2[i];
        }

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * return The string "Cosine Similarity".
     */
    @Override
    public String getName() {
        return "Cosine Similarity";
    }
    /**
     * return A comparator sorting the entries.
     */
    @Override
    public Comparator<Map.Entry<WordEmbedding, Double>> getResultComparator() {
        return (e1, e2) -> Double.compare(e2.getValue(), e1.getValue());
    }
}