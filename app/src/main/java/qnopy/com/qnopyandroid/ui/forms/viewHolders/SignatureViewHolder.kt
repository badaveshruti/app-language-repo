package qnopy.com.qnopyandroid.ui.forms.viewHolders

import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.MetaData
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.db.AttachmentDataSource
import qnopy.com.qnopyandroid.requestmodel.CustomerSign
import qnopy.com.qnopyandroid.signature.SignatureAdapter
import qnopy.com.qnopyandroid.signature.SignatureUpdateListener
import qnopy.com.qnopyandroid.ui.activity.FormActivity
import qnopy.com.qnopyandroid.ui.forms.FormOperations
import qnopy.com.qnopyandroid.ui.forms.FormsAdapter
import qnopy.com.qnopyandroid.ui.forms.scopeMainThread
import qnopy.com.qnopyandroid.util.Util

class SignatureViewHolder(
    view: View, private val formOperations: FormOperations,
    private val context: FormActivity, private val formsAdapter: FormsAdapter
) : RecyclerView.ViewHolder(view) {

    val tvSignatureLabel: CustomTextView = view.findViewById(R.id.tvPhotosLabel)
    val layoutFieldControls: LinearLayout = view.findViewById(R.id.layoutFieldControls)
    private val rvPhotos: RecyclerView = view.findViewById(R.id.rvPhotos)
    private lateinit var signatureAdapter: SignatureAdapter

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
        formOperations.addSignatureIcon(metaData, layoutFieldControls)

        scopeMainThread.launch {
            tvSignatureLabel.setOnClickListener {
                formOperations.startSignatureActivity(metaData)
            }
        }
    }

    fun setSignature(metaData: MetaData) {
        val attachmentDataSource = AttachmentDataSource(context)
        val customerSigns = attachmentDataSource.getAttachmentListForSignature(
            context.eventID, context.siteID, metaData.metaParamID, context.locationID,
            context.userID,
            context.currentAppID, context.curSetID
        )

        signatureAdapter = SignatureAdapter(
            customerSigns,
            context, object : SignatureUpdateListener {
                override fun onSignatureRemoved(sign: ArrayList<CustomerSign>) {}
                override fun onSignatureViewClicked() {
                    formOperations.startSignatureActivity(metaData)
                }
            }, true
        )
        rvPhotos.adapter = signatureAdapter
    }
}