/**
 * 
 */
package com.cmmobi.icuiniao.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.Activity;
import com.cmmobi.icuiniao.Activity.WelcomeActivity;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.OfflineLog;

/**
 * @author hw
 *新的开机帮助适配器
 */
public class HelpAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	private Context context;
	private boolean isInSetting;
	
	public HelpAdapter(Context context,boolean isInSetting){
		this.context = context;
		this.isInSetting = isInSetting;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
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
		HelpHolder holder = null;
		if(convertView == null){
			holder = new HelpHolder();
			switch (position) {
			case 0:
				convertView = mInflater.inflate(R.layout.help1a, null);
				holder.layout = (LinearLayout)convertView.findViewById(R.id.help1a);
				convertView.setTag(holder);
				break;
			case 1:
				convertView = mInflater.inflate(R.layout.help2a, null);
				holder.layout = (LinearLayout)convertView.findViewById(R.id.help2a);
				convertView.setTag(holder);
				break;
			case 2:
				convertView = mInflater.inflate(R.layout.help3a, null);
				holder.layout = (LinearLayout)convertView.findViewById(R.id.help3a);
				ImageView help3_2 = (ImageView)convertView.findViewById(R.id.help3_2);
				help3_2.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						CommonUtil.saveInHelpPage(context, false);
						if(!isInSetting){
							OfflineLog.writeMainMenu_Help((byte)1);//写入离线日志
							context.startActivity(new Intent(context, WelcomeActivity.class));
						}
						((Activity)context).finish();
					}
				});
				convertView.setTag(holder);
				break;
			}
		}else{
			holder = (HelpHolder)convertView.getTag();
		}
		return convertView;
	}

	public final class HelpHolder{
		public LinearLayout layout = null;
	}
}
