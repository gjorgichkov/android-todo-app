package com.example.todo.data

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todo.worker.ToDoReminderWorker
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class OfflineToDosRepository(
    context: Context,
    private val toDoDao: ToDoDao
) : ToDosRepository {

    private val workManager = WorkManager.getInstance(context)

    override fun getAllToDosStream(): Flow<List<ToDo>> {
        return toDoDao.getAllToDos()
    }

    override fun getToDoStream(id: Int): Flow<ToDo?> {
        return toDoDao.getToDo(id)
    }

    override suspend fun insertToDo(toDo: ToDo) {
        toDoDao.insert(toDo)
    }

    override suspend fun updateToDo(toDo: ToDo) {
        toDoDao.update(toDo)
    }

    override suspend fun deleteToDo(toDo: ToDo) {
        toDoDao.delete(toDo)
    }

    override fun scheduleReminder(
        duration: Long,
        unit: TimeUnit,
        toDoTitle: String
    ) {
        val data = Data.Builder()
        data.putString(ToDoReminderWorker.nameKey, toDoTitle)

        val workRequestBuilder = OneTimeWorkRequestBuilder<ToDoReminderWorker>()
            .setInitialDelay(duration, unit)
            .setInputData(data.build())
            .build()

        workManager.enqueueUniqueWork(
            toDoTitle + duration,
            ExistingWorkPolicy.REPLACE,
            workRequestBuilder
        )
    }
}