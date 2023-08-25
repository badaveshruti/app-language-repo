package qnopy.com.qnopyandroid.uicontrols;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.Toast;

public class CustomToast {
	public static void showToast (Activity context, String msg, int duration) {
		LayoutInflater inflater = context.getLayoutInflater();
//		View layout = inflater.inflate(R.layout.custom_toast,
//		                               (ViewGroup) context.findViewById(R.id.toast_layout_root));
////		TextView text1 = (TextView) layout.findViewById(R.id.text);
////		text1.setText(msg);
//
//		Toast toast = new Toast(context);
////		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//
//		toast.setDuration(duration);
////		toast.setView(layout);
//		toast.setText(msg);
//		toast.show();

		Toast toast = Toast.makeText(context,msg,duration);
		//toast.setGravity(Gravity.CENTER,Gravity.BOTTOM, 0);
		toast.show();
	}
}
