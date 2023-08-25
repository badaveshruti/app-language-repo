/*
 * This class converts the fetched time which is in 24hrs format to 12hrs format just
 * to display and store the time in 24hr in db that is needed by server
 */
package qnopy.com.qnopyandroid.customWidgets;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import qnopy.com.qnopyandroid.clientmodel.MetaData;
import qnopy.com.qnopyandroid.uiutils.FormMaster;
import qnopy.com.qnopyandroid.util.Util;

/**
 * @author PatelSanket
 */
public class FBTimeView extends androidx.appcompat.widget.AppCompatTextView {

    Context ObjectContext;

    FormMaster form_master_context;

    String displayTime = null;

    public String AM_PM;
    private String blankTime = null;

    public FBTimeView(Context context) {
        super(context);
        ObjectContext = context;
    }

    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }

    public void setFormMasterContext(FormMaster context) {
        this.form_master_context = context;
    }

    public void calculateTime() {
        int hour = 0, minute = 0;
        Calendar mcurrentTime = Calendar.getInstance();
        if ((displayTime == null) || (displayTime.length() == 0)) {

            hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            minute = mcurrentTime.get(Calendar.MINUTE);
            displayTime = String.format("%02d", hour) + ":" + String.format("%02d", minute);
/*
            if (hour < 12) {
                setAM_PM("AM");
            } else {
                setAM_PM("PM");
            }
*/
            displayTime = Util.get12hrFormatTime(displayTime);

            if (getBlankTime() == null || getBlankTime().equalsIgnoreCase("current")
                    || getBlankTime().isEmpty()) {
                this.setText(getDisplayTime());
            } else if (getBlankTime().equalsIgnoreCase("blank")) {
                this.setText("Click here to set");
                form_master_context.blankTimeValue = false;
            }
        } else if (displayTime.contains(":")) {
            displayTime = Util.get12hrFormatTime(displayTime);

                /*String[] hhmm = displayTime.split(":");

                if (hhmm.length == 2) {
                    String hh = hhmm[0];
                    String mm = hhmm[1];
                    hour = Integer.parseInt(hh);
                    minute = Integer.parseInt(mm);
                }
                if (hour < 12) {
                    setAM_PM("AM");
                } else {
                    setAM_PM("PM");
                }*/
            if (form_master_context.blankTimeValue) {
                if (getBlankTime() == null || getBlankTime().equalsIgnoreCase("current")) {
                    this.setText(getDisplayTime());
                } else if (getBlankTime().equalsIgnoreCase("blank")) {
                    this.setText("Click here to set");
                    form_master_context.blankTimeValue = false;
                }
            } else {
                this.setText(getDisplayTime());
            }
        } else if (getBlankTime().equalsIgnoreCase("blank")
                || getDisplayTime().equalsIgnoreCase("blank")) {
            this.setText("Click here to set");
            form_master_context.blankTimeValue = false;
        }
    }

    public void setListeners(final MetaData metaData) {
        final FBTimeView timeView = this;
        timeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View viewIn) {

                final View tempView = viewIn;
                Calendar mcurrentTime = Calendar.getInstance();
                final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                final int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ObjectContext,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view,
                                                  int selHour, int selMinute) {

                                //String AM_PM ;
/*                                if (selHour < 12) {
                                    setAM_PM("AM");
                                } else {
                                    setAM_PM("PM");
                                }*/
                                String timeString = String.format("%02d", selHour) + ":" + String.format("%02d", selMinute);

                    /*             gText = selHour + ":" +
                                        String.format("%02d", selMinute) + " " +
                                        getAM_PM();
*/
                                String gText = Util.get12hrFormatTime(timeString);

                                TextView text1 = (TextView) tempView;
                                text1.setText(gText);

                                Calendar mCurrentTime = Calendar.getInstance();
                                mCurrentTime.set(Calendar.HOUR_OF_DAY, selHour);
                                mCurrentTime.set(Calendar.MINUTE, selMinute);

                                setDisplayTime(timeString);

                                if (metaData.getMetaParamID() != 15 || metaData.getMetaParamID() != 25) {
                                    if (metaData.getMetaParamLabel().trim().equalsIgnoreCase("Sample Time"))
                                        form_master_context.isSampleDateOrTimeSet = true;
                                }

                                form_master_context.handleDateAndTimeData(metaData, timeString, form_master_context.getCurrentSetID());
//                                form_master_context.updateTime(value);
                            }
                        }, hour, minute, true);// Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
    }

    public long getTimeLong() {
        String mTime = getDisplayTime();
        long time = 0;
        long hr = 0;
        long min = 0;
        if (mTime.contains(":")) {
            String[] hhmm = mTime.split(":");

            if (hhmm.length == 2) {
                String hh = hhmm[0];
                String mm = hhmm[1];
                hr = Integer.parseInt(hh);
                min = Integer.parseInt(mm);
                hr = hr * 3600 * 1000;
                min = min * 60 * 1000;
                time = hr + min;
            }
        }
        return time;
    }

    public String getDisplayTime() {
        return displayTime;
    }

    public String getAM_PM() {
        return AM_PM;
    }

    public void setAM_PM(String aM_PM) {
        AM_PM = aM_PM;
    }

    public String getBlankTime() {
        return blankTime;
    }

    public void setBlankTime(String blankTime) {
        this.blankTime = blankTime;
    }
}
