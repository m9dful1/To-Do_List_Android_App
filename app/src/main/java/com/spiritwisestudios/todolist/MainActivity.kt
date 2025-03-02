package com.spiritwisestudios.todolist

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
import com.spiritwisestudios.todolist.ui.theme.ToDoListTheme

class MainActivity : ComponentActivity() {
    private lateinit var dbHelper: TodoDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = TodoDatabaseHelper(this)

        setContent {
            ToDoListTheme {
                TodoListScreen(dbHelper)
            }
        }
    }
}

@Composable
fun TodoListScreen(dbHelper: TodoDatabaseHelper) {
    val todoList = remember { mutableStateListOf<Todo>().apply { addAll(dbHelper.getAllTodos()) } }
    var showDialog by remember { mutableStateOf(false) }

    fun refreshList() {
        todoList.clear()
        todoList.addAll(dbHelper.getAllTodos().sortedBy { it.isCompleted }) // ✅ Moves completed tasks to the bottom
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Text("+")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(todoList) { todo ->
                    TodoItem(todo, dbHelper, refreshList = { refreshList() }) // ✅ Fix function reference
                }
            }
        }
    }

    if (showDialog) {
        AddTodoDialog(
            onDismiss = { showDialog = false },
            onConfirm = { title: String, priority: Float ->
                if (title.isNotEmpty()) {
                    val newTodo = Todo(title = title, priority = priority.toInt())
                    dbHelper.addTodo(newTodo)
                    refreshList()
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun TodoItem(todo: Todo, dbHelper: TodoDatabaseHelper, refreshList: () -> Unit) {
    val priorityColor = when (todo.priority) {
        5 -> androidx.compose.ui.graphics.Color.Red
        4 -> androidx.compose.ui.graphics.Color(0xFFFFA500) // Orange
        3 -> androidx.compose.ui.graphics.Color.Yellow
        2 -> androidx.compose.ui.graphics.Color.Green
        else -> androidx.compose.ui.graphics.Color.Blue // Priority 1
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Colored Circle using Canvas (Fixes cut-off issue)
            Canvas(
                modifier = Modifier
                    .size(30.dp) // Ensures a perfect circle size
                    .padding(end = 12.dp)
            ) {
                drawCircle(color = priorityColor)
            }

            // Task Title - Ensuring proper space distribution
            Column(
                modifier = Modifier
                    .weight(1f) // Ensures title expands properly
                    .padding(start = 4.dp) // Added small padding for visual balance
            ) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
            }

            // Checkbox & Delete Button
            Row {
                Checkbox(
                    checked = todo.isCompleted,
                    onCheckedChange = { isChecked ->
                        todo.isCompleted = isChecked
                        dbHelper.updateTodo(todo)
                        refreshList()
                    }
                )

                IconButton(onClick = {
                    dbHelper.deleteTodo(todo.id)
                    refreshList()
                }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Task")
                }
            }
        }
    }
}