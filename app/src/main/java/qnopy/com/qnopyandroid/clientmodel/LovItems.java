package qnopy.com.qnopyandroid.clientmodel;

//get meta data for particular site and mobile app and location


@SuppressWarnings("unused")
public class LovItems {

    Integer lov_item_id;
    Integer lov_id;

    public String item_display_name;
    public String item_value;
    public String item_description;

    public Integer 	company_id;
    public Integer 	site_id,syncFlag;
    //private String	notes;
    //public Integer 	created_by;


    public String ext_field1;
    public String ext_field2;
    public String ext_field3;
    public String ext_field4;
    public String ext_field5;

    String notes;
    Long creation_date;
    //Date 	ModifiedDate;
    Integer created_by,modifiedBy;


    public Integer getSyncFlag() {
        return syncFlag;
    }

    public void setSyncFlag(Integer syncFlag) {
        this.syncFlag = syncFlag;
    }

    public Integer getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Integer modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Integer getCompany_id() {
        return company_id;
    }

    public void setCompany_id(Integer company_id) {
        this.company_id = company_id;
    }

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Integer getLovItemID() {
        return lov_item_id;
    }

    public void setLovItemID(Integer value) {
        lov_item_id = value;
    }

    public Integer getLovID() {
        return lov_id;
    }

    public void setLovID(Integer value) {
        lov_id = value;
    }

    public String getItemDisplayName() {
        return item_display_name;
    }

    public void setItemDisplayName(String value) {
        item_display_name = value;
    }

    public String getItemDescription() {
        return item_description;
    }

    public void setItemDescription(String value) {
        item_description = value;
    }

    public String getItemValue() {
        return item_value;
    }

    public void setItemValue(String value) {
        item_value = value;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String value) {
        notes = value;
    }

    public Integer getCreatedBy() {
        return created_by;
    }

    public void setCreatedBy(Integer value) {
        created_by = value;
    }

    public Long getCreationDate() {
        return creation_date;
    }

    public void setCreationDate(Long value) {
        creation_date = value;
    }


    public String getExtField1() {
        return ext_field1;
    }

    public void setExtField1(String value) {
        ext_field1 = value;
    }

    public String getExtField2() {
        return ext_field2;
    }

    public void setExtField2(String value) {
        ext_field2 = value;
    }

    public String getExtField3() {
        return ext_field3;
    }

    public void setExtField3(String value) {
        ext_field3 = value;
    }

    public String getExtField4() {
        return ext_field4;
    }

    public void setExtField4(String value) {
        ext_field4 = value;
    }

    public String getExtField5() {
        return ext_field5;
    }

    public void setExtField5(String value) {
        ext_field5 = value;
    }

}