package qnopy.com.qnopyandroid.ui.printCOC;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.print.PDFPrint;
import android.print.PrintAttributes;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.collect.Lists;
import com.tejpratapsingh.pdfcreator.utils.FileManager;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;
import com.tom_roush.pdfbox.io.MemoryUsageSetting;
import com.tom_roush.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.FieldData;
import qnopy.com.qnopyandroid.clientmodel.Location;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.SiteMobileAppDataSource;
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.ui.activity.ShowFilesActivity;
import qnopy.com.qnopyandroid.ui.printCOC.adapter.SelectLocationsAdapter;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.FormMaster;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

public class PrintCOCLabelsActivity extends ProgressDialogActivity {

    private static final int LIST_CHUNK_SIZE = 30;
    private SearchView searchView;
    private RecyclerView rvSelectLocations;
    private int eventId;
    private int siteId;
    private int rollAppId;
    private SelectLocationsAdapter adapter;
    private boolean isSelectAll = true;
    private MenuItem itemSelectAll;
    private String siteName;
    private String userID;
    private String userNameInitials;
    private String outDir;
    private PDFMergerUtility mergerUtility;
    private int totalPdfsToPrint = 0;
    private String cocDir;

    public static void startPrintCOCLabelsActivity(AppCompatActivity activity,
                                                   int siteId, int eventId,
                                                   int rollAppId) {
        Intent intent = new Intent(activity, PrintCOCLabelsActivity.class);
        intent.putExtra(GlobalStrings.KEY_EVENT_ID, eventId);
        intent.putExtra(GlobalStrings.KEY_SITE_ID, siteId);
        intent.putExtra(GlobalStrings.KEY_ROLL_APP_ID, rollAppId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_coclabels);

        setTitle("Select Locations");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        siteName = Util.getSharedPreferencesProperty(this, GlobalStrings.CURRENT_SITENAME);
        userID = Util.getSharedPreferencesProperty(this, GlobalStrings.SESSION_USERID);
        UserDataSource userDataSource = new UserDataSource(this);
        userNameInitials = userDataSource.getUserNameFromIDWithInitials(userID);

        if (getIntent() != null) {
            eventId = getIntent().getIntExtra(GlobalStrings.KEY_EVENT_ID, 0);
            siteId = getIntent().getIntExtra(GlobalStrings.KEY_SITE_ID, 0);
            rollAppId = getIntent().getIntExtra(GlobalStrings.KEY_ROLL_APP_ID, 0);
        }

        setUpUi();
    }

    private void setUpUi() {
        setUpSearchView();

        cocDir = Util.getFileFolderDirPathForCOCPDF(this);

        rvSelectLocations = findViewById(R.id.rvSelectLocations);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        rvSelectLocations.addItemDecoration(dividerItemDecoration);

        getLocationData();
    }

    private void getLocationData() {
        LocationDataSource locationDataSource = new LocationDataSource(this);
        HashMap<String, ArrayList<Location>> mapLocations
                = locationDataSource.getAllDataLocFormDefaultOrNon(siteId, rollAppId);

        if (mapLocations.containsKey(GlobalStrings.NON_FORM_DEFAULT)
                && mapLocations.get(GlobalStrings.NON_FORM_DEFAULT).size() > 0) {
            adapter = new SelectLocationsAdapter(mapLocations.get(GlobalStrings.NON_FORM_DEFAULT),
                    this);
            rvSelectLocations.setAdapter(adapter);
        }
    }

