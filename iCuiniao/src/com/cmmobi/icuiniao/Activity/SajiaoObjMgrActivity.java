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
import com.cmmobi.icuiniao.entity.SajiaoObj;
import com.cmmobi.icuiniao.menuclick.AddrManager_MenuClick;
import com.cmmobi.icuiniao.ui.adapter.SajiaoMgrAdapter;
import com.cmmobi.icuiniao.ui.view.LitviewNoOverScroll;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.PageID;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

public class SajiaoObjMgrActivity extends Activity {	
	
	private ProgressBar loadingBar;	
	private LitviewNoOverScroll listView;	
	private Button titlebar_backbutton;
	private Button titlebar_addbutton;
	private RelativeLayout empLinear;
	private Button addSajiaoObj;
	
	private ArrayList<SajiaoObj> arrSajiao;
	private String sajiaoObjMgr;//撒娇管理数据,jsonArray
	private int currSajiao;//当前选中的地址	
	private SajiaoMgrAdapter adapter;
	
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
		setContentView(R.layout.sajiaomgr);
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		titlebar_backbutton.setOnClickListener(backClickListener);
		titlebar_addbutton = (Button)findViewById(R.id.titlebar_addbutton);
		titlebar_addbutton.setOnClickListener(addClickListener);
		empLinear = (RelativeLayout)findViewById(R.id.empLinear);
		addSajiaoObj  = (Button)findViewById(R.id.addSajiaoObj);
		addSajiaoObj.setOnClickListener(addClickListener);
		listView = (LitviewNoOverScroll)findViewById(R.id.listSajiaoMgr);
		
		adapter = new SajiaoMgrAdapter(this, mHandler);				
		sajiaoObjMgr = CommonUtil.getSajiaoObjMgr(this);
		currSajiao = CommonUtil.getCurSajiaoObj(this);
		adapter.setCurrSajiao(currSajiao);
		listView.setAdapter(adapter);
		
		if(sajiaoObjMgr.length() <= 2)
		{
			mConnectUtil = new ConnectUtil(this, mHandler,0); //需参数oid，联网架构会加
			mConnectUtil.connect(URLUtil.URL_SAJIAO_OBJ_GET, HttpThread.TYPE_PAGE, THREAD_GET);
		}	
	}
	
	@Override
	public void onResume(){
		super.onResume();
		sajiaoObjMgr = CommonUtil.getSajiaoObjMgr(this);
		//本地没有数据就不更新了，减少联网
		if(sajiaoObjMgr.length() > 0){
			currSajiao = CommonUtil.getCurSajiaoObj(this);
			adapter.setCurrSajiao(currSajiao);
			refreshListData(sajiaoObjMgr);
		}
	}
	
	private OnClickListener addClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {			
			Intent intent = new Intent(SajiaoObjMgrActivity.this,
					AddSajiaoObjActivity.class);			
			startActivity(intent);		}
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
				switch (msg.arg1) {
				case THREAD_GET:
					JsonGetSajiaoList((byte[]) msg.obj);
					closeProgress();
					break;
				case THREAD_DEL:
					final String url = msg.getData().getString("mUrl");
					final String delIndex = CommonUtil.getSubString(url,
							"index=", "&id=");
					JsonDelResult((byte[]) msg.obj, Integer.valueOf(delIndex));
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
				final String id = arrSajiao.get(delIndex).id;
				mConnectUtil = new ConnectUtil(SajiaoObjMgrActivity.this, mHandler,0); //需参数oid，联网架构会加
				mConnectUtil.connect(URLUtil.URL_SAJIAO_OBJ_DEL + "?index=" + delIndex + "&id=" + id, HttpThread.TYPE_PAGE, THREAD_DEL);
				break;
			}
		}
	};
	
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
						if (arrSajiao.size() - 1 >= delIndex) {
							arrSajiao.remove(delIndex);
						}
						if (arrSajiao.size() == 0) {
							currSajiao = 0;
							empLinear.setVisibility(View.VISIBLE);
							listView.setVisibility(View.GONE);
							sajiaoObjMgr = "";
						} else {
							currSajiao = 0;
							adapter.setCurrSajiao(currSajiao);						
							sajiaoObjMgr = SajiaoObj
									.changeListToString(arrSajiao);
							refreshListData(sajiaoObjMgr);							
						}
						CommonUtil.saveSajiaoObjMgr(this, sajiaoObjMgr);
						CommonUtil.saveCurSajiaoObj(this, currSajiao);
					} else {
						CommonUtil.ShowToast(this, "删除失败");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
	
	//获取收货地址
	private void JsonGetSajiaoList(byte[] data) {
		try {
			LogPrint.Print("==========Json");
			String str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = " + str);
			sajiaoObjMgr = str;
			CommonUtil.saveSajiaoObjMgr(this, str);
			refreshListData(str);

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
			arrSajiao = SajiaoObj.changeStrToList(str);
			adapter.setArrSajiao(arrSajiao);
			adapter.notifyDataSetChanged();			
		}				
	}
	
	@Override
	public void finish(){
		currSajiao = CommonUtil.getCurSajiaoObj(this);
		if(listView.getVisibility() == View.GONE){
			super.finish();
			return;
		}
		if(currSajiao == -1){
			CommonUtil.ShowToast(this, "请选择一个常用撒娇对象");
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
