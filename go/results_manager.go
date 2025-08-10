package main

import (
	"fmt"
	"os"
	"strings"
	"sync"
	"time"
)

// ResultsManager manages the storage and output of processed results
type ResultsManager struct {
	results      []*ProcessedResult
	mu           sync.RWMutex
	outputFile   string
	resultsCount int
}

// NewResultsManager creates a new results manager
func NewResultsManager(outputFile string) *ResultsManager {
	return &ResultsManager{
		results:      make([]*ProcessedResult, 0),
		outputFile:   outputFile,
		resultsCount: 0,
	}
}

// AddResult adds a processed result to the results list
func (rm *ResultsManager) AddResult(result *ProcessedResult) error {
	if result == nil {
		return fmt.Errorf("result cannot be nil")
	}

	rm.mu.Lock()
	defer rm.mu.Unlock()

	rm.results = append(rm.results, result)
	rm.resultsCount++

	fmt.Printf("Result added: %s\n", result)
	return nil
}

// GetAllResults returns a copy of all results
func (rm *ResultsManager) GetAllResults() []*ProcessedResult {
	rm.mu.RLock()
	defer rm.mu.RUnlock()

	resultsCopy := make([]*ProcessedResult, len(rm.results))
	copy(resultsCopy, rm.results)
	return resultsCopy
}

// GetResultCount returns the number of results
func (rm *ResultsManager) GetResultCount() int {
	rm.mu.RLock()
	defer rm.mu.RUnlock()
	return rm.resultsCount
}

// WriteResultsToFile writes all results to the output file
func (rm *ResultsManager) WriteResultsToFile() error {
	rm.mu.RLock()
	defer rm.mu.RUnlock()

	file, err := os.Create(rm.outputFile)
	if err != nil {
		return fmt.Errorf("error creating output file: %v", err)
	}
	defer file.Close()

	// Write header
	fmt.Fprintf(file, "Data Processing System Results\n")
	fmt.Fprintf(file, "Generated at: %s\n", time.Now().Format("2006-01-02 15:04:05"))
	fmt.Fprintf(file, "Total Results: %d\n", rm.resultsCount)
	fmt.Fprintf(file, "%s\n\n", strings.Repeat("=", 80))

	// Write results
	for _, result := range rm.results {
		fmt.Fprintf(file, "%s\n", result.String())
		fmt.Fprintf(file, "%s\n", strings.Repeat("-", 40))
	}

	fmt.Printf("Results written to file: %s\n", rm.outputFile)
	return nil
}

// PrintSummary prints summary statistics to console
func (rm *ResultsManager) PrintSummary() {
	rm.mu.RLock()
	defer rm.mu.RUnlock()

	if rm.resultsCount == 0 {
		fmt.Println("No results to display.")
		return
	}

	var totalProcessingTime time.Duration
	for _, result := range rm.results {
		totalProcessingTime += result.ProcessingTime
	}

	avgProcessingTime := totalProcessingTime / time.Duration(rm.resultsCount)

	fmt.Println("\n=== Processing Summary ===")
	fmt.Printf("Total Tasks Processed: %d\n", rm.resultsCount)
	fmt.Printf("Total Processing Time: %v\n", totalProcessingTime)
	fmt.Printf("Average Processing Time: %v\n", avgProcessingTime)
	fmt.Printf("Results saved to: %s\n", rm.outputFile)
	fmt.Println("========================")
}
