/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;

import java.util.ArrayList;

/**
 * @author hw
 *我的撒娇列表
 */
public class Parser_Coquetry extends Parser_ParserEngine {

	private boolean mPartRender;
	private ArrayList<Parser_Coquetryitem> mCoquetryitems;
	private boolean empty;
	private ArrayList<String> urls;
	
	public Parser_Coquetry(){
		super();
		mPartRender = false;
		empty = false;
		mCoquetryitems = new ArrayList<Parser_Coquetryitem>();
		urls = new ArrayList<String>();
	}
	
	public void setPartRender(int value){mPartRender = value==TRUE?true:false;}
	public void addCoquetryList(Parser_Coquetryitem item){mCoquetryitems.add(item);}
	public void setEmpty(int value){empty = value==TRUE?true:false;}
	public void addUrls(String url){
		if(url!=null&&url.trim().length() > 0){
			urls.add(url);
		}
	}
	
	public boolean getPartRender(){return mPartRender;}
	public ArrayList<Parser_Coquetryitem> getCoquetryitems(){return mCoquetryitems;}
	public boolean getEmpty(){return empty;}
	public ArrayList<String> getUrls(){return urls;}
}
