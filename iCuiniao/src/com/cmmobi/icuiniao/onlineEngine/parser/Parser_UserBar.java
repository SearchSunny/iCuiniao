/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;

/**
 * @author hw
 *
 */
public class Parser_UserBar extends Parser_ParserEngine {

	private Parser_Image mLeftImage;
	
	public Parser_UserBar(){
		super();
		mLeftImage = null;
	}
	
	public void setLeftImage(Parser_Image image){mLeftImage = image;}
	public Parser_Image getLeftImage(){return mLeftImage;}
	
}
