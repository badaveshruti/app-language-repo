package qnopy.com.qnopyandroid.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.NotificationRow;
import qnopy.com.qnopyandroid.db.NotificationsDataSource;
import qnopy.com.qnopyandroid.ui.activity.MetaSyncActivity;
import qnopy.com.qnopyandroid.ui.activity.MobileReportRequiredActivity;
import qnopy.com.qnopyandroid.ui.activity.NotificationActivity;
import qnopy.com.qnopyandroid.util.Util;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    List<NotificationRow> notificationObjects;
    private Context objContext;
    String mUserAppType;
    OnNotificationClickListener mListener;

    public NotificationAdapter(List<NotificationRow> notificationObjects, Context objContext, OnNotificationClickListener listener) {
        this.notificationObjects = notificationObjects;
        this.objContext = objContext;
        this.mListener = listener;
        mUserAppType = Util.getSharedPreferencesProperty(this.objContext, GlobalStrings.USERAPPTYPE);
    }

    public interface OnNotificationClickListener {
        void onNotificationClicked(NotificationRow notification);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_row, parent, false);
        return new NotificationAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NotificationRow item = notificationObjects.get(position);
        holder.currentItem = item;
        Long date = Long.parseLong(item.date);

        String notiDate = Util.formatToYesterdayOrToday(date, objContext);
        holder.Titletxt.setText(item.title);
        holder.infotxt.setText(item.info);
        holder.datetxt.setText(notiDate);

        Typeface typeRegular = Typeface.createFromAsset(objContext.getAssets(), "fonts/Roboto-Regular.ttf");
