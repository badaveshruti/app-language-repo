package qnopy.com.qnopyandroid.ui.forms.viewHolders

import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.adapter.ObservedPhotosAdapter
import qnopy.com.qnopyandroid.clientmodel.MetaData
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.db.AttachmentDataSource
import qnopy.com.qnopyandroid.ui.activity.FormActivity
import qnopy.com.qnopyandroid.ui.forms.FormOperations
import qnopy.com.qnopyandroid.ui.forms.FormsAdapter
import qnopy.com.qnopyandroid.ui.forms.scopeMainThread

class PhotosViewHolder(
    view: View,
    private val formOperations: FormOperations,
    private val context: FormActivity,
    private val formsAdapter: FormsAdapter
) : RecyclerView.ViewHolder(view) {
    val tvPhotosLabel: CustomTextView = view.findViewById(R.id.tvPhotosLabel)
    val layoutFieldControls: LinearLayout = view.findViewById(R.id.layoutFieldControls)
    private val rvPhotos: RecyclerView = view.findViewById(R.id.rvPhotos)
    private lateinit var observedPhotosAdapter: ObservedPhotosAdapter

    init {
        rvPhotos.setHasFixedSize(true)
        rvPhotos.layoutManager = LinearLayoutManager(
            context, RecyclerView.HORIZONTAL,
            false
        )
    }

    fun setFieldControlIcons(metaData: MetaData) {
        scopeMainThread.launch { layoutFieldControls.removeAllViews() }
        formOperations.addSupportingIcons(metaData, layoutFieldControls, absoluteAdapterPosition)
        formOperations.addPhotosIcon(metaData, layoutFieldControls)
    }

    fun setPhotos(metaData: MetaData) {
        val attachmentDataSource = AttachmentDataSource(context)
        val listPhotos = attachmentDataSource.getAttachmentForFieldParam(
            formsAdapter.siteId,
            formsAdapter.eventId,
            formsAdapter.locationId,
            metaData.currentFormID.toString() + "",
            metaData.metaParamID.toString() + "",
            context.curSetID,
            context.siteName,
            context.currentLocationName
        )
        observedPhotosAdapter = if (listPhotos.size > 0) {
            ObservedPhotosAdapter(
                listPhotos,
                context, metaData.metaParamID
            )
        } else {
            ObservedPhotosAdapter(
                ArrayList(),
                context, metaData.metaParamID
            )
        }
        rvPhotos.adapter = observedPhotosAdapter
    }

    fun updatePhotos(path: String) {
        if (this::observedPhotosAdapter.isInitialized) {
            observedPhotosAdapter.addPhoto(path)
        }
    }
}