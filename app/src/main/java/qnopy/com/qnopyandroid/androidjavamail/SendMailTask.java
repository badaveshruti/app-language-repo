package qnopy.com.qnopyandroid.androidjavamail;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

public class SendMailTask extends AsyncTask {

	private ProgressDialog statusDialog;
	private Activity sendMailActivity;

	public SendMailTask(Activity activity) {
		System.out.println("mmmm"+"SendMailTask");
		sendMailActivity = activity;

	}

	protected void onPreExecute() {
		statusDialog = new ProgressDialog(sendMailActivity);
		statusDialog.setMessage("Getting Ready To Send Mail...");
		statusDialog.setIndeterminate(true);
		statusDialog.setCancelable(true);
		statusDialog.show();
	}

	@Override
	protected Object doInBackground(Object... args) {
		try {
			Log.i("SendMailTask", "About to instantiate GMail...");
			publishProgress("Processing input....");
			System.out.println("mmmm"+"GMail");
			GMail androidEmail = new GMail(args[0].toString(),
					args[1].toString(), (List) args[2], args[3].toString(),
					args[4].toString(), (List)args[5]);
			publishProgress("Preparing mail message....");
			androidEmail.createEmailMessage();
			publishProgress("Sending email....");
			androidEmail.sendEmail(sendMailActivity);
			publishProgress("Email Sent.");
			Log.i("SendMailTask", "Mail Sent.");
		} catch (Exception e) {
			publishProgress(e.getMessage());
			
			Log.e("SendMailTask", e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void onProgressUpdate(Object... values) {
		statusDialog.setMessage(values[0].toString());

	}

	@Override
	public void onPostExecute(Object result) {
		statusDialog.dismiss();
	}

}
