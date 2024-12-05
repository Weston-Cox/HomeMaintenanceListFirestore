package edu.uark.westonc.homemaintenancelist.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import edu.uark.westonc.homemaintenancelist.Models.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Annotates class to be a Room Database with a table (entity) of the Task class
@Database(entities = arrayOf(Task::class), version = 5, exportSchema = false)
public abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TaskDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                ).addCallback(TaskDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
    private class TaskDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    database.taskDao().deleteAll()
                }
            }
        }

//        suspend fun populateDatabase(taskDao: TaskDao) {
//            // Delete all content here.
//            taskDao.deleteAll()
//
//            // Add sample words.
//            var task = Task(null,"Hello", "", "", false)
//            taskDao.insert(task)
//            task = Task(null,"World!", "", "", false)
//            taskDao.insert(task)
//
//            // TODO: Add your own words!
//        }
    }
}