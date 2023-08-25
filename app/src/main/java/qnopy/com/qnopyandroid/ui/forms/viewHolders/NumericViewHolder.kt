package qnopy.com.qnopyandroid.ui.forms.viewHolders

import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.MetaData
import qnopy.com.qnopyandroid.customView.CustomEditText
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.db.DefaultValueDataSource
import qnopy.com.qnopyandroid.ui.activity.FormActivity
import qnopy.com.qnopyandroid.ui.forms.FormOperations
import qnopy.com.qnopyandroid.ui.forms.scopeIO
import qnopy.com.qnopyandroid.ui.forms.scopeMainThread
import java.util.*

class NumericViewHolder(
    view: View, private val formOperations: FormOperations, private val context: FormActivity
) : RecyclerView.ViewHolder(view) {
    val tvNumericLabel: CustomTextView = view.findViewById(R.id.tvNumericLabel)
    val layoutFieldControls: LinearLayout = view.findViewById(R.id.layoutFieldControls)
    private val edtNumeric: CustomEditText = view.findViewById(R.id.edtNumeric)

    init {
        if (Locale.getDefault().language.contains("pt")
            || Locale.getDefault().language.contains("fr")
        ) edtNumeric.keyListener =
            DigitsKeyListener.getInstance(
                "0123456789,-"
            )
    }

    fun setFieldControlIcons(metaData: MetaData) {
        scopeMainThread.launch { layoutFieldControls.removeAllViews() }
        formOperations.addSupportingIcons(metaData, layoutFieldControls, absoluteAdapterPosition)
        if (metaData.fieldParameterOperands != null && metaData.fieldParameterOperands.isNotEmpty()
            && metaData.metaInputType.equals("NUMERIC", ignoreCase = true)
        ) formOperations.addSummationIcon(metaData, layoutFieldControls)
    }

    fun setNumericValue(metaData: MetaData) {
        formOperations.getDefaultValueToSet(metaData, tvNumericLabel)

        var valueToShow = ""
        if (!metaData.currentReading.isNullOrEmpty()) {
            valueToShow = formOperations.getLocaleFormattedString(
                metaData.currentReading,
                Locale.getDefault()
            )
            val convertedValue: String =
                formOperations.getLocaleFormattedString(metaData.currentReading, Locale.ENGLISH)
            metaData.currentReading = convertedValue
        }

        formOperations.handleNumericData(metaData, edtNumeric, true, tvNumericLabel)

        scopeMainThread.launch {
            edtNumeric.setText(valueToShow)
        }

        if (formOperations.isShowLast2(metaData) && metaData.currentFormID == 142) {
            if (context.curSetID < 1)
                formOperations.setCalculatedFieldParams(metaData, edtNumeric, tvNumericLabel)
        } else
            formOperations.setCalculatedFieldParams(metaData, edtNumeric, tvNumericLabel)
    }

    fun setLabelChanges(metaData: MetaData) {

        val gText = metaData.ParamLabel

        val dv = DefaultValueDataSource(context)
        val dModel = dv.getDefaultValueToWarn(
            context.locationID,
            metaData.currentFormID.toString() + "",
            metaData.metaParamID.toString() + "", context.curSetID.toString()
        )

        var warnHighLimit = dModel.warningHighDefaultValue
        var warnLowLimit = dModel.warningLowDefaultValue

        if (dModel.warningHighDefaultValue == null || dModel.warningHighDefaultValue == "") {
            if (metaData.routineId == 111) {
                warnHighLimit = metaData.metaWarningHigh.toString()
            }
        }
        if (dModel.warningLowDefaultValue == null || dModel.warningLowDefaultValue == "") {
            if (metaData.routineId == 111) {
                warnLowLimit = metaData.metaWarningLow.toString()
            }
        }

        if (metaData.InputType != null && metaData.InputType.equals(
                "TOTALIZER",
                ignoreCase = true
            )
        ) {
            if (warnLowLimit != null && warnLowLimit.trim { it <= ' ' }.isNotEmpty()) {
                val styledText: String = (gText + "\n\n"
                        + "<font color='red'>Value should be greater than  " +
                        warnLowLimit + "</font>")
                val result: Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(styledText, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    Html.fromHtml(styledText)
                }
                scopeMainThread.launch { tvNumericLabel.text = result }
            } else {
                scopeMainThread.launch { tvNumericLabel.text = gText }
            }
        } else {
            if (warnHighLimit != null && warnHighLimit.trim { it <= ' ' }.isNotEmpty() &&
                !warnHighLimit.trim { it <= ' ' }.contains("0.0")
                || warnLowLimit != null && warnLowLimit.trim { it <= ' ' }.isNotEmpty() &&
                !warnLowLimit.trim { it <= ' ' }.contains("0.0")
            ) {
                //commented on 27 Oct, 21 to manage it. show normal
                // var styledText = "$gText\n<font color='red'><small>Range: $warnLowLimit to $warnHighLimit </small></font>"
                val styledText: String =
                    ("$gText \n<font color='blue'><small>Range: $warnLowLimit to $warnHighLimit </small></font>")
                val result: Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(styledText, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    Html.fromHtml(styledText)
                }
                scopeMainThread.launch { tvNumericLabel.text = result }
            } else {
                scopeMainThread.launch {
                    tvNumericLabel.text = gText
                }
            }
        }

        formOperations.setAsteriskIfRequiredField(metaData, tvNumericLabel)

        //adding this as label may have operand which may change label text
        formOperations.setCalculatedFieldParams(metaData, null, tvNumericLabel)
    }

    fun setListeners(metaData: MetaData) {
        scopeMainThread.launch {
            edtNumeric.addTextChangedListener(object : TextWatcher {
                var oldText = ""
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                    oldText = charSequence.toString()
                }

                override fun onTextChanged(
                    charSequence: CharSequence,
                    start: Int,
                    i1: Int,
                    i2: Int
                ) {
                    try {
                        val value: String = edtNumeric.text.toString()
                        if (metaData.metaInputType.equals("NUMERIC", ignoreCase = true)) {
                            if (value.length == 1 && charSequence == ",") {
                                return
                            } else if (value.isNotEmpty() && charSequence.length == start - 1
                                && charSequence[start] == ',' && value.contains(",")
                            ) {
                                return
                            }
                            var fetchedValue: String? = metaData.currentReading

                            if (fetchedValue != null && fetchedValue.isNotEmpty()) {
                                fetchedValue = formOperations.getLocaleFormattedString(
                                    fetchedValue,
                                    Locale.ENGLISH
                                )
                            }

                            val convertedValue: String =
                                formOperations.getLocaleFormattedString(value, Locale.ENGLISH)

                            if (value.isNotEmpty()) {
                                metaData.currentReading = convertedValue
                            } else {
                                metaData.currentReading = value
                            }

                            scopeIO.launch {
                                formOperations.handleNumericData(
                                    metaData, edtNumeric, false, tvNumericLabel
                                )
                            }

                            if (fetchedValue != null) if (fetchedValue != convertedValue) {
                                if (formOperations.hasExtField2ActionCalculated(metaData)
                                ) {
                                    //refresh all items to calculate expr
//                                setDataOnChanged(metaData.metaParamID)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun afterTextChanged(editable: Editable) {
                    val toMatch = editable.toString()
                    val commaCount = toMatch.replace("[^,]".toRegex(), "").length
                    if (editable.toString().length == 1 && editable.toString() == ",") {
                        edtNumeric.setText("")
                    } else if (commaCount > 1 &&
                        editable.toString()[editable.toString().length - 1] == ','
                    ) {
                        edtNumeric.setText(oldText)
                        edtNumeric.text?.let { edtNumeric.setSelection(it.length) }
                    }
                    formOperations.checkWarningValueForViolation(metaData)
                }
            })

            edtNumeric.setOnEditorActionListener { _, arg1, _ ->
                if (arg1 == EditorInfo.IME_ACTION_DONE) {
                    val value = edtNumeric.text.toString()
                    metaData.currentReading = value

                    scopeIO.launch {
                        formOperations.handleNumericData(metaData, edtNumeric, true, tvNumericLabel)
                        if (!formOperations.STORE_VALUE) {
                            edtNumeric.text = null
                        }
                    }

                    if (metaData.metaInputType.equals("NUMERIC", ignoreCase = true))
                        formOperations.setCalculatedFieldParams(
                            metaData,
                            edtNumeric,
                            tvNumericLabel
                        )

                    if (metaData.metaInputType.equals("TOTALIZER", ignoreCase = true)) {
                        val gText: String = metaData.ParamLabel
                        val dv = DefaultValueDataSource(context)
                        val d_model = dv.getDefaultValueToWarn(
                            formOperations.getLocId(),
                            metaData.currentFormID.toString() + "",
                            metaData.metaParamID.toString() + "", context.curSetID.toString()
                        )
                        val warnlowLimit = d_model.warningLowDefaultValue
                        if (warnlowLimit != null && warnlowLimit.trim { it <= ' ' }.isNotEmpty()) {
                            val styledText =
                                "$gText\n\n<font color='red'>Value should be greater than : $warnlowLimit</font>"
                            val result: Spanned =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    Html.fromHtml(styledText, Html.FROM_HTML_MODE_LEGACY)
                                } else {
                                    Html.fromHtml(styledText)
                                }
                            tvNumericLabel.text = result
                        } else {
                            tvNumericLabel.text = gText
                        }
                    }
                    formOperations.checkForSubFieldsForVisibility(metaData, absoluteAdapterPosition)
                }
                false
            }

            edtNumeric.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    try {
                        if (!formOperations.STORE_VALUE && (metaData.currentReading == null
                                    || metaData.currentReading.isEmpty())
                        ) {
                            //added AND condition to avoid setting empty value if there is a value
                            edtNumeric.text = null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        //considered tempdata is null already
                        if (!formOperations.STORE_VALUE) {
                            edtNumeric.text = null
                        }
                    }
                    scopeIO.launch {
                        formOperations.setCalculatedFieldParams(
                            metaData,
                            edtNumeric,
                            tvNumericLabel
                        )
                    } //added this in else as it'll be called in above methods
                    //so if none of them called this can be invoked
                    formOperations.validateValue(metaData, edtNumeric)
                    edtNumeric.clearFocus()
                    //Todo fields visibility
                    //                checkForSubFieldsForVisibility(metaData)
                }
            }

            edtNumeric.viewTreeObserver
                .addOnGlobalLayoutListener(OnGlobalLayoutListener {
                    edtNumeric.text?.let {
                        edtNumeric.setSelection(
                            it.length
                        )
                    }
                })
        }
    }
}