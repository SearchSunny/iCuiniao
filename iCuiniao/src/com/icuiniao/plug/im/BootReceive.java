/**
 * 
 */
package com.icuiniao.plug.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.icuiniao.plug.localmessage.LocalMessageService;

/**
 * @author hw
 *开机自启动
 */
public class BootReceive extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			context.stopService(new Intent(context, IMService.class));
			context.startService(new Intent(context, IMService.class));
			//启动本地消息服务
			context.startService(new Intent(context,LocalMessageService.class));
		}
	}

}
