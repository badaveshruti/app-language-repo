package qnopy.com.qnopyandroid.clientmodel;

import java.io.Serializable;

public class ConstructionPostDataModel implements Serializable {

    private Long postId;
    private int userId;
    private int siteId;
    private int locationId;
    private int displayFlag;
    private String postText;
    private int createdBy;
    private int modifiedBy;
    private Long serverCreationDate;
    private Long serverModificationDate;
    private Long creationDate;
    private Long modificationDate;
    private Double latitude;
    private Double longitude;
    private int sPostId;
    private String clientPostId;
    private String postUserName;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getDisplayFlag() {
        return displayFlag;
    }

    public void setDisplayFlag(int displayFlag) {
        this.displayFlag = displayFlag;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public int getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(int modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Long getServerCreationDate() {
        return serverCreationDate;
    }

    public void setServerCreationDate(Long serverCreationDate) {
        this.serverCreationDate = serverCreationDate;
    }

    public Long getServerModificationDate() {
        return serverModificationDate;
    }

    public void setServerModificationDate(Long serverModificationDate) {
        this.serverModificationDate = serverModificationDate;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Long getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Long modificationDate) {
        this.modificationDate = modificationDate;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getsPostId() {
        return sPostId;
    }

    public void setsPostId(int sPostId) {
        this.sPostId = sPostId;
    }

    public String getClientPostId() {
        return clientPostId;
    }

    public void setClientPostId(String clientPostId) {
        this.clientPostId = clientPostId;
    }

    public String getPostUserName() {
        return postUserName;
    }

    public void setPostUserName(String postUserName) {
        this.postUserName = postUserName;
    }
}
