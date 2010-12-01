package net.neoturbine.autolycus;

import java.text.SimpleDateFormat;
import net.neoturbine.autolycus.internal.PredictionBuilder;
import net.neoturbine.autolycus.providers.Predictions;
import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PredictionView extends LinearLayout {
	private Cursor pred;
	private boolean stopinfo; //if true, be verbose
	
	private TextView routeView; //for verbosity
	
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
	}
	
	private void init(Context context) {
		LayoutInflater l = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rellayout = l.inflate(R.layout.predictionview, this);
		
		timeView = (TextView)rellayout.findViewById(R.id.predictionview_timeview);
		routeView = (TextView)rellayout.findViewById(R.id.predictionview_routeview);
		predView = (TextView)rellayout.findViewById(R.id.predictionview_predview);
	}
	
	public void resetLabels() {
		long estTime = pred.getLong(pred.getColumnIndexOrThrow(Predictions.EstimatedTime));
		int predType = pred.getInt(pred.getColumnIndexOrThrow(Predictions.Type));
		long predTime = pred.getLong(pred.getColumnIndexOrThrow(Predictions.PredictionTime));
		String route = pred.getString(pred.getColumnIndexOrThrow(Predictions.RouteNumber));
		String dir = pred.getString(pred.getColumnIndexOrThrow(Predictions.Direction));
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
		if(stopinfo)
			routeView.setText(route + " - "+dir);
		else
			routeView.setText("");
	}
	
	public void setPrediction(Cursor pred, boolean showAllInfo) {
		this.pred = pred;
		this.stopinfo = showAllInfo;
		resetLabels();
	}

}
