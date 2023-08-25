package qnopy.com.qnopyandroid.responsemodel;

import java.util.List;

public class JsonCocDetailsObjectModel extends DefaultResponse {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9067718172460792902L;
	private List<SyncCocResponseModel> data;

	public List<SyncCocResponseModel> getData() {
		return data;
	}

	public void setData(List<SyncCocResponseModel> data) {
		this.data = data;
	}
}
