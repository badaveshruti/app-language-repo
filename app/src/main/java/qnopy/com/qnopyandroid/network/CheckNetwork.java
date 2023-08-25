package qnopy.com.qnopyandroid.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import qnopy.com.qnopyandroid.R;

public class CheckNetwork {

    private static final String TAG = CheckNetwork.class.getSimpleName();

    public static boolean isInternetAvailable(Context context) {
        NetworkInfo info = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null) {
            Log.d(TAG, "no internet connection");
            return false;
        } else {
            if (info.isConnected()) {
                Log.d(TAG, " internet connection available...");
            } else {
                Log.d(TAG, "no internet connection");
            }
            return true;
        }
    }

    public static boolean isInternetAvailable(Context context, boolean showMessage) {
        NetworkInfo info = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null) {
            Log.d(TAG, "no internet connection");
            if (showMessage)
                Toast.makeText(context, context.getString(R.string.bad_internet_connectivity),
                        Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (info.isConnected()) {
                Log.d(TAG, " internet connection available...");
            } else {
                Log.d(TAG, "no internet connection");
                if (showMessage)
                    Toast.makeText(context, context.getString(R.string.bad_internet_connectivity),
                            Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    }
}
