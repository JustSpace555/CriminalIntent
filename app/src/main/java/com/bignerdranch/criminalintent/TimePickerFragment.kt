package com.bignerdranch.criminalintent

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_TIME = "time"

class TimePickerFragment : DialogFragment() {

    interface Callbacks {
        fun onTimeSet(date: Date)
    }

    companion object {
        fun newInstance(date: Date): TimePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_TIME, date)
            }
            return TimePickerFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val clock = Calendar.getInstance()
        clock.time = arguments?.getSerializable(ARG_TIME) as Date
        val timeListener = TimePickerDialog.OnTimeSetListener {
            _: TimePicker, hour: Int, minute: Int ->
            val resultTime : Date = GregorianCalendar(clock.get(Calendar.YEAR),
                clock.get(Calendar.MONTH), clock.get(Calendar.DAY_OF_MONTH), hour, minute).time

            targetFragment?.let { fragment ->
                (fragment as Callbacks).onTimeSet(resultTime)
            }
        }
        return TimePickerDialog(requireContext(),
            timeListener, clock.get(Calendar.HOUR), clock.get(Calendar.MINUTE), true)
    }
}