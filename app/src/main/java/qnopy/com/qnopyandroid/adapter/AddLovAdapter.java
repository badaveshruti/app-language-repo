package qnopy.com.qnopyandroid.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.LovItems;
import qnopy.com.qnopyandroid.db.LovDataSource;
import qnopy.com.qnopyandroid.ui.activity.AddLovActivity;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by QNOPY on 5/5/2018.
 */

public class AddLovAdapter extends ArrayAdapter<LovItems> {

    List<LovItems> lovObjects;
    List<LovItems> filteredlovItemObjects;
    private Context mContext;

    Button btnsave, btncancel;
    EditText edtname, edtvalue;

    static AddLovActivity parentContext;

    public AddLovAdapter(@NonNull Context context, AddLovActivity parentContext, int resource) {
        super(context, resource, parentContext.Lov_List);
        this.mContext = context;
        lovObjects = new ArrayList<LovItems>();
        this.lovObjects = parentContext.Lov_List;
        this.filteredlovItemObjects = parentContext.Lov_List;
        this.parentContext = parentContext;
    }

    @Override
    public int getCount() {
        return filteredlovItemObjects.size();
    }

    @Nullable
    @Override
    public LovItems getItem(int position) {
        return filteredlovItemObjects.get(position);
    }

    public void updateLovItemsList() {
        this.lovObjects = parentContext.Lov_List;
        this.filteredlovItemObjects = parentContext.Lov_List;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        LovItems lovItem = new LovItems();
        lovItem = filteredlovItemObjects.get(position);

        final int companyid = Integer.parseInt(Util.getSharedPreferencesProperty(getContext(), GlobalStrings.COMPANYID));
        final String userid = Util.getSharedPreferencesProperty(getContext(), GlobalStrings.USERID);

        Log.i("AddLovAdapter", "Company ID:" + companyid);
        final ViewHolder viewHolder;
        //Location
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lovitemlist_item, null);

            viewHolder = new ViewHolder();
            viewHolder.lovitemdisplaytv = (TextView) convertView.findViewById(R.id.lovitemtitle_tv);
            viewHolder.lovdisplayvaluetv = (TextView) convertView.findViewById(R.id.lovdisplaytitle_tv);
            viewHolder.edit_iv = (ImageView) convertView.findViewById(R.id.edit_iv);
            viewHolder.delete_iv = (ImageView) convertView.findViewById(R.id.deletelov);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.lovitemdisplaytv.setText(lovItem.getItemDisplayName());
        viewHolder.lovdisplayvaluetv.setText(lovItem.getItemValue());
        int userrole = 0;

        String struserrole = Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERROLE);

        if (struserrole != null) {
            userrole = Integer.parseInt(struserrole);
        }

        if (userrole != 0 && companyid != 0) {
            Log.i("", "AddLovAdapter userrole && companyid : " + userrole + "  " + companyid);

            if ((userrole == GlobalStrings.CLIENT_ADMIN || userrole == GlobalStrings.PROJECT_MANAGER
                    || userrole == GlobalStrings.SUPER_ADMIN)) {
                //17-05-2018 SA,PM OR CA DELETE/UPDATE LOVs ONLY UNDER HIS COMPANY
                if ((lovItem.getCompany_id() == companyid)) {
                    viewHolder.edit_iv.setVisibility(View.VISIBLE);
                    if (lovItem.getSyncFlag() == 0) {
                        viewHolder.delete_iv.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.delete_iv.setVisibility(View.GONE);
                    }
                } else {
                    viewHolder.edit_iv.setVisibility(View.GONE);
                    viewHolder.delete_iv.setVisibility(View.GONE);
                }
            } else {
                viewHolder.edit_iv.setVisibility(View.GONE);
                viewHolder.delete_iv.setVisibility(View.GONE);
            }
        }
//        else {
//            viewHolder.edit_iv.setVisibility(View.GONE);
//            viewHolder.delete_iv.setVisibility(View.GONE);
//
//        }

        final LovItems finalLovItem1 = lovItem;
        viewHolder.edit_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater factory = LayoutInflater.from(mContext);
                final View DialogView = factory.inflate(R.layout.updatelovcustomdialog, null);
                final AlertDialog Dialog = new AlertDialog.Builder(mContext).create();
                Dialog.setCanceledOnTouchOutside(true);
                Dialog.setView(DialogView);
                btnsave = DialogView.findViewById(R.id.btn_save);
                btncancel = DialogView.findViewById(R.id.btn_cancel);
                edtname = DialogView.findViewById(R.id.edemname);
                edtvalue = DialogView.findViewById(R.id.edtemval);
                Dialog.show();

                edtname.setText(viewHolder.lovitemdisplaytv.getText());
                edtname.setSelection(edtname.getText().length());
                edtvalue.setText(viewHolder.lovdisplayvaluetv.getText());
                edtvalue.setSelection(edtvalue.getText().length());

                btncancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog.dismiss();
                    }
                });

                btnsave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = edtname.getText().toString();
                        String value = edtvalue.getText().toString();

                        LovDataSource lovDataSource = new LovDataSource(parentContext);

                        //  boolean checkname = lovDataSource.checkname(name, finalLovItem1.getLovID());
                        // boolean checkvalue = lovDataSource.checkvalue(value, finalLovItem1.getLovID());

//                        if (name.trim().equalsIgnoreCase("")) {
//                            edtname.setError("Please enter item name.");
//                        } else if (value.trim().equalsIgnoreCase("")) {
//                            edtvalue.setError("Please enter item value.");
//                        }
//                        if (checkname) {
//                            edtname.setError("Item name already exist.");
//
//                        } else if (checkvalue) {
//                            // edtname.requestFocus();
//                            edtvalue.setError("Item value already exist.");
//                        }

                        //LovDataSource lovDataSource = new LovDataSource(getContext());

                        lovDataSource.updateLovforItem(companyid + "", userid, finalLovItem1.getLovItemID(),
                                name, value,
                                Integer.parseInt(Util.getSharedPreferencesProperty(mContext, GlobalStrings.CURRENT_SITEID)));
                        //  parentContext.reloadData();
                        parentContext.reloadDatafordel();

                        Dialog.dismiss();
                    }
                });
            }
        });

        final LovItems finalLovItem = lovItem;
        viewHolder.delete_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(mContext.getString(R.string.delete_upper_case))
                        .setMessage(mContext.getString(R.string.are_you_sure_you_want_to_delete)
                                + finalLovItem.getItemDisplayName() + "'?")
                        .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                LovDataSource lovDataSource = new LovDataSource(getContext());

                                lovDataSource.deleteLovItem(companyid + "", finalLovItem1.getLovItemID());
                                //  lovDataSource.updateSyncFlag(companyid,finalLovItem1.getLovItemID());

                                parentContext.reloadDatafordel();
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return convertView;
    }

    static class ViewHolder {
        public TextView lovitemdisplaytv;
        public TextView lovdisplayvaluetv;
        public ImageView edit_iv, delete_iv;
    }
}
