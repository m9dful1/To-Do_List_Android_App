package com.spiritwisestudios.todolist

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Composable function that displays a dialog for adding a new to-do item.
 *
 * @param onDismiss Callback invoked when the dialog should be dismissed.
 * @param onConfirm Callback invoked with the entered title and selected priority when the user confirms adding the task.
 */
@Composable
fun AddTodoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Float) -> Unit
) {
    // Mutable state for the task title input by the user.
    var title by remember { mutableStateOf("") }
    // Mutable state for the task priority, defaulting to 1.
    var priority by remember { mutableStateOf(1f) }

    // Determine the color for the priority indicator based on the current priority value.
    // The colors change as priority increases, giving visual feedback.
    val priorityColor = when (priority.toInt()) {
        5 -> Color.Red                       // Highest priority
        4 -> Color(0xFFFFA500)               // Orange for high priority
        3 -> Color.Yellow                    // Yellow for medium priority
        2 -> Color.Green                     // Green for low priority
        else -> Color.Blue                   // Lowest priority (1)
    }

    // Create a dialog that will be dismissed when the user taps outside or when onDismiss is called.
    Dialog(onDismissRequest = onDismiss) {
        // Use a Surface with a medium shape to apply rounded corners defined in MaterialTheme settings.
        Surface(shape = MaterialTheme.shapes.medium) {
            // Arrange UI components vertically with padding.
            Column(modifier = Modifier.padding(16.dp)) {
                // Display the dialog title.
                Text(
                    text = "Add To-Do",
                    style = MaterialTheme.typography.headlineSmall
                )

                // OutlinedTextField for the user to enter the task title.
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Add vertical space between elements.
                Spacer(modifier = Modifier.height(16.dp))

                // Row that displays "Low" and "High" labels on either side of the slider.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Low", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "High", style = MaterialTheme.typography.bodyMedium)
                }

                // Row that contains the slider and the colored circle indicator.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Slider that lets the user select a priority value between 1 and 5.
                    // The steps parameter defines discrete increments.
                    Slider(
                        value = priority,
                        onValueChange = { priority = it },
                        valueRange = 1f..5f,
                        steps = 3,
                        modifier = Modifier.weight(1f)
                    )

                    // Add horizontal space between the slider and the priority indicator.
                    Spacer(modifier = Modifier.width(12.dp))

                    // Canvas used to draw a colored circle as the priority indicator.
                    // The circle's color changes based on the selected priority.
                    Canvas(modifier = Modifier.size(24.dp)) {
                        drawCircle(color = priorityColor)
                    }
                }

                // Add vertical space before the action buttons.
                Spacer(modifier = Modifier.height(16.dp))

                // Row for the Cancel and Add buttons, aligned to the right.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Cancel button that calls onDismiss when clicked.
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    // Spacer between the buttons.
                    Spacer(modifier = Modifier.width(8.dp))
                    // Add button that calls onConfirm with the entered title and selected priority.
                    Button(onClick = { onConfirm(title, priority) }) {
                        Text("Add")
                    }
                }
            }
        }
    }
}