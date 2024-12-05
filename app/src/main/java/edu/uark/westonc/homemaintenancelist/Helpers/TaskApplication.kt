package edu.uark.westonc.homemaintenancelist.Helpers

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import edu.uark.westonc.homemaintenancelist.DB.TaskDatabase
import edu.uark.westonc.homemaintenancelist.DB.TaskRepository

class TaskApplication: Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { TaskDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { TaskRepository(database.taskDao()) }
}