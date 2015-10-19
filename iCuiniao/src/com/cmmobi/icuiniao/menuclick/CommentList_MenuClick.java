package com.cmmobi.icuiniao.menuclick;

import android.os.Handler;
import android.os.Message;

import com.cmmobi.icuiniao.util.MessageID;
//未用
public class CommentList_MenuClick extends AbsMenuClick {
	private String userPage;
	private Handler handler;
	private String subjectId;
	private int isSubject;
	private int commentid;
	
	public CommentList_MenuClick(int isSubject, String userPage, String subjectId, String commentid, Handler handler){
		this.isSubject = isSubject;
		this.userPage = userPage;		
		this.subjectId = subjectId;
		this.commentid = StringToInt(commentid);
		this.handler = handler;
	}
	
	public void click(int index){
		switch (index) {
		case 0://查看此人资料
			Message message = new Message();
			message.what = MessageID.MESSAGE_MENUCLICK_COMMENT_LIST_USERPAGE;
			message.obj = userPage;			
			handler.sendMessage(message);
			break;
		case 1://回复
			Message message1 = new Message();
			message1.what = MessageID.MESSAGE_MENUCLICK_COMMENTC_LIST_COMMENT;			
			message1.obj = subjectId;
			message1.arg1 = isSubject;
			message1.arg2 = commentid;
			handler.sendMessage(message1);
			break;
		}
	}
	
	private int StringToInt(String str){
		int result = 0;
		try{
			result = Integer.parseInt(str);		
		}catch(Exception e){
			e.printStackTrace();			
		}
		return result;
	}
}
