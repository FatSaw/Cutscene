package net.minecraft.server.v1_10_R1;

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
	public Container defaultContainer;
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
	
	public EntityLiving getSpecatorTarget() {
		return null;
	}
	
	public void updateInventory(Container container) {
		
	}
}
