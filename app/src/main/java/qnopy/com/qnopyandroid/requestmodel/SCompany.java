package qnopy.com.qnopyandroid.requestmodel;

import java.util.HashSet;
import java.util.Set;

public class SCompany {

    private Integer companyId;

    private Set<SSite> sites;

    private Set<SUser> users;

    private String companyName;

    private Long creationDate;

    private Integer createdBy;

    public Set<SSite> getSites() {
        if (null == sites) {
            sites = new HashSet<SSite>();
        }
        return sites;
    }

    public void setSites(Set<SSite> sites) {
        this.sites = sites;
    }

    public Set<SUser> getUsers() {
        if (null == users) {
            users = new HashSet<SUser>();
        }
        return users;
    }

    public void setUsers(Set<SUser> users) {
        this.users = users;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(Integer id) {
        this.companyId = id;
    }

}
