/**
 * 
 */
package com.cmmobi.icuiniao.menuclick;

import android.os.Handler;
import android.os.Message;

import com.cmmobi.icuiniao.util.MessageID;

/**
 * @author xp
 *私信重新发送、删除取消
 */
public class Message_SendAndDelClick extends AbsMenuClick{

	private Handler handler;
	private int postion;
	public Message_SendAndDelClick(Handler handler,int postion){
		this.handler = handler;
		this.postion = postion;
	}
	
	public void click(int index){
		Message message = new Message();
		message.what = MessageID.MESSAGE_SENDANDDEL_CANCEL;
		message.arg1 = index;
		message.arg2 = postion;
		handler.sendMessage(message);
	}
}
