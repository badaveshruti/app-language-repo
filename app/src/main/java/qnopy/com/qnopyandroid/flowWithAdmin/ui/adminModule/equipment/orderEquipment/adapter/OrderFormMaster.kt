package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.adapter

import android.os.Build
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model.EquipmentField
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model.Order
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.OrderEquipmentsActivity
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.viewHolders.DateViewHolder
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.viewHolders.NumericViewHolder
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.viewHolders.PickerViewHolder
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.viewHolders.TextViewHolder
import qnopy.com.qnopyandroid.ui.forms.*
import qnopy.com.qnopyandroid.util.Util

const val keyVendorLocation: String = "vendorLocation"
const val keyVendorSiteId: String = "vendorSiteId"

class OrderFormMaster(
    val context: OrderEquipmentsActivity,
    private var listFields: ArrayList<EquipmentField>,
    var siteId: String, private var order: Order?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var jsonOrder: String? = null

    private val listRequiredKeys = listOf(
        "vendorId",
        "vendorSiteId",
        "dateEquipmentNeeded",
        "dateRentalStarts",
        "itemNeeded",
        "orderContact",
        "orderPhone",
        "orderEmail"
    )

    private var mapRequiredKeys: HashMap<String, String> = HashMap()

    //in case edit, fetch oldValues from here
    private var orderRequest: Order = Order()

    fun setFieldValue(key: String, value: String) {
        val jsonObject = JsonParser().parse(jsonOrder).asJsonObject
        jsonObject.addProperty(key, value)
        val orderToReplace = Gson().fromJson(jsonObject.toString(), Order::class.java)
        orderRequest = orderToReplace
        jsonOrder = Gson().toJson(orderRequest)
    }

    init {

        for (reqKey in listRequiredKeys) {
            mapRequiredKeys[reqKey] = reqKey
        }

        if (order != null) {
            jsonOrder = Gson().toJson(order)
            val jsonObjectOrder = JsonParser().parse(jsonOrder).asJsonObject

            for (field in listFields) {
                if (mapRequiredKeys.contains(field.key))
                    field.isRequiredField = true

                if (jsonObjectOrder.get(field.key) != null)
                    field.value = jsonObjectOrder.get(field.key).toString().replace("\"", "")
                else
                    field.value = null
            }
        } else {
            val userID = Util.getSharedPreferencesProperty(context, GlobalStrings.USERID)

            val orderId = Util.getRandomNumberInRange(100000, 99999999);
            orderRequest.orderId = -orderId
            orderRequest.creationDate = System.currentTimeMillis()
            orderRequest.createdBy = userID
            orderRequest.orderedBy = userID
            orderRequest.siteId = siteId.toInt()

            jsonOrder = Gson().toJson(orderRequest)

            for (field in listFields) {
                if (mapRequiredKeys.contains(field.key))
                    field.isRequiredField = true
            }
        }
    }

    companion object {
        fun addAsteriskToLabel(label: String, textView: TextView, isRequired: Boolean) {
            var title = label

            if (isRequired)
                title = "$label<font color='#D0312D'><b> *</b></font>"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textView.text = Html.fromHtml(
                    title,
                    Html.FROM_HTML_MODE_LEGACY
                )
            } else {
                textView.text = Html.fromHtml(title)
            }
        }
    }

    fun validateForm(): Boolean {
        for (field in listFields) {
            if (field.isRequiredField && field.value.isNullOrEmpty()) {
                return false;
            }
        }
        return true
    }

    fun getOrderRequest(): Order {
        return orderRequest
    }

    override fun getItemCount(): Int = listFields.size

    override fun getItemViewType(position: Int): Int {
        val field = listFields[position]

        return when (field.type) {
            "DATE" -> LAYOUT_DATE
            "NUMERIC" -> LAYOUT_NUMERIC
            "TEXTCONTAINER" -> LAYOUT_TEXT_CONTAINER
            "TEXT" -> LAYOUT_TEXT
            "PICKER" -> LAYOUT_PICKER
            "", null -> LAYOUT_TEXT
            else -> LAYOUT_TEXT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view: View? = null

        return when (viewType) {
            LAYOUT_DATE -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_date, parent, false)
                DateViewHolder(view, this@OrderFormMaster, context)
            }
            LAYOUT_NUMERIC -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_numeric, parent, false)
                NumericViewHolder(view, this@OrderFormMaster)
            }
            LAYOUT_TEXT -> {
                view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_text, parent, false)
                TextViewHolder(view, context, this@OrderFormMaster)
            }
            LAYOUT_TEXT_CONTAINER -> {
                view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_text, parent, false)
                TextViewHolder(view, context, this@OrderFormMaster)
            }
            LAYOUT_PICKER -> {
                view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_picker, parent, false)
                PickerViewHolder(view, context, this@OrderFormMaster)
            }
            else -> {
                view =
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_text, parent,
                        false
                    )
                TextViewHolder(view, context, this)
            }
        }
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, ex ->
        Log.e("CoroutineScope", "Caught ${Log.getStackTraceString(ex)}")
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val field = listFields[viewHolder.absoluteAdapterPosition]

        scopeIO.launch(exceptionHandler) {
            when (viewHolder.itemViewType) {
                LAYOUT_DATE -> {
                    val holder = viewHolder as DateViewHolder
                    holder.setDate(field)
                    holder.setClickListener(field)
                }
                LAYOUT_NUMERIC -> {
                    val holder = viewHolder as NumericViewHolder
                    holder.setNumericValue(field)
                    holder.setListeners(field)
                }
                LAYOUT_TEXT, LAYOUT_TEXT_CONTAINER -> {
                    val holder = viewHolder as TextViewHolder
                    holder.setTextValue(field)
                    holder.setTextChangeListener(field)
                }
                LAYOUT_PICKER -> {
                    val holder = viewHolder as PickerViewHolder
                    holder.setPickerAdapter(field)
                }
                else -> {
                    val holder = viewHolder as TextViewHolder
                    holder.setTextValue(field)
                    holder.setTextChangeListener(field)
                }
            }
        }
    }
}