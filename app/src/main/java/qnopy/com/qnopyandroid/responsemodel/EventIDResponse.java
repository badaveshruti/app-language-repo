package qnopy.com.qnopyandroid.responsemodel;

public class EventIDResponse {
	public int getEventID() {
		return eventID;
	}
	public void setEventID(int eventID) {
		this.eventID = eventID;
	}
	public String getErrorMesg() {
		return errorMesg;
	}
	public void setErrorMesg(String errorMesg) {
		this.errorMesg = errorMesg;
	}
	int eventID=0;
	String errorMesg=null;
}
