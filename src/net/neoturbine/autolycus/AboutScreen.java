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

import net.neoturbine.autolycus.prefs.Prefs;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Simple screen to show copyright information and some directions.
 * 
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.about, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about_prefs:
			startActivity(new Intent(this, Prefs.class));
			return true;
		}
		return false;
	}

}
