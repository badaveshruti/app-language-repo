package qnopy.com.qnopyandroid.ui.fragment;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.EventReportModel;
import qnopy.com.qnopyandroid.clientmodel.ReportTable;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.ui.activity.LocationDetailActivity;
import qnopy.com.qnopyandroid.util.Util;
import qnopy.com.qnopyandroid.util.VectorDrawableUtils;

public class MobileReportFragment extends Fragment {

    private ProgressDialog procDialog;
    private WebView reportView;
    private String TAG = "MobileReportFragment";

    private EventReportModel EventReport;
    private ArrayList<ReportTable> d_list;
    private ArrayList<String> header_list;
    private List<HashMap<String, String>> value_row;
    private ArrayList<List<HashMap<String, String>>> table_rows;
    private String report_main_title = "Event_Form_Name";
    private String report_site_title = "Event_Site";
    private String eventID = "", siteID = "";
    private ImageView ivRefreshReport;
    private String mobAppId;
    private String locId;
    private static final String key_parameter_id = "parameterId";
    private static final String key_set_id = "setId";

    public MobileReportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mobile_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        procDialog = new ProgressDialog(getActivity());
        procDialog.setMessage(getString(R.string.please_wait));
        procDialog.show();

        eventID = ((LocationDetailActivity) getActivity()).getEventID() + "";
        siteID = ((LocationDetailActivity) getActivity()).getSiteID() + "";
        locId = ((LocationDetailActivity) getActivity()).getLocationID();

        report_main_title = ((LocationDetailActivity) getActivity()).title;
        report_site_title = ((LocationDetailActivity) getActivity()).getSiteName();

        reportView = view.findViewById(R.id.reportview);
        ivRefreshReport = view.findViewById(R.id.ivRefreshReport);

        setReportDetails();

        ivRefreshReport.setImageDrawable(VectorDrawableUtils.getDrawable(getActivity(),
                R.drawable.ic_refresh, R.color.black_faint));

        ivRefreshReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setReportDetails();
            }
        });
    }

    public class WebAppInterface {

        @JavascriptInterface
        public void postMessage(String value) {

            HashMap<String, String> mapIds = new HashMap<>();
            ArrayList<String> splitIds = new ArrayList<>(Util.splitStringToArray(",", value));
            for (String str : splitIds) {
                String[] keyValue = str.split(":");
                mapIds.put(keyValue[0], keyValue[1]);
            }

            try {
                String fpId = mapIds.get(key_parameter_id);
                String setId = mapIds.get(key_set_id);

                MobileReportFragment.this.moveToFieldAndSetInForm(fpId, setId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void moveToFieldAndSetInForm(String fpId, String setId) {
        //todo make a call to move the form to field and set
        if (getActivity() instanceof LocationDetailActivity) {

            //as it is still on main thread but
            //had to run it on main thread as progress dialog was giving IllegalStateException
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LocationDetailActivity activity = ((LocationDetailActivity) getActivity());

                    activity.setJumpToFieldId(fpId);
                    activity.setJumpToField(true);

                    if (activity.getCurSetID() != Integer.parseInt(setId))
                        activity.jumpToAnySet(Integer.parseInt(setId));
                    else
                        activity.jumpToAnyField();
                }
            });
        }
    }

    public void setReportDetails() {
        mobAppId = ((LocationDetailActivity) getActivity()).getCurrentAppID() + "";

        reportView.clearCache(true);
        reportView.getSettings().setBuiltInZoomControls(true);
        reportView.getSettings().setDisplayZoomControls(false);
        reportView.getSettings().setJavaScriptEnabled(true);

        reportView.addJavascriptInterface(new WebAppInterface(), "det");

        reportView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e("Webview: error", error.toString());
            }
        });

        ArrayList<HashMap<String, List<ReportTable>>> multiple_location_data_list = new ArrayList<HashMap<String, List<ReportTable>>>();

        FieldDataSource fds = new FieldDataSource(getActivity());
        multiple_location_data_list = fds.getReportDataForForm(siteID, eventID, locId, mobAppId);
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
                "    <script>\n" +
                "            function sendDetails() {\n" +
                " webkit.message;\n" +
                "            }\n" +
                "\n" +
                "   </script> \n" +
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

    private String getHeaderOfReport(String event_name, String site_name) {

        String summary = "<table>\n" +
                "\t\t\t  <tr>\n" +
//                "\t\t\t\t<td class=\"w30p text-center\">\n" +
//                "\t\t\t\t\tLogo Here\t\n" +
//                "\t\t\t\t</td>\n" +
                "\t\t\t\t<td class=\"w70p text-center\">\n" +
                "\t\t\t\t\t<span class=\"mainHeader\">" + event_name + "</span><br>\n" +
                "\t\t\t\t\t<span class=\"subHeader\">Site/Project:" + site_name + "</span>\n" +
                "\t\t\t\t</td>\n" +
                "\t\t\t  </tr>\n" +
                "\t\t\t</table>\n";

        return summary;
    }

    private String getReportBody(EventReportModel erm) {
        StringBuilder reportbody = new StringBuilder();
        String locationName = "";
        ArrayList<HashMap<String, List<ReportTable>>> reportData = erm.getLocation_form_data();

        for (HashMap<String, List<ReportTable>> locationData : reportData) {
            for (Map.Entry<String, List<ReportTable>> hashob : locationData.entrySet()) {
                locationName = hashob.getKey();
                //10/16/2017 Report Section for Location
                reportbody.append("<div class=\"primary-header-text\">")
                        .append("<h2>").append(locationName).append("</h2>").append("</div>");
                List<ReportTable> dataTables = hashob.getValue();
                for (ReportTable rpt : dataTables) {
                    boolean isAllowMultipleSet = rpt.isAllowMultiple();
                    String formName = rpt.getTitle();
                    ArrayList<String> headers = rpt.getTable_headers();
                    HashMap<String, ArrayList<String>> mapFieldsParams = rpt.getMapFieldsParams();

                    ArrayList<List<HashMap<String, String>>> table_rows = rpt.getTable_value_rows();
                    if (!isAllowMultipleSet && table_rows.size() <= 1) {
                        reportbody.append(getSingleSetTable(formName, table_rows, mapFieldsParams));
                    } else {
                        reportbody.append(getMultiSetTable(formName, headers, table_rows, mapFieldsParams));
                    }
                }
            }
        }

        return reportbody.toString();
    }

    private String getSingleSetTable(String formName,
                                     ArrayList<List<HashMap<String, String>>> data_rows,
                                     HashMap<String, ArrayList<String>> mapFieldsParams) {
        String result =
                "<div class=\"header-text\">" +
                        "<h2>" + formName + "</h2>" +
                        "</div>" +
                        "<table class=\"data-tables\">";
        StringBuilder row = new StringBuilder();

        ArrayList<HashMap<String, String>> d_rows
                = (ArrayList<HashMap<String, String>>) data_rows.get(0);
        List<List<HashMap<String, String>>> parts = getBatchObjectlist(d_rows, 2);

        for (List<HashMap<String, String>> label_list : parts) {
            row.append("<tr>\n");

            for (HashMap<String, String> lable_value : label_list) {
                String paramLabel;
                String stringValue;

                for (Map.Entry<String, String> hashob : lable_value.entrySet()) {
                    paramLabel = hashob.getKey();
                    stringValue = hashob.getValue();
                    String fpId = "0";
                    try {
                        if (mapFieldsParams.containsKey(paramLabel))
                            fpId = Objects.requireNonNull(mapFieldsParams.get(paramLabel)).get(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String tdId = createRowId(fpId, 1);

                    String colItem = Util.getHtmlContent(getActivity(),
                            "mobileReportTemplate/singleSetCol.html");
                    colItem = colItem.replaceAll("#TD_ID#", tdId);
                    colItem = colItem.replaceAll("#COL_LABEL#", paramLabel);

                    colItem = colItem.replaceAll("#TD_DATA_ID#", tdId);
                    colItem = colItem.replaceAll("#COL_DATA#", stringValue);

                    row.append(colItem);
                }
            }
            row.append("</tr>\n");
        }

        result = result + row + " </table>";
        return result;
    }

    private String createRowId(String fpId, int setId) {
        return key_parameter_id + ":" + fpId + "," + key_set_id + ":" + setId;
    }

    private String getMultiSetTable(String formName, ArrayList<String> headers,
                                    ArrayList<List<HashMap<String, String>>> data_rows,
                                    HashMap<String, ArrayList<String>> mapFieldsParams) {
        String headerHTML = "<div class=\"header-text\">\n" +
                "\t\t\t\t<h2>" + formName + "</h2>\n" +
                "\t\t\t</div><table class=\"para-tables\">\n" +
                "\t\t\t\t<tr>", rowHTML = "", value = "";

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

                        String fpId = "0";
                        try {
                            if (mapFieldsParams.containsKey(paramLabel))
                                fpId = Objects.requireNonNull(mapFieldsParams.get(paramLabel)).get(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        int setId = j + 1;
                        String tdId = createRowId(fpId, setId);

                        if (paramLabel.equals(column_label)) {
                            String colItem = Util.getHtmlContent(getActivity(),
                                    "mobileReportTemplate/multiSetCol.html");
                            colItem = colItem.replaceAll("#TD_ID#", tdId);
                            colItem = colItem.replaceAll("#COL_DATA#", paramValue);

                            value = colItem;
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