/**
 * 
 */
package com.icuiniao.plug.localmessage;

/**
 * @author hw
 *info数据段数据封装
 */
public class TagInfo {

	private TagMessage message;
	private TagUser user;
	
	public TagInfo(){
		
	}
	
	public void setMessage(TagMessage value){message = value;}
	public void setUser(TagUser value){user = value;}
	
	public TagMessage getMessage(){return message;}
	public TagUser getUser(){return user;}
}
