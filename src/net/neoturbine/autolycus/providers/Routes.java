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

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author Joseph Booker
 * 
 */
public class Routes implements BaseColumns {
	private Routes() {
	}

	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ AutolycusProvider.AUTHORITY + "/routes");

	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.autolycus.routes";

	static final String TABLE_NAME = "routes";

	public static final String RouteNumber = "rt";
	public static final String RouteName = "rtnm";
	public static final String System = "system";

	static final String Expiration = "expires";
}
