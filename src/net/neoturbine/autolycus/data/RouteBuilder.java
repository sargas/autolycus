/**
 * 
 */
package net.neoturbine.autolycus.data;

import java.io.Serializable;

import android.util.Log;

/**
 * @author joe
 *
 */
public final class RouteBuilder implements Serializable{
		private transient static final long serialVersionUID = -8565655247745002659L;
		private transient static final String TAG = "Autolycus";
		private String name; //name of the route
		private String rt; //unique identifier, usually numeric
		private String system; //what system we use
		
		public RouteBuilder() {
			super();
		}
		
		public void setField(String name, String value) {
			if(name.equals("rtnm"))
				setName(value);
			else if (name.equals("rt"))
				setRt(value);
			else
				Log.w(TAG, "Unknown in Route object: "+name+" = "+value);
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
			return new Route(system,rt,name);
		}

}
