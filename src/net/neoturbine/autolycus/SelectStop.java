package net.neoturbine.autolycus;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class SelectStop extends ListActivity {
	public static final String SELECT_STOP = "net.neoturbine.autolycus.SELECT_STOP";
	public static final String EXTRA_SYSTEM = "net.neoturbine.autolycus.SelectStop.SYSTEM";
	public static final String EXTRA_ROUTE = "net.neoturbine.autolycus.SelectStop.ROUTE";
	public static final String EXTRA_DIRECTION = "net.neoturbine.autolycus.SelectStop.DIRECTION";
	
	public static final String TAG = "Autolycus";
	
	protected String direction;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*final Intent intent = getIntent();

		//show download progress
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(true); //do this while loading ui too

		route = new Route(intent.getBundleExtra(EXTRA_KEY_ROUTE));
		direction = intent.getStringExtra(EXTRA_KEY_DIRECTION);

		//what we want in this thread when we get the data
		final Handler uiThreadCallback = new Handler();
		final Context context = this; //need this to refer to in inner class
		final Runnable runInUIThread = new Runnable() {
			public void run () {
				if(tempEx!=null) //handle error
					Toast.makeText(context, tempEx.toString(), Toast.LENGTH_LONG).show();
				else
					setListAdapter(new StopListAdapter(context,stops));
				//by now, we have routes arraylist filled
				setProgressBarIndeterminateVisibility(false);
				
			}
		};
		
		//give some notice to the user		
		setProgressBarIndeterminateVisibility(true);
		new Thread() {
			@Override public void run() {
					try {
						stops = BusTimeAPI.getStops(context, route.getSystem(), route.getRt(),direction);
						tempEx = null;
					} catch (Exception ex) {
						tempEx = ex; //let ui thread deal with it
					}
				uiThreadCallback.post(runInUIThread);
			}
		}.start();*/
	}
	
	protected void  onListItemClick(ListView l, View v, int position, long id) {
		Intent result = new Intent();
		result.setAction(Intent.ACTION_DEFAULT);
		//result.putExtra(RESULT_STOP_KEY, ((StopInfo)l.getItemAtPosition(position)).toBundle());
		setResult(RESULT_OK,result);
		finish();
	}
	
	/*private class StopListAdapter extends BaseAdapter {
		private Context context;
		private ArrayList<StopInfo> stops;
		public StopListAdapter(Context context,ArrayList<StopInfo> stops) {
			this.context = context;
			this.stops = stops;
		}
		public int getCount() {
			return stops.size();
		}
		public Object getItem(int position) {
			return stops.get(position);
		}
		public long getItemId(int position) {
			return stops.get(position).getStopID(); //true id :)
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			StopView sv;
			if(convertView == null) {
				sv = new StopView(context,stops.get(position));
			} else {
				sv = (StopView) convertView;
				sv.setStop(stops.get(position));
			}
			return sv;
		}
	}*/
}
