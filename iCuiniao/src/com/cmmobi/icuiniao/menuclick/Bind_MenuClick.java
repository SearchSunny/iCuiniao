/**
 * 
 */
package com.cmmobi.icuiniao.menuclick;

import android.os.Handler;
import android.os.Message;

import com.cmmobi.icuiniao.util.MessageID;

/**
 * @author hw
 *取消绑定
 */
public class Bind_MenuClick extends AbsMenuClick{

	private Handler handler;
	private int logintype;
	public Bind_MenuClick(Handler handler,int logintype){
		this.handler = handler;
		this.logintype = logintype;
	}
	
	public void click(int index){
		Message message = new Message();
		message.arg1 = logintype;
		message.what = MessageID.MESSAGE_BIND_MENUCLICK;
		handler.sendMessage(message);
	}
	
	public void cancel(){
		Message message = new Message();
		message.what = MessageID.MESSAGE_BIND_MENUCLICK_CANCEL;
		handler.sendMessage(message);
	}
}
