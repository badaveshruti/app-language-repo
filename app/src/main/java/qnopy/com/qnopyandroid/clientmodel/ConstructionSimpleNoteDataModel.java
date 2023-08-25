package qnopy.com.qnopyandroid.clientmodel;

import java.io.Serializable;
import java.util.ArrayList;

public class ConstructionSimpleNoteDataModel implements Serializable {

    ArrayList<ConstructionPostDataModel> mArrayListPostData;
    ArrayList<ConstructionMediaDataModel> mArrayListMediaData;
    ArrayList<ConstructioncTagDataModel> mArrayListCTagData;
    ArrayList<ConstructionRefTagDataModel> mArrayListRTagData;
    ArrayList<ConstructionModifiedPostIds> mModifiedPostIds;
    ArrayList<ConstructionModifiedMediaIds> mModifiedMediaIds;
    ArrayList<ConstructionModifiedTagIds> mModifiedTagIds;
    String mLastSyncDate;

    public ArrayList<ConstructionPostDataModel> getmArrayListPostData() {
        return mArrayListPostData;
    }

    public void setmArrayListPostData(ArrayList<ConstructionPostDataModel> mArrayListPostData) {
        this.mArrayListPostData = mArrayListPostData;
    }

    public ArrayList<ConstructionMediaDataModel> getmArrayListMediaData() {
        return mArrayListMediaData;
    }

    public void setmArrayListMediaData(ArrayList<ConstructionMediaDataModel> mArrayListMediaData) {
        this.mArrayListMediaData = mArrayListMediaData;
    }

    public ArrayList<ConstructioncTagDataModel> getmArrayListCTagData() {
        return mArrayListCTagData;
    }

    public void setmArrayListCTagData(ArrayList<ConstructioncTagDataModel> mArrayListCTagData) {
        this.mArrayListCTagData = mArrayListCTagData;
    }

    public ArrayList<ConstructionRefTagDataModel> getmArrayListRTagData() {
        return mArrayListRTagData;
    }

    public void setmArrayListRTagData(ArrayList<ConstructionRefTagDataModel> mArrayListRTagData) {
        this.mArrayListRTagData = mArrayListRTagData;
    }

    public ArrayList<ConstructionModifiedPostIds> getmModifiedPostIds() {
        return mModifiedPostIds;
    }

    public void setmModifiedPostIds(ArrayList<ConstructionModifiedPostIds> mModifiedPostIds) {
        this.mModifiedPostIds = mModifiedPostIds;
    }

    public ArrayList<ConstructionModifiedMediaIds> getmModifiedMediaIds() {
        return mModifiedMediaIds;
    }

    public void setmModifiedMediaIds(ArrayList<ConstructionModifiedMediaIds> mModifiedMediaIds) {
        this.mModifiedMediaIds = mModifiedMediaIds;
    }

    public ArrayList<ConstructionModifiedTagIds> getmModifiedTagIds() {
        return mModifiedTagIds;
    }

    public void setmModifiedTagIds(ArrayList<ConstructionModifiedTagIds> mModifiedTagIds) {
        this.mModifiedTagIds = mModifiedTagIds;
    }

    public String getmLastSyncDate() {
        return mLastSyncDate;
    }

    public void setmLastSyncDate(String mLastSyncDate) {
        this.mLastSyncDate = mLastSyncDate;
    }
}
