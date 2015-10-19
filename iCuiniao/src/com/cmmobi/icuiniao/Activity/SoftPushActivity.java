/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.ui.view.MainMenu;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.MyWebViewClient;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.SystemProgress;
import com.cmmobi.icuiniao.util.URLUtil;

/**
 * @author hw
 *软件推荐
 */
public class SoftPushActivity extends Activity implements AnimationListener{

	private Button titlebar_menubutton;
	private WebView webview;
	private boolean isAnimationOpen;//主菜单动画状态
	private int animationIndex;
	private MainMenu mainMenu;
	private LinearLayout softpushpage;
	//左侧的隐藏菜单按钮，用于动画播放后的控制
	private Button titlebar_leftmenu = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.softpush);
        isAnimationOpen = false;
        softpushpage = (LinearLayout)findViewById(R.id.softpushpage);
        webview = (WebView)findViewById(R.id.webview);
        mainMenu = (MainMenu)findViewById(R.id.mainmenu);
        titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
        titlebar_leftmenu = (Button)findViewById(R.id.titlebar_leftmenu);
        titlebar_menubutton.setOnClickListener(menubtnClickListener);
        titlebar_leftmenu.setOnClickListener(menubtnClickListener);
        
        webview.requestFocus();
        webview.setDownloadListener(downloadListener);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(mWebViewClient);
        webview.setVerticalScrollBarEnabled(false);
        //部分机型乱码问题
        webview.getSettings().setDefaultTextEncodingName("utf-8");

        webview.loadUrl(URLUtil.URL_SOFTPUSH+"?fid="+URLUtil.fid);  
        webview.setWebViewClient(new MyWebViewClient(this, mHandler));
	}
	
	private DownloadListener downloadListener = new DownloadListener() {
		
		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			// TODO Auto-generated method stub
			Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                     startActivity(intent);
		}
	};
	
	private OnClickListener menubtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			gotoMenu();
		}
	};

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
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageID.MESSAGE_MAINMENU_OPEN:
				if(softpushpage != null){
					if(softpushpage.getRight() > getMainMenuAnimationPos(50)){
						softpushpage.offsetLeftAndRight(animationIndex);
						softpushpage.postInvalidate();
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_OPEN);
						animationIndex = -(CommonUtil.screen_width-getMainMenuAnimationPos(50))/5;
					}
				}
				break;
			case MessageID.MESSAGE_MAINMENU_CLOSE:
				if(softpushpage != null){
					if(softpushpage.getLeft() < 0){
						softpushpage.offsetLeftAndRight(animationIndex);
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_CLOSE);
						softpushpage.postInvalidate();
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
			}
		}
		
	};
	
	private SystemProgress progress;
	public void addProgress(){
		try {
			if(progress != null)return;
			progress = new SystemProgress(this, this, null){
				public void cancelData(){
					if(webview != null){
						webview.stopLoading();
					}
				}
			};
			
		} catch (Exception e) {
		}
	}
	
	public void closeProgress(){
		if(progress != null){
			progress.close();
			progress = null;
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		gotoMenu();
		return true;
	}
	
	private void gotoMenu(){
//		if(softpushpage != null){
//			animationIndex = 0;
//			isAnimationOpen = !isAnimationOpen;
//			if(isAnimationOpen){
//				if(mainMenu != null){
//					mainMenu.setIsCanClick(true);
//				}
//				mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_OPEN);
//				OfflineLog.writeMainMenu();//写入离线日志
//			}else{
//				if(mainMenu != null){
//					mainMenu.setIsCanClick(false);
//				}
//				mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_CLOSE);
//			}
//		}
		if(softpushpage != null){
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
	    		softpushpage.startAnimation(animation);
	    		OfflineLog.writeMainMenu();//写入离线日志
			}else{
				if(mainMenu != null){
					mainMenu.setIsCanClick(false);
				}
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.left_in);
				animation.setAnimationListener(this);
	    		animation.setFillEnabled(true);
	    		animation.setFillAfter(true);
	    		softpushpage.startAnimation(animation);
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
}
