package me.bomb.cutscene;

public class CutsceneAPI {
	private static CameraManager cameramanager = null;

	public static CameraManager getCameraManager() {
		return cameramanager;
	}

	protected CutsceneAPI(CameraManager cameramanager) {
		CutsceneAPI.cameramanager = cameramanager;
	}
}
