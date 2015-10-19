/**
 * 
 */
package com.icuiniao.plug.im;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.UserUtil;

/**
 * @author hw
 *im 解析器
 */
public class IMDecoder extends CumulativeProtocolDecoder{

	private final static String code = "UTF-8";
	
	public IMDecoder(){
		
	}
	
	@Override
	protected boolean doDecode(IoSession session, IoBuffer buffer,
			ProtocolDecoderOutput out) throws Exception {
		// TODO Auto-generated method stub
		try {
			int pos = buffer.position();
			buffer.mark();
			byte [] tagByte = new byte[4];
			buffer.get(tagByte);
			String tagStr = new String(tagByte,code);
			short tag = Short.parseShort(tagStr, 16);
			
			byte [] msg = null;
			if(tag != 201 && tag != 202){  //心跳、回复不带len
				byte [] lenByte = new byte[4];
				buffer.get(lenByte);
				String lenStr = new String(lenByte,code);
				short len = Short.parseShort(lenStr, 16);
				
				if((len - 8) > buffer.remaining()){//如果消息内容不够，则重置，相当于不读取size  
		            LogPrint.Print("im","#############粘包--"+tag);
					buffer.reset();
		            return false;//接收新数据，以拼凑成完整数据  
		        }
				
				byte [] msg2 = new byte[len-8];
		        buffer.get(msg2);

		        msg = new byte[len];
		        
		        System.arraycopy(tagByte, 0, msg, 0, tagByte.length); 
		        System.arraycopy(lenByte, 0, msg, 4, lenByte.length);
		        System.arraycopy(msg2, 0, msg, 8, msg2.length);
			}else{
				
				msg = new byte[buffer.limit()];
				System.arraycopy(tagByte, 0, msg, 0, tagByte.length); 
				byte [] msg2 = new byte[buffer.limit() - 4];
		        buffer.get(msg2);
		        System.arraycopy(msg2, 0, msg, 4, msg2.length);
			}
			
			
			Entity e = new Entity();
			decoder(msg,e);
			out.write(e); 
		} catch (Exception e) {
			// TODO: handle exception
		}
		return true;
	}
	
	private void decoder(byte[] data,Entity e){
		try {
			short tag = getTag(data);
			e.setTag(tag);
			short bodyMaxLen = (short)data.length;
			if(tag != 202){
				bodyMaxLen = getBodyMaxLen(data);
			}
			LogPrint.Print("im",tag+":数据包总长度="+bodyMaxLen);
			switch (tag) {
			case 1:
				decodeTag1(data,e);
				break;
			case 3:
				decodeTag3(data,e);
				break;
			case 4:
				decodeTag4(data,e);
				break;
			case 5:
				decodeTag5(data,e);
				break;
			case 202:
				decodeTag202(data,e);
				break;
			case 204:
				decodeTag204(data, e);
				break;
			case 205:
				decodeTag205(data, e);
				break;
			case 206:
				decodeTag206(data, e);
				break;
			case 207:
				decodeTag207(data, e);
				break;
			case 208:
				decodeTag208(data, e);
				break;
			case 209:
				decodeTag209(data, e);
				break;
			case 210:
				decodeTag210(data, e);
				break;
			case 299:
				decodeTag299(data, e);
				break;
			case 211:
				decodeTag211(data, e);
				break;
			case 212:
				decodeTag212(data, e);
				break;
			case 213:
				decodeTag213(data, e);
				break;
			}
		} catch (Exception ex) {
			// TODO: handle exception
		}
	}
	
	//获取tag
	private short getTag(byte [] msg){
		short result = -1;
    	try {
    		byte [] tagByte = new byte[4];
    		System.arraycopy(msg, 0, tagByte, 0, tagByte.length);
    		String tagStr = new String(tagByte,code);
    		result = Short.parseShort(tagStr, 16);
		} catch (Exception e) {
			// TODO: handle exception
		}
 		
 		return result;
    }
	
	//获取包的总长度
	private short getBodyMaxLen(byte [] msg){
		short result = -1;
    	try {
    		byte [] maxLenByte = new byte[4];
    		System.arraycopy(msg, 4, maxLenByte, 0, maxLenByte.length);
    		String maxLenStr = new String(maxLenByte,code);
    		result = Short.parseShort(maxLenStr, 16);
		} catch (Exception e) {
			// TODO: handle exception
		}
 		
 		return result;
    }
	
