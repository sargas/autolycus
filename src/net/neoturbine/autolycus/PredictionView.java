package net.neoturbine.autolycus;

import java.text.SimpleDateFormat;
import net.neoturbine.autolycus.internal.PredictionBuilder;
import net.neoturbine.autolycus.providers.Predictions;
import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PredictionView extends LinearLayout {
	private static final String TAG = "Autolycus";
	private Cursor pred;
	/*private boolean stopinfo; //if true, be verbose
	
	private TextView nameView; //for verbosity
	private TextView dirView;*/
	
	private TextView timeView;
	private TextView predView;
	
	private static final SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");


	public PredictionView(Context context, Cursor pred) {
		super(context);
		init(context);
		this.pred = pred;
		resetLabels();
	}
	public PredictionView(Context context, AttributeSet a) {
		super(context,a);
		init(context);
		Log.v(TAG,"view - created in shorter constructor");
	}
	
	private void init(Context context) {
		this.setOrientation(VERTICAL);
		
		timeView = new TextView(context);
		timeView.setTextSize(20);
		addView(timeView, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		predView = new TextView(context);
		predView.setGravity(Gravity.RIGHT);
		addView(predView, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}
	
	public void resetLabels() {
		long estTime = pred.getLong(pred.getColumnIndexOrThrow(Predictions.EstimatedTime));
		int predType = pred.getInt(pred.getColumnIndexOrThrow(Predictions.Type));
		long predTime = pred.getLong(pred.getColumnIndexOrThrow(Predictions.PredictionTime));
		if(Math.abs(System.currentTimeMillis() - estTime) < 60*1000) {
			if(predType == PredictionBuilder.ARRIVAL_PREDICTION)
				timeView.setText(formatter.format(estTime) +" - Arriving now!");
			else //departure
				timeView.setText(formatter.format(estTime) +" - Departing now!");
		} else { //more then a minute left		
			timeView.setText(formatter.format(estTime) +" - "+
					DateUtils.getRelativeTimeSpanString(estTime,
							System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, 0));
		}

		predView.setText("Predicted: "+formatter.format(predTime));
	}
	
	public void setPrediction(Cursor pred) {
		this.pred = pred;
		resetLabels();
	}

}
