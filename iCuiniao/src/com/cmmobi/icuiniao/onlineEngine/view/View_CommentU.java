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
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ListItem_Comment_U;
import com.cmmobi.icuiniao.ui.adapter.Comment_U_Adapter;
import com.cmmobi.icuiniao.util.DownImageManager;

/**
 * @author hw
 * 
 */
public class View_CommentU extends XmlViewLayout {

	private Parser_List parserList;
	private ArrayList<View_CommentU_Item> items;
	private int startIndex;//下载队列开始的索引号
	private int firstStartIndex;
	private Comment_U_Adapter adapter;
	private int totalpage;
	private int curIndex;
	
	public View_CommentU(Context context, Parser_List parserList, Handler handler,int totalpage) {
		super(context, handler, null);
		// TODO Auto-generated constructor stub
		this.parserList = parserList;
		this.totalpage = totalpage;
		curIndex = 1;
		items = new ArrayList<View_CommentU_Item>();
		startIndex = DownImageManager.Size();
		firstStartIndex = startIndex;
		ArrayList<Parser_ListItem> tmpArrayList = parserList.getListItems();
		View_CommentU_Item item;
		for(int i = 0;tmpArrayList != null&&i < tmpArrayList.size();i ++){
			item = new View_CommentU_Item(context, false, (Parser_ListItem_Comment_U)tmpArrayList.get(i), startIndex+i);
			items.add((View_CommentU_Item)item.addItem());
		}
		
		if(totalpage > 1){//查看更多
			item = new View_CommentU_Item(context, true, null, startIndex);
			items.add((View_CommentU_Item)item.addItem());
		}
		
		LinearLayout l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.list_comment_u, null);
		ListView listView = (ListView)l.findViewById(R.id.list_commentU);
		listView.setScrollbarFadingEnabled(true);
		adapter = new Comment_U_Adapter(items);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(itemClickListener);
		
		addView(l);
	}
	
	public View_CommentU_Item getListItem(int index){
		return items.get(index-firstStartIndex);
	}
	
	public void insertList(Parser_List parserList){
		if(curIndex < totalpage){
			startIndex = DownImageManager.Size();
			items.remove(items.size()-1);//把最后一个更多删除
			ArrayList<Parser_ListItem> tmpArrayList = parserList.getListItems();
			View_CommentU_Item item;
			for(int i = 0;tmpArrayList != null&&i < tmpArrayList.size();i ++){
				item = new View_CommentU_Item(getContext(), false, (Parser_ListItem_Comment_U)tmpArrayList.get(i), startIndex+i);
				items.add((View_CommentU_Item)item.addItem());
			}
			if(curIndex != totalpage-1){//查看更多
				item = new View_CommentU_Item(getContext(), true, null, startIndex);
				items.add((View_CommentU_Item)item.addItem());
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
			if(items.get(arg2).getParserListItemCommentU() == null){//更多
				onListItemCommentU_Click(null, -1, -1, true, curIndex, totalpage,-1,-1);
			}else{
				String url = items.get(arg2).getParserListItemCommentU().getHref();
				int userid = items.get(arg2).getParserListItemCommentU().getUserId();
				int action = -1;
				if(items.get(arg2).getParserListItemCommentU().getCommentButton() != null){
					action = items.get(arg2).getParserListItemCommentU().getCommentButton().getAction();
				}
				int subjectid = items.get(arg2).getParserListItemCommentU().getSubjectId();
				int commentid = items.get(arg2).getParserListItemCommentU().getCommentId();
				onListItemCommentU_Click(url, userid, action, false, curIndex, totalpage,subjectid,commentid);
			}
		}
	};

}
