package edu.uark.westonc.homemaintenancelist.DB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.uark.westonc.homemaintenancelist.Models.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {


    @MapInfo(keyColumn = "localID")
    @Query("SELECT * FROM task_table ORDER BY taskComplete ASC")
    fun getAlphabetizedTasks(): Flow<Map<Long,Task>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task): Long // Returns the localID of the inserted task

    @Query("DELETE FROM task_table")
    suspend fun deleteAll()

    @Query("DELETE FROM task_table WHERE localID = :localID")
    suspend fun delete(localID: Long)

    @Query("UPDATE task_table SET id = :id, userID = :userID, taskTitle = :taskTitle, taskDescription = :taskDescription, taskDeadline = :taskDeadline, taskComplete = :taskComplete WHERE localID = :localID")
    suspend fun update(localID: Long, id:String, userID:String, taskTitle: String, taskDescription: String, taskDeadline: String, taskComplete: Boolean)

    @Query("UPDATE task_table SET taskComplete = 1 WHERE localID = :localID")
    suspend fun complete(localID: Long)
}