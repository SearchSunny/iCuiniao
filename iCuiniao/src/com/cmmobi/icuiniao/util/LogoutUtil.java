package com.cmmobi.icuiniao.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.icuiniao.plug.im.IMService;

public class LogoutUtil {
	
	public static void logout(Context context) {		
		UserUtil.isNewLoginOrExit = true;
		UserUtil.isLogout = true;
		UserUtil.userState = 0;
		UserUtil.clearSharePreference(context, "您的账号在其他客户端登录中，请重新登录");		
		// 发送登出广播
		context.sendBroadcast(new Intent(ActionID.ACTION_BROADCAST_LOGOUT));
		// 重新连接
		context.startService(new Intent(context, IMService.class));
	}
	
	public static void showMessagetToLogout(final Context context){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		//异地登出标志复位
		UserUtil.isRemoteLogin = false;	
		CommonUtil.saveRemoteLogout(context, false);
		
		builder.setTitle("您的账号在其他客户端登录中，请重新登录").setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {					
			}
		}).setCancelable(false).show();
	}
}
