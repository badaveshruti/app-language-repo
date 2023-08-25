package qnopy.com.qnopyandroid.clientmodel;

import java.io.Serializable;

public class FieldParamInfo implements Serializable {
    private String fieldParameterLabel;
    private String fieldParameterId;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getFieldParameterLabel() {
        return fieldParameterLabel;
    }

    public void setFieldParameterLabel(String fieldParameterLabel) {
        this.fieldParameterLabel = fieldParameterLabel;
    }

    public String getFieldParameterId() {
        return fieldParameterId;
    }

    public void setFieldParameterId(String fieldParameterId) {
        this.fieldParameterId = fieldParameterId;
    }
}
