/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;

/**
 * @author lyb
 * lyb for userInfo_new
 *
 */
public class Parser_UserInfo_new extends Parser_ParserEngine {

	private Parser_Text mName;//用户昵称
	private Parser_Text mGender;//用户性别
	private Parser_Text mAddr;//用户地址
	private Parser_Button mLeftButton;
	private Parser_Button mRightButton;
	private Parser_Image mImage;

	
	public Parser_UserInfo_new(){
		super();
		mName = null;
		mGender = null;
		mAddr = null;
		mLeftButton = null;
		mRightButton = null;
		mImage = null;
	}
	
	public void setName(Parser_Text str){mName = str;}
	public void setGender(Parser_Text str){mGender = str;}
	public void setAddr(Parser_Text str){mAddr = str;}
	public void setLeftButton(Parser_Button button){mLeftButton = button;}
	public void setRightButton(Parser_Button button){mRightButton = button;}
	public void setImage(Parser_Image image){mImage = image;}
	
	public Parser_Text getName(){return mName;}
	public Parser_Text getGender(){return mGender;}
	public Parser_Text getAddr(){return mAddr;}
	public Parser_Button getLeftButton(){return mLeftButton;}
	public Parser_Button getRightButton(){return mRightButton;}
	public Parser_Image getImage(){return mImage;}

	
}
