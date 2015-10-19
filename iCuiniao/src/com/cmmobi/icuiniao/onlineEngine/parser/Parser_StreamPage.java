/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;

import java.util.ArrayList;

/**
 * @author hw
 *瀑布流
 */
public class Parser_StreamPage extends Parser_ParserEngine {

	private String nexturl;
	private ArrayList<Parser_Image> mImages;//图片数组
	private boolean empty;
	private ArrayList<String> urls;//保存全部的跳转地址
	
	public Parser_StreamPage(){
		super();
		empty = false;
		mImages = new ArrayList<Parser_Image>();
		urls = new ArrayList<String>();
	}
	
	public void setNextUrl(String str){nexturl = str;}
	public void addImage(Parser_Image image){mImages.add(image);}
	public void setEmpty(int value){empty = value==TRUE?true:false;}
	public void addUrl(String url){
		if(url!=null&&url.trim().length() > 0){
			urls.add(url);
		}
	}
	
	public String getNextUrl(){return nexturl;}
	public ArrayList<Parser_Image> getImages(){return mImages;}
	public boolean getEmpty(){return empty;}
	public ArrayList<String> getUrls(){return urls;}
}
