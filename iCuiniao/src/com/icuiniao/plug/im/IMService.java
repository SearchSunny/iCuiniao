/**
 * 
 */
package com.icuiniao.plug.im;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.MessageManagerActivityA;
import com.cmmobi.icuiniao.entity.Friend;
import com.cmmobi.icuiniao.store.DBFriendList;
import com.cmmobi.icuiniao.util.ActionID;
import com.cmmobi.icuiniao.util.CommentUtils;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.LogoutUtil;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineTimeService;
import com.cmmobi.icuiniao.util.PinyinUtils;
import com.cmmobi.icuiniao.util.TimerForRock;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;
import com.icuiniao.plug.localmessage.LocalMessageService;

/**
 * @author hw
 *消息服务
 */
public class IMService extends Service{

	private IMConnecterA imConnecter;//连接
	
	//正式外网
//	private final static String host = "125.39.224.103";
//	private final static int port = 7771;
	
	//测试
	private final static String host = "125.39.224.103";
	private final static int port = 7700;
	
	//王建测试
//	private final static String host = "192.168.1.172";
//	private final static int port = 7700;

	private boolean isRegeditd;//是否已经注册过广播
	private long[] heartInterval;//心跳发送间隔
	private Runnable heartRunnable;
	private Runnable heartExceptionRunnable;
	private Handler heartHandler;
	
	private Runnable connectRunnable;
	private Handler connectHandler;
	private IMDataBase imDataBase;
	
	//用户id
	private int who;
	//接收者昵称
	private String toname;
	//消息状态 - 首次接收消息为未读
	private int isread;
	//发送状态 - 默认
	private int sendstate;
	//消息类型 - 好友消息
	private int type;
	//产品ID
	private int cid;
	private Runnable commRunnable;
	private Handler commHandler;
	private boolean isServiceStart;//逻辑是否已经启动过了
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		LogPrint.Print("im","==========IMService create==========");
		doing();
		isServiceStart = true;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LogPrint.Print("im","==========IMService destroy==========");
		if(heartHandler != null&&heartRunnable != null){
			heartHandler.removeCallbacks(heartRunnable);
		}
		if(heartHandler != null&&heartExceptionRunnable != null){
			heartHandler.removeCallbacks(heartExceptionRunnable);
		}
		closeSocket();
		unRegisterReceiver();
		isRegeditd = false;
		if(commHandler != null){
			commHandler.removeCallbacks(commRunnable);
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		LogPrint.Print("im","==========IMService start==========");
		
		if(isServiceStart == false){
			doing();
		}
		
	}
	
	private void doing(){
		UserUtil.userid = CommonUtil.getUserId(this);
		UserUtil.userState = CommonUtil.getUserState(this);
		if(imConnecter == null){
			LogPrint.Print("im","==========init IMConnecter==========");
			if(heartHandler != null&&heartRunnable != null){
				heartHandler.removeCallbacks(heartRunnable);
			}
			if(heartHandler != null&&heartExceptionRunnable != null){
				heartHandler.removeCallbacks(heartExceptionRunnable);
			}
			connect();
		}else{
			if(imConnecter.isClosed()||!imConnecter.isConnected()){
				LogPrint.Print("im","==========reinit IMConnecter==========");
				if(heartHandler != null&&heartRunnable != null){
					heartHandler.removeCallbacks(heartRunnable);
				}
				if(heartHandler != null&&heartExceptionRunnable != null){
					heartHandler.removeCallbacks(heartExceptionRunnable);
				}
				closeSocket();
				connect();
			}
		}
		if(!isRegeditd){
			registerReceiver();
			isRegeditd = true;
		}
		LogPrint.Print("im", "2userState =" + UserUtil.userState);
		getCommentCnt();
		getCommentNow(0);
	}
	
