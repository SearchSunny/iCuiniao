/**
 * 
 */
package com.cmmobi.icuiniao.menuclick;

import android.os.Handler;
import android.os.Message;

import com.cmmobi.icuiniao.util.MessageID;

/**
 * @author hw
 *私信
 */
public class Message_MenuClick extends AbsMenuClick{

	private Handler handler;
	private int postion;
	public Message_MenuClick(Handler handler,int postion){
		this.handler = handler;
		this.postion = postion;
	}
	
	public void click(int index){
		Message message = new Message();
		message.what = MessageID.MESSAGE_MENUCLICK_MESSAGE;
		message.arg1 = index;
		message.arg2 = postion;
		handler.sendMessage(message);
	}
}
