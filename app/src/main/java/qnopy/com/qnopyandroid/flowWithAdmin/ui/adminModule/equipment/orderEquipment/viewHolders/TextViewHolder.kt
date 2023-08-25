package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.viewHolders

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.customView.CustomEditText
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model.EquipmentField
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.adapter.OrderFormMaster
import qnopy.com.qnopyandroid.ui.forms.scopeMainThread

class TextViewHolder(
    view: View,
    private val context: Context,
    private val formMaster: OrderFormMaster
) : RecyclerView.ViewHolder(view) {
    private val tvTextLabel: CustomTextView = view.findViewById(R.id.tvTextLabel)
    val layoutFieldControls: LinearLayout = view.findViewById(R.id.layoutFieldControls)
    val edtEnterText: CustomEditText = view.findViewById(R.id.edtEnterText)

    init {
        edtEnterText.hint = "Enter description"
        edtEnterText.imeOptions = EditorInfo.IME_ACTION_DONE
        edtEnterText.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
        edtEnterText.maxLines = 3
        edtEnterText.isSingleLine = false
        edtEnterText.isScrollContainer = true
        tvTextLabel.setTextColor(Color.BLACK)
    }

    fun setTextChangeListener(equipmentField: EquipmentField) {
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
                    formMaster.setFieldValue(equipmentField.key, value)
                    equipmentField.value = value
                    edtEnterText.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                }

                override fun afterTextChanged(editable: Editable) {
                }
            })
        }
    }

    fun setTextValue(equipmentField: EquipmentField) {
        scopeMainThread.launch {

/*            if (equipmentField.type == input_type_text_container)
                edtEnterText.height = 150*/
            OrderFormMaster.addAsteriskToLabel(
                equipmentField.title ?: "",
                tvTextLabel,
                equipmentField.isRequiredField
            )
            edtEnterText.setText(equipmentField.value)
        }
    }
}