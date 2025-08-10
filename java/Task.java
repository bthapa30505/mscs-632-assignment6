/**
 * Task class representing a data processing task
 */
public class Task {
    private int id;
    private String data;
    private long timestamp;
    
    public Task(int id, String data) {
        this.id = id;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    public int getId() {
        return id;
    }
    
    public String getData() {
        return data;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return "Task{id=" + id + ", data='" + data + "', timestamp=" + timestamp + "}";
    }
}
