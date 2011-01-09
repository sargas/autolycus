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
public class ServiceBulletins implements BaseColumns {
	private ServiceBulletins() {
	}
	
	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ AutolycusProvider.AUTHORITY + "/servicebulletins");
	
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.autolycus.bulletins";
	
	static final String TABLE_NAME = "servicebulletins";
	
	public static final String System = "system";
	public static final String Route = "rt";
	
	public static final String Name = "nm";
	public static final String Subject = "sbj";
	public static final String Detail = "dtl";
	public static final String Brief = "brf";
	public static final String Priority = "prty";

	public static final String ForStop = "for_stop";
	static final String Expiration = "expires";
}
