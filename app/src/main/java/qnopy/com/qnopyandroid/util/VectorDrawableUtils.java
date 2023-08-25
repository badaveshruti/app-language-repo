package qnopy.com.qnopyandroid.util;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

public class VectorDrawableUtils {

    public static Drawable getDrawable(Context context, int drawableResId) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            return ResourcesCompat.getDrawable(context.getResources(),
                    drawableResId, context.getTheme());
        } else {
            return VectorDrawableCompat.create(
                    context.getResources(),
                    drawableResId,
                    context.getTheme()
            );
        }
    }

    public static Drawable getDrawable(Context context, int drawableResId, int colorFilter) {
        Drawable drawable = getDrawable(context, drawableResId);
        drawable.setColorFilter(
                ContextCompat.getColor(context, colorFilter),
                PorterDuff.Mode.SRC_IN
        );

        return drawable;
    }

}
