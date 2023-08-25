package qnopy.com.qnopyandroid.clientmodel;

public class FieldData {
    int EventID;// INT NOT NULL REFERENCES d_Event (EventID)
    String LocationID;// INT NOT NULL REFERENCES s_Location (LocationID)
    int FieldParameterID;// INT NOT NULL REFERENCES s_FieldParameter
    // (FieldParameterID)
    String FieldPameterLabel;
    long MeasurementTime;// LONG NOT NULL
    String StringValue;// VARCHAR(100)
    double NumericValue;// REAL
    String Units;// VARCHAR(50)
    double Latitude;// REAL
    double Longitude;// REAL
    String ExtField1;// VARCHAR(100)
    String ExtField2;// VARCHAR(100)
    String ExtField3;// VARCHAR(100)
    String ExtField4;// VARCHAR(100)
    String ExtField5;// VARCHAR(100)
    String ExtField6;// VARCHAR(100)
    String ExtField7;// VARCHAR(100)
    String Notes;// VARCHAR(200)
    long CreationDate;// long
    String EmailSentFlag;// VARCHAR(1)
    String DataSyncFlag;// VARCHAR(1)
    private Long modificationDate;
    String modifiedByDeviceId;
    String modifiedBy;

    public String getParameterHint() {
        return parameterHint;
    }

    public void setParameterHint(String parameterHint) {
        this.parameterHint = parameterHint;
    }

    String parameterHint;

    public String getModifiedByDeviceId() {
        return modifiedByDeviceId;
    }

    public void setModifiedByDeviceId(String modifiedByDeviceId) {
        this.modifiedByDeviceId = modifiedByDeviceId;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    int FieldDataID;// INTEGER PRIMARY KEY AUTOINCREMENT

    int siteID, MobileAppID, UserID, parent_set_id;
    String deviceId;
    double correctedLatitude;// REAL
    double correctedLongitude;// REAL

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public double getCorrectedLatitude() {
        return correctedLatitude;
    }

    public void setCorrectedLatitude(double correctedLatitude) {
        this.correctedLatitude = correctedLatitude;
    }

    public double getCorrectedLongitude() {
        return correctedLongitude;
    }

    public void setCorrectedLongitude(double correctedLongitude) {
        this.correctedLongitude = correctedLongitude;
    }

    public String getFieldPameterLabel() {
        return FieldPameterLabel;
    }

    public void setFieldPameterLabel(String fieldPameterLabel) {
        FieldPameterLabel = fieldPameterLabel;
    }

    public double getNumericValue() {
        return NumericValue;
    }

    public void setNumericValue(double numericValue) {
        NumericValue = numericValue;
    }

    public int getParent_set_id() {
        return parent_set_id;
    }

    public void setParent_set_id(int parent_set_id) {
        this.parent_set_id = parent_set_id;
    }

    public int getEventID() {
        return EventID;
    }

    public void setEventID(int id) {
        EventID = id;
    }

    public String getLocationID() {
        return LocationID;
    }

    public void setLocationID(String loc) {
        LocationID = loc;
    }

    public int getFieldParameterID() {
        return FieldParameterID;
    }

    public String getFieldParameterLabel() {
        return FieldPameterLabel;
    }

    public void setFieldParameterLabel(String label) {
        FieldPameterLabel = label;
    }

    public long getMeasurementTime() {
        return MeasurementTime;
    }

    public void setMeasurementTime(long time) {
        MeasurementTime = time;
    }

    public String getStringValue() {
        return StringValue;
    }

    public double NumericValue() {
        return NumericValue;
    }

    public String getUnits() {
        return Units;
    }

    public void setUnits(String units) {
        Units = units;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double value) {
        Latitude = value;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double value) {
        Longitude = value;
    }

    public String getExtField1() {
        return ExtField1;
    }

    public String getExtField2() {
        return ExtField2;
    }

    public String getExtField3() {
        return ExtField3;
    }

    public String getExtField4() {
        return ExtField4;
    }

    public String getExtField5() {
        return ExtField5;
    }

    public String getExtField6() {
        return ExtField6;
    }

    public String getExtField7() {
        return ExtField7;
    }

    public void setExtField1(String extField1) {
        this.ExtField1 = extField1;
    }

    public void setExtField2(String extField2) {
        this.ExtField2 = extField2;
    }

    public void setExtField3(String extField3) {
        this.ExtField3 = extField3;
    }

    public void setExtField4(String extField4) {
        this.ExtField4 = extField4;
    }

    public void setExtField5(String extField5) {
        this.ExtField5 = extField5;
    }

    public void setExtField6(String extField6) {
        this.ExtField6 = extField6;
    }

    public void setExtField7(String extField7) {
        this.ExtField7 = extField7;
    }


    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }


    public void setCreationDate(long date) {
        CreationDate = date;
    }

    public long getCreationDate() {
        return CreationDate;
    }

    public Long getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Long modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getEmailSentFlag() {
        return EmailSentFlag;
    }

    public void setEmailSentFlag(String flag) {
        EmailSentFlag = flag;
    }

    public String getDataSyncFlag() {
        return DataSyncFlag;
    }

    public void setDataSyncFlag(String flag) {
        DataSyncFlag = flag;
    }

    public int getFieldDataID() {
        return FieldDataID;
    }

    public void setFieldDataID(int id) {
        FieldDataID = id;
    }

    public void setStringValue(String value) {
        StringValue = value;
    }

    public void setStringValuePrintUseOnly(String value) {
        if (value != null)
            StringValue = value;
        else
            StringValue = "";
    }

    public void setFieldParameterID(int id) {
        FieldParameterID = id;
    }

    public void setCurSetID(int ID) {
        ExtField1 = Integer.toString(ID);

    }

    public int getSiteID() {
        return this.siteID;
    }

    public void setSiteID(int id) {
        siteID = id;
    }

    public int getMobileAppID() {
        return this.MobileAppID;
    }

    public void setMobileAppID(int id) {
        MobileAppID = id;
    }

    public int getUserID() {
        return this.UserID;
    }

    public void setUserID(int id) {
        UserID = id;
    }

}