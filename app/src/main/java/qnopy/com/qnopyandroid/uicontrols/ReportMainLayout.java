package qnopy.com.qnopyandroid.uicontrols;
//import <span id="o0sz2u_7" class="o0sz2u">java</span>.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.customView.CustomItalicTextView;
import qnopy.com.qnopyandroid.customView.CustomTextView;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.ui.activity.ReportView;

public class ReportMainLayout extends RelativeLayout {

    public String timeLable = "Date/Time";
    public final String TAG = "TableMainLayout.java";
    FieldDataSource fSource;

    List<String> headers = new ArrayList<String>();

    int fixedWidth = 200;

    TableLayout tableA;
    TableLayout tableB;
    TableLayout tableC;
    TableLayout tableD;

    HorizontalScrollView horizontalScrollViewB;
    HorizontalScrollView horizontalScrollViewD;

    ScrollView scrollViewC;
    ScrollView scrollViewD;

    Context context;
    ReportView reportViewContext = null;

    HashMap<Integer, Integer> headerCellsWidthMap = new HashMap<Integer, Integer>();

    public ReportMainLayout(Context context) {
        super(context);
    }

    public ReportMainLayout(Context context, ReportView tableViewContext) {

        super(context);

        this.context = context;
        this.reportViewContext = tableViewContext;

        fSource = new FieldDataSource(context);
        this.initHeaders();
        // initialize the main components (TableLayouts, HorizontalScrollView, ScrollView)
        this.initComponents();
        this.setComponentsId();
        this.setScrollViewAndHorizontalScrollViewTag();


        // no need to assemble component A, since it is just a table
        this.horizontalScrollViewB.addView(this.tableB);

        this.scrollViewC.addView(this.tableC);

        this.scrollViewD.addView(this.horizontalScrollViewD);
        this.horizontalScrollViewD.addView(this.tableD);

        // add the components to be part of the main layout
        this.addComponentToMainLayout();
        this.setBackgroundColor(Color.WHITE);


        // add some table rows
        this.addTableRowToTableA();
        this.addTableRowToTableB();

        this.resizeHeaderHeight();

        this.getTableRowHeaderCellWidth();

        this.generateTableC_AndTable_D();

        this.resizeBodyTableRowHeight();
    }

    private void initHeaders() {

        List<String> head = fSource.getDistinctParamLabels(reportViewContext.getSiteID(),
                reportViewContext.getParentAppID(), reportViewContext.getCurrentAppID(),
                reportViewContext.getLocId(), reportViewContext.getParamLabelList());
        if (reportViewContext.getSiteName() != null) {
            headers.add(reportViewContext.getSiteName());
        } else {
            headers.add("REPORT");
        }
        //added this header as mandatory which will be first column always
        headers.add("SetID");

        if (head != null) {
            headers.addAll(head);
        }
    }

    // initalized components
    private void initComponents() {

        tableA = new TableLayout(this.context);
        tableB = new TableLayout(this.context);
        tableC = new TableLayout(this.context);
        tableD = new TableLayout(this.context);

        this.horizontalScrollViewB = new MyHorizontalScrollView(this.context);
        this.horizontalScrollViewD = new MyHorizontalScrollView(this.context);

        horizontalScrollViewB.setHorizontalScrollBarEnabled(false);
        horizontalScrollViewD.setHorizontalScrollBarEnabled(false);

        this.scrollViewC = new MyScrollView(this.context);
        this.scrollViewD = new MyScrollView(this.context);

        scrollViewC.setVerticalScrollBarEnabled(false);
        scrollViewD.setVerticalScrollBarEnabled(false);

        this.tableA.setBackgroundColor(Color.BLACK);
        this.tableB.setBackgroundColor(Color.BLACK);
        this.tableC.setBackgroundColor(Color.BLACK);

        this.horizontalScrollViewB.setBackgroundColor(Color.LTGRAY);
    }

    // set essential component IDs
    private void setComponentsId() {
        this.tableA.setId(R.id.tableAId);
        this.horizontalScrollViewB.setId(R.id.horizontalScrollViewBId);
        this.scrollViewC.setId(R.id.scrollViewC);
        this.scrollViewD.setId(R.id.scrollViewD);
    }

    // set tags for some horizontal and vertical scroll view
    private void setScrollViewAndHorizontalScrollViewTag() {

        this.horizontalScrollViewB.setTag("horizontal scroll view b");
        this.horizontalScrollViewD.setTag("horizontal scroll view d");

        this.scrollViewC.setTag("scroll view c");
        this.scrollViewD.setTag("scroll view d");
    }

