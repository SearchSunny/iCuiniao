/**
 * 
 */
package com.cmmobi.icuiniao.ui.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
public class MessageListItem extends LinearLayout{

	private MessageItem item;
	private TextView time;
	private ImageView listItem_c_icon;
	private LinearLayout lmsg;
	private TextView msg;
	private int pushid;//推荐的id
	
	public MessageListItem(Context context,MessageItem item) {
		super(context);
		// TODO Auto-generated constructor stub
		this.item = item;
		LinearLayout l;
		if(item.getFrom() == UserUtil.userid){
			l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.listitem_message_left,null);
		}else{
			l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.listitem_message_right,null);
		}
		time = (TextView)l.findViewById(R.id.time);
		listItem_c_icon = (ImageView)l.findViewById(R.id.listItem_c_icon);
		lmsg = (LinearLayout)l.findViewById(R.id.lmsg);
		msg = (TextView)l.findViewById(R.id.msg);
		
		time.setText(item.getTime());
		pushid = -1;
		//对推荐的文本进行特殊处理
		try {
			String textString = item.getMsg();
			if(item.getMsg().startsWith("%!%")){
				String t1 = textString.substring(textString.indexOf("{")+1,textString.length()-1);
				String[] t2 = t1.split("!");
				textString = "推荐给你一个好友: "+t2[0];
				pushid = Integer.parseInt(t2[1]);
				Spannable WordtoSpan = new SpannableString(textString);
				WordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), 10, WordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				msg.setText(WordtoSpan);
			}else{
				msg.setText(item.getMsg());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		//压入下载队列
		String url = "";
		url = URLUtil.URL_GET_USERICON+item.getFrom()+".jpg";
		DownImageItem downImageItem = new DownImageItem(Parser_Layout_AbsLayout.MODELTYPE_LISTITEM, url, 0,0);
		DownImageManager.add(downImageItem);
		
		addView(l);
	}
	
	public void setImageView(byte[] data){
		Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
		if(temp == null)return;
		Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.usericonbg);
		temp = CommonUtil.resizeImage(temp, background.getWidth(), background.getHeight());
		listItem_c_icon.setImageBitmap(CommonUtil.mergerIcon(background, temp,7));
	}
	
	public int getPushId(){
		return pushid;
	}

}
