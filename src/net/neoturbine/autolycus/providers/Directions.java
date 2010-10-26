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
public class Directions implements BaseColumns {
	private Directions() {}
	
	public static final Uri CONTENT_URI = Uri.parse(
			"content://" + AutolycusProvider.AUTHORITY + "/directions");

	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.autolycus.directions";

	static final String TABLE_NAME = "directions";

	public static final String RouteNumber = "rt";
	public static final String System = "system";
	public static final String Direction = "dir";

	public static final String Expiration = "expires";

}
