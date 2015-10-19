/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;

/**
 * @author hw
 *
 */
public class Parser_ListItem_Friends extends Parser_ListItem {

	private Parser_Image mImage;//用户头像
	private Parser_Text mName;//用户昵称
	private Parser_Text mGender;//用户性别
	private Parser_Text mAddr;//用户地址
	
	public Parser_ListItem_Friends(){
		super();
		mImage = null;
		mName = null;
		mGender = null;
		mAddr = null;
	}
	
	public void setImage(Parser_Image image){mImage = image;}
	public void setName(Parser_Text text){mName = text;}
	public void setGender(Parser_Text text){mGender = text;}
	public void setAddr(Parser_Text text){mAddr = text;}
	
	public Parser_Image getImage(){return mImage;}
	public Parser_Text getName(){return mName;}
	public Parser_Text getGender(){return mGender;}
	public Parser_Text getAddr(){return mAddr;}
}
