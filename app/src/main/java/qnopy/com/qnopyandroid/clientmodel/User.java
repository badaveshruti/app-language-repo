package qnopy.com.qnopyandroid.clientmodel;

public class User {
    int userID;
    String userName;
    String userPasswd;
    String toMail;
    String ccMail;
    String userGuid;
    String notes;//App_Type
    int companyID;
    private String firstName;
    private String lastName;
    private int userRole;
    private String userAppType;

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

    public int getUserRole() {
        return userRole;
    }

    public void setUserRole(int userRole) {
        this.userRole = userRole;
    }

    public String getUserAppType() {
        return userAppType;
    }

    public void setUserAppType(String userAppType) {
        this.userAppType = userAppType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUserGuid() {
        return userGuid;
    }

    public void setUserGuid(String userGuid) {
        this.userGuid = userGuid;
    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int compnyID) {
        this.companyID = compnyID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int id) {
        userID = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        userName = name;
    }

    public String getUserPasswd() {
        return userPasswd;
    }

    public void setUserPasswd(String passwd) {
        userPasswd = passwd;
    }

    public String getToMail() {
        return toMail;
    }

    public void setToMail(String mail) {
        toMail = mail;
    }

    public String getCCMail() {
        return ccMail;
    }

    public void setCCMail(String mail) {
        ccMail = mail;
    }
}
