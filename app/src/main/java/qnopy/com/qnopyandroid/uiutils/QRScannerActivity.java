package qnopy.com.qnopyandroid.uiutils;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import qnopy.com.qnopyandroid.GlobalStrings;

public class QRScannerActivity extends ProgressDialogActivity
        implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private int fpId;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

        if (getIntent() != null) {
            fpId = getIntent().getIntExtra(GlobalStrings.KEY_FIELD_PARAM_ID, 0);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Scan QR/Barcode");
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        Intent intent = new Intent();
        intent.putExtra(GlobalStrings.QR_SCANNED_TEXT, rawResult.getText());
        intent.putExtra(GlobalStrings.KEY_FIELD_PARAM_ID, fpId);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
