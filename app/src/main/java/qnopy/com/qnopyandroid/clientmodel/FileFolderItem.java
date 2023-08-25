package qnopy.com.qnopyandroid.clientmodel;

/**
 * Created by Yogendra on 21-Jan-16.
 */
public class FileFolderItem {
    String itemID;
    String itemTitle;
    String itemType;    //To Check whether it is file or folder
    String itemGuid;
    String itemPath;

    public String getItemPath() {
        return itemPath;
    }

    public void setItemPath(String itemPath) {
        this.itemPath = itemPath;
    }

    public String getItemGuid() {
        return itemGuid;
    }

    public void setItemGuid(String itemGuid) {
        this.itemGuid = itemGuid;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    @Override
    public String toString() {
        return "{" +
                "itemTitle='" + itemTitle + '\'' +
                ", itemType='" + itemType + '\'' +
                '}';
    }
}
