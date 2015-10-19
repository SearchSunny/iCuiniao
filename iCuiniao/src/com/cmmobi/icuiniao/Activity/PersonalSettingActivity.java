package com.cmmobi.icuiniao.Activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.menuclick.Bind_MenuClick;
import com.cmmobi.icuiniao.menuclick.Setting_MenuClick;
import com.cmmobi.icuiniao.onlineEngine.activity.UserPageActivityA;
import com.cmmobi.icuiniao.onlineEngine.activity.PushFriendActivity.User;
import com.cmmobi.icuiniao.ui.view.SlipButton;
import com.cmmobi.icuiniao.ui.view.SlipButton.OnChangedListener;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.PageID;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.menuclick.Setting_MenuClick;
import com.cmmobi.icuiniao.ui.view.SlipButton;
import com.cmmobi.icuiniao.ui.view.SlipButton.OnChangedListener;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.PageID;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

public class PersonalSettingActivity extends Activity {

	
	//private SlipButton likeFriendSlipButton = null;//喜欢好友可见
	//private SlipButton shareFriendSlipButton = null;//分享好友可见
	//private SlipButton baskFriendSlipButton = null;//晒单好友可见
	//private SlipButton allAddFriendSlipButton = null;//允许任何人加好友
	//private SlipButton bindSinaSlipButton = null;//绑定新浪微博
	//private SlipButton bindQQSlipButton = null;//绑定QQ号码
	private RelativeLayout logout = null;//退出
//	private TextView bindQQTextView = null;
//	private TextView bindSinaTextView = null;
	private TextView restsTextView = null;
//	private boolean likeFriendBoolean = false;
//	private boolean shareFriendBoolean = false;
//	private boolean baskFriendBoolean = false;
//	private boolean allAddFriendBoolean = false;
	private boolean isReFlushIcon;//是否刷新头像
	private ConnectUtil mConnectUtil;
	private ProgressBar loadingBar;
	private int bindNum;//已绑定的个数,大于1时才能解除绑定
	private int logintype;
//	private final static String BIND_WEIBO = "未绑定新浪微博";
//	private final static String BIND_QQ = "未绑定QQ";
//	private boolean isBindQQ = false;
//	private boolean isBindWeibo = false;
//	private boolean isSucceedQQ = false;
//	private boolean isSucceedWeibo = false;
//	private boolean isSucceed = false;
	private EditText nicknameEditText = null;
//	private Button titlebar_menubutton = null;
	private Button titlebar_backbutton = null;
	private RelativeLayout upHeadImage = null;
	private RelativeLayout sex = null;
	private RelativeLayout city = null;
	private RelativeLayout rests = null;
	private RelativeLayout myPageSetting = null;
	private RelativeLayout homePageSetting = null;
	private String photoPath = null;
	private String cameraPath = null;
	private String cameraPhotoName = null;
	private ImageView iconimage = null;
	private TextView sexTextView = null;
	private TextView cityTextView = null;
	private final int SEX_REQUEST = 110;
	private final int CITY_REQUEST = 111;
	private String sexString = null;
	private String area = null;
	private String areaView = null;
	private boolean isSave = false;
	private boolean isCity = false;
	
	private SlipButton slipPrice;
	private SlipButton slipFeature;
	
	private boolean isShowPrice;
	private boolean isShowFeature;
	
	//联网线程
//	private final int thread_like = 10;
//	private final int thread_share = 11;
//	private final int thread_comment = 12;
//	private final int thread_allow_addFriend = 13;
	
	private final int thread_saveInfo = 0;
	private final int thread_price = 1;
	private final int thread_feature = 2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personalsetting);
		initWidget();
		setListener();
		bindNum = 0;
		logintype = 0;
		isReFlushIcon = true;
		initData();
		OfflineLog.writeSettingMore();
		isShowPrice = CommonUtil.getPriceShow(this);
		isShowFeature = CommonUtil.getFeatureShow(this);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
		//为防止从绑定Activity中点击返回按钮情况重新为滑动按钮赋值
