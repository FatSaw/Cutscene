package me.bomb.camerautil;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.monster.EntityCreeper;
import net.minecraft.world.entity.monster.EntityEnderman;
import net.minecraft.world.entity.monster.EntitySpider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.PacketPlayInArmAnimation;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayInBlockPlace;
import net.minecraft.network.protocol.game.PacketPlayInBoatMove;
import net.minecraft.network.protocol.game.PacketPlayInEntityAction;
import net.minecraft.network.protocol.game.PacketPlayInFlying;
import net.minecraft.network.protocol.game.PacketPlayInFlying.PacketPlayInLook;
import net.minecraft.network.protocol.game.PacketPlayInFlying.PacketPlayInPosition;
import net.minecraft.network.protocol.game.PacketPlayInFlying.PacketPlayInPositionLook;
import net.minecraft.network.protocol.game.PacketPlayInSteerVehicle;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;
import net.minecraft.network.protocol.game.PacketPlayInUseItem;
import net.minecraft.network.protocol.game.PacketPlayInVehicleMove;
import net.minecraft.network.protocol.game.PacketPlayInWindowClick;
import net.minecraft.network.protocol.game.PacketPlayOutAbilities;
import net.minecraft.network.protocol.game.PacketPlayOutCamera;
import net.minecraft.network.protocol.game.PacketPlayOutEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.network.protocol.game.PacketPlayOutWindowItems;

final class CameraManager_v1_17_R1 extends CameraManager {
	
	private static final PacketPlayOutWindowItems packetemptywindowitems;
	
	static {
		byte i = 46;
		NonNullList<ItemStack> nnl = NonNullList.a();
		ItemStack air = new ItemStack(Item.getById(0));
		while(--i > -1) {
			nnl.add(air);
		}
		packetemptywindowitems = new PacketPlayOutWindowItems(0, 0, nnl, air);
	}

	protected void register(Player player, AtomicBoolean filter) {
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
            		if(packet instanceof PacketPlayOutWindowItems) {
            	        packet = packetemptywindowitems;
                    }
                    if(packet instanceof PacketPlayOutSetSlot) {
                    	return;
                    }
                	if (packet instanceof PacketPlayOutPlayerInfo) {
                		PacketPlayOutPlayerInfo info = (PacketPlayOutPlayerInfo) packet;
                		PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer(0));
            			info.a(packetdataserializer);
            			EnumPlayerInfoAction action = packetdataserializer.a(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.class);
                		switch (action) {
                		case b :
                			HashMap<UUID,Byte> gamemodes = new HashMap<UUID,Byte>();
                			int i = packetdataserializer.j();
                			for (int j = 0; j < i; ++j) {
                				UUID uuid = packetdataserializer.l();
                				if(player.getUniqueId().equals(uuid)) {
                					packetdataserializer.j();
                					gamemodes.put(uuid, (byte) -1);
                				} else {
                					gamemodes.put(uuid, (byte) packetdataserializer.j());
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
        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().b.a.k.pipeline();
        pipeline.addBefore("packet_handler", "cutscene", channelDuplexHandler);
	}

	protected void unregister(Player player) {
		Channel channel = ((CraftPlayer) player).getHandle().b.a.k;
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
			EntityArmorStand stand = new EntityArmorStand(entityplayer.t, location.getX(), location.getY() - type.eyeheight, location.getZ());
			stand.setYRot(location.getYaw());
			stand.setXRot(location.getPitch());
			cameraentity = stand;
			break;
		case GREEN:
			EntityCreeper creeper = new EntityCreeper(EntityTypes.o, entityplayer.t);
			creeper.setLocation(location.getX(), location.getY() - type.eyeheight, location.getZ(), location.getYaw(), location.getPitch());
			cameraentity = creeper;
			break;
		case NEGATIVE:
			EntityEnderman enderman = new EntityEnderman(EntityTypes.w, entityplayer.t);
			enderman.setLocation(location.getX(), location.getY() - type.eyeheight, location.getZ(), location.getYaw(), location.getPitch());
			cameraentity = enderman;
			break;
		case SPLIT:
			EntitySpider spider = new EntitySpider(EntityTypes.aI, entityplayer.t);
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
		
		PlayerConnection connection = entityplayer.b;
		connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.b, entityplayer));
		connection.sendPacket(packetemptywindowitems);
		connection.sendPacket(new PacketPlayOutGameStateChange(new PacketPlayOutGameStateChange.a(3), 3));
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
			EntityArmorStand stand = new EntityArmorStand(entityplayer.t, location.getX(), location.getY() - newtype.eyeheight, location.getZ());
			stand.setYRot(location.getYaw());
			stand.setXRot(location.getPitch());
			newcameraentity = stand;
			break;
		case GREEN:
			EntityCreeper creeper = new EntityCreeper(EntityTypes.o, entityplayer.t);
			creeper.setLocation(location.getX(), location.getY() - newtype.eyeheight, location.getZ(), location.getYaw(), location.getPitch());
			newcameraentity = creeper;
			break;
		case NEGATIVE:
			EntityEnderman enderman = new EntityEnderman(EntityTypes.w, entityplayer.t);
			enderman.setLocation(location.getX(), location.getY() - newtype.eyeheight, location.getZ(), location.getYaw(), location.getPitch());
			newcameraentity = enderman;
			break;
		case SPLIT:
			EntitySpider spider = new EntitySpider(EntityTypes.aI, entityplayer.t);
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
		
		PlayerConnection connection = entityplayer.b;
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
		
		PlayerConnection connection = entityplayer.b;
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
		
		PlayerConnection connection = entityplayer.b;
		connection.sendPacket(new PacketPlayOutEntityDestroy(cameraentity.getId()));
	}
	
	@Override
	protected void restore(Player player) {
		EntityPlayer entityplayer = ((CraftPlayer)player).getHandle();

		PlayerConnection connection = entityplayer.b;
		connection.sendPacket(new PacketPlayOutCamera(entityplayer.getSpecatorTarget()));
		connection.sendPacket(new PacketPlayOutGameStateChange(new PacketPlayOutGameStateChange.a(3), entityplayer.d.getGameMode().getId()));
		connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.b, entityplayer));
		connection.sendPacket(new PacketPlayOutAbilities(entityplayer.getAbilities()));
		entityplayer.bV.updateInventory();
	}
	
}