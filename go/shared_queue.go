package main

import (
	"context"
	"fmt"
	"sync"
	"time"
)

// SharedQueue represents a thread-safe queue for tasks
type SharedQueue struct {
	tasks     chan *Task
	mu        sync.RWMutex
	shutdown  bool
	maxSize   int
	ctx       context.Context
	cancel    context.CancelFunc
}

// NewSharedQueue creates a new shared queue with the specified max size
func NewSharedQueue(maxSize int) *SharedQueue {
	ctx, cancel := context.WithCancel(context.Background())
	return &SharedQueue{
		tasks:    make(chan *Task, maxSize),
		maxSize:  maxSize,
		ctx:      ctx,
		cancel:   cancel,
		shutdown: false,
	}
}

// AddTask adds a task to the queue
func (sq *SharedQueue) AddTask(task *Task) bool {
	if task == nil {
		return false
	}

	sq.mu.RLock()
	if sq.shutdown {
		sq.mu.RUnlock()
		return false
	}
	sq.mu.RUnlock()

	select {
	case sq.tasks <- task:
		return true
	case <-sq.ctx.Done():
		return false
	default:
		// Queue is full, try to add with timeout
		select {
		case sq.tasks <- task:
			return true
		case <-time.After(100 * time.Millisecond):
			return false
		case <-sq.ctx.Done():
			return false
		}
	}
}

// GetTask retrieves a task from the queue
func (sq *SharedQueue) GetTask() *Task {
	sq.mu.RLock()
	if sq.shutdown {
		sq.mu.RUnlock()
		return nil
	}
	sq.mu.RUnlock()

	select {
	case task := <-sq.tasks:
		return task
	case <-sq.ctx.Done():
		return nil
	}
}

// IsEmpty checks if the queue is empty
func (sq *SharedQueue) IsEmpty() bool {
	sq.mu.RLock()
	defer sq.mu.RUnlock()
	return len(sq.tasks) == 0
}

// Size returns the current size of the queue
func (sq *SharedQueue) Size() int {
	sq.mu.RLock()
	defer sq.mu.RUnlock()
	return len(sq.tasks)
}

// Shutdown shuts down the queue
func (sq *SharedQueue) Shutdown() {
	sq.mu.Lock()
	defer sq.mu.Unlock()
	if !sq.shutdown {
		sq.shutdown = true
		sq.cancel()
		close(sq.tasks)
	}
}

// IsShutdown checks if the queue is shutdown
func (sq *SharedQueue) IsShutdown() bool {
	sq.mu.RLock()
	defer sq.mu.RUnlock()
	return sq.shutdown
}

// String returns a string representation of the queue status
func (sq *SharedQueue) String() string {
	sq.mu.RLock()
	defer sq.mu.RUnlock()
	return fmt.Sprintf("SharedQueue{size=%d, maxSize=%d, shutdown=%v}", len(sq.tasks), sq.maxSize, sq.shutdown)
}
