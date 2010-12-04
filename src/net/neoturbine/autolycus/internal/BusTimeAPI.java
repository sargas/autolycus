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
package net.neoturbine.autolycus.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * @author Joseph Booker
 * 
 */
public final class BusTimeAPI {
	private static final String TAG = "Autolycus";

	// important: run this in its own thread!!!
	public static XmlPullParser loadData(Context context, String verb,
			String system, Bundle params) throws ClientProtocolException,
			IOException, XmlPullParserException {
		ArrayList<NameValuePair> qparams = new ArrayList<NameValuePair>();
		if (params != null) {
			for (String name : params.keySet()) {
				qparams.add(new BasicNameValuePair(name, params.getString(name)));
			}
		}

		String server = "";
		String key = "";
		if (system.equals("Chicago Transit Authority")) {
			server = "www.ctabustracker.com";
			key = "HeDbySM4CUDgRDsrGnRGZmD6K";
		} else if (system.equals("Ohio State University TRIP")) {
			server = "trip.osu.edu";
			key = "auixft7SWR3pWAcgkQfnfJpXt";
		} else if (system.equals("MTA New York City Transit")) {
			server = "bustime.mta.info";
			key = "t7YxRNCmvVCfrZzrcMFeYegjp";
		}

		qparams.add(new BasicNameValuePair("key", key));

		// assemble the url
		URI uri;
		try {
			uri = URIUtils.createURI("http", // Protocol
					server, // server
					80, // specified in API documentation
					"/bustime/api/v1/" + verb, // path
					URLEncodedUtils.format(qparams, "UTF-8"), null);
		} catch (URISyntaxException e) {
			// shouldn't happen
			throw new RuntimeException(e);
		}

		// assemble our request
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(uri);
		Log.i(TAG, "Retrieving " + httpget.getURI().toString());
		// from localytics recordEvent(context,system,verb,false);

		// ah, the blocking
		HttpResponse response = httpClient.execute(httpget);

		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			throw new RuntimeException(response.getStatusLine().toString());
		}
		InputStream content = response.getEntity().getContent();
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(content, null);
		return xpp;
	}

	public static ArrayList<Route> getRoutes(Context context, String system)
			throws Exception {
		ArrayList<Route> routes = new ArrayList<Route>();
		XmlPullParser xpp = BusTimeAPI.loadData(context, "getroutes", system,
				null);
		int eventType = xpp.getEventType();
		String curTag = "";
		RouteBuilder currentRoute = null;
		BusTimeError err = null;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				curTag = xpp.getName();
				if (curTag.equals("route")) { // on to new route
					if (currentRoute != null)
						routes.add(currentRoute.toRoute());
					currentRoute = new RouteBuilder();
					currentRoute.setSystem(system);
				} else if (curTag.equals("error"))
					err = new BusTimeError();
				break;
			case XmlPullParser.TEXT:
				String text = xpp.getText().trim();
				if (!curTag.equals("") && !text.equals("")) {
					if (err != null) {
						// eck, we got a problem
						err.setField(curTag, text);
					} else {
						currentRoute.setField(curTag, text);
					}
				}
				break;
			case XmlPullParser.END_TAG:
				curTag = "";
				break;
			}
			eventType = xpp.next();
		}

		if (err != null)
			throw err;

		// add last route
		if (currentRoute != null)
			routes.add(currentRoute.toRoute());

		return routes;
	}

	public static ArrayList<String> getDirections(Context context,
			String system, String route) throws Exception {
		// we don't check the date of the cache since we're going to assume
		// the route directions will never ever ever change
		// Bad assumption? we'll see
		ArrayList<String> directions = new ArrayList<String>();
		Bundle params = new Bundle();
		params.putString("rt", route);
		XmlPullParser xpp = BusTimeAPI.loadData(context, "getdirections",
				system, params);
		int eventType = xpp.getEventType();
		String curTag = "";
		BusTimeError err = null;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				curTag = xpp.getName();
				if (curTag.equals("dir")) { // on to new route
					// nothing yet
				} else if (curTag.equals("error"))
					err = new BusTimeError();
				break;
			case XmlPullParser.TEXT:
				String text = xpp.getText().trim();
				if (!curTag.equals("") && !text.equals("")) {
					if (err != null)
						err.setField(curTag, text);
					else
						directions.add(text);
				}
				break;
			case XmlPullParser.END_TAG:
				curTag = "";
				break;
			}
			eventType = xpp.next();
		}
		if (err != null)
			throw err;
		return directions;
	}

	public static ArrayList<StopInfo> getStops(Context context, String system,
			String route, String direction) throws Exception {
		ArrayList<StopInfo> stops = new ArrayList<StopInfo>();
		Bundle params = new Bundle();
		params.putString("rt", route);
		params.putString("dir", direction);
		XmlPullParser xpp = BusTimeAPI.loadData(context, "getstops", system,
				params);
		int eventType = xpp.getEventType();
		String curTag = "";
		StopInfoBuilder curBuilder = new StopInfoBuilder(system);
		BusTimeError err = null;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				curTag = xpp.getName();
				if (curTag.equals("stop")) { // on to new route
					if (curBuilder.getName() != null) { // finished a route
						stops.add(curBuilder.toStopInfo());
					}
					curBuilder = new StopInfoBuilder(system);
					curBuilder.setRoute(route);
					curBuilder.setDir(direction);
				} else if (curTag.equals("error"))
					err = new BusTimeError();
				break;
			case XmlPullParser.TEXT:
				String text = xpp.getText().trim();
				if (!curTag.equals("") && !text.equals("")) {
					if (err != null) {
						err.setField(curTag, text);
					} else
						curBuilder.setField(curTag, text);
				}
				break;
			case XmlPullParser.END_TAG:
				curTag = "";
				break;
			}
			eventType = xpp.next();
		}

		if (err != null)
			throw err;

		if (curBuilder.getName() != null)
			stops.add(curBuilder.toStopInfo());

		return stops;
	}

	public static ArrayList<Prediction> getPrediction(Context context,
			String system, String stopID, String route) throws Exception {
		ArrayList<Prediction> preds = new ArrayList<Prediction>();
		Bundle params = new Bundle();
		params.putString("stpid", stopID);
		if (route != null)
			params.putString("rt", route);
		PredictionBuilder curBuilder = new PredictionBuilder();
		XmlPullParser xpp = BusTimeAPI.loadData(context, "getpredictions",
				system, params);
		int eventType = xpp.getEventType();
		String curTag = "";
		BusTimeError err = null;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				curTag = xpp.getName();
				if (curTag.equals("prd")) { // on to new prediction
					if (curBuilder.isSet()) {
						preds.add(curBuilder.toPrediction());
					}
					curBuilder = new PredictionBuilder();
				} else if (curTag.equals("error")) {
					err = new BusTimeError();
				}
				break;
			case XmlPullParser.TEXT:
				String text = xpp.getText().trim();
				if (!curTag.equals("") && !text.equals("")) {
					if (err != null)
						err.setField(curTag, text);
					else
						curBuilder.setField(curTag, text);
				}
				break;
			case XmlPullParser.END_TAG:
				curTag = "";
				break;
			}
			eventType = xpp.next(); // moving on....
		}
		if (err != null)
			throw err;

		if (curBuilder.isSet())
			preds.add(curBuilder.toPrediction());
		return preds;
	}

}
