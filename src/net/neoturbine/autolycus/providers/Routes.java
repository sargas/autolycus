/**
 * 
 */
package net.neoturbine.autolycus.providers;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author Joseph Booker
 *
 */
public class Routes implements BaseColumns {
	private Routes() {}
	
	public static final Uri CONTENT_URI = Uri.parse(
			"content://" + AutolycusProvider.AUTHORITY + "/routes");

	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.autolycus.routes";

	static final String TABLE_NAME = "routes";

	public static final String RouteNumber = "rt";
	public static final String RouteName = "rtnm";
	public static final String System = "system";

	public static final String Expiration = "expires";
}
