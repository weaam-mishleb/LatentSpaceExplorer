package model;
import java.util.Arrays;
//1
/**
 * This class represents a word and its mathematical vector representation.
 */
public class WordEmbedding {
    private final String word;
    private final double[] vector;
    /**
   * constructor
     */
    public WordEmbedding(String word, double[] vector) {
        if (word == null || vector == null) {
            throw new IllegalArgumentException("the word or the vector cannot be null");
        }
        this.word = word;
        this.vector = Arrays.copyOf(vector, vector.length);
    }
    /**
     * returns a copy of the internal vector array.
     */
    public double[] getVector() {
        return Arrays.copyOf(vector, vector.length);
    }
    /**
     *return The word string.
     */
    public String getWord() {
        return word;
    }
    /**
     * retrieves the specific value of the vector at the given index.
     */
    public double getCoordinate(int index) {
        if (index < 0 || index >= vector.length) {
            throw new IndexOutOfBoundsException();
        }
        return vector[index];
    }
    /**
     * returns the length of the embedding vector.
     */
    public int getDimension() {
        return vector.length;
    }
}