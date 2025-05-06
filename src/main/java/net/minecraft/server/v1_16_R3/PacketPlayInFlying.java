package net.minecraft.server.v1_16_R3;
/**
 * Fake nms
 */
public final class PacketPlayInFlying implements Packet<PacketPlayInFlying> {
	public PacketPlayInFlying() {
	}

	@Override
	public void a(PacketDataSerializer serializer) {
	}

	@Override
	public void b(PacketDataSerializer serializer) {
	}
	
	public final static class PacketPlayInLook implements Packet<PacketPlayInLook> {

		@Override
		public void a(PacketDataSerializer serializer) {
		}

		@Override
		public void b(PacketDataSerializer serializer) {
		}
		
	}
	
	public final static class PacketPlayInPosition implements Packet<PacketPlayInLook> {

		@Override
		public void a(PacketDataSerializer serializer) {
		}

		@Override
		public void b(PacketDataSerializer serializer) {
		}
		
	}
	
	public final static class PacketPlayInPositionLook implements Packet<PacketPlayInLook> {

		@Override
		public void a(PacketDataSerializer serializer) {
		}

		@Override
		public void b(PacketDataSerializer serializer) {
		}
		
	}
	
}
