package me.bomb.cutscene;

import org.bukkit.Bukkit;

public class CutsceneAPI {
	protected final static CameraManager cameramanager;
	static {
		switch (Bukkit.getServer().getClass().getPackage().getName().substring(23)) {
		case "v1_18_R2":
			cameramanager = new CameraManager_v1_18_R2();
			break;
		case "v1_17_R1":
			cameramanager = new CameraManager_v1_17_R1();
			break;
		case "v1_16_R3":
			cameramanager = new CameraManager_v1_16_R3();
			break;
		case "v1_15_R1":
			cameramanager = new CameraManager_v1_15_R1();
			break;
		case "v1_14_R1":
			cameramanager = new CameraManager_v1_14_R1();
			break;
		case "v1_13_R2":
			cameramanager = new CameraManager_v1_13_R2();
			break;
		case "v1_12_R1":
			cameramanager = new CameraManager_v1_12_R1();
			break;
		case "v1_11_R1":
			cameramanager = new CameraManager_v1_11_R1();
			break;
		case "v1_10_R1":
			cameramanager = new CameraManager_v1_10_R1();
			break;
		case "v1_9_R2":
			cameramanager = new CameraManager_v1_9_R2();
			break;
		case "v1_8_R3":
			cameramanager = new CameraManager_v1_8_R3();
			break;
		default:
			cameramanager = null;
		}
	}
	public static CameraManager getCameraManager() {
		if(Cutscene.api) return cameramanager;
		return null;
	}
}
