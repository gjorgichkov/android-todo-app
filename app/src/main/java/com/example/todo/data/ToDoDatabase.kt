package com.example.todo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ToDo::class],
    version = 1,
    exportSchema = false
)
abstract class ToDoDatabase : RoomDatabase() {

    abstract fun toDoDao(): ToDoDao

    companion object {
        private var Instance: ToDoDatabase? = null

        fun getDatabase(context: Context): ToDoDatabase {
            return Instance ?: synchronized(this) {
                Room
                    .databaseBuilder(
                        context = context,
                        klass = ToDoDatabase::class.java,
                        name = "todo_database"
                    )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}