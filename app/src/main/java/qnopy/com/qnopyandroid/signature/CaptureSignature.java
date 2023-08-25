package qnopy.com.qnopyandroid.signature;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.customView.CustomButton;
import qnopy.com.qnopyandroid.db.AttachmentData;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.network.CheckNetwork;
import qnopy.com.qnopyandroid.requestmodel.CustomerSign;
import qnopy.com.qnopyandroid.requestmodel.PathsAndNames;
import qnopy.com.qnopyandroid.uicontrols.CustomToast;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

public class CaptureSignature extends ProgressDialogActivity implements SignatureUpdateListener {

    private static final String TAG = "CaptureSignature";
    LinearLayout mContent;
    Signature mSignature;
    String strname = null;

    Button mClear;//, mGetSign;//, addButton;
    public int count = 1;
    public ArrayList<CustomerSign> customerSigns = new ArrayList<>();
    ArrayList<PathsAndNames> pathsNnames;
    View mView;
    File mypath;
    public static final String DRAWING_STORAGE_DIR = "QnopyDrawings";
    private EditText yourName;
    private TextView emptyView;
    Context mContext;
    //    FloatingActionsMenu mActionMenu;
    //    FloatingActionButton mAddFab, mSubmitFab;
    androidx.appcompat.app.ActionBar actionBar;

    private Snackbar snackbar;
    private ConstraintLayout coordinatorLayout;
    //    ArrayList<CustomerSign> alist = new ArrayList<>();
    int fid, evntid, siteid, mappid, setid, userid = 0;
    String locid = "0";
    private RecyclerView rvSignatures;
    private SignatureAdapter signatureAdapter;
    private File thumbPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.signature);
        coordinatorLayout = findViewById(R.id.linearLayout1);

        setBtnClickListeners();
        yourName = findViewById(R.id.yourName);
        yourName.setImeOptions(EditorInfo.IME_ACTION_DONE);
        yourName.setSingleLine(true);

        emptyView = findViewById(R.id.emptytextView);

        mContext = this;

        initNewFileName();

        mContent = findViewById(R.id.linearLayout);
        mSignature = new Signature(mContext, null);
        mSignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mSignature, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        mClear = findViewById(R.id.clear);
//        mGetSign = (Button) findViewById(R.id.getsign);
        // mGetSign.setEnabled(false);
