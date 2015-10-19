/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;

/**
 * @author hw
 *
 */
public class Parser_ListItem_Comment_C extends Parser_ListItem {

	private Parser_Image mImage;//用户头像
	private Parser_Text mName;//用户昵称
	private Parser_Text mCommentTime;//发布时间
	private Parser_Text mCommentMsg;//评论内容
	private Parser_Button mCommentButton;//评论按钮
	
	public Parser_ListItem_Comment_C(){
		super();
		mImage = null;
		mName = null;
		mCommentTime = null;
		mCommentMsg = null;
		mCommentButton = null;
	}
	
	public void setImage(Parser_Image image){mImage = image;}
	public void setName(Parser_Text text){mName = text;}
	public void setCommentTime(Parser_Text text){mCommentTime = text;}
	public void setCommentMsg(Parser_Text text){mCommentMsg = text;}
	public void setCommentButton(Parser_Button button){mCommentButton = button;}
	
	public Parser_Image getImage(){return mImage;}
	public Parser_Text getName(){return mName;}
	public Parser_Text getCommentTime(){return mCommentTime;}
	public Parser_Text getCommentMsg(){return mCommentMsg;}
	public Parser_Button getCommentButton(){return mCommentButton;}
}
