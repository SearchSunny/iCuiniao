/**
 * 
 */
package com.cmmobi.icuiniao.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cmmobi.icuiniao.ui.view.MyPageSoftReference;
import com.cmmobi.icuiniao.ui.view.MyPageView;

/**
 * @author hw
 *
 */
public class MyPageAdapter extends BaseAdapter{

	private Context context;
	private int max;
	private MyPageSoftReference reference;
	
	public MyPageAdapter(Context context,int max,MyPageSoftReference reference){
		this.context = context;
		this.max = max;
		this.reference = reference;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return max;
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
		if(reference.get(""+position) == null){
			reference.add(new MyPageView(context, position), ""+position);
		}
		return reference.get(""+position);
	}

}