//        Typeface typeBold = Typeface.createFromAsset(ObjContext.getAssets(), "fonts/Roboto-Bold.ttf");

        holder.infotxt.setTypeface(typeRegular);

        if (item.status == 1) {
            holder.Titletxt.setTypeface(typeRegular);
            holder.datetxt.setTypeface(typeRegular);
            holder.datetxt.setTextColor(objContext.getResources().getColor(R.color.half_black));
        } else {
            holder.Titletxt.setTypeface(null, Typeface.BOLD);
            holder.datetxt.setTypeface(null, Typeface.BOLD);
            holder.datetxt.setTextColor(objContext.getResources().getColor(R.color.qnopy_teal));
        }
    }

    @Override
    public int getItemCount() {
        return notificationObjects.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView Titletxt, infotxt, datetxt;
        public NotificationRow currentItem = null;

        public ViewHolder(View itemView) {
            super(itemView);

            Titletxt = (TextView) itemView.findViewById(R.id.title_tv);
            infotxt = (TextView) itemView.findViewById(R.id.details_tv);
            datetxt = (TextView) itemView.findViewById(R.id.date_tv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentItem != null && currentItem.status == 0) {
                        final NotificationsDataSource nds = new NotificationsDataSource(objContext);

                        if (currentItem.operationCode == 0 || currentItem.operationCode == GlobalStrings.DOWNLOAD_EVENT_OPERATION_CODE || currentItem.operationCode == GlobalStrings.DOWNLOAD_FORMS_OPERATION_CODE) {
                            /*if (mUserAppType.equals("sNote")){
                                Intent intent = new Intent(ObjContext, MetaSyncActivityConstructionApp.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                ObjContext.startActivity(intent);
                                //ConstructionDrawerActivity.mConstructionDrawer.finish();
                            }else {
                                ObjContext.startActivity(new Intent(ObjContext, MetaSyncActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            }*/
                            objContext.startActivity(new Intent(objContext, MetaSyncActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            String update_codes = "0," + GlobalStrings.DOWNLOAD_EVENT_OPERATION_CODE + "," + GlobalStrings.DOWNLOAD_FORMS_OPERATION_CODE;
                            nds.updateAllDownloadFormNotifcationStatus(update_codes, currentItem.userID + "");

                            ((NotificationActivity) objContext).finish();
                        } else if (currentItem.operationCode == GlobalStrings.CHANGE_PASSWORD_OPERATION_CODE || currentItem.operationCode == GlobalStrings.SUSPEND_USER_OPERATION_CODE) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(objContext);
                            builder.setTitle(currentItem.title).setMessage(currentItem.info).setCancelable(false).setPositiveButton(objContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String update_codes = "0";
                                    update_codes = "0," + GlobalStrings.SUSPEND_USER_OPERATION_CODE + "," + GlobalStrings.CHANGE_PASSWORD_OPERATION_CODE + "," + GlobalStrings.DOWNLOAD_EVENT_OPERATION_CODE + "," + GlobalStrings.DOWNLOAD_FORMS_OPERATION_CODE;

//                                            if (currentItem.operationCode == GlobalStrings.CHANGE_PASSWORD_OPERATION_CODE) {
//                                                update_codes = update_codes + "," + GlobalStrings.CHANGE_PASSWORD_OPERATION_CODE;
//                                            } else {
//                                                update_codes = update_codes + "," + GlobalStrings.SUSPEND_USER_OPERATION_CODE;
//                                            }
                                    nds.updateAllDownloadFormNotifcationStatus(update_codes, currentItem.userID + "");
                                    Util.setLogout((NotificationActivity) objContext);
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else if (currentItem.operationCode == GlobalStrings.DOWNLOAD_COC_OPERATION_CODE) {

                            if (currentItem.title.equals("Report Generated")) {
                                Log.e("notify", "onClick: " + currentItem.info);
                                AlertDialog.Builder builder = new AlertDialog.Builder(objContext);
                                builder.setTitle(currentItem.title).setMessage(currentItem.info).setCancelable(false).setPositiveButton(objContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        currentItem.status = 1;
                                        nds.updateNotificationStatus(currentItem.id + "", currentItem.status + "");
                                        notifyDataSetChanged();

                                        ArrayList<NotificationRow> list = new ArrayList<>();
                                        list = nds.getNotificationEventId(currentItem.id);
                                        NotificationRow notificationRow = list.get(0);
                                        Log.e("notificationAdapter", "onClick: userID--" + notificationRow.userID + " formID-- " + notificationRow.formID + "siteID-- " + notificationRow.siteID + "EventID--" + notificationRow.eventID);
                                        SharedPreferences reportParameters = objContext.getSharedPreferences("PDF_REPORT_PARAMETERS", Context.MODE_PRIVATE);
                                        reportParameters.edit().clear().commit();

                                        SharedPreferences.Editor editor = objContext.getSharedPreferences("PDF_REPORT_PARAMETERS", MODE_PRIVATE).edit();
                                        editor.putString("USER_ID", notificationRow.userID + "");
                                        editor.putString("FORM_ID", notificationRow.formID + "");
                                        editor.putString("SITE_ID", notificationRow.siteID + "");
                                        editor.putString("EVENT_ID", notificationRow.eventID + "");
                                        editor.apply();

                                        for (int j = 0; j < list.size(); j++) {
                                            Intent intent = new Intent(objContext, MobileReportRequiredActivity.class);
                                            intent.putExtra("USER_ID", notificationRow.userID + "");
                                            intent.putExtra("FORM_ID", notificationRow.formID + "");
                                            intent.putExtra("SITE_ID", notificationRow.siteID + "");
                                            intent.putExtra("EVENT_ID", notificationRow.eventID + "");
                                            intent.putExtra("call", "NotificationAdapter");
                                            objContext.startActivity(intent);
                                        }
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            } else {

                                AlertDialog.Builder builder = new AlertDialog.Builder(objContext);
                                builder.setTitle(currentItem.title).setMessage(currentItem.info).setCancelable(false).setPositiveButton(objContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        currentItem.status = 1;
                                        nds.updateNotificationStatus(currentItem.id + "", currentItem.status + "");
                                        notifyItemChanged(getAbsoluteAdapterPosition());
//                                            notifyDataSetChanged();
                                        if (mListener != null)
                                            mListener.onNotificationClicked(currentItem);
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        } else {

                            if (currentItem.title.equals("Report Generated")) {
                                Log.e("notify", "onClick: " + currentItem.info);
                                AlertDialog.Builder builder = new AlertDialog.Builder(objContext);
                                builder.setTitle(currentItem.title).setMessage(currentItem.info).setCancelable(false).setPositiveButton(objContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        currentItem.status = 1;
                                        nds.updateNotificationStatus(currentItem.id + "", currentItem.status + "");
                                        notifyDataSetChanged();

                                        ArrayList<NotificationRow> list = new ArrayList<>();
                                        list = nds.getNotificationEventId(currentItem.id);
                                        NotificationRow notificationRow = list.get(0);
                                        Log.e("notificationAdapter", "onClick: userID--" + notificationRow.userID + " formID-- " + notificationRow.formID + "siteID-- " + notificationRow.siteID + "EventID--" + notificationRow.eventID);
                                        SharedPreferences reportParameters = objContext.getSharedPreferences("PDF_REPORT_PARAMETERS", Context.MODE_PRIVATE);
                                        reportParameters.edit().clear().commit();

                                        SharedPreferences.Editor editor = objContext.getSharedPreferences("PDF_REPORT_PARAMETERS", MODE_PRIVATE).edit();
                                        editor.putString("USER_ID", notificationRow.userID + "");
                                        editor.putString("FORM_ID", notificationRow.formID + "");
                                        editor.putString("SITE_ID", notificationRow.siteID + "");
                                        editor.putString("EVENT_ID", notificationRow.eventID + "");
                                        editor.apply();

                                        for (int j = 0; j < list.size(); j++) {
                                            Intent intent = new Intent(objContext, MobileReportRequiredActivity.class);
                                            intent.putExtra("USER_ID", notificationRow.userID + "");
                                            intent.putExtra("FORM_ID", notificationRow.formID + "");
                                            intent.putExtra("SITE_ID", notificationRow.siteID + "");
                                            intent.putExtra("EVENT_ID", notificationRow.eventID + "");
                                            intent.putExtra("call", "NotificationAdapter");
                                            objContext.startActivity(intent);
                                        }
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(objContext);
                                builder.setTitle(currentItem.title).setMessage(currentItem.info).setCancelable(false).setPositiveButton(objContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        currentItem.status = 1;
                                        nds.updateNotificationStatus(currentItem.id + "", currentItem.status + "");
                                        notifyDataSetChanged();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    } else if (currentItem != null && currentItem.operationCode == GlobalStrings.DOWNLOAD_COC_OPERATION_CODE && currentItem.status == 1) {
                        mListener.onNotificationClicked(currentItem);
                    }
                }
            });
        }
    }
}
