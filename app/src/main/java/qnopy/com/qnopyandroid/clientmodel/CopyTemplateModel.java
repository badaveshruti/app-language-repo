package qnopy.com.qnopyandroid.clientmodel;

import java.util.ArrayList;

public class CopyTemplateModel {
    private ArrayList<CopyData> copiedData;

    public ArrayList<CopyData> getCopiedData() {
        return copiedData;
    }

    public void setCopiedData(ArrayList<CopyData> copiedData) {
        this.copiedData = copiedData;
    }

    public static class CopyData {
        private String stringValue;

        private String extField1; //will have setId

        private String notes;

        private int mobileAppId;

        private int fieldParameterId;

        private int siteId;

        private int userId;

        private String fieldParameterLabel;

        public String getStringValue() {
            return stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        public String getExtField1() {
            return extField1;
        }

        public void setExtField1(String extField1) {
            this.extField1 = extField1;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public int getMobileAppId() {
            return mobileAppId;
        }

        public void setMobileAppId(int mobileAppId) {
            this.mobileAppId = mobileAppId;
        }

        public int getFieldParameterId() {
            return fieldParameterId;
        }

        public void setFieldParameterId(int fieldParameterId) {
            this.fieldParameterId = fieldParameterId;
        }

        public int getSiteId() {
            return siteId;
        }

        public void setSiteId(int siteId) {
            this.siteId = siteId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getFieldParameterLabel() {
            return fieldParameterLabel;
        }

        public void setFieldParameterLabel(String fieldParameterLabel) {
            this.fieldParameterLabel = fieldParameterLabel;
        }
    }

}
