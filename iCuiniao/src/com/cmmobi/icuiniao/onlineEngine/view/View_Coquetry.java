/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Coquetry;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Coquetryitem;
import com.cmmobi.icuiniao.ui.adapter.CoquetryAdapter;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.DownImageManager;
import com.cmmobi.icuiniao.util.MessageID;

/**
 * @author hw
 *我的撒娇
 */
public class View_Coquetry extends XmlViewLayout{

	private Parser_Coquetry parserCoquetry;
	private ArrayList<View_CoquetryItem> items;
	private int startIndex;//下载队列开始的索引号
	private int firstStartIndex;
	public CoquetryAdapter adapter;
	private int totalpage;
	private int curIndex;
	private boolean hide;
	private ArrayList<String> urls;
	//lyb
	private ListView listView;
	private Handler mHandler;
	private Animation animation;  
	
	
	public View_Coquetry(Context context, Parser_Coquetry parserCoquetry, Handler handler,int totalpage,boolean hide) {
		super(context, handler, null);
		//
		mHandler = handler;
		animation = (Animation) AnimationUtils.loadAnimation(context, R.anim.item_anim);
		//
		LinearLayout l = null;
		this.parserCoquetry = parserCoquetry;
		this.hide = hide;
		if(parserCoquetry.getEmpty()){
			l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.empty, null);
			TextView emptytext = (TextView)l.findViewById(R.id.emptytext);
			emptytext.setText("您还没有撒娇的信息哦！");
		} else {
			this.totalpage = totalpage;
			curIndex = 1;
			items = new ArrayList<View_CoquetryItem>();
			urls = parserCoquetry.getUrls();
			startIndex = DownImageManager.Size();
			firstStartIndex = startIndex;
			ArrayList<Parser_Coquetryitem> tmpArrayList = parserCoquetry
					.getCoquetryitems();
			View_CoquetryItem item;
			for (int i = 0; tmpArrayList != null && i < tmpArrayList.size(); i++) {
				item = new View_CoquetryItem(context, false,
						(Parser_Coquetryitem) tmpArrayList.get(i), startIndex
								+ i, hide);
				items.add((View_CoquetryItem) item.addItem());
			}
			if (totalpage > 1) {// 查看更多
				item = new View_CoquetryItem(context, true, null, startIndex,
						hide);
				items.add((View_CoquetryItem) item.addItem());
			}

			l = (LinearLayout) ((Activity) context).getLayoutInflater()
					.inflate(R.layout.list_coquetry, null);
			listView = (ListView) l.findViewById(R.id.list_coquetry);
			listView.setScrollbarFadingEnabled(true);
			adapter = new CoquetryAdapter(items, this);
			listView.setAdapter(adapter);

			listView.setOnItemClickListener(itemClickListener);

			// lyb
			listView.setOnTouchListener(new OnTouchListener() {
				float x, y, upx, upy;

				@Override
				public boolean onTouch(View view, MotionEvent event) {
					ImageView img = (ImageView)view.findViewById(R.id.coquetry_image);
					int imgW = img.getWidth();					
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						x = event.getX();
						y = event.getY();
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						float spaceX = Math.abs(event.getX() - x);
						float spaceY = Math.abs(event.getY() - y);
						float ratio = CommonUtil.screen_width / 480f;
						if (spaceX > (15 * ratio) || spaceY > (15 * ratio)) {
							return false;
						}
						int pos = ((ListView) view).pointToPosition((int) x,
								(int) y);
						// more
						if (items.get(pos).getParserCoquetryitem() == null) {
							return false;
						}
						
						float left_1X = 10f * (ratio);						
						if (x > left_1X && x < (imgW + left_1X)) {
							imageClick(pos, false);
						}
						float right_2X = CommonUtil.screen_width - 10f * ratio;
						float left_2X = right_2X - 85 * ratio;
						if (x > left_2X && x < right_2X) {
							if (isStateHide()) {
								return false;
							}
							View v = items.get(pos);
							removeListItem(v, pos);
						}
					}
					return false;
				}

			});
			// lyb resive end
		}
		
		addView(l);
	}	

	/**
	 * 删除屏蔽原删除菜单
	 * 发消息到菜单删除的操作即可
	 * @param selectIdx
	 */
	private void delItem(int selectIdx){		
		Message message = new Message();
		message.what = MessageID.MESSAGE_COQUETRY_TOUCH_DEL;
		message.arg1 = 0;
		message.arg2 = selectIdx;
		mHandler.sendMessage(message);
		
	}
	
    /** 
     * lyb
     * 删除item，并播放动画 
     * @param rowView 播放动画的view 
     * @param positon 要删除的item位置 
     */  
    protected void removeListItem(final View rowView, final int pos) {         
        
//        animation.setAnimationListener(new AnimationListener() {  
//            public void onAnimationStart(Animation animation) {}  
//  
//            public void onAnimationRepeat(Animation animation) {}  
//  
//            public void onAnimationEnd(Animation animation) {        	
//            }  
//        });          

        delItem(pos);
        animation.setFillAfter(true);
        rowView.startAnimation(animation);  
       
    }
	
	public View_CoquetryItem getCoquetryItem(int index){
		if(items != null&&items.size() > 0){
			return items.get(index-firstStartIndex);
		}
		return null;
	}
	
	public void insertList(Parser_Coquetry parserList){
		if(!parserList.getEmpty()){
			if(curIndex < totalpage){
				startIndex = DownImageManager.Size();
				items.remove(items.size()-1);//把最后一个更多删除
				ArrayList<Parser_Coquetryitem> tmpArrayList = parserList.getCoquetryitems();
				View_CoquetryItem item;
				for(int i = 0;tmpArrayList != null&&i < tmpArrayList.size();i ++){
					item = new View_CoquetryItem(getContext(), false, (Parser_Coquetryitem)tmpArrayList.get(i), startIndex+i,hide);
					items.add((View_CoquetryItem)item.addItem());
				}
				if(curIndex != totalpage-1){//查看更多
					item = new View_CoquetryItem(getContext(), true, null, startIndex,hide);
					items.add((View_CoquetryItem)item.addItem());
				}
				ArrayList<String> tmpUrls = parserList.getUrls();
				for(int i = 0;tmpUrls!=null&&i < tmpUrls.size();i ++){
					urls.add(tmpUrls.get(i));
				}
				adapter.notifyDataSetChanged();
				
				curIndex ++;
			}
		}
	}
	
	private OnItemClickListener itemClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long id) {
			// TODO Auto-generated method stub
			if(items.get(pos).getParserCoquetryitem() == null){//更多
				onCoquetryItemClick(curIndex, totalpage,true,-1,null);
			}else{
//				onCoquetryItemClick(curIndex, totalpage,false,pos,items.get(pos).getParserCoquetryitem().getHref());
			}
		}
		
	};
	
	/** 
     * lyb
     * 按键点击请求（）
     * @param pos  列表的行数
     * @param isNull 
     * @param isDel 是否是删除操作
     */  
	public void imageClick(int pos, boolean isNull){
		if(isNull){//更多
			onCoquetryItemClick(curIndex, totalpage,true,-1,null);
		}else{
			onCoquetryItemClick(curIndex, totalpage,false,pos,items.get(pos).getParserCoquetryitem().getHref());
		}
	}
	//lyb 删除图片是否隐藏的逻辑
	private boolean isHide = true;
	public void setState(boolean hide){
		isHide = hide;
		if(items != null&&items.size() > 0){
			for(int i = 0;i < items.size();i ++){
				items.get(i).setState(hide);
			}
			adapter.notifyDataSetInvalidated();
		}
	}
	/**
	 * lyb
	 * 删除是否隐藏
	 * 如果删除隐藏，即是编辑状态
	 * @return
	 */
	public boolean isStateHide(){
		return isHide;
	}
	
	public ArrayList<String> getUrls(){
		return urls;
	}

}
