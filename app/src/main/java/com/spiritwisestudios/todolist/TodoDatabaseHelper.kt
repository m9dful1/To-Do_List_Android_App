package com.spiritwisestudios.todolist

import android.content.ContentValues // For creating key-value pairs used in SQL operations
import android.content.Context // Provides access to application-specific resources and classes
import android.database.sqlite.SQLiteDatabase // Represents the SQLite database instance
import android.database.sqlite.SQLiteOpenHelper // Helps manage database creation and version management

/**
 * TodoDatabaseHelper is a helper class that manages the creation, versioning,
 * and operations (CRUD) on the SQLite database used to store to-do items.
 */
class TodoDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Companion object holds constants that define the database and table schema.
    companion object {
        private const val DATABASE_NAME = "todo.db"     // The name of the SQLite database file
        private const val DATABASE_VERSION = 2          // Database version; increment when schema changes are made

        // Table and column names used in the database
        const val TABLE_TODO = "todo"                   // The name of the table that stores to-do items
        const val COLUMN_ID = "id"                      // Unique identifier for each task (primary key)
        const val COLUMN_TITLE = "title"                // The title or description of the task
        const val COLUMN_COMPLETED = "completed"        // Integer flag (0 or 1) indicating if the task is completed
        const val COLUMN_PRIORITY = "priority"          // The task's priority level, with a default value of 1
    }

    /**
     * Called when the database is created for the first time.
     * This method creates the "todo" table with columns for id, title, completed status, and priority.
     */
    override fun onCreate(db: SQLiteDatabase) {
        // SQL statement to create the "todo" table with an auto-incrementing ID,
        // a text column for the title, an integer column for completed status,
        // and an integer column for priority with a default value of 1.
        val createTable = ("CREATE TABLE $TABLE_TODO ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_TITLE TEXT, "
                + "$COLUMN_COMPLETED INTEGER, "
                + "$COLUMN_PRIORITY INTEGER DEFAULT 1)") // Default priority is set to 1

        // Execute the SQL statement to create the table
        db.execSQL(createTable)
    }

    /**
     * Called when the database needs to be upgraded (e.g., when DATABASE_VERSION increases).
     * This method handles the schema migration.
     *
     * @param db The SQLite database instance.
     * @param oldVersion The previous version number of the database.
     * @param newVersion The new version number to upgrade to.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Check if the old database version is less than 2.
        if (oldVersion < 2) {
            // If upgrading from version 1 to 2, add the 'priority' column to the "todo" table.
            // The column is added with a default value of 1.
            db.execSQL("ALTER TABLE $TABLE_TODO ADD COLUMN $COLUMN_PRIORITY INTEGER DEFAULT 1")
        }
        // Future schema upgrades can be handled with additional conditions here.
    }

    /**
     * Inserts a new to-do item into the database.
     *
     * @param todo The Todo object containing title, completed status, and priority.
     * @return The row ID of the newly inserted to-do item, or -1 if an error occurred.
     */
    fun addTodo(todo: Todo): Long {
        // Open the database for writing.
        val db = this.writableDatabase
        // Create a ContentValues object to hold the values for insertion.
        val values = ContentValues().apply {
            put(COLUMN_TITLE, todo.title)                           // Add the task title
            put(COLUMN_COMPLETED, if (todo.isCompleted) 1 else 0)   // Convert boolean to integer (1 for true, 0 for false)
            put(COLUMN_PRIORITY, todo.priority)                     // Add the task's priority
        }
        // Insert the new row into the "todo" table and capture the new row's ID.
        val id = db.insert(TABLE_TODO, null, values)
        // Close the database connection.
        db.close()
        // Return the ID of the inserted row.
        return id
    }

    /**
     * Retrieves all to-do items from the database.
     *
     * @return A List of Todo objects sorted by priority (highest first) and then by ID.
     */
    fun getAllTodos(): List<Todo> {
        // Create an empty mutable list to hold the to-do items.
        val todoList = mutableListOf<Todo>()
        // Define the SQL query to select all rows from the "todo" table,
        // sorting by priority in descending order (highest priority first)
        // and then by ID in ascending order.
        val selectQuery = "SELECT * FROM $TABLE_TODO ORDER BY $COLUMN_PRIORITY DESC, $COLUMN_ID ASC"
        // Open the database for reading.
        val db = this.readableDatabase
        // Execute the query and get a cursor to iterate through the results.
        val cursor = db.rawQuery(selectQuery, null)

        // If the cursor is not empty, iterate over each row.
        if (cursor.moveToFirst()) {
            do {
                // Extract values from the current row.
                val todo = Todo(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),                        // Get task ID
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),               // Get task title
                    isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1,   // Convert integer to boolean
                    priority = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY))             // Get task priority
                )
                // Add the Todo object to the list.
                todoList.add(todo)
            } while (cursor.moveToNext()) // Move to the next row.
        }
        // Close the cursor and the database connection.
        cursor.close()
        db.close()
        // Return the list of Todo items.
        return todoList
    }

    /**
     * Updates an existing to-do item in the database.
     *
     * @param todo The Todo object containing updated data.
     * @return The number of rows affected by the update.
     */
    fun updateTodo(todo: Todo): Int {
        // Open the database for writing.
        val db = this.writableDatabase
        // Create a ContentValues object and add the updated values.
        val values = ContentValues().apply {
            put(COLUMN_TITLE, todo.title)                           // Update the task title
            put(COLUMN_COMPLETED, if (todo.isCompleted) 1 else 0)   // Update the completed flag
            put(COLUMN_PRIORITY, todo.priority)                     // Update the priority value
        }
        // Update the row in the "todo" table where the ID matches the provided Todo's ID.
        val rowsAffected = db.update(TABLE_TODO, values, "$COLUMN_ID=?", arrayOf(todo.id.toString()))
        // Close the database connection.
        db.close()
        // Return the number of rows that were affected by the update.
        return rowsAffected
    }

    /**
     * Deletes a to-do item from the database.
     *
     * @param id The unique ID of the to-do item to delete.
     * @return The number of rows deleted.
     */
    fun deleteTodo(id: Int): Int {
        // Open the database for writing.
        val db = this.writableDatabase
        // Delete the row in the "todo" table where the ID matches the provided id.
        val rowsDeleted = db.delete(TABLE_TODO, "$COLUMN_ID=?", arrayOf(id.toString()))
        // Close the database connection.
        db.close()
        // Return the number of rows that were deleted.
        return rowsDeleted
    }
}