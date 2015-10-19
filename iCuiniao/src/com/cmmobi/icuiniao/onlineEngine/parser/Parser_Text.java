/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;


/**
 * @author hw
 *
 */
public class Parser_Text extends Parser_ParserEngine{

	private String mStr;
	private String mHref;
	private int mAction;
	private boolean mClickable;
	private int mType;
	
	public Parser_Text(){
		super();
		mStr = null;
		mHref = null;
		mAction = -1;
		mClickable = false;
		mType = -1;
	}
	
	public void setStr(String str){mStr = str;}
	public void setHref(String str){mHref =str;}
	public void setAction(int value){mAction = value;}
	public void setClickable(int value){mClickable = value==TRUE?true:false;}
	public void setType(int value){mType = value;}
	
	public String getStr(){return mStr;}
	public String getHref(){return mHref;}
	public int getAction(){return mAction;}
	public boolean getClickable(){return mClickable;}
	public int getType(){return mType;}
}
