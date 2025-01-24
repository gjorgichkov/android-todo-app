package com.example.todo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoDao {
    @Query("SELECT * FROM todos ORDER BY title ASC")
    fun getAllToDos(): Flow<List<ToDo>>

    @Query("SELECT * FROM todos WHERE id = :id")
    fun getToDo(id: Int): Flow<ToDo>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(toDo: ToDo)

    @Update
    suspend fun update(toDo: ToDo)

    @Delete
    suspend fun delete(toDo: ToDo)
}