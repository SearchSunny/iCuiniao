/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

/**
 * @author hw
 *设置密码
 */
public class SettingPwActivity extends Activity{

	private Button titlebar_backbutton;
	private EditText opw_input;
	private EditText npw_input;
	private EditText npwa_input;
	private Button okbtn;
	
	private ConnectUtil mConnectUtil;
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_pw);
		getWindow().setSoftInputMode(  
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		opw_input = (EditText)findViewById(R.id.opw_input);
		npw_input = (EditText)findViewById(R.id.npw_input);
		npwa_input = (EditText)findViewById(R.id.npwa_input);
		okbtn = (Button)findViewById(R.id.okbtn);
		//lyb
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		
		titlebar_backbutton.setOnClickListener(backClickListener);
		okbtn.setOnClickListener(okClickListener);
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
						CommonUtil.hideSoftInput(opw_input, SettingPwActivity.this);
					}
					break;	
				}							
				return false;
			}
		});				
		OfflineLog.writeEditPassword();
	}
	
	private OnClickListener backClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	};
	
	private OnClickListener okClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			if(imm != null){
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			String opw="";
			String npw="";
			String npwa="";
			opw = opw_input.getText().toString().trim();
			npw = npw_input.getText().toString().trim();
			npwa = npwa_input.getText().toString().trim();
			if(opw.length() > 0&&npw.length() > 0&&npwa.length() > 0){
				if(npw.equals(npwa)){
					mConnectUtil = new ConnectUtil(SettingPwActivity.this, mHandler,0);
					mConnectUtil.connect(addUrlParam(URLUtil.URL_PASSWORD_RESET, UserUtil.userid, opw, npw), HttpThread.TYPE_PAGE, 0);
				}else{
					CommonUtil.ShowToast(SettingPwActivity.this, "主人，您分裂了，两次的密码都不一样！");
				}
			}else{
				CommonUtil.ShowToast(SettingPwActivity.this, "请输入密码");
			}
		}
	};
	
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
				CommonUtil.ShowToast(SettingPwActivity.this, (String)msg.obj);
				break;
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				closeProgress();
				Json((byte[])msg.obj,msg.getData().getString("content_type"));
				break;
			}
		}
		
	};
	
	private void Json(byte[] data,String contenttype){
		try{
			if("text/json".equals(contenttype)){
				String str = new String(data,"UTF-8");
				str = CommonUtil.formUrlEncode(str);
				JSONObject jObject = new JSONObject(str);
				String result = jObject.getString("result");
				if(result != null){
					if(result.equalsIgnoreCase("true")){
						CommonUtil.ShowToast(SettingPwActivity.this, "修改成功!");
						finish();
					}else{
						String msg = null;
						try {
							msg = jObject.getString("msg");
						} catch (Exception e) {
							CommonUtil.ShowToast(SettingPwActivity.this, "修改失败!");
						}
						if(msg != null){
							CommonUtil.ShowToast(SettingPwActivity.this, "修改失败!\n"+msg);
						}
					}
				}
			}
		}catch(Exception e){
			
		}
	}
	
	public String addUrlParam(String url,int oid,String pw,String npw){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"?oid="+oid+"&pw="+UserUtil.Encryption(pw)+"&npw="+UserUtil.Encryption(npw);
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
}
