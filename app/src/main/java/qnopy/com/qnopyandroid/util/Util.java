package qnopy.com.qnopyandroid.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.versionedparcelable.VersionedParcel;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.ScreenReso;
import qnopy.com.qnopyandroid.androidjavamail.GMail;
import qnopy.com.qnopyandroid.clientmodel.Attachment;
import qnopy.com.qnopyandroid.clientmodel.CheckUserSSOResponse;
import qnopy.com.qnopyandroid.clientmodel.Location;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.CocMasterDataSource;
import qnopy.com.qnopyandroid.db.EventDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.MetaDataSource;
import qnopy.com.qnopyandroid.db.TaskAttachmentsDataSource;
import qnopy.com.qnopyandroid.db.TaskCommentsDataSource;
import qnopy.com.qnopyandroid.db.TaskDetailsDataSource;
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.filefolder.FileFolderHandler;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse;
import qnopy.com.qnopyandroid.services.MyAlarmReceiver;
import qnopy.com.qnopyandroid.ui.activity.ActivationActivity;
import qnopy.com.qnopyandroid.ui.login.LoginActivity;
import qnopy.com.qnopyandroid.ui.activity.ReportFieldsSelectionActivity;
import qnopy.com.qnopyandroid.uiutils.BadgeDrawable;

/**
 * Created by Yogendra on 15/10/15.
 */
public class Util {
    static int TOTAL_SIZE = 20971520; //5242880;
    public static String LogfileName = "Log";

