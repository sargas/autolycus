package net.neoturbine.autolycus;

import net.neoturbine.autolycus.providers.Directions;
import net.neoturbine.autolycus.providers.Routes;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter;

public class SelectDirection extends ListActivity implements OnItemClickListener {
	public static final String SELECT_DIRECTION = "net.neoturbine.autolycus.SELECT_DIRECTION";
	public static final String EXTRA_RT = "net.neoturbine.autolycus.SelectDirection.ROUTE";
	public static final String EXTRA_SYS = "net.neoturbine.autolycus.SelectDirection.SYSTEM";
	public static final String TAG = "Autolycus";

	private static final int PICK_ROUTE = 2;

	//private ArrayList<String> directions;
	//private Route route;
	private String rt;
	private String system;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Intent intent = getIntent();

		if(intent.getAction().equals(Intent.ACTION_PICK)) {
			Intent routeintent = new Intent(Intent.ACTION_PICK,Routes.CONTENT_URI);
			startActivityForResult(routeintent, PICK_ROUTE);
		}
	}

	private void loadDirections() {
		getListView().setOnItemClickListener(this);

		new AsyncTask<Void, Void, Cursor>() {
			@Override
			protected Cursor doInBackground(Void... params) {
				final String[] PROJECTION = new String[] {
						Directions._ID, Directions.Direction
				};
				return managedQuery(Directions.CONTENT_URI, PROJECTION,
						Directions.System+"=? AND "+
						Directions.RouteNumber+"=?",
						new String[] {system, rt} , null);
			}
			@Override
			protected void onPostExecute(Cursor result) {
				if(getListAdapter() != null) {
					((SimpleCursorAdapter)getListAdapter()).changeCursor(result);
				} else {
					setListAdapter(new SimpleCursorAdapter(SelectDirection.this,
							android.R.layout.simple_list_item_1,
							result,
							new String[] { Directions.Direction },
							new int[] { android.R.id.text1 }));
				}
			}
		}.execute();
	}

	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if(position >= 0) {
			final Cursor c = (Cursor) parent.getItemAtPosition(position);
			final String dir = c.getString(c.getColumnIndexOrThrow(Directions.Direction));
			setResult(RESULT_OK,getStop(dir));
			finish();
		}
	}    


	protected Intent getStop(String direction) {
		Intent forStop = new Intent();

		forStop.setAction(SelectStop.SELECT_STOP);
		forStop.putExtra(SelectStop.EXTRA_SYSTEM, system);
		forStop.putExtra(SelectStop.EXTRA_ROUTE, rt);
		forStop.putExtra(SelectStop.EXTRA_DIRECTION, direction);

		return forStop;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_ROUTE) {
			if(resultCode == RESULT_OK) {
				rt = data.getStringExtra(EXTRA_RT);
				system = data.getStringExtra(EXTRA_SYS);
				loadDirections();
			}
		}
	}
}
