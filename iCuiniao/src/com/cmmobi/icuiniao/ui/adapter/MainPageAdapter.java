/**
 * 
 */
package com.cmmobi.icuiniao.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cmmobi.icuiniao.ui.view.MainPageView;
import com.cmmobi.icuiniao.util.UserUtil;

/**
 * @author hw
 *
 */
public class MainPageAdapter extends BaseAdapter{

	private Context context;
	private MainPageView[] mainPageViews;
	private boolean isShowPrice;
	private boolean isShowFeature;
	public MainPageAdapter(Context context,MainPageView[] mainPageViews,boolean isShowPrice,boolean isShowFeature){
		this.context = context;
		this.mainPageViews = mainPageViews;
		this.isShowPrice = isShowPrice;
		this.isShowFeature = isShowFeature;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 6;
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
		if(isShowPrice != UserUtil.isShowPrice || isShowFeature != UserUtil.isShowFeature){
			if(mainPageViews != null){
				for(int i = 0;i < mainPageViews.length;i ++){
					mainPageViews[i] = null;
				}
			}
			isShowPrice = UserUtil.isShowPrice;
			isShowFeature = UserUtil.isShowFeature;
		}
		if(mainPageViews[position] == null){
			mainPageViews[position] = new MainPageView(context,position);
		}
		
		return mainPageViews[position];
	}
	
}
