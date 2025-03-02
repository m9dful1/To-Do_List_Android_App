package com.spiritwisestudios.todolist

// Import necessary Android and Jetpack Compose libraries
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
// Import custom Compose theme for the app
import com.spiritwisestudios.todolist.ui.theme.ToDoListTheme

/**
 * MainActivity serves as the entry point for the To-Do List app.
 * It initializes the database helper and sets up the Compose UI using the custom theme.
 */
class MainActivity : ComponentActivity() {
    // Declare a database helper instance for SQLite operations.
    private lateinit var dbHelper: TodoDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the database helper
        dbHelper = TodoDatabaseHelper(this)

        // Set the content of the activity using Jetpack Compose.
        setContent {
            // Apply the custom ToDoListTheme to ensure consistent styling.
            ToDoListTheme {
                // Display the main screen which lists the to-do items.
                TodoListScreen(dbHelper)
            }
        }
    }
}

/**
 * TodoListScreen composable function displays the list of to-do items.
 * It also manages the state for displaying the "Add To-Do" dialog and refreshing the list.
 *
 * @param dbHelper: Provides access to SQLite database operations.
 */
@Composable
fun TodoListScreen(dbHelper: TodoDatabaseHelper) {
    // Create a mutable state list that holds the current to-do items, initialized from the database.
    val todoList = remember { mutableStateListOf<Todo>().apply { addAll(dbHelper.getAllTodos()) } }
    // State variable controlling the visibility of the Add To-Do dialog.
    var showDialog by remember { mutableStateOf(false) }

    // Function to refresh the list by clearing and repopulating it from the database.
    // Tasks are sorted so that completed tasks appear at the bottom.
    fun refreshList() {
        todoList.clear()
        todoList.addAll(dbHelper.getAllTodos().sortedBy { it.isCompleted })
    }

    // Scaffold provides the basic layout structure including a floating action button.
    Scaffold(
        floatingActionButton = {
            // FloatingActionButton that, when clicked, sets showDialog to true to display the Add To-Do dialog.
            FloatingActionButton(onClick = { showDialog = true }) {
                Text("+") // Display a plus sign indicating adding a new task.
            }
        }
    ) { paddingValues ->
        // Main content area arranged in a Column with padding provided by the Scaffold.
        Column(modifier = Modifier.padding(paddingValues)) {
            // LazyColumn efficiently displays a scrollable list of to-do items.
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                // Iterate over the to-do list and display each item using the TodoItem composable.
                items(todoList) { todo ->
                    // Pass the current todo item, the database helper, and the refreshList callback.
                    TodoItem(todo, dbHelper, refreshList = { refreshList() })
                }
            }
        }
    }

    // Display the Add To-Do dialog if showDialog is true.
    if (showDialog) {
        AddTodoDialog(
            onDismiss = { showDialog = false },  // Dismiss the dialog when requested.
            onConfirm = { title: String, priority: Float ->
                // If the title is not empty, create a new Todo object and add it to the database.
                if (title.isNotEmpty()) {
                    val newTodo = Todo(title = title, priority = priority.toInt())
                    dbHelper.addTodo(newTodo)
                    refreshList() // Refresh the list to include the new task.
                }
                showDialog = false // Close the dialog.
            }
        )
    }
}

/**
 * TodoItem composable function displays an individual to-do item.
 * It shows a colored circle representing the task's priority, the task title (with strikethrough if completed),
 * a checkbox to mark the task as completed, and a delete button to remove the task.
 *
 * @param todo: The to-do item data.
 * @param dbHelper: Provides access to update or delete the task in the database.
 * @param refreshList: Callback function to refresh the task list after changes.
 */
@Composable
fun TodoItem(todo: Todo, dbHelper: TodoDatabaseHelper, refreshList: () -> Unit) {
    // Determine the color of the priority circle based on the task's priority level.
    val priorityColor = when (todo.priority) {
        5 -> androidx.compose.ui.graphics.Color.Red            // Highest priority: Red
        4 -> androidx.compose.ui.graphics.Color(0xFFFFA500)        // Priority 4: Orange
        3 -> androidx.compose.ui.graphics.Color.Yellow             // Priority 3: Yellow
        2 -> androidx.compose.ui.graphics.Color.Green              // Priority 2: Green
        else -> androidx.compose.ui.graphics.Color.Blue            // Lowest priority: Blue (for priority 1)
    }

    // Card composable provides a material surface with rounded corners and elevation.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Row to arrange the priority indicator, task title, and action buttons horizontally.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Priority Indicator: Draw a colored circle using Canvas.
            Canvas(
                modifier = Modifier
                    .size(30.dp) // Fixed size to ensure the circle is not cut off.
                    .padding(end = 12.dp) // Padding to separate it from the task title.
            ) {
                drawCircle(color = priorityColor) // Draw the circle with the determined color.
            }

            // Task Title: Display the task's title in a Column to allow for flexible layout.
            Column(
                modifier = Modifier
                    .weight(1f) // Occupy remaining horizontal space.
                    .padding(start = 4.dp) // Small padding for visual balance.
            ) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge,
                    // If the task is marked as completed, apply a strikethrough decoration.
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
            }

            // Action Buttons: Row containing the checkbox and delete button.
            Row {
                // Checkbox for marking the task as completed or not.
                Checkbox(
                    checked = todo.isCompleted,
                    onCheckedChange = { isChecked ->
                        // Update the task's completed state.
                        todo.isCompleted = isChecked
                        // Update the task in the database.
                        dbHelper.updateTodo(todo)
                        // Refresh the list so that completed tasks move to the bottom.
                        refreshList()
                    }
                )
                // IconButton for deleting the task.
                IconButton(onClick = {
                    // Delete the task from the database using its unique ID.
                    dbHelper.deleteTodo(todo.id)
                    // Refresh the list to reflect the deletion.
                    refreshList()
                }) {
                    // Display a delete icon from the Material Icons library.
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Task")
                }
            }
        }
    }
}