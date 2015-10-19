/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.ui.view.MainMenu;
import com.cmmobi.icuiniao.util.ActionID;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.LogoutUtil;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;
import com.cmmobi.icuiniao.weibo.WeiboLoginActivity;
import com.icuiniao.plug.im.Entity;
import com.icuiniao.plug.im.IMDataBase;
import com.icuiniao.plug.im.IMService;

/**
 * @author hw
 *
 */
public class LoginAndRegeditActivity extends Activity implements AnimationListener{

	private int type;
	private final static int TYPE_LOGIN = 0;//未登陆界面
	private final static int TYPE_LOGIN_ALIVE = 1;//已登陆界面
	
	private LinearLayout loginpage;
	private Button titlebar_menubutton;
	private Button titlebar_backbutton;
	private Button titlebar_menuLeft;
	private EditText input_name;
	private EditText input_password;
//	private Button login_regedit;
	
	private Button login_login;
	private Button weibobtn;
	private Button qqbtn;
	private Button taobaobtn;
	//private TextView findpw_btn;
	
	private ImageView login_icon;
	private TextView login_name;
	private Button login_exit;
	
	private ConnectUtil mConnectUtil;
	
	private boolean isAnimationOpen;//主菜单动画状态
	private int animationIndex;
	private MainMenu mainMenu;
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;
	//消息提醒
	private ImageView messageImageId;
	private IMDataBase imDataBase;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imDataBase = new IMDataBase(this);
		getWindow().setSoftInputMode(  
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mConnectUtil = null;
		isAnimationOpen = false;
		if(UserUtil.userid != -1&&UserUtil.userState == 1){
			type = TYPE_LOGIN_ALIVE;
			setContentView(R.layout.login_alive);
			loginpage = (LinearLayout)findViewById(R.id.loginpage);
			titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
			titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
			titlebar_menuLeft = (Button)findViewById(R.id.titlebar_leftmenu);
			login_exit = (Button)findViewById(R.id.login_exit);
			login_icon = (ImageView)findViewById(R.id.login_icon);
			login_name = (TextView)findViewById(R.id.login_name);
			mainMenu = (MainMenu)findViewById(R.id.mainmenu);
			loadingBar = (ProgressBar)findViewById(R.id.loading);
			messageImageId = (ImageView)findViewById(R.id.messageImageId);
			if(UserUtil.username != null&&UserUtil.username.length() > 0){
				login_name.setText(UserUtil.username);
			}
			titlebar_backbutton.setVisibility(View.VISIBLE);
			titlebar_menubutton.setVisibility(View.INVISIBLE);
			titlebar_menuLeft.setVisibility(View.INVISIBLE);
			switch (UserUtil.logintype) {
			case 0://翠鸟
				login_exit.setBackgroundResource(R.drawable.local_exitbtn_0);
				break;
			case 1://qq
				login_exit.setBackgroundResource(R.drawable.local_exitbtnqq_0);
				break;
			case 2://微博
				login_exit.setBackgroundResource(R.drawable.local_exitbtnwb_0);
				break;
			case 3:
				login_exit.setBackgroundResource(R.drawable.local_exitbtntaobao_0);
				break;
			}
			
//			titlebar_menubutton.setOnClickListener(menuClickListener);
//			titlebar_menuLeft.setOnClickListener(menuClickListener);
			titlebar_backbutton.setOnClickListener(backClickListener);
			login_exit.setOnClickListener(exitClickListener);
			//获取用户头像
			mConnectUtil = new ConnectUtil(LoginAndRegeditActivity.this, mHandler,1,0);
			mConnectUtil.connect(URLUtil.URL_GET_USERICON+UserUtil.userid+".jpg", HttpThread.TYPE_IMAGE, 0);
			addProgress();
		}else{
//			Intent intent11 = new Intent();
//			intent11.setClass(this, WebViewLoginActivity.class);
//			String url = addUrlParam(URLUtil.URL_LOGIN);
//			LogPrint.Print("webview","url = "+url);
//			intent11.putExtra("url", url);
//			startActivity(intent11);
//			finish();
			type = TYPE_LOGIN;
			setContentView(R.layout.loginb);
			loginpage = (LinearLayout)findViewById(R.id.loginpage);
			titlebar_menuLeft = (Button)findViewById(R.id.titlebar_leftmenu);
			mainMenu = (MainMenu)findViewById(R.id.mainmenu);
			titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
			input_name = (EditText)findViewById(R.id.input_name);
			input_password = (EditText)findViewById(R.id.input_password);
//			login_regedit = (Button)findViewById(R.id.login_regedit);
			login_login = (Button)findViewById(R.id.login_login);
			weibobtn = (Button)findViewById(R.id.weibobtn);
			qqbtn = (Button)findViewById(R.id.qqbtn);
			taobaobtn = (Button)findViewById(R.id.taobaobtn);
//			findpw_btn = (TextView)findViewById(R.id.findpw_btn);
			messageImageId = (ImageView)findViewById(R.id.messageImageId);
			titlebar_menubutton.setOnClickListener(menuClickListener);
			titlebar_menuLeft.setOnClickListener(menuClickListener);
//			login_regedit.setOnClickListener(regeditClickListener);
			login_login.setOnClickListener(loginClickListener);
			weibobtn.setOnClickListener(weiboClickListener);
			qqbtn.setOnClickListener(qqClickListener);
			taobaobtn.setOnClickListener(taobaoClickListener);
//			findpw_btn.setOnClickListener(findpwClickListener);			
			
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(UserUtil.isRemoteLogin){
			LogoutUtil.showMessagetToLogout(this);
		}
		getMessageByUserId();
		//注册广播
		registerReceiver();
		if(isAnimationOpen){
			gotoMenu();
		}
	}
	
	private OnClickListener backClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	};
	
