/**
 * 
 */
package net.neoturbine.autolycus.data;

import java.io.Serializable;

/**
 * @author joe
 *
 */
public final class Route implements Serializable {
	private static final long serialVersionUID = 48872748438125376L;
	private final String name; //name of the route
	private final String rt; //unique identifier, usually numeric
	private final String system; //what system we use

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