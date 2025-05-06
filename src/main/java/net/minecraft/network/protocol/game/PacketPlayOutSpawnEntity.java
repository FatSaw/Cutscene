package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityLiving;

/**
 * Fake nms
 */
public final class PacketPlayOutSpawnEntity implements Packet<PacketPlayOutSpawnEntity> {
	public PacketPlayOutSpawnEntity(EntityLiving entity) {
		
	}

	@Override
	public void a(PacketDataSerializer serializer) {
	}

	@Override
	public void b(PacketDataSerializer serializer) {
	}
}
