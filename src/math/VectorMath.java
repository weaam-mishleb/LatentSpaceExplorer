package math;

import model.WordEmbedding;
import java.util.List;

public class VectorMath {

    public static double[] calculateAverageVector(List<WordEmbedding> embeddings) {
        if (embeddings == null || embeddings.isEmpty()) {
            return null;
        }

        int dim = embeddings.get(0).getVectorValues().length;
        double[] sum = new double[dim];

        for (WordEmbedding emb : embeddings) {
            double[] v = emb.getVectorValues();
            for (int i = 0; i < dim; i++) {
                sum[i] += v[i];
            }
        }

        for (int i = 0; i < dim; i++) {
            sum[i] /= embeddings.size();
        }

        return sum;
    }

    public static double[] calculateAnalogyVector(WordEmbedding w1, WordEmbedding w2, WordEmbedding w3) {
        double[] v1 = w1.getVectorValues();
        double[] v2 = w2.getVectorValues();
        double[] v3 = w3.getVectorValues();
        int dim = v1.length;

        double[] result = new double[dim];
        for (int i = 0; i < dim; i++) {
            result[i] = v1[i] - v2[i] + v3[i];
        }

        return result;
    }
}