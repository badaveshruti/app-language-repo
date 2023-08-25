package qnopy.com.qnopyandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.clientmodel.CopiedTemplate;
import qnopy.com.qnopyandroid.customView.CustomTextView;

public class CopiedFormsAdapter extends RecyclerView.Adapter<CopiedFormsAdapter.ViewHolder> {

    private ArrayList<CopiedTemplate> mTemplates;
    private OnCopiedFormClickListener mListener;

    public CopiedFormsAdapter(ArrayList<CopiedTemplate> pathList, OnCopiedFormClickListener listener) {
        this.mTemplates = pathList;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.copied_form_items,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CopiedTemplate copiedTemplate = mTemplates.get(position);
        holder.tvFileName.setText(copiedTemplate.getFileName());
    }

    @Override
    public int getItemCount() {
        return mTemplates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CustomTextView tvFileName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tvFileName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onCopiedFormClick(mTemplates.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public interface OnCopiedFormClickListener {
        void onCopiedFormClick(CopiedTemplate template);
    }
}
