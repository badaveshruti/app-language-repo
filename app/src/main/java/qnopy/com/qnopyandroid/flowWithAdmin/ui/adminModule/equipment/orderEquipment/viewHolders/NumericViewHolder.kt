package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.viewHolders

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.customView.CustomEditText
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model.EquipmentField
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.adapter.OrderFormMaster
import qnopy.com.qnopyandroid.ui.forms.FormOperations
import qnopy.com.qnopyandroid.ui.forms.scopeMainThread
import qnopy.com.qnopyandroid.util.Util
import java.text.NumberFormat
import java.util.*

class NumericViewHolder(
    view: View,
    private val formMaster: OrderFormMaster
) : RecyclerView.ViewHolder(view) {
    private val tvNumericLabel: CustomTextView = view.findViewById(R.id.tvNumericLabel)
    val layoutFieldControls: LinearLayout = view.findViewById(R.id.layoutFieldControls)
    private val edtNumeric: CustomEditText = view.findViewById(R.id.edtNumeric)

    init {
        edtNumeric.hint = "Enter data"
        tvNumericLabel.setTextColor(Color.BLACK)

        if (!Locale.getDefault().language.contains("en")) edtNumeric.keyListener =
            DigitsKeyListener.getInstance(
                "0123456789,-"
            )
    }

    fun setNumericValue(equipmentField: EquipmentField) {
        scopeMainThread.launch {
            OrderFormMaster.addAsteriskToLabel(
                equipmentField.title ?: "",
                tvNumericLabel,
                equipmentField.isRequiredField
            )

            edtNumeric.setText(equipmentField.value)
        }
    }

    fun setListeners(equipmentField: EquipmentField) {
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
                        var value: String = edtNumeric.text.toString()
                        if (value.length == 1 && charSequence == ",") {
                            return
                        }

                        if (value.isNotEmpty()) {
                            value = getLocaleFormattedString(
                                value,
                                Locale.ENGLISH
                            )
                        }

                        formMaster.setFieldValue(equipmentField.key, value)
                        equipmentField.value = value
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
                }
            })

/*            edtNumeric.setOnEditorActionListener { _, arg1, _ ->
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
                            metaData.metaParamID.toString() + ""
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
                })*/
        }
    }

    fun getLocaleFormattedString(value: String?, locale: Locale): String {
        var valueToConvert = value
        var convertedValue = ""
        val nf = NumberFormat.getInstance(locale)
        nf.isGroupingUsed = false

        try {
            if (!valueToConvert.isNullOrEmpty()) {
                if (valueToConvert.contains(".") || valueToConvert.contains(",")) {
                    nf.minimumFractionDigits = 2
                    nf.maximumFractionDigits = 5
                }
                valueToConvert = value?.replace(",".toRegex(), ".")
                if (Util.hasDigitDecimalOnly(valueToConvert)) convertedValue =
                    nf.format(valueToConvert?.toDouble())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return convertedValue
    }
}