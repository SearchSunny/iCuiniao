/**
 * 
 */
package com.cmmobi.icuiniao.ui.adapter;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cmmobi.icuiniao.onlineEngine.view.View_CommentU_Item;

/**
 * @author lenovo
 *
 */
public class Comment_U_Adapter extends BaseAdapter{

	ArrayList<View_CommentU_Item> items;
	
	public Comment_U_Adapter(ArrayList<View_CommentU_Item> items){
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
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return items.get(arg0);
	}

}
