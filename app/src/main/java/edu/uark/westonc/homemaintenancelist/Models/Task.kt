package edu.uark.westonc.homemaintenancelist.Models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import com.google.gson.annotations.Expose

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "localID") val localID: Long?,
    @Expose @ColumnInfo(name="id") val id:String?,
    @Expose @ColumnInfo(name="userID") val userID:String?,
    @Expose @ColumnInfo(name="taskTitle") val taskTitle:String,
    @Expose @ColumnInfo(name="taskDescription") val taskDescription:String,
    @Expose @ColumnInfo(name="taskDeadline") val taskDeadline:String,
    @Expose @ColumnInfo(name="taskComplete") val taskComplete:Boolean,
) {
    // No-Arguement constructor required for Firestore deserialization
    constructor() : this(null, null, null, "", "", "", false)
}
