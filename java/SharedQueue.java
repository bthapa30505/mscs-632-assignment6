import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe shared queue implementation using ReentrantLock and Condition
 */
public class SharedQueue {
    private final Queue<Task> queue;
    private final ReentrantLock lock;
    private final Condition notEmpty;
    private final Condition notFull;
    private final int maxSize;
    private volatile boolean shutdown = false;
    
    public SharedQueue(int maxSize) {
        this.maxSize = maxSize;
        this.queue = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.notFull = lock.newCondition();
    }
    
    /**
     * Add a task to the queue
     * @param task The task to add
     * @return true if added successfully, false if queue is full or shutdown
     */
    public boolean addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        
        lock.lock();
        try {
            while (queue.size() >= maxSize && !shutdown) {
                try {
                    notFull.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            
            if (shutdown) {
                return false;
            }
            
            queue.offer(task);
            notEmpty.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Get a task from the queue
     * @return The task, or null if queue is empty and shutdown
     */
    public Task getTask() {
        lock.lock();
        try {
            while (queue.isEmpty() && !shutdown) {
                try {
                    notEmpty.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            
            if (queue.isEmpty() && shutdown) {
                return null;
            }
            
            Task task = queue.poll();
            notFull.signal();
            return task;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Check if queue is empty
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        lock.lock();
        try {
            return queue.isEmpty();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Get current queue size
     * @return current size
     */
    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Shutdown the queue
     */
    public void shutdown() {
        lock.lock();
        try {
            shutdown = true;
            notEmpty.signalAll();
            notFull.signalAll();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Check if queue is shutdown
     * @return true if shutdown, false otherwise
     */
    public boolean isShutdown() {
        return shutdown;
    }
}
