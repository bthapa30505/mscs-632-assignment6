package main

import (
	"fmt"
	"strings"
	"sync"
	"time"
)

// DataProcessingSystem orchestrates the entire data processing system
type DataProcessingSystem struct {
	taskQueue        *SharedQueue
	resultsManager   *ResultsManager
	workers          []*Worker
	numWorkerThreads int
	maxQueueSize     int
	wg               sync.WaitGroup
}

// NewDataProcessingSystem creates a new data processing system
func NewDataProcessingSystem(numWorkerThreads, maxQueueSize int, outputFileName string) *DataProcessingSystem {
	return &DataProcessingSystem{
		taskQueue:        NewSharedQueue(maxQueueSize),
		resultsManager:   NewResultsManager(outputFileName),
		workers:          make([]*Worker, 0),
		numWorkerThreads: numWorkerThreads,
		maxQueueSize:     maxQueueSize,
	}
}

// Start starts the data processing system
func (dps *DataProcessingSystem) Start() {
	fmt.Println("Starting Data Processing System...")
	fmt.Printf("Number of worker threads: %d\n", dps.numWorkerThreads)
	fmt.Printf("Maximum queue size: %d\n", dps.maxQueueSize)
	fmt.Println(strings.Repeat("=", 50))
	
	// Create and start worker threads
	for i := 1; i <= dps.numWorkerThreads; i++ {
		worker := NewWorker(fmt.Sprintf("Worker-%d", i), dps.taskQueue, dps.resultsManager)
		dps.workers = append(dps.workers, worker)
		worker.Start()
	}
	
	fmt.Println("All worker threads started successfully")
}

// AddTasks adds tasks to the processing queue
func (dps *DataProcessingSystem) AddTasks(tasks []*Task) {
	if tasks == nil || len(tasks) == 0 {
		fmt.Println("No tasks to add")
		return
	}
	
	fmt.Printf("Adding %d tasks to the queue...\n", len(tasks))
	
	for _, task := range tasks {
		added := dps.taskQueue.AddTask(task)
		if added {
			fmt.Printf("Task %d added to queue\n", task.ID)
		} else {
			fmt.Printf("Failed to add task %d to queue\n", task.ID)
		}
		
		// Small delay to simulate real-world task arrival
		time.Sleep(50 * time.Millisecond)
	}
	
	fmt.Println("Finished adding tasks to queue")
}

// Shutdown shuts down the system gracefully
func (dps *DataProcessingSystem) Shutdown() {
	fmt.Println("\nShutting down Data Processing System...")
	
	// Shutdown the task queue
	dps.taskQueue.Shutdown()
	
	// Stop all worker threads
	for _, worker := range dps.workers {
		worker.Stop()
	}
	
	// Wait for all workers to complete
	dps.wg.Add(len(dps.workers))
	for _, worker := range dps.workers {
		go func(w *Worker) {
			defer dps.wg.Done()
			w.Wait()
		}(worker)
	}
	
	// Wait for completion with timeout
	done := make(chan struct{})
	go func() {
		dps.wg.Wait()
		close(done)
	}()
	
	select {
	case <-done:
		fmt.Println("All worker threads completed")
	case <-time.After(10 * time.Second):
		fmt.Println("Timeout waiting for worker threads to complete")
	}
	
	// Write results to file
	if err := dps.resultsManager.WriteResultsToFile(); err != nil {
		fmt.Printf("Failed to write results to file: %v\n", err)
	} else {
		dps.resultsManager.PrintSummary()
	}
	
	fmt.Println("Data Processing System shutdown complete")
}

// WaitForCompletion waits for all tasks to be processed
func (dps *DataProcessingSystem) WaitForCompletion() {
	fmt.Println("Waiting for all tasks to be processed...")
	
	for !dps.taskQueue.IsEmpty() || dps.taskQueue.Size() > 0 {
		time.Sleep(100 * time.Millisecond)
	}
	
	fmt.Println("All tasks have been processed")
}

// PrintStatus prints current system status
func (dps *DataProcessingSystem) PrintStatus() {
	fmt.Println("\n=== System Status ===")
	fmt.Printf("Queue size: %d\n", dps.taskQueue.Size())
	fmt.Printf("Queue empty: %v\n", dps.taskQueue.IsEmpty())
	fmt.Printf("Queue shutdown: %v\n", dps.taskQueue.IsShutdown())
	fmt.Printf("Results count: %d\n", dps.resultsManager.GetResultCount())
	fmt.Println("===================")
}

// GetTaskQueue returns the task queue
func (dps *DataProcessingSystem) GetTaskQueue() *SharedQueue {
	return dps.taskQueue
}

// GetResultsManager returns the results manager
func (dps *DataProcessingSystem) GetResultsManager() *ResultsManager {
	return dps.resultsManager
}
