package qnopy.com.qnopyandroid.clientmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yogendra on 04-Oct-17.
 */

public class ReportTable {

    String title; //Child Form Name
    boolean allowMultiple;
    ArrayList<String> table_headers;
    ArrayList<List<HashMap<String, String>>> table_value_rows;
    HashMap<String, ArrayList<String>> mapFieldsParams;

    public ReportTable(boolean allowMultiple, String title, ArrayList<String> table_headers,
                       ArrayList<List<HashMap<String, String>>> table_value_rows) {
        this.allowMultiple = allowMultiple;
        this.title = title;
        this.table_headers = table_headers;
        this.table_value_rows = table_value_rows;
    }

    public HashMap<String, ArrayList<String>> getMapFieldsParams() {
        return mapFieldsParams;
    }

    public void setMapFieldsParams(HashMap<String, ArrayList<String>> mapFieldsParams) {
        this.mapFieldsParams = mapFieldsParams;
    }

    public boolean isAllowMultiple() {
        return allowMultiple;
    }

    public void setAllowMultiple(boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getTable_headers() {
        return table_headers;
    }

    public void setTable_headers(ArrayList<String> table_headers) {
        this.table_headers = table_headers;
    }

    public ArrayList<List<HashMap<String, String>>> getTable_value_rows() {
        return table_value_rows;
    }

    public void setTable_value_rows(ArrayList<List<HashMap<String, String>>> table_value_rows) {
        this.table_value_rows = table_value_rows;
    }
}
