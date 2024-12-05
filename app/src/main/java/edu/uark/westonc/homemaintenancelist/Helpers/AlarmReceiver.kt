package edu.uark.westonc.homemaintenancelist.Helpers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import edu.uark.westonc.homemaintenancelist.Activities.TaskDetailScreenActivity
import edu.uark.westonc.homemaintenancelist.R
import edu.uark.westonc.homemaintenancelist.utils.NotificationUtil

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val notificationID = intent.getIntExtra(context.getString(R.string.EXTRA_NOTIFICATION_ID),0)
        val title = intent.getStringExtra(context.getString(R.string.EXTRA_TITLE))
        val taskID = intent.getLongExtra("id", -1) // Get the id from the intent

        Log.d("AlarmReceiver", notificationID.toString())
        val clickIntent:Intent = Intent(context, TaskDetailScreenActivity::class.java) // Where the TaskDetailScreenActivity is opened
        clickIntent.putExtra(context.getString(R.string.EXTRA_NOTIFICATION_ID),notificationID)
        clickIntent.putExtra("id",taskID) // Pass the id to the TaskDetailScreenActivity

        NotificationUtil().createClickableNotification(context,"$title","Your task is due",clickIntent, notificationID)
    }
}