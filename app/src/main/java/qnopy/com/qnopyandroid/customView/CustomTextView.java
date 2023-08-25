package qnopy.com.qnopyandroid.customView;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import qnopy.com.qnopyandroid.util.FontCache;

public class CustomTextView extends AppCompatTextView {

    Context mContext;

    public CustomTextView(@NonNull Context context) {
        super(context);
        mContext = context;
        if (!isInEditMode())
            init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        if (!isInEditMode())
            init();
    }

    private void init() {
        Typeface font = FontCache.getTypeface(FontCache.REGULAR_FONT, mContext);
        setTypeface(font);
    }
}
