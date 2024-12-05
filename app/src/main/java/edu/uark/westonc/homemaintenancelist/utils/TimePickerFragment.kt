package edu.uark.westonc.homemaintenancelist.utils

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment(val calendar: Calendar,val timeSetCallback:(calendar:Calendar)->Unit) :
    DialogFragment(), TimePickerDialog.OnTimeSetListener {

    //**********************************************************************************************
    //  TimePickerFragment
    //  Description: This is the constructor for the TimePickerFragment class.
    //**********************************************************************************************
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    //**********************************************************************************************
    //  onTimeSet
    //  Description: This function is called when the user sets the time in the TimePickerDialog.
    //**********************************************************************************************
    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
        calendar.set(Calendar.MINUTE,minute)
        timeSetCallback(calendar)
    }
}
