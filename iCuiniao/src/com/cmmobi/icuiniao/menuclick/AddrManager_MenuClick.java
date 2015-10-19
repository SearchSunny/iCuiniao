/**
 * 
 */
package com.cmmobi.icuiniao.menuclick;

import android.os.Handler;
import android.os.Message;

import com.cmmobi.icuiniao.util.MessageID;

/**
 * @author hw
 *
 */
public class AddrManager_MenuClick extends AbsMenuClick{

	private int deleteIndex;
	private Handler handler;
	public AddrManager_MenuClick(int deleteIndex,Handler handler){
		this.deleteIndex = deleteIndex;
		this.handler = handler;
	}
	
	public void click(int index){
		switch (index) {
		case 0:
			Message message = new Message();
			message.what = MessageID.MESSAGE_MENUCLICK_ADDRMANAGER;
			message.arg1 = deleteIndex;
			handler.sendMessage(message);
			break;
		case 1:
			break;
		}
	}
}
