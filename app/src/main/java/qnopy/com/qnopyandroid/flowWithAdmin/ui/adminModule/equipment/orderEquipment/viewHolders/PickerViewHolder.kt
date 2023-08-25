package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.viewHolders

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model.EquipmentField
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model.Option
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.adapter.OrderFormMaster
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.adapter.SpinnerDropdownAdapter
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.adapter.keyVendorLocation
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.adapter.keyVendorSiteId
import qnopy.com.qnopyandroid.ui.forms.scopeMainThread

class PickerViewHolder(
    view: View, private val context: Context, private val formMaster: OrderFormMaster
) : RecyclerView.ViewHolder(view) {
    private val tvPickerLabel: CustomTextView = view.findViewById(R.id.tvPickerLabel)
    val layoutFieldControls: LinearLayout = view.findViewById(R.id.layoutFieldControls)
    val spinner: Spinner = view.findViewById(R.id.spinner)
    lateinit var adapter: SpinnerDropdownAdapter

    init {
        tvPickerLabel.setTextColor(Color.BLACK)
    }

    fun setPickerAdapter(
        field: EquipmentField
    ) {
        scopeMainThread.launch {
            OrderFormMaster.addAsteriskToLabel(
                field.title ?: "",
                tvPickerLabel,
                field.isRequiredField
            )

            val listPickerItems: ArrayList<Option> = ArrayList()
            val selectOption = Option("-1", context.getString(R.string.select_spinner_first_item))
            listPickerItems.add(selectOption)

            field.options?.let { options ->
                listPickerItems.addAll(options)
            }

            adapter =
                object : SpinnerDropdownAdapter(context, listPickerItems) {
                    override fun isEnabled(position: Int): Boolean {
                        return position != 0
                    }
                }

            spinner.adapter = adapter

            field.value?.let {
                if (listPickerItems.isNotEmpty() && it.isNotEmpty()) {
                    listPickerItems.forEachIndexed { pos, item ->
                        if (item.itemId.equals(it, ignoreCase = true)) {
                            spinner.setSelection(pos)
                        }
                    }
                }
            }

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, arg1: View,
                    position: Int, arg3: Long
                ) {
                    (parent.getChildAt(0) as TextView).setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimary
                        )
                    )

                    val selectedItem = listPickerItems[position]
                    formMaster.setFieldValue(field.key, selectedItem.itemId)

                    if (field.key == keyVendorSiteId) {
                        formMaster.setFieldValue(keyVendorLocation, selectedItem.itemName!!)
                    }

                    field.value = selectedItem.itemId
                    Log.i("selected Item", selectedItem.itemName ?: "null")
                }

                override fun onNothingSelected(arg0: AdapterView<*>?) {
                    Log.d("", "")
                }
            }
        }
    }
}