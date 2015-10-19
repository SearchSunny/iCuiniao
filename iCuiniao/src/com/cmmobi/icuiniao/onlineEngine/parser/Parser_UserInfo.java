/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;

/**
 * @author hw
 *
 */
public class Parser_UserInfo extends Parser_ParserEngine {

	private Parser_Text mName;//用户昵称
	private Parser_Text mGender;//用户性别
	private Parser_Text mAddr;//用户地址
	private Parser_LikeButton mLikeButton;//喜欢按钮
	private Parser_FormButton mFormButton;//晒单按钮
	
	public Parser_UserInfo(){
		super();
		mName = null;
		mGender = null;
		mAddr = null;
		mLikeButton = null;
		mFormButton = null;
	}
	
	public void setName(Parser_Text str){mName = str;}
	public void setGender(Parser_Text str){mGender = str;}
	public void setAddr(Parser_Text str){mAddr = str;}
	public void setLikeButton(Parser_LikeButton likeButton){mLikeButton = likeButton;}
	public void setFormButton(Parser_FormButton formButton){mFormButton = formButton;}
	
	public Parser_Text getName(){return mName;}
	public Parser_Text getGender(){return mGender;}
	public Parser_Text getAddr(){return mAddr;}
	public Parser_LikeButton getLikeButton(){return mLikeButton;}
	public Parser_FormButton getFormButton(){return mFormButton;}
	
}
