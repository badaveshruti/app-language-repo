package qnopy.com.qnopyandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.customView.CustomTextView;

public class ShowFilesAdapter extends RecyclerView.Adapter<ShowFilesAdapter.ViewHolder> {

    private ArrayList<String> listFiles;
    private Context context;
    private OnFileClickListener mListener;

    public ShowFilesAdapter(ArrayList<String> listFiles, Context context,
                            OnFileClickListener mListener) {
        this.listFiles = listFiles;
        this.context = context;
        this.mListener = mListener;
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
        String path = listFiles.get(position);
        File file = new File(path);
        holder.tvFileName.setText(file.getName());
    }

    @Override
    public int getItemCount() {
        return listFiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CustomTextView tvFileName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            ImageView ivSheet = itemView.findViewById(R.id.ivSheet);
            ivSheet.setVisibility(View.VISIBLE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onFileClick(listFiles.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public interface OnFileClickListener {
        void onFileClick(String path);
    }
}
