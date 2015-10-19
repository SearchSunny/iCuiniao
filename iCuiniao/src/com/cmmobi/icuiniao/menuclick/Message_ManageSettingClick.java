/**
 * 
 */
package com.cmmobi.icuiniao.menuclick;

import android.os.Handler;
import android.os.Message;

import com.cmmobi.icuiniao.util.MessageID;

/**
 * @author xp
 *私信管理删除聊天记录
 */
public class Message_ManageSettingClick extends AbsMenuClick{

	private Handler handler;
	private int postion;
	public Message_ManageSettingClick(Handler handler,int postion){
		this.handler = handler;
		this.postion = postion;
	}
	
	public void click(int index){
		Message message = new Message();
		message.what = MessageID.MESSAGE_DELRECORD;
		message.arg1 = index;
		message.arg2 = postion;
		handler.sendMessage(message);
	}
}
