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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.menuclick.AddrManager_MenuClick;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.PageID;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

/**
 * @author hw
 *
 */
public class AddrManagerActivity extends Activity{

	private Button titlebar_backbutton;
	private Button addbtn;
	
	private String addrManager;//地址管理数据,jsonArray
	private int curAddr;//当前选中的地址
	private JSONArray jsonArray;
	private ArrayList<Addr> list;
	
	private ConnectUtil mConnectUtil;
	private ExpandableListView expandableList;
	private int deleteIndex;
	private int curSelected;
	private boolean defaultExpand;//是否默认打开常用项
	private boolean isFirst;
	private Addr modifyaddr;
	private boolean isToDelete;
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;

	/**
	 * @author TangDeQing
	 *
	 */
	private AddTreeAdapter addressAdapter;
    private GroupViewHolder groupviewholder = null;
    private ChildViewHolder cvh = null;
    private View myGroupView;
    private View myChildView;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addrmanager);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		list = null;
		isFirst = true;
		isToDelete = false;
		defaultExpand = getIntent().getBooleanExtra("defaultExpand", false);
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		addbtn = (Button)findViewById(R.id.titlebar_addbutton);
		//lyb
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		expandableList = (ExpandableListView)findViewById(R.id.newsList);
		
		titlebar_backbutton.setOnClickListener(backClickListener);
		addbtn.setOnClickListener(addClickListener);
		addrManager = "";
		curAddr = 0;
		curSelected = -1;
		addrManager = CommonUtil.getAddrManager(this);		
		curAddr = CommonUtil.getCurAddr(this);
		if(addrManager.length() <= 0)
		{
			mConnectUtil = new ConnectUtil(this, mHandler,0);
			mConnectUtil.connect(URLUtil.URL_ADDRMANAGER_GET, HttpThread.TYPE_PAGE, 0);
		}else
		{
			refreshAddress();
		}				
	}
	
	private void refreshAddress()
	{
			try 
			{
				list = new ArrayList<Addr>();
				jsonArray = new JSONArray(addrManager);
				LogPrint.Print("====onCreate======jsonArray =" + jsonArray.length());
				for(int i = 0;jsonArray!=null&&i < jsonArray.length();i++)
				{
					JSONObject jObject = jsonArray.getJSONObject(i);
					String result = jObject.getString("result");
					if(result != null)
					{
						if(result.equalsIgnoreCase("true"))
						{
							Addr addr = new Addr();
							addr.email = jObject.getString("email");
							addr.addr = jObject.getString("addr");
							addr.phone = jObject.getString("phone");
							addr.name = jObject.getString("name");
							list.add(addr);
						}
					}
				}			
			} 
			catch (Exception e) 
			{
			}
			addressAdapter = new AddTreeAdapter(this);
			expandableList.setAdapter(addressAdapter);
			if(isFirst){
				isFirst = false;
				if(defaultExpand){
					cvh.email_input.setText(list.get(curAddr).email);
					cvh.addr_input.setText(list.get(curAddr).addr);
					cvh.phone_input.setText(list.get(curAddr).phone);
					cvh.name_input.setText(list.get(curAddr).name);
					expandableList.expandGroup(curAddr);
					editTextWatcher(curAddr);
				}
			}else{
				if(curSelected != -1){
					cvh.email_input.setText(list.get(curSelected).email);
					cvh.addr_input.setText(list.get(curSelected).addr);
					cvh.phone_input.setText(list.get(curSelected).phone);
					cvh.name_input.setText(list.get(curSelected).name);
					expandableList.expandGroup(curSelected);
					editTextWatcher(curSelected);
				}
			}
			
			expandableList.setOnGroupExpandListener(new OnGroupExpandListener(){
			@Override
			public void onGroupExpand(int groupPosition) {
					CommonUtil.hideSoftInput(expandableList, AddrManagerActivity.this);
					for(int i = 0; i < addressAdapter.getGroupCount();i++)
					{
					     if(groupPosition != i)
					     {
					    	 LogPrint.Print("onGroupExpand***i= "+i);
					    	 expandableList.collapseGroup(i);
					     }
					     else
					     {
				        	LogPrint.Print("onGroupExpand***groupPosition= "+groupPosition);
							cvh.email_input.setText(list.get(groupPosition).email);
							cvh.addr_input.setText(list.get(groupPosition).addr);
							cvh.phone_input.setText(list.get(groupPosition).phone);
							cvh.name_input.setText(list.get(groupPosition).name);
					     }
					}
		    	 	editTextWatcher(curAddr);
		    	 	modifyaddr = new Addr();
		    	 	modifyaddr.email = list.get(groupPosition).email;
					modifyaddr.addr = list.get(groupPosition).addr;
					modifyaddr.phone = list.get(groupPosition).phone;
					modifyaddr.name = list.get(groupPosition).name;
				}
			});
			//一级菜单的收起.输入法关闭
			expandableList.setOnGroupCollapseListener(new OnGroupCollapseListener(){

				@Override
				public void onGroupCollapse(int groupPosition) {
					CommonUtil.hideSoftInput(expandableList, AddrManagerActivity.this);
					
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
				LogPrint.Print("=====handleMessage=====1001!!!!!!!!!");	
				//lyb 增加删除成功的提示
				if(isToDelete){
					CommonUtil.ShowToast(AddrManagerActivity.this, "删除成功");
					if(deleteIndex == curAddr){
						curAddr = 0;
					}else if(deleteIndex < curAddr){
						curAddr --;
					}
					curSelected = curAddr;
					isToDelete = false;
				}else{
					CommonUtil.ShowToast(AddrManagerActivity.this, "保存成功");
				}
				CommonUtil.saveAddrManager(AddrManagerActivity.this, addrManager);
				CommonUtil.saveCurAddr(AddrManagerActivity.this, curAddr);
				refreshAddress();
				addressAdapter.notifyDataSetChanged();
				break;
			case 1002://上传失败
				closeProgress();
				LogPrint.Print("=====handleMessage=====1002!!!!!!!!!");
				//lyb 增加删除失败的提示
				if(isToDelete){
					CommonUtil.ShowToast(AddrManagerActivity.this, "删除失败");
				}else{
					CommonUtil.ShowToast(AddrManagerActivity.this, "保存失败");
				}				
				isToDelete = false;
				break;
			case 1003:
				addProgress();
				new Thread(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						LogPrint.Print("=====handleMessage=====1003!!!!!!!!!");
						updataAddr();
					}
					
				}.start();
				break;
			case 1004:
				if(curSelected == -1){
					break;
				}
				final String em = msg.getData().getString("email").trim();
//				cvh.email_input.setText(em);
//				cvh.email_input.setSelection(em.length());
//				cvh.email_input.requestFocus();
				mHandler.removeMessages(1004);							
				LogPrint.Print("1004======= "+msg.getData().getString("email"));	
				LogPrint.Print("1004===email==== "+list.get(curSelected).email);
				if(!list.get(curSelected).email.equals(msg.getData().getString("email")))
				{					
					cvh.modifybtn.setBackgroundResource(R.drawable.addr_edit_0);
					if(modifyaddr == null){
						modifyaddr = new Addr();
						modifyaddr.email = list.get(curSelected).email;
						modifyaddr.addr = list.get(curSelected).addr;
						modifyaddr.phone = list.get(curSelected).phone;
						modifyaddr.name = list.get(curSelected).name;
					}
					modifyaddr.email = em;

					cvh.modifybtn.setOnClickListener(new OnClickListener() {
			            public void onClick(View v) {
			    			if(!CommonUtil.checkEmail(modifyaddr.email)){
			    				CommonUtil.ShowToast(AddrManagerActivity.this, "主人您可能填错邮箱了！");
			    				return;
			    			}
			    			if(modifyaddr.addr.length() <= 0){
			    				CommonUtil.ShowToast(AddrManagerActivity.this, "主人您可能填错地址了！");
			    				return;
			    			}
			    			if(modifyaddr.phone.length() != 11||!modifyaddr.phone.startsWith("1")){
			    				CommonUtil.ShowToast(AddrManagerActivity.this, "主人您把手机号填错了吧？");
			    				return;
			    			}
			    			if(modifyaddr.name.length() <= 0){
			    				CommonUtil.ShowToast(AddrManagerActivity.this, "主人您要闹哪样？名字都会填错？");
			    				return;
			    			}
			    			CommonUtil.hideSoftInput(v, AddrManagerActivity.this);
			    			list.set(curSelected,modifyaddr);
			    			updateModifyAddressData();
			            }
			        });
					addressAdapter.notifyDataSetChanged();
				}
				else
				{					
					cvh.modifybtn.setBackgroundResource(R.drawable.addr_edit_default);
					cvh.modifybtn.setClickable(false);
				}
				break;						
			case 1005:	
				if(curSelected == -1){
					break;
				}
				final String addr = msg.getData().getString("addr").trim();		
//				cvh.addr_input.setText(addr);
//				cvh.addr_input.setSelection(addr.length());
//				cvh.addr_input.requestFocus();
				mHandler.removeMessages(1005);							
				LogPrint.Print("1005======= "+msg.getData().getString("addr"));	
				LogPrint.Print("1005===addr==== "+list.get(curSelected).addr);
				if(!list.get(curSelected).addr.equals(msg.getData().getString("addr")))
				{

					cvh.modifybtn.setBackgroundResource(R.drawable.addr_edit_0);
					if(modifyaddr == null){
						modifyaddr = new Addr();
						modifyaddr.email = list.get(curSelected).email;
						modifyaddr.addr = list.get(curSelected).addr;
						modifyaddr.phone = list.get(curSelected).phone;
						modifyaddr.name = list.get(curSelected).name;
					}
					modifyaddr.addr = addr;

					cvh.modifybtn.setOnClickListener(new OnClickListener() {
			            public void onClick(View v) {
			            	if(!CommonUtil.checkEmail(modifyaddr.email)){
			    				CommonUtil.ShowToast(AddrManagerActivity.this, "主人您可能填错邮箱了！");
			    				return;
			    			}
			    			if(modifyaddr.addr.length() <= 0){
			    				CommonUtil.ShowToast(AddrManagerActivity.this, "主人您可能填错地址了！");
			    				return;
			    			}
			    			if(modifyaddr.phone.length() != 11||!modifyaddr.phone.startsWith("1")){
			    				CommonUtil.ShowToast(AddrManagerActivity.this, "主人您把手机号填错了吧？");
			    				return;
			    			}
			    			if(modifyaddr.name.length() <= 0){
			    				CommonUtil.ShowToast(AddrManagerActivity.this, "主人您要闹哪样？名字都会填错？");
			    				return;
			    			}
			    			CommonUtil.hideSoftInput(v, AddrManagerActivity.this);
			    			list.set(curSelected,modifyaddr);
			    			updateModifyAddressData();
			            }
			        });		
					addressAdapter.notifyDataSetChanged();
				}
				else
				{
					cvh.modifybtn.setBackgroundResource(R.drawable.addr_edit_default);
					cvh.modifybtn.setClickable(false);
				}
				break;				
			case 1006:		
				if(curSelected == -1){
					break;
				}
				final String phone = msg.getData().getString("phone").trim();
//				cvh.phone_input.setText(phone);
//				cvh.phone_input.setSelection(phone.length());
//				cvh.phone_input.requestFocus();
				mHandler.removeMessages(1006);							
				LogPrint.Print("1006======= "+msg.getData().getString("phone"));	
				LogPrint.Print("1006===phone==== "+list.get(curSelected).phone);
				if(!list.get(curSelected).phone.equals(msg.getData().getString("phone")))
				{
					cvh.modifybtn.setBackgroundResource(R.drawable.addr_edit_0);
					if(modifyaddr == null){
						modifyaddr = new Addr();
						modifyaddr.email = list.get(curSelected).email;
						modifyaddr.addr = list.get(curSelected).addr;
						modifyaddr.phone = list.get(curSelected).phone;
						modifyaddr.name = list.get(curSelected).name;
					}
					modifyaddr.phone = phone;
					
					cvh.modifybtn.setOnClickListener(new OnClickListener() {
			            public void onClick(View v) {	
			            	if(!CommonUtil.checkEmail(modifyaddr.email)){
			    				CommonUtil.ShowToast(AddrManagerActivity.this, "主人您可能填错邮箱了！");
			    				return;
			    			}
			    			if(modifyaddr.addr.length() <= 0){
			    				CommonUtil.ShowToast(AddrManagerActivity.this, "主人您可能填错地址了！");
			    				return;
			    			}
			    			if(modifyaddr.phone.length() != 11||!modifyaddr.phone.startsWith("1")){
			    				CommonUtil.ShowToast(AddrManagerActivity.this, "主人您把手机号填错了吧？");
			    				return;
			    			}
			    			if(modifyaddr.name.length() <= 0){
			    				CommonUtil.ShowToast(AddrManagerActivity.this, "主人您要闹哪样？名字都会填错？");
			    				return;
			    			}
			    			CommonUtil.hideSoftInput(v, AddrManagerActivity.this);
			            	list.set(curSelected,modifyaddr);
			    			updateModifyAddressData();
			            }
			        });			
					addressAdapter.notifyDataSetChanged();
				}
				else
				{
					cvh.modifybtn.setBackgroundResource(R.drawable.addr_edit_default);
					cvh.modifybtn.setClickable(false);
				}
				break;					
			case 1007:			
				if(curSelected == -1){
					break;
				}
				final String name = msg.getData().getString("name").trim();
//				cvh.name_input.setText(name);
//				cvh.name_input.setSelection(name.length());
//				cvh.name_input.requestFocus();
				mHandler.removeMessages(1007);							
				LogPrint.Print("1007======= "+msg.getData().getString("name"));	
				LogPrint.Print("1007===name==== "+list.get(curSelected).name);
				if(!list.get(curSelected).name.equals(msg.getData().getString("name")))
				{

					cvh.modifybtn.setBackgroundResource(R.drawable.addr_edit_0);
					if(modifyaddr == null){
						modifyaddr = new Addr();
						modifyaddr.email = list.get(curSelected).email;
						modifyaddr.addr = list.get(curSelected).addr;
						modifyaddr.phone = list.get(curSelected).phone;
						modifyaddr.name = list.get(curSelected).name;
					}
					modifyaddr.name = name;

					cvh.modifybtn.setOnClickListener(new OnClickListener() {
			            public void onClick(View v) {	
			            	if(!CommonUtil.checkEmail(modifyaddr.email)){
			    				CommonUtil.ShowToast(AddrManagerActivity.this, "主人您可能填错邮箱了！");
			    				return;
			    			}
			    			if(modifyaddr.addr.length() <= 0){
			    				CommonUtil.ShowToast(AddrManagerActivity.this, "主人您可能填错地址了！");
			    				return;
			    			}
			    			if(modifyaddr.phone.length() != 11||!modifyaddr.phone.startsWith("1")){
			    				CommonUtil.ShowToast(AddrManagerActivity.this, "主人您把手机号填错了吧？");
			    				return;
			    			}
			    			if(modifyaddr.name.length() <= 0){
			    				CommonUtil.ShowToast(AddrManagerActivity.this, "主人您要闹哪样？名字都会填错？");
			    				return;
			    			}
			    			CommonUtil.hideSoftInput(v, AddrManagerActivity.this);
			    			list.set(curSelected,modifyaddr);
			    			updateModifyAddressData();
			            }
			        });		
					addressAdapter.notifyDataSetChanged();
				}
				else
				{
					cvh.modifybtn.setBackgroundResource(R.drawable.addr_edit_default);
					cvh.modifybtn.setClickable(false);
				}
				break;
			case MessageID.MESSAGE_MENUCLICK_ADDRMANAGER:
				deleteIndex = msg.arg1;
            	list.remove(deleteIndex);
            	isToDelete = true;
            	updateDelAddressData();
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
	
	private OnClickListener addClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();			
			intent.setClass(AddrManagerActivity.this, AddAddressActivity.class);
			startActivity(intent);
			finish();
		}
	};	
	
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
			LogPrint.Print("==========Json");
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
				}else{
					Intent intent = new Intent();
					intent.setClass(AddrManagerActivity.this, AddAddressActivity.class);
					startActivity(intent);
					finish();
					return;
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
			refreshAddress();
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
	
	private void addMenu(int deleteIndex){
		AddrManager_MenuClick menuClick = new AddrManager_MenuClick(deleteIndex, mHandler);
		Intent intent = new Intent();
		intent.setClass(this, AbsCuiniaoMenu.class);
		intent.putExtra("title", PageID.SETTING_MESSAGE_MENU_TITLE);
		intent.putExtra("items", PageID.ADDRMANAGER_MENU_ITEM);
		startActivity(intent);
		AbsCuiniaoMenu.set(menuClick);
	}
	
	/**
	 * @author TangDeQing
	 *
	 */
	public class AddTreeAdapter extends BaseExpandableListAdapter {
	    private LayoutInflater inflater;
	    private LayoutInflater inflater1;
	    boolean isEmailTouch;
    	boolean isAddrTouch;
    	boolean isPhoneTouch;
    	boolean isNameTouch;

	    public AddTreeAdapter(Context c) {
	        this.inflater = LayoutInflater.from(c);
	        this.inflater1 = LayoutInflater.from(c);
	        isEmailTouch = false;
	    	isAddrTouch = false;
	    	isPhoneTouch = false;
	    	isNameTouch = false;
	    	myChildView = inflater1.inflate(R.layout.addresschild, null);
	        cvh = new ChildViewHolder();
	        cvh.email_input = (EditText)myChildView.findViewById(R.id.email_input);
	        cvh.addr_input = (EditText)myChildView.findViewById(R.id.addr_input);
	        cvh.phone_input = (EditText)myChildView.findViewById(R.id.phone_input);
	        cvh.name_input = (EditText)myChildView.findViewById(R.id.name_input);
	        cvh.modifybtn = (Button)myChildView.findViewById(R.id.modifybtn);
//	        cvh.usedbtn = (Button)myChildView.findViewById(R.id.usedbtn);
	        cvh.delbtn = (Button)myChildView.findViewById(R.id.delbtn);
	    }

	    @Override
	    public Object getChild(int groupPosition, int childPosition) {
	        return childPosition;
	    }

	    @Override
	    public long getChildId(int groupPosition, int childPosition) {
	        return 0;
	    }

	    @Override
	    public View getChildView(int groupPosition, int childPosition,boolean isLastChild, View convertView, ViewGroup parent) 
	    {
	    	final int a=groupPosition;
	    	if(convertView == null)
	    	{
		        myChildView.setBackgroundResource(R.drawable.selectbg_line2_f);	        
				if(list.size() <= 1)
				{
					cvh.delbtn.setVisibility(View.GONE);
				}else{
					cvh.delbtn.setVisibility(View.VISIBLE);
					cvh.delbtn.setOnClickListener(new OnClickListener() {
			            public void onClick(View arg0) {
			            	LogPrint.Print("delbtn groupPosition= "+a);
			            	addMenu(a);
			            }
			        });  	    			
				}
				myChildView.setTag(cvh);
			}
	    	else{
				cvh = (ChildViewHolder)myChildView.getTag();
				if(list.size() <= 1)
				{
					cvh.delbtn.setVisibility(View.GONE);
				}else{
					cvh.delbtn.setVisibility(View.VISIBLE);
					cvh.delbtn.setOnClickListener(new OnClickListener() {
			            public void onClick(View arg0) {
			            	LogPrint.Print("delbtn groupPosition= "+a);
			            	addMenu(a);
			            }
			        });  	    			
				}
				//del by lyb at 2012-12-06
//				if(curAddr == groupPosition){
//					cvh.usedbtn.setBackgroundResource(R.drawable.addr_usedbtn);
//				}else{
//					cvh.usedbtn.setBackgroundResource(R.drawable.addr_setusedbtn);
//					cvh.usedbtn.setOnClickListener(new OnClickListener() {
//						
//						@Override
//						public void onClick(View v) {
//							// TODO Auto-generated method stub
//							cvh.usedbtn.setBackgroundResource(R.drawable.addr_usedbtn);
//							curAddr = a;
//							CommonUtil.saveCurAddr(AddrManagerActivity.this, curAddr);
//							LogPrint.Print("=======curAddr===" + curAddr);
//							notifyDataSetInvalidated();
//						}
//					});
//				}
			}
	    	//软键盘出现后会影响布局,导致expandListView和EditText的焦点冲突
	    	//这里需要重新设置焦点,否则无法进行输入
	    	cvh.email_input.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if(event.getAction() == MotionEvent.ACTION_UP){
						isAddrTouch = false;
						isEmailTouch = true;
				    	isPhoneTouch = false;
				    	isNameTouch = false;
					}
					return false;
				}
			});
	    	cvh.addr_input.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if(event.getAction() == MotionEvent.ACTION_UP){
						isAddrTouch = true;
						isEmailTouch = false;
				    	isPhoneTouch = false;
				    	isNameTouch = false;
					}
					return false;
				}
			});
	    	cvh.phone_input.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if(event.getAction() == MotionEvent.ACTION_UP){
						isAddrTouch = false;
						isEmailTouch = false;
				    	isPhoneTouch = true;
				    	isNameTouch = false;
					}
					return false;
				}
			});
	    	cvh.name_input.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if(event.getAction() == MotionEvent.ACTION_UP){
						isAddrTouch = false;
						isEmailTouch = false;
				    	isPhoneTouch = false;
				    	isNameTouch = true;
					}
					return false;
				}
			});
	    	if(isEmailTouch){
	    		cvh.email_input.requestFocus();
	    	}
	    	if(isAddrTouch){
	    		cvh.addr_input.requestFocus();
	    	}
	    	if(isPhoneTouch){
	    		cvh.phone_input.requestFocus();
	    	}
	    	if(isNameTouch){
	    		cvh.name_input.requestFocus();
	    	}
	        return myChildView;
	    }

	    @Override
	    public int getChildrenCount(int groupPosition) {
	    	return 1;
	    }

	    @Override
	    public Object getGroup(int groupPosition) {
	        return "addressgroup";
	    }

	    @Override
	    public int getGroupCount() {
	        return list.size();
	    }

	    @Override
	    public long getGroupId(int groupPosition) {
	        return groupPosition;
	    }
	   
	    @Override
		public View getGroupView(final int groupPosition, final boolean isExpanded,
				final View convertView, final ViewGroup parent) {
			myGroupView = inflater.inflate(R.layout.addressgroup, null);
			groupviewholder = new GroupViewHolder();
			groupviewholder.used_image = (ImageView) myGroupView.findViewById(R.id.used);
//			groupviewholder.used_image.setTag("true");
			groupviewholder.name_textview = (TextView) myGroupView.findViewById(R.id.name);
			groupviewholder.address_textview = (TextView) myGroupView.findViewById(R.id.address);
			groupviewholder.name_textview.setText(list.get(groupPosition).name);
			groupviewholder.address_textview.setText(list.get(groupPosition).addr);
			//2012-12-5 lyb		
			
			groupviewholder.used_image.setOnClickListener(new OnClickListener() {
				
	
				@Override
				public void onClick(View v) {
//					if(curAddr == groupPosition){
//						v.setBackgroundResource(R.drawable.select1);					
//						
//					}else{
//						v.setBackgroundResource(R.drawable.select1_f);
//						
//					}
					curAddr = groupPosition;
					CommonUtil.saveCurAddr(AddrManagerActivity.this, curAddr);
					notifyDataSetInvalidated();
					
				}
			});
			
			if (isExpanded) {
				curSelected = groupPosition;
//				groupviewholder.used_image.setVisibility(View.INVISIBLE);
				groupviewholder.name_textview.setTextColor(0xff6B696A);
				groupviewholder.address_textview.setTextColor(0xff6B696A);
				myGroupView.setBackgroundResource(R.drawable.selectbg_line1_f);
			} else {
//				groupviewholder.used_image.setVisibility(View.VISIBLE);
				groupviewholder.name_textview.setTextColor(0xcc000000);
				groupviewholder.address_textview.setTextColor(0xcc000000);
				myGroupView.setBackgroundResource(R.drawable.selectbg_line);
				
			}
			if(curAddr == groupPosition){
				groupviewholder.used_image.setBackgroundResource(R.drawable.select1_f);
//				groupviewholder.used_image.setImageResource(R.drawable.select1_f);
			}else{
				groupviewholder.used_image.setBackgroundResource(R.drawable.select1);
//				groupviewholder.used_image.setImageResource(R.drawable.select1);
			}
			return myGroupView;
		}

	    @Override
	    public boolean hasStableIds() {
	        return false;
	    }

	    @Override
	    public boolean isChildSelectable(int groupPosition, int childPosition) {
	        return false;
	    }		
	   
	} 	
	
	public void editTextWatcher(final int index) {
		cvh.email_input.addTextChangedListener(new TextWatcher() {				
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {			
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				LogPrint.Print("====email_input======index =" + index);
				LogPrint.Print("====email_input======email =" + s.toString());
				Message message = new Message();
				message.what = 1004;
				Bundle bundle  = new Bundle();
				bundle.putString("email", s.toString());
				message.setData(bundle);
				mHandler.sendMessage(message);					
			}
		});  
		
		cvh.addr_input.addTextChangedListener(new TextWatcher() {				
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {			
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				LogPrint.Print("====addr_input======index =" + index);
				Message message = new Message();
				message.what = 1005;
				Bundle bundle  = new Bundle();
				bundle.putString("addr", s.toString());
				message.setData(bundle);
				mHandler.sendMessage(message);	
			}
		});
		
		cvh.phone_input.addTextChangedListener(new TextWatcher() {				
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {			
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				LogPrint.Print("====phone_input======index =" + index);
				Message message = new Message();
				message.what = 1006;
				Bundle bundle  = new Bundle();
				bundle.putString("phone", s.toString());
				message.setData(bundle);
				mHandler.sendMessage(message);	
			}
		});
		
		cvh.name_input.addTextChangedListener(new TextWatcher() {				
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {			
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				LogPrint.Print("====name_input======index =" + index);
				Message message = new Message();
				message.what = 1007;
				Bundle bundle  = new Bundle();				
				bundle.putString("name", s.toString());
				message.setData(bundle);
				mHandler.sendMessage(message);	
				}
			});			    
		}		
	
		public void updateDelAddressData() {
        	try{
            	LogPrint.Print("updateDelAddressData list.size()= "+list.size());
    			//组合成jsonArray
    			JSONArray jsonArray = new JSONArray();
    			for(int i = 0;i < list.size();i++){
    				JSONObject jsonObject = new JSONObject();
    				jsonObject.put("result", true);
    				jsonObject.put("email", list.get(i).email);
    				jsonObject.put("addr", list.get(i).addr);
    				jsonObject.put("phone", list.get(i).phone);
    				jsonObject.put("name", list.get(i).name);
    				jsonArray.put(jsonObject);
    			} 			
	    		addrManager = jsonArray.toString();
    			//上传数据
    			mHandler.sendEmptyMessage(1003);
    		} catch (Exception e) {
    			// TODO: handle exception
    		}
		}
		
		public void updateModifyAddressData() {
        	try{
            	LogPrint.Print("updateModifyAddressData list.size()= "+list.size());
    			//组合成jsonArray
    			JSONArray jsonArray = new JSONArray();
    			for(int i = 0;i < list.size();i++){
    				JSONObject jsonObject = new JSONObject();
    				jsonObject.put("result", true);
    				jsonObject.put("email", list.get(i).email);
    				jsonObject.put("addr", list.get(i).addr);
    				jsonObject.put("phone", list.get(i).phone);
    				jsonObject.put("name", list.get(i).name);
    				jsonArray.put(jsonObject);
    			}   			
	    		addrManager = jsonArray.toString();
	    		LogPrint.Print("updateModifyAddressData addrManager= "+addrManager);
    			//上传数据
    			mHandler.sendEmptyMessage(1003);
    		} catch (Exception e) {
    			// TODO: handle exception
    		}
		}
		
		public final class ChildViewHolder{
			public EditText email_input = null;
			public EditText addr_input = null;
			public EditText phone_input = null;
			public EditText name_input = null;
			public Button modifybtn = null;
//			public Button usedbtn = null;
			public Button delbtn = null;
		}	
	
		public final class GroupViewHolder{
			public ImageView used_image = null;
			public TextView name_textview = null;
			public TextView address_textview = null;
		}		
}
