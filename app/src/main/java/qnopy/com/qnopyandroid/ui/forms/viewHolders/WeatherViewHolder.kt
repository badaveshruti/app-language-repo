package qnopy.com.qnopyandroid.ui.forms.viewHolders

import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.MetaData
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.ui.activity.FormActivity
import qnopy.com.qnopyandroid.ui.forms.FormOperations
import qnopy.com.qnopyandroid.ui.forms.scopeMainThread

class WeatherViewHolder(
    view: View, private val formOperations: FormOperations, private val context: FormActivity
) : RecyclerView.ViewHolder(view) {
    val tvWeatherLabel: CustomTextView = view.findViewById(R.id.tvGpsLabel)
    val layoutFieldControls: LinearLayout = view.findViewById(R.id.layoutFieldControls)
    private val tvZipCode: CustomTextView = view.findViewById(R.id.tvGpsLoc)

    fun addIcons(metaData: MetaData) {
        scopeMainThread.launch { layoutFieldControls.removeAllViews() }
        formOperations.addWeatherIcon(metaData, layoutFieldControls)
        formOperations.addSupportingIcons(metaData, layoutFieldControls, absoluteAdapterPosition)
    }

    fun setWeather(metaData: MetaData) {
        formOperations.getDefaultValueToSet(metaData, tvWeatherLabel)

        scopeMainThread.launch {
            if (!metaData.currentReading.isNullOrEmpty())
                tvZipCode.text = metaData.currentReading
            else
                tvZipCode.hint = context.getString(R.string.zip_code)

            tvZipCode.setOnClickListener {
                formOperations.openWeatherActivity()
            }
        }
    }
}