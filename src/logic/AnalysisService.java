package logic;
import exceptions.WordNotFoundException;
import model.ProjectionResult;
import model.SearchResult;
import model.VectorSpace;
import model.WordEmbedding;
import math.Distance;
import math.VectorMath;
import java.util.*;
//6
/**
 * Design Pattern:Facade
 * this class provides the core business logic for vector analysis. it orchestrates
 * interactions between the data models (VectorSpace) and math strategies (Distance).
 */
public class AnalysisService {
    private final VectorSpace space;
    private Distance metric;
    public AnalysisService(VectorSpace space, Distance defaultMetric) {
        this.space = space;
        this.metric = defaultMetric;
    }
    /**
     * allows dynamic switching of the distance calculation strategy at runtime.
     */
    public void setMetric(Distance metric) { this.metric = metric; }

    public Distance getMetric() { return metric; }

    /**
     * finds the K-nearest words to a given target word using the current distance metric.
     */
    public List<SearchResult> findNearestNeighbors(String targetWord, int k) {
        WordEmbedding targetVec = space.getEmbedding(targetWord);
        if (targetVec == null) throw new WordNotFoundException(targetWord);

        List<Map.Entry<WordEmbedding, Double>> distances = new ArrayList<>();

        for (WordEmbedding w : space.getAllEmbeddings()) {
            if (!w.getWord().equals(targetWord)) { // prevent the word from matching with itself
                double dist = metric.calculate(targetVec, w);
                distances.add(new AbstractMap.SimpleEntry<>(w, dist));
            }
        }
        // uses the dynamic sorting rule from the Strategy
        distances.sort(metric.getResultComparator());
        List<SearchResult> results = new ArrayList<>();
        int limit = Math.min(k, distances.size());
        for (int i = 0; i < limit; i++) {
            Map.Entry<WordEmbedding, Double> entry = distances.get(i);
            results.add(new SearchResult(entry.getKey(), entry.getValue()));
        }

        return results;
    }
    /**
     * calculates the direct distance/similarity between two specific words.
     */
    public double getDistance(String w1, String w2) {
        WordEmbedding v1 = space.getEmbedding(w1);
        WordEmbedding v2 = space.getEmbedding(w2);
        if (v1 == null) throw new WordNotFoundException(w1);
        if (v2 == null) throw new WordNotFoundException(w2);
        return metric.calculate(v1, v2);
    }

    /**
     * return a SearchResult containing the best matching word, or null if inputs are missing.
     */
    public SearchResult computeAnalogy(String a, String b, String c) {
        WordEmbedding vA = space.getEmbedding(a);
        WordEmbedding vB = space.getEmbedding(b);
        WordEmbedding vC = space.getEmbedding(c);
        if (vA == null || vB == null || vC == null) return null;
        double[] targetVector = VectorMath.calculateAnalogyVector(vA, vB, vC);
        WordEmbedding targetEmb = new WordEmbedding("TARGET_TEMP", targetVector);
        Map.Entry<WordEmbedding, Double> bestMatch = null;
        Comparator<Map.Entry<WordEmbedding, Double>> comparator = metric.getResultComparator();
        for (WordEmbedding w : space.getAllEmbeddings()) {
            String wordText = w.getWord();
            // filters out the input words to prevent trivial answers
            if (!wordText.equals(a) && !wordText.equals(b) && !wordText.equals(c)) {
                double dist = metric.calculate(targetEmb, w);
                Map.Entry<WordEmbedding, Double> currentMatch = new AbstractMap.SimpleEntry<>(w, dist);

                if (bestMatch == null || comparator.compare(currentMatch, bestMatch) < 0) {
                    bestMatch = currentMatch;
                }
            }
        }

        if (bestMatch != null) {
            return new SearchResult(bestMatch.getKey(), bestMatch.getValue());
        }
        return null;
    }
    /**
     * calculates the centroid (average vector) of a list of words.
     */
    public WordEmbedding getCentroidVector(List<String> words) {
        if (words == null || words.isEmpty()) return null;

        List<WordEmbedding> validEmbeddings = new ArrayList<>();
        for (String w : words) {
            WordEmbedding emb = space.getEmbedding(w.trim());
            if (emb != null) {
                validEmbeddings.add(emb);
            }
        }

        if (validEmbeddings.isEmpty()) return null;

        double[] sumVector = VectorMath.calculateAverageVector(validEmbeddings);
        return new WordEmbedding("CENTROID_TEMP", sumVector);
    }

    /**
     * Finds the K-nearest words to a raw, non-dictionary vector.
     */
    public List<SearchResult> getKNearestToVector(WordEmbedding targetVec, int k, List<String> excludeWords) {
        if (targetVec == null) return Collections.emptyList();
        List<Map.Entry<WordEmbedding, Double>> distances = new ArrayList<>();
        for (WordEmbedding w : space.getAllEmbeddings()) {
            if (excludeWords == null || !excludeWords.contains(w.getWord())) {
                double dist = metric.calculate(targetVec, w);
                distances.add(new AbstractMap.SimpleEntry<>(w, dist));
            }
        }
        distances.sort(metric.getResultComparator());
        List<SearchResult> results = new ArrayList<>();
        int limit = Math.min(k, distances.size());
        for (int i = 0; i < limit; i++) {
            Map.Entry<WordEmbedding, Double> entry = distances.get(i);
            results.add(new SearchResult(entry.getKey(), entry.getValue()));
        }
        return results;
    }
    /**
     * projects all words in the space onto an axis defined by two words
     */
    public List<ProjectionResult> projectWordsOnAxis(String startWord, String endWord, int limit) {
        WordEmbedding vStart = space.getEmbedding(startWord);
        WordEmbedding vEnd = space.getEmbedding(endWord);
        if (vStart == null || vEnd == null) throw new WordNotFoundException(startWord + " or " + endWord);
        int dim = vStart.getDimension();
        double[] axisVector = new double[dim];
        double axisNormSq = 0;
        for(int i=0; i<dim; i++) {
            axisVector[i] = vEnd.getCoordinate(i) - vStart.getCoordinate(i);
            axisNormSq += axisVector[i] * axisVector[i];
        }
        List<ProjectionResult> allProjections = new ArrayList<>();
        for (WordEmbedding w : space.getAllEmbeddings()) {
            double dot = 0;
            for(int i=0; i<dim; i++) {
                double wVecRelative = w.getCoordinate(i) - vStart.getCoordinate(i);
                dot += wVecRelative * axisVector[i];
            }
            double projection = dot / axisNormSq;
            allProjections.add(new ProjectionResult(w.getWord(), projection));
        }
        allProjections.sort(Comparator.comparingDouble(ProjectionResult::getProjectionValue));
        List<ProjectionResult> results = new ArrayList<>();
        int actualLimit = Math.min(limit, allProjections.size());
        for (int i = 0; i < actualLimit; i++) {
            results.add(allProjections.get(i));
        }
        return results;
    }
}