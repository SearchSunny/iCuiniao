/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.menuclick.Setting_MenuClick;
import com.cmmobi.icuiniao.ui.view.MainMenu;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.MySoftReference;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.PageID;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

/**
 * @author hw
 *
 */
public class SettingActivity extends Activity implements AnimationListener{

	private Button titlebar_menubutton;
	private TextView name_input;
	private ImageView iconimage;
	private Button seticonbtn;
	private RelativeLayout moresetting;
	private Button local_kgbtn_l;
	private Button local_kgbtn_r;
	private RelativeLayout pwsetting;
	private Button deletecache;
	private RelativeLayout about;
	private RelativeLayout feedback;
	private Button updata;
	private RelativeLayout addrsetting;
	private RelativeLayout logout;
	private RelativeLayout headSetting;
	private RelativeLayout nickNameSetting;
	
	private ConnectUtil mConnectUtil;
	
	private boolean isReFlushIcon;//是否刷新头像
	
	private boolean isAnimationOpen;//主菜单动画状态
	private int animationIndex;
	private MainMenu mainMenu;
	private LinearLayout settingpage;
	
	private String photoPath;
	private String cameraPath;
	private String cameraPhotoName;
	
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;
	private Button leftMenu;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		isAnimationOpen = false;
		settingpage = (LinearLayout)findViewById(R.id.settingpage);
		mainMenu = (MainMenu)findViewById(R.id.mainmenu);
		titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
		name_input = (TextView)findViewById(R.id.name_input);
		iconimage = (ImageView)findViewById(R.id.iconimage);
		seticonbtn = (Button)findViewById(R.id.seticonbtn);
		moresetting = (RelativeLayout)findViewById(R.id.moresetting);
		local_kgbtn_l = (Button)findViewById(R.id.local_kgbtn_l);
		local_kgbtn_r = (Button)findViewById(R.id.local_kgbtn_r);
		pwsetting = (RelativeLayout)findViewById(R.id.pwsetting);
		deletecache = (Button)findViewById(R.id.local_setting_clearbtn);
		about = (RelativeLayout)findViewById(R.id.about);
		feedback = (RelativeLayout)findViewById(R.id.feedback);
		updata = (Button)findViewById(R.id.local_setting_updatabtn);
		addrsetting = (RelativeLayout)findViewById(R.id.addrsetting);
		logout = (RelativeLayout)findViewById(R.id.logout);
		headSetting = (RelativeLayout)findViewById(R.id.headSetting);
		nickNameSetting =  (RelativeLayout)findViewById(R.id.nickNameSet);
		//lyb
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		leftMenu = (Button)findViewById(R.id.titlebar_leftmenu);
		leftMenu.setOnClickListener(menuClickListener);
		
