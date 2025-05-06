package net.minecraft.server.v1_14_R1;
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
