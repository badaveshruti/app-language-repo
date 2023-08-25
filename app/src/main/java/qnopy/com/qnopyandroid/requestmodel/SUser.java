package qnopy.com.qnopyandroid.requestmodel;

import java.io.Serializable;

public class SUser implements Serializable {

    //private String userRole;
    private int activationCode;
    private int planId;
    private SCompany company;
    private String userNumber;
    private String userName;
    private String userGuid;
    private String password;

    // private Short role;

    private String passKey;
    private String firstName;
    private String lastName;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zipCode;
    private String companyId;
    private String companyName;
    private String roleName;
    private String roleId;

    private String toEmailList;
    private String ccEmailList;
    private String notes;
    private String primaryEmail;
    private Long creationDate;
    private Integer createdBy;
    private String confirmPassword;
    private String userType;
    private Integer status;
    private Integer userRole;
    private String userAppType;

    //this has millis of trial period
    private String trialStatus;
    private boolean passwordChangeRequired;

    public String getTrialStatus() {
        return trialStatus;
    }

    public void setTrialStatus(String trialStatus) {
        this.trialStatus = trialStatus;
    }

    public String getUserAppType() {
        return userAppType;
    }

    public void setUserAppType(String userAppType) {
        this.userAppType = userAppType;
    }

    public Integer getUserRole() {
        return userRole;
    }

    public void setUserRole(Integer userRole) {
        this.userRole = userRole;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public int getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(int activationCode) {
        this.activationCode = activationCode;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserGuid() {
        return userGuid;
    }

    public void setUserGuid(String userGuid) {
        this.userGuid = userGuid;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public boolean isPasswordChangeRequired() {
        return passwordChangeRequired;
    }

    public void setPasswordChangeRequired(boolean passwordChangeRequired) {
        this.passwordChangeRequired = passwordChangeRequired;
    }

    public SCompany getCompany() {
        return company;
    }

    public void setCompany(SCompany company) {
        this.company = company;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getPassKey() {
        return passKey;
    }

    public void setPassKey(String passKey) {
        this.passKey = passKey;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    private Integer userId;
    private String alternateEmail;
    String contactnumber;

    public String getContactnumber() {
        return contactnumber;
    }

    public void setContactnumber(String contactnumber) {
        this.contactnumber = contactnumber;
    }

    public String getAlternateEmail() {
        return alternateEmail;
    }

    public void setAlternateEmail(String alternateEmail) {
        this.alternateEmail = alternateEmail;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }


    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer id) {
        this.userId = id;
    }


    public String getToEmailList() {
        return this.toEmailList;
    }

    public void setToEmailList(String toEmailList) {
        this.toEmailList = toEmailList;
    }

    public String getCcEmailList() {
        return this.ccEmailList;
    }

    public void setCcEmailList(String ccEmailList) {
        this.ccEmailList = ccEmailList;
    }


}
