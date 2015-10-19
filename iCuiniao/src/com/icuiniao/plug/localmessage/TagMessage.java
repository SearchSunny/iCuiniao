/**
 * 
 */
package com.icuiniao.plug.localmessage;

/**
 * @author hw
 *
 */
public class TagMessage {

	private int id;
	private String activeTimeStart;//活动时间段,格式yyyy-MM-dd HH:mm:ss
	private String activeTimeEnd;
	private String showTimeStart;//显示时间段,格式HH:mm:ss
	private String showTimeEnd;
	private TagContent content;//文字内容
	
	public TagMessage(){
		id = -1;
		activeTimeStart = "";
		activeTimeEnd = "";
		showTimeStart = "";
		showTimeEnd = "";
	}
	
	public void setId(int value){id = value;}
	public void setActiveTimeStart(String value){activeTimeStart = value;}
	public void setActiveTimeEnd(String value){activeTimeEnd = value;}
	public void setShowTimeStart(String value){showTimeStart = value;}
	public void setShowTimeEnd(String value){showTimeEnd = value;}
	public void setContent(TagContent value){content = value;}
	
	public int getId(){return id;}
	public String getActiveTimeStart(){return activeTimeStart;}
	public String getActiveTimeEnd(){return activeTimeEnd;}
	public String getShowTimeStart(){return showTimeStart;}
	public String getShowTimeEnd(){return showTimeEnd;}
	public TagContent getContent(){return content;}
}
