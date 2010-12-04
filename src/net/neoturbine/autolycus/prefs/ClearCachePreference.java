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
package net.neoturbine.autolycus.prefs;

import net.neoturbine.autolycus.R;
import net.neoturbine.autolycus.providers.AutolycusProvider;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ClearCachePreference extends Preference {
	public ClearCachePreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public ClearCachePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ClearCachePreference(Context context) {
		super(context);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		final Button b = new Button(getContext());
		b.setText(R.string.pref_clear);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getContext());
				if (getContext().getDatabasePath(
						AutolycusProvider.DATABASE_NAME).exists()) {
					if (getContext().deleteDatabase(
							AutolycusProvider.DATABASE_NAME))
						builder.setMessage(R.string.pref_clear_suc);
					else
						builder.setMessage(R.string.pref_clear_fail);
				} else
					builder.setMessage(R.string.pref_clear_gone);

				builder.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

				AlertDialog alert = builder.create();
				alert.show();
			}
		});
		return b;
	}

}