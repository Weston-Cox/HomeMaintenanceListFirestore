package edu.uark.westonc.homemaintenancelist.Activities

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.DateFormat
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import edu.uark.westonc.homemaintenancelist.Helpers.AlarmReceiver
import edu.uark.westonc.homemaintenancelist.Helpers.TaskApplication
import edu.uark.westonc.homemaintenancelist.Models.Task
import edu.uark.westonc.homemaintenancelist.R
import edu.uark.westonc.homemaintenancelist.ViewModels.TaskDetailScreenViewModel
import edu.uark.westonc.homemaintenancelist.ViewModels.TaskDetailScreenViewModelFactory
import edu.uark.westonc.homemaintenancelist.utils.DatePickerFragment
import edu.uark.westonc.homemaintenancelist.utils.NotificationUtil
import edu.uark.westonc.homemaintenancelist.utils.TimePickerFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class TaskDetailScreenActivity : AppCompatActivity() {
    private val TAG = "TaskDetailScreenActivity"

    private lateinit var task: Task
    private lateinit var taskTitle: EditText
    private lateinit var taskDetails: EditText
    private lateinit var completeCheckBox: CheckBox
    private lateinit var dateTimeBtn: Button
    private lateinit var saveBtn: Button
    private lateinit var cancelBtn: Button
    private lateinit var firebaseDB: FirebaseFirestore
    var notificationTimeMillis: Long = 0
    var notificationId: Int = 0
    var newTask: Boolean = false
    var taskID: Long = -1
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                NotificationUtil().createNotificationChannel(this)
                scheduleNotification()
            } else {
                Toast.makeText(this,
                    "Unable to schedule notification",
                    Toast.LENGTH_SHORT)
                    .show()
            }
        }

    val newTaskViewModel: TaskDetailScreenViewModel by viewModels {
        TaskDetailScreenViewModelFactory((application as TaskApplication).repository)
    }

    //***********************************************************************************************
    // onCreate
    // Description: This function is called when the activity is created. It sets up the toolbar and
    // the onClickListeners for the date and time button, the save button, and the cancel button.
    // Overridden function.
    //***********************************************************************************************
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task_detail_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userID = intent.getStringExtra("USER_ID")

        val firebaseDB = FirebaseFirestore.getInstance()

        // Set up the toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.materialToolbar)
        val calendar: Calendar = Calendar.getInstance()

        taskTitle = findViewById(R.id.etItemTitle)
        taskDetails = findViewById(R.id.etItemDetails)
        completeCheckBox = findViewById(R.id.cbComplete)
        dateTimeBtn = findViewById(R.id.btnDateTime)

        val localID = intent.getLongExtra("id",-1)
        //If it doesn't exist, create a new Task object
        if(localID == (-1).toLong()){
            task = Task(null, null, userID,"", "", "", false)
            newTask = true
            dateTimeBtn.setText(
                java.text.DateFormat.getDateTimeInstance(
                    DateFormat.DEFAULT,
                    DateFormat.SHORT
                ).format(calendar.timeInMillis)
            )
        }else{
            //Otherwise, start the viewModel with the localID
            //And begin observing the task to set all the values
            newTask = false
            newTaskViewModel.start(localID)
            newTaskViewModel.task.observe(this){
                if(it != null){
                    taskID = it.localID!!
                    taskTitle.setText(it.taskTitle)
                    taskDetails.setText(it.taskDescription)
                    dateTimeBtn.setText(it.taskDeadline)
                    completeCheckBox.isChecked = it.taskComplete
                }
            }
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        // Set the onClickListener for the date and time button
        dateTimeBtn.setOnClickListener {
            dateClicked(it)
        }

        saveBtn = findViewById(R.id.btnSave)
        // Save button's onClickListener
        saveBtn.setOnClickListener {

            if (TextUtils.isEmpty(taskTitle.text) || TextUtils.isEmpty(taskDetails.text)) {
                val snackBar = Snackbar.make(it, "Incomplete Task. Please fill out all fields", Snackbar.LENGTH_LONG)
                snackBar.show()
            } else {

                val taskTitle = taskTitle.text.toString()
                val taskDetails = taskDetails.text.toString()
                val taskNotifTime = dateTimeBtn.text.toString()
                val taskComplete = completeCheckBox.isChecked

                if (newTask) { // If this is a new task, then insert a new task and return the generated localID
                    val newTaskObject = Task(null, null, userID, taskTitle, taskDetails, taskNotifTime, taskComplete)

                    newTaskViewModel.insert(newTaskObject).observe(this) { localID ->
                        taskID = localID

                        firebaseDB.collection("users").document(userID!!).collection("tasks")
                            .add(newTaskObject)
                            .addOnSuccessListener { documentReference ->
                                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")

                                // Update the task with the documentReference ID
                                newTaskViewModel.update(
                                    Task(
                                        localID,
                                        documentReference.id,
                                        userID,
                                        taskTitle,
                                        taskDetails,
                                        taskNotifTime,
                                        taskComplete
                                    )
                                )

                                // Update the task with the documentReference ID in Firestore
                                firebaseDB.collection("users").document(userID).collection("tasks")
                                    .document(documentReference.id)
                                    .update("localID", localID, "id", documentReference.id)
                                    .addOnSuccessListener { Log.d(TAG, "id successfully written!") }
                                    .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }

                        // Check for notification privilege and time
                        if (checkNotificationPrivilege()) {
                            scheduleNotification()
                        }

                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                } else { // If this is not a new task, then update the task
                    newTaskViewModel.task.value?.let { it1 ->
                        newTaskViewModel.update(
                            Task(
                                it1.localID,
                                it1.id,
                                it1.userID,
                                taskTitle,
                                taskDetails,
                                taskNotifTime,
                                taskComplete
                            )
                        )
                        taskID = it1.localID!!

                        CoroutineScope(Dispatchers.IO).launch {

                            try {
                                firebaseDB.collection("users").document(it1.userID!!).collection("tasks")
                                    .document(it1.id!!)
                                    .update(
                                        "localID", it1.localID,
                                        "taskTitle", taskTitle,
                                        "taskDescription", taskDetails,
                                        "taskDeadline", taskNotifTime,
                                        "taskComplete", taskComplete
                                    ).await()
                                // Check for notification privilege and time
                                if (checkNotificationPrivilege()) {
                                    scheduleNotification()
                                }

                                runOnUiThread {
                                    setResult(Activity.RESULT_OK)
                                    finish()
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error saving task:", e)
                            }
                        }

                    }
                }
            }
        }

        cancelBtn = findViewById(R.id.btnCancel)
        cancelBtn.setOnClickListener {
            val replyIntent = Intent()
            setResult(Activity.RESULT_CANCELED, replyIntent)
            finish()
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }


    //***********************************************************************************************
    // checkNotificationPrivilege
    // Description: This function checks if the app has the permission to post notifications. If it
    // does not, it will request the permission from the user. Private function.
    //***********************************************************************************************
    private fun checkNotificationPrivilege(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationUtil().createNotificationChannel(this)
            return true
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return false
            }
            return true
        }
    }


    //***********************************************************************************************
    // scheduleNotification`
    // Description: This function is called when the user saves a task. It schedules a notification
    // for the task at the time specified by the user.
    //***********************************************************************************************
    private fun scheduleNotification() {
        Log.d("TestingMillisTime", "Notification Time: $notificationTimeMillis Current Time: ${Calendar.getInstance().timeInMillis}")
        if(notificationTimeMillis > Calendar.getInstance().timeInMillis){
            Log.d(TAG,"Scheduling Notification")

            // Cancel any existing notification
            cancelNotification()

            val alarmManager = this.applicationContext.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val alarmIntent = Intent(this.applicationContext, AlarmReceiver::class.java)

            alarmIntent.putExtra(getString(R.string.EXTRA_NOTIFICATION_ID), notificationId)
            alarmIntent.putExtra(getString(R.string.EXTRA_TITLE), taskTitle.text.toString())
            alarmIntent.putExtra("id", taskID)

            val pendingAlarmIntent = PendingIntent.getBroadcast(this.applicationContext,
                notificationId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager?.setWindow(AlarmManager.RTC_WAKEUP,(notificationTimeMillis - (1000 * 30)) ,1000*10 , pendingAlarmIntent)
            notificationId++
        }
    }

    //***********************************************************************************************
    // cancelNotification
    // Description: This function is called when the user cancels a notification. It cancels the
    // notification that was scheduled previously.
    //***********************************************************************************************
    private fun cancelNotification() {
        val alarmManager = this.applicationContext.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val alarmIntent = Intent(this.applicationContext, AlarmReceiver::class.java)

        alarmIntent.putExtra(getString(R.string.EXTRA_NOTIFICATION_ID), notificationId)
        alarmIntent.putExtra(getString(R.string.EXTRA_TITLE), taskTitle.text.toString())
        alarmIntent.putExtra("id", taskID)

        val pendingAlarmIntent = PendingIntent.getBroadcast(this.applicationContext,
            notificationId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager?.cancel(pendingAlarmIntent)
    }


    //***********************************************************************************************
    // dateSet
    // Description: This function is called when the date button is clicked. It creates a new
    // TimePickerFragment and shows it to the user.
    //***********************************************************************************************
    private fun dateSet(calendar: Calendar){
        TimePickerFragment(calendar,this::timeSet).show(supportFragmentManager, "timePicker")
    }

    //***********************************************************************************************
    // timeSet
    // Description: This function is called when the time button is clicked. It sets the text of the
    // date and time button to the selected date and time.
    //***********************************************************************************************
    private fun timeSet(calendar: Calendar){
        dateTimeBtn.setText(java.text.DateFormat.getDateTimeInstance(
            DateFormat.DEFAULT,
            DateFormat.SHORT
        ).format(calendar.timeInMillis))
        notificationTimeMillis = calendar.timeInMillis
    }

    //***********************************************************************************************
    //  dateClicked
    //  Description: This function is called when the date button is clicked. It creates a new
    //  DatePickerFragment and shows it to the user.
    //***********************************************************************************************
    @Suppress("unused")
    private fun dateClicked(view: View){
        Log.d(TAG,"Date Clicked: ${view.id}")
        DatePickerFragment(this::dateSet).show(supportFragmentManager, "datePicker")
    }
}