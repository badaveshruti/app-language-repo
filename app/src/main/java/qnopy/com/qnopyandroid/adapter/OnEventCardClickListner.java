package qnopy.com.qnopyandroid.adapter;

import android.view.View;

public interface OnEventCardClickListner{
    public void onEventCardClick(View view, int position, int appId, int eventId, int siteId, String siteName);
}
