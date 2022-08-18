package me.bomb.cutscene;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import me.bomb.cutscene.internal.LocationPoint;
import me.bomb.cutscene.internal.RouteProvider;

public class Route extends RouteProvider {
	private final List<RouteLocationPoint> locations;
	
	public Route(String routename, World world, List<RouteLocationPoint> locations) {
		super(routename, world);
		this.locations = locations;
	}
	
	public static Route readRoute(FileConfiguration routedata,String routename,RouteLocationPoint previouslocation) {
		if (routedata!=null && routedata.contains(routename) && routedata.isList(routename + ".locations")) {
			World world = null;
			if (routedata.contains(routename + ".world") && routedata.isString(routename + ".world")) {
				world = Bukkit.getWorld(routedata.getString(routename + ".world"));
			}
			List<RouteLocationPoint> locations = new ArrayList<RouteLocationPoint>();
			for (String location : routedata.getStringList(routename + ".locations")) {
				RouteLocationPoint rlp = RouteLocationPoint.fromString(location, previouslocation);
				locations.add(rlp);
				previouslocation = rlp;
			}
			return new Route(routename, world, locations);
		}
		return null;
	}

	public void writeRoute(FileConfiguration routedata) {
		if (routedata.contains(routename)) routedata.set(routename, null);
		ArrayList<String> locationpoints = new ArrayList<String>();
		for (RouteLocationPoint locationpoint : locations) {
			locationpoints.add(locationpoint.toString());
		}
		if(world!=null) routedata.set(routename + ".world", world.getName());
		routedata.set(routename + ".locations", locationpoints);
		Cutscene.saveRoutes();
	}

	@Override
	public boolean hasNext() {
		return getStage() < locations.size();
	}

	public Location[] getLocations() {
		Location[] locations = new Location[this.locations.size()];
		int i = 0;
		for (LocationPoint locationPoint : this.locations) {
			locations[i] = locationPoint.toLocation(world);
			++i;
		}
		return locations;
	}

	@Override
	public LocationPoint getNext() {
		LocationPoint trl = null;
		int stage = getStage();
		if (stage < locations.size()) {
			trl = locations.get(stage);
			nextStage();
		}
		return trl;
	}
	
	public static class RouteLocationPoint extends LocationPoint {
		private boolean overrideyawpitch = false;
		private float oyaw;
		private float opitch;

		public RouteLocationPoint(double x,double y,double z,float yaw,float pitch) {
			super(x, y, z, yaw, pitch);
			this.oyaw = 0;
			this.opitch = 0;
			this.overrideyawpitch = false;
		}
		
		public RouteLocationPoint(double x,double y,double z,float yaw,float pitch,float oyaw,float opitch) {
			super(x, y, z, yaw, pitch);
			this.oyaw = oyaw;
			this.opitch = opitch;
			this.overrideyawpitch = true;
		}
		
