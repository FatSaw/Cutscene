package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

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
