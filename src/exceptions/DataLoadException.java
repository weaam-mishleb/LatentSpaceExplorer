package exceptions;
/**
 *checked Exception: Used for recoverable errors during the data ingestion process.
 * this exception forces the caller to handle potential I/O or parsing failures.
 */
public class DataLoadException extends Exception {
    public DataLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}