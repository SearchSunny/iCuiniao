package com.cmmobi.icuiniao.onlineEngine.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.Activity;
import com.cmmobi.icuiniao.Activity.WebViewLoginActivity;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.ShareUtils;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.WXAppID;
import com.cmmobi.icuiniao.weibo.WeiboLoginActivity;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class ShareInviteAct extends Activity {
	
	private TextView titlebar_titletext;
	private Button titlebar_backbutton;
	private ProgressBar loadingBar;
	
	private int shareIndex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shareinvite);
		if (UserUtil.userid == -1 || UserUtil.userState != 1) {
			finish();
			return;
		}		
		shareIndex = getIntent().getIntExtra("shareIndex", 0);
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		String titleName = getIntent().getStringExtra("titleName");
		initXmlView(titleName);
	}
	
	private void initXmlView(String titleName){
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		titlebar_backbutton.setOnClickListener(backOnclickListener);
		titlebar_titletext = (TextView)findViewById(R.id.titlebar_titletext);
		titlebar_titletext.setText(titleName);
		//分享按钮
		Button sharebtn = (Button)findViewById(R.id.sharebtn);
		sharebtn.setOnClickListener(sharebtnListener);
		
	}
	
	private View.OnClickListener backOnclickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			ShareInviteAct.this.finish();
			
		}
	};
	
	private Handler mHandler = new Handler() {

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
				CommonUtil.ShowToast(getApplicationContext(), (String) msg.obj);
				break;
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				closeProgress();
				break;
			}
		}
	};
	
	private View.OnClickListener sharebtnListener = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			switch(shareIndex){
			case 0: //新浪
				Intent qqIntent = new Intent(ShareInviteAct.this, WeiboLoginActivity.class);
				/*LogPrint.Print("webview","url = "+addUrlParam(URLUtil.URL_SHARE,2));
				qqIntent.putExtra("url", addUrlParam(URLUtil.URL_SHARE,2));*/
				String title = getString(R.string.share_invite);
				qqIntent.putExtra("title",title);
				qqIntent.putExtra("urlString", URLUtil.URL_APPDOWNLOAD);
				qqIntent.putExtra("actionType", "firend");
				startActivity(qqIntent);
				finish();
				break;
			case 3: //qq空间
				qqIntent = new Intent(ShareInviteAct.this, WebViewLoginActivity.class);
				LogPrint.Print("webview","url = "+addUrlParam(URLUtil.URL_SHARE,1));
				qqIntent.putExtra("url", addUrlParam(URLUtil.URL_SHARE,1));
				startActivity(qqIntent);
				finish();
				break;
			case 2:  //微信
				shareWeixin(getString(R.string.share_invite) + " \n\n (分享自 翠鸟客户端)", URLUtil.URL_APPDOWNLOAD + "wechat");
				break;
			case 4:  //sms
				shareSms(getString(R.string.share_invite) + " \n\n (分享自 翠鸟客户端)\n\n"+"【翠鸟下载地址】"+URLUtil.URL_APPDOWNLOAD + "love");
				break;	
			case 1: //微信朋友圈
				shareWeixinFreind(getString(R.string.share_invite) + " \n\n (分享自 翠鸟客户端)", URLUtil.URL_APPDOWNLOAD + "wechat");
				break;
			}
			
		}
		
	};
	
	public String addUrlParam(String url,int logintype){
  		if(URLUtil.IsLocalUrl()){
			return url;
		}
  		return url + "?type=2" + "&oid="+UserUtil.userid+"&dpi="+URLUtil.dpi()+"&logintype="+logintype+"&plaid="+URLUtil.plaid+"&ver="+URLUtil.version+"&title="+CommonUtil.toUrlEncode(getString(R.string.share_invite));
  	}
	
	private void shareWeixin(String description, String wapUrl){		
		ShareUtils shareUtils = new ShareUtils();
		shareUtils.setDescription(description);
		if(CommonUtil.isNetWorkOpen(ShareInviteAct.this)){
			// 通过WXAPIFactory工厂，获取IWXAPI的实例
			IWXAPI api = WXAPIFactory.createWXAPI(ShareInviteAct.this, WXAppID.APP_ID, false);
			//注册微信
			api.registerApp(WXAppID.APP_ID);
	    	//检查是否安装微信
	    	if(!api.isWXAppInstalled()){
	    		//提示用户安装微信
	    		CommonUtil.ShowToast(ShareInviteAct.this, "未检测到微信终端");
	    		
	    	}else{
	    		Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.iconshare);
	    		shareUtils.wxSend(temp, this, api, wapUrl);
				
	    	}
			}else{
				
				CommonUtil.ShowToast(ShareInviteAct.this, "杯具了- -!\n联网不给力啊");
			}
	}
	//分享微信朋友圈
	private void shareWeixinFreind(String description, String wapUrl){		
		ShareUtils shareUtils = new ShareUtils();
		shareUtils.setDescription(description);
		if(CommonUtil.isNetWorkOpen(ShareInviteAct.this)){
			// 通过WXAPIFactory工厂，获取IWXAPI的实例
			IWXAPI api = WXAPIFactory.createWXAPI(ShareInviteAct.this, WXAppID.APP_ID, false);
			//注册微信
			api.registerApp(WXAppID.APP_ID);
	    	//检查是否安装微信
	    	if(!api.isWXAppInstalled()){
	    		//提示用户安装微信
	    		CommonUtil.ShowToast(ShareInviteAct.this, "未检测到微信终端");
	    		
	    	}else{
	    		if(shareUtils.isWeiXinFreind(this, api)){
	    			
	    			Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.iconshare);
		    		shareUtils.wxFreindSend(temp, this, api, wapUrl);
	    		}
	    	}
			}else{
				
				CommonUtil.ShowToast(ShareInviteAct.this, "杯具了- -!\n联网不给力啊");
			}
	}
	
	private void shareSms(String message){
		Uri smsToUri = Uri.parse("smsto:");
		Intent smsIntent = new Intent(Intent.ACTION_SENDTO,smsToUri);
		smsIntent.putExtra("sms_body", message);
		startActivity(smsIntent);		
	}
	
	public void addProgress() {
		if (loadingBar != null) {
			loadingBar.setVisibility(View.VISIBLE);
		}

	}

	public void closeProgress() {
		if (loadingBar != null) {
			loadingBar.setVisibility(View.INVISIBLE);
		}
	}

}
