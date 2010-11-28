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
public class Predictions implements BaseColumns {
	private Predictions() {
	}

	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ AutolycusProvider.AUTHORITY + "/predictions");

	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.autolycus.predictions";

	static final String TABLE_NAME = "predictions";

	public static final String RouteNumber = "rt";
	public static final String System = "system";
	public static final String Direction = "dir";
	public static final String StopID = "stpid";
	public static final String StopName = "stpnm";
	public static final String VehicleID = "vid";
	public static final String DistanceToStop = "distance";
	public static final String isDelayed = "delay";
	public static final String Destination = "des";
	public static final String Type = "type";
	public static final String EstimatedTime = "esttime";
	public static final String PredictionTime = "predtime";

	public static final String Expiration = "expires";

	public static final String[] getColumns = { _ID, RouteNumber, System,
			Direction, StopID, StopName, VehicleID, DistanceToStop, isDelayed,
			Destination, Type, EstimatedTime, PredictionTime };

}