    // we add the components here in our TableMainLayout
    private void addComponentToMainLayout() {

        // RelativeLayout params were very useful here
        // the addRule method is the key to arrange the components properly
        LayoutParams componentB_Params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        componentB_Params.addRule(RelativeLayout.RIGHT_OF, this.tableA.getId());

        LayoutParams componentC_Params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        componentC_Params.addRule(RelativeLayout.BELOW, this.tableA.getId());

        LayoutParams componentD_Params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        componentD_Params.addRule(RelativeLayout.RIGHT_OF, this.scrollViewC.getId());
        componentD_Params.addRule(RelativeLayout.BELOW, this.horizontalScrollViewB.getId());

        // 'this' is a relative layout,
        // we extend this table layout as relative layout as seen during the creation of this class
        this.addView(this.tableA);
        this.addView(this.horizontalScrollViewB, componentB_Params);
        this.addView(this.scrollViewC, componentC_Params);
        this.addView(this.scrollViewD, componentD_Params);
    }

    private void addTableRowToTableA() {
        this.tableA.addView(this.componentATableRow());
    }

    private void addTableRowToTableB() {
        this.tableB.addView(this.componentBTableRow());
    }

    // generate table row of table A
    TableRow componentATableRow() {

        TableRow componentATableRow = new TableRow(this.context);
        TextView textView = this.headerTextView(this.headers.get(0));
        textView.setMaxLines(5);
        textView.setMovementMethod(new ScrollingMovementMethod());

//        TableRow.LayoutParams params = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
        TableRow.LayoutParams params = new TableRow.LayoutParams(fixedWidth, LayoutParams.MATCH_PARENT);

        params.setMargins(0, 0, 0, 2);

        textView.setLayoutParams(params);

        componentATableRow.addView(textView);

        return componentATableRow;
    }

    // generate table row of table B
    TableRow componentBTableRow() {

        TableRow componentBTableRow = new TableRow(this.context);
        int headerFieldCount = this.headers.size();

//        TableRow.LayoutParams params = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
        TableRow.LayoutParams params = new TableRow.LayoutParams(fixedWidth, LayoutParams.MATCH_PARENT);
        params.setMargins(2, 0, 0, 2);

        for (int x = 0; x < (headerFieldCount - 1); x++) {
            TextView textView = this.headerTextView(this.headers.get(x + 1));
            textView.setLayoutParams(params);
            textView.setMaxLines(5);
            textView.setMovementMethod(new ScrollingMovementMethod());
            componentBTableRow.addView(textView);
        }

        return componentBTableRow;
    }

    // generate table row of table C and table D
    private void generateTableC_AndTable_D() {

        // just seeing some header <span id="o0sz2u_3" class="o0sz2u">cell width</span>
        for (int x = 0; x < this.headerCellsWidthMap.size(); x++) {
            Log.v("TableMainLayout", this.headerCellsWidthMap.get(x) + "");
        }

        LocationDataSource locData = new LocationDataSource(context);
        List<FieldDataSource.LocSet> list = fSource.getLocationSetMap(reportViewContext.getEventID(),
                reportViewContext.getSiteID(), reportViewContext.getParentAppID(),
                reportViewContext.getCurrentAppID(), reportViewContext.getLocId());
        FieldDataSource.LocSet locSet;

        Log.i(TAG, "LocSet size=" + list.size());
        HashMap<String, String> map;
        for (int i = 0; i < list.size(); i++) {
            locSet = list.get(i);
            Log.i(TAG, "locationID=" + locSet.getLocID() + "setID=" + locSet.getSetID());

            map = fSource.getRowData(locSet.getMobID(), locSet.getLocID(),
                    locSet.getSetID(), reportViewContext.getParamLabelList(),
                    reportViewContext.getSiteID(), reportViewContext.getEventID());

            TableRow tableRowForTableC = this.tableRowForTableC(locData.getLocationName(locSet.getLocID()), locSet);
            TableRow taleRowForTableD = this.taleRowForTableD(map, locSet);

            tableRowForTableC.setBackgroundColor(Color.LTGRAY);
            taleRowForTableD.setBackgroundColor(Color.LTGRAY);

            this.tableC.addView(tableRowForTableC);
            this.tableD.addView(taleRowForTableD);
//        }
        }
    }

