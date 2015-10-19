package com.cmmobi.icuiniao.Activity;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.entity.AddressInfo;
import com.cmmobi.icuiniao.menuclick.AddrManager_MenuClick;
import com.cmmobi.icuiniao.ui.adapter.AddressMgrAdapter;
import com.cmmobi.icuiniao.ui.view.LitviewNoOverScroll;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.PageID;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

public class AddrManagerActivityA extends Activity{	
	
	private ProgressBar loadingBar;	
	private LitviewNoOverScroll listView;
	private Button titlebar_backbutton;
	private Button titlebar_addbutton;
	private RelativeLayout empLinear;
	private Button addAddress;
	
	private AddressMgrAdapter addrMgrAdapter;	
	private ArrayList<AddressInfo> arrAddress;
	private String addrManager;//地址管理数据,jsonArray	
	private int currAddrIndex;//当前选中的地址
	
	private ConnectUtil mConnectUtil;	
	private final int THREAD_GET = 0;
	private final int THREAD_DEL = 1;	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
		setContentView(R.layout.addrmanager_a);
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		empLinear = (RelativeLayout)findViewById(R.id.empLinear);
		addAddress = (Button)findViewById(R.id.addAddress);
		addAddress.setOnClickListener(addClickListener);
		listView = (LitviewNoOverScroll)findViewById(R.id.listDelivery);
		titlebar_backbutton  = (Button)findViewById(R.id.titlebar_backbutton);
		titlebar_backbutton.setOnClickListener(backClickListener);
		titlebar_addbutton  = (Button)findViewById(R.id.titlebar_addbutton);
		titlebar_addbutton.setOnClickListener(addClickListener);
		
		addrMgrAdapter = new AddressMgrAdapter(this, mHandler);			
		addrManager = CommonUtil.getReceiveAddrMgr(this);
		currAddrIndex = CommonUtil.getCurReceiveAddr(this);
		addrMgrAdapter.setCurrAddrIndex(currAddrIndex);
		listView.setAdapter(addrMgrAdapter);
		if(addrManager.length() <= 2){		
			mConnectUtil = new ConnectUtil(this, mHandler,0); //需参数oid，联网架构会加
			mConnectUtil.connect(URLUtil.URL_RECEIVE_ADDR_GET, HttpThread.TYPE_PAGE, THREAD_GET);
		}
//		else{
//			refreshListData(addrManager);
//		}
		
	}
	@Override
	public void onResume(){
		super.onResume();
		addrManager = CommonUtil.getReceiveAddrMgr(this);
		//本地没有数据中断返回就不更新了，减少联网
		if(addrManager.length() > 0){
			currAddrIndex = CommonUtil.getCurReceiveAddr(this);
			addrMgrAdapter.setCurrAddrIndex(currAddrIndex);
			refreshListData(addrManager);
		}
	}
	
	private OnClickListener addClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {			
			Intent intent = new Intent(AddrManagerActivityA.this,
					AddAddressActivityA.class);			
			startActivity(intent);
//			finish();
		}
	};
	
	private OnClickListener backClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {			
			finish();
		}
	};
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
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
				switch(msg.arg1){
				case THREAD_GET:
					JsonGetADDList((byte[])msg.obj);
					closeProgress();
					break;
				case THREAD_DEL:
					final String url = msg.getData().getString("mUrl");
					final String delIndex = CommonUtil.getSubString(url, "index=" , "&id=");
					JsonDelResult((byte[])msg.obj, Integer.valueOf(delIndex));
					closeProgress();
					break;						
				}				
				break;
			case MessageID.MESSAGE_DEL_ADDRESS:
				addDelMenu(msg.arg1);
				break;
			//地址删除
			case MessageID.MESSAGE_MENUCLICK_ADDRMANAGER:
				final int delIndex = msg.arg1;
				final String id = arrAddress.get(delIndex).id;
				mConnectUtil = new ConnectUtil(AddrManagerActivityA.this, mHandler,0); //需参数oid，联网架构会加
				mConnectUtil.connect(URLUtil.URL_RECEIVE_ADDR_DEL + "?index=" + delIndex + "&id=" + id, HttpThread.TYPE_PAGE, THREAD_DEL);
				break;
			}
		}
	};
	//获取收货地址
	private void JsonGetADDList(byte[] data) {
		try {
			LogPrint.Print("==========Json");
			String str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = " + str);
			addrManager = str;
			CommonUtil.saveReceiveAddrMgr(this, addrManager);
			refreshListData(addrManager);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//删除的结果
	private void JsonDelResult(byte[] data, int delIndex) {
		try {
			String str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = " + str);
			if (str.length() > 0) {
				if (str.indexOf("true") > 0) {
					JSONObject jsonObj = new JSONObject(str);
					boolean result = jsonObj.getBoolean("result");
					if (result) {
						if (arrAddress.size() - 1 >= delIndex) {
							arrAddress.remove(delIndex);
						}
						if (arrAddress.size() == 0) {
							currAddrIndex = 0;
							empLinear.setVisibility(View.VISIBLE);
							listView.setVisibility(View.GONE);
							addrManager = "";
						} else {
							currAddrIndex = 0;
							addrMgrAdapter.setCurrAddrIndex(currAddrIndex);							
							addrManager = AddressInfo
									.changeListToString(arrAddress);
							refreshListData(addrManager);							
						}
						CommonUtil.saveReceiveAddrMgr(this, addrManager);
						CommonUtil.saveCurReceiveAddr(this, currAddrIndex);
					} else {
						CommonUtil.ShowToast(this, "删除失败");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void refreshListData(String str){
		if (str.length() <= 2) {
			empLinear.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		}else{			
			empLinear.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			arrAddress = AddressInfo.changeStrToList(str);
			addrMgrAdapter.setArrAddress(arrAddress);
			addrMgrAdapter.notifyDataSetChanged();			
		}				
	}
	
	private void addDelMenu(int deleteIndex){
		AddrManager_MenuClick menuClick = new AddrManager_MenuClick(deleteIndex, mHandler);
		Intent intent = new Intent();
		intent.setClass(this, AbsCuiniaoMenu.class);
		intent.putExtra("title", PageID.SETTING_MESSAGE_MENU_TITLE);
		intent.putExtra("items", PageID.ADDRMANAGER_MENU_ITEM);
		startActivity(intent);
		AbsCuiniaoMenu.set(menuClick);
	}

	
	@Override
	public void finish(){
		currAddrIndex = CommonUtil.getCurReceiveAddr(this);
		if(listView.getVisibility() == View.GONE){
			super.finish();
			return;
		}
		if(currAddrIndex == -1){
			CommonUtil.ShowToast(this, "请选择一个地址为常用地址");
			return;
		}
		super.finish();
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
