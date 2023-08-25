package qnopy.com.qnopyandroid.clientmodel;

//get meta data for particular site and mobile app and location

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedHashMap;
import java.util.List;

public class MetaData implements Parcelable {

    int FieldParameterID;
    int FormID = 0;
    int currentFormID = 0;
    public int LovID;
    public int L_itemID;

    public String locationIds;

    public String ParamLabel;
    public int RowOrder;
    public int ColumnOrder;
    public String InputType;
    private String NameValuePair;
    public String DesiredUnits;
    public String ValueType;

    public float WarningHigh;
    public float WarningLow;
    public float HighLimit;
    public float LowLimit;
    private Integer labelWidth;

    private boolean isStandardApp;

    private String defaultValue;

    private String parameterHint;

    private Long parentParameterId;

    private Double objectWidth;

    private Double objectHeight;

    private boolean isShowLast2;

    private Double percentDifference;

    private Integer routineId;

    private boolean isEnableParameterNotes;

    private String fieldParameterOperands;

    private String extField1;

    private String extField2;

    private String extField3;

    private String extField4;

    private String extField5;

    private String extField6;

    private String extField7;

    private boolean isReviewer;
    public boolean isParentField;
    public boolean isChildField;
    public boolean isVisible = false;
    public boolean isRowVisible = false;
    public boolean isExpanded = false;
    public boolean isRendered = false;
    public List<Integer> childParamList;

    private View form_field_row;
//    private FormMaster.ViewHolder row_holder;

    public String Required_Y_N;
    public String mandatoryField;
    public LinkedHashMap<String, String> nameValueMap;

    //added on 	03/06/2021 to replace extField3,2 6 and 7
    private String straightDifference;//for ext3
    private String fieldAction;//for ext2
    private String fieldScore;//for ext6
    private String fontStyle;//for ext7

    //28 Jan, 22 adding for my convenience for forms recyclerview
    private String currentReading;
    private String prevReading1;
    private String prevReading2;
    private String prevReading3;
    private boolean hasNote;
    private Integer parentLovItemId = 0;
    private Integer gotoFormId = 0;
    private RecyclerView.ViewHolder viewHolder;
    private int rowPosition;//used in mapping hidden objects list

    public String getCurrentReading() {
        return currentReading;
    }

    public void setCurrentReading(String currentReading) {
        this.currentReading = currentReading;
    }

    public String getPrevReading1() {
        return prevReading1;
    }

    public void setPrevReading1(String prevReading1) {
        this.prevReading1 = prevReading1;
    }

    public String getPrevReading2() {
        return prevReading2;
    }

    public void setPrevReading2(String prevReading2) {
        this.prevReading2 = prevReading2;
    }

    public String getPrevReading3() {
        return prevReading3;
    }

    public void setPrevReading3(String prevReading3) {
        this.prevReading3 = prevReading3;
    }

    public boolean isHasNote() {
        return hasNote;
    }

    public void setHasNote(boolean hasNote) {
        this.hasNote = hasNote;
    }

    public Integer getParentLovItemId() {
        return parentLovItemId;
    }

    public void setParentLovItemId(Integer parentLovItemId) {
        this.parentLovItemId = parentLovItemId;
    }

    public Integer getGotoFormId() {
        return gotoFormId;
    }

    public void setGotoFormId(Integer gotoFormId) {
        this.gotoFormId = gotoFormId;
    }

    public RecyclerView.ViewHolder getViewHolder() {
        return viewHolder;
    }