    // a TableRow for table C
    TableRow tableRowForTableC(String val, FieldDataSource.LocSet locSet) {


//        TableRow.LayoutParams params = new TableRow.LayoutParams( this.headerCellsWidthMap.get(0),LayoutParams.MATCH_PARENT);
        TableRow.LayoutParams params = new TableRow.LayoutParams(fixedWidth, LayoutParams.MATCH_PARENT);
        params.setMargins(0, 0, 2, 2);


        TableRow tableRowForTableC = new TableRow(this.context);

        TextView textView = this.bodyTextView(val);

        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToSet(locSet);
            }
        });

        tableRowForTableC.addView(textView, params);

        return tableRowForTableC;
    }

    private void jumpToSet(FieldDataSource.LocSet locSet) {
        showPopupToJumpToSet(locSet);
    }

    private void showPopupToJumpToSet(FieldDataSource.LocSet locSet) {
        View view =
                LayoutInflater.from(context).inflate(R.layout.alert_simple_message,
                        null, false);

        CustomItalicTextView btnShowSet = view.findViewById(R.id.btnShowSet);
        CustomItalicTextView btnNo = view.findViewById(R.id.btnNo);
        CustomTextView tvSetLabel = view.findViewById(R.id.tvSetLabel);

        String label = "Do you want to navigate to set " + locSet.getSetID() + " ?";
        tvSetLabel.setText(label);

        androidx.appcompat.app.AlertDialog.Builder builder
                = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setView(view);

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btnShowSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                Intent intent = new Intent();
                intent.putExtra(GlobalStrings.FORM_DETAILS, locSet.getSetID());
                reportViewContext.setResult(Activity.RESULT_OK, intent);
                reportViewContext.finish();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
    }

    TableRow taleRowForTableD(HashMap<String, String> map, FieldDataSource.LocSet locSet) {

        TableRow taleRowForTableD = new TableRow(this.context);
        String key = null;
        String value = null;

        int loopCount = ((TableRow) this.tableB.getChildAt(0)).getChildCount();

        for (int x = 0; x < loopCount; x++) {
//            TableRow.LayoutParams params = new TableRow.LayoutParams( headerCellsWidthMap.get(x+1),LayoutParams.MATCH_PARENT);
            TableRow.LayoutParams params = new TableRow.LayoutParams(fixedWidth, LayoutParams.MATCH_PARENT);
            params.setMargins(2, 0, 0, 2);
            key = headers.get(x + 1);
            value = map.get(key);
            if (key.equalsIgnoreCase(timeLable)) {
                value = map.get("Time");
                if ((value == null) || (value.length() == 0)) {
                    value = map.get("Date");
                }
                if ((value != null) && (value.length() != 0)) {
                    /*
	            	Date date=new Date(Long.parseLong(value));
	                SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yy HH:mm");
	                value = df2.format(date);
	                */
                    Calendar calendar = Calendar.getInstance();
//            		calendar.setTimeZone(TimeZone.getDefault());
                    calendar.setTimeInMillis(Long.parseLong(value));

                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DATE);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
//	                value = month+"/"+day+"/"+year+" "+hour+":"+minute;
                    value = String.format("%02d", month) + "/" +
                            String.format("%02d", day) + "/" + year + " " +
                            String.format("%02d", hour) + ":" + String.format("%02d", minute);
                }
            }

            TextView textViewB = this.bodyTextView(value);

            textViewB.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpToSet(locSet);
                }
            });

