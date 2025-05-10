package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

/**
 * Fake nms
 */
public final class PacketPlayOutEntityHeadRotation implements Packet<PacketPlayOutEntityHeadRotation> {
	public PacketPlayOutEntityHeadRotation(Entity entity, byte y) {
	}

	@Override
	public void a(PacketDataSerializer serializer) {
	}

	@Override
	public void b(PacketDataSerializer serializer) {
	}
}
