package vo;

import java.util.List;

public class ResponseVO {

	private boolean success;
	private List<Prediction> predictions;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<Prediction> getPredictions() {
		return predictions;
	}

	public void setPredictions(List<Prediction> predictions) {
		this.predictions = predictions;
	}
	
}
