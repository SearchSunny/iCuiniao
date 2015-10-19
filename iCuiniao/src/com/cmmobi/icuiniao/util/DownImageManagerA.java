/**
 * 
 */
package com.cmmobi.icuiniao.util;

import java.util.ArrayList;

/**
 * @author hw
 *管理需要下载的图片的队列(单品页，首页)
 */
public class DownImageManagerA {

	/**
	 * 
	 */
	private ArrayList<DownImageItem> downImageItems = new ArrayList<DownImageItem>();
	/**
	 * 队列中的项是否全部下载完成了
	 */
	private boolean mbOver = false;
	
	public DownImageManagerA(){
		
	}
	
	public void add(DownImageItem imageItem){
		downImageItems.add(imageItem);
	}
	
	public void delete(int index){
		downImageItems.get(index).setUsedState(true);
	}
	
	public void clear(){
		mbOver = false;
		downImageItems.clear();
	}
	
	public void reset(){
		mbOver = false;
	}
	
	//从队列中取出一个可用的下载项,如没有了,返回null
	public DownImageItem get(){
		if(downImageItems == null)return null;
		if(downImageItems.size() <= 0)return null;
		if(mbOver)return null;
		
		int size = downImageItems.size();
		for(int i = 0;i < size; i ++){
			if(!downImageItems.get(i).getUsedState()){
				downImageItems.get(i).setUsedState(true);
				mbOver = false;
				return downImageItems.get(i);
			}
		}
		mbOver = true;
		
		return null;
	}
	
	public DownImageItem get(int index){
		if(downImageItems == null)return null;
		if(downImageItems.size() <= 0)return null;
		
		return downImageItems.get(index);
	}
	
	public int Size(){
		return downImageItems.size();
	}
	
	/**
	 * 获得未下载的个数
	 * @return
	 */
	public int getUnDownLoadSize(){
		int result = 0;
		if(downImageItems == null)return 0;
		if(downImageItems.size() <= 0)return 0;
		if(mbOver)return 0;
		
		int size = downImageItems.size();
		for(int i = 0;i < size; i ++){
			if(!downImageItems.get(i).getUsedState()){
				result ++;
			}
		}
		
		return result;
	}
	
}
