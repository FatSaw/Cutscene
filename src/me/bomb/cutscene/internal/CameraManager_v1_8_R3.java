package me.bomb.cutscene.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import me.bomb.cutscene.CameraType;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EntityCreeper;
import net.minecraft.server.v1_8_R3.EntityEnderman;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EntitySpider;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.MathHelper;

import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R3.PacketPlayInWindowClick;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying.PacketPlayInLook;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying.PacketPlayInPosition;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying.PacketPlayInPositionLook;
import net.minecraft.server.v1_8_R3.PacketPlayOutCamera;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutWindowItems;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutAbilities;
import net.minecraft.server.v1_8_R3.PacketPlayOutSetSlot;

class CameraManager_v1_8_R3 extends CameraManager {
	
	private static final PacketPlayOutWindowItems packetemptywindowitems;
	
	static {
		ArrayList<ItemStack> nnl = new ArrayList<ItemStack>();
		for (byte slot = 0; slot < 46; slot++) {
			nnl.add(slot, new ItemStack(Item.getById(0)));
		}
		packetemptywindowitems = new PacketPlayOutWindowItems(0, nnl);
	}

	protected void register(Player player) {
		ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
			@Override
			public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
				if (contains(player)) {
					if (packet instanceof PacketPlayInSteerVehicle || packet instanceof PacketPlayInFlying
							|| packet instanceof PacketPlayInPosition || packet instanceof PacketPlayInPositionLook
							|| packet instanceof PacketPlayInLook || packet instanceof PacketPlayInBlockDig
							|| packet instanceof PacketPlayInBlockPlace || packet instanceof PacketPlayInArmAnimation
							|| packet instanceof PacketPlayInWindowClick || packet instanceof PacketPlayInEntityAction
							|| packet instanceof PacketPlayInUseEntity) {
						return;
					}
				}
				super.channelRead(context, packet);
			}

