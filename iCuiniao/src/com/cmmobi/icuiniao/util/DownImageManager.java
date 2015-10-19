/**
 * 
 */
package com.cmmobi.icuiniao.util;

import java.util.ArrayList;

/**
 * @author hw
 *
 *管理需要下载的图片的队列
 */
public class DownImageManager {

	private static ArrayList<DownImageItem> downImageItems = new ArrayList<DownImageItem>();
	private static boolean mbOver = false;//队列中的项是否全部下载完成了
	
	public DownImageManager(){
		
	}
	
	public static void add(DownImageItem imageItem){
		downImageItems.add(imageItem);
	}
	
	public static void delete(int index){
		downImageItems.get(index).setUsedState(true);
	}
	
	public static void clear(){
		mbOver = false;
		downImageItems.clear();
	}
	
	public static void reset(){
		mbOver = false;
	}
	
	//从队列中取出一个可用的下载项,如没有了,返回null
	public static DownImageItem get(){
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
	
	public static DownImageItem get(int index){
		if(downImageItems == null)return null;
		if(downImageItems.size() <= 0)return null;
		
		return downImageItems.get(index);
	}
	
	public static int Size(){
		return downImageItems.size();
	}
}
