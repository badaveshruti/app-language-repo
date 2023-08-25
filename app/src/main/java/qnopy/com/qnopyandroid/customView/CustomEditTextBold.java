package qnopy.com.qnopyandroid.customView;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import qnopy.com.qnopyandroid.util.FontCache;

/**
 * Created by PatelSanket on 01-Apr-18.
 */

public class CustomEditTextBold extends AppCompatEditText {
    Context context;

    public CustomEditTextBold(Context context, AttributeSet attrs) {
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

