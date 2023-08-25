package qnopy.com.qnopyandroid.ui.forms.viewHolders

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.MetaData
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.ui.forms.FormOperations
import qnopy.com.qnopyandroid.ui.forms.scopeMainThread
import qnopy.com.qnopyandroid.util.Util

class RadioBtnViewHolder(view: View, private val formOperations: FormOperations) :
    RecyclerView.ViewHolder(view) {
    val tvRadioLabel: CustomTextView = view.findViewById(R.id.tvRadioLabel)
    val layoutFieldControls: LinearLayout = view.findViewById(R.id.layoutFieldControls)
    val radioGroupLayout: LinearLayout = view.findViewById(R.id.radioGroupLayout)
    private val formColor = Color.parseColor("#196b76")

    fun setFieldControlIcons(metaData: MetaData) {
        scopeMainThread.launch { layoutFieldControls.removeAllViews() }
        formOperations.addSupportingIcons(metaData, layoutFieldControls, absoluteAdapterPosition)
    }

    fun setRadioItems(metaData: MetaData, context: Context, locationId: String) {

        formOperations.getDefaultValueToSet(metaData, tvRadioLabel)
        formOperations.handleTextData(metaData, tvRadioLabel)

        scopeMainThread.launch {
            radioGroupLayout.removeAllViews()

            val names = ArrayList<String>()
            for (key in metaData.nameValueMap.keys) {
                names.add(key)
            }

            if (names.size > 3) {
                val parts = Util.getChopped(names, 4)
                for (i in parts.indices) {

                    val radioGroup =
                        getRadioGroup(metaData, parts[i], context)
                    radioGroupLayout.addView(radioGroup)
                }
            } else {
                val radioGroup = getRadioGroup(
                    metaData, names, context
                )
                radioGroupLayout.addView(radioGroup)
            }
        }
    }

    private fun getRadioGroup(
        metaData: MetaData, strList: List<String>, context: Context
    ): RadioGroup {
        val radioGroup = RadioGroup(context)
        var radio: RadioButton? = null
        radioGroup.orientation = LinearLayout.HORIZONTAL // 0-horizontal and 1-vertical
        radioGroup.gravity = Gravity.CENTER
        radioGroup.bottom = 4
        val params = RadioGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.weight = 1f
        params.setMargins(0, 5, 0, 0)

        for (i in strList.indices) {
            radio = RadioButton(context)
//            radio.textSize = Util.dpToPx(5).toFloat()
            radio.text = strList[i]
            radio.layoutParams = params

            radio.id = i
            radio.tag = metaData.metaParamID.toString()

            if (!metaData.currentReading.isNullOrBlank() && strList[i].trim().equals(
                    metaData.currentReading.trim(),
                    ignoreCase = true
                )
            ) {
                radio.isChecked = true
                radio.setTextColor(formColor)
            }

            radio.setOnClickListener(View.OnClickListener { v ->
                val checked = (v as RadioButton).isChecked
                v.setTextColor(formColor)

                // Check which radio button was clicked
                val selectedText = v.text as String
                metaData.currentReading = selectedText

                try {
                    for (view in radioGroup.children) {
                        if (!(view as RadioButton).text.equals(selectedText))
                            view.setTextColor(ContextCompat.getColor(context, R.color.black_faint))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                formOperations.handleTextData(metaData, tvRadioLabel)
                formOperations.checkWarningValueForViolation(metaData)
                formOperations.checkForSubFieldsForVisibility(metaData, absoluteAdapterPosition)
            })
            radioGroup.addView(radio)
        }
        return radioGroup
    }
}