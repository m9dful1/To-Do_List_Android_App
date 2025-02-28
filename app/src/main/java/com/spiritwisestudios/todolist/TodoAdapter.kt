package com.spiritwisestudios.todolist  // Organizes the file within the project package

import android.graphics.Paint
import android.view.LayoutInflater      // Inflates layout XML files into View objects
import android.view.View                // Base class for all UI components
import android.view.ViewGroup           // Container for views; used in RecyclerView item layouts
import android.widget.CheckBox          // Widget for displaying a checkbox (for marking tasks complete/incomplete)
import android.widget.ImageButton       // Widget for a button with an image (for the delete action)
import android.widget.TextView          // Widget for displaying text (for the task title)
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView // Provides a flexible view for displaying lists efficiently

// TodoAdapter binds a list of Todo items to a RecyclerView and handles user interactions
class TodoAdapter(
    private val todos: MutableList<Todo>,                   // List of Todo items displayed in the RecyclerView
    private val listener: OnTodoItemClickListener           // Listener for handling item interactions (checkbox changes and deletion)
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {    // Extends RecyclerView.Adapter with a custom ViewHolder

    // Interface defining callbacks for Todo item interactions
    interface OnTodoItemClickListener {
        fun onTodoCheckedChanged(todo: Todo, isChecked: Boolean) // Triggered when a Todo's checkbox is toggled
        fun onDeleteClicked(todo: Todo)                          // Triggered when the delete button is clicked for a Todo item
    }

    // ViewHolder class that holds the views for each Todo item in the list
    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxCompleted)  // Checkbox for marking task completion
        val titleTextView: TextView = itemView.findViewById(R.id.todoTitle)     // TextView for displaying the task title
        val deleteButton: ImageButton = itemView.findViewById(R.id.btnDelete)   // Button for deleting the task
        val priorityIndicator: View = itemView.findViewById(R.id.priorityIndicator) // View to display the priority color
    }

    // Called when RecyclerView needs a new ViewHolder to display an item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)   // Inflates the item_todo layout
        return TodoViewHolder(view)                                                                 // Returns a new instance of TodoViewHolder with the inflated view
    }

    // Called to bind data to a ViewHolder for a specific position in the list
    // Called to bind data to a ViewHolder for a specific position in the list
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todos[position]                        // Retrieves the Todo item for the current position
        holder.titleTextView.text = todo.title              // Sets the task title in the TextView
        holder.checkBox.isChecked = todo.isCompleted        // Sets the checkbox state based on task completion status

        // Apply strike-through effect if the task is completed, otherwise remove it
        if (todo.isCompleted) {
            holder.titleTextView.paintFlags = holder.titleTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.titleTextView.paintFlags = holder.titleTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        // Set the priority indicator color based on the Todo's priority value
        val colorRes = when (todo.priority) {
            5 -> R.color.priority5
            4 -> R.color.priority4
            3 -> R.color.priority3
            2 -> R.color.priority2
            else -> R.color.priority1
        }
        val color = ContextCompat.getColor(holder.itemView.context, colorRes)
        // Tint the background of the indicator view (its shape is defined in circle.xml)
        holder.priorityIndicator.background.setTint(color)

        // Handle checkbox state changes: Update task status via the listener callback
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                listener.onTodoCheckedChanged(todo, isChecked)
            }
        }

        // Handle delete button click: Remove the task via the listener callback
        holder.deleteButton.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                listener.onDeleteClicked(todo)
            }
        }
    }

    override fun getItemCount(): Int = todos.size // Returns the total number of Todo items

    // updateTodos() replaces the current Todo list with a new list and refreshes the RecyclerView
    fun updateTodos(newTodos: List<Todo>) {
        todos.clear()              // Clears the existing Todo list
        todos.addAll(newTodos)     // Adds all items from the new list
        notifyDataSetChanged()     // Notifies the adapter to refresh the RecyclerView
    }
}