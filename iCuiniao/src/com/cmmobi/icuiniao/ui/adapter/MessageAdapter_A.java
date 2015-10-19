/**
 * 
 */
package com.cmmobi.icuiniao.ui.adapter;

import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.activity.UserPageActivityA;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_CommodityInfo;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Image;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ParserEngine;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_TitleBarFloat;
import com.cmmobi.icuiniao.util.AsyncImageLoader_noCache;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.DownImageManagerA;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.AsyncImageLoader_noCache.ImageCallback;
import com.cmmobi.icuiniao.util.connction.HttpThread;
import com.icuiniao.plug.im.Entity;
import com.icuiniao.plug.im.MessageBody;

/**
 * @author hw
 * 
 */
public class MessageAdapter_A extends BaseAdapter {

	private ArrayList<Entity> listItem;

	//private AsyncImageLoader asyncImageLoader;
	private AsyncImageLoader_noCache asyncImageLoaderNoCache;
	private ConnectUtil mConnectUtil;

	private Parser_ParserEngine parserEngine;
	private Parser_Image parserImage;
	private String title;
	private String commodityImageString;
	private String commodityinfoString;
	private Parser_CommodityInfo parserCommodityInfo;
	private DownImageManagerA downImageManagerA;
	
	private Map<Integer, ImageView> mapImgUrl; //商品图片
	private Map<Integer,TextView> mapTitle; //商品标题
	private Map<Integer,TextView> mapDetails; //商品介绍

	final int TYPE_LEFT_C = 0; // 左商品
	final int TYPE_LEFT_MSG = 1; // 左消息
	final int TYPE_RIGHT_C = 2; // 右商品
	final int TYPE_RIGHT_MSG = 3; // 右消息
	private long maxTime = 0; //记录每次时间

	private ArrayList<TempTimeStore> timeStores;
	public void setMaxTime(long maxTime) {
		this.maxTime = maxTime;
	}
	public ArrayList<Entity> getListItem() {
		return listItem;
	}

	public void setListItem(ArrayList<Entity> listItem) {
		this.listItem = listItem;
	}

	private LayoutInflater mInflater;
	private Context context;
	private Handler mHandler;

	private HashMap<String, SoftReference<Drawable>> imageCache;
	String url = "";
	SimpleDateFormat format = new SimpleDateFormat("MM-dd");
	String nowTime=format.format(new Date());
	
	//时间对比
	SimpleDateFormat objFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	String parTime = objFormat.format(new Date());
	
	public MessageAdapter_A(ArrayList<Entity> listItem, Context context,
			Handler mHandler) {
		this.listItem = listItem;
		LogPrint.Print("mess", listItem.size() + "");
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
		this.mHandler = mHandler;

		imageCache = new HashMap<String, SoftReference<Drawable>>();

		asyncImageLoaderNoCache = new AsyncImageLoader_noCache(context);
		downImageManagerA = new DownImageManagerA();
		
		mapImgUrl = new HashMap<Integer, ImageView>();
		mapTitle = new HashMap<Integer, TextView>();
		mapDetails = new HashMap<Integer, TextView>();
		
		timeStores = new ArrayList<TempTimeStore>();
		
		getItemCount();
	}

	@Override
	public int getCount() {
		return listItem.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 4;
	}

