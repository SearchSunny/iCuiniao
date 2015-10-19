package com.cmmobi.icuiniao.util;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 统一的webview错误处理页面
 * @author lyb
 *
 */
public class MyWebViewClient extends WebViewClient{  
	String tag = "webClient";
	String errorHtml = "";
	private Handler mHandler;
//	String errorHtml = "<html><body><h1>Page not find！</h1></body></html>";
//	String errorHtml = "<HTML>在模拟器 2.1 上测试,这是<IMG src=\"APK'>file:///android_asset/err.png\"/>APK里的图片";

	
	public MyWebViewClient(Context context, Handler handler){
		AssetManager am = null;   
		am = context.getAssets();   
		try {
			InputStream is = am.open("err.html");
			int len = is.available();
			byte[] data = new byte[len];
			is.read(data);
			errorHtml = new String(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		mHandler = handler;
	}
	
//	@Override
//    public boolean shouldOverrideUrlLoading(WebView view, String url) {  
//         LogPrint.Print(tag, "-MyWebViewClient->shouldOverrideUrlLoading()--");  
//         view.loadUrl(url);  
//        return true;  
//    }  
       
	@Override 
    public void onPageStarted(WebView view, String url, Bitmap favicon) {  
		LogPrint.Print(tag, "-MyWebViewClient->onPageStarted()--");  
        super.onPageStarted(view, url, favicon);          
        mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_START);
    }  
       
    @Override 
    public void onPageFinished(WebView view, String url) {  
    	LogPrint.Print(tag, "-MyWebViewClient->onPageFinished()--");  
        super.onPageFinished(view, url);         
        mHandler.sendEmptyMessage(MessageID.MESSAGE_CLOSEPROGRESS);
    }  
       
       
    @Override 
    public void onReceivedError(WebView view, int errorCode,  
            String description, String failingUrl) {  
        super.onReceivedError(view, errorCode, description, failingUrl);  
           
        LogPrint.Print(tag, "-MyWebViewClient->onReceivedError()--\n errorCode="+errorCode+" \ndescription="+description+" \nfailingUrl="+failingUrl);  
             //这里进行无网络或错误处理，具体可以根据errorCode的值进行判断，做跟详细的处理。  
//              view.loadData(errorHtml, "", "UTF-8"); 
        mHandler.sendEmptyMessage(MessageID.MESSAGE_CLOSEPROGRESS);
        view.loadDataWithBaseURL(null, errorHtml, "text/html", "utf-8", null);           
          
    } 
    
    @Override
	public void onLoadResource(WebView view, String url) {
		// TODO Auto-generated method stub
		super.onLoadResource(view, url);
		LogPrint.Print("=============page loadResource:"+url);
	}
}  
