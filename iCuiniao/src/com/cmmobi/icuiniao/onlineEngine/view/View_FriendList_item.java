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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ListItem_OnlyText;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.DownImageManager;
import com.cmmobi.icuiniao.util.PageID;

/**
 * @author hw
 *
 */
public class View_FriendList_item extends XmlViewLayout{

	private boolean isMoreItem;//是否为"更多"
	private LinearLayout l;
	private ImageView listitem_friend_icon;
	private TextView listitem_friend_name;
	private Button listitem_friend_state;
	private LinearLayout llistitem_friend_state;
	private TextView listItem_more;
	
	private Parser_ListItem_OnlyText parserListItemOnlyText;
	private boolean isSelected;
	
	private Handler mHandler;
	
	public View_FriendList_item(Context context,Handler handler,boolean isMoreitem,Parser_ListItem_OnlyText parserListItemOnlyText,int index,Handler mHandler) {
		super(context,handler,null);
		// TODO Auto-generated constructor stub
		this.isMoreItem = isMoreitem;
		this.parserListItemOnlyText = parserListItemOnlyText;
		this.mHandler = mHandler;
		String url = null;
		if(isMoreitem){
			l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.listitem_friend_more,null);
			l.setGravity(Gravity.CENTER_HORIZONTAL);
			listItem_more = (TextView)l.findViewById(R.id.listItem_c_comment);
			listItem_more.setGravity(Gravity.CENTER_HORIZONTAL);
		}else{
			if(parserListItemOnlyText.getPageId() == PageID.PAGEID_PUSH_FRIEDN){
				l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.listitem_pushfriend,null);
			}else{
				l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.listitem_onlytext,null);
				llistitem_friend_state = (LinearLayout)l.findViewById(R.id.llistitem_friend_state);
				llistitem_friend_state.setFocusable(false);
				listitem_friend_state = (Button)l.findViewById(R.id.listitem_friend_state);
				listitem_friend_state.setFocusable(false);//这里需要设置一下,否则list中将无法响应onitemclick事件
//				listitem_friend_state.setClickable(false);
				listitem_friend_state.setClickable(true);
			}
			listitem_friend_icon = (ImageView)l.findViewById(R.id.listitem_friend_icon);
			listitem_friend_name = (TextView)l.findViewById(R.id.listitem_friend_name);
			switch (parserListItemOnlyText.getPageId()) {
			case PageID.PAGEID_PUSH_FRIEDN:
				break;
			case PageID.PAGEID_FRIEND_SEARCH:
				break;
			default:
//				listitem_friend_state.setOnClickListener(childcListener);
				listitem_friend_state.setOnTouchListener(new View.OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if(event.getAction() == MotionEvent.ACTION_UP){
							childEvent();
						}
						return false;
					}
				});
				break;
			}
			
			if(parserListItemOnlyText.getImage().getSrc() == null||parserListItemOnlyText.getImage().getSrc().equalsIgnoreCase("null")||parserListItemOnlyText.getImage().getSrc().equalsIgnoreCase("")){
				url = null;
			}else{
				url = parserListItemOnlyText.getImage().getSrc();
			}
			//压入下载队列
			DownImageItem downImageItem = new DownImageItem(Parser_Layout_AbsLayout.MODELTYPE_LISTITEM, index, url, parserListItemOnlyText.getPageId(),0);
			DownImageManager.add(downImageItem);
		}
		
		addView(l);
	}
	
	public boolean getIsMoreItem(){
		return isMoreItem;
	}
	
	public LinearLayout addItem(){
		if(!isMoreItem){
			switch (parserListItemOnlyText.getPageId()) {
			case PageID.PAGEID_PUSH_FRIEDN:
				isSelected = false;
				setBackgroundResource(R.drawable.listitem_pushfriendbg);
				break;
			case PageID.PAGEID_FRIEND_SEARCH:
				listitem_friend_state.setVisibility(INVISIBLE);
				break;
			default:
				switch (parserListItemOnlyText.getType()) {
				case Parser_Layout_AbsLayout.TYPE_ADD:
					listitem_friend_state.setText("加关注");
					break;
				case Parser_Layout_AbsLayout.TYPE_ADDEACHOTHER:
					listitem_friend_state.setText("相互关注");
					break;
				case Parser_Layout_AbsLayout.TYPE_CANCEL:
					listitem_friend_state.setText("取消关注");
					break;
				}
				break;
			}
			listitem_friend_name.setText(parserListItemOnlyText.getText().getStr());
		}else{
			listItem_more.setText("查看更多");
		}
		return this;
	}
	
	public void setImageView(byte[] data){
		Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
		if(temp == null)return;
		Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.usericonbg);
		temp = CommonUtil.resizeImage(temp, background.getWidth(), background.getHeight());
		listitem_friend_icon.setImageBitmap(CommonUtil.mergerIcon(background, temp,7));
	}
	
	public Parser_ListItem_OnlyText getListItemOnlyText(){
		return parserListItemOnlyText;
	}
	
	private OnClickListener childcListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			childEvent();			
		}
	};
	
	private void childEvent(){
		switch (parserListItemOnlyText.getPageId()) {
		case PageID.PAGEID_PUSH_FRIEDN:
			break;
		case PageID.PAGEID_FRIEND_SEARCH:
			break;
		default:
			onFriendListItemChildClick(parserListItemOnlyText.getUserId(),  parserListItemOnlyText.getFlush(), false, parserListItemOnlyText.getType(),"");
			break;
		}
	}
	
	public boolean IsPushFriendPage(){
		return parserListItemOnlyText.getPageId() == PageID.PAGEID_PUSH_FRIEDN?true:false;
	}
	
	public void itemClick(){
		isSelected = !isSelected;
		if(isSelected){
			setBackgroundResource(R.drawable.listitem_pushfriendbg_f);
		}else{
			setBackgroundResource(R.drawable.listitem_pushfriendbg);
		}
		onFriendListItemChildClick(parserListItemOnlyText.getUserId(), parserListItemOnlyText.getFlush(), isSelected, parserListItemOnlyText.getType(),parserListItemOnlyText.getText().getStr());
	}

}
