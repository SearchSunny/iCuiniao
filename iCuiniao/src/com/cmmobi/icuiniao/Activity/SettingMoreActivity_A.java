/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
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
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.menuclick.Setting_MenuClick;
import com.cmmobi.icuiniao.menuclick.Setting_More_Education_MenuClick;
import com.cmmobi.icuiniao.menuclick.Setting_More_Money_MenuClick;
import com.cmmobi.icuiniao.menuclick.Setting_More_Occuption_MenuClick;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.PageID;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

/**
 * 个人信息设置
 * @author hw
 *
 */
public class SettingMoreActivity_A extends Activity{

	private Button titlebar_backbutton;
//	private Button titlebar_menubutton;
	
	private ImageView iconimage;
	private EditText realname_input;
	private EditText phoneNumberEditText;
	private EditText emailEditText;
	private EditText addressEditText;
	private RelativeLayout education_btn;//教育程度
	private RelativeLayout money_btn;//月收入
	private Button unmarrybtn;
	private Button marrybtn;
	private RelativeLayout occupation_btn;//职业
	private RelativeLayout interest_btn;//兴趣
	private Button genderbtn_l;//女
	private Button genderbtn_r;//男
	private RelativeLayout birthday_btn;
	
	private ConnectUtil mConnectUtil;
	private boolean isReFlushIcon;//是否刷新用户头像
	
	private String educationString;
	private String moneyString;
	private String marryString;
	private String occupationString;
	private String interestString;
	private String genderString;
	private String birthdayString;
	private String phoneNumber = "";
	private String email = "";
	private String address = "";	
	private int _year, _month, _day;
	private Calendar c;
	
	private String photoPath;
	private String cameraPath;
	private String cameraPhotoName;
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;
	private ScrollView scrollview;
	
