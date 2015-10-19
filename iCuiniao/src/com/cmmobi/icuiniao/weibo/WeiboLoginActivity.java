package com.cmmobi.icuiniao.weibo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.R.bool;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.Activity;
import com.cmmobi.icuiniao.Activity.ShareActivity;
import com.cmmobi.icuiniao.onlineEngine.activity.MainPageActivityA;
import com.cmmobi.icuiniao.onlineEngine.activity.MyPageActivityA;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.TimerForRock;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;
import com.icuiniao.plug.im.IMService;
import com.sina.weibo.sdk.WeiboSDK;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.sina.weibo.sdk.api.WeiboApiImpl;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;

/**
 * 新浪微博授权
 * @author XP
 *
 */
public class WeiboLoginActivity extends Activity {
	private Weibo mWeibo;
	// 新浪微博分享的开放接口
	private IWeiboAPI weiboAPI;
	private AccountAPI accontApi;
	private StatusesAPI statusesAPI;
	public static Oauth2AccessToken accessToken;
	/**
     * SsoHandler 仅当sdk支持sso时有效，
     */
    private SsoHandler mSsoHandler;
    //用户UID
    String uid;
  //令牌
	String token;
	//昵称
	String screen_name;
	//有效期
    long date;
    //类型(分享、登录、绑定)
    String actionType;
    //商品ID
    int commodityid;
    //商品标题
    String title;
    //商品描述
    String info;
    //商品图片地址
    String image;
    String urlString;
    
    private ProgressBar loadingBar;
    
    private final static String MODE = MyPageActivityA.BUTTONMODE;
	private final static String HAND = MyPageActivityA.RIGHTHAND; 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weibologin);
		//透明
		setTheme(android.R.style.Theme_Translucent);
		
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		
		actionType = getIntent().getExtras().getString("actionType");
		if(getIntent().getExtras().getInt("pid") > 0){
			
			commodityid = getIntent().getExtras().getInt("pid");
		}
		if(getIntent().getExtras().getString("title") != null){
			
			 title = getIntent().getExtras().getString("title");
		}
		if(getIntent().getExtras().getString("info") != null){
			
			info = getIntent().getExtras().getString("info");
		}
		if(getIntent().getExtras().getString("image") != null){
			
			image =	getIntent().getExtras().getString("image");
		}
		if(getIntent().getExtras().getString("urlString") != null){
			
			urlString = getIntent().getExtras().getString("urlString");
		}
		
		//获取上次登录的token
		accessToken = AccessTokenKeeper.readAccessToken(this);
		
		if (accessToken.isSessionValid()) {
            String date = new java.text.SimpleDateFormat("yyyy/MM/dd hh:mm:ss")
                    .format(new java.util.Date(accessToken
                            .getExpiresTime()));
            LogPrint.Print("access_token 仍在有效期内,无需再次登录: \naccess_token:"
                  + accessToken.getToken() + "\n有效期：" + date);  
        } else {
        	LogPrint.Print("使用SSO登录前，请检查手机上是否已经安装新浪微博客户端，" +
            		"目前仅3.0.0及以上微博客户端版本支持SSO；如果未安装，将自动转为Oauth2.0进行认证");
        }
	
		// 初始化SDK
		weiboAPI = new WeiboApiImpl(this,ConstantS.APP_KEY,false); 
		//WeiboSDK.createWeiboAPI(this, ConstantS.APP_KEY);
		mWeibo = Weibo.getInstance(ConstantS.APP_KEY, ConstantS.REDIRECT_URL);
		// 检查用户是否安装了微博客户端程序
		//if (weiboAPI.isWeiboAppInstalled()) {
			// 获取版本号
			int version = weiboAPI.getWeiboAppSupportAPI();
			LogPrint.Print("listview","version = "+version);
//			if (version >= 3) {
				// 目前仅3.0.0及以上微博客户端版本支持SSO,如果未安装,将自动转为浏览器页面Oauth2.0进行认证
				mSsoHandler = new SsoHandler(WeiboLoginActivity.this, mWeibo,mHandler);
				mSsoHandler.authorize(new AuthDialogListener(WeiboLoginActivity.this),mHandler);
