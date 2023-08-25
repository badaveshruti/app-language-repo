package qnopy.com.qnopyandroid.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.Site;
import qnopy.com.qnopyandroid.db.SiteDataSource;
import qnopy.com.qnopyandroid.db.SiteUserRoleDataSource;
import qnopy.com.qnopyandroid.requestmodel.SSiteUserRole;
import qnopy.com.qnopyandroid.requestmodel.SUser;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by QNOPY on 5/6/2018.
 */

public class UserListAdapter extends ArrayAdapter<SUser> {
    List<SUser> userObjects;
    List<SUser> filtereduserObjects;
    private Context ObjContext;
    List<Site> spnsitelist = new ArrayList<>();
    String data = null;
    String role = null;
    int roleid=0;
    int siteid=0;

    public UserListAdapter(@NonNull Context context, int resource, List<SUser> userList,int siteid) {
        super(context, resource, userList);
        this.ObjContext = context;
        userObjects = new ArrayList<SUser>();
        this.userObjects = userList;
        this.filtereduserObjects = userList;
        this.siteid=siteid;
    }

    @Override
    public int getCount() {
        return filtereduserObjects.size();
    }

    @Nullable
    @Override
    public SUser getItem(int position) {
        return filtereduserObjects.get(position);
    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SUser sUser = new SUser();
        sUser = filtereduserObjects.get(position);
        final ViewHolder viewHolder;
        //Location
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ObjContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.userlist_item, null);

            viewHolder = new ViewHolder();
            viewHolder.usertitletv = convertView.findViewById(R.id.usertitle_tv);
            //ss viewHolder.spnsite = (Spinner) convertView.findViewById(R.id.spn_site);
            viewHolder.spnrole = convertView.findViewById(R.id.spn_role);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.usertitletv.setText(sUser.getUserName());
        // if (sUser.getUserRole() == 3)
        //{
        //   String val="Standard User";
        //}


        List<String> list = new ArrayList<>();

        //  list.add("Select role");
        list.add("Standard User");
        list.add("Project Manager");
        list.add("QA validator");

        // spnlist=siteDataSource.getSiteListforaddLov(userid);

        final SiteDataSource siteDataSource = new SiteDataSource(ObjContext);
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ObjContext,
                R.layout.spinner_text_item, list);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // spinner2.setAdapter(dataAdapter);
        // SpinnerdropdownAdapter adapter = new SpinnerdropdownAdapter(ObjContext, spnlist);
        viewHolder.spnrole.setAdapter(dataAdapter);

        final List<Site> sitelist = new ArrayList<>();
        Site site = new Site();
        site.setSiteName("Select Project");
        site.setSiteID(0);
        sitelist.add(site);

        final String userid = Util.getSharedPreferencesProperty(ObjContext, GlobalStrings.USERID);


        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



//        spnsitelist = siteDataSource.getSitesForUser(sUser.getUserId()+"");
//
//        for (int i = 0; i < spnsitelist.size(); i++) {
//            sitelist.add(spnsitelist.get(i));
//        }
//
//        ArrayAdapter<Site> spndataAdapter = new ArrayAdapter<Site>(ObjContext,
//                R.layout.spinner_item, sitelist);
//
//
//        // spinner2.setAdapter(dataAdapter);
//        // SpinnerdropdownAdapter adapter = new SpinnerdropdownAdapter(ObjContext, spnlist);
//        viewHolder.spnsite.setAdapter(spndataAdapter);
//        viewHolder.spnsite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//                data = viewHolder.spnsite.getItemAtPosition(i).toString();
//
//                if (data != null && data.length()>2) {
//                    viewHolder.spnrole.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        final SUser finalSUser = sUser;
        viewHolder.spnrole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String role = viewHolder.spnrole.getItemAtPosition(i).toString();
                if (role != null) {

                    //   int siteid=sitelist.get(i).getSiteID();

                    if(role.equalsIgnoreCase("Project Manager"))
                    {
                        roleid=5;
                    }else if(role.equalsIgnoreCase("Standard User"))
                    {
                        roleid=3;
                    }else if(role.equalsIgnoreCase("QA validator"))
                    {
                        roleid=4;
                    }

//                    if(finalSUser.getUserRole().equals(3))
//                    {
//                        viewHolder.spnrole.setSelection(position);
//                    }

                    SiteUserRoleDataSource siteUserRoleDataSource=new SiteUserRoleDataSource(ObjContext);
                    SSiteUserRole userRole=new SSiteUserRole();
                    userRole=siteUserRoleDataSource.isSiteAssigned(siteid, String.valueOf(finalSUser.getUserId()),roleid);

                    if(userRole.getSiteId()!=null && userRole.getRoleId()!=null && userRole.getUserId()!=null)
                    {
                        if(userRole.getRoleId()!=roleid) {
                            siteUserRoleDataSource.updateroleid(siteid, String.valueOf(finalSUser.getUserId()), roleid,userRole.getRoleId());
                        }
                    }

// else
//                    {
//                        siteUserRoleDataSource.insertSiteUserRole(siteid,roleid, Integer.parseInt(userid));
//                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


//        viewHolder.spnrole.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//
//                viewHolder.spnrole.setSelection(position);
//
//            }
//        });

        return convertView;
    }



    static class ViewHolder {
        public TextView usertitletv;
        public Spinner spnsite;
        public Spinner spnrole;
        public View mView;
    }
}
