import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main Data Processing System that orchestrates worker threads and task processing
 */
public class DataProcessingSystem {
    private final SharedQueue taskQueue;
    private final ResultsManager resultsManager;
    private final List<WorkerThread> workers;
    private final ExecutorService executorService;
    private final int numWorkerThreads;
    private final int maxQueueSize;
    
    public DataProcessingSystem(int numWorkerThreads, int maxQueueSize, String outputFileName) {
        this.numWorkerThreads = numWorkerThreads;
        this.maxQueueSize = maxQueueSize;
        this.taskQueue = new SharedQueue(maxQueueSize);
        this.resultsManager = new ResultsManager(outputFileName);
        this.workers = new ArrayList<>();
        this.executorService = Executors.newFixedThreadPool(numWorkerThreads);
    }
    
    /**
     * Start the data processing system
     */
    public void start() {
        System.out.println("Starting Data Processing System...");
        System.out.println("Number of worker threads: " + numWorkerThreads);
        System.out.println("Maximum queue size: " + maxQueueSize);
        System.out.println("=" .repeat(50));
        
        // Create and start worker threads
        for (int i = 1; i <= numWorkerThreads; i++) {
            WorkerThread worker = new WorkerThread("Worker-" + i, taskQueue, resultsManager);
            workers.add(worker);
            executorService.submit(worker);
        }
        
        System.out.println("All worker threads started successfully");
    }
    
    /**
     * Add tasks to the processing queue
     * @param tasks List of tasks to add
     */
    public void addTasks(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            System.out.println("No tasks to add");
            return;
        }
        
        System.out.println("Adding " + tasks.size() + " tasks to the queue...");
        
        for (Task task : tasks) {
            try {
                boolean added = taskQueue.addTask(task);
                if (added) {
                    System.out.println("Task " + task.getId() + " added to queue");
                } else {
                    System.err.println("Failed to add task " + task.getId() + " to queue");
                }
                
                // Small delay to simulate real-world task arrival
                Thread.sleep(50);
                
            } catch (InterruptedException e) {
                System.err.println("Interrupted while adding tasks: " + e.getMessage());
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Error adding task " + task.getId() + ": " + e.getMessage());
            }
        }
        
        System.out.println("Finished adding tasks to queue");
    }
    
    /**
     * Shutdown the system gracefully
     */
    public void shutdown() {
        System.out.println("\nShutting down Data Processing System...");
        
        // Shutdown the task queue
        taskQueue.shutdown();
        
        // Stop all worker threads
        for (WorkerThread worker : workers) {
            worker.stop();
        }
        
        // Shutdown executor service
        executorService.shutdown();
        
        try {
            // Wait for all threads to complete
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                System.out.println("Forcing shutdown of remaining threads...");
                executorService.shutdownNow();
                
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("Executor service did not terminate");
                }
            }
        } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting for threads to complete: " + e.getMessage());
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        System.out.println("All worker threads completed");
        
        // Write results to file
        boolean success = resultsManager.writeResultsToFile();
        if (success) {
            resultsManager.printSummary();
        } else {
            System.err.println("Failed to write results to file");
        }
        
        System.out.println("Data Processing System shutdown complete");
    }
    
    /**
     * Wait for all tasks to be processed
     */
    public void waitForCompletion() {
        System.out.println("Waiting for all tasks to be processed...");
        
        while (!taskQueue.isEmpty() || taskQueue.size() > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.err.println("Interrupted while waiting for completion: " + e.getMessage());
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println("All tasks have been processed");
    }
    
    /**
     * Get current system status
     */
    public void printStatus() {
        System.out.println("\n=== System Status ===");
        System.out.println("Queue size: " + taskQueue.size());
        System.out.println("Queue empty: " + taskQueue.isEmpty());
        System.out.println("Queue shutdown: " + taskQueue.isShutdown());
        System.out.println("Results count: " + resultsManager.getResultCount());
        System.out.println("===================");
    }
    
    /**
     * Get the task queue
     * @return The task queue
     */
    public SharedQueue getTaskQueue() {
        return taskQueue;
    }
    
    /**
     * Get the results manager
     * @return The results manager
     */
    public ResultsManager getResultsManager() {
        return resultsManager;
    }
}
