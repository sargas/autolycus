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

import net.neoturbine.autolycus.providers.Routes;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.TextView;

/**
 * This activity creates shortcuts on the home screen, using other activities to
 * find out what shortcut to create.
 * 
 * @author Joseph Booker
 * 
 */
public class BusShortcuts extends Activity {
	/**
	 * Request Code for passing to startActivityForResult.
	 */
	private static final int PICK_STOP = 1;

	private String system;
	private String route;
	private String direction;
	private String stopname;
	private int stopid;

	private int selectedIcon = R.drawable.ic_launcher_black;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = getIntent();
		final String action = intent.getAction();

		if (Intent.ACTION_CREATE_SHORTCUT.equals(action)) {
			Intent stopintent = new Intent(Intent.ACTION_PICK,
					Routes.CONTENT_URI);
			startActivityForResult(stopintent, PICK_STOP);
		} else {
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_STOP) {
			if (resultCode == RESULT_OK) {
				setContentView(R.layout.shortcut);
				system = data.getStringExtra(SelectStop.EXTRA_SYSTEM);
				route = data.getStringExtra(SelectStop.EXTRA_ROUTE);
				direction = data.getStringExtra(SelectStop.EXTRA_DIRECTION);
				stopid = data.getIntExtra(SelectStop.EXTRA_STOPID, -1);
				stopname = data.getStringExtra(SelectStop.EXTRA_STOPNAME);

				((TextView) findViewById(R.id.shortcut_sys)).setText(system);
				((TextView) findViewById(R.id.shortcut_route)).setText("Route "
						+ route);
				((TextView) findViewById(R.id.shortcut_direction))
						.setText(direction);
				((TextView) findViewById(R.id.shortcut_stpnm))
						.setText(stopname);
				((EditText) findViewById(R.id.shortcut_stopname))
						.setText(stopname);

				final Gallery g = (Gallery) findViewById(R.id.shortcut_icon);
				g.setAdapter(new IconAdapter(this));
				g.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						selectedIcon = ((Integer) g.getItemAtPosition(position))
								.intValue();
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

				((Button) findViewById(R.id.shortcut_submit))
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								returnShortcut();
							}
						});
			}
		}
	}

	/**
	 * Creates a new shortcut on the home screen using the fields of this
	 * activity and the value of the text box containing the name of the
	 * shortcut.
	 * 
	 * @see net.neoturbine.StopPrediction#OPEN_STOP_ACTION
	 */
	public void returnShortcut() {
		Intent shortcutIntent = new Intent();
		shortcutIntent.setAction(StopPrediction.OPEN_STOP_ACTION);
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shortcutIntent.putExtra(SelectStop.EXTRA_SYSTEM, system)
				.putExtra(SelectStop.EXTRA_ROUTE, route)
				.putExtra(SelectStop.EXTRA_DIRECTION, direction)
				.putExtra(SelectStop.EXTRA_STOPNAME, stopname)
				.putExtra(SelectStop.EXTRA_STOPID, stopid);

		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				((TextView) findViewById(R.id.shortcut_stopname)).getText()
						.toString());
		Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this,
				selectedIcon);
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);

		setResult(RESULT_OK, intent);
		finish();
	}

}
