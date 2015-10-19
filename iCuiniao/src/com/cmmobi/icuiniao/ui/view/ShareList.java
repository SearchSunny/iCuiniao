/**
 * 
 */
package com.cmmobi.icuiniao.ui.view;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.ui.adapter.ShareListAdapter;

/**
 * @author hw
 *
 *分享列表
 */
public class ShareList extends ListView{

	public ArrayList<HashMap<String, Object>> appInfos;
	public ShareListAdapter shareListAdapter;
	
	public int[] itemImages = {R.drawable.listitem_weibo,R.drawable.wxfreind1,R.drawable.listitem_wx,R.drawable.listitem_qq,R.drawable.listitem_sms,R.drawable.listitem_email};
	public int[] itemStrings = {R.string.share_item_weibo,R.string.share_item_pengyou,R.string.share_item_wx,R.string.share_item_qq,R.string.share_item_sms,R.string.share_item_email};
	
	public ShareList(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public ShareList(Context context, AttributeSet attrs){
		super(context, attrs);
		getAppInfos();
		shareListAdapter = new ShareListAdapter(appInfos,context);
		setAdapter(shareListAdapter);
	}
	
	//获得应用程序信息
	public void getAppInfos(){
		appInfos = new ArrayList<HashMap<String,Object>>();
		for(int i = 0;i < itemStrings.length;i ++){
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImages", itemImages[i]);
			map.put("itemStrings",getContext().getString(itemStrings[i]));
			appInfos.add(map);
		}
	}

}
