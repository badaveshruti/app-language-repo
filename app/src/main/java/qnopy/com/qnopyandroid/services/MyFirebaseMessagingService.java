package qnopy.com.qnopyandroid.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.NotificationRow;
import qnopy.com.qnopyandroid.db.NotificationsDataSource;
import qnopy.com.qnopyandroid.ui.activity.MobileReportRequiredActivity;
import qnopy.com.qnopyandroid.ui.activity.NotificationActivity;
import qnopy.com.qnopyandroid.ui.activity.SplashScreenActivity;
import qnopy.com.qnopyandroid.util.Util;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String TAG = "FirebaseMessagingService";
    //Context context = MyFirebaseInstanceIDService.this;
    int operationCode = 0, currentUser = 0, formID = 0, siteID = 0, eventID = 0;
    private int cocId = 0;
    private String fileName = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        //(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("notification", "Message data payload: " + remoteMessage.getData());

            try {

                if (remoteMessage.getData().containsKey("operationCode")) {
                    operationCode = Integer.parseInt(remoteMessage.getData().get("operationCode"));
                }

                if (remoteMessage.getData().containsKey("userId")) {
                    currentUser = Integer.parseInt(remoteMessage.getData().get("userId"));
                }

                String detail = remoteMessage.getData().get("detail");
                String title = remoteMessage.getData().get("title");

                //String userID  = remoteMessage.getData().get("userId");
                if (remoteMessage.getData().containsKey("formId")) {
                    formID = Integer.parseInt(remoteMessage.getData().get("formId"));
                }
                if (remoteMessage.getData().containsKey("siteId")) {
                    siteID = Integer.parseInt(remoteMessage.getData().get("siteId"));
                }
                if (remoteMessage.getData().containsKey("eventId")) {
                    eventID = Integer.parseInt(remoteMessage.getData().get("eventId"));
                }

                if (remoteMessage.getData().containsKey("fileName")) {
                    fileName = remoteMessage.getData().get("fileName");
                }

                if (remoteMessage.getData().containsKey("cocId")) {
                    cocId = Integer.parseInt(remoteMessage.getData().get("cocId"));
                }
                /*formID = Integer.parseInt(remoteMessage.getData().get("formId"));
                siteID = Integer.parseInt(remoteMessage.getData().get("siteId"));
                eventID = Integer.parseInt(remoteMessage.getData().get("eventId"));*/

                Log.d("notification", "Message data payload: " + currentUser + " formid---" + formID + " siteid----" + siteID + " eventid---- " + eventID);
                NotificationsDataSource nds = new NotificationsDataSource(this);

                String sess = Util.getSharedPreferencesProperty(this, GlobalStrings.IS_SESSION_ACTIVE);
                boolean isSessionActive = false;
                if (sess != null) {
                    isSessionActive = Boolean.parseBoolean(sess);
                }
                Log.e("sessionStatus", "onMessageReceived: " + isSessionActive);

                nds.insertNotifications(new NotificationRow(1, title, detail
                        , isSessionActive ? 0 : 1, System.currentTimeMillis() + "", operationCode,
                        currentUser, formID, siteID, eventID, fileName, cocId));

               /* String userAppType = Util.getSharedPreferencesProperty(this, GlobalStrings.USERAPPTYPE);
                String userId = String.valueOf(currentUser);
                if (userAppType == null){
                    UserDataSource userDataSource = new UserDataSource(this);
                    userAppType = userDataSource.getUserAppType(userId);
                    Util.setSharedPreferencesProperty(this,GlobalStrings.USERAPPTYPE, userAppType);
                }
                Log.e("notificationUserAppType", "onMessageReceived: USERAPPTYPE is- "+userAppType);*/

                sendNotification(detail, title);
                sendMyBroadCast();

//                if (operationCode == GlobalStrings.SUSPEND_USER_OPERATION_CODE) {
//                    Util.setLogout((MainDrawerActivity) GlobalStrings.currentContext);
//                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "ERROR:" + e.getMessage());
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            //Calling method to generate notification
            // sendNotification(remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Get updated InstanceID token.
        //  REFRESH_TOKEN=refreshedToken;
        Log.d(TAG, "Refreshed (FCM) token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        saveRegistrationToken(token);
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void saveRegistrationToken(String token) {
        try {
            if (token != null && !token.trim().isEmpty())
                Util.setSharedPreferencesProperty(GlobalStrings.currentContext, GlobalStrings.NOTIFICATION_REGISTRATION_ID, token);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "saveRegistrationToken() Error:" + e.getMessage());
        }
    }

    public static void generateFireBaseToken(final Context context) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("Generate fcm token", "getInstanceId failed", task.getException());
                        return;
                    }
                    String token = "";
                    // Get new Instance ID token
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            token = Objects.requireNonNull(task.getResult());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e("Generated fcm token", token);

                    String existingToken
                            = Util.getSharedPreferencesProperty(context,
                            GlobalStrings.NOTIFICATION_REGISTRATION_ID);

                    if (existingToken == null || existingToken.trim().isEmpty())
                        Util.setSharedPreferencesProperty(context,
                                GlobalStrings.NOTIFICATION_REGISTRATION_ID, token);
                });
    }

    /**
     * This method is responsible to send broadCast to specific Action
     */
    private void sendMyBroadCast() {
        try {
            Intent broadCastIntent = new Intent();
//            broadCastIntent.setAction(GlobalStrings.BROADCAST_ACTION);

            // uncomment this line if you want to send data
//            broadCastIntent.putExtra("data", "abc");

            sendBroadcast(broadCastIntent);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //This method is only generating push notification

    private void sendNotification(String messageBody, String title) {
//        Intent intent = new Intent(this, NotificationActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_ONE_SHOT);

        // Create an Intent for the activity you want to start

        //24-05-2018 Start an Activity from a Notification (https://developer.android.com/training/notify-user/navigation)
        Intent resultIntent;
        String sess = Util.getSharedPreferencesProperty(this, GlobalStrings.IS_SESSION_ACTIVE);
        boolean isSessionActive = false;
        if (sess != null) {
            isSessionActive = Boolean.parseBoolean(sess);
        }

        if (!isSessionActive) {
            resultIntent = new Intent(this, SplashScreenActivity.class);
        } else {
            resultIntent = new Intent(this, NotificationActivity.class);
        }

        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String CHANNEL_ID = "qnopy_channel_01";// The id of the channel.

      /*  CharSequence name = getString(R.string.channel_name);// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        }*/

        PendingIntent pendingIntentDownload;
        Intent downloadIntent = null;
        if (isSessionActive) {
            SharedPreferences reportParameters = getSharedPreferences("PDF_REPORT_PARAMETERS", Context.MODE_PRIVATE);
            reportParameters.edit().clear().commit();

            SharedPreferences.Editor editor = getSharedPreferences("PDF_REPORT_PARAMETERS", MODE_PRIVATE).edit();
            editor.putString("USER_ID", currentUser + "");
            editor.putString("FORM_ID", formID + "");
            editor.putString("SITE_ID", siteID + "");
            editor.putString("EVENT_ID", eventID + "");
            editor.apply();

            downloadIntent = new Intent(this, MobileReportRequiredActivity.class);

            if (operationCode == GlobalStrings.DOWNLOAD_COC_OPERATION_CODE)
                downloadIntent = new Intent(this, NotificationActivity.class);

            downloadIntent.putExtra("USER_ID", currentUser + "");
            downloadIntent.putExtra("FORM_ID", formID + "");
            downloadIntent.putExtra("SITE_ID", siteID + "");
            downloadIntent.putExtra("EVENT_ID", eventID + "");
            downloadIntent.putExtra("call", "MyFirebaseMessagingService");
            downloadIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Log.e("MyFireBaseMessaging", "sendNotification: " + currentUser + " FFF-- " + formID + " ssss-- " + siteID + " eee-- " + eventID);
        }

        TaskStackBuilder stackBuilderr = TaskStackBuilder.create(this);
        stackBuilderr.addNextIntentWithParentStack(downloadIntent);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntentDownload = PendingIntent.getActivity(MyFirebaseMessagingService.this, 0, downloadIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntentDownload = PendingIntent.getActivity(MyFirebaseMessagingService.this, 0, downloadIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Log.e(TAG, "sendNotification: " + title);
        if (title.equals(getString(R.string.report_generated))) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int notifyID = 1;
                CharSequence name = getString(R.string.channel_name);// The user-visible name of the channel.
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setTicker(getString(R.string.report_gen_download_report))
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)   // heads-up
                        .setWhen(System.currentTimeMillis())
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .addAction(R.drawable.download_forms_icon, getString(R.string.download), pendingIntentDownload)
                        .setContentIntent(pendingIntentDownload)
                        .build();

                mNotificationManager.createNotificationChannel(mChannel);
                mNotificationManager.notify(notifyID, notification);

            } else {
                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setTicker(getString(R.string.report_gen_download_report))
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)   // heads-up
                        .setWhen(System.currentTimeMillis())
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .addAction(R.drawable.download_forms_icon, getString(R.string.download), pendingIntentDownload)
                        .setContentIntent(pendingIntentDownload)
                        .build();

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                if (notificationManager != null) {
                    notificationManager.notify(0, notification);
                }
            }
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setTicker(getString(R.string.channel_name))
                    .setSound(defaultSoundUri)
                    .setContentIntent(resultPendingIntent)
                    .setWhen(System.currentTimeMillis());

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify(0, notificationBuilder.build());
            }
        }
    }
}
