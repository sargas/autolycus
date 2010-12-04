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

import java.io.Serializable;

/**
 * @author joe
 * 
 */
public final class Route implements Serializable {
	private static final long serialVersionUID = 48872748438125376L;
	private final String name; // name of the route
	private final String rt; // unique identifier, usually numeric
	private final String system; // what system we use

	public Route(String system, String rt, String name) {
		this.system = system;
		this.rt = rt;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getRt() {
		return rt;
	}

	public String getSystem() {
		return system;
	}

}