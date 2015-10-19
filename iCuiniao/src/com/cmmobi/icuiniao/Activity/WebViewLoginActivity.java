/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.R.color;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.MyWebViewClient;
import com.cmmobi.icuiniao.util.SystemProgress;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.MyWebViewClient;

/**
 * @author hw
 *新的登陆,注册,第三方逻辑等
 */
public class WebViewLoginActivity extends Activity{

	private WebView webview;
	private String urlString;
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webviewlogin);
        urlString = getIntent().getExtras().getString("url");
        int logintype = getIntent().getExtras().getInt("logintype", -1);
        webview = (WebView)findViewById(R.id.webview);
        if(logintype >= 0){
        	webview.setBackgroundColor(0x00000000); // 设置webview背景透明
        }
               
        //lyb
		loadingBar = (ProgressBar)findViewById(R.id.loading);
        
        webview.requestFocus();
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new MyWebViewClient(this, mHandler));
        webview.setVerticalScrollBarEnabled(false);
        
        //增加js回调逻辑
        JSDate jsdate = new JSDate(this);
        webview.addJavascriptInterface(jsdate, "icuiniao");
        
        LogPrint.Print("=============urlString:" + urlString);
        webview.loadUrl(urlString);
        LogPrint.Print("-------------urlString:" + urlString);
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
			case MessageID.MESSAGE_CLOSEPROGRESS:
				closeProgress();
				break;
			case MessageID.MESSAGE_CONNECT_START:
				addProgress();
				break;
			}
		}
	};
	
	public void addProgress(){
		try {
			if(loadingBar != null){
				loadingBar.setVisibility(View.VISIBLE);
			}
			
		} catch (Exception e) {
		}
	}
	
	public void closeProgress(){
		if(loadingBar != null){
			loadingBar.setVisibility(View.INVISIBLE);
		}
	}
	
}
