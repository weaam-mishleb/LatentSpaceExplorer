package IO;
import model.VectorSpace;
import exceptions.DataLoadException;
//8
/**
 * Design Pattern: Strategy
 * This interface defines the contract for loading word vectors into the system.
 * It provides a layer of abstraction, allowing the application to support multiple
 */
public interface IDataLoader {
    /**
     * reads vector data from a specified source and populates the target VectorSpace.
     */
    void loadVectors(String filePath, VectorSpace space) throws DataLoadException;
}