package qnopy.com.qnopyandroid.signature;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.customView.CustomTextView;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.requestmodel.CustomerSign;

public class SignatureAdapter extends RecyclerView.Adapter<SignatureAdapter.ViewHolder> {

    private ArrayList<CustomerSign> signList = new ArrayList<>();
    private final Context mContext;
    private final SignatureUpdateListener mListener;
    private final boolean isFromForm;

    public SignatureAdapter(ArrayList<CustomerSign> signList, Context context,
                            SignatureUpdateListener listener, boolean isFromFormScreen) {
        this.signList = signList;
        this.mContext = context;
        this.mListener = listener;
        this.isFromForm = isFromFormScreen;
    }

    @NonNull
    @Override
    public SignatureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_signature_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SignatureAdapter.ViewHolder holder, int position) {
        CustomerSign sign = signList.get(position);

        Glide.with(mContext).load(sign.getFilepath())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.ivSignature);

        if (sign.getName() != null && !sign.getName().isEmpty()) {
            holder.tvSignatureName.setVisibility(View.VISIBLE);
            holder.tvSignatureName.setText(sign.getName());
        } else {
            holder.tvSignatureName.setVisibility(View.GONE);
        }

        if (!isFromForm) {
            holder.ivDelete.setVisibility(View.VISIBLE);
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeletePopup(sign, holder);
                }
            });
        } else {
            holder.ivDelete.setVisibility(View.GONE);

            holder.layoutSignatureItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onSignatureViewClicked();
                }
            });
        }
    }

    private void showDeletePopup(CustomerSign sign, ViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Do you want to remove this signature?");
        builder.setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String path = sign.getFilepath();

                AttachmentDataSource dataSource = new AttachmentDataSource(mContext);

                dataSource.deleteImage(path);
                Log.i("Deleted Attachment", "Deleted successful");

                signList.remove(sign);
                mListener.onSignatureRemoved(signList);
                notifyItemRemoved(holder.getBindingAdapterPosition());

                Toast.makeText(mContext, "Signature Removed", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void addSignatures(ArrayList<CustomerSign> signatures) {
        signList.clear();
        signList.addAll(signatures);
        notifyDataSetChanged();
    }

    public void addSignature(CustomerSign signature) {
        signList.add(signature);
        notifyItemInserted(signList.size() - 1);
    }

    @Override
    public int getItemCount() {
        return signList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivSignature;
        private final ImageView ivDelete;
        private final CustomTextView tvSignatureName;
        private final ConstraintLayout layoutSignatureItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutSignatureItem = itemView.findViewById(R.id.layoutSignatureItem);
            ivSignature = itemView.findViewById(R.id.ivSignature);
            tvSignatureName = itemView.findViewById(R.id.tvSignatureName);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
