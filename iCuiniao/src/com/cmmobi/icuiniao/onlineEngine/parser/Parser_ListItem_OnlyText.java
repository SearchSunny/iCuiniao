/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;

/**
 * @author hw
 *
 */
public class Parser_ListItem_OnlyText extends Parser_ListItem {

	
	private Parser_Image mImage;//用户头像
	private Parser_Text mText;//用户昵称
	
	public Parser_ListItem_OnlyText(){
		super();
		mImage = null;
		mText = null;
	}
	
	
	public void setImage(Parser_Image image){mImage = image;}
	public void setText(Parser_Text text){mText = text;}
	
	public Parser_Image getImage(){return mImage;}
	public Parser_Text getText(){return mText;}
	
}
