package net.neoturbine.autolycus;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class SelectDirection extends ListActivity {
	public static final String SELECT_DIRECTION = "net.neoturbine.autolycus.SELECT_DIRECTION";
	public static final String EXTRA_RT = "net.neoturbine.autolycus.SelectDirection.ROUTE";
	public static final String EXTRA_SYS = "net.neoturbine.autolycus.SelectDirection.SYSTEM";
	public static final String TAG = "Autolycus";
	
	public static final int STOP_REQUEST_CODE = 0;
	
	//private ArrayList<String> directions;
	//private Route route;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    //final Intent intent = getIntent();
        
		//show download progress
		/*requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(true); //do this while loading ui too
        
	    route = new Route(intent.getBundleExtra(EXTRA_KEY));
	    
		//what we want in this thread when we get the data
		final Handler uiThreadCallback = new Handler();
		final Context context = this; //need this to refer to in inner class
		final Runnable runInUIThread = new Runnable() {
			public void run () {
				setProgressBarIndeterminateVisibility(false);
				
				if(tempEx != null)
					Toast.makeText(context, tempEx.toString(), Toast.LENGTH_LONG).show();
				else {
					if(directions.size() == 1) {
						getStop(directions.get(0));
					}
					//load this adapter even if only one direction,
					//makes it work more with back button
					setListAdapter(new ArrayAdapter<String>(
							context,android.R.layout.simple_list_item_1,directions));
				}
			}
		};
		
		//give some notice to the user		
		setProgressBarIndeterminateVisibility(true);
		new Thread() {
			@Override public void run() {
				try {
					directions = BusTimeAPI.getDirections(context, route.getSystem(), route.getRt());
					tempEx = null;
				} catch (Exception ex) {
					tempEx = ex;
				}
				uiThreadCallback.post(runInUIThread);
			}
		}.start();*/
    }

    protected void onListItemClick(ListView I, View v, int position, long id) {
    	getStop(((TextView)v).getText().toString());
    }
    
    protected void getStop(String direction) {
    	Intent getStop = new Intent();
    	
    	getStop.setAction(SelectStop.SELECT_STOP);
    	getStop.putExtra(SelectStop.EXTRA_KEY_DIRECTION, direction);
    	//getStop.putExtra(SelectStop.EXTRA_KEY_ROUTE, route.toBundle());
    	
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