		private static RouteLocationPoint fromString(String location,RouteLocationPoint previouslocation) {
			boolean overrideyawpitch = false;
			String sx = "";
			String sy = "";
			String sz = "";
			String syaw = "";
			String spitch = "";
			String soyaw = "";
			String sopitch = "";
			boolean relativelocation = false;
			if (location.startsWith("~")) {
				location = location.substring(1);
				relativelocation = true;
			}
			try {
				int i = 0;
				if (location.contains("#")) {
					i += location.indexOf("#");
					sx = location.substring(i + 1);
					if (sx.contains("#")) {
						i += sx.indexOf("#");
						sy = location.substring(i + 2);
						if (sy.contains("#")) {
							i += sy.indexOf("#");
							sz = location.substring(i + 3);
							if (sz.contains("#")) {
								i += sz.indexOf("#");
								syaw = location.substring(i + 4);
								if (syaw.contains("#")) {
									i += syaw.indexOf("#");
									spitch = location.substring(i + 5);
									if (relativelocation) {
										if (spitch.contains("#")) {
											i += spitch.indexOf("#");
											soyaw = location.substring(i + 6);
											if (soyaw.contains("#")) {
												i += soyaw.indexOf("#");
												sopitch = location.substring(i + 7);
											}
										}
									}
								}

							}

						}

					}
				}
				boolean relativex = false;
				boolean relativey = false;
				boolean relativez = false;
				boolean relativeyaw = false;
				boolean relativepitch = false;
				boolean relativeoyaw = false;
				boolean relativeopitch = false;
				sx = sx.substring(0, sx.indexOf("#"));
				sy = sy.substring(0, sy.indexOf("#"));
				sz = sz.substring(0, sz.indexOf("#"));
				syaw = syaw.substring(0, syaw.indexOf("#"));
				spitch = spitch.substring(0, spitch.indexOf("#"));
				if (sx.startsWith("~")) {
					sx = sx.substring(1);
					relativex = true;
				}
				if (sy.startsWith("~")) {
					sy = sy.substring(1);
					relativey = true;
				}
				if (sz.startsWith("~")) {
					sz = sz.substring(1);
					relativez = true;
				}
				if (syaw.startsWith("~")) {
					syaw = syaw.substring(1);
					relativeyaw = true;
				}
				if (spitch.startsWith("~")) {
					spitch = spitch.substring(1);
					relativepitch = true;
				}

				if (relativelocation && !soyaw.isEmpty() && !sopitch.isEmpty()) {
					soyaw = soyaw.substring(0, soyaw.indexOf("#"));
					sopitch = sopitch.substring(0, sopitch.indexOf("#"));
					if (soyaw.startsWith("~")) {
						soyaw = soyaw.substring(1);
						relativeoyaw = true;
					}
					if (sopitch.startsWith("~")) {
						sopitch = sopitch.substring(1);
						relativeopitch = true;
					}
					overrideyawpitch = true;
				}
				try {
					double x = Double.parseDouble(sx);
					double y = Double.parseDouble(sy);
					double z = Double.parseDouble(sz);
					float yaw = Float.parseFloat(syaw);
					float pitch = Float.parseFloat(spitch);
					
					if (previouslocation!=null) {
						if (relativeyaw) {
							yaw += previouslocation.getYaw();
						}
						if (relativepitch) {
							pitch += previouslocation.getPitch();
						}
						if (relativelocation) {
							if (x == -0.0D) {
								x = 0.0D;
							}

							if (y == -0.0D) {
								y = 0.0D;
							}

							if (z == -0.0D) {
								z = 0.0D;
							}
							
							float apitch = (float) Math.toRadians(pitch);
						    float ayaw = (float) Math.toRadians(yaw);
						    
						    double cospitch = TheMath.cos(apitch);
						    double cosyaw = TheMath.cos(ayaw);
						    double sinyaw = TheMath.sin(ayaw);
						    double sinpitch = TheMath.sin(apitch);
						    
						    double forwardadj = cospitch;
						    double forwardbz = cosyaw;
						    double forwardbx = sinyaw;
						    double forwardby = sinpitch;
						    
						    double sidebz = cosyaw;
						    double sidebx = sinyaw;
						    
						    double upadj = sinpitch;
						    double upbz = cosyaw;
						    double upbx = sinyaw;
						    double upby = cospitch;
						    
						    forwardbx*=forwardadj;
						    forwardbz*=forwardadj;
						    upbx*=upadj;
						    upbz*=upadj;
						    
						    double forwards = Math.sqrt(forwardbx*forwardbx+forwardby*forwardby+forwardbz*forwardbz);
						    double sides = Math.sqrt(sidebx*sidebx+sidebz*sidebz);
						    double ups = Math.sqrt(upbx*upbx+upby*upby+upbz*upbz);

							double forwardx = -(forwardbx/forwards)*x;
							double forwardy = -(forwardby/forwards)*x;
							double forwardz = (forwardbz/forwards)*x;
							double sidedx = (sidebz/sides)*z;
							double sidedz = (sidebx/sides)*z;
							double updx = -(upbx/ups)*y;
							double updy = (upby/ups)*y;
							double updz = (upbz/ups)*y;
							
							x=(forwardx+sidedx+updx);
							y=(forwardy+updy);
							z=(forwardz+sidedz+updz);
							
							x += previouslocation.getX();
							y += previouslocation.getY();
							z += previouslocation.getZ();
						} else {
							if (relativex) {
								x += previouslocation.getX();
							}
							if (relativey) {
								y += previouslocation.getY();
							}
							if (relativez) {
								z += previouslocation.getZ();
							}
						}
					}
					if (overrideyawpitch) {
						float oyaw = Float.parseFloat(soyaw);
						float opitch = Float.parseFloat(sopitch);
						if (previouslocation!=null) {
							if (relativeoyaw) {
								oyaw += previouslocation.oyaw;
							}
							if (relativeopitch) {
								opitch += previouslocation.opitch;
							}
						}
						return new RouteLocationPoint(x, y, z, yaw, pitch, oyaw, opitch);
					} 
					return new RouteLocationPoint(x, y, z, yaw, pitch);
				} catch (NullPointerException | NumberFormatException e) {
					return null;
				}
			} catch (IndexOutOfBoundsException e) {
				return null;
			}
		}
		
		@Override
		public String toString() {
			String str = super.toString();
			if (overrideyawpitch) {
				StringBuilder sb = new StringBuilder(str);
				sb.append(oyaw);
				sb.append("#");
				sb.append(opitch);
				sb.append("#");
				str = sb.toString();
			}
			return str;
		}
		
		public boolean isOverrideYawPitch() {
			return overrideyawpitch;
		}
		
		@Override
		public Location toLocation(World world) {
			if (overrideyawpitch) {
				return new Location(world, x, y, z, oyaw, opitch);
			}
			return new Location(world, x, y, z, yaw, pitch);
		}
		
		@Override
		public float getYaw() {
			if (overrideyawpitch) {
				return oyaw;
			}
			return yaw;
		}
		
		@Override
		public float getPitch() {
			if (overrideyawpitch) {
				return opitch;
			}
			return pitch;
		}

	}
}
