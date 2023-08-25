package qnopy.com.qnopyandroid.util;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

public class FontCache {


    public static final String REGULAR_FONT = "Roboto-Regular.ttf";
    public static final String BOLD_FONT = "Roboto-Bold.ttf";
    public static final String SEMI_BOLD_FONT = "Roboto-Medium.ttf";
    public static final String ITALIC_FONT = "Roboto-Italic.ttf";

    private static HashMap<String, Typeface> fontCache = new HashMap<>();

    public static Typeface getTypeface(String fontname, Context context) {
        Typeface typeface = fontCache.get(fontname);
        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontname);
            } catch (Exception e) {
                return null;
            }
            fontCache.put(fontname, typeface);
        }
        return typeface;
    }
}