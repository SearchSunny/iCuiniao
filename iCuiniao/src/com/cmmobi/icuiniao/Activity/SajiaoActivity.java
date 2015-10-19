/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.entity.AddressInfo;
import com.cmmobi.icuiniao.entity.SajiaoObj;
import com.cmmobi.icuiniao.onlineEngine.activity.WebviewActivity;
import com.cmmobi.icuiniao.ui.adapter.SajiaoAdapter;
import com.cmmobi.icuiniao.ui.view.MGridView;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

/**
 * @author hw
 *撒娇
 */
public class SajiaoActivity extends Activity{

	private ScrollView scrollView;
	private Button titlebar_backbutton;
	private TextView item1;
	private TextView item2;
	private LinearLayout litem1;
	private LinearLayout litem2;
	private MGridView button1;
	private MGridView button2;
	private EditText email_input;
	private EditText addr_input;
	private EditText phone_input;
	private EditText name_input;
	private EditText msg_input;
	private EditText num_input;
	private Button sendbtn;
	private TextView commodityname;
	private ImageView commodityimg;
	
	private TextView email_text;
	private TextView addr_text;
	private TextView phone_text;
	private TextView name_text;
	private ImageView addrmanager;
	private ImageView emailmanager;
	
	private TextView addr_text_a;
	private TextView phone_text_a;
	private TextView name_text_a;
	private TextView email_text_a;
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;
	
	private ConnectUtil mConnectUtil;
	private int commodityid;
	private int item1_id;
	private int item2_id;
	private String[] button1_array;
	private String[] button2_array;
	private boolean[] button1_state;
	private boolean[] button2_state;
	private SajiaoAdapter button1_Adapter;
	private SajiaoAdapter button2_Adapter;
	
	private String titleString;
	private String commodityImageString;
	private String commodityInfoString;
	
	private boolean isFirst;//是否地址管理里无数据
	private String addrManagerString;
	private int curAddrIndex;
	private String defaultMsgString;
	
	private int lastButton1_index;
	private int lastButton2_index;
	private String lastNum;
	private String lastEm;
	private String lastAddr;
	private String lastPhone;
	private String lastName;
	private String lastMsg;
	
	String em = "";
	String addr = "";
	String phone = "";
	String name = "";
	
	private int imageWidth;
	private int imageHeight;
	
	private SajiaoObj sa;
	private AddressInfo ad;
	
	private int needDownObj;
	
	private final int thread_sajiaoInfo = 0;
	private final int thread_modityImage = 1;
	private final int THREAD_SAJIAO_UPLOAD = 2;
	private final int THREAD_ADDRESS_UPLOAD = 3;
	private final int thread_sajiaoObjGet = 4;
	private final int thread_AddressObjGet = 5;
	
	private LinearLayout linea_address_edit;
	private LinearLayout linea_address_text;
	private RelativeLayout rela_email_edit;	
	private RelativeLayout rela_email_text;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(  
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);	
		
		setContentView(R.layout.sajiao_b);
		
		linea_address_edit = (LinearLayout)findViewById(R.id.linea_address_edit);
		linea_address_text = (LinearLayout)findViewById(R.id.linea_address_text);
		rela_email_edit = (RelativeLayout)findViewById(R.id.linea_email_edit);
		rela_email_text = (RelativeLayout)findViewById(R.id.linea_email_text);
		
		//撒娇对象
		sa =SajiaoObj.getCurrSajiaoObj(this);
		//收获地址
		ad =  AddressInfo.getCurrAddressInfo(this);	
		
		setLayout();
		
		boolean isFirst = getIntent().getBooleanExtra("isfirst", true);
		
		getIntentExtra();
		
		item1_id = -1;
		item2_id = -1;

