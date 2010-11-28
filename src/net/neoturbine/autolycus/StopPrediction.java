package net.neoturbine.autolycus;

import net.neoturbine.autolycus.providers.Predictions;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class StopPrediction extends ListActivity {
	public static final String OPEN_STOP_ACTION = "net.neoturbine.autolycus.openstop";

	private String system;
	private String route;
	private String direction;
	private int stpid;
	private String stpnm;

	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(true); // do this while loading ui
		// too

		loadIntent();
	}

	private void loadIntent() {
		final Intent intent = getIntent();

		setContentView(R.layout.stop_predictions);

		if (intent.getAction().equals(OPEN_STOP_ACTION)) {
			system = intent.getStringExtra(SelectStop.EXTRA_SYSTEM);
			route = intent.getStringExtra(SelectStop.EXTRA_ROUTE);
			direction = intent.getStringExtra(SelectStop.EXTRA_DIRECTION);
			stpid = intent.getIntExtra(SelectStop.EXTRA_STOPID, -1);
			stpnm = intent.getStringExtra(SelectStop.EXTRA_STOPNAME);
			loadStop();
		}
	}

	private void loadStop() {
		TextView titleView = (TextView) findViewById(R.id.txt_stop_name);
		titleView.setText("Route " + route + ": " + stpnm);
		((TextView) findViewById(R.id.txt_stop_dir)).setText(direction);
	}

	@Override
	public void onResume() {
		super.onResume();

		updatePredictions();
	}

	private void updatePredictions() {
		new AsyncTask<Void, Void, Cursor>() {
			@Override
			protected Cursor doInBackground(Void... params) {
				return managedQuery(Predictions.CONTENT_URI,
						Predictions.getColumns, Predictions.System + "=? AND "
								+ Predictions.StopID + "=?", new String[] {
								system, Integer.toString(stpid) }, null);
			}

			@Override
			protected void onPostExecute(Cursor cur) {
				setProgressBarIndeterminateVisibility(false);

				setListAdapter(null);
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
						pred.setPrediction(cursor);
						return true;
					}
				});

				if (cur.getCount() != 0)
					setListAdapter(adp);
				else
					((TextView) findViewById(R.id.txt_stop_error))
							.setText(R.string.no_arrival);
			}
		}.execute();
	}
}