    public void setViewHolder(RecyclerView.ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    public int getRowPosition() {
        return rowPosition;
    }

    public void setRowPosition(int rowPosition) {
        this.rowPosition = rowPosition;
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

    public int getCurrentFormID() {
        return currentFormID;
    }

    public void setCurrentFormID(int currentFormID) {
        this.currentFormID = currentFormID;
    }

    public View getForm_field_row() {
        return form_field_row;
    }

    public void setForm_field_row(View form_field_row) {
        this.form_field_row = form_field_row;
    }

    public String getMandatoryField() {
        return mandatoryField;
    }

    public void setMandatoryField(String mandatoryField) {
        this.mandatoryField = mandatoryField;
    }

    public boolean isReviewer() {
        return isReviewer;
    }

    public void setReviewer(boolean reviewer) {
        isReviewer = reviewer;
    }

    public int getL_itemID() {
        return L_itemID;
    }

    public void setL_itemID(int l_itemID) {
        L_itemID = l_itemID;
    }

    public int getFormID() {
        return FormID;
    }

    public void setFormID(int formID) {
        FormID = formID;
    }

    public float getMetaWarningHigh() {
        return WarningHigh;
    }

    public void setMetaWarningHigh(float value) {
        WarningHigh = value;
    }

    public float getMetaWarningLow() {
        return WarningLow;
    }

    public void setMetaWarningLow(float value) {
        WarningLow = value;
    }

    public float getMetaHighLimit() {
        return HighLimit;
    }

    public void setMetaHighLimit(float value) {
        HighLimit = value;
    }

    public float getMetaLowLimit() {
        return LowLimit;
    }

    public void setMetaLowLimit(float value) {
        LowLimit = value;
    }

    public String getMetaRequired_Y_N() {
        return Required_Y_N;
    }

    public void setMetaRequired_Y_N(String type) {
        Required_Y_N = type;
    }

    public String getMetaValueType() {
        return ValueType;
    }

    public void setMetaValueType(String type) {
        ValueType = type;
    }

    public int getMetaLovId() {
        return LovID;
    }

    public void setMetaLovId(int id) {
        LovID = id;
    }

    public String getMetaDesiredUnits() {
        return DesiredUnits;
    }

    public void setMetaDesiredUnits(String units) {
        DesiredUnits = units;
    }

    public String getMetaInputType() {
        return InputType;
    }

    public void setMetaInputType(String type) {
        InputType = type;
    }

    public String getMetaNameValuePair() {
        return NameValuePair;
    }

    public void setMetaNameValuePair(String pair) {
        NameValuePair = pair;

        nameValueMap = new LinkedHashMap<String, String>();
        try {

            if ((NameValuePair != null) && (NameValuePair.length() != 0) && !NameValuePair.equalsIgnoreCase("NULL")) {

                String[] tempPair;

                String[] valuePairArr = NameValuePair.split(",");
                if (valuePairArr != null) {
                    for (int i = 0; i < valuePairArr.length; i++) {
                        Log.e("MetaData", "setMetaNameValuePair() valuepair:" + valuePairArr[i]);
                        if (!valuePairArr[i].contains("^")) {
                            nameValueMap.put(valuePairArr[i], valuePairArr[i]);
                        } else {
                            tempPair = valuePairArr[i].split("\\^");
                            if (tempPair != null) {
                                nameValueMap.put(tempPair[0], tempPair[1]);
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LinkedHashMap<String, String> getNameValueMap() {
        return nameValueMap;
    }

    public int getMetaParamID() {
        return FieldParameterID;
    }

    public void setMetaParamID(int id) {
        this.FieldParameterID = id;
    }

    public String getMetaParamLabel() {
        return ParamLabel;
    }

    public void setMetaParamLabel(String name) {
        this.ParamLabel = name;
    }

    public int getMetaRowOrder() {
        return RowOrder;
    }

    public void setMetaRowOrder(int order) {
        RowOrder = order;
    }

    public int getMetaColOrder() {
        return ColumnOrder;
    }

    public void setMetaColOrder(int order) {
        ColumnOrder = order;
    }

    public Integer getLabelWidth() {
        return this.labelWidth;
    }

    public void setLabelWidth(Integer labelWidth) {
        this.labelWidth = labelWidth;
    }

    public boolean isIsStandardApp() {
        return this.isStandardApp;
    }

    public void setIsStandardApp(boolean isStandardApp) {
        this.isStandardApp = isStandardApp;
    }

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

    public Long getParentParameterId() {
        return this.parentParameterId;
    }

    public void setParentParameterId(Long parentParameterId) {
        this.parentParameterId = parentParameterId;
    }

    public Double getObjectWidth() {
        return this.objectWidth;
    }

    public void setObjectWidth(Double objectWidth) {
        this.objectWidth = objectWidth;
    }

    public Double getObjectHeight() {
        return this.objectHeight;
    }

    public void setObjectHeight(Double objectHeight) {
        this.objectHeight = objectHeight;
    }

    public boolean isIsShowLast2() {
        return this.isShowLast2;
    }

    public void setIsShowLast2(boolean isShowLast2) {
        this.isShowLast2 = isShowLast2;
    }

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

    public boolean isIsEnableParameterNotes() {
        return this.isEnableParameterNotes;
    }

    public void setIsEnableParameterNotes(boolean isEnableParameterNotes) {
        this.isEnableParameterNotes = isEnableParameterNotes;
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

    public String getExtField4() {
        return this.extField4;
    }

    public void setExtField4(String extField4) {
        this.extField4 = extField4;
    }

    public String getExtField5() {
        return this.extField5;
    }

    public void setExtField5(String extField5) {
        this.extField5 = extField5;
    }

    public String getExtField6() {
        return this.extField6;
    }

    public void setExtField6(String extField6) {
        this.extField6 = extField6;
    }

    public String getExtField7() {
        return this.extField7;
    }

    public void setExtField7(String extField7) {
        this.extField7 = extField7;
    }

    public MetaData() {
        super();
    }

    public MetaData(Parcel in) {
        FieldParameterID = in.readInt();
        FormID = in.readInt();
        currentFormID = in.readInt();
        LovID = in.readInt();
        L_itemID = in.readInt();
        ParamLabel = in.readString();
        RowOrder = in.readInt();
        ColumnOrder = in.readInt();
        InputType = in.readString();
        NameValuePair = in.readString();
        DesiredUnits = in.readString();
        ValueType = in.readString();
        WarningHigh = in.readFloat();
        WarningLow = in.readFloat();
        HighLimit = in.readFloat();
        LowLimit = in.readFloat();
        if (in.readByte() == 0) {
            labelWidth = null;
        } else {
            labelWidth = in.readInt();
        }
        isStandardApp = in.readByte() != 0;
        defaultValue = in.readString();
        parameterHint = in.readString();
        if (in.readByte() == 0) {
            parentParameterId = null;
        } else {
            parentParameterId = in.readLong();
        }
        if (in.readByte() == 0) {
            objectWidth = null;
        } else {
            objectWidth = in.readDouble();
        }
        if (in.readByte() == 0) {
            objectHeight = null;
        } else {
            objectHeight = in.readDouble();
        }
        isShowLast2 = in.readByte() != 0;
        if (in.readByte() == 0) {
            percentDifference = null;
        } else {
            percentDifference = in.readDouble();
        }
        if (in.readByte() == 0) {
            routineId = null;
        } else {
            routineId = in.readInt();
        }
        isEnableParameterNotes = in.readByte() != 0;
        fieldParameterOperands = in.readString();
        extField1 = in.readString();
        extField2 = in.readString();
        extField3 = in.readString();
        extField4 = in.readString();
        extField5 = in.readString();
        extField6 = in.readString();
        extField7 = in.readString();
        isReviewer = in.readByte() != 0;
        isParentField = in.readByte() != 0;
        isChildField = in.readByte() != 0;
        isVisible = in.readByte() != 0;
        isExpanded = in.readByte() != 0;
        isRendered = in.readByte() != 0;
        Required_Y_N = in.readString();
        mandatoryField = in.readString();
    }

    public static final Creator<MetaData> CREATOR = new Creator<MetaData>() {
        @Override
        public MetaData createFromParcel(Parcel in) {
            return new MetaData(in);
        }

        @Override
        public MetaData[] newArray(int size) {
            return new MetaData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(FieldParameterID);
        dest.writeInt(FormID);
        dest.writeInt(currentFormID);
        dest.writeInt(LovID);
        dest.writeInt(L_itemID);
        dest.writeString(ParamLabel);
        dest.writeInt(RowOrder);
        dest.writeInt(ColumnOrder);
        dest.writeString(InputType);
        dest.writeString(NameValuePair);
        dest.writeString(DesiredUnits);
        dest.writeString(ValueType);
        dest.writeFloat(WarningHigh);
        dest.writeFloat(WarningLow);
        dest.writeFloat(HighLimit);
        dest.writeFloat(LowLimit);
        if (labelWidth == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(labelWidth);
        }
        dest.writeByte((byte) (isStandardApp ? 1 : 0));
        dest.writeString(defaultValue);
        dest.writeString(parameterHint);
        if (parentParameterId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(parentParameterId);
        }
        if (objectWidth == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(objectWidth);
        }
        if (objectHeight == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(objectHeight);
        }
        dest.writeByte((byte) (isShowLast2 ? 1 : 0));
        if (percentDifference == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(percentDifference);
        }
        if (routineId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(routineId);
        }
        dest.writeByte((byte) (isEnableParameterNotes ? 1 : 0));
        dest.writeString(fieldParameterOperands);
        dest.writeString(extField1);
        dest.writeString(extField2);
        dest.writeString(extField3);
        dest.writeString(extField4);
        dest.writeString(extField5);
        dest.writeString(extField6);
        dest.writeString(extField7);
        dest.writeByte((byte) (isReviewer ? 1 : 0));
        dest.writeByte((byte) (isParentField ? 1 : 0));
        dest.writeByte((byte) (isChildField ? 1 : 0));
        dest.writeByte((byte) (isVisible ? 1 : 0));
        dest.writeByte((byte) (isExpanded ? 1 : 0));
        dest.writeByte((byte) (isRendered ? 1 : 0));
        dest.writeString(Required_Y_N);
        dest.writeString(mandatoryField);
    }

    @Override
    public String toString() {
        return "MetaData{" +
                "FieldParameterID=" + FieldParameterID +
                ", FormID=" + FormID +
                ", currentFormID=" + currentFormID +
                ", LovID=" + LovID +
                ", L_itemID=" + L_itemID +
                ", ParamLabel='" + ParamLabel + '\'' +
                ", RowOrder=" + RowOrder +
                ", ColumnOrder=" + ColumnOrder +
                ", InputType='" + InputType + '\'' +
                ", NameValuePair='" + NameValuePair + '\'' +
                ", DesiredUnits='" + DesiredUnits + '\'' +
                ", ValueType='" + ValueType + '\'' +
                ", WarningHigh=" + WarningHigh +
                ", WarningLow=" + WarningLow +
                ", HighLimit=" + HighLimit +
                ", LowLimit=" + LowLimit +
                ", labelWidth=" + labelWidth +
                ", isStandardApp=" + isStandardApp +
                ", defaultValue='" + defaultValue + '\'' +
                ", parameterHint='" + parameterHint + '\'' +
                ", parentParameterId=" + parentParameterId +
                ", objectWidth=" + objectWidth +
                ", objectHeight=" + objectHeight +
                ", isShowLast2=" + isShowLast2 +
                ", percentDifference=" + percentDifference +
                ", routineId=" + routineId +
                ", isEnableParameterNotes=" + isEnableParameterNotes +
                ", fieldParameterOperands='" + fieldParameterOperands + '\'' +
                ", extField1='" + extField1 + '\'' +
                ", extField2='" + extField2 + '\'' +
                ", extField3='" + extField3 + '\'' +
                ", extField4='" + extField4 + '\'' +
                ", extField5='" + extField5 + '\'' +
                ", extField6='" + extField6 + '\'' +
                ", extField7='" + extField7 + '\'' +
                ", isReviewer=" + isReviewer +
                ", isParentField=" + isParentField +
                ", isChildField=" + isChildField +
                ", isVisible=" + isVisible +
                ", isExpanded=" + isExpanded +
                ", isRendered=" + isRendered +
                ", childParamList=" + childParamList +
                ", form_field_row=" + form_field_row +
                ", Required_Y_N='" + Required_Y_N + '\'' +
                ", mandatoryField='" + mandatoryField + '\'' +
                ", nameValueMap=" + nameValueMap +
                '}';
    }
}