	private OnClickListener menuClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			gotoMenu();
		}
		
	};
	
	private OnClickListener regeditClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			if(imm != null){
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			// TODO Auto-generated method stub
			startActivityForResult(new Intent(LoginAndRegeditActivity.this, RegeditActivity.class),9001);
		}
	};
	
	private OnClickListener loginClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
//			final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//			if(imm != null){
//				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//			}
//			String name;
//			String pw;
//			if(input_name.getText() != null&&input_name.getText().length() > 0){
//				name = input_name.getText().toString().trim();
//			}else{
//				CommonUtil.ShowToast(LoginAndRegeditActivity.this, "请输入常用邮箱");
//				return;
//			}
//			if(input_password.getText() != null&&input_password.getText().length() > 0){
//				pw = input_password.getText().toString().trim();
//			}else{
//				CommonUtil.ShowToast(LoginAndRegeditActivity.this, "请输入密码");
//				return;
//			}
//			mConnectUtil = new ConnectUtil(LoginAndRegeditActivity.this, mHandler,0);
//			mConnectUtil.connect(addUrlParam(URLUtil.URL_LOGIN, name, pw, 0), HttpThread.TYPE_PAGE, 0);
			if(CommonUtil.isNetWorkOpen(LoginAndRegeditActivity.this)){
				Intent intent11 = new Intent();
				intent11.setClass(LoginAndRegeditActivity.this, LoginCuiniaoActivity.class);
				startActivity(intent11);
//				Intent intent11 = new Intent();
//				intent11.setClass(LoginAndRegeditActivity.this, WebViewLoginActivity.class);
//				String url = addUrlParam(URLUtil.URL_LOGIN, name, pw, 0);
//				LogPrint.Print("webview","login url = "+url);
//				intent11.putExtra("url", url);
//				startActivityForResult(intent11, 9000);
			}else{
				CommonUtil.ShowToast(LoginAndRegeditActivity.this, "杯具了- -!\n联网不给力啊");
			}
			
		}
	};
	
	private OnClickListener findpwClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			startActivity(new Intent(LoginAndRegeditActivity.this, FindPasswordActivity.class));
		}
	};
	
	private OnClickListener exitClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mConnectUtil = new ConnectUtil(LoginAndRegeditActivity.this, mHandler,0);
			mConnectUtil.connect(URLUtil.URL_LOGOUT+"?logintype="+UserUtil.logintype+"&plaid="+URLUtil.plaid, HttpThread.TYPE_PAGE, 101);
		}
	};
	
	private OnClickListener weiboClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			if(imm != null){
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			if(CommonUtil.isNetWorkOpen(LoginAndRegeditActivity.this)){
				Intent intent11 = new Intent();
				intent11.setClass(LoginAndRegeditActivity.this, WeiboLoginActivity.class);
				//String url = addUrlParam(URLUtil.URL_LOGIN, "", "", 2);
				//LogPrint.Print("webview","login url = "+url);
				//intent11.putExtra("url", url);
				intent11.putExtra("actionType", "login");
				startActivityForResult(intent11, 9000);
			}else{
				CommonUtil.ShowToast(LoginAndRegeditActivity.this, "杯具了- -!\n联网不给力啊");
			}
//			if(UserUtil.userid == -1){
//				CommonUtil.isConnecting = false;
//				Intent intent = new Intent();
//				intent.putExtra("type", 1);
//				intent.setClass(LoginAndRegeditActivity.this, AuthorizeActivity.class);
//				startActivity(intent);
//				finish();
//			}
		}
	};
	
	private OnClickListener qqClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			if(imm != null){
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			if(CommonUtil.isNetWorkOpen(LoginAndRegeditActivity.this)){
				Intent intent11 = new Intent();
				intent11.setClass(LoginAndRegeditActivity.this, WebViewLoginActivity.class);
				String url = addUrlParam(URLUtil.URL_LOGIN, "", "", 1);
				LogPrint.Print("webview","login url = "+url);
				intent11.putExtra("url", url);
				startActivityForResult(intent11, 9000);
			}else{
				CommonUtil.ShowToast(LoginAndRegeditActivity.this, "杯具了- -!\n联网不给力啊");
			}
//			if(UserUtil.userid == -1){
//				CommonUtil.isConnecting = false;
//				Intent intent = new Intent();
//				intent.putExtra("type", 1);
//				intent.setClass(LoginAndRegeditActivity.this, TAuthorzeActivity.class);
//				startActivity(intent);
//				finish();
//			}
		}
	};
	/**淘宝登陆监听
	 * 
	 */
	private OnClickListener taobaoClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//隐藏键盘
			final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			if(imm != null){
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			if(CommonUtil.isNetWorkOpen(LoginAndRegeditActivity.this)){
				Intent intent11 = new Intent();
				intent11.setClass(LoginAndRegeditActivity.this, WebViewLoginActivity.class);
				String url = addUrlParam(URLUtil.URL_LOGIN, "", "", 3);
				LogPrint.Print("taobao","login url = "+url);
				intent11.putExtra("url", url);
				intent11.putExtra("logintype", 3);
				startActivityForResult(intent11, 9000);
			}else{
				CommonUtil.ShowToast(LoginAndRegeditActivity.this, "杯具了- -!\n联网不给力啊");
			}
		
		}
	};
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageID.MESSAGE_CONNECT_START:
				addProgress();
				break;
			case MessageID.MESSAGE_CONNECT_ERROR:
				closeProgress();
				CommonUtil.ShowToast(getApplicationContext(), (String)msg.obj);
				break;
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				closeProgress();
				if(mConnectUtil != null){
					if("text/json".equals(msg.getData().getString("content_type"))){
						Json((byte[])msg.obj,msg.arg1);
					}else if("text/html".equalsIgnoreCase(msg.getData().getString("content_type"))){
						try {
							String tmpurl = new String((byte[])msg.obj,"UTF-8");
							if(tmpurl != null){
								mConnectUtil = new ConnectUtil(LoginAndRegeditActivity.this, mHandler,0);
								mConnectUtil.connect(tmpurl, HttpThread.TYPE_IMAGE, 0);
								addProgress();
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}else{//图片
						setImageView((byte[])msg.obj,msg.getData().getString("mUrl"));
					}
				}
				break;
			case MessageID.MESSAGE_MAINMENU_OPEN:
				if(loginpage != null){
					if(loginpage.getRight() > getMainMenuAnimationPos(50)){
						loginpage.offsetLeftAndRight(animationIndex);
						loginpage.postInvalidate();
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_OPEN);
						animationIndex = -(CommonUtil.screen_width-getMainMenuAnimationPos(50))/5;
					}
				}
				break;
			case MessageID.MESSAGE_MAINMENU_CLOSE:
				if(loginpage != null){
					if(loginpage.getLeft() < 0){
						loginpage.offsetLeftAndRight(animationIndex);
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_CLOSE);
						loginpage.postInvalidate();
						animationIndex = (CommonUtil.screen_width-getMainMenuAnimationPos(50))/5;
					}
				}
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
	
	private void Json(byte[] data,int index){
		try{
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = "+str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			if(index == 101){//登出
				if(result != null){
					if(result.equalsIgnoreCase("true")){
						CommonUtil.saveRemoteLogout(this, false);
						UserUtil.isNewLoginOrExit = true;
						UserUtil.isLogout = true;
						UserUtil.userState = 0;
						try {
							String oid = jObject.getString("oid");
							if(oid != null){
								UserUtil.userid = Integer.parseInt(oid);
								CommonUtil.saveUserId(LoginAndRegeditActivity.this, UserUtil.userid);
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
						UserUtil.clearSharePreference(LoginAndRegeditActivity.this, "退出成功，小C会想你的。");
						//停止信息服务
						stopService(new Intent(LoginAndRegeditActivity.this,IMService.class));
						OfflineLog.writeMainMenu_MessageButton((byte)0);//写入离线日志
						Intent intent11 = new Intent();
						intent11.setClass(LoginAndRegeditActivity.this, LoginAndRegeditActivity.class);
						startActivity(intent11);
						//发送登出广播
						sendBroadcast(new Intent(ActionID.ACTION_BROADCAST_LOGOUT));
						//重新连接
						startService(new Intent(LoginAndRegeditActivity.this,IMService.class));
						finish();
					}else{
						CommonUtil.ShowToast(LoginAndRegeditActivity.this, "退出失败，爱你就应给你自由，在退一次");
					}
				}
			}else{//登录
//				if(result != null){
//					if(result.equalsIgnoreCase("true")){
//						String oid = jObject.getString("oid");
//						String name = jObject.getString("name");
//						String gender = jObject.getString("gender");
//						if(oid != null){
//							UserUtil.userid = Integer.parseInt(oid);
//						}
//						UserUtil.username = name;
//						if(gender != null){
//							UserUtil.gender = Integer.parseInt(gender);
//						}else{
//							UserUtil.gender = -1;
//						}
//						if(UserUtil.userid != -1&&UserUtil.username != null){
//							UserUtil.isNewLoginOrExit = true;
//							LogPrint.Print("=============oid = "+UserUtil.userid);
//							LogPrint.Print("=============name = "+UserUtil.username);
//							LogPrint.Print("=============gender = "+UserUtil.gender);
//							CommonUtil.saveUserId(LoginAndRegeditActivity.this, UserUtil.userid);
//							CommonUtil.saveUserName(LoginAndRegeditActivity.this, UserUtil.username);
//							UserUtil.logintype = 0;
//							CommonUtil.saveLoginType(LoginAndRegeditActivity.this, UserUtil.logintype);
//							UserUtil.userState = 1;
//							CommonUtil.saveUserState(LoginAndRegeditActivity.this, UserUtil.userState);
//							CommonUtil.saveGender(LoginAndRegeditActivity.this, UserUtil.gender);
//							CommonUtil.ShowToast(LoginAndRegeditActivity.this, UserUtil.username+"，\n等您好久了，您终于回来啦！");
//							//开启信息服务
//							if(CommonUtil.getMessageReceiverState(this)){
//								startService(new Intent(this,MessageReceiveService.class));
//								CommonUtil.saveMessageReceiverState(this, true);
//							}else{
//								stopService(new Intent(this,MessageReceiveService.class));
//								CommonUtil.saveMessageReceiverState(this, false);
//							}
////								Intent intent11 = new Intent();
////								intent11.setClass(LoginAndRegeditActivity.this, LoginAndRegeditActivity.class);
////								startActivity(intent11);
//							finish();
//						}
//					}else{
//						String msg = jObject.getString("msg");
//						if(msg != null){
//							CommonUtil.ShowToast(LoginAndRegeditActivity.this, "囧！登不进去，再来一次！\n"+msg);
//						}else{
//							CommonUtil.ShowToast(LoginAndRegeditActivity.this, "囧！登不进去，再来一次！");
//						}
//					}
//				}
			}
		}catch(Exception e){
			
		}
	}
	
	public void setImageView(byte[] data,String url){
		try {
			Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
			if(temp == null)return;
			Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.usericonbg);
			temp = CommonUtil.resizeImage(temp, background.getWidth(), background.getHeight());
			login_icon.setImageBitmap(CommonUtil.mergerIcon(background, temp,7));
//			//保存图片
//			CommonUtil.writeToFile(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(url), data);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
//	private SystemProgress progress;
	public void addProgress(){
		try {
//			if(progress != null)return;
//			progress = new SystemProgress(this, this, null){
//				public void cancelData(){
//					
//				}
//			};
			if(loadingBar != null){
				loadingBar.setVisibility(View.VISIBLE);
			}
			
		} catch (Exception e) {
		}
	}
	
	public void closeProgress(){
//		if(progress != null){
//			progress.close();
//			progress = null;
//		}
		if(loadingBar != null){
			loadingBar.setVisibility(View.INVISIBLE);
		}
	}
	
	public String addUrlParam(String url,String em,String pw,int logintype){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		if(logintype == 0){
			return url+"?oid="+UserUtil.userid+"&uname="+CommonUtil.toUrlEncode(em)+"&upwd="+CommonUtil.toUrlEncode(UserUtil.Encryption(pw))+"&logintype="+logintype
			+"&pc="+URLUtil.pc+"&fid="+URLUtil.fid+"&os_version="+CommonUtil.toUrlEncode(CommonUtil.getOs_Version())+"&network_type="+CommonUtil.getApnType(this)
	  		+"&plaid="+URLUtil.plaid+"&dpi="+URLUtil.dpi()+"&imsi="+CommonUtil.getIMSI(this)+"&imei="+CommonUtil.getIMEI(this)+"&sim="+CommonUtil.getSimType(this)+"&reletm="+URLUtil.reletm+"&pla="+CommonUtil.toUrlEncode(CommonUtil.getDeviceName())+"&ver="+URLUtil.version+"&deviceid="+CommonUtil.getIMEI(this);
		}else{
			return url+"?oid="+UserUtil.userid+"&logintype="+logintype
			+"&pc="+URLUtil.pc+"&fid="+URLUtil.fid+"&os_version="+CommonUtil.toUrlEncode(CommonUtil.getOs_Version())+"&network_type="+CommonUtil.getApnType(this)
	  		+"&plaid="+URLUtil.plaid+"&dpi="+URLUtil.dpi()+"&imsi="+CommonUtil.getIMSI(this)+"&imei="+CommonUtil.getIMEI(this)+"&sim="+CommonUtil.getSimType(this)+"&reletm="+URLUtil.reletm+"&pla="+CommonUtil.toUrlEncode(CommonUtil.getDeviceName())+"&ver="+URLUtil.version+"&deviceid="+CommonUtil.getIMEI(this);
		}
		
	}
	
	public String addUrlParam(String url,int oid){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"?oid="+oid;
	}
	
	public String addUrlParam(String url){
  		if(URLUtil.IsLocalUrl()){
			return url;
		}
  		return url+"?oid="+UserUtil.userid+"&pc="+URLUtil.pc+"&fid="+URLUtil.fid+"&os_version="+CommonUtil.toUrlEncode(CommonUtil.getOs_Version())+"&network_type="+CommonUtil.getApnType(this)
  		+"&plaid="+URLUtil.plaid+"&dpi="+URLUtil.dpi()+"&imsi="+CommonUtil.getIMSI(this)+"&imei="+CommonUtil.getIMEI(this)+"&sim="+CommonUtil.getSimType(this)+"&reletm="+URLUtil.reletm+"&pla="+CommonUtil.toUrlEncode(CommonUtil.getDeviceName())+"&ver="+URLUtil.version+"&deviceid="+CommonUtil.getIMEI(this);
  	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		gotoMenu();
		return true;
	}
	
	private void gotoMenu(){
		if(type == TYPE_LOGIN_ALIVE)return;
		if(loginpage != null){
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
	    		loginpage.startAnimation(animation);
	    		OfflineLog.writeMainMenu();//写入离线日志
			}else{
				if(mainMenu != null){
					mainMenu.setIsCanClick(false);
				}
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.left_in);
				animation.setAnimationListener(this);
	    		animation.setFillEnabled(true);
	    		animation.setFillAfter(true);
	    		loginpage.startAnimation(animation);
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

	//登录：9000
	//注册：9001
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			switch (requestCode) {
			case 9000:
				if("true".equals(data.getAction())){
					LogPrint.Print("login ok");					
					finish();
				}else{
					LogPrint.Print("login false");
				}
				break;
			case 9001:
				if("true".equals(data.getAction())){
					LogPrint.Print("regedit ok");					
					finish();
				}
				break;
			}
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if(isAnimationOpen){		
			titlebar_menuLeft.setVisibility(View.VISIBLE);			
			titlebar_menubutton.setClickable(false);
		}else{
			titlebar_menubutton.setClickable(true);
		}
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		if(!isAnimationOpen){
			titlebar_menuLeft.setVisibility(View.INVISIBLE);			
		}
		
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
	
	//新增消息广播
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//销毁广播
		unRegisterReceiver();
	}
	
}
