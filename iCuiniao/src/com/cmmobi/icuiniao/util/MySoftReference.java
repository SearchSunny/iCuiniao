/**
 * 
 */
package com.cmmobi.icuiniao.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import com.cmmobi.icuiniao.onlineEngine.view.MLinearLayout;

/**
 * @author hw
 *用于首页的换存,保存page对象布局
 */
public class MySoftReference {

	private static MySoftReference sReference;
	private static Map<String, SoftReference<MLinearLayout>> cacheMap;
	
	public MySoftReference(){
		cacheMap = new HashMap<String, SoftReference<MLinearLayout>>();
	}
	
	public static MySoftReference getInstance(){
		if(sReference == null){
			sReference = new MySoftReference();
		}
		return sReference;
	}
	
	public void add(MLinearLayout page,String tag){
		if(cacheMap.get(tag) != null){
			cacheMap.remove(tag);
		}
		SoftReference<MLinearLayout> reference = new SoftReference<MLinearLayout>(page);
		cacheMap.put(tag, reference);
	}
	
	public MLinearLayout get(String tag){
		SoftReference<MLinearLayout> reference = cacheMap.get(tag);
		if(reference == null){
			return null;
		}
		return reference.get();
	}
	
	public void clear(){
		cacheMap.clear();
	}
	
	public void remove(int index){
		if(cacheMap.get(""+index) != null){
			cacheMap.remove(""+index);
		}
	}
	
	public void destory(){
		cacheMap = null;
		sReference = null;
	}
}
