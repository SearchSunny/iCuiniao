/**
 * 
 */
package com.cmmobi.icuiniao.ui.adapter;

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
public class SajiaoAdapter extends BaseAdapter{

	private String[] items;
	private boolean[] state;
	private LayoutInflater mInflater;
	
	public SajiaoAdapter(String[] items , boolean[] state, Context context){
		this.items = items;
		this.state = state;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.length;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder holder = null;
		if(convertView == null){
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.sajiao_item, null);
			holder.textView = (TextView)convertView.findViewById(R.id.sajiao_item);
			convertView.setTag(holder);
		}else{
			holder = (Holder)convertView.getTag();
		}
		
		holder.textView.setText(items[position]);
		if(state[position]){
			holder.textView.setBackgroundResource(R.drawable.sajiaokuang1);
		}else{
			holder.textView.setBackgroundResource(R.drawable.sajiaokuang2);
		}
		
		return convertView;
	}
	
	public final class Holder{
		public TextView textView;
	}

}
