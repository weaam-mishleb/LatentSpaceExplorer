package model;

import exceptions.DimensionMismatchException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VectorSpaceTest {

    @Test
    void testAddWordValidDimensions() {
        VectorSpace space = new VectorSpace();
        WordEmbedding w1 = new WordEmbedding("Apple", new double[]{1.0, 2.0});
        WordEmbedding w2 = new WordEmbedding("Banana", new double[]{3.0, 4.0});

        assertDoesNotThrow(() -> {
            space.addWord(w1);
            space.addWord(w2);
        });

        assertEquals(2, space.getAllEmbeddings().size());
        assertNotNull(space.getEmbedding("Apple"));
    }

    @Test
    void testDimensionMismatchException() {
        VectorSpace space = new VectorSpace();
        WordEmbedding w1 = new WordEmbedding("2D_Word", new double[]{1.0, 2.0});
        WordEmbedding w2 = new WordEmbedding("3D_Word", new double[]{1.0, 2.0, 3.0});

        space.addWord(w1);

        assertThrows(DimensionMismatchException.class, () -> {
            space.addWord(w2);
        });
    }
}