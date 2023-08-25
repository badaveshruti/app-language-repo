package qnopy.com.qnopyandroid.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Objects;

import qnopy.com.qnopyandroid.BuildConfig;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.customView.CustomTextView;
import qnopy.com.qnopyandroid.customView.CustomTextViewSemiBold;
import qnopy.com.qnopyandroid.interfacemodel.AlertButtonOnClick;
import qnopy.com.qnopyandroid.ui.activity.DownloadYourOwnDataActivity;

public class AlertManager {
    public static CustomTextViewSemiBold tvLoaderMsg;

    public static AlertDialog showProgressBar(AppCompatActivity context,
                                              boolean showApplyingFilterLabel) {
        View view =
                LayoutInflater.from(context).inflate(R.layout.progress_dialog,
                        null, false);

        CustomTextView tvMessage = view.findViewById(R.id.tvApplyingFilters);

        if (showApplyingFilterLabel)
            tvMessage.setVisibility(View.VISIBLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return alertDialog;
    }

    public static void showDownloadDataWaitAlert(AppCompatActivity context, String title,
                                                 String message, long remainingTimeInMillis) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                //we would need to close download data activity or it'll stay there as it is
                //transparent you wont notice but screen click will be disabled
                if (context instanceof DownloadYourOwnDataActivity) {
                    context.finish();
                }
            }
        });
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        long FIVE_MIN = 300000;
        long ONE_SEC = 1000;

        new CountDownTimer(FIVE_MIN - remainingTimeInMillis, ONE_SEC) {
            @Override
            public void onTick(long duration) {
                long Mmin = (duration / 1000) / 60;
                long Ssec = (duration / 1000) % 60;

                String time = " 0" + Mmin + "m" + Ssec + "s ";

                alertDialog.setMessage(context.getString(R.string.please_wait_for) + time
                        + context.getString(R.string.and_try_again));
            }

            @Override
            public void onFinish() {
                alertDialog.setMessage(context.getString(R.string.please_download_data));
            }
        }.start();
    }

    public static AlertDialog showProgressBar(AppCompatActivity context, String message) {
        View view =
                LayoutInflater.from(context).inflate(R.layout.progress_dialog,
                        null, false);

        CustomTextView tvMessage = view.findViewById(R.id.tvApplyingFilters);
        tvMessage.setText(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return alertDialog;
    }

    public static AlertDialog showQnopyProgressBar(AppCompatActivity context, String message) {
        View view =
                LayoutInflater.from(context).inflate(R.layout.layout_qnopy_progress_bar,
                        null, false);

        ProgressBar ivQnopyLoader = view.findViewById(R.id.ivQnopyLoader);
        ImageView ivLoader = view.findViewById(R.id.ivLoader);

        Glide.with(context).asGif().load(
                        R.drawable.cow_loder
                ).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.cow_loder).into(ivLoader);

        tvLoaderMsg = view.findViewById(R.id.tvLoaderMessage);
        tvLoaderMsg.setText(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(context,
                R.style.WrapContentDialog);
        builder.setView(view);
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return alertDialog;
    }

    public static void showNormalAlert(String title, String message, String positiveTitle,
                                       String negativeTitle, boolean showNegative, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveTitle, (dialog, which) -> dialog.cancel());

        if (showNegative) {
            builder.setNegativeButton(negativeTitle, (dialog, which) -> dialog.cancel());
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showNormalAlertWithCallback(String title, String message, String positiveTitle,
                                                   String negativeTitle,
                                                   boolean showNegative,
                                                   Context context, AlertButtonOnClick listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton(positiveTitle, (dialog, which) -> {
            dialog.cancel();
            listener.positiveButtonClick();
        });

        if (showNegative) {
            builder.setNegativeButton(negativeTitle, (dialog, which) -> {
                dialog.cancel();
                listener.negativeButtonClick();
            });
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showPermissionSettingsAlert(String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Permission Access Required");
        builder.setMessage(message);
        builder.setPositiveButton("Open Settings", (dialog, which) -> {
            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + BuildConfig.APPLICATION_ID));
            context.startActivity(i);
            dialog.cancel();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
