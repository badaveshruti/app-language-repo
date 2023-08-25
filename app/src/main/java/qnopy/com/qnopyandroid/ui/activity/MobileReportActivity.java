package qnopy.com.qnopyandroid.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.ActionBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.EventReportModel;
import qnopy.com.qnopyandroid.clientmodel.ReportTable;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;

public class MobileReportActivity extends ProgressDialogActivity {

    WebView reportView;
    Context mContext;
    String TAG = "MobileReportAct";

    EventReportModel EventReport;
    ArrayList<ReportTable> d_list;
    ArrayList<String> header_list;
    List<HashMap<String, String>> value_row;
    ArrayList<List<HashMap<String, String>>> table_rows;
    String report_main_title = "Event_Form_Name";
    String report_site_title = "Event_Site";
    ActionBar actionBar;
    String eventID = "", siteID = "";
    ProgressDialog procDialog = null;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_report);
        mContext = this;
        procDialog = new ProgressDialog(mContext);
        procDialog.setMessage("Please wait!");
        procDialog.show();

        extras = getIntent().getExtras();

        if (extras != null) {
            eventID = extras.getString("EVENT_ID");
            siteID = extras.getString("SITE_ID");
            report_main_title = extras.getString("APP_NAME");
            report_site_title = extras.getString("SITE_NAME");
        } else {
            Log.i(TAG, "NO SiteID & EventID found");
            finish();
        }

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        reportView = findViewById(R.id.reportview);
        reportView.clearCache(true);
        reportView.getSettings().setBuiltInZoomControls(true);
        reportView.getSettings().setDisplayZoomControls(false);
        reportView.getSettings().setJavaScriptEnabled(true);
        reportView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e("Webview: error", error.toString());
            }
        });

        ArrayList<HashMap<String, List<ReportTable>>> multiple_location_data_list = new ArrayList<HashMap<String, List<ReportTable>>>();

        FieldDataSource fds = new FieldDataSource(mContext);
        multiple_location_data_list = fds.getReportData(siteID, eventID);
        EventReport = new EventReportModel(report_main_title, report_site_title, multiple_location_data_list);

        report_main_title = EventReport.getReportTitle();
        report_site_title = EventReport.getReportSite();

        String reportBaseHTML = "<!DOCTYPE html>\n" +
                " <html>\n" +
                "    <head>\n" +
                "        <meta content=\"text/html; charset=utf-8\" http-equiv=\"content-type\">\n" +
                "            <title>A simple, clean and responsive HTML invoice template</title>\n" +
                "            <style>\n" +
                "                .invoice-box{\n" +
                "                    max-width:1500px;\n" +
                "                    margin:auto;\n" +
                "                    padding:30px;\n" +
                "                    border:1px solid #eee;\n" +
                "                    box-shadow:0 0 10px rgba(0, 0, 0, .15); \n" +
                "                    font-size:16px;\n" +
                "                    line-height:24px;\n" +
                "                    font-family:'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif;\n" +
                "                    color:#555;\n" +
                "                }\n" +
                "\t\t\t\ttable {width: 100%}\n" +
                "\t\t\t\t.w30p {width: 30%;}\n" +
                "\t\t\t\t.w20p {width: 20%;}\n" +
                "\t\t\t\t.w25p {width: 25%;}\n" +
                "\t\t\t\t.w70p {width: 70%;}\n" +
                "\t\t\t\t.text-center{text-align: center}\n" +
                "\t\t\t\t.mainHeader{color: #00999f;font-size: 20px;font-weight: 500}\n" +
                "\t\t\t\t.subHeader{color: #000;font-size: 17px;}\n" +
                "\t\t\t\t.header-text {border-bottom: 2px solid #ddd;margin-bottom: 15px;}\n" +
                "\t\t\t\t .primary-header-text {border : 1px solid #000;font-size: 14px;margin-top: 10px;}\n" +
                "\t\t\t\t .primary-header-text h2{color: #00999f;font-size: 14px;font-weight: 500;margin-bottom: 5px;margin-left: 5px;}\n" +
                "\t\t\t\t.header-text h3{margin-left: 5px;}\n" +
                "\t\t\t\t.header-text h2{font-size: 14px;font-weight: bold;margin-bottom: 2px;margin-left: 5px;}\n" +
