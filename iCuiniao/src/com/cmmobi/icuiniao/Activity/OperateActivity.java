package com.cmmobi.icuiniao.Activity;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.activity.MyPageActivityA;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

public class OperateActivity extends Activity {

	private Button titlebar_backbutton;
	//private Button titlebar_savebutton;
	private Button gestureModeButton;
	private Button buttonModeButton;
	private Button leftHandButton;
	private Button rightHandButton;
	
	private boolean isGestureMode = false;
	private boolean isButtonMode = false;
	private boolean isLeftHand = false;
	private boolean isRightHand = false;
	
	private static final String GESTUREMODE = "1";
	private static final String BUTTONMODE = "0";
	private static final String LEFTHAND = "1";
	private static final String RIGHTHAND = "0";
	
	private String mode = "";
	private String hand = "";
	private ConnectUtil mConnectUtil;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.operate);
		initWidget();
		initListener();
		mode = CommonUtil.getOperateMode(OperateActivity.this);
		hand = CommonUtil.getOperateHand(OperateActivity.this);
		if(mode.equals(GESTUREMODE)){
			isGestureMode = true;
			gestureModeButton.setBackgroundResource(R.drawable.gesturemode_f);
			buttonModeButton.setBackgroundResource(R.drawable.buttonmode);
		}
		else if(mode.equals(BUTTONMODE)){
			isButtonMode = true;
			buttonModeButton.setBackgroundResource(R.drawable.buttonmode_f);
			gestureModeButton.setBackgroundResource(R.drawable.gesturemode);
		}
		if(hand.equals(LEFTHAND)){
			isLeftHand = true;
			leftHandButton.setBackgroundResource(R.drawable.lefthand_f);
			rightHandButton.setBackgroundResource(R.drawable.righthand);
		}
		else if(hand.equals(RIGHTHAND)){
			isRightHand = true;
			rightHandButton.setBackgroundResource(R.drawable.righthand_f);
			leftHandButton.setBackgroundResource(R.drawable.lefthand);
		}
	}
	
	private void initWidget(){
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		//titlebar_savebutton = (Button)findViewById(R.id.titlebar_savebutton);
		gestureModeButton = (Button)findViewById(R.id.gestureModeButton);
		buttonModeButton = (Button)findViewById(R.id.buttonModeButton);
		leftHandButton = (Button)findViewById(R.id.leftHandButton);
		rightHandButton = (Button)findViewById(R.id.rightHandButton);
	}
	
	private void initListener(){
		titlebar_backbutton.setOnClickListener(backbtnClickListener);
		//titlebar_savebutton.setOnClickListener(savebtnClickListener);
		gestureModeButton.setOnClickListener(listener);
		buttonModeButton.setOnClickListener(listener);
		leftHandButton.setOnClickListener(listener);
		rightHandButton.setOnClickListener(listener);
	}
	
	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.gestureModeButton:
				gestureModeButton.setBackgroundResource(R.drawable.gesturemode_f);
				buttonModeButton.setBackgroundResource(R.drawable.buttonmode);
				isGestureMode = true;
				isButtonMode = false;
				break;
			case R.id.buttonModeButton:
				gestureModeButton.setBackgroundResource(R.drawable.gesturemode);
				buttonModeButton.setBackgroundResource(R.drawable.buttonmode_f);
				isGestureMode = false;
				isButtonMode = true;
				break;
			case R.id.leftHandButton:
				leftHandButton.setBackgroundResource(R.drawable.lefthand_f);
				rightHandButton.setBackgroundResource(R.drawable.righthand);
				isLeftHand = true;
				isRightHand = false;
				break;
			case R.id.rightHandButton:
				leftHandButton.setBackgroundResource(R.drawable.lefthand);
				rightHandButton.setBackgroundResource(R.drawable.righthand_f);
				isLeftHand = false;
				isRightHand = true;
				break;
			}
		}
	};
	
	private OnClickListener backbtnClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			finish();			
		}		
	};
	
	private OnClickListener savebtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(isGestureMode){
				CommonUtil.saveOperateMode(OperateActivity.this, GESTUREMODE);
				mode = GESTUREMODE;
			}
			else if(isButtonMode){
				CommonUtil.saveOperateMode(OperateActivity.this, BUTTONMODE);
				mode = BUTTONMODE;
			}
			if(isLeftHand){
				CommonUtil.saveOperateHand(OperateActivity.this, LEFTHAND);
				hand = LEFTHAND;
			}
			else if(isRightHand){
				CommonUtil.saveOperateHand(OperateActivity.this, RIGHTHAND);
				hand = RIGHTHAND;
			}
			finish();
		}
	};

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};
	
	@Override
	public void finish() {
		//未保存情况
		if(mode.equals("")||hand.equals("")){
//			CommonUtil.saveOperateMode(OperateActivity.this, GESTUREMODE);
			CommonUtil.saveOperateMode(OperateActivity.this, BUTTONMODE);//lyb redo at 2013.06.08
			CommonUtil.saveOperateHand(OperateActivity.this, RIGHTHAND);
		}
		if(isGestureMode){
			CommonUtil.saveOperateMode(OperateActivity.this, GESTUREMODE);
			mode = GESTUREMODE;
		}
		else if(isButtonMode){
			CommonUtil.saveOperateMode(OperateActivity.this, BUTTONMODE);
			mode = BUTTONMODE;
		}
		if(isLeftHand){
			CommonUtil.saveOperateHand(OperateActivity.this, LEFTHAND);
			hand = LEFTHAND;
		}
		else if(isRightHand){
			CommonUtil.saveOperateHand(OperateActivity.this, RIGHTHAND);
			hand = RIGHTHAND;
		}
		mConnectUtil = new ConnectUtil(OperateActivity.this, mHandler,1,0);
		mConnectUtil.connect(addUrlParam(URLUtil.URL_USER_OPERATE,UserUtil.userid,mode,hand),HttpThread.TYPE_PAGE, 0);
		Intent intent = new Intent(OperateActivity.this,MyPageActivityA.class);
		Bundle bundle = new Bundle();
		bundle.putString("newMode", mode);
		bundle.putString("newHand", hand);
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		super.finish();
	}
	
	public String addUrlParam(String url,int oid,String bIsGestureMode,String bIsLeftMode){
		if (URLUtil.IsLocalUrl()) {
			return url;
		}
		return url + "?oid="+oid+"&bIsGestureMode="+bIsGestureMode+"&bIsLeftMode="+bIsLeftMode;
	}
	
	private String jsonBoolean(byte[] data){
		try{
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = "+str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			return result;
		}catch(Exception e){
			e.printStackTrace();
		}	
		return "";
	}
}
