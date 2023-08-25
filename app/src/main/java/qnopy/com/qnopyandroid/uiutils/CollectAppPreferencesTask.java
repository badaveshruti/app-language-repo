package qnopy.com.qnopyandroid.uiutils;

import android.app.ProgressDialog;
import android.content.Context;

import qnopy.com.qnopyandroid.interfacemodel.OnTaskCompleted;
import qnopy.com.qnopyandroid.photogallery.AsyncTask;
import qnopy.com.qnopyandroid.responsemodel.PreferenceMappingModel;
import qnopy.com.qnopyandroid.responsemodel.PreferenceMappingResponse;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;

/**
 * Created by Yogendra on 19-Jun-17.
 */

public class CollectAppPreferencesTask extends AsyncTask {

    private static final String TAG = "CollectAppPreferencesTask";
    AquaBlueServiceImpl mAquaBlueService;
    String username = null;
    String password = null;
    Context objContext = null;
    ProgressDialog procDialog = null;
    PreferenceMappingModel model;
    PreferenceMappingResponse pref_response;

    private OnTaskCompleted listener;


    public CollectAppPreferencesTask(OnTaskCompleted listner, String userguid) {
        objContext = (Context) listner;
        this.listener = listner;
        mAquaBlueService = new AquaBlueServiceImpl(objContext);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        return null;
    }
}
