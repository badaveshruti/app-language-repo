package qnopy.com.qnopyandroid.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import qnopy.com.qnopyandroid.uicontrols.CustomToast;

public class AquaBlueServiceException extends Exception {

    private Context mContext;

    public AquaBlueServiceException(Context context) {
        mContext = context;
    }

    public AquaBlueServiceException(String message) {
        super(message);
        CustomToast.showToast((Activity) mContext, message, Toast.LENGTH_LONG);
    }
}