		scrollView = (ScrollView)findViewById(R.id.scrollview);
		scrollView.setScrollbarFadingEnabled(true);		
		litem1 = (LinearLayout)findViewById(R.id.litem1);
		litem2 = (LinearLayout)findViewById(R.id.litem2);
		litem1.setVisibility(View.GONE);
		litem2.setVisibility(View.GONE);
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		item1 = (TextView)findViewById(R.id.item1);
		item2 = (TextView)findViewById(R.id.item2);
		button1 = (MGridView)findViewById(R.id.button1);
		button2 = (MGridView)findViewById(R.id.button2);
		num_input = (EditText)findViewById(R.id.num_input);
		msg_input = (EditText)findViewById(R.id.msg_input);		
		scrollView.setOnTouchListener(new View.OnTouchListener() {
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
						CommonUtil.hideSoftInput(msg_input, SajiaoActivity.this);
					}
					break;	
				}							
				return false;
			}
		});
		commodityname = (TextView)findViewById(R.id.commodityname);
		commodityimg = (ImageView)findViewById(R.id.commodityimg);
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		commodityname.setText(titleString);
		button1.setSelector(new ColorDrawable(0x00000000));
		button2.setSelector(new ColorDrawable(0x00000000));		

		if(lastNum.length() > 0){
			num_input.setText(lastNum);
		}
		if(lastMsg.length() > 0){
			msg_input.setText(lastMsg);
		}
		
		num_input.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				try {
					if(num_input.getText().length() > 0){
						if(Integer.parseInt(num_input.getText().toString()) <= 0){
							num_input.setText("1");
						}
						if(Integer.parseInt(num_input.getText().toString()) >= 100){
							num_input.setText("99");
						}
					}
				} catch (Exception e) {
					num_input.setText("1");
				}
			}
		});	
		
		getViewInlayout();
		
		sendbtn = (Button)findViewById(R.id.sendbtn);
		
		titlebar_backbutton.setOnClickListener(backClickListener);
		sendbtn.setOnClickListener(sendClickListener);
		
		boolean isNeedGetByNet = false;
		if(ad == null || sa == null){
			isNeedGetByNet = true;
		}
		//是第一次并且有一项为空，则需要联网
		if(isFirst && isNeedGetByNet){
			loadingBar = (ProgressBar)findViewById(R.id.loading);
			//add by lyb 撒娇对象或者收货地址为空时的联网获取
			mConnectUtil = new ConnectUtil(this, mHandler,0);
			if(sa == null){
				needDownObj ++;
			}
			if(ad == null){	
				needDownObj ++;
			}
			if(sa == null){
				 //需参数oid，联网架构会加
				mConnectUtil.connect(URLUtil.URL_SAJIAO_OBJ_GET, HttpThread.TYPE_PAGE, thread_sajiaoObjGet);				
			}
			if(ad == null){				
				mConnectUtil.connect(URLUtil.URL_RECEIVE_ADDR_GET, HttpThread.TYPE_PAGE, thread_AddressObjGet);				
			}		
			
		}
		
		mConnectUtil = new ConnectUtil(SajiaoActivity.this, mHandler,0);
		mConnectUtil.connect(addUrlParam1(URLUtil.URL_SAJIAO, commodityid), HttpThread.TYPE_PAGE, 0);
		new ConnectUtil(SajiaoActivity.this, mHandler,0).connect(commodityImageString, HttpThread.TYPE_IMAGE, 1);
		addProgress();
	}
	
	//获取传入的值
	private void getIntentExtra(){
		commodityid = getIntent().getExtras().getInt("commodityid");
		titleString = getIntent().getExtras().getString("title");
		commodityImageString = getIntent().getExtras().getString("commodityImageString");
		commodityInfoString = getIntent().getExtras().getString("commodityInfoString");
		
		lastButton1_index = getIntent().getExtras().getInt("button1");
		lastButton2_index = getIntent().getExtras().getInt("button2");
		lastNum = getIntent().getExtras().getString("num");
		lastEm = getIntent().getExtras().getString("em");
		lastAddr = getIntent().getExtras().getString("addr");
		lastPhone = getIntent().getExtras().getString("phone");
		lastName = getIntent().getExtras().getString("name");
		lastMsg = getIntent().getExtras().getString("msg");
	}
	//设置地址和撒娇的布局显示
	private void setLayout(){
		if(ad != null && sa != null){	
			linea_address_edit.setVisibility(View.GONE);
			linea_address_text.setVisibility(View.VISIBLE);
			rela_email_edit.setVisibility(View.GONE);
			rela_email_text.setVisibility(View.VISIBLE);
		}else if(sa == null && ad != null){			
			linea_address_edit.setVisibility(View.GONE);
			linea_address_text.setVisibility(View.VISIBLE);
			rela_email_edit.setVisibility(View.VISIBLE);
			rela_email_text.setVisibility(View.GONE);
		}else if(sa != null && ad == null){			
			linea_address_edit.setVisibility(View.VISIBLE);
			linea_address_text.setVisibility(View.GONE);
			rela_email_edit.setVisibility(View.GONE);
			rela_email_text.setVisibility(View.VISIBLE);		
		}else if(sa == null && ad == null){			
			linea_address_edit.setVisibility(View.VISIBLE);
			linea_address_text.setVisibility(View.GONE);
			rela_email_edit.setVisibility(View.VISIBLE);
			rela_email_text.setVisibility(View.GONE);
		}
	}
	//获取布局的控件
	private void getViewInlayout(){
		if(sa == null && ad == null){
			email_input = (EditText)findViewById(R.id.email_input);
			addr_input = (EditText)findViewById(R.id.addr_input);
			phone_input = (EditText)findViewById(R.id.phone_input);
			name_input = (EditText)findViewById(R.id.name_input);
			if(lastEm.length() > 0){
				email_input.setText(lastEm);
			}
			if(lastAddr.length() > 0){
				addr_input.setText(lastAddr);
			}
			if(lastPhone.length() > 0){
				phone_input.setText(lastPhone);
			}
			if(lastName.length() > 0){
				name_input.setText(lastName);
			}
			defaultMsgString = "亲，我好喜欢这个，买给我吧！";
			msg_input.setHint(defaultMsgString);
			
		}else{
			
			if(sa != null && ad != null){
				
				addrmanager = (ImageView)findViewById(R.id.addrmanager);
				emailmanager = (ImageView)findViewById(R.id.emailmanager);
				addrmanager.setOnClickListener(addrManagerClickListener);
				emailmanager.setOnClickListener(emailManagerClickListener);
				
				addr_text_a = (TextView)findViewById(R.id.addr_texta);
				phone_text_a = (TextView)findViewById(R.id.phone_texta);
				name_text_a = (TextView)findViewById(R.id.name_texta);
				email_text_a = (TextView)findViewById(R.id.email_texta);
			}else if(ad != null || sa != null){		
				
				if(ad == null){
					addr_input= (EditText)findViewById(R.id.addr_input);
					phone_input = (EditText)findViewById(R.id.phone_input);
					name_input = (EditText)findViewById(R.id.name_input);
					email_text_a = (TextView)findViewById(R.id.email_texta);
					//撒娇编辑
					emailmanager = (ImageView)findViewById(R.id.emailmanager);
					emailmanager.setOnClickListener(emailManagerClickListener);
				}else{
					addr_text_a = (TextView)findViewById(R.id.addr_texta);
					phone_text_a = (TextView)findViewById(R.id.phone_texta);
					name_text_a = (TextView)findViewById(R.id.name_texta);
					email_input = (EditText)findViewById(R.id.email_input);
					//地址编辑
					addrmanager = (ImageView)findViewById(R.id.addrmanager);
					addrmanager.setOnClickListener(addrManagerClickListener);
					
					
				}
			}
			email_text = (TextView)findViewById(R.id.email_text);
			addr_text = (TextView)findViewById(R.id.addr_text);
			phone_text = (TextView)findViewById(R.id.phone_text);
			name_text = (TextView)findViewById(R.id.name_text);
			
			
			
			
			defaultMsgString = "亲，我好喜欢这个，买给我吧！";
			msg_input.setHint(defaultMsgString);
		}
	}
	//设置控件的值
	public void setViewValue(){
		if(sa != null && ad != null){
			if(!sa.name.equals("")){
				
				email_text_a.setText(sa.name+"("+sa.email+")");
			}else{
				
				email_text_a.setText(sa.email);
			}
//			email_text_a.setTextSize(15);
			
			addr_text_a.setText(ad.addr);
//			addr_text_a.setTextSize(15);
			
			phone_text_a.setText(ad.phone);
//			phone_text_a.setTextSize(15);
			
			name_text_a.setText(ad.name);
			name_text_a.setTextSize(15);
			
//			email_text.setText("对方邮箱    ");
//			addr_text.setText( "地        址  ");
//			phone_text.setText("手        机  ");
//			name_text.setText( "姓        名  ");
			//defaultMsgString = ad.name+": 亲，我好喜欢这个，买给我吧！";
			msg_input.setHint(defaultMsgString);
		}else if(sa != null && ad == null){
//			email_text.setText("对方邮箱   ");
//			addr_text.setText( "地        址  ");
//			phone_text.setText("手        机  ");
//			name_text.setText( "姓        名  ");
			if(!sa.name.equals("")){
				
				email_text_a.setText(sa.name+"("+sa.email+")");
			}else{
				
				email_text_a.setText(sa.email);
			}
			email_text_a.setTextSize(15);			
			msg_input.setHint(defaultMsgString);
		}else if(ad != null && sa == null){
			addr_text_a.setText(ad.addr);
			addr_text_a.setTextSize(15);
			
			phone_text_a.setText(ad.phone);
			phone_text_a.setTextSize(15);
			
			name_text_a.setText(ad.name);
			name_text_a.setTextSize(15);
			
//			addr_text.setText("地        址  ");
//			phone_text.setText("手        机  ");
//			name_text.setText("姓        名  ");
			//defaultMsgString = ad.name+": 亲，我好喜欢这个，买给我吧！";
			msg_input.setHint(defaultMsgString);
			
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
				//撒娇对象
				sa =SajiaoObj.getCurrSajiaoObj(this);
				//收获地址
				ad =  AddressInfo.getCurrAddressInfo(this);
				setViewValue();
				
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	//地址管理
	private OnClickListener addrManagerClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
				Intent intent = new Intent();
//				intent.putExtra("addressInfo", AddressInfo.getCurrAddressInfo(SajiaoActivity.this));
//				intent.putExtra("index", CommonUtil.getCurSajiaoObj(SajiaoActivity.this));
//				intent.setClass(SajiaoActivity.this, AddAddressActivityA.class);
				intent.setClass(SajiaoActivity.this, AddrManagerActivityA.class);
				startActivity(intent);
		}
	};
	//邮箱管理
	private OnClickListener emailManagerClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			Intent intent = new Intent();
