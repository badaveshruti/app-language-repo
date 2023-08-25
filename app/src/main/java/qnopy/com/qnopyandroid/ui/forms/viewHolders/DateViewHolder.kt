package qnopy.com.qnopyandroid.ui.forms.viewHolders

import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import android.widget.DatePicker
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.MetaData
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.ui.forms.FormOperations
import qnopy.com.qnopyandroid.ui.forms.scopeIO
import qnopy.com.qnopyandroid.ui.forms.scopeMainThread
import java.util.*

class DateViewHolder(view: View, private val formOperations: FormOperations) :
    RecyclerView.ViewHolder(view) {
    val tvDateLabel: CustomTextView = view.findViewById(R.id.tvDateLabel)
    val tvDate: CustomTextView = view.findViewById(R.id.tvDate)

    fun setClickListener(metadata: MetaData, context: Context) {
        scopeMainThread.launch {
            tvDate.setOnClickListener {
                val mCurrentTime = Calendar.getInstance()
                val date = mCurrentTime[Calendar.DAY_OF_MONTH]
                val month = mCurrentTime[Calendar.MONTH]
                val year = mCurrentTime[Calendar.YEAR]

                val mDatePicker = DatePickerDialog(
                    context,
                    { _: DatePicker?, pickerYear: Int, monthOfYear: Int, dayOfMonth: Int ->
                        mCurrentTime[Calendar.DAY_OF_MONTH] = dayOfMonth
                        mCurrentTime[Calendar.MONTH] = monthOfYear
                        mCurrentTime[Calendar.YEAR] = pickerYear
                        val storingDate = String.format(
                            "%02d",
                            monthOfYear + 1
                        ) + "/" + String.format(
                            "%02d",
                            dayOfMonth
                        ) + "/" + String.format("%02d", pickerYear)

                        metadata.currentReading = storingDate
                        tvDate.text = storingDate
                        scopeIO.launch {
                            formOperations.handleDateAndTimeData(
                                metadata,
                                tvDateLabel
                            )
                        }
                    }, year, month, date
                )

                mDatePicker.setTitle("Select Date")
                mDatePicker.show()
            }
        }
    }

    fun setDate(metaData: MetaData, context: Context) {
        val defaultValue = metaData.defaultValue

        val mCurrentDate = Calendar.getInstance()
        val day = mCurrentDate[Calendar.DAY_OF_MONTH]
        val month = mCurrentDate[Calendar.MONTH]
        val year = mCurrentDate[Calendar.YEAR]

        val dateString = String.format("%02d", month + 1) + "/" + String.format(
            "%02d", day
        ) + "/" + String.format("%02d", year)

        if (metaData.currentReading.isNullOrEmpty()) {
            if (defaultValue == null || defaultValue.trim { it <= ' ' }.isEmpty()
                || defaultValue.equals("current", ignoreCase = true)
            ) {
                metaData.currentReading = dateString
            }

            scopeMainThread.launch {
                tvDate.text = metaData.currentReading
                if (defaultValue != null && defaultValue == "blank") {
                    tvDate.text = context.getString(R.string.click_here_to_set)
                }
                formOperations.setCalculatedFieldParams(metaData, null, tvDate)
            }
        } else {
            scopeMainThread.launch {
                tvDate.text = metaData.currentReading
            }
        }

        scopeMainThread.launch {
            formOperations.handleDateAndTimeData(
                metaData,
                tvDateLabel
            )
        }
    }
}