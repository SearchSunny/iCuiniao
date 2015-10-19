/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
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

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;

/**
 * @author hw
 *
 */
public class AddAddressActivity extends Activity{

	private Button titlebar_backbutton;

	private EditText email_input;
	private EditText addr_input;
	private EditText phone_input;
	private EditText name_input;

	private Button addbtn;
	
	private String addrManager;//地址管理数据,jsonArray
	private int curAddr;//当前选中的地址
	private JSONArray jsonArray;
	private ArrayList<Addr> list;
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;
	private ScrollView scrollview;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		list = null;
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		titlebar_backbutton.setOnClickListener(backClickListener);	
		
		email_input = (EditText)findViewById(R.id.email_input);
		addr_input = (EditText)findViewById(R.id.addr_input);
		phone_input = (EditText)findViewById(R.id.phone_input);
		name_input = (EditText)findViewById(R.id.name_input);

		addbtn = (Button)findViewById(R.id.addbtn);
		addbtn.setOnClickListener(sendClickListener);
		curAddr = 0;
		
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		//lyb
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
						CommonUtil.hideSoftInput(email_input, AddAddressActivity.this);
					}
					break;	
				}							
				return false;
			}
		});		
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
				Json((byte[])msg.obj);
				break;

			case 1001://上传成功	
				closeProgress();
				CommonUtil.ShowToast(AddAddressActivity.this, "保存成功");
				CommonUtil.saveAddrManager(AddAddressActivity.this, addrManager);
				LogPrint.Print("=====1001=====curAddr" + curAddr);
				CommonUtil.saveCurAddr(AddAddressActivity.this, curAddr);
				Intent intent = new Intent();			
				intent.setClass(AddAddressActivity.this, AddrManagerActivity.class);
				startActivity(intent);				
				finish();
				break;
			case 1002://上传失败
				closeProgress();
				CommonUtil.ShowToast(AddAddressActivity.this, "保存失败");
				break;
			case 1003:
				addProgress();
				new Thread(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						updataAddr();
					}
					
				}.start();
				break;
			}
		}
		
	};
	
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
			send();
		}
	};
	
	private void send(){
		try {
			LogPrint.Print("==========send");
			String em = email_input.getText().toString().trim();
			if(!CommonUtil.checkEmail(em)){
				CommonUtil.ShowToast(AddAddressActivity.this, "主人您可能填错邮箱了！");
				return;
			}
			String addr = addr_input.getText().toString().trim();
			if(addr.length() <= 0){
				CommonUtil.ShowToast(AddAddressActivity.this, "主人您可能填错地址了！");
				return;
			}
			String phone = phone_input.getText().toString().trim();
			if(phone_input.getText().toString().trim().length() != 11||!phone_input.getText().toString().trim().startsWith("1")){
				CommonUtil.ShowToast(AddAddressActivity.this, "主人您把手机号填错了吧？");
				return;
			}
			String name = name_input.getText().toString().trim();
			if(name.length() <= 0){
				CommonUtil.ShowToast(AddAddressActivity.this, "主人您要闹哪样？名字都会填错？");
				return;
			}
			
			Addr addr2 = new Addr();
			String tempAddrManager = CommonUtil.getAddrManager(this);
			ArrayList<Addr> templist = null;
			if (tempAddrManager.length() > 0) {
				try {
					templist = new ArrayList<Addr>();
					JSONArray tempjsonArray = new JSONArray(tempAddrManager);
					for (int i = 0; tempjsonArray != null
							&& i < tempjsonArray.length(); i++) {
						JSONObject jObject = tempjsonArray.getJSONObject(i);
						String result = jObject.getString("result");
						if (result != null) {
							if (result.equalsIgnoreCase("true")) {
								Addr tempaddr = new Addr();
								tempaddr.email = jObject.getString("email");
								tempaddr.addr = jObject.getString("addr");
								tempaddr.phone = jObject.getString("phone");
								tempaddr.name = jObject.getString("name");
								templist.add(tempaddr);
							}
						}
					}
				} catch (Exception e) {
				}
			}
			boolean isSame = false;
			if (templist != null && templist.size() > 0) {
				for (int i = 0; i < templist.size(); i++) {
					if (templist.get(i).email.equals(em)
							&& templist.get(i).addr.equals(addr)
							&& templist.get(i).phone.equals(phone)
							&& templist.get(i).name.equals(name)) {
						isSame = true;
						break;
					}
				}
			}
			if (isSame) {
				CommonUtil.ShowToast(AddAddressActivity.this,
						"主人，这个地址已经有了，不用再写一遍了，汗！");
				return;
			} else {
				addr2.email = em;
				addr2.addr = addr;
				addr2.phone = phone;
				addr2.name = name;
				list = new ArrayList<Addr>();
				list.add(addr2);

				// 组合成jsonArray
				JSONArray jsonArray = new JSONArray();
				for (int i = 0; i < list.size(); i++) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("result", true);
					jsonObject.put("email", list.get(i).email);
					jsonObject.put("addr", list.get(i).addr);
					jsonObject.put("phone", list.get(i).phone);
					jsonObject.put("name", list.get(i).name);
					jsonArray.put(jsonObject);
				}

				String temp = CommonUtil.getAddrManager(this);
				if (temp.length() <= 0) {
					addrManager = jsonArray.toString();
				} else {
					addrManager = (jsonArray.toString()).substring(0,
							(jsonArray.toString()).length() - 1)
							+ "," + temp.substring(1, temp.length());
				}

				// 上传数据
				mHandler.sendEmptyMessage(1003);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void updataAddr(){
		String actionUrl = URLUtil.URL_ADDRMANAGER_UPDATA+"?oid="+UserUtil.userid;
		LogPrint.Print("connect","uploadurl = "+actionUrl);
		try {
			URL url = new URL(actionUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setConnectTimeout(10000);
			/* 设置传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type","text/html");
			/* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			
			ByteArrayInputStream bais = new ByteArrayInputStream(CommonUtil.toUrlEncode(addrManager).getBytes("UTF-8"));
			LogPrint.Print("bais.len = "+bais.available());
			/* 设置每次写入1024bytes */
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			int sum = 0;
			/* 从文件读取数据至缓冲区 */
			while ((length = bais.read(buffer)) != -1) {
				/* 将资料写入DataOutputStream中 */
				sum += length;
				ds.write(buffer, 0, length);
			}
			LogPrint.Print("sum = "+sum);
			/* close streams */
			bais.close();
			ds.flush();
			/* 取得Response内容 */
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			/* 将Response显示于Dialog */
			String resultString = b.toString().trim();
			resultString = CommonUtil.formUrlEncode(resultString);
			LogPrint.Print("============="+resultString);
			if(resultString.indexOf("true") > 0){
				mHandler.sendEmptyMessage(1001);
			}else if(resultString.indexOf("false") > 0){
				mHandler.sendEmptyMessage(1002);
			}
			/* 关闭DataOutputStream */
			ds.close();
			//del by lyb at 2012-12-14(UI操作移入mHandler中)
//			closeProgress();
		} catch (Exception e) {
//			closeProgress();
			mHandler.sendEmptyMessage(1002);
			LogPrint.Print("============="+e.toString());
		}
	}
	
	private void Json(byte[] data){
		try {
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = "+str);
			jsonArray = new JSONArray(str);
			if(str.length() > 0){
				if(str.indexOf("true") > 0){
					addrManager = str;
					CommonUtil.saveAddrManager(this, addrManager);
					CommonUtil.saveCurAddr(this, curAddr);
					list = new ArrayList<Addr>();
				}
			}
			for(int i = 0;jsonArray!=null&&i < jsonArray.length();i ++){
				JSONObject jObject = jsonArray.getJSONObject(i);
				String result = jObject.getString("result");
				if(result != null){
					if(result.equalsIgnoreCase("true")){
						Addr addr = new Addr();
						addr.email = jObject.getString("email");
						addr.addr = jObject.getString("addr");
						addr.phone = jObject.getString("phone");
						addr.name = jObject.getString("name");
						list.add(addr);
					}
				}
			}
			mHandler.sendEmptyMessage(MessageID.MESSAGE_FLUSH_PAGE);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
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
	
	protected class Addr{
		String email;
		String addr;
		String phone;
		String name;
	}
}
