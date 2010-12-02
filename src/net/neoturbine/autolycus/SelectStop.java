package net.neoturbine.autolycus;

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
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class SelectStop extends ListActivity  implements OnItemClickListener{
	public static final String SELECT_STOP = "net.neoturbine.autolycus.SELECT_STOP";
	public static final String EXTRA_SYSTEM = "net.neoturbine.autolycus.SelectStop.SYSTEM";
	public static final String EXTRA_ROUTE = "net.neoturbine.autolycus.SelectStop.ROUTE";
	public static final String EXTRA_DIRECTION = "net.neoturbine.autolycus.SelectStop.DIRECTION";
	public static final String EXTRA_STOPID = "net.neoturbine.autolycus.SelectStop.StopID";
	public static final String EXTRA_STOPNAME = "net.neoturbine.autolycus.SelectStop.StopName";

	public static final String TAG = "Autolycus";

	private String system;
	private String route;
	private String direction;
	
	private static final int PICK_DIRECTION = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Intent intent = getIntent();

		//show download progress
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		if(intent.getAction().equals(Intent.ACTION_PICK)) {
			Intent dirintent = new Intent(Intent.ACTION_PICK,Directions.CONTENT_URI);
			startActivityForResult(dirintent, PICK_DIRECTION);
		}
		this.getListView().setFastScrollEnabled(true);
	}
	
	private void loadStops() {
		getListView().setOnItemClickListener(this);

		new AsyncTask<Void, Void, Cursor>() {
			@Override
			protected void onPreExecute() {
				setProgressBarIndeterminateVisibility(true);
			}
			@Override
			protected Cursor doInBackground(Void... params) {
				final String[] PROJECTION = new String[] {
						Stops._ID, Stops.Name, Stops.StopID
				};
				return managedQuery(Stops.CONTENT_URI, PROJECTION,
						Stops.System+" =? AND "+
						Stops.RouteNumber+" =? AND "+
						Stops.Direction+" =?",
						new String[] {system, route,direction} , null);
			}
			@Override
			protected void onPostExecute(Cursor result) {
				setProgressBarIndeterminateVisibility(false);
				if(getListAdapter() != null) {
					((SimpleCursorAdapter)getListAdapter()).changeCursor(result);
				} else {
					setListAdapter(new SimpleCursorAdapter(SelectStop.this,
							android.R.layout.simple_list_item_1,
							result,
							new String[] { Stops.Name },
							new int[] { android.R.id.text1 }));
				}
			}
		}.execute();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode,
            Intent data) {
		if(requestCode == PICK_DIRECTION) {
			if(resultCode == RESULT_OK) {
				system = data.getStringExtra(EXTRA_SYSTEM);
				route = data.getStringExtra(EXTRA_ROUTE);
				direction = data.getStringExtra(EXTRA_DIRECTION);

				loadStops();
			}
		}
	}

	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if(position >= 0) {
			Intent result = new Intent();
			result.setAction(Intent.ACTION_DEFAULT);
			result.putExtra(EXTRA_SYSTEM, system);
			result.putExtra(EXTRA_DIRECTION, direction);
			result.putExtra(EXTRA_ROUTE, route);
			final Cursor c = (Cursor) parent.getItemAtPosition(position);
			result.putExtra(EXTRA_STOPID, c.getInt(c.getColumnIndexOrThrow(Stops.StopID)));
			result.putExtra(EXTRA_STOPNAME, c.getString(c.getColumnIndexOrThrow(Stops.Name)));
			setResult(RESULT_OK,result);
			finish();
		}
    }
}
