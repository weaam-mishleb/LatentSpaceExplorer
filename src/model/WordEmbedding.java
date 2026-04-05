package model;

public class WordEmbedding {
    private final String word;
    private final Vector vector;


    public WordEmbedding(String word, Vector vector) {
        this.word = word;
        this.vector = vector;
    }

    public WordEmbedding(String word, double[] rawValues) {
        this.word = word;
        this.vector = new RawVector(rawValues);
    }

    public String getWord() {
        return word;
    }

    public Vector getVector() {
        return vector;
    }

    public double[] getVectorValues() {
        return vector.getValues();
    }
}