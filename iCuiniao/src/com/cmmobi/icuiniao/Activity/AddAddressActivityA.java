package com.cmmobi.icuiniao.Activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.entity.AddressInfo;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

public class AddAddressActivityA extends Activity{
	private ProgressBar loadingBar;
	private ScrollView scrollview;
	private Button addbtn;
	private EditText addr_input;
	private EditText phone_input;
	private EditText name_input;
	private Button titlebar_backbutton;
	private TextView titlebar_titletext;
	private ConnectUtil mConnectUtil;
	
	private AddressInfo addressInfo = null;
	private int editIndex = -1;
	private boolean isEdit;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address_a);
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		addressInfo = (AddressInfo)getIntent().getSerializableExtra("addressInfo");
		editIndex = getIntent().getIntExtra("index", -1);
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		addbtn = (Button)findViewById(R.id.addbtn);
		addbtn.setOnClickListener(sendClickListener);
		addr_input = (EditText)findViewById(R.id.addr_input);
		phone_input = (EditText)findViewById(R.id.phone_input);
		name_input = (EditText)findViewById(R.id.name_input);
		scrollview = (ScrollView)findViewById(R.id.scrollview);
		scrollview.setOnTouchListener(touchListener);
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		titlebar_backbutton.setOnClickListener(backClickListener);
		titlebar_titletext = (TextView)findViewById(R.id.titlebar_titletext);
		//index=-1 添加，否则是编辑
		if(addressInfo != null){
			addbtn.setBackgroundResource(R.drawable.save_addrbtn_0);
			titlebar_titletext.setText("编辑收货地址");
			addr_input.setText(addressInfo.addr);
			phone_input.setText(addressInfo.phone);
			name_input.setText(addressInfo.name);
			isEdit = true;
		}
	}
	
	private View.OnTouchListener touchListener = new View.OnTouchListener() {		
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
					CommonUtil.hideSoftInput(addr_input, AddAddressActivityA.this);
				}
				break;	
			}							
			return false;
		}
	};
	
	private OnClickListener backClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {			
			finish();
		}
	};
	
	private OnClickListener sendClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {			
			send();
		}
	};
	
	private void send(){
		String addr = addr_input.getText().toString().trim();
		if(addr.length() <= 0){
			CommonUtil.ShowToast(AddAddressActivityA.this, "主人您可能填错地址了！");
			return;
		}
		String phone = phone_input.getText().toString().trim();
		if(phone_input.getText().toString().trim().length() != 11||!phone_input.getText().toString().trim().startsWith("1")){
			CommonUtil.ShowToast(AddAddressActivityA.this, "主人您把手机号填错了吧？");
			return;
		}
		String name = name_input.getText().toString().trim();
		if(name.length() <= 0){
			CommonUtil.ShowToast(AddAddressActivityA.this, "主人您要闹哪样？名字都会填错？");
			return;
		}
		String tempAddrManager = CommonUtil.getReceiveAddrMgr(this);
		boolean isSame = false;
		ArrayList<AddressInfo> tempList = new ArrayList<AddressInfo>();
		if(tempAddrManager.length() > 0){
			tempList = AddressInfo.changeStrToList(tempAddrManager);
		}
		for (int i = 0; i < tempList.size(); i++) {
			AddressInfo addrInfo = tempList.get(i);
			if (addrInfo.addr.equals(addr) && addrInfo.phone.equals(phone)
					&& addrInfo.name.equals(name)) {
				isSame = true;
				break;
			}
		}
		if (isSame) {
			CommonUtil.ShowToast(AddAddressActivityA.this,
					"主人，这个地址已经有了，不用再写一遍了，汗！");
			return;
		}
		
		//添加。id传-1
		String id = "-1";
		if(isEdit){
			id = addressInfo.id;
		}
		String url = addUrlParam(URLUtil.URL_RECEIVE_ADDR_UPLOAD, name, addr, phone, id);
		mConnectUtil = new ConnectUtil(this, mHandler,0); //需参数oid，联网架构会加
		mConnectUtil.connect(url, HttpThread.TYPE_PAGE, 0);
	}
	
	private String addUrlParam(String url, String name, String addr, String phone, String id){
		if(url.indexOf("?") > 0){
			return url+"&name="+name +"&addr="+ addr + "&phone="+ phone + "&id="+ id;
		}
		return url+"?name="+name +"&addr="+ addr + "&phone="+ phone + "&id="+ id;
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
				Json(msg);
				closeProgress();				
				break;
			}
		}
	};
	
	private void Json( Message msg){
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
					if(!isEdit){
						addResult(result, msg, id);
					}else{
						modifyResult(result, msg, id);
					}
				}				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void modifyResult(boolean result, Message msg, String id){
		try {
			if (result) {
				// 上传成功，则更新本地json字符串
				String url = msg.getData().getString("mUrl");
				String name = CommonUtil.getSubString(url, "name=", "&addr=");
				String addr = CommonUtil.getSubString(url, "&addr=", "&phone=");
				String phone = CommonUtil.getSubString(url, "&phone=", "&id=");
				AddressInfo newAddrInfo = new AddressInfo();
				newAddrInfo.name = name;
				newAddrInfo.addr = addr;
				newAddrInfo.phone = phone;
				newAddrInfo.id = id;
				String temp = CommonUtil.getReceiveAddrMgr(this);
				ArrayList<AddressInfo> arrAddress = AddressInfo.changeStrToList(temp);
				arrAddress.set(editIndex, newAddrInfo);
				String addrManager = AddressInfo.changeListToString(arrAddress);
				CommonUtil.saveReceiveAddrMgr(this, addrManager);
				finish();
			} else {
				CommonUtil.ShowToast(this, "修改失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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
	
	public void addProgress() {
		if (loadingBar != null) {
			loadingBar.setVisibility(View.VISIBLE);
		}
	}
	
	public void closeProgress(){
		if(loadingBar != null){
			loadingBar.setVisibility(View.INVISIBLE);
		}
	}
}
