package qnopy.com.qnopyandroid.responsemodel;

import java.util.List;

/**
 * Created by shantanu on 7/30/16.
 */
public class newFormLabelResponse {

    private int locationId;
    private int siteId;
    private int mobileAppId;
    private String fieldParameterId;
    private String fieldParameterLabelAlias;
    private String fieldInputType;
    private String valueType;
    private String nameValuePair;
    private String defaultValue;
    private String lowLimit;
    private String highLimit;
    private String warningHigh;
    private String warningLow;
    private String objectWidth;
    private String fieldParameterOperands;
    private String percentDifference;
    private String routineId;
    private String parameterHint;
    private String extField1;
    private String extField2;
    private String extField3;
    private String lovId;
    private String rowOrder;
    private String showLast2;
    private String required;
    private String enableParameterNotes;

    private String stateDifference; //for ext1
    private String fieldAction;//for ext2
    private String fieldScore;//for ext6
    private String fontStyle;//for ext7

    public String getStateDifference() {
        return stateDifference;
    }

    public void setStateDifference(String stateDifference) {
        this.stateDifference = stateDifference;
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

    public newFormLabelResponse() {
    }

    public newFormLabelResponse(List<newFormLabelResponse> labelData) {
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getMobileAppId() {
        return mobileAppId;
    }

    public void setMobileAppId(int mobileAppId) {
        this.mobileAppId = mobileAppId;
    }


    public String getFieldParameterId() {
        return fieldParameterId;
    }

    public void setFieldParameterId(String fieldParameterId) {
        this.fieldParameterId = fieldParameterId;
    }

    public String getFieldParameterLabelAlias() {
        return fieldParameterLabelAlias;
    }

    public void setFieldParameterLabelAlias(String fieldParameterLabelAlias) {
        this.fieldParameterLabelAlias = fieldParameterLabelAlias;
    }

    public String getFieldInputType() {
        return fieldInputType;
    }

    public void setFieldInputType(String fieldInputType) {
        this.fieldInputType = fieldInputType;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getNameValuePair() {
        return nameValuePair;
    }

    public void setNameValuePair(String nameValuePair) {
        this.nameValuePair = nameValuePair;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(String lowLimit) {
        this.lowLimit = lowLimit;
    }

    public String getHighLimit() {
        return highLimit;
    }

    public void setHighLimit(String highLimit) {
        this.highLimit = highLimit;
    }

    public String getWarningHigh() {
        return warningHigh;
    }

    public void setWarningHigh(String warningHigh) {
        this.warningHigh = warningHigh;
    }

    public String getWarningLow() {
        return warningLow;
    }

    public void setWarningLow(String warningLow) {
        this.warningLow = warningLow;
    }

    public String getObjectWidth() {
        return objectWidth;
    }

    public void setObjectWidth(String objectWidth) {
        this.objectWidth = objectWidth;
    }

    public String getFieldParameterOperands() {
        return fieldParameterOperands;
    }

    public void setFieldParameterOperands(String fieldParameterOperands) {
        this.fieldParameterOperands = fieldParameterOperands;
    }

    public String getPercentDifference() {
        return percentDifference;
    }

    public void setPercentDifference(String percentDifference) {
        this.percentDifference = percentDifference;
    }

    public String getRoutineId() {
        return routineId;
    }

    public void setRoutineId(String routineId) {
        this.routineId = routineId;
    }

    public String getParameterHint() {
        return parameterHint;
    }

    public void setParameterHint(String parameterHint) {
        this.parameterHint = parameterHint;
    }

    public String getExtField1() {
        return extField1;
    }

    public void setExtField1(String extField1) {
        this.extField1 = extField1;
    }

    public String getExtField2() {
        return extField2;
    }

    public void setExtField2(String extField2) {
        this.extField2 = extField2;
    }

    public String getExtField3() {
        return extField3;
    }

    public void setExtField3(String extField3) {
        this.extField3 = extField3;
    }

    public String getLovId() {
        return lovId;
    }

    public void setLovId(String lovId) {
        this.lovId = lovId;
    }

    public String getRowOrder() {
        return rowOrder;
    }

    public void setRowOrder(String rowOrder) {
        this.rowOrder = rowOrder;
    }

    public String getShowLast2() {
        return showLast2;
    }

    public void setShowLast2(String showLast2) {
        this.showLast2 = showLast2;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getEnableParameterNotes() {
        return enableParameterNotes;
    }

    public void setEnableParameterNotes(String enableParameterNotes) {
        this.enableParameterNotes = enableParameterNotes;
    }

    @Override
    public String toString() {
        return this.fieldParameterLabelAlias;
    }
}
