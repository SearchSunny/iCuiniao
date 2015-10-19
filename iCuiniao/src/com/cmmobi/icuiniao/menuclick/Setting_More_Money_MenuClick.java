/**
 * 
 */
package com.cmmobi.icuiniao.menuclick;

import android.os.Handler;
import android.os.Message;

import com.cmmobi.icuiniao.util.MessageID;

/**
 * @author hw
 *详细个人信息教育菜单
 */
public class Setting_More_Money_MenuClick extends AbsMenuClick{

	private Handler handler;
	public Setting_More_Money_MenuClick(Handler handler){
		this.handler = handler;
	}
	
	public void click(int index){
		Message message = new Message();
		message.what = MessageID.MESSAGE_MENUCLICK_SETTING_MORE_MONEY;
		message.arg1 = index;
		handler.sendMessage(message);
	}
}
