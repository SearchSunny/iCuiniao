/**
 * 
 */
package com.cmmobi.icuiniao.util;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.icuiniao.plug.im.IMService;
import com.icuiniao.plug.localmessage.LocalMessageService;

/**
 * @author hw
 *离线日志时间计数服务
 */
public class OfflineTimeService extends Service{

	//时间计数
	private static int timedef;
	private boolean isServiceStart;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		LogPrint.Print("message","***********offline-onCreate-service");
		doing();
		isServiceStart = true;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		LogPrint.Print("message","***********offline-onDestory-service");
		mHandler.removeMessages(334455);
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		LogPrint.Print("message","***********offline-onStart-service");
		if(isServiceStart == false){
			doing();
		}
	}
	
	private void doing(){
		if(timedef == 0){
			mHandler.sendEmptyMessageDelayed(334455, 1000);
		}
	}
	
	//获取时间差
	public static int getTimeDef(){
		if(timedef >= Integer.MAX_VALUE){
			timedef = Integer.MAX_VALUE;
		}
		return timedef;
	}
	
	//时间差清零
	public static void resetTimeDef(){
		timedef = 0;
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 334455:
				timedef ++;
				if(timedef == 60){
					OfflineLog.upload(getApplicationContext());
				}
				mHandler.sendEmptyMessageDelayed(334455, 1000);
				//检测另外两个服务是否运行，守护
				if(timedef > 0 && timedef%1800==0){//半小时检测一次
					if(UserUtil.isRemoteLogin == false){
						if(CommonUtil.isServiceRunning(OfflineTimeService.this, "com.icuiniao.plug.im.IMService") == false){
							startService(new Intent(OfflineTimeService.this, IMService.class));
						}
					}
					if(CommonUtil.isServiceRunning(OfflineTimeService.this, "com.icuiniao.plug.localmessage.LocalMessageService") == false){
			    		startService(new Intent(OfflineTimeService.this, LocalMessageService.class));
			    	}
				}
				break;
			}
		}
		
	};

}
