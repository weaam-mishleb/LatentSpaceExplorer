package model;

import exceptions.DimensionMismatchException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class manages a collection of WordEmbedding objects,
 * ensuring dimension consistency and providing fast lookups.
 */
public class VectorSpace {
    private final List<WordEmbedding> storage;
    private final Map<String, WordEmbedding> quickLookup;
    private int dimensions = -1;

    public VectorSpace() {
        this.storage = new ArrayList<>();
        this.quickLookup = new HashMap<>();
    }

    /**
     * Adds a new WordEmbedding to the space. Ensures all embeddings have the same dimension.
     */
    public void addWord(WordEmbedding word) {
        if (word == null) return;

        // Using our new Vector Interface to get the dimension!
        int currentDim = word.getVector().getDimension();

        // The first added embedding strictly defines the dimension size for the entire space
        if (dimensions == -1) {
            dimensions = currentDim;
        } else if (currentDim != dimensions) {
            throw new DimensionMismatchException(dimensions, currentDim);
        }

        storage.add(word);
        quickLookup.put(word.getWord(), word);
    }

    /**
     * Returns The WordEmbedding, or null if not found.
     */
    public WordEmbedding getEmbedding(String word) {
        return quickLookup.get(word);
    }

    /**
     * Returns all stored word embeddings.
     */
    public List<WordEmbedding> getAllEmbeddings() {
        // Excellent use of unmodifiableList for encapsulation!
        return Collections.unmodifiableList(storage);
    }

    /**
     * Returns The total number of embeddings.
     */
    public int getSize() {
        return storage.size();
    }

    /**
     * Returns the uniform dimension size for all embeddings in this space.
     */
    public int getDimensions() {
        return dimensions;
    }
}