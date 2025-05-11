package net.minecraft.network.protocol.game;

import java.util.Collection;
import java.util.EnumSet;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.EntityPlayer;

public class ClientboundPlayerInfoUpdatePacket implements Packet<ClientboundPlayerInfoUpdatePacket> {

	public ClientboundPlayerInfoUpdatePacket(EnumSet<ClientboundPlayerInfoUpdatePacket.a> actions, Collection<EntityPlayer> entityplayers) {
    }
	
	public ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.a action, EntityPlayer entityplayer) {
    }
	
	public static enum a {
		c;
	}
	
	@Override
	public void a(PacketDataSerializer serializer) {
	}

	@Override
	public void b(PacketDataSerializer serializer) {
	}

}
