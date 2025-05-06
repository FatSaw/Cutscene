package org.bukkit.craftbukkit.v1_18_R2.entity;

import org.bukkit.entity.Player;

import net.minecraft.server.level.EntityPlayer;
/**
 * Fake nms
 */
public abstract class CraftPlayer implements Player {
	public EntityPlayer getHandle() {
		return null;
	}
}