    public static long getTimeInMillisAdding6Hrs(String dateSelected) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Date date = null;
        try {
            date = df.parse(dateSelected);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(Calendar.HOUR, 6);
        Date convertedDate = gc.getTime();
        return convertedDate.getTime();
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        if (html == null) {
            // return an empty spannable if the html is null
            return new SpannableString("");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
            // we are using this flag to give a consistent behaviour
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    public static long getTimeInMillisAddingCurrentTime(String dateSelected) {

        Calendar mCurrentTime = Calendar.getInstance();
        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mCurrentTime.get(Calendar.MINUTE);
        int second = mCurrentTime.get(Calendar.SECOND);
        String displayTime = dateSelected + " " + String.format("%02d", hour) + ":"
                + String.format("%02d", minute);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        Date date = null;
        try {
            date = df.parse(displayTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar gc = new GregorianCalendar();
        if (date != null) {
            gc.setTime(date);
        }
        Date convertedDate = gc.getTime();
        return convertedDate.getTime();
    }

    public static Long getMilliseconds(String dateTime, String dateFormat) {
        long timeInMilliseconds = 0L;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());

        Date mDate = null;
        try {
            mDate = sdf.parse(dateTime);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    public static boolean isThereAnyDataToSync(Context context) {
        EventDataSource eventData = new EventDataSource(context);
        FieldDataSource fieldSource = new FieldDataSource(context);
        LocationDataSource locationSource = new LocationDataSource(context);
        AttachmentDataSource attachDataSrc = new AttachmentDataSource(context);
        CocMasterDataSource cocDataSource = new CocMasterDataSource(context);

        //CHECK AND UPDATE -VE EVENT FILTER
        fieldSource.checkAndUpdateClientEventInFieldData();
        fieldSource.checkAndUpdateClientEventInAttachmentData();

        locationSource.checkAndUpdateClientLocationInFieldData();
        locationSource.checkAndUpdateClientLocationInAttachmentData();

        boolean isLocationsAvailableToSync = locationSource.isOfflineLocationsAvailable();
//        boolean isFieldDataAvailableToSync = fieldSource.isFieldDataAvailableToSync();
        boolean isAttachmentsAvailableToSync = attachDataSrc.attachmentsAvailableToSync();
        boolean isCoCAvailableToSync = cocDataSource.getSyncableCOCID().size() > 0;

        ArrayList<DEvent> eventList = eventData
                .getClientGeneratedEventIDs(context);

        return isLocationsAvailableToSync || isCoCAvailableToSync || fieldSource.getFieldDataCount()
                || isAttachmentsAvailableToSync || !eventList.isEmpty()
                || checkAnyTaskToSync(context);
    }

    public static boolean isShowNewForms(Context context) {
        return Util.getSharedPrefBoolProperty(context, GlobalStrings.IS_SHOW_FASTER_FORMS);
    }

    public static List<String> splitStringToArray(String splitBy, String valueToSplit) {
        try {
            if (valueToSplit != null && !valueToSplit.isEmpty())
                if (splitBy.contains("*"))
                    return Arrays.asList(valueToSplit.split(splitBy));
                else if (splitBy.contains("|"))
                    return Arrays.asList(valueToSplit.split("\\|"));
                else
                    return Arrays.asList(valueToSplit.split("\\s*" + splitBy + "\\s*"));
            else
                return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static boolean checkAnyTaskToSync(Context context) {
        final TaskDetailsDataSource taskDetailsDataSource = new TaskDetailsDataSource(context);
        final TaskCommentsDataSource commentsDataSource = new TaskCommentsDataSource(context);
        TaskAttachmentsDataSource attachmentsDataSource = new TaskAttachmentsDataSource(context);
        ArrayList<TaskDataResponse.CommentList> commentList
                = commentsDataSource.getAllUnSyncedComments("");
        ArrayList<TaskDataResponse.TaskDataList> dataList
                = taskDetailsDataSource.getAllUnSyncedTasks("");
        ArrayList<TaskDataResponse.AttachmentList> attachmentList
                = attachmentsDataSource.getAllUnSyncAttachments("");

        return commentList.size() != 0 || dataList.size() != 0 || attachmentList.size() != 0;
    }

    public static void setBadgeCount(Context context, MenuItem menuItem, String count,
                                     boolean showBadge) {

        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count, showBadge);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    public static boolean unpackZip(String path, String zipName) {

        Log.i(TAG, "unpackZip() IN time:" + System.currentTimeMillis());
        Log.i(TAG, "unpackZip() File Path:" + path + ",ZipName=" + zipName);

        InputStream is;
        ZipInputStream zis;
        try {
            path = path + File.separator;

            String filename;
            is = new FileInputStream(path + zipName);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null) {

                filename = ze.getName();

                if (ze.isDirectory()) {
                    File fmd = new File(path + filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(path + filename);

                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error in UN-Zipping File:" + e.getMessage());
            return false;
        }
        Log.i(TAG, "unpackZip() OUT time:" + System.currentTimeMillis());

        return true;
    }

    public static boolean isTablet(Context context) {
/*        if (BuildConfig.DEBUG) {
            return true;
        } else*/
        return context.getResources().getBoolean(R.bool.isTablet);
//        return true; //for testing
    }

    public static boolean hasDigitDecimalOnly(String value) {
        if (value != null)
            return value.matches("-?\\d+(\\.\\d+)?") || value.matches("(-?\\.\\d+)?");
        else
            return false;
    }

    public static void setDataAndTypeForIntent(String url, Intent intent, Uri uri) {
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document`
            intent.setDataAndType(uri, "application/msword");
        } else if (url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }
    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(
                v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getWidth(), v.getHeight());
        v.draw(c);
        return b;
    }

    public static boolean containsAtLeastOneAlphabet(String value) {
        return value.matches(".*[a-zA-Z]+.*");
    }

    public static String splitArrayToString(String[] arrToSplit) {
        return Arrays.toString(arrToSplit).replaceAll("[\\[.\\].\\s+]", "");
    }

    public static String splitArrayListToString(ArrayList<String> arrToSplit) {
        String commaSeparatedList = arrToSplit.toString();

        commaSeparatedList
                = commaSeparatedList.replace("[", "")
                .replace("]", "")
                .replace(" ", "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return String.join(",", arrToSplit);
        } else return commaSeparatedList;
    }

    public static boolean isUrlV20OrMobileTest(Context context) {
        return context.getString(R.string.prod_base_uri).toLowerCase().contains("v20") ||
                context.getString(R.string.prod_base_uri).toLowerCase().contains("mobiletest");
    }

    public static boolean isStagingUrl(Context context) {
        return context.getString(R.string.prod_base_uri).toLowerCase().contains("staging")
                || context.getString(R.string.prod_base_uri).toLowerCase().contains("http:");
    }

    public static String covertBase64ToString(String formsString) {
        try {
            return new String(Base64.decode(formsString, 0), "UTF-8");
        } catch (UnsupportedEncodingException | IllegalArgumentException unused) {
            return "";
        }
    }

    public static String randomUUID(Context context, boolean isFieldUUID) {
        try {
            String uuid = UUID.randomUUID().toString();
            if (!uuid.isEmpty()) {
                if (isFieldUUID && new FieldDataSource(context).isFieldUUIDExist(uuid))
                    randomUUID(context, true);
                else if (!isFieldUUID && new AttachmentDataSource(context).isAttachmentUUIDExist(uuid))
                    randomUUID(context, false);
            }
            return uuid;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getCabSeparatedString(ArrayList<String> options) {

        ArrayList<String> cabSeparatedOptions = new ArrayList<>();
        for (String str : options) {
            String option = str.trim() + "^" + str.trim();
            cabSeparatedOptions.add(option);
        }

        return cabSeparatedOptions.toString().replaceAll("\\[", "")
                .replaceAll("\\]", "");
    }

    public static boolean isLocationAvailable() {
        try {
            return GlobalStrings.CURRENT_GPS_LOCATION != null
                    && GlobalStrings.CURRENT_GPS_LOCATION.getLatitude() != 0.0
                    && GlobalStrings.CURRENT_GPS_LOCATION.getLongitude() != 0.0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String createThumbDir(String baseDir) {
        String thumbDir = baseDir + File.separator + GlobalStrings.THUMBNAILS_DIR + File.separator;

        File thumbStorageDir = new File(thumbDir);

        if (!thumbStorageDir.exists())
            thumbStorageDir.mkdirs();

        return thumbDir;
    }

    public static long calculateDaysCountFromMillis(Long trialPeriod) {
        return (TimeUnit.MILLISECONDS.toDays(trialPeriod)
                - TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()));
    }

    public static int getDecimalPlaces(String value) {
        int integerPlaces = value.indexOf('.');
        return value.length() - integerPlaces - 1;
    }

    public static long addDaysToCurrentDate(int daysCount) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); // Using today's date
        c.add(Calendar.DATE, daysCount); // Adding days
        return c.getTimeInMillis();
    }

    public interface Callback {
        public void call();

    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private final static String TAG = Util.class.getSimpleName();

    public static void showKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // Setup a recurring alarm every half hour
    public static void scheduleAlarm(Context context) {
        Log.i(TAG, "start Alarm");

        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(context, MyAlarmReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        PendingIntent pIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pIntent = PendingIntent.getBroadcast(context, MyAlarmReceiver.REQUEST_CODE, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pIntent = PendingIntent.getBroadcast(context, MyAlarmReceiver.REQUEST_CODE, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
        // Setup periodic alarm every every half hour from this point onwards
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, pIntent);
    }

    public static void stopAlarm(Context context) {
        Log.i(TAG, "stop Alarm");
        Intent intent = new Intent(context, MyAlarmReceiver.class);
        final PendingIntent pIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pIntent = PendingIntent.getBroadcast(context, MyAlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pIntent = PendingIntent.getBroadcast(context, MyAlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

    public static void setOverflowButtonColor(final Activity activity, final int color) {
        final String overflowDescription = activity.getString(R.string.abc_action_menu_overflow_description);
        final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final ArrayList<View> outViews = new ArrayList<View>();
                decorView.findViewsWithText(outViews, overflowDescription,
                        View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
                if (outViews.isEmpty()) {
                    return;
                }

                AppCompatImageView overflow = (AppCompatImageView) outViews.get(0);
                overflow.setColorFilter(color);

                removeOnGlobalLayoutListener(decorView, this);
            }
        });
    }

    public static Bitmap cropToSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = Math.min(height, width);
        int newHeight = (height > width) ? height - (height - width) : height;
        int cropW = (width - height) / 2;
        cropW = Math.max(cropW, 0);
        int cropH = (height - width) / 2;
        cropH = Math.max(cropH, 0);

        return Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public static String saveBitmapToSDCard(Bitmap bitmap, File outputPath, int compressionRate) {
        try {

            if (outputPath.exists())
                outputPath.delete(); //warning is appearing as its return type is not used so it can be ignored

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            outputPath.createNewFile(); //warning is appearing as its return type is not used so it can be ignored
            //write the bytes in file
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(outputPath);
                fos.write(bytes.toByteArray());
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressionRate, fos);
                fos.flush();
                fos.close();
                bytes.close();
            } catch (IOException e) {
                Log.e("GREC", e.getMessage());
            }
            return outputPath.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getFormattedDate(Long millis) throws VersionedParcel.ParcelException {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatter.format(calendar.getTime());
    }

    public static String get24hrFormatTime(String time, Locale locale) {
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm", locale);
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a", locale);
        Date date = null;
        try {
            date = parseFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            return displayFormat.format(date);
        }

        return "";
    }

    public static String get12hrFormatTime(String time) {
        SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = null;
        try {
            date = parseFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            return displayFormat.format(date);
        }

        return "";
    }

    public static String getFormattedDateTime(Long millis, String dateFormat) throws VersionedParcel.ParcelException {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.US);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatter.format(calendar.getTime());
    }

    public static String getHourFromDateString(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = null;
        String dateTime = null;
        try {
            date = format.parse(dateString);
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh a", Locale.getDefault());
            if (date != null) {
                dateTime = dateFormat.format(date).replace("am", "AM")
                        .replace("pm", "PM");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateTime;
    }

    public static void setOverflowButtonIcon(final Activity activity, final int resID) {
        final String overflowDescription = activity.getString(R.string.abc_action_menu_overflow_description);
        final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final ArrayList<View> outViews = new ArrayList<View>();
                decorView.findViewsWithText(outViews, overflowDescription,
                        View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
                if (outViews.isEmpty()) {
                    return;
                }

                AppCompatImageView overflow = (AppCompatImageView) outViews.get(0);
                overflow.setImageResource(resID);

                removeOnGlobalLayoutListener(decorView, this);
            }
        });
    }

    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    public static Bitmap getSignbitmap(String path) {
        Bitmap imgthumBitmap = null;
        try {

            final int THUMBNAIL_WIDTH = 390;
            final int THUMBNAIL_HEIGHT = 190;

            FileInputStream fis = new FileInputStream(path);
            imgthumBitmap = BitmapFactory.decodeStream(fis);

            imgthumBitmap = Bitmap.createScaledBitmap(imgthumBitmap,
                    THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, false);

            ByteArrayOutputStream bytearroutstream = new ByteArrayOutputStream();
            imgthumBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytearroutstream);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return imgthumBitmap;
    }


    public static Bitmap getbitmap(String path) {
        Bitmap imgthumBitmap = null;
        try {

            final int THUMBNAIL_SIZE = 110;

            FileInputStream fis = new FileInputStream(path);
            imgthumBitmap = BitmapFactory.decodeStream(fis);

            imgthumBitmap = Bitmap.createScaledBitmap(imgthumBitmap,
                    THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

            ByteArrayOutputStream bytearroutstream = new ByteArrayOutputStream();
            imgthumBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytearroutstream);


        } catch (Exception ex) {

        }
        return imgthumBitmap;
    }

    public static Bitmap getbitmap(String path, int size) {
        Bitmap imgThumBitmap = null;
        try {

            FileInputStream fis = new FileInputStream(path);
            imgThumBitmap = BitmapFactory.decodeStream(fis);

            imgThumBitmap = Bitmap.createScaledBitmap(imgThumBitmap,
                    size, size, false);

            ByteArrayOutputStream bytearroutstream = new ByteArrayOutputStream();
            imgThumBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytearroutstream);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return imgThumBitmap;
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (drawable != null) {
                drawable = (DrawableCompat.wrap(drawable)).mutate();
            }
        }

        Bitmap bitmap = null;
        Canvas canvas = null;
        if (drawable != null) {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            if (bitmap != null) {
                canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
            }
        }

        return bitmap;
    }

    public static void setSharedPreferencesProperty(Context context,
                                                    String propertyName, String propertyValue) {

        final SharedPreferences prefs = context.getSharedPreferences(
                Util.class.getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(propertyName, propertyValue);
        editor.apply();
    }

    public static void clearSharedPreferences(Context context) {

        final SharedPreferences prefs = context.getSharedPreferences(
                Util.class.getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public static void setSharedPreferencesProperty(Context context,
                                                    String propertyName, int propertyValue) {

        final SharedPreferences prefs = context.getSharedPreferences(
                Util.class.getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(propertyName, propertyValue);
        editor.apply();
    }

    public static void setSharedPreferencesProperty(Context context,
                                                    String propertyName, boolean propertyValue) {

        final SharedPreferences prefs = context.getSharedPreferences(
                Util.class.getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(propertyName, propertyValue);
        editor.apply();
    }

    public static void setSharedPreferencesProperty(Context context,
                                                    String propertyName, long propertyValue) {

        final SharedPreferences prefs = context.getSharedPreferences(
                Util.class.getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(propertyName, propertyValue);
        editor.apply();
    }

    public static Location getSharedPrefLocJsonProperty(Context context,
                                                        String propertyName, Class<Location> locationClass) {

        final SharedPreferences prefs = context.getSharedPreferences(
                Util.class.getSimpleName(), Context.MODE_PRIVATE);
        String locString = prefs.getString(propertyName, null);
        Location location = null;
        try {
            if (locString != null && !locString.isEmpty())
                location = new Gson().fromJson(locString, locationClass);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            location = null;
        }
        return location;
    }

    public static boolean getSharedPrefBoolProperty(Context context,
                                                    String propertyName) {

        final SharedPreferences prefs = context.getSharedPreferences(
                Util.class.getSimpleName(), Context.MODE_PRIVATE);
        return prefs.getBoolean(propertyName, false);
    }

    public static String getSharedPreferencesProperty(Context context,
                                                      String propertyName) {

        final SharedPreferences prefs = context.getSharedPreferences(
                Util.class.getSimpleName(), Context.MODE_PRIVATE);
        return prefs.getString(propertyName, null);
    }

    public static Long getSharedPrefLongProperty(Context context,
                                                 String propertyName) {

        final SharedPreferences prefs = context.getSharedPreferences(
                Util.class.getSimpleName(), Context.MODE_PRIVATE);
        return prefs.getLong(propertyName, 0);
    }

    public static int getSharedPrefIntProperty(Context context,
                                               String propertyName) {

        final SharedPreferences prefs = context.getSharedPreferences(
                Util.class.getSimpleName(), Context.MODE_PRIVATE);
        return prefs.getInt(propertyName, 0);
    }

    public static void setSharedPrefFieldProperty(Context context,
                                                  String propertyName, String propertyValue) {

        final SharedPreferences prefs = context.getSharedPreferences(
                ReportFieldsSelectionActivity.FieldParamsSelected.class.getSimpleName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(propertyName, propertyValue);
        editor.apply();
    }

    public static String getSharedPrefFieldProperty(Context context,
                                                    String propertyName) {

        final SharedPreferences prefs = context.getSharedPreferences(
                ReportFieldsSelectionActivity.FieldParamsSelected.class.getSimpleName(),
                Context.MODE_PRIVATE);
        return prefs.getString(propertyName, null);
    }

/*    public static TokenResponse getSharedPrefOpenIdTokenProperty(Context context,
                                                                 String propertyName) {

        final SharedPreferences prefs = context.getSharedPreferences(
                Util.class.getSimpleName(),
                Context.MODE_PRIVATE);
        return new Gson().fromJson(prefs.getString(propertyName, null), TokenResponse.class);
    }*/

    public static CheckUserSSOResponse getSharedPrefSSOResponseProperty(Context context,
                                                                        String propertyName) {

        final SharedPreferences prefs = context.getSharedPreferences(
                Util.class.getSimpleName(),
                Context.MODE_PRIVATE);
        return new Gson().fromJson(prefs.getString(propertyName, null), CheckUserSSOResponse.class);
    }

    public static void clearSharedPrefField(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(ReportFieldsSelectionActivity
                .FieldParamsSelected.class.getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public static String getAppVersion(Context context) {

        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();

        try {
            String ver = "" + pm.getPackageInfo(packageName, 0).versionCode;
            Log.i(TAG, "App Version :" + ver);
            return ver;

        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        return "1";
    }

    public static void setSnackbarMessage(LinearLayout coordinatorLayout, Snackbar snackbar, String status, boolean showBar) {
        String internetStatus = status;

        final Snackbar finalSnackbar = snackbar;
        snackbar = Snackbar
                .make(coordinatorLayout, internetStatus, Snackbar.LENGTH_INDEFINITE)
                .setAction("Sync Data", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finalSnackbar.dismiss();
                    }
                });


        // Changing message text color
        snackbar.setActionTextColor(Color.WHITE);
        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();

    }

    public static String getAppVersionName(Context context) {

        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();

        try {
            String ver = "" + pm.getPackageInfo(packageName, 0).versionName;
            Log.i(TAG, "App Version Name:" + ver);
            return ver;

        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        return "1";
    }

    public static void setDeviceNOT_ACTIVATED(final Activity context, final String uname,
                                              final String pswd) {

        AlertDialog alert = null;
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setMessage(GlobalStrings.responseMessage);
        alertBuilder.setTitle("Alert");
        alertBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (!uname.isEmpty()) {
                    UserDataSource userSource = new UserDataSource(context);
                    userSource.deleteUser(uname, pswd);
                }
                setLogout(context);
                context.finish();
            }
        });

        alert = alertBuilder.create();
        alert.show();
    }

    public static void setDevicLOCKED(final Activity context, final String username, final String password) {

        AlertDialog alert = null;
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setMessage(GlobalStrings.responseMessage);
        alertBuilder.setTitle("Alert");
        alertBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent applicationIntent = new Intent(context, ActivationActivity.class);
                applicationIntent.putExtra("Username", username);
                applicationIntent.putExtra("Password", password);
                context.startActivity(applicationIntent);
                context.finish();

            }
        });


        alert = alertBuilder.create();
        alert.show();


    }

    public static String getAboutMsg(Context context) {
        final String versionNumber = Util.getAppVersionName(context);
        final String versionCode = Util.getAppVersion(context);
        String aboutMsg = "Version Number : "
                + versionNumber + "\nVersion Code : " + versionCode;
        return aboutMsg;
    }

    public static boolean createZipFile(String ipFile, String outputFilePath, String filename) {

        // create byte buffer

        File outputfile = null;
        byte[] buffer = new byte[1024];
        FileOutputStream fos = null;
        try {

            Util.createFolder(outputFilePath);
            //copy unsyncPhoto dir
            outputfile = new File(outputFilePath, filename);

            if (!outputfile.exists()) {
                outputfile.createNewFile();
            }

            fos = new FileOutputStream(outputfile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            ZipEntry ze = new ZipEntry(GlobalStrings.DATABASE_NAME);
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(ipFile);

            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            in.close();
            zos.closeEntry();

            //remember close it
            zos.close();
            Log.i(TAG, "File for Zip:" + outputfile.getName());

            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            Log.e("LocationActivity", "Error creating zip file :" + ex.getMessage());
        }
        return false;

    }


    /*
     *
     * Zips a file at a location and places the resulting zip file at the toLocation
     * Example: zipFileAtPath("downloads/myfolder", "downloads/myFolder.zip");
     */

    public static boolean zipFileAtPath(String sourcePath, String toLocation, String outputfileName) {
        final int BUFFER = 2048;
        File outputfile;
        File sourceFile = new File(sourcePath);

        try {

            Util.createFolder(toLocation);
            outputfile = new File(toLocation, outputfileName);

            if (!outputfile.exists()) {
                outputfile.createNewFile();
            }


            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(outputfile);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            if (sourceFile.isDirectory()) {
                zipSubFolder(out, sourceFile, sourceFile.getParent().length());
            } else {
                byte data[] = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(sourcePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*
     *
     * Zips a subfolder
     *
     */

    private static void zipSubFolder(ZipOutputStream out, File folder,
                                     int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }

    /*
     * gets the last path component
     *
     * Example: getLastPathComponent("downloads/example/fileToZip");
     * Result: "fileToZip"
     */
    public static String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        String lastPathComponent = segments[segments.length - 1];
        return lastPathComponent;
    }


    public static boolean createFolder(String path) {
        File fp = new File(path);
        if (!fp.exists()) {
            return fp.mkdirs();
        } else
            return false;
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static boolean deleteFile_Folder(String path, String filename) {
        File file = new File(path + File.separator + filename);
        if (file.exists()) {
            Log.i(TAG, "File " + file.getName() + " deleted.");

            if (!file.delete()) {
                try {
                    file.getCanonicalFile().delete();
                    File dir = new File(path);
                    if (dir.exists()) {
                        if (dir.isDirectory()) {
                            File[] files = dir.listFiles();
                            if (files.length < 1) {
                                return dir.delete();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        File dir = new File(path);
        if (dir.exists()) {
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files.length < 1) {
                    Log.i(TAG, "Directory " + dir.getName() + " deleted.");
                    return dir.delete();
                }
            }
        }
        return true;
    }


    public static String getBaseContextPath(Context context) {

        String BaseContextPath;
        BaseContextPath = context.getFilesDir().getAbsolutePath();
        BaseContextPath = BaseContextPath.substring(0, BaseContextPath.lastIndexOf("/"));
        Log.i("getBaseContextPath", "BaseContextPath:" + BaseContextPath);
        return BaseContextPath;
    }

    public static String getExternalDataDirPath(Context context) {

        String BaseContextPath;
        try {
            BaseContextPath
                    = context.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath())
                    .getAbsolutePath();
            BaseContextPath = BaseContextPath.substring(0, BaseContextPath.lastIndexOf("/"));
            Log.i("ExternalDataDir", "ExternalDataDir:" + BaseContextPath);
            return BaseContextPath;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getExternalDataDirPath error:" + e.getMessage());
        }
        return null;
    }

    public static String getFileFolderDirPath(Context context, String siteID) {
        String makeDir = Util.getExternalDataDirPath(context);
//        String makeDir = "";

        if (makeDir != null) {
            makeDir = makeDir + GlobalStrings.FILEFOLDER + siteID;
            Log.e("Folder path", "getFileFolderDirPath: mkdir not null" + makeDir);
        } else {
            makeDir = "";
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                String root = Environment.getExternalStorageDirectory().toString();
                makeDir = root + GlobalStrings.EXT_FILEFOLDER_PATH + siteID;
                Log.e("Folder path", "getFileFolderDirPath: mkdir null" + makeDir);
            }
        }
        FileFolderHandler.createDirectory(makeDir);
        Log.i(TAG, "FileFolder DownLoad Directory:" + makeDir);

        return makeDir;
    }

    public static String getFileFolderDirPathForPDF(Context context, String siteID, String eventID) {
        String makeDir = Util.getExternalDataDirPath(context);
//        String makeDir = "";

        if (makeDir != null) {
            makeDir = makeDir + GlobalStrings.FILEFOLDER + siteID + GlobalStrings.FILEFOLDERPDF + eventID;
            Log.e("Folder path", "getFileFolderDirPath: mkdir not null" + makeDir);
        } else {
            makeDir = "";
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                String root = Environment.getExternalStorageDirectory().toString();
                makeDir = root + GlobalStrings.EXT_FILEFOLDER_PATH_PDF + siteID + GlobalStrings.FILEFOLDERPDF + eventID;
                Log.e("Folder path", "getFileFolderDirPath: mkdir null" + makeDir);
            }
        }
        FileFolderHandler.createDirectory(makeDir);
        Log.i(TAG, "FileFolder DownLoad Directory:" + makeDir);

        return makeDir;
    }

    public static String getFileFolderDirPathForCOCPDF(Context context) {
        String makeDir = Util.getExternalDataDirPath(context);

        if (makeDir != null) {
            makeDir = makeDir + GlobalStrings.FILEFOLDER + GlobalStrings.COC_FOLDER_PDF;
        } else {
            makeDir = "";
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                String root = Environment.getExternalStorageDirectory().toString();
                makeDir = root + GlobalStrings.EXT_FILEFOLDER_PATH_PDF + GlobalStrings.COC_FOLDER_PDF;
            }
        }
        FileFolderHandler.createDirectory(makeDir);
        return makeDir;
    }

    public static boolean deleteFileFolderDir(Context context) {
        String deleteDir = Util.getExternalDataDirPath(context);

        deleteDir = deleteDir + GlobalStrings.FILEFOLDER;
        File dDir = new File(deleteDir);
        if (dDir.isDirectory()) {
            String[] children = dDir.list();
            for (int i = 0; i < children.length; i++) {
                File childDir = new File(dDir, children[i]);

                if (childDir.isDirectory()) {
                    if (childDir.list().length < 1) {
                        childDir.delete();
                    } else {
                        String[] base_children = childDir.list();

                        for (int j = 0; j < base_children.length; j++) {
                            new File(childDir, base_children[j]).delete();
                        }

                        childDir.delete();

                    }
                } else {
                    childDir.delete();
                }

            }
        }


        if (dDir.exists()) {
            return dDir.delete();
        }

        return false;
    }

    public static String getHtmlContent(Context context, String assetFileName) {
        String htmlData = "";
        try {
            InputStream isRow = context.getAssets().open(assetFileName);
            int size = isRow.available();

            byte[] buffer = new byte[size];
            isRow.read(buffer);
            isRow.close();
            htmlData = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            htmlData = "";
        }
        return htmlData;
    }

    public static <T> List[] partition(List<T> list, int n) {
        // Calculate the total number of partitions of size `n` each
        int m = list.size() / n;
        if (list.size() % n != 0) {
            m++;
        }

        // partition the list into sublists of size `n` each
        List<List<T>> itr = Lists.partition(list, n);

        // create `m` empty lists and initialize them with sublists
        List<T>[] partition = new ArrayList[m];
        for (int i = 0; i < m; i++) {
            partition[i] = new ArrayList(itr.get(i));
        }

        // return the lists
        return partition;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static void msgHandler(String message, Handler mHandler) {

        Message msg = Message.obtain(); // Creates a new Message instance
        msg.obj = message; // Put the string into Message, into "obj" field.
        msg.setTarget(mHandler); // Set the Handler
        msg.sendToTarget();
    }

    public static void setLogout(Activity context) {
        Log.i(TAG, "setLogout() IN time:" + System.currentTimeMillis());
        String key_userguid = getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
        setSharedPreferencesProperty(context, key_userguid, "");
        setSharedPreferencesProperty(context, GlobalStrings.USERID, "");
        setSharedPreferencesProperty(context, GlobalStrings.USERNAME, "");
        setSharedPreferencesProperty(context, GlobalStrings.PASSWORD, "");
        setSharedPreferencesProperty(context, GlobalStrings.COMPANYID, "");
        setSharedPreferencesProperty(context, GlobalStrings.CURRENT_APPID, "");
        setSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITEID, "");
        setSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITENAME, "");
        setSharedPreferencesProperty(context, GlobalStrings.CURRENT_LOCATIONID, "");
        setSharedPreferencesProperty(context, GlobalStrings.CURRENT_LOCATIONNAME, "");
        setSharedPreferencesProperty(context, GlobalStrings.IS_SESSION_ACTIVE, "false");
        setSharedPreferencesProperty(context, GlobalStrings.IS_SHOW_FASTER_FORMS, false);
        setSharedPreferencesProperty(context, GlobalStrings.USERAPPTYPE, "");
        setSharedPreferencesProperty(context, GlobalStrings.ENABLE_SPLIT_SCREEN, false);
        setSharedPreferencesProperty(context, GlobalStrings.IS_CAPTURE_LOG, String.valueOf(false));

        clearSharedPreferences(context);
        //clearing any saved selected field params for form report

        //in case if person has login and logout to some other user without closing app
        ScreenReso.isLimitedUser = false;
        ScreenReso.isCalendarUser = false;
        ScreenReso.isProjectUser = false;
        ScreenReso.isMobile2POINT0 = false;

        MetaDataSource md = new MetaDataSource(context);
        //commented on 22 Feb, 23 as when user logout we shouldn't erase data so that data will
        //stay as it is, in case of guid cleared on web and app logouts the data should persist
/*        md.resetAppData();
        delete_All_Log();*/
        md.resetUsersData();

        Intent intent = new Intent(new Intent(context, LoginActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.left_to_right,
                R.anim.right_to_left);
    }

    public static void toastInBackground(final Activity context, String msg) {
        final String toastMsg = msg;
        // Toast.makeText(objContext, toastMsg, Toast.LENGTH_SHORT).show();

        context.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show();

            }
        });
    }

    public static void reLogin(Activity context, String uName, String pswd) {
        UserDataSource ob = new UserDataSource(context);
        if (ob.deleteUser(uName, pswd)) {
            setLogout(context);
        }
    }

    public static boolean isAlreadyLogin(Context context) {
        String username = getSharedPreferencesProperty(context, GlobalStrings.USERNAME);
        String pass = getSharedPreferencesProperty(context, GlobalStrings.PASSWORD);
        String guid = getSharedPreferencesProperty(context, username);


        if (username != null && !username.isEmpty() && pass != null && !pass.isEmpty()) {

            if (guid == null || guid.isEmpty()) {
                UserDataSource ob = new UserDataSource(context);
                ob.deleteUser(username, pass);
                setSharedPreferencesProperty(context, GlobalStrings.USERNAME, "");
                setSharedPreferencesProperty(context, GlobalStrings.PASSWORD, "");
                setSharedPreferencesProperty(context, GlobalStrings.USERID, "");

                return false;
            } else {
                //Crittercism.setUsername(username);// TODO: 13-Dec-15
                return true;
            }

        }
        return false;

    }

    public static String getMD5(InputStream fis) throws NoSuchAlgorithmException,
            IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] dataBytes = new byte[1024];

        int readCount;
        while ((readCount = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, readCount);
        }

        byte[] mdBytes = md.digest();

        // convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mdBytes.length; i++) {
            sb.append(Integer.toString((mdBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }

        System.out.println("Digest(in hex format):: " + sb.toString());
        return sb.toString();
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static String formatToDatabaseDateTimeString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static String getMMddyyyyFromMilliSeconds(String millisec) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(millisec));

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format("%02d", mMonth + 1) + "/"
                + String.format("%02d", mDay) + "/" + String.format("%02d", mYear);
    }

    public static String getyyyyMMddFromMilliSeconds(String millisec) {
        Calendar calendar = Calendar.getInstance();
        if (millisec.length() != 13)
            millisec = millisec + "000";
        calendar.setTimeInMillis(Long.parseLong(millisec));

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        String storingDate = String.format("%02d", mYear) + "-" + String.format("%02d", mMonth + 1)
                + "-" + String.format("%02d", mDay);
        return storingDate;
    }

    public static String getMMFromMilliSeconds(String millisec) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(millisec));

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        String storingDate = String.format("%02d", mMonth + 1) + " " + String.format("%02d", mYear);
        return storingDate;
    }

    public static String gethhmmFromMilliSeconds(String millisec) {
        String hhmm = "";

        try {
            hhmm = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(Long.parseLong(millisec)),
                    TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(millisec)) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(Long.parseLong(millisec))),
                    TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(millisec)) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(millisec))));
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
        return hhmm;
    }

    public static String gethhmmFromMilliS(long millis) {
        // New date object from millis
        Date date = new Date(millis);
        // formattter
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        // Pass date object
        String formatted = formatter.format(date);
        return formatted;
    }

    public static String getFormattedDateFromMilliS(long millis, String dateFormat) {
        // New date object from millis
        Date date = new Date(millis);
        // formattter
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
        // Pass date object
        String formatted = formatter.format(date);
        return formatted;
    }

    public static int getQuarter(String DateInmmddYY) {
        try {
            System.out.println("Input DAte:" + DateInmmddYY);
            SimpleDateFormat df;
            df = new SimpleDateFormat("MM/dd/yy");
            Date dd = df.parse(DateInmmddYY);
            Log.i("getQuarter", "mm/dd/yy Formated Date:" + dd.toString());
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            /* Consider whether you need to set the calendar's timezone. */
            cal.setTime(dd);
            int month = cal.get(Calendar.MONTH); /* 0 through 11 */

            System.out.println("Input DAte Month:" + month);

            int quarter = (month / 3) + 1;
            Log.i("getQuarter", "Input Date Quarter:" + quarter);
            return quarter;
        } catch (ParseException ex) {
            ex.printStackTrace();
            Log.e("getQuarter", "Error in getQuarter:" + ex.getMessage());
        }

        return 0;
    }

    public static Date parseFromDatabaseDateTimeString(String dateString) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatToYesterdayOrToday(long date, Context context) {

        try {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
            Calendar today = Calendar.getInstance();
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DATE, -1);

            Date time = new Date(date);
            // formattter
            SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
            // Pass date object
            String formattedTime = timeFormatter.format(time);


            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                return context.getString(R.string.today) + formattedTime;
            } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR)
                    && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
                return context.getString(R.string.yesterday) + formattedTime;
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd hh:mm a");
                // Create a calendar object that will convert the date and time value in milliseconds to date.
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTimeInMillis(date);
                return formatter.format(calendar1.getTime());
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "formatToYesterdayOrToday() Error:" + e.getMessage());

            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd hh:mm a");
            // Create a calendar object that will convert the date and time value in milliseconds to date.
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(date);
            return formatter.format(calendar1.getTime());
        }

    }

    public static String parseMillisToMMMddyyy_hh_mm_ss_aa(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss aaa");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String parseMillisToMMMdd_hh_mm_aa(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, hh:mm aaa");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String parseMillisToMMMddyyy(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String getDatabaseDateFormat(String dateString) {
        String formattedDate = null;
        try {
            formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd-MM-yyyy").parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    public static String getDatePeriodQuarter(String dateString) {
        String formattedDate = null;
        try {
            formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd-MM-yyyy").parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    public static String getDisplayDateFormat(String dateString) {
        String formattedDate = null;
        try {
            formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    public static int parseInteger(String string, int defaultValue) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static double parseDouble(String string, double defaultValue) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


    public static String toSnakeCase(String string) {
        StringBuilder stringBuilder = new StringBuilder(string.length() * 2);
        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            if (Character.isSpaceChar(ch)) {
                stringBuilder.append('_');
            } else if (Character.isUpperCase(ch)) {
                if (i - 1 >= 0 && Character.isLowerCase(string.charAt(i - 1))) {
                    stringBuilder.append('_');
                }
                stringBuilder.append(Character.toLowerCase(ch));
            }
        }
        return stringBuilder.toString();
    }

    private static String[] ordinalSuffix = {"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};

    public static String ordinal(int num) {
        int m = num % 100;
        return num + ordinalSuffix[(m > 10 && m < 20) ? 0 : (m % 10)];
    }


    public static int sendDatabase(Context context, String userName) {
        int ret = -1;
        boolean dbAttached = false;
        boolean error = false;
        String root = Environment.getExternalStorageDirectory().toString();
//        String demo_path=root+GlobalStrings.DEMO_IMAGE_PATH;
        String db_zipFile = root + GlobalStrings.ZIP_DB_PATH + GlobalStrings.DATABASE_NAME + ".zip";
        String db_zipFilepath = root + GlobalStrings.ZIP_DB_PATH;
        String dbName = getBaseContextPath(context) + GlobalStrings.DB_PATH + GlobalStrings.DATABASE_NAME;
        File dbFile = new File(dbName);


        List<Attachment> attachList = new ArrayList<Attachment>();
        List<Attachment> sentList = new ArrayList<Attachment>();

        // DataSource for attachment from d_attachment table
        AttachmentDataSource attachDataSource = new AttachmentDataSource(context);


        String fromEmail = "donotreplyaqua@gmail.com";
        String fromPassword = "AQUAdonotreply";

        String toEmails = "aquabluesolutions@gmail.com";//techsupport@goaquablue.com,


        List<String> toEmailList = Arrays.asList(toEmails.split("\\s*,\\s*"));
        String emailSubject = "Database for User-" + userName
                + " Date-" + new Date();
        String emailBody = "";

        Attachment dbAttachment = new Attachment();

        //06-Nov-15 Zip DB File Location
        if (Util.createZipFile(dbName, db_zipFilepath, GlobalStrings.DATABASE_NAME + ".zip")) {
            dbAttachment.setFileLocation(db_zipFile);
            Log.i(TAG, "DB FIle Attached:" + db_zipFile);
        } else {
            dbAttachment.setFileLocation(dbName);
            Log.i(TAG, "DB FIle Attached:" + dbName);
        }

        //dbAttachment.setFileLocation(dbName);

        // add the field data to attachment list
        if (dbFile.exists()) {
            attachList.add(dbAttachment);
            dbAttached = true;
        }

        List<String> attachFileList = new ArrayList<String>();
        File file;
        int totalSize = 0;

        if (attachList.size() == 0) {
            toastInBackground((Activity) context, "No Db found");
            return 0;
        }

        if (attachList != null) {
            for (int i = 0; i < attachList.size(); ) { // do not do i++ here
                String fileStr = attachList.get(i).getFileLocation();
                if (fileStr == null) {
                    sentList.add(attachList.get(i));
                    i++;
                    continue;
                }
                file = new File(fileStr);
                // System.out.println("mmmm"+"attach Filename="+attachList.get(i).getFileLocation());
                if ((file != null) && (file.exists())) {
                    if ((file.length() + totalSize) <= TOTAL_SIZE) { // can not
                        // send
                        // in
                        // current
                        // lot
                        attachFileList.add(attachList.get(i).getFileLocation());
                        totalSize += file.length();

                        sentList.add(attachList.get(i));
                        i++;
                    } else if (file.length() > TOTAL_SIZE) { // size too big so
                        // can never be
                        // sent
                        toastInBackground((Activity) context, "File "
                                + attachList.get(i).getFileLocation()
                                + "  Too Big, Can not be sent");
                        sentList.add(attachList.get(i));
                        i++;
                    } else {// current lot has filled so send
                        GMail androidEmail = new GMail(fromEmail, fromPassword,
                                (List) toEmailList, emailSubject, emailBody,
                                (List) attachFileList);
                        try {
                            androidEmail.createEmailMessage();
                        } catch (AddressException e) {
                            e.printStackTrace();
                            error = true;
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            error = true;
                        } catch (MessagingException e) {
                            e.printStackTrace();
                            error = true;
                        }
                        if (!error) {
                            try {
                                androidEmail.sendEmail(context);
                            } catch (AddressException e) {
                                e.printStackTrace();
                                error = true;
                            } catch (MessagingException e) {
                                e.printStackTrace();
                                error = true;
                            } finally {
                                for (i = 0; i < sentList.size(); i++) {
                                    sentList.remove(i);
                                }
                                if (dbAttached) {
                                    dbAttached = false;
                                }
                                ret = 1;
                            }
                        }

                        attachFileList = new ArrayList<String>();
                        totalSize = 0;
                        if (!error) {
                            for (int count = 0; count < sentList.size(); count++) {
                                try {
                                    attachDataSource.setImageSyncFlag(
                                            AttachmentDataSource.SyncType.email, sentList.get(i));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            error = false;
                            sentList = new ArrayList<Attachment>();
                        }
                    }
                } else {
                    sentList.add(attachList.get(i));
                    i++;
                }
            }
        }
        if (attachFileList.size() > 0) {
            GMail androidEmail = new GMail(fromEmail, fromPassword,
                    (List) toEmailList, emailSubject, emailBody,
                    (List) attachFileList);
            try {
                androidEmail.createEmailMessage();
            } catch (AddressException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            try {
                androidEmail.sendEmail(context);
            } catch (MessagingException e) {
                e.printStackTrace();
            } finally {
                for (int i = 0; i < sentList.size(); i++) {
                    // attaDchataSource.setImageSyncFlag(SyncType.email,
                    // sentList.get(i));
                    sentList.remove(i);
                }
                if (dbAttached) {
                    dbAttached = false;
                }
                ret = 1;
            }
        }
        return ret;

    }

    public static double getRealDeviceSizeInInches(Activity context) {
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);


        // since SDK_INT = 1;
        int mWidthPixels = displayMetrics.widthPixels;
        int mHeightPixels = displayMetrics.heightPixels;

        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                mWidthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                mHeightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Exception ignored) {
            }
        }

        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
                mWidthPixels = realSize.x;
                mHeightPixels = realSize.y;
            } catch (Exception ignored) {
            }
        }
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(mWidthPixels / dm.xdpi, 2);
        double y = Math.pow(mHeightPixels / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        Log.d("debug", "Screen inches : " + screenInches);
        return screenInches;
    }

    // chops a list into non-view sublists of length L
    public static <String> List<List<String>> getChopped(List<String> list, final int L) {
        List<List<String>> parts = new ArrayList<List<String>>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<String>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }

    // chops a list into non-view sublists of length L
    public static <T> List<List<T>> getBatchlist(List<T> list, final int L) {
        List<List<T>> parts = new ArrayList<>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }


    public static int randInt(int min, int max) {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;
        System.out.println("Random No." + randomNum);
        return randomNum;
    }


    public static void appendLog(String text) {
//        if (GlobalStrings.CAPTURE_LOG) {//09-Jan-17 Capture Log is ENABLED
        String root = Environment.getExternalStorageDirectory().toString();
        // String demo_path=root+GlobalStrings.DEMO_IMAGE_PATH;
        String logFile_path = root + GlobalStrings.LOG_FILE_PATH + LogfileName + ".txt";
        File logFile = new File(logFile_path);
        if (!logFile.isFile()) {
            try {
                Util.createFolder(root + GlobalStrings.LOG_FILE_PATH);
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error in creation of Log File");
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error in writting a Log File");
        }
    }

    public static void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    public static boolean delete_All_Log() {
        String path;

        String root = Environment.getExternalStorageDirectory().toString();
        path = root + GlobalStrings.LOG_FILE_PATH;
        File logFile_DIR = new File(path);
        deleteRecursive(logFile_DIR);

        return true;
    }

    public static boolean deleteAllInternalImageFolders(Context context) {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
            return false;

        String internalDir = Util.getExternalDataDirPath(context);

        String imageDir = internalDir + File.separator + Environment.DIRECTORY_PICTURES
                + File.separator;
        File image = new File(imageDir);
        deleteRecursive(image);

        String fileFolderDir = internalDir + GlobalStrings.FILEFOLDER;
        File fileFolder = new File(fileFolderDir);
        deleteRecursive(fileFolder);

        String fileFolderPdfDir = internalDir + File.separator + Environment.DIRECTORY_PICTURES
                + File.separator;
        File fileFolderPdf = new File(fileFolderPdfDir);
        deleteRecursive(fileFolderPdf);

        return true;
    }


    public static String RoundUpto_AfterDecimal(double inputNum, int digitAfterDecimal) {

        Log.i(TAG, "RoundUpto_AfterDecimal() I/P:" + inputNum + ", Round Upto decimal:" + digitAfterDecimal);

        String decFormat;
        if (digitAfterDecimal <= 0) {
            decFormat = "#";
        } else {
            decFormat = "#.";
        }
        for (int i = 0; i < digitAfterDecimal; i++) {
            decFormat = decFormat + "#";
        }

//        System.out.println("Decimal format:"+decFormat);
        DecimalFormat df = new DecimalFormat(decFormat);
        df.setRoundingMode(RoundingMode.CEILING);


        String outputNo = df.format(inputNum);
        Log.i(TAG, "RoundUpto_AfterDecimal() Result:" + outputNo);
        return outputNo;
    }

    public static void WaterMarkPhoto(Context mContext, String fileName, String waterMarkString) {
        Bitmap scaledBitmap = BitmapFactory.decodeFile(fileName);
        int y_start_position = 0;
        double width = 0, height = 0;
        width = scaledBitmap.getWidth();
        height = scaledBitmap.getHeight();
        int textSize;
        if ((int) height < 550) {
            y_start_position = (int) (height - 40);

        } else {
            y_start_position = (int) (height - (height * 0.045));
        }
        Log.i(TAG, "WaterMarkPhoto() WaterMark start position:" + y_start_position);
        //06-Apr-17 Add Water mark

        Point pont = new Point();
        pont.x = 20;
        pont.y = y_start_position;//1% of total height is reduced and is a start point

        ScreenReso application = new ScreenReso();
        application.getScreenReso(mContext);
        int density = (int) application.getDensity();
        if (density >= 240) {
            if ((int) height < 550) {
                textSize = 13;
            } else {
                textSize = 10; //15
            }
        } else {
            textSize = 10;
        }

        Log.i(TAG, "WaterMarkPhoto() Water mark text size:" + textSize);
        //height<500?(int) (height*0.020)*density:(int) (height*0.011)*density
        scaledBitmap = CreateWaterMark(mContext, scaledBitmap, waterMarkString, pont, Color.WHITE, 100, textSize, false);


        Log.i(TAG, "Point x=" + pont.x + " y=" + pont.y);
//        Log.i(TAG, "WaterMarkPhoto() Water mark=" + waterMarkString);
        FileOutputStream out = null;
        // String filename = getFilename();

        File outfile = new File(fileName);
        try {

            out = new FileOutputStream(outfile);
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "WaterMarkPhoto() error:" + e.getMessage());
        }

    }

    public static Bitmap CreateWaterMark(Context context, Bitmap src, String watermark, Point location, int color, int alpha, int textsize, boolean underline) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        String[] values = watermark.split("\\|");
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);

        Log.i(TAG, "Water-Mark : " + values[0] + "\n" + values[1] + "\n" + values[2]);

        //method 2
        TextView textView = new TextView(context);
        textView.layout(0, 0, w, h); //text box size 300px x 500px

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
        textView.setTextColor(color);
        textView.setBackgroundResource(R.drawable.textview_shader_bg);
        textView.setPadding(20, 5, 5, 15);

        if (values[0] == null || values[0].isEmpty()) {
            values[0] = getSharedPreferencesProperty(context, GlobalStrings.CURRENT_LOCATIONNAME);
        }
        textView.setText(values[0] + "\n" + values[1] + "  " + values[2]);
        textView.setDrawingCacheEnabled(true);

        try {
            canvas.drawBitmap(textView.getDrawingCache(), 0, location.y, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static int getRandomNumberInRange(int low, int high) {
        Random r = new Random();
        int Low = low;
        int High = high;
        int Result = r.nextInt(High - Low) + Low;
        return Result;
    }

    public static void isPlayServicesAvailable(Context context) {

        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (status == ConnectionResult.SUCCESS) {
            //Success! Do what you want
            Log.i(TAG, "Google Play Service is Available...");
        } else {
            GoogleApiAvailability.getInstance().getErrorDialog((Activity) context, status, 1).show();
        }
    }

    //TODO METHODS FOR DISK LRU CACHE STORAGE
    public static final int IO_BUFFER_SIZE = 8 * 1024;

    public static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    public static File getExternalCacheDir(Context context) {
        if (hasExternalCacheDir()) {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static Bitmap correctBitmapRotation(String path) {
        ExifInterface exif = null;
        Bitmap adjustedBitmap = null;
        try {
            exif = new ExifInterface(path);

            Bitmap sourceBitmap = BitmapFactory.decodeFile(path, new BitmapFactory.Options());

            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);
            Matrix matrix = new Matrix();
            if (rotation != 0) {
                matrix.preRotate(rotationInDegrees);
            }

            adjustedBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(),
                    sourceBitmap.getHeight(), matrix, true);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return adjustedBitmap;
    }

    public static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 1;
    }

    public static File createCSVFileInStorage(String text) {

        File filePath = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                GlobalStrings.DOCUMENT_STORAGE_DIR);

        File file = new File(filePath, "qnopy_sheet_" + System.currentTimeMillis() + ".csv");
        if (!file.exists()) {
            file.mkdir();
        }

        try {
            FileWriter writer = new FileWriter(file);
            writer.append(text);
            writer.flush();
            writer.close();
            Log.e("csv file saved", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    public static String writeFileExternalStorage(String data) {
        String state = Environment.getExternalStorageState();
        //external storage availability check
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return "";
        }

        String fileName = "qnopy_sheet_" + System.currentTimeMillis() + ".csv";

        String dirPath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/"
                + "Qnopy docs";

        File dir = new File(dirPath);
        if (!dir.exists())
            dir.mkdir();

        File file = new File(dirPath, fileName);

        FileOutputStream outputStream = null;
        try {
            file.createNewFile();
            //second argument of FileOutputStream constructor indicates whether to append or create new file if one exists
            outputStream = new FileOutputStream(file, true);

            outputStream.write(data.getBytes());
            outputStream.flush();
            outputStream.close();
            Log.e("csv file saved", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }

    public static String getMainBaseDirPath(Context context) {
        String baseDir = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            baseDir = Util.getExternalDataDirPath(context);
            baseDir = baseDir + File.separator + Environment.DIRECTORY_PICTURES
                    + File.separator;
        } else {
            baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .getAbsolutePath();
        }

        return baseDir;
    }

    public static String writeCopiedFormData(String data, String siteName, String locationName) {
        String state = Environment.getExternalStorageState();
        //external storage availability check
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return "";
        }

        String fileName = siteName + "_" + locationName + "_"
                + getFormattedDateFromMilliS(System.currentTimeMillis(),
                GlobalStrings.DATE_FORMAT_MM_DD_YYYY_MIN_24HR) + ".json";

        String dirPath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/"
                + GlobalStrings.FOLDER_QNOPY_DOCS + "/" + GlobalStrings.FOLDER_TEMPLATES;

        File dir = new File(dirPath);
        if (!dir.exists())
            dir.mkdir();

        File file = new File(dirPath, fileName);

        FileOutputStream outputStream = null;
        try {
            file.createNewFile();
            //second argument of FileOutputStream constructor indicates whether to append or create new file if one exists
            outputStream = new FileOutputStream(file, true);

            outputStream.write(data.getBytes());
            outputStream.flush();
            outputStream.close();
            Log.e("csv/json file saved", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }

    public static String readJson(String filePath) {

        String ret = "";

        try {
            FileInputStream inputStream = new FileInputStream(new File(filePath));

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("Json File", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("json File", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static ArrayList<String> getFolderData(String filesPath) {
        ArrayList<String> locationList = new ArrayList<>();

/*
        String path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/"
                + GlobalStrings.FOLDER_QNOPY_DOCS + "/" + GlobalStrings.FOLDER_TEMPLATES + "/";

        Log.d("Files", "Path: " + path);
*/

        try {
            File f = new File(filesPath);
            File[] file = f.listFiles();

            if (file != null) {
                Log.d("Files", "Size: " + file.length);
                // adding header names to list i.e location data
                for (File value : file) {
                    Log.d("Files", "FileName:" + value.getName());
//                    if (value.getName().endsWith(".json")) {
                    String filePath = filesPath + value.getName();
                    locationList.add(filePath);
//                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return locationList;
    }

}
