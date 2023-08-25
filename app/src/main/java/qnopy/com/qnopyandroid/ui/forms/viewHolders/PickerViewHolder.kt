package qnopy.com.qnopyandroid.ui.forms.viewHolders

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
import qnopy.com.qnopyandroid.adapter.SpinnerDropdownAdapter
import qnopy.com.qnopyandroid.clientmodel.MetaData
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.db.LocationDataSource
import qnopy.com.qnopyandroid.requestmodel.SCocMaster
import qnopy.com.qnopyandroid.ui.activity.FormActivity
import qnopy.com.qnopyandroid.ui.forms.FormOperations
import qnopy.com.qnopyandroid.ui.forms.scopeMainThread
import kotlin.collections.set

class PickerViewHolder(
    view: View, private val formOperations: FormOperations,
    private val context: FormActivity
) : RecyclerView.ViewHolder(view) {
    val tvPickerLabel: CustomTextView = view.findViewById(R.id.tvPickerLabel)
    val layoutFieldControls: LinearLayout = view.findViewById(R.id.layoutFieldControls)
    val spinner: Spinner = view.findViewById(R.id.spinner)
    var adapter: SpinnerDropdownAdapter? = null
    val cocMasterValueMap = LinkedHashMap<String, SCocMaster>()

    fun setFieldControlIcons(metaData: MetaData) {
        scopeMainThread.launch { layoutFieldControls.removeAllViews() }
        formOperations.addSupportingIcons(metaData, layoutFieldControls, absoluteAdapterPosition)
    }

    fun setPickerData(metaData: MetaData) {

        if (metaData.nameValueMap.containsKey("Coc")
            || metaData.nameValueMap.containsKey("COC")
        ) {
            val ld = LocationDataSource(context)
            val cocMasterArrayList = ld.getAllCoCIDs(
                context.eventID.toString(),
                context.siteID.toString()
            )
            val pickerCocNameValueMap = LinkedHashMap<String, String>()
            for (master in cocMasterArrayList) {
                pickerCocNameValueMap[master.cocDisplayId] = master.cocDisplayId
                cocMasterValueMap[master.cocDisplayId] = master
            }
            metaData.nameValueMap = pickerCocNameValueMap
        } else {
            metaData.nameValueMap = metaData.nameValueMap
        }

        formOperations.getDefaultValueToSet(metaData, tvPickerLabel)
    }

    fun setPickerAdapter(
        metaData: MetaData
    ) {
            val listPickerItems: ArrayList<String> = ArrayList()
            listPickerItems.add(context.getString(R.string.select_spinner_first_item))

            for (key in metaData.nameValueMap.keys) {
                listPickerItems.add(key)
            }

            adapter =
                object : SpinnerDropdownAdapter(context, listPickerItems) {
                    override fun isEnabled(position: Int): Boolean {
                        return position != 0
                    }
                }

            spinner.adapter = adapter

            if (listPickerItems.isNotEmpty() && !metaData.currentReading.isNullOrEmpty()) {
                listPickerItems.forEachIndexed { pos, item ->
                    if (item.equals(metaData.currentReading, ignoreCase = true)) {
                        spinner.setSelection(pos)
                    }
                }
            }

            formOperations.handleTextData(metaData, tvPickerLabel)

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
/*                    if (metaData.getNameValueMap() == null) {
                        if (metaData.getNameValueMap().containsKey("Coc")
                            || metaData.getNameValueMap().containsKey("COC")
                        ) {
                            val ld = LocationDataSource(context)
                            val cocMasterArrayList = ld.getAllCoCIDs(
                                context.eventID.toString(),
                                context.siteID.toString()
                            )
                            val pickerCocNameValueMap = LinkedHashMap<String, String>()
                            for (master in cocMasterArrayList) {
                                pickerCocNameValueMap[master.cocDisplayId] = master.cocDisplayId
                                cocMasterValueMap[master.cocDisplayId] = master
                            }
                            tempData.setNameValuePair(pickerCocNameValueMap)
                        } else {
                            tempData.setNameValuePair(metaData.getNameValueMap())
                        }
                    }*/

                    val selectedItem =
                        metaData.getNameValueMap()[listPickerItems[position]] ?: return
                    metaData.currentReading = selectedItem
                    formOperations.handleTextData(metaData, tvPickerLabel)
                    formOperations.checkWarningValueForViolation(metaData)

                    if (metaData.getNameValueMap().containsKey("Coc")
                        || metaData.getNameValueMap().containsKey("COC")
                    ) {
                        if (cocMasterValueMap.containsKey(metaData.currentReading)) {
                            context.currCocID =
                                cocMasterValueMap[metaData.currentReading]?.cocId
                                    .toString()
                            //Todo need to refresh adapter for any calculations
//                            Handler().postDelayed({ setDataOnChanged(0) }, 200)
                        }
                    }
                    formOperations.checkForSubFieldsForVisibility(metaData, absoluteAdapterPosition)
                }

                override fun onNothingSelected(arg0: AdapterView<*>?) {
                    Log.d("", "")
                }
            }

        }
}