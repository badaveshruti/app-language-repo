package qnopy.com.qnopyandroid.fetchdraw;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class AndroiUtils {
   public static Bitmap rotateImage(Bitmap src, float degree) {
       // create new matrix object
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(degree);
        // return new bitmap rotated using matrix
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
}

 

 /*int width = bitmapOrg.getWidth();

 int height = bitmapOrg.getHeight();


 int newWidth = 200;

 int newHeight  = 200;

 // calculate the scale - in this case = 0.4f

  float scaleWidth = ((float) newWidth) / width;

  float scaleHeight = ((float) newHeight) / height;

  Matrix matrix = new Matrix();

  matrix.postScale(scaleWidth, scaleHeight);
  matrix.postRotate(x);
  // this will create image with new size
  Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,width, height, matrix, true);

  iv.setScaleType(ScaleType.CENTER);
  iv.setImageBitmap(resizedBitmap);*/