/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.activity;


import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.Activity;
import com.cmmobi.icuiniao.Activity.LoginAndRegeditActivity;
import com.cmmobi.icuiniao.Activity.SajiaoActivity;
import com.cmmobi.icuiniao.ui.view.LikeLimitDialog;
import com.cmmobi.icuiniao.ui.view.MainMenu;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.MyWebViewClient;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

/**
 * @author hw
 *
 */
public class WebviewActivity extends Activity implements AnimationListener{
	
	private FrameLayout fwebview;
	private WebView webview;
	private ImageView webview_sajiao_pop;
//	private String urlString;
	private String titleString;
	private int commodityid;
	private String commodityImageString;
	private String commodityInfoString;
	private ProgressBar loadingBar;
	
	private boolean isAnimationOpen;//主菜单动画状态
	private int animationIndex;
	private MainMenu mainMenu;
	
	public static boolean isSajiaoSuccess;//撒娇是否成功
	
	private Button titlebar_backbutton;
	private Button titlebar_menubutton;
	private Button titlebar_leftmenu;
	private final int SAJIAO_RESULT_CODE = 1;
	private String urlString;
	
	private boolean likeState;
	private int deleteId;
	private String likenum;
	private ImageView webview_likebtn;
	private TextView webview_likenumbtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webviewpage);
//		urlString = null;
		likeState = getIntent().getExtras().getBoolean("likestate");
		deleteId = getIntent().getExtras().getInt("deleteid");
		likenum = getIntent().getExtras().getString("likenum");
		isSajiaoSuccess = false;
		titleString = null;
		commodityid = -1;
		commodityImageString = null;
		commodityInfoString = null;
		isAnimationOpen = false;
		fwebview = (FrameLayout)findViewById(R.id.fwebview);
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
		titlebar_leftmenu = (Button)findViewById(R.id.titlebar_leftmenu); 
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		webview = (WebView)findViewById(R.id.webview);
//		ImageView webview_leftbtn = (ImageView)findViewById(R.id.webview_leftbtn);
//		ImageView webview_rightbtn = (ImageView)findViewById(R.id.webview_rightbtn);
		webview_likebtn = (ImageView)findViewById(R.id.webview_likebtn);
		ImageView webview_flushbtn = (ImageView)findViewById(R.id.webview_flushbtn);
		ImageView webview_sajiao = (ImageView)findViewById(R.id.webview_sajiao);
		webview_sajiao_pop = (ImageView)findViewById(R.id.webview_sajiao_pop);
		mainMenu = (MainMenu)findViewById(R.id.mainmenu);
		
		if(likeState){
			webview_likebtn.setBackgroundResource(R.drawable.webview_heart_f);
        }
		webview_likenumbtn = (TextView)findViewById(R.id.webview_likenum);
		webview_likenumbtn.setText(likenum + "");
		
//		if(CommonUtil.getCoqutryButtonState(this)){//hide
//			webview_sajiao_pop.setVisibility(View.INVISIBLE);
//		}else{//show
//			webview_sajiao_pop.setVisibility(View.VISIBLE);
//		}
		//需求变化，一直显示； 2.1.0.000
		webview_sajiao_pop.setVisibility(View.VISIBLE);
		
		titlebar_backbutton.setOnClickListener(backbtnClickListener);
		titlebar_menubutton.setOnClickListener(menubtnClickListener);
		titlebar_leftmenu.setOnClickListener(menubtnClickListener);
		webview_likebtn.setOnClickListener(likebtnClickListener);
//		webview_leftbtn.setOnClickListener(leftbtnClickListener);
//		webview_rightbtn.setOnClickListener(rightbtnClickListener);
		webview_flushbtn.setOnClickListener(flushbtnClickListener);
		webview_sajiao.setOnClickListener(sajiaoClickListener);
		
		urlString = getIntent().getExtras().getString("url");
