/**
 * 
 */
package com.cmmobi.icuiniao.ui.adapter;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cmmobi.icuiniao.onlineEngine.view.View_AddFriendsList_item;

/**
 * @author hw
 *
 */
public class AddFriendsAdapter extends BaseAdapter{

	private ArrayList<View_AddFriendsList_item> items;
	
	public AddFriendsAdapter(ArrayList<View_AddFriendsList_item> items){
		this.items = items;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
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
		return items.get(position);
	}

}
