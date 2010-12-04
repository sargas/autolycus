/**
 * 
 */
package net.neoturbine.autolycus.prefs;

import net.neoturbine.autolycus.R;
import net.neoturbine.autolycus.providers.Systems;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

/**
 * @author Joseph Booker
 *
 */
public class Prefs extends PreferenceActivity {
	public final static int DEFAULT_UPDATE_DELAY = 60;
	private ListPreference defaultsys;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.prefs);
		
		defaultsys = (ListPreference) findPreference("default_system");
		
		loadSystems();
	}
	
	private void loadSystems() {
		final String[] SYSTEM_PROJECTION = new String[] {
				Systems._ID, Systems.Name
		};
		Cursor cur = managedQuery(Systems.CONTENT_URI, SYSTEM_PROJECTION,
				null, null, null);
		final int count = cur.getCount();
		String[] systems = new String[count];
		cur.moveToFirst();
		for(int i=0;i<count;++i) {
			systems[i] = cur.getString(cur.getColumnIndexOrThrow(Systems.Name));
			cur.moveToNext();
		}
		
		defaultsys.setEntries(systems);
		defaultsys.setEntryValues(systems);
		
		cur.close();
	}
}
