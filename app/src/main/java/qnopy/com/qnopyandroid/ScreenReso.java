package qnopy.com.qnopyandroid;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.HiltAndroidApp;
import qnopy.com.qnopyandroid.clientmodel.User;
import qnopy.com.qnopyandroid.flowWithAdmin.utility.Utils;
import qnopy.com.qnopyandroid.flowWithAdmin.utility.imageCache.DiskLruImageCache;
import qnopy.com.qnopyandroid.util.ImagesCache;
import zendesk.answerbot.AnswerBot;
import zendesk.chat.Chat;
import zendesk.core.AnonymousIdentity;
import zendesk.core.Identity;
import zendesk.core.Zendesk;
import zendesk.support.Guide;
import zendesk.support.Support;

@HiltAndroidApp
public class ScreenReso extends MultiDexApplication {

    //This boolean is used to know whether the refresh button of Calendar frag,
    //Site frag and Submittals(Event) frag is clicked so that in onResume event api shouldn't
    //get hit every time
    public static boolean isDownloadData;
    private float density;
    private int textSize;
    String TAG = "ScreenReso";
    public static boolean isLimitedUser;
    public static boolean isProjectUser;
    public static boolean isCalendarUser;
    public static ImagesCache mImageCache;
    public static boolean isMobile2POINT0;
    public static DiskLruImageCache diskLruCache = null;

    public static User userDetails = null;

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(base);
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        mImageCache = ImagesCache.getInstance();
        mImageCache.initializeCache();

        Utils.INSTANCE.initDiskCache(this);
        initZendesk();
        initFirebaseAppCheck();
    }

    private void initFirebaseAppCheck() {
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());
    }

    private void initZendesk() {

        final String zendesk_url = "https://qnopy.zendesk.com";
        final String zendesk_app_id = "9c4541fa53990b20184aa322ac4d1118698adb1061b77427";
        final String zendesk_oauth_id = "mobile_sdk_client_f8a1f12fb7243e9228fa";
        final String zendesk_acc_key = "sW0JuDgdOEZKaCz06VYn4Fz8XUV5uVoW";

        Zendesk.INSTANCE.init(this, zendesk_url, zendesk_app_id, zendesk_oauth_id);
        Identity identity = new AnonymousIdentity();
        Zendesk.INSTANCE.setIdentity(identity);

        Support.INSTANCE.init(Zendesk.INSTANCE);
        Guide.INSTANCE.init(Zendesk.INSTANCE);
        AnswerBot.INSTANCE.init(Zendesk.INSTANCE, Guide.INSTANCE);
        Chat.INSTANCE.init(this, zendesk_acc_key, zendesk_app_id);
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void getScreenReso(Context context) {
        density = context.getResources().getDisplayMetrics().densityDpi;
        Log.i(TAG, "getScreenReso() Screen Density=" + density);
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }
}