	private TextView birthdayStr;
	private TextView occupationStr;
	private TextView interestStr;
	private TextView moneyStr;
	private TextView educationStr;
	//是否已完善信息
	private boolean isUserInfo;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_more_a);
		
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
		getWindow().setSoftInputMode(  
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
//		titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
		
		birthday_btn = (RelativeLayout)findViewById(R.id.birthday_btn);
		occupation_btn = (RelativeLayout)findViewById(R.id.occupation_btn);
		interest_btn = (RelativeLayout)findViewById(R.id.interest_btn);
		money_btn = (RelativeLayout)findViewById(R.id.money_btn);
		education_btn = (RelativeLayout)findViewById(R.id.education_btn);
		//真实姓名
		realname_input = (EditText)findViewById(R.id.realname_input);
		
		unmarrybtn = (Button)findViewById(R.id.unmarrybtn);
		marrybtn = (Button)findViewById(R.id.marrybtn);
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		//生日
		birthdayStr = (TextView)findViewById(R.id.birthday_str);
		//职业
		occupationStr = (TextView)findViewById(R.id.occupation_str);
		//兴趣
		interestStr = (TextView)findViewById(R.id.interest_str);
		//电话
		phoneNumberEditText = (EditText)findViewById(R.id.phoneNumberEditText);
		//电子邮箱
		emailEditText = (EditText)findViewById(R.id.emailEditText);
		//联系方式
		addressEditText = (EditText)findViewById(R.id.addressEditText);
		//月收入
		moneyStr = (TextView)findViewById(R.id.money_str);
		//教育程度
		educationStr = (TextView)findViewById(R.id.education_str);
		
		
		titlebar_backbutton.setOnClickListener(backClickListener);
//		titlebar_menubutton.setOnClickListener(sendClickListener);
		birthday_btn.setOnClickListener(birthdayClickListener);
		occupation_btn.setOnClickListener(occupationClickListener);
		interest_btn.setOnClickListener(interestClickListener);
		money_btn.setOnClickListener(moneyClickListener);
		education_btn.setOnClickListener(educationClickListener);
		unmarrybtn.setOnClickListener(unmarryClickListener);
		marrybtn.setOnClickListener(marryClickListener);		
        
        isReFlushIcon = true;
		educationString = "";
		moneyString = "";
		marryString = "未婚";
		occupationString = "";
		interestString = "";
		birthdayString = "";
		
		if(CommonUtil.getRealName(SettingMoreActivity_A.this).length() > 0){
			realname_input.setText(CommonUtil.getRealName(SettingMoreActivity_A.this));
		}
		if(CommonUtil.getBirthday(SettingMoreActivity_A.this).length() > 0){
			birthdayString = CommonUtil.getBirthday(SettingMoreActivity_A.this);
			birthdayStr.setText(birthdayString);
			try {
				String[] birInfo = birthdayString.split("-");
				_year = Integer.parseInt(birInfo[0]);
				_month = Integer.parseInt(birInfo[1])-1 ;
				_day = Integer.parseInt(birInfo[2]);
			} catch (Exception e) {
				e.printStackTrace();
				c = Calendar.getInstance();
		        _year = c.get(Calendar.YEAR);
		        _month = c.get(Calendar.MONTH);
		        _day = c.get(Calendar.DAY_OF_MONTH);
			}
			
		}else{
			//取得当前日期
			c = Calendar.getInstance();
	        _year = c.get(Calendar.YEAR);
	        _month = c.get(Calendar.MONTH);
	        _day = c.get(Calendar.DAY_OF_MONTH);
		}
		if(CommonUtil.getEducation(SettingMoreActivity_A.this).length() > 0){
			educationString = CommonUtil.getEducation(SettingMoreActivity_A.this);
			educationStr.setText(educationString);
		}
		if(CommonUtil.getMoney(SettingMoreActivity_A.this).length() > 0){
			moneyString = CommonUtil.getMoney(SettingMoreActivity_A.this);
			moneyStr.setText(moneyString);
		}
		
		if(CommonUtil.getMarry(SettingMoreActivity_A.this).length() > 0){
			marryString = CommonUtil.getMarry(SettingMoreActivity_A.this);
			if(marryString.indexOf("未婚") >= 0){
				unmarrybtn.setBackgroundResource(R.drawable.local_kgbtn_l_f);
				marrybtn.setBackgroundResource(R.drawable.local_kgbtn_r);
				unmarrybtn.setTextColor(0xffffffff);
				marrybtn.setTextColor(0xcc000000);
			}else{
				unmarrybtn.setBackgroundResource(R.drawable.local_kgbtn_l);
				marrybtn.setBackgroundResource(R.drawable.local_kgbtn_r_f);
				unmarrybtn.setTextColor(0xcc000000);
				marrybtn.setTextColor(0xffffffff);
			}
		}
		
		if(CommonUtil.getOccuption(SettingMoreActivity_A.this).length() > 0){
			occupationString = CommonUtil.getOccuption(SettingMoreActivity_A.this);
			occupationStr.setText(occupationString);
		}
		if(CommonUtil.getInterest(SettingMoreActivity_A.this).length() > 0){
			interestString = CommonUtil.getInterest(SettingMoreActivity_A.this);
			interestStr.setText(interestString);
		}else{
			
			interestStr.setText("");
		}
		if(CommonUtil.getPhone(SettingMoreActivity_A.this).length() >0){
			phoneNumber = CommonUtil.getPhone(SettingMoreActivity_A.this);
			phoneNumberEditText.setText(phoneNumber);
		}
		if(CommonUtil.getEmail(SettingMoreActivity_A.this).length() >0){
			email = CommonUtil.getEmail(SettingMoreActivity_A.this);
			emailEditText.setText(email);
		}
		if(CommonUtil.getAddr(SettingMoreActivity_A.this).length() >0){
			address = CommonUtil.getAddr(SettingMoreActivity_A.this);
			addressEditText.setText(address);
		}
		OfflineLog.writeSettingOther();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
	}
	
	public void finish(){
		super.finish();
	}
	
	
	
	private OnClickListener backClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//Intent intent = new Intent();
			sendInfo();
			
		}
	};
	
	private void sendInfo(){
		if(isFinishUserInfo()){
			
			isUserInfo = true;
			CommonUtil.saveIsUserInfo(SettingMoreActivity_A.this, isUserInfo);
		}
		else{
			isUserInfo = false;
			CommonUtil.saveIsUserInfo(SettingMoreActivity_A.this, isUserInfo);
		}
		String realName = "";
		if(realname_input.getText().length() > 0){
			realName = realname_input.getText().toString().trim();
		}
		if(phoneNumberEditText.getText().length() > 0){
			phoneNumber = phoneNumberEditText.getText().toString().trim();
			
		}
		if(emailEditText.getText().length() > 0){
			email = emailEditText.getText().toString().trim();
		}
		if(addressEditText.getText().length() > 0){
			address = addressEditText.getText().toString().trim();
		}
		mConnectUtil = new ConnectUtil(SettingMoreActivity_A.this, mHandler,0);
		mConnectUtil.connect(addUrlParam(URLUtil.Url_USER_OTHER_SET, UserUtil.userid, realName, birthdayString,educationString,moneyString,marryString,occupationString,
				interestString,phoneNumber,email,address), HttpThread.TYPE_PAGE, 10);
	}
	
