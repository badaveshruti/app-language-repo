package qnopy.com.qnopyandroid.ui.task.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.db.UserDataSource;
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse;
import qnopy.com.qnopyandroid.util.Util;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<TaskDataResponse.CommentList> commentList;
    private ArrayList<TaskDataResponse.CommentList> filterCommentList;
    private CommentsFilter commentsFilter;

    public CommentsAdapter(Context mContext, ArrayList<TaskDataResponse.CommentList> commentList) {
        this.mContext = mContext;
        this.commentList = new ArrayList<>();
        this.commentList = commentList;
        this.filterCommentList = commentList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUserName;
        private final TextView tvComment;
        private final TextView tvCommentDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvCommentDate = itemView.findViewById(R.id.tvCommentDate);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_task_comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskDataResponse.CommentList comment = filterCommentList.get(position);
        UserDataSource userDataSource = new UserDataSource(mContext);
        String userName = userDataSource.getUserNameFromID(comment.getCreatedBy() + "");
        holder.tvUserName.setText(userName);
        holder.tvCommentDate.setText(Util.getFormattedDateTime(comment.getCreationDate(), GlobalStrings.DATE_FORMAT_MMM_DD_YYYY_H_M_12HR));
        holder.tvComment.setText(comment.getComment());
    }

    @Override
    public int getItemCount() {
        return filterCommentList.size();
    }

    public void addComment(TaskDataResponse.CommentList comment) {
        filterCommentList.add(comment);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        if (commentsFilter == null) {
            commentsFilter = new CommentsFilter();
        }
        return commentsFilter;
    }

    public class CommentsFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                results.values = commentList;
                results.count = commentList.size();
            } else {

                String filterableString;
                ArrayList<TaskDataResponse.CommentList> filtered = new ArrayList<>();

                for (TaskDataResponse.CommentList comment : commentList) {
                    filterableString = comment.getComment();
                    if (filterableString.toLowerCase(Locale.getDefault()).contains(filterString)) {
                        filtered.add(comment);
                    }
                }
                results.values = filtered;
                results.count = filtered.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0) {
                filterCommentList = new ArrayList<>();
            } else {
                filterCommentList = (ArrayList<TaskDataResponse.CommentList>) results.values;
            }
            notifyItemRangeRemoved(0, getItemCount());
            notifyDataSetChanged();
        }
    }
}
