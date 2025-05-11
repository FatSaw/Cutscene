package me.bomb.camerautil;

import java.lang.reflect.Field;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
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
import net.minecraft.network.NetworkManager;
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
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.a;
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.protocol.game.PacketPlayOutWindowItems;

final class CameraManager_v1_19_R3 extends CameraManager {
	
	private static final PacketPlayOutWindowItems packetemptywindowitems;
	
	static {
		byte i = 46;
		NonNullList<ItemStack> nnl = NonNullList.a();
		ItemStack air = new ItemStack(Item.b(0));
		while(--i > -1) {
			nnl.add(air);
		}
		packetemptywindowitems = new PacketPlayOutWindowItems(0, 0, nnl, air);
	}

	protected void register(Player player, AtomicBoolean filter) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
        NetworkManager networkmanager;
		try {
			Field h = connection.getClass().getDeclaredField("h");
	        h.setAccessible(true);
	        networkmanager = (NetworkManager) h.get(connection);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return;
		}
		ChannelPipeline pipeline = networkmanager.m.pipeline();
        pipeline.addBefore("packet_handler", "cutscene", new PacketFilter(player.getUniqueId(), filter));
	}

	protected void unregister(Player player) {
		PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
        NetworkManager networkmanager;
		try {
			Field h = connection.getClass().getDeclaredField("h");
	        h.setAccessible(true);
	        networkmanager = (NetworkManager) h.get(connection);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return;
		}
		Channel channel = networkmanager.m;
		channel.eventLoop().submit(new Unregister(channel.pipeline()));
	}

	protected void spawnCamera(Player player) {
		EntityPlayer entityplayer = ((CraftPlayer)player).getHandle();
		if(!cameradata.containsKey(entityplayer.cs())) return;
		CameraData data = cameradata.get(entityplayer.cs());
		EntityLiving cameraentity = null;
		CameraType type = data.cameratype;
		LocationPoint location = data.currentlocation;
		switch (type) {
		case NORMAL:
			EntityArmorStand stand = new EntityArmorStand(entityplayer.H, location.getX(), location.getY() - type.eyeheight, location.getZ());
			stand.f(location.getYaw());
			stand.e(location.getPitch());
			cameraentity = stand;
			break;
		case GREEN:
			EntityCreeper creeper = new EntityCreeper(EntityTypes.u, entityplayer.H);
			creeper.a(location.getX(), location.getY() - type.eyeheight, location.getZ(), location.getYaw(), location.getPitch());
			cameraentity = creeper;
			break;
		case NEGATIVE:
			EntityEnderman enderman = new EntityEnderman(EntityTypes.E, entityplayer.H);
			enderman.a(location.getX(), location.getY() - type.eyeheight, location.getZ(), location.getYaw(), location.getPitch());
			cameraentity = enderman;
			break;
		case SPLIT:
			EntitySpider spider = new EntitySpider(EntityTypes.aS, entityplayer.H);
			spider.a(location.getX(), location.getY() - type.eyeheight, location.getZ(), location.getYaw(), location.getPitch());
			cameraentity = spider;
			break;
		default:
			break;
		}
		if(cameraentity==null) return;
		cameraentity.e(true);
		cameraentity.j(true);
		cameraentity.m(true);
		cameraentity.d(true);
		data.cameraentity = cameraentity;
		
		PlayerConnection connection = entityplayer.b;
		connection.a(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.a.c, entityplayer));
		connection.a(packetemptywindowitems);
		connection.a(new PacketPlayOutGameStateChange(new PacketPlayOutGameStateChange.a(3), 3));
		connection.a(new PacketPlayOutSpawnEntity(cameraentity, cameraentity.af()));
		connection.a(new PacketPlayOutEntityMetadata(cameraentity.af(), cameraentity.aj().c()));
		connection.a(new PacketPlayOutCamera(cameraentity));
	}
	
	protected void updateCameraType(Player player) {
		EntityPlayer entityplayer = ((CraftPlayer)player).getHandle();
		if(!cameradata.containsKey(entityplayer.cs())) return;
		CameraData data = cameradata.get(entityplayer.cs());
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
			EntityArmorStand stand = new EntityArmorStand(entityplayer.H, location.getX(), location.getY() - newtype.eyeheight, location.getZ());
			stand.f(location.getYaw());
			stand.e(location.getPitch());
			newcameraentity = stand;
			break;
		case GREEN:
			EntityCreeper creeper = new EntityCreeper(EntityTypes.u, entityplayer.H);
			creeper.a(location.getX(), location.getY() - newtype.eyeheight, location.getZ(), location.getYaw(), location.getPitch());
			newcameraentity = creeper;
			break;
		case NEGATIVE:
			EntityEnderman enderman = new EntityEnderman(EntityTypes.E, entityplayer.H);
			enderman.a(location.getX(), location.getY() - newtype.eyeheight, location.getZ(), location.getYaw(), location.getPitch());
			newcameraentity = enderman;
			break;
		case SPLIT:
			EntitySpider spider = new EntitySpider(EntityTypes.aS, entityplayer.H);
			spider.a(location.getX(), location.getY() - newtype.eyeheight, location.getZ(), location.getYaw(), location.getPitch());
			newcameraentity = spider;
			break;
		default:
			break;
		}
		if(newcameraentity==null) return;
		newcameraentity.e(true);
		newcameraentity.j(true);
		newcameraentity.m(true);
		newcameraentity.d(true);
		data.cameraentity = newcameraentity;
		
		PlayerConnection connection = entityplayer.b;
		connection.a(new PacketPlayOutSpawnEntity(newcameraentity, newcameraentity.af()));
		connection.a(new PacketPlayOutEntityMetadata(newcameraentity.af(), newcameraentity.aj().c()));
		connection.a(new PacketPlayOutCamera(newcameraentity));
		connection.a(new PacketPlayOutEntityDestroy(oldcameraentity.af()));
	}
	
	protected void updateCameraLocation(Player player) {
		EntityPlayer entityplayer = ((CraftPlayer)player).getHandle();
		if(!cameradata.containsKey(entityplayer.cs())) return;
		CameraData data = cameradata.get(entityplayer.cs());
		if(data.cameraentity == null || data.cameratype == null) return;
		EntityLiving cameraentity = (EntityLiving) data.cameraentity;
		LocationPoint location = data.currentlocation;
		
		PlayerConnection connection = entityplayer.b;
		if(data.previouslocation!=null&&data.previouslocation.hasMove(location)) {
			cameraentity.a(location.getX(), location.getY() - data.cameratype.eyeheight, location.getZ(), location.getYaw(), location.getPitch());
			connection.a(new PacketPlayOutEntityTeleport(cameraentity));
		}
		connection.a(new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(cameraentity.af(), (short) 0, (short) 0, (short) 0, (byte) MathHelper.d(location.getYaw() * 256.0F / 360.0F), (byte) MathHelper.d(location.getPitch() * 256.0F / 360.0F), false));
		connection.a(new PacketPlayOutEntityHeadRotation(cameraentity,(byte) MathHelper.d(location.getYaw() * 256.0F / 360.0F)));
	}
	
	protected void despawnCamera(Player player) {
		EntityPlayer entityplayer = ((CraftPlayer)player).getHandle();
		if(!cameradata.containsKey(entityplayer.cs())) return;
		CameraData data = cameradata.get(entityplayer.cs());
		EntityLiving cameraentity = (EntityLiving) data.cameraentity;
		
		PlayerConnection connection = entityplayer.b;
		connection.a(new PacketPlayOutEntityDestroy(cameraentity.af()));
	}
	
	@Override
	protected void restore(Player player) {
		EntityPlayer entityplayer = ((CraftPlayer)player).getHandle();

		PlayerConnection connection = entityplayer.b;
		connection.a(new PacketPlayOutCamera(entityplayer.G()));
		connection.a(new PacketPlayOutGameStateChange(new PacketPlayOutGameStateChange.a(3), entityplayer.d.b().a()));
		connection.a(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.a.c, entityplayer));
		connection.a(new PacketPlayOutAbilities(entityplayer.fK()));
		entityplayer.bP.b();
	}
	
