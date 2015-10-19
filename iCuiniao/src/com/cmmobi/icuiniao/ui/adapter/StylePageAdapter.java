/**
 * 
 */
package com.cmmobi.icuiniao.ui.adapter;

import com.cmmobi.icuiniao.ui.view.StyleItem;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author hw
 *
 */
public class StylePageAdapter extends BaseAdapter{

	private StyleItem[] styleItems;
	
	public StylePageAdapter(StyleItem[] styleItems){
		this.styleItems = styleItems;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return styleItems.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return styleItems[position];
	}

}
