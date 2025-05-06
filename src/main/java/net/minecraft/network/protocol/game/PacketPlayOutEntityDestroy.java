package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

/**
 * Fake nms
 */
public final class PacketPlayOutEntityDestroy implements Packet<PacketPlayOutEntityDestroy> {
	public PacketPlayOutEntityDestroy(int id) {
		
	}

	@Override
	public void a(PacketDataSerializer serializer) {
	}

	@Override
	public void b(PacketDataSerializer serializer) {
	}
}
