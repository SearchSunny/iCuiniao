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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.menuclick.Setting_MenuClick;
import com.cmmobi.icuiniao.menuclick.Setting_More_Education_MenuClick;
import com.cmmobi.icuiniao.menuclick.Setting_More_Money_MenuClick;
import com.cmmobi.icuiniao.menuclick.Setting_More_Occuption_MenuClick;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.PageID;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

/**
 * @author hw
 *
 */
public class SettingMoreActivity extends Activity{

	private Button titlebar_backbutton;
	private Button titlebar_menubutton;
	
	private EditText name_input;
	private ImageView iconimage;
	private Button seticonbtn;
	private EditText realname_input;
	private EditText email_input;
	private EditText area_input;
	private EditText addr_input;
	private EditText phone_input;
	
	private Button education_btn;//教育程度
	private Button money_btn;//月收入
	private Button unmarrybtn;
	private Button marrybtn;
	private Button occupation_btn;//职业
	private Button interest_btn;//兴趣
	private Button genderbtn_l;//女
	private Button genderbtn_r;//男
	private Button birthday_btn;
	
	private ConnectUtil mConnectUtil;
	private boolean isReFlushIcon;//是否刷新用户头像
	
	private String educationString;
	private String moneyString;
	private String marryString;
	private String occupationString;
	private String interestString;
	private String genderString;
	private String birthdayString;
	
	private int _year, _month, _day;
	private Calendar c;
	