//            TextView textViewB = this.bodyTextView(info[x]);
            taleRowForTableD.addView(textViewB, params);
        }
        return taleRowForTableD;
    }

    // table cell standard TextView
    TextView bodyTextView(String label) {

        TextView bodyTextView = new TextView(this.context);
        bodyTextView.setBackgroundColor(Color.WHITE);
        bodyTextView.setText(label);
        bodyTextView.setGravity(Gravity.CENTER);
        bodyTextView.setPadding(5, 5, 5, 5);
        bodyTextView.setMaxLines(5);
        bodyTextView.setMovementMethod(new ScrollingMovementMethod());

        return bodyTextView;
    }

    // header standard TextView
    TextView headerTextView(String label) {

        TextView headerTextView = new TextView(this.context);
        headerTextView.setBackgroundColor(Color.WHITE);
        headerTextView.setText(label);
        headerTextView.setGravity(Gravity.CENTER);
        headerTextView.setPadding(5, 5, 5, 5);

        return headerTextView;
    }

    // resizing TableRow height starts here
    void resizeHeaderHeight() {

        TableRow productNameHeaderTableRow = (TableRow) this.tableA.getChildAt(0);
        TableRow productInfoTableRow = (TableRow) this.tableB.getChildAt(0);

        int rowAHeight = this.viewHeight(productNameHeaderTableRow);
        int rowBHeight = this.viewHeight(productInfoTableRow);

        TableRow tableRow = rowAHeight < rowBHeight ? productNameHeaderTableRow : productInfoTableRow;
        int finalHeight = rowAHeight > rowBHeight ? rowAHeight : rowBHeight;

        this.matchLayoutHeight(tableRow, finalHeight);
    }

    void getTableRowHeaderCellWidth() {

        int tableAChildCount = ((TableRow) this.tableA.getChildAt(0)).getChildCount();
        int tableBChildCount = ((TableRow) this.tableB.getChildAt(0)).getChildCount();

        for (int x = 0; x < (tableAChildCount + tableBChildCount); x++) {
            if (x == 0) {
                this.headerCellsWidthMap.put(x, this.viewWidth(((TableRow) this.tableA.getChildAt(0)).getChildAt(x)));
            } else {
                this.headerCellsWidthMap.put(x, this.viewWidth(((TableRow) this.tableB.getChildAt(0)).getChildAt(x - 1)));
            }
        }
    }

    // resize body table row height
    void resizeBodyTableRowHeight() {

        int tableC_ChildCount = this.tableC.getChildCount();

        for (int x = 0; x < tableC_ChildCount; x++) {

            TableRow productNameHeaderTableRow = (TableRow) this.tableC.getChildAt(x);
            TableRow productInfoTableRow = (TableRow) this.tableD.getChildAt(x);

            int rowAHeight = this.viewHeight(productNameHeaderTableRow);
            int rowBHeight = this.viewHeight(productInfoTableRow);

            TableRow tableRow = rowAHeight < rowBHeight ? productNameHeaderTableRow : productInfoTableRow;
            int finalHeight = Math.max(rowAHeight, rowBHeight);

            this.matchLayoutHeight(tableRow, finalHeight);
        }
    }

    // match all height in a table row
    // to make a standard TableRow height
    private void matchLayoutHeight(TableRow tableRow, int height) {

        int tableRowChildCount = tableRow.getChildCount();

        // if a TableRow has only 1 child
        if (tableRow.getChildCount() == 1) {

            View view = tableRow.getChildAt(0);
            TableRow.LayoutParams params = (TableRow.LayoutParams) view.getLayoutParams();
            params.height = height - (params.bottomMargin + params.topMargin);

            return;
        }

        // if a TableRow has more than 1 child
        for (int x = 0; x < tableRowChildCount; x++) {

            View view = tableRow.getChildAt(x);

            TableRow.LayoutParams params = (TableRow.LayoutParams) view.getLayoutParams();

            if (!isTheHighestLayout(tableRow, x)) {
                params.height = height - (params.bottomMargin + params.topMargin);
                return;
            }
        }

    }

    // check if the view has the highest height in a TableRow
    private boolean isTheHighestLayout(TableRow tableRow, int layoutPosition) {

        int tableRowChildCount = tableRow.getChildCount();
        int heighestViewPosition = -1;
        int viewHeight = 0;

        for (int x = 0; x < tableRowChildCount; x++) {
            View view = tableRow.getChildAt(x);
            int height = this.viewHeight(view);

            if (viewHeight < height) {
                heighestViewPosition = x;
                viewHeight = height;
            }
        }

        return heighestViewPosition == layoutPosition;
    }

    // read a view's height
    private int viewHeight(View view) {
        view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }

    // read a view's width
    private int viewWidth(View view) {
        view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        return view.getMeasuredWidth();

    }

    // horizontal scroll view custom class
    class MyHorizontalScrollView extends HorizontalScrollView {

        public MyHorizontalScrollView(Context context) {
            super(context);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            String tag = (String) this.getTag();

            if (tag.equalsIgnoreCase("horizontal scroll view b")) {
                horizontalScrollViewD.scrollTo(l, 0);
            } else {
                horizontalScrollViewB.scrollTo(l, 0);
            }
        }
    }

    // scroll view custom class
    class MyScrollView extends ScrollView {

        public MyScrollView(Context context) {
            super(context);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {

            String tag = (String) this.getTag();

            if (tag.equalsIgnoreCase("scroll view c")) {
                scrollViewD.scrollTo(0, t);
            } else {
                scrollViewC.scrollTo(0, t);
            }
        }
    }
}

