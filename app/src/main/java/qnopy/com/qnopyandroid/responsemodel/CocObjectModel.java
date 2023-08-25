package qnopy.com.qnopyandroid.responsemodel;

import java.io.Serializable;
import java.util.List;

import qnopy.com.qnopyandroid.requestmodel.SCocDetails;
import qnopy.com.qnopyandroid.requestmodel.SCocMaster;

public class CocObjectModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4805954619595348439L;
	private List<SCocDetails> sCocDetails;
	private SCocMaster sCocMaster;
	public List<SCocDetails> getsCocDetails() {
		return sCocDetails;
	}
	public void setsCocDetails(List<SCocDetails> sCocDetails) {
		this.sCocDetails = sCocDetails;
	}
	public SCocMaster getsCocMaster() {
		return sCocMaster;
	}
	public void setsCocMaster(SCocMaster sCocMaster) {
		this.sCocMaster = sCocMaster;
	}
}
