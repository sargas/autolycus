/**
 * 
 */
package net.neoturbine.autolycus;

import net.neoturbine.autolycus.providers.Systems;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

/**
 * @author Joseph Booker
 *
 */
public class BusShortcuts extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        final Intent intent = getIntent();
        final String action = intent.getAction();
		
		if (Intent.ACTION_CREATE_SHORTCUT.equals(action)) {
			setContentView(R.layout.shortcut);
			Spinner systemSpin = (Spinner) findViewById(R.id.system_spinner);
			
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
		}
	}
}
