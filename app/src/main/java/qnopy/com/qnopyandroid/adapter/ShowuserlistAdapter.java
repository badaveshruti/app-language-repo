package qnopy.com.qnopyandroid.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.Site;
import qnopy.com.qnopyandroid.requestmodel.SUser;

/**
 * Created by QNOPY on 5/9/2018.
 */

public class ShowuserlistAdapter extends ArrayAdapter<SUser> {

    List<SUser> userObjects;
    List<SUser> filtereduserObjects;
    private Context ObjContext;
    List<Site> spnsitelist = new ArrayList<>();
    String data = null;
    String role = null;
    int roleid=0;
    ArrayList<SUser> checkedList=new ArrayList<>();


    public ShowuserlistAdapter(@NonNull Context context, int resource, List<SUser> userList) {
        super(context, resource, userList);
        this.ObjContext = context;
        userObjects = new ArrayList<SUser>();
        this.userObjects = userList;
        this.filtereduserObjects = userList;
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

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SUser sUser = new SUser();
        sUser = filtereduserObjects.get(position);
        final ViewHolder viewHolder;
        //Location
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ObjContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.useritem_view, null);

            viewHolder = new ViewHolder();
            viewHolder.usertext = (TextView) convertView.findViewById(R.id.usertext);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkuser);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.usertext.setText(sUser.getUserName());

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    SUser user=new SUser();
                    user.setUserId(filtereduserObjects.get(position).getUserId());
                    user.setUserName(filtereduserObjects.get(position).getUserName());

                    checkedList.add(user);
                    //   notifyDataSetChanged();

                }else
                {
                    String name= String.valueOf(filtereduserObjects.get(position).getUserName());

                    for(int i=0;i<checkedList.size();i++)
                    {
                        if(checkedList.get(i).getUserName()==name)
                        {
                            checkedList.remove(i);
                            //  notifyDataSetChanged();

                        }
                    }

                }
            }
        });

        return convertView;
    }

    public ArrayList<SUser> getUserCheckedList() {
        if (checkedList == null) {

            return null;
        }
        else
            return checkedList;
    }


    static class ViewHolder {
        public TextView usertext;
        public CheckBox checkBox;
        public View mView;
    }
}
