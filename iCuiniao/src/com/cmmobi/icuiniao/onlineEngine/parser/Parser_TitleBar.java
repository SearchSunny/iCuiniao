/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;

/**
 * @author hw
 *
 */
public class Parser_TitleBar extends Parser_ParserEngine {

	private String mStr;
	private boolean mMenuable;
	private int mType;
	private Parser_Button mLeftButton;
	private Parser_Button mMiddleButton;
	private Parser_Button mRightButton;
	
	public Parser_TitleBar(){
		super();
		mStr = null;
		mMenuable = false;
		mType = DEFAULT;
		mLeftButton = null;
		mMiddleButton = null;
		mRightButton = null;
	}
	
	public void setStr(String str){mStr = str;}
	public void setMenuable(int value){mMenuable = value==TRUE?true:false;}
	public void setType(int value){mType = value;}
	public void setLeftButton(Parser_Button button){mLeftButton = button;}
	public void setMiddleButton(Parser_Button button){mMiddleButton = button;}
	public void setRightButton(Parser_Button button){mRightButton = button;}
	
	public String getStr(){return mStr;}
	public boolean getMenuable(){return mMenuable;}
	public int getType(){return mType;}
	public Parser_Button getLeftButton(){return mLeftButton;}
	public Parser_Button getMiddleButton(){return mMiddleButton;}
	public Parser_Button getRightButton(){return mRightButton;}
}
