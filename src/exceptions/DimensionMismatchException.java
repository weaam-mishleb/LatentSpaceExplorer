package exceptions;
/**
 * unchecked Exception: Ensures data integrity within the vector space.
 * triggered when attempting to add or compare vectors of different lengths.
 */
public class DimensionMismatchException extends RuntimeException {
    public DimensionMismatchException(int expected, int actual) {
        super("dimension mismatch: expected " + expected + " dimensions, but got " + actual);
    }
}