	// 每个convert view都会调用此方法，获得当前所需要的view样式
	@Override
	public int getItemViewType(int position) {
		Entity item = listItem.get(position);
		if (item.getFromId() == UserUtil.userid && item.getCid() != 0) {
			return TYPE_RIGHT_C;
		} else if (item.getFromId() == UserUtil.userid && item.getCid() == 0) {
			return TYPE_RIGHT_MSG;
		} else if (item.getFromId() != UserUtil.userid && item.getCid() != 0) {
			return TYPE_LEFT_C;
		} else if (item.getFromId() != UserUtil.userid && item.getCid() == 0) {
			return TYPE_LEFT_MSG;
		}
		return -1;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		final Entity item = listItem.get(position);
		
		int type = getItemViewType(position);

		if (convertView == null) {

			holder = new ViewHolder();

			switch (type) {
			case TYPE_LEFT_C:
				convertView = mInflater.inflate(R.layout.listitem_message_left_a, parent, false);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.cmsg = (TextView) convertView.findViewById(R.id.cmsg);
				holder.msgImage = (ImageView) convertView.findViewById(R.id.msgImage);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				holder.listItem_c_icon = (ImageView) convertView.findViewById(R.id.listItem_c_icon);
				holder.listItem_c_icon.setTag(position);
				holder.ltime = (LinearLayout)convertView.findViewById(R.id.ltime);
				//用户头像
				holder.listItem_m_icon = (ImageView)convertView.findViewById(R.id.listItem_m_icon);
				holder.listItem_m_icon.setTag(position);
				holder.msg = (TextView)convertView.findViewById(R.id.msg);
				
				LogPrint.Print("convertView = ", "NULL TYPE_1");
				
				 //头像点击事件
				 holder.listItem_c_icon.setOnClickListener(new View.OnClickListener() {
				 @Override
				 public void onClick(View v) {
					 Entity item = listItem.get((Integer)v.getTag());
					 final int uid = item.getFromId();			
					 url =  URLUtil.URL_USER_HOMEPAGE + "?oid=" + UserUtil.userid + "&uid=" + uid;				
					 //url = URLUtil.URL_USERPAGE+"?uid="+listItem.get(position).getUserId();
					 Intent intent = new Intent();
					 intent.putExtra("url",url);
					 intent.putExtra("userid",uid);
					 intent.putExtra("nickname",listItem.get(position).getNickName());
					 intent.setClass(context, UserPageActivityA.class);
					 context.startActivity(intent);
				 }
				 });
				 
				//头像点击事件
				 holder.listItem_m_icon.setOnClickListener(new View.OnClickListener() {
				 @Override
				 public void onClick(View v) {
				Entity item = listItem.get((Integer)v.getTag());
			     final int uid = item.getFromId();	 
				 url =  URLUtil.URL_USER_HOMEPAGE + "?oid=" + UserUtil.userid + "&uid=" + uid;				
				 //url = URLUtil.URL_USERPAGE+"?uid="+listItem.get(position).getUserId();
				 Intent intent = new Intent();
				 intent.putExtra("url",url);
				 intent.putExtra("userid",uid);
				 intent.putExtra("nickname",listItem.get(position).getNickName());
				 intent.setClass(context, UserPageActivityA.class);
				 context.startActivity(intent);
				 }
				 });
				 
				 convertView.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							LogPrint.Print("console","商品列点击........" + listItem.get(position).getCid());
							String url = URLUtil.URL_MYPAGE + "?cid=" + listItem.get(position).getCid() + "&dpi="
							+ URLUtil.dpi() + "&pi=0&plaid=" + URLUtil.plaid;
							Message msg = new Message();
							msg.what = 1128;
							msg.obj = url;
							mHandler.sendMessage(msg);
						}
					});
				break;
			case TYPE_LEFT_MSG:
				convertView = mInflater.inflate(R.layout.listitem_message_left,parent, false);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				holder.listItem_c_icon = (ImageView) convertView.findViewById(R.id.listItem_c_icon);
				holder.listItem_c_icon.setTag(position);
				holder.msg = (TextView) convertView.findViewById(R.id.msg);
				holder.ltime = (LinearLayout)convertView.findViewById(R.id.ltime);
				//头像点击事件
				 holder.listItem_c_icon.setOnClickListener(new View.OnClickListener() {
				 @Override
				 public void onClick(View v) {
					 Entity item = listItem.get((Integer)v.getTag());
					 final int uid = item.getFromId();			
					 url =  URLUtil.URL_USER_HOMEPAGE + "?oid=" + UserUtil.userid + "&uid=" + uid;				
					 //url = URLUtil.URL_USERPAGE+"?uid="+listItem.get(position).getUserId();
					 Intent intent = new Intent();
					 intent.putExtra("url",url);
					 intent.putExtra("userid",uid);
					 intent.putExtra("nickname",listItem.get(position).getNickName());
					 intent.setClass(context, UserPageActivityA.class);
					 context.startActivity(intent);
				 }
				 });
				 LogPrint.Print("convertView = ", "NULL TYPE_2");
				break;
			case TYPE_RIGHT_C:
				convertView = mInflater.inflate(R.layout.listitem_message_right_a, parent, false);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.cmsg = (TextView) convertView.findViewById(R.id.cmsg);
				holder.msgImage = (ImageView) convertView.findViewById(R.id.msgImage);
				holder.time = (TextView) convertView.findViewById(R.id.mtime);
				holder.ltime = (LinearLayout)convertView.findViewById(R.id.ltime);
				holder.listItem_c_icon = (ImageView) convertView.findViewById(R.id.listItem_c_icon);
				holder.listItem_c_icon.setTag(position);
				holder.loading = (ProgressBar)convertView.findViewById(R.id.loading);
				holder.listItem_tanhao = (ImageView)convertView.findViewById(R.id.listItem_tanhao);
				holder.critem = (RelativeLayout)convertView.findViewById(R.id.critem);
				holder.msg =  (TextView)convertView.findViewById(R.id.msg);
				holder.listItem_m_icon = (ImageView) convertView.findViewById(R.id.listItem_m_icon);
				holder.listItem_m_icon.setTag(position);
				
				
				
