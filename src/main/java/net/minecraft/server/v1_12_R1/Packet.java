package net.minecraft.server.v1_12_R1;

/**
 * Fake nms
 */
public interface Packet<T> {
	public void a(PacketDataSerializer serializer);
	public void b(PacketDataSerializer serializer);
}
