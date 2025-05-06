package net.minecraft.server.v1_9_R2;

/**
 * Fake nms
 */
public interface Packet<T> {
	public void a(PacketDataSerializer serializer);
	public void b(PacketDataSerializer serializer);
}
