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

/**
 * PredictionView is a view for using in a ListView to visualize the results of
 * a query for a bus stop prediction.
 * 
 * @see Predictions
 * 
 * @author Joseph Booker
 * 
 */
public class PredictionView extends LinearLayout {
	/**
	 * Cursor at the row containing the information we are displaying.
	 */
	private Cursor pred;
	/**
	 * If true, we show more information (specifically, the router number)
	 */
	private boolean stopinfo;

	private TextView routeView;

	private TextView timeView;

	/**
	 * SimpleDateFormat for displaying the time.
	 * @see #prettyTime(long)
	 */
	private static final SimpleDateFormat formatter = new SimpleDateFormat(
			"hh:mm a");

	public PredictionView(Context context, Cursor pred) {
		super(context);
		init(context);
		this.pred = pred;
		resetLabels();
	}

	public PredictionView(Context context, AttributeSet a) {
		super(context, a);
		init(context);
	}

	/**
	 * Helper method called by constructors to make sure the local fields have
	 * the correct references.
	 * 
	 * @param context
	 */
	private void init(Context context) {
		LayoutInflater l = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rellayout = l.inflate(R.layout.predictionview, this);

		timeView = (TextView) rellayout
				.findViewById(R.id.predictionview_timeview);
		routeView = (TextView) rellayout
				.findViewById(R.id.predictionview_routeview);
	}

	/**
	 * Reloads the data from this cursor and sets the views accordingly
	 */
	public void resetLabels() {
		long estTime = pred.getLong(pred
				.getColumnIndexOrThrow(Predictions.EstimatedTime));
		int predType = pred
				.getInt(pred.getColumnIndexOrThrow(Predictions.Type));
		String route = pred.getString(pred
				.getColumnIndexOrThrow(Predictions.RouteNumber));
		String dest = pred.getString(pred
				.getColumnIndexOrThrow(Predictions.Destination));
		if (Math.abs(System.currentTimeMillis() - estTime) < 60 * 1000) {
			if (predType == PredictionBuilder.ARRIVAL_PREDICTION)
				timeView.setText(prettyTime(estTime) + " - Arriving now!");
			else
				// departure
				timeView.setText(prettyTime(estTime) + " - Departing now!");
		} else { // more then a minute left
			timeView.setText(prettyTime(estTime)
					+ " - "
					+ DateUtils.getRelativeTimeSpanString(estTime,
							System.currentTimeMillis(),
							DateUtils.MINUTE_IN_MILLIS, 0));
		}

		if (stopinfo)
			routeView.setText(route + " - To " + dest);
		else
			routeView.setText("To " + dest);
	}

	/**
	 * Changes the cursor used for this view.
	 * @param pred the row to get the prediction information from
	 * @param showRoute determines if the route is displayed
	 */
	public void setPrediction(Cursor pred, boolean showRoute) {
		this.pred = pred;
		this.stopinfo = showRoute;
		resetLabels();
	}

	/**
	 * Formats a time stamp as a localized string
	 * @param time the number milliseconds since January 1, 1970.
	 * @return a representation of the time as "hh:mm a"
	 */
	public static String prettyTime(long time) {
		return formatter.format(time);
	}

}
