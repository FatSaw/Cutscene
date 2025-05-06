package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

/**
 * Fake nms
 */
public final class PacketPlayOutGameStateChange implements Packet<PacketPlayOutGameStateChange> {
	public PacketPlayOutGameStateChange(a type, float state) {
		
	}

	@Override
	public void a(PacketDataSerializer serializer) {
	}

	@Override
	public void b(PacketDataSerializer serializer) {
	}
	
	public final static class a {
		public a(int id) {
		}
	}
}
