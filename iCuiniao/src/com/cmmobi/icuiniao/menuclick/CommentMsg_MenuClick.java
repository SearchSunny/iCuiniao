package com.cmmobi.icuiniao.menuclick;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.ReplayPermit;
//消息评论的回复（回复评论，查看商品）
public class CommentMsg_MenuClick extends AbsMenuClick {
	private String userPage;
	private Handler handler;
	private String subjectId;
	private int isSubject;
	private int commentid;
	//接收商品ID、发出者昵称、接收者昵称
	private String tousername;
	private int touserid;
	private String cid;
	
	private int position;
	
	public void setPosition(int position) {
		this.position = position;
	}

	public CommentMsg_MenuClick(int isSubject, String userPage, String subjectId, String commentid, Handler handler){
		this.isSubject = isSubject;
		this.userPage = userPage;		
		this.subjectId = subjectId;
		this.commentid = StringToInt(commentid);
		this.handler = handler;
	}
	public CommentMsg_MenuClick(int isSubject, String userPage, String subjectId, String commentid, 
			int userid, String username, String cid, Handler handler){
		this.isSubject = isSubject;
		this.userPage = userPage;		
		this.subjectId = subjectId;
		this.commentid = StringToInt(commentid);
		this.touserid = userid;
		this.tousername = username;
		this.cid = cid;
		this.handler = handler;
	}
	
	
	public void click(int index){
		switch (index) {
		
		case 0://回复评论
			if(!ReplayPermit.isMayClick){
				return;
			}
			ReplayPermit.isMayClick = false;
			Message message1 = new Message();
			message1.what = MessageID.MESSAGE_MENUCLICK_COMMENTC_MSG_REPLY;	
			Bundle data = new Bundle();
			data.putInt("is_subject", isSubject);
			data.putInt("subjectid", Integer.parseInt(subjectId));
			data.putInt("commentid", commentid);
			data.putInt("touserid", touserid);
			data.putString("tousername", tousername);
			data.putString("cid", cid);
			message1.setData(data);
//			message1.obj = subjectId;
//			message1.arg1 = isSubject;
//			message1.arg2 = commentid;
			handler.sendMessage(message1);
			break;
		case 1://查看商品
			Message message2 = new Message();
			message2.what = MessageID.MESSAGE_MENUCLICK_COMMENTC_MSG_COMMODITY;			
			Bundle data2 = new Bundle();
			data2.putInt("position", position);
			message2.setData(data2);			
			handler.sendMessage(message2);
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
