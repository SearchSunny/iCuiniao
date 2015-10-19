/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;

/**
 * @author hw
 *
 */
public class Parser_Commodity extends Parser_ParserEngine {

	private Parser_Image mImage;
	private Parser_Text mCommodityName;
	private Parser_Text mCommodityInfo;
	
	public Parser_Commodity(){
		super();
		mImage = null;
		mCommodityName = null;
		mCommodityInfo = null;
	}
	
	public void setImage(Parser_Image image){mImage = image;}
	public void setCommodityName(Parser_Text text){mCommodityName = text;}
	public void setCommodityInfo(Parser_Text text){mCommodityInfo = text;}
	
	public Parser_Image getImage(){return mImage;}
	public Parser_Text getCommodityName(){return mCommodityName;}
	public Parser_Text getCommodityInfo(){return mCommodityInfo;}
}
