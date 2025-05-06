package net.minecraft.network.protocol.game;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

/**
 * Fake nms
 */
public final class PacketPlayOutWindowItems implements Packet<PacketPlayOutWindowItems> {
	public PacketPlayOutWindowItems(int i, int j,NonNullList<ItemStack> items, ItemStack item) {
		
	}

	@Override
	public void a(PacketDataSerializer serializer) {
	}

	@Override
	public void b(PacketDataSerializer serializer) {
	}
}
