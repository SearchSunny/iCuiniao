package com.cmmobi.icuiniao.Activity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.activity.UserPageActivityA;
import com.cmmobi.icuiniao.ui.view.MainMenu;
import com.cmmobi.icuiniao.ui.view.SlipButton;
import com.cmmobi.icuiniao.ui.view.SlipButton.OnChangedListener;
import com.cmmobi.icuiniao.util.ActionID;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.MySoftReference;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.icuiniao.plug.im.Entity;
import com.icuiniao.plug.im.IMDataBase;

public class SettingActivityA extends Activity implements AnimationListener{
	private ConnectUtil mConnectUtil;	
	private boolean isAnimationOpen;
	private MainMenu mainMenu;
	private Button titlebar_menubutton;
	private Button leftMenu;
	private LinearLayout settingpage;
	private ProgressBar loadingBar;
	//消息提醒
	private ImageView messageImageId;
	private int animationIndex;
	//个人设置
	private RelativeLayout personalSetting;
	//我的主页
	private RelativeLayout myPage;
	//撒娇对象管理
	private RelativeLayout sajiao;
	//收货地址
	private RelativeLayout takeAddress;
	//用户反馈 
	private RelativeLayout feedback;
	//通知提醒
	private SlipButton notificationSlipButton;
	//省流量模式
	//private SlipButton fluxSlipButton;
	//清除缓存
	private Button cacheButton;
	//关于
	private RelativeLayout about;
	//软件更新 
	private Button updateButton;
	private boolean notificationBoolean = false;
	private boolean fluxBoolean = false;
	IMDataBase imDataBase;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		imDataBase = new IMDataBase(this);
		setContentView(R.layout.settinga);
		//初始化组件
		initWidget();
		//设置监听
		setListener();
		//设置通知提醒
		notificationBoolean = CommonUtil.getMessageReceiverState(SettingActivityA.this);
		notificationSlipButton.setCheck(notificationBoolean);
		//设置流量
		//fluxBoolean = CommonUtil.getFluxState(SettingActivityA.this);
		//fluxSlipButton.setCheck(fluxBoolean);
	}
	
	/**
	 * 个人设置监听
	 */
	private OnClickListener personalSettingListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(SettingActivityA.this,PersonalSettingActivity.class);
			startActivity(intent);
		}
	};
	
	/**
	 * 我的主页监听
	 */
	private OnClickListener myPageListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String pageUrl = URLUtil.URL_USER_HOMEPAGE + "?oid=" + UserUtil.userid + "&uid=" + UserUtil.userid;
			Intent intent = new Intent(SettingActivityA.this, UserPageActivityA.class);
			intent.putExtra("url", pageUrl);
			LogPrint.Print("lyb", "click userid = " + UserUtil.userid);
			intent.putExtra("userid", UserUtil.userid);  //int
			intent.putExtra("nickname", UserUtil.username);
			SettingActivityA.this.startActivity(intent);
		}
	};
	
	/**
	 * 撒娇对象管理监听
	 */
	private OnClickListener sajiaoListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(SettingActivityA.this, SajiaoObjMgrActivity.class);
			startActivity(intent);			
		}
	};
	
	/**
	 * 收货地址监听
	 */
	private OnClickListener takeAddressListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(SettingActivityA.this, AddrManagerActivityA.class);
			startActivity(intent);
			OfflineLog.writeAddrManager();
		}
	};
	
	/**
	 * 用户反馈监听
	 */
	private OnClickListener feedbackListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(SettingActivityA.this, FeedbackActivity.class);
			startActivity(intent);
		}
	};
	
	/**
	 * 通知提醒监听
	 */
	private OnChangedListener notificationListener = new OnChangedListener() {
		
		@Override
		public void onChanged(boolean checkState) {
			if(checkState){
				CommonUtil.saveMessageReceiverState(SettingActivityA.this, true);
				OfflineLog.writeMainMenu_MessageButton((byte)1);//写入离线日志
			}
			else{
				CommonUtil.saveMessageReceiverState(SettingActivityA.this, false);
				OfflineLog.writeMainMenu_MessageButton((byte)0);
			}
		}
	};
	
	/**
	 * 省流量监听
	 */
	private OnChangedListener fluxListener = new OnChangedListener() {
		
		@Override
		public void onChanged(boolean checkState) {
			CommonUtil.saveFluxState(SettingActivityA.this, checkState);
		}
	};
	
	/**
	 * 清除缓存监听
	 */
	private OnClickListener cacheListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			addProgress();
			new Thread(){
				public void run(){
					super.run();
					CommonUtil.deleteAll(new File(CommonUtil.dir_cache));
					CommonUtil.deleteAll(new File(CommonUtil.dir_media));
					mHandler.sendEmptyMessage(msg_delOver);
				}
			}.start();			
			MySoftReference.getInstance().clear();
			OfflineLog.writeMainMenu_ClearCache();
		}
	};
	
	/**
	 * 关于监听
	 */
	private OnClickListener aboutListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(SettingActivityA.this,AboutActivity.class);
			startActivity(intent);
		}
	};
	
	/**
	 * 软件更新监听
	 */
	private OnClickListener updateListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			addProgress();
			sotfUpdate(URLUtil.URL_SOFT_UPDATA_CMMOBI,URLUtil.str_version,URLUtil.system,URLUtil.productcode,CommonUtil.getIMEI(SettingActivityA.this),URLUtil.fid);
			OfflineLog.writeMainMenu_Softupdate();			
		}
	};
	
	private void initWidget(){
		mainMenu = (MainMenu)findViewById(R.id.mainmenu);
		titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
		personalSetting = (RelativeLayout)findViewById(R.id.personalSetting);
		myPage = (RelativeLayout)findViewById(R.id.myPage);
		sajiao = (RelativeLayout)findViewById(R.id.sajiao);
		takeAddress = (RelativeLayout)findViewById(R.id.takeAddress);
		feedback = (RelativeLayout)findViewById(R.id.feedback);
		about = (RelativeLayout)findViewById(R.id.about);
		notificationSlipButton = (SlipButton)findViewById(R.id.notificationSlipButton);
		//fluxSlipButton = (SlipButton)findViewById(R.id.fluxSlipButton);
		cacheButton = (Button)findViewById(R.id.cacheButton);
		updateButton = (Button)findViewById(R.id.updateButton);
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		settingpage = (LinearLayout)findViewById(R.id.settingpage);
		leftMenu = (Button)findViewById(R.id.titlebar_leftmenu);
		messageImageId = (ImageView)findViewById(R.id.messageImageId);
	}
	
	private void setListener(){
		personalSetting.setOnClickListener(personalSettingListener);
		myPage.setOnClickListener(myPageListener);
		sajiao.setOnClickListener(sajiaoListener);
		takeAddress.setOnClickListener(takeAddressListener);
		cacheButton.setOnClickListener(cacheListener);
		updateButton.setOnClickListener(updateListener);
		feedback.setOnClickListener(feedbackListener);
		about.setOnClickListener(aboutListener);
		notificationSlipButton.setOnChangedListener(notificationListener);
		//fluxSlipButton.setOnChangedListener(fluxListener);
		titlebar_menubutton.setOnClickListener(menuClickListener);
		leftMenu.setOnClickListener(menuClickListener);
	}
	
	public void addProgress(){
		try {
			if(loadingBar != null){
				loadingBar.setVisibility(View.VISIBLE);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeProgress(){
		if(loadingBar != null){
			loadingBar.setVisibility(View.INVISIBLE);
		}
	}	
	
	private OnClickListener menuClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			gotoMenu();
		}
	};
	
	public void sotfUpdate(final String url,String version,int system,int productcode,String imei,String channelcode){
  		JSONObject jsonObject = new JSONObject();
  		try {
  			jsonObject.put("version", version);
  			jsonObject.put("system", ""+system);
  			jsonObject.put("productcode", ""+productcode);
  			jsonObject.put("imei", imei);
  			jsonObject.put("channelcode", channelcode);
  			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		final List <NameValuePair> params=new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("requestapp",jsonObject.toString()));
		
		new Thread(){
			public void run(){
				String result = CommonUtil.httpPost(url, params);
				//lyb 无网崩溃问题
				if(result == null){
					mHandler.sendEmptyMessage(112266);
					return;
				}
				try {
					Json(result.getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}.start();
  	}
	
	private void Json(byte[] data){
		try{
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = "+str);
			JSONObject jObject = new JSONObject(str);
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
				mHandler.sendMessage(message);
			}else{
				mHandler.sendEmptyMessage(112244);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private final int msg_delOver = 112255;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageID.MESSAGE_CONNECT_START:
				addProgress();
				break;
			case MessageID.MESSAGE_CONNECT_ERROR:
				closeProgress();
				CommonUtil.ShowToast(getApplicationContext(), (String)msg.obj);
				break;
			case MessageID.MESSAGE_MAINMENU_OPEN:
				if(settingpage != null){
					if(settingpage.getRight() > getMainMenuAnimationPos(50)){
						settingpage.offsetLeftAndRight(animationIndex);
						settingpage.postInvalidate();
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_OPEN);
						animationIndex = -(CommonUtil.screen_width-getMainMenuAnimationPos(50))/5;
					}
				}
				break;
			case MessageID.MESSAGE_MAINMENU_CLOSE:
				if(settingpage != null){
					if(settingpage.getLeft() < 0){
						settingpage.offsetLeftAndRight(animationIndex);
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_CLOSE);
						settingpage.postInvalidate();
						animationIndex = (CommonUtil.screen_width-getMainMenuAnimationPos(50))/5;
					}
				}
				break;
			case 112233:
				closeProgress();
				buildSoftUpdataDialog(msg.getData().getString("msg"), msg.getData().getBoolean("force"), msg.getData().getString("url"),msg.getData().getInt("filesize"),msg.getData().getString("versionnumber"));
				break;
			case 112244:
				closeProgress();
				CommonUtil.ShowToast(SettingActivityA.this, "小C已是最新版本。");
				break;
			//add by lyb 删除缓存
			case msg_delOver:
				closeProgress();
				CommonUtil.ShowToast(SettingActivityA.this, "缓存已清除");
				break;
			case 112266:
				closeProgress();
				CommonUtil.ShowToast(SettingActivityA.this, "网络异常");
				break;
			case 998877:
				//查询消息已读、未读
				long messageId = msg.getData().getLong("messageId");
				int state = imDataBase.getIsReadState(messageId);
				//已读
				if(state == 1){
					mainMenu.setMessageButtonRes(false);
					messageImageId.setVisibility(View.GONE);
				}else if(state == 0 && UserUtil.userState == 1){
					mainMenu.setMessageButtonRes(true);
					messageImageId.setVisibility(View.VISIBLE);
				}else if(state == 0 && UserUtil.userState != 1){
					mainMenu.setMessageButtonRes(false);
					messageImageId.setVisibility(View.GONE);
				}
				else if(state == -1){
					mainMenu.setMessageButtonRes(false);
					messageImageId.setVisibility(View.GONE);
				}
				break;
			}
		}
	};
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		gotoMenu();
		return true;
	}
	
	private void gotoMenu(){
		if(settingpage != null){
			isAnimationOpen = !isAnimationOpen;			
			if(isAnimationOpen){
				if(mainMenu != null){
					mainMenu.setIsCanClick(true);
				}
				mHandler.removeMessages(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.right_out);
	    		animation.setFillEnabled(true);
	    		animation.setFillAfter(true);
	    		animation.setAnimationListener(this);
	    		//动画播放，实际布局坐标不变
	    		settingpage.startAnimation(animation);
	    		OfflineLog.writeMainMenu();//写入离线日志
			}else{
				if(mainMenu != null){
					mainMenu.setIsCanClick(false);
				}
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.left_in);
				animation.setAnimationListener(this);
	    		animation.setFillEnabled(true);
	    		animation.setFillAfter(true);
	    		settingpage.startAnimation(animation);
			}						
		}
	}
	private int getMainMenuAnimationPos(int dip){
		int result = CommonUtil.dip2px(this, dip);
		if(result%10 == 0){
			return result;
		}else{
			if(result%10 < 5){
				result -= result%10;
			}else{
				result += 10 - result%10;
			}
		}
		return result;
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		Button btMenu = (Button) settingpage.findViewById(R.id.titlebar_menubutton);
		if(isAnimationOpen){			
			leftMenu.setVisibility(View.VISIBLE);			
			btMenu.setClickable(false);
		}else{
			btMenu.setClickable(true);
		}
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		if(!isAnimationOpen){
			leftMenu.setVisibility(View.INVISIBLE);			
		}	
	}
	
	private void buildSoftUpdataDialog(String str,final boolean force,final String url,final int filesize,final String versionnumber){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("软件更新").setMessage("更新内容:\n"+str).setPositiveButton("更新", new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				CommonUtil.saveSoftUpdata(SettingActivityA.this, System.currentTimeMillis());
				Intent intent= new Intent();
				intent.putExtra("url", url);
				intent.putExtra("filesize", filesize);
				intent.putExtra("versionnumber", versionnumber);
				intent.setClass(SettingActivityA.this, SoftUpdateActivity.class);
				startActivity(intent);
			}
		}).setNegativeButton(force?"退出":"取消", new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(force){
					CommonUtil.saveSoftUpdata(SettingActivityA.this, 0);
					CommonUtil.saveLastExitTime(SettingActivityA.this);
					CommonUtil.isInMessageScreen = false;
					CommonUtil.isNormalInToApp = false;
					android.os.Process.killProcess(android.os.Process.myPid());
				}else{
					CommonUtil.saveSoftUpdata(SettingActivityA.this, System.currentTimeMillis());
				}
			}
		}).setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				if(force){
					CommonUtil.saveSoftUpdata(SettingActivityA.this, 0);
					CommonUtil.isInMessageScreen = false;
					CommonUtil.isNormalInToApp = false;
					android.os.Process.killProcess(android.os.Process.myPid());
				}else{
					CommonUtil.saveSoftUpdata(SettingActivityA.this, System.currentTimeMillis());
				}
			}
		}).show();
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int barY = 80 * CommonUtil.screen_width / 480 ;
		if(isAnimationOpen){			
			if(ev.getY() <= barY+15){
				return super.dispatchTouchEvent(ev);
			}
			final int offsetY = 40 * CommonUtil.screen_width / 480;
			ev.setLocation(ev.getX(), ev.getY() - offsetY);		
			return mainMenu.dispatchTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			//新增消息广播
			if(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_MESSAGE.equals(action)||
					ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_PRIVATEMESSAGE.equals(action)||
					ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_SYSTEMMESSAGE.equals(action)||
					ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE.equals(action)||
					ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE_SUCCESS.equals(action) ||
					ActionID.ACTION_BROADCAST_LOGOUT.equals(action)){
				Message message = new Message();
				message.what = 998877;
				Bundle bundle = new Bundle();
				if(intent.getExtras() != null){
					
					bundle.putLong("messageId", intent.getExtras().getLong("messageId"));
				}
				
				message.setData(bundle);
				mHandler.sendMessageDelayed(message, 1000);
			
			}
			
		}
		
	};
	/**
	 * 注册广播接收器
	 */
	private final void registerReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ActionID.ACTION_BROADCAST_APN_CHANGE);
		//新增消息广播
		//接收到一条消息
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_MESSAGE);
		//接收到一条私信
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_PRIVATEMESSAGE);
		//接收到一条系统广播
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_SYSTEMMESSAGE);
		//接收建立好友关系消息
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE_SUCCESS);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_LOGOUT);
		registerReceiver(mReceiver, intentFilter);
	}
	
	/**
	 * 卸载广播接收器
	 */
	private final void unRegisterReceiver() {
		try {
			if(mReceiver != null){
				unregisterReceiver(mReceiver);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//销毁广播
		unRegisterReceiver();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
		getMessageByUserId();
		//注册广播
		registerReceiver();
	}
	/**
	 * 根据消息已读\未读状态
	 */
	private void getMessageByUserId(){
		
		ArrayList<Entity> entitys =  imDataBase.getTheMessageByUserId(UserUtil.userid);
		if(entitys != null){
			boolean isFind = false;
			for (Entity entity : entitys) {
				//未读
				if(entity.getIsRead() == 0 && entity.getFromId() != UserUtil.userid && UserUtil.userState == 1){
					isFind = true;
					break;
				}
			}
			if(isFind){
				mainMenu.setMessageButtonRes(true);
				messageImageId.setVisibility(View.VISIBLE);
			}else{
				mainMenu.setMessageButtonRes(false);
				messageImageId.setVisibility(View.GONE);
			}
			
		}else{
			mainMenu.setMessageButtonRes(false);
			messageImageId.setVisibility(View.GONE);
		}
		
	}
}
