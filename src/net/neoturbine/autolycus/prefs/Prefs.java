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
package net.neoturbine.autolycus.prefs;

import net.neoturbine.autolycus.R;
import net.neoturbine.autolycus.providers.Systems;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

/**
 * @author Joseph Booker
 * 
 */
public class Prefs extends PreferenceActivity {
	public final static int DEFAULT_UPDATE_DELAY = 90;
	public final static int MIN_UPDATE_DELAY = 60;
	private ListPreference defaultsys;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.prefs);

		defaultsys = (ListPreference) findPreference("default_system");

		loadSystems();

		findPreference("update_delay").setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange (Preference preference, Object newValue) {
				if(preference.getKey().equals("update_delay")) {
					String newvalue = newValue.toString();
					if(Integer.parseInt(newvalue) < MIN_UPDATE_DELAY) {
						AlertDialog.Builder builder = new AlertDialog.Builder(Prefs.this);
						builder.setMessage(R.string.prefs_delay_error);
						builder.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});
						AlertDialog alert = builder.create();
						alert.show();
						return false;
					}
				}
				return true;
			}
		});
	}

	private void loadSystems() {
		final String[] SYSTEM_PROJECTION = new String[] { Systems._ID,
				Systems.Name };
		Cursor cur = managedQuery(Systems.CONTENT_URI, SYSTEM_PROJECTION, null,
				null, null);
		final int count = cur.getCount();
		String[] systems = new String[count];
		cur.moveToFirst();
		for (int i = 0; i < count; ++i) {
			systems[i] = cur.getString(cur.getColumnIndexOrThrow(Systems.Name));
			cur.moveToNext();
		}

		defaultsys.setEntries(systems);
		defaultsys.setEntryValues(systems);

		cur.close();
	}
}
