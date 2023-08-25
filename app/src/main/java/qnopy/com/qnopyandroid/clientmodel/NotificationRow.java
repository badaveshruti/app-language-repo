package qnopy.com.qnopyandroid.clientmodel;

public class NotificationRow {

    public String title, info, date;
    public int id, status, operationCode, userID, formID, siteID, eventID;
    public int cocId;
    public String fileName;

    public NotificationRow(int row_id, String title, String info,
                           int status, String timestamp, int operationCode,
                           int userID, int formID, int siteID, int eventID, String fileName, int cocId) {
        this.id = row_id;
        this.title = title;
        this.info = info;
        this.status = status;
        this.date = timestamp;
        this.operationCode = operationCode;
        this.userID = userID;
        this.formID = formID;
        this.siteID = siteID;
        this.eventID = eventID;
        this.cocId = cocId;
        this.fileName = fileName;
    }
}
