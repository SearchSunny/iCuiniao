/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;


/**
 * @author hw
 *
 */
public class Parser_Button extends Parser_ParserEngine {

	private String mStr;
	private String mHref;
	private int mAction;
	private int mType;
	private boolean like;
	private int likeId;
	
	public Parser_Button(){
		super();
		mStr = null;
		mHref = null;
		mAction = -1;
		mType = -1;
		like = false;
		likeId = -1;
	}
	
	public void setStr(String str){mStr = str;}
	public void setHref(String str){mHref =str;}
	public void setAction(int value){mAction = value;}
	public void setType(int value){mType = value;}
	public void setLike(int value){like = value==TRUE?true:false;}
	public void setLikeId(String value){
		if(value != null&&value.length() > 0){
			likeId = Integer.parseInt(value);
		}
	}
	
	public String getStr(){return mStr;}
	public String getHref(){return mHref;}
	public int getAction(){return mAction;}
	public int getType(){return mType;}
	public boolean getLike(){return like;}
	public int getLikeId(){return likeId;}
	
}
