package edu.uark.westonc.homemaintenancelist.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment(val dateSetCallback:(calendar:Calendar)->Unit) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireContext(), this, year, month, day)

    }

    //**********************************************************************************************
    //  onDateSet
    //  Description: This function is called when the user sets the date in the DatePickerDialog.
    //**********************************************************************************************
    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val cal = Calendar.getInstance()
        cal.set(year,month,day)
        dateSetCallback(cal)
    }
}
