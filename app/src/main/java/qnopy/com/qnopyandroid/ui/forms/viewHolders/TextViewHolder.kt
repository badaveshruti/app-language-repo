package qnopy.com.qnopyandroid.ui.forms.viewHolders

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView.OnEditorActionListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.MetaData
import qnopy.com.qnopyandroid.customView.CustomEditText
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.db.CocDetailDataSource
import qnopy.com.qnopyandroid.db.DefaultValueDataSource
import qnopy.com.qnopyandroid.ui.activity.FormActivity
import qnopy.com.qnopyandroid.ui.forms.FormOperations
import qnopy.com.qnopyandroid.ui.forms.scopeIO
import qnopy.com.qnopyandroid.ui.forms.scopeMainThread
import java.util.*

class TextViewHolder(
    private val view: View, private val formOperations: FormOperations,
    private val formActivity: FormActivity
) :
    RecyclerView.ViewHolder(view) {
    val tvTextLabel: CustomTextView = view.findViewById(R.id.tvTextLabel)
    val layoutItemText: ConstraintLayout = view.findViewById(R.id.layout_item_text)
    val layoutFieldControls: LinearLayout = view.findViewById(R.id.layoutFieldControls)
    val edtEnterText: CustomEditText = view.findViewById(R.id.edtEnterText)

    init {
        edtEnterText.imeOptions = EditorInfo.IME_ACTION_DONE
        edtEnterText.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
        edtEnterText.maxLines = 3
        edtEnterText.isSingleLine = false
        edtEnterText.isScrollContainer = true
        //TPH-Dx \n MNA [NO3/SO4, PO4/NH3 ,(D) and (T) Metals]
    }

    fun setFieldControlIcons(metaData: MetaData) {
        scopeMainThread.launch { layoutFieldControls.removeAllViews() }
        formOperations.addSupportingIcons(metaData, layoutFieldControls, absoluteAdapterPosition)
    }

    fun setTextChangeListener(metaData: MetaData, context: Context) {
        scopeMainThread.launch {
            edtEnterText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    val value: String = edtEnterText.text.toString()
                    metaData.currentReading = value
                    edtEnterText.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))

                    val expression = metaData.fieldParameterOperands
                    if (expression != null && (expression.contains("SAMPLE")
                                || expression.lowercase(Locale.getDefault()).contains("!!coc!!"))
                        && value.isNotEmpty()
                    ) {
                        formOperations.setIfSampleDateOrTimeSet(false)
                    }
                    scopeIO.launch {
                        formOperations.handleTextData(metaData, tvTextLabel)
                    }
                }

                override fun afterTextChanged(editable: Editable) {
                    scopeIO.launch { formOperations.checkWarningValueForViolation(metaData) }
                }
            })

            edtEnterText.setOnEditorActionListener(OnEditorActionListener { _, arg1, _ ->
                if (arg1 == EditorInfo.IME_ACTION_DONE) {
                    val expression = metaData.fieldParameterOperands
                    if (expression != null && (expression.contains("SAMPLE4")
                                || expression.contains("DUPSAMPLE4"))
                        && edtEnterText.text.toString().isNotEmpty()
                    ) {
//                    calculateSample4Expression(metaData, edtEnterText.text.toString())
                    }
                    formOperations.checkForSubFieldsForVisibility(metaData, absoluteAdapterPosition)
                }
                false
            })

            edtEnterText.onFocusChangeListener = OnFocusChangeListener { _, b ->
                if (!b) {
                    edtEnterText.clearFocus()
//                    formOperations.checkForSubFieldsForVisibility(metaData) //cause crash as recycler is already computing
                }
            }
        }
    }

    fun setTextValue(metaData: MetaData, context: Context) {
        scopeMainThread.launch {
            val cocDataSource = CocDetailDataSource(context)
            formOperations.getDefaultValueToSet(metaData, tvTextLabel)

            if (metaData.currentReading == null) {

                //CHECK CM_DETAILS FOR SAMPLE ID OR DUPLICATE SAMPLE ID
                metaData.currentReading = cocDataSource.getSampleID_from_cocDetail(
                    formOperations.getCurCocId(),
                    formOperations.getLocId(),
                    metaData.metaParamID.toString() + ""
                )
                if (metaData.currentReading == null) {
                    val dv = DefaultValueDataSource(context)
                    metaData.currentReading = dv.getDefaultValue(
                        formOperations.getLocId(), metaData.currentFormID.toString(),
                        metaData.metaParamID.toString(), formActivity.curSetID.toString()
                    )
                }
                if (metaData.currentReading == null) {
                    metaData.currentReading = metaData.defaultValue
                }
                formOperations.handleTextData(metaData, tvTextLabel)
            } else {
                //06-04-2018 IF VALUE COLLECTED FOR 1st COC AND NOW 2nd COC FOR SAME FIELD
                if (formOperations.getCurCocId() != null) {
                    //CHECK CM_DETAILS FOR SAMPLE ID OR DUPLICATE SAMPLE ID
                    val value = cocDataSource.getSampleID_from_cocDetail(
                        formOperations.getCurCocId(),
                        formOperations.getLocId(),
                        metaData.metaParamID.toString()
                    )
                    if (value != null && value.isNotEmpty()) {
                        metaData.currentReading = value
                    }
                }
            }

/*            if (!metaData.currentReading.isNullOrEmpty())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    edtEnterText.setText(
                        Html.fromHtml(
                            metaData.currentReading,
                            Html.FROM_HTML_MODE_LEGACY
                        )
                    )
                } else {
                    edtEnterText.setText(Html.fromHtml(metaData.currentReading))
                }
            else*/

            //replacing with "\n" as database saves the "\n" text with preceding "\"
            if (!metaData.currentReading.isNullOrEmpty() && metaData.currentReading.contains("\\n"))
                metaData.currentReading = metaData.currentReading.replace("\\n", "\n")

            edtEnterText.setText(metaData.currentReading)
            formOperations.setCalculatedFieldParams(metaData, edtEnterText, tvTextLabel)
        }
    }
}