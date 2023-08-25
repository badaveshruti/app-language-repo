package qnopy.com.qnopyandroid.ui.activity;


import java.util.List;

import qnopy.com.qnopyandroid.requestmodel.AddSite;
import qnopy.com.qnopyandroid.requestmodel.SLocation;

/**
 * Created by shantanu on 11/7/16.
 */
public class AddSiteDataModel {

    private List<AddSite> site;

    private List<SLocation> location;


    public List<AddSite> getSite() {
        return site;
    }

    public void setSite(List<AddSite> site) {
        this.site = site;
    }

    public List<SLocation> getLocation() {
        return location;
    }

    public void setLocation(List<SLocation> location) {
        this.location = location;
    }
}
