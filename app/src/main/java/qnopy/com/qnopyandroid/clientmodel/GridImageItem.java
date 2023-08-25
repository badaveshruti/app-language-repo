package qnopy.com.qnopyandroid.clientmodel;

import java.io.Serializable;

public class GridImageItem implements Serializable {
    //    private Bitmap image;
    private String image_path;

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }


    public GridImageItem(String image_path) {
        super();
//        this.image = image;
        this.image_path = image_path;
    }

//    public Bitmap getImage() {
//        return image;
//    }
//
//    public void setImage(Bitmap image) {
//        this.image = image;
//    }


}
