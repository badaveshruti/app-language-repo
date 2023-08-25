package qnopy.com.qnopyandroid.filefolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.FileFolderItem;

/**
 * Created by Yogendra on 22-Jan-16.
 */
public class FileFolderAdapter extends ArrayAdapter<FileFolderItem> {


    Context mContext;
    ArrayList<FileFolderItem> mContent;

    public FileFolderAdapter(Context context, int resource, List<FileFolderItem> objects) {
        super(context, resource, objects);
        mContent=new ArrayList<FileFolderItem>();
        mContext = context;
        mContent.addAll(objects);
    }

    @Override
    public int getCount() {
        return mContent.size();
    }

    @Override
    public FileFolderItem getItem(int position) {
        return mContent.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.fragment_file_item, parent, false);

            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.itemiconiv);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.itemnametv);
            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // update the item view
        FileFolderItem item = getItem(position);

        viewHolder.tvTitle.setText(item.getItemTitle());

        if (item.getItemType().equals("folder")) {
            viewHolder.ivIcon.setImageResource(R.mipmap.ic_folder);
        } else {
            viewHolder.ivIcon.setImageResource(R.mipmap.ic_doc);
        }
        return convertView;
    }

    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     */
    private static class ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
    }
}
