package qnopy.com.qnopyandroid.clientmodel;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by QNOPY on 11/2/2017.
 */

public class clusterphoto implements ClusterItem {

    public String name;
    public Drawable profilePhoto;
    private LatLng mPosition;

    public clusterphoto(String name, Drawable profilePhoto, LatLng mPosition) {
        this.name = name;
        this.profilePhoto = profilePhoto;
        this.mPosition = mPosition;
    }

    public clusterphoto() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfilePhoto(Drawable profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public void setmPosition(LatLng mPosition) {
        this.mPosition = mPosition;
    }

    public Drawable getProfilePhoto() {
        return profilePhoto;
    }

    public LatLng getmPosition() {
        return mPosition;
    }


    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Nullable
    @Override
    public String getTitle() {
        return name;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return null;
    }
}
