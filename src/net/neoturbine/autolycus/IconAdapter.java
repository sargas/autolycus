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

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * @author Joseph Booker
 *
 */
public class IconAdapter extends BaseAdapter {
	private final Context context;
	private final static Integer[] icons = {
			R.drawable.ic_launcher_black, R.drawable.ic_launcher_blue,
			R.drawable.ic_launcher_green, R.drawable.ic_launcher_orange,
			R.drawable.ic_launcher_red, R.drawable.ic_launcher_yellow };
	private int iconBackground;
	
	public IconAdapter(Context c) {
		context = c;
		TypedArray a = c.obtainStyledAttributes(R.styleable.GalleryTheme);
		iconBackground = a.getResourceId(R.styleable.GalleryTheme_android_galleryItemBackground, 0);
		a.recycle();
	}
	
	@Override
	public int getCount() {
		return icons.length;
	}

	@Override
	public Object getItem(int position) {
		return icons[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView i = new ImageView(context);
		
		i.setImageResource(icons[position]);
		i.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.WRAP_CONTENT,Gallery.LayoutParams.WRAP_CONTENT));
		i.setScaleType(ImageView.ScaleType.FIT_XY);
		i.setBackgroundResource(iconBackground);
		return i;
	}

}
