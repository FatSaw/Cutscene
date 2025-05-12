package me.bomb.cutscene.route;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import me.bomb.camerautil.DirectedLocationPoint;
import me.bomb.camerautil.LocationPoint;

import static me.bomb.cutscene.utils.TheMath.sin;
import static me.bomb.cutscene.utils.TheMath.cos;

public class Route extends AbstractRoute {
	private final LocationPoint[] locations;
	
	public Route(String routename, LocationPoint[] locations) {
		super(routename);
		this.locations = locations;
	}
	
	public Route(FileConfiguration routedata,String routename,LocationPoint previouslocation) throws IllegalArgumentException {
		super(routename);
		List<String> locationss;
		if (routedata==null || (locationss = routedata.getStringList(routename.concat(".locations"))).isEmpty()) {
			throw new IllegalArgumentException(routename);
		}
		ArrayList<LocationPoint> locations = new ArrayList<LocationPoint>();
		for(String location : locationss) {
			if(location==null||location.isEmpty()) {
				continue;
			}
			int previousseparatorindex = location.indexOf("$");
			int repeatcount = 1;
			if(previousseparatorindex>-1) {
				try {
					repeatcount = Integer.valueOf(location.substring(0, previousseparatorindex));
				} catch (NumberFormatException e) {
				}
			}
			previousseparatorindex = location.indexOf("#");
			if(previousseparatorindex<0) {
				continue;
			}
			boolean relativelocation = previousseparatorindex>0 && location.charAt(previousseparatorindex-1) == '~';
			int separatorindex = location.indexOf("#", ++previousseparatorindex);
			if(separatorindex<0) {
				continue;
			}
			boolean relativex = location.charAt(previousseparatorindex) == '~';
			if(relativex) {
				++previousseparatorindex;
			}
			double sx,sy,sz;
			try {
				sx = Double.parseDouble(location.substring(previousseparatorindex, separatorindex));
			} catch (NumberFormatException e) {
				continue;
			}
			previousseparatorindex = ++separatorindex;
			separatorindex = location.indexOf("#", separatorindex);
			if(separatorindex<0) {
				continue;
			}
			boolean relativey = location.charAt(previousseparatorindex) == '~';
			if(relativey) {
				++previousseparatorindex;
			}
			try {
				sy = Double.parseDouble(location.substring(previousseparatorindex, separatorindex));
			} catch (NumberFormatException e) {
				continue;
			}
			previousseparatorindex = ++separatorindex;
			separatorindex = location.indexOf("#", separatorindex);
			if(separatorindex<0) {
				continue;
			}
			boolean relativez = location.charAt(previousseparatorindex) == '~';
			if(relativez) {
				++previousseparatorindex;
			}
			try {
				sz = Double.parseDouble(location.substring(previousseparatorindex, separatorindex));
			} catch (NumberFormatException e) {
				continue;
			}
			previousseparatorindex = ++separatorindex;
			separatorindex = location.indexOf("#", separatorindex);
			if(separatorindex<0) {
				continue;
			}
			boolean relativeyaw = location.charAt(previousseparatorindex) == '~';
			if(relativeyaw) {
				++previousseparatorindex;
			}
			float syaw,spitch;
			try {
				syaw = Float.parseFloat(location.substring(previousseparatorindex, separatorindex));
			} catch (NumberFormatException e) {
				continue;
			}
			previousseparatorindex = ++separatorindex;
			separatorindex = location.indexOf("#", separatorindex);
			if(separatorindex<0) {
				continue;
			}
			boolean relativepitch = location.charAt(previousseparatorindex) == '~';
			if(relativepitch) {
				++previousseparatorindex;
			}
			try {
				spitch = Float.parseFloat(location.substring(previousseparatorindex, separatorindex));
			} catch (NumberFormatException e) {
				continue;
			}
			previousseparatorindex = ++separatorindex;
			separatorindex = location.indexOf("#", separatorindex);
			boolean directed = separatorindex>-1;
			float soyaw = 0f,sopitch = 0f;
			boolean relativeoyaw = false,relativeopitch = false;
			if(directed) {
				relativeoyaw = location.charAt(previousseparatorindex) == '~';
				if(relativeoyaw) {
					++previousseparatorindex;
				}
				try {
					soyaw = Float.parseFloat(location.substring(previousseparatorindex, separatorindex));
				} catch (NumberFormatException e) {
					continue;
				}
				previousseparatorindex = ++separatorindex;
				separatorindex = location.indexOf("#", separatorindex);
				if(separatorindex<0) {
					continue;
				}
				relativeopitch = location.charAt(previousseparatorindex) == '~';
				if(relativeopitch) {
					++previousseparatorindex;
				}
				try {
					sopitch = Float.parseFloat(location.substring(previousseparatorindex, separatorindex));
				} catch (NumberFormatException e) {
					continue;
				}
				previousseparatorindex = ++separatorindex;
				separatorindex = location.indexOf("#", separatorindex);
			}
			for(int j = repeatcount; --j > -1;) {
				double x = sx, y = sy, z = sz;
				float yaw = syaw, pitch = spitch, oyaw = soyaw, opitch = sopitch;
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
						float apitch = (float) Math.toRadians(pitch),ayaw = (float) Math.toRadians(yaw);
					    double cospitch = cos(apitch),cosyaw = cos(ayaw),sinyaw = sin(ayaw),sinpitch = sin(apitch), forwardbz = cosyaw * cospitch,forwardbx = sinyaw * cospitch, upbz = cosyaw * sinpitch,upbx = sinyaw * sinpitch, forwards = Math.sqrt(forwardbx*forwardbx+sinpitch*sinpitch+forwardbz*forwardbz),sides = Math.sqrt(sinyaw*sinyaw+cosyaw*cosyaw),ups = Math.sqrt(upbx*upbx+cospitch*cospitch+upbz*upbz), forwardx = -(forwardbx/forwards)*x,forwardy = -(sinpitch/forwards)*x,forwardz = (forwardbz/forwards)*x, sidedx = (cosyaw/sides)*z,sidedz = (sinyaw/sides)*z, updx = -(upbx/ups)*y,updy = (cospitch/ups)*y,updz = (upbz/ups)*y;
						x = previouslocation.getX();
						y = previouslocation.getY();
						z = previouslocation.getZ();
					    x+=forwardx+sidedx+updx;
					    y+=forwardy+updy;
					    z+=forwardz+sidedz+updz;
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
				LocationPoint locationpoint;
				if(!directed) {
					locationpoint = new LocationPoint(x, y, z, yaw, pitch);
					locations.add(locationpoint);
					previouslocation = locationpoint;
					continue;
				}
				if (previouslocation!=null && previouslocation instanceof DirectedLocationPoint) {
					DirectedLocationPoint previousroutelocation = (DirectedLocationPoint) previouslocation;
					if (relativeoyaw) {
						oyaw += previousroutelocation.getDirectionYaw();
					}
					if (relativeopitch) {
						opitch += previousroutelocation.getDirectionPitch();
					}
				}
				locationpoint = new DirectedLocationPoint(x, y, z, yaw, pitch, oyaw, opitch);
				locations.add(locationpoint);
				previouslocation = locationpoint;
			}
		}
		this.locations = locations.toArray(new LocationPoint[locations.size()]);
	}

	@Override
	public boolean hasNext() {
		return getStage() < locations.length;
	}

	@Override
	public LocationPoint getNext() {
		LocationPoint trl = null;
		int stage = getStage();
		if (stage < locations.length) {
			trl = locations[stage];
			nextStage();
		}
		return trl;
	}
}
