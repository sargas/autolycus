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
package net.neoturbine.autolycus;

import net.neoturbine.autolycus.providers.AutolycusProvider;
import net.neoturbine.autolycus.providers.Directions;
import net.neoturbine.autolycus.providers.Stops;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;

/**
 * SelectDirection will allow the user to choose a direction provided it is
 * passed the route and system name in an intent. Currently the only action upon
 * selecting a direction is to request for a stop and pass this along to the
 * calling activity.
 * 
 * @author Joseph Booker
 * 
 */
public class SelectDirection extends ListActivity implements
		OnItemClickListener {
	public static final String EXTRA_RT = "net.neoturbine.autolycus.SelectDirection.ROUTE";
	public static final String EXTRA_SYS = "net.neoturbine.autolycus.SelectDirection.SYSTEM";
	public static final String TAG = "Autolycus";

	/**
	 * Request Code for passing to startActivityForResult.
	 */
	private static final int GET_STOP = 0;

	private String rt;
	private String system;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(true);

		final Intent intent = getIntent();

		if (intent.getAction().equals(Intent.ACTION_PICK)) {
			rt = intent.getStringExtra(EXTRA_RT);
			system = intent.getStringExtra(EXTRA_SYS);
			loadDirections();
		}
	}

	/**
	 * Fills this ListActivity with directions.
	 * 
	 * An AsyncTask is created to determine the proper directions. A ListAdapter
	 * is created as needed, or the current one is changed to the cursor
	 * returned by the query.
	 * <p>
	 * As a convenience for OSU TRIPS, if there is only one direction, the list
	 * will be populated with the one and it is treated as if the only direction
	 * is selected.
	 * 
	 * @see Directions
	 */
	private void loadDirections() {
		getListView().setOnItemClickListener(this);

		new AsyncTask<Void, Void, Cursor>() {
			@Override
			protected void onPreExecute() {
				setProgressBarIndeterminateVisibility(true);
			}

			@Override
			protected Cursor doInBackground(Void... params) {
				final String[] PROJECTION = new String[] { Directions._ID,
						Directions.Direction };
				return managedQuery(Directions.CONTENT_URI, PROJECTION,
						Directions.System + "=? AND " + Directions.RouteNumber
								+ "=?", new String[] { system, rt }, null);
			}

			@Override
			protected void onPostExecute(Cursor result) {
				setProgressBarIndeterminateVisibility(false);

				if (result.getExtras().containsKey(AutolycusProvider.ERROR_MSG)) {
					setListAdapter(new ArrayAdapter<String>(
							SelectDirection.this,
							android.R.layout.simple_list_item_1,
							android.R.id.text1, new String[] { result
									.getExtras().getString(
											AutolycusProvider.ERROR_MSG) }));
					return;
				}

				if (getListAdapter() != null
						&& getListAdapter().getClass() != ArrayAdapter.class) {
					((SimpleCursorAdapter) getListAdapter())
							.changeCursor(result);
				} else {
					setListAdapter(new SimpleCursorAdapter(
							SelectDirection.this,
							android.R.layout.simple_list_item_1, result,
							new String[] { Directions.Direction },
							new int[] { android.R.id.text1 }));
				}

				if (result.getCount() == 1) {
					result.moveToFirst();
					String dir = result.getString(result
							.getColumnIndexOrThrow(Directions.Direction));
					getStopIntent(dir);
				}
			}
		}.execute();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if (position >= 0) {
			final Cursor c = (Cursor) parent.getItemAtPosition(position);
			final String dir = c.getString(c
					.getColumnIndexOrThrow(Directions.Direction));
			getStopIntent(dir);
		}
	}

	/**
	 * Creates an intent to allow the user to pick a stop.
	 * @param direction the direction which the user has selected.
	 * @see SelectStop
	 */
	protected void getStopIntent(String direction) {
		Intent forStop = new Intent(Intent.ACTION_PICK, Stops.CONTENT_URI);

		forStop.putExtra(SelectStop.EXTRA_SYSTEM, system);
		forStop.putExtra(SelectStop.EXTRA_ROUTE, rt);
		forStop.putExtra(SelectStop.EXTRA_DIRECTION, direction);

		startActivityForResult(forStop, GET_STOP);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GET_STOP) {
			if (resultCode == RESULT_OK) {
				setResult(RESULT_OK, data);
				finish();
			}
		}
	}
}
