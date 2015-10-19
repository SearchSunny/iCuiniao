/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ListItem;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ListItem_Comment_C;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.DownImageManager;
import com.cmmobi.icuiniao.util.MessageID;

/**
 * @author hw
 *
 */
public class View_NoScrollListItem extends XmlViewLayout{

	private boolean isMoreItem;//是否为"更多"
	private Parser_ListItem_Comment_C parserListItemCommentC;
	
	private LinearLayout l;
	private TextView listItem_more;
	private LinearLayout lListItem_c;
	private ImageView listItem_c_icon;
	private TextView listItem_c_name;
	private TextView listItem_c_time;
	private TextView listItem_c_comment;
	private int buttonAction;
	private int curIndex;
	private int totalpage;
	private Handler mHandler;
	
	public View_NoScrollListItem(Context context, boolean isMoreItem, Parser_ListItem parserListItem, Handler handler,int index,int curIndex,int totalpage,int tag) {
		super(context,handler,null);
		// TODO Auto-generated constructor stub
		mHandler = handler;
		this.isMoreItem = isMoreItem;
		this.curIndex = curIndex;
		this.totalpage = totalpage;
		parserListItemCommentC = (Parser_ListItem_Comment_C)parserListItem;
		
		if(isMoreItem){
			l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.listitem_comment_c_more,null);
			l.setGravity(Gravity.CENTER_HORIZONTAL);
			listItem_more = (TextView)l.findViewById(R.id.listItem_c_comment);
			listItem_more.setGravity(Gravity.CENTER_HORIZONTAL);
			l.setOnClickListener(moreClickListener);
		}else{
			if(parserListItemCommentC.getCommentButton() == null){
				buttonAction = -1;
			}else{
				buttonAction = parserListItemCommentC.getCommentButton().getAction();
			}
			
			String url = null;
			LinearLayout lListitem_c_icon;
			
			if(buttonAction == Parser_Layout_AbsLayout.ACTION_COMMENT){
				l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.listitem_comment_c_more,null);
				lListItem_c = (LinearLayout)l.findViewById(R.id.lListItem_c);
				listItem_c_comment = (TextView)l.findViewById(R.id.listItem_c_comment);
			}else{
				l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.listitem_comment_c,null);
				lListItem_c = (LinearLayout)l.findViewById(R.id.lListItem_c);
				lListitem_c_icon = (LinearLayout)l.findViewById(R.id.lListItem_c_icon);
				listItem_c_icon = (ImageView)l.findViewById(R.id.listItem_c_icon);
				listItem_c_name = (TextView)l.findViewById(R.id.listItem_c_name);
				listItem_c_time = (TextView)l.findViewById(R.id.listItem_c_time);
				listItem_c_comment = (TextView)l.findViewById(R.id.listItem_c_comment);
				if(parserListItemCommentC.getImage().getSrc()==null||parserListItemCommentC.getImage().getSrc().equalsIgnoreCase("null")||parserListItemCommentC.getImage().getSrc().equalsIgnoreCase("")){
					url = null;
					lListitem_c_icon.setVisibility(GONE);
				}else{
					url = parserListItemCommentC.getImage().getSrc();
				}
			}
			
			lListItem_c.setOnClickListener(itemClickListener);
			
			//压入下载队列
			DownImageItem downImageItem = new DownImageItem(Parser_Layout_AbsLayout.MODELTYPE_LISTITEM, index, url, parserListItemCommentC.getPageId(),tag);
			DownImageManager.add(downImageItem);
		}
		
		addView(l);
	}
	
	int flingX,flingY;
	boolean isFling;
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			flingX = (int)ev.getRawX();
			flingY = (int)ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			int tmp = (int)ev.getRawX();
			int tmpy = (int)ev.getRawY();
			if(Math.abs(tmpy-flingY) <= 100){
				if(flingX - tmp > 100){
					isFling = true;
					mHandler.sendEmptyMessage(MessageID.MESSAGE_FLING_NEXT);
				}else if(tmp - flingX > 100){
					isFling = true;
					mHandler.sendEmptyMessage(MessageID.MESSAGE_FLING_PRE);
				}else{
					isFling = false;
				}
			}else{
				isFling = false;
			}
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}
	
	public View_NoScrollListItem render(){
		if(!isMoreItem){
			if(buttonAction != Parser_Layout_AbsLayout.ACTION_COMMENT){
				listItem_c_name.setText(parserListItemCommentC.getName().getStr());
				listItem_c_time.setText(parserListItemCommentC.getCommentTime().getStr());
			}
			listItem_c_comment.setText(parserListItemCommentC.getCommentMsg().getStr());
		}else{
			listItem_more.setText("查看更多");
		}
		
		return this;
	}

	private OnClickListener itemClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(!isFling){
				onListItemCommentC_Click(parserListItemCommentC.getHref(), parserListItemCommentC.getUserId(),buttonAction,parserListItemCommentC.getSubjectId());
			}
			isFling = false;
		}
	};
	
	private OnClickListener moreClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(!isFling){
				onListItemCommentC_Click(curIndex, totalpage);
			}
			isFling = false;
		}
	};
	
	public void setImageView(final byte[] data,final String cacheUrl){
		try {
			//保存图片
			new Thread(){
				public void run(){
					CommonUtil.writeToFile(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(cacheUrl), data);
				}
			}.start();
		} catch (Exception e) {
		}
		Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
		if(temp == null)return;
		Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.usericonbg);
		temp = CommonUtil.resizeImage(temp, background.getWidth(), background.getHeight());
		listItem_c_icon.setImageBitmap(CommonUtil.mergerIcon(background, temp,7));
	}
	
	public Parser_ListItem_Comment_C getParserListItemCommentC(){
		return parserListItemCommentC;
	}
}
