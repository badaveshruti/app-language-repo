package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.adapter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SpinnerAdapter
import android.widget.TextView
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model.Option

open class SpinnerDropdownAdapter(
    private val context: Context,
    private val itemList: ArrayList<Option>
) :
    BaseAdapter(), SpinnerAdapter {
    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(position: Int): Option {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getDropDownView(position: Int, convrtView: View?, parent: ViewGroup): View {
        var convertView = convrtView
        if (convertView == null) {
            convertView = TextView(context)
        }
        val txt = convertView as TextView
        txt.setPadding(14, 14, 14, 14)
        txt.textSize = 18f
        txt.text = getItem(position).itemName
        txt.setTextColor(Color.parseColor("#000000"))
        txt.post { txt.isSingleLine = false }
        return txt
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val txt = TextView(context)
        txt.setPadding(10, 10, 10, 10)
        txt.textSize = 16f
        txt.gravity = Gravity.CENTER
        txt.text = itemList[position].itemName
        txt.setTextColor(Color.parseColor("#000000"))
        return txt
    }
}
