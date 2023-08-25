package qnopy.com.qnopyandroid.flowWithAdmin.ui.soilLogReport

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.MobileApp
import qnopy.com.qnopyandroid.customView.CustomItalicTextView
import qnopy.com.qnopyandroid.customView.CustomTextView
import kotlin.math.max

class SoilReportMainLayout(context: Context?) : RelativeLayout(context) {

    private val allFirstColWidth: Int = 70
    private val mapHeaders: HashMap<String, MobileApp> = HashMap()
    private val tag = "SoilReport"

    var headers: ArrayList<String> = ArrayList()

    var fixedWidth = 200

    lateinit var tableA: TableLayout
    lateinit var tableB: TableLayout
    lateinit var tableC: TableLayout
    lateinit var tableD: TableLayout

    lateinit var horizontalScrollViewB: HorizontalScrollView
    lateinit var horizontalScrollViewD: HorizontalScrollView

    lateinit var scrollViewC: ScrollView
    lateinit var scrollViewD: ScrollView

    var headerCellsWidthMap = HashMap<Int, Int>()
    var activityContext: SoilLogReportActivity

    init {
        activityContext = context as SoilLogReportActivity

        initHeaders()
        // initialize the main components (TableLayouts, HorizontalScrollView, ScrollView)

        initComponents()
        setComponentsId()
        setScrollViewAndHorizontalScrollViewTag()

        // no need to assemble component A, since it is just a table
        horizontalScrollViewB.addView(tableB)

        scrollViewC.addView(tableC)

        scrollViewD.addView(horizontalScrollViewD)
        horizontalScrollViewD.addView(tableD)

        // add the components to be part of the main layout
        addComponentToMainLayout()

        setBackgroundColor(Color.WHITE)

        // add some table rows
        addTableRowToTableA()
        addTableRowToTableB()

        resizeHeaderHeight()

        getTableRowHeaderCellWidth()

        generateTableCAndTableD()

        resizeBodyTableRowHeight()
    }

    private fun initHeaders() {
        headers.add("Depth")

        val listTabs = activityContext.listTabs

        for (app in listTabs) {
            headers.add(app.appName)
            mapHeaders[app.appName] = app
        }
    }

    // initialized components
    private fun initComponents() {
        tableA = TableLayout(context)
        tableB = TableLayout(context)
        tableC = TableLayout(context)
        tableD = TableLayout(context)

        tableA.setBackgroundColor(Color.BLACK)
        tableB.setBackgroundColor(Color.BLACK)
        tableC.setBackgroundColor(Color.BLACK)

        horizontalScrollViewB = MyHorizontalScrollView(context)
        horizontalScrollViewB.isHorizontalScrollBarEnabled = false
        horizontalScrollViewB.setBackgroundColor(Color.LTGRAY)

        horizontalScrollViewD = MyHorizontalScrollView(context)
        horizontalScrollViewD.isHorizontalScrollBarEnabled = false

        scrollViewC = MyScrollView(context)
        scrollViewC.isVerticalScrollBarEnabled = false

        scrollViewD = MyScrollView(context)
        scrollViewD.isVerticalScrollBarEnabled = false
    }

    // set essential component IDs
    private fun setComponentsId() {
        tableA.id = R.id.tableAId
        horizontalScrollViewB.id = R.id.horizontalScrollViewBId
        scrollViewC.id = R.id.scrollViewC
        scrollViewD.id = R.id.scrollViewD
    }

    // set tags for some horizontal and vertical scroll view
    private fun setScrollViewAndHorizontalScrollViewTag() {
        horizontalScrollViewB.tag = "horizontal scroll view b"
        horizontalScrollViewD.tag = "horizontal scroll view d"
        scrollViewC.tag = "scroll view c"
        scrollViewD.tag = "scroll view d"
    }

