/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ListItem_Friends;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.DownImageManager;

/**
 * @author hw
 *
 */
public class View_AddFriendsList_item extends LinearLayout{

	private boolean isMoreItem;//是否为"更多"
	private LinearLayout l;
	private TextView listItem_more;
	private ImageView listitem_friend_icon;
	private TextView listitem_friend_name;
	private ImageView listitem_friend_gender;
	private TextView listitem_friend_addr;
	
	private Parser_ListItem_Friends parserListItemFriends;
	
	public View_AddFriendsList_item(Context context,boolean isMoreitem,Parser_ListItem_Friends parserListItemFriends,int index) {
		super(context);
		// TODO Auto-generated constructor stub
		this.isMoreItem = isMoreitem;
		this.parserListItemFriends = parserListItemFriends;
		String url = null;
		if(isMoreitem){
			l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.listitem_friend_more,null);
			l.setGravity(Gravity.CENTER_HORIZONTAL);
			listItem_more = (TextView)l.findViewById(R.id.listItem_c_comment);
			listItem_more.setGravity(Gravity.CENTER_HORIZONTAL);
		}else{
			l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.listitem_addfriends,null);
			listitem_friend_icon = (ImageView)l.findViewById(R.id.listitem_friend_icon);
			listitem_friend_name = (TextView)l.findViewById(R.id.listitem_friend_name);
			listitem_friend_gender = (ImageView)l.findViewById(R.id.listitem_friend_gender);
			listitem_friend_addr = (TextView)l.findViewById(R.id.listitem_friend_addr);
			if(parserListItemFriends.getImage().getSrc()==null||parserListItemFriends.getImage().getSrc().equalsIgnoreCase("null")||parserListItemFriends.getImage().getSrc().equalsIgnoreCase("")){
				url = null;
			}else{
				url = parserListItemFriends.getImage().getSrc();
			}
			//压入下载队列
			DownImageItem downImageItem = new DownImageItem(Parser_Layout_AbsLayout.MODELTYPE_LISTITEM, index, url, parserListItemFriends.getPageId(),0);
			DownImageManager.add(downImageItem);
		}
		
		addView(l);
	}
	
	public boolean getIsMoreItem(){
		return isMoreItem;
	}
	
	public LinearLayout addItem(){
		if(!isMoreItem){
			listitem_friend_name.setText(parserListItemFriends.getName().getStr());
			if(parserListItemFriends.getGender().getStr().equalsIgnoreCase("男")){
				listitem_friend_gender.setBackgroundResource(R.drawable.manbtn);
			}
			listitem_friend_addr.setText(parserListItemFriends.getAddr().getStr());
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
	
	public Parser_ListItem_Friends getParserListItemFriends(){
		return parserListItemFriends;
	}

}
