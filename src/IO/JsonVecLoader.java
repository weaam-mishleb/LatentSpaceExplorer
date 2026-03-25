package IO;
import exceptions.DataLoadException;
import model.VectorSpace;
import model.WordEmbedding;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
//9
/**
 * Design Pattern: Strategy
 * this class handles the extraction of word embeddings from a JSON file.
 * By implementing the IDataLoader interface, it supports Polymorphism, allowing
 */
public class JsonVecLoader implements IDataLoader {
    /**
     * reads a JSON file, parses its contents manually, and populates the given VectorSpace.
     */
    @Override
    public void loadVectors(String jsonFilePath, VectorSpace space) throws DataLoadException {
        System.out.println("reading vectors from: " + jsonFilePath);
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(jsonFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line);
            }
        } catch (IOException e) {
            throw new DataLoadException("failed to read the JSON file: " + jsonFilePath, e);
        }
        String json = contentBuilder.toString().trim();
        if (json.startsWith("[") && json.endsWith("]")) {
            json = json.substring(1, json.length() - 1);
        }
        String[] objects = json.split("\\},\\s*\\{");

        int count = 0;
        for (String obj : objects) {
            obj = obj.replace("{", "").replace("}", "");
            try {
                String word = extractValue(obj, "\"word\":");
                if (word != null) word = word.replace("\"", "").trim();

                String vectorStr = extractValue(obj, "\"vector\":");
                if (word != null && vectorStr != null) {
                    vectorStr = vectorStr.replace("[", "").replace("]", "");
                    String[] numStrings = vectorStr.split(",");
                    double[] vector = new double[numStrings.length];
                    for (int i = 0; i < numStrings.length; i++) {
                        vector[i] = Double.parseDouble(numStrings[i].trim());
                    }
                    space.addWord(new WordEmbedding(word, vector));
                    count++;
                }
            } catch (Exception e) {
                System.err.println("warning: Corrupted data " + e.getMessage());
            }
        }
        System.out.println("loaded " + count + " words into VectorSpace.");
    }

    /**
     * a lightweight helper method to extract values for a specific key from a raw JSON string.
     * return The extracted string value, or null if not found.
     */
    private String extractValue(String text, String key) {
        int startIdx = text.indexOf(key);
        if (startIdx == -1) return null;
        startIdx += key.length();
        while (startIdx < text.length() && Character.isWhitespace(text.charAt(startIdx))) {
            startIdx++;
        }
        int endIdx;
        if (text.charAt(startIdx) == '[') {
            endIdx = text.indexOf("]", startIdx);
            if (endIdx != -1) endIdx++;
        } else {
            endIdx = text.indexOf(", \"", startIdx);
            if (endIdx == -1) endIdx = text.length();
        }
        if (endIdx == -1) return null;
        return text.substring(startIdx, endIdx).trim();
    }
}