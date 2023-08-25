package qnopy.com.qnopyandroid.clientmodel;

/**
 * Created by QNOPY on 7/19/2017.
 */

public class TaskFromSite {
    private String planname;
    private String taskcount;
    private String wo_planendDate;
    private String siteid;
    private String workorderid;
    private String parentappid;
    private String taskid;
    private String w0_planstartdate;
    private String formname;
    private String sitename;

    public String getFormname() {
        return formname;
    }

    public void setFormname(String formname) {
        this.formname = formname;
    }

    public String getSitename() {
        return sitename;
    }

    public void setSitename(String sitename) {
        this.sitename = sitename;
    }

    public String getSiteid() {
        return siteid;
    }

    public void setSiteid(String siteid) {
        this.siteid = siteid;
    }

    public String getWorkorderid() {
        return workorderid;
    }

    public void setWorkorderid(String workorderid) {
        this.workorderid = workorderid;
    }

    public String getParentappid() {
        return parentappid;
    }

    public void setParentappid(String parentappid) {
        this.parentappid = parentappid;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getPlanname() {
        return planname;
    }

    public void setPlanname(String planname) {
        this.planname = planname;
    }

    public String getTaskcount() {
        return taskcount;
    }

    public void setTaskcount(String taskcount) {
        this.taskcount = taskcount;
    }

    public String getWo_planendDate() {
        return wo_planendDate;
    }

    public void setWo_planendDate(String wo_planendDate) {
        this.wo_planendDate = wo_planendDate;
    }

    public String getW0_planstartdate() {
        return w0_planstartdate;
    }

    public void setW0_planstartdate(String w0_planstartdate) {
        this.w0_planstartdate = w0_planstartdate;
    }
}