		titlebar_menubutton.setOnClickListener(menuClickListener);
		name_input.setText(UserUtil.username);
//		seticonbtn.setOnClickListener(seticonClickListener);
		headSetting.setOnClickListener(seticonClickListener);
		nickNameSetting.setOnClickListener(moresettingClickListener);
		moresetting.setOnClickListener(moresettingClickListener);
		local_kgbtn_l.setOnClickListener(kgbtnLClickListener);
		local_kgbtn_r.setOnClickListener(kgbtnRClickListener);
		pwsetting.setOnClickListener(pwsettingClickListener);
		deletecache.setOnClickListener(deletecacheClickListener);
		about.setOnClickListener(aboutClickListener);
		feedback.setOnClickListener(feedbackClickListener);
		updata.setOnClickListener(updataClickListener);
		addrsetting.setOnClickListener(addrsettingClickListener);
		logout.setOnClickListener(logoutClickListener);
		isReFlushIcon = true;
		//初始化按钮
		if(CommonUtil.getMessageReceiverState(this)){
			local_kgbtn_l.setBackgroundResource(R.drawable.local_kgbtn_l_f);
			local_kgbtn_l.setTextColor(0xffffffff);
			local_kgbtn_r.setBackgroundResource(R.drawable.local_kgbtn_r);
			local_kgbtn_r.setTextColor(0xcc000000);
		}else{
			local_kgbtn_l.setBackgroundResource(R.drawable.local_kgbtn_l);
			local_kgbtn_l.setTextColor(0xcc000000);
			local_kgbtn_r.setBackgroundResource(R.drawable.local_kgbtn_r_f);
			local_kgbtn_r.setTextColor(0xffffffff);
		}
		
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
		if(isReFlushIcon){
			isReFlushIcon = false;
			name_input.setText(UserUtil.username);
			if(CommonUtil.isNetWorkOpen(this)){
				//获取用户头像
				mConnectUtil = new ConnectUtil(SettingActivity.this, mHandler,1,0);
				mConnectUtil.connect(URLUtil.URL_GET_USERICON+UserUtil.userid+".jpg", HttpThread.TYPE_IMAGE, 0);
				addProgress();
			}
		}
		if(isAnimationOpen){
			gotoMenu();
		}
	}
	
	private OnClickListener menuClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			gotoMenu();
		}
	};
	
	private OnClickListener seticonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			isReFlushIcon = false;
			Setting_MenuClick menuClick = new Setting_MenuClick(mHandler);
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, AbsCuiniaoMenu.class);
			intent.putExtra("title", PageID.SETTING_MENU_TITLE);
			intent.putExtra("items", PageID.SETTING_MENU_ITEM);
			startActivity(intent);
			AbsCuiniaoMenu.set(menuClick);
		}
	};
	
	private OnClickListener moresettingClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			isReFlushIcon = true;
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, SettingMoreActivity_A.class);
			startActivity(intent);
		}
	};

	private OnClickListener kgbtnLClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			local_kgbtn_l.setBackgroundResource(R.drawable.local_kgbtn_l_f);
			local_kgbtn_l.setTextColor(0xffffffff);
			local_kgbtn_r.setBackgroundResource(R.drawable.local_kgbtn_r);
			local_kgbtn_r.setTextColor(0xcc000000);
			//开启信息服务
			startService(new Intent(SettingActivity.this,MessageReceiveService.class));
			CommonUtil.saveMessageReceiverState(SettingActivity.this, true);
			OfflineLog.writeMainMenu_MessageButton((byte)1);//写入离线日志
		}
	};
	
	private OnClickListener kgbtnRClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			local_kgbtn_l.setBackgroundResource(R.drawable.local_kgbtn_l);
			local_kgbtn_l.setTextColor(0xcc000000);
			local_kgbtn_r.setBackgroundResource(R.drawable.local_kgbtn_r_f);
			local_kgbtn_r.setTextColor(0xffffffff);
			//关闭信息服务
			stopService(new Intent(SettingActivity.this,MessageReceiveService.class));
			CommonUtil.saveMessageReceiverState(SettingActivity.this, false);
			OfflineLog.writeMainMenu_MessageButton((byte)0);//写入离线日志
		}
	};
	
	private OnClickListener pwsettingClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(UserUtil.logintype == 0){
				Intent intent = new Intent();
				intent.setClass(SettingActivity.this, SettingPwActivity.class);
				startActivity(intent);
			}else{
				//第三方登录用户
				if(CommonUtil.getLoginName(SettingActivity.this).equals("")){
					CommonUtil.ShowToast(SettingActivity.this, "请完善资料之后再修改密码");
					Intent intent = new Intent();
					//换成新绑定Activity
					intent.setClass(SettingActivity.this, BindActivity.class);
					startActivity(intent);
				}else{
					Intent intent = new Intent();
					intent.setClass(SettingActivity.this, SettingPwActivity.class);
					startActivity(intent);
				}
//				CommonUtil.ShowToast(SettingActivity.this, "请登录或者注册成为小C的主人吧。");
			}
		}
	};
	
	private OnClickListener deletecacheClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			addProgress();
			new Thread(){
				public void run(){
					super.run();
					CommonUtil.deleteAll(new File(CommonUtil.dir_cache));
					CommonUtil.deleteAll(new File(CommonUtil.dir_media));
					mHandler.sendEmptyMessage(msg_delOver);
				}
			}.start();			
