/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;


/**
 * @author hw
 *
 */
public class Parser_Page extends Parser_ParserEngine{

	private int mVersion;//版本号
	private int mUserid;//用户id
	private int mCommodityid;//商品id
	private int mTotalpage;//总页数
	private int mType;//页面展示方式，全屏或窗口
	private String mWapUrl;//第三方页面链接地址
	private int mMerchantid;//商户id
	private boolean mPartRender;
	private String preUrl;//上一个单品页的地址
	private String nextUrl;//下一个单品页的地址
	private int curPageId;//当前单品页在首页中的页码
	private int mUid;//用于他人喜欢返回的时候的记录
	private boolean empty;//页面是否为空
	
	public Parser_Page(){
		super();
		mVersion = VERSION;
		mUserid = -1;
		mCommodityid = -1;
		mTotalpage = 1;
		mType = DEFAULT;
		mWapUrl = null;
		mMerchantid = -1;
		mPartRender = false;
		preUrl = null;
		nextUrl = null;
		curPageId = -1;
		mUid = -1;
		empty = false;
	}
	
	public void setVersion(String str){
		mVersion = Integer.parseInt(str);
	}
	
	public void setUserId(String str){
		mUserid = Integer.parseInt(str);
	}
	
	public void setCommodityId(String str){
		mCommodityid = Integer.parseInt(str);
	}
	
	public void setTotalpage(String str){
		mTotalpage = Integer.parseInt(str);
	}
	
	public void setType(String str){
		if(str.equalsIgnoreCase(VALUE_DIALOG)){
			mType = TYPE_DIALOG;
		}else{
			mType = DEFAULT;
		}
	}
	
	public void setWapUrl(String str){
		mWapUrl = str;
	}
	
	public void setMerchantId(String str){
		mMerchantid = Integer.parseInt(str);
	}
	
	public void setPartRender(int value){mPartRender = value==TRUE?true:false;}
	
	public void setPreUrl(String str){
		preUrl = str;
		if(str == null||str.length() <= 0){
			preUrl = null;
		}
	}
	
	public void setNextUrl(String str){
		nextUrl = str;
		if(str == null||str.length() <= 0){
			nextUrl = null;
		}
	}
	
	public void setCurPageId(String str){
		curPageId = Integer.parseInt(str);
	}
	
	public void setUid(String str){
		mUid = Integer.parseInt(str);
	}
	
	public void setEmpty(int value){empty = value==TRUE?true:false;}
	
	public int getVersion(){return mVersion;}
	public int getUserId(){return mUserid;}
	public int getCommodityId(){return mCommodityid;}
	public int getTotalPage(){return mTotalpage;}
	public int getType(){return mType;}
	public String getWapUrl(){return mWapUrl;}
	public int getMerchantId(){return mMerchantid;}
	public boolean getPartRender(){return mPartRender;}
	public String getPreUrl(){return preUrl;}
	public String getNextUrl(){return nextUrl;}
	public int getCurPageId(){return curPageId;}
	public int getUid(){return mUid;}
	public boolean getEmpty(){return empty;}
}
