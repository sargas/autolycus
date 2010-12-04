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
public final class Systems implements BaseColumns {
	private Systems() {}
	
	public static final Uri CONTENT_URI = Uri.parse(
			"content://" + AutolycusProvider.AUTHORITY + "/systems");
	
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.autolycus.systems";
	
	static final String TABLE_NAME = "systems";
	
	public static final String Name = "name";
	public static final String Abbrivation = "abbriv";
}
