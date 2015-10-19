/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.menuclick.Message_MenuClick;
import com.cmmobi.icuiniao.onlineEngine.activity.UserPageActivity;
import com.cmmobi.icuiniao.ui.adapter.MessageAdapter;
import com.cmmobi.icuiniao.ui.view.MessageListItem;
import com.cmmobi.icuiniao.util.ActionID;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.DownImageManager;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.MessageItem;
import com.cmmobi.icuiniao.util.MessgeManager;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.OfflineTimeService;
import com.cmmobi.icuiniao.util.PageID;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

/**
 * @author hw
 *点对点的私信
 */
public class MessageActivity extends Activity{

	private int from;
	private int to;
	private int limit;
	
	private Button titlebar_backbutton;
	private Button message_loadbtn;
	private ListView listview;
	private EditText message_input;
	private Button sendbtn;
	private TextView titlebar_titletext;
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;
	
	private MessageAdapter adapter;
	private ArrayList<MessageListItem> items;
	private ArrayList<MessageItem> messageItems;
	
	private String realurl;
	private MessgeManager manager;
	private ConnectUtil mConnectUtil;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message);
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
		getWindow().setSoftInputMode(  
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		if(CommonUtil.isNormalInToApp == false){
			//===============调试模式需要在正式发布时关闭================
	        //启动/关闭 调试模式
	        LogPrint.isPrintLogMsg(false);
	        //启动/关闭 本地链接调试
	        URLUtil.setIsLocalUrl(false);
	        //=========================================================
	        init();
		}
		limit = 0;
		from = getIntent().getExtras().getInt("from");
		to = getIntent().getExtras().getInt("to");
		limit = getIntent().getExtras().getInt("limit");
		
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		message_loadbtn = (Button)findViewById(R.id.message_loadbtn);
		listview = (ListView)findViewById(R.id.listview);
		listview.setScrollbarFadingEnabled(true);
		message_input = (EditText)findViewById(R.id.message_input);
		sendbtn = (Button)findViewById(R.id.sendbtn);
		titlebar_titletext = (TextView)findViewById(R.id.titlebar_titletext);
		//lyb
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		
		titlebar_backbutton.setOnClickListener(backClickListener);
		message_loadbtn.setOnClickListener(loadClickListener);
		sendbtn.setOnClickListener(sendClickListener);
		
		addProgress();
		mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_INIT,200);
		DownImageManager.clear();
	}
	
	private void init(){
		try {
			ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
			String icuiniao_channel = bundle.getString("ICUINIAO_CHANNEL");
			LogPrint.Print("ICUINIAO_CHANNEL = "+icuiniao_channel);
			if(!"icuiniao_channel".equals(icuiniao_channel)){
				URLUtil.fid = icuiniao_channel;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	CommonUtil.createDirs(CommonUtil.dir_cache, true,MessageActivity.this);
    	CommonUtil.createDirs(CommonUtil.dir_user, true,MessageActivity.this);
    	CommonUtil.createDirs(CommonUtil.dir_cache_page, false, MessageActivity.this);
    	CommonUtil.createDirs(CommonUtil.dir_download, false, MessageActivity.this);
    	DisplayMetrics dm = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(dm);
    	CommonUtil.screen_width = dm.widthPixels;
    	CommonUtil.screen_height = dm.heightPixels;
    	LogPrint.Print("screen_width = "+CommonUtil.screen_width);
    	LogPrint.Print("screen_height = "+CommonUtil.screen_height);
    	//初始化用户
    	UserUtil.userid = CommonUtil.getUserId(getApplicationContext());
    	UserUtil.username = CommonUtil.getUserName(getApplicationContext());
    	UserUtil.logintype = CommonUtil.getLoginType(getApplicationContext());
    	UserUtil.gender = CommonUtil.getGender(getApplicationContext());
    	UserUtil.userState = CommonUtil.getUserState(getApplicationContext());
    	if(UserUtil.userid == -1){
    		CommonUtil.saveAddrManager(this, "");
    		CommonUtil.saveCurAddr(this, -1);
    	}else{
    		UserUtil.userState = 1;
    	}
    	String vidString = CommonUtil.getVId(this);
    	if("-1".equals(vidString)){
    		UserUtil.vid = createVId();
    		CommonUtil.saveVId(this, UserUtil.vid);
    	}else{
    		UserUtil.vid = vidString;
    	}
    	UserUtil.uid = -1;
    	UserUtil.callOnNum = CommonUtil.getCallOnNum(this);
    	CommonUtil.deleteAll(new File(CommonUtil.dir_cache));
    	OfflineLog.initAccessFile();
    	//启动离线日志服务
        startService(new Intent(this, OfflineTimeService.class));
    }
	
	public void finish(){
		Intent intent2 = new Intent();
		intent2.putExtra("type", 0);//默认为用户消息		
		intent2.setClass(MessageActivity.this, MessageManagerActivity.class);
		startActivity(intent2);
		super.finish();
		OfflineLog.writeMessageManager();//写入离线日志
	}
	
	public void finish(boolean restart){
		super.finish();
	}
	
	private OnClickListener backClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	};
	
	private OnClickListener loadClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.putExtra("from", from);
			intent.putExtra("to", to);
			intent.putExtra("limit", -1);
			intent.setClass(MessageActivity.this, MessageActivity.class);
			startActivity(intent);
			finish(true);
		}
	};
	
	private OnClickListener sendClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			if(imm != null){
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			if(message_input.getText() == null){
				msgString = "";
			}else{
				msgString = message_input.getText().toString();
			}
			mConnectUtil = new ConnectUtil(MessageActivity.this, mHandler,0);
			mConnectUtil.connect(addUrlParam(URLUtil.URL_MESSAGE_SEND, from==UserUtil.userid?from:to, to!=UserUtil.userid?to:from, msgString), HttpThread.TYPE_PAGE, 999999);
		}
	};
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Message_MenuClick menuClick = new Message_MenuClick(mHandler,arg2);
			Intent intent = new Intent();
			intent.setClass(MessageActivity.this, AbsCuiniaoMenu.class);
			intent.putExtra("title", PageID.SETTING_MESSAGE_MENU_TITLE);
			if(items.get(arg2).getPushId() == -1){
				intent.putExtra("items", PageID.SETTING_MESSAGE_MENU_ITEM);
			}else{
				intent.putExtra("items", PageID.SETTING_MESSAGE_MENU_ITEM1);
			}
			startActivity(intent);
			AbsCuiniaoMenu.set(menuClick);
		}
	};
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageID.MESSAGE_INIT:
				manager = new MessgeManager();
				manager.loadFile(CommonUtil.dir_message_user+UserUtil.userid+CommonUtil.endName);
				messageItems = manager.getMessageList(from, to, limit==-1?limit:3);
				if(messageItems.size() > 0){
					String title = messageItems.get(0).getToName();
					titlebar_titletext.setText(title);
				}
				items = new ArrayList<MessageListItem>();
				for(int i = 0;i < messageItems.size();i ++){
					items.add(new MessageListItem(MessageActivity.this, messageItems.get(i)));
				}
				//adapter = new MessageAdapter(items);
				listview.setAdapter(adapter);
				listview.setOnItemClickListener(itemClickListener);
				listview.setSelection(items.size()-1);//定位到最后
				closeProgress();
				
				mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
				break;
			case MessageID.MESSAGE_CONNECT_LAYOUTOVER:
				//取出一个可用的图片下载
				DownImageItem downImageItem = DownImageManager.get();
				if(downImageItem != null){
					LogPrint.Print("image downloading");
					String urlString = downImageItem.getUrl();
					LogPrint.Print("urlString =  "+urlString);
					if(urlString != null){
						new ConnectUtil(MessageActivity.this, mHandler,0).connect(urlString, HttpThread.TYPE_IMAGE, downImageItem.getIndex());
					}
					//发起下一个询问
					mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_CONNECT_LAYOUTOVER, 100);
				}else{
					LogPrint.Print("downImageItem = null");
				}
				break;
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				if(msg.arg1 == 999999){
					Json((byte[])msg.obj);
					closeProgress();
				}else{
					try {
						Bundle bundle = msg.getData();
						
						switch (bundle.getInt("mType")) {
						case HttpThread.TYPE_PAGE:
							realurl = new String((byte[])msg.obj,"utf-8");
							new ConnectUtil(MessageActivity.this, mHandler,0).connect(realurl, HttpThread.TYPE_IMAGE, msg.arg1);
							break;
						case HttpThread.TYPE_IMAGE:
							//保存图片
							if(!CommonUtil.exists(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(msg.getData().getString("mUrl")))){
								CommonUtil.writeToFile(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(msg.getData().getString("mUrl")), (byte[])msg.obj);
							}
							items.get(msg.arg1).setImageView((byte[])msg.obj);
							break;
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				break;
			case MessageID.MESSAGE_MENUCLICK_MESSAGE:
				switch (msg.arg1) {
				case 0://删除
					manager.deleteItem(messageItems.get(msg.arg2).getIndex());
					Intent intent = new Intent();
					intent.putExtra("from", from);
					intent.putExtra("to", to);
					intent.setClass(MessageActivity.this, MessageActivity.class);
					startActivity(intent);
					finish(true);
					CommonUtil.ShowToast(MessageActivity.this, "删除成功");
					break;
				case 1://推荐
					Intent intent4 = new Intent();
					intent4.putExtra("url", addUrlParam1(URLUtil.URL_USERPAGE, UserUtil.userid, items.get(msg.arg1).getPushId()));
					intent4.setClass(MessageActivity.this, UserPageActivity.class);
					startActivity(intent4);
					break;
				}
				break;
			}
		}
		
	};
	
	public String addUrlParam(String url,int oid,int uid,String msg){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		if(msg == null)msg="";
		return url+"?oid="+oid+"&uid="+uid+"&msg="+CommonUtil.toUrlEncode(msg);
	}
	
	public String addUrlParam1(String url,int oid,int uid){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"?oid="+oid+"&uid="+uid;
	}
	
	private String msgString;
	private void Json(byte[] data){
		try {
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = "+str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			if(result != null){
				if(result.equalsIgnoreCase("true")){
					CommonUtil.ShowToast(MessageActivity.this, "发送成功");
					String to = jObject.getString("to");
					String to_name = jObject.getString("to_name");
					String msg = jObject.getString("msg");
					String time = jObject.getString("time");
					//写入本地文件
					CommonUtil.writeMessage(CommonUtil.dir_message_user+UserUtil.userid+CommonUtil.endName, ""+UserUtil.userid, UserUtil.username, to, to_name, msg, time);
					Intent intent = new Intent();
					intent.putExtra("from", this.from);
					intent.putExtra("to", this.to);
					intent.setClass(MessageActivity.this, MessageActivity.class);
					startActivity(intent);
					finish(true);
				}else{
					String msg = jObject.getString("msg");
					if(msg != null){
						CommonUtil.ShowToast(MessageActivity.this, "发送失败!\n"+msg);
					}else{
						CommonUtil.ShowToast(MessageActivity.this, "发送失败!");
					}
				}
			}else{
				CommonUtil.ShowToast(MessageActivity.this, "发送失败");
			}
		} catch (Exception e) {
			// TODO: handle exception
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
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals(ActionID.ACTION_BROADCAST_MESSAGE_CHICK)){
				
			}
		}
	};
	
	/**
	 * 注册广播接收器
	 */
	private final void registerReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ActionID.ACTION_BROADCAST_MESSAGE_CHICK);
		registerReceiver(receiver, intentFilter);
	}
	
	/**
	 * 卸载广播接收器
	 */
	private final void unRegisterReceiver() {
		try {
			if(receiver != null){
				unregisterReceiver(receiver);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unRegisterReceiver();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
		registerReceiver();
		super.onResume();
	}
	
	//创建vid
	private String createVId(){
		long l = System.currentTimeMillis();
		int random = Math.abs(new Random().nextInt())%1000;
		return "v"+l+random;
	}
}