				//头像点击事件
				 holder.listItem_c_icon.setOnClickListener(new View.OnClickListener() {
				 @Override
				 public void onClick(View v) {
					 Entity item = listItem.get((Integer)v.getTag());
					 final int uid = item.getUserId();			
					 url =  URLUtil.URL_USER_HOMEPAGE + "?oid=" + UserUtil.userid + "&uid=" + uid;				
					 //url = URLUtil.URL_USERPAGE+"?uid="+listItem.get(position).getUserId();
					 Intent intent = new Intent();
					 intent.putExtra("url",url);
					 intent.putExtra("userid",uid);
					 intent.putExtra("nickname",listItem.get(position).getNickName());
					 intent.setClass(context, UserPageActivityA.class);
					 context.startActivity(intent);
				 }
				 });
				 
				//头像点击事件
				 holder.listItem_m_icon.setOnClickListener(new View.OnClickListener() {
				 @Override
				 public void onClick(View v) {
					 Entity item = listItem.get((Integer)v.getTag());
					 final int uid = item.getUserId();	
					 url =  URLUtil.URL_USER_HOMEPAGE + "?oid=" + UserUtil.userid + "&uid=" + uid;				
					 //url = URLUtil.URL_USERPAGE+"?uid="+listItem.get(position).getUserId();
					 Intent intent = new Intent();
					 intent.putExtra("url",url);
					 intent.putExtra("userid",uid);
					 intent.putExtra("nickname",listItem.get(position).getNickName());
					 intent.setClass(context, UserPageActivityA.class);
					 context.startActivity(intent);
				 }
				 });
				 
