/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;

/**
 * @author hw
 *
 */
public class Parser_StyleItem extends Parser_ParserEngine{

	private String mSrc;
	private String mStr;
	private String mId;
	private boolean mSelected;
	
	public Parser_StyleItem(){
		super();
		mSrc = null;
		mStr = null;
		mId = null;
		mSelected = false;
	}
	
	public void setSrc(String value){mSrc = value;}
	public void setStr(String value){mStr = value;}
	public void setId(String value){mId = value;}
	public void setSelected(int value){mSelected = value==TRUE?true:false;}
	
	public String getSrc(){return mSrc;}
	public String getStr(){return mStr;}
	public String getId(){return mId;}
	public boolean getSelected(){return mSelected;}
}
