/**
 * 
 */
package com.cmmobi.icuiniao.ui.adapter;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cmmobi.icuiniao.onlineEngine.view.View_Coquetry;
import com.cmmobi.icuiniao.onlineEngine.view.View_CoquetryItem;

/**
 * @author hw
 *
 */
public class CoquetryAdapter extends BaseAdapter{

	ArrayList<View_CoquetryItem> items;
	View_Coquetry view_Coquetry;
	
	public CoquetryAdapter(ArrayList<View_CoquetryItem> items){
		this.items = items;
	}
	//lyb
	public CoquetryAdapter(ArrayList<View_CoquetryItem> items, View_Coquetry view_Coquetry){
		this.items = items;
		this.view_Coquetry = view_Coquetry;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
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
	public View getView(int id, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		return items.get(id);
	}
	


}
