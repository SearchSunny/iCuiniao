package com.cmmobi.icuiniao.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;


public class ShareInFriendAdapter extends BaseAdapter{

	
	private LayoutInflater mInflater;
	
	public int[] itemImages = {R.drawable.share1,R.drawable.wxfreind2,R.drawable.share3,R.drawable.share2,R.drawable.share4};
	private String[] itemStrings;
	
	public ShareInFriendAdapter(Context context, String[] itemStrings){		
		mInflater = LayoutInflater.from(context);
		this.itemStrings = itemStrings;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return itemStrings.length;
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
			convertView = mInflater.inflate(R.layout.listitem_share, null);
			holder.img = (ImageView)convertView.findViewById(R.id.imgShareIcon);
			holder.text = (TextView)convertView.findViewById(R.id.shareName);
			convertView.setTag(holder);
		}else{
			holder = (ListViewHolder)convertView.getTag();
		}
		holder.img.setBackgroundResource(itemImages[position]);
		holder.text.setText(itemStrings[position]);
		return convertView;
	}
	
	public final class ListViewHolder{
		public ImageView img;
		public TextView text;
	}

}
