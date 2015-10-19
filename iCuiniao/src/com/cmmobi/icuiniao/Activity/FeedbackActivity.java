/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

/**
 * @author hw
 *反馈
 */
public class FeedbackActivity extends Activity{

	private Button titlebar_backbutton;
	private EditText phone_input;
	private EditText qq_input;
	private EditText feedback_input;
	private TextView input_num;
	private Button okbtn;
	private int input_cur;
	
	private ConnectUtil mConnectUtil;
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
//        getWindow().setSoftInputMode(  
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        input_cur = 100;
        titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
        phone_input = (EditText)findViewById(R.id.phone_input);
        qq_input = (EditText)findViewById(R.id.qq_input);
        feedback_input = (EditText)findViewById(R.id.feedback_input);
        input_num = (TextView)findViewById(R.id.input_num);
        okbtn = (Button)findViewById(R.id.okbtn);
        loadingBar = (ProgressBar)findViewById(R.id.loading);
        
        titlebar_backbutton.setOnClickListener(backClickListener);
        okbtn.setOnClickListener(okClickListener);
        
        feedback_input.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				input_num.setText("您还可输入"+input_cur+"字");
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				input_num.setText("您还可输入"+input_cur+"字");
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if(feedback_input.getText().length() >= 100){
					CommonUtil.ShowToast(FeedbackActivity.this, "主人，您写那么多字做咩？");
				}
				input_cur = 100-feedback_input.getText().length();
				input_num.setText("您还可输入"+input_cur+"字");
			}
		});
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
						CommonUtil.hideSoftInput(feedback_input, FeedbackActivity.this);
					}
					break;	
				}							
				return false;
			}
		});				        
		OfflineLog.writeFeedBack();
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
			feedback();
		}
	};
	
	private void feedback(){
		mConnectUtil = new ConnectUtil(FeedbackActivity.this, mHandler,0);
		String phone = phone_input.getText().toString().trim();
		if(phone.length() != 0&&phone.length() != 11){
			CommonUtil.ShowToast(FeedbackActivity.this, "主人您把手机号填错了吧？");
			return;
		}
		String qq = qq_input.getText().toString().trim();
		String msg = feedback_input.getText().toString().trim();
		if(msg.length() <= 0){
			CommonUtil.ShowToast(FeedbackActivity.this, "主人，您无语么？");
			return;
		}
		mConnectUtil.connect(addUrlParam(URLUtil.URL_FEEDBACK, phone, qq, msg,UserUtil.userid), HttpThread.TYPE_PAGE, 0);
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
				Json((byte[])msg.obj,msg.getData().getString("content_type"));
				break;
			}
		}
		
	};
	
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
	
	private void Json(byte[] data,String contenttype){
		try{
			if("text/json".equals(contenttype)){
				String str = new String(data,"UTF-8");
				str = CommonUtil.formUrlEncode(str);
				JSONObject jObject = new JSONObject(str);
				String result = jObject.getString("result");
				if(result != null){
					if(result.equalsIgnoreCase("true")){
						feedback_input.setText("");//清空内容
						CommonUtil.ShowToast(FeedbackActivity.this, "跪谢主人的好建议，小C这厢有礼了！");
					}else{
						String msg = null;
						try {
							msg = jObject.getString("msg");
						} catch (Exception e) {
							CommonUtil.ShowToast(FeedbackActivity.this, "出问题啦！再试一次吧！");
						}
						if(msg != null){
							CommonUtil.ShowToast(FeedbackActivity.this, "出问题啦！再试一次吧！\n"+msg);
						}
					}
				}
			}
		}catch(Exception e){
			
		}
	}
	
	public String addUrlParam(String url,String phone,String qq,String msg,int oid){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"?oid="+oid+"&phone="+CommonUtil.toUrlEncode(phone)+"&qq="+CommonUtil.toUrlEncode(qq)+"&msg="+CommonUtil.toUrlEncode(msg);
	}
}
