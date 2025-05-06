package net.minecraft.server.v1_15_R1;
/**
 * Fake nms
 */
public final class PacketPlayOutPlayerInfo implements Packet<PacketPlayOutPlayerInfo> {
	public PacketPlayOutPlayerInfo(EnumPlayerInfoAction action, EntityPlayer player) {
		
	}
	
	public static enum EnumPlayerInfoAction {
		UPDATE_GAME_MODE;
	}

	@Override
	public void a(PacketDataSerializer serializer) {
	}

	@Override
	public void b(PacketDataSerializer serializer) {
	}
}
