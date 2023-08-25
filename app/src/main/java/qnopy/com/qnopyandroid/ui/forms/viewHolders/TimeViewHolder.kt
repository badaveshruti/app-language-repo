package qnopy.com.qnopyandroid.ui.forms.viewHolders

import android.app.TimePickerDialog
import android.content.Context
import android.view.View
import android.widget.TimePicker
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.MetaData
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.ui.activity.FormActivity
import qnopy.com.qnopyandroid.ui.forms.FormOperations
import qnopy.com.qnopyandroid.ui.forms.scopeIO
import qnopy.com.qnopyandroid.ui.forms.scopeMainThread
import qnopy.com.qnopyandroid.util.Util
import java.util.*

class TimeViewHolder(view: View, private val formOperations: FormOperations) :
    RecyclerView.ViewHolder(view) {
    val tvTimeLabel: CustomTextView = view.findViewById(R.id.tvTimeLabel)
    val tvTime: CustomTextView = view.findViewById(R.id.tvTime)

    fun setTimeClickListener(metaData: MetaData, context: Context) {
        scopeMainThread.launch {
            tvTime.setOnClickListener {

                val mCurrentTime = Calendar.getInstance()
                val hour = mCurrentTime[Calendar.HOUR_OF_DAY]
                val minute = mCurrentTime[Calendar.MINUTE]

                val mTimePicker = TimePickerDialog(
                    context,
                    { _: TimePicker?, hourOfDay: Int, minute1: Int ->
                        val timeString24Hr = String.format(
                            "%02d",
                            hourOfDay
                        ) + ":" + String.format("%02d", minute1)
                        val timeString12Hr =
                            Util.get12hrFormatTime(timeString24Hr)

                        metaData.currentReading = timeString24Hr
                        tvTime.text = timeString12Hr

                        scopeIO.launch {
                            formOperations.handleDateAndTimeData(
                                metaData,
                                tvTimeLabel
                            )
                        }
                    }, hour, minute, true
                )
                mTimePicker.setTitle("Select Time")
                mTimePicker.show()
            }
        }
    }

    fun setTime(metaData: MetaData, formActivity: FormActivity) {
        //Note: the storing time will always be in 24hr format only display format is 12hr so the
        // current reading will also be in 24hr format just convert and show it
        // if there is any value recorded already

        val mCurrentTime = Calendar.getInstance()
        val hour = mCurrentTime[Calendar.HOUR_OF_DAY]
        val minute = mCurrentTime[Calendar.MINUTE]
        val storingTime = String.format("%02d", hour) + ":" + String.format("%02d", minute)
        val defaultValue = metaData.defaultValue
        val displayTime = Util.get12hrFormatTime(storingTime)

        scopeMainThread.launch {
            if (metaData.currentReading.isNullOrEmpty()) {
                if (defaultValue.isNullOrEmpty() || defaultValue.equals(
                        "current",
                        ignoreCase = true
                    )
                ) {
                    tvTime.text = displayTime
                    metaData.currentReading = storingTime
                } else if (defaultValue.equals("blank", ignoreCase = true)) {
                    tvTime.text = "Click here to set"
                    metaData.currentReading = null
                }
                formOperations.setCalculatedFieldParams(metaData, null, tvTime)
                formOperations.handleDateAndTimeData(metaData, tvTimeLabel)
            } else {
                tvTime.text = Util.get12hrFormatTime(metaData.currentReading)
            }
        }
    }
}