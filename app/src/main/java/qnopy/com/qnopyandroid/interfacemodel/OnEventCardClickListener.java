package qnopy.com.qnopyandroid.interfacemodel;

import android.view.View;

import qnopy.com.qnopyandroid.clientmodel.EventData;

public interface OnEventCardClickListener {
    void onEventCloseClick(View view, int position, EventData event);

    void onEmailLogsClicked(EventData event);

    void onEventCardClicked(EventData event);

    void onDownloadDataClicked(EventData event);
}