    // we add the components here in our TableMainLayout
    private fun addComponentToMainLayout() {

        // RelativeLayout params were very useful here
        // the addRule method is the key to arrange the components properly
        val componentBParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        componentBParams.addRule(RIGHT_OF, tableA.id)
        val componentCParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        componentCParams.addRule(BELOW, tableA.id)
        val componentDParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        componentDParams.addRule(RIGHT_OF, scrollViewC.id)
        componentDParams.addRule(BELOW, horizontalScrollViewB.id)

        // 'this' is a relative layout,
        // we extend this table layout as relative layout as seen during the creation of this class
        addView(tableA)
        addView(horizontalScrollViewB, componentBParams)
        addView(scrollViewC, componentCParams)
        addView(scrollViewD, componentDParams)
    }

    private fun addTableRowToTableA() {
        tableA.addView(componentATableRow())
    }

    private fun addTableRowToTableB() {
        tableB.addView(componentBTableRow())
    }

    // generate table row of table A
    private fun componentATableRow(): TableRow {
        val componentATableRow = TableRow(context)
        val textView: TextView = headerTextView(headers[0])
//        textView.maxLines = 5
        textView.movementMethod = ScrollingMovementMethod()

        val params = TableRow.LayoutParams(allFirstColWidth, LayoutParams.MATCH_PARENT)
        params.setMargins(0, 0, 0, 2)
        textView.layoutParams = params
        componentATableRow.addView(textView)
        return componentATableRow
    }

    // generate table row of table B
    private fun componentBTableRow(): TableRow {
        val componentBTableRow = TableRow(context)

        val params = TableRow.LayoutParams(fixedWidth, LayoutParams.MATCH_PARENT)
        params.setMargins(2, 0, 0, 2)
        for (x in 1 until headers.size) {
            val textView: TextView = headerTextView(headers[x])
            textView.layoutParams = params
//            textView.maxLines = 5
            textView.movementMethod = ScrollingMovementMethod()
            componentBTableRow.addView(textView)
        }

        return componentBTableRow
    }

    // generate table row of table C and table D
    private fun generateTableCAndTableD() {

        for (i in 0..activityContext.maxDepth) {
            val tableRowForTableC =
                tableRowForTableC(i.toString())//first cols line
            val taleRowForTableD = tableRowForTableD(i)
            tableRowForTableC.setBackgroundColor(Color.LTGRAY)
            taleRowForTableD.setBackgroundColor(Color.LTGRAY)
            tableC.addView(tableRowForTableC)
            tableD.addView(taleRowForTableD)
        }
    }

    // a TableRow for table C
    private fun tableRowForTableC(value: String?): TableRow {

        val params = TableRow.LayoutParams(allFirstColWidth, LayoutParams.MATCH_PARENT)
        params.setMargins(0, 0, 2, 2)
        val tableRowForTableC = TableRow(context)
        val textView = bodyTextView(value)
        textView.setTextColor(Color.BLACK)
        tableRowForTableC.addView(textView, params)
        return tableRowForTableC
    }

    private fun jumpToSet(depthFieldData: DepthFieldData?, appID: Int, hasStringValue: Boolean) {

        //changing the tabId value as the depthFieldData will only have the tabId which has string
        //value so if u click on empty col it'll take u to another tabId which we don't want.
        if (!hasStringValue) {
            depthFieldData?.tabId = appID
        }

        showPopupToJumpToSet(depthFieldData)
    }

