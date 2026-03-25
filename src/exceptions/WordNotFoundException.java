package exceptions;
/**
 * unchecked Exception: Thrown when a requested word does not exist in the VectorSpace.
 * extends RuntimeException because this usually indicates a logic gap or invalid user input.
 */
public class WordNotFoundException extends RuntimeException {
    public WordNotFoundException(String word) {
        super("the word '" + word + "' was not found in the latent space.");
    }
}