//		if(bindQQSlipButton!=null&&bindSinaSlipButton!=null){
//			if(isSucceed){
//				bindQQSlipButton.setCheck(isBindQQ);
//				bindSinaSlipButton.setCheck(isBindWeibo);
//			}
//			else{
//				if(isSucceedQQ){
//					bindQQSlipButton.setCheck(false);
//				}
//				if(isSucceedWeibo){
//					bindSinaSlipButton.setCheck(false);
//				}
//			}
//		}	
		if(isReFlushIcon){
			if(CommonUtil.isNetWorkOpen(this)){
				//获取用户头像
				mConnectUtil = new ConnectUtil(PersonalSettingActivity.this, mHandler,1,0);
				mConnectUtil.connect(URLUtil.URL_GET_USERICON+UserUtil.userid+".jpg", HttpThread.TYPE_IMAGE, 0);
				addProgress();
				initData();
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
				postFinish(msg.arg1);
				break;
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				closeProgress();
				if(mConnectUtil != null){
					if("text/html".equalsIgnoreCase(msg.getData().getString("content_type"))){
						try {
							String tmpurl = new String((byte[])msg.obj,"UTF-8");
							if(tmpurl != null){
								mConnectUtil = new ConnectUtil(PersonalSettingActivity.this, mHandler,0);
								mConnectUtil.connect(tmpurl, HttpThread.TYPE_IMAGE, 0);
								addProgress();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else if("text/json".equals(msg.getData().getString("content_type"))){
//						if(msg.arg1 == thread_like || msg.arg1 == thread_share || msg.arg1 == thread_comment || msg.arg1 == thread_allow_addFriend){
//							String resultBool = jsonBoolean((byte[])msg.obj);
//							int checkState = msg.getData().getInt("tag");							
//							boolean checkBool = getBoolCheckValue(msg.arg1, checkState);
//							if(resultBool.equalsIgnoreCase("true")){
//								saveAuthory(msg.arg1, checkBool);
//							}else{
//								setOldCheck(msg.arg1, !checkBool);
//							}
//							return;
//						}
						
						if(msg.arg1 == thread_price || msg.arg1 == thread_feature){
							jsonPriceOrFeature((byte[])msg.obj, msg);
							return;
						}
						if(isSave){
							Json((byte[])msg.obj);
							isSave = false;
							postFinish(msg.arg1);
						}
						else{
							Json((byte[])msg.obj, msg.arg1);							
						}
					}else{//图片
						setImageView((byte[])msg.obj,msg.getData().getString("mUrl"));
					}
				}
				break;
//			case MessageID.MESSAGE_BIND_MENUCLICK_CANCEL:
//				bindSinaSlipButton.setCheck(isBindWeibo);
//				bindQQSlipButton.setCheck(isBindQQ);
//				break;
			case MessageID.MESSAGE_BIND_MENUCLICK:
				switch (msg.arg1) {
				case 1://qq
					if(CommonUtil.isNetWorkOpen(PersonalSettingActivity.this)){
						Intent intent11 = new Intent();
						intent11.setClass(PersonalSettingActivity.this, WebViewLoginActivity.class);
						logintype = 1;
						String url = addUrlParam(URLUtil.URL_BIND_REMOVE, UserUtil.userid, CommonUtil.getBindId_QQ(PersonalSettingActivity.this),logintype);
						LogPrint.Print("webview","unbind qq url = "+url);
						intent11.putExtra("url", url);
						startActivityForResult(intent11, 9002);
					}else{
						CommonUtil.ShowToast(PersonalSettingActivity.this, "杯具了- -!\n联网不给力啊");
					}
					break;
				case 2://weibo
					if(CommonUtil.isNetWorkOpen(PersonalSettingActivity.this)){
						Intent intent11 = new Intent();
						intent11.setClass(PersonalSettingActivity.this, WebViewLoginActivity.class);
						logintype = 2;
						String url = addUrlParam(URLUtil.URL_BIND_REMOVE, UserUtil.userid, CommonUtil.getBindId_Weibo(PersonalSettingActivity.this),logintype);
						LogPrint.Print("webview","unbind weibo url = "+url);
						intent11.putExtra("url", url);
						startActivityForResult(intent11, 9002);
					}else{
						CommonUtil.ShowToast(PersonalSettingActivity.this, "杯具了- -!\n联网不给力啊");
					}
					break;
				}
				break;
			case MessageID.MESSAGE_MENUCLICK_SETTING_CAMERA:
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//				try {
//					//某些手机无法得到此路径
//					cameraPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
//				} catch (Exception e) {
					cameraPath = CommonUtil.getExtendsCardPath()+"iCuiniao/camera";
//				}
				cameraPhotoName = "IMG_"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
				File out = new File(cameraPath);
				if (!out.exists()) {
					out.mkdirs();
				}
				out = new File(cameraPath, cameraPhotoName);
				photoPath = cameraPath+File.separator+cameraPhotoName;// 该照片的绝对路径
				Uri uri = Uri.fromFile(out);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);//图片质量

				startActivityForResult(intent, 900);//相机
				break;
			case MessageID.MESSAGE_MENUCLICK_SETTING_PHOTO:
				Intent intent1 =new Intent(Intent.ACTION_GET_CONTENT);
				intent1.setType("image/*");
				startActivityForResult(intent1, 901);//相册
				break;
			case MessageID.MESSAGE_PHOTO_RESULT:
				LogPrint.Print("photo result");
				isReFlushIcon = true;
				Intent intent2 = new Intent();
				intent2.setClass(PersonalSettingActivity.this, CropActivity.class);
				intent2.putExtra("imagepath", photoPath);
				startActivity(intent2);
				break;
			}
		}
	};
	//绑定：9002
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			switch (requestCode) {
			case SEX_REQUEST:
				Bundle bundle = data.getExtras();
				String sexString = bundle.getString("sex");
				sexTextView.setText(sexString);
				isReFlushIcon = false;
				break;
			case CITY_REQUEST:
				Bundle bundle2 = data.getExtras();
				String cityName = bundle2.getString("cityName");
				isCity = bundle2.getBoolean("isCity");
				area = cityName;
				if(!cityName.equals("")){
					try{
						areaView = cityName.split(" ")[1];
						cityTextView.setText(areaView);
					}catch(Exception e){
						cityTextView.setText("");
					}
				}
				else{
					cityTextView.setText("");
				}
				isReFlushIcon = false;
				break;
//			case 9002:
//				if("true".equals(data.getAction())){
//					isSucceed = true;
//					LogPrint.Print("bind ok");
//					int bindtype = data.getIntExtra("bindtype", -1);
//					String nickname = data.getStringExtra("nickname");
//					int binderid = data.getIntExtra("binderid", -1);
//					LogPrint.Print("bindtype = "+bindtype);
//					LogPrint.Print("logintype = "+logintype);
//					if(bindtype != -1){
//						if(bindtype == 0){//绑定完成
//							if(logintype == 1){//qq
//								bindNum ++;
//								bindQQTextView.setText(nickname);
//								bindQQSlipButton.setCheck(true);
//								isBindQQ = true;
//								isSucceedQQ = true;
//								CommonUtil.saveBindId_QQ(PersonalSettingActivity.this, binderid);
//							}else if(logintype == 2){//weibo
//								bindNum ++;
//								bindSinaTextView.setText(nickname);
//								bindSinaSlipButton.setCheck(true);
//								isBindWeibo = true;
//								isSucceedWeibo = true;
//								CommonUtil.saveBindId_Weibo(PersonalSettingActivity.this, binderid);
//							}
//						}else{//解除绑定完成
//							if(logintype == 1){//qq
//								bindNum --;
//								bindQQTextView.setText(BIND_QQ);
//								bindQQSlipButton.setCheck(false);
//								isBindQQ = false;
//								isSucceedQQ = false;
//							}else if(logintype == 2){//weibo
//								bindNum --;
//								bindQQTextView.setText(BIND_WEIBO);
//								bindQQSlipButton.setCheck(false);
//								isBindWeibo = false;
//								isSucceedWeibo = false;
//							}
//						}
//					}
//				}else{
//					LogPrint.Print("bind false");
//				}
//				break;
			case 900:
				LogPrint.Print("photoPath = "+photoPath);
				if(photoPath != null){
					mHandler.sendEmptyMessage(MessageID.MESSAGE_PHOTO_RESULT);
				}
				break;
			case 901:
				if(data != null){
					Uri selectImage = data.getData();
					String[] proj = {MediaStore.Images.Media.DATA};
					Cursor cursor = getContentResolver().query(selectImage, proj, null, null, null);
					cursor.moveToFirst();
					int columnIndex = cursor.getColumnIndex(proj[0]);
					photoPath = cursor.getString(columnIndex);
					LogPrint.Print("photoPath = "+photoPath);
					cursor.close();
				}
				if(photoPath != null){
					mHandler.sendEmptyMessage(MessageID.MESSAGE_PHOTO_RESULT);
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
//			case 0:
//				JSONArray jsonArray = new JSONArray(str);
//				int count = 0;
//				for(int i = 0;jsonArray!=null&&i < jsonArray.length();i ++){
//					JSONObject jObject = jsonArray.getJSONObject(i);
//					String result = jObject.getString("result");
//					if(result != null){
//						if(result.equalsIgnoreCase("true")){
//							boolean binder = jObject.getBoolean("binder");
//							int logintype = jObject.getInt("logintype");
//							int binderid = jObject.getInt("binderid");
//							String nickname = jObject.getString("nickname");
//							String loginname = jObject.getString("loginname");
//							if(binder){//已绑定
//								switch (logintype) {
//								case 1://qq
//									bindNum ++;
//									bindQQTextView.setText(nickname);
//									bindQQSlipButton.setCheck(true);
//									isBindQQ = true;
//									break;
//								case 2://weibo
//									bindNum ++;
//									bindSinaTextView.setText(nickname);
//									bindSinaSlipButton.setCheck(true);
//									isBindWeibo = true;
//									break;
//								}
//							}else{//未绑定
//								switch (logintype) {
//								case 1://qq
//									bindQQTextView.setText(BIND_QQ);
//									bindQQSlipButton.setCheck(false);
//									isBindQQ = false;
//									break;
//								case 2://weibo
//									bindSinaTextView.setText(BIND_WEIBO);
//									bindSinaSlipButton.setCheck(false);
//									isBindWeibo = false;
//									break;
//								}
//							}
//							//保存binderid
//							switch (logintype) {
//							case 1://qq
//								CommonUtil.saveBindId_QQ(PersonalSettingActivity.this, binderid);
//								break;
//							case 2://weibo
//								CommonUtil.saveBindId_Weibo(PersonalSettingActivity.this, binderid);
//								break;
//							}
//						}
//					}
//				}
//				break;
			case 3:
				JSONObject jsonObject = new JSONObject(str);
				String result = jsonObject.getString("result");
				if(result.equalsIgnoreCase(result)){
					Intent intent = new Intent(PersonalSettingActivity.this,BindActivity.class);
					startActivity(intent);
					finish();
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//价格或特征的上传解析
	private void jsonPriceOrFeature(byte[] data, Message msg){
		try{
		int	threadIdx = msg.arg1;
		String str = new String(data,"UTF-8");
		str = CommonUtil.formUrlEncode(str);
		LogPrint.Print("json price feature = "+str);
		JSONObject jObject = new JSONObject(str);
		if(jObject.isNull("result")){
			if(threadIdx == thread_price){
				UserUtil.isShowPrice = jObject.getBoolean("isShowPrice");
				CommonUtil.savePriceShow(this, UserUtil.isShowPrice);
			}else if(threadIdx == thread_feature){
				UserUtil.isShowFeature= jObject.getBoolean("isShowFeature");
				CommonUtil.saveFeatureShow(this, UserUtil.isShowFeature);
			}
		}else{
			 boolean oldCheck = CommonUtil.byte2Bool((byte)msg.getData().getInt("tag"));			
			CommonUtil.ShowToast(this, "上传失败");
			if(threadIdx == thread_price){
				slipPrice.setCheck(oldCheck);
			}else if(threadIdx == thread_feature){
				slipFeature.setCheck(oldCheck);
			}
			
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 解析设置的bool返回值
	 * @param data
	 * @return
	 */
	private String jsonBoolean(byte[] data){
		try{
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = "+str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			return result;
		}catch(Exception e){
			e.printStackTrace();
		}	
		return "";
	}
	
	private void Json(byte[] data){
		try{
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = "+str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			if(result != null){
				if(result.equalsIgnoreCase("true")){
					String oid = jObject.getString("oid");
					String name = jObject.getString("name");
					String gender = jObject.getString("gender");
					if(!jObject.isNull("isShowPrice")){
						UserUtil.isShowPrice = jObject.getBoolean("isShowPrice");
					}
					if(!jObject.isNull("isShowFeature")){
						UserUtil.isShowFeature = jObject.getBoolean("isShowFeature");
					}
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
						LogPrint.Print("=============oid = "+UserUtil.userid);
						LogPrint.Print("=============name = "+UserUtil.username);
						LogPrint.Print("=============gender = "+UserUtil.gender);
						CommonUtil.saveUserId(PersonalSettingActivity.this, UserUtil.userid);
						CommonUtil.saveUserName(PersonalSettingActivity.this, UserUtil.username);
						UserUtil.logintype = 0;
						CommonUtil.saveLoginType(PersonalSettingActivity.this, UserUtil.logintype);
						CommonUtil.saveGender(PersonalSettingActivity.this, UserUtil.gender);
						//价格设置
						CommonUtil.savePriceShow(PersonalSettingActivity.this, UserUtil.isShowPrice);
						CommonUtil.saveFeatureShow(PersonalSettingActivity.this, UserUtil.isShowFeature);
						UserUtil.userState = 1;
						CommonUtil.saveUserState(PersonalSettingActivity.this, UserUtil.userState);
						CommonUtil.ShowToast(PersonalSettingActivity.this, "主人，您的资料更新成功。");
						
						mHandler.sendEmptyMessage(MessageID.MESSAGE_FLUSH_PAGE);
					}
				}else{
					String msg = jObject.getString("msg");
					if(msg != null){
						CommonUtil.ShowToast(PersonalSettingActivity.this, "更新失败鸟，\n"+msg);
					}else{
						CommonUtil.ShowToast(PersonalSettingActivity.this, "更新失败鸟");
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void setImageView(byte[] data,String url){
		try {
			Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
			if(temp == null)return;
			Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.usericonbg);
			temp = CommonUtil.resizeImage(temp, background.getWidth(), background.getHeight());
			iconimage.setImageBitmap(CommonUtil.mergerIcon(background, temp,7));
			//保存图片
			CommonUtil.writeToFile(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(url), data);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	public String addUrlParam(String url, int oid, String name,
			 String email, String gender,
			String area, String addr, String phone,
			boolean isShowPrice, boolean isShowFeature) {
		if (URLUtil.IsLocalUrl()) {
			return url;
		}
		return url + "?oid=" + oid + "&name=" + CommonUtil.toUrlEncode(name)				
				+ "&email=" + CommonUtil.toUrlEncode(email)
				+ "&gender=" + CommonUtil.toUrlEncode(gender)				
				+ "&area=" + CommonUtil.toUrlEncode(area)
				+ "&addr=" + CommonUtil.toUrlEncode(addr)
				+ "&phone=" + CommonUtil.toUrlEncode(phone)
				+ "&isShowPrice=" + Boolean.toString(isShowPrice)
				+ "&isShowFeature=" + Boolean.toString(isShowFeature);
				
	}
	/**
	 * 性别监听
	 */
	private OnClickListener sexListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(PersonalSettingActivity.this,SexActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("sex", UserUtil.gender);
			intent.putExtras(bundle);
			startActivityForResult(intent, SEX_REQUEST);
		}
	};
	
	/**
	 * 所在地监听
	 */
	private OnClickListener cityListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(PersonalSettingActivity.this,CityActivity.class);
			Bundle bundle = new Bundle();
			area = CommonUtil.getArea(PersonalSettingActivity.this);
			bundle.putString("area", area);
			intent.putExtras(bundle);
			startActivityForResult(intent, CITY_REQUEST);
		}
	};
	
	private OnClickListener homePageListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(PersonalSettingActivity.this,HomePageSettingActivity.class);
			startActivity(intent);
		}
	};
	
	/**
	 * 
	 * @param threadIndex
	 * @param parmName likeperm：喜欢；sharaperm：分享；评论： "commperm" ；
	 * @param checkState
	 */
//	private void connectSetAuthority(int threadIndex, String parmName, boolean checkState){
//		int value = getIntCheckValue(threadIndex, checkState);
//		String url = URLUtil.Url_SET_QUIRY_AUTHOR + "?" +  parmName + "=" + value + "&oid=" + UserUtil.userid;
//		ConnectUtil connectUtil = new
//		 ConnectUtil(PersonalSettingActivity.this, mHandler, 1, value);
//		connectUtil.connect(url, HttpThread.TYPE_PAGE,
//				threadIndex);
//	}
//	
//	private void connectSetPermitAddFriend( boolean checkState){
//		final int threadIndex = thread_allow_addFriend;
//		int value = getIntCheckValue(threadIndex, checkState);
//		String url = URLUtil.Url_SET_PERMIT_ADD_FRIEND + "?addfriendperm=" +  value + "&oid=" + UserUtil.userid;
//		ConnectUtil connectUtil = new
//		 ConnectUtil(PersonalSettingActivity.this, mHandler, 1, value);
//		connectUtil.connect(url, HttpThread.TYPE_PAGE,
//				threadIndex);
//	}	
	
	/**
	 * //喜欢的查询权限(1:好友可见、2:自己可见) ;开关关闭为仅自己可见2，开关开启为好友可见1
	 * 分享的查询权限(0:全部可见、1:好友可见)  ; 开关开启为任何人可见0，开关关闭为好友可见1
	 * 评论的查询权限(0:全部可见、1:好友可见)
	 * @param threadIndex
	 * @param checkState
	 * @return
	 */
//	private int getIntCheckValue(int threadIndex, boolean checkState){
//		int checkInt = 1;
//		if(checkState){
//			checkInt = 0;
//		}
//		
//		if(threadIndex == thread_like){
//			checkInt ++;
//		}
//		return checkInt;
//	}
	
//	private boolean getBoolCheckValue(int threadIndex, int checkState){
//		boolean checkBool = true;
//		if(threadIndex == thread_like){
//			checkState --;
//		}
//		if(checkState == 0){
//			checkBool = true;
//		}else if(checkState == 1){
//			checkBool = false;
//		}
//		return checkBool;
//	}
	
//	private void saveAuthory(int threadIndex, boolean checkState){
//		switch(threadIndex){
//		case thread_like:
//			CommonUtil.saveLikeFriendState(PersonalSettingActivity.this, checkState);
//			break;
//		case thread_share:
//			CommonUtil.saveShareFriendState(PersonalSettingActivity.this, checkState);
//			break;
//		case thread_comment:
//			CommonUtil.saveBaskFriendState(PersonalSettingActivity.this, checkState);
//			break;
//		case thread_allow_addFriend:
//			CommonUtil.saveAllAddFriendState(PersonalSettingActivity.this, checkState);
//			break;
//		}
//	}
	
//	private void setOldCheck(int threadIndex, boolean oldCheck){
//		switch(threadIndex){
//		case thread_like:
//			likeFriendSlipButton.setCheck(oldCheck);
//			break;
//		case thread_share:
//			shareFriendSlipButton.setCheck(oldCheck);
//			break;
//		case thread_comment:
//			baskFriendSlipButton.setCheck(oldCheck);
//			break;
//		case thread_allow_addFriend:
//			allAddFriendSlipButton.setCheck(oldCheck);
//			break;
//		}
//	}
	
	/**
	 * 喜欢好友可见监听
	 */
//	private OnChangedListener likeFriendChangedListener = new OnChangedListener() {
//		
//		@Override
//		public void onChanged(boolean checkState) {
//			connectSetAuthority(thread_like, "likeperm", checkState);			
//		}
//	};
	
	/**
	 * 分享好友可见监听
	 */
//	private OnChangedListener shareFriendListener = new OnChangedListener() {
//		
//		@Override
//		public void onChanged(boolean checkState) {
//			connectSetAuthority(thread_share, "sharaperm", checkState);			
//		}
//	};
	
	/**
	 * 晒单好友可见监听
	 */
//	private OnChangedListener baskFriendListener = new OnChangedListener() {
//		
//		@Override
//		public void onChanged(boolean checkState) {
//			connectSetAuthority(thread_comment, "commperm", checkState);			
//		}
//	};
	
	/**
	 * 允许任何人加好友 监听
	 */
//	private OnChangedListener allAddFriendListener = new OnChangedListener() {
//		
//		@Override
//		public void onChanged(boolean checkState) {
//			connectSetPermitAddFriend(checkState);			
//		}
//	};
	
	
//	/**
//	 * 绑定新浪微博监听
//	 */
//	private OnChangedListener bindSinaListener = new OnChangedListener() {
//		
//		@Override
//		public void onChanged(boolean checkState) {
//			logintype = 2;
//			//去绑定
//			if(checkState){
//				if(CommonUtil.isNetWorkOpen(PersonalSettingActivity.this)){
//					Intent intent11 = new Intent();
//					intent11.setClass(PersonalSettingActivity.this, WebViewLoginActivity.class);
//					String url = addUrlParam(URLUtil.URL_BIND_ADD, UserUtil.userid, logintype, "sina");
//					LogPrint.Print("webview","bind weibo url = "+url);
//					intent11.putExtra("url", url);
//					isSucceedWeibo = true;
//					startActivityForResult(intent11, 9002);
//				}else{
//					CommonUtil.ShowToast(PersonalSettingActivity.this, "杯具了- -!\n联网不给力啊");
//				}
//			}
//			//取消绑定
//			else{
//				Bind_MenuClick menuClick = new Bind_MenuClick(mHandler,2);
//				Intent intent3 = new Intent();
//				intent3.setClass(PersonalSettingActivity.this, AbsCuiniaoMenu.class);
//				intent3.putExtra("title", PageID.SETTING_MESSAGE_MENU_TITLE);
//				intent3.putExtra("items", PageID.BIND_MENU_ITEM);
//				startActivity(intent3);
//				AbsCuiniaoMenu.set(menuClick);
//			}
//		}
//	};
	
//	/**
//	 * 绑定QQ监听
//	 */
//	private OnChangedListener bindQQListener = new OnChangedListener() {
//		
//		@Override
//		public void onChanged(boolean checkState) {
//			logintype = 1;
//			//去绑定
//			if(checkState){
//				if(CommonUtil.isNetWorkOpen(PersonalSettingActivity.this)){
//					Intent intent11 = new Intent();
//					intent11.setClass(PersonalSettingActivity.this, WebViewLoginActivity.class);
//					String url = addUrlParam(URLUtil.URL_BIND_ADD, UserUtil.userid, logintype, "qq");
//					LogPrint.Print("webview","bind qq url = "+url);
//					intent11.putExtra("url", url);
//					isSucceedQQ = true;
//					startActivityForResult(intent11, 9002);
//				}else{
//					CommonUtil.ShowToast(PersonalSettingActivity.this, "杯具了- -!\n联网不给力啊");
//				}
//			}
//			//取消绑定
//			else{
//				Bind_MenuClick menuClick = new Bind_MenuClick(mHandler,1);
//				Intent intent3 = new Intent();
//				intent3.setClass(PersonalSettingActivity.this, AbsCuiniaoMenu.class);
//				intent3.putExtra("title", PageID.SETTING_MESSAGE_MENU_TITLE);
//				intent3.putExtra("items", PageID.BIND_MENU_ITEM);
//				startActivity(intent3);
//				AbsCuiniaoMenu.set(menuClick);
//			}
//		}
//	};
	
	//商品价格显示监听
	private OnChangedListener priceShowListener = new OnChangedListener() {
		
		@Override
		public void onChanged(boolean checkState) {		
			
//			UserUtil.isShowPrice = checkState;
			UserUtil.isPriceOrFeatureChange = true;
//			CommonUtil.savePriceShow(PersonalSettingActivity.this, checkState);
			isShowPrice = checkState;
			
			String url = URLUtil.URL_PRICE_FEATURE + "?isShowPrice=" + checkState;
			mConnectUtil = new ConnectUtil(PersonalSettingActivity.this, mHandler, 1, CommonUtil.bool2Byte(checkState));
			mConnectUtil.connect(url, HttpThread.TYPE_PAGE, thread_price);
		}
	};
	
	//商品特征显示监听
	private OnChangedListener featureShowListener = new OnChangedListener() {
		
		@Override
		public void onChanged(boolean checkState) {
//			UserUtil.isShowFeature = checkState;
			UserUtil.isPriceOrFeatureChange = true;
//			CommonUtil.saveFeatureShow(PersonalSettingActivity.this, checkState);
			isShowFeature = checkState;
			
			String url = URLUtil.URL_PRICE_FEATURE + "?isShowFeature=" + checkState;
			mConnectUtil = new ConnectUtil(PersonalSettingActivity.this, mHandler, 1, CommonUtil.bool2Byte(checkState));
			mConnectUtil.connect(url, HttpThread.TYPE_PAGE, thread_feature);
		}
	};
	/**
	 * 操作条设置监听
	 */
	private OnClickListener myPageSettingListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(PersonalSettingActivity.this,OperateActivity.class);
			startActivity(intent);
		}
	};
	
	/**
	 * 退出监听
	 */
	private OnClickListener logoutListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			isReFlushIcon = true;
			Intent intent = new Intent(PersonalSettingActivity.this, LoginAndRegeditActivity.class);
			startActivity(intent);
			OfflineLog.writeLogout();
		}
	};
	
	/**
	 * 返回监听
	 */
	private OnClickListener backClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
//			finish();
			saveInfo();
		}
	};
	
	/**
	 * 保存监听
	 */
