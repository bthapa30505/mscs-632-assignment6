package main

import (
	"math/rand"
	"time"
)

func main() {	
	// Seed random number generator
	rand.Seed(time.Now().UnixNano())
	
	// Configuration
	numWorkerThreads := 4
	maxQueueSize := 20
	outputFileName := "go_processing_results.txt"
	
	// Create the data processing system
	system := NewDataProcessingSystem(numWorkerThreads, maxQueueSize, outputFileName)
	
	// Start the system
	system.Start()
	
	// Create sample tasks
	tasks := createSampleTasks(15)
	
	// Add tasks to the system
	system.AddTasks(tasks)
	
	// Print initial status
	system.PrintStatus()
	
	// Wait for all tasks to be processed
	system.WaitForCompletion()
	
	// Print final status
	system.PrintStatus()
	
	// Shutdown the system gracefully
	system.Shutdown()
}

// createSampleTasks creates sample tasks for demonstration
func createSampleTasks(count int) []*Task {
	tasks := make([]*Task, 0, count)
	
	sampleData := []string{
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
		"health_check",
	}
	
	for i := 0; i < count && i < len(sampleData); i++ {
		task := NewTask(i+1, sampleData[i])
		tasks = append(tasks, task)
	}
	
	return tasks
}
