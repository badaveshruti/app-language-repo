package qnopy.com.qnopyandroid.requestmodel;

import java.io.Serializable;

public final class SSiteUserRolePK implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userId;

    private Integer siteId;

    private Integer roleId;

    public SSiteUserRolePK(Integer userId, Integer siteId, Integer roleId) {
        super();
        this.userId = userId;
        this.siteId = siteId;
        this.roleId = roleId;
    }

    public SSiteUserRolePK() {
        super();
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime
                * result
                + ((roleId == null) ? 0
                : roleId.hashCode());
        result = prime
                * result
                + ((siteId == null) ? 0
                : siteId.hashCode());
        result = prime
                * result
                + ((userId == null) ? 0
                : userId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SSiteUserRolePK other = (SSiteUserRolePK) obj;
        if (roleId == null) {
            if (other.roleId != null)
                return false;
        } else if (!roleId.equals(other.roleId))
            return false;
        if (siteId == null) {
            if (other.siteId != null)
                return false;
        } else if (!siteId.equals(other.siteId))
            return false;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        return true;
    }

}
