/**
 * 
 */
package com.cmmobi.icuiniao.util;

/**
 * @author hw
 *信息项
 */
public class MessageItem {

	private int from;
	private int to;
	private String from_name;
	private String to_name;
	private String msg;
	private String time;
	private int index;
	////推荐的id
	private int pushid;
	public MessageItem(int from,int to,String from_name,String to_name,String msg,String time,int index){
		this.from = from;
		this.to = to;
		this.from_name = from_name;
		this.to_name = to_name;
		this.msg = msg;
		this.time = time;
		this.index = index;
	}
	
	public void setFrom(int from){this.from = from;}
	public void setTo(int to){this.to = to;}
	public void setFromName(String name){this.from_name = name;}
	public void setToName(String name){this.to_name = name;}
	public void setMsg(String msg){this.msg = msg;}
	public void setTime(String time){this.time = time;}
	public void setIndex(int index){this.index = index;}
	
	public int getFrom(){return from;}
	public int getTo(){return to;}
	public String getFromName(){return from_name;}
	public String getToName(){return to_name;}
	public String getMsg(){return msg;}
	public String getTime(){return time;}
	public int getIndex(){return index;}

	public int getPushid() {
		return pushid;
	}

	public void setPushid(int pushid) {
		this.pushid = pushid;
	}
	
	
}