    private fun showPopupToJumpToSet(depthFieldData: DepthFieldData?) {
        val view = LayoutInflater.from(context).inflate(
            R.layout.alert_simple_message,
            null, false
        )
        val btnShowSet = view.findViewById<CustomItalicTextView>(R.id.btnShowSet)
        val btnNo = view.findViewById<CustomItalicTextView>(R.id.btnNo)
        val tvSetLabel = view.findViewById<CustomTextView>(R.id.tvSetLabel)
        val label = "Do you want to navigate to the form?"
        tvSetLabel.text = label
        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        val alertDialog = builder.create()
        alertDialog.show()
        btnShowSet.setOnClickListener {
            alertDialog.cancel()
            val intent = Intent()
            intent.putExtra(GlobalStrings.FORM_DETAILS, depthFieldData)
            intent.putExtra(
                GlobalStrings.KEY_FIELD_PARAM_ID,
                activityContext.mapDepthFields[depthFieldData?.tabId]?.metaParamID
            )
            activityContext.setResult(Activity.RESULT_OK, intent)
            activityContext.finish()
        }
        btnNo.setOnClickListener { alertDialog.cancel() }
    }

    private fun tableRowForTableD(depth: Int): TableRow {
        val taleRowForTableD = TableRow(context)
        val loopCount = (tableB.getChildAt(0) as TableRow).childCount

        for (x in 0 until loopCount) {
            val params = TableRow.LayoutParams(fixedWidth, LayoutParams.MATCH_PARENT)
            params.setMargins(2, 0, 0, 2)
            val key = headers[x + 1]//+1 coz 0th pos has depth col
            val tab = mapHeaders[key]
            var value: String? = null

            var depthFieldData: DepthFieldData? = null

            //check if mapDepthTab has tabId if it has key then fetch the hashmap and check
            //if there is any value for depth if there is any value then show the string value else
            //put the dummy fieldDepthData to navigate to form when col clicked

            if (activityContext.mapDepthsByTab.containsKey(tab?.appID)) {
                val mapDepths = activityContext.mapDepthsByTab[tab?.appID]

                if (mapDepths != null && mapDepths.containsKey(depth)) {
                    val depthShowFieldsValue = mapDepths[depth]
                    depthShowFieldsValue?.let {
                        if (tab?.appID == depthShowFieldsValue.tabId) {
                            value = depthShowFieldsValue.showFieldsValue
                        }
                        depthFieldData = depthShowFieldsValue
                    }
                }
            }

            val textViewB = bodyTextView(value)
            textViewB.setOnClickListener {
                if (depthFieldData == null) {
                    val depthField = DepthFieldData(
                        siteId = activityContext.siteId,
                        activityContext.locId, tab!!.appID, 1,
                        activityContext.eventId, depth, ""
                    )
                    jumpToSet(
                        depthField,
                        tab.appID,
                        textViewB.text.toString().isNotEmpty()
                    )
                } else
                    jumpToSet(
                        depthFieldData,
                        tab!!.appID,
                        textViewB.text.toString().isNotEmpty()
                    )
            }

//            TextView textViewB = bodyTextView(info[x]);
            taleRowForTableD.addView(textViewB, params)
        }
        return taleRowForTableD
    }

    // table cell standard TextView
    private fun bodyTextView(label: String?): TextView {
        val bodyTextView = TextView(context)
        bodyTextView.setBackgroundColor(Color.WHITE)
        bodyTextView.text = label
        bodyTextView.gravity = Gravity.CENTER
        bodyTextView.setPadding(5, 5, 5, 5)
//        bodyTextView.maxLines = 5
        bodyTextView.movementMethod = ScrollingMovementMethod()
        return bodyTextView
    }

    // header standard TextView
    fun headerTextView(label: String?): TextView {
        val headerTextView = TextView(context)
        headerTextView.setBackgroundColor(Color.WHITE)
        headerTextView.text = label
        headerTextView.gravity = Gravity.CENTER
        headerTextView.setPadding(5, 5, 5, 5)
        return headerTextView
    }

    // resizing TableRow height starts here
    private fun resizeHeaderHeight() {
        val productNameHeaderTableRow = tableA.getChildAt(0) as TableRow
        val productInfoTableRow = tableB.getChildAt(0) as TableRow
        val rowAHeight = viewHeight(productNameHeaderTableRow)
        val rowBHeight = viewHeight(productInfoTableRow)
        val tableRow =
            if (rowAHeight < rowBHeight) productNameHeaderTableRow else productInfoTableRow
        val finalHeight = if (rowAHeight > rowBHeight) rowAHeight else rowBHeight
        matchLayoutHeight(tableRow, finalHeight)
    }

