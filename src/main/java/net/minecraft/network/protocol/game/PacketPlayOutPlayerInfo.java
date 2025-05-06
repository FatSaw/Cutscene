package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.EntityPlayer;

/**
 * Fake nms
 */
public final class PacketPlayOutPlayerInfo implements Packet<PacketPlayOutPlayerInfo> {
	public PacketPlayOutPlayerInfo(EnumPlayerInfoAction action, EntityPlayer player) {
		
	}
	
	public static enum EnumPlayerInfoAction {
		b;
	}

	@Override
	public void a(PacketDataSerializer serializer) {
	}

	@Override
	public void b(PacketDataSerializer serializer) {
	}
}