//			} else {
//
//				mWeibo.authorize(WeiboLoginActivity.this,new AuthDialogListener(WeiboLoginActivity.this));
//			}

		//}
		/*// 提示用户下载新浪微博
		else {

			//WeiboSDK.createWeiboAPI(this, ConstantS.APP_KEY,true);
			weiboAPI.registerWeiboDownloadListener(new IWeiboDownloadListener() {
	            @Override
	            public void onCancel() {
	                Toast.makeText(WeiboLoginActivity.this, "取消下载",Toast.LENGTH_SHORT).show();
	            }
	        });
		}*/
	}
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}*/
	@Override
	public void finish() {
		super.finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// sso 授权回调
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
	}

	class AuthDialogListener  implements WeiboAuthListener{
		

		private Context mcontext;
		
		public AuthDialogListener(Context context) {
			mcontext = context;
		}
		/**
		 * Oauth2.0认证过程中，如果认证窗口被关闭或认证取消时调用
		 */
		@Override
		public void onCancel() {
			
			LogPrint.Print("Auth cancel");
			finish();
			//Toast.makeText(mcontext, "Auth cancel",Toast.LENGTH_LONG).show();
		}

		/**
		 *  认证结束后将调用此方法
		 */
		@Override
		public void onComplete(Bundle values) {
			String code = values.getString("code");
        	if(code != null){
	        	LogPrint.Print("取得认证code: \r\n Code: " + code);
	        	//Toast.makeText(MainActivity.this, "认证code成功", Toast.LENGTH_SHORT).show();
	        	//return;
        	}
			//令牌
			token = values.getString("access_token");
			
	        String expires_in = values.getString("expires_in");
	        //有效期
	        date = Long.parseLong(expires_in);
	        accessToken = new Oauth2AccessToken(token, expires_in);
	        if (accessToken.isSessionValid()) {
	            AccessTokenKeeper.keepAccessToken(mcontext,accessToken);
	            accontApi = new AccountAPI(accessToken);
	            statusesAPI = new StatusesAPI(accessToken);
	            //获取用户UID
	            accontApi.getUid(new RequestListener() {
					
					@Override
					public void onIOException(IOException arg0) {
						
					}
					
					@Override
					public void onError(WeiboException exception) {
						
						LogPrint.Print("新浪微博获取用户ID"+exception.getMessage());
					}
					
					@Override
					public void onComplete4binary(ByteArrayOutputStream arg0) {
						
					}
					
					@Override
					public void onComplete(String userId) {
						try {
							JSONObject jObject = new JSONObject(userId);
							String result = jObject.getString("uid");
							uid = result;
							//查询用户基本信息
				            accontApi.getUserShow(uid, new RequestListener() {
								
								@Override
								public void onIOException(IOException arg0) {
									
								}
								
								@Override
								public void onError(WeiboException arg0) {
									
								}
								
								@Override
								public void onComplete4binary(ByteArrayOutputStream arg0) {
									
								}
								
								@Override
								public void onComplete(String arg0) {
									
									try {
										String userShowString = arg0;
										LogPrint.Print(userShowString);
										JSONObject jObject = new JSONObject(userShowString);
										//昵称
										screen_name = jObject.getString("screen_name");
										//性别
										String gender = jObject.getString("gender");
										//大头像地址
										String avatar_large = jObject.getString("avatar_large");
										//小头像地址
										String profile_image_url = jObject.getString("profile_image_url");
										if(actionType.equals("login")){
											
											addUrlLoginParam(URLUtil.URL_SINA, token,2,uid, date, screen_name, gender, profile_image_url, avatar_large, "login", String.valueOf(UserUtil.userid), "2", "");
										
										}else if(actionType.equals("share")){
											
											/*//跳转到分享
											Intent intent = new Intent();
											intent.setClass(WeiboLoginActivity.this, RequestMessageActivity.class);
											//商品ID
											intent.putExtra("pid", commodityid);
											//标题
											intent.putExtra("title", title);
											//描述
											intent.putExtra("info", info);
											//图片地址
											intent.putExtra("image", image);
											startActivity(intent);*/
											statusesAPI.upload(info+urlString+"【翠鸟客户端下载地址】"+URLUtil.URL_APPDOWNLOAD,image,"","","", new RequestListener() {
										
												@Override
												public void onIOException(IOException arg0) {
													
													LogPrint.Print("IOException==="+arg0.getMessage());
													
												}
												
												@Override
												public void onError(WeiboException arg0) {
													
													LogPrint.Print("WeiboException==="+arg0.getMessage());
													mHandler.sendEmptyMessage(9094);
												}
												
												@Override
												public void onComplete4binary(ByteArrayOutputStream arg0) {
													
												}
												
												@Override
												public void onComplete(String arg0) {
													String reString = arg0;
													LogPrint.Print(reString);
													
													addUrlShareParam(URLUtil.URL_SINA, token,uid, date, screen_name, "share", String.valueOf(1),"2",String.valueOf(commodityid));
													//mHandler.sendEmptyMessage(9093);
												}
											});
											
											
										}else if(actionType.equals("bind")){
											
											addUrlBindParam(URLUtil.URL_SINA, token,2,uid, date, screen_name, gender, profile_image_url, avatar_large, "bind", String.valueOf(UserUtil.userid), "2", "");
											
										}else if(actionType.equals("firend")){
											
											statusesAPI.upload(title+urlString,CommonUtil.dir_friends+"/friends.png","","","", new RequestListener() {
												
												@Override
												public void onIOException(IOException arg0) {
													
													LogPrint.Print("IOException==="+arg0.getMessage());
													
												}
												
												@Override
												public void onError(WeiboException arg0) {
													
													LogPrint.Print("WeiboException==="+arg0.getMessage());
													mHandler.sendEmptyMessage(9094);
													
													finish();
												}
												
												@Override
												public void onComplete4binary(ByteArrayOutputStream arg0) {
													
												}
												
												@Override
												public void onComplete(String arg0) {
													String reString = arg0;
													LogPrint.Print(reString);
													addUrlShareParam(URLUtil.URL_SINA, token,uid, date, screen_name, "share", String.valueOf(2),"2",String.valueOf(commodityid));
													mHandler.sendEmptyMessage(9093);
													finish();
													
												}
											});
										}
										
										
									} catch (Exception e) {
										e.printStackTrace();
										LogPrint.Print("AAAAAAAAAAAAAAA========="+e.getMessage());
									}
									
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
							LogPrint.Print("BBBBBBBBBBBBBBBB========="+e.getMessage());
						}
					
						
					}
				});
	            
	            
	            //finish();
	        }
	        
	        /*mSsoHandler = new SsoHandler(WeiboLoginActivity.this, mWeibo);
			mSsoHandler.authorize(new AuthDialogListener(WeiboLoginActivity.this), null);*/
		}

		
		
		/**
		 * Oauth2.0认证过程中，当认证对话框中的webview接收数据出现错误时调用此方法
		 */
		@Override
		public void onError(WeiboDialogError e) {
			LogPrint.Print("新浪微博认证"+e.getMessage());
		}

		/**
		 *  当认证过程中捕获到WeiboException时调用
		 */
		@Override
		public void onWeiboException(WeiboException e) {
			LogPrint.Print("新浪微博认证异常"+e.getMessage());
		}
		
		
	}
	
	/**
	 * 登录
	 * @param url 后台地址
	 * @param token 令牌 
	 * @param uid 用户ID
	 * @param expireIn 过期时间
	 * @param nickName 用户昵称
	 * @param sax 用户性别
	 * @param headPicSmall 小头像
	 * @param headPicBig 大头像
	 * @param actionType 行业类型
	 * @param shareType 分享类型
	 * @param thirdType 登录类型
	 * @param pid 商品ID
	 * @return
	 */
	public void addUrlLoginParam(final String url,String token,int logintype,String uid,long expireIn,String nickName,String sax,String headPicSmall,String headPicBig,String actionType,String shareType,String thirdType,String pid){

		final List <NameValuePair> params=new ArrayList<NameValuePair>();		
		params.add(new BasicNameValuePair("oid", UserUtil.userid+""));
		params.add(new BasicNameValuePair("uid", CommonUtil.toUrlEncode(uid) ));
		params.add(new BasicNameValuePair("accessToken",CommonUtil.toUrlEncode(token)));
		params.add(new BasicNameValuePair("expireIn",expireIn+""));
		params.add(new BasicNameValuePair("nickName", CommonUtil.toUrlEncode(nickName)));
		params.add(new BasicNameValuePair("sax", CommonUtil.toUrlEncode(sax)));
		params.add(new BasicNameValuePair("headPicSmall",CommonUtil.toUrlEncode(headPicSmall)));
		params.add(new BasicNameValuePair("headPicBig",CommonUtil.toUrlEncode(headPicBig)));
		params.add(new BasicNameValuePair("logintype", logintype+""));
		params.add(new BasicNameValuePair("pc", CommonUtil.toUrlEncode(URLUtil.pc+"") ));
		params.add(new BasicNameValuePair("fid",CommonUtil.toUrlEncode(URLUtil.fid)));
		params.add(new BasicNameValuePair("os_version",CommonUtil.toUrlEncode(CommonUtil.getOs_Version())));
		params.add(new BasicNameValuePair("network_type", CommonUtil.toUrlEncode(CommonUtil.getApnType(this))));
		params.add(new BasicNameValuePair("plaid", CommonUtil.toUrlEncode(URLUtil.plaid)));
		params.add(new BasicNameValuePair("dpi",CommonUtil.toUrlEncode(URLUtil.dpi())));
		params.add(new BasicNameValuePair("imsi",CommonUtil.toUrlEncode(CommonUtil.getIMSI(this))));
		params.add(new BasicNameValuePair("imei",CommonUtil.toUrlEncode(CommonUtil.getIMEI(this))));
		params.add(new BasicNameValuePair("sim",CommonUtil.toUrlEncode(CommonUtil.getSimType(this))));
		params.add(new BasicNameValuePair("reletm",CommonUtil.toUrlEncode(URLUtil.reletm)));
		params.add(new BasicNameValuePair("pla",CommonUtil.toUrlEncode(CommonUtil.getDeviceName())));
		params.add(new BasicNameValuePair("ver",CommonUtil.toUrlEncode(URLUtil.version+"")));
		params.add(new BasicNameValuePair("deviceid",CommonUtil.toUrlEncode(CommonUtil.getIMEI(this))));
		if(!pid.equals("")){
			
			params.add(new BasicNameValuePair("state",actionType+","+shareType+","+thirdType+","+URLUtil.plaid+","+URLUtil.dpi()+","+pid));
		}else{
			
			params.add(new BasicNameValuePair("state",actionType+","+shareType+","+thirdType+","+URLUtil.plaid+","+URLUtil.dpi()));
		}
		new Thread() {
			public void run() {
				try {
					mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_START);
					String result = CommonUtil.httpPost(url,params);
					if (result != null) {
						Message msg = new Message();
						msg.obj = result.getBytes("utf-8");
						msg.what = 9090;
						mHandler.sendMessage(msg);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
  	}
	
	/**
	 * 绑定
	 * @param url
	 * @param token
	 * @param logintype
	 * @param uid
	 * @param expireIn
	 * @param nickName
	 * @param sax
	 * @param headPicSmall
	 * @param headPicBig
	 * @param actionType
	 * @param shareType
	 * @param thirdType
	 * @param pid
	 */
	public void addUrlBindParam(final String url,String token,int logintype,String uid,long expireIn,String nickName,String sax,String headPicSmall,String headPicBig,String actionType,String shareType,String thirdType,String pid){

		final List <NameValuePair> params=new ArrayList<NameValuePair>();		
		params.add(new BasicNameValuePair("oid", UserUtil.userid+""));
		params.add(new BasicNameValuePair("uid", CommonUtil.toUrlEncode(uid) ));
		params.add(new BasicNameValuePair("accessToken",CommonUtil.toUrlEncode(token)));
		params.add(new BasicNameValuePair("expireIn",expireIn+""));
		params.add(new BasicNameValuePair("nickName", CommonUtil.toUrlEncode(nickName)));
		params.add(new BasicNameValuePair("sax", CommonUtil.toUrlEncode(sax)));
		params.add(new BasicNameValuePair("headPicSmall",CommonUtil.toUrlEncode(headPicSmall)));
		params.add(new BasicNameValuePair("headPicBig",CommonUtil.toUrlEncode(headPicBig)));
		params.add(new BasicNameValuePair("logintype", logintype+""));
		params.add(new BasicNameValuePair("pc", CommonUtil.toUrlEncode(URLUtil.pc+"") ));
		params.add(new BasicNameValuePair("fid",CommonUtil.toUrlEncode(URLUtil.fid)));
		params.add(new BasicNameValuePair("os_version",CommonUtil.toUrlEncode(CommonUtil.getOs_Version())));
		params.add(new BasicNameValuePair("network_type", CommonUtil.toUrlEncode(CommonUtil.getApnType(this))));
		params.add(new BasicNameValuePair("plaid", CommonUtil.toUrlEncode(URLUtil.plaid)));
		params.add(new BasicNameValuePair("dpi",CommonUtil.toUrlEncode(URLUtil.dpi())));
		params.add(new BasicNameValuePair("imsi",CommonUtil.toUrlEncode(CommonUtil.getIMSI(this))));
		params.add(new BasicNameValuePair("imei",CommonUtil.toUrlEncode(CommonUtil.getIMEI(this))));
		params.add(new BasicNameValuePair("sim",CommonUtil.toUrlEncode(CommonUtil.getSimType(this))));
		params.add(new BasicNameValuePair("reletm",CommonUtil.toUrlEncode(URLUtil.reletm)));
		params.add(new BasicNameValuePair("pla",CommonUtil.toUrlEncode(CommonUtil.getDeviceName())));
		params.add(new BasicNameValuePair("ver",CommonUtil.toUrlEncode(URLUtil.version+"")));
		params.add(new BasicNameValuePair("deviceid",CommonUtil.toUrlEncode(CommonUtil.getIMEI(this))));
		if(!pid.equals("")){
			
			params.add(new BasicNameValuePair("state",actionType+","+shareType+","+thirdType+","+URLUtil.plaid+","+URLUtil.dpi()+","+pid));
		}else{
			
			params.add(new BasicNameValuePair("state",actionType+","+shareType+","+thirdType+","+URLUtil.plaid+","+URLUtil.dpi()));
		}
		new Thread() {
			public void run() {
				try {
					mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_START);
					String result = CommonUtil.httpPost(url,params);
					if (result != null) {
						Message msg = new Message();
						msg.obj = result.getBytes("utf-8");
						msg.what = 9091;
						mHandler.sendMessage(msg);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
  	}
	
	
	
	
	/**
	 * 分享
	 * @param url
	 * @param token
	 * @param logintype
	 * @param uid
	 * @param expireIn
	 * @param nickName
	 * @param sax
	 * @param headPicSmall
	 * @param headPicBig
	 * @param actionType
	 * @param shareType
	 * @param thirdType
	 * @param pid
	 */
	public void addUrlShareParam(final String url,String token,String uid,long expireIn,String nickName,String actionType,String shareType,String thirdType,String pid){

		final List <NameValuePair> params=new ArrayList<NameValuePair>();		
		params.add(new BasicNameValuePair("uid", CommonUtil.toUrlEncode(uid) ));
		params.add(new BasicNameValuePair("accessToken",CommonUtil.toUrlEncode(token)));
		params.add(new BasicNameValuePair("expireIn",expireIn+""));
		params.add(new BasicNameValuePair("nickName", CommonUtil.toUrlEncode(nickName)));
	
		params.add(new BasicNameValuePair("state",actionType+","+shareType+","+thirdType+","+URLUtil.plaid+","+UserUtil.userid+","+pid+","+URLUtil.dpi()));
		new Thread() {
			public void run() {
				try {
					mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_START);
					String result = CommonUtil.httpPost(url,params);
					if (result != null) {
						Message msg = new Message();
						msg.obj = result.getBytes("utf-8");
						msg.what = 9092;
						mHandler.sendMessage(msg);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
  	}
	
	/**
	 * 解析登录返回值
	 * @param data
	 */
	private void jsonLogin(byte[] data){
		try {
		String str = new String(data,"UTF-8");
		str = CommonUtil.formUrlEncode(str);
		str = str.replace(",null", "");
		LogPrint.Print("message","json = "+str);
		JSONObject jObject = new JSONObject(str);
		String result = jObject.getString("result");
		if(result != null){
			if(result.equalsIgnoreCase("true")){
				String oid = jObject.getString("oid");
				String name = jObject.getString("name");
				String gender = jObject.getString("gender");
				UserUtil.logintype = jObject.getInt("logintype");
				//所在地
				String location = jObject.getString("location");
				//真实姓名
				String buyerName = jObject.getString("buyerName");
				//生日
				String birthday = jObject.getString("birthday");
				//兴趣爱好
				String interest = jObject.getString("interest");
				//职业
				String occupational = jObject.getString("occupational");
				//教育
				String study = jObject.getString("study");
				//婚姻
				String marriage = jObject.getString("marriage");
				//收入
				String salary = jObject.getString("salary");
				//喜欢好友可见
				int likeperm = jObject.getInt("likeperm");
				//公开分享
				int sharaperm = jObject.getInt("sharaperm");
				//公开评论
				int commperm = jObject.getInt("commperm");
				//允许任何人加好友
				int addfriendperm = jObject.getInt("addfriendperm");
				//mode
				int bIsGestureMode = jObject.getInt("bIsGestureMode");
				//hand
				int bIsLeftMode = jObject.getInt("bIsLeftMode");
				if(oid != null){
					UserUtil.userid = Integer.parseInt(oid);
				}
				UserUtil.username = name;
				if(gender != null){
					UserUtil.gender = Integer.parseInt(gender);
				}else{
					UserUtil.gender = -1;
				}
				if(UserUtil.userid != -1&&UserUtil.username != null){
					UserUtil.isNewLoginOrExit = true;
					TimerForRock.reset();
					LogPrint.Print("=============oid = "+UserUtil.userid);
					LogPrint.Print("=============name = "+UserUtil.username);
					LogPrint.Print("=============gender = "+UserUtil.gender);
					LogPrint.Print("=============location = "+location);
					CommonUtil.saveUserId(this, UserUtil.userid);
					CommonUtil.saveUserName(this, UserUtil.username);
					CommonUtil.saveLoginType(this, UserUtil.logintype);
					CommonUtil.saveGender(this, UserUtil.gender);
					CommonUtil.saveArea(this, location);
					CommonUtil.saveLikeFriendState(this, likeperm==1?true:false);
					CommonUtil.saveShareFriendState(this,sharaperm==0?true:false);
					CommonUtil.saveBaskFriendState(this, commperm==0?true:false);
					CommonUtil.saveAllAddFriendState(this, addfriendperm==0?true:false);
					CommonUtil.saveRealName(this, buyerName);
					CommonUtil.saveBirthday(this, birthday);
					CommonUtil.saveInterest(this, interest);
					CommonUtil.saveOccuption(this, occupational);
					CommonUtil.saveMoney(this, salary);
					CommonUtil.saveEducation(this, study);
					CommonUtil.saveMarry(this, marriage);
					//判断操作条参数
					if (bIsGestureMode!=-1&&bIsLeftMode!=-1) {
						CommonUtil.saveOperateMode(this, bIsGestureMode+"");
						CommonUtil.saveOperateHand(this, bIsLeftMode+"");
					}
					else{
						CommonUtil.saveOperateMode(this, MODE);
						CommonUtil.saveOperateHand(this, HAND);
					}
					
					mHandler.sendEmptyMessage(MessageID.MESSAGE_CLOSEPROGRESS);
					UserUtil.userState = 1;
					CommonUtil.saveUserState(this, UserUtil.userState);
					CommonUtil.ShowToast(this, UserUtil.username+"，\n等您好久了，您终于回来啦！");
					//先关闭，重新连接
					this.stopService(new Intent(this,IMService.class));
					//开启信息服务
					this.startService(new Intent(this,IMService.class));
					CommonUtil.saveMessageReceiverState(this, true);
					OfflineLog.writeMainMenu_MessageButton((byte)1);//写入离线日志
					Intent intent = new Intent("true");
					this.setResult(Activity.RESULT_OK,intent);
					this.finish();
					//登录操作后直接跳转到首页
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(this, MainPageActivityA.class);
					this.startActivity(intent);
				}
				
			}else{
				String msg = jObject.getString("msg");
				if(msg != null){
					CommonUtil.ShowToast(this, "囧！登不进去，再来一次！\n"+msg);
				}else{
				CommonUtil.ShowToast(this, "囧！登不进去，再来一次！");
				}
				Intent intent = new Intent("false");
				this.setResult(Activity.RESULT_OK,intent);
				this.finish();
		
			}
		}
		
		} catch (Exception e) {
			//e.printStackTrace();
			Intent intent = new Intent("false");
			this.setResult(Activity.RESULT_OK,intent);
			this.finish();
		}
		

	}
	/**
	 * 解析绑定返回值
	 * @param data
	 */
	private void jsonBind(byte[] data){
		
		try {
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("webview","event_bind = "+str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			String bindtype = jObject.getString("bindtype");
			int binderid = -1;
			if(result != null){
				String msg = jObject.getString("msg");
				String nickname = jObject.getString("nickname");
				//绑定成功
				if(bindtype.equals("0")){
					binderid = jObject.getInt("binderid");
				}
				//绑定或者解绑成功,nickname有值
				if(result.equalsIgnoreCase("true")){
					if(msg != null){
						CommonUtil.ShowToast(this, msg);
					}
					Intent intent = new Intent("true");
					intent.putExtra("bindtype", jObject.getInt("bindtype"));
					intent.putExtra("nickname", nickname);
					intent.putExtra("binderid", binderid);
					this.setResult(Activity.RESULT_OK,intent);
					this.finish();
				}
				//绑定或者解绑失败,nickname空值
				else{
					if(msg != null){
						CommonUtil.ShowToast(this, msg);
					}
					Intent intent = new Intent("false");
					intent.putExtra("bindtype", jObject.getInt("bindtype"));
					intent.putExtra("nickname", nickname);
					this.setResult(Activity.RESULT_OK,intent);
					this.finish();
				}
				
			}
		} catch (Exception e) {
			//e.printStackTrace();
			Intent intent = new Intent("false");
			intent.putExtra("bindtype", -1);
			this.setResult(Activity.RESULT_OK,intent);
			this.finish();
		}
	}
	
	/**
	 * 解析分享返回值
	 * @param data
	 */
	private void jsonShare(byte[] data){
		try {
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("webview","event_share = "+str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			if(result != null){
				if(result.equalsIgnoreCase("true")){
					CommonUtil.saveUserState(this, UserUtil.userState);
					CommonUtil.ShowToast(this, "分享成功");
					mHandler.sendEmptyMessage(9093);
					this.finish();
				}else{
					try{
						String msg = jObject.getString("msg");
						if(msg != null){
							CommonUtil.ShowToast(this, "分享失败！\n"+msg);
						}else{
							mHandler.sendEmptyMessage(9093);
							CommonUtil.ShowToast(this, "分享失败！");
							
						}
					}catch (Exception e) {
						// TODO: handle exception
						CommonUtil.ShowToast(this, "分享失败！");
					}
					try {
						String code = jObject.getString("code");
						if(code != null){
							LogPrint.Print("webview","event_share code = "+code);
						}
					} catch (Exception e) {
					}
					this.finish();
				}
			}
		} catch (Exception e) {
			this.finish();
		}
	}
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			//登录
			case 9090:
				jsonLogin((byte[])msg.obj);
				finish();
				break;
			//绑定
			case 9091:
				jsonBind((byte[])msg.obj);
				break;
			//分享
			case 9092:
				jsonShare((byte[])msg.obj);
				break;
			//分享成功
			case 9093:
				CommonUtil.ShowToast(getApplicationContext(), "分享成功");
				finish();
				break;
			//分享失败
			case 9094:
				CommonUtil.ShowToast(getApplicationContext(), "分享失败");
				finish();
				break;
			case MessageID.MESSAGE_CLOSEPROGRESS:
				closeProgress();
				break;
			case MessageID.MESSAGE_CONNECT_START:
				addProgress();
				break;
			case 9099:
				finish();
				break;
			}
		}
	};
	
	
	public void addProgress(){
		
			if(loadingBar != null){
				loadingBar.setVisibility(View.VISIBLE);
			}
		
	}
	
	public void closeProgress(){
		if(loadingBar != null){
			loadingBar.setVisibility(View.GONE);
		}
	}
	
}
