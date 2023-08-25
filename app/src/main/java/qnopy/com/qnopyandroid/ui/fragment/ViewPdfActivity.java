package qnopy.com.qnopyandroid.ui.fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;

import java.io.File;
import java.util.Objects;

import qnopy.com.qnopyandroid.R;

public class ViewPdfActivity extends AppCompatActivity {
    public static final String PDF_URI = "pdfUriString";
    PDFView pdfView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        pdfView = findViewById(R.id.bartekscPdfView);
        pdfView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        pdfView.setDrawingCacheEnabled(true);
        pdfView.enableRenderDuringScale(false);


        Objects.requireNonNull(getSupportActionBar()).setTitle("PDF");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (getIntent()!=null){
            String uri = getIntent().getExtras().getString(PDF_URI);

                pdfView.fromUri(Uri.parse(uri)).onError(t -> {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    finish();
                }).enableSwipe(true).enableDoubletap(true).defaultPage(0).load();


        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}