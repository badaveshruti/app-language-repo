package qnopy.com.qnopyandroid.requestmodel;

/**
 * Created by shantanu on 6/8/17.
 */

public class WorkOrderTask {
    private Integer locationId;
    private String locationName;
    private Integer taskId;
    private String taskDescription;
    private Integer workOrderId;
    private Integer workOrderTaskId;
    private String planName;
    private String taskName;
    private Integer cocFlag;
    private Integer userId;
    private Double latitude;
    private Double longitude;
    private String Instruction;
    private String formId;
    private String planStartDate;
    private String planEndDate;
    private String status;
    private String woPlanStartDate;
    private String woPlanEndDate;

    public String getWoPlanStartDate() {
        return woPlanStartDate;
    }

    public void setWoPlanStartDate(String woPlanStartDate) {
        this.woPlanStartDate = woPlanStartDate;
    }

    public String getWoPlanEndDate() {
        return woPlanEndDate;
    }

    public void setWoPlanEndDate(String woPlanEndDate) {
        this.woPlanEndDate = woPlanEndDate;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getPlanStartDate() {
        return planStartDate;
    }

    public void setPlanStartDate(String planStartDate) {
        this.planStartDate = planStartDate;
    }

    public String getPlanEndDate() {
        return planEndDate;
    }

    public void setPlanEndDate(String planEndDate) {
        this.planEndDate = planEndDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getInstruction() {
        return Instruction;
    }

    public void setInstruction(String instruction) {
        Instruction = instruction;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public Integer getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Integer workOrderId) {
        this.workOrderId = workOrderId;
    }

    public Integer getWorkOrderTaskId() {
        return workOrderTaskId;
    }

    public void setWorkOrderTaskId(Integer workOrderTaskId) {
        this.workOrderTaskId = workOrderTaskId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getCocFlag() {
        return cocFlag;
    }

    public void setCocFlag(Integer cocFlag) {
        this.cocFlag = cocFlag;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
