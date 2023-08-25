
package qnopy.com.qnopyandroid.ui.activity;

public class CheckingUpdates {

}
/*
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;

public class CheckingUpdates extends AsyncTask {

    private static final String TAG = "CheckingUpdates";
    boolean updateAvailable = false;
    private int updateFlag = 0;
    private String username = null;
    private String passwd = null;
    int protocolVersion = 0;
    String url = null;
    //    final File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/temp.apk");
    File apkFile;
    ProgressDialog procDialog = null;
    Context objContext = null;
    // private OnTaskCompleted listener;
    int version = 0;
    String label = null;

    public void setApplicationContext(Context context) {
        objContext = context;
    }

    private Context getApplicationContext() {
        return objContext;
    }

    public CheckingUpdates(Context context, String username, String passwd, int flag, int version) {
        setApplicationContext(context);

        this.setUsername(username);
        this.setPasswd(passwd);
        setUpdateFlag(flag);

        this.version = version;
        protocolVersion = GlobalStrings.protocolVersion;
        url = objContext.getResources().getString(R.string.upgrade_base_uri);
        url += "?ver=" + this.version + "&package=" + context.getPackageName() + "&user=" + getUsername() + "&pass=" + getPasswd() + "&pVer=" + protocolVersion + "&q=d";

        File externalRootDir = Environment.getExternalStorageDirectory();
        boolean success = new File(externalRootDir, ".FieldBuddy").mkdir();
        File tempDir = new File(externalRootDir, ".FieldBuddy");
        apkFile = new File(tempDir, GlobalStrings.tempApkName);

        System.out.println("apkFile path=" + apkFile.getAbsolutePath());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        startProcDialog();
    }

    @Override
    protected Object doInBackground(Object... arg0) {
        String strFinal = null;
        if (getUpdateFlag() == 1) {
            HttpResponse response = null;
            try {

                HttpGet request = new HttpGet();

                HttpParams httpParameters = new BasicHttpParams();
                int connectionTimeout = 5000;
                HttpConnectionParams.setConnectionTimeout(httpParameters, connectionTimeout);
                int socketTimeout = 5000;
                HttpConnectionParams.setSoTimeout(httpParameters, socketTimeout);

                HttpClient httpClient = new DefaultHttpClient(httpParameters);

                strFinal = objContext.getResources().getString(R.string.upgrade_base_uri);
                strFinal += "?ver=" + this.version + "&user=" + getUsername() + "&pass=" + getPasswd() + "&pVer=" + protocolVersion + "&q=c";
                Log.i(TAG, "Checking Update Url:" + strFinal);
                request.setURI(new URI(strFinal));
                response = httpClient.execute(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String responseText = null;

            try {
                if (response != null) {
                    responseText = EntityUtils.toString(response.getEntity());
                    JSONObject json = new JSONObject(responseText);
                    updateAvailable = json.getBoolean("Update");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (getUpdateFlag() == 2) {
            downloadFile(url, apkFile);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object fn) {
        remProcDialog();
        if (getUpdateFlag() == 1 && updateAvailable == true) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
            alert.setMessage("Application Update Available..\n\n   Do you want to Update?");
            alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    setUpdateFlag(2);
                    CheckingUpdates check = new CheckingUpdates(getApplicationContext(), getUsername(), getPasswd(), 2, version);
                    check.execute();
                }
            });

            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    //listener.onTaskCompleted();
                }
            });

            AlertDialog dialog = alert.create();
            dialog.show();

        } else if (getUpdateFlag() == 1 && updateAvailable == false) {

            //listener.onTaskCompleted();
            Toast.makeText(objContext, objContext.getString(R.string.updated_app_alert), Toast.LENGTH_LONG).show();
            return;
        } else if (getUpdateFlag() == 2) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            objContext.startActivity(intent);

        }
    }

    void startProcDialog() {
        if (getUpdateFlag() == 1) {
            procDialog = ProgressDialog.show(getApplicationContext(), "Check Updates", "Please wait ...", true);
        } else {
            procDialog = ProgressDialog.show(getApplicationContext(), "Updating Application", "Please wait ...", true);
        }
        procDialog.setCancelable(false);
    }

    void remProcDialog() {
        if ((procDialog != null) && (procDialog.isShowing())) {
            System.out.println("kkk" + "Dismissing dialog");
            try {
                procDialog.dismiss();
            } catch (Exception e) {
                System.out.println("kkkk" + "dismiss" + e.getLocalizedMessage());
            }
        }
    }

    public static void downloadFile(String url, File outputFile) {
        try {
            URL u = new URL(url);
            URLConnection conn = u.openConnection();
            int contentLength = conn.getContentLength();

            DataInputStream stream = new DataInputStream(u.openStream());

            byte[] buffer = new byte[contentLength];
            stream.readFully(buffer);
            stream.close();

            DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
            fos.write(buffer);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return; // swallow a 404
        }
    }

    int getUpdateFlag() {
        return updateFlag;
    }

    void setUpdateFlag(int updateFlag) {
        this.updateFlag = updateFlag;
    }

    String getUsername() {
        return username;
    }

    void setUsername(String username) {
        this.username = username;
    }

    String getPasswd() {
        return passwd;
    }

    void setPasswd(String passwd) {
        this.passwd = passwd;
    }

}
*/
