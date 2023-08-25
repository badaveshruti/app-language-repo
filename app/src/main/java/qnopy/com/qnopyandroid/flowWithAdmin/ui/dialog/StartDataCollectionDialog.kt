package qnopy.com.qnopyandroid.flowWithAdmin.ui.dialog

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.Site
import qnopy.com.qnopyandroid.databinding.DialogStartDataCollectionBinding
import qnopy.com.qnopyandroid.requestmodel.DEvent
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp
import qnopy.com.qnopyandroid.util.DeviceInfo
import qnopy.com.qnopyandroid.util.Util
import java.util.*

class StartDataCollectionDialog(private var project: Site, private var form: SSiteMobileApp) :
    DialogFragment() {

    private lateinit var date: Calendar
    private lateinit var guid: String
    private lateinit var userName: String
    private lateinit var userId: String
    private lateinit var binding: DialogStartDataCollectionBinding
    private val dateFormat: String = "MMM dd, yyyy hh:mm"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userId = Util.getSharedPreferencesProperty(requireActivity(), GlobalStrings.USERID)
        userName = Util.getSharedPreferencesProperty(requireContext(), GlobalStrings.USERNAME)
        guid = Util.getSharedPreferencesProperty(requireContext(), userName)

        binding = DialogStartDataCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvFormName.text = "\"${form.display_name}\" ?"
            tvDate.text = Util.getFormattedDateFromMilliS(
                form.creationDate ?: Calendar.getInstance().timeInMillis, dateFormat
            )

            tvChangeDate.setOnClickListener { openDateTimePicker() }

            btnStart.setOnClickListener {
                Toast.makeText(
                    requireActivity(),
                    "Start data collection",
                    Toast.LENGTH_SHORT
                ).show()
            }
            btnCancel.setOnClickListener { dismiss() }
        }
    }

    private fun openDateTimePicker() {
        val currentDate = Calendar.getInstance()
        date = Calendar.getInstance()
        DatePickerDialog(
            requireContext(), R.style.datetimePickerStyle, { _, year, month, day ->
                date.set(year, month, day)
                TimePickerDialog(requireContext(), R.style.datetimePickerStyle, { _, hour, minute ->
                    date[Calendar.HOUR_OF_DAY] = hour
                    date[Calendar.MINUTE] = minute

                    binding.tvDate.text =
                        Util.getFormattedDateFromMilliS(date.timeInMillis, dateFormat)
                }, currentDate[Calendar.HOUR_OF_DAY], currentDate[Calendar.MINUTE], true).show()
            }, currentDate[Calendar.YEAR], currentDate[Calendar.MONTH],
            currentDate[Calendar.DATE]
        ).show()
    }

    override fun onStart() {
        super.onStart()

        val background = ColorDrawable(Color.TRANSPARENT)
        val margin = 20
        val inset = InsetDrawable(background, margin)

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(inset)
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    private fun startEvent() {

        val event = DEvent()
        event.siteId = project.siteID
        event.mobileAppId = form.mobileAppId
        event.userId = userId.toInt()
        event.deviceId = DeviceInfo.getDeviceID(requireContext())
        event.userName = userName
        event.eventName = null
        event.eventDate = date.timeInMillis
        event.eventStartDate = date.timeInMillis
        event.eventEndDate = 0
//        val eventHandler = AsyncEventCreate(event, this)
//        eventHandler.execute()
    }
}