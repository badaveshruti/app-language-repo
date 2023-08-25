package qnopy.com.qnopyandroid.requestmodel;
import java.util.HashSet;
import java.util.Set;

public class EventFieldData {

    private Integer eventId;

    private Set<DAttachment> attachments;

    private Set<DFieldData> fieldDataList;

    private String deviceId;
    
    private String mobileAppName;

    private Integer mobileAppId;
    
    private String siteName;

    private Integer siteId;

    private Long eventDate;

    private Double latitude;

    private Double longitude;
    
    private String userName;

    private Integer userId;

    private String notes;
    
    private Long eventCreationDate;
    
    private Long eventModificationDate;
    
    private boolean active;


    public String getMobileAppName() {
		return mobileAppName;
	}

	public void setMobileAppName(String mobileAppName) {
		this.mobileAppName = mobileAppName;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

    public Set<DAttachment> getAttachments() {
    	if (null == this.attachments) {
    		this.attachments = new HashSet<DAttachment>(); 
    	}
        return attachments;
    }

    public void setAttachments(Set<DAttachment> attachments) {
        this.attachments = attachments;
    }

    public Set<DFieldData> getFieldDataList() {
    	if (null == this.fieldDataList) {
    		this.fieldDataList = new HashSet<DFieldData>(); 
    	}
        return fieldDataList;
    }

    public void setFieldDataList(Set<DFieldData> fieldDataList) {
        this.fieldDataList = fieldDataList;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getMobileAppId() {
        return mobileAppId;
    }

    public void setMobileAppId(Integer mobileAppId) {
        this.mobileAppId = mobileAppId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Long getEventDate() {
        return eventDate;
    }

    public void setEventDate(Long eventDate) {
        this.eventDate = eventDate;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getEventId() {
        return this.eventId;
    }

    public void setEventId(Integer id) {
        this.eventId = id;
    }

    public Long getEventCreationDate() {
		return eventCreationDate;
	}

	public void setEventCreationDate(Long eventCreationDate) {
		this.eventCreationDate = eventCreationDate;
	}

	public Long getEventModificationDate() {
		return eventModificationDate;
	}

	public void setEventModificationDate(Long eventModificationDate) {
		this.eventModificationDate = eventModificationDate;
	}

    public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
}
