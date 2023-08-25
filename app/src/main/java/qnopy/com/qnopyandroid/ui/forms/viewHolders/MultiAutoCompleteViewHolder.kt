package qnopy.com.qnopyandroid.ui.forms.viewHolders

import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import com.pchmn.materialchips.ChipView
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.MetaData
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.db.CocDetailDataSource
import qnopy.com.qnopyandroid.db.FieldDataSource
import qnopy.com.qnopyandroid.db.LovDataSource
import qnopy.com.qnopyandroid.db.MethodDataSource
import qnopy.com.qnopyandroid.requestmodel.CoCBottles
import qnopy.com.qnopyandroid.requestmodel.SCocDetails
import qnopy.com.qnopyandroid.ui.activity.AutoCompleteHandlerActivity
import qnopy.com.qnopyandroid.ui.activity.FormActivity
import qnopy.com.qnopyandroid.ui.forms.*
import qnopy.com.qnopyandroid.uiutils.AutoCompleteHandler

class MultiAutoCompleteViewHolder(
    view: View, private val formOperations: FormOperations, private val context: FormActivity,
    private val formsAdapter: FormsAdapter
) :
    RecyclerView.ViewHolder(view) {
    var tvAutoCompleteLabel: CustomTextView = view.findViewById(R.id.tvAutoCompleteLabel)
    private var tvEmptyAutoComplete: CustomTextView = view.findViewById(R.id.tvEmptyAutoComplete)
    var layoutFieldControls: LinearLayout = view.findViewById(R.id.layoutFieldControls)
    private var flexBoxAutoComplete: FlexboxLayout = view.findViewById(R.id.flexBoxAutoComplete)

    fun setFieldControlIcons(metaData: MetaData) {
        scopeMainThread.launch { layoutFieldControls.removeAllViews() }
        formOperations.addSupportingIcons(metaData, layoutFieldControls, absoluteAdapterPosition)
    }

    fun addChipsToContainer(metaData: MetaData) {

        val lovDS = LovDataSource(context)
        val nameValPair = lovDS.getLovNameValuePair(
            metaData.metaLovId,
            formOperations.siteId
        )

        if (!metaData.metaInputType.isNullOrEmpty()
            && metaData.metaInputType.equals(
                input_type_autocomplete
            )
        ) tvEmptyAutoComplete.text =
            context.getString(R.string.tap_here_to_select_value)
        else tvEmptyAutoComplete.text =
            context.getString(R.string.tap_here_to_select_multiple_values)

        formOperations.getDefaultValueToSet(metaData, tvAutoCompleteLabel)

/*        val value = formOperations.calculateSetExpression(metaData)
        if (value != null && value.isNotEmpty()) metaData.currentReading = value*/

        flexBoxAutoComplete.removeAllViews()

        if (metaData.metaInputType.equals("MULTIMETHODS", ignoreCase = true)) {
            updateMultiMethodAutoCompleteView(metaData, metaData.currentReading)
        } else {

            setClickListenerEmptyView(metaData)

            if (metaData.metaInputType.equals("AUTOSETGENERATOR", ignoreCase = true)
                && formsAdapter.AUTO_GENERATE
            ) {
                val fieldOperandsExpression =
                    formOperations.getExpressionFromMetaOrAttribute(metaData)

                if (fieldOperandsExpression != null && fieldOperandsExpression.isNotEmpty()
                    && fieldOperandsExpression.contains("COPY")
                ) {
                    val mobAppID = fieldOperandsExpression
                        .substring(
                            fieldOperandsExpression.indexOf("{") + 1,
                            fieldOperandsExpression.lastIndexOf("}")
                        ).toInt()
                    metaData.formID = mobAppID
                }

                if (metaData.formID > 0) {
                    formOperations.addEnableFormsIcon(metaData, layoutFieldControls)
                }
            }

            if (nameValPair != null) {
                //MULTIAUTOCOMPLTE
                val displayValues = metaData.currentReading
                var displayKeys = AutoCompleteHandler.getCharSeperatedKeys(
                    nameValPair,
                    displayValues,
                    "\\|"
                ) //displayValue contains comma
                if (displayKeys != null && displayKeys.isNotEmpty()) {
                    tvEmptyAutoComplete.visibility = View.GONE
                    flexBoxAutoComplete.visibility = View.VISIBLE

                    if (displayKeys.contains(";null")) {
                        displayKeys = displayKeys.replace(";null", "")
                    } else if (displayKeys.contains("null")) {
                        displayKeys = displayKeys.replace("null", "")
                    }
                    if (displayKeys.contains(";")) { //Multiple values selected MULTI-AUTOCOMPLETE
                        val selectedKeys = displayKeys.split(";".toRegex()).toTypedArray()
                        if (selectedKeys.isNotEmpty()) {
                            for (label in selectedKeys) {
                                val chipView = ChipView(context)
                                formOperations.updateChild(metaData, label)
                                formOperations.updateNavigateToFormID(metaData, label)
                                chipView.setDeletable(false)
                                chipView.label = label

                                chipView.setOnChipClicked {
                                    val intent = Intent(
                                        context,
                                        AutoCompleteHandlerActivity::class.java
                                    )
                                    intent.putExtra("INPUT_TYPE", metaData.metaInputType)
                                    intent.putExtra("LOV_ID", metaData.metaLovId)
                                    intent.putExtra("PARENT_LOV_ID", 0)
                                    intent.putExtra("SELECTED_VALUES", displayValues)
                                    intent.putExtra("POSITION", metaData.metaParamID.toString())
                                    intent.putExtra("SET_ID", context.curSetID)
                                    context.startActivityForResult(
                                        intent,
                                        FormActivity.AUTOCOMPLETE_REQUEST_CODE
                                    )
                                }
                                flexBoxAutoComplete.addView(chipView)
                            }
                        }
                    } else { //Single value selected
                        val chipView = ChipView(context)
                        chipView.setDeletable(false)
                        chipView.label = displayKeys
                        formOperations.updateChild(metaData, displayKeys)
                        formOperations.updateNavigateToFormID(metaData, displayKeys)

                        chipView.setOnChipClicked {
                            val intent = Intent(
                                context,
                                AutoCompleteHandlerActivity::class.java
                            )
                            intent.putExtra("INPUT_TYPE", metaData.metaInputType)
                            intent.putExtra("LOV_ID", metaData.metaLovId)
                            intent.putExtra("PARENT_LOV_ID", 0)
                            intent.putExtra("SELECTED_VALUES", displayValues)
                            intent.putExtra("POSITION", metaData.metaParamID.toString())
                            intent.putExtra("SET_ID", context.curSetID)
                            context.startActivityForResult(
                                intent,
                                FormActivity.AUTOCOMPLETE_REQUEST_CODE
                            )
                        }
                        flexBoxAutoComplete.addView(chipView)
                    }
                } else {
                    //BUG FIX: PARENT-CHILD NAVIGATION FORM NOT REFRESHING
                    formOperations.updateChild(metaData, metaData.currentReading)
                    formOperations.updateNavigateToFormID(metaData, metaData.currentReading)
                    tvEmptyAutoComplete.visibility = View.VISIBLE
                    flexBoxAutoComplete.visibility = View.GONE
                }

                if (metaData.metaInputType.equals("AUTOSETGENERATOR", ignoreCase = true)
                    && formsAdapter.AUTO_GENERATE
                ) {
                    formOperations.manageAutoGenerateSet(metaData)
                }
            } else {
                //11-Jul-17 AUTOCOMPLETE
                if (metaData.currentReading != null && metaData.currentReading.isNotEmpty()) {
                    tvEmptyAutoComplete.visibility = View.GONE
                    flexBoxAutoComplete.visibility = View.VISIBLE

                    var key = lovDS.getKeyForLovValue(
                        metaData.metaLovId, metaData.currentReading,
                        formOperations.siteId
                    )
                    if (key == null || key.isEmpty()) {
                        key = metaData.currentReading
                    }
                    formOperations.updateChild(metaData, key)
                    formOperations.updateNavigateToFormID(metaData, key)
                    val chipView = ChipView(context)
                    chipView.setDeletable(false)
                    chipView.label = key
                    chipView.setOnChipClicked {
                        val intent = Intent(context, AutoCompleteHandlerActivity::class.java)
                        intent.putExtra("INPUT_TYPE", metaData.metaInputType)
                        intent.putExtra("LOV_ID", metaData.metaLovId)
                        intent.putExtra("PARENT_LOV_ID", 0)
                        intent.putExtra("SELECTED_VALUES", metaData.currentReading)
                        intent.putExtra("POSITION", metaData.metaParamID.toString())
                        intent.putExtra("SET_ID", context.curSetID)
                        context.startActivityForResult(
                            intent,
                            FormActivity.AUTOCOMPLETE_REQUEST_CODE
                        )
                    }
                    flexBoxAutoComplete.addView(chipView)
                } else {
                    //12-Sep-17 BUG FIX: PARENT-CHILD NAVIGATION FORM NOT REFRESHING
                    formOperations.updateChild(metaData, metaData.currentReading)
                    formOperations.updateNavigateToFormID(metaData, metaData.currentReading)
                    tvEmptyAutoComplete.visibility = View.VISIBLE
                    flexBoxAutoComplete.visibility = View.GONE
                }
            }
        }

        scopeIO.launch {
            formOperations.handleTextData(metaData, tvAutoCompleteLabel)
            formOperations.checkWarningValueForViolation(metaData)
        }

//        formOperations.checkForSubFieldsForVisibility(metaData)
    }

    private fun setClickListenerEmptyView(metaData: MetaData) {
        tvEmptyAutoComplete.setOnClickListener {
            val intent = Intent(context, AutoCompleteHandlerActivity::class.java)
            intent.putExtra("INPUT_TYPE", metaData.metaInputType)
            intent.putExtra("LOV_ID", metaData.metaLovId)
            intent.putExtra("PARENT_LOV_ID", 0)
            intent.putExtra("SELECTED_VALUES", metaData.currentReading)
            intent.putExtra("POSITION", metaData.metaParamID.toString())
            intent.putExtra("SET_ID", context.curSetID)

            context.startActivityForResult(intent, FormActivity.AUTOCOMPLETE_REQUEST_CODE)
        }
    }

    fun updateMultiMethodAutoCompleteView(
        metaData: MetaData,
        displayVals: String?,
    ) {

        var displayValues = displayVals

        val fpID = metaData.metaParamID.toString()
        if (context.currCocID != null && context.currCocID.isNotEmpty()) {
            val sCocDetailsList: List<SCocDetails>

            val detailDataSource = CocDetailDataSource(context)
            sCocDetailsList = detailDataSource.getDefaultMethodfromcocDetail(
                context.currCocID,
                context.locationID
            )

            if (sCocDetailsList != null && sCocDetailsList.isNotEmpty()) {
                metaData.currentReading = ""
                for (i in sCocDetailsList.indices) {
                    val methodName = sCocDetailsList[i].method
                    if (sCocDetailsList.size - 1 == i) {
                        metaData.currentReading = metaData.currentReading + methodName
                    } else {
                        metaData.currentReading = metaData.currentReading + methodName + "|"
                    }
                }
            }

            val expression: String? = formOperations.getExpressionFromMetaOrAttribute(metaData)

            val cocBottles: MutableList<CoCBottles> = ArrayList()
            val bottle = StringBuilder()

            if (expression != null && expression.contains("COPY1")) {
                //COPY1(|1196|#1#|2530|)
                val bottleFpID = expression.substring(
                    expression.lastIndexOf("#")
                            + 2, expression.lastIndexOf("|")
                ).toInt()
                if (sCocDetailsList != null && sCocDetailsList.isNotEmpty()) {
                    val methodIdList = StringBuilder()
                    for (i in sCocDetailsList.indices) {
                        if (i == sCocDetailsList.size - 1) {
                            methodIdList.append(sCocDetailsList[i].methodId)
                        } else {
                            methodIdList.append(sCocDetailsList[i].methodId).append(",")
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
                    context.curSetID, context.locationID, context.toString(),
                    context.currentAppID, bottleFpID, bottle.toString()
                )

                metaData.currentReading = bottle.toString()
                formsAdapter.updateBottles(bottleFpID, bottle.toString())
            }
        }

        displayValues = metaData.currentReading
        if ((displayValues != null) && displayValues.isNotEmpty() && !displayValues.equals(
                "null",
                ignoreCase = true
            )
        ) {
            if (displayValues.contains("|null")) {
                displayValues = displayValues.replace("|null", "")
            } else if (displayValues.contains("null")) {
                displayValues = displayValues.replace("null", "")
            }
            if (displayValues.contains("|")) { //Multiple values selected MULTI-AUTOCOMPLETE
                val selectedKeys = displayValues.split("\\|").toTypedArray()
                if (selectedKeys.isNotEmpty()) {
                    for (label: String in selectedKeys) {
                        val chipview = ChipView(context)
                        chipview.setDeletable(false)
                        chipview.label = label
                        val finalDisplayValues: String = displayValues
                        chipview.setOnChipClicked {
                            val intent = Intent(
                                context,
                                AutoCompleteHandlerActivity::class.java
                            )
                            intent.putExtra("INPUT_TYPE", metaData.metaInputType)
                            intent.putExtra("LOV_ID", 0)
                            intent.putExtra("PARENT_LOV_ID", 0)
                            intent.putExtra("SELECTED_VALUES", finalDisplayValues)
                            intent.putExtra("POSITION", fpID)
                            context.startActivityForResult(
                                intent,
                                FormActivity.AUTOCOMPLETE_REQUEST_CODE
                            )
                        }
                        if (label.isNotEmpty()) flexBoxAutoComplete.addView(chipview)
                    }
                }
            } else { //Single value selected
                val chipView = ChipView(context)
                chipView.setDeletable(false)
                chipView.label = displayValues
                val finalDisplayValues: String = displayValues
                chipView.setOnChipClicked {
                    val intent = Intent(context, AutoCompleteHandlerActivity::class.java)
                    intent.putExtra("INPUT_TYPE", metaData.metaInputType)
                    intent.putExtra("LOV_ID", 0)
                    intent.putExtra("PARENT_LOV_ID", 0)
                    intent.putExtra("SELECTED_VALUES", finalDisplayValues)
                    intent.putExtra("POSITION", fpID)
                    context.startActivityForResult(
                        intent,
                        FormActivity.AUTOCOMPLETE_REQUEST_CODE
                    )
                }
                if (displayValues.isNotEmpty()) flexBoxAutoComplete.addView(chipView)
            }
        } else {
            tvEmptyAutoComplete.visibility = View.VISIBLE
            flexBoxAutoComplete.visibility = View.GONE
        }

        val finalDisplayValues1 = displayValues
        tvEmptyAutoComplete.setOnClickListener {
            val intent = Intent(context, AutoCompleteHandlerActivity::class.java)
            intent.putExtra("INPUT_TYPE", metaData.metaInputType)
            intent.putExtra("LOV_ID", 0)
            intent.putExtra("PARENT_LOV_ID", 0)
            intent.putExtra("SELECTED_VALUES", finalDisplayValues1)
            intent.putExtra("POSITION", fpID)
            context.startActivityForResult(intent, FormActivity.AUTOCOMPLETE_REQUEST_CODE)
        }

        metaData.currentReading = displayValues
        formOperations.handleTextData(metaData, tvAutoCompleteLabel)
        formOperations.checkWarningValueForViolation(metaData)
        //adding here at last line coz below method takes method from dFieldData so whatever
        //methods are stored above will get fetched
        //Note: commented on 13 August, 21 as for now commented it to check the form loading performance
        //by commenting you wont be able to update bottle data if analysis is added in case of !!set!! expression
        for (entry: Map.Entry<String?, MetaData?> in formsAdapter.mapSetExprMetaObjects.entries) {
            if (metaData.metaInputType.lowercase().contains("checkbox"))
                entry.value?.let { formsAdapter.updateBottlesData(it.metaParamID) }
        }
    }
}