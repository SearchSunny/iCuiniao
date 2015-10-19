/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import java.io.ByteArrayOutputStream;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.ui.view.ShareList;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.WXAppID;
import com.cmmobi.icuiniao.util.connction.HttpThread;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

/**
 * @author hw
 *
 *分享
 */
public class ShareActivity extends Activity{

	private String wapUrl;
	private String titleString;
	private String commodityImageString;
	private String commodityInfoString;
	public static int commodityid;
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;
	// IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    private byte[] wxByte;
    //微信版本号
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share);
        wapUrl = getIntent().getExtras().getString("url");
        if(wapUrl != null){
        	//2012.8.29修改为直接加数值,用于短链接
        	if(wapUrl.indexOf("?") >= 0){
//        		wapUrl += "&type=0";
        		wapUrl += "0";
        	}else{
//        		wapUrl += "?type=0";
        		wapUrl += "0";
        	}
        }
        titleString = getIntent().getExtras().getString("title");
        commodityImageString = getIntent().getExtras().getString("image");
        commodityInfoString = getIntent().getExtras().getString("commodityInfoString");
        commodityid = getIntent().getExtras().getInt("commodityid");
        ShareList shareList = (ShareList)findViewById(R.id.share_list);
        shareList.setOnItemClickListener(shareItemClickListener);
        //lyb
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		OfflineLog.writeShareMenuMyPage(commodityid);
	}
	
	//按键监听
	public OnItemClickListener shareItemClickListener = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			switch (arg2) {
			case 0://微博
//				LogPrint.Print("shareItem = weibo");
//				Intent intent = new Intent();
//				intent.putExtra("url", wapUrl);
//		  		intent.putExtra("title", titleString);
//		  		intent.putExtra("image", commodityImageString);
//		  		intent.setClass(ShareActivity.this, AuthorizeActivity.class);
//				startActivity(intent);
//				LogPrint.Print("shareItem = weibo");
//				Intent intent = new Intent();
//				LogPrint.Print("webview","url = "+addUrlParam(URLUtil.URL_SHARE,2));
//				intent.putExtra("url", addUrlParam(URLUtil.URL_SHARE,2));
//		  		intent.setClass(ShareActivity.this, WebViewLoginActivity.class);
//				startActivity(intent);
//				finish();
				
				LogPrint.Print("shareItem = weibo");
				new ConnectUtil(ShareActivity.this, mHandler, 0).connect(URLUtil.URL_GET_SHORTLINK+"?type=3"+"&oid="+UserUtil.userid+"&cid="+commodityid, HttpThread.TYPE_PAGE, 2);
				break;
			case 3://腾讯空间
//				LogPrint.Print("shareItem = qq");
//				Intent qqIntent = new Intent(ShareActivity.this, TAuthorzeActivity.class);
//				qqIntent.putExtra("title", "我给的恰好是您的所爱！");
//				qqIntent.putExtra("url", URLUtil.URL_APPDOWNLOAD+"qzone");
//				qqIntent.putExtra("comment", titleString+wapUrl);
//				qqIntent.putExtra("summary", commodityInfoString);
//				qqIntent.putExtra("images", commodityImageString);
//				qqIntent.putExtra("playurl", "");
//				startActivity(qqIntent);
//				LogPrint.Print("shareItem = qq");
//				Intent qqIntent = new Intent(ShareActivity.this, TAuthorzeActivity.class);
//				qqIntent.putExtra("title", "在翠鸟寻找属于自己的惊喜");
//				qqIntent.putExtra("url", wapUrl);
//				qqIntent.putExtra("comment", "更多精彩,尽在翠鸟");
//				qqIntent.putExtra("summary", titleString);
//				qqIntent.putExtra("images", commodityImageString);
//				qqIntent.putExtra("playurl", "");
//				startActivity(qqIntent);
//				LogPrint.Print("shareItem = qq");
//				Intent qqIntent = new Intent(ShareActivity.this, WebViewLoginActivity.class);
//				LogPrint.Print("webview","url = "+addUrlParam(URLUtil.URL_SHARE,1));
//				qqIntent.putExtra("url", addUrlParam(URLUtil.URL_SHARE,1));
//				startActivity(qqIntent);
//				finish();
				LogPrint.Print("shareItem = qq");
				Intent qqIntent = new Intent();
				qqIntent.putExtra("title", titleString);
				qqIntent.putExtra("image", commodityImageString);
				qqIntent.putExtra("commodityInfoString", commodityInfoString);
				qqIntent.putExtra("commodityid", commodityid);
				qqIntent.putExtra("logintype", 1);
				qqIntent.setClass(ShareActivity.this, ShareEditActivity.class);
				startActivity(qqIntent);
				finish();
				OfflineLog.writeShareQQ(commodityid);
				break;
			case 2://微信
				LogPrint.Print("shareItem = wx");
				if(CommonUtil.isNetWorkOpen(ShareActivity.this)){
				// 通过WXAPIFactory工厂，获取IWXAPI的实例
				api = WXAPIFactory.createWXAPI(ShareActivity.this, WXAppID.APP_ID, false);
				//注册微信
				api.registerApp(WXAppID.APP_ID);
		    	//检查是否安装微信
		    	if(!api.isWXAppInstalled()){
		    		//提示用户安装微信
		    		CommonUtil.ShowToast(ShareActivity.this, "未检测到微信终端");
		    		
		    	}else{
					//判断缓存是否有图片
					if(CommonUtil.exists(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(commodityImageString))){
						//获取图片
						wxByte = CommonUtil.getSDCardFileByteArray(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(commodityImageString));
						wxSend(wxByte);
						finish();
					}else{
						new ConnectUtil(ShareActivity.this, mHandler, 0).connect(commodityImageString, HttpThread.TYPE_IMAGE, 10);
						finish();
					}
					
		    	}
				}else{
					
					CommonUtil.ShowToast(ShareActivity.this, "杯具了- -!\n联网不给力啊");
				}
		    	
		    	break;
			case 4://短信
				LogPrint.Print("shareItem = sms");
				new ConnectUtil(ShareActivity.this, mHandler, 0).connect(URLUtil.URL_GET_SHORTLINK+"?type=6"+"&oid="+UserUtil.userid+"&cid="+commodityid, HttpThread.TYPE_PAGE, 0);
				new ConnectUtil(ShareActivity.this, mHandler, 0).connect(URLUtil.URL_SHARE_UPLAOD+"?logintype=12"+"&oid="+UserUtil.userid+"&cid="+commodityid, HttpThread.TYPE_PAGE, 0);
				break;
			case 5://邮件
				LogPrint.Print("shareItem = email");
				new ConnectUtil(ShareActivity.this, mHandler, 0).connect(URLUtil.URL_GET_SHORTLINK+"?type=5"+"&oid="+UserUtil.userid+"&cid="+commodityid, HttpThread.TYPE_PAGE, 1);
				new ConnectUtil(ShareActivity.this, mHandler, 0).connect(URLUtil.URL_SHARE_UPLAOD+"?logintype=11"+"&oid="+UserUtil.userid+"&cid="+commodityid, HttpThread.TYPE_PAGE, 0);
				break;
			case 1: //分享朋友圈
				LogPrint.Print("shareItem = wxpengyou");
				if (CommonUtil.isNetWorkOpen(ShareActivity.this)) {
					// 通过WXAPIFactory工厂，获取IWXAPI的实例
					api = WXAPIFactory.createWXAPI(ShareActivity.this,
							WXAppID.APP_ID, false);
					// 注册微信
					api.registerApp(WXAppID.APP_ID);
					// 检查是否安装微信
					if (!api.isWXAppInstalled()) {
						// 提示用户安装微信
						CommonUtil.ShowToast(ShareActivity.this, "未检测到微信终端");

					} else {
						// 判斷是否支持分享朋友圈
						if (isWeiXinFreind()) {
							// 判断缓存是否有图片
							if (CommonUtil.exists(CommonUtil.dir_cache+ "/"+ CommonUtil.urlToNum(commodityImageString))) {
								// 获取图片
								wxByte = CommonUtil.getSDCardFileByteArray(CommonUtil.dir_cache+ "/"+ CommonUtil.urlToNum(commodityImageString));
								wxSendFreind(wxByte);
								finish();
							} else {
								new ConnectUtil(ShareActivity.this, mHandler, 0).connect(commodityImageString,HttpThread.TYPE_IMAGE, 10);
								finish();
							}
						}
					}
				} else {

					CommonUtil.ShowToast(ShareActivity.this, "杯具了- -!\n联网不给力啊");
				}
				break;
			default:
				break;
			}
		}
	};
	
	public void shareSms(String message){
		Uri smsToUri = Uri.parse("smsto:");
		Intent smsIntent = new Intent(Intent.ACTION_SENDTO,smsToUri);
		smsIntent.putExtra("sms_body", message);
		startActivity(smsIntent);
		OfflineLog.writeShareSms(commodityid);//写入离线日志
	}
	//微博分享
	public void shareWeibo(String urlString){
		Intent intent = new Intent();
		intent.putExtra("urlString", urlString);
		intent.putExtra("title", titleString);
		intent.putExtra("image", commodityImageString);
		intent.putExtra("commodityInfoString", commodityInfoString);
		intent.putExtra("commodityid", commodityid);
		intent.putExtra("logintype", 2);
		intent.setClass(ShareActivity.this, ShareEditActivity.class);
		startActivity(intent);
		finish();
		OfflineLog.writeShareWeibo(commodityid);
		
	}
	
	public void shareEmail(String title,String message,String imgDir){
		try {
			if(CommonUtil.isNetWorkOpen(this)){
				Intent emailIntent = new Intent(Intent.ACTION_SEND);
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
				emailIntent.putExtra(Intent.EXTRA_TEXT, message);
				if(imgDir != null){
					emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imgDir));
				}
				emailIntent.setType("plain/text");
				startActivity(emailIntent);
				OfflineLog.writeShareEmail(commodityid);//写入离线日志
			}else{
				CommonUtil.ShowToast(this, "主人，邮件发送失败了");
			}
		} catch (Exception e) {
			CommonUtil.ShowToast(ShareActivity.this, "主人，邮件罢工了，呼叫失败！");
		}
	}
	
	public String addUrlParam(String url,int logintype){
  		if(URLUtil.IsLocalUrl()){
			return url;
		}
  		return url+"?pid="+commodityid+"&oid="+UserUtil.userid+"&dpi="+URLUtil.dpi()+"&logintype="+logintype+"&plaid="+URLUtil.plaid;
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
	
	private void Json(byte[] data,int threadindex){
		try {
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("share json = "+str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			if(result != null){
				if(result.equalsIgnoreCase("true")){
					String url = jObject.getString("url");
					Bundle bundle = new Bundle();
					bundle.putString("url", url);
					Message message = new Message();
					if(threadindex == 0){//短信
						message.what = 100006;
						message.setData(bundle);
						mHandler.sendMessage(message);
					}else if(threadindex == 1){//邮件
						message.what = 100005;
						message.setData(bundle);
						mHandler.sendMessage(message);
					}else{
						message.what = 100008;
						message.setData(bundle);
						mHandler.sendMessage(message);
					}
				}else{
					mHandler.sendEmptyMessage(100007);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
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
				if("text/json".equals(msg.getData().getString("content_type"))){
					Json((byte[])msg.obj,msg.arg1);
				}else{
					wxSend((byte[])msg.obj);
				}
				break;
			case 100005://邮件分享
				shareEmail("翠鸟分享:"+titleString,commodityInfoString+"\n\n"+msg.getData().getString("url")+"\n\n(分享自 翠鸟客户端)\n\n"+"【翠鸟下载地址】"+URLUtil.URL_APPDOWNLOAD+"mail", null);
				finish();
				break;
			case 100006://短信分享
				shareSms("翠鸟分享:"+titleString+"\n\n去看看 "+msg.getData().getString("url")+"\n\n(分享自 翠鸟客户端)\n\n"+"【翠鸟下载地址】"+URLUtil.URL_APPDOWNLOAD+"love");
				finish();
			case 100008: //微博分享
				shareWeibo(msg.getData().getString("url"));
				break;
			case 100007:
				CommonUtil.ShowToast(ShareActivity.this, "失败了!");
				break;
			default:
				break;
			}
		}
		
	};
	
	/**
	 * 微信分享 发送消息
	 * @param wxData
	 */
	public void wxSend(byte[] wxData){
		
		Bitmap temp = BitmapFactory.decodeByteArray(wxData, 0, wxData.length);
		//设置分享图片的大小,不能超过32KB
		temp = CommonUtil.resizeImage(temp, 110, 110);
		WXWebpageObject webpage = new WXWebpageObject();
		//第三方展示页面
		webpage.webpageUrl = wapUrl;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		//微信分享标题
		msg.title =getString(R.string.wx_share_title);
		//商品描述
		msg.description = commodityInfoString;
		msg.thumbData =bmpToByteArray(temp,true);
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		//req.scene = req.WXSceneTimeline;
		api.sendReq(req);
		OfflineLog.writeShareWX(commodityid);
	}
	
	/**
	 * 微信朋友圈分享 发送消息
	 * @param wxData
	 */
	public void wxSendFreind(byte[] wxData){
		
		Bitmap temp = BitmapFactory.decodeByteArray(wxData, 0, wxData.length);
		//设置分享图片的大小,不能超过32KB
		temp = CommonUtil.resizeImage(temp, 110, 110);
		WXWebpageObject webpage = new WXWebpageObject();
		//第三方展示页面
		webpage.webpageUrl = wapUrl;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		//微信分享标题
		msg.title =getString(R.string.wx_share_title);
		//商品描述
		msg.description = commodityInfoString;
		msg.thumbData =bmpToByteArray(temp,true);
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(req);
		OfflineLog.writeShareWX(commodityid);
	}
	/**
	 * 
	 * @param bmp 位图对象
	 * @param needRecycle 是否回收
	 * @return
	 */
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	//用于标识一个唯一请求
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	/**
	 * 检测是否支持微信朋友圈
	 * @return
	 */
	public boolean isWeiXinFreind(){
		//当前微信版本号
		int wxSdkVersion = api.getWXAppSupportAPI();
		if (wxSdkVersion >= TIMELINE_SUPPORTED_VERSION) {
			return true;
		} else {
			CommonUtil.ShowToast(ShareActivity.this, "此版本不支持!");
			return false;
		}
	}

}
