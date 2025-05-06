package net.minecraft.network.protocol;

import net.minecraft.network.PacketDataSerializer;

/**
 * Fake nms
 */
public interface Packet<T> {
	public void a(PacketDataSerializer serializer);
	public void b(PacketDataSerializer serializer);
}
