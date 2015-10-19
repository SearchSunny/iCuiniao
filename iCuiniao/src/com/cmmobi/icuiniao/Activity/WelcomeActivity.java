  package com.cmmobi.icuiniao.Activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.activity.MainPageActivityA;
import com.cmmobi.icuiniao.onlineEngine.activity.MyPageActivityA;
import com.cmmobi.icuiniao.util.ActionID;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.DownLoadPool;
import com.cmmobi.icuiniao.util.DownLoadPoolItem;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MHandlerThread;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.MySoftReference;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.OfflineTimeService;
import com.cmmobi.icuiniao.util.TimerForRock;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;
import com.icuiniao.plug.im.IMService;
import com.icuiniao.plug.localmessage.LocalMessageService;

/**
 * @author hw 
 *
 *欢迎界面，进行软件数据初始化操作
 */
  public class WelcomeActivity extends Activity {
	
	private static final int START_SLEEP = 0X0001;
	private static final int END_SLEEP = START_SLEEP + 1;
	private static final int SLEEP_TIMES = 2000;
	
	Intent mIntent;
	private ConnectUtil mConnectUtil;
	private ImageView imgView;
	
	private int startpi;
	private int limit;
	private long startTime;
	private boolean isShowStartTime;
	private boolean autoupdate;//是否获取新的6页数据
	
	private boolean isServiceStart;
	private boolean isPressBack;//是否已经点击了退出
	
	//启动原则:先获取软件更新状态,再上报登陆信息,再获取系统时间,再获取缓存
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMold();
        newHandler();
        isPressBack = false;
        isServiceStart = false;
        autoupdate = false;
        isShowStartTime = true;
        startTime = System.currentTimeMillis();
        CommonUtil.isInMessageScreen = false;
        CommonUtil.isNormalInToApp = true;//标示正常进入app
        //===============调试模式需要在正式发布时关闭================
        //启动/关闭 调试模式
        LogPrint.isPrintLogMsg(true);
        //启动/关闭 本地链接调试
        URLUtil.setIsLocalUrl(false);
        //=========================================================
        try {
			ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
			String icuiniao_channel = bundle.getString("ICUINIAO_CHANNEL");
			LogPrint.Print("ICUINIAO_CHANNEL = "+icuiniao_channel);
			if(!"icuiniao_channel".equals(icuiniao_channel)){
				URLUtil.fid = icuiniao_channel;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
        //安卓市场增加首发
		if (URLUtil.fid.equals(URLUtil.himarket)) {
			setContentView(R.layout.welcome_market);
		}else if(URLUtil.fid.equals(URLUtil.qihu360)){
			setContentView(R.layout.welcome_360);
		}else if(URLUtil.fid.equals(URLUtil.zs91)){
			setContentView(R.layout.welcome_91);
		} else {
			setContentView(R.layout.welcome);
		}
        imgView = (ImageView)findViewById(R.id.img);
        
        init();
        
        mIntent = new Intent(this,MainPageActivityA.class);
        mIntent.putExtra("url", URLUtil.URL_MAINPAGE);
        mIntent.putExtra("firstpage", true);
        mIntent.putExtra("isDownLoadCache", false);
        mIntent.putExtra("cacheUrl", URLUtil.URL_MAINPAGE_CACHE_INFO);
        mIntent.putExtra("cacheUrlType", URLUtil.TYPE_MAINPAGE_CACHE_INFO);
        mIntent.putExtra("threadindex", URLUtil.THREAD_MAINPAGE_CACHE_INFO);
        mIntent.putExtra("pageIndex", 0);
        //判断网络是否存在
        if(CommonUtil.isNetWorkOpen(this)){
        	//2g下不进行版本的检测
        	String apnString = CommonUtil.getApnType(this);
        	if(apnString == null)apnString = "wifi";
        	if(apnString.toLowerCase().indexOf("wifi") < 0&&apnString.toLowerCase().indexOf("3g") < 0){
        		//登陆通知
    			handler.sendEmptyMessage(112244);
        	}else{
        		if(System.currentTimeMillis()-CommonUtil.getSoftUpdata(this) > 3600*24*1000){
        			//超过1天检测
        			mConnectUtil = new ConnectUtil(this, handler,0);
//        			mConnectUtil.connect(addUrlParam(URLUtil.URL_SOFT_UPDATA, URLUtil.fid, URLUtil.version, URLUtil.plaid), HttpThread.TYPE_PAGE, 0);
//        			mConnectUtil.connect(addUrlParam(URLUtil.URL_SOFT_UPDATA_CMMOBI,URLUtil.str_version,URLUtil.system,URLUtil.productcode,CommonUtil.getIMEI(WelcomeActivity.this),URLUtil.fid), HttpThread.TYPE_PAGE, 0);
        			sotfUpdate(URLUtil.URL_SOFT_UPDATA_CMMOBI,URLUtil.str_version,URLUtil.system,URLUtil.productcode,CommonUtil.getIMEI(WelcomeActivity.this),URLUtil.fid);
        		}else{
        			//登陆通知
        			handler.sendEmptyMessage(112244);
        		}
        	}
        }else{
        	CommonUtil.ShowToast(this, "未找到网络\n翠鸟将进入历史模式。");
        	new Thread() {
    			public void run() {
    				onSleep(SLEEP_TIMES);
    			};
    		}.start();
        }
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
    	if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
    		//关闭所有activity
    		isPressBack = true;
			Intent intent1 = new Intent();
			intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent1.setClass(WelcomeActivity.this, FirstPageActivity.class);
			intent1.putExtra("type", 1);
			startActivity(intent1);
			finish();
			return true;
    	}
		return super.onKeyDown(keyCode, event);
	}

	@Override  
    public void onWindowFocusChanged(boolean hasFocus) {  
        super.onWindowFocusChanged(hasFocus);  
        LogPrint.Print("lyb", "imgView =" + imgView);
        if(imgView == null){
        	imgView = (ImageView)findViewById(R.id.img);
        }
        imgView.setBackgroundResource(R.anim.covergif);  
        AnimationDrawable anim = (AnimationDrawable) imgView.getBackground();  
        anim.start();  
    } 

	private void init(){
    	CommonUtil.createDirs(CommonUtil.dir_cache, true,WelcomeActivity.this);
    	CommonUtil.createDirs(CommonUtil.dir_user, true,WelcomeActivity.this);
    	CommonUtil.createDirs(CommonUtil.dir_cache_page, false, WelcomeActivity.this);
    	CommonUtil.createDirs(CommonUtil.dir_download, false, WelcomeActivity.this);
    	String cameraDirString = CommonUtil.getExtendsCardPath()+"iCuiniao/camera";
		CommonUtil.deleteAll(new File(cameraDirString));
    	DisplayMetrics dm = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(dm);
    	CommonUtil.screen_width = dm.widthPixels;
    	CommonUtil.screen_height = dm.heightPixels;
    	LogPrint.Print("screen_width = "+CommonUtil.screen_width);
    	LogPrint.Print("screen_height = "+CommonUtil.screen_height);
    	//初始化用户
    	UserUtil.userid = CommonUtil.getUserId(getApplicationContext());
    	UserUtil.username = CommonUtil.getUserName(getApplicationContext());
    	UserUtil.logintype = CommonUtil.getLoginType(getApplicationContext());
    	UserUtil.gender = CommonUtil.getGender(getApplicationContext());
    	UserUtil.userState = CommonUtil.getUserState(getApplicationContext());
    	//redo at 2013.06.05
    	if(UserUtil.userState == 0){
    		CommonUtil.saveAddrManager(this, "");
    		CommonUtil.saveCurAddr(this, -1);
    		//新收货地址和撒娇对象清空
			CommonUtil.saveReceiveAddrMgr(this, "");
			CommonUtil.saveCurReceiveAddr(this, 0);
			CommonUtil.saveSajiaoObjMgr(this, "");
			CommonUtil.saveCurSajiaoObj(this, 0);
    	}
    	String vidString = CommonUtil.getVId(this);
    	if("-1".equals(vidString)){
    		UserUtil.vid = createVId();
    		CommonUtil.saveVId(this, UserUtil.vid);
    	}else{
    		UserUtil.vid = vidString;
    	}
    	UserUtil.uid = -1;
    	UserUtil.callOnNum = CommonUtil.getCallOnNum(this);
    	//将老版本的消息服务终止
    	stopService(new Intent(this,MessageReceiveService.class));
    	
    	//商品价格和特征是否显示
        UserUtil.isShowPrice = CommonUtil.getFeatureShow(this);
        UserUtil.isShowPrice = CommonUtil.getPriceShow(this);
        
        setFriendsToSdcardImage();
    }

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

  	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//注册广播
		registerReceiver();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MySoftReference.getInstance().clear();
		//销毁广播
		unRegisterReceiver();
	}

	private MHandlerThread mHandlerThread;
	private Handler handler;
	private void newHandler(){
		mHandlerThread = new MHandlerThread("WelcomeActivity");
		mHandlerThread.start();
		handler = new Handler(mHandlerThread.getLooper(),mHandlerThread) {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				//结束加载,进入首页
				case END_SLEEP:
					if(isShowStartTime){
						isShowStartTime = false;
//						CommonUtil.ShowToast(WelcomeActivity.this, "启动时间:"+(System.currentTimeMillis()-startTime)/1000+"秒");
					}
					if(!isServiceStart){
						//启动消息服务
						startService(new Intent(WelcomeActivity.this, IMService.class));
						isServiceStart = true;
						//启动本地消息服务
						startService(new Intent(WelcomeActivity.this,LocalMessageService.class));
					}
					MyPageActivityA.urls = null;
					getAllCommdityUrl();
					break;
				//开始连接
				case MessageID.MESSAGE_CONNECT_START:
//					addProgress();
					break;
				//连接错误
				case MessageID.MESSAGE_CONNECT_ERROR:
//					closeProgress();
					if(msg.arg1 == 10){
						if(limit <= 0){
							startpi = CommonUtil.getStartPi(WelcomeActivity.this);
							limit = CommonUtil.getLimit(WelcomeActivity.this);
							mIntent.putExtra("startpi", startpi);
					        mIntent.putExtra("limit", limit);
						}
						startActivity(mIntent);
						finish();
					}else{
						handler.sendEmptyMessage(END_SLEEP);
					}
					break;
				//页面数据加载完成
				case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
//					closeProgress();
					if(msg.arg1 == 10){
						Json1((byte[])msg.obj, msg.getData().getString("content_type"));
					}else{
						Json((byte[])msg.obj,msg.arg1,msg.getData().getString("content_type"));
					}
					break;
				//请求后台缓存
				case 998866:
					connectCacheInfo();
					break;
				case 998877:
					CommonUtil.ShowToast(WelcomeActivity.this, "嘘！别喊，重启！小C将满血复活！");
					break;
				case 112233:
					buildSoftUpdataDialog(msg.getData().getString("msg"), msg.getData().getBoolean("force"), msg.getData().getString("url"),msg.getData().getInt("filesize"),msg.getData().getString("versionnumber"));
					//2014-09-28
					//buildSoftUpdataDialog(msg.getData().getString("msg"),false, msg.getData().getString("url"),msg.getData().getInt("filesize"),msg.getData().getString("versionnumber"));
					break;
				case 112244:
					//登陆通知
					mConnectUtil = new ConnectUtil(WelcomeActivity.this, handler,0);
					mConnectUtil.connect(addUrlParam(URLUtil.URL_LOGIN_INFORM), HttpThread.TYPE_PAGE, 1);
					break;
				}
			}
		};
	}

  	private void onSleep(final long times) {
		try {
			Thread.currentThread().sleep(times);
		} catch (InterruptedException e) {
		}
		handler.sendEmptyMessage(END_SLEEP);
	}

  	public String addUrlParam(String url,int fid,int ver,String plaid){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		
		return url+"?fid="+fid+"&ver="+ver+"&plaid="+plaid;
	}
  	
  	//技术中心管理平台检查更新