//		if(urlString != null){
//			//2012.8.29修改为直接加数值,用于短链接
//        	if(urlString.indexOf("?") >= 0){
////        		urlString += "&type=1";
//        		urlString += "1";
//        	}else{
////        		urlString += "?type=1";
//        		urlString += "1";
//        	}
//        }
//		this.urlString = urlString;
		this.titleString = getIntent().getExtras().getString("title");
		this.commodityid = getIntent().getExtras().getInt("commodityid");
		this.commodityImageString = getIntent().getExtras().getString("commodityImageString");
		this.commodityInfoString = getIntent().getExtras().getString("commodityInfoString");
		if(urlString != null){
			webview.requestFocus();
			webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
			webview.getSettings().setJavaScriptEnabled(true);
			webview.setWebViewClient(new MyWebViewClient(this, mHandler));
			webview.setVerticalScrollBarEnabled(false);
			webview.loadUrl(urlString);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(isSajiaoSuccess){
			finish();
			return;
		}
		//向服务器要地址信息
		if(UserUtil.userid != -1&&UserUtil.userState == 1){
			String strtmp = CommonUtil.getAddrManager(this);
			if(strtmp.length() <= 0){
				new ConnectUtil(this, mHandler,0).connect(URLUtil.URL_ADDRMANAGER_GET, HttpThread.TYPE_PAGE, 0);
			}
		}
		if(isAnimationOpen){
			gotoMenu();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(webview != null){
			webview.clearCache(false);
			webview.clearHistory();
			webview.stopLoading();
		}
	}
	
	@Override  //从视频进入看一看后返回的传值
	public void finish(){
		Intent intent = new Intent();
		intent.putExtra("likestate", likeState);
		intent.putExtra("likenum", likenum);
		setResult(RESULT_OK, intent);
		super.finish();
	}
	
	private WebViewClient mWebViewClient = new WebViewClient(){
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			closeProgress();
			LogPrint.Print("=============page finish");
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			addProgress();
			LogPrint.Print("=============page start");
			LogPrint.Print("=============page url:"+view.getUrl());
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// TODO Auto-generated method stub
			super.onReceivedError(view, errorCode, description, failingUrl);
			closeProgress();
			LogPrint.Print("=============page error:"+errorCode);
		}

		@Override
		public void onLoadResource(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onLoadResource(view, url);
			LogPrint.Print("=============page loadResource:"+url);
		}

	};
	
	private OnClickListener backbtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	};
	
	private OnClickListener menubtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			gotoMenu();
		}
	};
	
//	private OnClickListener leftbtnClickListener = new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			if(webview != null){
//				webview.goBack();
//			}
//		}
//	};
	
//	private OnClickListener rightbtnClickListener = new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			if(webview != null){
//				webview.goForward();
//			}
//		}
//	};
	
	private OnClickListener likebtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(UserUtil.userid != -1&&UserUtil.userState == 1){
				if(likeState){//删除
					new ConnectUtil(WebviewActivity.this, mHandler,0).connect(URLUtil.URL_MYLIKE_DELETE_SINGLE+"?id="+deleteId+"&oid="+UserUtil.userid+"&cid="+commodityid, HttpThread.TYPE_PAGE, 4);
				}else{//添加
					new ConnectUtil(WebviewActivity.this, mHandler,0).connect(URLUtil.URL_ADDLIKE+"?oid="+UserUtil.userid+"&cid="+commodityid+"&plaid="+URLUtil.plaid, HttpThread.TYPE_PAGE, 5);
				}
			}else{				
				CommonUtil.ShowToast(WebviewActivity.this, "请登陆或者注册，变身为小C的主人！");
				Intent intent11 = new Intent();
				intent11.setClass(WebviewActivity.this, LoginAndRegeditActivity.class);
				startActivity(intent11);
			}
		}			
		
	};
	
	private OnClickListener flushbtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(webview != null){
//				webview.reload();
				webview.loadUrl(urlString);  //断网再次联网无法刷新问题
			}
			LogPrint.Print("=================flush");
		}
	};
	
	private OnClickListener sajiaoClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆
				CommonUtil.ShowToast(WebviewActivity.this, "请登陆或者注册，变身为小C的主人！");
				Intent intent11 = new Intent();
				intent11.setClass(WebviewActivity.this, LoginAndRegeditActivity.class);
				startActivity(intent11);
				return;
			}
			//del by lyb 需求变化 2.1.0.000
//			if(webview_sajiao_pop.getVisibility() == View.VISIBLE){
//				CommonUtil.saveCoqutryButtonState(WebviewActivity.this, true);
//				webview_sajiao_pop.setVisibility(View.INVISIBLE);
//			}
			
//			shareEmail(mailTitle, mailMessage+titleString+"\n"+urlString, null);
			Intent intent = new Intent();
			intent.putExtra("commodityid", commodityid);
			intent.putExtra("title", titleString);
			intent.putExtra("commodityImageString", commodityImageString);
			intent.putExtra("commodityInfoString", commodityInfoString);
			intent.putExtra("button1", -1);
			intent.putExtra("button2", -1);
			intent.putExtra("num", "");
			intent.putExtra("em", "");
			intent.putExtra("addr", "");
			intent.putExtra("phone", "");
			intent.putExtra("name", "");
			intent.putExtra("msg", "");
			intent.setClass(WebviewActivity.this, SajiaoActivity.class);
			startActivity(intent);
			OfflineLog.writeSajiao(commodityid);
			LogPrint.Print("=================sajiao");
		}
	};
	
