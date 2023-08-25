package qnopy.com.qnopyandroid.uiutils;

import static qnopy.com.qnopyandroid.ui.locations.LocationActivity.LOCATION_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import qnopy.com.qnopyandroid.BuildConfig;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.customView.CustomTextView;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by Yogendra on 05-Mar-16.
 */
public class CustomAlert {

    public static void showAlert(final Context objContext, String msg, String title) {
        AlertDialog alert = null;
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(objContext);
        if (msg.equals("Your device was disabled. Contact QNOPY Admin to activate device.")
                || msg.equals("Your account was suspended. Contact QNOPY Admin if this was an error.")) {
            alertBuilder.setMessage(msg);
            alertBuilder.setTitle(title);
            alertBuilder.setNeutralButton(objContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Util.setLogout((Activity) objContext);
                }
            });
            alert = alertBuilder.create();
            alert.setCancelable(false);
            alert.setCanceledOnTouchOutside(false);
            alert.show();
        } else {
            alertBuilder.setMessage(msg);
            alertBuilder.setTitle(title);
            alertBuilder.setNeutralButton(objContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert = alertBuilder.create();
            alert.show();
        }
    }

    public static void showUnAuthAlert(final Context objContext, String msg, String title) {
        AlertDialog alert = null;
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(objContext);
        alertBuilder.setMessage(msg);
        alertBuilder.setTitle(title);
        alertBuilder.setNeutralButton(objContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Util.setLogout((Activity) objContext);
            }
        });
        alert = alertBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public static void showSettingsAlert(Context context, String msg, String title,
                                         String positiveMsg, String negativeMsg) {
        androidx.appcompat.app.AlertDialog.Builder builder
                = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveMsg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton(negativeMsg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showLocationPermissionAlert(Context context, LocationServiceAlertListener listener) {
        androidx.appcompat.app.AlertDialog.Builder builder
                = new androidx.appcompat.app.AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.location_permission_alert, null, false);
        builder.setView(view);
        builder.setCancelable(false);

        CustomTextView btnTurnOn = view.findViewById(R.id.btnTurnOn);
        CustomTextView btnNoThanks = view.findViewById(R.id.btnNoThanks);

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btnTurnOn.setOnClickListener(v -> {
//            listener.onLocationAllow();
            alertDialog.cancel();
            requestLocationPermission(context);
        });

        btnNoThanks.setOnClickListener(v -> {
            listener.onLocationDeny();
            alertDialog.cancel();
        });
    }

    private static void requestLocationPermission(Context context) {
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        String permissionCoarse = Manifest.permission.ACCESS_COARSE_LOCATION;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, permissionCoarse)
                    != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) context,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions((AppCompatActivity) context,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    CustomAlert.showSettingsAlert(context,
                            "No access to Location. Please allow location " +
                                    "permission access manually from Settings->App Permissions.",
                            "Location Permission Denied", "Open Settings", "Cancel");
                }
            }
        }
    }

    public static boolean checkPermission(Context context, String permission) {
        int result = ContextCompat.checkSelfPermission(context, permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public interface LocationServiceAlertListener {
//        void onLocationAllow();

        void onLocationDeny();
    }
}
