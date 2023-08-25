package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.viewHolders

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TimePicker
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model.EquipmentField
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.OrderEquipmentsActivity
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.adapter.OrderFormMaster
import qnopy.com.qnopyandroid.ui.forms.scopeMainThread
import qnopy.com.qnopyandroid.util.Util
import java.text.SimpleDateFormat
import java.util.*

class DateViewHolder(
    view: View, private val formMaster: OrderFormMaster,
    private val context: Context
) :
    RecyclerView.ViewHolder(view) {
    private val tvDateLabel: CustomTextView = view.findViewById(R.id.tvDateLabel)
    val tvDate: CustomTextView = view.findViewById(R.id.tvDate)
    lateinit var date: Calendar

    init {
        tvDateLabel.setTextColor(Color.BLACK)
    }

    fun setClickListener(equipmentField: EquipmentField) {
        scopeMainThread.launch {
            tvDate.setOnClickListener {
                val currentDate = Calendar.getInstance()
                date = Calendar.getInstance()

                DatePickerDialog(
                    context,
                    OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        date[year, monthOfYear] = dayOfMonth
                        TimePickerDialog(
                            context,
                            { _: TimePicker?, hourOfDay: Int, minute: Int ->
                                date[Calendar.HOUR_OF_DAY] = hourOfDay
                                date[Calendar.MINUTE] = minute

                                formMaster.setFieldValue(
                                    equipmentField.key,
                                    date.timeInMillis.toString()
                                )

                                equipmentField.value = date.timeInMillis.toString()

                                val sdf = SimpleDateFormat(
                                    GlobalStrings.DATE_FORMAT_MM_DD_YYYY_MIN_12HR,
                                    Locale.US
                                )

                                tvDate.text = sdf.format(date.time)
                            }, currentDate[Calendar.HOUR_OF_DAY], currentDate[Calendar.MINUTE],
                            false
                        ).show()
                    }, currentDate[Calendar.YEAR], currentDate[Calendar.MONTH],
                    currentDate[Calendar.DATE]
                ).show()
            }
        }
    }

    fun setDate(equipmentField: EquipmentField) {
        scopeMainThread.launch {
            OrderFormMaster.addAsteriskToLabel(
                equipmentField.title ?: "",
                tvDateLabel,
                equipmentField.isRequiredField
            )
            tvDate.text = context.getString(R.string.click_here_to_set)

            equipmentField.value?.let {
                if (it.isNotEmpty()) {
                    val date = Util.getFormattedDateTime(
                        it.toLong(),
                        GlobalStrings.DATE_FORMAT_MMM_DD_YYYY_H_M_12HR
                    )
                    tvDate.text = date
                }
            }
        }
    }
}