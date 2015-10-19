/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;

/**
 * @author hw
 *
 */
public class Parser_CommodityBar extends Parser_ParserEngine {

	private Parser_CommodityButton mCommodityButton;//商品按钮
	private Parser_ActivityButton mActivityButton;//活动按钮
	
	public Parser_CommodityBar(){
		super();
		mCommodityButton = null;
		mActivityButton = null;
	}
	
	public void setCommodityButton(Parser_CommodityButton button){mCommodityButton = button;}
	public void setActivityButton(Parser_ActivityButton button){mActivityButton = button;}
	
	public Parser_CommodityButton getCommodityButton(){return mCommodityButton;}
	public Parser_ActivityButton getActivityButton(){return mActivityButton;}
}
