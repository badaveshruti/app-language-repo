package qnopy.com.qnopyandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.MobileApp;

public class ChildAppsAdapter extends RecyclerView.Adapter<ChildAppsAdapter.ViewHolder> {

    private List<MobileApp> childAppList;
    private Context mContext;
    private OnChildAppClickedListener mListener;
    private int oldSelectedPosition = 0;

    public ChildAppsAdapter(List<MobileApp> childAppList, Context context, OnChildAppClickedListener listener) {

        if (childAppList.size() > 0) {
            childAppList.get(0).setSelected(true);
        }

        this.childAppList = childAppList;
        this.mContext = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child_apps,
                null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MobileApp app = childAppList.get(holder.getAbsoluteAdapterPosition());
        holder.tvChildAppName.setText(app.getAppName());

        if (app.isSelected()) {
            holder.viewSelectionIndicator.setBackgroundColor(ContextCompat.getColor(mContext,
                    R.color.orange_500));
        } else {
            holder.viewSelectionIndicator.setBackgroundColor(ContextCompat.getColor(mContext,
                    R.color.transparent));
        }
    }

    @Override
    public int getItemCount() {
        return childAppList.size();
    }

    //this method is called when item is selected from appNavigation list
    public void setItemSelected(int position) {
        if (!childAppList.get(position).isSelected()) {
            childAppList.get(oldSelectedPosition).setSelected(false);
            notifyItemChanged(oldSelectedPosition);
            childAppList.get(position).setSelected(true);
            notifyItemChanged(position);
            oldSelectedPosition = position;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvChildAppName;
        private final View viewSelectionIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChildAppName = itemView.findViewById(R.id.tvChildAppName);
            viewSelectionIndicator = itemView.findViewById(R.id.viewSelectionIndicator);

            itemView.setOnClickListener(v -> {
                if (!childAppList.get(getAbsoluteAdapterPosition()).isSelected()) {
                    mListener.onChildAppSelected(childAppList.get(getAbsoluteAdapterPosition()),
                            getAbsoluteAdapterPosition());
                }
            });
        }
    }

    public interface OnChildAppClickedListener {
        void onChildAppSelected(MobileApp app, int position);
    }
}
