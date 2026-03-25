package math;
import model.WordEmbedding;
import java.util.Comparator;
import java.util.Map;
//3
/**
// * Design Pattern: Strategy
 * This interface defines the contract for various distance or similarity metrics
 */
public interface Distance {
    /**
     * calculates the mathematical distance or similarity score between two word embeddings.
     */
    double calculate(WordEmbedding v1, WordEmbedding v2);

    /**
     * * return The name of the algorithm (e.g., "Cosine Similarity").
     */
    String getName();

    /**
     * return A Comparator to sort entries based on their calculated score.
     */
    Comparator<Map.Entry<WordEmbedding, Double>> getResultComparator();
}
