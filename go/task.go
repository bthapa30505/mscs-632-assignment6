package main

import (
	"fmt"
	"time"
)

// Task represents a data processing task
type Task struct {
	ID        int
	Data      string
	Timestamp time.Time
}

// NewTask creates a new task with the given ID and data
func NewTask(id int, data string) *Task {
	return &Task{
		ID:        id,
		Data:      data,
		Timestamp: time.Now(),
	}
}

// String returns a string representation of the task
func (t *Task) String() string {
	return fmt.Sprintf("Task{id=%d, data='%s', timestamp=%s}", t.ID, t.Data, t.Timestamp.Format("2006-01-02 15:04:05"))
}
