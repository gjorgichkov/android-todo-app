package com.example.todo.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todo.R

class ToDoReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        val toDoTitle = inputData.getString(nameKey)

        makeToDoReminderNotification(
            applicationContext.resources.getString(R.string.time_to_do, toDoTitle),
            applicationContext
        )

        return Result.success()
    }

    companion object {
        const val nameKey = "NAME"
    }
}
