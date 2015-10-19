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
public class Mypage_MenuClick extends AbsMenuClick{

	private int userid;
	private int subjectid;
	private Handler handler;
	
	public Mypage_MenuClick(int userid,int subjectid,Handler handler){
		this.userid = userid;
		this.subjectid = subjectid;
		this.handler = handler;
	}
	
	public void click(int index){
		switch (index) {
		case 0://查看此人资料
			Message message = new Message();
			message.what = MessageID.MESSAGE_MENUCLICK_MYPAGE_COMMENTC_USERPAGE;
			message.arg1 = userid;
			message.arg2 = subjectid;
			handler.sendMessage(message);
			break;
		case 1://回复
			Message message1 = new Message();
			message1.what = MessageID.MESSAGE_MENUCLICK_MYPAGE_COMMENTC_COMMENT;
			message1.arg1 = userid;
			message1.arg2 = subjectid;
			handler.sendMessage(message1);
			break;
		}
	}
	
}
