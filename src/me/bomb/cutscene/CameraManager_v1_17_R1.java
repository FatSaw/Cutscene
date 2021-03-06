package me.bomb.cutscene;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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

class CameraManager_v1_17_R1 extends CameraManager {
	
	private static final PacketPlayOutWindowItems packetemptywindowitems;
	
	static {
		NonNullList<ItemStack> nnl = NonNullList.a();
        for (byte slot = 0; slot < 46; slot++) {
        	nnl.add(slot, new ItemStack(Item.getById(0)));
        }
        packetemptywindowitems = new PacketPlayOutWindowItems(0,0,nnl,new ItemStack(Item.getById(0)));
	}
	
	protected CameraManager_v1_17_R1() {
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
		    				if (cameraentity.u - loc.getX() >= 0.1 || loc.getX() - cameraentity.u >= 0.1 || cameraentity.v - loc.getY() >= 0.1 || loc.getY() - cameraentity.v >= 0.1 || cameraentity.w - loc.getZ() >= 0.1 || loc.getZ() - cameraentity.w >= 0.1) {
		    					if(cameraentity instanceof EntityArmorStand) {
			    					cameraentity.setLocation(loc.getX(), loc.getY()-Consts.armorstandeyeheight, loc.getZ(), loc.getYaw(), loc.getPitch());
			    				} else if(cameraentity instanceof EntityCreeper) {
			    					cameraentity.setLocation(loc.getX(), loc.getY()-Consts.creepereyeheight, loc.getZ(), loc.getYaw(), loc.getPitch());
			    				} else if(cameraentity instanceof EntityEnderman) {
			    					cameraentity.setLocation(loc.getX(), loc.getY()-Consts.endermaneyeheight, loc.getZ(), loc.getYaw(), loc.getPitch());
			    				} else if(cameraentity instanceof EntitySpider) {
			    					cameraentity.setLocation(loc.getX(), loc.getY()-Consts.spidereyeheight, loc.getZ(), loc.getYaw(), loc.getPitch());
			    				}
		    					cameraentity.u = loc.getX();
		    					cameraentity.v = loc.getY();
		    					cameraentity.w = loc.getZ();
		    					entityplayer.b.sendPacket(new PacketPlayOutEntityTeleport(cameraentity));
		    				}
		    				entityplayer.b.sendPacket(new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(cameraentity.getId(), (short) 0, (short) 0, (short) 0, (byte) MathHelper.d(loc.getYaw() * 256.0F / 360.0F), (byte) MathHelper.d(loc.getPitch() * 256.0F / 360.0F), false));
		    				entityplayer.b.sendPacket(new PacketPlayOutEntityHeadRotation(cameraentity,(byte) MathHelper.d(loc.getYaw() * 256.0F / 360.0F)));
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
	
	public void startroute(Player player, RouteProvider route,CameraType type) {
		if(route.isValid()) {
			EntityPlayer entityplayer = ((CraftPlayer) player).getHandle();
			LocationPoint loc = route.getNext();
			EntityLiving entity = null;
			switch (type) {
			case NORMAL:
				EntityArmorStand stand = new EntityArmorStand(entityplayer.t, loc.getX(), loc.getY()-Consts.armorstandeyeheight, loc.getZ());
				stand.setYRot(loc.getYaw());
				stand.setXRot(loc.getPitch());
				entity = stand;
			break;
			case GREEN:
				EntityCreeper creeper = new EntityCreeper(EntityTypes.o,entityplayer.t);
				creeper.setLocation(loc.getX(), loc.getY()-Consts.creepereyeheight, loc.getZ(), loc.getYaw(), loc.getPitch());
				entity = creeper;
			break;
			case NEGATIVE:
				EntityEnderman enderman = new EntityEnderman(EntityTypes.w,entityplayer.t);
				enderman.setLocation(loc.getX(), loc.getY()-Consts.endermaneyeheight, loc.getZ(), loc.getYaw(), loc.getPitch());
				entity = enderman;
			break;
			case SPLIT:
				EntitySpider spider = new EntitySpider(EntityTypes.aI,entityplayer.t);
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
				entity.u = loc.getX();
				entity.v = loc.getY();
				entity.w = loc.getZ();
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
        pipeline.addBefore("packet_handler", "cutscene_" + player.getUniqueId(), channelDuplexHandler);
	}
	
	protected void unregisterHandler(Player player) {
		Channel channel = ((CraftPlayer) player).getHandle().b.a.k;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove("cutscene_" + player.getUniqueId());
            return null;
        });
	}
	
	private void spawnCamera(EntityPlayer entityplayer, RouteProvider route, EntityLiving cameraentity) {
		eas.put(entityplayer.getUniqueID(), cameraentity);
		pos.put(entityplayer.getUniqueID(), route);
		PlayerConnection connection = entityplayer.b;
		connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.b, entityplayer));
		connection.sendPacket(packetemptywindowitems);
		connection.sendPacket(new PacketPlayOutGameStateChange(new PacketPlayOutGameStateChange.a(3), 3));
		connection.sendPacket(new PacketPlayOutSpawnEntityLiving(cameraentity));
		connection.sendPacket(new PacketPlayOutEntityMetadata(cameraentity.getId(), cameraentity.getDataWatcher(), false));
		connection.sendPacket(new PacketPlayOutCamera(cameraentity));
	}
	
	private void despawnCamera(EntityPlayer entityplayer, EntityLiving cameraentity) {
		eas.remove(entityplayer.getUniqueID());
		pos.remove(entityplayer.getUniqueID());
		PlayerConnection connection = entityplayer.b;
		connection.sendPacket(new PacketPlayOutCamera(entityplayer));
		connection.sendPacket(new PacketPlayOutEntityDestroy(cameraentity.getId()));
		connection.sendPacket(new PacketPlayOutGameStateChange(new PacketPlayOutGameStateChange.a(3), entityplayer.d.getGameMode().getId()));
		connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.b, entityplayer));
		entityplayer.syncInventory();
	}
	
}
