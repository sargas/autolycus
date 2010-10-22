/**
 * 
 */
package net.neoturbine.autolycus;

import net.neoturbine.autolycus.providers.Routes;
import net.neoturbine.autolycus.providers.Systems;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

/**
 * @author Joseph Booker
 *
 */
public class BusShortcuts extends ListActivity {
	private Spinner systemSpin;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        final Intent intent = getIntent();
        final String action = intent.getAction();
		
		if (Intent.ACTION_CREATE_SHORTCUT.equals(action)) {
			setContentView(R.layout.shortcut);
			systemSpin = (Spinner) findViewById(R.id.system_spinner);
			
			final String[] PROJECTION = new String[] {
		        Systems._ID, Systems.Name
		    };
			Cursor cur = managedQuery(Systems.CONTENT_URI, PROJECTION, null, null, null);
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
					loadRoutes(cur.getString(cur.getColumnIndex(Systems.Name)));
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {}
			});
		}
	}
	
	public void loadRoutes(final String system) {
		new AsyncTask<Void, Void, Cursor>() {
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
				SimpleCursorAdapter adapter = new SimpleCursorAdapter(BusShortcuts.this,
						android.R.layout.simple_list_item_2,
						result,
						new String[] { Routes.RouteNumber, Routes.RouteName },
						new int[] { android.R.id.text1, android.R.id.text2 });
				setListAdapter(adapter);
				return;
			}
		}.execute();
	}
}
