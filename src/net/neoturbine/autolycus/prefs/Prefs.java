/**
 * 
 */
package net.neoturbine.autolycus.prefs;

import net.neoturbine.autolycus.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author Joseph Booker
 *
 */
public class Prefs extends PreferenceActivity {
	public final static int DEFAULT_UPDATE_DELAY = 60;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.prefs);
	}
}
