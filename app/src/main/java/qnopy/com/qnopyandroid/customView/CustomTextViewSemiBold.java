package qnopy.com.qnopyandroid.customView;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import qnopy.com.qnopyandroid.util.FontCache;

/**
 * Created by PatelSanket on 01-Apr-18.
 */

public class CustomTextViewSemiBold extends AppCompatTextView {
    Context context;

    public CustomTextViewSemiBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (!isInEditMode())
            init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null && !isInEditMode()) {
            Typeface font = FontCache.getTypeface(FontCache.SEMI_BOLD_FONT, context);
            setTypeface(font, Typeface.NORMAL);
        }
    }

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(tf);
    }
}
