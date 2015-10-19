/**
 * 
 */
package com.cmmobi.icuiniao.menuclick;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cmmobi.icuiniao.util.MessageID;

/**
 * @author hw
 *
 */
public class ThirdComment_MenuClick extends AbsMenuClick{

	private int userid;
	private int subjectid;
	private int commentid;
	private Handler handler;
	public ThirdComment_MenuClick(int userid,int subjectid,int commentid,Handler handler){
		this.userid = userid;
		this.subjectid = subjectid;
		this.commentid = commentid;
		this.handler = handler;
	}
	
	public void click(int index){
		switch (index) {
		case 0://查看此人资料
			Bundle bundle = new Bundle();
			bundle.putInt("userid", userid);
			bundle.putInt("subjectid", subjectid);
			bundle.putInt("commentid", commentid);
			Message message = new Message();
			message.what = MessageID.MESSAGE_MENUCLICK_MYPAGE_COMMENTC_USERPAGE;
			message.setData(bundle);
			handler.sendMessage(message);
			break;
		case 1://回复
			Bundle bundle1 = new Bundle();
			bundle1.putInt("userid", userid);
			bundle1.putInt("subjectid", subjectid);
			bundle1.putInt("commentid", commentid);
			Message message1 = new Message();
			message1.what = MessageID.MESSAGE_MENUCLICK_MYPAGE_COMMENTC_COMMENT;
			message1.setData(bundle1);
			handler.sendMessage(message1);
			break;
		}
	}
}
