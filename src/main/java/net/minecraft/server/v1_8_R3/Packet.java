package net.minecraft.server.v1_8_R3;

/**
 * Fake nms
 */
public interface Packet<T> {
	public void a(PacketDataSerializer serializer);
	public void b(PacketDataSerializer serializer);
}
