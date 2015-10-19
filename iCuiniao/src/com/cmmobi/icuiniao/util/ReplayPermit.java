package com.cmmobi.icuiniao.util;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.widget.Toast;

import com.cmmobi.icuiniao.Activity.CommentPageActivity;
import com.cmmobi.icuiniao.Activity.CommentPageActivity_A;
import com.cmmobi.icuiniao.Activity.SendMessageActivity;
import com.cmmobi.icuiniao.entity.Friend;

public class ReplayPermit {
	
	public String ismyfriend;
	public String refused;
	public String msg;
	//用于分割subjectid subjectid commentid(均为int型)
	public static String split = "s";  
	
	public static boolean isMayClick = true;
	public static long lastClickTime = 0;	

	
	public static void replayComment(Context mContext, Message msg){
		ReplayPermit permit = jsonMayReplay((byte[])msg.obj);
		String url = msg.getData().getString("mUrl");
		String paramValue = CommonUtil.getSubString(url, "&parmValue=", "&uid="); 
		//is_subject + "-" +  subjectid + "-" + commentid + "-" + cid + "-" + touserid  + "-" + tousername;							
		String[] arrayParam = paramValue.split(split);
		int is_subject = Integer.parseInt(arrayParam[0]);
		int subjectid = Integer.parseInt(arrayParam[1]);
		int commentid = Integer.parseInt(arrayParam[2]);
		int cid = Integer.parseInt(arrayParam[3]);
		int touserid =  Integer.parseInt(arrayParam[4]);
		String tousernmae =  CommonUtil.getSubString(url, "&tousername=", "&parmValue=");
		if(permit.ismyfriend.equalsIgnoreCase("false")){//不是好友，普通评论
			Intent intent2 = new Intent();
			intent2.setClass(mContext, CommentPageActivity.class);
			intent2.putExtra("issubject", is_subject);
			intent2.putExtra("subjectid", subjectid);
			intent2.putExtra("commentid", commentid);
			((Activity)mContext).startActivityForResult(intent2, 9100);
			
		}else{ 
			if(permit.refused.equalsIgnoreCase("true")){	//私聊不可达							
				Toast.makeText(mContext, permit.msg, Toast.LENGTH_SHORT).show();
				ReplayPermit.isMayClick = true;
			}else{	//私聊评论								
				Intent intent3 = new Intent();
				intent3.setClass(mContext, CommentPageActivity_A.class);
				intent3.putExtra("issubject", is_subject);
				intent3.putExtra("subjectid", subjectid);
				intent3.putExtra("commentid", commentid);
				//接收商品ID、发出者昵称、接收者昵称									
				intent3.putExtra("cid", cid);
				intent3.putExtra("touserid", touserid);
				intent3.putExtra("toname", tousernmae);
				((Activity)mContext).startActivityForResult(intent3, 9100);
			}			
		}		
	}
	
	private static ReplayPermit jsonMayReplay(byte[] data) {
		ReplayPermit replayPermit = new ReplayPermit();
		try {
			String str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);
			if (str.length() <= 2) {
				return replayPermit;
			}
			JSONObject json = new JSONObject(str);
			replayPermit.ismyfriend = json.getString("ismyfriend");
			if (replayPermit.ismyfriend.equalsIgnoreCase("true")) {
				replayPermit.refused = json.getString("refused");
				if(replayPermit.refused.equalsIgnoreCase("true")){
					replayPermit.msg = json.getString("msg");
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return replayPermit;
	}
	
	//**************************发消息
	public static int jsonRefused(byte[] data){		
		try {			
			String str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);			
			LogPrint.Print("lybconnect", "jsonRefused = " + str);
			if(str.length() <= 2){				
				return -1;
			}
			JSONObject json = new JSONObject(str);
			int isOtherBlack = json.getInt("isotherblack");			
			return isOtherBlack;
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return -1;
	}
	//根据解析情况发送消息
	public static void replaySendMsg(Activity activity, Message msg){
		int isOtherBlack = ReplayPermit.jsonRefused((byte[])msg.obj);						
		if(isOtherBlack == Friend.BOOL_BLACK){
			Toast.makeText(activity, "由于对方设置，无法发送消息", Toast.LENGTH_SHORT).show();
			ReplayPermit.isMayClick = true;
		}else{
			final String url = msg.getData().getString("mUrl");
			String username = CommonUtil.getSubString(url, "&nickname=", "&oid=");
			int userid = CommonUtil.parserUserid(CommonUtil.getSubString(url, "?uid=", "&nickname="));
			if(userid == -1){
				ReplayPermit.isMayClick = true;
				return;
			}
			Intent intent = new Intent(activity, SendMessageActivity.class);	
			intent.putExtra("uid", userid);
			intent.putExtra("nickname", username);
			activity.startActivityForResult(intent, MessageID.REQUEST_SEND_MSG);
		}
	}

}
