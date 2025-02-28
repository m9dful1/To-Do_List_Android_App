package com.spiritwisestudios.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

class MainActivity : ComponentActivity() {
    private lateinit var dbHelper: TodoDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = TodoDatabaseHelper(this)

        setContent {
            MaterialTheme {
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
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                Text(
                    text = "Priority: ${todo.priority}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row {
                Checkbox(
                    checked = todo.isCompleted, // Use the actual property
                    onCheckedChange = { isChecked ->
                        todo.isCompleted = isChecked
                        dbHelper.updateTodo(todo)
                        refreshList() // Refresh list correctly after update
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