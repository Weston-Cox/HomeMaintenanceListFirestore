package edu.uark.westonc.homemaintenancelist.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.uark.westonc.homemaintenancelist.DB.TaskRepository
import edu.uark.westonc.homemaintenancelist.Models.Task
import kotlinx.coroutines.launch


class TaskDetailScreenViewModel(private val repository: TaskRepository) : ViewModel() {
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    val _task = MutableLiveData<Task>().apply{value=null}
    val task: LiveData<Task>
        get() = _task

    //**********************************************************************************************
    // start
    // Description: Starts the task
    // Parameters: Long
    // Returns: Unit
    //**********************************************************************************************
    fun start(taskId:Long){
        viewModelScope.launch {
            repository.allTasks.collect{
                _task.value = it[taskId]
            }
        }
    }

    //**********************************************************************************************
    // insert
    // Description: Inserts a task into the database
    // Parameters: Task
    // Returns: LiveData<Long>
    //**********************************************************************************************
    fun insert(task: Task): LiveData<Long> {
        val result = MutableLiveData<Long>()
        viewModelScope.launch {
            result.value = repository.insert(task)
        }
        Log.d("TaskDetailScreenViewModel", "Task inserted with localID: ${result.value}")
        return result
    }

    //**********************************************************************************************
    // update
    // Description: Updates a task in the database
    // Parameters: Task
    // Returns: Unit
    //**********************************************************************************************
    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
    }


}

// Factory for constructing TaskDetailScreenViewModel with repository
class TaskDetailScreenViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskDetailScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskDetailScreenViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
