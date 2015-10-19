/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import java.io.File;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.OfflineTimeService;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.icuiniao.plug.im.IMService;
import com.icuiniao.plug.localmessage.LocalMessageService;

/**
 * @author hw
 *启动页,判断是进入帮助引导或是开机页面
 */
public class FirstPageActivity extends Activity{

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //0正常启动，1正常退出，2进程中断补救
        int type = getIntent().getIntExtra("type", 0);
        if(type == 0){
        	if(UserUtil.userid == 0){
        		UserUtil.userid = -1;
        	}
        	UserUtil.preNetApn = CommonUtil.getPreApn(this);
        	OfflineLog.initAccessFile();
        	//启动离线日志服务
        	startService(new Intent(this, OfflineTimeService.class));
        	//先关闭消息服务
			stopService(new Intent(this,IMService.class));
        }else{
        	if(type == 1){
        		CommonUtil.saveLastExitTime(this);//保存最后一次退出时间
            	finish();
        	}else{//进程中断补救
        		stopService(new Intent(this, IMService.class));
        		stopService(new Intent(this, OfflineTimeService.class));
        		stopService(new Intent(this, LocalMessageService.class));
        		Intent intent1 = new Intent();
        		intent1.putExtra("type", 0);
        		intent1.setClass(this, FirstPageActivity.class);
        		startActivity(intent1);
        		finish();
        	}
        }
	}
	
	public void onResume(){
		super.onResume();
		CommonUtil.isAppOnForeground = CommonUtil.isAppOnForeground(this);
		//0正常启动，1正常退出，2进程中断补救
        int type = getIntent().getIntExtra("type", 0);
        if(type == 1){
        	finish();
        }else{
        	//创建快捷方式
        	if(CommonUtil.getVersion(this) != URLUtil.version){
        		creatIcon();
        		Intent intent = new Intent();
        		intent.putExtra("isInSetting", false);
        		intent.setClass(this, HelpActivity.class);
        		startActivity(intent);
        	}else{
        		creatIcon();
        		if(CommonUtil.getInHelpPage(this)){
        			Intent intent = new Intent();
        			intent.putExtra("isInSetting", false);
        			intent.setClass(this, HelpActivity.class);
        			startActivity(intent);
        		}else{
        			CommonUtil.saveInHelpPage(this, false);
        			startActivity(new Intent(this, WelcomeActivity.class));
        		}
        	}
        }
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		CommonUtil.isAppOnForeground = CommonUtil.isAppOnForeground(this);
	}

	public void creatIcon(){
		int saveVersion = CommonUtil.getVersion(this);
		if(saveVersion == URLUtil.version){//版本相同,比对是否创建过
			if(!CommonUtil.getCreateIcon(this)){
				if(!hasShortcut()){
					addShortcut();
				}
				CommonUtil.saveCreateIcon(this, true);
				LogPrint.Print("version same! create icon");
			}else{
				LogPrint.Print("version same! not create icon");
			}
		}else{
			//版本不同清除缓存文件
			CommonUtil.deleteAll(new File(CommonUtil.dir_cache));
			LogPrint.Print("version deferent! create icon");
			if(!CommonUtil.getCreateIcon(this)){
				if(!hasShortcut()){
					addShortcut();
				}
			}
			CommonUtil.saveVersion(this, URLUtil.version);
			CommonUtil.saveCreateIcon(this, true);
		}
	}
	
//	//判断桌面是否有此快捷方式
//	private boolean hasShortcut() {
//		boolean isInstallShortcut = false;
//		final ContentResolver cr = getContentResolver();
//		final String AUTHORITY = "com.android.launcher.settings";
//		final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
//				+ "/favorites?notify=true");
//		Cursor c = cr.query(CONTENT_URI,
//				new String[] { "title", "iconResource" }, "title=?",
//				new String[] { getString(R.string.app_name)
//						.trim() }, null);
//		if (c != null && c.getCount() > 0) {
//			isInstallShortcut = true;
//		}
//		return isInstallShortcut;
//	}
	
	/**
	 * 判断桌面是否已添加快捷方式
	 * 
	 * @return
	 */
	public boolean hasShortcut() {
		boolean result = false;
		// 获取当前应用名称
		String title = null;
		try {
			final PackageManager pm = getPackageManager();
			title = pm.getApplicationLabel(pm.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA)).toString();
		} catch (Exception e) {
		}

		final String uriStr;
		if (android.os.Build.VERSION.SDK_INT < 8) {
			uriStr = "content://com.android.launcher.settings/favorites?notify=true";
		} else {
			uriStr = "content://com.android.launcher2.settings/favorites?notify=true";
		}
		final Uri CONTENT_URI = Uri.parse(uriStr);
		final Cursor c = getContentResolver().query(CONTENT_URI, null, "title=?", new String[] { title }, null);
		if (c != null && c.getCount() > 0) {
			result = true;
		}
		return result;
	}
	
	/**
	 * 添加快捷方式到桌面 1.给Intent指定action="com.android.launcher.INSTALL_SHORTCUT"
	 * 2.给定义为Intent.EXTRA_SHORTCUT_INENT的Intent设置与安装时一致的action(必须要有)
	 * 3.添加权限:com.android.launcher.permission.INSTALL_SHORTCUT
	 */
	public void addShortcut() {
		Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name));
		shortcut.putExtra("duplicate", false); // 不允许重复创建 不一定有作用

		// 指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
		ComponentName comp = new ComponentName(getPackageName(), "com.cmmobi.icuiniao.Activity.FirstPageActivity");
		Intent loadIntent = new Intent(Intent.ACTION_MAIN).setComponent(comp);
		loadIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		loadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		loadIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, loadIntent);
		// 快捷方式的图标
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(this, R.drawable.icon);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
		sendBroadcast(shortcut);
	}
	
//	//创建快捷方式
//	private void addShortcut() {
//		Intent shortcut = new Intent(
//				"com.android.launcher.action.INSTALL_SHORTCUT");
//
//		// 快捷方式的名称
//		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
//				getString(R.string.app_name));
//		shortcut.putExtra("duplicate", false); // 不允许重复创建
//
//		// 指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
//		// 注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程序
//		ComponentName comp = new ComponentName(this.getPackageName(), "."
//				+ this.getLocalClassName());
//		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
//				Intent.ACTION_MAIN).setComponent(comp));
//
//		// 快捷方式的图标
//		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(
//				this, R.drawable.icon);
//		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
//
//		sendBroadcast(shortcut);
//	}
	
	//删除快捷方式
	private void delShortcut() {
		Intent shortcut = new Intent(
				"com.android.launcher.action.UNINSTALL_SHORTCUT");

		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.app_name));

		// 指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
		// 注意: ComponentName的第二个参数必须是完整的类名（包名+类名），否则无法删除快捷方式
		ComponentName comp = new ComponentName(this.getPackageName(), "com.cmmobi.icuiniao.Activity.FirstPageActivity");
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
				Intent.ACTION_MAIN).setComponent(comp));

		sendBroadcast(shortcut);

	}
}