//                "\t\t\t\t.header-text h2{color: #00999f;font-size: 14px;font-weight: 500;margin-bottom: 2px;}\n" +
                "\t\t\t\ttable.data-tables tr td{padding: 3px 10px;font-size: 14px;}\n" +
                "\t\t\t\ttable.data-tables tr td:nth-child(2),\n" +
                "\t\t\t\ttable.data-tables tr td:nth-child(4){border-bottom: 1px solid #ddd}\n" +
                "\t\t\t\t.label-head{padding-left: 15px;}\n" +
                "\t\t\t\ttable.para-tables,\n" +
                "\t\t\t\ttable.para-tables th,\n" +
                "\t\t\t\ttable.para-tables td{\n" +
                "\t\t\t\t\tborder: 1px solid #ddd;\n" +
                "\t\t\t\t\tborder-collapse: collapse;\n" +
                "\t\t\t\t\tpadding: 3px;\n" +
                "\t\t\t\t\tfont-size: 14px;\n" +
                "\t\t\t\t}\n" +
                "table.para-tables {\n" +
                " display:inline-block;\n" +
                " overflow-x: scroll;\n" +
                "}" +
                "\t\t\t\ttable.para-tables th{background-color: #f5f5f5;}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div >\n";//class="invoice-box"

        String ReportHeader = getHeaderOfReport(report_main_title, report_site_title);

        String ReportBody = getReportBody(EventReport);
        String htmlReport = reportBaseHTML + ReportHeader + ReportBody;

        String htmlCode = htmlReport + "</div></body></html>";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            htmlCode = Base64.encodeToString(htmlCode.getBytes(), Base64.NO_PADDING);
            reportView.loadData(htmlCode, "text/html", "base64");
        } else {
            reportView.loadDataWithBaseURL("", htmlCode, "text/html", "UTF-8", "");
        }
        procDialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return false;
        }
    }

    private String getHeaderOfReport(String event_name, String site_name) {

        String summary = "<table>\n" +
                "\t\t\t  <tr>\n" +
//                "\t\t\t\t<td class=\"w30p text-center\">\n" +
//                "\t\t\t\t\tLogo Here\t\n" +
//                "\t\t\t\t</td>\n" +
                "\t\t\t\t<td class=\"w70p text-center\">\n" +
                "\t\t\t\t  \t<span class=\"mainHeader\">" + event_name + "</span><br>\n" +
                "\t\t\t\t\t<span class=\"subHeader\">Site/Project:" + site_name + "</span>\n" +
                "\t\t\t\t</td>\n" +
                "\t\t\t  </tr>\n" +
                "\t\t\t</table>\n";

        return summary;
    }

    private String getReportBody(EventReportModel erm) {
        StringBuilder reportBody = new StringBuilder();
        String locationName = "";
        ArrayList<HashMap<String, List<ReportTable>>> reportData = erm.getLocation_form_data();

        for (HashMap<String, List<ReportTable>> locationData : reportData) {
            for (Map.Entry<String, List<ReportTable>> hashob : locationData.entrySet()) {
                locationName = hashob.getKey();
                //10/16/2017 Report Section for Location
                reportBody.append("<div class=\"primary-header-text\">").append("<h2>").append(locationName).append("</h2>").append("</div>");
                List<ReportTable> dataTables = hashob.getValue();
                for (ReportTable rpt : dataTables) {
                    boolean isAllowMultipleSet = rpt.isAllowMultiple();
                    String formName = rpt.getTitle();
                    ArrayList<String> headers = rpt.getTable_headers();
                    ArrayList<List<HashMap<String, String>>> table_rows = rpt.getTable_value_rows();
                    if (!isAllowMultipleSet && table_rows.size() <= 1) {
                        reportBody.append(getSingleSetTable(formName, headers, table_rows));
                    } else {
                        reportBody.append(getMultiSetTable(formName, headers, table_rows));
                    }
                }
            }
        }

        return reportBody.toString();
    }


    private String getSingleSetTable(String formname, ArrayList<String> headers,
                                     ArrayList<List<HashMap<String, String>>> data_rows) {
        String result =
                "<div class=\"header-text\">" +
                        "<h2>" + formname + "</h2>" +
                        "</div>" +
                        "<table class=\"data-tables\">";
        StringBuilder row = new StringBuilder();

        ArrayList<HashMap<String, String>> d_rows = (ArrayList<HashMap<String, String>>) data_rows.get(0);
        List<List<HashMap<String, String>>> parts = getBatchObjectlist(d_rows, 2);

        for (List<HashMap<String, String>> label_list : parts) {
            row.append("<tr>\n");

            for (HashMap<String, String> lable_value : label_list) {
                String paramLabel;
                String stringValue;

                for (Map.Entry<String, String> hashob : lable_value.entrySet()) {
                    paramLabel = hashob.getKey();
                    stringValue = hashob.getValue();
                    row.append("<td class=\"w20p\">").append(paramLabel).append("</td>\n").append(" <td class=\"w30p\">").append(stringValue).append(" </td>");
                }
            }
            row.append("</tr>\n");
        }

        result = result + row + " </table>";

        return result;
    }

    private String getMultiSetTable(String formname, ArrayList<String> headers,
                                    ArrayList<List<HashMap<String, String>>> data_rows) {
        String headerHTML = "<div class=\"header-text\">\n" +
                "\t\t\t\t<h2>" + formname + "</h2>\n" +
                "\t\t\t</div><table class=\"para-tables\">\n" +
                "\t\t\t\t<tr>", rowHTML = "", value = "";

        if (headers == null) {
            showToast("Unable to format report", true);
            finish();
        }

        for (String header_name : headers) {
            headerHTML = headerHTML + "<th>" + header_name + "</th>";
        }

        headerHTML = headerHTML + " </tr>";

        for (int j = 0; j < data_rows.size(); j++) {//Extract each row
            rowHTML = "\t\t\t\t<tr>\n";

            List<HashMap<String, String>> d_row = data_rows.get(j);//List of column_value pair in each row
            for (int i = 0; i < headers.size(); i++) {//Get column name
                boolean matchFound = false;

                String column_label = headers.get(i);

                outerloop:
                for (int k = 0; k < d_row.size(); k++) {//Extract each column_value and attach
                    HashMap<String, String> row_item = d_row.get(k);
                    for (Map.Entry<String, String> hashob : row_item.entrySet()) {
                        String paramLabel = hashob.getKey();
                        String paramValue = hashob.getValue();
                        if (paramLabel.equals(column_label)) {
                            value = "<td>" + paramValue + "</td>";
                            matchFound = true;
                            rowHTML = rowHTML + value;
                            break outerloop;
                        }

                    }

                    if (k == (d_row.size() - 1) && !matchFound) {
                        value = "<td></td>";
                        matchFound = false;
                        rowHTML = rowHTML + value;
                    }
                }
            }

            rowHTML = rowHTML + "\t\t\t\t  </tr>";
            headerHTML = headerHTML + rowHTML;
        }

        return headerHTML + "\t</table>";
    }

    // chops a list into non-view sublists of length L
    private List<List<HashMap<String, String>>> getBatchObjectlist
    (ArrayList<HashMap<String, String>> list, final int L) {
        List<List<HashMap<String, String>>> parts = new ArrayList<>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }

}

