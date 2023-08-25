package qnopy.com.qnopyandroid.ui.forms.viewHolders

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.MetaData
import qnopy.com.qnopyandroid.customView.CustomEditText
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.ui.activity.FormActivity
import qnopy.com.qnopyandroid.ui.forms.FormOperations
import qnopy.com.qnopyandroid.ui.forms.scopeMainThread

class BarcodeViewHolder(
    view: View, private val formOperations: FormOperations, private val context: FormActivity
) : RecyclerView.ViewHolder(view) {
    val tvBarcodeLabel: CustomTextView = view.findViewById(R.id.tvBarcodeLabel)
    val layoutFieldControls: LinearLayout = view.findViewById(R.id.layoutFieldControls)
    private val tvBarcode: CustomEditText = view.findViewById(R.id.tvBarcode)

    fun setFieldControlIcons(metaData: MetaData) {
        scopeMainThread.launch { layoutFieldControls.removeAllViews() }

        if (metaData.metaInputType.equals("BARCODE")) {
            formOperations.addBarcodeQRIcon(metaData, R.drawable.ic_barcode, layoutFieldControls)
        } else
            formOperations.addBarcodeQRIcon(
                metaData,
                R.drawable.ic_qr_code_scanner,
                layoutFieldControls
            )

        formOperations.addSupportingIcons(metaData, layoutFieldControls, absoluteAdapterPosition)
    }

    fun setBarcodeText(metaData: MetaData) {
        if (metaData.metaInputType.equals("BARCODE")) {
            tvBarcode.hint = context.getString(R.string.barcode_text)
        } else
            tvBarcode.hint = context.getString(R.string.qr_text)

        formOperations.getDefaultValueToSet(metaData, tvBarcodeLabel)
        tvBarcode.setText(metaData.currentReading)
    }

    fun addClickListener(metaData: MetaData) {
        scopeMainThread.launch {
/*            tvBarcode.setOnClickListener {
                formOperations.openQRCodeActivity(metaData.metaParamID)
            }*/

            tvBarcode.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }

                override fun onTextChanged(
                    charSequence: CharSequence,
                    start: Int,
                    i1: Int,
                    i2: Int
                ) {
                    val value: String = tvBarcode.text.toString()
                    metaData.currentReading = value
                    formOperations.saveDataAndUpdateCreationDate(
                        metaData
                    )
                }

                override fun afterTextChanged(editable: Editable) {}
            })
        }
    }
}