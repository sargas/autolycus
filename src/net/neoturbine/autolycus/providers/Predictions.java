/**
 * This file is part of Autolycus.
 * Copyright 2010 Joseph Jon Booker.
 *
 * Autolycus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Autolycus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with Autolycus.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.neoturbine.autolycus.providers;

import net.neoturbine.autolycus.internal.Prediction;
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

	public static String typeToNoun(String type) {
		if (type.equals(Integer.toString(Prediction.ARRIVAL_PREDICTION)))
			return "Arrival";
		else if (type.equals(Integer.toString(Prediction.DEPARTURE_PREDICTION)))
			return "Departure";
		return null;
	}

	public static String typeToVerb(int type) {
		if(type == Prediction.ARRIVAL_PREDICTION)
			return "Arriving";
		else if (type == Prediction.DEPARTURE_PREDICTION)
			return "Departing";
		return null;
	}

	public static String typeToVerb(String type) {
		return typeToVerb(Integer.parseInt(type));
	}
}
