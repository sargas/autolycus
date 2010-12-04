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

import net.neoturbine.autolycus.prefs.Prefs;
import net.neoturbine.autolycus.providers.AutolycusProvider;
import net.neoturbine.autolycus.providers.Predictions;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class StopPrediction extends ListActivity {
	/**
	 * The action which home screen shortcuts must have to be displayed by this
	 * activity.
	 */
	public static final String OPEN_STOP_ACTION = "net.neoturbine.autolycus.openstop";

	/**
	 * How often we recalculate the times for the predictions.
	 */
	public static final long PREDICTION_DRAWING_DELAY = 20 * 1000;

	private String system;
	private String route;
	private String direction;
	private int stpid;
	private String stpnm;
	private long previousPrediction = -1;

	/**
	 * Reference to the timer mechanism used to control the periodic updates.
	 */
	private Handler timerHandler = new Handler();

	/**
	 * If true, filter buses at this stop by those with the correct route number
	 * and direction.
	 */
	private boolean limitRoute = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(true); // do this while loading ui

		setContentView(R.layout.stop_predictions);

		findViewById(R.id.predictions_showall).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						limitRoute = !(((CheckBox) v).isChecked());
						new updatePredictionsTask().execute();
					}
				});
		loadIntent();
	}

	/**
	 * Process the data from the current intent into local fields.
	 */
	private void loadIntent() {
		final Intent intent = getIntent();

		if (intent.getAction().equals(OPEN_STOP_ACTION)) {
			system = intent.getStringExtra(SelectStop.EXTRA_SYSTEM);
			route = intent.getStringExtra(SelectStop.EXTRA_ROUTE);
			direction = intent.getStringExtra(SelectStop.EXTRA_DIRECTION);
			stpid = intent.getIntExtra(SelectStop.EXTRA_STOPID, -1);
			stpnm = intent.getStringExtra(SelectStop.EXTRA_STOPNAME);
			loadStop();
		}
	}

	@Override
	public void onNewIntent(Intent i) {
		super.onNewIntent(i);
		setIntent(i);
		loadIntent();
	}

	/**
	 * Provide UI support for loading components of the stop information.
	 */
	private void loadStop() {
		TextView titleView = (TextView) findViewById(R.id.txt_stop_name);
		titleView.setText("Route " + route + ": " + stpnm);
		((TextView) findViewById(R.id.txt_stop_dir)).setText(direction);
	}

	@Override
	public void onResume() {
		super.onResume();
		limitRoute = true;
		new updatePredictionsTask().execute();
	}

	@Override
	public void onPause() {
		super.onPause();
		timerHandler.removeCallbacks(updateViewsTask);
	}

	/**
	 * A Runnable which calculates the next time it should be run, and either
	 * forces the list view to re-display itself or calls updatePredictionsTask
	 * to retrieve new stop predictions.
	 */
	private Runnable updateViewsTask = new Runnable() {
		public void run() {
			final long currentTime = System.currentTimeMillis();
			final int delay = Integer
					.parseInt(PreferenceManager.getDefaultSharedPreferences(
							StopPrediction.this).getString("update_delay",
							new Integer(Prefs.DEFAULT_UPDATE_DELAY).toString()));

			if (delay == 0)
				return;
			if (currentTime - previousPrediction > delay * 1000) {
				new updatePredictionsTask().execute();
			} else {
				android.util.Log.v("StopPrediction", "Redrawing");
				((BaseAdapter)getListAdapter()).notifyDataSetChanged();
				final long timeTillUpdate = delay * 1000
						- (currentTime - previousPrediction);
				if (timeTillUpdate < PREDICTION_DRAWING_DELAY)
					timerHandler.postDelayed(this, timeTillUpdate);
				else
					timerHandler.postDelayed(this, PREDICTION_DRAWING_DELAY);
			}

		}
	};

	/**
	 * An AsyncTask which performs a network query in the background, using the
	 * result to populate the ListView and schedule an update.
	 */
	private class updatePredictionsTask extends AsyncTask<Void, Void, Cursor> {
		@Override
		protected void onPreExecute() {
			timerHandler.removeCallbacks(updateViewsTask);
			setProgressBarIndeterminateVisibility(true);
			((CheckBox) findViewById(R.id.predictions_showall))
					.setChecked(!limitRoute);
		}

		@Override
		protected Cursor doInBackground(Void... params) {
			if (!limitRoute)
				return managedQuery(Predictions.CONTENT_URI,
						Predictions.getColumns, Predictions.System + "=? AND "
								+ Predictions.StopID + "=?", new String[] {
								system, Integer.toString(stpid) }, null);
			else
				return managedQuery(Predictions.CONTENT_URI,
						Predictions.getColumns, Predictions.System + "=? AND "
								+ Predictions.StopID + "=? AND "
								+ Predictions.RouteNumber + "=? AND "
								+ Predictions.Direction + "=?", new String[] {
								system, Integer.toString(stpid), route,
								direction }, null);
		}

		@Override
		protected void onPostExecute(Cursor cur) {
			setProgressBarIndeterminateVisibility(false);

			setListAdapter(null);
			((TextView) findViewById(R.id.txt_stop_predtime)).setText("");

			if (cur.getExtras().containsKey(AutolycusProvider.ERROR_MSG)) {
				((TextView) findViewById(R.id.txt_stop_error)).setText(cur
						.getExtras().getString(AutolycusProvider.ERROR_MSG));
				return;
			}
			((TextView) findViewById(R.id.txt_stop_error)).setText("");

			SimpleCursorAdapter adp = new SimpleCursorAdapter(
					StopPrediction.this, R.layout.prediction, cur,
					new String[] { Predictions._ID },
					new int[] { R.id.prediction_entry });

			adp.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
				@Override
				public boolean setViewValue(View view, Cursor cursor,
						int columnIndex) {
					PredictionView pred = (PredictionView) view;
					pred.setPrediction(cursor, !limitRoute);
					return true;
				}
			});

			if (cur.getCount() != 0) {
				cur.moveToFirst();
				((TextView) findViewById(R.id.txt_stop_predtime))
						.setText("Predicted: "
								+ PredictionView.prettyTime(cur.getLong(cur
										.getColumnIndexOrThrow(Predictions.PredictionTime))));
				setListAdapter(adp);
				registerForContextMenu(getListView());
				previousPrediction = System.currentTimeMillis();
				timerHandler.removeCallbacks(updateViewsTask);
				timerHandler.postDelayed(updateViewsTask,
						PREDICTION_DRAWING_DELAY);
			} else
				((TextView) findViewById(R.id.txt_stop_error))
						.setText(R.string.no_arrival);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.prediction, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.prediction_update:
			new updatePredictionsTask().execute();
			return true;
		case R.id.prediction_prefs:
			startActivity(new Intent(this, Prefs.class));
			return true;
		}
		return false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.prediction_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.prediction_details:
			Cursor c = (Cursor) getListAdapter().getItem(info.position);

			Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.prediction_detail);
			dialog.setTitle("Details");

			((TextView) dialog.findViewById(R.id.prediction_detail_sys))
					.setText(c.getString(c
							.getColumnIndexOrThrow(Predictions.System)));
			((TextView) dialog.findViewById(R.id.prediction_detail_rt))
					.setText(c.getString(c
							.getColumnIndexOrThrow(Predictions.RouteNumber)));
			((TextView) dialog.findViewById(R.id.prediction_detail_dir))
					.setText(c.getString(c
							.getColumnIndexOrThrow(Predictions.Direction)));
			((TextView) dialog.findViewById(R.id.prediction_detail_des))
					.setText(c.getString(c
							.getColumnIndexOrThrow(Predictions.Destination)));
			((TextView) dialog.findViewById(R.id.prediction_detail_type))
					.setText(Predictions.typeToString(c.getString(c
							.getColumnIndexOrThrow(Predictions.Type))));
			((TextView) dialog.findViewById(R.id.prediction_detail_stpid))
					.setText(c.getString(c
							.getColumnIndexOrThrow(Predictions.StopID)));
			((TextView) dialog.findViewById(R.id.prediction_detail_stpnm))
					.setText(c.getString(c
							.getColumnIndexOrThrow(Predictions.StopName)));
			((TextView) dialog.findViewById(R.id.prediction_detail_dist))
					.setText(c.getString(c
							.getColumnIndexOrThrow(Predictions.DistanceToStop)));
			((TextView) dialog.findViewById(R.id.prediction_detail_delayed))
					.setText(c.getString(c
							.getColumnIndexOrThrow(Predictions.isDelayed)));
			((TextView) dialog.findViewById(R.id.prediction_detail_vid))
					.setText(c.getString(c
							.getColumnIndexOrThrow(Predictions.VehicleID)));
			((TextView) dialog.findViewById(R.id.prediction_detail_esttime))
					.setText(PredictionView.prettyTime(c.getLong(c
							.getColumnIndexOrThrow(Predictions.EstimatedTime))));
			((TextView) dialog.findViewById(R.id.prediction_detail_predtime))
					.setText(PredictionView.prettyTime(c.getLong(c
							.getColumnIndexOrThrow(Predictions.PredictionTime))));

			dialog.setOwnerActivity(this);
			dialog.show();

		}
		return false;
	}
}
