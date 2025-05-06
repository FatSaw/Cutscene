package me.bomb.camerautil;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityCreeper;
import net.minecraft.server.v1_16_R3.EntityEnderman;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EntitySpider;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.Item;
import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.MathHelper;
import net.minecraft.server.v1_16_R3.NonNullList;

import net.minecraft.server.v1_16_R3.PacketDataSerializer;
import net.minecraft.server.v1_16_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_16_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_16_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_16_R3.PacketPlayInBoatMove;
import net.minecraft.server.v1_16_R3.PacketPlayInEntityAction;
import net.minecraft.server.v1_16_R3.PacketPlayInFlying;
import net.minecraft.server.v1_16_R3.PacketPlayInSteerVehicle;
import net.minecraft.server.v1_16_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_16_R3.PacketPlayInUseItem;
import net.minecraft.server.v1_16_R3.PacketPlayInVehicleMove;
import net.minecraft.server.v1_16_R3.PacketPlayInWindowClick;
import net.minecraft.server.v1_16_R3.PacketPlayInFlying.PacketPlayInLook;
import net.minecraft.server.v1_16_R3.PacketPlayInFlying.PacketPlayInPosition;
import net.minecraft.server.v1_16_R3.PacketPlayInFlying.PacketPlayInPositionLook;
import net.minecraft.server.v1_16_R3.PacketPlayOutCamera;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntity;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_16_R3.PacketPlayOutGameStateChange;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_16_R3.PacketPlayOutWindowItems;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_16_R3.PacketPlayOutAbilities;
import net.minecraft.server.v1_16_R3.PacketPlayOutSetSlot;

final class CameraManager_v1_16_R3 extends CameraManager {
	
	private static final PacketPlayOutWindowItems packetemptywindowitems;
	
	static {
		byte i = 46;
		NonNullList<ItemStack> nnl = NonNullList.a();
		ItemStack air = new ItemStack(Item.getById(0));
		while(--i > -1) {
			nnl.add(air);
		}
		packetemptywindowitems = new PacketPlayOutWindowItems(0, nnl);
	}

