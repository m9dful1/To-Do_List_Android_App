package com.spiritwisestudios.todolist // Defines the package to organize the project files

data class Todo(                           // Data class representing a single to-do item
    var id: Int = 0,                       // Unique identifier for the task (default 0; typically set by the database)
    var title: String,                     // Title or description of the task
    var isCompleted: Boolean = false,       // Flag indicating whether the task is completed (default is false)
    var priority: Int = 1                  // Priority from 1 (low) to 5 (high); default is 1

)