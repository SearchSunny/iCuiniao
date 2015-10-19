/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.menuclick.Bind_MenuClick;
import com.cmmobi.icuiniao.ui.view.MainMenu;
import com.cmmobi.icuiniao.ui.view.SlipButton;
import com.cmmobi.icuiniao.ui.view.SlipButton.OnChangedListener;
import com.cmmobi.icuiniao.util.ActionID;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.PageID;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;
import com.cmmobi.icuiniao.weibo.WeiboLoginActivity;
import com.icuiniao.plug.im.Entity;
import com.icuiniao.plug.im.IMDataBase;

/**
 * @author hw
 *绑定
 */
public class BindActivity extends Activity implements AnimationListener{
	
	//菜单按钮
	private Button titlebar_menubutton = null;
	//左侧的隐藏菜单按钮，用于动画播放后的控制
	private Button titlebar_leftmenu = null;
	//滑动控件
	private SlipButton slipbuttonWeibo = null;
	//滑动控件
	private SlipButton slipbuttonQQ = null;
	//显示账号
	private TextView textViewAccount = null;
	//修改按钮
	private ImageButton imageButtonPassword = null;
	//显示微博昵称
	private TextView textViewNicknameWeibo = null;
	//显示QQ昵称
	private TextView textViewNicknameQQ = null;
	private ConnectUtil mConnectUtil;
	private int bindNum;//已绑定的个数,大于1时才能解除绑定
	private final static String BIND_WEIBO = "未绑定新浪微博";
	private final static String BIND_QQ = "未绑定QQ";
	private boolean isBindQQ = false;
	private boolean isBindWeibo = false;
	private boolean isSucceedQQ = false;
	private boolean isSucceedWeibo = false;
	private boolean isSucceed = false;
	private boolean isAnimationOpen;//主菜单动画状态
	private int animationIndex;
	private MainMenu mainMenu;
	private ScrollView scrollView;
	private int logintype;
	private ProgressBar loadingBar;
	private EditText editTextEmail;
	private EditText editTextPassword;
	private Button buttonComplete;
	