public final static class PacketFilter extends ChannelDuplexHandler {
		
		private final UUID playeruuid;
		private final AtomicBoolean filter;
		
		public PacketFilter(UUID playeruuid, AtomicBoolean filter) {
			this.playeruuid = playeruuid;
			this.filter = filter;
		}
		
		@Override
        public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
			if (filter.get() && (packet instanceof PacketPlayInSteerVehicle || packet instanceof PacketPlayInVehicleMove
					|| packet instanceof PacketPlayInFlying || packet instanceof PacketPlayInPosition
					|| packet instanceof PacketPlayInPositionLook || packet instanceof PacketPlayInLook
					|| packet instanceof PacketPlayInBlockDig || packet instanceof PacketPlayInBlockPlace
					|| packet instanceof PacketPlayInArmAnimation || packet instanceof PacketPlayInWindowClick
					|| packet instanceof PacketPlayInBoatMove || packet instanceof PacketPlayInEntityAction
					|| packet instanceof PacketPlayInUseEntity || packet instanceof PacketPlayInUseItem)) {
				return;
			}
        	super.channelRead(context, packet);
        }
        @Override
        public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) throws Exception {
        	if(!filter.get()) {
        		super.write(context, packet, channelPromise);
        		return;
        	}
    		if(packet instanceof PacketPlayOutWindowItems) {
    	        packet = packetemptywindowitems;
            }
            if(packet instanceof PacketPlayOutSetSlot) {
            	return;
            }
        	if (packet instanceof ClientboundPlayerInfoUpdatePacket) {
        		ClientboundPlayerInfoUpdatePacket info = (ClientboundPlayerInfoUpdatePacket) packet;
        		PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer(0));
    			
        		info.a(packetdataserializer);
    			BitSet bitset = packetdataserializer.f(6);
    			EnumSet<a> enumset = EnumSet.noneOf(ClientboundPlayerInfoUpdatePacket.a.class);
    			a[] ae = ClientboundPlayerInfoUpdatePacket.a.values();
    	        for (int i = 0; i < 6; ++i) {
    	            if (bitset.get(i)) {
    	                enumset.add(ae[i]);
    	            }
    	        }
    	        for (a action : enumset) {
    	        	if(action != a.c) {
    	        		continue;
    	        	}
    	        	HashMap<UUID,Byte> gamemodes = new HashMap<UUID,Byte>();
        			int i = readVarInt(packetdataserializer); 
        			for (int j = 0; j < i; ++j) {
        				UUID uuid = packetdataserializer.o();
        				if(playeruuid.equals(uuid)) {
        					readVarInt(packetdataserializer);
        					gamemodes.put(uuid, (byte) -1);
        				} else {
        					gamemodes.put(uuid, (byte) readVarInt(packetdataserializer));
        				}
        			}
        			packetdataserializer.a(a.c);
        			packetdataserializer.d(gamemodes.size());
					for(Entry<UUID,Byte> entry : gamemodes.entrySet()) {
						packetdataserializer.a(entry.getKey());
						packetdataserializer.d(entry.getValue());
					}
        			info.a(packetdataserializer);
        			packet = info;
    	        }
        	}
        	super.write(context, packet, channelPromise);
        }
        
        private int readVarInt(PacketDataSerializer packetdataserializer) {
        	int i = 0;
            int j = 0;

            byte b0;

            do {
                b0 = packetdataserializer.readByte();
                i |= (b0 & 127) << j++ * 7;
                if (j > 5) {
                    throw new RuntimeException("VarInt too big");
                }
            } while ((b0 & 128) == 128);

            return i;
        }
		
	}
	
}