/**
 * 
 */
package net.neoturbine.autolycus.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.util.Log;

/**
 * @author Joseph Booker
 *
 */
public class ServiceBulletinBuilder {
	private boolean isSet = false;
	
	private String name;
	private String subject;
	private String detail;
	private String brief;
	private String priority;
	private List<Map<String,String>> serviceAffected = new ArrayList<Map<String,String>>();
	
	public boolean isSet() {
		return isSet; 
	}
	
	public ServiceBulletin toBulletin() {
		return new ServiceBulletin(name, subject, detail, brief, priority, serviceAffected);
	}
	
	public void setField(String tag, String value) {
		isSet = true;
		if(tag.equals("nm"))
			name = value;
		else if (tag.equals("sbj"))
			subject = value;
		else if (tag.equals("dtl"))
			detail = value;
		else if (tag.equals("brf"))
			brief = value;
		else if (tag.equals("prty"))
			priority = value;
		else if(tag.equals("rt") || tag.equals("srvc") || tag.equals("rtdir"))
			Log.w("Bus Track", "Skipping rt|srvc|rtdir");
		else
			Log.w("Bus Track", "Unknown in ServiceBulletin object: " + tag + " = "
					+ value);
	}
}
