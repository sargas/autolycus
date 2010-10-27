package net.neoturbine.autolycus;

import net.neoturbine.autolycus.providers.Directions;
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
	
	public static final int STOP_REQUEST_CODE = 0;
	
	//private ArrayList<String> directions;
	//private Route route;
	private String rt;
	private String system;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    final Intent intent = getIntent();
        
	    rt = intent.getStringExtra(EXTRA_RT);
	    system = intent.getStringExtra(EXTRA_SYS);
	    
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
			getStop(dir);
		}
    }    
    
    
    protected void getStop(String direction) {
    	Intent getStop = new Intent();
    	
    	getStop.setAction(SelectStop.SELECT_STOP);
    	getStop.putExtra(SelectStop.EXTRA_SYSTEM, system);
    	getStop.putExtra(SelectStop.EXTRA_ROUTE, rt);
    	getStop.putExtra(SelectStop.EXTRA_DIRECTION, direction);
    	
    	startActivityForResult(getStop,STOP_REQUEST_CODE);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == STOP_REQUEST_CODE) {
    		if(resultCode == RESULT_OK) {
    			setResult(RESULT_OK,data); //moving along
    			finish();
    		}
    	}
    }
}
