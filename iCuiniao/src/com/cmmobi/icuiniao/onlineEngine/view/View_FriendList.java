/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_List;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ListItem;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ListItem_OnlyText;
import com.cmmobi.icuiniao.ui.adapter.OnlyTextAdapter;
import com.cmmobi.icuiniao.util.DownImageManager;
import com.cmmobi.icuiniao.util.MessageID;

/**
 * @author hw
 *用于展示关注,相互关注,粉丝,推荐好友用的列表
 */
public class View_FriendList extends XmlViewLayout{

	private Parser_List parserList;
	private ArrayList<View_FriendList_item> items;
	private int startIndex;//下载队列开始的索引号
	private int firstStartIndex;
	private OnlyTextAdapter adapter;
	private int totalpage;
	private int curIndex;
	
	private Handler handler;
	
	public View_FriendList(Context context, Parser_List parserList, Handler handler,int totalpage) {
		super(context, handler, null);
		// TODO Auto-generated constructor stub
		this.parserList = parserList;
		this.totalpage = totalpage;
		this.handler = handler;
		curIndex = 1;
		items = new ArrayList<View_FriendList_item>();
		startIndex = DownImageManager.Size();
		firstStartIndex = startIndex;
		ArrayList<Parser_ListItem> tmpArrayList = parserList.getListItems();
		View_FriendList_item item;
		for(int i = 0;tmpArrayList != null&&i < tmpArrayList.size();i ++){
			item = new View_FriendList_item(context, handler, false, (Parser_ListItem_OnlyText)tmpArrayList.get(i), startIndex+i,mHandler);
			items.add((View_FriendList_item)item.addItem());
		}
		
		if(totalpage > 1){//查看更多
			item = new View_FriendList_item(context, handler, true, null, startIndex,mHandler);
			items.add((View_FriendList_item)item.addItem());
		}
		
		LinearLayout l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.list_onlytext, null);
		ListView listView = (ListView)l.findViewById(R.id.list_onlytext);
		listView.setScrollbarFadingEnabled(true);
		adapter = new OnlyTextAdapter(items);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(itemClickListener);
		
		addView(l);
	}
	
	public View_FriendList_item getListItem(int index){
		return items.get(index-firstStartIndex);
	}
	
	public void insertList(Parser_List parserList){
		if(curIndex < totalpage){
			startIndex = DownImageManager.Size();
			items.remove(items.size()-1);//把最后一个更多删除
			ArrayList<Parser_ListItem> tmpArrayList = parserList.getListItems();
			View_FriendList_item item;
			for(int i = 0;tmpArrayList != null&&i < tmpArrayList.size();i ++){
				item = new View_FriendList_item(getContext(), handler, false, (Parser_ListItem_OnlyText)tmpArrayList.get(i), startIndex+i,mHandler);
				items.add((View_FriendList_item)item.addItem());
			}
			
			if(curIndex != totalpage-1){//查看更多
				item = new View_FriendList_item(getContext(), handler, true, null, startIndex,mHandler);
				items.add((View_FriendList_item)item.addItem());
			}
			adapter.notifyDataSetChanged();
			
			curIndex ++;
		}
	}

	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if(items.get(arg2).getListItemOnlyText() == null){//更多
				onFriendListItemClick(null, -1, -1, true, curIndex, totalpage);
			}else{
				if(items.get(arg2).IsPushFriendPage()){
					items.get(arg2).itemClick();
				}else{
					String url = items.get(arg2).getListItemOnlyText().getHref();
					int userid = items.get(arg2).getListItemOnlyText().getUserId();
					int action = items.get(arg2).getListItemOnlyText().getAction();
					onFriendListItemClick(url, userid, action, false, curIndex, totalpage);
				}
			}
		}
	};
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageID.MESSAGE_FLUSH_ADAPTER:
				adapter.notifyDataSetInvalidated();
				break;
			}
		}
		
	};
}
