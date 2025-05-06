package net.minecraft.server.v1_13_R2;

import java.util.UUID;

/**
 * Fake nms
 */
public final class EntityPlayer extends Entity {
	public PlayerConnection playerConnection;
	public Entity vehicle;
	public int dimension, expTotal, expLevel;
	public double locX, locY, locZ;
	public float yaw, pitch, exp;
	public ContainerPlayer defaultContainer;
	public PlayerAbilities abilities;
	public World world;
	public PlayerInteractManager playerInteractManager;
	public String locale;

	public int getId() {
		return 0;
	}
	
	public UUID getUniqueID() {
		return null;
	}
	
	public Entity getSpecatorTarget() {
		return null;
	}
	
	public void updateInventory(Container container) {
		
	}
}
