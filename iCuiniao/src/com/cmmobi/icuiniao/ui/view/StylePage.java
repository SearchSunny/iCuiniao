/**
 * 
 */
package com.cmmobi.icuiniao.ui.view;

import java.util.ArrayList;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.activity.MainPageActivityA;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ParserEngine;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_StyleItem;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_StylePage;
import com.cmmobi.icuiniao.ui.adapter.StylePageAdapter;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.DownImageManagerA;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.PageID;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author hw
 *自定义商品
 */
public class StylePage extends LinearLayout{
	
	private MGridView stylepage_grid;
	private ProgressBar loadingBar;
	private Parser_ParserEngine parserEngine;
	private Parser_StylePage parserStylePage;
	private ArrayList<Parser_StyleItem> items;
	private StyleItem[] styleItems;
	private DownImageManagerA downImageManagerA;
	private StylePageAdapter adapter;
	
	public String selectedIndex;//选中项
	public String removeIndex;//删除项
	public String selectedName;

	public StylePage(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		selectedIndex = "";
		removeIndex = "";
		selectedName = "";
		downImageManagerA = new DownImageManagerA();
		LinearLayout l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.stylepage,null);
		stylepage_grid = (MGridView)l.findViewById(R.id.stylepage_grid);
		loadingBar = (ProgressBar)l.findViewById(R.id.loading);
		addView(l);
	}
	
	public void initConnect(Context context){
		selectedIndex = "";
		removeIndex = "";
		selectedName = "";
		new ConnectUtil(context, mHandler, 0).connect(URLUtil.URL_STYLEPAGE, HttpThread.TYPE_PAGE, 0);
	}
	
	public void addProgress(){
		if(loadingBar != null){
			loadingBar.setVisibility(View.VISIBLE);
		}
	}
	
	public void closeProgress(){
		if(loadingBar != null){
			loadingBar.setVisibility(View.INVISIBLE);
		}
	}
	
	private void buildPage(Message msg,int threadindex,int type){
		if(type == HttpThread.TYPE_PAGE){
			parserEngine = new Parser_ParserEngine(getContext());
			parserEngine.parser((byte[])msg.obj);
			Parser_Layout_AbsLayout[] tmpAbsLayouts = parserEngine.getLayouts();
			for(int i = 0;tmpAbsLayouts!=null&&i < tmpAbsLayouts.length;i ++){
				if(tmpAbsLayouts[i]!=null&&tmpAbsLayouts[i].getModelType() == Parser_Layout_AbsLayout.MODELTYPE_STYLEPAGE){
					parserStylePage = (Parser_StylePage)tmpAbsLayouts[i];
					break;
				}
			}
			if(parserStylePage != null){
				items = parserStylePage.getItems();
				styleItems = new StyleItem[items.size()];
				//压入下载队列
				for(int i = 0;items != null&&i < items.size();i ++){
					styleItems[i] = new StyleItem(getContext());
					DownImageItem downImageItem = new DownImageItem(Parser_Layout_AbsLayout.MODELTYPE_STYLEITEM, i,items.get(i).getSrc(), PageID.PAGEID_STYLEPAGE, 0);
					downImageManagerA.add(downImageItem);
				}
				adapter = new StylePageAdapter(styleItems);
				stylepage_grid.setAdapter(adapter);
			}
			mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_CONNECT_LAYOUTOVER, 100);
		}else{
			try {
				if(styleItems != null){
					styleItems[threadindex].setStr(items.get(threadindex).getStr());
					styleItems[threadindex].setImage((byte[])msg.obj, msg.getData().getString("mUrl"),items.get(threadindex).getSelected());
					adapter.notifyDataSetChanged();
					boolean over = true;
					for(int i = 0;i < styleItems.length;i ++){
						if(!styleItems[i].isrenderover){
							over = false;
							break;
						}
					}
					if(over){
						closeProgress();
						downImageManagerA.clear();
						stylepage_grid.setOnItemClickListener(itemClickListener);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				closeProgress();
				downImageManagerA.clear();
			}
		}
	}
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if(items.get(arg2).getSelected() == false){
				items.get(arg2).setSelected(Parser_Layout_AbsLayout.TRUE);
				selectedIndex = items.get(arg2).getId();
				removeIndex = "";
				selectedName = items.get(arg2).getStr();
				UserUtil.isCallEditerOver = false;
			}else{
				items.get(arg2).setSelected(Parser_Layout_AbsLayout.FALSE);
				selectedIndex = "";
				removeIndex = items.get(arg2).getId();
				selectedName = items.get(arg2).getStr();
			}
			//单选，需要把其他的图片复位
			for(int i = 0;i < items.size();i ++){
				if(i==arg2)continue;
				if(items.get(i).getSelected()){
					items.get(i).setSelected(Parser_Layout_AbsLayout.FALSE);
					styleItems[i].setImage(items.get(i).getSelected());
				}
			}
			styleItems[arg2].setImage(items.get(arg2).getSelected());
			adapter.notifyDataSetChanged();
			((MainPageActivityA)getContext()).gotoStylePage();
		}
	};
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageID.MESSAGE_CONNECT_START:
				addProgress();
				break;
			case MessageID.MESSAGE_CONNECT_ERROR:
				closeProgress();
				break;
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				buildPage(msg,msg.arg1, msg.getData().getInt("mType"));
				break;
			case MessageID.MESSAGE_CONNECT_LAYOUTOVER:
				DownImageItem downImageItem = downImageManagerA.get();
				if(downImageItem != null){
					String urlString = downImageItem.getUrl();
					if(urlString != null){
						new ConnectUtil(getContext(), mHandler,0).connect(urlString, HttpThread.TYPE_IMAGE, downImageItem.getIndex());
					}
					//发起下一个询问
					mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_CONNECT_LAYOUTOVER, 100);
				}
				break;
			}
		}
		
	};

}
