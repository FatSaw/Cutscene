package me.bomb.cutscene;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class Route extends RouteProvider {
	private ArrayList<RouteLocationPoint> locations = new ArrayList<RouteLocationPoint>();

	public Route(String routename, Location... locations) {
		super(routename, locations[0].getWorld());
		int i = 0;
		while (i < locations.length) {
			Location location = locations[i];
			RouteLocationPoint lp = new RouteLocationPoint(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
			this.locations.add(lp);
			++i;
		}
		if (locations.length > 0) {
			save();
		}
	}
	
	public Route(String routename, World world, RouteLocationPoint... locations) {
		super(routename, world);
		int i = 0;
		while (i < locations.length) {
			RouteLocationPoint lp = locations[i];
			this.locations.add(lp);
			++i;
		}
		if (locations.length > 0) {
			save();
		}
	}
	
	protected Route(String routename,Location startlocation) {
		super(routename);
		FileConfiguration routedata = Cutscene.routedata;
		if (routedata!=null && routedata.contains(routename) && routedata.isList(routename + ".locations")) {
			int i = 0;
			for (String location : routedata.getStringList(routename + ".locations")) {
				RouteLocationPoint previouslocationpoint = null;
				try {
					if(i==0) {
						byte cc = 0;
						for(char c : location.toCharArray()) {
							if('#' == c) ++cc;
						}
						if(cc==8){
							previouslocationpoint = new RouteLocationPoint(startlocation.getX(), startlocation.getY(), startlocation.getZ(), startlocation.getYaw(), startlocation.getPitch(), startlocation.getYaw(), startlocation.getPitch());
						} else {
							previouslocationpoint = new RouteLocationPoint(startlocation.getX(), startlocation.getY(), startlocation.getZ(), startlocation.getYaw(), startlocation.getPitch());
						}
					} else {
						previouslocationpoint = locations.get(i-1);
					}
				} catch (IndexOutOfBoundsException e) {
				}
				RouteLocationPoint lp = RouteLocationPoint.readroutelocationpointfromstring(location,previouslocationpoint);
				if(lp!=null) {
					locations.add(lp);
					++i;
				}
			}
			locations.add(new RouteLocationPoint(startlocation.getX(), startlocation.getY(), startlocation.getZ(), startlocation.getYaw(), startlocation.getPitch()));
		}
	}

	private void save() {
		String routename = getRouteName();
		FileConfiguration routedata = Cutscene.routedata;
		if (routedata.contains(routename))
			routedata.set(routename, null);
		ArrayList<String> locationpoints = new ArrayList<String>();
		for (LocationPoint locationpoint : locations) {
			locationpoints.add(locationpoint.toPointString());
		}
		routedata.set(routename + ".world", getWorld().getName());
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
			locations[i] = locationPoint.toLocation(getWorld());
			++i;
		}
		return locations;
	}

	@Override
	public LocationPoint getNext() {
		int locationssize = locations.size();
		LocationPoint trl = null;
		int stage = getStage();
		if (stage < locationssize) {
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
		
		private static RouteLocationPoint readroutelocationpointfromstring(String location,RouteLocationPoint previouslocation) {
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
							yaw += previouslocation.yaw;
						}
						if (relativepitch) {
							pitch += previouslocation.pitch;
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
							
							x += previouslocation.x;
							y += previouslocation.y;
							z += previouslocation.z;
						} else {
							if (relativex) {
								x += previouslocation.x;
							}
							if (relativey) {
								y += previouslocation.y;
							}
							if (relativez) {
								z += previouslocation.z;
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
		protected String toPointString() {
			if (overrideyawpitch) {
				return "#" + x + "#" + y + "#" + z + "#" + yaw + "#" + pitch + "#" + oyaw + "#" + opitch + "#";
			}
			return "#" + x + "#" + y + "#" + z + "#" + yaw + "#" + pitch + "#";
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
		protected float getYaw() {
			if (overrideyawpitch) {
				return oyaw;
			}
			return yaw;
		}
		
		@Override
		protected float getPitch() {
			if (overrideyawpitch) {
				return opitch;
			}
			return pitch;
		}

	}
}
