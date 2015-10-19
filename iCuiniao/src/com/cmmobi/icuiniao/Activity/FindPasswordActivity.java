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
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

/**
 * @author hw
 *
 */
public class FindPasswordActivity extends Activity{

	private Button titlebar_backbutton;
	private EditText email_input;
	private Button findpwbtn;
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;
	
	private ConnectUtil mConnectUtil;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.findpassword);
//		getWindow().setSoftInputMode(  
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		email_input = (EditText)findViewById(R.id.email_input);
		findpwbtn = (Button)findViewById(R.id.findpwbtn);
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		
		titlebar_backbutton.setOnClickListener(backClickListener);
		findpwbtn.setOnClickListener(findpwClickListener);
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
						CommonUtil.hideSoftInput(email_input, FindPasswordActivity.this);
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
	
	private OnClickListener findpwClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			if(imm != null){
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			String email = email_input.getText().toString().trim();
			if(email.indexOf("@") < 0||email.indexOf(".") < 0){
				CommonUtil.ShowToast(FindPasswordActivity.this, "主人您可能填错邮箱了！");
				return;
			}
			
			mConnectUtil = new ConnectUtil(FindPasswordActivity.this, mHandler,0);
			mConnectUtil.connect(addUrlParam(URLUtil.URL_PASSWORD_FIND, email), HttpThread.TYPE_PAGE, 0);
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
				CommonUtil.ShowToast(FindPasswordActivity.this, (String)msg.obj);
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
						CommonUtil.ShowToast(FindPasswordActivity.this, "主人，密码已经发送到您的注册邮箱，别再忘记了！");
					}else{
						String msg = null;
						try {
							msg = jObject.getString("msg");
						} catch (Exception e) {
							CommonUtil.ShowToast(FindPasswordActivity.this, "出问题啦！再试一次吧！");
						}
						if(msg != null){
							CommonUtil.ShowToast(FindPasswordActivity.this, "出问题啦！再试一次吧！\n"+msg);
						}
					}
				}
			}
		}catch(Exception e){
			
		}
	}
	
	public String addUrlParam(String url,String em){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"?em="+CommonUtil.toUrlEncode(em);
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
