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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import net.neoturbine.autolycus.internal.BusTimeAPI;
import net.neoturbine.autolycus.internal.Prediction;
import net.neoturbine.autolycus.internal.Route;
import net.neoturbine.autolycus.internal.StopInfo;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;

/**
 * @author Joseph Booker
 * 
 */
public class AutolycusProvider extends ContentProvider {
	private static final String TAG = "Autolycus";
	public static final String AUTHORITY = "net.neoturbine.providers.autolycus";
	public static final String ERROR_MSG = "error";

	public static final String DATABASE_NAME = "cache.db";
	private static final int DATABASE_VERSION = 2;

	private static final UriMatcher uriMatcher;
	private static final int URI_ROUTES = 1;
	private static final int URI_SYSTEMS = 2;
	private static final int URI_DIRECTIONS = 3;
	private static final int URI_STOPS = 4;
	private static final int URI_PREDICTIONS = 5;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, Routes.TABLE_NAME, URI_ROUTES);
		uriMatcher.addURI(AUTHORITY, Systems.TABLE_NAME, URI_SYSTEMS);
		uriMatcher.addURI(AUTHORITY, Directions.TABLE_NAME, URI_DIRECTIONS);
		uriMatcher.addURI(AUTHORITY, Stops.TABLE_NAME, URI_STOPS);
		uriMatcher.addURI(AUTHORITY, Predictions.TABLE_NAME, URI_PREDICTIONS);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			StringBuilder str = new StringBuilder();
			str.append("CREATE TABLE ").append(Routes.TABLE_NAME).append(" ('")
					.append(Routes._ID)
					.append("' INTEGER PRIMARY KEY AUTOINCREMENT, '")
					.append(Routes.System).append("' VARCHAR(255), '")
					.append(Routes.Expiration).append("' INTEGER, '")
					.append(Routes.RouteNumber)
					.append("' VARCHAR(255) UNIQUE, '")
					.append(Routes.RouteName).append("' VARCHAR(255) UNIQUE")
					.append(");");
			db.execSQL(str.toString());

			str = new StringBuilder();
			str.append("CREATE TABLE ").append(Systems.TABLE_NAME).append(" (")
					.append(Systems._ID)
					.append(" INTEGER PRIMARY KEY AUTOINCREMENT, '")
					.append(Systems.Name).append("' VARCHAR(255) UNIQUE, '")
					.append(Systems.TimeZone).append("' VARCHAR(255), '")
					.append(Systems.Abbrivation)
					.append("' VARCHAR(255) UNIQUE").append(");");
			db.execSQL(str.toString());

			addSystem("Chicago Transit Authority", "CTA", "America/Chicago", db);
			addSystem("Ohio State University TRIP", "OSU TRIP", "America/New_York", db);
			addSystem("MTA New York City Transit", "MTA", "America/New_York", db);

			str = new StringBuilder();
			str.append("CREATE TABLE ").append(Directions.TABLE_NAME)
					.append(" (").append(Directions._ID)
					.append(" INTEGER PRIMARY KEY AUTOINCREMENT, '")
					.append(Directions.System).append("' VARCHAR(255), '")
					.append(Directions.RouteNumber).append("' VARCHAR(255), '")
					.append(Directions.Direction).append("' VARCHAR(255), '")
					.append(Directions.Expiration).append("' INTEGER")
					.append(");");
			db.execSQL(str.toString());

			str = new StringBuilder();
			str.append("CREATE TABLE ").append(Stops.TABLE_NAME).append(" (")
					.append(Stops._ID)
					.append(" INTEGER PRIMARY KEY AUTOINCREMENT, '")
					.append(Stops.System).append("' VARCHAR(255), '")
					.append(Stops.RouteNumber).append("' VARCHAR(255), '")
					.append(Stops.Direction).append("' VARCHAR(255), '")
					.append(Stops.Name).append("' VARCHAR(255), '")
					.append(Stops.StopID).append("' INTEGER, '")
					.append(Stops.Longitude).append("' REAL, '")
					.append(Stops.Latitude).append("' REAL, '")
					.append(Stops.Expiration).append("' INTEGER").append(");");
			db.execSQL(str.toString());
		}

		private void addSystem(String name, String abbriv, String timezone, SQLiteDatabase db) {
			ContentValues cv = new ContentValues();
			cv.put(Systems.Name, name);
			cv.put(Systems.Abbrivation, abbriv);
			cv.put(Systems.TimeZone, timezone);
			db.insert(Systems.TABLE_NAME, null, cv);
		}

		/**
		 * Wipes database and recreates it. No need for anything persistant in the cache.
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			
			db.execSQL("DROP TABLE IF EXISTS "+Systems.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS "+Routes.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS "+Directions.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS "+Stops.TABLE_NAME);

			onCreate(db);
		}
	}

	private DatabaseHelper dbHelper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#delete(android.net.Uri,
	 * java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// nope
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case URI_ROUTES:
			return Routes.CONTENT_TYPE;
		case URI_SYSTEMS:
			return Systems.CONTENT_TYPE;
		case URI_DIRECTIONS:
			return Directions.CONTENT_TYPE;
		case URI_STOPS:
			return Stops.CONTENT_TYPE;
		case URI_PREDICTIONS:
			return Predictions.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#insert(android.net.Uri,
	 * android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext());
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#query(android.net.Uri,
	 * java.lang.String[], java.lang.String, java.lang.String[],
	 * java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		try {
			switch (uriMatcher.match(uri)) {
			case URI_ROUTES:
				qb.setTables(Routes.TABLE_NAME);
				// selection should be 'system=?'
				fetchRoutes(selectionArgs[0]);
				break;
			case URI_SYSTEMS:
				qb.setTables(Systems.TABLE_NAME);
				break;
			case URI_DIRECTIONS:
				qb.setTables(Directions.TABLE_NAME);
				// selection should be 'system=? AND rt=?'
				fetchDirections(selectionArgs[0], selectionArgs[1]);
				break;
			case URI_STOPS:
				qb.setTables(Stops.TABLE_NAME);
				// selection should be 'system=? rt=? dir=?'
				fetchStops(selectionArgs[0], selectionArgs[1], selectionArgs[2]);
				break;
			case URI_PREDICTIONS:
				// selection should be
				// 'system=? stpid=? [route=?]'
				return fetchPreds(selectionArgs[0], selectionArgs[1],
						selectionArgs.length < 3 ? null : selectionArgs[2],
						selectionArgs.length < 4 ? null : selectionArgs[3]);
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
			}
			final SQLiteDatabase db = dbHelper.getReadableDatabase();
			final Cursor c = qb.query(db, projection, selection, selectionArgs,
					null, null, sortOrder);
			c.setNotificationUri(getContext().getContentResolver(), uri);
			return c;
		} catch (NetworkRetrivalFailed e) {
			Cursor c = new MatrixCursor(new String[] { "_ID" }) {
				private Bundle mBundle = new Bundle();

				@Override
				public Bundle getExtras() {
					return mBundle;
				}
			};
			c.getExtras().putString(ERROR_MSG, e.getMessage());
			return c;
		}
	}

	private void fetchRoutes(String system) throws NetworkRetrivalFailed {
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(Routes.TABLE_NAME);
			/* don't query unless I am the only one querying/inserting */
			synchronized (this) {
				final Cursor c = qb.query(db, null, Routes.System + " = ? AND "
						+ Routes.Expiration + " > ?", new String[] { system,
						new Long(getToday()).toString() }, null, null, null);
				if (c.moveToLast()) {
					c.close();
					return;
				} else
					c.close();
				db.delete(Routes.TABLE_NAME, Routes.System + "=?",
						new String[] { system });
				ArrayList<Route> routes = BusTimeAPI.getRoutes(getContext(),
						system);
				for (Route r : routes) {
					ContentValues cv = new ContentValues();
					cv.put(Routes.RouteName, r.getName());
					cv.put(Routes.RouteNumber, r.getRt());
					cv.put(Routes.System, r.getSystem());
					cv.put(Routes.Expiration, getTomorrow());
					db.insert(Routes.TABLE_NAME, null, cv);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			throw new NetworkRetrivalFailed(e.getMessage());
		}
	}

	private void fetchDirections(String system, String rt)
			throws NetworkRetrivalFailed {
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(Directions.TABLE_NAME);
			/* don't query unless I am the only one querying/inserting */
			synchronized (this) {
				final Cursor c = qb.query(db, null, Directions.System
						+ " = ? AND " + Directions.RouteNumber + " = ? AND "
						+ Directions.Expiration + " > ?", new String[] {
						system, rt, new Long(getToday()).toString() }, null,
						null, null);
				if (c.moveToLast()) {
					c.close();
					return;
				} else
					c.close();
				db.delete(Directions.TABLE_NAME, Directions.System + "=? AND "
						+ Directions.RouteNumber + "=?", new String[] { system,
						rt });
				ArrayList<String> dirs = BusTimeAPI.getDirections(getContext(),
						system, rt);
				for (String d : dirs) {
					ContentValues cv = new ContentValues();
					cv.put(Directions.System, system);
					cv.put(Directions.RouteNumber, rt);
					cv.put(Directions.Direction, d);
					cv.put(Directions.Expiration, getFuture());
					db.insert(Directions.TABLE_NAME, null, cv);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			throw new NetworkRetrivalFailed(e.getMessage());
		}
	}

	private void fetchStops(String system, String rt, String dir)
			throws NetworkRetrivalFailed {
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(Stops.TABLE_NAME);
			/* don't query unless I am the only one querying/inserting */
			synchronized (this) {
				final Cursor c = qb.query(
						db,
						null,
						Stops.System + " = ? AND " + Stops.RouteNumber
								+ " = ? AND " + Stops.Direction + " = ? AND "
								+ Stops.Expiration + " > ?",
						new String[] { system, rt, dir,
								new Long(getToday()).toString() }, null, null,
						null);
				if (c.moveToLast()) {
					c.close();
					return;
				} else
					c.close();
				db.delete(Stops.TABLE_NAME, Stops.System + "=? AND "
						+ Stops.RouteNumber + "=? AND " + Stops.Direction
						+ "=?", new String[] { system, rt, dir });
				ArrayList<StopInfo> stops = BusTimeAPI.getStops(getContext(),
						system, rt, dir);
				for (StopInfo stop : stops) {
					ContentValues cv = new ContentValues();
					cv.put(Stops.System, system);
					cv.put(Stops.RouteNumber, rt);
					cv.put(Stops.Direction, dir);
					cv.put(Stops.Name, stop.getStpnm());
					cv.put(Stops.StopID, stop.getStpid());
					cv.put(Stops.Longitude, stop.getLon());
					cv.put(Stops.Latitude, stop.getLat());
					cv.put(Stops.Expiration, getTomorrow());
					db.insert(Stops.TABLE_NAME, null, cv);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			throw new NetworkRetrivalFailed(e.getMessage());
		}
	}

	private Cursor fetchPreds(String system, String stpid, String route,
			String dir) {
		// need some out of band communications....
		MatrixCursor cur = new MatrixCursor(Predictions.getColumns) {
			private Bundle mBundle = new Bundle();

			@Override
			public Bundle getExtras() {
				return mBundle;
			}
		};
		try {
			// Get our system for the time zone information
			Cursor sysCur = query(Systems.CONTENT_URI,
						new String[] {Systems.TimeZone},
						Systems.Name +" = ?",
						new String[] {system}, null);

			sysCur.moveToFirst();
			final String timeZone = sysCur.getString(sysCur.getColumnIndex(Systems.TimeZone));
			sysCur.close();

			ArrayList<Prediction> preds = BusTimeAPI.getPrediction(
					getContext(), system, stpid, route, timeZone);
			int i = 0;
			for (Prediction pred : preds) {
				if (dir != null)
					if (!pred.getDirection().equals(dir))
						continue;
				MatrixCursor.RowBuilder row = cur.newRow();
				row.add(i++);
				row.add(pred.getRoute());
				row.add(system);
				row.add(pred.getDirection());
				row.add(pred.getStopID());
				row.add(pred.getStopName());
				row.add(pred.getVid());
				row.add(pred.getDistance());
				row.add(pred.isDelayed());
				row.add(pred.getDestination());
				row.add(pred.getType());
				row.add(pred.getEstTime().getTime());
				row.add(pred.getPredTime().getTime());
			}
		} catch (Exception e) {
			cur.getExtras().putString(ERROR_MSG, e.getMessage());
		}
		return cur;
	}

	private long getTomorrow() {
		return getToday() + DateUtils.DAY_IN_MILLIS;
	}

	private long getToday() {
		return new Date().getTime();
	}

	private long getFuture() {
		Calendar cal = Calendar.getInstance();
		cal.set(2030, 1, 1);
		return cal.getTimeInMillis();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#update(android.net.Uri,
	 * android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

	private class NetworkRetrivalFailed extends Exception {
		private static final long serialVersionUID = 5966554991083244524L;

		public NetworkRetrivalFailed(String message) {
			super(message);
		}
	}

}