//			CommonUtil.ShowToast(SettingActivity.this, "缓存已清除");
			MySoftReference.getInstance().clear();
			OfflineLog.writeMainMenu_ClearCache();//写入离线日志
		}
	};
	
	private OnClickListener aboutClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent12 = new Intent();
			intent12.setClass(SettingActivity.this, AboutActivity.class);
			startActivity(intent12);
			OfflineLog.writeMainMenu_About();//写入离线日志
		}
	};
	
	private OnClickListener feedbackClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent13 = new Intent();
			intent13.setClass(SettingActivity.this, FeedbackActivity.class);
			startActivity(intent13);
		}
	};
	
	private OnClickListener updataClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			addProgress();
			sotfUpdate(URLUtil.URL_SOFT_UPDATA_CMMOBI,URLUtil.str_version,URLUtil.system,URLUtil.productcode,CommonUtil.getIMEI(SettingActivity.this),URLUtil.fid);
			OfflineLog.writeMainMenu_Softupdate();//写入离线日志
		}
	};
	
	private OnClickListener addrsettingClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, AddrManagerActivity.class);
			startActivity(intent);
		}
	};
	
	private OnClickListener logoutClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			isReFlushIcon = true;
			Intent intent11 = new Intent();
			intent11.setClass(SettingActivity.this, LoginAndRegeditActivity.class);
			startActivity(intent11);
		}
	};
	
	private OnClickListener name_inputClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			isReFlushIcon = true;
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, SettingMoreActivity.class);
			startActivity(intent);
		}
	};
	
	public void sotfUpdate(final String url,String version,int system,int productcode,String imei,String channelcode){
  		JSONObject jsonObject = new JSONObject();
  		try {
  			jsonObject.put("version", version);
  			jsonObject.put("system", ""+system);
  			jsonObject.put("productcode", ""+productcode);
  			jsonObject.put("imei", imei);
  			jsonObject.put("channelcode", channelcode);
  			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		final List <NameValuePair> params=new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("requestapp",jsonObject.toString()));
		
		new Thread(){
			public void run(){
				String result = CommonUtil.httpPost(url, params);
				try {
					Json(result.getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
				}
			}
		}.start();
  	}
	private final int msg_delOver = 112255;
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
					if("text/html".equalsIgnoreCase(msg.getData().getString("content_type"))){
						try {
							String tmpurl = new String((byte[])msg.obj,"UTF-8");
							if(tmpurl != null){
								mConnectUtil = new ConnectUtil(SettingActivity.this, mHandler,0);
								mConnectUtil.connect(tmpurl, HttpThread.TYPE_IMAGE, 0);
								addProgress();
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}else if("text/json".equals(msg.getData().getString("content_type"))){
						Json((byte[])msg.obj);
					}else{//图片
						setImageView((byte[])msg.obj,msg.getData().getString("mUrl"));
					}
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
//				intent1.addCategory(Intent.CATEGORY_OPENABLE);
				intent1.setType("image/*");
//				intent1.putExtra("crop", "true");
//				intent1.putExtra("aspectX", 1);
//				intent1.putExtra("aspectY", 1);
//				intent1.putExtra("outputX", CommonUtil.screen_width);
//				intent1.putExtra("outputY", CommonUtil.screen_width);
//				intent1.putExtra("return-data", true);
				startActivityForResult(intent1, 901);//相册
				break;
			case MessageID.MESSAGE_PHOTO_RESULT:
				LogPrint.Print("photo result");
				isReFlushIcon = true;
				Intent intent2 = new Intent();
				intent2.setClass(SettingActivity.this, CropActivity.class);
				intent2.putExtra("imagepath", photoPath);
				startActivity(intent2);
				break;
			case MessageID.MESSAGE_MAINMENU_OPEN:
				if(settingpage != null){
					if(settingpage.getRight() > getMainMenuAnimationPos(50)){
						settingpage.offsetLeftAndRight(animationIndex);
						settingpage.postInvalidate();
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_OPEN);
						animationIndex = -(CommonUtil.screen_width-getMainMenuAnimationPos(50))/5;
					}
				}
				break;
			case MessageID.MESSAGE_MAINMENU_CLOSE:
				if(settingpage != null){
					if(settingpage.getLeft() < 0){
						settingpage.offsetLeftAndRight(animationIndex);
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_CLOSE);
						settingpage.postInvalidate();
						animationIndex = (CommonUtil.screen_width-getMainMenuAnimationPos(50))/5;
					}
				}
				break;
			case 112233:
				closeProgress();
				buildSoftUpdataDialog(msg.getData().getString("msg"), msg.getData().getBoolean("force"), msg.getData().getString("url"),msg.getData().getInt("filesize"),msg.getData().getString("versionnumber"));
				break;
			case 112244:
				closeProgress();
				CommonUtil.ShowToast(SettingActivity.this, "小C已是最新版本。");
				break;
			//add by lyb 删除缓存
			case msg_delOver:
				closeProgress();
				CommonUtil.ShowToast(SettingActivity.this, "缓存已清除");
				break;
			}
		}
		
	};
	
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
	
	public String addUrlParam(String url,int oid){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"?oid="+oid;
	}
	
	public String addUrlParam(String url,String fid,int ver,String plaid){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		
		return url+"?fid="+fid+"&ver="+ver+"&plaid="+plaid;
	}
	
	//技术中心管理平台检查更新
  	public String addUrlParam(String url,String version,int system,int productcode,String imei,String channelcode){
  		JSONObject jsonObject = new JSONObject();
  		try {
  			jsonObject.put("version", version);
  			jsonObject.put("system", ""+system);
  			jsonObject.put("productcode", ""+productcode);
  			jsonObject.put("imei", imei);
  			jsonObject.put("channelcode", channelcode);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return url+"?requestapp="+jsonObject.toString();
  	}

	//处理相机的回调结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		LogPrint.Print("onActivityResult");
		if(resultCode == RESULT_OK){
			if(requestCode == 900){//相机
				LogPrint.Print("photoPath = "+photoPath);
			}else if(requestCode == 901&&data != null){//相册
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
		}
//		if(requestCode == 900||requestCode == 901||requestCode == 902){//相机,相册
//			photo = null;
//			if(data == null)return;
//			LogPrint.Print("requestCode = "+requestCode);
//			//1次尝试
//			photo = data.getParcelableExtra("data");
//			LogPrint.Print("photo 1 = "+photo);
//			//2次尝试
//			if (photo == null) {
//				Uri uri = data.getData();
//				if (uri != null) {
//					photo = BitmapFactory.decodeFile(uri.getPath());
//				}
//			}
//			LogPrint.Print("photo 2 = "+photo);
//			//3次尝试
//			if (photo == null) {
//				Bundle bundle = data.getExtras();
//				if (bundle != null) {
//					photo = (Bitmap) bundle.get("data");
//				} else {
//					CommonUtil.ShowToast(SettingActivity.this, "失败");
//				}
//			}
//			LogPrint.Print("photo 3 = "+photo);
//			if(photo != null){
//				if(requestCode == 901||requestCode == 902){
//					mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_PHOTO_RESULT, 1000);
//				}else if(requestCode == 900){
//					mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_CAMERA_RESULT, 1000);
//				}
//			}
//		}
	}
	
//	private Bitmap editBitmap(){
//		Bitmap result = null;
//		if(photo.getWidth() == CommonUtil.screen_width&&photo.getHeight() == CommonUtil.screen_width){
//			return photo;
//		}else{
//			result = CommonUtil.resizeImage(photo, CommonUtil.screen_width, CommonUtil.screen_width);
//			photo.recycle();
//		}
//		
//		return result;
//	}
	
	private void Json(byte[] data){
		try{
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = "+str);
			JSONObject jObject = new JSONObject(str);
			int result_type = jObject.getInt("type");
			if(result_type > 0){
				boolean force = result_type == 1?true:false;
				String msg = jObject.getString("description");
				String url = jObject.getString("path");
				int filesize = jObject.getInt("filesize");
				String versionnumber = jObject.getString("versionnumber");
				Message message = new Message();
				message.what=112233;
				Bundle bundle = new Bundle();
				bundle.putBoolean("force", force);
				bundle.putString("msg", msg);
				bundle.putString("url", url);
				bundle.putInt("filesize", filesize);
				bundle.putString("versionnumber", versionnumber);
				message.setData(bundle);
				mHandler.sendMessage(message);
			}else{
				mHandler.sendEmptyMessage(112244);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void buildSoftUpdataDialog(String str,final boolean force,final String url,final int filesize,final String versionnumber){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("软件更新").setMessage("更新内容:\n"+str).setPositiveButton("更新", new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				CommonUtil.saveSoftUpdata(SettingActivity.this, System.currentTimeMillis());
				Intent intent= new Intent();
				intent.putExtra("url", url);
				intent.putExtra("filesize", filesize);
				intent.putExtra("versionnumber", versionnumber);
				intent.setClass(SettingActivity.this, SoftUpdateActivity.class);
				startActivity(intent);
//			    intent.setAction("android.intent.action.VIEW");    
//			    Uri content_url = Uri.parse(url);   
//			    intent.setData(content_url);  
//			    startActivity(intent);
			}
		}).setNegativeButton(force?"退出":"取消", new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(force){
					CommonUtil.saveSoftUpdata(SettingActivity.this, 0);
					CommonUtil.isInMessageScreen = false;
					CommonUtil.isNormalInToApp = false;
					android.os.Process.killProcess(android.os.Process.myPid());
				}else{
					CommonUtil.saveSoftUpdata(SettingActivity.this, System.currentTimeMillis());
				}
			}
		}).setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				if(force){
					CommonUtil.saveSoftUpdata(SettingActivity.this, 0);
					CommonUtil.isInMessageScreen = false;
					CommonUtil.isNormalInToApp = false;
					android.os.Process.killProcess(android.os.Process.myPid());
				}else{
					CommonUtil.saveSoftUpdata(SettingActivity.this, System.currentTimeMillis());
				}
			}
		}).show();
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		gotoMenu();
		return true;
	}
	
	private void gotoMenu(){
		if(settingpage != null){
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
	    		settingpage.startAnimation(animation);
	    		OfflineLog.writeMainMenu();//写入离线日志
			}else{
				if(mainMenu != null){
					mainMenu.setIsCanClick(false);
				}
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.left_in);
				animation.setAnimationListener(this);
	    		animation.setFillEnabled(true);
	    		animation.setFillAfter(true);
	    		settingpage.startAnimation(animation);
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
		Button btMenu = (Button) settingpage.findViewById(R.id.titlebar_menubutton);
		if(isAnimationOpen){			
			leftMenu.setVisibility(View.VISIBLE);			
			btMenu.setClickable(false);
		}else{
			btMenu.setClickable(true);
		}
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		if(!isAnimationOpen){
			leftMenu.setVisibility(View.INVISIBLE);			
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
