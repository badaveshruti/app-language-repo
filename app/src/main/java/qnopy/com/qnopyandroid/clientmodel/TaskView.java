package qnopy.com.qnopyandroid.clientmodel;

/**
 * Created by QNOPY on 7/20/2017.
 */

public class TaskView {

    private String taskname;
    private String location;
    private int eventId;
    private String  formname;
    private String  taskduedate;
    private String  taskstartdate;
    private int  locationId;
    private String status;
    private String woTaskID;

    public String getWoTaskID() {
        return woTaskID;
    }

    public void setWoTaskID(String woTaskID) {
        this.woTaskID = woTaskID;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTaskstartdate() {
        return taskstartdate;
    }

    public void setTaskstartdate(String taskstartdate) {
        this.taskstartdate = taskstartdate;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFormname() {
        return formname;
    }

    public void setFormname(String formname) {
        this.formname = formname;
    }

    public String getTaskduedate() {
        return taskduedate;
    }

    public void setTaskduedate(String taskduedate) {
        this.taskduedate = taskduedate;
    }
}
