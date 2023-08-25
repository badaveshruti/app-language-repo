package qnopy.com.qnopyandroid.requestmodel;

import java.util.HashSet;
import java.util.Set;

public class RRole {

    private Integer roleId;

    private Set<SSiteUserRole> siteUserRoles;

    private String role;

    private String roleDescription;

    private String notes;

    private Long creationDate;

    private Long modifiedDate;

    private Integer createdBy;

    public Set<SSiteUserRole> getSiteUserRoles() {
        if (null == siteUserRoles) {
            siteUserRoles = new HashSet<SSiteUserRole>();
        }
        return siteUserRoles;
    }

    public void setSiteUserRoles(Set<SSiteUserRole> siteUserRoles) {
        this.siteUserRoles = siteUserRoles;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public Integer getRoleId() {
        return this.roleId;
    }

    public void setRoleId(Integer id) {
        this.roleId = id;
    }

}
