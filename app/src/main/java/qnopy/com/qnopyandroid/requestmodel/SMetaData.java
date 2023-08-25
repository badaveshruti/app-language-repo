package qnopy.com.qnopyandroid.requestmodel;

public class SMetaData {

    private Integer locationId;
    private String locationIds;
    private Integer lovId;
    private Integer siteId;
    private Integer mobileAppId;
    private Integer fieldParameterId;

    private String fieldParameterLabelAlias;

    private Integer rowOrder;

    //    private Integer columnOrder;
    private String fieldInputType;
    //
    private String nameValuePair;//options will be string array

    //    private String desiredUnits;
//
    private String valueType;
    //
//    private SLov lov;
//
    private boolean required;//mandatory
    private Integer mandatoryField;
    //
    private Double warningHigh;//warningUpperLimit
    //
    private Double warningLow;//warningLowerLimit
    //
    private Double highLimit;//notToExceedUpperLimit
    //
    private Double lowLimit;//notToExceedLowerLimit

    //    private String notes;
//
    private Long creationDate;

    private Long modifiedDate;
//
//    private Integer createdBy;
//
//    private Integer labelWidth;
//
//    private boolean isStandardApp;

    private String defaultValue;

    private String parameterHint;//guide

    private Double objectWidth;

//    private Double objectHeight;

    private boolean showLast2;
    //
    private Double percentDifference;
    //
    private Integer routineId;
    //
    private boolean enableParameterNotes;//enableAdditionalNotes
    private boolean enableParameterTasks;
    //
    private String fieldParameterOperands;//formula
    //
    private String extField1;

    private String extField2;

    private String extField3;
    private String extField7;//fontStyle
    private String parentParameterId;//parentFieldParameterAliasId

    private boolean reviewer;

    private String multinote;//multiNotes

    //added on 	03/06/2021 to replace extField1,2 6 and 7
    private String straightDifference; //for ext1
    private String fieldAction;//for ext2
    private String fieldScore;//for ext6
    private String fontStyle;//for ext7

    private int formFieldId;
    private String status;
    private boolean insert;

    public boolean isInsert() {
        return insert;
    }

    public void setInsert(boolean insert) {
        this.insert = insert;
    }

    public int getFormFieldId() {
        return formFieldId;
    }

