/**
 * 
 */
package net.neoturbine.autolycus.internal;

/**
 * @author joe
 *
 */
public final class StopInfo {
	private final String system;
	private final String rt;
	private final String dir;
	private final String stpnm;
	private final int stpid;
	private final double lat; //both in WGS 84
	private final double lon;

	public StopInfo(String system, String rt, String dir,
			String stpnm, int stpid,
			double lat, double lon) {
		this.system = system;
		this.rt = rt;
		this.dir = dir;
		this.stpnm = stpnm;
		this.stpid = stpid;
		this.lat = lat;
		this.lon = lon;
	}

	public String getSystem() {
		return system;
	}

	public String getRt() {
		return rt;
	}

	public String getDir() {
		return dir;
	}

	public String getStpnm() {
		return stpnm;
	}

	public int getStpid() {
		return stpid;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}
}
