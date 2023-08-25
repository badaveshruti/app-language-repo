package qnopy.com.qnopyandroid.ui.forms.viewHolders

import android.graphics.Typeface
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.MetaData
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.db.MetaDataAttributesDataSource
import qnopy.com.qnopyandroid.ui.activity.FormActivity
import qnopy.com.qnopyandroid.ui.forms.FormOperations
import qnopy.com.qnopyandroid.ui.forms.scopeMainThread

class HeaderViewHolder(
    view: View,
    private val context: FormActivity,
    private val formOperations: FormOperations
) : RecyclerView.ViewHolder(view) {
    val tvHeaderLabel: CustomTextView = view.findViewById(R.id.tvHeaderLabel)
    val layoutFieldControls: LinearLayout = view.findViewById(R.id.layoutFieldControls)

    fun setFieldControlIcons(metaData: MetaData) {
        scopeMainThread.launch { layoutFieldControls.removeAllViews() }
        formOperations.addSupportingIcons(metaData, layoutFieldControls, absoluteAdapterPosition)
    }

    fun setLabelTypeFace(metaData: MetaData) {
        val source = MetaDataAttributesDataSource(context)
        val attributes = source.getMetaDataAttributes(
            context.siteID,
            context.currentAppID, metaData.metaParamID
        )

        var type = Typeface.createFromAsset(context.assets, "fonts/Roboto-Regular.ttf")
        tvHeaderLabel.typeface = type

        var fontStyle = metaData.extField7

        if (metaData.fontStyle != null
            && metaData.fontStyle.isNotEmpty()
        ) fontStyle = metaData.fontStyle

        if (attributes != null) if (attributes.ext_field7 != null
            && attributes.ext_field7.isNotEmpty()
        ) fontStyle = attributes.ext_field7

        type = if (fontStyle != null) {
            if (fontStyle.equals("fontNormal", ignoreCase = true)) Typeface.createFromAsset(
                context.assets,
                "fonts/Roboto-Regular.ttf"
            ) else Typeface.createFromAsset(context.assets, "fonts/Roboto-Bold.ttf")
        } else Typeface.createFromAsset(context.assets, "fonts/Roboto-Bold.ttf")
        tvHeaderLabel.typeface = type
    }
}