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
import android.widget.AdapterView.OnItemClickListener;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.ui.adapter.UserMenuAdapter;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.MessageID;

/**
 * @author hw
 *
 */
public class PopUserTitleMenu extends Dialog{

	private String[] RES = {"我的好友", "黑名单", "添加好友"};	
	private Handler handler;
	private LinearLayout l;
	private UserMenuAdapter userMenuAdapter;
	
	public static final int INDEX_FRIENDS = 0;
	public static final int INDEX_BLACK = 1;
	public static final int INDEX_ADD_FRIEND = 2;
	
	
	public PopUserTitleMenu(Context context, int theme, Handler handler, int index) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		this.handler = handler;
		l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.popusertitlemenu, null);
		ListView listView = (ListView)l.findViewById(R.id.poptitlemenu_list);
//		SimpleAdapter adapter = new SimpleAdapter(context, getData(), R.layout.popusertitlemenu_item, new String[]{"item"}, new int[]{R.id.popusertitlemenu_item});
//		listView.setAdapter(adapter);
//		listView.setOnItemClickListener(listener);
		
		setContentView(l,new LayoutParams(CommonUtil.screen_width/2, LayoutParams.WRAP_CONTENT));
		userMenuAdapter = new UserMenuAdapter(context);
		userMenuAdapter.setChoiceIdx(index);
		userMenuAdapter.setListStr(RES);
		
		listView.setAdapter(userMenuAdapter);
		listView.setOnItemClickListener(listener);
		this.setCanceledOnTouchOutside(true);
		
		
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
			userMenuAdapter.setChoiceIdx(arg2);
			Message msg = new Message();
			msg.what = MessageID.MESSAGE_POPTITLEMENU;
			msg.arg1 = arg2;
			handler.sendMessage(msg);
			cancel();
		}
	};

}
