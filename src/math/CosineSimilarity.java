package math;
import exceptions.DimensionMismatchException;
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
    public double calculate(WordEmbedding v1, WordEmbedding v2) {
        double dotProduct = 0.0, normA = 0.0, normB = 0.0;
        if (v1.getDimension() != v2.getDimension()) {
            throw new DimensionMismatchException(v1.getDimension(), v2.getDimension());
        }
        for (int i = 0; i < v1.getDimension(); i++) {
            dotProduct += v1.getCoordinate(i) * v2.getCoordinate(i);
            normA += Math.pow(v1.getCoordinate(i), 2);
            normB += Math.pow(v2.getCoordinate(i), 2);
        }
        if (normA == 0.0 || normB == 0.0) return 0.0;

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
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