	protected void register(Player player) {
		ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
            	if(contains(player)) {
            		if (packet instanceof PacketPlayInSteerVehicle || packet instanceof PacketPlayInVehicleMove
							|| packet instanceof PacketPlayInFlying || packet instanceof PacketPlayInPosition
							|| packet instanceof PacketPlayInPositionLook || packet instanceof PacketPlayInLook
							|| packet instanceof PacketPlayInBlockDig || packet instanceof PacketPlayInBlockPlace
							|| packet instanceof PacketPlayInArmAnimation || packet instanceof PacketPlayInWindowClick
							|| packet instanceof PacketPlayInBoatMove || packet instanceof PacketPlayInEntityAction
							|| packet instanceof PacketPlayInUseEntity || packet instanceof PacketPlayInUseItem) {
						return;
					}
            	}
            	super.channelRead(context, packet);
            }
            @Override
            public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) throws Exception {
            	if(contains(player)) {
					CameraData data = cameradata.get(player.getUniqueId());
					if (packet instanceof PacketPlayOutWindowItems&&data.hideinventory) {
            	        packet = packetemptywindowitems;
                    }
                    if(packet instanceof PacketPlayOutSetSlot) {
                    	return;
                    }
                	if (packet instanceof PacketPlayOutPlayerInfo&&data.hideinterface) {
                		PacketPlayOutPlayerInfo info = (PacketPlayOutPlayerInfo) packet;
                		PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer(0));
            			info.b(packetdataserializer);
            			EnumPlayerInfoAction action = packetdataserializer.a(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.class);
                		switch (action) {
                		case UPDATE_GAME_MODE :
                			HashMap<UUID,Byte> gamemodes = new HashMap<UUID,Byte>();
                			int i = packetdataserializer.i();
                			for (int j = 0; j < i; ++j) {
                				UUID uuid = packetdataserializer.k();
                				if(player.getUniqueId().equals(uuid)) {
                					packetdataserializer.i();
                					gamemodes.put(uuid, (byte) -1);
                				} else {
                					gamemodes.put(uuid, (byte) packetdataserializer.i());
                				}
                			}
                			packetdataserializer.a(action);
    						packetdataserializer.d(gamemodes.size());
    						for(UUID uuid : gamemodes.keySet()) {
    							packetdataserializer.a(uuid);
    							packetdataserializer.d(gamemodes.get(uuid));
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
        pipeline.addBefore("packet_handler", "cutscene", channelDuplexHandler);
	}

	protected void unregister(Player player) {
		Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
		channel.eventLoop().submit(() -> {
			channel.pipeline().remove("cutscene");
			return null;
		});
	}

	protected void spawnCamera(Player player) {
		EntityPlayer entityplayer = ((CraftPlayer)player).getHandle();
		if(!cameradata.containsKey(entityplayer.getUniqueID())) return;
		CameraData data = cameradata.get(entityplayer.getUniqueID());
		EntityLiving cameraentity = null;
		CameraType type = data.cameratype;
		LocationPoint location = data.currentlocation;
		switch (type) {
		case NORMAL:
			EntityArmorStand stand = new EntityArmorStand(entityplayer.world, location.getX(), location.getY() - type.eyeheight, location.getZ());
			stand.yaw = location.getYaw();
			stand.pitch = location.getPitch();
			cameraentity = stand;
			break;
		case GREEN:
			EntityCreeper creeper = new EntityCreeper(EntityTypes.CREEPER, entityplayer.world);
			creeper.setLocation(location.getX(), location.getY() - type.eyeheight, location.getZ(), location.getYaw(), location.getPitch());
			cameraentity = creeper;
			break;
		case NEGATIVE:
			EntityEnderman enderman = new EntityEnderman(EntityTypes.ENDERMAN, entityplayer.world);
			enderman.setLocation(location.getX(), location.getY() - type.eyeheight, location.getZ(), location.getYaw(), location.getPitch());
			cameraentity = enderman;
			break;
		case SPLIT:
			EntitySpider spider = new EntitySpider(EntityTypes.SPIDER, entityplayer.world);
			spider.setLocation(location.getX(), location.getY() - type.eyeheight, location.getZ(), location.getYaw(), location.getPitch());
			cameraentity = spider;
			break;
		default:
			break;
		}
		if(cameraentity==null) return;
		cameraentity.setNoGravity(true);
		cameraentity.setInvisible(true);
		cameraentity.setInvulnerable(true);
		cameraentity.setSilent(true);
		data.cameraentity = cameraentity;
		
		PlayerConnection connection = entityplayer.playerConnection;
		if(data.hideinterface) connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE, entityplayer));
		if(data.hideinventory) connection.sendPacket(packetemptywindowitems);
		if(data.hideinterface) connection.sendPacket(new PacketPlayOutGameStateChange(new PacketPlayOutGameStateChange.a(3), 3));
		connection.sendPacket(new PacketPlayOutSpawnEntityLiving(cameraentity));
		connection.sendPacket(new PacketPlayOutEntityMetadata(cameraentity.getId(), cameraentity.getDataWatcher(), false));
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

		LocationPoint location = data.currentlocation;
		switch (newtype) {
		case NORMAL:
			EntityArmorStand stand = new EntityArmorStand(entityplayer.world, location.getX(), location.getY() - newtype.eyeheight, location.getZ());
			stand.yaw = location.getYaw();
			stand.pitch = location.getPitch();
			newcameraentity = stand;
			break;
		case GREEN:
			EntityCreeper creeper = new EntityCreeper(EntityTypes.CREEPER, entityplayer.world);
			creeper.setLocation(location.getX(), location.getY() - newtype.eyeheight, location.getZ(), location.getYaw(), location.getPitch());
			newcameraentity = creeper;
			break;
		case NEGATIVE:
			EntityEnderman enderman = new EntityEnderman(EntityTypes.ENDERMAN, entityplayer.world);
			enderman.setLocation(location.getX(), location.getY() - newtype.eyeheight, location.getZ(), location.getYaw(), location.getPitch());
			newcameraentity = enderman;
			break;
		case SPLIT:
			EntitySpider spider = new EntitySpider(EntityTypes.SPIDER, entityplayer.world);
			spider.setLocation(location.getX(), location.getY() - newtype.eyeheight, location.getZ(), location.getYaw(), location.getPitch());
			newcameraentity = spider;
			break;
		default:
			break;
		}
		if(newcameraentity==null) return;
		newcameraentity.setNoGravity(true);
		newcameraentity.setInvisible(true);
		newcameraentity.setInvulnerable(true);
		newcameraentity.setSilent(true);
		data.cameraentity = newcameraentity;
		
		PlayerConnection connection = entityplayer.playerConnection;
		connection.sendPacket(new PacketPlayOutSpawnEntityLiving(newcameraentity));
		connection.sendPacket(new PacketPlayOutEntityMetadata(newcameraentity.getId(), newcameraentity.getDataWatcher(), false));
		connection.sendPacket(new PacketPlayOutCamera(newcameraentity));
		connection.sendPacket(new PacketPlayOutEntityDestroy(oldcameraentity.getId()));
	}
	
	protected void updateCameraLocation(Player player) {
		EntityPlayer entityplayer = ((CraftPlayer)player).getHandle();
		if(!cameradata.containsKey(entityplayer.getUniqueID())) return;
		CameraData data = cameradata.get(entityplayer.getUniqueID());
		if(data.cameraentity == null || data.cameratype == null) return;
		EntityLiving cameraentity = (EntityLiving) data.cameraentity;
		LocationPoint location = data.currentlocation;
		
		PlayerConnection connection = entityplayer.playerConnection;
		if(data.previouslocation!=null&&data.previouslocation.hasMove(location)) {
			cameraentity.setLocation(location.getX(), location.getY() - data.cameratype.eyeheight, location.getZ(), location.getYaw(), location.getPitch());
			connection.sendPacket(new PacketPlayOutEntityTeleport(cameraentity));
		}
		connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(cameraentity.getId(), (short) 0, (short) 0, (short) 0, (byte) MathHelper.d(location.getYaw() * 256.0F / 360.0F), (byte) MathHelper.d(location.getPitch() * 256.0F / 360.0F), false));
		connection.sendPacket(new PacketPlayOutEntityHeadRotation(cameraentity,(byte) MathHelper.d(location.getYaw() * 256.0F / 360.0F)));
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
		connection.sendPacket(new PacketPlayOutCamera(entityplayer.getSpecatorTarget()));
		connection.sendPacket(new PacketPlayOutGameStateChange(new PacketPlayOutGameStateChange.a(3), entityplayer.playerInteractManager.getGameMode().getId()));
		connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE,entityplayer));
		connection.sendPacket(new PacketPlayOutAbilities(entityplayer.abilities));
		entityplayer.updateInventory(entityplayer.defaultContainer);
	}
	
}
