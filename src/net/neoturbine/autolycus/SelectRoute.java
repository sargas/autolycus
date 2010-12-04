package net.neoturbine.autolycus;

import net.neoturbine.autolycus.providers.AutolycusProvider;
import net.neoturbine.autolycus.providers.Directions;
import net.neoturbine.autolycus.providers.Routes;
import net.neoturbine.autolycus.providers.Systems;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class SelectRoute extends ListActivity implements OnItemClickListener {
	private Spinner systemSpin;
	private String system;
	private SimpleCursorAdapter rva;
	private TextView errorText;
	
	private static final int GET_DIRECTION = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(true);

		setContentView(R.layout.routepicker);
		
		getListView().setOnItemClickListener(this);
		systemSpin = (Spinner) findViewById(R.id.system_spinner);
		errorText = (TextView) findViewById(R.id.txt_route_error);
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
				if (result.getExtras().containsKey(AutolycusProvider.ERROR_MSG)) {
					setListAdapter(null);
					errorText.setVisibility(View.VISIBLE);
					errorText.setText(result
						.getExtras().getString(AutolycusProvider.ERROR_MSG));
					return;
				}
				//errorText.setVisibility(View.GONE);
				if(rva != null) {
					rva.changeCursor(result);
				} else {
					rva = new SimpleCursorAdapter(SelectRoute.this,
							R.layout.route_list_item,
							result,
							new String[] { Routes.RouteNumber, Routes.RouteName },
							new int[] { android.R.id.text1, android.R.id.text2 });
				}
				setListAdapter(rva);
			}
		}.execute();
	}

	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if(position >= 0) {
			final Cursor c = (Cursor) parent.getItemAtPosition(position);
			final String selectedRoute = c.getString(c.getColumnIndexOrThrow(Routes.RouteNumber));
			final Intent dirintent = new Intent(Intent.ACTION_PICK,Directions.CONTENT_URI);
			dirintent.putExtra(SelectDirection.EXTRA_RT, selectedRoute);
			dirintent.putExtra(SelectDirection.EXTRA_SYS, system);
			startActivityForResult(dirintent, GET_DIRECTION);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode,
            Intent data) {
		if(requestCode == GET_DIRECTION) {
			if(resultCode == RESULT_OK) {
				setResult(RESULT_OK,data);
				finish();
			}
		}
	}
}