	private String photoPath;
	private String cameraPath;
	private String cameraPhotoName;
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;
	private ScrollView scrollview;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_more);
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
		getWindow().setSoftInputMode(  
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		c = Calendar.getInstance();
        _year = c.get(Calendar.YEAR);
        _month = c.get(Calendar.MONTH);
        _day = c.get(Calendar.DAY_OF_MONTH);
		
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
		name_input = (EditText)findViewById(R.id.name_input);
		iconimage = (ImageView)findViewById(R.id.iconimage);
		seticonbtn = (Button)findViewById(R.id.seticonbtn);
		realname_input = (EditText)findViewById(R.id.realname_input);
		email_input = (EditText)findViewById(R.id.email_input);
		genderbtn_l = (Button)findViewById(R.id.genderbtn_l);
		genderbtn_r = (Button)findViewById(R.id.genderbtn_r);
		birthday_btn = (Button)findViewById(R.id.birthday_btn);
		area_input = (EditText)findViewById(R.id.area_input);
		addr_input = (EditText)findViewById(R.id.addr_input);
		phone_input = (EditText)findViewById(R.id.phone_input);
		education_btn = (Button)findViewById(R.id.education_btn);
		money_btn = (Button)findViewById(R.id.money_btn);
		unmarrybtn = (Button)findViewById(R.id.unmarrybtn);
		marrybtn = (Button)findViewById(R.id.marrybtn);
		occupation_btn = (Button)findViewById(R.id.occupation_btn);
		interest_btn = (Button)findViewById(R.id.interest_btn);
		//lyb
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		
		isReFlushIcon = true;
		educationString = "";
		moneyString = "";
		marryString = "未婚";
		occupationString = "";
		interestString = "";
		genderString = "女";
		birthdayString = "";
		
		if(CommonUtil.getRealName(SettingMoreActivity.this).length() > 0){
			realname_input.setText(CommonUtil.getRealName(SettingMoreActivity.this));
		}
		if(CommonUtil.getEmail(SettingMoreActivity.this).length() > 0){
			email_input.setText(CommonUtil.getEmail(SettingMoreActivity.this));
		}
		if(CommonUtil.getBirthday(SettingMoreActivity.this).length() > 0){
			birthdayString = CommonUtil.getBirthday(SettingMoreActivity.this);
			birthday_btn.setText(birthdayString);
		}
		if(CommonUtil.getArea(SettingMoreActivity.this).length() > 0){
			area_input.setText(CommonUtil.getArea(SettingMoreActivity.this));
		}
		if(CommonUtil.getAddr(SettingMoreActivity.this).length() > 0){
			addr_input.setText(CommonUtil.getAddr(SettingMoreActivity.this));
		}
		if(CommonUtil.getPhone(SettingMoreActivity.this).length() > 0){
			phone_input.setText(CommonUtil.getPhone(SettingMoreActivity.this));
		}
		if(CommonUtil.getEducation(SettingMoreActivity.this).length() > 0){
			educationString = CommonUtil.getEducation(SettingMoreActivity.this);
			education_btn.setText(educationString);
		}
		if(CommonUtil.getMoney(SettingMoreActivity.this).length() > 0){
			moneyString = CommonUtil.getMoney(SettingMoreActivity.this);
			money_btn.setText(moneyString);
		}
		if(CommonUtil.getMarry(SettingMoreActivity.this).length() > 0){
			marryString = CommonUtil.getMarry(SettingMoreActivity.this);
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
		if(CommonUtil.getOccuption(SettingMoreActivity.this).length() > 0){
			occupationString = CommonUtil.getOccuption(SettingMoreActivity.this);
			occupation_btn.setText(occupationString);
		}
		if(CommonUtil.getInterest(SettingMoreActivity.this).length() > 0){
			interestString = CommonUtil.getInterest(SettingMoreActivity.this);
			interest_btn.setText(interestString);
		}
		
		titlebar_backbutton.setOnClickListener(backClickListener);
		titlebar_menubutton.setOnClickListener(sendClickListener);
		name_input.setText(UserUtil.username);
		seticonbtn.setOnClickListener(seticonClickListener);
		if(UserUtil.gender != -1){
			genderString = UserUtil.gender == 0?"男":"女";
			if(genderString.indexOf("女") >= 0){
				genderbtn_l.setBackgroundResource(R.drawable.local_kgbtn_l_f);
				genderbtn_r.setBackgroundResource(R.drawable.local_kgbtn_r);
				genderbtn_l.setTextColor(0xffffffff);
				genderbtn_r.setTextColor(0xcc000000);
			}else{
				genderbtn_l.setBackgroundResource(R.drawable.local_kgbtn_l);
				genderbtn_r.setBackgroundResource(R.drawable.local_kgbtn_r_f);
				genderbtn_l.setTextColor(0xcc000000);
				genderbtn_r.setTextColor(0xffffffff);
			}
		}
		education_btn.setOnClickListener(educationClickListener);
		money_btn.setOnClickListener(moneyClickListener);
		unmarrybtn.setOnClickListener(unmarryClickListener);
		marrybtn.setOnClickListener(marryClickListener);
		occupation_btn.setOnClickListener(occupationClickListener);
		interest_btn.setOnClickListener(interestClickListener);
		genderbtn_l.setOnClickListener(genderbtn_lClickListener);
		genderbtn_r.setOnClickListener(genderbtn_rClickListener);
		birthday_btn.setOnClickListener(birthdayClickListener);
		scrollview = (ScrollView)findViewById(R.id.scrollview);
		scrollview.setOnTouchListener(new View.OnTouchListener() {
			float x = 0, y = 0;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_DOWN:
					x = event.getX();
					y = event.getY();
					break;
				case MotionEvent.ACTION_UP:
					float moveX = Math.abs(x - event.getX());
					float moveY = Math.abs(y - event.getY());
					float ratio = CommonUtil.screen_width / 480f;
					if(moveX < 15 * ratio && moveY < 15 * ratio){
						CommonUtil.hideSoftInput(realname_input, SettingMoreActivity.this);
					}
					break;	
				}							
				return false;
			}
		});
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
			if(UserUtil.gender == 0){
				genderbtn_l.setBackgroundResource(R.drawable.local_kgbtn_l);
				genderbtn_r.setBackgroundResource(R.drawable.local_kgbtn_r_f);
				genderbtn_l.setTextColor(0xcc000000);
				genderbtn_r.setTextColor(0xffffffff);
			}else if(UserUtil.gender == 1){
				genderbtn_l.setBackgroundResource(R.drawable.local_kgbtn_l_f);
				genderbtn_r.setBackgroundResource(R.drawable.local_kgbtn_r);
				genderbtn_l.setTextColor(0xffffffff);
				genderbtn_r.setTextColor(0xcc000000);
			}
			if(CommonUtil.isNetWorkOpen(this)){
				//获取用户头像
				mConnectUtil = new ConnectUtil(SettingMoreActivity.this, mHandler,1,0);
				mConnectUtil.connect(URLUtil.URL_GET_USERICON+UserUtil.userid+".jpg", HttpThread.TYPE_IMAGE, 0);
				addProgress();
			}
		}
	}
	
	public void finish(){
		CommonUtil.saveRealName(SettingMoreActivity.this, realname_input.getText().toString().trim());
		CommonUtil.saveEmail(SettingMoreActivity.this, email_input.getText().toString().trim());
		CommonUtil.saveBirthday(SettingMoreActivity.this, birthdayString);
		CommonUtil.saveArea(SettingMoreActivity.this, area_input.getText().toString().trim());
		CommonUtil.saveAddr(SettingMoreActivity.this, addr_input.getText().toString().trim());
		CommonUtil.savePhone(SettingMoreActivity.this, phone_input.getText().toString().trim());
		CommonUtil.saveEducation(SettingMoreActivity.this, educationString);
		CommonUtil.saveMoney(SettingMoreActivity.this, moneyString);
		CommonUtil.saveMarry(SettingMoreActivity.this, marryString);
		CommonUtil.saveOccuption(SettingMoreActivity.this, occupationString);
		CommonUtil.saveInterest(SettingMoreActivity.this, interestString);
		super.finish();
	}
	
	private OnClickListener backClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	};
	
	private OnClickListener sendClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String name = "";
			String realName = "";
			String email = "";
			String gender = "";
			String birthday = "";
			String area = "";
			String addr = "";
			String phone = "";
			if(name_input.getText().length() > 0){
				name = name_input.getText().toString().trim();
			}
			if(realname_input.getText().length() > 0){
				realName = realname_input.getText().toString().trim();
			}
			if(email_input.getText().length() > 0){
				if(email_input.getText().toString().indexOf("@") > 0&&email_input.getText().toString().indexOf(".") > 0){
					email = email_input.getText().toString().trim();
				}else{
					CommonUtil.ShowToast(SettingMoreActivity.this, "邮箱格式不正确");
					return;
				}
			}
			if(area_input.getText().length() > 0){
				area = area_input.getText().toString().trim();
			}
			if(addr_input.getText().length() > 0){
				addr = addr_input.getText().toString().trim();
			}
			if(phone_input.getText().length() > 0){
				phone = phone_input.getText().toString().trim();
				if(!phone.equals("")){
					//获取第一位
					int firstPhone = Integer.parseInt(phone.substring(0,1));
					//手机长度
					int phoneLength = phone.length();
					if(phoneLength!=11||firstPhone!=1){
						CommonUtil.ShowToast(SettingMoreActivity.this, "主人您把手机号填错了吧?");
						return;
					}
				}
			}
			
			mConnectUtil = new ConnectUtil(SettingMoreActivity.this, mHandler,0);
			mConnectUtil.connect(addUrlParam(URLUtil.URL_SETTING_MORE, UserUtil.userid,name,realName,email,genderString,birthdayString,area,addr,phone,educationString,moneyString,marryString,occupationString,interestString), HttpThread.TYPE_PAGE, 0);
		}
	};
	
	private OnClickListener seticonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			isReFlushIcon = false;
			Setting_MenuClick menuClick = new Setting_MenuClick(mHandler);
			Intent intent = new Intent();
			intent.setClass(SettingMoreActivity.this, AbsCuiniaoMenu.class);
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
			intent.setClass(SettingMoreActivity.this, AbsCuiniaoMenu.class);
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
			intent.setClass(SettingMoreActivity.this, AbsCuiniaoMenu.class);
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
			intent.setClass(SettingMoreActivity.this, AbsCuiniaoMenu.class);
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
			intent.setClass(SettingMoreActivity.this, InterestActivity.class);
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
                birthday_btn.setText(birthdayString);
            }

        }, _year, _month, _day);
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
				if(mConnectUtil != null){
					if("text/json".equals(msg.getData().getString("content_type"))){
						Json((byte[])msg.obj);
					}else{
						if("text/html".equalsIgnoreCase(msg.getData().getString("content_type"))){
							try {
								String tmpurl = new String((byte[])msg.obj,"UTF-8");
								if(tmpurl != null){
									mConnectUtil = new ConnectUtil(SettingMoreActivity.this, mHandler,0);
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
				intent2.setClass(SettingMoreActivity.this, CropActivity.class);
				intent2.putExtra("imagepath", photoPath);
				startActivity(intent2);
				break;
			case MessageID.MESSAGE_MENUCLICK_SETTING_MORE_EDUCATION:
				educationString = PageID.SETTING_MORE_EDUCATION_MENU_ITEM[msg.arg1];
				education_btn.setText(PageID.SETTING_MORE_EDUCATION_MENU_ITEM[msg.arg1]);
				break;
			case MessageID.MESSAGE_MENUCLICK_SETTING_MORE_MONEY:
				moneyString = PageID.SETTING_MORE_MONEY_MENU_ITEM[msg.arg1];
				money_btn.setText(PageID.SETTING_MORE_MONEY_MENU_ITEM[msg.arg1]);
				break;
			case MessageID.MESSAGE_MENUCLICK_SETTING_MORE_OCCUPTION:
				occupationString = PageID.SETTING_MORE_OCCUPTION_MENU_ITEM[msg.arg1];
				occupation_btn.setText(PageID.SETTING_MORE_OCCUPTION_MENU_ITEM[msg.arg1]);
				break;
			case MessageID.MESSAGE_FLUSH_PAGE://保存用户数据
				if(interestString.length() > 0){
					interest_btn.setText(interestString);
				}
				CommonUtil.saveRealName(SettingMoreActivity.this, realname_input.getText().toString().trim());
				CommonUtil.saveEmail(SettingMoreActivity.this, email_input.getText().toString().trim());
				CommonUtil.saveBirthday(SettingMoreActivity.this, birthdayString);
				CommonUtil.saveArea(SettingMoreActivity.this, area_input.getText().toString().trim());
				CommonUtil.saveAddr(SettingMoreActivity.this, addr_input.getText().toString().trim());
				CommonUtil.savePhone(SettingMoreActivity.this, phone_input.getText().toString().trim());
				CommonUtil.saveEducation(SettingMoreActivity.this, educationString);
				CommonUtil.saveMoney(SettingMoreActivity.this, moneyString);
				CommonUtil.saveMarry(SettingMoreActivity.this, marryString);
				CommonUtil.saveOccuption(SettingMoreActivity.this, occupationString);
				CommonUtil.saveInterest(SettingMoreActivity.this, interestString);
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
	
	public String addUrlParam(String url, int oid, String name,
			String realname, String email, String gender, String birthday,
			String area, String addr, String phone, String education,
			String money, String marry, String occupation, String interest) {
		if (URLUtil.IsLocalUrl()) {
			return url;
		}
		return url + "?oid=" + oid + "&name=" + CommonUtil.toUrlEncode(name)
				+ "&realname=" + CommonUtil.toUrlEncode(realname)
				+ "&email=" + CommonUtil.toUrlEncode(email)
				+ "&gender=" + CommonUtil.toUrlEncode(gender)
				+ "&birthday=" + CommonUtil.toUrlEncode(birthday)
				+ "&area=" + CommonUtil.toUrlEncode(area)
				+ "&addr=" + CommonUtil.toUrlEncode(addr)
				+ "&phone=" + CommonUtil.toUrlEncode(phone)
				+ "&education=" + CommonUtil.toUrlEncode(education)
				+ "&money=" + CommonUtil.toUrlEncode(money)
				+ "&marry=" + CommonUtil.toUrlEncode(marry)
				+ "&occupation=" + CommonUtil.toUrlEncode(occupation)
				+ "&interest=" + CommonUtil.toUrlEncode(interest);
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
//					CommonUtil.ShowToast(SettingMoreActivity.this, "失败");
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
//		}else if(requestCode == 903){
//			interestString = data.getExtras().getString("result");
//			mHandler.sendEmptyMessage(MessageID.MESSAGE_FLUSH_PAGE);
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
						CommonUtil.saveUserId(SettingMoreActivity.this, UserUtil.userid);
						CommonUtil.saveUserName(SettingMoreActivity.this, UserUtil.username);
						UserUtil.logintype = 0;
						CommonUtil.saveLoginType(SettingMoreActivity.this, UserUtil.logintype);
						CommonUtil.saveGender(SettingMoreActivity.this, UserUtil.gender);
						UserUtil.userState = 1;
						CommonUtil.saveUserState(SettingMoreActivity.this, UserUtil.userState);
						CommonUtil.ShowToast(SettingMoreActivity.this, "主人，您的资料更新成功。");
						
						mHandler.sendEmptyMessage(MessageID.MESSAGE_FLUSH_PAGE);
					}
				}else{
					String msg = jObject.getString("msg");
					if(msg != null){
						CommonUtil.ShowToast(SettingMoreActivity.this, "更新失败鸟，\n"+msg);
					}else{
						CommonUtil.ShowToast(SettingMoreActivity.this, "更新失败鸟");
					}
				}
			}
		}catch(Exception e){
			
		}
	}
	
}
