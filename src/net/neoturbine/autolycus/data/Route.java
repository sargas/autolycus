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
	private String name; //name of the route
	private String rt; //unique identifier, usually numeric
	private String system; //what system we use

	public Route(String system,String rt) {
		this.system = system;
		this.rt = rt;
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