/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;

/**
 * @author hw
 *此类以不再使用，注册功能已移至后台处理
 */
public class RegeditActivity extends Activity{

	private Button titlebar_backbutton;
	private EditText email_input;
	private EditText name_input;
	private EditText password_input;
	private Button womenbtn;
	private Button manbtn;
//	private ImageView iconimage;
	private Button overbtn;
	
	private int gender;//性别 0:男,1:女
	private int iconIndex;//图标索引 0:男默认,1:女默认,2:自定义
	private ConnectUtil mConnectUtil;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regedit);
		getWindow().setSoftInputMode(  
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		gender = -1;
		iconIndex = 1;
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		email_input = (EditText)findViewById(R.id.email_input);
		name_input = (EditText)findViewById(R.id.name_input);
		password_input = (EditText)findViewById(R.id.password_input);
		womenbtn = (Button)findViewById(R.id.womenbtn);
		manbtn = (Button)findViewById(R.id.manbtn);
//		iconimage = (ImageView)findViewById(R.id.iconimage);
		overbtn = (Button)findViewById(R.id.overbtn);
		
		titlebar_backbutton.setOnClickListener(backClickListener);
		womenbtn.setOnClickListener(womenbtnClickListener);
		manbtn.setOnClickListener(manbtnClickListener);
		overbtn.setOnClickListener(overbtnClickListener);
		//lyb
		ScrollView scrollview = (ScrollView)findViewById(R.id.scrollview);
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
						CommonUtil.hideSoftInput(email_input, RegeditActivity.this);
					}
					break;	
				}							
				return false;
			}
		});					
	}
	
	private OnClickListener backClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
		
	};
	
	private OnClickListener womenbtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			if(imm != null){
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			gender = 1;
			iconIndex = 1;
			womenbtn.setBackgroundResource(R.drawable.local_womenbtn_f);
			manbtn.setBackgroundResource(R.drawable.local_manbtn);
//			iconimage.setBackgroundResource(R.drawable.local_icon_women);
		}
	};
	
	private OnClickListener manbtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			if(imm != null){
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			gender = 0;
			iconIndex = 0;
			womenbtn.setBackgroundResource(R.drawable.local_womenbtn);
			manbtn.setBackgroundResource(R.drawable.local_manbtn_f);
//			iconimage.setBackgroundResource(R.drawable.local_icon_man);
		}
	};
	
	private OnClickListener overbtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			if(imm != null){
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
//			mConnectUtil = new ConnectUtil(RegeditActivity.this, mHandler,0);
			String em = null;
			String name = null;
			String pw = null;
			if(email_input.getText() != null){
				if(email_input.getText().toString().indexOf("@") > 0&&email_input.getText().toString().indexOf(".") > 0){
					em = email_input.getText().toString().trim();
				}else{
					CommonUtil.ShowToast(RegeditActivity.this, "邮箱格式不正确");
					return;
				}
			}else{
				CommonUtil.ShowToast(RegeditActivity.this, "请输入邮箱");
				return;
			}
			if(name_input.getText() != null&&name_input.getText().length() > 0){
				name = name_input.getText().toString().trim();
			}else{
				CommonUtil.ShowToast(RegeditActivity.this, "请输入昵称");
				return;
			}
			if(password_input.getText() != null&&password_input.getText().length() > 0){
				pw = password_input.getText().toString().trim();
			}else{
				CommonUtil.ShowToast(RegeditActivity.this, "请输入密码");
				return;
			}
			if(gender == -1){
				CommonUtil.ShowToast(RegeditActivity.this, "请选择性别");
				return;
			}
			
//			mConnectUtil.connect(addUrlParam(URLUtil.URL_REGEDIT,em,name,pw,gender,iconIndex), HttpThread.TYPE_PAGE, 0);
			if(CommonUtil.isNetWorkOpen(RegeditActivity.this)){
				Intent intent11 = new Intent();
				intent11.setClass(RegeditActivity.this, WebViewLoginActivity.class);
				String url = addUrlParam(URLUtil.URL_REGEDIT,em,name,pw,gender);
				LogPrint.Print("webview","regedit url = "+url);
				intent11.putExtra("url", url);
				startActivityForResult(intent11, 9001);
			}else{
				CommonUtil.ShowToast(RegeditActivity.this, "杯具了- -!\n联网不给力啊");
			}
		}
	};
	