//  	public String addUrlParam(String url,String version,int system,int productcode,String imei,String channelcode){
//  		JSONObject jsonObject = new JSONObject();
//  		try {
//  			jsonObject.put("version", version);
//  			jsonObject.put("system", ""+system);
//  			jsonObject.put("productcode", ""+productcode);
//  			jsonObject.put("imei", imei);
//  			jsonObject.put("channelcode", channelcode);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		return url+"?requestapp="+jsonObject.toString();
//  	}
  	/**
  	 * 应用更新
  	 * @param url 下载地址
  	 * @param version 版本号
  	 * @param system
  	 * @param productcode
  	 * @param imei
  	 * @param channelcode
  	 */
  	public void sotfUpdate(final String url,String version,int system,int productcode,String imei,String channelcode){
  		JSONObject jsonObject = new JSONObject();
  		try {
  			jsonObject.put("version", version);
  			jsonObject.put("system", ""+system);
  			jsonObject.put("productcode", ""+productcode);
  			jsonObject.put("imei", imei);
  			jsonObject.put("channelcode", channelcode);
  			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		final List <NameValuePair> params=new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("requestapp",jsonObject.toString()));
		
		new Thread(){
			public void run(){
				String result = CommonUtil.httpPost(url, params);
				try {
					if(result == null){
						//登陆通知
						handler.sendEmptyMessage(112244);
					}else{
						Json(result.getBytes("UTF-8"), 0, "text/json");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//登陆通知
					handler.sendEmptyMessage(112244);
				}
			}
		}.start();
  	}
  	
  	public String addUrlParam(String url,int pi,int oid){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"?pi="+pi+"&type=0"+"&oid="+oid+"&dpi="+URLUtil.dpi()+"&deviceid="+CommonUtil.getIMEI(this)+"&plaid="+URLUtil.plaid;
	}
  	
  	public String addUrlParam(String url,String dpi,int pi,int oid){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		if(url.indexOf("?") > 0){
			return url+"&dpi="+dpi+"&pi="+pi+"&plaid="+URLUtil.plaid+"&deviceid="+CommonUtil.getIMEI(this);
		}
		return url+"?dpi="+dpi+"&pi="+pi+"&plaid="+URLUtil.plaid+"&deviceid="+CommonUtil.getIMEI(this);
	}
  	
  	public String addUrlParam(String url){
  		if(URLUtil.IsLocalUrl()){
			return url;
		}
  		return url+"?oid="+UserUtil.userid+"&pc="+URLUtil.pc+"&fid="+URLUtil.fid+"&os_version="+CommonUtil.toUrlEncode(CommonUtil.getOs_Version())+"&network_type="+CommonUtil.getApnType(this)
  		+"&plaid="+URLUtil.plaid+"&dpi="+URLUtil.dpi()+"&imsi="+CommonUtil.getIMSI(this)+"&imei="+CommonUtil.getIMEI(this)+"&sim="+CommonUtil.getSimType(this)+"&reletm="+URLUtil.reletm+"&pla="+CommonUtil.toUrlEncode(CommonUtil.getDeviceName())+"&ver="+URLUtil.version+"&deviceid="+CommonUtil.getIMEI(this);
  	}
  	
  	public String addUrlParam(String url,String dpi){
  		if(URLUtil.IsLocalUrl()){
			return url;
		}
  		return url+"?dpi="+dpi;
  	}
  	
  	//获取全部54个单品页商品id
  	private void getAllCommdityUrl(){
  		if(CommonUtil.isNetWorkOpen(this)){
  			//大于30分钟
  			if(CommonUtil.getSysTime(WelcomeActivity.this)-CommonUtil.getCreateTime(WelcomeActivity.this) > 1800000||autoupdate){
  				mConnectUtil = new ConnectUtil(this, handler,0);
  				mConnectUtil.connect(addUrlParam(URLUtil.URL_ALLCOMMDITYID,URLUtil.dpi()), HttpThread.TYPE_PAGE, 10);
  			}else{
  				String temp = CommonUtil.get54Cid(WelcomeActivity.this);
  				try {
  					if(temp != null&&temp.length() > 2){
  	  					JSONArray jsonArray = new JSONArray(temp);
  						int length = jsonArray.length();
  						for(int i = 0;i < length;i ++){
  							JSONObject jsonObject = jsonArray.getJSONObject(i);
  							int cid = jsonObject.getInt("cid");
  							bulidUrl(i, cid, length);
  						}
  						MyPageActivityA.saveUrlsFromMain = MyPageActivityA.urls;
  						if(limit <= 0){
  							startpi = CommonUtil.getStartPi(WelcomeActivity.this);
  							limit = CommonUtil.getLimit(WelcomeActivity.this);
  							mIntent.putExtra("startpi", startpi);
  					        mIntent.putExtra("limit", limit);
  						}
  						startActivity(mIntent);
  						finish();
  	  				}else{
	  	  				mConnectUtil = new ConnectUtil(this, handler,0);
	  	  				mConnectUtil.connect(addUrlParam(URLUtil.URL_ALLCOMMDITYID,URLUtil.dpi()), HttpThread.TYPE_PAGE, 10);
  	  				}
				} catch (Exception e) {
					// TODO: handle exception
				}
  			}
  		}else{
  			if(limit <= 0){
				startpi = CommonUtil.getStartPi(WelcomeActivity.this);
				limit = CommonUtil.getLimit(WelcomeActivity.this);
				mIntent.putExtra("startpi", startpi);
		        mIntent.putExtra("limit", limit);
			}
			startActivity(mIntent);
			finish();
  		}
  	}
  	
  	public void bulidUrl(int index,int cid,int length){
  		if(MyPageActivityA.urls == null){
  			MyPageActivityA.urls = new String[length];
  		}else{
  			if(MyPageActivityA.urls.length != length){
  				MyPageActivityA.urls = new String[length];
  			}
  		}
  		UserUtil.cidMaxNum = length;
  		MyPageActivityA.urls[index] = URLUtil.URL_MYPAGE+"?cid="+cid+"&dpi="+URLUtil.dpi()+"&pi=0&plaid="+URLUtil.plaid;
  	}
  	
  	private void Json1(byte[] data,String contenttype){
  		try {
  			if("text/json".equals(contenttype)){
  				String str = new String(data,"UTF-8");
				str = CommonUtil.formUrlEncode(str);
				LogPrint.Print("jsonarray = "+str);
				if(str != null&&str.length() > 2){
					CommonUtil.save54Cid(WelcomeActivity.this, str);
					JSONArray jsonArray = new JSONArray(str);
					int length = jsonArray.length();
					for(int i = 0;i < length;i ++){
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						int cid = jsonObject.getInt("cid");
						bulidUrl(i, cid, length);
					}
					MyPageActivityA.saveUrlsFromMain = MyPageActivityA.urls;
				}
  			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			if(limit <= 0){
				startpi = CommonUtil.getStartPi(WelcomeActivity.this);
				limit = CommonUtil.getLimit(WelcomeActivity.this);
				mIntent.putExtra("startpi", startpi);
		        mIntent.putExtra("limit", limit);
			}
			startActivity(mIntent);
			finish();
		}
  	}
  	
//  	private SystemProgress progress;
//	public void addProgress(){
//		try {
//			if(progress != null)return;
//			progress = new SystemProgress(this, this, null){
//				public void cancelData(){
//					
//				}
//			};
//			
//		} catch (Exception e) {
//		}
//	}
	
//	public void closeProgress(){
//		if(progress != null){
//			progress.close();
//			progress = null;
//		}
//	}
	
	private void Json(byte[] data,int threadIndex,String contenttype){
		try{
			if("text/json".equals(contenttype)){
				String str = new String(data,"UTF-8");
				str = CommonUtil.formUrlEncode(str);
				LogPrint.Print("json = "+str);
				JSONObject jObject = new JSONObject(str);
				String result;
				switch (threadIndex) {
				case 0:
					try {
						int result_type = jObject.getInt("type");
						if(result_type > 0){
							boolean force = result_type == 1?true:false;
							String msg = jObject.getString("description");
							String url = jObject.getString("path");
							int filesize = jObject.getInt("filesize");
							String versionnumber = jObject.getString("versionnumber");
							Message message = new Message();
							message.what=112233;
							Bundle bundle = new Bundle();
							bundle.putBoolean("force", force);
							bundle.putString("msg", msg);
							bundle.putString("url", url);
							bundle.putInt("filesize", filesize);
							bundle.putString("versionnumber", versionnumber);
							message.setData(bundle);
							handler.sendMessage(message);
						}else{
							//登陆通知
							handler.sendEmptyMessage(112244);
						}
					} catch (Exception e) {
						// TODO: handle exception
						//登陆通知
						handler.sendEmptyMessage(112244);
					}
					break;
				case 1:
					result = jObject.getString("result");
					if(result != null){
						if(result.equalsIgnoreCase("true")){
							UserUtil.userid = jObject.getInt("oid");
							UserUtil.userState = jObject.getInt("status");
							CommonUtil.saveUserId(this, UserUtil.userid);
							CommonUtil.saveUserState(this, UserUtil.userState);
							//启动消息服务
							startService(new Intent(this, IMService.class));
							isServiceStart = true;
							//启动本地消息服务
							startService(new Intent(this,LocalMessageService.class));
						}
						
						//获取服务器时间
						mConnectUtil = new ConnectUtil(this, handler,0);
		        		mConnectUtil.connect(URLUtil.URL_GET_SYSTIME, HttpThread.TYPE_PAGE, 2);
					}
					break;
				case 2:
					result = jObject.getString("result");
					UserUtil.sysDate = "";
					if(result != null){
						if(result.equalsIgnoreCase("true")){
							UserUtil.sysTime = jObject.getLong("time");
							UserUtil.sysDate = jObject.getString("date");
							OfflineTimeService.resetTimeDef();//时间差清零
							OfflineLog.writeUserId();//写入离线日志
						}else{
							UserUtil.sysTime = System.currentTimeMillis();
							UserUtil.sysDate = "";
						}
						LogPrint.Print("sysTime = "+UserUtil.sysTime);
						LogPrint.Print("sysDate = "+UserUtil.sysDate);
						CommonUtil.saveSysTime(WelcomeActivity.this, UserUtil.sysTime);
						CommonUtil.saveSysDate(WelcomeActivity.this, UserUtil.sysDate);
						deleteCache(UserUtil.sysTime, CommonUtil.getSysTime(WelcomeActivity.this),isNewDay(UserUtil.sysDate, CommonUtil.getSysDate(WelcomeActivity.this)));
					}
					break;
				case URLUtil.THREAD_MAINPAGE_CACHE_INFO:
					result = jObject.getString("result");
					if(result != null){
						if(result.equalsIgnoreCase("true")){
							startpi = jObject.getInt("startpi");
							limit = jObject.getInt("limit");
							CommonUtil.saveStartPi(WelcomeActivity.this, startpi);
							CommonUtil.saveLimit(WelcomeActivity.this, limit);
							UserUtil.callOnNum = jObject.getInt("callonnum");
							autoupdate = jObject.getBoolean("autoupdate");
							boolean showmsg = jObject.getBoolean("showmsg");
							long createtime = jObject.getLong("createtime");
							UserUtil.sysTime = jObject.getLong("time");
							UserUtil.sysDate = jObject.getString("date");
							CommonUtil.saveSysTime(WelcomeActivity.this, UserUtil.sysTime);
							CommonUtil.saveSysDate(WelcomeActivity.this, UserUtil.sysDate);
							CommonUtil.saveCreateTime(WelcomeActivity.this, createtime);
							if(UserUtil.userid != -1&&UserUtil.userState == 1){//登录
								//初始化摇一摇倒计时
								TimerForRock.initLimitTime(CommonUtil.getLimitTime(CommonUtil.getCreateTime(WelcomeActivity.this), CommonUtil.getSysTime(WelcomeActivity.this)), WelcomeActivity.this);
							}
							int lastCallOnNum = CommonUtil.getCallOnNum(WelcomeActivity.this);
							LogPrint.Print("lastCallonTime = "+lastCallOnNum+" | callontime = "+UserUtil.callOnNum);
							CommonUtil.saveCallOnNum(WelcomeActivity.this, UserUtil.callOnNum);
							mIntent.putExtra("startpi", startpi);
					        mIntent.putExtra("limit", limit);
					        //判断联网方式,决定预加载机制
					        final String temp = CommonUtil.getApnType(this);
					        if(UserUtil.callOnNum == 0&&lastCallOnNum == 0){
					        	handler.sendEmptyMessage(END_SLEEP);
					        }else{
					        	if(autoupdate){//需要刷新 新的六页数据
					        		//清除缓存
					        		CommonUtil.deleteAll(new File(CommonUtil.dir_cache));
					        		//提示文本
					        		if(showmsg){
					        			AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
					        			builder.setTitle("每半小时翠鸟会自动摇一下，别忘了标记喜欢的商品哦！").setPositiveButton("确定", new OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
												// TODO Auto-generated method stub
												//加载数据
								        		addToDownLoadPool(temp);
											}
										}).setCancelable(false).show();
					        		}else{
					        			//加载数据
					        			addToDownLoadPool(temp);
					        		}
					        	}else{
					        		handler.sendEmptyMessage(END_SLEEP);
					        	}
					        }
						}else{
							handler.sendEmptyMessage(998877);
							CommonUtil.saveSoftUpdata(WelcomeActivity.this, 0);
							CommonUtil.isInMessageScreen = false;
							CommonUtil.isNormalInToApp = false;
							//关闭所有activity
							Intent intent1 = new Intent();
							intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							intent1.setClass(WelcomeActivity.this, FirstPageActivity.class);
							intent1.putExtra("type", 1);
							startActivity(intent1);
							finish();
						}
					}
					break;
				}
			}
		}catch(Exception e){
			if(threadIndex == 0){
				//登陆通知
				handler.sendEmptyMessage(112244);
			}
			e.printStackTrace();
		}
	}
	/**
	 * 软件更新
	 * @param str
	 * @param force
	 * @param url
	 * @param filesize
	 * @param versionnumber
	 */
	private void buildSoftUpdataDialog(String str,final boolean force,final String url,final int filesize,final String versionnumber){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("软件更新").setMessage("更新内容:\n"+str).setPositiveButton("更新", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				CommonUtil.saveSoftUpdata(WelcomeActivity.this, System.currentTimeMillis());
				Intent intent= new Intent();
				intent.putExtra("url", url);
				intent.putExtra("filesize", filesize);
				intent.putExtra("versionnumber", versionnumber);
				intent.setClass(WelcomeActivity.this, SoftUpdateActivity.class);
				startActivity(intent);
//			    intent.setAction("android.intent.action.VIEW");    
//			    Uri content_url = Uri.parse(url);   
//			    intent.setData(content_url);  
//			    startActivity(intent);
			}
		}).setNegativeButton(force?"退出":"取消", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(force){
					CommonUtil.saveSoftUpdata(WelcomeActivity.this, 0);
					CommonUtil.isInMessageScreen = false;
					CommonUtil.isNormalInToApp = false;
					//关闭所有activity
					Intent intent1 = new Intent();
					intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent1.setClass(WelcomeActivity.this, FirstPageActivity.class);
					intent1.putExtra("type", 1);
					startActivity(intent1);
					finish();
				}else{
					CommonUtil.saveSoftUpdata(WelcomeActivity.this, System.currentTimeMillis());
					//登陆通知
					mConnectUtil = new ConnectUtil(WelcomeActivity.this, handler,0);
	        		mConnectUtil.connect(addUrlParam(URLUtil.URL_LOGIN_INFORM), HttpThread.TYPE_PAGE, 1);
				}
			}
		}).setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				if(force){
					CommonUtil.saveSoftUpdata(WelcomeActivity.this, 0);
					CommonUtil.isInMessageScreen = false;
					CommonUtil.isNormalInToApp = false;
					//关闭所有activity
					Intent intent1 = new Intent();
					intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent1.setClass(WelcomeActivity.this, FirstPageActivity.class);
					intent1.putExtra("type", 1);
					startActivity(intent1);
					finish();
				}else{
					CommonUtil.saveSoftUpdata(WelcomeActivity.this, System.currentTimeMillis());
					handler.sendEmptyMessage(END_SLEEP);
				}
			}
		}).show();
	}
	
	//创建vid
	private String createVId(){
		long l = System.currentTimeMillis();
		int random = Math.abs(new Random().nextInt())%1000;
		return "v"+l+random;
	}
	
	/**请求后台缓存*/
	private void connectCacheInfo(){
		LogPrint.Print("load cache");
		mConnectUtil = new ConnectUtil(this, handler,0);
		mConnectUtil.connect(addUrlParam(URLUtil.URL_MAINPAGE_CACHE_INFO, 0, UserUtil.userid), HttpThread.TYPE_PAGE, URLUtil.THREAD_MAINPAGE_CACHE_INFO);
	}
	
	/**加入下载池*/
	private void addToDownLoadPool(String apn){
		if(limit <= 0){
			startpi = CommonUtil.getStartPi(WelcomeActivity.this);
			limit = CommonUtil.getLimit(WelcomeActivity.this);
			mIntent.putExtra("startpi", startpi);
	        mIntent.putExtra("limit", limit);
		}
		if(limit > 0){
			int len = 0;
			//预加载逻辑，wifi/3g加1页，2g不加
			if(apn != null){
				if(apn.toLowerCase().indexOf("wifi") >= 0||apn.toLowerCase().indexOf("3g") >= 0) {
					len = 1;
		        }
			}else{
				len = 1;
			}
			//如果是2g，不进行加载
			if(len == 0){
				handler.sendEmptyMessage(END_SLEEP);
			}else{
				for(int i = startpi;i < startpi+len;i ++){
					LogPrint.Print("addToDownLoadPool: "+i);
					DownLoadPoolItem item = new DownLoadPoolItem(addUrlParam(URLUtil.URL_MAINPAGE,URLUtil.dpi(),i,UserUtil.userid), HttpThread.TYPE_PAGE, false);
					DownLoadPool.getInstance(getApplicationContext()).add(item, -1);
				}
			}
		}
	}
	
	/**手势动作广播接收器*/
	private BroadcastReceiver mGestuReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(ActionID.ACTION_BROADCAST_DOWNLOADOVER.equals(action)){
//				closeProgress();
				handler.sendEmptyMessage(END_SLEEP);
			}
		}
		
	};

  	/**
	 * 注册广播接收器
	 */
	private final void registerReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ActionID.ACTION_BROADCAST_DOWNLOADOVER);
		registerReceiver(mGestuReceiver, intentFilter);
	}
	
	/**
	 * 卸载广播接收器
	 */
	private final void unRegisterReceiver() {
		try {
			if(mGestuReceiver != null){
				unregisterReceiver(mGestuReceiver);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	protected void setFullScreenMold(){
    	//隐藏标题
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	//隐藏信号条
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
	
	
	
	//获取wifi强度和速度
	private void getWifiSpeed(){
		try {
			WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
			WifiInfo info = wifiManager.getConnectionInfo();
			if(info.getBSSID() != null){
				// 链接信号强度
				int strength = WifiManager.calculateSignalLevel(info.getRssi(),5);
				LogPrint.Print("wifi strength = "+strength);
				//  链接速度
				int speed = info.getLinkSpeed();
				LogPrint.Print("wifi speed = "+speed);
				//  链接速度单位
				String units = WifiInfo.LINK_SPEED_UNITS;
				LogPrint.Print("wifi units = "+units);
				// Wifi源名称
				String ssid = info.getSSID();
				LogPrint.Print("wifi ssid = "+ssid);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	//清除缓存逻辑
	private void deleteCache(final long lastTime,final long saveTime,final boolean isNewDay){
//		new Thread(){
//			public void run(){
//				LogPrint.Print("deleteCache:callontime = "+CommonUtil.getCallOnNum(WelcomeActivity.this));
//				LogPrint.Print("time = "+(lastTime-saveTime));
//				//网络存在时删除,不存在时使用缓存
//		    	if(CommonUtil.isNetWorkOpen(WelcomeActivity.this)){
//		    		if(isNewDay){
//		    			CommonUtil.saveCallOnNum(WelcomeActivity.this, -1);
//		    			CommonUtil.deleteAll(new File(CommonUtil.dir_cache));
//	    				handler.sendEmptyMessage(998866);
//		    		}
//		    		else{
//		    			if(saveTime > lastTime){//时间错误
//		    				CommonUtil.deleteAll(new File(CommonUtil.dir_cache));
//		    				handler.sendEmptyMessage(998866);
//		    			}else if(lastTime-saveTime >= 1800000){//大于30分钟
//		    				if(CommonUtil.getCallOnNum(WelcomeActivity.this) > 0){//未到次数
//		    					CommonUtil.deleteAll(new File(CommonUtil.dir_cache));
//		    					handler.sendEmptyMessage(998866);
//		    				}else{
//		    					handler.sendEmptyMessage(END_SLEEP);
//		    				}
//		    			}else{
//		    				if(CommonUtil.getCallOnNum(WelcomeActivity.this) < 0){
//		    					CommonUtil.deleteAll(new File(CommonUtil.dir_cache));
//		    					handler.sendEmptyMessage(998866);
//		    				}else{
//		    					handler.sendEmptyMessage(END_SLEEP);
//		    				}
//		    			}
//		    		}
//		    	}else{
//		    		handler.sendEmptyMessage(END_SLEEP);
//		    	}
//			}
//		}.start();
		
		
		LogPrint.Print("deleteCache:callontime = "+CommonUtil.getCallOnNum(WelcomeActivity.this));
		//网络不存在时使用缓存
    	if(CommonUtil.isNetWorkOpen(WelcomeActivity.this)){
    		if(isNewDay){
    			OfflineLog.saveForceUpload(WelcomeActivity.this, false);
    			CommonUtil.saveCallOnNum(WelcomeActivity.this, -1);
    			handler.sendEmptyMessage(998866);//获取缓存
    		}else{
    			handler.sendEmptyMessage(998866);//获取缓存
//    			if(UserUtil.userid == -1||UserUtil.userState != 1){//游客
//    				OfflineLog.saveForceUpload(WelcomeActivity.this, false);
//        			CommonUtil.saveCallOnNum(WelcomeActivity.this, -1);
//        			handler.sendEmptyMessage(998866);//获取缓存
//    			}else{
//    				handler.sendEmptyMessage(END_SLEEP);
//    			}
    		}
    		//30分钟的判断取消
//    		else{
//    			//大于30分钟
//    			if(CommonUtil.getSysTime(WelcomeActivity.this)-CommonUtil.getCreateTime(WelcomeActivity.this) > 1800000){
//    				handler.sendEmptyMessage(998866);//获取缓存
//    			}else{
//    				LogPrint.Print("not out 30m");
//    				handler.sendEmptyMessage(END_SLEEP);
//    			}
//    		}
    	}else{
    		handler.sendEmptyMessage(END_SLEEP);
    	}
	}
	
	//是否是新的一天了
	private boolean isNewDay(String last,String save){
		//2012-09-09 17:00:00:000
		try {
			int last_year;
			int last_month;
			int last_day;
			int save_year;
			int save_month;
			int save_day;
			if(save == null)return true;
			if(save.length() <= 0)return true;
			String lastString = last.substring(0, last.indexOf(" "));
			String saveString = save.substring(0, save.indexOf(" "));
			String[] array = lastString.split("-");
			last_year = Integer.parseInt(array[0]);
			if(array[1].length() == 2){
				if(array[1].charAt(0) == '0'){
					last_month = Integer.parseInt(array[1].substring(1));
				}else{
					last_month = Integer.parseInt(array[1]);
				}
			}else{
				last_month = Integer.parseInt(array[1]);
			}
			if(array[2].length() == 2){
				if(array[2].charAt(0) == '0'){
					last_day = Integer.parseInt(array[2].substring(1));
				}else{
					last_day = Integer.parseInt(array[2]);
				}
			}else{
				last_day = Integer.parseInt(array[2]);
			}
			
			array = saveString.split("-");
			save_year = Integer.parseInt(array[0]);
			if(array[1].length() == 2){
				if(array[1].charAt(0) == '0'){
					save_month = Integer.parseInt(array[1].substring(1));
				}else{
					save_month = Integer.parseInt(array[1]);
				}
			}else{
				save_month = Integer.parseInt(array[1]);
			}
			if(array[2].length() == 2){
				if(array[2].charAt(0) == '0'){
					save_day = Integer.parseInt(array[2].substring(1));
				}else{
					save_day = Integer.parseInt(array[2]);
				}
			}else{
				save_day = Integer.parseInt(array[2]);
			}
			//对比时间
			if(last_year > save_year){
				return true;
			}else{
				if(last_year < save_year){//错误时间
					return true;
				}else{
					if(last_month > save_month){
						return true;
					}else{
						if(last_month < save_month){//错误时间
							return true;
						}else{
							if(last_day > save_day){
								return true;
							}else{
								if(last_day < save_day){//错误时间
									return true;
								}else{
									return false;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return true;
	}
	
	public void startActivity(Intent intent){
		if(isPressBack){
			if(intent.equals(mIntent)){
				return;
			}
		}
		super.startActivity(intent);
	}
	
	/**
	 * 邀请好友时用到的图片
	 */
	public void setFriendsToSdcardImage(){
		new Thread(){
			@Override
			public void run() {
				//资源图片
				//String dir = getResources().getAssets().toString()+"/friends.png";
				//写入SDCARD图片路径
				String sdCardString = CommonUtil.dir_friends;
				//读文件
				//byte[] tempbytes = CommonUtil.getSDCardFileByteArray(dir);
				//写文件
				//CommonUtil.writeToFile(sdCardString,tempbytes);

				if (!new File(sdCardString).exists()) {
					File f = new File(sdCardString);
					if (!f.exists()) {
						f.mkdir();
					}
					try {
						InputStream is = getResources().getAssets().open(
								"friends.png");
						OutputStream os = new FileOutputStream(sdCardString
								+ "friends.png");
						byte[] buffer = new byte[1024];
						int length;
						while ((length = is.read(buffer)) > 0) {
							os.write(buffer, 0, length);
						}
						os.flush();
						os.close();
						is.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		
	}
}
