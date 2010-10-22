/**
 * 
 */
package net.neoturbine.autolycus;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import net.neoturbine.autolycus.data.Route;
import net.neoturbine.autolycus.data.RouteBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
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

	//important: run this in its own thread!!!
	public static XmlPullParser loadData(Context context, String verb, String system, Bundle params) throws Exception {
		try {
			//TODO: check if online (or airplane mode, or something)
			ArrayList<NameValuePair> qparams = new ArrayList<NameValuePair>();
			if(params != null) {
				for(String name : params.keySet()) {
					qparams.add(new BasicNameValuePair(name,params.getString(name)));
				}
			}

			String server = "";
			String key = "";
			if(system.equals("Chicago Transit Authority")) {
				server = "www.ctabustracker.com";
				key = "HeDbySM4CUDgRDsrGnRGZmD6K";
			} else if(system.equals("Ohio State University TRIP")) {
				server = "trip.osu.edu";
				key = "auixft7SWR3pWAcgkQfnfJpXt";
			}
			
			qparams.add(new BasicNameValuePair("key",key));

			//assemble the url
			URI uri = URIUtils.createURI("http", //Protocol
					server, //server
					80, //specified in API documentation
					"/bustime/api/v1/"+verb, //path
					URLEncodedUtils.format(qparams, "UTF-8"), null);

			//assemble our request
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(uri);
			Log.i(TAG,"Retrieving "+httpget.getURI().toString());
			//from localytics recordEvent(context,system,verb,false);

			//ah, the blocking
			HttpResponse response = httpClient.execute(httpget);

			if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new Exception(response.getStatusLine().toString());
			}
			InputStream content = response.getEntity().getContent();
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(content, null);
			return xpp;
		} catch (Exception e) {
			Log.e(TAG, e.toString()); //TODO: should more be done?
			throw e;
		} finally {}
	}

	public static ArrayList<Route> getRoutes(Context context,String system) throws Exception {
		ArrayList<Route> routes = new ArrayList<Route>();
		try {
			XmlPullParser xpp = BusTimeAPI.loadData(context,"getroutes", system, null);
			int eventType = xpp.getEventType();
			String curTag = "";
			RouteBuilder currentRoute = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch(eventType) {
				case XmlPullParser.START_TAG:
					curTag = xpp.getName();
					if(curTag.equals("route")) { //on to new route
						if(currentRoute != null) routes.add(currentRoute.toRoute());
						currentRoute = new RouteBuilder();
						currentRoute.setSystem(system);
					}
					break;
				case XmlPullParser.TEXT:
					String text = xpp.getText().trim();
					if(!curTag.equals("") && !text.equals("")) {
						if(curTag.equals("error")) {
							//eck, we got a problem
							throw new Exception(text);
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
			//add last route
			if(currentRoute != null) routes.add(currentRoute.toRoute());
		} catch (Exception ex) {
			Log.e(TAG, ex.toString()); //automatic logging
			throw ex;
		}
		
		return routes;
	}

}
