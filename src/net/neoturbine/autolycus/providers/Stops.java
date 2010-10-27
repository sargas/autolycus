/**
 * 
 */
package net.neoturbine.autolycus.providers;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author joe
 *
 */
public class Stops implements BaseColumns {
	private Stops() {}
	
	public static final Uri CONTENT_URI = Uri.parse(
			"content://" + AutolycusProvider.AUTHORITY + "/stops");

	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.autolycus.stops";

	static final String TABLE_NAME = "stops";

	public static final String RouteNumber = "rt";
	public static final String System = "system";
	public static final String Direction = "dir";
	public static final String Name = "stpnm";
	public static final String StopID = "stpid";
	public static final String Latitude = "lat";
	public static final String Longitude = "lon";

	public static final String Expiration = "expires";


}
