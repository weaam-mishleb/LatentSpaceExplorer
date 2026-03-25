package integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
//7
/**
 * Design Pattern: Adapter
 * this class acts as a bridge between the Java application and external Python scripts.
 * it encapsulates the OS-level complexities of launching a process, capturing its output,
 * and handling potential execution errors.
 */
public class PyAdapter {
    /**
     * executes the embedder.py script as a separate operating system process.
     * it captures the live console output from Python and pipes it into the Java console.
     */
    public static boolean runFile() {
        String scriptName = "embedder.py";
        ProcessBuilder pb = new ProcessBuilder("python3", scriptName);
        pb.redirectErrorStream(true);

        System.out.println("java is starting Python script: " + scriptName + "...");
        try {
            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("python: " + line);
                }
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println(" python script finished successfully.");
                return true;
            } else {
                System.err.println("python script failed");
                return false;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}