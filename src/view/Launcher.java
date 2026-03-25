package view;
import IO.IDataLoader;
import IO.JsonVecLoader;
import model.VectorSpace;
import integration.PyAdapter;
import java.io.File;
/**
 * this class handles system initialization, including Python integration and data loading.
 */
public class Launcher {
    public static void main(String[] args) {
        System.out.println("Starting the Engine...");
        File fullJsonFile = new File("full_vectors.json");
        File pcaJsonFile = new File("pca_vectors.json");
        if (!fullJsonFile.exists() || !pcaJsonFile.exists()) {
            System.out.println("JSON files missing. Orchestrating Python to generate vectors...");
            boolean pythonSuccess = PyAdapter.runFile();

            if (!pythonSuccess) {
                System.err.println("Critical Error: Python script failed. Cannot start application.");
                return;
            }
            fullJsonFile = new File("full_vectors.json");
            pcaJsonFile = new File("pca_vectors.json");
        }

        VectorSpace fullSpace = new VectorSpace();
        VectorSpace pcaSpace = new VectorSpace();

        try {
            IDataLoader dataLoader = new JsonVecLoader();
            dataLoader.loadVectors(fullJsonFile.getAbsolutePath(), fullSpace);
            dataLoader.loadVectors(pcaJsonFile.getAbsolutePath(), pcaSpace);
        } catch (Exception e) {
            System.err.println("Error loading vectors. Please ensure JSON files are present.");
            e.printStackTrace();
            return;
        }

        // Launches the UI application
        Visualizer visualizer = new JavaFX3DVisualizer();
        visualizer.display(fullSpace, pcaSpace);
    }
}