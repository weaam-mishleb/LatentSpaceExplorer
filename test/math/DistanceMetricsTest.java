package math;

import model.WordEmbedding;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DistanceMetricsTest {

    private static final double DELTA = 0.0001;

    @Test
    void testEuclideanDistance() {
        Distance euclidean = new EuclideanDistance();
        WordEmbedding p1 = new WordEmbedding("Origin", new double[]{0.0, 0.0});
        WordEmbedding p2 = new WordEmbedding("Point", new double[]{3.0, 4.0});

        double expected = 5.0;
        double actual = euclidean.calculate(p1, p2);

        assertEquals(expected, actual, DELTA);
    }

    @Test
    void testCosineSimilarityIdentical() {
        Distance cosine = new CosineSimilarity();
        WordEmbedding v1 = new WordEmbedding("A", new double[]{1.0, 2.0, 3.0});
        WordEmbedding v2 = new WordEmbedding("B", new double[]{1.0, 2.0, 3.0});

        assertEquals(1.0, cosine.calculate(v1, v2), DELTA);
    }

    @Test
    void testCosineSimilarityOrthogonal() {
        Distance cosine = new CosineSimilarity();
        WordEmbedding vX = new WordEmbedding("X", new double[]{1.0, 0.0});
        WordEmbedding vY = new WordEmbedding("Y", new double[]{0.0, 1.0});

        assertEquals(0.0, cosine.calculate(vX, vY), DELTA);
    }
}