    private void setUpSearchView() {
        searchView = findViewById(R.id.searchViewLocations);
        searchView.setQueryHint("Search locations");

        searchView.setOnClickListener(v -> searchView.setIconified(false));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_print_labels, menu);
        itemSelectAll = menu.findItem(R.id.item_select_all);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        else if (item.getItemId() == R.id.item_print) {
            if (adapter != null) {
                if (!adapter.getSelectedItemsList().isEmpty()) {
                    if (CheckNetwork.isInternetAvailable(PrintCOCLabelsActivity.this))
                        new PrintLabels().execute();
                    else
                        CustomToast.showToast(PrintCOCLabelsActivity.this,
                                getString(R.string.bad_internet_connectivity), Toast.LENGTH_LONG);
                } else
                    CustomToast.showToast(PrintCOCLabelsActivity.this,
                            "Please select location to proceed", Toast.LENGTH_SHORT);
            }
        } else if (item.getItemId() == R.id.item_select_all) {
            if (adapter != null) {
                adapter.selectAll(isSelectAll);
            }
            isSelectAll = !isSelectAll;

            if (isSelectAll) {
                itemSelectAll.setIcon(ContextCompat.getDrawable(PrintCOCLabelsActivity.this,
                        R.drawable.ic_select_all));
                itemSelectAll.setTitle("Select All");
            } else {
                itemSelectAll.setIcon(ContextCompat.getDrawable(PrintCOCLabelsActivity.this,
                        R.drawable.ic_deselect_all));
                itemSelectAll.setTitle("Deselect All");
            }
        } else if (item.getItemId() == R.id.item_folder) {
            startShowFilesActivity(cocDir);
        }
        return super.onOptionsItemSelected(item);
    }

    private void startShowFilesActivity(String filePath) {
        Intent intent = new Intent(this, ShowFilesActivity.class);
        intent.putExtra(GlobalStrings.KEY_FILE_PATH, filePath);
        intent.setAction(GlobalStrings.PRINT_COC);
        startActivity(intent);
    }

    class PrintLabels extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showAlertProgress("Processing data to print..");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            printLabels();
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            cancelAlertProgress();
        }
    }

    private void printLabels() {
        if (adapter != null) {
            outDir = Util.getFileFolderDirPathForCOCPDF(this) + "COC_LABELS_"
                    + Util.getFormattedDateFromMilliS(System.currentTimeMillis(),
                    GlobalStrings.DATE_FORMAT_COC_LABELS) + ".pdf";
            mergerUtility = new PDFMergerUtility();
            mergerUtility.setDestinationFileName(outDir);

            ArrayList<Location> listLocations = adapter.getSelectedItemsList();
            SiteMobileAppDataSource mobileAppDataSource = new SiteMobileAppDataSource(this);
            String mobAppId = mobileAppDataSource.getAppTypeCOC(rollAppId);

            ArrayList<String> totalLabelsList = new ArrayList<>();
            ArrayList<String> htmlToPdfStringsList = new ArrayList<>();

            StringBuilder htmlRows = new StringBuilder();

            FieldDataSource fieldDataSource = new FieldDataSource(this);

            for (Location location : listLocations) {
                HashMap<String, FieldData> mapFieldData
                        = fieldDataSource.getRowDataForPrintLabel(eventId, "1",
                        location.getLocationID(), siteId + "", mobAppId);

                if (!mapFieldData.isEmpty()) {

                    String containers = mapFieldData.get("no. of containers").getStringValue();
                    String duplicateId = mapFieldData.get("duplicate sample id").getStringValue();

                    if (containers != null && !containers.isEmpty()) {
                        int container
                                = (int) FormMaster.round(Float.parseFloat(containers), 0);
                        for (int i = 0; i < container; i++) {
                            totalLabelsList.add(renderHtml(mapFieldData, false));
                        }

                        if (duplicateId != null && !duplicateId.isEmpty()) {
                            for (int i = 0; i < container; i++) {
                                totalLabelsList.add(renderHtml(mapFieldData, true));
                            }
                        }
                    } else {
                        totalLabelsList.add(renderHtml(mapFieldData, false));

                        if (duplicateId != null && !duplicateId.isEmpty()) {
                            totalLabelsList.add(renderHtml(mapFieldData, true));
                        }
                    }
                }
            }

            try {
                List<List> chunkedList = Arrays.asList(Util.partition(totalLabelsList, LIST_CHUNK_SIZE));

                for (List<String> list : chunkedList) {
                    for (String html : Lists.newArrayList(list)) {
                        htmlRows.append(html);
                    }
                    htmlToPdfStringsList.add(htmlRows.toString());
                    htmlRows.setLength(0);//clearing object
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            totalPdfsToPrint = htmlToPdfStringsList.size();
            for (String htmlToPdf : htmlToPdfStringsList) {
                String htmlItem = Util.getHtmlContent(this, "CocTemplate.html");
                htmlItem = htmlItem.replaceAll("#ITEMS#", htmlToPdf);

                String finalHtmlItem = htmlItem;
                runOnUiThread(() -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        saveToPDf(finalHtmlItem);
                    }
                });
            }
        }
    }

    private String renderHtml(HashMap<String, FieldData> mapFieldData, boolean hasDuplicateId) {
        String htmlRowData = Util.getHtmlContent(this, "RowData.html");

        String containers = mapFieldData.get("no. of containers").getStringValue();
        String matrix = mapFieldData.get("matrix").getStringValue();
        String duplicateSampleId = mapFieldData.get("duplicate sample id").getStringValue();
        String duplicateSampleTime = mapFieldData.get("dup sample time").getStringValue();

        try {
            htmlRowData = htmlRowData.replaceAll("#CLIENT_NAME#", "_______");
            htmlRowData = htmlRowData.replaceAll("#PROJECT_NAME#", siteName);

            if (hasDuplicateId) {
                htmlRowData = htmlRowData.replaceAll("#SAMPLE_ID#", duplicateSampleId);
                htmlRowData = htmlRowData.replaceAll("#TIME#", duplicateSampleTime);
            } else {
                htmlRowData = htmlRowData.replaceAll("#SAMPLE_ID#", mapFieldData.get("sample id").getStringValue());
                htmlRowData = htmlRowData.replaceAll("#TIME#", mapFieldData.get("sample time").getStringValue());
            }

            htmlRowData = htmlRowData.replaceAll("#DATE#", mapFieldData.get("sample date").getStringValue());

            htmlRowData = htmlRowData.replaceAll("#BOTTLE_DETAILS#", mapFieldData.get("analysis").getStringValue());

            if (!matrix.isEmpty() && !containers.isEmpty())
                htmlRowData = htmlRowData.replaceAll("#CONTAINER#", containers);
            else htmlRowData = htmlRowData.replaceAll("#CONTAINER#,", containers);

            htmlRowData = htmlRowData.replaceAll("#MATRIX#", mapFieldData.get("matrix").getStringValue());
            htmlRowData = htmlRowData.replaceAll("#USER_NAME#", userNameInitials);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Exception", "While html rendering row label" + e.getLocalizedMessage());
        }
        return htmlRowData;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void saveToPDf(String htmlString) {
        // Create Temp File to save Pdf To
        //        FileManager.getInstance().cleanTempFolder(getApplicationContext());
        // Create Temp File to save Pdf To
        final File savedPDFFile = FileManager.getInstance().createTempFile(getApplicationContext(),
                "pdf", false);

        // Generate Pdf From Html
        PDFUtil.generatePDFFromHTML(getApplicationContext(), savedPDFFile, htmlString, new PDFPrint.OnPDFPrintListener() {
            @Override
            public void onSuccess(File file) {
                try {
                    totalPdfsToPrint--;
                    mergerUtility.addSource(file);

                    if (totalPdfsToPrint == 0) {
                        mergerUtility.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());
                        FileManager.getInstance().cleanTempFolder(getApplicationContext(), true);
                        doPrintAction();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    private void doPrintAction() {

        File file = new File(outDir);

        if (file.exists()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
/*                Intent intentPdfViewer = new Intent(this, PdfViewerActivity.class);
                intentPdfViewer.putExtra(PdfViewerActivity.PDF_FILE_URI, outDir);
                startActivity(intentPdfViewer);*/

                cancelAlertProgress();

                PrintAttributes.Builder printAttributeBuilder = new PrintAttributes.Builder();
                printAttributeBuilder.setMediaSize(new PrintAttributes.MediaSize("LABEL",
                        "android", 8500, 14000));
                printAttributeBuilder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);

                PDFUtil.printPdf(this, file, printAttributeBuilder.build());
            }
        } else
            showToast("Pdf file doesn't exist", true);
    }
}
