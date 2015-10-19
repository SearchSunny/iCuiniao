/**
 * 
 */
package com.cmmobi.icuiniao.ui.adapter;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author hw
 *
 */
public class ListAdapter extends BaseAdapter{

	private ArrayList<? extends View>lstViews;
	
	public ListAdapter(ArrayList<? extends View>lstViews){
		this.lstViews = lstViews;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lstViews.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if(getCount() == 0)return null;
		return lstViews.get(arg0);
	}
	
}
