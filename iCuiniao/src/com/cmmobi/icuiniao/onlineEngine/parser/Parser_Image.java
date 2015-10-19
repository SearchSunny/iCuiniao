/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;


/**
 * @author hw
 *
 */
public class Parser_Image extends Parser_ParserEngine{

	private String mSrc;
	private String mHref;
	private int mAction;
	private boolean mClickable;
	private boolean mPlayable;
	private int mType;
	private int id;
	private int weight = -1;
	private String price;//价格
	private String feature;//特色
	private boolean validable;//是否有效
	
	public Parser_Image(){
		super();
		mSrc = null;
		mHref = null;
		mAction = -1;
		mClickable = false;
		mPlayable = false;
		mType = -1;
		id = -1;
		price = null;
		feature = null;
		validable = true;
	}
	
	public void setSrc(String str){mSrc = str;}
	public void setHref(String str){mHref =str;}
	public void setAction(int value){mAction = value;}
	public void setClickable(int value){mClickable = value==TRUE?true:false;}
	public void setPlayable(int value){mPlayable = value==TRUE?true:false;}
	public void setType(int value){mType = value;}
	public void setId(String value){
		if(value != null&&value.length() > 0){
			id = Integer.parseInt(value);
		}
	}
	public void setWeight(String value){
		if(value != null&&value.length() > 0){
			weight = Integer.parseInt(value);
		}
	}
	public void setPrice(String value){price = value;}
	public void setFeature(String value){feature = value;}
	public void setValidable(int value){validable = value==TRUE?true:false;;}
	
	public String getSrc(){return mSrc;}
	public String getHref(){return mHref;}
	public int getAction(){return mAction;}
	public boolean getClickable(){return mClickable;}
	public boolean getPlayable(){return mPlayable;}
	public int getType(){return mType;}
	public int getId(){return id;}
	public int getWeight() {return weight;}
	public String getPrice(){return price;}
	public String getFeature(){return feature;}
	public boolean getValidable(){return validable;}
}
