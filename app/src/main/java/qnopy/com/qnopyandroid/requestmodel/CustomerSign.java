package qnopy.com.qnopyandroid.requestmodel;

import android.view.View;

public class CustomerSign {
    private int id;
    private String name;
    private String filepath;
    private View view;
    private String attachmentUUID;

    public String getAttachmentUUID() {
        return attachmentUUID;
    }

    public void setAttachmentUUID(String attachmentUUID) {
        this.attachmentUUID = attachmentUUID;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

}