			@Override
			public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) throws Exception {
				if (contains(player)) {
					if (packet instanceof PacketPlayOutWindowItems) {
						packet = packetemptywindowitems;
					}
					if (packet instanceof PacketPlayOutSetSlot) {
						return;
					}
					if (packet instanceof PacketPlayOutPlayerInfo) {
						PacketPlayOutPlayerInfo info = (PacketPlayOutPlayerInfo) packet;
						PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer(0));
						info.b(packetdataserializer);
						EnumPlayerInfoAction action = packetdataserializer.a(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.class);
						switch (action) {
						case UPDATE_GAME_MODE:
							HashMap<UUID, Byte> gamemodes = new HashMap<UUID, Byte>();
							int i = packetdataserializer.e();
							for (int j = 0; j < i; ++j) {
								UUID uuid = packetdataserializer.g();
								if (player.getUniqueId().equals(uuid)) {
									packetdataserializer.e();
									gamemodes.put(uuid, (byte) 3);
								} else {
									gamemodes.put(uuid, (byte) packetdataserializer.e());
								}
							}
							packetdataserializer.a(action);
							packetdataserializer.b(gamemodes.size());
							for (UUID uuid : gamemodes.keySet()) {
								packetdataserializer.a(uuid);
								packetdataserializer.b(gamemodes.get(uuid));
							}
							info.a(packetdataserializer);
							packet = info;
						default:
							break;
						}
					}
				}
				super.write(context, packet, channelPromise);
			}
		};
		ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();
		pipeline.addBefore("packet_handler", "cutscene_" + player.getUniqueId(), channelDuplexHandler);
	}

	protected void unregister(Player player) {
		Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
		channel.eventLoop().submit(() -> {
			channel.pipeline().remove("cutscene_" + player.getUniqueId());
			return null;
		});
	}

	protected void spawnCamera(Player player) {
		EntityPlayer entityplayer = ((CraftPlayer)player).getHandle();
		if(!cameradata.containsKey(entityplayer.getUniqueID())) return;
		CameraData data = cameradata.get(entityplayer.getUniqueID());
		RouteProvider route = data.route;
		EntityLiving cameraentity = null;
		CameraType type = data.cameratype;
		if(!route.hasNext()) return;
		LocationPoint location = route.getNext();
		switch (type) {
		case NORMAL:
			EntityArmorStand stand = new EntityArmorStand(entityplayer.world);
			stand.setLocation(location.x, location.y - type.eyeheight, location.z, location.yaw, location.pitch);
			cameraentity = stand;
			break;
		case GREEN:
			EntityCreeper creeper = new EntityCreeper(entityplayer.world);
			creeper.setLocation(location.x, location.y - type.eyeheight, location.z, location.yaw, location.pitch);
			cameraentity = creeper;
			break;
		case NEGATIVE:
			EntityEnderman enderman = new EntityEnderman(entityplayer.world);
			enderman.setLocation(location.x, location.y - type.eyeheight, location.z, location.yaw, location.pitch);
			cameraentity = enderman;
			break;
		case SPLIT:
			EntitySpider spider = new EntitySpider(entityplayer.world);
			spider.setLocation(location.x, location.y - type.eyeheight, location.z, location.yaw, location.pitch);
			cameraentity = spider;
			break;
		default:
			break;
		}
		if(cameraentity==null) return;
		cameraentity.setInvisible(true);
		cameraentity.lastX = location.x;
		cameraentity.lastY = location.y;
		cameraentity.lastZ = location.z;
		data.cameraentity = cameraentity;
		
		PlayerConnection connection = entityplayer.playerConnection;
		connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE, entityplayer));
		connection.sendPacket(packetemptywindowitems);
		connection.sendPacket(new PacketPlayOutGameStateChange(3, -1));
		connection.sendPacket(new PacketPlayOutSpawnEntityLiving(cameraentity));
		connection.sendPacket(new PacketPlayOutCamera(cameraentity));
	}
	
	protected void updateCameraType(Player player) {
		EntityPlayer entityplayer = ((CraftPlayer)player).getHandle();
		if(!cameradata.containsKey(entityplayer.getUniqueID())) return;
		CameraData data = cameradata.get(entityplayer.getUniqueID());
		if(data.cameraentity==null || data.cameratype==null) return;
		EntityLiving oldcameraentity = (EntityLiving) data.cameraentity;
		CameraType newtype = data.cameratype;
		CameraType oldtype = null;
		if (oldcameraentity instanceof EntityArmorStand) {
			oldtype = CameraType.NORMAL;
		} else if (oldcameraentity instanceof EntityCreeper) {
			oldtype = CameraType.GREEN;
		} else if (oldcameraentity instanceof EntityEnderman) {
			oldtype = CameraType.NEGATIVE;
		} else if (oldcameraentity instanceof EntitySpider) {
			oldtype = CameraType.SPLIT;
		}
		if(newtype==oldtype) return;
		EntityLiving newcameraentity = null;

		switch (newtype) {
		case NORMAL:
			EntityArmorStand stand = new EntityArmorStand(entityplayer.world);
			stand.setLocation(oldcameraentity.locX, oldcameraentity.locY + oldtype.eyeheight - newtype.eyeheight, oldcameraentity.locZ, oldcameraentity.yaw, oldcameraentity.pitch);
			newcameraentity = stand;
			break;
		case GREEN:
			EntityCreeper creeper = new EntityCreeper(entityplayer.world);
			creeper.setLocation(oldcameraentity.locX, oldcameraentity.locY + oldtype.eyeheight - newtype.eyeheight, oldcameraentity.locZ, oldcameraentity.yaw, oldcameraentity.pitch);
			newcameraentity = creeper;
			break;
		case NEGATIVE:
			EntityEnderman enderman = new EntityEnderman(entityplayer.world);
			enderman.setLocation(oldcameraentity.locX, oldcameraentity.locY + oldtype.eyeheight - newtype.eyeheight, oldcameraentity.locZ, oldcameraentity.yaw, oldcameraentity.pitch);
			newcameraentity = enderman;
			break;
		case SPLIT:
			EntitySpider spider = new EntitySpider(entityplayer.world);
			spider.setLocation(oldcameraentity.locX, oldcameraentity.locY + oldtype.eyeheight - newtype.eyeheight, oldcameraentity.locZ, oldcameraentity.yaw, oldcameraentity.pitch);
			newcameraentity = spider;
			break;
		default:
			break;
		}
		if(newcameraentity==null) return;
		newcameraentity.setInvisible(true);
		newcameraentity.lastX = oldcameraentity.lastX;
		newcameraentity.lastY = oldcameraentity.lastY;
		newcameraentity.lastZ = oldcameraentity.lastZ;
		data.cameraentity = newcameraentity;
		
		PlayerConnection connection = entityplayer.playerConnection;
		connection.sendPacket(new PacketPlayOutSpawnEntityLiving(newcameraentity));
		connection.sendPacket(new PacketPlayOutCamera(newcameraentity));
		connection.sendPacket(new PacketPlayOutEntityDestroy(oldcameraentity.getId()));
	}
	
	protected void updateCameraLocation(Player player) {
		EntityPlayer entityplayer = ((CraftPlayer)player).getHandle();
		if(!cameradata.containsKey(entityplayer.getUniqueID())) return;
		CameraData data = cameradata.get(entityplayer.getUniqueID());
		RouteProvider route = data.route;
		if(!route.hasNext() || data.cameraentity == null || data.cameratype == null) return;
		EntityLiving cameraentity = (EntityLiving) data.cameraentity;
		LocationPoint location = route.getNext();
		
		PlayerConnection connection = entityplayer.playerConnection;
		if (location.hasMove(cameraentity.lastX, cameraentity.lastY, cameraentity.lastZ)) {
			cameraentity.lastX = location.x;
			cameraentity.lastY = location.y;
			cameraentity.lastZ = location.z;
			cameraentity.setLocation(location.x, location.y - data.cameratype.eyeheight, location.z, location.yaw, location.pitch);
			connection.sendPacket(new PacketPlayOutEntityTeleport(cameraentity));
		} else {
			entityplayer.playerConnection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(cameraentity.getId(),(byte) MathHelper.d(location.yaw * 256.0F / 360.0F),(byte) MathHelper.d(location.pitch * 256.0F / 360.0F), false));
		}
	}
	
	protected void despawnCamera(Player player) {
		EntityPlayer entityplayer = ((CraftPlayer)player).getHandle();
		if(!cameradata.containsKey(entityplayer.getUniqueID())) return;
		CameraData data = cameradata.get(entityplayer.getUniqueID());
		EntityLiving cameraentity = (EntityLiving) data.cameraentity;
		
		PlayerConnection connection = entityplayer.playerConnection;
		connection.sendPacket(new PacketPlayOutEntityDestroy(cameraentity.getId()));
	}
	
	@Override
	protected void restore(Player player) {
		EntityPlayer entityplayer = ((CraftPlayer)player).getHandle();

		PlayerConnection connection = entityplayer.playerConnection;
		connection.sendPacket(new PacketPlayOutCamera(entityplayer.C()));
		connection.sendPacket(new PacketPlayOutGameStateChange(3,entityplayer.playerInteractManager.getGameMode().getId()));
		connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE,entityplayer));
		connection.sendPacket(new PacketPlayOutAbilities(entityplayer.abilities));
		entityplayer.updateInventory(entityplayer.defaultContainer);
	}
	
}
