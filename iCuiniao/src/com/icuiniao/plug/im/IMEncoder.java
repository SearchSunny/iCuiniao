/**
 * 
 */
package com.icuiniao.plug.im;

import java.util.ArrayList;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;

/**
 * @author hw
 *im 封装器
 */
public class IMEncoder extends ProtocolEncoderAdapter{

	private final static String code = "UTF-8";
	
	public IMEncoder(){
		
	}
	
	@Override
	public void encode(IoSession arg0, Object message, ProtocolEncoderOutput out)
			throws Exception {
		// TODO Auto-generated method stub
		try {
			Entity pe = (Entity) message;
			short tag = pe.getTag();
			IoBuffer buffer = IoBuffer.allocate(1000).setAutoExpand(true);
			
			switch (tag) {
			case 0:
				buffer.put(addTag0(pe.getUserId(),pe.getDeviceId()));
				break;
			case 2:
				buffer.put(addTag2(pe.getRegInfo()));
				break;
			case 201:
				buffer.put(addTag201());
				break;
			case 205:
				buffer.put(addTag205(pe));
				break;
			case 207:
				buffer.put(addTag207(pe));
				break;
			case 208:
				buffer.put(addTag208(pe));
				break;
			case 209:
				buffer.put(addTag209(pe));
				break;
			case 210:
				buffer.put(addTag210(pe));
				break;
			case 213:
				buffer.put(addTag213(pe));
				break;
			}
			
			buffer.flip();
			out.write(buffer);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	//客户端请求连接,tag:0
	private byte[] addTag0(int userid,String deviceid){
		byte[] result = null;
		try {
			byte[] tag = CommonUtil.toHexString(0, 4).getBytes(code);
			byte[] userId = CommonUtil.toHexString(userid, 8).getBytes(code);
			byte[] deviceId = deviceid.getBytes(code);
			byte[] deviceIdSize = CommonUtil.toHexString(deviceId.length, 2).getBytes(code);
			int size = userId.length + deviceId.length+deviceIdSize.length;
			byte[] bodySize = CommonUtil.toHexString(size, 2).getBytes(code);
			
			result = new byte[4+tag.length+bodySize.length+size];
			//新增数据总长度，用于断包时连包使用
			byte[] bodyMaxLen = CommonUtil.toHexString(result.length, 4).getBytes(code);
			System.arraycopy(tag, 0, result, 0, tag.length);
			System.arraycopy(bodyMaxLen, 0, result, tag.length, bodyMaxLen.length);//新增数据总长度
			System.arraycopy(bodySize, 0, result, tag.length+bodyMaxLen.length, bodySize.length);
			System.arraycopy(userId, 0, result, tag.length+bodyMaxLen.length+bodySize.length, userId.length);
			System.arraycopy(deviceIdSize, 0, result, tag.length+bodyMaxLen.length+bodySize.length+userId.length, deviceIdSize.length);
			System.arraycopy(deviceId, 0, result, tag.length+bodyMaxLen.length+bodySize.length+userId.length+deviceIdSize.length, deviceId.length);
			LogPrint.Print("im","send-0: result.len = "+result.length);
			LogPrint.Print("im","客户端请求连接,tag:0");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	
	//客户端建立连接，tag:2
	private byte[] addTag2(String regInfo){
		byte[] result = null;
		try {
			byte[] tag = CommonUtil.toHexString(2, 4).getBytes(code);
			byte [] regInfoByte = regInfo.getBytes(code);  //16长度
			int bodySize = regInfoByte.length;
			byte [] sizeByte = CommonUtil.toHexString(bodySize, 4).getBytes(code);
			result = new byte[4+tag.length + sizeByte.length +bodySize];
			//新增数据总长度，用于断包时连包使用
			byte[] bodyMaxLen = CommonUtil.toHexString(result.length, 4).getBytes(code);
			
			System.arraycopy(tag, 0, result, 0, tag.length); 
			System.arraycopy(bodyMaxLen, 0, result, tag.length, bodyMaxLen.length);//新增数据总长度
			System.arraycopy(sizeByte, 0, result, tag.length+bodyMaxLen.length, sizeByte.length);
			System.arraycopy(regInfoByte, 0, result, tag.length+bodyMaxLen.length + sizeByte.length, regInfoByte.length);
			LogPrint.Print("im","send-2: result.len = "+result.length);
			LogPrint.Print("im","客户端建立连接，tag:2");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	
	//心跳请求,tag:201
	private byte[] addTag201(){
		byte[] result = null;
		try {
			byte[] tag = CommonUtil.toHexString(201, 4).getBytes(code);
			byte[] state = CommonUtil.toHexString(0, 1).getBytes(code);
			result = new byte[tag.length + state.length];
			
			System.arraycopy(tag, 0, result, 0, tag.length); 
			System.arraycopy(state, 0, result, tag.length, state.length);
			LogPrint.Print("im","send-201: result.len = "+result.length);
			LogPrint.Print("im","心跳请求，tag:201");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	
	//发送消息，tag:205
	private byte[] addTag205(Entity pe){
		byte[] result = null;
		try {
			int length = 0;
			byte[] tag = CommonUtil.toHexString(205, 4).getBytes(code);//标识
			byte[] tempMessageId = CommonUtil.toHexString(pe.getTempMessageId(), 16).getBytes(code);//流水号
			byte[] commenttime = new byte[7];//评论时间
			String strcommentTime = pe.getCommenttime();
			commenttime[0]=(byte)(((Integer.parseInt(strcommentTime.substring(0, 1)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(1, 2)) & 0x0f))));
			commenttime[1]=(byte)(((Integer.parseInt(strcommentTime.substring(2, 3)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(3, 4)) & 0x0f))));
			commenttime[2]=(byte)(((Integer.parseInt(strcommentTime.substring(4, 5)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(5, 6)) & 0x0f))));//月
			commenttime[3]=(byte)(((Integer.parseInt(strcommentTime.substring(6, 7)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(7, 8)) & 0x0f))));//日
			commenttime[4]=(byte)(((Integer.parseInt(strcommentTime.substring(8, 9)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(9, 10)) & 0x0f))));//时
			commenttime[5]=(byte)(((Integer.parseInt(strcommentTime.substring(10, 11)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(11, 12)) & 0x0f))));//分
			commenttime[6]=(byte)(((Integer.parseInt(strcommentTime.substring(12, 13)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(13, 14)) & 0x0f))));//秒
			byte[] revicerUserId = CommonUtil.toHexString(pe.getRevicerUserId(), 8).getBytes(code);//接收人id
			byte[] nickname = pe.getNickName().getBytes(code);//昵称
			byte[] nicknameLength = CommonUtil.toHexString(nickname.length, 2).getBytes(code);//昵称长度
			byte[] remarks = pe.getRemarks().getBytes(code);//备注
			byte[] remarksLength = CommonUtil.toHexString(remarks.length, 2).getBytes(code);//备注长度
			byte[] repetSend = CommonUtil.toHexString(pe.getRepetSend(), 1).getBytes(code);//是否重发
			
			length = tag.length+tempMessageId.length+commenttime.length+revicerUserId.length+nicknameLength.length+nickname.length+remarksLength.length+remarks.length+repetSend.length;
			
			ArrayList<MessageBody> messageBodies = pe.getMessageBodies();
			for(int i = 0;messageBodies != null&&i < messageBodies.size();i ++){
				byte[] body = messageBodies.get(i).getMessage().getBytes(code);
				length += 1;
				length += 4;
				length += body.length;
			}
			
			result = new byte[4+length];
			//新增数据总长度，用于断包时连包使用
			byte[] bodyMaxLen = CommonUtil.toHexString(result.length, 4).getBytes(code);
			int seekPos = 0;
			System.arraycopy(tag, 0, result, seekPos, tag.length);
			seekPos += tag.length;
			System.arraycopy(bodyMaxLen, 0, result, tag.length, bodyMaxLen.length);//新增数据总长度
			seekPos += bodyMaxLen.length;
			System.arraycopy(tempMessageId, 0, result, seekPos, tempMessageId.length);
			seekPos += tempMessageId.length;
			System.arraycopy(commenttime, 0, result, seekPos, commenttime.length);
			seekPos += commenttime.length;
			System.arraycopy(revicerUserId, 0, result, seekPos, revicerUserId.length);
			seekPos += revicerUserId.length;
			System.arraycopy(nicknameLength, 0, result, seekPos, nicknameLength.length);
			seekPos += nicknameLength.length;
			System.arraycopy(nickname, 0, result, seekPos, nickname.length);
			seekPos += nickname.length;
			System.arraycopy(remarksLength, 0, result, seekPos, remarksLength.length);
			seekPos += remarksLength.length;
			System.arraycopy(remarks, 0, result, seekPos, remarks.length);
			seekPos += remarks.length;
			System.arraycopy(repetSend, 0, result, seekPos, repetSend.length);
			seekPos += repetSend.length;
			for(int i = 0;messageBodies != null&&i < messageBodies.size();i ++){
				byte[] actionType = CommonUtil.toHexString(messageBodies.get(i).getActionType(), 1).getBytes(code);
				byte[] message = messageBodies.get(i).getMessage().getBytes(code);
				byte[] messageLength = CommonUtil.toHexString(message.length, 4).getBytes(code);
				System.arraycopy(actionType, 0, result, seekPos, actionType.length);
				seekPos += actionType.length;
				System.arraycopy(messageLength, 0, result, seekPos, messageLength.length);
				seekPos += messageLength.length;
				System.arraycopy(message, 0, result, seekPos, message.length);
				seekPos += message.length;
			}
			LogPrint.Print("im","send-205: result.len = "+result.length);
			LogPrint.Print("im","发送消息，tag:205");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	
	//发送私信，tag:207
	private byte[] addTag207(Entity pe){
		byte[] result = null;
		try {
			int length = 0;
			byte[] tag = CommonUtil.toHexString(207, 4).getBytes(code);//标识
			byte[] tempMessageId = CommonUtil.toHexString(pe.getTempMessageId(), 16).getBytes(code);//流水号
			byte[] commenttime = new byte[7];//评论时间
			String strcommentTime = pe.getCommenttime();
			commenttime[0]=(byte)(((Integer.parseInt(strcommentTime.substring(0, 1)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(1, 2)) & 0x0f))));
			commenttime[1]=(byte)(((Integer.parseInt(strcommentTime.substring(2, 3)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(3, 4)) & 0x0f))));
			commenttime[2]=(byte)(((Integer.parseInt(strcommentTime.substring(4, 5)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(5, 6)) & 0x0f))));//月
			commenttime[3]=(byte)(((Integer.parseInt(strcommentTime.substring(6, 7)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(7, 8)) & 0x0f))));//日
			commenttime[4]=(byte)(((Integer.parseInt(strcommentTime.substring(8, 9)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(9, 10)) & 0x0f))));//时
			commenttime[5]=(byte)(((Integer.parseInt(strcommentTime.substring(10, 11)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(11, 12)) & 0x0f))));//分
			commenttime[6]=(byte)(((Integer.parseInt(strcommentTime.substring(12, 13)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(13, 14)) & 0x0f))));//秒
			byte[] revicerUserId = CommonUtil.toHexString(pe.getRevicerUserId(), 8).getBytes(code);//接收人id
			byte[] nickname = pe.getNickName().getBytes(code);//昵称
			byte[] nicknameLength = CommonUtil.toHexString(nickname.length, 2).getBytes(code);//昵称长度
			byte[] remarks = pe.getRemarks().getBytes(code);//备注
			byte[] remarksLength = CommonUtil.toHexString(remarks.length, 2).getBytes(code);//备注长度
			byte[] repetSend = CommonUtil.toHexString(pe.getRepetSend(), 1).getBytes(code);//是否重发
			byte[] cid = CommonUtil.toHexString(pe.getCid(), 16).getBytes(code);//商品id
			
			length = tag.length+tempMessageId.length+commenttime.length+revicerUserId.length+nicknameLength.length+nickname.length+remarksLength.length+remarks.length+repetSend.length+cid.length;
			
			ArrayList<MessageBody> messageBodies = pe.getMessageBodies();
			for(int i = 0;messageBodies != null&&i < messageBodies.size();i ++){
				byte[] body = messageBodies.get(i).getMessage().getBytes(code);
				length += 1;
				length += 4;
				length += body.length;
			}
			
			result = new byte[4+length];
			//新增数据总长度，用于断包时连包使用
			byte[] bodyMaxLen = CommonUtil.toHexString(result.length, 4).getBytes(code);
			int seekPos = 0;
			System.arraycopy(tag, 0, result, seekPos, tag.length);
			seekPos += tag.length;
			System.arraycopy(bodyMaxLen, 0, result, tag.length, bodyMaxLen.length);//新增数据总长度
			seekPos += bodyMaxLen.length;
			System.arraycopy(tempMessageId, 0, result, seekPos, tempMessageId.length);
			seekPos += tempMessageId.length;
			System.arraycopy(commenttime, 0, result, seekPos, commenttime.length);
			seekPos += commenttime.length;
			System.arraycopy(revicerUserId, 0, result, seekPos, revicerUserId.length);
			seekPos += revicerUserId.length;
			System.arraycopy(nicknameLength, 0, result, seekPos, nicknameLength.length);
			seekPos += nicknameLength.length;
			System.arraycopy(nickname, 0, result, seekPos, nickname.length);
			seekPos += nickname.length;
			System.arraycopy(remarksLength, 0, result, seekPos, remarksLength.length);
			seekPos += remarksLength.length;
			System.arraycopy(remarks, 0, result, seekPos, remarks.length);
			seekPos += remarks.length;
			System.arraycopy(repetSend, 0, result, seekPos, repetSend.length);
			seekPos += repetSend.length;
			System.arraycopy(cid, 0, result, seekPos, cid.length);
			seekPos += cid.length;
			for(int i = 0;messageBodies != null&&i < messageBodies.size();i ++){
				byte[] actionType = CommonUtil.toHexString(messageBodies.get(i).getActionType(), 1).getBytes(code);
				byte[] message = messageBodies.get(i).getMessage().getBytes(code);
				byte[] messageLength = CommonUtil.toHexString(message.length, 4).getBytes(code);
				System.arraycopy(actionType, 0, result, seekPos, actionType.length);
				seekPos += actionType.length;
				System.arraycopy(messageLength, 0, result, seekPos, messageLength.length);
				seekPos += messageLength.length;
				System.arraycopy(message, 0, result, seekPos, message.length);
				seekPos += message.length;
			}
			LogPrint.Print("im","send-207: result.len = "+result.length);
			LogPrint.Print("im","发送私信，tag:207");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	
	//接收成功反馈服务器，tag:208
	private byte[] addTag208(Entity pe){
		byte[] result = null;
		try {
			byte[] tag = CommonUtil.toHexString(208, 4).getBytes(code);//标识
			byte[] messageId = CommonUtil.toHexString(pe.getMessageid(), 16).getBytes(code);//消息id
			byte[] state = CommonUtil.toHexString(0, 1).getBytes(code);//状态
			byte[] revicerUserId = CommonUtil.toHexString(pe.getRevicerUserId(), 8).getBytes(code);//接收人id
			
			result = new byte[4+tag.length+messageId.length+state.length+revicerUserId.length];
			//新增数据总长度，用于断包时连包使用
			byte[] bodyMaxLen = CommonUtil.toHexString(result.length, 4).getBytes(code);
			int seekPos = 0;
			System.arraycopy(tag, 0, result, seekPos, tag.length);
			seekPos += tag.length;
			System.arraycopy(bodyMaxLen, 0, result, tag.length, bodyMaxLen.length);//新增数据总长度
			seekPos += bodyMaxLen.length;
			System.arraycopy(messageId, 0, result, seekPos, messageId.length);
			seekPos += messageId.length;
			System.arraycopy(state, 0, result, seekPos, state.length);
			seekPos += state.length;
			System.arraycopy(revicerUserId, 0, result, seekPos, revicerUserId.length);
			LogPrint.Print("im","send-208: result.len = "+result.length);
			LogPrint.Print("im","接收成功反馈服务器，tag:208");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	
	//发送建立好友关系消息，tag:209
	private byte[] addTag209(Entity pe){
		byte[] result = null;
		try {
			int length = 0;
			byte[] tag = CommonUtil.toHexString(209, 4).getBytes(code);//标识
			byte[] tempMessageId = CommonUtil.toHexString(pe.getTempMessageId(), 16).getBytes(code);//流水号
			byte[] commenttime = new byte[7];//评论时间
			String strcommentTime = pe.getCommenttime();
			commenttime[0]=(byte)(((Integer.parseInt(strcommentTime.substring(0, 1)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(1, 2)) & 0x0f))));
			commenttime[1]=(byte)(((Integer.parseInt(strcommentTime.substring(2, 3)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(3, 4)) & 0x0f))));
			commenttime[2]=(byte)(((Integer.parseInt(strcommentTime.substring(4, 5)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(5, 6)) & 0x0f))));//月
			commenttime[3]=(byte)(((Integer.parseInt(strcommentTime.substring(6, 7)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(7, 8)) & 0x0f))));//日
			commenttime[4]=(byte)(((Integer.parseInt(strcommentTime.substring(8, 9)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(9, 10)) & 0x0f))));//时
			commenttime[5]=(byte)(((Integer.parseInt(strcommentTime.substring(10, 11)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(11, 12)) & 0x0f))));//分
			commenttime[6]=(byte)(((Integer.parseInt(strcommentTime.substring(12, 13)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(13, 14)) & 0x0f))));//秒
			byte[] revicerUserId = CommonUtil.toHexString(pe.getRevicerUserId(), 8).getBytes(code);//接收人id
			byte[] nickname = pe.getNickName().getBytes(code);//昵称
			byte[] nicknameLength = CommonUtil.toHexString(nickname.length, 2).getBytes(code);//昵称长度
			byte[] remarks = pe.getRemarks().getBytes(code);//备注
			byte[] remarksLength = CommonUtil.toHexString(remarks.length, 2).getBytes(code);//备注长度
			byte[] repetSend = CommonUtil.toHexString(pe.getRepetSend(), 1).getBytes(code);//是否重发
			
			length = tag.length+tempMessageId.length+commenttime.length+revicerUserId.length+nicknameLength.length+nickname.length+remarksLength.length+remarks.length+repetSend.length;
			
			ArrayList<MessageBody> messageBodies = pe.getMessageBodies();
			for(int i = 0;messageBodies != null&&i < messageBodies.size();i ++){
				byte[] body = messageBodies.get(i).getMessage().getBytes(code);
				length += 1;
				length += 4;
				length += body.length;
			}
			
			result = new byte[4+length];
			//新增数据总长度，用于断包时连包使用
			byte[] bodyMaxLen = CommonUtil.toHexString(result.length, 4).getBytes(code);
			int seekPos = 0;
			System.arraycopy(tag, 0, result, seekPos, tag.length);
			seekPos += tag.length;
			System.arraycopy(bodyMaxLen, 0, result, tag.length, bodyMaxLen.length);//新增数据总长度
			seekPos += bodyMaxLen.length;
			System.arraycopy(tempMessageId, 0, result, seekPos, tempMessageId.length);
			seekPos += tempMessageId.length;
			System.arraycopy(commenttime, 0, result, seekPos, commenttime.length);
			seekPos += commenttime.length;
			System.arraycopy(revicerUserId, 0, result, seekPos, revicerUserId.length);
			seekPos += revicerUserId.length;
			System.arraycopy(nicknameLength, 0, result, seekPos, nicknameLength.length);
			seekPos += nicknameLength.length;
			System.arraycopy(nickname, 0, result, seekPos, nickname.length);
			seekPos += nickname.length;
			System.arraycopy(remarksLength, 0, result, seekPos, remarksLength.length);
			seekPos += remarksLength.length;
			System.arraycopy(remarks, 0, result, seekPos, remarks.length);
			seekPos += remarks.length;
			System.arraycopy(repetSend, 0, result, seekPos, repetSend.length);
			seekPos += repetSend.length;
			for(int i = 0;messageBodies != null&&i < messageBodies.size();i ++){
				byte[] actionType = CommonUtil.toHexString(messageBodies.get(i).getActionType(), 1).getBytes(code);
				byte[] message = messageBodies.get(i).getMessage().getBytes(code);
				byte[] messageLength = CommonUtil.toHexString(message.length, 4).getBytes(code);
				System.arraycopy(actionType, 0, result, seekPos, actionType.length);
				seekPos += actionType.length;
				System.arraycopy(messageLength, 0, result, seekPos, messageLength.length);
				seekPos += messageLength.length;
				System.arraycopy(message, 0, result, seekPos, message.length);
				seekPos += message.length;
			}
			LogPrint.Print("im","send-209: result.len = "+result.length);
			LogPrint.Print("im","发送建立好友关系消息，tag:209");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	
	//发送确认建立好友关系消息，tag:210
	private byte[] addTag210(Entity pe){
		byte[] result = null;
		try {
			int length = 0;
			byte[] tag = CommonUtil.toHexString(210, 4).getBytes(code);//标识
			byte[] tempMessageId = CommonUtil.toHexString(pe.getTempMessageId(), 16).getBytes(code);//流水号
			byte[] commenttime = new byte[7];//评论时间
			String strcommentTime = pe.getCommenttime();
			commenttime[0]=(byte)(((Integer.parseInt(strcommentTime.substring(0, 1)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(1, 2)) & 0x0f))));
			commenttime[1]=(byte)(((Integer.parseInt(strcommentTime.substring(2, 3)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(3, 4)) & 0x0f))));
			commenttime[2]=(byte)(((Integer.parseInt(strcommentTime.substring(4, 5)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(5, 6)) & 0x0f))));//月
			commenttime[3]=(byte)(((Integer.parseInt(strcommentTime.substring(6, 7)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(7, 8)) & 0x0f))));//日
			commenttime[4]=(byte)(((Integer.parseInt(strcommentTime.substring(8, 9)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(9, 10)) & 0x0f))));//时
			commenttime[5]=(byte)(((Integer.parseInt(strcommentTime.substring(10, 11)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(11, 12)) & 0x0f))));//分
			commenttime[6]=(byte)(((Integer.parseInt(strcommentTime.substring(12, 13)) & 0x0f) << 4) | (((Integer.parseInt(strcommentTime.substring(13, 14)) & 0x0f))));//秒
			byte[] revicerUserId = CommonUtil.toHexString(pe.getRevicerUserId(), 8).getBytes(code);//接收人id
			byte[] nickname = pe.getNickName().getBytes(code);//昵称
			byte[] nicknameLength = CommonUtil.toHexString(nickname.length, 2).getBytes(code);//昵称长度
			byte[] remarks = pe.getRemarks().getBytes(code);//备注
			byte[] remarksLength = CommonUtil.toHexString(remarks.length, 2).getBytes(code);//备注长度
			byte[] repetSend = CommonUtil.toHexString(pe.getRepetSend(), 1).getBytes(code);//是否重发
			
			length = tag.length+tempMessageId.length+commenttime.length+revicerUserId.length+nicknameLength.length+nickname.length+remarksLength.length+remarks.length+repetSend.length;
			
			ArrayList<MessageBody> messageBodies = pe.getMessageBodies();
			for(int i = 0;messageBodies != null&&i < messageBodies.size();i ++){
				byte[] body = messageBodies.get(i).getMessage().getBytes(code);
				length += 1;
				length += 4;
				length += body.length;
			}
			
			result = new byte[4+length];
			//新增数据总长度，用于断包时连包使用
			byte[] bodyMaxLen = CommonUtil.toHexString(result.length, 4).getBytes(code);
			int seekPos = 0;
			System.arraycopy(tag, 0, result, seekPos, tag.length);
			seekPos += tag.length;
			System.arraycopy(bodyMaxLen, 0, result, tag.length, bodyMaxLen.length);//新增数据总长度
			seekPos += bodyMaxLen.length;
			System.arraycopy(tempMessageId, 0, result, seekPos, tempMessageId.length);
			seekPos += tempMessageId.length;
			System.arraycopy(commenttime, 0, result, seekPos, commenttime.length);
			seekPos += commenttime.length;
			System.arraycopy(revicerUserId, 0, result, seekPos, revicerUserId.length);
			seekPos += revicerUserId.length;
			System.arraycopy(nicknameLength, 0, result, seekPos, nicknameLength.length);
			seekPos += nicknameLength.length;
			System.arraycopy(nickname, 0, result, seekPos, nickname.length);
			seekPos += nickname.length;
			System.arraycopy(remarksLength, 0, result, seekPos, remarksLength.length);
			seekPos += remarksLength.length;
			System.arraycopy(remarks, 0, result, seekPos, remarks.length);
			seekPos += remarks.length;
			System.arraycopy(repetSend, 0, result, seekPos, repetSend.length);
			seekPos += repetSend.length;
			for(int i = 0;messageBodies != null&&i < messageBodies.size();i ++){
				byte[] actionType = CommonUtil.toHexString(messageBodies.get(i).getActionType(), 1).getBytes(code);
				byte[] message = messageBodies.get(i).getMessage().getBytes(code);
				byte[] messageLength = CommonUtil.toHexString(message.length, 4).getBytes(code);
				System.arraycopy(actionType, 0, result, seekPos, actionType.length);
				seekPos += actionType.length;
				System.arraycopy(messageLength, 0, result, seekPos, messageLength.length);
				seekPos += messageLength.length;
				System.arraycopy(message, 0, result, seekPos, message.length);
				seekPos += message.length;
			}
			LogPrint.Print("im","send-210: result.len = "+result.length);
			LogPrint.Print("im","发送确认建立好友关系消息，tag:210");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	
	//发送加好友反馈 213
	private byte[] addTag213(Entity pe){
		byte[] result = null;
		try {
			int seekPos = 0;
			byte[] tag = CommonUtil.toHexString(213, 4).getBytes(code);
			byte[] len = CommonUtil.toHexString(25, 4).getBytes(code);
			byte[] useridSponsor = CommonUtil.toHexString(pe.getUseridSponsor(), 8).getBytes(code);//加好友发起者id
			byte[] useridBeAdd = CommonUtil.toHexString(pe.getUseridBeAdd(), 8).getBytes(code);//接收人id (被动加好友id)			
			byte[] isAddSuccess = CommonUtil.toHexString(pe.getIsAddFriendSccess(), 1).getBytes(code);//加好友结果
			result = new byte[tag.length + len.length + useridSponsor.length + useridBeAdd.length + isAddSuccess.length];			
			System.arraycopy(tag, 0, result, seekPos, tag.length); 
			seekPos += tag.length;
			System.arraycopy(len, 0, result, seekPos, len.length); 
			seekPos += len.length;
			System.arraycopy(useridSponsor, 0, result, seekPos, useridSponsor.length);
			seekPos += useridSponsor.length;
			System.arraycopy(useridBeAdd, 0, result, seekPos, useridBeAdd.length);
			seekPos += useridBeAdd.length;			
			System.arraycopy(isAddSuccess, 0, result, seekPos, isAddSuccess.length);
			seekPos += isAddSuccess.length;
			LogPrint.Print("im","send-213: result.len = "+result.length);
			LogPrint.Print("im","发送加好友反馈，tag:213");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
