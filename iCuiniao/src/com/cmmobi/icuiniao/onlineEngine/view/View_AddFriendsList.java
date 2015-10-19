/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_List;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ListItem;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ListItem_Friends;
import com.cmmobi.icuiniao.ui.adapter.AddFriendsAdapter;
import com.cmmobi.icuiniao.util.DownImageManager;

/**
 * @author hw
 *添加好友
 */
public class View_AddFriendsList extends XmlViewLayout{

	private Parser_List parserList;
	private ArrayList<View_AddFriendsList_item> items;
	private int startIndex;//下载队列开始的索引号
	private int firstStartIndex;
	private AddFriendsAdapter adapter;
	private int totalpage;
	private int curIndex;
	
	public View_AddFriendsList(Context context, Parser_List parserList, Handler handler,int totalpage) {
		super(context, handler, null);
		// TODO Auto-generated constructor stub
		this.parserList = parserList;
		this.totalpage = totalpage;
		curIndex = 1;
		items = new ArrayList<View_AddFriendsList_item>();
		startIndex = DownImageManager.Size();
		firstStartIndex = startIndex;
		ArrayList<Parser_ListItem> tmpArrayList = parserList.getListItems();
		View_AddFriendsList_item item;
		for(int i = 0;tmpArrayList != null&&i < tmpArrayList.size();i ++){
			item = new View_AddFriendsList_item(context, false, (Parser_ListItem_Friends)tmpArrayList.get(i), startIndex+i);
			items.add((View_AddFriendsList_item)item.addItem());
		}
		if(totalpage > 1){//查看更多
			item = new View_AddFriendsList_item(context, true, null, startIndex);
			items.add((View_AddFriendsList_item)item.addItem());
		}
		
		LinearLayout l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.list_addfriends, null);
		ListView listView = (ListView)l.findViewById(R.id.list_addfriends);
		listView.setScrollbarFadingEnabled(true);
		adapter = new AddFriendsAdapter(items);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(itemClickListener);
		
		addView(l);
	}
	
	public View_AddFriendsList_item getListItem(int index){
		return items.get(index-firstStartIndex);
	}
	
	public void insertList(Parser_List parserList){
		if(curIndex < totalpage){
			startIndex = DownImageManager.Size();
			items.remove(items.size()-1);//把最后一个更多删除
			ArrayList<Parser_ListItem> tmpArrayList = parserList.getListItems();
			View_AddFriendsList_item item;
			for(int i = 0;tmpArrayList != null&&i < tmpArrayList.size();i ++){
				item = new View_AddFriendsList_item(getContext(), false, (Parser_ListItem_Friends)tmpArrayList.get(i), startIndex+i);
				items.add((View_AddFriendsList_item)item.addItem());
			}
			if(curIndex != totalpage-1){//查看更多
				item = new View_AddFriendsList_item(getContext(), true, null, startIndex);
				items.add((View_AddFriendsList_item)item.addItem());
			}
			adapter.notifyDataSetChanged();
			
			curIndex ++;
		}
	}
	
	private OnItemClickListener itemClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if(items.get(arg2).getParserListItemFriends() == null){//更多
				onAddFriendsListItemClick(null, -1, -1, true, curIndex, totalpage);
			}else{
				String url = items.get(arg2).getParserListItemFriends().getHref();
				int userid = items.get(arg2).getParserListItemFriends().getUserId();
				int action = items.get(arg2).getParserListItemFriends().getAction();
				onAddFriendsListItemClick(url, userid, action, false, curIndex, totalpage);
			}
		}
		
	};

}
