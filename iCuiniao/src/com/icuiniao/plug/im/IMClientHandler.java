/**
 * 
 */
package com.icuiniao.plug.im;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import android.content.Context;
import android.content.Intent;

import com.cmmobi.icuiniao.util.ActionID;
import com.cmmobi.icuiniao.util.LogPrint;

/**
 * @author hw
 *
 */
public class IMClientHandler extends IoHandlerAdapter{

	private Context mContext;
	
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		execute(session, (Entity) message);
	}
	
	public void setContext(Context context){
		this.mContext = context;
	}
	
	//接收服务器返回的结果以后的后续逻辑
	private void execute(IoSession session,Entity entity){
		short tag = entity.getTag();
		Entity pe = new Entity();
		Intent intent;
		switch (tag) {
		case 1://服务器回应客户端连接申请，tag:1
			LogPrint.Print("im","服务器回应客户端连接申请，tag:1");
			pe.setTag((short)2);
			pe.setRegInfo(entity.getRegInfo());
			session.write(pe);
			break;
		case 3://服务器回应客户端允许建立连接成功，tag:3
			LogPrint.Print("im","服务器回应客户端允许建立连接成功，tag:3");
			//发送广播通知
	    	intent = new Intent(ActionID.ACTION_BROADCAST_CONNECT_SUCCESS_RESPONSE);
	    	intent.putExtra("heartstarttime", entity.getHeartStarttime());
	    	intent.putExtra("heartendtime", entity.getHeartEndtime());
	    	intent.putExtra("heartinterval", entity.getHeartInterval());
	    	intent.putExtra("currenttime", entity.getCurrentTime());
	    	mContext.sendBroadcast(intent);
			break;
		case 4:
			LogPrint.Print("im","服务器回应客户端拒绝建立连接，tag:4");
			//发送广播通知
			intent = new Intent(ActionID.ACTION_BROADCAST_CONNECT_REFUSE_RESPONSE);
			intent.putExtra("refusetag", entity.getRefuseTag());
			mContext.sendBroadcast(intent);
			break;
		case 5:
			LogPrint.Print("im","服务器回应客户端建立连接失败，tag:5");
			//发送广播通知
			intent = new Intent(ActionID.ACTION_BROADCAST_CONNECT_FAIL_RESPONSE);
			intent.putExtra("refusetag", entity.getRefuseTag());
			mContext.sendBroadcast(intent);
			break;
		case 202:
			LogPrint.Print("im","服务器回应心跳请求，tag:202");
			//发送广播通知
			intent = new Intent(ActionID.ACTION_BROADCAST_HEART_RESPONSE);
			intent.putExtra("heartResponse", entity.getHeartResponse());
			mContext.sendBroadcast(intent);
			break;
		case 204:
			LogPrint.Print("im","接收系统消息，tag:204");
			//发送广播通知
			intent = new Intent(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_SYSTEMMESSAGE);
			intent.putExtra("messageId", entity.getMessageid());
			intent.putExtra("commentTime", entity.getCommenttime());
			intent.putExtra("message", entity.getMessageBodies().get(0).getMessage());
			mContext.sendBroadcast(intent);
			break;
		case 205:
			LogPrint.Print("im","接收到一条好友消息，tag:205");
			//发送广播通知
			intent = new Intent(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_MESSAGE);
			intent.putExtra("revicerUserId", entity.getRevicerUserId());
			intent.putExtra("nickname", entity.getNickName());
			intent.putExtra("remarks", entity.getRemarks());
			intent.putExtra("repetSend", entity.getRepetSend());
			intent.putExtra("messageId", entity.getMessageid());
			intent.putExtra("message", entity.getMessageBodies().get(0).getMessage());
			intent.putExtra("commentTime", entity.getCommenttime());
			mContext.sendBroadcast(intent);
			break;
		case 206:
			LogPrint.Print("im","消息发送成功，tag:206");
			//发送广播通知
			intent = new Intent(ActionID.ACTION_BROADCAST_RESPONSE_SENDMESSAGE_SUCCESS);
			intent.putExtra("messageId", entity.getMessageid());
			intent.putExtra("tempMessageId", entity.getTempMessageId());
			intent.putExtra("commentTime", entity.getCommenttime());
			mContext.sendBroadcast(intent);
			break;
		case 207:
			LogPrint.Print("im","接收到一条好友私信，tag:207");
			//发送广播通知
			intent = new Intent(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_PRIVATEMESSAGE);
			intent.putExtra("revicerUserId", entity.getRevicerUserId());
			intent.putExtra("nickname", entity.getNickName());
			intent.putExtra("remarks", entity.getRemarks());
			intent.putExtra("repetSend", entity.getRepetSend());
			intent.putExtra("messageId", entity.getMessageid());
			intent.putExtra("message", entity.getMessageBodies().get(0).getMessage());
			intent.putExtra("cid", entity.getCid());
			intent.putExtra("commentTime", entity.getCommenttime());
			mContext.sendBroadcast(intent);
			break;
		case 208:
			LogPrint.Print("im","对方已收到消息，tag:208");
			break;
		case 209:
			LogPrint.Print("im","接收到一条建立好友关系消息，tag:209");
			//发送广播通知
			intent = new Intent(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE);
			intent.putExtra("revicerUserId", entity.getRevicerUserId());
			intent.putExtra("nickname", entity.getNickName());
			intent.putExtra("remarks", entity.getRemarks());
			intent.putExtra("repetSend", entity.getRepetSend());
			intent.putExtra("messageId", entity.getMessageid());
			intent.putExtra("message", entity.getMessageBodies().get(0).getMessage());
			intent.putExtra("commentTime", entity.getCommenttime());
			mContext.sendBroadcast(intent);
			break;
		case 210:
			LogPrint.Print("im","接收到一条确认建立好友关系消息，tag:210");
			//发送广播通知
			intent = new Intent(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE_SUCCESS);
			intent.putExtra("revicerUserId", entity.getRevicerUserId());
			intent.putExtra("nickname", entity.getNickName());
			intent.putExtra("remarks", entity.getRemarks());
			intent.putExtra("repetSend", entity.getRepetSend());
			intent.putExtra("messageId", entity.getMessageid());
			intent.putExtra("message", entity.getMessageBodies().get(0).getMessage());
			intent.putExtra("commentTime", entity.getCommenttime());
			mContext.sendBroadcast(intent);
			break;
		case 299:
			LogPrint.Print("im","接收到服务器异常通知，tag:299");
			LogPrint.Print("im","error:"+entity.getError());
			mContext.sendBroadcast(new Intent(ActionID.ACTION_BROADCAST_SOCKET_IOERROR));
			break;
		case 211:
			LogPrint.Print("im","接收到赠送好友摇一下机会，tag:211");
			//发送广播通知
			intent = new Intent(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_GIVEROCKMESSAGE);
			intent.putExtra("messageId", entity.getMessageid());
			intent.putExtra("commentTime", entity.getCommenttime());
			intent.putExtra("message", entity.getMessageBodies().get(0).getMessage());
			mContext.sendBroadcast(intent);
			break;
		case 212:
			LogPrint.Print("im","异地登录通知，tag:212");
			mContext.sendBroadcast(new Intent(ActionID.ACTION_BROADCAST_REMOTE_LOGIN));
			break;
		case 213:
			LogPrint.Print("im","收到加好友结果反馈，tag:213");
			intent = new Intent(ActionID.ACTION_BROADCAST_RESPONSE_ADDFRIEND_FEEDBACK);
			intent.putExtra("userid_sponsor", entity.getUseridSponsor());
			intent.putExtra("useid_beAdd", entity.getUseridBeAdd());				
			intent.putExtra("issucess", entity.getIsAddFriendSccess());
			mContext.sendBroadcast(intent);
			break;
		}
	}
	
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		LogPrint.Print("im","error:"+cause.toString());
		mContext.sendBroadcast(new Intent(ActionID.ACTION_BROADCAST_SOCKET_IOERROR));
	}
}
