package qnopy.com.qnopyandroid.util;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public abstract class MyRecyclerScroll extends RecyclerView.OnScrollListener {

    private int scrollDist = 0;
    private boolean isVisible = true;
    private static final float MINIMUM = 25;

    @Override
    public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (isVisible && scrollDist > MINIMUM) {
            hide();
            scrollDist = 0;
            isVisible = false;
        } else if (!isVisible && scrollDist < -MINIMUM) {
            show();
            scrollDist = 0;
            isVisible = true;
        }

        if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
            scrollDist += dy;
        }
    }

    public abstract void show();

    public abstract void hide();
}
