/**
 * 
 */
package com.cmmobi.icuiniao.ui.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.MessageID;

/**
 * @author hw
 *
 */
public class PopTitleMenu extends Dialog{

	private int[] RES = {R.drawable.titlebarmenu_all_0,R.drawable.titlebarmenu_discount_0,R.drawable.titlebarmenu_recommend_0,R.drawable.titlebarmenu_form_0/*,R.drawable.titlebarmenu_activities_0*/};
	
	private Handler handler;
	
	public PopTitleMenu(Context context, int theme, Handler handler) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		this.handler = handler;
		LinearLayout l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.poptitlemenu, null);
		ListView listView = (ListView)l.findViewById(R.id.poptitlemenu_list);
		SimpleAdapter adapter = new SimpleAdapter(context, getData(), R.layout.poptitlemenu_item, new String[]{"item"}, new int[]{R.id.poptitlemenu_item});
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(listener);
		
		setContentView(l,new LayoutParams(CommonUtil.screen_width/2, LayoutParams.WRAP_CONTENT));
	}
	
	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		for(int i = 0;i < RES.length;i ++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("item", RES[i]);
			list.add(map);
		}
		
		return list;
	}
	
	private OnItemClickListener listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Message msg = new Message();
			msg.what = MessageID.MESSAGE_POPTITLEMENU;
			msg.arg1 = arg2;
			handler.sendMessage(msg);
			cancel();
		}
	};

}
