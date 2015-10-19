/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;

/**
 * @author hw
 *
 */
public class Parser_CommodityInfo extends Parser_ParserEngine {

	private Parser_Image mImage;//图片
	private Parser_Button mFormButton;//晒单按钮
	private Parser_Button mLikeButton;//喜欢按钮
	private Parser_Text mCommodityInfo;//商品介绍
	private Parser_Text mCommodityTime;//发布时间
	
	public Parser_CommodityInfo(){
		super();
		mImage = null;
		mFormButton = null;
		mLikeButton = null;
		mCommodityInfo = null;
		mCommodityTime = null;
	}
	
	public void setImage(Parser_Image image){mImage = image;}
	public void setFormButton(Parser_Button button){mFormButton = button;}
	public void setLikeButton(Parser_Button button){mLikeButton = button;}
	public void setCommodityInfo(Parser_Text text){mCommodityInfo = text;}
	public void setCommodityTime(Parser_Text text){mCommodityTime = text;}
	
	public Parser_Image getImage(){return mImage;}
	public Parser_Button getFormButton(){return mFormButton;}
	public Parser_Button getLikeButton(){return mLikeButton;}
	public Parser_Text getCommodityInfo(){return mCommodityInfo;}
	public Parser_Text getCommodityTime(){return mCommodityTime;}
}