	//心跳异常逻辑
	private void keepHeartError(long time){
		if(heartExceptionRunnable == null){//心跳异常
			heartExceptionRunnable = new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					LogPrint.Print("im","---心跳中断---");
					if(heartHandler != null&&heartRunnable != null){
						heartHandler.removeCallbacks(heartRunnable);
					}
					closeSocket();
					imConnecter = null;
					delayConnect(15);
				}
			};
		}
		
		if(heartHandler == null){
			heartHandler = new Handler();
    	}
		heartHandler.removeCallbacks(heartExceptionRunnable);
		heartHandler.postDelayed(heartExceptionRunnable, time*1000*3);//3次心跳时间内无返回则重连。
	}
	
	//心跳维持
	private void keepHeart(long time){
		if(heartRunnable == null){//心跳维持
			heartRunnable = new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					//发送心跳
			    	Entity pe = new Entity();
			    	pe.setTag((short)201);
			    	write(pe);
			    	//检测另外两个服务是否运行，守护
			    	if(CommonUtil.isServiceRunning(IMService.this, "com.icuiniao.plug.localmessage.LocalMessageService") == false){
			    		startService(new Intent(IMService.this, LocalMessageService.class));
			    	}
			    	if(CommonUtil.isServiceRunning(IMService.this, "com.cmmobi.icuiniao.util.OfflineTimeService") == false){
			    		startService(new Intent(IMService.this, OfflineTimeService.class));
			    	}
				}
			};
		}
		if(heartHandler == null){
			heartHandler = new Handler();
    	}
		heartHandler.removeCallbacks(heartRunnable);
		heartHandler.postDelayed(heartRunnable, time*1000);
		
		if(heartHandler != null&&heartExceptionRunnable != null){
			heartHandler.removeCallbacks(heartExceptionRunnable);
			heartHandler.postDelayed(heartExceptionRunnable, time*1000*3);//3次心跳时间内无返回则重连。
		}
	}

	private BroadcastReceiver mGestuReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			imDataBase = new IMDataBase(context);
			if(ActionID.ACTION_BROADCAST_CONNECT_SUCCESS_RESPONSE.equals(action)){//服务器回应客户端允许建立连接成功，tag:3
				imConnecter.setIsConnectSuccess(true);
				String[] HeartStarttime = intent.getExtras().getStringArray("heartstarttime");
		    	String[] HeartEndtime = intent.getExtras().getStringArray("heartendtime");
		    	heartInterval = intent.getExtras().getLongArray("heartinterval");
		    	String currentTime = intent.getExtras().getString("currenttime");
		    	LogPrint.Print("im","currentTime = "+currentTime);
		    	for(int i = 0;i < HeartStarttime.length;i ++){
		    		LogPrint.Print("im","HeartStarttime["+i+"] = "+HeartStarttime[i]);
		    		LogPrint.Print("im","HeartEndtime["+i+"] = "+HeartEndtime[i]);
		    		LogPrint.Print("im","heartInterval["+i+"] = "+heartInterval[i]);
		    	}
		    	//发送心跳
		    	Entity pe = new Entity();
		    	pe.setTag((short)201);
		    	write(pe);
		    	keepHeartError(heartInterval[0]);//心跳异常监听
			}else if(ActionID.ACTION_BROADCAST_CONNECT_REFUSE_RESPONSE.equals(action)){//服务器回应客户端拒绝建立连接，tag:4
				int refuseTag = intent.getExtras().getInt("refusetag");
				LogPrint.Print("im","refuseTag = "+refuseTag);
				closeSocket();
				imConnecter = null;
				delayConnect(15);
			}else if(ActionID.ACTION_BROADCAST_CONNECT_FAIL_RESPONSE.equals(action)){//服务器回应客户端建立连接失败，tag:5
				int refuseTag = intent.getExtras().getInt("refusetag");
				LogPrint.Print("im","refuseTag = "+refuseTag);
				closeSocket();
				imConnecter = null;
				delayConnect(15);
			}else if(ActionID.ACTION_BROADCAST_HEART_RESPONSE.equals(action)){//服务器回应心跳请求，tag:202
				int heartResponse = intent.getExtras().getInt("heartResponse");
				LogPrint.Print("im","heartResponse = "+heartResponse);
				if(imConnecter != null){
					imConnecter.printSession();
				}
				if(heartResponse == 0){
					keepHeart(heartInterval[0]);//心跳监听
				}
			}else if(ActionID.ACTION_BROADCAST_SOCKET_IOERROR.equals(action)){//io异常
				closeSocket();
				imConnecter = null;
				Intent intent1 = new Intent(ActionID.ACTION_BROADCAST_RESPONSE_SENDMESSAGE_FAIL);
				intent1.putExtra("code", 2);
				sendBroadcast(intent1);
			}else if(ActionID.ACTION_BROADCAST_RESPONSE_SENDMESSAGE_FAIL.equals(action)){//失败
				closeSocket();
				imConnecter = null;
				delayConnect(15);
			}else if(ActionID.ACTION_BROADCAST_REQUST_SEND_MESSAGE.equals(action)){//发送消息
				int revicerUserId = intent.getExtras().getInt("revicerUserId");
				String nickname = intent.getExtras().getString("nickname");
				String remarks = intent.getExtras().getString("remarks");
				byte repetSend = intent.getExtras().getByte("repetSend");
				long messageId = intent.getExtras().getLong("messageId");
				String message = intent.getExtras().getString("message");
				String commenttime = createCommentTime();
				LogPrint.Print("im","revicerUserId = "+revicerUserId);
				LogPrint.Print("im","nickname = "+nickname);
				LogPrint.Print("im","remarks = "+remarks);
				LogPrint.Print("im","repetSend = "+repetSend);
				LogPrint.Print("im","messageId = "+messageId);
				LogPrint.Print("im","message = "+message);
				LogPrint.Print("im","commenttime = "+commenttime);
				Entity pe = new Entity();
				pe.setTag((short)205);
				pe.setRevicerUserId(revicerUserId);
				pe.setNickName(nickname);
				pe.setRemarks(remarks);
				pe.setRepetSend(repetSend);
				pe.setTempMessageId(messageId);
				pe.setCommenttime(commenttime);
				MessageBody messageBody = new MessageBody();
				messageBody.setActionType(0);
				messageBody.setMessage(message);
				pe.setMessageBodies(messageBody);
				write(pe);
			}else if(ActionID.ACTION_BROADCAST_REQUST_SEND_PRIVATEMESSAGE.equals(action)){//发送私信
				int revicerUserId = intent.getExtras().getInt("revicerUserId");
				String nickname = intent.getExtras().getString("nickname");
				String remarks = intent.getExtras().getString("remarks");
				byte repetSend = intent.getExtras().getByte("repetSend");
				long messageId = intent.getExtras().getLong("messageId");
				String message = intent.getExtras().getString("message");
				String commenttime = createCommentTime();
				int cid = intent.getExtras().getInt("cid");
				LogPrint.Print("im","revicerUserId = "+revicerUserId);
				LogPrint.Print("im","nickname = "+nickname);
				LogPrint.Print("im","remarks = "+remarks);
				LogPrint.Print("im","repetSend = "+repetSend);
				LogPrint.Print("im","messageId = "+messageId);
				LogPrint.Print("im","message = "+message);
				LogPrint.Print("im","commenttime = "+commenttime);
				LogPrint.Print("im","cid = "+cid);
				Entity pe = new Entity();
				pe.setTag((short)207);
				pe.setRevicerUserId(revicerUserId);
				pe.setNickName(nickname);
				pe.setRemarks(remarks);
				pe.setRepetSend(repetSend);
				pe.setTempMessageId(messageId);
				pe.setCommenttime(commenttime);
				pe.setCid(cid);
				MessageBody messageBody = new MessageBody();
				messageBody.setActionType(0);
				messageBody.setMessage(message);
				pe.setMessageBodies(messageBody);
				write(pe);
			}else if(ActionID.ACTION_BROADCAST_REQUST_SEND_FRIENDMESSAGE.equals(action)){//发送申请建立好友关系的消息
				int revicerUserId = intent.getExtras().getInt("revicerUserId");
				String nickname = intent.getExtras().getString("nickname");
				String remarks = intent.getExtras().getString("remarks");
				byte repetSend = intent.getExtras().getByte("repetSend");
				long messageId = intent.getExtras().getLong("messageId");
				String message = intent.getExtras().getString("message");
				String commenttime = createCommentTime();
				LogPrint.Print("im","revicerUserId = "+revicerUserId);
				LogPrint.Print("im","nickname = "+nickname);
				LogPrint.Print("im","remarks = "+remarks);
				LogPrint.Print("im","repetSend = "+repetSend);
				LogPrint.Print("im","messageId = "+messageId);
				LogPrint.Print("im","message = "+message);
				LogPrint.Print("im","commenttime = "+commenttime);
				Entity pe = new Entity();
				pe.setTag((short)209);
				pe.setRevicerUserId(revicerUserId);
				pe.setNickName(nickname);
				pe.setRemarks(remarks);
				pe.setRepetSend(repetSend);
				pe.setTempMessageId(messageId);
				pe.setCommenttime(commenttime);
				MessageBody messageBody = new MessageBody();
				messageBody.setActionType(0);
				messageBody.setMessage(message);
				pe.setMessageBodies(messageBody);
				write(pe);
			}else if(ActionID.ACTION_BROADCAST_REQUST_SEND_FRIENDMESSAGE_SUCCESS.equals(action)){//发送确认建立好友关系的消息
				int revicerUserId = intent.getExtras().getInt("revicerUserId");
				String nickname = intent.getExtras().getString("nickname");
				String remarks = intent.getExtras().getString("remarks");
				byte repetSend = intent.getExtras().getByte("repetSend");
				long messageId = intent.getExtras().getLong("messageId");
				String message = intent.getExtras().getString("message");
				String commenttime = createCommentTime();
				LogPrint.Print("im","revicerUserId = "+revicerUserId);
				LogPrint.Print("im","nickname = "+nickname);
				LogPrint.Print("im","remarks = "+remarks);
				LogPrint.Print("im","repetSend = "+repetSend);
				LogPrint.Print("im","messageId = "+messageId);
				LogPrint.Print("im","message = "+message);
				LogPrint.Print("im","commenttime = "+commenttime);
				Entity pe = new Entity();
				pe.setTag((short)210);
				pe.setRevicerUserId(revicerUserId);
				pe.setNickName(nickname);
				pe.setRemarks(remarks);
				pe.setRepetSend(repetSend);
				pe.setTempMessageId(messageId);
				pe.setCommenttime(commenttime);
				MessageBody messageBody = new MessageBody();
				messageBody.setActionType(0);
				messageBody.setMessage(message);
				pe.setMessageBodies(messageBody);
				write(pe);
			}else if(ActionID.ACTION_BROADCAST_RESPONSE_SENDMESSAGE_SUCCESS.equals(action)){//发送成功（206）
				long messageId = intent.getExtras().getLong("messageId");
				long tempMessageId = intent.getExtras().getLong("tempMessageId");
				String commenttime = intent.getExtras().getString("commentTime");
				imDataBase.updataCommentTimeByTempMessageId(tempMessageId, commenttime);
				imDataBase.updataMessageId(tempMessageId, messageId);
				LogPrint.Print("im","messageId = "+messageId);
				LogPrint.Print("im","tempMessageId = "+tempMessageId);
				LogPrint.Print("im","commenttime = "+commenttime);
				if(!CommonUtil.isInMessageScreen){
					CommonUtil.ShowToast(IMService.this, "发送成功");
				}
			}else if(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_MESSAGE.equals(action)){//接收到一条消息
				int revicerUserId = intent.getExtras().getInt("revicerUserId");
				String nickname = intent.getExtras().getString("nickname");
				String remarks = intent.getExtras().getString("remarks");
				byte repetSend = intent.getExtras().getByte("repetSend");
				long messageId = intent.getExtras().getLong("messageId");
				String message = intent.getExtras().getString("message");
				String commenttime = intent.getExtras().getString("commentTime");
				LogPrint.Print("im","revicerUserId = "+revicerUserId);
				LogPrint.Print("im","nickname = "+nickname);
				LogPrint.Print("im","remarks = "+remarks);
				LogPrint.Print("im","repetSend = "+repetSend);
				LogPrint.Print("im","messageId = "+messageId);
				LogPrint.Print("im","message = "+message);
				LogPrint.Print("im","commenttime = "+commenttime);
				//用户id
				who = UserUtil.userid;
				//接收者昵称
				toname = UserUtil.username;
				//消息状态 - 首次接收消息为未读
				isread = 0;
				//发送状态 - 默认
				sendstate = 2;
				//消息类型 - 好友消息
				type = 0;
				//产品ID
				cid = 0;
				//将好友消息插入到数据库中
				imDataBase.insertDB(who, messageId, 0, revicerUserId, who, nickname, toname, isread, message, sendstate, commenttime, remarks, cid, type, (int)repetSend);			
				showNotification(0, nickname, message,0);
				//反馈服务器接收成功
				Entity pe = new Entity();
				pe.setTag((short)208);
				pe.setMessageid(messageId);
				pe.setRevicerUserId(revicerUserId);
				write(pe);
			}else if(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_SYSTEMMESSAGE.equals(action)){//接收到一条系统广播
				long messageId = intent.getExtras().getLong("messageId");
				String message = intent.getExtras().getString("message");
				String commenttime = intent.getExtras().getString("commentTime");
				LogPrint.Print("im","messageId = "+messageId);
				LogPrint.Print("im","message = "+message);
				LogPrint.Print("im","commenttime = "+commenttime);
				//用户id
				who = UserUtil.userid;
				//接收者昵称
				toname = UserUtil.username;
				//消息状态 - 首次接收消息为未读
				isread = 0;
				//消息类型 - 系统消息
				sendstate = 2;
				//消息类型 - 系统消息
				type = 2;
				//产品ID
				cid = 0;
				String nickname = "翠鸟";
				int revicerUserId = 0;
				byte repetSend = 0;
				//将好友消息插入到数据库中
				imDataBase.insertDB(who, messageId, 0, revicerUserId, who, nickname, toname, isread, message, sendstate, commenttime, "", cid, type, (int)repetSend);			
				
				showNotification(1, "翠鸟", message,2);
				
				//反馈服务器接收成功
				Entity pe = new Entity();
				pe.setTag((short)208);
				pe.setMessageid(messageId);
				pe.setRevicerUserId(0);
				write(pe);
			}else if(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_GIVEROCKMESSAGE.equals(action)){
				//连接一次缓存，获取摇一摇次数
				rockHandler.sendEmptyMessage(102030);
				//发送一条系统广播
				long messageId = intent.getExtras().getLong("messageId");
				String message = intent.getExtras().getString("message");
				String commenttime = intent.getExtras().getString("commentTime");
				LogPrint.Print("im","messageId = "+messageId);
				LogPrint.Print("im","message = "+message);
				LogPrint.Print("im","commenttime = "+commenttime);
				Intent intent1 = new Intent(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_SYSTEMMESSAGE);
				intent1.putExtra("messageId", messageId);
				intent1.putExtra("commentTime", commenttime);
				intent1.putExtra("message", message);
				sendBroadcast(intent1);
			}else if(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_PRIVATEMESSAGE.equals(action)){//接收到一条私信
				int revicerUserId = intent.getExtras().getInt("revicerUserId");
				String nickname = intent.getExtras().getString("nickname");
				String remarks = intent.getExtras().getString("remarks");
				byte repetSend = intent.getExtras().getByte("repetSend");
				long messageId = intent.getExtras().getLong("messageId");
				String message = intent.getExtras().getString("message");
				String commenttime = intent.getExtras().getString("commentTime");
				int cid = intent.getExtras().getInt("cid");
				
				//用户id
				who = UserUtil.userid;
				//接收者昵称
				toname = UserUtil.username;
				//消息状态 - 首次接收消息为未读
				isread = 0;
				//发送状态 - 默认
				sendstate = 2;
				//消息类型 - 私信消息
				type = 1;
				
				imDataBase.insertDB(who, messageId, 0, revicerUserId, who, nickname, toname, isread, message, sendstate, commenttime, remarks, cid, type, (int)repetSend);	
				
				LogPrint.Print("im","revicerUserId = "+revicerUserId);
				LogPrint.Print("im","nickname = "+nickname);
				LogPrint.Print("im","remarks = "+remarks);
				LogPrint.Print("im","repetSend = "+repetSend);
				LogPrint.Print("im","messageId = "+messageId);
				LogPrint.Print("im","message = "+message);
				LogPrint.Print("im","commenttime = "+commenttime);
				LogPrint.Print("im","cid = "+cid);
				
				showNotification(0, nickname, message,1);
				
				//反馈服务器接收成功
				Entity pe = new Entity();
				pe.setTag((short)208);
				pe.setMessageid(messageId);
				pe.setRevicerUserId(revicerUserId);
				write(pe);
			}else if(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE.equals(action)){//接收到一条建立好友关系的消息
				int revicerUserId = intent.getExtras().getInt("revicerUserId");
				String nickname = intent.getExtras().getString("nickname");
				String remarks = intent.getExtras().getString("remarks");
				byte repetSend = intent.getExtras().getByte("repetSend");
				long messageId = intent.getExtras().getLong("messageId");
				String message = intent.getExtras().getString("message");
				String commenttime = intent.getExtras().getString("commentTime");
				
				//用户id
				who = UserUtil.userid;
				//接收者昵称
				toname = UserUtil.username;
				//消息状态 - 首次接收消息为未读
				isread = 0;
				//发送状态 - 默认
				sendstate = 2;
				//消息类型 - 申请建立好友关系的消息
				type = 3;
				
				imDataBase.insertDB(who, messageId, 0, revicerUserId, who, nickname, toname, isread, message, sendstate, commenttime, remarks, cid, type, (int)repetSend);	
				
				LogPrint.Print("im","revicerUserId = "+revicerUserId);
				LogPrint.Print("im","nickname = "+nickname);
				LogPrint.Print("im","remarks = "+remarks);
				LogPrint.Print("im","repetSend = "+repetSend);
				LogPrint.Print("im","messageId = "+messageId);
				LogPrint.Print("im","message = "+message);
				LogPrint.Print("im","commenttime = "+commenttime);
				
				showNotification(0, nickname, message,3);
				
				//反馈服务器接收成功
				Entity pe = new Entity();
				pe.setTag((short)208);
				pe.setMessageid(messageId);
				pe.setRevicerUserId(revicerUserId);
				write(pe);
			}else if(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE_SUCCESS.equals(action)){//接收到一条确认建立好友关系的消息
				int revicerUserId = intent.getExtras().getInt("revicerUserId");
				String nickname = intent.getExtras().getString("nickname");
				String remarks = intent.getExtras().getString("remarks");
				byte repetSend = intent.getExtras().getByte("repetSend");
				long messageId = intent.getExtras().getLong("messageId");
				String message = intent.getExtras().getString("message");
				String commenttime = intent.getExtras().getString("commentTime");
				
				//用户id
				who = UserUtil.userid;
				//接收者昵称
				toname = UserUtil.username;
				//消息状态 - 首次接收消息为未读
				isread = 0;
				//发送状态 - 默认
				sendstate = 2;
				//消息类型 - 确认建立好友关系
				type = 4;
				
				imDataBase.insertDB(who, messageId, 0, revicerUserId, who, nickname, toname, isread, message, sendstate, commenttime, remarks, cid, type, (int)repetSend);	
				
				LogPrint.Print("im","revicerUserId = "+revicerUserId);
				LogPrint.Print("im","nickname = "+nickname);
				LogPrint.Print("im","remarks = "+remarks);
				LogPrint.Print("im","repetSend = "+repetSend);
				LogPrint.Print("im","messageId = "+messageId);
				LogPrint.Print("im","message = "+message);
				LogPrint.Print("im","commenttime = "+commenttime);
				
				showNotification(0, nickname, message,4);
				
				//反馈服务器接收成功
				Entity pe = new Entity();
				pe.setTag((short)208);
				pe.setMessageid(messageId);
				pe.setRevicerUserId(revicerUserId);
				write(pe);
				connectAddFriend(revicerUserId, 1);
			}else if(ActionID.ACTION_BROADCAST_CUTCONNECT_AND_RECONNECT.equals(action)){//主动断开连接并重连
				closeSocket();
				imConnecter = null;
				delayConnect(2);
			}else if(ActionID.ACTION_BROADCAST_STOP_COMMENT_CONNECT.equals(action)){
				if(commHandler != null){
					commHandler.removeCallbacks(commRunnable);
				}
			}else if(ActionID.ACTION_BROADCAST_START_COMMENT_CONNECT.equals(action)){
				if(commHandler != null){
					commHandler.removeCallbacks(commRunnable);				
					commHandler.postDelayed(commRunnable, getCommTime());
				}				
				getCommentNow(1);
			}else if(ActionID.ACTION_BROADCAST_REMOTE_LOGIN.equals(action)){
				LogPrint.Print("im", "ACTION_BROADCAST_REMOTE_LOGIN");
				// 停止信息服务
				context.stopService(new Intent(context, IMService.class));
				UserUtil.isRemoteLogin = true;
				CommonUtil.saveRemoteLogout(IMService.this, UserUtil.isRemoteLogin);
				CommonUtil.saveUserId(IMService.this, UserUtil.userid);
				LogoutUtil.logout(IMService.this);				
			}else if(ActionID.ACTION_BROADCAST_SEND_ADDFRIEND_FEEDBACK.equals(action)){  //发送加好友反馈
				int userid_sponsor = intent.getExtras().getInt("userid_sponsor");
				int userid_beAdd = intent.getExtras().getInt("useid_beAdd");				
				boolean issucess = intent.getExtras().getBoolean("issucess");
				Entity pe = new Entity();
				pe.setTag((short)213);
				pe.setUseridSponsor(userid_sponsor);
				pe.setUseridBeAdd(userid_beAdd);
				byte byteSuccess = 0;
				if(issucess){
					byteSuccess = 1;
				}
				pe.setIsAddFriendSccess(byteSuccess);				
				write(pe);
				
			}else if(ActionID.ACTION_BROADCAST_RESPONSE_ADDFRIEND_FEEDBACK.equals(action)){ //收到对方加好友结果反馈
				int userid_sponsor = intent.getExtras().getInt("userid_sponsor");  //发起人
				int userid_beAdd = intent.getExtras().getInt("useid_beAdd");		//本机		
				byte issucess = intent.getExtras().getByte("issucess");
				if(issucess == 1 && userid_sponsor == UserUtil.userid){
					//更新好友列表
					connectFriendInfo(userid_beAdd, 2);	
				}
			}
		}
		
	};
	
	/**
	 * 注册广播接收器
	 */
	private final void registerReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ActionID.ACTION_BROADCAST_CONNECT_SUCCESS_RESPONSE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_CONNECT_REFUSE_RESPONSE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_CONNECT_FAIL_RESPONSE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_HEART_RESPONSE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_SOCKET_IOERROR);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_REQUST_SEND_MESSAGE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_REQUST_SEND_PRIVATEMESSAGE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_REQUST_SEND_FRIENDMESSAGE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_SENDMESSAGE_SUCCESS);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_MESSAGE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_SYSTEMMESSAGE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_PRIVATEMESSAGE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_CUTCONNECT_AND_RECONNECT);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_STOP_COMMENT_CONNECT);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_START_COMMENT_CONNECT);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_REQUST_SEND_FRIENDMESSAGE_SUCCESS);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE_SUCCESS);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_SENDMESSAGE_FAIL);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_GIVEROCKMESSAGE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_REMOTE_LOGIN);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_SEND_ADDFRIEND_FEEDBACK);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_ADDFRIEND_FEEDBACK);
		registerReceiver(mGestuReceiver, intentFilter);
	}
	
	/**
	 * 卸载广播接收器
	 */
	private final void unRegisterReceiver() {
		if(mGestuReceiver != null){
			try {
				unregisterReceiver(mGestuReceiver);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	//生成时间
	private String createCommentTime(){
		Date now=new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		String strNow=sdf.format(now);
		return strNow;
	}
	
	private void connect(){
		if(CommonUtil.isNetWorkOpen(IMService.this)){
			LogPrint.Print("im","host:post = "+host+":"+port);
			imConnecter = new IMConnecterA(IMService.this, host, port);
			new Thread(){
				public void run(){
					imConnecter.connect();
				}
			}.start();
		}else{
			Intent intent = new Intent(ActionID.ACTION_BROADCAST_RESPONSE_SENDMESSAGE_FAIL);
			intent.putExtra("code", 0);
			sendBroadcast(intent);
		}
	}
	
	//延时连接
	private void delayConnect(int time){
		if(connectRunnable == null){
			connectRunnable = new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					connect();
				}
			};
		}
		if(connectHandler == null){
			connectHandler = new Handler();
		}
		connectHandler.removeCallbacks(connectRunnable);
		connectHandler.postDelayed(connectRunnable, time*1000);
	}
	
	private void write(Entity pe){
		if(imConnecter != null){
			imConnecter.write(pe);
		}else{
			Intent intent = new Intent(ActionID.ACTION_BROADCAST_RESPONSE_SENDMESSAGE_FAIL);
			intent.putExtra("code", 2);
			sendBroadcast(intent);
		}
	}
	
	private void closeSocket(){
		if(imConnecter != null){
			imConnecter.closeSocket();
		}
	}
	
	//顶栏提示有新消息
	private void showNotification(int type,String from,String msg,int msgType){
		if(!CommonUtil.isInMessageScreen&&CommonUtil.getMessageReceiverState(IMService.this)){//不在消息界面的时候提示
			Intent intent = new Intent();
			intent.putExtra("type", type);
			intent.putExtra("animation", true);
			intent.setClass(IMService.this, MessageManagerActivityA.class);
			NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	        Notification notification = new Notification(R.drawable.notifytionicon, "翠鸟提示:您有新消息", System.currentTimeMillis());
	        notification.defaults |= Notification.DEFAULT_VIBRATE;//震动通知
	        notification.flags |= Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
	        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
	        switch (msgType) {
			case 0:
			case 2:
				notification.setLatestEventInfo(this, "来自"+from+"的消息", msg, pendingIntent);
				break;
			case 1:
				notification.setLatestEventInfo(this, "来自"+from+"的私信", msg, pendingIntent);
				break;
			case 3:
				notification.setLatestEventInfo(this, from+" 申请与您建立好友", msg, pendingIntent);
				break;
			case 4:
				notification.setLatestEventInfo(this, "与 "+from+" 建立好友关系成功", msg, pendingIntent);
				break;
			}
	        notificationManager.notify(R.drawable.notifytionicon, notification);
		}
	}
	
	//定时获取新评论数
	private void getCommentCnt() {
		if (commHandler == null) {
			commHandler = new Handler();
		}
		if (commRunnable == null) {
			commRunnable = new Runnable() {

				@Override
				public void run() {
					getCommentNow(1);
				}
			};
		}
		commHandler.removeCallbacks(commRunnable);
		
		commHandler.postDelayed(commRunnable, getCommTime());
	}
	
	private void getCommentNow(int isGoOn) {
		if ((UserUtil.userid != -1) && (UserUtil.userState == 1)) {
			LogPrint.Print("imComment", "comment");
			ConnectUtil mConnectUtil = new ConnectUtil(IMService.this,
					mHandler, 1, isGoOn);
			String url = URLUtil.Url_NEW_COMMENT + "?oid=" + UserUtil.userid
					+ "&commentid=" + CommentUtils.newCommId;
			mConnectUtil.connect(url, HttpThread.TYPE_PAGE, 0);
		}

	}
	
	/**
	 * 获得评论联网间隔时间
	 * @return
	 */
	private int getCommTime(){
		String apn = CommonUtil.getApnType(this);
		int time = CommentUtils.time_2g * 1000;
		if (apn == null || apn.equals("")) {
			time = CommentUtils.time_2g * 1000;
		} else {
			if (apn.toLowerCase().indexOf("3g") > 0
					|| apn.toLowerCase().indexOf("wifi") > 0) {
				time = CommentUtils.time_wifi * 1000;
			} else {
				time = CommentUtils.time_2g * 1000;
			}
		}
		return time;
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:				
				if ("text/json".equals(msg.getData().getString("content_type"))) {
					switch(msg.arg1){
					case 0:
						int tempCount = jsonCntResult((byte[])msg.obj);
						if(tempCount != CommentUtils.newCommCount){
							CommentUtils.newCommCount = tempCount;
							Intent intent = new Intent(ActionID.ACTION_BROADCAST_COMMENT_COUNT);
							IMService.this.sendBroadcast(intent);
						}
						LogPrint.Print("im", "commcout = " + CommentUtils.newCommCount);
						if (commHandler == null) {
							commHandler = new Handler();
						}
						if(msg.getData().getInt("tag") == 1){
							commHandler.postDelayed(commRunnable, getCommTime());
						}						
						break;
					case 1:  //建立好友关系的请求
						boolean success = jsonBool((byte[])msg.obj);
						final int uid = msg.getData().getInt("tag");
						if(success){							
							connectFriendInfo(uid, 2);							
						}
						//发送加好友结果反馈(成功或者失败都要向另一方发送反馈)
						Intent intent = new Intent(ActionID.ACTION_BROADCAST_SEND_ADDFRIEND_FEEDBACK);
						intent.putExtra("userid_sponsor", uid);  //加好友的发起人userid
						intent.putExtra("useid_beAdd", UserUtil.userid); //被加好友方						
						intent.putExtra("issucess", success);
						IMService.this.sendBroadcast(intent);
						break;
					case 2:  //获取用户信息
						jsonSingle((byte[])msg.obj);
						break;					
					}
					
				}
				break;
			case MessageID.MESSAGE_CONNECT_ERROR:
				if (commHandler == null) {
					commHandler = new Handler();
				}
				commHandler.postDelayed(commRunnable, getCommTime());
				break;
			}
		}
	};
	
	private int jsonCntResult(byte[] data){		
		try {			
			String str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);			
			LogPrint.Print("lybconnect", "button json = " + str);
			if(str.length() <= 2){				
				return 0;
			}
			JSONObject json = new JSONObject(str);
			int count = json.getInt("totalcount");
			return count;
		}catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	
	//添加好友请求
	private void connectAddFriend(int uid, int threadIndex){
		String url = URLUtil.Url_ADD_FRIEND + "?oid=" +
		 UserUtil.userid + "&uid=" + uid;
		ConnectUtil mConnectUtil = new ConnectUtil(IMService.this,
				mHandler, 1, uid);		
		mConnectUtil.connect(url, HttpThread.TYPE_PAGE, threadIndex);
	}
	
	//获取用户好友信息
	private void connectFriendInfo(int uid, int threadIndex){
		String url = URLUtil.Url_FRIEND_OR_BLACK + "?oid=" +
		 UserUtil.userid + "&uid=" + uid;
		ConnectUtil mConnectUtil = new ConnectUtil(IMService.this,
				mHandler, 1, 0);
		mConnectUtil.connect(url, HttpThread.TYPE_PAGE, threadIndex);
	}
	
	private boolean jsonBool(byte[] data) {		
		try {			
			String str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("lybconnect", "button json = " + str);
			if (str.length() <= 2) {				
				return false;
			}
			JSONObject json = new JSONObject(str);
			String boolstr = json.getString("result");
			return Boolean.parseBoolean(boolstr);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	//更新好友数据库
	private void jsonSingle(byte[] data) {
		try {
			String str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("lybconnect", "jsonSingle = " + str);
			if (str.length() <= 2) {
				return;
			}
			JSONObject json = new JSONObject(str);
			Friend friend = new Friend();
			friend.isblack = Integer.parseInt(json.getString("isblack"));
			friend.userid = Integer.parseInt(json.getString("userid"));
			DBFriendList dbFriendList = new DBFriendList(this);
			if (friend.isblack == Friend.BOOL_STRANGER) {
				int delLine = dbFriendList.removeByUserid(friend.userid);
				LogPrint.Print("lyb", "del =" + delLine);
			} else {
				friend.id = Integer.parseInt(json.getString("id"));
				friend.username = json.getString("username");
				friend.icon_src = json.getString("icon_src");
				friend.userPage = json.getString("userpage") + "oid="
						+ UserUtil.userid + "&uid=" + friend.userid;
				if (friend.username != null && !friend.username.equals("")) {
					friend.allpinyin = PinyinUtils.getPinyin(friend.username);
					friend.firstletter = PinyinUtils
							.getHeadLetterByString(friend.username);
				}

				if (dbFriendList.isUserExist(friend.userid)) {
					dbFriendList.update(friend);
				} else {
					dbFriendList.insert(friend);
				}

			}
			//刷新UI的广播
			sendBroadcast(new Intent(ActionID.ACTION_BROADCAST_REFRESH_FRIEND_LIST));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Handler rockHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 102030:
				new ConnectUtil(IMService.this, rockHandler,0).connect(addUrlParam(URLUtil.URL_MAINPAGE_CACHE_INFO, 0, UserUtil.userid), HttpThread.TYPE_PAGE, URLUtil.THREAD_MAINPAGE_CACHE_INFO);
				break;
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				Json((byte[])msg.obj);
				break;
			}
		}
		
	};
	
	public String addUrlParam(String url,int pi,int oid){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"?pi="+pi+"&type=0"+"&oid="+oid+"&dpi="+URLUtil.dpi()+"&deviceid="+CommonUtil.getIMEI(this)+"&plaid="+URLUtil.plaid;
	}
	
	private void Json(byte[] data){
		try {
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = "+str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			if(result != null){
				if(result.equalsIgnoreCase("true")){
					long createtime = jObject.getLong("createtime");
					UserUtil.sysTime = jObject.getLong("time");
					UserUtil.callOnNum = jObject.getInt("callonnum");
					CommonUtil.saveSysTime(IMService.this, UserUtil.sysTime);
					CommonUtil.saveCreateTime(IMService.this, createtime);
					CommonUtil.saveCallOnNum(IMService.this, UserUtil.callOnNum);
					if(UserUtil.userid != -1&&UserUtil.userState == 1){//登录
						//初始化摇一摇倒计时
						TimerForRock.initLimitTime(CommonUtil.getLimitTime(CommonUtil.getCreateTime(IMService.this), CommonUtil.getSysTime(IMService.this)), IMService.this);
						sendBroadcast(new Intent(ActionID.ACTION_BROADCAST_ROCK_TIME_CHANGE));
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
