package me.bomb.cutscene;

public enum CameraType {
	NORMAL(1.777d), GREEN(1.445d), NEGATIVE(2.55d), SPLIT(0.65d);
	public double eyeheight;
	private CameraType(double eyeheight) {
		this.eyeheight = eyeheight;
	}
}
