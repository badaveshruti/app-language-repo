package qnopy.com.qnopyandroid.ui.forms.viewHolders

import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.MetaData
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.db.CocDetailDataSource
import qnopy.com.qnopyandroid.db.FieldDataSource
import qnopy.com.qnopyandroid.db.MethodDataSource
import qnopy.com.qnopyandroid.requestmodel.CoCBottles
import qnopy.com.qnopyandroid.ui.activity.FormActivity
import qnopy.com.qnopyandroid.ui.forms.FormOperations
import qnopy.com.qnopyandroid.ui.forms.scopeMainThread
import qnopy.com.qnopyandroid.util.Util
import java.util.*

class CheckBoxViewHolder(
    view: View, private val formOperations: FormOperations, private val context: FormActivity
) : RecyclerView.ViewHolder(view) {

    var tvCheckBoxLabel: CustomTextView = view.findViewById(R.id.tvCheckBoxLabel)
    var layoutFieldControls: LinearLayout = view.findViewById(R.id.layoutFieldControls)
    private var layoutCheckBox: LinearLayout = view.findViewById(R.id.layoutCheckBox)

    fun setFieldControlIcons(metaData: MetaData) {
        scopeMainThread.launch { layoutFieldControls.removeAllViews() }
        formOperations.addSupportingIcons(metaData, layoutFieldControls, absoluteAdapterPosition)
    }

    fun setCheckBoxViews(metaData: MetaData) {
        val fieldDataSource = FieldDataSource(context)
        val checkOptions = fieldDataSource.getBottleCheckOptions(
            context.eventID.toString(),
            context.curSetID, context.locationID, context.siteID.toString(),
            context.currentAppID, metaData.metaParamID
        )

        if (metaData.getNameValueMap().size > 0 && checkOptions.size == 0) {
            for (item in metaData.getNameValueMap().keys) {
                layoutCheckBox.addView(getCheckBox(metaData, item))
            }
        } else {
            if (checkOptions.size > 0) {
                for (item in checkOptions) {
                    layoutCheckBox.addView(getCheckBox(metaData, item))
                }
            }
        }
    }

    private fun getCheckBox(metaData: MetaData, item: String): CheckBox {
        val mapCheckedOptions = getSeparatedBottlesStringValues(metaData)
        val checkBox = AppCompatCheckBox(context)
        checkBox.text = item

        if (isMapContainsKey(mapCheckedOptions, item.trim())) checkBox.isChecked = true

        checkBox.setOnCheckedChangeListener { checkBoxView: CompoundButton, isChecked: Boolean ->
            val checkedLabelNames =
                getSeparatedBottlesStringValues(metaData)
            val bottles = StringBuilder()
            val bottleName = checkBoxView.text.toString()
            if (isChecked) {
                checkedLabelNames[bottleName.trim()] = bottleName.trim()
            } else {
                checkedLabelNames.remove(bottleName.trim())
            }
            for ((i, bottleLabel) in checkedLabelNames.keys.withIndex()) {
                if (i == checkedLabelNames.size - 1) {
                    bottles.append(bottleLabel)
                } else {
                    bottles.append(bottleLabel).append("|")
                }
            }

            metaData.currentReading = bottles.toString()
            formOperations.saveDataAndUpdateCreationDate(metaData)
        }
        return checkBox
    }

    private fun isMapContainsKey(
        mapCheckedOptions: HashMap<String, String>,
        item: String
    ): Boolean {
        for ((key, _) in mapCheckedOptions) {
            if (key.lowercase(Locale.getDefault()) == item.lowercase(Locale.getDefault())) {
                return true
            }
        }
        return false
    }

    fun getSeparatedBottlesStringValues(metaData: MetaData): HashMap<String, String> {
        val fieldDataSource = FieldDataSource(context)
        val value = fieldDataSource.getStringValueFromId(
            context.eventID,
            context.locationID,
            context.currentAppID,
            context.curSetID,
            metaData.metaParamID.toString()
        )
        val mapBottles = HashMap<String, String>()
        if (value != null) {
            if (value.isNotEmpty()) {
                val splitArray = value.split("|")
                for (bottleString in splitArray) {
                    mapBottles[bottleString.trim()] = bottleString.trim()
                }
            }
        }
        return mapBottles
    }

    private fun updateBottleData(metaData: MetaData) {

        val operand: String = formOperations.getExpressionFromMetaOrAttribute(metaData) ?: return

        if (operand.isEmpty()) return

        var methodNames: String? = ""

        val exprArray = Util.splitStringToArray("~", operand)

        for (expression in exprArray) {
            if (expression.lowercase().contains("!!set!!")) {
                if (formOperations.isExpressionQueryValid(expression)) {
                    val query: String = formOperations.replaceSetOrVisibleQueryCols(expression)
                    if (query.isNotEmpty()) {
                        methodNames = FieldDataSource(context).hitExpressionQuery(query)
                    }
                } else return
            } else return
        }

        val cocBottles: MutableList<CoCBottles> = ArrayList()
        val bottle = java.lang.StringBuilder()
        if (methodNames != null && methodNames.isNotEmpty()) {
            val methodIdList = java.lang.StringBuilder()
            val listMethodNames = Util.splitStringToArray("|", methodNames)
            val cocDetailDataSource = CocDetailDataSource(context)
            for (i in listMethodNames.indices) {
                val methodName = listMethodNames[i]
                val methodId = cocDetailDataSource.getMethodIDForMethods(methodName)
                if (i == listMethodNames.size - 1) {
                    methodIdList.append(methodId)
                } else {
                    methodIdList.append(methodId).append(",")
                }
            }
            val methodDataSource1 = MethodDataSource(context)
            var allBottlesList: List<CoCBottles>? = ArrayList()
            allBottlesList = methodDataSource1.getBottles(methodIdList.toString())
            cocBottles.addAll(allBottlesList)
            if (cocBottles.size > 0) {
                for (i in cocBottles.indices) {
                    val bottleName = cocBottles[i].bottleName
                    if (i == cocBottles.size - 1) {
                        bottle.append(bottleName)
                    } else {
                        bottle.append(bottleName).append("|")
                    }
                }
            }
        }

        val fieldDataSource = FieldDataSource(context)
        fieldDataSource.updateCheckOptions(
            context.eventID.toString(),
            context.curSetID, context.locationID, context.siteID.toString(),
            context.currentAppID, metaData.metaParamID, bottle.toString()
        )
        handleBottleData(metaData, bottle.toString())
    }

    private fun handleBottleData(metaData: MetaData, value: String?) {
        metaData.currentReading = value
        updateCheckboxValues(metaData)
        formOperations.handleTextData(metaData, tvCheckBoxLabel)
    }

    private fun updateCheckboxValues(metaData: MetaData) {
        val fieldDataSource = FieldDataSource(context)
        val checkOptions = fieldDataSource.getBottleCheckOptions(
            context.eventID.toString(),
            context.curSetID, context.locationID, context.siteID.toString(),
            context.currentAppID, metaData.metaParamID
        )

        if (metaData.getNameValueMap().size == 0 && checkOptions.size > 0) {
            layoutCheckBox.removeAllViews()
            for (item in checkOptions) {
                layoutCheckBox.addView(getCheckBox(metaData, item))
            }
        } else if (metaData.getNameValueMap().size > 0 && checkOptions.size == 0) {
            layoutCheckBox.removeAllViews()
            for (item in metaData.getNameValueMap().keys) {
                layoutCheckBox.addView(getCheckBox(metaData, item))
            }
        }
    }
}