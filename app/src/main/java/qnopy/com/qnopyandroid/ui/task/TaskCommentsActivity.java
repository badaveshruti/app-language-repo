package qnopy.com.qnopyandroid.ui.task;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.db.TaskCommentsDataSource;
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse;
import qnopy.com.qnopyandroid.ui.task.adapter.CommentsAdapter;
import qnopy.com.qnopyandroid.uiutils.DividerItemDecoration;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;
import qnopy.com.qnopyandroid.util.Util;

public class TaskCommentsActivity extends ProgressDialogActivity {

    private RecyclerView rvComments;
    private CommentsAdapter adapter;
    private TextView tvNoComments;
    private TextView tvCommentHint;
    private CardView cvSearch;
    private SearchView searchViewComment;
    private TextView tvSend;
    private EditText edtAddComment;
    private int userID;
    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_comments);

        if (getIntent() != null) {
            taskId = getIntent().getIntExtra(GlobalStrings.TASK_ID, 0);
        }
        userID = Integer.parseInt(Util.getSharedPreferencesProperty(this, GlobalStrings.USERID));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Comments");

        rvComments = findViewById(R.id.rvComments);
        tvNoComments = findViewById(R.id.noComments);
        tvCommentHint = findViewById(R.id.commentHint);
        edtAddComment = findViewById(R.id.edtAddComment);
        tvSend = findViewById(R.id.tvSend);

        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveComment();
            }
        });

        initSearchView();
        setComments();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void saveComment() {
        if (!edtAddComment.getText().toString().isEmpty()) {

            ArrayList<TaskDataResponse.CommentList> list = new ArrayList<>();
            TaskDataResponse.CommentList comment = new TaskDataResponse.CommentList();
            comment.setTaskId(taskId);
            comment.setTaskCommentId(-Util.randInt(0, 9999));
            comment.setComment(edtAddComment.getText().toString());
            comment.setAttachment(0);
            comment.setCreatedBy(userID);
            comment.setCreationDate(System.currentTimeMillis());
            list.add(comment);

            TaskCommentsDataSource dataSource = new TaskCommentsDataSource(this);
            dataSource.insertTaskComments(list, 0);

            if (adapter != null) {
                adapter.addComment(comment);
                rvComments.scrollToPosition(adapter.getItemCount() - 1);
                handleRecyclerViewVisibility(true);
            }

            edtAddComment.setText("");
        }
    }

    private void setComments() {
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setHasFixedSize(true);
        rvComments.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        TaskCommentsDataSource commentsDataSource
                = new TaskCommentsDataSource(this);
        ArrayList<TaskDataResponse.CommentList> commentList
                = commentsDataSource.getAllComments(taskId);

        adapter = new CommentsAdapter(this, commentList);
        handleRecyclerViewVisibility(commentList.size() > 0);
        rvComments.setAdapter(adapter);
    }

    public void handleRecyclerViewVisibility(boolean hasData) {
        if (hasData) {
            tvCommentHint.setVisibility(View.GONE);
            tvNoComments.setVisibility(View.GONE);
            rvComments.setVisibility(View.VISIBLE);
            cvSearch.setVisibility(View.VISIBLE);
        } else {
            tvCommentHint.setVisibility(View.VISIBLE);
            tvNoComments.setVisibility(View.VISIBLE);
            rvComments.setVisibility(View.INVISIBLE);
            cvSearch.setVisibility(View.INVISIBLE);
        }
    }

    private void initSearchView() {
        cvSearch = findViewById(R.id.cardViewSearch);
        searchViewComment = findViewById(R.id.searchViewComments);
        searchViewComment.setQueryHint("search");
        searchViewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchViewComment.setIconified(false);
            }
        });

        searchViewComment.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                }
                return false;
            }
        });
    }
}
