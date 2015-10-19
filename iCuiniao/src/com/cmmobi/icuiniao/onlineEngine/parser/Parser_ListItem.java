/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;


/**
 * @author hw
 *
 */
public class Parser_ListItem extends Parser_ParserEngine {

	private int mUserId;//用户id
	private int mAction;
	private int mType;
	private String mHref;
	private String mFlush;//刷新页面地址
	private int mSubjectId;//主题id
	private int mCommentId;//评论id
	
	public Parser_ListItem(){
		super();
		mUserId = -1;
		mAction = -1;
		mType = -1;
		mHref = null;
		mFlush = null;
		mSubjectId = -1;
		mCommentId = -1;
	}
	
	public void setUserId(int value){mUserId = value;}
	public void setHref(String str){mHref = str;}
	public void setAction(int value){mAction = value;}
	public void setType(int value){mType = value;}
	public void setFlush(String str){mFlush = str;}
	public void setSubjectId(int value){mSubjectId = value;}
	public void setCommentId(int value){mCommentId = value;}
	
	public int getUserId(){return mUserId;}
	public String getHref(){return mHref;}
	public int getAction(){return mAction;}
	public int getType(){return mType;}
	public String getFlush(){return mFlush;}
	public int getSubjectId(){return mSubjectId;}
	public int getCommentId(){return mCommentId;}
}
