/**
 * 
 */
package net.neoturbine.autolycus;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author Joseph Booker
 *
 */
public class BusShortcuts extends ListActivity {
	public static final int ROUTE_REQUEST_CODE = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        final Intent intent = getIntent();
        final String action = intent.getAction();
		
		if (Intent.ACTION_CREATE_SHORTCUT.equals(action)) {
			Intent stopintent = new Intent();
        	stopintent.setAction(SelectRoute.SELECT_ROUTE);
        	startActivityForResult(stopintent,ROUTE_REQUEST_CODE);
		}
	}
	
}
