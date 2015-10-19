/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.SystemProgress;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

/**
 * @author hw
 *
 */
public class SajiaoRequestActivity extends Activity{

	private TextView email_text;
	private TextView name_text;
	private TextView addr_text;
	private TextView phone_text;
	private TextView item1_text;
	private TextView item2_text;
	private TextView num_text;
	private TextView msg_text;
	private TextView info_text;
	private ImageView img;
	private Button sendbtn;
	private Button backbtn;
	
	private String titleString;
	private int commodityid;
	private String item1;
	private String item2;
	private String button1;
	private String button2;
	private String em;
	private String addr;
	private String phone;
	private String name;
	private String msg;
	private String num;
	private String commodityImageString;
	private String commodityInfoString;
	
	private ConnectUtil mConnectUtil;
	private boolean isFirst;
	
	private int lastButton1_index;
	private int lastButton2_index;
	//add by lyb 小鱼loading
//	private ProgressBar loadingBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sajiao_request);
		email_text = (TextView)findViewById(R.id.email_text);
		name_text = (TextView)findViewById(R.id.name_text);
		addr_text = (TextView)findViewById(R.id.addr_text);
		phone_text = (TextView)findViewById(R.id.phone_text);
		item1_text = (TextView)findViewById(R.id.item1_text);
		item2_text = (TextView)findViewById(R.id.item2_text);
		num_text = (TextView)findViewById(R.id.num_text);
		msg_text = (TextView)findViewById(R.id.msg_text);
		info_text = (TextView)findViewById(R.id.info_text);
		img = (ImageView)findViewById(R.id.img);
		sendbtn = (Button)findViewById(R.id.sendbtn);
		backbtn = (Button)findViewById(R.id.backbtn);
		//lyb
//		loadingBar = (ProgressBar)findViewById(R.id.loading);
		
		isFirst = getIntent().getExtras().getBoolean("isfirst");
		titleString = getIntent().getExtras().getString("title");
		commodityid = getIntent().getExtras().getInt("commodityid");
		item1 = getIntent().getExtras().getString("item1");
		item2 = getIntent().getExtras().getString("item2");
		button1 = getIntent().getExtras().getString("button1");
		button2 = getIntent().getExtras().getString("button2");
		em = getIntent().getExtras().getString("em");
		addr = getIntent().getExtras().getString("addr");
		phone = getIntent().getExtras().getString("phone");
		name = getIntent().getExtras().getString("name");
		msg = getIntent().getExtras().getString("msg");
		num = getIntent().getExtras().getString("num");
		commodityImageString = getIntent().getExtras().getString("commodityImageString");
		commodityInfoString = getIntent().getExtras().getString("commodityInfoString");
		lastButton1_index = getIntent().getExtras().getInt("button1_index");
		lastButton2_index = getIntent().getExtras().getInt("button2_index");
		if(commodityInfoString == null)commodityInfoString = "";
		
		email_text.setText("邮箱: "+em);
		name_text.setText("姓名: "+name);
		addr_text.setText("地址: "+addr);
		phone_text.setText("电话: "+phone);
		if(item1.length() <= 0){
			item1_text.setVisibility(View.GONE);
		}else{
			item1_text.setText(item1.substring(0, item1.indexOf("#"))+": "+button1);
		}
		if(item2.length() <= 0){
			item2_text.setVisibility(View.GONE);
		}else{
			item2_text.setText(item2.substring(0, item2.indexOf("#"))+": "+button2);
		}
		num_text.setText("数量: "+num);
		msg_text.setText(msg);
		info_text.setText(commodityInfoString);
		
		sendbtn.setOnClickListener(sendbtnClickListener);
		backbtn.setOnClickListener(backbtnClickListener);
		
		mConnectUtil = new ConnectUtil(SajiaoRequestActivity.this, mHandler,0);
		mConnectUtil.connect(commodityImageString, HttpThread.TYPE_IMAGE, 0);
		addProgress();
	}
	
	private OnClickListener sendbtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mConnectUtil = new ConnectUtil(SajiaoRequestActivity.this, mHandler,0);
			mConnectUtil.connect(addUrlParam(URLUtil.URL_SAJIAO_UPLOAD, UserUtil.userid, commodityid, item1, item2, button1, button2, em, addr, phone, name, msg,num), HttpThread.TYPE_PAGE, 1);
			addProgress();
		}
	};
	
	private OnClickListener backbtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.putExtra("commodityid", commodityid);
			intent.putExtra("title", titleString);
			intent.putExtra("commodityImageString", commodityImageString);
			intent.putExtra("commodityInfoString", commodityInfoString);
			intent.putExtra("button1", lastButton1_index);
			intent.putExtra("button2", lastButton2_index);
			intent.putExtra("num", num);
			intent.putExtra("em", em);
			intent.putExtra("addr", addr);
			intent.putExtra("phone", phone);
			intent.putExtra("name", name);
			intent.putExtra("msg", msg);
			intent.setClass(SajiaoRequestActivity.this, SajiaoActivity.class);
			startActivity(intent);
			finish();
		}
	};
	
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
						Json((byte[])msg.obj);
					}else{
						closeProgress();
						byte[] data = (byte[])msg.obj;
						Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
						img.setBackgroundDrawable(new BitmapDrawable(temp));
					}
				}
				break;
			}
		}
		
	};
	
	private SystemProgress progress;
	public void addProgress(){
		try {
			if(progress != null)return;
			progress = new SystemProgress(this, this, null){
				public void cancelData(){
					
				}
			};
//			if(loadingBar != null){
//				loadingBar.setVisibility(View.VISIBLE);
//			}
			
		} catch (Exception e) {
		}
	}
	
	public void closeProgress(){
		if(progress != null){
			progress.close();
			progress = null;
		}
//		if(loadingBar != null){
//			loadingBar.setVisibility(View.INVISIBLE);
//		}
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
	
	private void Json(byte[] data){
		try {
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			str = str.replace(",null", "");
			LogPrint.Print("json = "+str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			if(result != null){
				if(result.equalsIgnoreCase("true")){
					CommonUtil.ShowToast(SajiaoRequestActivity.this, "发送成功!");
					if(isFirst){
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
						CommonUtil.saveAddrManager(SajiaoRequestActivity.this, addrManagerString);
						CommonUtil.saveCurAddr(SajiaoRequestActivity.this, 0);
					}
					finish();
				}else{
					String msg=null;
					try {
						msg = jObject.getString("msg");
					} catch (Exception e) {
					}
					if(msg != null){
						CommonUtil.ShowToast(SajiaoRequestActivity.this, "发送失败!\n"+msg);
					}else{
						CommonUtil.ShowToast(SajiaoRequestActivity.this, "发送失败!");
					}
				}
			}else{
				CommonUtil.ShowToast(SajiaoRequestActivity.this, "失败!");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
