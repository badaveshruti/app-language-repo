package qnopy.com.qnopyandroid.ui.forms.viewHolders

import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.MetaData
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.ui.activity.FormActivity
import qnopy.com.qnopyandroid.ui.activity.MapDragActivity
import qnopy.com.qnopyandroid.ui.forms.FormOperations
import qnopy.com.qnopyandroid.ui.forms.scopeMainThread

class GPSViewHolder(
    view: View, private val formOperations: FormOperations, private val context: FormActivity
) : RecyclerView.ViewHolder(view) {
    val tvGpsLabel: CustomTextView = view.findViewById(R.id.tvGpsLabel)
    val layoutFieldControls: LinearLayout = view.findViewById(R.id.layoutFieldControls)
    private val tvGpsLoc: CustomTextView = view.findViewById(R.id.tvGpsLoc)

    fun setFieldControlIcons(metaData: MetaData) {
        scopeMainThread.launch { layoutFieldControls.removeAllViews() }
        formOperations.addSupportingIcons(metaData, layoutFieldControls, absoluteAdapterPosition)
        formOperations.addGPSIcon(metaData, layoutFieldControls)
    }

    fun setGpsLocation(metadata: MetaData) {
        formOperations.getDefaultValueToSet(metadata, tvGpsLabel)
        scopeMainThread.launch {
            if (metadata.currentReading != null && metadata.currentReading.isNotEmpty()) {
                tvGpsLoc.text = metadata.currentReading
            } else {
                tvGpsLoc.text = "0.0000,0.0000"
            }
        }
        formOperations.setMandatoryFieldAlert(metadata, tvGpsLabel)
        //todo show visible fields call
    }

    fun addClickListener(metadata: MetaData) {
        scopeMainThread.launch {
            tvGpsLoc.setOnClickListener {
                val intent = Intent(context, MapDragActivity::class.java)
                intent.putExtra(GlobalStrings.KEY_META_DATA, metadata)
                context.startActivityForResult(
                    intent,
                    FormActivity.CAPTURE_GPS_LOCATION_REQUEST_CODE
                )
            }
        }
    }
}