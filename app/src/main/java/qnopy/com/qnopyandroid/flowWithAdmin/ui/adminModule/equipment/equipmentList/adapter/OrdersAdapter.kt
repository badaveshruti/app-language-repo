package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.databinding.ItemEquipmentOrderBinding
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model.OnEquipmentOderClickListener
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model.Order
import qnopy.com.qnopyandroid.flowWithAdmin.utility.Utils
import qnopy.com.qnopyandroid.util.Util

class OrdersAdapter(
    val orderList: ArrayList<Order>,
    val listener: OnEquipmentOderClickListener,
    val context: Context
) : RecyclerView.Adapter<OrdersAdapter.OrderHolder>() {

    inner class OrderHolder(val binding: ItemEquipmentOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) = with(binding) {

            val date = Util.getFormattedDateTime(
                order.creationDate,
                GlobalStrings.DATE_FORMAT_MMM_DD_YYYY_H_M_12HR
            )
            val orderDate = "$date from ${order.vendorLocation}"
            tvOrderDate.text = orderDate

            tvOrderNotes.text = order.itemNeeded

            val status = "Status: ${order.orderStatus}"
            tvOrderStatus.text = status

            ivEdit.setOnClickListener { listener.onOrderEditClicked(order) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        val binding =
            ItemEquipmentOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        holder.bind(orderList[position])
    }

    override fun getItemCount(): Int = orderList.size

    fun updateList(list: ArrayList<Order>) {
        orderList.clear()
        orderList.addAll(list)
        notifyDataSetChanged()
    }
}