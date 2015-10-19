/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;


/**
 * @author hw
 *
 */
public class Parser_InputLine extends Parser_ParserEngine {

	private int mAction;
	private String mHref;
	
	public Parser_InputLine(){
		super();
		mAction = -1;
		mHref = null;
	}
	
	public void setHref(String str){mHref =str;}
	public void setAction(int value){mAction = value;}
	
	public String getHref(){return mHref;}
	public int getAction(){return mAction;}
}
