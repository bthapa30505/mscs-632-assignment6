package main

import (
	"fmt"
	"time"
)

// ProcessedResult represents the result of a processed task
type ProcessedResult struct {
	TaskID           int
	OriginalData     string
	ProcessedData    string
	ProcessingTime   time.Duration
	WorkerThreadName string
	Timestamp        time.Time
}

// NewProcessedResult creates a new processed result
func NewProcessedResult(taskID int, originalData, processedData, workerThreadName string, processingTime time.Duration) *ProcessedResult {
	return &ProcessedResult{
		TaskID:           taskID,
		OriginalData:     originalData,
		ProcessedData:    processedData,
		ProcessingTime:   processingTime,
		WorkerThreadName: workerThreadName,
		Timestamp:        time.Now(),
	}
}

// String returns a string representation of the processed result
func (pr *ProcessedResult) String() string {
	return fmt.Sprintf("ProcessedResult{taskId=%d, originalData='%s', processedData='%s', processingTime=%v, workerThread='%s', timestamp=%s}",
		pr.TaskID, pr.OriginalData, pr.ProcessedData, pr.ProcessingTime, pr.WorkerThreadName, pr.Timestamp.Format("2006-01-02 15:04:05"))
}
