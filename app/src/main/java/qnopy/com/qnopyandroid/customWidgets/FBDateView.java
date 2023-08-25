package qnopy.com.qnopyandroid.customWidgets;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.MetaData;
import qnopy.com.qnopyandroid.uiutils.FormMaster;

public class FBDateView extends androidx.appcompat.widget.AppCompatTextView {

    Context mContext;

    FormMaster formMasterContext;

    private String displayDate = null;

    private String blankDate = null;

    public FBDateView(Context context) {
        super(context);
        mContext = context;
    }

    public void setDisplayDate(String displayDate) {
        this.displayDate = displayDate;
    }

    public FormMaster getFormMasterContext() {
        return formMasterContext;
    }

    public void setFormMasterContext(FormMaster context) {
        this.formMasterContext = context;
    }

    public void calculateDate() {
        int day = 0, month = 0, year = 0;
        Calendar mcurrentDate = Calendar.getInstance();

        if ((getDisplayDate() == null) || (getDisplayDate().isEmpty())) {
            day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
            month = mcurrentDate.get(Calendar.MONTH);
            year = mcurrentDate.get(Calendar.YEAR);
            String dateString = String.format("%02d", month + 1) + "/" + String.format("%02d", day) + "/" + String.format("%02d", year);
            Log.i("FBDateView", "calculateDate()  :" + mcurrentDate + " " + getDisplayDate());
            setDisplayDate(dateString);

            if (getBlankDate() == null || getBlankDate().equalsIgnoreCase("current") || getBlankDate().isEmpty() || getBlankDate().equals("")) {
                this.setText(getDisplayDate());
            } else if (getBlankDate().equalsIgnoreCase("blank")) {
                this.setText(mContext.getString(R.string.click_here_to_set));
                formMasterContext.blankDateValue = false;
            }
        } else if (getDisplayDate().equalsIgnoreCase("blank")) {
            this.setText(mContext.getString(R.string.click_here_to_set));
            formMasterContext.blankDateValue = false;
        } else {
            if (formMasterContext.blankDateValue) {
                if (getBlankDate() == null || getBlankDate().equalsIgnoreCase("current")
                        || getBlankDate().isEmpty()) {
                    this.setText(getDisplayDate());
                } else if (getBlankDate().equalsIgnoreCase("blank")) {
                    this.setText(mContext.getString(R.string.click_here_to_set));
                    formMasterContext.blankDateValue = false;
                }
            } else {
                this.setText(getDisplayDate());
            }
        }
    }

    public String getDisplayDate() {
        return displayDate;
    }

    public long getDateLong() {
        String mDate = getDisplayDate();
        long date = 0;
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        try {
            java.util.Date d = (java.util.Date) format.parse(mDate);
            date = d.getTime();
            System.out.println("time.....d" + date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public void setListeners(final MetaData metaData) {
        final FBDateView dateView = this;
        dateView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View viewIn) {

                final View tempView = viewIn;
                Calendar mcurrentTime = Calendar.getInstance();
                int date = mcurrentTime.get(Calendar.DAY_OF_MONTH);
                int month = mcurrentTime.get(Calendar.MONTH);
                int year = mcurrentTime.get(Calendar.YEAR);
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        TextView tect1 = (TextView) tempView;
                        String gText = String.format("%02d", monthOfYear + 1) + "/" +
                                String.format("%02d", dayOfMonth) + "/" +
                                String.format("%02d", year);
                        tect1.setText(gText);

                        Calendar mcurrentTime = Calendar.getInstance();
                        mcurrentTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        mcurrentTime.set(Calendar.MONTH, monthOfYear);
                        mcurrentTime.set(Calendar.YEAR, year);

                        String keyStr = tempView.getTag() + "";

                        String storingDate = String.format("%02d", monthOfYear + 1) + "/"
                                + String.format("%02d", dayOfMonth) + "/" + String.format("%02d", year);

                        setDisplayDate(storingDate);
                        long value = getDateLong();

                        if (metaData.getMetaParamID() != 15 || metaData.getMetaParamID() != 25) {
                            if (metaData.getMetaParamLabel().trim().equalsIgnoreCase("Sample Date"))
                                formMasterContext.isSampleDateOrTimeSet = true;
                        }

                        formMasterContext.handleDateAndTimeData(metaData, storingDate,
                                formMasterContext.getCurrentSetID());
                    }
                }, year, month, date);

                mDatePicker.setTitle("Select Date");
                mDatePicker.show();

            }
        });
    }

    public String getBlankDate() {
        return blankDate;
    }

    public void setBlankDate(String blankDate) {
        this.blankDate = blankDate;
    }
}
