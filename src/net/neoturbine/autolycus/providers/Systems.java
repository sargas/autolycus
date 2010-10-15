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
public final class Systems implements BaseColumns {
	private Systems() {}
	
	public static final Uri CONTENT_URI = Uri.parse(
			"content://" + AutolycusProvider.AUTHORITY + "/systems");
	
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.autolycus.systems";
	
	static final String TABLE_NAME = "systems";
	
	public static final String Name = "name";
	public static final String Abbrivation = "abbriv";
}
