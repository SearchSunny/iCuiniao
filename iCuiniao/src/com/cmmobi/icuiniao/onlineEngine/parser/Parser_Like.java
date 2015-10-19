/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;


/**
 * @author hw
 *
 */
public class Parser_Like extends Parser_ParserEngine {

	private Parser_LikeItem mLikeItem;
	
	public Parser_Like(){
		super();
	}
	
	public void setLikeItem(Parser_LikeItem item){mLikeItem = item;}
	public Parser_LikeItem getLikeItem(){return mLikeItem;}
}
