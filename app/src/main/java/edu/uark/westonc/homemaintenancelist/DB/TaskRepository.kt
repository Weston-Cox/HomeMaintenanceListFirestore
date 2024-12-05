package edu.uark.westonc.homemaintenancelist.DB

import androidx.annotation.WorkerThread
import edu.uark.westonc.homemaintenancelist.Models.Task
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class TaskRepository(private val taskDao: TaskDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allTasks: Flow<Map<Long,Task>> = taskDao.getAlphabetizedTasks()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.

    //******************************************************************************************************
    // insert
    // Description: Inserts a task into the database via the DAO
    // Parameters: Task
    // Returns: Long
    //******************************************************************************************************
    @WorkerThread
    suspend fun insert(task: Task): Long { // Returns the localID of the inserted task
        return taskDao.insert(task)
    }

    //******************************************************************************************************
    // delete
    // Description: Deletes a task from the database via the DAO
    // Parameters: Task
    // Returns: Unit
    //******************************************************************************************************
    @WorkerThread
    suspend fun delete(task: Task) {
        taskDao.delete(task.localID!!)
    }

    //******************************************************************************************************
    // deleteAll
    // Description: Deletes all tasks from the database via the DAO
    // Parameters: None
    // Returns: Unit
    //******************************************************************************************************
    @WorkerThread
    suspend fun deleteAll() {
        taskDao.deleteAll()
    }

    //******************************************************************************************************
    // update
    // Description: Updates a task in the database via the DAO
    // Parameters: Task
    // Returns: Unit
    //******************************************************************************************************
    @WorkerThread
    suspend fun update(task: Task) {
        taskDao.update(task.localID!!, task.id!!, task.userID!!, task.taskTitle, task.taskDescription, task.taskDeadline, task.taskComplete)
    }

    //******************************************************************************************************
    // complete
    // Description: Completes a task in the database via the DAO
    // Parameters: Task
    // Returns: Unit
    //******************************************************************************************************
    @WorkerThread
    suspend fun complete(task: Task) {
        taskDao.complete(task.localID!!)
    }
}