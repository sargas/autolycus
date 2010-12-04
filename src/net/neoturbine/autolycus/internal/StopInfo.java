/**
 * This file is part of Autolycus.
 * Copyright 2010 Joseph Jon Booker.
 *
 * Autolycus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Autolycus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with Autolycus.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.neoturbine.autolycus.internal;

/**
 * @author Joseph Booker
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
