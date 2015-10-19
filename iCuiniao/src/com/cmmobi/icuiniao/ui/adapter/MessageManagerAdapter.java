/**
 * 
 */
package com.cmmobi.icuiniao.ui.adapter;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cmmobi.icuiniao.ui.view.MessageManagerListItem;

/**
 * @author hw
 *
 */
public class MessageManagerAdapter extends BaseAdapter{

	private ArrayList<MessageManagerListItem> item;
	
	public MessageManagerAdapter(ArrayList<MessageManagerListItem> item){
		this.item = item;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return item.size();
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
		
		return item.get(position);
	}
	
}
