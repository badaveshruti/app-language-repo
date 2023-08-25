package qnopy.com.qnopyandroid.ui.printCOC;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.utils.DateUtils;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.PrintCOCByDatesResponse;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.ui.activity.ShowFilesActivity;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

public class PrintCOCActivity extends ProgressDialogActivity {

    private ArrayList<String> selectedDates = new ArrayList<>();
    private AquaBlueServiceImpl mAquaBlueService;
    private CalendarView calendarView;
    private String eventId;
    private String siteId;
    private String cocDir;

    public static void startPrintCOCActivity(AppCompatActivity activity, String eventId, String siteId) {
        Intent intent = new Intent(activity, PrintCOCActivity.class);
        intent.putExtra(GlobalStrings.KEY_EVENT_ID, eventId);
        intent.putExtra(GlobalStrings.KEY_SITE_ID, siteId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_coc);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Print COC");

        if (getIntent() != null) {
            eventId = getIntent().getStringExtra(GlobalStrings.KEY_EVENT_ID);
            siteId = getIntent().getStringExtra(GlobalStrings.KEY_SITE_ID);
        }
        cocDir = Util.getFileFolderDirPathForCOCPDF(PrintCOCActivity.this);

        mAquaBlueService = new AquaBlueServiceImpl(PrintCOCActivity.this);
        setUpUI();
    }

    private void setUpUI() {
        setUpCalendar();

        Button btnPrintCOC = findViewById(R.id.btnPrintCOC);
        btnPrintCOC.setOnClickListener(v -> {
            selectedDates.clear();

            for (Calendar calendar : calendarView.getSelectedDates()) {
                String formattedDate = "";
                try {
                    formattedDate = Util.getFormattedDateFromMilliS(calendar.getTimeInMillis(),
                            GlobalStrings.DATE_FORMAT_MM_DD_YYYY);
                } catch (Exception e) {
                    e.printStackTrace();
                    formattedDate = "";
                }

                if (!formattedDate.isEmpty())
                    selectedDates.add(formattedDate);
            }

            new PrintCOCApi().execute();
        });
    }

    private void setUpCalendar() {
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        List<EventDay> events = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
//        calendarView.setSelectedDates(getSelectedDays());

        cal.add(Calendar.DAY_OF_MONTH, 7);
        events.add(new EventDay(cal, R.drawable.ic_red_dot));

        calendarView.setEvents(events);
    }

    class PrintCOCApi extends AsyncTask<Void, Void, PrintCOCByDatesResponse> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showAlertProgress();
        }

        @Override
        protected PrintCOCByDatesResponse doInBackground(Void... voids) {
            return hitPrintCOCApi();
        }

        @Override
        protected void onPostExecute(PrintCOCByDatesResponse response) {
            super.onPostExecute(response);
            cancelAlertProgress();
            if (response.getData() != null) {
                List<String> list = Util.splitStringToArray("|", response.getData());
                if (list.size() == 2) {
                    File file = new File(cocDir, list.get(1));
                    if (!file.exists())
                        new DownloadCOCApi(list.get(0), list.get(1)).execute();
                    else
                        startShowFilesActivity(cocDir);
                }
            } else showToast(response.getMessage(), true);
        }
    }

    private PrintCOCByDatesResponse hitPrintCOCApi() {
        return mAquaBlueService.printCOCByDates(getString(R.string.prod_base_uri),
                getString(R.string.url_export_coc), eventId, Util.splitArrayListToString(selectedDates));
    }

    class DownloadCOCApi extends AsyncTask<Void, Void, String> {

        String key;
        String fileName;

        public DownloadCOCApi(String key, String fileName) {
            this.key = key;
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showAlertProgress();
        }

        @Override
        protected String doInBackground(Void... voids) {
            return downloadCOC(key, fileName);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (!response.contains("false")) {
                doPrintAction(response);
            }
            cancelAlertProgress();
        }
    }

    private String downloadCOC(String key, String fileName) {
        return mAquaBlueService.cocFileDownload(getString(R.string.prod_base_uri),
                getString(R.string.url_download_gen_coc), siteId, fileName, key, eventId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_print_coc, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        else if (item.getItemId() == R.id.menu_folder)
            startShowFilesActivity(cocDir);
        return super.onOptionsItemSelected(item);
    }

    private List<Calendar> getSelectedDays() {
        List<Calendar> calendars = new ArrayList<>();

        for (int i = 0; i < 1; i++) {
            Calendar calendar = DateUtils.getCalendar();
            calendar.add(Calendar.DAY_OF_MONTH, 0);
            calendars.add(calendar);
        }

        return calendars;
    }

    private void startShowFilesActivity(String filePath) {
        Intent intent = new Intent(this, ShowFilesActivity.class);
        intent.putExtra(GlobalStrings.KEY_FILE_PATH, filePath);
        intent.setAction(GlobalStrings.PRINT_COC);
        startActivity(intent);
    }

    private void doPrintAction(String path) {

        File file = new File(path);

        if (file.exists()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
/*                Intent intentPdfViewer = new Intent(this, PdfViewerActivity.class);
                intentPdfViewer.putExtra(PdfViewerActivity.PDF_FILE_URI, path);
                startActivity(intentPdfViewer);*/

                PrintAttributes.Builder printAttributeBuilder = new PrintAttributes.Builder();
                printAttributeBuilder.setMediaSize(new PrintAttributes.MediaSize("LABEL",
                        "android", 612, 792));
                printAttributeBuilder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);

                PDFUtil.printPdf(this, file, printAttributeBuilder.build());
            }
        } else
            showToast("Pdf file doesn't exist", true);
    }
}