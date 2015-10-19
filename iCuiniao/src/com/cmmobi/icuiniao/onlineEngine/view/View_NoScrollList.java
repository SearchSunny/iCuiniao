/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.widget.LinearLayout;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_List;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ListItem;
import com.cmmobi.icuiniao.util.DownImageManager;

/**
 * @author hw
 *
 *不带滚动条的自定义列表
 */
public class View_NoScrollList extends XmlViewLayout{

	private Parser_List parserList;
	private LinearLayout l;
	private ArrayList<View_NoScrollListItem> listItems;
	private Handler handler;
	
	private int startIndex;//下载队列开始的索引号
	private int firstStartIndex;
	
	private int totalpage;
	private int curIndex;
	private LinearLayout lCommentList;
	private boolean isAddFlushBtn;//是否添加刷新评论按钮
	
	public View_NoScrollList(Context context, Parser_List parserList, Handler handler, int totalpage,int tag) {
		super(context, handler,null);
		// TODO Auto-generated constructor stub
		this.parserList = parserList;
		this.handler = handler;
		this.totalpage = totalpage;
		curIndex = 1;
		isAddFlushBtn = true;
		l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.list_comment, null);
		lCommentList = (LinearLayout)l.findViewById(R.id.lCommentList);
		listItems = new ArrayList<View_NoScrollListItem>();
		ArrayList<Parser_ListItem> tmpArrayList = parserList.getListItems();
		startIndex = DownImageManager.Size();
		firstStartIndex = startIndex;
		for(int i = 0;tmpArrayList!=null&&i < tmpArrayList.size();i ++){
			listItems.add(initListItem(tmpArrayList.get(i),startIndex+i,false,curIndex,totalpage,tag));
			lCommentList.addView(listItems.get(i));
		}
		
		if(totalpage > 1){//查看更多
			isAddFlushBtn = false;
			listItems.add(initListItem(null, startIndex, true, curIndex, totalpage,tag));
			lCommentList.addView(listItems.get(listItems.size()-1));
		}
		
		addView(l);
	}
	
	
	private View_NoScrollListItem initListItem(Parser_ListItem parserListItem,int index,boolean isMoreItem,int curIndex,int totalpage,int tag){
		View_NoScrollListItem item = new View_NoScrollListItem(getContext(), isMoreItem,parserListItem, handler,index,curIndex,totalpage,tag);
		return item.render();
	}
	
	public View_NoScrollListItem getListItem(int index){
		return listItems.get(index-firstStartIndex);
	}
	
	public void insertList(Parser_List parserList,int tag){
		if(curIndex < totalpage){
			isAddFlushBtn = true;
			startIndex = DownImageManager.Size();
			lCommentList.removeAllViews();
			listItems.remove(listItems.size()-1);//把最后一个更多删除
			ArrayList<Parser_ListItem> tmpArrayList = parserList.getListItems();
			for(int i = 0;tmpArrayList!=null&&i < tmpArrayList.size();i ++){
				listItems.add(initListItem(tmpArrayList.get(i),startIndex+i,false,curIndex,totalpage,tag));
			}
			if(curIndex != totalpage-1){//查看更多
				isAddFlushBtn = false;
				listItems.add(initListItem(null, startIndex, true, curIndex, totalpage,tag));
			}
			for(int i = 0;listItems!=null&&i < listItems.size();i ++){
				lCommentList.addView(listItems.get(i));
			}
			curIndex ++;
			lCommentList.postInvalidate();
		}
	}
	
	public boolean IsAddFlushBtn(){
		return isAddFlushBtn;
	}
	
}
