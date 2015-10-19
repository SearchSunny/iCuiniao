/**
 * 
 */
package com.cmmobi.icuiniao.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;

/**
 * @author hw
 *
 */
public class CuiniaoListAdapter extends BaseAdapter{

	private ArrayList<String> items;
	private LayoutInflater mInflater;
	
	public CuiniaoListAdapter(ArrayList<String> items,Context context){
		this.items = items;
		mInflater = LayoutInflater.from(context);
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
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ListViewHolder holder = null;
		if(convertView == null){
			holder = new ListViewHolder();
			convertView = mInflater.inflate(R.layout.cuiniao_listitem, null);
			holder.text = (TextView)convertView.findViewById(R.id.cuiniaoListItem_text);
			convertView.setTag(holder);
		}else{
			holder = (ListViewHolder)convertView.getTag();
		}
		holder.text.setText(items.get(position));
		return convertView;
	}
	
	public final class ListViewHolder{
		public TextView text;
	}

}