//	private Handler mHandler = new Handler(){
//
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			super.handleMessage(msg);
//			switch (msg.what) {
//			case MessageID.MESSAGE_CONNECT_START:
//				addProgress();
//				break;
//			case MessageID.MESSAGE_CONNECT_ERROR:
//				closeProgress();
//				CommonUtil.ShowToast(getApplicationContext(), (String)msg.obj);
//				break;
//			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
//				closeProgress();
//				Json((byte[])msg.obj,msg.getData().getString("content_type"));
//				break;
//			}
//		}
//		
//	};
//	
//	private void Json(byte[] data,String contenttype){
//		try{
//			if("text/json".equals(contenttype)){
//				String str = new String(data,"UTF-8");
//				str = CommonUtil.formUrlEncode(str);
//				JSONObject jObject = new JSONObject(str);
//				String result = jObject.getString("result");
//				if(result != null){
//					if(result.equalsIgnoreCase("true")){
//						String oid = jObject.getString("oid");
//						String name = jObject.getString("name");
//						String gender = jObject.getString("gender");
//						if(oid != null){
//							UserUtil.userid = Integer.parseInt(oid);
//						}
//						UserUtil.username = name;
//						if(gender != null){
//							UserUtil.gender = Integer.parseInt(gender);
//						}else{
//							UserUtil.gender = -1;
//						}
//						if(UserUtil.userid != -1&&UserUtil.username != null){
//							UserUtil.isNewLoginOrExit = true;
//							LogPrint.Print("=============oid = "+UserUtil.userid);
//							LogPrint.Print("=============name = "+UserUtil.username);
//							LogPrint.Print("=============gender = "+UserUtil.gender);
//							CommonUtil.saveUserId(RegeditActivity.this, UserUtil.userid);
//							CommonUtil.saveUserName(RegeditActivity.this, UserUtil.username);
//							UserUtil.logintype = 0;
//							CommonUtil.saveLoginType(RegeditActivity.this, UserUtil.logintype);
//							CommonUtil.saveGender(RegeditActivity.this, UserUtil.gender);
//							CommonUtil.ShowToast(RegeditActivity.this, "注册成功，主人你好，我是小C。");
//							//开启信息服务
//							startService(new Intent(this,MessageReceiveService.class));
//							finish();
//						}
//					}else{
//						String msg = jObject.getString("msg");
//						if(msg != null){
//							CommonUtil.ShowToast(RegeditActivity.this, "囧！注册失败，再来一次！\n"+msg);
//						}else{
//							CommonUtil.ShowToast(RegeditActivity.this, "囧！注册失败，再来一次！");
//						}
//					}
//				}
//				finish();
//			}
//		}catch(Exception e){
//			
//		}
//	}
//	
//	private SystemProgress progress;
//	public void addProgress(){
//		try {
//			if(progress != null)return;
//			progress = new SystemProgress(this, this, null){
//				public void cancelData(){
//					
//				}
//			};
//			
//		} catch (Exception e) {
//		}
//	}
//	
//	public void closeProgress(){
//		if(progress != null){
//			progress.close();
//			progress = null;
//		}
//	}
	
	public String addUrlParam(String url,String em,String name,String pw,int gender){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"?type=1&oid="+UserUtil.userid+"&mail="+CommonUtil.toUrlEncode(em)+"&rname="+CommonUtil.toUrlEncode(name)+"&rpwd="+CommonUtil.toUrlEncode(UserUtil.Encryption(pw))+"&sax="+(gender==0?"m":"f")
		+"&pc="+URLUtil.pc+"&fid="+URLUtil.fid+"&os_version="+CommonUtil.toUrlEncode(CommonUtil.getOs_Version())+"&network_type="+CommonUtil.getApnType(this)
  		+"&plaid="+URLUtil.plaid+"&dpi="+URLUtil.dpi()+"&imsi="+CommonUtil.getIMSI(this)+"&imei="+CommonUtil.getIMEI(this)+"&sim="+CommonUtil.getSimType(this)+"&reletm="+URLUtil.reletm+"&pla="+CommonUtil.toUrlEncode(CommonUtil.getDeviceName())+"&ver="+URLUtil.version+"&deviceid="+CommonUtil.getIMEI(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			switch (requestCode) {
			case 9001:
				if("true".equals(data.getAction())){
					LogPrint.Print("regedit ok");
					Intent intent = new Intent("true");
					setResult(RESULT_OK,intent);
					finish();
				}else{
					LogPrint.Print("regedit false");
				}
				break;
			}
		}
	}
	
}
