package qnopy.com.qnopyandroid.uiutils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import qnopy.com.qnopyandroid.interfacemodel.AlertButtonOnClick;

public class Alert extends AlertDialog {
	Context objContext = null;
	String msg = null;
	String title = null;
	AlertButtonOnClick listener;
	String pos = null;
	String neg;
	
	void setApplicationContext(Context context) {
		this.objContext = context;
	}
	
	Context getApplicationContext() {
		return this.objContext;
	}

	public Alert(AlertButtonOnClick listener, String title, String msg, Context context, String pos, String neg) {
		super (context);
		setApplicationContext((Context) listener);
		this.msg = msg;
		this.listener = listener;
		this.title = title;
		this.pos = pos;
		this.neg = neg;
	}
	
	public void showAlert() {
		AlertDialog alert = this;
		Builder alertBuilder = new Builder(objContext);
		alertBuilder.setMessage(msg);
		alertBuilder.setTitle(title);
		alertBuilder.setPositiveButton(pos, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				listener.positiveButtonClick();
			}
		});
		
		alertBuilder.setNegativeButton(neg, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		alert = alertBuilder.create();
		alert.show();
	}

}
