package main

import (
	"fmt"
	"math/rand"
	"strings"
	"time"
)

// Worker represents a worker that processes tasks from the shared queue
type Worker struct {
	name           string
	taskQueue      *SharedQueue
	resultsManager *ResultsManager
	done           chan bool
	running        bool
}

// NewWorker creates a new worker
func NewWorker(name string, taskQueue *SharedQueue, resultsManager *ResultsManager) *Worker {
	return &Worker{
		name:           name,
		taskQueue:      taskQueue,
		resultsManager: resultsManager,
		done:           make(chan bool),
		running:        true,
	}
}

// Start starts the worker
func (w *Worker) Start() {
	fmt.Printf("Worker thread '%s' started\n", w.name)
	
	go w.work()
}

// work is the main work loop for the worker
func (w *Worker) work() {
	for w.running {
		// Get a task from the queue
		task := w.taskQueue.GetTask()
		
		if task == nil {
			// Queue is shutdown and empty
			fmt.Printf("Worker thread '%s' shutting down - no more tasks\n", w.name)
			break
		}
		
		// Process the task
		result, err := w.processTask(task)
		if err != nil {
			fmt.Printf("Worker thread '%s' encountered error processing task %d: %v\n", 
				w.name, task.ID, err)
			continue
		}
		
		// Add result to results manager
		if err := w.resultsManager.AddResult(result); err != nil {
			fmt.Printf("Worker thread '%s' error adding result: %v\n", w.name, err)
		}
		
		// Simulate some variation in processing time
		time.Sleep(time.Duration(rand.Intn(100)+50) * time.Millisecond)
	}
	
	fmt.Printf("Worker thread '%s' completed\n", w.name)
	w.done <- true
}

// processTask processes a single task
func (w *Worker) processTask(task *Task) (*ProcessedResult, error) {
	startTime := time.Now()
	
	fmt.Printf("Worker thread '%s' processing task %d\n", w.name, task.ID)
	
	// Simulate computational work with a delay
	processingDelay := time.Duration(rand.Intn(200)+100) * time.Millisecond
	time.Sleep(processingDelay)
	
	// Process the data (simple transformation for demonstration)
	processedData := w.processData(task.Data)
	
	processingTime := time.Since(startTime)
	
	return NewProcessedResult(
		task.ID,
		task.Data,
		processedData,
		w.name,
		processingTime,
	), nil
}

// processData processes the data (simple transformation for demonstration)
func (w *Worker) processData(data string) string {
	if data == "" {
		return "EMPTY_DATA"
	}
	
	// Simple data processing: convert to uppercase and add timestamp
	return strings.ToUpper(data) + "_PROCESSED_" + fmt.Sprintf("%d", time.Now().UnixNano())
}

// Stop stops the worker
func (w *Worker) Stop() {
	w.running = false
}

// IsRunning checks if the worker is running
func (w *Worker) IsRunning() bool {
	return w.running
}

// GetName returns the worker name
func (w *Worker) GetName() string {
	return w.name
}

// Wait waits for the worker to complete
func (w *Worker) Wait() {
	<-w.done
}