//        mCancel = (Button) findViewById(R.id.cancel);
        mView = new View(mContext);
        mView = mContent;

        rvSignatures = findViewById(R.id.rvSignatures);
        rvSignatures.setLayoutManager(new GridLayoutManager(this, 3));

        Bundle extras1 = getIntent().getExtras();
        if (extras1 != null) {
            if (extras1.containsKey("CLOSE")) {
                evntid = extras1.getInt("EVENT_ID");
                siteid = extras1.getInt("SITE_ID");
                mappid = extras1.getInt("APP_ID");
                try {
                    userid = extras1.getInt("UserID");
                } catch (Exception e) {
                    e.printStackTrace();
                    userid = Integer.parseInt(extras1.getString("UserID"));
                }
            } else {
                evntid = Integer.parseInt(extras1.getString("EVENT_ID"));
                siteid = Integer.parseInt(extras1.getString("SITE_ID"));
                userid = Integer.parseInt(extras1.getString("UserID"));

                fid = extras1.getInt("paramID");
                mappid = extras1.getInt("APP_ID");
                locid = extras1.getString("LOC_ID");
                setid = extras1.getInt("setID");
//              position = extras1.getString("POSITION");
            }
        }

        AttachmentDataSource attachmentDataSource = new AttachmentDataSource(mContext);
        customerSigns = attachmentDataSource.getAttachmentListForSignature(evntid, siteid, fid,
                locid, userid, mappid, setid);
        setAdapter();

        setSignatureNameString();

        initActionBar();
        mClear.setOnClickListener(v -> {
            Log.v("log_tag", "Panel Cleared");
            mSignature.clear();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    public void setSignatureNameString() {
        if (customerSigns != null && customerSigns.size() > 0) {
            ArrayList<String> signNames = new ArrayList<>();

            for (CustomerSign sign : customerSigns) {
                if (sign.getName() != null && !sign.getName().isEmpty())
                    signNames.add(sign.getName());
            }

            strname = TextUtils.join("|", signNames);
            if (strname.isEmpty())
                strname = null;
        } else {
            strname = null;
        }

        FieldDataSource fieldDataSource = new FieldDataSource(mContext);
        int count = fieldDataSource.updateStringValueForSign(fid, mappid, strname == null ? null : strname.trim(), setid, evntid + "", locid);
        Log.i("Updated signature", "" + count);
    }

    private void setBtnClickListeners() {
        CustomButton btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> submitSignature());
        CustomButton btnAdd = findViewById(R.id.btnAddSign);
        btnAdd.setOnClickListener(v -> addSignature(mSignature));
    }

    private void submitSignature() {
        final String[] path = {null};
        boolean error = captureSignature();

        if (!error) {

            Toast.makeText(mContext, "Signature(s) added Successfully", Toast.LENGTH_SHORT).show();

            if (fid == 0) {
                if (customerSigns.isEmpty()) {
                    pathsNnames = new ArrayList<>();

                    CustomerSign customerSign = new CustomerSign();
                    customerSign.setId(mSignature.getId());
                    customerSign.setName(yourName.getText().toString());
                    customerSign.setView(mSignature);
                    customerSigns.add(customerSign);

                    for (CustomerSign k : customerSigns) {
                        k.getView().setDrawingCacheEnabled(true);
                        path[0] = mSignature.save(k.getView(), k.getFilepath(), thumbPath.getAbsolutePath());
                        if (path[0] != null) {
                            PathsAndNames pathName = new PathsAndNames(path[0], k
                                    .getName());
                            pathsNnames.add(pathName);
                        }
                    }
                }

                Bundle bundle = new Bundle();
                bundle.putString("status", "done");
                bundle.putParcelableArrayList("pathsNnames", pathsNnames);

                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);

            } else {
                setResultForSignature();
            }

            finish();
        }
    }

    private void setResultForSignature() {
        Log.i(TAG, "setResultForSignature() SignatureName:" + strname);

        Intent intent = new Intent();
        //  intent.putExtras(bundle);
        intent.putExtra("POSITION", fid);
        intent.putExtra("SIGNATURENAMES", strname);

        setResult(RESULT_OK, intent);
    }

    public void setAdapter() {
        if (customerSigns != null && customerSigns.size() > 0) {
            emptyView.setVisibility(View.GONE);
            rvSignatures.setVisibility(View.VISIBLE);
            signatureAdapter = new SignatureAdapter(customerSigns, this,
                    this, false);
            rvSignatures.setAdapter(signatureAdapter);
        } else {
            rvSignatures.setVisibility(View.INVISIBLE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private void initActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Signature");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        // all touch events close the keyboard before they are processed except EditText instances.
        // if focus is an EditText we need to check, if the touchevent was inside the focus editTexts
        final View currentFocus = getCurrentFocus();
        try {

            if (!(currentFocus instanceof EditText) || !isTouchInsideView(ev, currentFocus)) {
                ((InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("dispatchTouch", "dispatchTouchEvent Error:" + e.getMessage());
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isTouchInsideView(final MotionEvent ev, final View currentFocus) {
        final int[] loc = new int[2];
        currentFocus.getLocationOnScreen(loc);
        return ev.getRawX() > loc[0] && ev.getRawY() > loc[1] && ev.getRawX() < (loc[0] + currentFocus.getWidth())
                && ev.getRawY() < (loc[1] + currentFocus.getHeight());
    }

    @Override
    protected void onDestroy() {
        Log.w("GetSignature", "onDestory");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerInternetCheckReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void initNewFileName() {

        String cacheDir = Util.getExternalDataDirPath(this);

        File directory = new File(cacheDir + "/"
                + GlobalStrings.signatureDirectory);
        if (!directory.exists()) {
            directory.mkdir();
        }

        /* String uniqueId = "Signature_" + getTodaysDate() + "_" + getCurrentTime()
                + "_" + Math.random();
        current = uniqueId.replace(".", "") + ".png";
        */
        String uniqueId = "Signature_" + System.currentTimeMillis();
        String current = uniqueId + ".png";
        mypath = new File(directory.getAbsoluteFile(), current);

        String thumbDirPath = directory.getAbsolutePath() + File.separator + GlobalStrings.THUMBNAILS_DIR
                + File.separator;
        File thumbDir = new File(thumbDirPath);
        if (!thumbDir.exists()) {
            thumbDir.mkdir();
        }

        String thumbName = uniqueId + GlobalStrings.THUMBNAIL_EXTENSION_PNG;
        thumbPath = new File(thumbDirPath, thumbName);
    }

    private boolean captureSignature() {

        boolean error = false;
        String name = yourName.getEditableText().toString();

        //04 Jan, 2022 - commented the name validation everywhere
        if (customerSigns.isEmpty()) {
            /*if (name.isEmpty()) {
                yourName.setError("Please enter your name");
                yourName.requestFocus();
                error = true;
            } else*/
            if (mSignature.isEmptySignature()) {
                CustomToast.showToast((Activity) mContext, "Draw Signature ", 8);
                error = true;
            } else {
                addSignature(mSignature);
            }
        } else {
            if (!name.isEmpty() && mSignature.isEmptySignature()) {
                CustomToast.showToast((Activity) mContext, "Draw Signature", 8);
                error = true;
            } else if (!name.isEmpty() && !mSignature.isEmptySignature()) {
                yourName.setError(null);
                addSignature(mSignature);
            } else if (!mSignature.isEmptySignature()) {
                addSignature(mSignature);
            }

        }
        return error;
    }

    private void addSignature(View view) {

        initNewFileName();
        final String[] path = {null};

        String name = yourName.getText().toString();
        /*if (name.isEmpty()) {
            yourName.setError("Please enter your name");
        } else*/
        if (mSignature.isEmptySignature()) {
            CustomToast.showToast((Activity) mContext, "Draw Signature ", 8);
        } else {
            yourName.setError(null);
            CustomerSign customerSign = new CustomerSign();
            Log.i(TAG, "View ID:" + view.getId());
            customerSign.setId(view.getId());
            customerSign.setName(yourName.getText().toString());
            customerSign.setView(mSignature);
            customerSign.setFilepath(mypath.getPath());

            pathsNnames = new ArrayList<>();
            PathsAndNames pathNname = new PathsAndNames();
            try {
                customerSign.getView().getWidth();
                customerSign.getView().getHeight();

//                    Log.v("log_tag out", "Width: " + w);
//                    Log.v("log_tag out", "Height: " + h);
                path[0] = mSignature.save(customerSign.getView(), customerSign.getFilepath(), thumbPath.getAbsolutePath());
                if (path[0] != null) {
                    pathNname = new PathsAndNames(path[0], customerSign.getName());
                    pathsNnames.add(pathNname);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.v("log_tag out Error:", "Error: " + e.getMessage());
            }

            AttachmentData dAttachment = new AttachmentData();
            //  String name1 = extras.getString("name");

            dAttachment.setEventID(evntid);
            dAttachment.setSiteId(siteid);
            dAttachment.setMobileAppId(mappid);
            dAttachment.setLocationID(String.valueOf(locid));
            //  dAttachment.setExtField2(String.valueOf(fid));
            dAttachment.setUserId(userid);
            dAttachment.setSetId(setid);
            dAttachment.setTimeTaken(System.currentTimeMillis());
            dAttachment.setAttachementType("S");
            dAttachment.setExtField1(name);
            dAttachment.setName(name);
            dAttachment.setFieldParameterID(fid + "");

            dAttachment.setExtField5(String.valueOf(System.currentTimeMillis()));

            SimpleDateFormat currentDate = new SimpleDateFormat("MM/dd/yyyy",
                    Locale.getDefault());
            Date todayDate = new Date();
            String thisDate = currentDate.format(todayDate);
            dAttachment.setExtField3(thisDate);
            dAttachment.setAttachmentDate(thisDate);

            int hours = todayDate.getHours();
            int minutes = todayDate.getMinutes();
            String curTime = hours + ":" + minutes;
            dAttachment.setExtField4(curTime);
            dAttachment.setAttachmentTime(curTime);

            dAttachment.setFileLocation(pathNname.getPath());
            dAttachment.setFile1000(pathNname.getPath());
            dAttachment.setFileThumb(thumbPath.getAbsolutePath());

            AttachmentDataSource attachdataSource = new AttachmentDataSource(mContext);
            long i = attachdataSource.insertAttachmentDataForSignature(dAttachment);
            Log.i("", "Attachment Added Successfully" + i);

            customerSigns.add(customerSign);
            setSignatureNameString();

            yourName.setText("");
            mContent.removeAllViews();
            mSignature = new Signature(CaptureSignature.this, null);
            mSignature.setBackgroundColor(Color.WHITE);
            mSignature.clear();
            mContent.addView(mSignature, LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);

            if (signatureAdapter != null) {
                signatureAdapter.notifyItemInserted(customerSigns.size() - 1);
                emptyView.setVisibility(View.GONE);
                rvSignatures.setVisibility(View.VISIBLE);
            } else
                setAdapter();
        }
    }

    @Override
    public void onBackPressed() {
        if (fid == 0) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
        } else {
            setResultForSignature();
        }

        super.onBackPressed();
    }

    private String getTodaysDate() {

        final Calendar c = Calendar.getInstance();
        int todaysDate = (c.get(Calendar.YEAR) * 10000)
                + ((c.get(Calendar.MONTH) + 1) * 100)
                + (c.get(Calendar.DAY_OF_MONTH));
        Log.w("DATE:", String.valueOf(todaysDate));
        return (String.valueOf(todaysDate));

    }

    private String getCurrentTime() {

        final Calendar c = Calendar.getInstance();
        int currentTime = (c.get(Calendar.HOUR_OF_DAY) * 10000)
                + (c.get(Calendar.MINUTE) * 100) + (c.get(Calendar.SECOND));
        Log.w("TIME:", String.valueOf(currentTime));
        return (String.valueOf(currentTime));
    }

    @Override
    public void onSignatureRemoved(ArrayList<CustomerSign> signList) {
        customerSigns = signList;
        setSignatureNameString();
        if (customerSigns.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            rvSignatures.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onSignatureViewClicked() {
        //no use here. will be using this in formActivity
    }

    public class Signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public Signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.setDrawingCacheEnabled(true);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public String save(View view, String path, String thumbPath) {
            Log.v("log_tag", "Width: " + view.getWidth());
            Log.v("log_tag", "Height: " + view.getHeight());
            Log.i(TAG, "Path: " + path);

            if (view.getWidth() < 0 && view.getHeight() < 0)
                return null;

/*                    mBitmap = Bitmap.createBitmap(view.getWidth(),
                            view.getHeight(), Bitmap.Config.RGB_565);*/
            Bitmap mBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

            //  initNewFileName();
            File file;
            if (path == null || path.isEmpty()) {
                file = mypath;
            } else {
                file = new File(path);
            }

            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                OutputStream mFileOutStream = new FileOutputStream(file);

                Canvas canvas = new Canvas(mBitmap);
                view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                view.draw(canvas);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 0, mFileOutStream);

                mFileOutStream.flush();
                mFileOutStream.close();

                Bitmap thumbBmp = Util.getResizedBitmap(mBitmap, 160, 160);
                Util.saveBitmapToSDCard(thumbBmp, new File(thumbPath), GlobalStrings.COMPRESSION_RATE_100);

                path = file.getAbsolutePath();

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("log_tag", e.toString());
            }

            return path;
        }

        public void clear() {
            path.reset();
            invalidate();
        }

        public boolean isEmptySignature() {
            // myBitmap is empty/blank
            return path.isEmpty();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
            //addButton.setEnabled(true);

        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
//                    enableControl();

                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
//                    enableControl();
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

//        private void enableControl() {
//            mAddFab.setEnabled(true);
//            mActionMenu.setEnabled(true);
//            mSubmitFab.setEnabled(true);
//            //  mActionMenu.collapse();
//        }

        private void debug(String string) {
        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        internetFilter.addAction("android.net.wifi.STATE_CHANGE");
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, internetFilter);
    }

    /**
     * Runtime Broadcast receiver inner class to capture internet connectivity events
     */
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = "No Internet Connection.";

            if (CheckNetwork.isInternetAvailable(context)) {
                status = "Internet is Connected.";
            }
            setSnackbarMessage(status, false);
        }
    };

    private void setSnackbarMessage(String status, boolean showBar) {
        String internetStatus = status;

        snackbar = Snackbar
                .make(coordinatorLayout, internetStatus, Snackbar.LENGTH_INDEFINITE)
                .setAction("X", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
        // Changing message text color
        snackbar.setActionTextColor(Color.WHITE);
        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        if (showBar)
            snackbar.show();
    }
}
