package qnopy.com.qnopyandroid.clientmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by myog3 on 10/16/2017.
 */

public class EventReportModel {
    String ReportTitle;//Main Title-Event Name
    String ReportSite;//Site Name
    ArrayList<HashMap<String, List<ReportTable>>> location_form_data;//Data collected under each location

    public EventReportModel(String reportTitle, String reportSite,
                            ArrayList<HashMap<String, List<ReportTable>>> location_form_data) {
        ReportTitle = reportTitle;
        ReportSite = reportSite;
        this.location_form_data = location_form_data;
    }

    public String getReportTitle() {
        return ReportTitle;
    }

    public void setReportTitle(String reportTitle) {
        ReportTitle = reportTitle;
    }

    public String getReportSite() {
        return ReportSite;
    }

    public void setReportSite(String reportSite) {
        ReportSite = reportSite;
    }

    public ArrayList<HashMap<String, List<ReportTable>>> getLocation_form_data() {
        return location_form_data;
    }

    public void setLocation_form_data(ArrayList<HashMap<String, List<ReportTable>>> location_form_data) {
        this.location_form_data = location_form_data;
    }
}
