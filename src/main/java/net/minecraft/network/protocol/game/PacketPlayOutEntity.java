package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

/**
 * Fake nms
 */
public final class PacketPlayOutEntity implements Packet<PacketPlayOutEntity> {

	@Override
	public void a(PacketDataSerializer serializer) {
	}

	@Override
	public void b(PacketDataSerializer serializer) {
	}
	public final static class PacketPlayOutRelEntityMoveLook implements Packet<PacketPlayOutRelEntityMoveLook> {

		public PacketPlayOutRelEntityMoveLook(int id, short vx, short vy, short vz, byte y, byte p , boolean f) {
			
		}
		
		@Override
		public void a(PacketDataSerializer serializer) {
		}

		@Override
		public void b(PacketDataSerializer serializer) {
		}
		
	}
}
