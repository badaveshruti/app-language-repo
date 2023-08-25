package qnopy.com.qnopyandroid.customView;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import qnopy.com.qnopyandroid.util.FontCache;

/**
 * Created by PatelSanket on 01-Apr-18.
 */

public class CustomBoldTextView extends AppCompatTextView {
    Context context;

    public CustomBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (!isInEditMode())
            init();
    }

    private void init() {
        Typeface font = FontCache.getTypeface(FontCache.BOLD_FONT, context);
        setTypeface(font, Typeface.BOLD);
    }

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(tf);
    }
}
