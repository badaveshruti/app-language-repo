package qnopy.com.qnopyandroid.clientmodel;

import android.graphics.Bitmap;
import java.io.Serializable;

public class ConstructionBitmapCaptionDataModel implements Serializable {

    Bitmap mBitmap;
    String mCaption;

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public String getmCaption() {
        return mCaption;
    }

    public void setmCaption(String mCaption) {
        this.mCaption = mCaption;
    }
}