//	private OnClickListener saveListener = new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			//昵称
//			String nickname = "";
//			//真实姓名
////			String realName = "";
//			//邮箱
//			String email = "";
//			//生日
////			String birthday = "";
//			//地址
//			String addr = "";
//			//电话
//			String phone = "";
//			if(nicknameEditText.getText().length()>0){
//				nickname = nicknameEditText.getText().toString().trim();
//			}
//			if(sexTextView.getText().length()>0){
//				sexString = sexTextView.getText().toString().trim();
//			}
//			isSave = true;
//			mConnectUtil = new ConnectUtil(PersonalSettingActivity.this, mHandler,0);
////			mConnectUtil.connect(addUrlParam(URLUtil.URL_SETTING_MORE, UserUtil.userid,nickname,realName,email,sexString,"",area,addr,phone,"","","","",""), HttpThread.TYPE_PAGE, 0);
//			mConnectUtil.connect(addUrlParam(URLUtil.Url_USER_DETAIL_SET, UserUtil.userid, nickname, email, sexString, area, addr, phone
//					, isShowPrice, isShowFeature), HttpThread.TYPE_PAGE, 0);
//			CommonUtil.saveArea(PersonalSettingActivity.this, area);
//		}
//	};
	
	private void saveInfo(){
		//昵称
		String nickname = "";
		//真实姓名
//		String realName = "";
		//邮箱
		String email = "";
		//生日
//		String birthday = "";
		//地址
		String addr = "";
		//电话
		String phone = "";
		if(nicknameEditText.getText().length()>0){
			nickname = nicknameEditText.getText().toString().trim();
		}
		if(sexTextView.getText().length()>0){
			sexString = sexTextView.getText().toString().trim();
		}
		isSave = true;
		mConnectUtil = new ConnectUtil(PersonalSettingActivity.this, mHandler,0);
//		mConnectUtil.connect(addUrlParam(URLUtil.URL_SETTING_MORE, UserUtil.userid,nickname,realName,email,sexString,"",area,addr,phone,"","","","",""), HttpThread.TYPE_PAGE, 0);
		mConnectUtil.connect(addUrlParam(URLUtil.Url_USER_DETAIL_SET, UserUtil.userid, nickname, email, sexString, area, addr, phone
				, isShowPrice, isShowFeature), HttpThread.TYPE_PAGE, thread_saveInfo);
		CommonUtil.saveArea(PersonalSettingActivity.this, area);
	}
	
	/**
	 * 上传头像监听
	 */
	private OnClickListener upHeadImageListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			isReFlushIcon = false;
			Setting_MenuClick menuClick = new Setting_MenuClick(mHandler);
			Intent intent = new Intent();
			intent.setClass(PersonalSettingActivity.this, AbsCuiniaoMenu.class);
			intent.putExtra("title", PageID.SETTING_MENU_TITLE);
			intent.putExtra("items", PageID.SETTING_MENU_ITEM);
			startActivity(intent);
			AbsCuiniaoMenu.set(menuClick);
		}
	};
	
	/**
	 * 完善其他信息监听
	 */
	private OnClickListener resetListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(PersonalSettingActivity.this, SettingMoreActivity_A.class);
			startActivity(intent);
		}
	};
	
	private void initWidget(){
//		likeFriendSlipButton = (SlipButton)findViewById(R.id.likeFriendSlipButton);
//		shareFriendSlipButton = (SlipButton)findViewById(R.id.shareFriendSlipButton);
//		baskFriendSlipButton = (SlipButton)findViewById(R.id.baskFriendSlipButton);
//		allAddFriendSlipButton = (SlipButton)findViewById(R.id.allAddFriendSlipButton);
//		bindSinaSlipButton = (SlipButton)findViewById(R.id.bindSinaSlipButton);
//		bindQQSlipButton = (SlipButton)findViewById(R.id.bindQQSlipButton);
		logout = (RelativeLayout)findViewById(R.id.logout);
		myPageSetting = (RelativeLayout)findViewById(R.id.myPageSetting);
		loadingBar = (ProgressBar)findViewById(R.id.loading);
//		bindQQTextView = (TextView)findViewById(R.id.bindQQTextView);
//		bindSinaTextView = (TextView)findViewById(R.id.bindSinaTextView);
		nicknameEditText = (EditText)findViewById(R.id.nicknameEditText);
//		titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		upHeadImage = (RelativeLayout)findViewById(R.id.upHeadImage);
		iconimage = (ImageView)findViewById(R.id.iconimage);
		sex = (RelativeLayout)findViewById(R.id.sex);
		sexTextView = (TextView)findViewById(R.id.sexTextView);
		city = (RelativeLayout)findViewById(R.id.city);
		cityTextView = (TextView)findViewById(R.id.cityTextView);
		rests = (RelativeLayout)findViewById(R.id.rests);
		restsTextView = (TextView)findViewById(R.id.restsTextView);
		homePageSetting = (RelativeLayout)findViewById(R.id.homePageSetting);
		
		slipPrice = (SlipButton)findViewById(R.id.slipPrice);
		slipFeature = (SlipButton)findViewById(R.id.slipFeature);
	}
	
	private void setListener(){
//		likeFriendSlipButton.setOnChangedListener(likeFriendChangedListener);
//		shareFriendSlipButton.setOnChangedListener(shareFriendListener);
//		baskFriendSlipButton.setOnChangedListener(baskFriendListener);
//		allAddFriendSlipButton.setOnChangedListener(allAddFriendListener);
//		bindSinaSlipButton.setOnChangedListener(bindSinaListener);
//		bindQQSlipButton.setOnChangedListener(bindQQListener);
		myPageSetting.setOnClickListener(myPageSettingListener);
		logout.setOnClickListener(logoutListener);
//		titlebar_menubutton.setOnClickListener(saveListener);
		titlebar_backbutton.setOnClickListener(backClickListener);
		upHeadImage.setOnClickListener(upHeadImageListener);
		sex.setOnClickListener(sexListener);
		city.setOnClickListener(cityListener);
		rests.setOnClickListener(resetListener);
		homePageSetting.setOnClickListener(homePageListener);
		//
		slipPrice.setOnChangedListener(priceShowListener);
		slipFeature.setOnChangedListener(featureShowListener);
	}
	
	public void initData(){
//		//喜欢好友可见设置
//		likeFriendBoolean = CommonUtil.getLikeFriendState(PersonalSettingActivity.this);
//		likeFriendSlipButton.setCheck(likeFriendBoolean);
//		//分享公开可见设置
//		shareFriendBoolean = CommonUtil.getShareFriendState(PersonalSettingActivity.this);
//		shareFriendSlipButton.setCheck(shareFriendBoolean);
//		//公开评论可见设置
//		baskFriendBoolean = CommonUtil.getBaskFriendState(PersonalSettingActivity.this);
//		baskFriendSlipButton.setCheck(baskFriendBoolean);
//		//允许任何人加好友
//		allAddFriendBoolean = CommonUtil.getAllAddFriendState(PersonalSettingActivity.this);
//		allAddFriendSlipButton.setCheck(allAddFriendBoolean);
		nicknameEditText.setText(UserUtil.username);
//		mConnectUtil = new ConnectUtil(this, mHandler,0);
//		mConnectUtil.connect(URLUtil.URL_BIND_GET+"?plaid="+URLUtil.plaid, HttpThread.TYPE_PAGE, 0);
		if(UserUtil.gender != -1){
			sexString = UserUtil.gender==0?"男":"女";
			sexTextView.setText(sexString);
		}
		area = CommonUtil.getArea(PersonalSettingActivity.this)==null?"":CommonUtil.getArea(PersonalSettingActivity.this);
		if(area!=null&&!area.equals("")){
			areaView = area.split(" ")[1];
			cityTextView.setText(areaView);
		}
		else{
			cityTextView.setText(area);
		}
		if(CommonUtil.getIsUserInfo(PersonalSettingActivity.this)){
			restsTextView.setText("");
		}
		else{
			restsTextView.setText("马上完善");
		}
		
		slipPrice.setCheck(UserUtil.isShowPrice);
		slipFeature.setCheck(UserUtil.isShowFeature);
		
	}
	
	@Override
	public void finish(){
		super.finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			saveInfo();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	private void postFinish(int threadIdx){
		if(threadIdx == thread_saveInfo){
			mHandler.postDelayed(new Runnable(){

				@Override
				public void run() {
					finish();								
				}
				
			}, 100);
		}
	}
}
