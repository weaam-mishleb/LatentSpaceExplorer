package model;
/**
 * this class represents a single result from a vector search operation,
 */
public class SearchResult {
    private final WordEmbedding embedding;
    private final double score; // can represent distance or similarity
    /**
     * constructs a new SearchResult.
     */
    public SearchResult(WordEmbedding embedding, double score) {
        this.embedding = embedding;
        this.score = score;
    }
    /**
     * returns the embedding associated with this result.
     */
    public WordEmbedding getEmbedding() {
        return embedding;
    }
    /**
     * returns the calculated relevance score for this result.
     */
    public double getScore() {
        return score;
    }

    /**
     * return The associated word string.
     */
    public String getWord() {
        return embedding.getWord();
    }
}