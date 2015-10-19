package com.cmmobi.icuiniao.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;

public class UserMenuAdapter  extends BaseAdapter{

	private String[] listStr;
	public void setListStr(String[] listStr) {
		this.listStr = listStr;
	}

	private int choiceIdx;
	public void setChoiceIdx(int choiceIdx) {
		this.choiceIdx = choiceIdx;
	}

	private LayoutInflater mInflater;
	
	public UserMenuAdapter(Context context){
		mInflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {		
		return listStr.length;
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
		Holder holder = null;
		if(convertView == null){
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.popusertitlemenu_item, null);
			holder.content = (TextView)convertView.findViewById(R.id.popusertitlemenu_item);			
			holder.linear = (LinearLayout)convertView.findViewById(R.id.linearUser);			
			convertView.setTag(holder);
		}else{
			holder = (Holder)convertView.getTag();
		}		
		if(choiceIdx == position){
			holder.linear.setBackgroundResource(R.drawable.usermenuchoice);
			holder.content.setTextColor(Color.BLACK);
		}else{
			holder.linear.setBackgroundResource(0);
			holder.content.setTextColor(Color.WHITE);
		}
		holder.content.setText(listStr[position]);
		return convertView;
	}
	
	public final class Holder{
		public TextView content;		
		public LinearLayout linear;		
		
	}

}
