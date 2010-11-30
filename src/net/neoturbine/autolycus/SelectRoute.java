package net.neoturbine.autolycus;

import net.neoturbine.autolycus.providers.Routes;
import net.neoturbine.autolycus.providers.Systems;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class SelectRoute extends ListActivity implements OnItemClickListener {
	public static final String SELECT_ROUTE = "net.neoturbine.autolycus.SELECT_ROUTE";
	private Spinner systemSpin;
	private String system;
	private SimpleCursorAdapter rva;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(true);

		setContentView(R.layout.routepicker);
		
		getListView().setOnItemClickListener(this);
		systemSpin = (Spinner) findViewById(R.id.system_spinner);
		loadSystems();
	}

	private void loadSystems() {
		final String[] SYSTEM_PROJECTION = new String[] {
				Systems._ID, Systems.Name
		};
		Cursor cur = managedQuery(Systems.CONTENT_URI, SYSTEM_PROJECTION,
				null, null, null);
		SimpleCursorAdapter adapter2 = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item,
				cur,
				new String[] {Systems.Name},
				new int[] {android.R.id.text1});
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		systemSpin.setAdapter(adapter2);

		systemSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				Cursor cur = (Cursor)parent.getItemAtPosition(pos);

				system = cur.getString(cur.getColumnIndex(Systems.Name));
				loadRoutes();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}

	public void loadRoutes() {
		new AsyncTask<Void, Void, Cursor>() {
			@Override
			protected void onPreExecute() {
				setProgressBarIndeterminateVisibility(true);
			}
			@Override
			protected Cursor doInBackground(Void... params) {
				final String[] PROJECTION = new String[] {
						Routes._ID, Routes.RouteNumber, Routes.RouteName
				};
				return managedQuery(Routes.CONTENT_URI, PROJECTION,
						Routes.System+"=?", new String[] {system} , null);
			}
			@Override
			protected void onPostExecute(Cursor result) {
				setProgressBarIndeterminateVisibility(false);
				if(rva != null) {
					rva.changeCursor(result);
				} else {
					rva = new SimpleCursorAdapter(SelectRoute.this,
							R.layout.route_list_item,
							result,
							new String[] { Routes.RouteNumber, Routes.RouteName },
							new int[] { android.R.id.text1, android.R.id.text2 });

					setListAdapter(rva);
				}
			}
		}.execute();
	}

	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if(position >= 0) {
			final Cursor c = (Cursor) parent.getItemAtPosition(position);
			final String selectedRoute = c.getString(c.getColumnIndexOrThrow(Routes.RouteNumber));
			final Intent dirintent = new Intent();
			dirintent.putExtra(SelectDirection.EXTRA_RT, selectedRoute);
			dirintent.putExtra(SelectDirection.EXTRA_SYS, system);
			setResult(RESULT_OK,dirintent);
			finish();
		}
	}
}