    private fun getTableRowHeaderCellWidth() {
        val tableAChildCount = (tableA.getChildAt(0) as TableRow).childCount
        val tableBChildCount = (tableB.getChildAt(0) as TableRow).childCount
        for (x in 0 until tableAChildCount + tableBChildCount) {
            if (x == 0) {
                headerCellsWidthMap[x] = viewWidth((tableA.getChildAt(0) as TableRow).getChildAt(x))
            } else {
                headerCellsWidthMap[x] =
                    viewWidth((tableB.getChildAt(0) as TableRow).getChildAt(x - 1))
            }
        }
    }

    // resize body table row height
    private fun resizeBodyTableRowHeight() {
        val tableCChildCount = tableC.childCount

        for (x in 0 until tableCChildCount) {
            val productNameHeaderTableRow = tableC.getChildAt(x) as TableRow
            val productInfoTableRow = tableD.getChildAt(x) as TableRow
            val rowAHeight = viewHeight(productNameHeaderTableRow)
            val rowBHeight = viewHeight(productInfoTableRow)
            val tableRow =
                if (rowAHeight < rowBHeight) productNameHeaderTableRow else productInfoTableRow
            val finalHeight = max(rowAHeight, rowBHeight)
            matchLayoutHeight(tableRow, finalHeight)
        }
    }

    // match all height in a table row
    // to make a standard TableRow height
    private fun matchLayoutHeight(tableRow: TableRow, height: Int) {
        val tableRowChildCount = tableRow.childCount

        // if a TableRow has only 1 child
        if (tableRow.childCount == 1) {
            val view = tableRow.getChildAt(0)
            val params = view.layoutParams as TableRow.LayoutParams
            params.height = height - (params.bottomMargin + params.topMargin)
            return
        }

        // if a TableRow has more than 1 child
        for (x in 0 until tableRowChildCount) {
            val view = tableRow.getChildAt(x)
            val params = view.layoutParams as TableRow.LayoutParams
            if (!isTheHighestLayout(tableRow, x)) {
                params.height = height - (params.bottomMargin + params.topMargin)
                return
            }
        }
    }

    // check if the view has the highest height in a TableRow
    private fun isTheHighestLayout(tableRow: TableRow, layoutPosition: Int): Boolean {
        val tableRowChildCount = tableRow.childCount
        var highestViewPosition = -1
        var viewHeight = 0
        for (x in 0 until tableRowChildCount) {
            val view = tableRow.getChildAt(x)
            val height = viewHeight(view)
            if (viewHeight < height) {
                highestViewPosition = x
                viewHeight = height
            }
        }
        return highestViewPosition == layoutPosition
    }

    // read a view's height
    private fun viewHeight(view: View): Int {
        view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        return view.measuredHeight
    }

    // read a view's width
    private fun viewWidth(view: View): Int {
        view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        return view.measuredWidth
    }

    // horizontal scroll view custom class
    inner class MyHorizontalScrollView(context: Context?) :
        HorizontalScrollView(context) {
        override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
            val tag = tag as String
            if (tag.equals("horizontal scroll view b", ignoreCase = true)) {
                horizontalScrollViewD.scrollTo(l, 0)
            } else {
                horizontalScrollViewB.scrollTo(l, 0)
            }
        }
    }

    // scroll view custom class
    inner class MyScrollView(context: Context?) : ScrollView(context) {
        override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
            val tag = tag as String
            if (tag.equals("scroll view c", ignoreCase = true)) {
                scrollViewD.scrollTo(0, t)
            } else {
                scrollViewC.scrollTo(0, t)
            }
        }
    }
}