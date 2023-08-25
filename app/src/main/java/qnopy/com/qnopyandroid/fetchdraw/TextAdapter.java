package qnopy.com.qnopyandroid.fetchdraw;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import qnopy.com.qnopyandroid.R;


public class TextAdapter extends BaseAdapter{

	int[] img_id;
	LayoutInflater mInflater;
	Context mContext;
	Activity mActivity;
	
	
	// For Text Size and Line Stroke Size Setting Adapter
	
	public TextAdapter(Activity activity, int[] text_img_id) {
		mActivity = activity;
		mContext = mActivity;
		img_id = text_img_id;
	}

	@Override
	public int getCount() {
		return img_id.length;
	}

	@Override
	public Object getItem(int position) {
		return img_id[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	private class ViewHolder {
		ImageView mImageView;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		View vi = convertView;
		if(convertView == null) {
			mHolder = new ViewHolder();
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = mInflater.inflate(R.layout.text_size_spinner, parent, false);
			mHolder.mImageView = (ImageView) vi.findViewById(R.id.img);

			vi.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) vi.getTag();
		}
		mHolder.mImageView.setImageResource(img_id[position]);
		return vi;
	}
}