    public void setFormFieldId(int formFieldId) {
        this.formFieldId = formFieldId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStraightDifference() {
        return straightDifference;
    }

    public void setStraightDifference(String straightDifference) {
        this.straightDifference = straightDifference;
    }

    public String getFieldAction() {
        return fieldAction;
    }

    public void setFieldAction(String fieldAction) {
        this.fieldAction = fieldAction;
    }

    public String getFieldScore() {
        return fieldScore;
    }

    public void setFieldScore(String fieldScore) {
        this.fieldScore = fieldScore;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public String getLocationIds() {
        return locationIds;
    }

    public void setLocationIds(String locationIds) {
        this.locationIds = locationIds;
    }

    public String getExtField7() {
        return extField7;
    }

    public void setExtField7(String extField7) {
        this.extField7 = extField7;
    }

    public String getMultinote() {
        return multinote;
    }

    public void setMultinote(String multinote) {
        this.multinote = multinote;
    }

    public boolean isReviewer() {
        return reviewer;
    }

    public void setReviewer(boolean reviewer) {
        this.reviewer = reviewer;
    }

    //    private String extField4;
//
//    private String extField5;
//
//    private String extField6;
//
//    private String extField7;


    public Integer getMandatoryField() {
        return mandatoryField;
    }

    public void setMandatoryField(Integer mandatoryField) {
        this.mandatoryField = mandatoryField;
    }

    // TODO: 29-Nov-16
    public String getParentParameterId() {
        return parentParameterId;
    }

    public void setParentParameterId(String parentParameterId) {
        this.parentParameterId = parentParameterId;
    }

    public boolean isShowLast2() {
        return showLast2;
    }

    public void setShowLast2(boolean showLast2) {
        this.showLast2 = showLast2;
    }

    public boolean isEnableParameterNotes() {
        return enableParameterNotes;
    }

    public void setEnableParameterNotes(boolean enableParameterNotes) {
        this.enableParameterNotes = enableParameterNotes;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public Integer getMobileAppId() {
        return mobileAppId;
    }

    public void setMobileAppId(Integer mobileAppId) {
        this.mobileAppId = mobileAppId;
    }

    public Integer getFieldParameterId() {
        return fieldParameterId;
    }

    public void setFieldParameterId(Integer fieldParameterId) {
        this.fieldParameterId = fieldParameterId;
    }


//    public List<SMobileApp> getParentMobileApps() {
//        if (null == parentMobileApps) {
//            parentMobileApps = new LinkedList<SMobileApp>();
//        }
//        return parentMobileApps;
//    }
//
//    public void setParentMobileApps(List<SMobileApp> parentMobileApps) {
//        this.parentMobileApps = parentMobileApps;
//    }


    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

//    public RFieldParameter getFieldParameter() {
//        return fieldParameter;
//    }
//
//    public void setFieldParameter(RFieldParameter fieldParameter) {
//        this.fieldParameter = fieldParameter;
//    }

//    public SLocation getLocation() {
//        return location;
//    }
//
//    public void setLocation(SLocation location) {
//        this.location = location;
//    }

//    public SMobileApp getMobileApp() {
//        return mobileApp;
//    }
//
//    public void setMobileApp(SMobileApp mobileApp) {
//        this.mobileApp = mobileApp;
//    }

//    public SSite getSite() {
//        return site;
//    }
//
//    public void setSite(SSite site) {
//        this.site = site;
//    }

    public Integer getRowOrder() {
        return rowOrder;
    }

    public void setRowOrder(Integer rowOrder) {
        this.rowOrder = rowOrder;
    }

//    public Integer getColumnOrder() {
//        return columnOrder;
//    }
//
//    public void setColumnOrder(Integer columnOrder) {
//        this.columnOrder = columnOrder;
//    }

    public String getFieldInputType() {
        return fieldInputType;
    }

    public void setFieldInputType(String fieldInputType) {
        this.fieldInputType = fieldInputType;
    }

    public String getNameValuePair() {
        return nameValuePair;
    }

    public void setNameValuePair(String nameValuePair) {
        this.nameValuePair = nameValuePair;
    }

//    public String getDesiredUnits() {
//        return desiredUnits;
//    }
//
//    public void setDesiredUnits(String desiredUnits) {
//        this.desiredUnits = desiredUnits;
//    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public Integer getLovId() {
        return lovId;
    }

    public void setLovId(Integer lovId) {
        this.lovId = lovId;
    }


    public Double getWarningHigh() {
        return warningHigh;
    }

    public void setWarningHigh(Double warningHigh) {
        this.warningHigh = warningHigh;
    }

    public Double getWarningLow() {
        return warningLow;
    }

    public void setWarningLow(Double warningLow) {
        this.warningLow = warningLow;
    }

    public Double getHighLimit() {
        return highLimit;
    }

    public void setHighLimit(Double highLimit) {
        this.highLimit = highLimit;
    }

    public Double getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(Double lowLimit) {
        this.lowLimit = lowLimit;
    }

    //    public String getNotes() {
//        return notes;
//    }
//
//    public void setNotes(String notes) {
//        this.notes = notes;
//    }
//
    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
//
//    public Integer getCreatedBy() {
//        return createdBy;
//    }
//
//    public void setCreatedBy(Integer createdBy) {
//        this.createdBy = createdBy;
//    }

    private Integer id;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFieldParameterLabelAlias() {
        return this.fieldParameterLabelAlias;
    }

    public void setFieldParameterLabelAlias(String fieldParameterLabelAlias) {
        this.fieldParameterLabelAlias = fieldParameterLabelAlias;
    }
//
//    public Integer getLabelWidth() {
//        return this.labelWidth;
//    }
//
//    public void setLabelWidth(Integer labelWidth) {
//        this.labelWidth = labelWidth;
//    }
//
//    public boolean isIsStandardApp() {
//        return this.isStandardApp;
//    }
//
//    public void setIsStandardApp(boolean isStandardApp) {
//        this.isStandardApp = isStandardApp;
//    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getParameterHint() {
        return this.parameterHint;
    }

    public void setParameterHint(String parameterHint) {
        this.parameterHint = parameterHint;
    }

//    public Long getParentParameterId() {
//        return this.parentParameterId;
//    }
//
//    public void setParentParameterId(Long parentParameterId) {
//        this.parentParameterId = parentParameterId;
//    }

    public Double getObjectWidth() {
        return this.objectWidth;
    }

    public void setObjectWidth(Double objectWidth) {
        this.objectWidth = objectWidth;
    }

//    public Double getObjectHeight() {
//        return this.objectHeight;
//    }
//
//    public void setObjectHeight(Double objectHeight) {
//        this.objectHeight = objectHeight;
//    }


    public Double getPercentDifference() {
        return this.percentDifference;
    }

    public void setPercentDifference(Double percentDifference) {
        this.percentDifference = percentDifference;
    }

    public Integer getRoutineId() {
        return this.routineId;
    }

    public void setRoutineId(Integer routineId) {
        this.routineId = routineId;
    }


    public String getFieldParameterOperands() {
        return this.fieldParameterOperands;
    }

    public void setFieldParameterOperands(String fieldParameterOperands) {
        this.fieldParameterOperands = fieldParameterOperands;
    }

    public String getExtField1() {
        return this.extField1;
    }

    public void setExtField1(String extField1) {
        this.extField1 = extField1;
    }

    public String getExtField2() {
        return this.extField2;
    }

    public void setExtField2(String extField2) {
        this.extField2 = extField2;
    }

    public String getExtField3() {
        return this.extField3;
    }

    public void setExtField3(String extField3) {
        this.extField3 = extField3;
    }

    public boolean isEnableParameterTasks() {
        return enableParameterTasks;
    }

    public void setEnableParameterTasks(boolean enableParameterTasks) {
        this.enableParameterTasks = enableParameterTasks;
    }


    //    public String getExtField4() {
//        return this.extField4;
//    }
//
//    public void setExtField4(String extField4) {
//        this.extField4 = extField4;
//    }
//
//    public String getExtField5() {
//        return this.extField5;
//    }
//
//    public void setExtField5(String extField5) {
//        this.extField5 = extField5;
//    }
//
//    public String getExtField6() {
//        return this.extField6;
//    }
//
//    public void setExtField6(String extField6) {
//        this.extField6 = extField6;
//    }
//
//    public String getExtField7() {
//        return this.extField7;
//    }
//
//    public void setExtField7(String extField7) {
//        this.extField7 = extField7;
//    }
//
//    public SLov getLov() {
//        return this.lov;
//    }
//
//    public void setLov(SLov lov) {
//        this.lov = lov;
//    }
}
