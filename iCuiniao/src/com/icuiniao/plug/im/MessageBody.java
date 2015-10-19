/**
 * 
 */
package com.icuiniao.plug.im;

/**
 * @author hw
 *保存消息内容
 */
public class MessageBody {
	private int actionType;//内容类型，0：文本，1：图片，2：表情，3：音频，4：视频，5：调整单页
	private String Message;
	
	public int getActionType() {
		return actionType;
	}

	public void setActionType(int actionType) {
		this.actionType = actionType;
	}
	
	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}
}
