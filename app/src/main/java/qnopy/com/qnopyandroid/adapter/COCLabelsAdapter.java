package qnopy.com.qnopyandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.customView.CustomTextView;
import qnopy.com.qnopyandroid.requestmodel.SCocMaster;

public class COCLabelsAdapter extends RecyclerView.Adapter<COCLabelsAdapter.ViewHolder> {

    private ArrayList<SCocMaster> cocList;
    private OnCOCLabelClickListener mListener;

    public COCLabelsAdapter(ArrayList<SCocMaster> cocLabelsList,
                            OnCOCLabelClickListener cocClickListener) {
        this.cocList = cocLabelsList;
        this.mListener = cocClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_print_labels_coc,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvCOCDisplayName.setText(cocList.get(position).getCocDisplayId());
    }

    @Override
    public int getItemCount() {
        return cocList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CustomTextView tvCOCDisplayName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCOCDisplayName = itemView.findViewById(R.id.tvCOCDisplayName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onCOCLabelClicked(cocList.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public interface OnCOCLabelClickListener {
        void onCOCLabelClicked(SCocMaster cocSelected);
    }
}
