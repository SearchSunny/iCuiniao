/**
 * 
 */
package com.cmmobi.icuiniao.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

/**
 * @author hw
 *
 *获得应用程序信息
 */
public class AppInfoUtil {

	private Context context;
	
	public AppInfoUtil(Context context){
		this.context = context;
	}
	
	//获得所有分享功能的应用信息
	public ArrayList<AppInfo> getShareAppInfo(){
		ArrayList<AppInfo> mAppInfoList = new ArrayList<AppInfo>();
		List<ResolveInfo> list = getActionSendTargets();
		for(int i = 0;i < list.size();i ++){
			ResolveInfo resolveInfo = list.get(i);
			AppInfo info = new AppInfo();
			info.appName = resolveInfo.activityInfo.loadLabel(context.getPackageManager()).toString();
			info.packageName = resolveInfo.activityInfo.packageName;
			info.className = resolveInfo.activityInfo.applicationInfo.className;
			info.appIcon = resolveInfo.activityInfo.loadIcon(context.getPackageManager());
			
			mAppInfoList.add(info);
		}
		
		return mAppInfoList;
	}
	
	private List<ResolveInfo> getActionSendTargets(){
		Intent intent = new Intent(Intent.ACTION_SEND,null);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		
		PackageManager pManager = context.getPackageManager();
		
		return pManager.queryIntentActivities(intent, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
	}
	
	public class AppInfo{
		public String appName;
		public String packageName;
		public String className;
		public Drawable appIcon;
	}
}