				 convertView.findViewById(R.id.oneRelative) .setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							LogPrint.Print("console","商品列点击........" + listItem.get(position).getCid());
							String url = URLUtil.URL_MYPAGE + "?cid=" + listItem.get(position).getCid() + "&dpi="
							+ URLUtil.dpi() + "&pi=0&plaid=" + URLUtil.plaid;
							Message msg = new Message();
							msg.what = 1128;
							msg.obj = url;
							mHandler.sendMessage(msg);
						}
					});
				 LogPrint.Print("convertView = ", "NULL TYPE_3");
				break;
			case TYPE_RIGHT_MSG:
				convertView = mInflater.inflate(R.layout.listitem_message_right, parent, false);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				holder.listItem_tanhao = (ImageView) convertView.findViewById(R.id.listItem_tanhao);
				holder.loading = (ProgressBar) convertView.findViewById(R.id.loading);
				holder.msg = (TextView) convertView.findViewById(R.id.msg);
				holder.ltime = (LinearLayout)convertView.findViewById(R.id.ltime);
				
				holder.listItem_c_icon = (ImageView) convertView.findViewById(R.id.listItem_c_icon);
				holder.linearMsg = (RelativeLayout)convertView.findViewById(R.id.ritem);
				holder.listItem_c_icon.setTag(position);
				//头像点击事件
				 holder.listItem_c_icon.setOnClickListener(new View.OnClickListener() {
				 @Override
				 public void onClick(View v) {
					 Entity item = listItem.get((Integer)v.getTag());
					 final int uid = item.getFromId();			
					 url =  URLUtil.URL_USER_HOMEPAGE + "?oid=" + UserUtil.userid + "&uid=" + uid;				
					 //url = URLUtil.URL_USERPAGE+"?uid="+listItem.get(position).getUserId();
					 Intent intent = new Intent();
					 intent.putExtra("url",url);
					 intent.putExtra("userid",uid);
					 intent.putExtra("nickname",listItem.get(position).getNickName());
					 intent.setClass(context, UserPageActivityA.class);
					 context.startActivity(intent);
				 }
				 });
				 LogPrint.Print("convertView = ", "NULL TYPE_3");
				break;
			}

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
			if(holder.ltime.getVisibility() == View.VISIBLE){
				
				holder.ltime.setVisibility(View.GONE);
			}
			LogPrint.Print("mess", "2 position = " + position);
		}

		//处理时间
		 StringBuffer commenttime = new StringBuffer(item.getCommenttime().substring(4,12));
					
		 String day = commenttime.substring(0,2)+"-"+commenttime.substring(2,4).toString();
		 String time = commenttime.substring(4,6)+":"+commenttime.substring(6,8).toString();
		 
		switch (type) {
		
		case TYPE_LEFT_C:
			
			// 获取商品ID
			int cid = item.getCid();
			mapImgUrl.put(position, holder.msgImage);
			mapTitle.put(position, holder.title);
			mapDetails.put(position, holder.cmsg);
			initContent(cid, position);

			if (item.getMessageBodies() != null) {

				ArrayList<MessageBody> messageBodys = item.getMessageBodies();
				for (MessageBody messageBody : messageBodys) {
					LogPrint.Print("console", "消息内容=====" + messageBody.getMessage());

					holder.msg.setText(messageBody.getMessage());
				}
			}
			
			String timeLeft_c = timeContrast(position, timeStores);
			if(position == 0){
				holder.ltime.setVisibility(View.VISIBLE);
				holder.time.setVisibility(View.VISIBLE);
				if(day.equals(nowTime)){
					
					holder.time.setText("今天  " + time);
				}else{
					
					holder.time.setText(day+" "+time);
				}
				
			}
			if(!timeLeft_c.equals("")){
				LogPrint.Print("mess","timeString//////////////////////////");
				holder.ltime.setVisibility(View.VISIBLE);
				holder.time.setVisibility(View.VISIBLE);
				String tempSplit[] =  timeLeft_c.split(" ");
				if(position == 0){
					if(tempSplit[0].equals(nowTime)){
						
						holder.time.setText("今天  " + tempSplit[1]);
					}else{
						
						holder.time.setText(timeLeft_c);
					}
					
				}else{
					
					holder.time.setText(timeLeft_c);
				}
			}
			
			/*long tempTime =  timeContrast(item.getCommenttime(),itemNext.getCommenttime());
			
			if(tempTime < 30 && position != 0){
				//时间控件隐藏
				holder.ltime.setVisibility(View.GONE);
				holder.time.setVisibility(View.GONE);
				
			}else if(tempTime < 30 && position == 0){
				holder.ltime.setVisibility(View.VISIBLE);
				holder.time.setVisibility(View.VISIBLE);
				
				if (day.equals(nowTime)) {

					holder.time.setText("今天  " + time);

				} else {

					holder.time.setText(day + " " + time);
				}
				
			}else if(tempTime > 30){
				
				holder.ltime.setVisibility(View.VISIBLE);
				holder.time.setVisibility(View.VISIBLE);
				
				if (day.equals(nowTime)) {

					holder.time.setText("今天  " + time);

				} else {

					holder.time.setText(day + " " + time);
				}
			}*/
			 
			 url = URLUtil.URL_GET_USERICON+item.getFromId()+".jpg";
			 loadUserImage(url, holder.listItem_c_icon);
			 loadUserImage(url, holder.listItem_m_icon);
			
			break;
		case TYPE_RIGHT_C:
			// 获取商品ID
			int ccid = item.getCid();
			mapImgUrl.put(position, holder.msgImage);
			mapTitle.put(position, holder.title);
			mapDetails.put(position, holder.cmsg);
			initContent(ccid, position);

			if (item.getMessageBodies() != null) {

				ArrayList<MessageBody> messageBodys = item.getMessageBodies();
				for (MessageBody messageBody : messageBodys) {
					LogPrint.Print("console", "消息内容=====" + messageBody.getMessage());

					holder.msg.setText(messageBody.getMessage());
				}
			}
			String timeRight_c = timeContrast(position, timeStores);
			if(position == 0){
				holder.ltime.setVisibility(View.VISIBLE);
				holder.time.setVisibility(View.VISIBLE);
				if(day.equals(nowTime)){
					
					holder.time.setText("今天  " + time);
				}else{
					
					holder.time.setText(day+" "+time);
				}
				
			}
			if(!timeRight_c.equals("")){
				LogPrint.Print("mess","timeString//////////////////////////");
				holder.ltime.setVisibility(View.VISIBLE);
				holder.time.setVisibility(View.VISIBLE);
				String tempSplit[] =  timeRight_c.split(" ");
				if(position == 0){
					if(tempSplit[0].equals(nowTime)){
						
						holder.time.setText("今天  " + tempSplit[1]);
					}else{
						
						holder.time.setText(timeRight_c);
					}
					
				}else{
					
					holder.time.setText(timeRight_c);
				}
			}
			/*long tempTimeb =  timeContrast(item.getCommenttime(),itemNext.getCommenttime());
			
			if(tempTimeb < 30 && position != 0){
				//时间控件隐藏
				holder.ltime.setVisibility(View.GONE);
				holder.time.setVisibility(View.GONE);
				
			}else if(tempTimeb < 30 && position == 0){
				holder.ltime.setVisibility(View.VISIBLE);
				holder.time.setVisibility(View.VISIBLE);
				
				if (day.equals(nowTime)) {

					holder.time.setText("今天  " + time);

				} else {

					holder.time.setText(day + " " + time);
				}
				
			}else if(tempTimeb > 30){
				
				holder.ltime.setVisibility(View.VISIBLE);
				holder.time.setVisibility(View.VISIBLE);
				
				if (day.equals(nowTime)) {

					holder.time.setText("今天  " + time);

				} else {

					holder.time.setText(day + " " + time);
				}
			}*/
			 
			 url = URLUtil.URL_GET_USERICON+item.getUserId()+".jpg";
			 loadUserImage(url, holder.listItem_c_icon);
			 loadUserImage(url, holder.listItem_m_icon);
			 holder.critem.setOnLongClickListener(new View.OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						Message msg = new Message();
						msg.what = 1120;
						msg.arg1 = position;
						mHandler.sendMessage(msg);
						return false;
					}
				});
			 
			// 状态发送中
				if (item.getSendState() == 1) {

					holder.loading.setVisibility(View.VISIBLE);
					holder.listItem_tanhao.setVisibility(View.GONE);
				}
				// 状态为发送成功
				else if (item.getSendState() == 2) {
					holder.loading.setVisibility(View.GONE);
					holder.listItem_tanhao.setVisibility(View.GONE);
				}
				// 状态为发送失败
				else if (item.getSendState() == 3) {
					holder.loading.setVisibility(View.GONE);
					holder.listItem_tanhao.setVisibility(View.VISIBLE);
				}
			 
			break;
		case TYPE_LEFT_MSG:
			String timeLeft_msg = timeContrast(position, timeStores);
			if(position == 0){
				holder.ltime.setVisibility(View.VISIBLE);
				holder.time.setVisibility(View.VISIBLE);
				if(day.equals(nowTime)){
					
					holder.time.setText("今天  " + time);
				}else{
					
					holder.time.setText(day+" "+time);
				}
				
			}
			if(!timeLeft_msg.equals("")){
				LogPrint.Print("mess","timeString//////////////////////////");
				holder.ltime.setVisibility(View.VISIBLE);
				holder.time.setVisibility(View.VISIBLE);
				String tempSplit[] =  timeLeft_msg.split(" ");
				if(position == 0){
					if(tempSplit[0].equals(nowTime)){
						
						holder.time.setText("今天  " + tempSplit[1]);
					}else{
						
						holder.time.setText(timeLeft_msg);
					}
					
				}else{
					
					holder.time.setText(timeLeft_msg);
				}
			}
			/*long minute =  timeContrast(item.getCommenttime(),itemNext.getCommenttime());
			if(minute < 30 && position != 0){
				//时间控件隐藏
				holder.ltime.setVisibility(View.GONE);
				holder.time.setVisibility(View.GONE);
				
			}else if(minute < 30 && position == 0){
				holder.ltime.setVisibility(View.VISIBLE);
				holder.time.setVisibility(View.VISIBLE);
				
				if (day.equals(nowTime)) {

					holder.time.setText("今天  " + time);

				} else {

					holder.time.setText(day + " " + time);
				}
				
			}else if(minute > 30){
				
				holder.ltime.setVisibility(View.VISIBLE);
				holder.time.setVisibility(View.VISIBLE);
				
				if(position == 0){
					
					holder.time.setText("今天  " + time);
				}else {
					holder.time.setText(day + " " + time);
				}
				
			}*/
			url = URLUtil.URL_GET_USERICON+item.getFromId()+".jpg";
			 loadUserImage(url, holder.listItem_c_icon);
			if (item.getMessageBodies() != null) {

				ArrayList<MessageBody> messageBodys = item.getMessageBodies();
				for (MessageBody messageBody : messageBodys) {
					LogPrint.Print("console", "消息内容=====" + messageBody.getMessage());

					holder.msg.setText(messageBody.getMessage());
				}
			}
			break;
		case TYPE_RIGHT_MSG:
			LogPrint.Print("mess", "position===="+position);
			
			String timeRight_msg = timeContrast(position, timeStores);
			if(position == 0){
				holder.ltime.setVisibility(View.VISIBLE);
				holder.time.setVisibility(View.VISIBLE);
				if(day.equals(nowTime)){
					
					holder.time.setText("今天  " + time);
				}else{
					
					holder.time.setText(day+" "+time);
				}
				
			}
			if(!timeRight_msg.equals("")){
				LogPrint.Print("mess","timeString//////////////////////////");
				holder.ltime.setVisibility(View.VISIBLE);
				holder.time.setVisibility(View.VISIBLE);
				String tempSplit[] =  timeRight_msg.split(" ");
				if(position == 0){
					if(tempSplit[0].equals(nowTime)){
						
						holder.time.setText("今天  " + tempSplit[1]);
					}else{
						
						holder.time.setText(timeRight_msg);
					}
					
				}else{
					
					holder.time.setText(timeRight_msg);
				}
			}
			url = URLUtil.URL_GET_USERICON+item.getFromId()+".jpg";
			 loadUserImage(url, holder.listItem_c_icon);
			if (item.getMessageBodies() != null) {

				ArrayList<MessageBody> messageBodys = item.getMessageBodies();
				for (MessageBody messageBody : messageBodys) {
					LogPrint.Print("console", "消息内容=====" + messageBody.getMessage());

					holder.msg.setText(messageBody.getMessage());
				}
			}
			
			holder.linearMsg.setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					Message msg = new Message();
					msg.what = 1120;
					msg.arg1 = position;
					mHandler.sendMessage(msg);
					return false;
				}
			});
			// 状态发送中
			if (item.getSendState() == 1) {

				holder.loading.setVisibility(View.VISIBLE);
				holder.listItem_tanhao.setVisibility(View.GONE);
			}
			// 状态为发送成功
			else if (item.getSendState() == 2) {
				holder.loading.setVisibility(View.GONE);
				holder.listItem_tanhao.setVisibility(View.GONE);
			}
			// 状态为发送失败
			else if (item.getSendState() == 3) {
				holder.loading.setVisibility(View.GONE);
				holder.listItem_tanhao.setVisibility(View.VISIBLE);
			}

			break;
		}

		return convertView;
	}


	public final class ViewHolder {

		private TextView time;
		private ImageView listItem_c_icon;
		private ImageView listItem_m_icon;
		private RelativeLayout critem;
		private TextView msg;
		private ImageView listItem_tanhao;
		private ProgressBar loading;
		private LinearLayout ltime;
		// 商品标题
		private TextView title;
		// 商品描述
		private TextView cmsg;
		// 商品图片
		private ImageView msgImage;
		private RelativeLayout linearMsg;

	}

	// 引入线程池，并引入内存缓存功能,并对外部调用封装了接口，简化调用过程
	private void loadImage(final String url, final ImageView imageView) {
		// 如果缓存过就会从缓存中取出图像，ImageCallback接口中方法也不会被执行
		Drawable cacheImage = asyncImageLoaderNoCache.loadDrawable(url,
				new ImageCallback() {
					// 请参见实现：如果第一次加载url时下面方法会执行
					public void imageLoaded(Drawable imageDrawable) {

						imageView.setImageBitmap(getCoverBitmap(mergeImg(imageDrawable)));
					}
				});
		if (cacheImage != null) {
			imageView.setImageBitmap(getCoverBitmap(mergeImg(cacheImage)));
		}
	}
	// 引入线程池，并引入内存缓存功能,并对外部调用封装了接口，简化调用过程
	private void loadUserImage(final String url, final ImageView imageView) {
		// 如果缓存过就会从缓存中取出图像，ImageCallback接口中方法也不会被执行
		Drawable cacheImage = asyncImageLoaderNoCache.loadDrawable(url,
				new ImageCallback() {
					// 请参见实现：如果第一次加载url时下面方法会执行
					public void imageLoaded(Drawable imageDrawable) {

						imageView.setImageBitmap(mergeImg(imageDrawable));
					}
				});
		if (cacheImage != null) {
			imageView.setImageBitmap(getCoverBitmap(mergeImg(cacheImage)));
		}
	}

	private Bitmap mergeImg(Drawable drawable) {
		if (drawable == null) {
			return null;
		}
		Bitmap background = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.usericonbg);
		BitmapDrawable bd = (BitmapDrawable) drawable;
		Bitmap bm = bd.getBitmap();
		bm = CommonUtil.resizeImage(bm, background.getWidth(), background
				.getHeight());
		bm = CommonUtil.mergerIcon(background, bm, 7);
		return bm;
	}

	/**
	 * 通知该对象已经变化，重新刷新列表
	 */
	@Override
	public void notifyDataSetChanged() {
		getItemCount();
		super.notifyDataSetChanged();
	}

	// 初始化连接
	private void initContent(int cid, int posistion) {

		String url = URLUtil.URL_MYPAGE + "?cid=" + cid + "&dpi="
				+ URLUtil.dpi() + "&pi=0&plaid=" + URLUtil.plaid;

		mConnectUtil = new ConnectUtil(context, mContentHandler, 0);

		mConnectUtil.connect(url, HttpThread.TYPE_PAGE, posistion);
	}

	public void setImageView(ImageView img, byte[] data) {
		Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
		if (temp == null)
			return;
		img.setImageBitmap(temp);
	}

	public void setTitleTextView(TextView textView,String data){
		
		textView.setText(data);
		
	}
	
	public void setDetailsTextView(TextView textView,String data){
		
		textView.setText(data);
		
	}
	private Handler mContentHandler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				switch (msg.getData().getInt("mType")) {
				case HttpThread.TYPE_PAGE:
					int pos = msg.arg1;
					buildPage(msg, 0, pos);
					break;
				case HttpThread.TYPE_IMAGE:
					LogPrint.Print("mess", "index =" + msg.arg1);
					ImageView img = mapImgUrl.get(msg.arg1);
					setImageView(img, (byte[]) msg.obj);
					break;

				default:
					break;
				}

				break;
			case 113367:
				DownImageItem item = downImageManagerA.get();
				if (item == null) {
					break;
				}
				String urlString = item.getUrl();
				if (urlString != null) {
					new ConnectUtil(context, mContentHandler, 0).connect(
							urlString, HttpThread.TYPE_IMAGE, item.getIndex());
				}
				mHandler.sendEmptyMessageDelayed(11367, 100);
				//LogPrint.Print("mess", "113367");
				break;
			}

		};
	};

	private void buildPage(Message msg, int type, int pos) {
		if (type == HttpThread.TYPE_PAGE) {
			parserEngine = new Parser_ParserEngine(context);
			parserEngine.parser((byte[]) msg.obj);
			Parser_Layout_AbsLayout[] tmpAbsLayouts = parserEngine.getLayouts();
			for (int i = 0; tmpAbsLayouts != null && i < tmpAbsLayouts.length; i++) {
				if (tmpAbsLayouts[i].getModelType() == Parser_Layout_AbsLayout.MODELTYPE_TITLEBARFLOAT) {
					Parser_TitleBarFloat parserTitleBarFloat = (Parser_TitleBarFloat) tmpAbsLayouts[i];
					// 商品标题
					title = parserTitleBarFloat.getStr();
					TextView textTitle = mapTitle.get(pos);
					setTitleTextView(textTitle, title);
					
				} else if (tmpAbsLayouts[i].getModelType() == Parser_Layout_AbsLayout.MODELTYPE_IMAGE) {
					Parser_Image parserImage = (Parser_Image) tmpAbsLayouts[i];
					// 图片地址
					String commodityImageString = parserImage.getSrc();
					LogPrint.Print("mess", "commodityImageString ="+ commodityImageString);
					// 压入下载队列
					if (commodityImageString != null) {
						
						loadImage(commodityImageString, mapImgUrl.get(pos));
					}
					// mContentHandler.sendEmptyMessage(113367);
				} else if (tmpAbsLayouts[i].getModelType() == Parser_Layout_AbsLayout.MODELTYPE_COMMODITYINFO) {
					parserCommodityInfo = (Parser_CommodityInfo) tmpAbsLayouts[i];
					// 商品介绍
					commodityinfoString = parserCommodityInfo.getCommodityInfo().getStr();
					
					TextView textDetails = mapDetails.get(pos);
					setDetailsTextView(textDetails, commodityinfoString);
					
					
				}
			}
		}

	}
	
	/**
	 * 图片加边框
	 * @param src
	 * @return
	 */
	private Bitmap getCoverBitmap(Bitmap src){
		
		Bitmap bmp = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);
    	Canvas canvas = new Canvas();
    	Paint paint = new Paint();
    
    	canvas.setBitmap(bmp);
    	canvas.drawBitmap(src, 0, 0, null);
    	//绘制边框
    	paint.setColor(0xffacacac);
    	paint.setStyle(Style.STROKE);
    	canvas.drawRect(0,0, src.getWidth()-1, src.getHeight()-1, paint);
    	canvas.save();
    	return bmp;
	}
	/**
	 * 此类用于时间存储
	 * @author XP
	 *
	 */
	class TempTimeStore{
		
		int id; //索引
		String time; //日期字符
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
	}
	/**
	 * 循环对比时间
	 */
	public void getItemCount(){
		LogPrint.Print("mess", "getItemCount()==============================");
		timeStores.clear();
		if(listItem.size() != 0){
			if(listItem.size() == 1){
				TempTimeStore timeStore = new TempTimeStore();
				timeStore.setId(0);
				timeStore.setTime(listItem.get(0).getCommenttime());
				timeStores.add(timeStore);
				
			}else {
				for (int i = 0; i < listItem.size();) {
					if (i != listItem.size()-1) {
						for (int j = i; j < listItem.size()-1; j++) {
							long time = timeContrast(listItem.get(i).getCommenttime(),listItem.get(j+1).getCommenttime(),listItem.get(j+1).getSendState());
							if(time >= 30){
								TempTimeStore timeStore = new TempTimeStore();
								i = j+1;
								timeStore.setId(j+1);
								timeStore.setTime(listItem.get(j+1).getCommenttime());
								timeStores.add(timeStore);
								break;
							}
							//是否循环到最后一个
							if((j+1) == listItem.size()-1){
								i++;
								break;
							}
						}
						
					}else{
						break;
					}
				}
				
			}
			
			LogPrint.Print("mess","timeStoresSize====="+timeStores.size());
		}
	}
	
	/**
	 * 日期对比
	 * @param time1 数据库查出的信息时间
	 * @param time2 下一条数据信息时间
	 * @return
	 */
	private long timeContrast(String time1,String time2,int sendState){
		//时间对比
		SimpleDateFormat objFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		if(sendState == 2){
			
			try {
				Date d1 = objFormat.parse(time2);
				Date d2 = objFormat.parse(time1);
				long d3 = d1.getTime() - d2.getTime();
				long minute = d3 / (1000 * 60);
				return minute;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return 0;
		
	}
	/**
	 * 返回时间
	 * @param id
	 * @param times
	 * @return
	 */
	private String timeContrast(long id,ArrayList<TempTimeStore> times){
		
		String teString = "";
		for (int i = 0; i < times.size(); i++) {
		     LogPrint.Print("mess", "timeID====="+times.get(i).getId());  	
			 if(id == times.get(i).getId()){
				 String tempTimeString = 	times.get(i).getTime();
				  StringBuffer commenttime = new StringBuffer(tempTimeString.substring(4,12));
					
				  String day = commenttime.substring(0,2)+"-"+commenttime.substring(2,4).toString();
			      String time = commenttime.substring(4,6)+":"+commenttime.substring(6,8).toString();
			      teString = day +" "+time;
			      break;
			 }	
			  
			
		}
		return teString;
	}

}