	IMDataBase imDataBase;
	//消息提醒
	private ImageView messageImageId;
	private boolean messageImageState;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imDataBase = new IMDataBase(this);
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
		setContentView(R.layout.bind_loading);
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		isAnimationOpen = false;
		bindNum = 0;
		logintype = 0;	
		mConnectUtil = new ConnectUtil(this, mHandler,0);
		mConnectUtil.connect(URLUtil.URL_BIND_GET+"?plaid="+URLUtil.plaid, HttpThread.TYPE_PAGE, 0);
		OfflineLog.writeBind();
	}

	/**
	 * QQ滑动按钮监听
	 */
	private OnChangedListener qqListener = new OnChangedListener() {
		
		@Override
		public void onChanged(boolean checkState) {
			logintype = 1;
			//去绑定
			if(checkState){
				if(CommonUtil.isNetWorkOpen(BindActivity.this)){
					Intent intent11 = new Intent();
					intent11.setClass(BindActivity.this, WebViewLoginActivity.class);
					String url = addUrlParam(URLUtil.URL_BIND_ADD, UserUtil.userid, logintype, "qq");
					LogPrint.Print("webview","bind qq url = "+url);
					intent11.putExtra("url", url);
					isSucceedQQ = true;
					startActivityForResult(intent11, 9002);
				}else{
					CommonUtil.ShowToast(BindActivity.this, "杯具了- -!\n联网不给力啊");
				}
			}
			//取消绑定
			else{
				Bind_MenuClick menuClick = new Bind_MenuClick(mHandler,1);
				Intent intent3 = new Intent();
				intent3.setClass(BindActivity.this, AbsCuiniaoMenu.class);
				intent3.putExtra("title", PageID.SETTING_MESSAGE_MENU_TITLE);
				intent3.putExtra("items", PageID.BIND_MENU_ITEM);
				startActivity(intent3);
				AbsCuiniaoMenu.set(menuClick);
			}
		}
	};
	
	/**
	 * 微博滑动按钮监听
	 */
	private OnChangedListener weiboListener = new OnChangedListener() {
		
		@Override
		public void onChanged(boolean checkState) {
			logintype = 2;
			//去绑定
			if(checkState){
				if(CommonUtil.isNetWorkOpen(BindActivity.this)){
					Intent intent11 = new Intent();
					intent11.setClass(BindActivity.this, WeiboLoginActivity.class);
					String url = addUrlParam(URLUtil.URL_BIND_ADD, UserUtil.userid, logintype, "sina");
					//LogPrint.Print("webview","bind weibo url = "+url);
					intent11.putExtra("actionType","bind");
					isSucceedWeibo = true;
					startActivityForResult(intent11, 9002);
				}else{
					CommonUtil.ShowToast(BindActivity.this, "杯具了- -!\n联网不给力啊");
				}
			}
			//取消绑定
			else{
				Bind_MenuClick menuClick = new Bind_MenuClick(mHandler,2);
				Intent intent3 = new Intent();
				intent3.setClass(BindActivity.this, AbsCuiniaoMenu.class);
				intent3.putExtra("title", PageID.SETTING_MESSAGE_MENU_TITLE);
				intent3.putExtra("items", PageID.BIND_MENU_ITEM);
				startActivity(intent3);
				AbsCuiniaoMenu.set(menuClick);
			}
		}
	};
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(messageImageState){
			
			getMessageByUserId();
		}
		//注册广播
		registerReceiver();
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
		//为防止从绑定Activity中点击返回按钮情况重新为滑动按钮赋值
		if(slipbuttonQQ!=null&&slipbuttonWeibo!=null){
			if(isSucceed){
				slipbuttonQQ.setCheck(isBindQQ);
				slipbuttonWeibo.setCheck(isBindWeibo);
			}
			else{
				if(isSucceedQQ){
					slipbuttonQQ.setCheck(false);
				}
				if(isSucceedWeibo){
					slipbuttonWeibo.setCheck(false);
				}
			}
		}	
		//lyb 关闭菜单
		if(isAnimationOpen){
			gotoMenu();
		}
	}	
	
	/**
	 * 触摸软键盘外部，软键盘消失
	 */
	private void hideSoftInput(){
		scrollView.setOnTouchListener(new View.OnTouchListener() {
			float x = 0, y = 0;
			@Override
			public boolean onTouch(View v, MotionEvent event) {				
				switch(event.getAction()){
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_DOWN:
					x = event.getX();
					y = event.getY();
					break;
				case MotionEvent.ACTION_UP:
					float moveX = Math.abs(x - event.getX());
					float moveY = Math.abs(y - event.getY());
					float ratio = CommonUtil.screen_width / 480f;
					if(moveX < 15 * ratio && moveY < 15 * ratio){
						CommonUtil.hideSoftInput(editTextEmail, BindActivity.this);
					}
					break;	
				}							
				return false;
			}
		});						
	}
	/**
	 * 初始化控件
	 */
	private void initWidgetByBind(){
		//初始化控件
		titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
		titlebar_leftmenu = (Button)findViewById(R.id.titlebar_leftmenu);
		slipbuttonWeibo = (SlipButton)findViewById(R.id.slitbuttonWeibo);
		slipbuttonQQ = (SlipButton)findViewById(R.id.slitbuttonQQ);
		textViewAccount = (TextView)findViewById(R.id.textViewAccount);
		imageButtonPassword = (ImageButton)findViewById(R.id.imageButtonPassword);
		textViewNicknameWeibo = (TextView)findViewById(R.id.textViewNicknameWeibo);
		textViewNicknameQQ = (TextView)findViewById(R.id.textViewNicknameQQ);
		scrollView = (ScrollView)findViewById(R.id.scrollView);
		mainMenu = (MainMenu)findViewById(R.id.mainmenu);
		
		messageImageId = (ImageView)findViewById(R.id.messageImageId);
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		//设置滑动按钮事件
		slipbuttonWeibo.setOnChangedListener(weiboListener);
		slipbuttonQQ.setOnChangedListener(qqListener);
		//设置修改密码按钮事件
		imageButtonPassword.setOnClickListener(passwordListener);
		//菜单按钮事件
		titlebar_menubutton.setOnClickListener(menuClickListener);
		titlebar_leftmenu.setOnClickListener(menuClickListener);
		
	}
	private void initWidgetByBindInfo(){
		titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
		titlebar_leftmenu = (Button)findViewById(R.id.titlebar_leftmenu);
		slipbuttonWeibo = (SlipButton)findViewById(R.id.slitbuttonWeibo);
		slipbuttonQQ = (SlipButton)findViewById(R.id.slitbuttonQQ);
		textViewNicknameWeibo = (TextView)findViewById(R.id.textViewNicknameWeibo);
		textViewNicknameQQ = (TextView)findViewById(R.id.textViewNicknameQQ);
		scrollView = (ScrollView)findViewById(R.id.scrollView);
		mainMenu = (MainMenu)findViewById(R.id.mainmenu);
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		editTextEmail = (EditText)findViewById(R.id.editTextEmail);
		editTextPassword = (EditText)findViewById(R.id.editTextPassword);
		buttonComplete = (Button)findViewById(R.id.buttonComplete);
		
		messageImageId = (ImageView)findViewById(R.id.messageImageId);
		//设置滑动按钮事件
		slipbuttonWeibo.setOnChangedListener(weiboListener);
		slipbuttonQQ.setOnChangedListener(qqListener);
		//菜单按钮事件
		titlebar_menubutton.setOnClickListener(menuClickListener);
		titlebar_leftmenu.setOnClickListener(menuClickListener);
		//完善按钮监听
		buttonComplete.setOnClickListener(completeListener);
		hideSoftInput();
	}
	/**
	 * 修改密码按钮监听
	 */
	private OnClickListener passwordListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(UserUtil.logintype == 0||UserUtil.logintype == 1||UserUtil.logintype == 2||UserUtil.logintype == 3){
				Intent intent = new Intent();
				intent.setClass(BindActivity.this, SettingPwActivity.class);
				startActivity(intent);
			}else{
				CommonUtil.ShowToast(BindActivity.this, "请登录或者注册成为小C的主人吧。");
			}
		}
	}; 
	
	/**
	 * 完善按钮监听
	 */
	private OnClickListener completeListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String email = editTextEmail.getText().toString();
			String password = editTextPassword.getText().toString();
			if(email.equals("")){
				CommonUtil.ShowToast(BindActivity.this, "请输入注册邮箱。");
			}
			else if(password.equals("")){
				CommonUtil.ShowToast(BindActivity.this, "请输入密码。");
			}
			else{
				mConnectUtil = new ConnectUtil(BindActivity.this, mHandler,0);
				mConnectUtil.connect(addUrlParam(URLUtil.URL_COMPLETE_ADD, UserUtil.userid, email, UserUtil.Encryption(password),URLUtil.plaid), HttpThread.TYPE_PAGE, 3);
			}
		}
	};
	
	/**
	 * 菜单按钮监听
	 */
	public OnClickListener menuClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			gotoMenu();
		}
	};
	
	/**
	 * 开打或关闭Menu
	 */
	private void gotoMenu(){
//		if(scrollView != null){
//			animationIndex = 0;
//			isAnimationOpen = !isAnimationOpen;
//			if(isAnimationOpen){
//				if(mainMenu != null){
//					mainMenu.setIsCanClick(true);
//				}
//				mHandler.removeMessages(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
//				mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_OPEN);
//				OfflineLog.writeMainMenu();//写入离线日志
//			}else{
//				if(mainMenu != null){
//					mainMenu.setIsCanClick(false);
//				}
//				mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_CLOSE);
//				mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
//			}
//		}
		if(scrollView != null){
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
	    		scrollView.startAnimation(animation);
	    		OfflineLog.writeMainMenu();//写入离线日志
			}else{
				if(mainMenu != null){
					mainMenu.setIsCanClick(false);
				}
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.left_in);
				animation.setAnimationListener(this);
	    		animation.setFillEnabled(true);
	    		animation.setFillAfter(true);
	    		scrollView.startAnimation(animation);
			}						
		}
	}
	
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
				Json((byte[])msg.obj, msg.arg1);
				mHandler.sendEmptyMessage(9900);
				break;
			case MessageID.MESSAGE_BIND_MENUCLICK_CANCEL:
				slipbuttonWeibo.setCheck(isBindWeibo);
				slipbuttonQQ.setCheck(isBindQQ);
				break;
			case MessageID.MESSAGE_BIND_MENUCLICK:
				switch (msg.arg1) {
				case 1://qq
					if(CommonUtil.isNetWorkOpen(BindActivity.this)){
						Intent intent11 = new Intent();
						intent11.setClass(BindActivity.this, WebViewLoginActivity.class);
						logintype = 1;
						String url = addUrlParam(URLUtil.URL_BIND_REMOVE, UserUtil.userid, CommonUtil.getBindId_QQ(BindActivity.this),logintype);
						LogPrint.Print("webview","unbind qq url = "+url);
						intent11.putExtra("url", url);
						startActivityForResult(intent11, 9002);
					}else{
						CommonUtil.ShowToast(BindActivity.this, "杯具了- -!\n联网不给力啊");
					}
					break;
				case 2://weibo
					if(CommonUtil.isNetWorkOpen(BindActivity.this)){
						Intent intent11 = new Intent();
						intent11.setClass(BindActivity.this, WebViewLoginActivity.class);
						logintype = 2;
						String url = addUrlParam(URLUtil.URL_BIND_REMOVE, UserUtil.userid, CommonUtil.getBindId_Weibo(BindActivity.this),logintype);
						LogPrint.Print("webview","unbind weibo url = "+url);
						intent11.putExtra("url", url);
						startActivityForResult(intent11, 9002);
					}else{
						CommonUtil.ShowToast(BindActivity.this, "杯具了- -!\n联网不给力啊");
					}
					break;
				}
				break;
			case MessageID.MESSAGE_MAINMENU_OPEN:
				if(scrollView != null){
					if(scrollView.getRight() > getMainMenuAnimationPos(50)){
						scrollView.offsetLeftAndRight(animationIndex);
						scrollView.postInvalidate();
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_OPEN);
						animationIndex = -(CommonUtil.screen_width-getMainMenuAnimationPos(50))/5;
					}
				}
				break;
			case MessageID.MESSAGE_MAINMENU_CLOSE:
				if(scrollView != null){
					if(scrollView.getLeft() < 0){
						scrollView.offsetLeftAndRight(animationIndex);
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_CLOSE);
						scrollView.postInvalidate();
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
			case 9900:
				getMessageByUserId();
				messageImageState = true;
			}
		}
	};
	
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
	public String addUrlParam(String url,int oid,int logintype,String type){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"?oid="+oid+"&plaid="+URLUtil.plaid+"&logintype="+logintype+"&type="+type;
	}
	
	public String addUrlParam(String url,int oid,int binderid,int logintype){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"?oid="+oid+"&plaid="+URLUtil.plaid+"&binderid="+binderid+"&logintype="+logintype;
	}
	
	public String addUrlParam(String url,int oid,String loginname,String loginpassword,String plaid){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"?oid="+oid+"&loginname="+loginname+"&loginpwd="+loginpassword+"&plaid="+plaid;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		gotoMenu();
		return true;
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
	//绑定：9002
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			switch (requestCode) {
			case 9002:
				if("true".equals(data.getAction())){
					isSucceed = true;
					LogPrint.Print("bind ok");
					int bindtype = data.getIntExtra("bindtype", -1);
					String nickname = data.getStringExtra("nickname"); 
					int binderid = data.getIntExtra("binderid",-1);
					LogPrint.Print("bindtype = "+bindtype);
					LogPrint.Print("logintype = "+logintype);
					if(bindtype != -1){
						if(bindtype == 0){//绑定完成
							if(logintype == 1){//qq
								bindNum ++;
								textViewNicknameQQ.setText(nickname);
								slipbuttonQQ.setCheck(true);
								isBindQQ = true;
								isSucceedQQ = true;
								CommonUtil.saveBindId_QQ(BindActivity.this, binderid);
							}else if(logintype == 2){//weibo
								bindNum ++;
								textViewNicknameWeibo.setText(nickname);
								slipbuttonWeibo.setCheck(true);
								isBindWeibo = true;
								isSucceedWeibo = true;
								CommonUtil.saveBindId_Weibo(BindActivity.this, binderid);
							}
						}else{//解除绑定完成
							if(logintype == 1){//qq
								bindNum --;
								textViewNicknameQQ.setText(BIND_QQ);
								slipbuttonQQ.setCheck(false);
								isBindQQ = false;
								isSucceedQQ = false;
							}else if(logintype == 2){//weibo
								bindNum --;
								textViewNicknameWeibo.setText(BIND_WEIBO);
								slipbuttonWeibo.setCheck(false);
								isBindWeibo = false;
								isSucceedWeibo = false;
							}
						}
					}
				}else{
					LogPrint.Print("bind false");
				}
				break;
			}
		}
	}
	
	//0:get 1:remove qq 2:remove weibo
	private void Json(byte[] data,int type){
		try {
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = "+str);
			switch (type) {
			case 0:
				JSONArray jsonArray = new JSONArray(str);
				int count = 0;
				for(int i = 0;jsonArray!=null&&i < jsonArray.length();i ++){
					JSONObject jObject = jsonArray.getJSONObject(i);
					String result = jObject.getString("result");
					if(result != null){
						if(result.equalsIgnoreCase("true")){
							boolean binder = jObject.getBoolean("binder");
							int logintype = jObject.getInt("logintype");
							int binderid = jObject.getInt("binderid");
							String nickname = jObject.getString("nickname");
							String loginname = jObject.getString("loginname");
							if(loginname.equals("")&&count==0){
								setContentView(R.layout.bind_info);
								initWidgetByBindInfo();
								CommonUtil.saveLoginName(BindActivity.this, loginname);
								count ++;
							}
							else if(!loginname.equals("")&&count==0){
								setContentView(R.layout.bind);
								initWidgetByBind();
								//将loginname写入到SharedPreferences
								CommonUtil.saveLoginName(BindActivity.this, loginname);
								count ++;
							}
							//显示用户账号信息
							if(loginname!=null&&!loginname.equals("")){
								textViewAccount.setText(loginname);
							}
							if(binder){//已绑定
								switch (logintype) {
								case 1://qq
									bindNum ++;
									textViewNicknameQQ.setText(nickname);
									slipbuttonQQ.setCheck(true);
									isBindQQ = true;
									break;
								case 2://weibo
									bindNum ++;
									textViewNicknameWeibo.setText(nickname);
									slipbuttonWeibo.setCheck(true);
									isBindWeibo = true;
									break;
								}
							}else{//未绑定
								switch (logintype) {
								case 1://qq
									textViewNicknameQQ.setText(BIND_QQ);
									slipbuttonQQ.setCheck(false);
									isBindQQ = false;
									break;
								case 2://weibo
									textViewNicknameWeibo.setText(BIND_WEIBO);
									slipbuttonWeibo.setCheck(false);
									isBindWeibo = false;
									break;
								}
							}
							//保存binderid
							switch (logintype) {
							case 1://qq
								CommonUtil.saveBindId_QQ(BindActivity.this, binderid);
								break;
							case 2://weibo
								CommonUtil.saveBindId_Weibo(BindActivity.this, binderid);
								break;
							}
						}
					}
				}
				break;
			case 3:
				JSONObject jsonObject = new JSONObject(str);
				String result = jsonObject.getString("result");
				if(result.equalsIgnoreCase(result)){
					Intent intent = new Intent(BindActivity.this,BindActivity.class);
					startActivity(intent);
					finish();
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onAnimationEnd(Animation animation) {
		if(isAnimationOpen){						
			titlebar_leftmenu.setVisibility(View.VISIBLE);			
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
		if (!isAnimationOpen) {
			titlebar_leftmenu.setVisibility(View.GONE);			
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
