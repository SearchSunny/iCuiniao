package com.cmmobi.icuiniao.menuclick;

import android.os.Handler;
import android.os.Message;

import com.cmmobi.icuiniao.util.MessageID;

public class Setting_MenuClick extends AbsMenuClick{

	private Handler handler;
	public Setting_MenuClick(Handler handler){
		this.handler = handler;
	}
	
	public void click(int index){
		switch (index) {
		case 0://拍照
			Message message = new Message();
			message.what = MessageID.MESSAGE_MENUCLICK_SETTING_CAMERA;
			handler.sendMessage(message);
			break;
		case 1://用户相册
			Message message1 = new Message();
			message1.what = MessageID.MESSAGE_MENUCLICK_SETTING_PHOTO;
			handler.sendMessage(message1);
			break;
		}
	}
}
