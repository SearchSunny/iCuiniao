/**
 * 
 */
package com.cmmobi.icuiniao.ui.adapter;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cmmobi.icuiniao.onlineEngine.view.View_FriendList_item;

/**
 * @author hw
 *
 */
public class OnlyTextAdapter extends BaseAdapter{

	ArrayList<View_FriendList_item> items;
	
	public OnlyTextAdapter(ArrayList<View_FriendList_item> items){
		this.items = items;
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
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return items.get(arg0);
	}

}
