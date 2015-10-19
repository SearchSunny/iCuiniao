/**
 * 
 */
package com.cmmobi.icuiniao.util;

/**
 * @author lenovo
 *
 */
public class DownImageItem {

	private int modeltype;//模块类型
	private int itemIndex;//索引
	private String url;
	private boolean mbUsed;//是否使用中
	private int pageid;//当前的页面id
	private int tag;//有效域的表示,可用页码表示
	public DownImageItem(int modeltype,String url, int pageid, int tag){
		this.modeltype = modeltype;
		this.itemIndex = DownImageManager.Size();
		this.url = url;
		mbUsed = false;
		this.pageid = pageid;
		this.tag = tag;
	}
	
	public DownImageItem(int modeltype, int itemindex, String url, int pageid, int tag){
		this.modeltype = modeltype;
		this.itemIndex = itemindex;
		this.url = url;
		mbUsed = false;
		this.pageid = pageid;
		this.tag = tag;
	}
	
	public void set(int modeltype, int itemindex, String url, int pageid, int tag){
		this.modeltype = modeltype;
		this.itemIndex = itemindex;
		this.url = url;
		this.pageid = pageid;
		this.tag = tag;
	}
	
	public void setUsedState(boolean b){
		mbUsed = b;
	}
	
	public int getType(){
		return modeltype;
	}
	
	public int getIndex(){
		return itemIndex;
	}
	
	public String getUrl(){
		return url;
	}
	
	public boolean getUsedState(){
		return mbUsed;
	}
	
	public int getPageId(){
		return pageid;
	}
	
	public int getTag(){
		return tag;
	}
}
