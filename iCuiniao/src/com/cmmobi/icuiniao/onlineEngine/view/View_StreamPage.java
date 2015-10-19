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
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Image;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_StreamPage;
import com.cmmobi.icuiniao.ui.adapter.StreampageAdapter;
import com.cmmobi.icuiniao.util.DownImageManager;
import com.cmmobi.icuiniao.util.PageID;

/**
 * @author hw
 *瀑布流
 */
public class View_StreamPage extends XmlViewLayout{

	private Parser_StreamPage parserStreamPage;
	private ArrayList<View_StreamPageItem> items;
	private ArrayList<Parser_Image> tmpArrayList;
	private int startIndex;//下载队列开始的索引号
	private int firstStartIndex;
	private StreampageAdapter adapter;
	private String nexturl;
	private ArrayList<String> tmpUrls;
	private LinearLayout streampage_next;
	
	public View_StreamPage(Context context, Parser_StreamPage parserStreamPage, Handler handler) {
		super(context, handler, null);
		// TODO Auto-generated constructor stub
		LinearLayout l = null;
		if(parserStreamPage.getEmpty()){
			l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.empty, null);
			TextView emptytext = (TextView)l.findViewById(R.id.emptytext);
			if(parserStreamPage.getPageId() == PageID.PAGEID_MAINPAGE_MYLIKE){
				emptytext.setText("您还没有喜欢过商品哦！");
			}else if(parserStreamPage.getPageId() == PageID.PAGEID_MAINPAGE_MYLOOKED){
				emptytext.setText("您还没有看过商品哦！");
			}
		}else{
			this.parserStreamPage = parserStreamPage;
			nexturl = parserStreamPage.getNextUrl();
			items = new ArrayList<View_StreamPageItem>();
			startIndex = DownImageManager.Size();
			firstStartIndex = startIndex;
			tmpArrayList = parserStreamPage.getImages();
			tmpUrls = parserStreamPage.getUrls();
			View_StreamPageItem item;
			for(int i = 0;tmpArrayList != null&&i < tmpArrayList.size();i ++){
				item = new View_StreamPageItem(getContext(), tmpArrayList.get(i), startIndex+i);
				items.add(item);
			}
			
			l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.streampage, null);
			GridView gridView = (GridView)l.findViewById(R.id.streampage_grid);
			streampage_next = (LinearLayout)l.findViewById(R.id.streampage_next);
			if(nexturl != null&&nexturl.trim().length() > 0){
				streampage_next.setVisibility(VISIBLE);
			}else{
				streampage_next.setVisibility(INVISIBLE);
			}
			adapter = new StreampageAdapter(items);
			gridView.setAdapter(adapter);
			gridView.setOnItemClickListener(gridItemClickListener);
			streampage_next.setOnClickListener(nextClickListener);
		}
		
		addView(l);
	}
	
	public View_StreamPageItem getStreamPageItem(int index){
		if(items != null&&items.size() > 0){
			return items.get(index-firstStartIndex);
		}
		return null;
	}
	
	public void insertList(Parser_StreamPage parserStreamPage){
		if(!parserStreamPage.getEmpty()){
			nexturl = parserStreamPage.getNextUrl();
			startIndex = DownImageManager.Size();
			ArrayList<Parser_Image> tmpArrayList1 = parserStreamPage.getImages();
			ArrayList<String> tmpUrls1 = parserStreamPage.getUrls();
			if(nexturl != null&&nexturl.trim().length() > 0){
				streampage_next.setVisibility(VISIBLE);
			}else{
				streampage_next.setVisibility(INVISIBLE);
			}
			View_StreamPageItem item;
			for(int i = 0;tmpArrayList1 != null&&i < tmpArrayList1.size();i ++){
				tmpArrayList.add(tmpArrayList1.get(i));
				item = new View_StreamPageItem(getContext(), tmpArrayList1.get(i), startIndex+i);
				items.add(item);
			}
			for(int i = 0;tmpUrls1 != null&&i < tmpUrls1.size();i ++){
				tmpUrls.add(tmpUrls1.get(i));
			}
			adapter.notifyDataSetChanged();
		}
	}
	
	private OnItemClickListener gridItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			onStreamPageItemClick(tmpArrayList.get(arg2).getHref(),arg2);
		}
	};
	
	private OnClickListener nextClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(nexturl != null){
				onStreamPageMoreClick(nexturl);
			}
		}
	};
	
	public int size(){
		return items.size();
	}
	
	public ArrayList<String> getUrls(){
		return tmpUrls;
	}

}