//	String mailTitle = "亲,帮我买个单呗"+new Date();
//	String mailMessage = "亲,我在商城上看上一件好东东,帮我买个单呗:)\n\n";
//	public void shareEmail(String title,String message,String imgDir){
//		Intent emailIntent = new Intent(Intent.ACTION_SEND);
//		emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
//		emailIntent.putExtra(Intent.EXTRA_TEXT, message);
//		if(imgDir != null){
//			emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imgDir));
//		}
//		emailIntent.setType("application/octet-stream");
//		startActivity(emailIntent);
//	}
	
//	private SystemProgress progress;
	public void addProgress(){
		try {
//			if(progress != null)return;
//			progress = new SystemProgress(this, this, null){
//				public void cancelData(){
//					if(webview != null){
//						webview.stopLoading();
//					}
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
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				final int threadIdx = msg.arg1;
				if (threadIdx == 4 || threadIdx == 5) {
					JsonLike((byte[]) msg.obj, threadIdx);
				} else {
					Json((byte[]) msg.obj);
				}
				closeProgress();
				break;
			case MessageID.MESSAGE_MAINMENU_OPEN:
				if(fwebview != null){
					if(fwebview.getRight() > getMainMenuAnimationPos(50)){
						fwebview.offsetLeftAndRight(animationIndex);
						fwebview.postInvalidate();
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_OPEN);
						animationIndex = -(CommonUtil.screen_width-getMainMenuAnimationPos(50))/5;
					}
				}
				break;
			case MessageID.MESSAGE_MAINMENU_CLOSE:
				if(fwebview != null){
					if(fwebview.getLeft() < 0){
						fwebview.offsetLeftAndRight(animationIndex);
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_CLOSE);
						fwebview.postInvalidate();
						animationIndex = (CommonUtil.screen_width-getMainMenuAnimationPos(50))/5;
					}
				}
				break;
			case MessageID.MESSAGE_CLOSEPROGRESS:
				closeProgress();
				break;
			case MessageID.MESSAGE_CONNECT_START:
				addProgress();
				break;
			case 100008://删除喜欢
				likeState = false;
				webview_likebtn.setBackgroundResource(R.drawable.webview_heart);
				webview_likenumbtn.setText(msg.getData().getString("likenum"));
				break;
			case 100009://添加喜欢
				boolean mayAddLike = msg.getData().getBoolean("likeNumLimit");
				//喜欢可以添加喜欢
				if(!mayAddLike){					
					new LikeLimitDialog(WebviewActivity.this);
				}else{
					likeState = true;
					webview_likebtn.setBackgroundResource(R.drawable.webview_heart_f);
					webview_likenumbtn.setText(msg.getData().getString("likenum"));
				}					
				break;			
			}
			
		}
		
	};
	
	private void Json(byte[] data){
		try {
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = "+str);
			if(str.length() > 0){
				if(str.indexOf("true") > 0){
					CommonUtil.saveAddrManager(this, str);
					CommonUtil.saveCurAddr(this, 0);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private void JsonLike(byte[] data, int threadIdx){
		try {
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = "+str);
			JSONObject jObject = new JSONObject(str);
			if(str.length() ==0 || str.indexOf("true") > 0){			
				String likeNum = jObject.getString("likenum");
				Bundle bundle = new Bundle();
				bundle.putString("likenum", likeNum);
				Message message = new Message();
				if(threadIdx == 4){//删除
					message.setData(bundle);
					message.what = 100008;
					mHandler.sendMessage(message);
//					mHandler.sendEmptyMessage(100008);
				}else if(threadIdx == 5){//添加
					bundle.putBoolean("likeNumLimit", jObject.getBoolean("likeNumLimit"));
					message.setData(bundle);
					message.what = 100009;
					mHandler.sendMessage(message);
				}
			}else{
				CommonUtil.ShowToast(this, "失败了!");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		gotoMenu();
		return true;
	}
	
	private void gotoMenu(){
		if(fwebview != null){
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
	    		fwebview.startAnimation(animation);
	    		OfflineLog.writeMainMenu();//写入离线日志
			}else{
				if(mainMenu != null){
					mainMenu.setIsCanClick(false);
				}
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.left_in);
				animation.setAnimationListener(this);
	    		animation.setFillEnabled(true);
	    		animation.setFillAfter(true);
	    		fwebview.startAnimation(animation);
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
		if(isAnimationOpen){
			titlebar_backbutton.setVisibility(View.INVISIBLE);			
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
			titlebar_backbutton.setVisibility(View.VISIBLE);			
			titlebar_leftmenu.setVisibility(View.INVISIBLE);
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

	
}
