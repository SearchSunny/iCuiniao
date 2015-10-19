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
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ListItem_Comment_U;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.DownImageManager;

/**
 * @author hw
 *
 */
public class View_CommentU_Item extends LinearLayout{

	private boolean isMoreItem;//是否为"更多"
	private LinearLayout l;
	private TextView listItem_more;
	private TextView listItem_c_comment;
	private LinearLayout lListitem_c_icon;
	private ImageView listItem_c_icon;
	private TextView listItem_c_name;
	private TextView listItem_c_time;
	private Parser_ListItem_Comment_U parserListItemCommentU;
	
	public View_CommentU_Item(Context context,boolean isMoreitem,Parser_ListItem_Comment_U parserListItemCommentU,int index) {
		super(context);
		// TODO Auto-generated constructor stub
		this.isMoreItem = isMoreitem;
		this.parserListItemCommentU = parserListItemCommentU;
		String url = null;
		if(isMoreitem){
			l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.listitem_comment_c_more,null);
			l.setGravity(Gravity.CENTER_HORIZONTAL);
			listItem_more = (TextView)l.findViewById(R.id.listItem_c_comment);
			listItem_more.setGravity(Gravity.CENTER_HORIZONTAL);
		}else{
			l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.listitem_comment_c,null);
			lListitem_c_icon = (LinearLayout)l.findViewById(R.id.lListItem_c_icon);
			listItem_c_icon = (ImageView)l.findViewById(R.id.listItem_c_icon);
			listItem_c_name = (TextView)l.findViewById(R.id.listItem_c_name);
			listItem_c_time = (TextView)l.findViewById(R.id.listItem_c_time);
			listItem_c_comment = (TextView)l.findViewById(R.id.listItem_c_comment);
			if(parserListItemCommentU.getImage().getSrc()==null||parserListItemCommentU.getImage().getSrc().equalsIgnoreCase("null")||parserListItemCommentU.getImage().getSrc().equalsIgnoreCase("")){
				url = null;
				lListitem_c_icon.setVisibility(GONE);
			}else{
				url = parserListItemCommentU.getImage().getSrc();
			}
			//压入下载队列
			DownImageItem downImageItem = new DownImageItem(Parser_Layout_AbsLayout.MODELTYPE_LISTITEM, index, url, parserListItemCommentU.getPageId(),0);
			DownImageManager.add(downImageItem);
		}
		
		addView(l);
	}
	
	public boolean getIsMoreItem(){
		return isMoreItem;
	}
	
	public LinearLayout addItem(){
		if(!isMoreItem){
			listItem_c_name.setText(parserListItemCommentU.getName().getStr());
			listItem_c_time.setText(parserListItemCommentU.getCommentTime().getStr());
			listItem_c_comment.setText(parserListItemCommentU.getCommentMsg().getStr());
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
		listItem_c_icon.setImageBitmap(CommonUtil.mergerIcon(background, temp,7));
	}
	
	public Parser_ListItem_Comment_U getParserListItemCommentU(){
		return parserListItemCommentU;
	}

}
