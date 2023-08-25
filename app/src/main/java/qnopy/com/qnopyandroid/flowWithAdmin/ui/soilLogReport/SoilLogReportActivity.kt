package qnopy.com.qnopyandroid.flowWithAdmin.ui.soilLogReport

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.clientmodel.MetaData
import qnopy.com.qnopyandroid.clientmodel.MobileApp
import qnopy.com.qnopyandroid.db.FieldDataSource
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity

@AndroidEntryPoint
class SoilLogReportActivity : ProgressDialogActivity() {

    var mapDepthFields: java.util.HashMap<Int, MetaData> =
        HashMap()//this has tab id and metadata mapping for depth fields as per form
    var maxDepth: Int = 0
    private var mobileAppIds: String = ""
    var listTabs: ArrayList<MobileApp> = ArrayList()
    var siteId: String = ""
    var locId: String = ""
    var eventId: String = ""
    var mapDepthsByTab: HashMap<Int, HashMap<Int, DepthFieldData>> = HashMap()

    companion object {
        const val keyTabsList = "tabsList"

        fun startSoilLogActivity(
            context: AppCompatActivity,
            listTabs: MutableList<out MobileApp>,
            siteId: String,
            locId: String,
            eventId: String,
            launcher: ActivityResultLauncher<Intent>
        ) {
            val intent = Intent(context, SoilLogReportActivity::class.java)
            intent.putExtra(keyTabsList, ArrayList(listTabs))
            intent.putExtra(GlobalStrings.KEY_SITE_ID, siteId)
            intent.putExtra(GlobalStrings.KEY_LOCATION_ID, locId)
            intent.putExtra(GlobalStrings.KEY_EVENT_ID, eventId)
            launcher.launch(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentData()
        getMaxRowsByDepth()

        setContentView(SoilReportMainLayout(this))

        supportActionBar?.title = " Soil Log Report"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getIntentData() {
        intent?.let {

            siteId = it.getStringExtra(GlobalStrings.KEY_SITE_ID).toString()
            locId = it.getStringExtra(GlobalStrings.KEY_LOCATION_ID).toString()
            eventId = it.getStringExtra(GlobalStrings.KEY_EVENT_ID).toString()

            listTabs =
                it.getSerializableExtra(keyTabsList) as ArrayList<MobileApp>
            listTabs = ArrayList(listTabs.filter { tab ->
                tab.appDescription.lowercase().contains("|")
            })
            //list tabs has header cols
            listTabs = ArrayList(listTabs.sortedWith(compareBy(MobileApp::getTabOrderForReport)))

            mobileAppIds =
                (listTabs.map { tab -> tab.appID.toString() }).joinToString(separator = ",")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    private fun getMaxRowsByDepth() {
//        Util.getCabSeparatedString()

        val fieldDataSrc = FieldDataSource(this)
        mapDepthFields = fieldDataSrc.getDepthFields(mobileAppIds)

        val depthFieldIds =
            (mapDepthFields.map { field -> field.value.metaParamID.toString() })
                .joinToString(separator = ",")

        maxDepth =
            fieldDataSrc.getMaxSoilLogDepth(siteId, locId, eventId, mobileAppIds, depthFieldIds)

        for (depthField in mapDepthFields) {

            //below list will have depth string values rounded as per set to match with the depths we
            //had to put its show fields concatenated values accordingly
            val listDepthStringValues =
                fieldDataSrc.getDepthFieldsStringValues(siteId, locId, eventId, depthField.value)

            for (depthFieldData in listDepthStringValues) {

                //taking out all show fields which are already sorted so that we take their string values by set
                val listShowFields = fieldDataSrc.getShowFields(depthField.value.formID.toString())

                val showFieldIds =
                    (listShowFields.map { field -> field.metaParamID.toString() })
                        .joinToString(separator = ",")
                val showFieldStringValues = fieldDataSrc.getShowFieldsStringValues(
                    siteId,
                    locId,
                    depthField.value.formID.toString(),
                    depthFieldData.setId.toString(), showFieldIds
                )

                depthFieldData.stringValue?.let {
                    val key = depthFieldData.stringValue.toInt()
                    val value = DepthFieldData(
                        siteId = siteId,
                        locId = locId,
                        tabId = depthField.value.formID,
                        setId = depthFieldData.setId,
                        showFieldsValue = showFieldStringValues,
                        eventId = eventId, depthValue = depthFieldData.stringValue.toInt()
                    )

                    if (mapDepthsByTab.containsKey(depthField.value.formID)) {
                        //if map has tab in it means it has hashmap value
                        //now check if hashmap for depth has any data for depth
                        //if no depth found then add entry to hashmap else get value of depth and concat the data
                        val mapDepths = mapDepthsByTab[depthField.value.formID]

                        if (mapDepths!!.contains(key)) {
                            if (showFieldStringValues != null)
                                if (!mapDepths[key]?.showFieldsValue.isNullOrEmpty()) {
                                    mapDepths[key]?.showFieldsValue =
                                        "${mapDepths[key]?.showFieldsValue},$showFieldStringValues"
                                } else {
                                    mapDepths[key]?.showFieldsValue =
                                        showFieldStringValues
                                }
                        } else {
                            mapDepths[key] = value
                        }
                    } else {
                        //if map don't have tab then add entry and new hashmap as value
                        val mapDepths: HashMap<Int, DepthFieldData> = HashMap()
                        mapDepths[key] = value
                        mapDepthsByTab[depthField.value.formID] = mapDepths
                    }
                }
            }
        }
    }
}