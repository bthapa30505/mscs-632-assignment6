import java.util.ArrayList;
import java.util.List;

/**
 * Main class to demonstrate the Data Processing System
 */
public class Main {
    public static void main(String[] args) {
        
        try {
            // Configuration
            int numWorkerThreads = 4;
            int maxQueueSize = 20;
            String outputFileName = "processing_results.txt";
            
            // Create the data processing system
            DataProcessingSystem system = new DataProcessingSystem(numWorkerThreads, maxQueueSize, outputFileName);
            
            // Start the system
            system.start();
            
            // Create sample tasks
            List<Task> tasks = createSampleTasks(15);
            
            // Add tasks to the system
            system.addTasks(tasks);
            
            // Print initial status
            system.printStatus();
            
            // Wait for all tasks to be processed
            system.waitForCompletion();
            
            // Print final status
            system.printStatus();
            
            // Shutdown the system gracefully
            system.shutdown();
            
        } catch (Exception e) {
            System.err.println("Error in main execution: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create sample tasks for demonstration
     * @param count Number of tasks to create
     * @return List of sample tasks
     */
    private static List<Task> createSampleTasks(int count) {
        List<Task> tasks = new ArrayList<>();
        
        String[] sampleData = {
            "user_login_data",
            "payment_transaction",
            "inventory_update",
            "customer_feedback",
            "order_processing",
            "analytics_report",
            "system_backup",
            "email_notification",
            "database_cleanup",
            "performance_metrics",
            "security_audit",
            "backup_verification",
            "cache_refresh",
            "log_rotation",
            "health_check"
        };
        
        for (int i = 0; i < count && i < sampleData.length; i++) {
            Task task = new Task(i + 1, sampleData[i]);
            tasks.add(task);
        }
        
        return tasks;
    }
}
