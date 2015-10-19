package com.cmmobi.icuiniao.Activity;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.ui.view.SlipButton;
import com.cmmobi.icuiniao.ui.view.SlipButton.OnChangedListener;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

public class HomePageSettingActivity extends Activity {
	
	private Button titlebar_backbutton = null;
	private SlipButton allAddFriendSlipButton = null;//允许任何人加好友
	private SlipButton likeFriendSlipButton = null;//喜欢好友可见
	private SlipButton shareFriendSlipButton = null;//分享好友可见
	private SlipButton baskFriendSlipButton = null;//晒单好友可见
	//联网线程
	private final int thread_like = 10;
	private final int thread_share = 11;
	private final int thread_comment = 12;
	private final int thread_allow_addFriend = 13;
	private boolean likeFriendBoolean = false;
	private boolean shareFriendBoolean = false;
	private boolean baskFriendBoolean = false;
	private boolean allAddFriendBoolean = false;
	private ProgressBar loadingBar;
	private ConnectUtil mConnectUtil;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homepagesetting);
		initWidget();
		initData();
		setListener();
	}

	/**
	 * 允许任何人加好友 监听
	 */
	private OnChangedListener allAddFriendListener = new OnChangedListener() {
		
		@Override
		public void onChanged(boolean checkState) {
			connectSetPermitAddFriend(checkState);			
		}
	};
	/**
	 * 喜欢好友可见监听
	 */
	private OnChangedListener likeFriendChangedListener = new OnChangedListener() {
		
		@Override
		public void onChanged(boolean checkState) {
			connectSetAuthority(thread_like, "likeperm", checkState);			
		}
	};	
	
	/**
	 * 分享好友可见监听
	 */
	private OnChangedListener shareFriendListener = new OnChangedListener() {
		
		@Override
		public void onChanged(boolean checkState) {
			connectSetAuthority(thread_share, "sharaperm", checkState);			
		}
	};	
	
	/**
	 * 晒单好友可见监听
	 */
	private OnChangedListener baskFriendListener = new OnChangedListener() {
		
		@Override
		public void onChanged(boolean checkState) {
			connectSetAuthority(thread_comment, "commperm", checkState);			
		}
	};
	/**
	 * 返回监听
	 */
	private OnClickListener backClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finish();
		}
	};	
	private void connectSetPermitAddFriend( boolean checkState){
		final int threadIndex = thread_allow_addFriend;
		int value = getIntCheckValue(threadIndex, checkState);
		String url = URLUtil.Url_SET_PERMIT_ADD_FRIEND + "?addfriendperm=" +  value + "&oid=" + UserUtil.userid;
		mConnectUtil = new ConnectUtil(HomePageSettingActivity.this, mHandler, 1, value);
		mConnectUtil.connect(url, HttpThread.TYPE_PAGE,threadIndex);
	}		
	/**
	 * //喜欢的查询权限(1:好友可见、2:自己可见) ;开关关闭为仅自己可见2，开关开启为好友可见1
	 * 分享的查询权限(0:全部可见、1:好友可见)  ; 开关开启为任何人可见0，开关关闭为好友可见1
	 * 评论的查询权限(0:全部可见、1:好友可见)
	 * @param threadIndex
	 * @param checkState
	 * @return
	 */
	private int getIntCheckValue(int threadIndex, boolean checkState){
		int checkInt = 1;
		if(checkState){
			checkInt = 0;
		}
		
		if(threadIndex == thread_like){
			checkInt ++;
		}
		return checkInt;
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
					if("text/html".equalsIgnoreCase(msg.getData().getString("content_type"))){
						try {
							String tmpurl = new String((byte[])msg.obj,"UTF-8");
							if(tmpurl != null){
								mConnectUtil = new ConnectUtil(HomePageSettingActivity.this, mHandler,0);
								mConnectUtil.connect(tmpurl, HttpThread.TYPE_IMAGE, 0);
								addProgress();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else if("text/json".equals(msg.getData().getString("content_type"))){
						if(msg.arg1 == thread_like || msg.arg1 == thread_share || msg.arg1 == thread_comment || msg.arg1 == thread_allow_addFriend){
							String resultBool = jsonBoolean((byte[])msg.obj);
							int checkState = msg.getData().getInt("tag");							
							boolean checkBool = getBoolCheckValue(msg.arg1, checkState);
							if(resultBool.equalsIgnoreCase("true")){
								saveAuthory(msg.arg1, checkBool);
							}else{
								setOldCheck(msg.arg1, !checkBool);
							}
							return;
						}
						Json((byte[])msg.obj, msg.arg1);
					}
				}
				break;	
			}
		}
	};
	/**
	 * 解析设置的bool返回值
	 * @param data
	 * @return
	 */
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

	private boolean getBoolCheckValue(int threadIndex, int checkState){
		boolean checkBool = true;
		if(threadIndex == thread_like){
			checkState --;
		}
		if(checkState == 0){
			checkBool = true;
		}else if(checkState == 1){
			checkBool = false;
		}
		return checkBool;
	}
	
	private void saveAuthory(int threadIndex, boolean checkState){
		switch(threadIndex){
		case thread_like:
			CommonUtil.saveLikeFriendState(HomePageSettingActivity.this, checkState);
			break;
		case thread_share:
			CommonUtil.saveShareFriendState(HomePageSettingActivity.this, checkState);
			break;
		case thread_comment:
			CommonUtil.saveBaskFriendState(HomePageSettingActivity.this, checkState);
			break;
		case thread_allow_addFriend:
			CommonUtil.saveAllAddFriendState(HomePageSettingActivity.this, checkState);
			break;
		}
	}
	
	private void setOldCheck(int threadIndex, boolean oldCheck){
		switch(threadIndex){
		case thread_like:
			likeFriendSlipButton.setCheck(oldCheck);
			break;
		case thread_share:
			shareFriendSlipButton.setCheck(oldCheck);
			break;
		case thread_comment:
			baskFriendSlipButton.setCheck(oldCheck);
			break;
		case thread_allow_addFriend:
			allAddFriendSlipButton.setCheck(oldCheck);
			break;
		}
	}
	
	private void Json(byte[] data,int type){
		try {
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = "+str);
			switch (type) {
			case 3:
				JSONObject jsonObject = new JSONObject(str);
				String result = jsonObject.getString("result");
				if(result.equalsIgnoreCase(result)){
					Intent intent = new Intent(HomePageSettingActivity.this,BindActivity.class);
					startActivity(intent);
					finish();
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addProgress(){
		try {
			if(loadingBar != null){
				loadingBar.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeProgress(){
		if(loadingBar != null){
			loadingBar.setVisibility(View.INVISIBLE);
		}
	}	
	/**
	 * 
	 * @param threadIndex
	 * @param parmName likeperm：喜欢；sharaperm：分享；评论： "commperm" ；
	 * @param checkState
	 */
	private void connectSetAuthority(int threadIndex, String parmName, boolean checkState){
		int value = getIntCheckValue(threadIndex, checkState);
		String url = URLUtil.Url_SET_QUIRY_AUTHOR + "?" +  parmName + "=" + value + "&oid=" + UserUtil.userid;
		mConnectUtil = new ConnectUtil(HomePageSettingActivity.this, mHandler, 1, value);
		mConnectUtil.connect(url, HttpThread.TYPE_PAGE,threadIndex);
	}	
	/**
	 * 初始化控件
	 */
	public void initWidget(){
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		allAddFriendSlipButton = (SlipButton)findViewById(R.id.allAddFriendSlipButton);
		likeFriendSlipButton = (SlipButton)findViewById(R.id.likeFriendSlipButton);
		shareFriendSlipButton = (SlipButton)findViewById(R.id.shareFriendSlipButton);
		baskFriendSlipButton = (SlipButton)findViewById(R.id.baskFriendSlipButton);
		loadingBar = (ProgressBar)findViewById(R.id.loading);
	}
	
	/**
	 * 设置监听
	 */
	public void setListener(){
		titlebar_backbutton.setOnClickListener(backClickListener);
		allAddFriendSlipButton.setOnChangedListener(allAddFriendListener);
		likeFriendSlipButton.setOnChangedListener(likeFriendChangedListener);
		shareFriendSlipButton.setOnChangedListener(shareFriendListener);
		baskFriendSlipButton.setOnChangedListener(baskFriendListener);
		
	}
	
	/**
	 * 初始化页面数据
	 */
	public void initData(){
		//喜欢好友可见设置
		likeFriendBoolean = CommonUtil.getLikeFriendState(HomePageSettingActivity.this);
		likeFriendSlipButton.setCheck(likeFriendBoolean);
		//分享公开可见设置
		shareFriendBoolean = CommonUtil.getShareFriendState(HomePageSettingActivity.this);
		shareFriendSlipButton.setCheck(shareFriendBoolean);
		//公开评论可见设置
		baskFriendBoolean = CommonUtil.getBaskFriendState(HomePageSettingActivity.this);
		baskFriendSlipButton.setCheck(baskFriendBoolean);
		//允许任何人加好友
		allAddFriendBoolean = CommonUtil.getAllAddFriendState(HomePageSettingActivity.this);
		allAddFriendSlipButton.setCheck(allAddFriendBoolean);
	}
	public void finish(){
		super.finish();
	}
}
