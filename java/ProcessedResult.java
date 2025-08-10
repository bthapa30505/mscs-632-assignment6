/**
 * ProcessedResult class representing the result of a processed task
 */
public class ProcessedResult {
    private int taskId;
    private String originalData;
    private String processedData;
    private long processingTime;
    private String workerThreadName;
    private long timestamp;
    
    public ProcessedResult(int taskId, String originalData, String processedData, 
                          long processingTime, String workerThreadName) {
        this.taskId = taskId;
        this.originalData = originalData;
        this.processedData = processedData;
        this.processingTime = processingTime;
        this.workerThreadName = workerThreadName;
        this.timestamp = System.currentTimeMillis();
    }
    
    public int getTaskId() {
        return taskId;
    }
    
    public String getOriginalData() {
        return originalData;
    }
    
    public String getProcessedData() {
        return processedData;
    }
    
    public long getProcessingTime() {
        return processingTime;
    }
    
    public String getWorkerThreadName() {
        return workerThreadName;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return "ProcessedResult{taskId=" + taskId + 
               ", originalData='" + originalData + "'" +
               ", processedData='" + processedData + "'" +
               ", processingTime=" + processingTime + "ms" +
               ", workerThread='" + workerThreadName + "'" +
               ", timestamp=" + timestamp + "}";
    }
}
