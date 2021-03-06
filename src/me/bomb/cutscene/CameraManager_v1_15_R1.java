package me.bomb.cutscene;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_15_R1.EntityArmorStand;
import net.minecraft.server.v1_15_R1.EntityCreeper;
import net.minecraft.server.v1_15_R1.EntityEnderman;
import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.EntitySpider;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.Item;
import net.minecraft.server.v1_15_R1.ItemStack;
import net.minecraft.server.v1_15_R1.MathHelper;
import net.minecraft.server.v1_15_R1.NonNullList;

import net.minecraft.server.v1_15_R1.PacketDataSerializer;
import net.minecraft.server.v1_15_R1.PacketPlayInArmAnimation;
import net.minecraft.server.v1_15_R1.PacketPlayInBlockDig;
import net.minecraft.server.v1_15_R1.PacketPlayInBlockPlace;
import net.minecraft.server.v1_15_R1.PacketPlayInBoatMove;
import net.minecraft.server.v1_15_R1.PacketPlayInEntityAction;
import net.minecraft.server.v1_15_R1.PacketPlayInFlying;
import net.minecraft.server.v1_15_R1.PacketPlayInSteerVehicle;
import net.minecraft.server.v1_15_R1.PacketPlayInUseEntity;
import net.minecraft.server.v1_15_R1.PacketPlayInUseItem;
import net.minecraft.server.v1_15_R1.PacketPlayInVehicleMove;
import net.minecraft.server.v1_15_R1.PacketPlayInWindowClick;
import net.minecraft.server.v1_15_R1.PacketPlayInFlying.PacketPlayInLook;
import net.minecraft.server.v1_15_R1.PacketPlayInFlying.PacketPlayInPosition;
import net.minecraft.server.v1_15_R1.PacketPlayInFlying.PacketPlayInPositionLook;
import net.minecraft.server.v1_15_R1.PacketPlayOutCamera;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntity;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_15_R1.PacketPlayOutGameStateChange;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_15_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_15_R1.PacketPlayOutWindowItems;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_15_R1.PacketPlayOutSetSlot;

class CameraManager_v1_15_R1 extends CameraManager {
	
	private static final PacketPlayOutWindowItems packetemptywindowitems;
	
	static {
		NonNullList<ItemStack> nnl = NonNullList.a();
		for (byte slot = 0; slot < 46; slot++) {
			nnl.add(slot, new ItemStack(Item.getById(0)));
		}
		packetemptywindowitems = new PacketPlayOutWindowItems(0, nnl);
	}

