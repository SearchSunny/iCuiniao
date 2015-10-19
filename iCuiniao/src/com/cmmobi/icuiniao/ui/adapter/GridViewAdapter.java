/**
 * 
 */
package com.cmmobi.icuiniao.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cmmobi.icuiniao.R;

/**
 * @author hw
 *
 */
public class GridViewAdapter extends BaseAdapter{

	private Bitmap[] lstImages = null;
	private LayoutInflater mInflater;
	
	public GridViewAdapter(Bitmap[] lstImages, Context context){
		this.lstImages = lstImages;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(lstImages == null)return 0;
		return lstImages.length;
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
		GridViewHolder gridViewHolder = null;
		if(convertView == null){
			gridViewHolder = new GridViewHolder();
			convertView = mInflater.inflate(R.layout.poplike_griditem, null);
			gridViewHolder.img = (ImageView)convertView.findViewById(R.id.poplike_gridImage);
			if(lstImages[arg0] != null){
				gridViewHolder.img.setImageBitmap(lstImages[arg0]);
			}
			convertView.setTag(gridViewHolder);
			notifyDataSetChanged();
		}else{
			gridViewHolder = (GridViewHolder)convertView.getTag();
		}
		return convertView;
	}
	
	public final class GridViewHolder{
		public ImageView img = null;
	}

}
