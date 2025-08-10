import java.util.Random;

/**
 * Worker thread that processes tasks from the shared queue
 */
public class WorkerThread implements Runnable {
    private final String threadName;
    private final SharedQueue taskQueue;
    private final ResultsManager resultsManager;
    private final Random random;
    private volatile boolean running;
    
    public WorkerThread(String threadName, SharedQueue taskQueue, ResultsManager resultsManager) {
        this.threadName = threadName;
        this.taskQueue = taskQueue;
        this.resultsManager = resultsManager;
        this.random = new Random();
        this.running = true;
    }
    
    @Override
    public void run() {
        System.out.println("Worker thread '" + threadName + "' started");
        
        try {
            while (running && !Thread.currentThread().isInterrupted()) {
                Task task = taskQueue.getTask();
                
                if (task == null) {
                    // Queue is shutdown and empty
                    System.out.println("Worker thread '" + threadName + "' shutting down - no more tasks");
                    break;
                }
                
                try {
                    ProcessedResult result = processTask(task);
                    resultsManager.addResult(result);
                    
                    // Simulate some variation in processing time
                    Thread.sleep(random.nextInt(100) + 50);
                    
                } catch (InterruptedException e) {
                    System.err.println("Worker thread '" + threadName + "' interrupted during processing");
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Worker thread '" + threadName + "' encountered error processing task " + 
                                    task.getId() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Worker thread '" + threadName + "' encountered unexpected error: " + e.getMessage());
        } finally {
            System.out.println("Worker thread '" + threadName + "' completed");
        }
    }
    
    /**
     * Process a single task
     * @param task The task to process
     * @return The processed result
     * @throws InterruptedException if interrupted during processing
     */
    private ProcessedResult processTask(Task task) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        
        System.out.println("Worker thread '" + threadName + "' processing task " + task.getId());
        
        // Simulate computational work with a delay
        int processingDelay = random.nextInt(200) + 100; // 100-300ms
        Thread.sleep(processingDelay);
        
        // Process the data (simple transformation for demonstration)
        String processedData = processData(task.getData());
        
        long processingTime = System.currentTimeMillis() - startTime;
        
        return new ProcessedResult(
            task.getId(),
            task.getData(),
            processedData,
            processingTime,
            threadName
        );
    }
    
    /**
     * Process the data (simple transformation for demonstration)
     * @param data The input data
     * @return The processed data
     */
    private String processData(String data) {
        if (data == null || data.isEmpty()) {
            return "EMPTY_DATA";
        }
        
        // Simple data processing: convert to uppercase and add timestamp
        return data.toUpperCase() + "_PROCESSED_" + System.currentTimeMillis();
    }
    
    /**
     * Stop the worker thread
     */
    public void stop() {
        running = false;
    }
    
    /**
     * Check if the worker thread is running
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Get the thread name
     * @return The thread name
     */
    public String getThreadName() {
        return threadName;
    }
}
