import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Thread-safe results manager for storing and writing processed results
 */
public class ResultsManager {
    private final List<ProcessedResult> results;
    private final ReentrantLock lock;
    private final String outputFileName;
    private final DateTimeFormatter formatter;
    
    public ResultsManager(String outputFileName) {
        this.results = new ArrayList<>();
        this.lock = new ReentrantLock();
        this.outputFileName = outputFileName;
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }
    
    /**
     * Add a processed result to the results list
     * @param result The processed result to add
     */
    public void addResult(ProcessedResult result) {
        if (result == null) {
            throw new IllegalArgumentException("Result cannot be null");
        }
        
        lock.lock();
        try {
            results.add(result);
            System.out.println("Result added: " + result);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Get all results (thread-safe copy)
     * @return List of all results
     */
    public List<ProcessedResult> getAllResults() {
        lock.lock();
        try {
            return new ArrayList<>(results);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Get the number of results
     * @return Number of results
     */
    public int getResultCount() {
        lock.lock();
        try {
            return results.size();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Write all results to the output file
     * @return true if successful, false otherwise
     */
    public boolean writeResultsToFile() {
        lock.lock();
        try {
            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFileName))) {
                writer.println("Data Processing System Results");
                writer.println("Generated at: " + LocalDateTime.now().format(formatter));
                writer.println("Total Results: " + results.size());
                writer.println("=" .repeat(80));
                writer.println();
                
                for (ProcessedResult result : results) {
                    writer.println(result.toString());
                    writer.println("-".repeat(40));
                }
                
                System.out.println("Results written to file: " + outputFileName);
                return true;
            } catch (IOException e) {
                System.err.println("Error writing results to file: " + e.getMessage());
                return false;
            }
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Print summary statistics to console
     */
    public void printSummary() {
        lock.lock();
        try {
            if (results.isEmpty()) {
                System.out.println("No results to display.");
                return;
            }
            
            long totalProcessingTime = 0;
            for (ProcessedResult result : results) {
                totalProcessingTime += result.getProcessingTime();
            }
            
            double avgProcessingTime = (double) totalProcessingTime / results.size();
            
            System.out.println("\n=== Processing Summary ===");
            System.out.println("Total Tasks Processed: " + results.size());
            System.out.println("Total Processing Time: " + totalProcessingTime + "ms");
            System.out.println("Average Processing Time: " + String.format("%.2f", avgProcessingTime) + "ms");
            System.out.println("Results saved to: " + outputFileName);
            System.out.println("========================");
        } finally {
            lock.unlock();
        }
    }
}
