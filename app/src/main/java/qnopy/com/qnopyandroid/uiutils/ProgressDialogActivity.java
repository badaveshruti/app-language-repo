package qnopy.com.qnopyandroid.uiutils;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ui.login.LoginActivity;
import qnopy.com.qnopyandroid.util.AlertManager;

public abstract class ProgressDialogActivity extends AppCompatActivity
        implements ProgressDialogHelper {

    protected static String TAG = LoginActivity.class.getSimpleName();

    private ProgressDialog progressDialog;

    private boolean destroyed = false;
    private AlertDialog progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (!Util.isTablet(this))
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        progressBar = AlertManager.showProgressBar(this, false);
        progressBar = AlertManager.showQnopyProgressBar(this, getString(R.string.loading));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
//        if (!Util.isTablet(this))
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.destroyed = true;
    }

    public void showAlertProgress() {
        if (progressBar != null) {
            progressBar.show();
        }
    }

    public void showAlertProgress(String msg) {
        progressBar = AlertManager.showQnopyProgressBar(this, msg);
        progressBar.show();
    }

    public void updateAlertProgressMsg(String message) {
        if (this.progressBar != null && this.progressBar.isShowing()) {
            if (AlertManager.tvLoaderMsg != null)
                AlertManager.tvLoaderMsg.setText(message);
        }
    }

    public void cancelAlertProgress() {
        if (progressBar != null && progressBar.isShowing()) {
            progressBar.cancel();
            AlertManager.tvLoaderMsg = null;
        }
    }

    // ***************************************
    // Public methods
    // ***************************************
    public void showLoadingProgressDialog() {
        this.showProgressDialog(getString(R.string.processing));
        //this.showProgressDialog("Processing...");
    }

    public void updateLoadingProgressDialogMsg(String message) {
        if (this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.setMessage(message);
        }
    }

    public void showProgressDialog(CharSequence message) {
        if (this.progressDialog == null) {
            this.progressDialog = new ProgressDialog(this);
            this.progressDialog.setIndeterminate(true);
            this.progressDialog.setCancelable(false);
        }

        this.progressDialog.setMessage(message);
        this.progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (this.progressDialog != null && !this.destroyed) {
            this.progressDialog.dismiss();
        }
    }

    public void showToast(String msg, boolean isLengthShort) {
        if (isLengthShort)
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
