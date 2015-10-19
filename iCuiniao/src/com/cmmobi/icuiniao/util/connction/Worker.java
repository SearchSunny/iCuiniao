/**
 * 
 */
package com.cmmobi.icuiniao.util.connction;

/**
 * @author hw
 *任务工作者
 */
public class Worker {

	private String url;
	private int type;
	private int index;
	public Worker(String url,int type,int index){
		this.url = url;
		this.type = type;
		this.index = index;
	}
	
	public void setUrl(String url){this.url = url;}
	public void setType(int type){this.type = type;}
	public void setIndex(int index){this.index = index;}
	
	public String getUrl(){return url;}
	public int getType(){return type;}
	public int getIndex(){return index;}
}
