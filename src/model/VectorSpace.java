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
     * adds a new WordEmbedding to the space. Ensures all embeddings have the same dimension.
     */
    public void addWord(WordEmbedding word) {
        if (word == null) return;
        int currentDim = word.getVector().getDimension();
        if (dimensions == -1) {
            dimensions = currentDim;
        } else if (currentDim != dimensions) {
            throw new DimensionMismatchException(dimensions, currentDim);
        }

        storage.add(word);
        quickLookup.put(word.getWord(), word);
    }

    /**
     * returns The WordEmbedding, or null if not found.
     */
    public WordEmbedding getEmbedding(String word) {
        return quickLookup.get(word);
    }

    /**
     * returns all stored word embeddings.
     */
    public List<WordEmbedding> getAllEmbeddings() {
        return Collections.unmodifiableList(storage);
    }

    /**
     * returns The total number of embeddings.
     */
    public int getSize() {
        return storage.size();
    }

    /**
     * returns the uniform dimension size for all embeddings in this space.
     */
    public int getDimensions() {
        return dimensions;
    }
}