//	private OnClickListener sendClickListener = new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
////			String name = UserUtil.username;
//			String realName = "";
////			String email = "";
////			String gender = "";
////			String birthday = "";
////			String area = "";
////			String addr = "";
////			String phone = "";
//			if(realname_input.getText().length() > 0){
//				realName = realname_input.getText().toString().trim();
//			}
//			if(phoneNumberEditText.getText().length() > 0){
//				phoneNumber = phoneNumberEditText.getText().toString().trim();
//				
//			}
//			if(emailEditText.getText().length() > 0){
//				email = emailEditText.getText().toString().trim();
//			}
//			if(addressEditText.getText().length() > 0){
//				address = addressEditText.getText().toString().trim();
//			}
//			mConnectUtil = new ConnectUtil(SettingMoreActivity_A.this, mHandler,0);
//			mConnectUtil.connect(addUrlParam(URLUtil.Url_USER_OTHER_SET, UserUtil.userid, realName, birthdayString,educationString,moneyString,marryString,occupationString,interestString,phoneNumber,email,address), HttpThread.TYPE_PAGE, 0);
//		}
//	};
	
	private OnClickListener seticonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			isReFlushIcon = false;
			Setting_MenuClick menuClick = new Setting_MenuClick(mHandler);
			Intent intent = new Intent();
			intent.setClass(SettingMoreActivity_A.this, AbsCuiniaoMenu.class);
			intent.putExtra("title", PageID.SETTING_MENU_TITLE);
			intent.putExtra("items", PageID.SETTING_MENU_ITEM);
			startActivity(intent);
			AbsCuiniaoMenu.set(menuClick);
		}
	};
	
	private OnClickListener educationClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			isReFlushIcon = false;
			Setting_More_Education_MenuClick menuClick = new Setting_More_Education_MenuClick(mHandler);
			Intent intent = new Intent();
			intent.setClass(SettingMoreActivity_A.this, AbsCuiniaoMenu.class);
			intent.putExtra("title", PageID.SETTING_MORE_EDUCATION_MENU_TITLE);
			intent.putExtra("items", PageID.SETTING_MORE_EDUCATION_MENU_ITEM);
			startActivity(intent);
			AbsCuiniaoMenu.set(menuClick);
		}
	};
	
	private OnClickListener moneyClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			isReFlushIcon = false;
			Setting_More_Money_MenuClick menuClick = new Setting_More_Money_MenuClick(mHandler);
			Intent intent = new Intent();
			intent.setClass(SettingMoreActivity_A.this, AbsCuiniaoMenu.class);
			intent.putExtra("title", PageID.SETTING_MORE_MONEY_MENU_TITLE);
			intent.putExtra("items", PageID.SETTING_MORE_MONEY_MENU_ITEM);
			startActivity(intent);
			AbsCuiniaoMenu.set(menuClick);
		}
	};
	
	private OnClickListener unmarryClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			unmarrybtn.setBackgroundResource(R.drawable.local_kgbtn_l_f);
			marrybtn.setBackgroundResource(R.drawable.local_kgbtn_r);
			unmarrybtn.setTextColor(0xffffffff);
			marrybtn.setTextColor(0xcc000000);
			marryString = "未婚";
		}
	};
	
	private OnClickListener marryClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			unmarrybtn.setBackgroundResource(R.drawable.local_kgbtn_l);
			marrybtn.setBackgroundResource(R.drawable.local_kgbtn_r_f);
			unmarrybtn.setTextColor(0xcc000000);
			marrybtn.setTextColor(0xffffffff);
			marryString = "已婚";
		}
	};
	
	private OnClickListener genderbtn_lClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			genderbtn_l.setBackgroundResource(R.drawable.local_kgbtn_l_f);
			genderbtn_r.setBackgroundResource(R.drawable.local_kgbtn_r);
			genderbtn_l.setTextColor(0xffffffff);
			genderbtn_r.setTextColor(0xcc000000);
			genderString = "女";
		}
	};
	
	private OnClickListener genderbtn_rClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			genderbtn_l.setBackgroundResource(R.drawable.local_kgbtn_l);
			genderbtn_r.setBackgroundResource(R.drawable.local_kgbtn_r_f);
			genderbtn_l.setTextColor(0xcc000000);
			genderbtn_r.setTextColor(0xffffffff);
			genderString = "男";
		}
	};
	
	private OnClickListener occupationClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			isReFlushIcon = false;
			Setting_More_Occuption_MenuClick menuClick = new Setting_More_Occuption_MenuClick(mHandler);
			Intent intent = new Intent();
			intent.setClass(SettingMoreActivity_A.this, AbsCuiniaoMenu.class);
			intent.putExtra("title", PageID.SETTING_MORE_OCCUPTION_MENU_TITLE);
			intent.putExtra("items", PageID.SETTING_MORE_OCCUPTION_MENU_ITEM);
			startActivity(intent);
			AbsCuiniaoMenu.set(menuClick);
		}
	};
	
	private OnClickListener interestClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			isReFlushIcon = false;
			Intent intent = new Intent();
			intent.setClass(SettingMoreActivity_A.this, InterestActivity.class);
			intent.putExtra("interestStr", interestString);
			startActivityForResult(intent, 903);
		}
	};
	
	private OnClickListener birthdayClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			showDialog(0);
		}
	};
	
	protected Dialog onCreateDialog(int id) {
		return new DatePickerDialog(this, new OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year,
                    int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                _year = year;
                _month = monthOfYear;
                _day = dayOfMonth;
                birthdayString = _year+"-"+(_month+1)+"-"+_day;
                birthdayStr.setText(birthdayString);
            }

        }, _year, _month, _day);
	}
	
	private void postFinish(int threadIdx){
		if(threadIdx == 10){
			mHandler.postDelayed(new Runnable(){

				@Override
				public void run() {
					finish();								
				}
				
			}, 100);
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
					if("text/json".equals(msg.getData().getString("content_type"))){
						Json((byte[])msg.obj);
						postFinish(msg.arg1);
					}else{
						if("text/html".equalsIgnoreCase(msg.getData().getString("content_type"))){
							try {
								String tmpurl = new String((byte[])msg.obj,"UTF-8");
								if(tmpurl != null){
									mConnectUtil = new ConnectUtil(SettingMoreActivity_A.this, mHandler,0);
									mConnectUtil.connect(tmpurl, HttpThread.TYPE_IMAGE, 0);
									addProgress();
								}
							} catch (Exception e) {
								// TODO: handle exception
							}
						}else{//图片
							setImageView((byte[])msg.obj,msg.getData().getString("mUrl"));
						}
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
				isReFlushIcon = true;
				Intent intent2 = new Intent();
				intent2.setClass(SettingMoreActivity_A.this, CropActivity.class);
				intent2.putExtra("imagepath", photoPath);
				startActivity(intent2);
				break;
			case MessageID.MESSAGE_MENUCLICK_SETTING_MORE_EDUCATION:
				educationString = PageID.SETTING_MORE_EDUCATION_MENU_ITEM[msg.arg1];
				educationStr.setText(PageID.SETTING_MORE_EDUCATION_MENU_ITEM[msg.arg1]);
				break;
			case MessageID.MESSAGE_MENUCLICK_SETTING_MORE_MONEY:
				moneyString = PageID.SETTING_MORE_MONEY_MENU_ITEM[msg.arg1];
				moneyStr.setText(PageID.SETTING_MORE_MONEY_MENU_ITEM[msg.arg1]);
				break;
			case MessageID.MESSAGE_MENUCLICK_SETTING_MORE_OCCUPTION:
				occupationString = PageID.SETTING_MORE_OCCUPTION_MENU_ITEM[msg.arg1];
				occupationStr.setText(PageID.SETTING_MORE_OCCUPTION_MENU_ITEM[msg.arg1]);
				break;
			case MessageID.MESSAGE_FLUSH_PAGE://保存用户数据
				if(interestString.length() > 0){
					interestStr.setText(interestString);
				}else{
					
					interestStr.setText("");
				}
				CommonUtil.saveRealName(SettingMoreActivity_A.this, realname_input.getText().toString().trim());
				//CommonUtil.saveEmail(SettingMoreActivity_A.this, email_input.getText().toString().trim());
				CommonUtil.saveBirthday(SettingMoreActivity_A.this, birthdayString);
				//CommonUtil.saveArea(SettingMoreActivity_A.this, area_input.getText().toString().trim());
				//CommonUtil.saveAddr(SettingMoreActivity_A.this, addr_input.getText().toString().trim());
				//CommonUtil.savePhone(SettingMoreActivity_A.this, phone_input.getText().toString().trim());
				CommonUtil.saveEducation(SettingMoreActivity_A.this, educationString);
				CommonUtil.saveMoney(SettingMoreActivity_A.this, moneyString);
				CommonUtil.saveMarry(SettingMoreActivity_A.this, marryString);
				CommonUtil.saveOccuption(SettingMoreActivity_A.this, occupationString);
				CommonUtil.saveInterest(SettingMoreActivity_A.this, interestString);
				CommonUtil.savePhone(SettingMoreActivity_A.this, phoneNumber);
				CommonUtil.saveAddr(SettingMoreActivity_A.this, address);
				CommonUtil.saveEmail(SettingMoreActivity_A.this, email);				
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
	
	public String addUrlParam(String url, int oid, 
			String realname,  String birthday,
			String education,
			String money, String marry, String occupation, String interest,String phoneNumber,String email,String address) {
		if (URLUtil.IsLocalUrl()) {
			return url;
		}
		return url + "?oid=" + oid
				+ "&realname=" + CommonUtil.toUrlEncode(realname)			
				+ "&birthday=" + CommonUtil.toUrlEncode(birthday)			
				+ "&education=" + CommonUtil.toUrlEncode(education)
				+ "&money=" + CommonUtil.toUrlEncode(money)
				+ "&marry=" + CommonUtil.toUrlEncode(marry)
				+ "&occupation=" + CommonUtil.toUrlEncode(occupation)
				+ "&interest=" + CommonUtil.toUrlEncode(interest)
				+ "&phone=" + CommonUtil.toUrlEncode(phoneNumber)
				+ "&email=" + CommonUtil.toUrlEncode(email)
				+ "&addr=" + CommonUtil.toUrlEncode(address);
	}

	//处理相机的回调结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		LogPrint.Print("onActivityResult:"+requestCode);
		if (requestCode == 903) {
			interestString = data.getExtras().getString("result");
			LogPrint.Print("interest = " + interestString);
			mHandler.sendEmptyMessage(MessageID.MESSAGE_FLUSH_PAGE);
		} else {
			if (resultCode == RESULT_OK) {
				if (requestCode == 900) {// 相机
					LogPrint.Print("photoPath = " + photoPath);
				} else if (requestCode == 901 && data != null) {// 相册
					Uri selectImage = data.getData();
					String[] proj = { MediaStore.Images.Media.DATA };
					Cursor cursor = getContentResolver().query(selectImage,
							proj, null, null, null);
					cursor.moveToFirst();
					int columnIndex = cursor.getColumnIndex(proj[0]);
					photoPath = cursor.getString(columnIndex);
					LogPrint.Print("photoPath = " + photoPath);
					cursor.close();
				}

				if (photoPath != null) {
					mHandler.sendEmptyMessage(MessageID.MESSAGE_PHOTO_RESULT);
				}
			}
		}
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
			String result = jObject.getString("result");
			if(result != null){
				if(result.equalsIgnoreCase("true")){
					String oid = jObject.getString("oid");
					String name = jObject.getString("name");
					String gender = jObject.getString("gender");
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
						CommonUtil.saveUserId(SettingMoreActivity_A.this, UserUtil.userid);
						CommonUtil.saveUserName(SettingMoreActivity_A.this, UserUtil.username);
						UserUtil.logintype = 0;
						CommonUtil.saveLoginType(SettingMoreActivity_A.this, UserUtil.logintype);
						CommonUtil.saveGender(SettingMoreActivity_A.this, UserUtil.gender);
						UserUtil.userState = 1;
						CommonUtil.saveUserState(SettingMoreActivity_A.this, UserUtil.userState);
						CommonUtil.ShowToast(SettingMoreActivity_A.this, "主人，您的资料更新成功。");
						
						mHandler.sendEmptyMessage(MessageID.MESSAGE_FLUSH_PAGE);
						
					}
				}else{
					String msg = jObject.getString("msg");
					if(msg != null){
						CommonUtil.ShowToast(SettingMoreActivity_A.this, "更新失败鸟，\n"+msg);
					}else{
						CommonUtil.ShowToast(SettingMoreActivity_A.this, "更新失败鸟");
					}
					
				}
			}
		}catch(Exception e){
			
		}
	}
	
	
	//判断用户是否完善信息
	public boolean isFinishUserInfo(){
		if(realname_input.getText().toString().length() == 0 || birthdayStr.getText().toString().equals("")
		 || interestStr.getText().toString().equals("") || occupationStr.getText().toString().equals("") 
		 || moneyStr.getText().toString().equals("") || educationStr.getText().toString().equals("")
		 || phoneNumberEditText.getText().toString().equals("")||emailEditText.getText().toString().equals("")
		 || addressEditText.getText().toString().equals("")){
			
			return false;
			
		}else{
			
			return true;
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			sendInfo();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
}
