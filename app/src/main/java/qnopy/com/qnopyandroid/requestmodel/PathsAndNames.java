package qnopy.com.qnopyandroid.requestmodel;

import android.os.Parcel;
import android.os.Parcelable;

public class PathsAndNames implements Parcelable {
    private String path;
    private String name;

    public PathsAndNames() {
    }

    public PathsAndNames(Parcel in) {
        this.path = in.readString();
        this.name = in.readString();
    }

    public PathsAndNames(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel arg0, int arg1) {
        arg0.writeString(this.path);
        arg0.writeString(this.name);
    }

    public static final Creator CREATOR = new Creator() {
        public PathsAndNames createFromParcel(Parcel in) {
            return new PathsAndNames(in);
        }

        public PathsAndNames[] newArray(int size) {
            return new PathsAndNames[size];
        }
    };

}
