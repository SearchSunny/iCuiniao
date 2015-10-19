/**
 * 
 */
package com.cmmobi.icuiniao.ui.view;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;


/**
 * @author hw
 *用于存储单品页数据的虚引用
 */
public class MyPageSoftReference {

	private Map<String, SoftReference<MyPageView>> cacheMap;
	
	public MyPageSoftReference(){
		cacheMap = new HashMap<String, SoftReference<MyPageView>>();
	}
	
	public void add(MyPageView page,String tag){
		if(cacheMap.get(tag) != null){
			cacheMap.remove(tag);
		}
		SoftReference<MyPageView> reference = new SoftReference<MyPageView>(page);
		cacheMap.put(tag, reference);
	}
	
	public MyPageView get(String tag){
		SoftReference<MyPageView> reference = cacheMap.get(tag);
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
			MyPageView view = get(""+index); 
			if(view != null){
				view.recycle();
			}
			cacheMap.get(""+index).clear();
			cacheMap.remove(""+index);
		}
	}
	
	public void destory(){
		cacheMap = null;
	}
	
	public void recycle(int index){
//		int num = 10;
//		if(android.os.Build.VERSION.SDK_INT <= 7){ //2.1及以下
//			num = 5;
//		}		
//		if(CommonUtil.getDeviceName().toLowerCase().indexOf("n7108") >= 0){
//			num = 5;
//		}
		int num = 5;
		remove(index-num);
		remove(index+num);
		System.gc();
	}
}
