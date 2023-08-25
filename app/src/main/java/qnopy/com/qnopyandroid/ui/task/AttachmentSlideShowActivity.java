package qnopy.com.qnopyandroid.ui.task;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.db.TaskAttachmentsDataSource;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.liveFeed.FeedSlideShowPagerAdapter;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.liveFeed.model.LiveFeedResponse;
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse;
import qnopy.com.qnopyandroid.ui.task.adapter.AttachmentSlideShowAdapter;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;

public class AttachmentSlideShowActivity extends ProgressDialogActivity
        implements View.OnClickListener {

    private ImageView ivPrev;
    private ImageView ivClose;
    private ImageView ivNext;
    private TextView tvImagePosition;
    private ViewPager2 pagerAttachment;
    private AttachmentSlideShowAdapter adapter;
    private ArrayList<TaskDataResponse.AttachmentList> attachmentList = new ArrayList<>();
    private int taskId;
    private ArrayList<LiveFeedResponse.LiveFeedPictureData> feedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment_slide_show);
        if (getIntent() != null) {
            taskId = getIntent().getIntExtra(GlobalStrings.TASK_ID, 0);
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    feedList = getIntent().getParcelableArrayListExtra(GlobalStrings.FEED_PICS_LIST,
                            LiveFeedResponse.LiveFeedPictureData.class);
                } else {
                    feedList = getIntent().getParcelableArrayListExtra(GlobalStrings.FEED_PICS_LIST);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setUpUi();
    }

    private void setUpUi() {
        ivClose = findViewById(R.id.ivClose);
        ImageView ivDelete = findViewById(R.id.ivDelete);
        ivPrev = findViewById(R.id.ivPrev);
        ivNext = findViewById(R.id.ivNext);
        tvImagePosition = findViewById(R.id.tvImagePosition);
        pagerAttachment = findViewById(R.id.pagerSlideShow);

        ivClose.setOnClickListener(this);
        ivPrev.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivDelete.setOnClickListener(this);

        if (feedList.size() > 0)
            ivDelete.setVisibility(View.GONE);

        setUpPager();
    }

    private void setUpPager() {

        if (attachmentList.size() > 0) {
            TaskAttachmentsDataSource dataSource = new TaskAttachmentsDataSource(this);
            attachmentList =
                    dataSource.getAllAttachments(taskId);

            String posText = "1/" + attachmentList.size();
            tvImagePosition.setText(posText);

            adapter = new AttachmentSlideShowAdapter(attachmentList, this);
            pagerAttachment.setAdapter(adapter);
        } else {
            String posText = "1/" + feedList.size();
            tvImagePosition.setText(posText);

            FeedSlideShowPagerAdapter feedAdapter = new FeedSlideShowPagerAdapter(feedList, this);
            pagerAttachment.setAdapter(feedAdapter);
        }

        pagerAttachment.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                int count = position + 1;
                int size = attachmentList.size();

                if (feedList.size() > 0)
                    size = feedList.size();

                String posText = count + "/" + size;
                tvImagePosition.setText(posText);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ivClose:
                finish();
                break;
            case R.id.ivDelete:
                deleteImage();
                break;
            case R.id.ivPrev:
                if (pagerAttachment.getCurrentItem() > 0)
                    pagerAttachment.setCurrentItem(pagerAttachment.getCurrentItem() - 1);
                break;
            case R.id.ivNext:

                int size = attachmentList.size();
                if (feedList.size() > 0)
                    size = feedList.size();

                if (pagerAttachment.getCurrentItem() < size)
                    pagerAttachment.setCurrentItem(pagerAttachment.getCurrentItem() + 1);
                break;
        }
    }

    private void deleteImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.do_you_wish_to_remove_attachment));
        builder.setTitle(getString(R.string.remove_attachment));

        builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
            TaskDataResponse.AttachmentList attachment = attachmentList.get(pagerAttachment.getCurrentItem());
            TaskAttachmentsDataSource dataSource = new TaskAttachmentsDataSource(this);
            dataSource.updateDisplayFlag(attachment.getTaskId(), attachment.getFileName(),
                    attachment.getTaskAttachmentId());
            if (adapter != null) {
                adapter.removeAttachment(pagerAttachment.getCurrentItem());
                int count = pagerAttachment.getCurrentItem() + 1;
                String posText = count + "/" + adapter.getItemCount();
                tvImagePosition.setText(posText);
            }
        });

        builder.setNegativeButton(getString(R.string.no), (dialog, which) -> {
            dialog.cancel();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
