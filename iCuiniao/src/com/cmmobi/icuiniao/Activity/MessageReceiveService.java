/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.util.ActionID;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.UserUtil;

/**
 * @author hw
 *已废弃
 */
public class MessageReceiveService extends Service{

	//轮询的时间间隔
	public static int CHICK_TIME = 30000;
	private ConnectUtil mConnectUtil;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
//		LogPrint.Print("message","***********************onCreate-service");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
//		LogPrint.Print("message","***********************onDestory-service");
		mHandler.removeMessages(MessageID.MESSAGE_CHICK);
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		stopService(intent);
//		mHandler.sendEmptyMessage(MessageID.MESSAGE_CHICK);
//		LogPrint.Print("message","***********************onStart-service");
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageID.MESSAGE_CHICK:
//				LogPrint.Print("message","======chick message");
//				if(UserUtil.userid != -1&&UserUtil.userState == 1){
//					mConnectUtil = new ConnectUtil(MessageReceiveService.this, mHandler,0);
//					//by lyb
////					mConnectUtil.connect(URLUtil.URL_MESSAGE_GET+"?oid="+UserUtil.userid, HttpThread.TYPE_PAGE, 0);
//					mConnectUtil.connect(URLUtil.URL_MESSAGE_GET+"?oid="+UserUtil.userid, HttpThread.TYPE_MESSAGE_GET, 0);
//				}
//				mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_CHICK, CHICK_TIME);
				break;
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
//				if(mConnectUtil!=null&&"text/json".equals(msg.getData().getString("content_type"))){
//					Json((byte[])msg.obj);
//				}
				break;
			}
		}
		
	};
	
	private void Json(byte[] data){
		try {
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = "+str);
			JSONArray jsonArray = new JSONArray(str);
			for(int i = 0;jsonArray!=null&&i < jsonArray.length();i ++){
				JSONObject jObject = jsonArray.getJSONObject(i);
				String result = jObject.getString("result");
				if(result != null){
					if(result.equalsIgnoreCase("true")){
						String from = jObject.getString("from");
						String to = jObject.getString("to");
						String from_name = jObject.getString("from_name");
						String to_name = jObject.getString("to_name");
						String msgString = jObject.getString("msg");
						String time = jObject.getString("time");
						//写入本地文件
						CommonUtil.writeMessage(CommonUtil.dir_message_user+UserUtil.userid+CommonUtil.endName, from, from_name, to, to_name, msgString, time);
						
						if(CommonUtil.isInMessageScreen){
							Intent intent = new Intent(ActionID.ACTION_BROADCAST_MESSAGE_CHICK);
							intent.putExtra("from", Integer.parseInt(from));
							intent.putExtra("to", Integer.parseInt(to));
							intent.putExtra("from_name", from_name);
							intent.putExtra("to_name", to_name);
							intent.putExtra("msg", msgString);
							intent.putExtra("time", time);
							sendBroadcast(intent);
						}else{
							Intent intent = new Intent();
							intent.setClass(MessageReceiveService.this, MessageActivity_A.class);
							intent.putExtra("from", Integer.parseInt(from));
							intent.putExtra("to", Integer.parseInt(to));
					        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
					        Notification notification = new Notification(R.drawable.notifytionicon, "翠鸟提示:您有新消息", System.currentTimeMillis());
					        notification.defaults |= Notification.DEFAULT_VIBRATE;//震动通知
					        notification.flags |= Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
					        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
					        
					        if(msgString.startsWith("%!%")){
					        	notification.setLatestEventInfo(this, "来自"+from_name+"的消息", "推荐好友", pendingIntent);
					        }else{
					        	notification.setLatestEventInfo(this, "来自"+from_name+"的消息", msgString, pendingIntent);
					        }
					        notificationManager.notify((int)System.currentTimeMillis(), notification);
						}
					}
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
//	private void Json(byte[] data){
//		try {
//			String str = new String(data,"UTF-8");
//			str = CommonUtil.formUrlEncode(str);
//			LogPrint.Print("json = "+str);
//			JSONArray jsonArray = new JSONArray(str);
//			for(int i = 0;jsonArray!=null&&i < jsonArray.length();i ++){
//				JSONObject jObject = jsonArray.getJSONObject(i);
//				String result = jObject.getString("result");
//				if(result != null){
//					if(result.equalsIgnoreCase("true")){
//						String from = jObject.getString("from");
//						String to = jObject.getString("to");
//						String from_name = jObject.getString("from_name");
//						String to_name = jObject.getString("to_name");
//						String msgString = jObject.getString("msg");
//						String time = jObject.getString("time");
//						//写入本地文件
//						CommonUtil.writeMessage(CommonUtil.dir_message_user+UserUtil.userid+CommonUtil.endName, from, from_name, to, to_name, msgString, time);
//						
//						if(CommonUtil.isInMessageScreen){
//							Intent intent = new Intent(ActionID.ACTION_BROADCAST_MESSAGE_CHICK);
//							intent.putExtra("from", Integer.parseInt(from));
//							intent.putExtra("to", Integer.parseInt(to));
//							intent.putExtra("from_name", from_name);
//							intent.putExtra("to_name", to_name);
//							intent.putExtra("msg", msgString);
//							intent.putExtra("time", time);
//							sendBroadcast(intent);
//						}else{
//							Intent intent = new Intent();
//							intent.setClass(MessageReceiveService.this, MessageActivity.class);
//							intent.putExtra("from", Integer.parseInt(from));
//							intent.putExtra("to", Integer.parseInt(to));
//					        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//					        Notification notification = new Notification(R.drawable.notifytionicon, "翠鸟提示:您有新消息", System.currentTimeMillis());
//					        notification.defaults |= Notification.DEFAULT_VIBRATE;//震动通知
//					        notification.flags |= Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
//					        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//					        
//					        if(msgString.startsWith("%!%")){
//					        	notification.setLatestEventInfo(this, "来自"+from_name+"的消息", "推荐好友", pendingIntent);
//					        }else{
//					        	notification.setLatestEventInfo(this, "来自"+from_name+"的消息", msgString, pendingIntent);
//					        }
//					        notificationManager.notify((int)System.currentTimeMillis(), notification);
//						}
//					}
//				}
//			}
//			
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//	}

}
