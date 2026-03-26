package math;

import model.WordEmbedding;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class VectorMathTest {

    private static final double DELTA = 0.0001;

    @Test
    void testCalculateAverageVector() {
        WordEmbedding w1 = new WordEmbedding("A", new double[]{2.0, 4.0, 6.0});
        WordEmbedding w2 = new WordEmbedding("B", new double[]{4.0, 6.0, 8.0});
        List<WordEmbedding> list = Arrays.asList(w1, w2);

        double[] expected = {3.0, 5.0, 7.0};
        double[] actual = VectorMath.calculateAverageVector(list);

        assertArrayEquals(expected, actual, DELTA);
    }

    @Test
    void testCalculateAnalogyVector() {
        WordEmbedding vA = new WordEmbedding("A", new double[]{5.0, 5.0});
        WordEmbedding vB = new WordEmbedding("B", new double[]{2.0, 1.0});
        WordEmbedding vC = new WordEmbedding("C", new double[]{1.0, 3.0});

        double[] expected = {4.0, 7.0};
        double[] actual = VectorMath.calculateAnalogyVector(vA, vB, vC);

        assertArrayEquals(expected, actual, DELTA);
    }
}