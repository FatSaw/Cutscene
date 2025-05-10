package net.minecraft.server.level;

import java.util.UUID;

import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.PlayerAbilities;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.level.World;

/**
 * Fake nms
 */
public final class EntityPlayer extends Entity {
	public PlayerConnection b;
	public Entity vehicle;
	public int dimension, expTotal, expLevel;
	public double locX, locY, locZ;
	public float yaw, pitch, exp;
	public Container bV, bU;
	public World t, s;
	public PlayerInteractManager d;
	public String locale;

	public int ae() {
		return 0;
	}
	public int getId() {
		return 0;
	}
	
	public UUID co() {
		return null;
	}
	
	public UUID cm() {
		return null;
	}
	
	public UUID getUniqueID() {
		return null;
	}
	
	public Entity G() {
		return null;
	}
	
	public Entity getSpecatorTarget() {
		return null;
	}
	
	public PlayerAbilities fB() {
		return null;
	}
	
	public PlayerAbilities fs() {
		return null;
	}
	
	public PlayerAbilities getAbilities() {
		return null;
	}
}
