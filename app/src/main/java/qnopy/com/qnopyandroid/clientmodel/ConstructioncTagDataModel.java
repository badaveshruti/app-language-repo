package qnopy.com.qnopyandroid.clientmodel;

import java.io.Serializable;

public class ConstructioncTagDataModel implements Serializable {

    private int mediaId;
    private int postId;
    private int tagId;
    private int modifiedBy;
    private int createdBy;
    private Long serverCreationDate;
    private Long serverModificationDate;
    private Long creationDate;
    private Long modificationDate;
    private int sTagId;
    private int siteId;
    private int displayFlag;
    private String clientTagId;
    private int ctagId;

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public int getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(int modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
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

    public int getsTagId() {
        return sTagId;
    }

    public void setsTagId(int sTagId) {
        this.sTagId = sTagId;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getDisplayFlag() {
        return displayFlag;
    }

    public void setDisplayFlag(int displayFlag) {
        this.displayFlag = displayFlag;
    }

    public int getCtagId() {
        return ctagId;
    }

    public void setCtagId(int ctagId) {
        this.ctagId = ctagId;
    }

    public String getClientTagId() {
        return clientTagId;
    }

    public void setClientTagId(String clientTagId) {
        this.clientTagId = clientTagId;
    }
}
