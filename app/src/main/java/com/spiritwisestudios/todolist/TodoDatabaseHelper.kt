package com.spiritwisestudios.todolist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TodoDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "todo.db"
        private const val DATABASE_VERSION = 2  // Incremented version to apply schema updates

        const val TABLE_TODO = "todo"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_COMPLETED = "completed"
        const val COLUMN_PRIORITY = "priority"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_TODO ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_TITLE TEXT, "
                + "$COLUMN_COMPLETED INTEGER, "
                + "$COLUMN_PRIORITY INTEGER DEFAULT 1)") // Default priority set to 1

        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // If upgrading from version 1 to 2, add 'priority' column
            db.execSQL("ALTER TABLE $TABLE_TODO ADD COLUMN $COLUMN_PRIORITY INTEGER DEFAULT 1")
        }
        // Future upgrades can be added here with additional conditions
    }

    fun addTodo(todo: Todo): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, todo.title)
            put(COLUMN_COMPLETED, if (todo.isCompleted) 1 else 0)
            put(COLUMN_PRIORITY, todo.priority)
        }
        val id = db.insert(TABLE_TODO, null, values)
        db.close()
        return id
    }

    fun getAllTodos(): List<Todo> {
        val todoList = mutableListOf<Todo>()
        val selectQuery = "SELECT * FROM $TABLE_TODO ORDER BY $COLUMN_PRIORITY DESC, $COLUMN_ID ASC" // Sort by priority (highest first)
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val todo = Todo(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1,
                    priority = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY))
                )
                todoList.add(todo)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return todoList
    }

    fun updateTodo(todo: Todo): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, todo.title)
            put(COLUMN_COMPLETED, if (todo.isCompleted) 1 else 0)
            put(COLUMN_PRIORITY, todo.priority)
        }
        val rowsAffected = db.update(TABLE_TODO, values, "$COLUMN_ID=?", arrayOf(todo.id.toString()))
        db.close()
        return rowsAffected
    }

    fun deleteTodo(id: Int): Int {
        val db = this.writableDatabase
        val rowsDeleted = db.delete(TABLE_TODO, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted
    }
}