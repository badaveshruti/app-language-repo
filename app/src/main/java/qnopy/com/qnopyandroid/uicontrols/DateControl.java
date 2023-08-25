package qnopy.com.qnopyandroid.uicontrols;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.widget.AppCompatButton;

import java.util.Calendar;

public class DateControl extends AppCompatButton implements Button.OnClickListener {

    Context ObjContext;
    public String gText;
    public TextView self = this;

    public DateControl(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        ObjContext = context;
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        System.out.println("MMM" + "DateControlOnClidk");
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(ObjContext,
                (timePicker, selectedHour, selectedMinute) -> {
                    gText += selectedHour + ":"
                            + selectedMinute;
                    System.out.println("MMM" + selectedHour
                            + selectedMinute);
                    self.setText(gText);
                }, hour, minute, false);// Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }
}
