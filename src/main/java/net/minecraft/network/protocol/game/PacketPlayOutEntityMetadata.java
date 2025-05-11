package net.minecraft.network.protocol.game;

import java.util.List;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.DataWatcher;

/**
 * Fake nms
 */
public final class PacketPlayOutEntityMetadata implements Packet<PacketPlayOutEntityMetadata> {
	public PacketPlayOutEntityMetadata(int id, List<DataWatcher.b<?>> values) {
		
	}
	
	public PacketPlayOutEntityMetadata(int id, DataWatcher dw, boolean f) {
		
	}

	@Override
	public void a(PacketDataSerializer serializer) {
	}

	@Override
	public void b(PacketDataSerializer serializer) {
	}
}
