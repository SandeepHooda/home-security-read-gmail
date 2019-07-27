package vo;

public class Prediction {
	public static String humanStr = "person";
	private String label; //person
	private float confidence;

	private int y_min;
	private int y_max;
	private int x_min;
	private int x_max;
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public float getConfidence() {
		return confidence;
	}
	public void setConfidence(float confidence) {
		this.confidence = confidence;
	}
	public int getY_min() {
		return y_min;
	}
	public void setY_min(int y_min) {
		this.y_min = y_min;
	}
	public int getY_max() {
		return y_max;
	}
	public void setY_max(int y_max) {
		this.y_max = y_max;
	}
	public int getX_min() {
		return x_min;
	}
	public void setX_min(int x_min) {
		this.x_min = x_min;
	}
	public int getX_max() {
		return x_max;
	}
	public void setX_max(int x_max) {
		this.x_max = x_max;
	}
}