//			intent.putExtra("SajiaoObj", SajiaoObj.getCurrSajiaoObj(SajiaoActivity.this));
//			intent.putExtra("index", CommonUtil.getCurReceiveAddr(SajiaoActivity.this));
//			intent.setClass(SajiaoActivity.this, AddSajiaoObjActivity.class);
			intent.setClass(SajiaoActivity.this, SajiaoObjMgrActivity.class);
			startActivity(intent);
		}
	};
	
	private OnClickListener backClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			WebviewActivity.isSajiaoSuccess = false;
			finish();
		}
		
	};
	
	private OnItemClickListener button1ItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if(button1_state != null){
				lastButton1_index = arg2;
				for(int i = 0;i < button1_state.length;i ++){
					if(i == arg2)continue;
					button1_state[i] = false;
				}
				button1_state[arg2] = !button1_state[arg2];
				button1_Adapter.notifyDataSetChanged();
			}
		}
	};
	
	private OnItemClickListener button2ItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if(button2_state != null){
				lastButton2_index = arg2;
				for(int i = 0;i < button2_state.length;i ++){
					if(i == arg2)continue;
					button2_state[i] = false;
				}
				button2_state[arg2] = !button2_state[arg2];
				button2_Adapter.notifyDataSetChanged();
			}
		}
	};
	
	private OnClickListener sendClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			if(imm != null){
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			if(UserUtil.isRemoteLogin){			
				Intent intent = new Intent();
				intent.setClass(SajiaoActivity.this,
						LoginAndRegeditActivity.class);
				SajiaoActivity.this.startActivity(intent);
				return;
			}
			send();
		}
		
	};
	
	private void send(){
		boolean isSelected = false;
		String item1 = "";
		String item2 = "";
		String button1 = "";
		String button2 = "";
		em = "";
		addr = "";
		phone = "";
		name = "";
		String msg = "";
		String num = "";
		if(button1_state != null){
			for(int i = 0;i < button1_state.length;i ++){
				if(button1_state[i]){
					item1 = this.item1.getText().toString().trim()+"#"+item1_id;
					button1 = button1_array[i].trim();
					isSelected = true;
					break;
				}
			}
			if(isSelected == false){
				CommonUtil.ShowToast(SajiaoActivity.this, "您还未选择"+this.item1.getText().toString().trim());
				return;
			}
		}
		isSelected = false;
		if(button2_state != null){
			for(int i = 0;i < button2_state.length;i ++){
				if(button2_state[i]){
					item2 = this.item2.getText().toString().trim()+"#"+item2_id;
					button2 = button2_array[i].trim();
					isSelected = true;
					break;
				}
			}
			if(isSelected == false){
				CommonUtil.ShowToast(SajiaoActivity.this, "您还未选择"+this.item2.getText().toString().trim());
				return;
			}
		}
		if(sa == null && ad == null){
			
			addr = addr_input.getText().toString().trim();
			if(addr.length() <= 0){
				CommonUtil.ShowToast(SajiaoActivity.this, "主人您可能填错地址了！");
				return;
			}
			phone = phone_input.getText().toString().trim();
			if(phone_input.getText().toString().trim().length() != 11||!phone_input.getText().toString().trim().startsWith("1")){
				CommonUtil.ShowToast(SajiaoActivity.this, "主人您把手机号填错了吧？");
				return;
			}
			name = name_input.getText().toString().trim();
			if(name.length() <= 0){
				CommonUtil.ShowToast(SajiaoActivity.this, "主人您要闹哪样？名字都会填错？");
				return;
			}
			em = email_input.getText().toString().trim();
			if(em.length() <= 0||em.indexOf("@") < 0||em.indexOf(".") < 0){
				CommonUtil.ShowToast(SajiaoActivity.this, "主人您可能填错邮箱了！");
				return;
			}
			
			//撒娇对象上传
			String url = addUrlParam(URLUtil.URL_SAJIAO_OBJ_UPLOAD, "",  em, "-1");
			mConnectUtil = new ConnectUtil(this, mHandler,0); //需参数oid，联网架构会加
			mConnectUtil.connect(url, HttpThread.TYPE_PAGE, THREAD_SAJIAO_UPLOAD);
			
			
			//收货地址上传
			String urladd = addUrlParam(URLUtil.URL_RECEIVE_ADDR_UPLOAD, name, addr, phone, "-1");
			mConnectUtil = new ConnectUtil(this, mHandler,0); //需参数oid，联网架构会加
			mConnectUtil.connect(urladd, HttpThread.TYPE_PAGE, THREAD_ADDRESS_UPLOAD);
			
		}
		else if(sa != null && ad == null){
			addr = addr_input.getText().toString().trim();
			if(addr.length() <= 0){
				CommonUtil.ShowToast(SajiaoActivity.this, "主人您可能填错地址了！");
				return;
			}
			phone = phone_input.getText().toString().trim();
			if(phone_input.getText().toString().trim().length() != 11||!phone_input.getText().toString().trim().startsWith("1")){
				CommonUtil.ShowToast(SajiaoActivity.this, "主人您把手机号填错了吧？");
				return;
			}
			name = name_input.getText().toString().trim();
			if(name.length() <= 0){
				CommonUtil.ShowToast(SajiaoActivity.this, "主人您要闹哪样？名字都会填错？");
				return;
			}			
			em  = email_text_a.getText().toString().trim();
			if(em.indexOf("(") > -1){
				em = em.substring(em.indexOf("(")+1, em.lastIndexOf(")"));
			}			
			addr = addr_input.getText().toString().trim();
			phone = phone_input.getText().toString().trim();
			name = name_input.getText().toString().trim();
			
			//收货地址上传
			String urladd = addUrlParam(URLUtil.URL_RECEIVE_ADDR_UPLOAD, name, addr, phone, "-1");
			mConnectUtil = new ConnectUtil(this, mHandler,0); //需参数oid，联网架构会加
			mConnectUtil.connect(urladd, HttpThread.TYPE_PAGE, THREAD_ADDRESS_UPLOAD);
			
		}else if(ad != null && sa == null){
			em = email_input.getText().toString().trim();
			if(em.length() <= 0||em.indexOf("@") < 0||em.indexOf(".") < 0){
				CommonUtil.ShowToast(SajiaoActivity.this, "主人您可能填错邮箱了！");
				return;
			}
			em  = email_input.getText().toString().trim();
			if(em.indexOf("(") > -1){
				em = em.substring(em.indexOf("(")+1, em.lastIndexOf(")"));
			}			
			addr = addr_text_a.getText().toString().trim();
			phone = phone_text_a.getText().toString().trim();
			name = name_text_a.getText().toString().trim();
			
			//撒娇对象上传
			String url = addUrlParam(URLUtil.URL_SAJIAO_OBJ_UPLOAD, "",  em, "-1");
			mConnectUtil = new ConnectUtil(this, mHandler,0); //需参数oid，联网架构会加
			mConnectUtil.connect(url, HttpThread.TYPE_PAGE, THREAD_SAJIAO_UPLOAD);
		}else{
			
			em  = email_text_a.getText().toString().trim();
			if(em.indexOf("(") > -1){
				em = em.substring(em.indexOf("(")+1, em.lastIndexOf(")"));
			}			
			addr = addr_text_a.getText().toString().trim();
			phone = phone_text_a.getText().toString().trim();
			name = name_text_a.getText().toString().trim();
			
		}
		
		if(msg_input.getText().toString().trim().length() <= 0){
			msg = defaultMsgString;
		}else{
			msg = msg_input.getText().toString().trim();
		}
		num = num_input.getText().toString().trim();
		if(num.length() <= 0){
			num_input.setText("1");
			num = "1";
		}else{
			if(Integer.parseInt(num) >= 100){
				num_input.setText("99");
				num = "99";
			}
		}

		final List <NameValuePair> params=new ArrayList<NameValuePair>();		
		params.add(new BasicNameValuePair("commodityid",commodityid+""));
		params.add(new BasicNameValuePair("item1", item1));
		params.add(new BasicNameValuePair("item2", item2));
		params.add(new BasicNameValuePair("button1", button1));
		params.add(new BasicNameValuePair("button2", button2));
		params.add(new BasicNameValuePair("oid", UserUtil.userid+""));
		params.add(new BasicNameValuePair("em", CommonUtil.toUrlEncode(em) ));
		params.add(new BasicNameValuePair("addr",CommonUtil.toUrlEncode(addr)));
		params.add(new BasicNameValuePair("phone",CommonUtil.toUrlEncode(phone)));
		params.add(new BasicNameValuePair("name", CommonUtil.toUrlEncode(name)));
		params.add(new BasicNameValuePair("msg", CommonUtil.toUrlEncode(msg)));
		params.add(new BasicNameValuePair("num",CommonUtil.toUrlEncode(num)));
		params.add(new BasicNameValuePair("deviceid",CommonUtil.toUrlEncode(CommonUtil.getIMEI(this)) ));
		params.add(new BasicNameValuePair("ver",URLUtil.version+""));
		params.add(new BasicNameValuePair("network_type",CommonUtil.getApnType(this)));
		params.add(new BasicNameValuePair("plaid", URLUtil.plaid));
		new Thread() {
			public void run() {
				try {
					String result = CommonUtil.httpPost(URLUtil.URL_SAJIAO_UPLOAD,
							params);
					if (result != null) {
						Message msg = new Message();
						msg.arg1 = 1;
						msg.what = msg_post_sajiao;
						msg.obj = result.getBytes("utf-8");
						mHandler.sendMessage(msg);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		
//		mConnectUtil = new ConnectUtil(SajiaoActivity.this, mHandler,0);
//		mConnectUtil.connect(addUrlParam(URLUtil.URL_SAJIAO_UPLOAD, UserUtil.userid, commodityid, item1, item2, button1, button2, em, addr, phone, name, msg,num), HttpThread.TYPE_PAGE, 1);
		addProgress();
	}
	
	private String addUrlParam(String url, String name, String email, String id){
		if(url.indexOf("?") > 0){
			return url+"&name="+name  + "&email="+ email + "&id="+ id;
		}
		return url+"?name="+name  + "&email="+ email + "&id="+ id;
	}
	
	private String addUrlParam(String url, String name, String addr, String phone, String id){
		if(url.indexOf("?") > 0){
			return url+"&name="+name +"&addr="+ addr + "&phone="+ phone + "&id="+ id;
		}
		return url+"?name="+name +"&addr="+ addr + "&phone="+ phone + "&id="+ id;
	}
	
	private int getObjCount;
	private final int msg_post_sajiao = 12345;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				if(mConnectUtil != null){
					if("text/json".equals(msg.getData().getString("content_type"))){
						closeProgress();
						if(msg.arg1 == 0 || msg.arg1 == 1){ //页面信息
							byte[] data = (byte[])msg.obj;
							Json(data, msg.arg1);
							
						}else if(msg.arg1 <= 3){ //2,3 上传
							Json(msg);
						}else{   //4,5  获取撒娇对象和收货地址
							if(msg.arg1 == thread_sajiaoObjGet){
								JsonGetSajiaoObj((byte[])msg.obj);
								getObjCount ++;
							}else if(msg.arg1 == thread_AddressObjGet){
								JsonGetADDList((byte[])msg.obj);
								getObjCount ++;
							}
							if(needDownObj == getObjCount){
							mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
							}
						}
						
					}else{
						closeProgress();
						byte[] data = (byte[])msg.obj;
						Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
						temp = setImageSize(temp);
						commodityimg.setBackgroundDrawable(new BitmapDrawable(getCoverBitmap(temp)));
					}
				}
				break;
			case MessageID.MESSAGE_CONNECT_LAYOUTOVER:				
				flushPage();				
				break;
			case MessageID.MESSAGE_CONNECT_START:
				addProgress();
				break;
			case MessageID.MESSAGE_CONNECT_ERROR:
				closeProgress();
				CommonUtil.ShowToast(getApplicationContext(), (String)msg.obj);
				getObjCount ++;
				if(needDownObj == getObjCount){
					flushPage();
				}
				break;
			case msg_post_sajiao:
				byte[] data = (byte[])msg.obj;
				Json(data, msg.arg1);
				closeProgress();
				break;
			}
		}
		
	};
	
	private void flushPage(){
//		Intent intent = new Intent();
//		intent.putExtra("isfirst", false);
//		intent.putExtra("commodityid", commodityid);
//		intent.putExtra("title", titleString);
//		intent.putExtra("commodityImageString", commodityImageString);
//		intent.putExtra("commodityInfoString", commodityInfoString);
//		intent.putExtra("button1", lastButton1_index);
//		intent.putExtra("button2", lastButton2_index);
//		intent.putExtra("num", lastNum);
//		intent.putExtra("em", lastEm);
//		intent.putExtra("addr", lastAddr);
//		intent.putExtra("phone", lastPhone);
//		intent.putExtra("name", lastName);
//		intent.putExtra("msg", lastMsg);
//		intent.setClass(SajiaoActivity.this, SajiaoActivity.class);
//		SajiaoActivity.this.startActivity(intent);
//		finish();
		//撒娇对象
		sa =SajiaoObj.getCurrSajiaoObj(this);
		//收获地址
		ad =  AddressInfo.getCurrAddressInfo(this);
		setLayout();
		getViewInlayout();
		setViewValue();
	}
	
	//获取收货地址
	private void JsonGetSajiaoObj(byte[] data) {
		try {
			LogPrint.Print("==========Json");
			String str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("sajiao","json = " + str);			
			CommonUtil.saveSajiaoObjMgr(this, str);	

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//获取收货地址
	private void JsonGetADDList(byte[] data) {
		try {
			LogPrint.Print("==========Json");
			String str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("sajiao","json = " + str);			
			CommonUtil.saveReceiveAddrMgr(this, str);		

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void Json(Message msg){
		try {
			byte[] data = (byte[])msg.obj;
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = "+str);
			if(str.length() > 0){
				if(str.indexOf("true") > 0){
					JSONObject jsonObj = new JSONObject(str);
					boolean result = jsonObj.getBoolean("result");
					String id = jsonObj.getString("id");
					
					if(msg.arg1 == THREAD_ADDRESS_UPLOAD){
						
						addResult(result, msg, id);
					}else if(msg.arg1 == THREAD_SAJIAO_UPLOAD){
						
						addSajiaoResult(result, msg, id);
					}
					
					
					
				}				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void Json(byte[] data,int type){
		try {
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			str = str.replace(",null", "");
			LogPrint.Print("message","json = "+str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			if(result != null){
				if(result.equalsIgnoreCase("true")){
					if(type == 0){//获取数据
						String item1,item2,button1,button2;
						try {
							item1 = jObject.getString("item1");
						} catch (Exception e) {
							item1 = null;
							litem1.setVisibility(View.GONE);
						}
						try {
							button1 = jObject.getString("button1");
						} catch (Exception e) {
							button1 = null;
						}
						try {
							item2 = jObject.getString("item2");
						} catch (Exception e) {
							item2 = null;
							litem2.setVisibility(View.GONE);
						}
						try {
							button2 = jObject.getString("button2");
						} catch (Exception e) {
							button2 = null;
						}
						String[] tmps=null;
						//将文本赋给item1并保存id
						if(item1 != null){
							litem1.setVisibility(View.VISIBLE);
							tmps = item1.split("#");
							this.item1.setText(tmps[0].trim());
							item1_id = Integer.parseInt(tmps[1]);
						}
						//将文本赋给item2并保存id
						if(item2 != null){
							litem2.setVisibility(View.VISIBLE);
							tmps = item2.split("#");
							this.item2.setText(tmps[0].trim());
							item2_id = Integer.parseInt(tmps[1]);
						}
						//给button1_array赋值
						button1_array = null;
						if(button1 != null){
							button1_array = button1.split("#");
						}
						//给button2_array赋值
						button2_array = null;
						if(button2 != null){
							button2_array = button2.split("#");
						}
						//初始化gridview
						if(button1_array != null){
							button1_state = new boolean[button1_array.length];
							if(lastButton1_index != -1){
								button1_state[lastButton1_index] = true;
							}
							button1_Adapter = new SajiaoAdapter(button1_array, button1_state, SajiaoActivity.this);
							this.button1.setAdapter(button1_Adapter);
							this.button1.setOnItemClickListener(button1ItemClickListener);
						}
						if(button2_array != null){
							button2_state = new boolean[button2_array.length];
							if(lastButton2_index != -1){
								button2_state[lastButton2_index] = true;
							}
							button2_Adapter = new SajiaoAdapter(button2_array, button2_state, SajiaoActivity.this);
							this.button2.setAdapter(button2_Adapter);
							this.button2.setOnItemClickListener(button2ItemClickListener);
						}
					}else if(type == 1){
						CommonUtil.ShowToast(SajiaoActivity.this, "发送成功!");
						/*if(isFirst){
							isFirst = false;
							String addrManagerString = "";
							JSONArray jsonArray = new JSONArray();
							JSONObject jsonObject1 = new JSONObject();
							jsonObject1.put("result", true);
							jsonObject1.put("email", em);
							jsonObject1.put("addr", addr);
							jsonObject1.put("phone", phone);
							jsonObject1.put("name", name);
							jsonArray.put(jsonObject1);
							addrManagerString = jsonArray.toString();
							LogPrint.Print("addrmanager = "+addrManagerString);
							CommonUtil.saveAddrManager(SajiaoActivity.this, addrManagerString);
							CommonUtil.saveCurAddr(SajiaoActivity.this, 0);
						}*/
						WebviewActivity.isSajiaoSuccess = true;
						finish();
					}
				}else{
					if(type == 1){
						
						String msg=null;
						try {
							msg = jObject.getString("msg");
						} catch (Exception e) {
						}
						if(msg != null){
							CommonUtil.ShowToast(SajiaoActivity.this, "发送失败!\n"+msg);
						}else{
							CommonUtil.ShowToast(SajiaoActivity.this, "发送失败!");
						}
					}
					
				}
				/*else{
					if(type == 0){
						item1 = null;
						litem1.setVisibility(View.GONE);
						item2 = null;
						litem2.setVisibility(View.GONE);
						LogPrint.Print("获取数据失败!");
					}else if(type == 1){
						String msg=null;
						try {
							msg = jObject.getString("msg");
						} catch (Exception e) {
						}
						if(msg != null){
							CommonUtil.ShowToast(SajiaoActivity.this, "发送失败!\n"+msg);
						}else{
							CommonUtil.ShowToast(SajiaoActivity.this, "发送失败!");
						}
					}
				}*/
			}else{
				CommonUtil.ShowToast(SajiaoActivity.this, "失败!");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public String addUrlParam1(String url,int commodityid){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"?commodityid="+commodityid;
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
	
	public String addUrlParam(String url, int oid, int commodityid,
			String item1, String item2, String button1, String button2,
			String em, String addr, String phone, String name, String msg, String num) {
		if (URLUtil.IsLocalUrl()) {
			return url;
		}
		
		return url + "?oid=" + oid + "&commodityid=" + commodityid + "&item1="
				+ CommonUtil.toUrlEncode(item1)+ "&item2="
				+ CommonUtil.toUrlEncode(item2)+ "&button1="
				+ CommonUtil.toUrlEncode(button1)+ "&button2="
				+ CommonUtil.toUrlEncode(button2)+ "&em="
				+ CommonUtil.toUrlEncode(em)+ "&addr="
				+ CommonUtil.toUrlEncode(addr)+ "&phone="
				+ CommonUtil.toUrlEncode(phone)+ "&name="
				+ CommonUtil.toUrlEncode(name)+ "&num="
				+ num+ "&msg="
				+ CommonUtil.toUrlEncode(msg);
	}
	
	/**
	 * 图片加边框
	 * @param src
	 * @return
	 */
	private Bitmap getCoverBitmap(Bitmap src){
		
		Bitmap bmp = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);
    	Canvas canvas = new Canvas();
    	Paint paint = new Paint();
    
    	canvas.setBitmap(bmp);
    	canvas.drawBitmap(src, 0, 0, null);
    	//绘制边框
    	paint.setColor(0xffacacac);
    	paint.setStyle(Style.STROKE);
    	canvas.drawRect(0,0, src.getWidth()-1, src.getHeight()-1, paint);
    	canvas.save();
    	return bmp;
	}
	/**
	 * 缩放图片大小
	 * @param src
	 */
	public Bitmap setImageSize(Bitmap src){
		Bitmap bmp = null;
		int dpi = getResources().getDisplayMetrics().densityDpi;
		if(dpi <= 120){//qvga 240x320
			bmp = CommonUtil.resizeImage(src, 60, 60);
		}else if(dpi <= 160){//hvga 320x480
			bmp = CommonUtil.resizeImage(src, 95, 95);
		}else if(dpi <= 320){//wvga 480x800
			bmp = CommonUtil.resizeImage(src, 140, 140);
			if(CommonUtil.screen_width >= 540&&CommonUtil.screen_height >= 960){ //540x960
				bmp = CommonUtil.resizeImage(src, 160, 160);
			}else if(CommonUtil.screen_height >= 1280){
				bmp = CommonUtil.resizeImage(src, 217, 217);
				if(CommonUtil.screen_width >= 800){
					bmp = CommonUtil.resizeImage(src, 244, 244);
				}
			}
		}else{//更大屏幕分辨率
			bmp = CommonUtil.resizeImage(src, 160, 160);
		}
		return bmp;
	}
	
	
	//上传撒娇邮箱
	private void addSajiaoResult(boolean result, Message msg, String id) {
		try {
			if (result) {
				// 上传成功，则更新本地json字符串
				String url = msg.getData().getString("mUrl");
				String name = CommonUtil.getSubString(url, "name=", "&email=");				
				String email = CommonUtil.getSubString(url, "&email=", "&id=");
				JSONArray jsonArray = new JSONArray();
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", id);				
				jsonObject.put("name", name);
				jsonObject.put("email", email);
				jsonArray.put(jsonObject);
				String temp = CommonUtil.getSajiaoObjMgr(this);
				String addrSajiaoMgr;
				if (temp.length() <= 0) {
					addrSajiaoMgr = jsonArray.toString();
				} else {
					addrSajiaoMgr = (jsonArray.toString()).substring(0,
							(jsonArray.toString()).length() - 1)
							+ "," + temp.substring(1, temp.length());
				}
				CommonUtil.saveSajiaoObjMgr(this, addrSajiaoMgr);				
				finish();
			} else {
				CommonUtil.ShowToast(this, "上传失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//上传收货地址
	private void addResult(boolean result, Message msg, String id) {
		try {
			if (result) {
				// 上传成功，则更新本地json字符串
				String url = msg.getData().getString("mUrl");
				String name = CommonUtil.getSubString(url, "name=", "&addr=");
				String addr = CommonUtil.getSubString(url, "&addr=", "&phone=");
				String phone = CommonUtil.getSubString(url, "&phone=", "&id=");
				JSONArray jsonArray = new JSONArray();
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", id);
				jsonObject.put("addr", addr);
				jsonObject.put("phone", phone);
				jsonObject.put("name", name);
				jsonArray.put(jsonObject);
				String temp = CommonUtil.getReceiveAddrMgr(this);
				String addrManager;
				if (temp.length() <= 0) {
					addrManager = jsonArray.toString();
				} else {
					addrManager = (jsonArray.toString()).substring(0,
							(jsonArray.toString()).length() - 1)
							+ "," + temp.substring(1, temp.length());
				}
				CommonUtil.saveReceiveAddrMgr(this, addrManager);				
				finish();
			} else {
				CommonUtil.ShowToast(this, "上传失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
