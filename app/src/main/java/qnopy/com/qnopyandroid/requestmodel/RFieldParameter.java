package qnopy.com.qnopyandroid.requestmodel;

import java.util.HashSet;
import java.util.Set;

public class RFieldParameter {

    private Integer fieldParameterId;

    private Set<DFieldData> fieldDatas;

    private Set<RFieldParameterUnit> fieldParameterUnits;

    private Set<SMetaData> metaDatas;

    private String parameterLabel;

    private String cas;

    private Long creationDate;

    private Long modifiedDate;

    private Integer createdBy;

    public Set<DFieldData> getFieldDatas() {
        if (null == fieldDatas) {
            fieldDatas = new HashSet<DFieldData>();
        }
        return fieldDatas;
    }

    public void setFieldDatas(Set<DFieldData> fieldDatas) {
        this.fieldDatas = fieldDatas;
    }

    public Set<RFieldParameterUnit> getFieldParameterUnits() {
        if (null == fieldParameterUnits) {
            fieldParameterUnits = new HashSet<RFieldParameterUnit>();
        }
        return fieldParameterUnits;
    }

    public void setFieldParameterUnits(Set<RFieldParameterUnit> fieldParameterUnits) {
        this.fieldParameterUnits = fieldParameterUnits;
    }

    public Set<SMetaData> getMetaDatas() {
        if (null == metaDatas) {
            metaDatas = new HashSet<SMetaData>();
        }
        return metaDatas;
    }

    public void setMetaDatas(Set<SMetaData> metaDatas) {
        this.metaDatas = metaDatas;
    }

    public String getParameterLabel() {
        return parameterLabel;
    }

    public void setParameterLabel(String parameterLabel) {
        this.parameterLabel = parameterLabel;
    }

    public String getCas() {
        return cas;
    }

    public void setCas(String cas) {
        this.cas = cas;
    }

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

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getFieldParameterId() {
        return this.fieldParameterId;
    }

    public void setFieldParameterId(Integer id) {
        this.fieldParameterId = id;
    }

}
