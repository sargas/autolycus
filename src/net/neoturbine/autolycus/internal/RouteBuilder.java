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

import android.util.Log;

/**
 * @author joe
 * 
 */
public final class RouteBuilder implements Serializable {
	private transient static final long serialVersionUID = -8565655247745002659L;
	private transient static final String TAG = "Autolycus";
	private String name; // name of the route
	private String rt; // unique identifier, usually numeric
	private String system; // what system we use

	public RouteBuilder() {
		super();
	}

	public void setField(String name, String value) {
		if (name.equals("rtnm"))
			setName(value);
		else if (name.equals("rt"))
			setRt(value);
		else
			Log.w(TAG, "Unknown in Route object: " + name + " = " + value);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setRt(String rt) {
		this.rt = rt;
	}

	public String getRt() {
		return rt;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getSystem() {
		return system;
	}

	public Route toRoute() {
		return new Route(system, rt, name);
	}

}
