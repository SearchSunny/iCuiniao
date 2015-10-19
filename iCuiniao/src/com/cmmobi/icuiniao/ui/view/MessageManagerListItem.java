/**
 * 
 */
package com.cmmobi.icuiniao.ui.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.DownImageManager;
import com.cmmobi.icuiniao.util.MessageItem;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;

/**
 * @author hw
 *
 */
public class MessageManagerListItem extends LinearLayout{

	private MessageItem item;
	private ImageView img;
	private TextView title;
	private TextView msg;
	private TextView time;
	
	public MessageManagerListItem(Context context,MessageItem item) {
		super(context);
		// TODO Auto-generated constructor stub
		this.item = item;
		LinearLayout l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.listitem_messagemanager,null);
		img = (ImageView)l.findViewById(R.id.listItem_c_icon);
		title = (TextView)l.findViewById(R.id.listItem_c_name);
		time = (TextView)l.findViewById(R.id.listItem_c_time);
		msg = (TextView)l.findViewById(R.id.listItem_c_comment);
		
		
		if(item.getFrom() == UserUtil.userid){
			title.setText("发给"+item.getToName());
		}else{
			title.setText("来自"+item.getFromName());
		}
		if(item.getMsg().startsWith("%!%")){
			msg.setText("好友推荐");
		}else{
			msg.setText(item.getMsg());
		}
		time.setText(item.getTime());
		
		//压入下载队列
		DownImageItem downImageItem = new DownImageItem(Parser_Layout_AbsLayout.MODELTYPE_LISTITEM, URLUtil.URL_GET_USERICON+item.getFrom()+".jpg", 0,0);
		DownImageManager.add(downImageItem);
		
		addView(l);
	}
	
	public void setImageView(byte[] data){
		Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
		if(temp == null)return;
		Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.usericonbg);
		temp = CommonUtil.resizeImage(temp, background.getWidth(), background.getHeight());
		img.setImageBitmap(CommonUtil.mergerIcon(background, temp,7));
	}

}
