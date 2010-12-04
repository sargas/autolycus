/**
 * 
 */
package net.neoturbine.autolycus;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

/**
 * @author Joseph Booker
 * 
 */
public class AboutScreen extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.about);
		setTitle(getString(R.string.about_title));

		TextView copyrights = (TextView) findViewById(R.id.about_copyrights);
		Linkify.addLinks(copyrights, Linkify.WEB_URLS);
		copyrights.setMovementMethod(LinkMovementMethod.getInstance());

		TextView appname = (TextView) findViewById(R.id.about_appname);
		PackageInfo info;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			return;
		}
		appname.setText(getString(R.string.app_name) + " " + info.versionName);
	}

}

