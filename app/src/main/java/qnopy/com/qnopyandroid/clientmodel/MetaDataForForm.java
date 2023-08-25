package qnopy.com.qnopyandroid.clientmodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by shantanu on 7/27/16.
 */
public class MetaDataForForm {

    int FieldParameterID;
    public String ParamLabel;
    public int RowOrder;
    public int ColumnOrder;
    public String InputType;
    private String NameValuePair;
    public String DesiredUnits;
    public String ValueType;
    public int LovID;
    public int siteID;
    public String Required_Y_N;
    public float WarningHigh;
    public float WarningLow;
    public float HighLimit;
    public float LowLimit;
    private Integer labelWidth;
    private int MobileAppID;

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

    String Notes;
    Date CreationDate;
    Date ModifiedDate;
    int Createdby;

    LinkedHashMap<String, String> nameValueMap;


    public int getMobileAppID() {
        return MobileAppID;
    }

    public void setMobileAppID(int mobileAppID) {
        MobileAppID = mobileAppID;
    }

    public int getSiteID() {
        return siteID;
    }

    public void setSiteID(int siteID) {
        this.siteID = siteID;
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


        System.out.println("ppp" + "IN SetNameValuePair");
        System.out.println("ppp" + "PAIR" + NameValuePair);

        try {

            if ((NameValuePair != null) && (NameValuePair.length() != 0)) {
//			List<String> name = new ArrayList<String>();
                String[] tempPair;

                String[] valuePairArr = NameValuePair.split(",");
                if (valuePairArr != null) {
                    for (int i = 0; i < valuePairArr.length; i++) {
                        System.out.println("ppp" + "valuePair" + valuePairArr[i]);
                        tempPair = valuePairArr[i].split("\\^");
                        if (tempPair != null) {
                            nameValueMap.put(tempPair[0], tempPair[1]);
                            System.out.println("ppp" + "name=" + tempPair[0] + "value=" + tempPair[1]);
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
        System.out.println("ppp" + "OUT SetNameValuePair");
    }

    public LinkedHashMap<String, String> getNameValueMap() {
        return nameValueMap;
    }

    public List<String> getItemNames() {
        List<String> names = null;
        System.out.println("mmm" + "IN getItemNames");


        if (nameValueMap != null) {
            names = new ArrayList<String>();
            for (String key : nameValueMap.keySet()) {

                names.add(key);
                System.out.println("mmm" + "key added=" + key + "value=" + nameValueMap.get(key));
            }

        }

        System.out.println("mmm" + "OUT getItemNames");
        return names;
    }

    public String getItemValue(String key) {
        String value = null;
        if (nameValueMap != null) {
            System.out.println("mmm" + "value req for key=" + key);
            value = nameValueMap.get(key);
            System.out.println("mmm" + "value for key=" + value);
        }
        return value;
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

    @Override
    public String toString() {
        return this.getMetaParamLabel();
    }
}

