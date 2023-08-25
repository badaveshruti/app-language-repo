package qnopy.com.qnopyandroid.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.adapter.NotesImagesSlideShowAdapter;
import qnopy.com.qnopyandroid.clientmodel.GridImageItem;
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity;

public class NotesImagesSlideShowActivity extends ProgressDialogActivity implements View.OnClickListener {

    private ImageView ivPrev;
    private ImageView ivClose;
    private ImageView ivNext;
    private TextView tvImagePosition;
    private ViewPager2 pagerAttachment;
    private ArrayList<GridImageItem> pathList = new ArrayList<>();
    private NotesImagesSlideShowAdapter adapter;
    private int selectedImagePos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment_slide_show);

        if (getIntent() != null) {
            pathList = (ArrayList<GridImageItem>) getIntent().getSerializableExtra(GlobalStrings.PATH_LIST);
            selectedImagePos = getIntent().getIntExtra(GlobalStrings.POSITION, 0);
        }
        setUpUi();
    }

    private void setUpUi() {
        findViewById(R.id.ivDelete).setVisibility(View.GONE);
        ivClose = findViewById(R.id.ivClose);
        ivPrev = findViewById(R.id.ivPrev);
        ivNext = findViewById(R.id.ivNext);
        tvImagePosition = findViewById(R.id.tvImagePosition);
        pagerAttachment = findViewById(R.id.pagerSlideShow);

        ivClose.setOnClickListener(this);
        ivPrev.setOnClickListener(this);
        ivNext.setOnClickListener(this);

        setUpPager();
    }

    private void setUpPager() {

        String posText = "1/" + pathList.size();
        tvImagePosition.setText(posText);

        adapter = new NotesImagesSlideShowAdapter(pathList, this);
        pagerAttachment.setAdapter(adapter);

        pagerAttachment.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                int count = position + 1;
                String posText = count + "/" + pathList.size();
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

        if (selectedImagePos < pathList.size())
            pagerAttachment.setCurrentItem(selectedImagePos);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivClose:
                finish();
                break;
            case R.id.ivPrev:
                if (pagerAttachment.getCurrentItem() > 0)
                    pagerAttachment.setCurrentItem(pagerAttachment.getCurrentItem() - 1);
                break;
            case R.id.ivNext:
                if (pagerAttachment.getCurrentItem() < pathList.size())
                    pagerAttachment.setCurrentItem(pagerAttachment.getCurrentItem() + 1);
                break;
        }
    }
}