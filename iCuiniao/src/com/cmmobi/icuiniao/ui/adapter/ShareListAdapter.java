/**
 * 
 */
package com.cmmobi.icuiniao.ui.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;

/**
 * @author hw
 *
 */
public class ShareListAdapter extends BaseAdapter{

	public ArrayList<HashMap<String, Object>> appInfos;
	private LayoutInflater mInflater;
	
	public ShareListAdapter(ArrayList<HashMap<String, Object>> appInfos,Context context){
		this.appInfos = appInfos;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return appInfos.size();
	}

	@Override
	public Object getItem(int arg0) {
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
		ListViewHolder holder = null;
		if(convertView == null){
			holder = new ListViewHolder();
			convertView = mInflater.inflate(R.layout.share_item, null);
			holder.img = (ImageView)convertView.findViewById(R.id.shareItem_icon);
			holder.text = (TextView)convertView.findViewById(R.id.shareItem_text);
			convertView.setTag(holder);
		}else{
			holder = (ListViewHolder)convertView.getTag();
		}
		holder.img.setBackgroundResource((Integer)appInfos.get(position).get("itemImages"));
		holder.text.setText((String)appInfos.get(position).get("itemStrings"));
		return convertView;
	}
	
	public final class ListViewHolder{
		public ImageView img;
		public TextView text;
	}

}
