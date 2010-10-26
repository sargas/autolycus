/**
 * 
 */
package net.neoturbine.autolycus.providers;

import java.util.ArrayList;
import java.util.Date;
import net.neoturbine.autolycus.BusTimeAPI;
import net.neoturbine.autolycus.data.Route;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;

/**
 * @author Joseph Booker
 *
 */
public class AutolycusProvider extends ContentProvider {
	private static final String TAG = "Autolycus";
	public static final String AUTHORITY = "net.neoturbine.providers.autolycus";

	private static final String DATABASE_NAME = "cache.db";
	private static final int DATABASE_VERSION = 1;

	private static final UriMatcher uriMatcher;
	private static final int URI_ROUTES = 1;
	private static final int URI_SYSTEMS = 2;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, Routes.TABLE_NAME, URI_ROUTES);
		uriMatcher.addURI(AUTHORITY, Systems.TABLE_NAME, URI_SYSTEMS);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context,DATABASE_NAME,null,DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//TODO (potentially): convert to specialized methods in SQLiteDatabase
			StringBuilder str = new StringBuilder();
			str.append("CREATE TABLE ").append(Routes.TABLE_NAME)
			.append(" ('").append(Routes._ID).append("' INTEGER PRIMARY KEY AUTOINCREMENT, '")
			.append(Routes.System).append("' VARCHAR(255), '")
			.append(Routes.Expiration).append("' INTEGER, '")
			.append(Routes.RouteNumber).append("' VARCHAR(255) UNIQUE, '")
			.append(Routes.RouteName).append("' VARCHAR(255) UNIQUE")
			.append(");");
			db.execSQL(str.toString());

			str = new StringBuilder();
			str.append("CREATE TABLE ").append(Systems.TABLE_NAME)
			.append(" (").append(Systems._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, '")
			.append(Systems.Name).append("' VARCHAR(255) UNIQUE, '")
			.append(Systems.Abbrivation).append("' VARCHAR(255) UNIQUE")
			.append(");");
			db.execSQL(str.toString());

			ContentValues cta = new ContentValues();
			cta.put(Systems.Name, "Chicago Transit Authority");
			cta.put(Systems.Abbrivation, "CTA");
			db.insert(Systems.TABLE_NAME, null, cta);

			ContentValues trip = new ContentValues();
			trip.put(Systems.Name, "Ohio State University TRIP");
			trip.put(Systems.Abbrivation, "OSU TRIP");
			db.insert(Systems.TABLE_NAME, null, trip);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + Routes.TABLE_NAME);
			onCreate(db);
		}
	}
	private DatabaseHelper dbHelper;


	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		//nope
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		switch(uriMatcher.match(uri)) {
		case URI_ROUTES:
			return Routes.CONTENT_TYPE;
		case URI_SYSTEMS:
			return Systems.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext());
		return true;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch(uriMatcher.match(uri)) {
		case URI_ROUTES:
			qb.setTables(Routes.TABLE_NAME);
			//selection should be 'system=?'
			Log.v(TAG,"Performing a route query on "+selectionArgs[0]);
			fetchRoutes(selectionArgs[0]);
			break;
		case URI_SYSTEMS:
			qb.setTables(Systems.TABLE_NAME);
			Log.v(TAG,"Performing a system query");
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		final SQLiteDatabase db = dbHelper.getReadableDatabase();
		final Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	private void fetchRoutes(String system) {
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(Routes.TABLE_NAME);
			/* don't query unless I am the only one querying/inserting */
			synchronized(this) {
				final Cursor c = qb.query(db,null,
						Routes.System+" = ? AND "+
						Routes.Expiration+" > ?",
						new String[] {system, new Long(getToday()).toString()}, null, null, null);
				if(c.moveToLast()) {
					c.close();return;
				} else c.close();
				db.delete(Routes.TABLE_NAME,
						Routes.System +"=?", new String[] {system});
				ArrayList<Route> routes = BusTimeAPI.getRoutes(getContext(), system);
				for(Route r : routes) {
					ContentValues cv = new ContentValues();
					cv.put(Routes.RouteName, r.getName());
					cv.put(Routes.RouteNumber, r.getRt());
					cv.put(Routes.System, r.getSystem());
					cv.put(Routes.Expiration, getTomorrow());
					db.insert(Routes.TABLE_NAME, null, cv);
				}
			}
		} catch (Exception e) {
			Log.e(TAG,e.toString());
		}
	}

	private long getTomorrow() {
		return getToday() + DateUtils.DAY_IN_MILLIS;
	}

	private long getToday() {
		return new Date().getTime();
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

}