	protected CameraManager_v1_15_R1() {
		new BukkitRunnable(){
		    @Override
		    public void run(){
		    	Bukkit.getOnlinePlayers().forEach(player -> {
		    		if(contains(player.getUniqueId())) {
		    			EntityPlayer entityplayer = ((CraftPlayer) player).getHandle();
		    			EntityLiving cameraentity = (EntityLiving) eas.get(player.getUniqueId());
		    			RouteProvider route = pos.get(player.getUniqueId());
		    			if(route.isValid() && route.hasNext()) {
		    				LocationPoint loc = route.getNext();
		    				if (cameraentity.lastX - loc.getX() >= 0.1 || loc.getX() - cameraentity.lastX >= 0.1 || cameraentity.lastY - loc.getY() >= 0.1 || loc.getY() - cameraentity.lastY >= 0.1 || cameraentity.lastZ - loc.getZ() >= 0.1 || loc.getZ() - cameraentity.lastZ >= 0.1) {
		    					if(cameraentity instanceof EntityArmorStand) {
			    					cameraentity.setLocation(loc.getX(), loc.getY()-Consts.armorstandeyeheight, loc.getZ(), loc.getYaw(), loc.getPitch());
			    				} else if(cameraentity instanceof EntityCreeper) {
			    					cameraentity.setLocation(loc.getX(), loc.getY()-Consts.creepereyeheight, loc.getZ(), loc.getYaw(), loc.getPitch());
			    				} else if(cameraentity instanceof EntityEnderman) {
			    					cameraentity.setLocation(loc.getX(), loc.getY()-Consts.endermaneyeheight, loc.getZ(), loc.getYaw(), loc.getPitch());
			    				} else if(cameraentity instanceof EntitySpider) {
			    					cameraentity.setLocation(loc.getX(), loc.getY()-Consts.spidereyeheight, loc.getZ(), loc.getYaw(), loc.getPitch());
			    				}
		    					cameraentity.lastX = loc.getX();
		    					cameraentity.lastY = loc.getY();
		    					cameraentity.lastZ = loc.getZ();
		    					entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityTeleport(cameraentity));
		    				}
		    				entityplayer.playerConnection.sendPacket(new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(cameraentity.getId(), (short) 0, (short) 0, (short) 0, (byte) MathHelper.d(loc.getYaw() * 256.0F / 360.0F), (byte) MathHelper.d(loc.getPitch() * 256.0F / 360.0F), false));
		    				entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityHeadRotation(cameraentity,(byte) MathHelper.d(loc.getYaw() * 256.0F / 360.0F)));
		    			} else {
		    				if(Cutscene.api) {
		    					CameraType cameratype = CameraType.NOT_SET;
			    				if(cameraentity instanceof EntityArmorStand) {
			    					cameratype = CameraType.NORMAL;
			    				} else if(cameraentity instanceof EntityCreeper) {
			    					cameratype = CameraType.GREEN;
			    				} else if(cameraentity instanceof EntityEnderman) {
			    					cameratype = CameraType.NEGATIVE;
			    				} else if(cameraentity instanceof EntitySpider) {
			    					cameratype = CameraType.SPLIT;
			    				}
			    				boolean despawn = true;
			    				SceneEndEvent sceneendevent = new SceneEndEvent(player,route,cameratype);
			    				Bukkit.getPluginManager().callEvent(sceneendevent);
			    				if (sceneendevent.getNextRoute() != null && sceneendevent.getNextRoute().getRouteName().equals(route.getRouteName())) {
									route.resetStage();
									despawn = false;
								} else {
									RouteProvider newroute = sceneendevent.getNextRoute();
									if (newroute != null && newroute.isValid()) {
										pos.put(player.getUniqueId(), newroute);
										despawn = false;
									}
								}
			    				if(despawn == false) {
			    					SceneStartEvent sse = new SceneStartEvent(player, pos.get(player.getUniqueId()), cameratype);
			    					Bukkit.getPluginManager().callEvent(sse);
			    					if(sse.isCanceled()) despawn = true;
			    				}
			    				if(despawn) {
			    					despawnCamera(entityplayer, cameraentity);
			    				}
		    				} else {
		    					despawnCamera(entityplayer, cameraentity);
		    				}
		    			}
		    		}
		    	});
		    }
		}.runTaskTimerAsynchronously(Cutscene.getPlugin(Cutscene.class), 0L, 1);
	}
	
	public void startroute(Player player, RouteProvider route,CameraType type) {;
		if(route.isValid()) {
			EntityPlayer entityplayer = ((CraftPlayer) player).getHandle();
			LocationPoint loc = route.getNext();
			EntityLiving entity = null;
			switch (type) {
			case NORMAL:
				EntityArmorStand stand = new EntityArmorStand(entityplayer.world, loc.getX(), loc.getY()-Consts.armorstandeyeheight, loc.getZ());
				stand.yaw = loc.getYaw();
				stand.pitch = loc.getPitch();
				entity = stand;
			break;
			case GREEN:
				EntityCreeper creeper = new EntityCreeper(EntityTypes.CREEPER,entityplayer.world);
				creeper.setLocation(loc.getX(), loc.getY()-Consts.creepereyeheight, loc.getZ(), loc.getYaw(), loc.getPitch());
				entity = creeper;
			break;
			case NEGATIVE:
				EntityEnderman enderman = new EntityEnderman(EntityTypes.ENDERMAN,entityplayer.world);
				enderman.setLocation(loc.getX(), loc.getY()-Consts.endermaneyeheight, loc.getZ(), loc.getYaw(), loc.getPitch());
				entity = enderman;
			break;
			case SPLIT:
				EntitySpider spider = new EntitySpider(EntityTypes.SPIDER,entityplayer.world);
				spider.setLocation(loc.getX(), loc.getY()-Consts.spidereyeheight, loc.getZ(), loc.getYaw(), loc.getPitch());
				entity = spider;
			break;
			default:
			break;
			}
			if(entity!=null) {
				entity.setNoGravity(true);
				entity.setInvisible(true);
				entity.setInvulnerable(true);
				entity.setSilent(true);
				entity.lastX = loc.getX();
				entity.lastY = loc.getY();
				entity.lastZ = loc.getZ();
				if(Cutscene.api) {
					SceneStartEvent sse = new SceneStartEvent(player, route, type);
					Bukkit.getPluginManager().callEvent(sse);
					if(!sse.isCanceled()) {
						spawnCamera(entityplayer, route, entity);
					}
				} else {
					spawnCamera(entityplayer, route, entity);
				}
			}
		}
	}
	
	protected void registerHandler(Player player) {
		ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
            	if(contains(player.getUniqueId())) {
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
            	if(contains(player.getUniqueId())) {
            		if(packet instanceof PacketPlayOutWindowItems) {
            	        packet = packetemptywindowitems;
                    }
                    if(packet instanceof PacketPlayOutSetSlot) {
                    	return;
                    }
                	if (packet instanceof PacketPlayOutPlayerInfo) {
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
        pipeline.addBefore("packet_handler", "cutscene_" + player.getUniqueId(), channelDuplexHandler);
	}
	
	protected void unregisterHandler(Player player) {
		Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove("cutscene_" + player.getUniqueId());
            return null;
        });
	}

	private void spawnCamera(EntityPlayer entityplayer, RouteProvider route, EntityLiving cameraentity) {
		eas.put(entityplayer.getUniqueID(), cameraentity);
		pos.put(entityplayer.getUniqueID(), route);
		PlayerConnection connection = entityplayer.playerConnection;
		connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE, entityplayer));
		connection.sendPacket(packetemptywindowitems);
		connection.sendPacket(new PacketPlayOutGameStateChange(3, 3));
		connection.sendPacket(new PacketPlayOutSpawnEntityLiving(cameraentity));
		connection.sendPacket(new PacketPlayOutEntityMetadata(cameraentity.getId(), cameraentity.getDataWatcher(), false));
		connection.sendPacket(new PacketPlayOutCamera(cameraentity));
	}
	
	private void despawnCamera(EntityPlayer entityplayer, EntityLiving cameraentity) {
		eas.remove(entityplayer.getUniqueID());
		pos.remove(entityplayer.getUniqueID());
		PlayerConnection connection = entityplayer.playerConnection;
		connection.sendPacket(new PacketPlayOutCamera(entityplayer));
		connection.sendPacket(new PacketPlayOutEntityDestroy(cameraentity.getId()));
		connection.sendPacket(new PacketPlayOutGameStateChange(3,entityplayer.playerInteractManager.getGameMode().getId()));
		connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE, entityplayer));
		entityplayer.updateInventory(entityplayer.defaultContainer);
	}
	
}
