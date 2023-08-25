package qnopy.com.qnopyandroid.responsemodel;

import java.util.List;

import qnopy.com.qnopyandroid.requestmodel.SCocDetails;
import qnopy.com.qnopyandroid.requestmodel.SCocMaster;

/**
 * Created by myog3 on 22-03-2018.
 */

public class SyncCocResponseModel extends DefaultResponse {

    private List<SCocDetails> sCocDetails;
    private List<SCocMaster> sCocMaster;

    public List<SCocDetails> getCocDetailsList() {
        return sCocDetails;
    }

    public void setCocDetailsList(List<SCocDetails> cocDetailsList) {
        this.sCocDetails = cocDetailsList;
    }

    public List<SCocMaster> getCocMasterList() {
        return sCocMaster;
    }

    public void setCocMasterList(List<SCocMaster> cocMasterList) {
        this.sCocMaster = cocMasterList;
    }
}