	//获取长度
	private int getLength(byte [] msg,int pos,int len){
		int result = 0;
		try {
			byte [] sizeByte = new byte[len];
			System.arraycopy(msg, pos, sizeByte, 0, sizeByte.length);
			String sizeStr = new String(sizeByte,code);
			result = Integer.parseInt(sizeStr, 16);
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	return result;
    }
	
	//获取注册解密信息
	private String getRegInfo(byte [] msg, int size){
    	String result = "";
    	try {
    		byte [] regInfoByte = new byte[size];
    		System.arraycopy(msg, 8+4, regInfoByte, 0, regInfoByte.length);
    		result = new String(regInfoByte,code);
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return result;
    }
	
	//获取心跳开始时间
	private String getHeartStarttime(byte [] msg,int index){

    	byte [] serverTime = new byte[1]; 		
        System.arraycopy(msg, (index*(1+1+4))+8+7+4, serverTime, 0, serverTime.length);
        String commenttime="";
//        for(int i=0;i<7;i++)
//        {
//        	byte []yearhigh=new byte[1];
//	        System.arraycopy(serverTime, i, yearhigh, 0, yearhigh.length);
//	        commenttime+=((yearhigh[0]>>4)&(0x0f));
//	        commenttime+=""+(yearhigh[0]&(0x0f));
//        }
        try {
        	commenttime = ""+serverTime[0];
		} catch (Exception e) {
			// TODO: handle exception
		}
        
    	return commenttime;
    }
	
	//获取心跳结束时间
	private String getHeartEndtime(byte [] msg,int index){
    	
    	byte [] serverTime = new byte[1]; 		
        System.arraycopy(msg, (index*(1+1+4))+8+7+1+4, serverTime, 0, serverTime.length);
        String commenttime="";
//        for(int i=0;i<7;i++)
//        {
//        	byte []yearhigh=new byte[1];
//	        System.arraycopy(serverTime, i, yearhigh, 0, yearhigh.length);
//	        commenttime+=((yearhigh[0]>>4)&(0x0f));
//	        commenttime+=""+(yearhigh[0]&(0x0f));
//        }
        try {
        	commenttime = ""+serverTime[0];;
		} catch (Exception e) {
			// TODO: handle exception
		}
        
    	return commenttime;
    }
	
	//获取心跳间隔时间，秒
	private long getHeartInterval(byte [] msg,int index){
    	long result = 15;
    	try {
    		byte [] HeartInterval = new byte[4];
    		System.arraycopy(msg, (index*(1+1+4))+8+7+1+1+4, HeartInterval, 0, HeartInterval.length);
			result = Long.parseLong(new String(HeartInterval,code), 16);
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	return result;
    }
	
	//获取当前的时间
	private String getCurrentTime(byte [] msg){
    	
    	byte [] serverTime = new byte[7]; 		
        System.arraycopy(msg, 4+4+4, serverTime, 0, serverTime.length);
        String commenttime="";
        for(int i=0;i<7;i++)
        {
        	byte []yearhigh=new byte[1];
	        System.arraycopy(serverTime, i, yearhigh, 0, yearhigh.length);
	        commenttime+=((yearhigh[0]>>4)&(0x0f));
	        commenttime+=""+(yearhigh[0]&(0x0f));
        }
        
    	return  commenttime;
    }
	
	//获取拒绝标示
	private int getRefuseTag(byte [] msg){
		int result = 0;
		try {
			byte [] refuseByte = new byte[1];
			System.arraycopy(msg, 4+4, refuseByte, 0, refuseByte.length);
			String sizeStr = new String(refuseByte,code);
			result = Integer.parseInt(sizeStr, 16);
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	return result;
    }
	
	//获取心跳请求
	private int getHeartResponse(byte [] msg){
		int result = 0x01;
		try {
			byte [] heart = new byte[1];
			System.arraycopy(msg, 4, heart, 0, heart.length);
			String sizeStr = new String(heart,code);
			result = Integer.parseInt(sizeStr, 16);
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	return result;
    }
	
	//获取消息id
	private long getMessageId(byte[] msg,int pos){
		long result = 0;
		try {
			byte[] messageIdByte = new byte[16];
			System.arraycopy(msg, pos, messageIdByte, 0, messageIdByte.length);
			String messageId = new String(messageIdByte,code);
			result = Long.parseLong(messageId, 16);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	
	//获取时间
	private String getCommentTime(byte[] msg,int pos){
		String commenttime="";
		try {
			byte[] timeByte = new byte[7];
			System.arraycopy(msg, pos, timeByte, 0, timeByte.length);
	        for(int i=0;i<7;i++)
	        {
	        	byte []yearhigh=new byte[1];
		        System.arraycopy(timeByte, i, yearhigh, 0, yearhigh.length);
		        commenttime+=((yearhigh[0]>>4)&(0x0f));
		        commenttime+=""+(yearhigh[0]&(0x0f));
	        }
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return commenttime;
	}
	
	//获取内容类型
	private int getActionType(byte[] msg,int pos){
		int result = 0;
		try {
			byte[] actionTypeByte = new byte[1];
			System.arraycopy(msg, pos, actionTypeByte, 0, actionTypeByte.length);
			String sizeStr = new String(actionTypeByte,code);
			result = Integer.parseInt(sizeStr, 16);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	
	//获取消息内容长度
	private int getMessageLength(byte[] msg,int pos){
		int result = 0;
		try {
			byte[] lengthByte = new byte[4];
			System.arraycopy(msg, pos, lengthByte, 0, lengthByte.length);
			String lengthStr = new String(lengthByte,code);
			LogPrint.Print("im","lengthByte = "+lengthByte[0]+","+lengthByte[1]+","+lengthByte[2]+","+lengthByte[3]);
			LogPrint.Print("im","lengthStr = "+lengthStr);
			result = Integer.parseInt(lengthStr,16);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	
	//获取消息内容
	private String getMessage(byte[] msg,int pos,int len){
		String result = null;
		try {
			byte[] messageByte = new byte[len];
			System.arraycopy(msg, pos, messageByte, 0, messageByte.length);
			result = new String(messageByte,code);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	
	//获取发送者id
	private int getUserId(byte[] msg,int pos){
		int result = -1;
		try {
			byte[] id = new byte[8];
			System.arraycopy(msg, pos, id, 0, id.length);
			String strid = new String(id,code);
			result = Integer.parseInt(strid,16);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	
	//服务器回应客户端连接申请，tag:1
	private void decodeTag1(byte[] msg,Entity e){
		LogPrint.Print("im","receive-1: result.len = "+msg.length);
		int size = getLength(msg,4+4,4);
    	String regInfo = getRegInfo(msg, size);
    	e.setSize(size);
    	e.setRegInfo(regInfo);
	}
	
	//服务器回应客户端允许建立连接成功，tag:3
	private void decodeTag3(byte[] msg,Entity e){
		LogPrint.Print("im","receive-3: result.len = "+msg.length);
		int size = getLength(msg,4+4,4);
		LogPrint.Print("im","size = "+size);
		String currentTime = getCurrentTime(msg);//yyyymmddhhmmss 
		e.setSize(size);
		e.setCurrentTime(currentTime);
		int bodyLen = size-7;
		int arrayLen = bodyLen/(1+1+4);
		for(int i = 0;i < arrayLen;i ++){
			String HeartStarttime = getHeartStarttime(msg,i);
			String HeartEndtime = getHeartEndtime(msg,i);
			long heartInterval=getHeartInterval(msg,i);
			e.setHeartStarttime(HeartStarttime,arrayLen,i);
			e.setHeartEndtime(HeartEndtime,arrayLen,i);
			e.setHeartInterval(heartInterval,arrayLen,i);
		}
	}
	
	//服务器回应客户端拒绝建立连接，tag:4
	private void decodeTag4(byte[] msg,Entity e){
		LogPrint.Print("im","receive-4: result.len = "+msg.length);
		int refuseTag = getRefuseTag(msg);
		e.setRefuseTag(refuseTag);
	}
	
	//服务器回应客户端建立连接失败，tag:5
	private void decodeTag5(byte[] msg,Entity e){
		LogPrint.Print("im","receive-5: result.len = "+msg.length);
		int refuseTag = getRefuseTag(msg);
		e.setRefuseTag(refuseTag);
	}
	
	//服务器回应心跳请求，tag:202
	private void decodeTag202(byte[] msg,Entity e){
		LogPrint.Print("im","receive-202: result.len = "+msg.length);
		int heartResponse = getHeartResponse(msg);
		e.setHeartResponse(heartResponse);
	}
	
	//接收系统消息，tag:204
	private void decodeTag204(byte[] msg,Entity e){
		LogPrint.Print("im","receive-204: result.len = "+msg.length);
		long messageId = getMessageId(msg, 4+4);
		String commentTime = getCommentTime(msg, 4+16+4);
		e.setMessageid(messageId);
		e.setCommenttime(commentTime);
		
		int startReadPos = 4+16+7+4;
		int count = 0;
		while(true){
			count ++;
			if(count >= 10)break;//保证不会出现死循环
			
			//消息内容类型
			int actionType = getActionType(msg, startReadPos);
			//消息长度
			int messageLength = getMessageLength(msg, startReadPos+1);
			//消息内容
			String messageString = getMessage(msg, startReadPos+1+4, messageLength);
			MessageBody messageBody = new MessageBody();
			messageBody.setActionType(actionType);
			messageBody.setMessage(messageString);
			e.setMessageBodies(messageBody);
			
			if(startReadPos+1+4+messageLength >= msg.length){
				break;
			}else{
				startReadPos += (1+4+messageLength);
			}
		}
	}
	
	//接收发送过来的消息，tag:205
	private void decodeTag205(byte[] msg,Entity e){
		LogPrint.Print("im","receive-205: result.len = "+msg.length);
		int seekPos = 4+4;
		long messageId = getMessageId(msg, seekPos);
		seekPos += 16;
		String commentTime = getCommentTime(msg, seekPos);
		seekPos += 7;
		int reviceUserId = getUserId(msg, seekPos);
		seekPos += 8;
		int nickNameLength = getLength(msg, seekPos, 2);
		seekPos += 2;
		String nickName = getMessage(msg, seekPos, nickNameLength);
		seekPos += nickNameLength;
		int remarksLength = getLength(msg, seekPos, 2);
		seekPos += 2;
		String remarks = getMessage(msg, seekPos, remarksLength);
		seekPos += remarksLength;
		byte repet = (byte)getLength(msg, seekPos, 1);
		seekPos += 1;
		
		e.setMessageid(messageId);
		e.setCommenttime(commentTime);
		e.setRevicerUserId(reviceUserId);
		e.setNickName(nickName);
		e.setRemarks(remarks);
		e.setRepetSend(repet);
		
		int count = 0;
		while(true){
			count ++;
			if(count >= 10)break;//保证不会出现死循环
			
			//消息内容类型
			int actionType = getActionType(msg, seekPos);
			//消息长度
			int messageLength = getMessageLength(msg, seekPos+1);
			//消息内容
			String messageString = getMessage(msg, seekPos+1+4, messageLength);
			MessageBody messageBody = new MessageBody();
			messageBody.setActionType(actionType);
			messageBody.setMessage(messageString);
			e.setMessageBodies(messageBody);
			
			if(seekPos+1+4+messageLength >= msg.length){
				break;
			}else{
				seekPos += (1+4+messageLength);
			}
		}
	}
	
	//服务器已收到发出的信息，tag:206
	private void decodeTag206(byte[] msg,Entity e){
		LogPrint.Print("im","receive-206: result.len = "+msg.length);
		int seekPos = 4+4;
		long messageId = getMessageId(msg, seekPos);
		seekPos += 16;
		byte state = (byte)getLength(msg, seekPos, 1);
		seekPos += 1;
		long tempMessageId = getMessageId(msg, seekPos);
		seekPos += 16;
		String commentTime = getCommentTime(msg, seekPos);
		
		e.setMessageid(messageId);
		e.setServiceState(state);
		e.setTempMessageId(tempMessageId);
		e.setCommenttime(commentTime);
	}
	
	//接收发送过来的私信，tag:207
	private void decodeTag207(byte[] msg,Entity e){
		LogPrint.Print("im","receive-207: result.len = "+msg.length);
		int seekPos = 4+4;
		long messageId = getMessageId(msg, seekPos);
		seekPos += 16;
		String commentTime = getCommentTime(msg, seekPos);
		seekPos += 7;
		int reviceUserId = getUserId(msg, seekPos);
		seekPos += 8;
		int nickNameLength = getLength(msg, seekPos, 2);
		seekPos += 2;
		String nickName = getMessage(msg, seekPos, nickNameLength);
		seekPos += nickNameLength;
		int remarksLength = getLength(msg, seekPos, 2);
		seekPos += 2;
		String remarks = getMessage(msg, seekPos, remarksLength);
		seekPos += remarksLength;
		byte repet = (byte)getLength(msg, seekPos, 1);
		seekPos += 1;
		int cid = getLength(msg, seekPos, 16);
		seekPos += 16;
		
		e.setMessageid(messageId);
		e.setCommenttime(commentTime);
		e.setRevicerUserId(reviceUserId);
		e.setNickName(nickName);
		e.setRemarks(remarks);
		e.setRepetSend(repet);
		e.setCid(cid);
		
		int count = 0;
		while(true){
			count ++;
			if(count >= 10)break;//保证不会出现死循环
			
			//消息内容类型
			int actionType = getActionType(msg, seekPos);
			//消息长度
			int messageLength = getMessageLength(msg, seekPos+1);
			//消息内容
			String messageString = getMessage(msg, seekPos+1+4, messageLength);
			MessageBody messageBody = new MessageBody();
			messageBody.setActionType(actionType);
			messageBody.setMessage(messageString);
			e.setMessageBodies(messageBody);
			
			if(seekPos+1+4+messageLength >= msg.length){
				break;
			}else{
				seekPos += (1+4+messageLength);
			}
		}
	}
	
	//服务器回应对方已收到消息，tag:208
	private void decodeTag208(byte[] msg,Entity e){
		LogPrint.Print("im","receive-208: result.len = "+msg.length);
		int seekPos = 4+4;
		long messageId = getMessageId(msg, seekPos);
		seekPos += 16;
		byte state = (byte)getLength(msg, seekPos, 1);
		seekPos += 1;
		int reviceUserId = getUserId(msg, seekPos);
		
		e.setMessageid(messageId);
		e.setServiceState(state);
		e.setRevicerUserId(reviceUserId);
	}
	
	//接收到一条建立好友关系消息，tag:209
	private void decodeTag209(byte[] msg,Entity e){
		LogPrint.Print("im","receive-209: result.len = "+msg.length);
		int seekPos = 4+4;
		long messageId = getMessageId(msg, seekPos);
		seekPos += 16;
		String commentTime = getCommentTime(msg, seekPos);
		seekPos += 7;
		int reviceUserId = getUserId(msg, seekPos);
		seekPos += 8;
		int nickNameLength = getLength(msg, seekPos, 2);
		seekPos += 2;
		String nickName = getMessage(msg, seekPos, nickNameLength);
		seekPos += nickNameLength;
		int remarksLength = getLength(msg, seekPos, 2);
		seekPos += 2;
		String remarks = getMessage(msg, seekPos, remarksLength);
		seekPos += remarksLength;
		byte repet = (byte)getLength(msg, seekPos, 1);
		seekPos += 1;
		
		e.setMessageid(messageId);
		e.setCommenttime(commentTime);
		e.setRevicerUserId(reviceUserId);
		e.setNickName(nickName);
		e.setRemarks(remarks);
		e.setRepetSend(repet);
		
		int count = 0;
		while(true){
			count ++;
			if(count >= 10)break;//保证不会出现死循环
			
			//消息内容类型
			int actionType = getActionType(msg, seekPos);
			//消息长度
			int messageLength = getMessageLength(msg, seekPos+1);
			LogPrint.Print("im","messageLength = "+messageLength);
			//消息内容
			String messageString = getMessage(msg, seekPos+1+4, messageLength);
			MessageBody messageBody = new MessageBody();
			messageBody.setActionType(actionType);
			messageBody.setMessage(messageString);
			e.setMessageBodies(messageBody);
			
			if(seekPos+1+4+messageLength >= msg.length){
				break;
			}else{
				seekPos += (1+4+messageLength);
			}
		}
	}
	
	//接收到一条确认建立好友关系消息，tag:210
	private void decodeTag210(byte[] msg,Entity e){
		LogPrint.Print("im","receive-210: result.len = "+msg.length);
		int seekPos = 4+4;
		long messageId = getMessageId(msg, seekPos);
		seekPos += 16;
		String commentTime = getCommentTime(msg, seekPos);
		seekPos += 7;
		int reviceUserId = getUserId(msg, seekPos);
		seekPos += 8;
		int nickNameLength = getLength(msg, seekPos, 2);
		seekPos += 2;
		String nickName = getMessage(msg, seekPos, nickNameLength);
		seekPos += nickNameLength;
		int remarksLength = getLength(msg, seekPos, 2);
		seekPos += 2;
		String remarks = getMessage(msg, seekPos, remarksLength);
		seekPos += remarksLength;
		byte repet = (byte)getLength(msg, seekPos, 1);
		seekPos += 1;
		
		e.setMessageid(messageId);
		e.setCommenttime(commentTime);
		e.setRevicerUserId(reviceUserId);
		e.setNickName(nickName);
		e.setRemarks(remarks);
		e.setRepetSend(repet);
		
		int count = 0;
		while(true){
			count ++;
			if(count >= 10)break;//保证不会出现死循环
			
			//消息内容类型
			int actionType = getActionType(msg, seekPos);
			//消息长度
			int messageLength = getMessageLength(msg, seekPos+1);
			//消息内容
			String messageString = getMessage(msg, seekPos+1+4, messageLength);
			MessageBody messageBody = new MessageBody();
			messageBody.setActionType(actionType);
			messageBody.setMessage(messageString);
			e.setMessageBodies(messageBody);
			
			if(seekPos+1+4+messageLength >= msg.length){
				break;
			}else{
				seekPos += (1+4+messageLength);
			}
		}
	}
	
	//服务器业务异常反馈
	private void decodeTag299(byte[] msg,Entity e){
		LogPrint.Print("im","receive-299: result.len = "+msg.length);
		int seekPos = 4+4;
		String commentTime = getCommentTime(msg, seekPos);
		seekPos += 7;
		int errorLength = getLength(msg, seekPos, 2);
		seekPos += 2;
		String error = getMessage(msg, seekPos, errorLength);
		e.setCommenttime(commentTime);
		e.setError(error);
	}
	
	//接收赠送好友摇一下机会，tag:211
	private void decodeTag211(byte[] msg,Entity e){
		LogPrint.Print("im","receive-211: result.len = "+msg.length);
		long messageId = getMessageId(msg, 4+4);
		String commentTime = getCommentTime(msg, 4+16+4);
		e.setMessageid(messageId);
		e.setCommenttime(commentTime);
		
		int startReadPos = 4+16+7+4;
		int count = 0;
		while(true){
			count ++;
			if(count >= 10)break;//保证不会出现死循环
			
			//消息内容类型
			int actionType = getActionType(msg, startReadPos);
			//消息长度
			int messageLength = getMessageLength(msg, startReadPos+1);
			//消息内容
			String messageString = getMessage(msg, startReadPos+1+4, messageLength);
			MessageBody messageBody = new MessageBody();
			messageBody.setActionType(actionType);
			messageBody.setMessage(messageString);
			e.setMessageBodies(messageBody);
			
			if(startReadPos+1+4+messageLength >= msg.length){
				break;
			}else{
				startReadPos += (1+4+messageLength);
			}
		}
	}
	
	//异地登录通知
	private void decodeTag212(byte[] msg, Entity e){
		LogPrint.Print("im","receive-212: result.len = "+msg.length);
		int seekPos = 4+4;
		UserUtil.userid = getLength(msg, seekPos, 8);		
		LogPrint.Print("im","212 userid = "+ UserUtil.userid);		
	}
	
	//加好友结果反馈
	private void decodeTag213(byte[] msg, Entity e){
		LogPrint.Print("im","receive-213: result.len = "+msg.length);
		int seekPos = 4+4; //tag+maxlen
		e.setUseridSponsor(getLength(msg, seekPos, 8));
		seekPos += 8;
		e.setUseridBeAdd(getLength(msg, seekPos, 8));
		seekPos += 8;
		e.setIsAddFriendSccess((byte)getLength(msg, seekPos, 1));			
	}

}
