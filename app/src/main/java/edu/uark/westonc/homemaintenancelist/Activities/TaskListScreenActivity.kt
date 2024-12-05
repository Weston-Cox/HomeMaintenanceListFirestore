package edu.uark.westonc.homemaintenancelist.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import edu.uark.westonc.homemaintenancelist.Helpers.TaskApplication
import edu.uark.westonc.homemaintenancelist.Helpers.TaskListAdapter
import edu.uark.westonc.homemaintenancelist.Models.Task
import edu.uark.westonc.homemaintenancelist.R
import edu.uark.westonc.homemaintenancelist.ViewModels.TaskViewModel
import edu.uark.westonc.homemaintenancelist.ViewModels.TaskViewModelFactory
import edu.uark.westonc.homemaintenancelist.utils.SwipeCallback

class TaskListScreenActivity : AppCompatActivity() {

    private lateinit var firebaseDB: FirebaseFirestore
    private lateinit var task: Task
    private val TAG = "TaskListScreenActivity"


    val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory((application as TaskApplication).repository)
    }

    //******************************************************************************************************
    // launchNewTaskActivity
    // Description: Launches the TaskDetailScreenActivity. Used as a callback to the TaskListAdapter
    // Parameters: Long
    // Returns: Unit
    //******************************************************************************************************
    fun launchNewTaskActivity(id: Long) {
        val secondActivityIntent = Intent (this, TaskDetailScreenActivity::class.java)
        secondActivityIntent.putExtra("id", id)
        this.startActivity(secondActivityIntent)
    }

    //******************************************************************************************************
    // onCreate
    // Description: Called when the activity is starting
    // Parameters: Bundle
    // Returns: Unit
    //******************************************************************************************************
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task_list_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Delete all tasks from the database
        taskViewModel.deleteAll()

        // Retrieve user information
        val userID = intent.getStringExtra("USER_ID")
        val userEmail = intent.getStringExtra("USER_EMAIL")

        // Initialize the firebase database
        firebaseDB = FirebaseFirestore.getInstance()

        // Use userID to query the Firestore database
        if (userID != null) {
            firebaseDB.collection("users").document(userID).collection("tasks")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        val task = document.toObject(Task::class.java).copy(localID = null)
                        taskViewModel.insert(task)
                    }
        }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }

        // Get a reference to the recycler view object
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        // Create an adapter object, passing the launchNewTaskActivity callback
        val adapter = TaskListAdapter(this::launchNewTaskActivity)
        // Set the adapter for the recycler view to the adapter object
        recyclerView.adapter = adapter
        // Set the recycler view layout to be a linear layout manager with activity context
        recyclerView.layoutManager = LinearLayoutManager(this)
        //Start observing the words list (now map), and pass updates through
        //to the adapter
        val itemTouchHelper = ItemTouchHelper(SwipeCallback(adapter, taskViewModel, userID!!))
        itemTouchHelper.attachToRecyclerView(recyclerView)

        taskViewModel.allTasks.observe(this) { tasks ->
            // Update the cached copy of the words in the adapter.
            tasks?.let { adapter.submitList(it.values.toList()) } // Change map to a list
        }
        val fab = findViewById<FloatingActionButton>(R.id.fabAddTask) // Gets the floating action button
        fab.setOnClickListener {
            val intent = Intent(this@TaskListScreenActivity, TaskDetailScreenActivity::class.java)
            intent.putExtra("USER_ID", userID)
            startActivity(intent)
        }

        findViewById<FloatingActionButton>(R.id.fabLogout).setOnClickListener {
            val intent = Intent(this@TaskListScreenActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}