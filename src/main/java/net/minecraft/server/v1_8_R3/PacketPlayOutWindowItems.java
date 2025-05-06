package net.minecraft.server.v1_8_R3;

import java.util.ArrayList;

/**
 * Fake nms
 */
public final class PacketPlayOutWindowItems implements Packet<PacketPlayOutWindowItems> {
	public PacketPlayOutWindowItems(int i,ArrayList<ItemStack> items) {
		
	}

	@Override
	public void a(PacketDataSerializer serializer) {
	}

	@Override
	public void b(PacketDataSerializer serializer) {
	}
}
