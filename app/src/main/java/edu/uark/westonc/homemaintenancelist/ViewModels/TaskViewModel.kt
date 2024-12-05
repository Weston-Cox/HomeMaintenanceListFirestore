package edu.uark.westonc.homemaintenancelist.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import edu.uark.westonc.homemaintenancelist.DB.TaskRepository
import edu.uark.westonc.homemaintenancelist.Models.Task
import kotlinx.coroutines.launch


class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allTasks: LiveData<Map<Long,Task>> = repository.allTasks.asLiveData()
//    val firebaseDB: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Launching a new coroutine to change the data in a non-blocking way
     */

    fun delete(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }

    fun insert(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun complete(task: Task) = viewModelScope.launch {
        repository.complete(task)
    }
}

class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}