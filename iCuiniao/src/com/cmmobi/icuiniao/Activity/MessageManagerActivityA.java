/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.menuclick.CommentMsg_MenuClick;
import com.cmmobi.icuiniao.menuclick.Message_MenuClick;
import com.cmmobi.icuiniao.onlineEngine.activity.MyPageActivityA;
import com.cmmobi.icuiniao.ui.adapter.CommentMsgAdapter;
import com.cmmobi.icuiniao.ui.adapter.MessageAdapter;
import com.cmmobi.icuiniao.ui.view.ListviewForRefresh;
import com.cmmobi.icuiniao.ui.view.MainMenu;
import com.cmmobi.icuiniao.ui.view.ListviewForRefresh.OnContinusLoadListener;
import com.cmmobi.icuiniao.ui.view.ListviewForRefresh.OnRefreshListener;
import com.cmmobi.icuiniao.util.ActionID;
import com.cmmobi.icuiniao.util.Comment;
import com.cmmobi.icuiniao.util.CommentUtils;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.DownImageManager;
import com.cmmobi.icuiniao.util.DownLoadPool;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.MessgeManager;
import com.cmmobi.icuiniao.util.MySoftReference;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.OfflineTimeService;
import com.cmmobi.icuiniao.util.PageID;
import com.cmmobi.icuiniao.util.ReplayPermit;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;
import com.icuiniao.plug.im.Entity;
import com.icuiniao.plug.im.IMDataBase;


/**
 * @author hw
 *信息管理
 *增加消息评论
 */
public class MessageManagerActivityA extends Activity implements AnimationListener{

	private Button titlebar_backbutton;
	private Button titlebar_menubutton;
	private Button message_leftbtn;
	private Button message_rightbtn;
	private Button message_centerbtn;
	private ListView listview;
	private ListviewForRefresh listview_c;
	private MessageAdapter adapter;
	private String realurl;
	private MessgeManager manager;
	private int type;//0:用户消息,1:系统消息 2:评论消息
	private String noCache;
	
	private boolean isAnimationOpen;//主菜单动画状态
	private int animationIndex;
	private MainMenu mainMenu;
	private LinearLayout messagemanagerpage;
	
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;
	private Button titlebar_leftmenu;
	
	private final int TYPE_LEFT = 0;
	private final int TYPE_RIGHT = 1;
	private final int TYPE_CENTER = 2;
	
	private ConnectUtil mConnectUtil;
	private CommentMsgAdapter commentAdapter;
	private ArrayList<Comment> arrComment; 
	
	private int totalPage; //评论页数
//	private int totalcount; //评论总数
	
	private final int TAG_REFRESH = 1; //刷新
	private final int TAG_NEXT_DATA = 2; //下一页
	private final int TAG_COMMENT_REPLY = 3; //评论回复
	private final int TAG_MAY_REPLAY = 4;  //是否可以回复评论
	
	//以下为新版消息功能
	private List<Entity> entitys = null;
	private int messageCount = 0;
	private int notificationCount = 0;
	private int userId = 0;
	private IMDataBase imDataBase = null;
	private List<Entity> entities = null;
	private final int FRIENDTYPE = 0;
	private final int SYSTEMTYPE = 2;
	private int up = -1;
	private LinearLayout linearLogin = null;
	private TextView loginTextView = null;
	private Button loginButton = null;
	private TextView titlebar_titletext = null;
	private ImageView titleMessageImageId; //消息点
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//内部子项目的跳转不显示动画
		boolean isAnimation = getIntent().getBooleanExtra("animation", false);
		if(!isAnimation){
			setTheme(R.style.MessageActivityStyle);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messagemanagera);
		
		if(CommonUtil.isNormalInToApp == false){
			//===============调试模式需要在正式发布时关闭================
	        //启动/关闭 调试模式
	        LogPrint.isPrintLogMsg(false);
	        //启动/关闭 本地链接调试
	        URLUtil.setIsLocalUrl(false);
	        //=========================================================
	        init();
		}
		
		isAnimationOpen = false;
		CommonUtil.isInMessageScreen = true;
		messagemanagerpage = (LinearLayout)findViewById(R.id.messagemanagerpage);
		mainMenu = (MainMenu)findViewById(R.id.mainmenu);
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
		message_leftbtn = (Button)findViewById(R.id.message_leftbtn);
		message_rightbtn = (Button)findViewById(R.id.message_rightbtn);
		message_centerbtn = (Button)findViewById(R.id.message_centerbtn);
		listview = (ListView)findViewById(R.id.listview);
		listview.setScrollbarFadingEnabled(true);
		listview_c = (ListviewForRefresh)findViewById(R.id.listview_c);
		listview_c.setScrollbarFadingEnabled(true);
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		titlebar_leftmenu = (Button)findViewById(R.id.titlebar_leftmenu);
		titlebar_leftmenu.setOnClickListener(menuClickListener);
		type = getIntent().getExtras().getInt("type");
		imDataBase = new IMDataBase(this);
		linearLogin = (LinearLayout)findViewById(R.id.linearLogin);
		loginTextView = (TextView)findViewById(R.id.loginTextView);
		loginButton = (Button)findViewById(R.id.loginButton);
		loginButton.setOnClickListener(loginListener);
		titlebar_titletext = (TextView)findViewById(R.id.titlebar_titletext);
		titleMessageImageId = (ImageView)findViewById(R.id.messageImageId);
		
		userId = UserUtil.userid;
		if(type == TYPE_LEFT){
			message_leftbtn.setBackgroundResource(R.drawable.message_left);
			message_rightbtn.setBackgroundResource(R.drawable.message_right_empty);
			message_centerbtn.setBackgroundResource(R.drawable.message_center_empty);
			message_leftbtn.setTextColor(0xffffffff);
			message_rightbtn.setTextColor(0xcc000000);
			message_centerbtn.setTextColor(0xcc000000);
			listview_c.setVisibility(View.GONE);
			listview.setVisibility(View.VISIBLE);
		}else if(type == TYPE_CENTER){
			message_centerbtn.setBackgroundResource(R.drawable.message_center);	
			message_leftbtn.setBackgroundResource(R.drawable.message_left_empty);
			message_rightbtn.setBackgroundResource(R.drawable.message_right_empty);
			message_centerbtn.setTextColor(0xffffffff);
			message_leftbtn.setTextColor(0xcc000000);
			message_rightbtn.setTextColor(0xcc000000);			
			listview_c.setVisibility(View.VISIBLE);
			listview.setVisibility(View.GONE);
		}else if(type == TYPE_RIGHT){
			message_rightbtn.setBackgroundResource(R.drawable.message_right);
			message_centerbtn.setBackgroundResource(R.drawable.message_center_empty);
			message_leftbtn.setBackgroundResource(R.drawable.message_left_empty);	
			message_rightbtn.setTextColor(0xffffffff);	
			message_centerbtn.setTextColor(0xcc000000);
			message_leftbtn.setTextColor(0xcc000000);		
			listview_c.setVisibility(View.GONE);
			listview.setVisibility(View.VISIBLE);
		}
		titlebar_backbutton.setOnClickListener(backClickListener);
		titlebar_menubutton.setOnClickListener(menuClickListener);
		message_leftbtn.setOnClickListener(leftClickListener);
		message_rightbtn.setOnClickListener(rightClickListener);
		message_centerbtn.setOnClickListener(centerClickListener);
		listview.setOnItemLongClickListener(itemLongClickListener);
		
		addProgress();
		noCache = getIntent().getStringExtra("noCache");
		if(noCache == null){
			noCache = "";
		}

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
    	CommonUtil.createDirs(CommonUtil.dir_cache, true,MessageManagerActivityA.this);
    	CommonUtil.createDirs(CommonUtil.dir_user, true,MessageManagerActivityA.this);
    	CommonUtil.createDirs(CommonUtil.dir_cache_page, false, MessageManagerActivityA.this);
    	CommonUtil.createDirs(CommonUtil.dir_download, false, MessageManagerActivityA.this);
    	DisplayMetrics dm = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(dm);
    	//初始化用户
    	UserUtil.userid = CommonUtil.getUserId(getApplicationContext());
    	UserUtil.username = CommonUtil.getUserName(getApplicationContext());
    	UserUtil.logintype = CommonUtil.getLoginType(getApplicationContext());
    	UserUtil.gender = CommonUtil.getGender(getApplicationContext());
    	UserUtil.userState = CommonUtil.getUserState(getApplicationContext());
    	//redo at 2013.06.05
    	if(UserUtil.userState == 0){
    		CommonUtil.saveAddrManager(this, "");
    		CommonUtil.saveCurAddr(this, -1);
    		//新收货地址和撒娇对象清空
			CommonUtil.saveReceiveAddrMgr(this, "");
			CommonUtil.saveCurReceiveAddr(this, 0);
			CommonUtil.saveSajiaoObjMgr(this, "");
			CommonUtil.saveCurSajiaoObj(this, 0);
    	}    	
    	UserUtil.uid = -1;
    	UserUtil.callOnNum = CommonUtil.getCallOnNum(this);
    	CommonUtil.deleteAll(new File(CommonUtil.dir_cache));
    	OfflineLog.initAccessFile();
    	//启动离线日志服务
        startService(new Intent(this, OfflineTimeService.class));
        
        //商品价格和特征是否显示
        UserUtil.isShowPrice = CommonUtil.getFeatureShow(this);
        UserUtil.isShowPrice = CommonUtil.getPriceShow(this);
    }
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getMessageByUserId();
		registerReceiver();
		if(isAnimationOpen){
			gotoMenu();
		}
		registerReceiver();
		userId = UserUtil.userid;
		entitys = imDataBase.getEntities(userId);
		setCount();
		mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_INIT,0);
		DownImageManager.clear();
		up = CommonUtil.getImTop(MessageManagerActivityA.this);
	}

	public void finish(){
		if(CommonUtil.isNormalInToApp == false){
			buildExitDialog();
		}else{
			CommonUtil.isInMessageScreen = false;
			super.finish();
		}
	}
	
	public void finish(boolean restart){
		super.finish();
	}
	
	private OnClickListener menuClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			gotoMenu();
		}
	};
	
	private OnClickListener backClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
			
			Message_MenuClick menuClick = new Message_MenuClick(mHandler,arg2);
			Intent intent = new Intent();
			intent.setClass(MessageManagerActivityA.this, AbsCuiniaoMenu.class);
			if(type == 0){
				intent.putExtra("title", PageID.SETTING_MESSAGE_MENU_TITLE_NEW);
				intent.putExtra("items", PageID.SETTING_MESSAGE_MENU_ITEM_NEW);
			}
			//系统通知
			else if(type == 1){
				intent.putExtra("title", PageID.SETTING_MESSAGE_MENU_TITLE);
				intent.putExtra("items", PageID.ADDRMANAGER_MENU_ITEM);
			}
			startActivity(intent);
			AbsCuiniaoMenu.set(menuClick);
			return false;
		}
	};
	
	
	private OnClickListener leftClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(type != TYPE_LEFT){
				message_leftbtn.setBackgroundResource(R.drawable.message_left);
				message_rightbtn.setBackgroundResource(R.drawable.message_right_empty);
				message_centerbtn.setBackgroundResource(R.drawable.message_center_empty);
				type = TYPE_LEFT;
				Intent intent2 = new Intent();
				intent2.putExtra("type", TYPE_LEFT);//用户消息
				intent2.setClass(MessageManagerActivityA.this, MessageManagerActivityA.class);
				startActivity(intent2);
				finish(true);
			}
		}
	};
	

	private OnClickListener centerClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (type != TYPE_CENTER) {
				message_centerbtn.setBackgroundResource(R.drawable.message_center);
				message_leftbtn.setBackgroundResource(R.drawable.message_left_empty);
				message_rightbtn.setBackgroundResource(R.drawable.message_right_empty);
				type = TYPE_CENTER;
				Intent intent2 = new Intent();
				intent2.putExtra("type", TYPE_CENTER);// 系统消息
				intent2.setClass(MessageManagerActivityA.this,MessageManagerActivityA.class);
				startActivity(intent2);
				finish(true);
			}
		}
	};
	
	private OnClickListener rightClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(type != TYPE_RIGHT){
				message_rightbtn.setBackgroundResource(R.drawable.message_right);
				message_leftbtn.setBackgroundResource(R.drawable.message_left_empty);
				message_centerbtn.setBackgroundResource(R.drawable.message_center_empty);
				type = TYPE_RIGHT;
				Intent intent2 = new Intent();
				intent2.putExtra("type", TYPE_RIGHT);//系统消息
				intent2.setClass(MessageManagerActivityA.this, MessageManagerActivityA.class);
				startActivity(intent2);
				finish(true);
			}
		}
	};
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			//intent.putExtra("from", managerItems.get(arg2).getFrom());
			//intent.putExtra("to", managerItems.get(arg2).getTo());
			intent.setClass(MessageManagerActivityA.this, MessageActivity_A.class);
			int revicerUserId = entities.get(arg2).getRevicerUserId();
			String nickname = entities.get(arg2).getNickName();
			Bundle bundle = new Bundle();
			bundle.putInt("revicerUserId", revicerUserId);
			bundle.putString("nickname", nickname);
			bundle.putInt("type", type);
			bundle.putString("remarks", entities.get(arg2).getRemarks());
			intent.putExtras(bundle);
			startActivity(intent);
			finish(true);
			OfflineLog.writeMessage();//写入离线日志
		}
	};
	
	/**
	 * 获取本地缓存的评论消息个数
	 */
	private void setCacheCommentMsgCnt() {		
//		String urlStr = addUrlParam(URLUtil.URL_COMMENT_MSG, UserUtil.userid, 0);
//		String dir = CommonUtil.dir_cache_page + "/" + CommonUtil.urlToNum(urlStr);
//		byte[] data = CommonUtil.getSDCardFileByteArray(dir);		
//		if(data != null){
//			int josonLen = jsonCnt(data);
//			message_centerbtn.setText("评论(" + josonLen + ")");
//		}	
		message_centerbtn.setText("评论(" + CommentUtils.newCommCount + ")");
	}
	
	/**
	 * 获得消息评论列表所有商品id
	 * @return
	 */
	private ArrayList<String> getCommodityIdList(){
		ArrayList<String> arrCidList = new ArrayList<String>();
		if(commentAdapter != null){
			arrCidList =  commentAdapter.getAllCidList();
		}
		return arrCidList;
	}
	
	/**
	 * 根据解析到的评论条数决定是否有上推加载旧评论功能
	 * @param commentSize
	 */
	private void setFootRefreshable(){
		if(listview_c.currPage < totalPage-1){
			listview_c.setFootLoadListener(new OnContinusLoadListener(){

				@Override
				public void nextLoad(int page) {
					//tag==2表示 上推续加载数据 ; noCache为1，不使用缓存数据
					mConnectUtil = new ConnectUtil(
							MessageManagerActivityA.this, mHandler, 1,  TAG_NEXT_DATA);
					mConnectUtil.setShowProgress(false);
					String urlStr = addUrlParam(URLUtil.URL_COMMENT_MSG + "?plaid=" + URLUtil.plaid,
							UserUtil.userid, page);									
					mConnectUtil.connect(urlStr, HttpThread.TYPE_PAGE, TYPE_CENTER);
					
				}
				
			});
		}else{
			listview_c.setFootRefreshable(false);
		}
		
	}
	
	/**
	 * 设置消息和通知的个数
	 */
	private void setMessageCnt(){
//		manager = new MessgeManager();
//		manager.loadFile(CommonUtil.dir_message_user + UserUtil.userid
//				+ CommonUtil.endName);
//		message_leftbtn.setText("消息(" + manager.getMessageNum(0) + ")");
//		message_rightbtn
//				.setText("通知(" + manager.getMessageNum(1) + ")");	
		message_leftbtn.setText("消息(" + messageCount + ")");
		message_rightbtn.setText("通知(" + notificationCount + ")");
		message_centerbtn.setText("评论(" + CommentUtils.newCommCount + ")");
	}
	
	private OnClickListener loginListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			CommonUtil.ShowToast(MessageManagerActivityA.this, "请登陆或者注册，变身为小C的主人！");
			Intent intent11 = new Intent();
			intent11.setClass(MessageManagerActivityA.this, LoginAndRegeditActivity.class);
			startActivity(intent11);
		}
	};
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageID.MESSAGE_MENUCLICK_MESSAGE:
				int index = msg.arg1;
				int postion = msg.arg2;
				//删除选中消息信息
				Entity message = entities.get(postion);
				if(type == TYPE_LEFT){
					//点击确定
					if(index == 0){
						boolean isOk = imDataBase.deleteByFromid(message.getUserId(), message.getRevicerUserId());
						//删除成功,更新好友消息列表
						if(true){
							CommonUtil.saveImTop(MessageManagerActivityA.this, -1);
							entitys = imDataBase.getEntities(userId);
							messageCount = 0;
							setCount();
							adapter.getEntitys().remove(postion);
							adapter.notifyDataSetChanged();
							message_leftbtn.setText("消息(" + messageCount + ")");
							OfflineLog.writeDeleteMessage();
						}
					}
				}
				else if(type == TYPE_RIGHT){
					//点击删除
					if(index == 0){
						//删除选中系统消息信息
						boolean isOk = imDataBase.deleteSystemMessage(message.getUserId(),SYSTEMTYPE);
						if(isOk){
							entitys = getMessages(userId,SYSTEMTYPE);
							adapter.getEntitys().remove(postion);
							adapter.notifyDataSetChanged();
							message_rightbtn.setText("通知(" + 0 + ")");
							OfflineLog.writeDeleteMessage();
						}
					}
				}
				break;
			case MessageID.MESSAGE_CONNECT_START:
				addProgress();
				break;
			case MessageID.MESSAGE_CONNECT_ERROR:
				closeProgress();
				CommonUtil.ShowToast(getApplicationContext(), (String) msg.obj);
				listview_c.onFootRefreshComplete();	
				listview_c.onHeadRefreshComplete();				
				ReplayPermit.isMayClick = true;			
				break;
			case MessageID.MESSAGE_INIT:
				
				if(UserUtil.userid == -1||UserUtil.userState != 1){
					message_leftbtn.setText("消息(" + 0 + ")");
					message_centerbtn.setText("评论(" + 0 + ")");					
					titlebar_titletext.setText("消息中心[游客状态]");
					if(type == TYPE_LEFT){
						linearLogin.setVisibility(View.VISIBLE);
						loginTextView.setText("请登录翠鸟查看新消息");
					}
					else if(type == TYPE_CENTER){
						linearLogin.setVisibility(View.VISIBLE);
						loginTextView.setText("请登录翠鸟查看新评论");
					}	
					if(type!=TYPE_RIGHT){
						listview.setVisibility(View.GONE);
						listview_c.setVisibility(View.GONE);
					}
					else{
						message_rightbtn.setText("通知(" + notificationCount + ")");
						entities = getMessages(userId,SYSTEMTYPE);
						if(entities!=null){
							listview.setAdapter(adapter);
						}
						listview.setOnItemClickListener(itemClickListener);	
					}
					closeProgress();
					mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
				}
				else{
					linearLogin.setVisibility(View.GONE);					
					titlebar_titletext.setText("消息中心");
					// 评论
					if (type == TYPE_CENTER) {
						try {
							listview_c.setVisibility(View.VISIBLE);
							setMessageCnt();
							String urlStr = addUrlParam(URLUtil.URL_COMMENT_MSG + "?plaid=" + URLUtil.plaid,
									UserUtil.userid, 0);
							String dir = CommonUtil.dir_cache_page + "/"
									+ CommonUtil.urlToNum(urlStr);
							File file = new File(dir);
							// 先判断文件是否存在 .并且没有新评论
							if (!noCache.equals("noCache") && file.exists() && file.length() > 0 && CommentUtils.newCommCount == 0) {
								byte[] data = CommonUtil.getSDCardFileByteArray(dir);												
								Message msg1 = new Message();
								msg1.obj = data;
								msg1.arg1 = TYPE_CENTER;
								msg1.what = MessageID.MESSAGE_CONNECT_DOWNLOADOVER;							
								mHandler.sendMessage(msg1);
							} else {
								mConnectUtil = new ConnectUtil(MessageManagerActivityA.this, mHandler, 0);
								mConnectUtil.connect(urlStr, HttpThread.TYPE_PAGE,TYPE_CENTER);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						return;
					}
					listview.setVisibility(View.VISIBLE);
					message_leftbtn.setText("消息(" + messageCount + ")");
					message_rightbtn.setText("通知(" + notificationCount + ")");
					//获取好友消息信息
					if(type==TYPE_LEFT){
						entities = getMessages(userId,FRIENDTYPE);
					}
					else if(type==TYPE_RIGHT){
						entities = getMessages(userId,SYSTEMTYPE);
					}
					adapter = new MessageAdapter(MessageManagerActivityA.this, entities);
					if(entities!=null){
						listview.setAdapter(adapter);
					}
					listview.setOnItemClickListener(itemClickListener);			
					closeProgress();
					mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
									
					break;
				}
				

			case MessageID.MESSAGE_CONNECT_LAYOUTOVER:
				// 取出一个可用的图片下载
				DownImageItem downImageItem = DownImageManager.get();
				if (downImageItem != null) {
					LogPrint.Print("image downloading");
					String urlString = downImageItem.getUrl();
					LogPrint.Print("urlString =  " + urlString);
					if (urlString != null) {
						new ConnectUtil(MessageManagerActivityA.this, mHandler,0).connect(urlString, HttpThread.TYPE_IMAGE,downImageItem.getIndex());
					}
					// 发起下一个询问
					mHandler.sendEmptyMessageDelayed(
							MessageID.MESSAGE_CONNECT_LAYOUTOVER, 100);
				} else {
					LogPrint.Print("downImageItem = null");
				}
				break;
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				try {					
					if (msg.arg1 == TYPE_CENTER && type == TYPE_CENTER) {
						byte[] data = (byte[]) msg.obj;	
						// 写缓存 (url为空，说明读取了本地缓存)
						String url = msg.getData().getString("mUrl");						
						if(url != null){
						//截断&deviceid之后的内容
						final int idxDevice = url.indexOf("&deviceid");
						String subUrl = url.substring(0, idxDevice);
						//没有评论，则不缓存，并删除缓存文件
						String fileName = CommonUtil.dir_cache_page + "/"
						+ CommonUtil.urlToNum(subUrl);
							if(!isNoResult(data)){
								CommonUtil.writeToFile(fileName, data);
							}else{
								File file = new File(fileName);
								if(file.exists()){
									file.delete();
								}
							}
						}							
						//tag==1为刷新（下拉刷新和回复后的刷新）
						if(msg.getData().getInt("tag") == TAG_REFRESH){
							arrComment = Json(data, true);
							message_centerbtn.setText("评论(" + CommentUtils.newCommCount + ")");
							commentAdapter.setArrComment(arrComment);
							commentAdapter.notifyDataSetChanged();
							listview_c.onHeadRefreshComplete();
							listview_c.setSelection(0);
							listview_c.currPage = 0;
							setFootRefreshable();
						//向下续加载数据
						}else if(msg.getData().getInt("tag") == TAG_NEXT_DATA){
							ArrayList<Comment> temp = Json((byte[]) msg.obj, false);
							//如果没有数据，则currpage需要恢复
							if(temp.size() == 0){
								listview_c.currPage --;
							}else{
								arrComment.addAll(temp);
							}					
							commentAdapter.setArrComment(arrComment);
							commentAdapter.notifyDataSetChanged();
							listview_c.onFootRefreshComplete();	
							setFootRefreshable();
						//评论消息回复
						}else if(msg.getData().getInt("tag") == TAG_COMMENT_REPLY){
							jsonCommentResult(data);
						}else if(msg.getData().getInt("tag") == TAG_MAY_REPLAY){  //评论回复许可
							ReplayPermit.replayComment(MessageManagerActivityA.this, msg);							
						}else{							
							arrComment = Json(data, true);
							message_centerbtn.setText("评论(" + CommentUtils.newCommCount + ")");
							commentAdapter = new CommentMsgAdapter(arrComment, mHandler,
									MessageManagerActivityA.this);						
							listview_c.setAdapter(commentAdapter);
							listview_c.setonRefreshListener(new OnRefreshListener() {
								@Override
								public void onRefresh() {
									String urlStr = addUrlParam(URLUtil.URL_COMMENT_MSG + "?plaid=" + URLUtil.plaid,
											UserUtil.userid, 0);
									//tag==1 为下拉刷新 ；  noCache==1，不使用缓存数据
									mConnectUtil = new ConnectUtil(
											MessageManagerActivityA.this, mHandler, 1, TAG_REFRESH);
									mConnectUtil.setShowProgress(false);
									mConnectUtil.connect(urlStr, HttpThread.TYPE_PAGE,
											TYPE_CENTER);
								}
							});
							setFootRefreshable();							
						}						
						closeProgress();
						return;
					}

					Bundle bundle = msg.getData();
					switch (bundle.getInt("mType")) {
					case HttpThread.TYPE_PAGE:
						realurl = new String((byte[]) msg.obj, "utf-8");
						new ConnectUtil(MessageManagerActivityA.this, mHandler,0).connect(realurl, HttpThread.TYPE_IMAGE,msg.arg1);
						break;
					case HttpThread.TYPE_IMAGE:
						// 保存图片
						if (!CommonUtil.exists(CommonUtil.dir_cache+ "/"+ CommonUtil.urlToNum(msg.getData().getString("mUrl")))) {
							CommonUtil.writeToFile(CommonUtil.dir_cache+ "/"+ CommonUtil.urlToNum(msg.getData().getString("mUrl")),(byte[]) msg.obj);
						}
						adapter.setImageView((byte[]) msg.obj);
						adapter.notifyDataSetChanged();
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case MessageID.MESSAGE_MAINMENU_OPEN:
				if (messagemanagerpage != null) {
					if (messagemanagerpage.getRight() > getMainMenuAnimationPos(50)) {
						messagemanagerpage.offsetLeftAndRight(animationIndex);
						messagemanagerpage.postInvalidate();
						mHandler
								.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_OPEN);
						animationIndex = -(CommonUtil.screen_width - getMainMenuAnimationPos(50)) / 5;
					}
				}
				break;
			case MessageID.MESSAGE_MAINMENU_CLOSE:
				if (messagemanagerpage != null) {
					if (messagemanagerpage.getLeft() < 0) {
						messagemanagerpage.offsetLeftAndRight(animationIndex);
						mHandler
								.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_CLOSE);
						messagemanagerpage.postInvalidate();
						animationIndex = (CommonUtil.screen_width - getMainMenuAnimationPos(50)) / 5;
					}
				}
				break;
				// 回复
			case MessageID.MESSAGE_MENUCLICK_COMMENTC_MSG_REPLY:
				if (UserUtil.userid != -1 && UserUtil.userState == 1) {
					mConnectUtil = new ConnectUtil(
							MessageManagerActivityA.this, mHandler, 1,  TAG_MAY_REPLAY);			
					int is_subject = msg.getData().getInt("is_subject");
					int subjectid = msg.getData().getInt("subjectid");
					int commentid = msg.getData().getInt("commentid");
					int cid = Integer.parseInt(msg.getData().getString("cid"));
					int touserid = msg.getData().getInt("touserid");
					String tousername = msg.getData().getString("tousername");
					//is_subject + "-" +  subjectid + "-" + commentid + cid + "-" + touserid  + "-" + tousername;
					String split = ReplayPermit.split;
					String parmValue = is_subject + split +  subjectid + split + commentid  + split + cid + split + touserid;
					mConnectUtil.connect(URLUtil.Url_IS_PRIVATE_MSG + "?oid=" + UserUtil.userid + "&tousername=" + tousername + "&parmValue=" + parmValue + "&uid=" + touserid , 
							HttpThread.TYPE_PAGE, TYPE_CENTER);
				} else {
					CommonUtil.ShowToast(MessageManagerActivityA.this,
							"小C的主人才能评论哦，登录或者注册成为小C的主人吧。");
					Intent intent11 = new Intent();
					intent11.setClass(MessageManagerActivityA.this,
							LoginAndRegeditActivity.class);
					startActivity(intent11);
				}

				break;
			// 进入商品
			case MessageID.MESSAGE_MENUCLICK_COMMENTC_MSG_COMMODITY:				
				final int selectIndex = msg.getData().getInt("position");
				buildUrl(selectIndex);
				if(MyPageActivityA.urls[0] != null){
					Intent intent1 = new Intent();
					intent1.setClass(MessageManagerActivityA.this, MyPageActivityA.class);
					intent1.putExtra("url", MyPageActivityA.urls[0]);
					intent1.putExtra("chickPos", 0);
					intent1.putExtra("type", PageID.PAGEID_MESSAGEMANAGER);
					startActivity(intent1);
				}
				break;
				// 刷新页面
			case MessageID.MESSAGE_FLUSH_PAGE:
				Intent intent = new Intent(MessageManagerActivityA.this,
						MessageManagerActivityA.class);
				intent.putExtra("type", TYPE_CENTER);//默认为用户消息
				intent.putExtra("noCache", "noCache");
				startActivity(intent);
				finish(true);
				break;
			//评论回复后的刷新
			case 112233:
				String urlStr = addUrlParam(URLUtil.URL_COMMENT_MSG + "?plaid=" + URLUtil.plaid,
						UserUtil.userid, 0);
				mConnectUtil = new ConnectUtil(
							MessageManagerActivityA.this, mHandler, TAG_REFRESH);
				mConnectUtil.connect(urlStr, HttpThread.TYPE_PAGE,
							TYPE_CENTER);
				break;
			//消息评论item点击(commentmsgadapter)
			case 223344:
				int position = msg.arg1;
				LogPrint.Print("lyb", "position = " + position );
				Comment comment = arrComment.get(position);
				CommentMsg_MenuClick menuClick = new CommentMsg_MenuClick(
						comment.isSubject, comment.userpage,
						comment.subjectid, comment.commentid, comment.userid, comment.username, comment.cid, mHandler);
				menuClick.setPosition(position);
				intent = new Intent(MessageManagerActivityA.this,
						AbsCuiniaoMenu.class);					
				intent.putExtra("title", PageID.MYPAGE_MENU_TITLE);
				intent.putExtra("items", PageID.COMMENT_MSG_MENU_ITEM);
				startActivity(intent);
				AbsCuiniaoMenu.set(menuClick);
				break;
				
			case 998877:
				//查询消息已读、未读
				long messageId = msg.getData().getLong("messageId");
				int state = imDataBase.getIsReadState(messageId);
				//已读
				if(state == 1){
					mainMenu.setMessageButtonRes(false);
					titleMessageImageId.setVisibility(View.GONE);
				}else if(state == 0 && UserUtil.userState == 1){
					mainMenu.setMessageButtonRes(true);
					titleMessageImageId.setVisibility(View.VISIBLE);
				}else if(state == 0 && UserUtil.userState != 1){
					mainMenu.setMessageButtonRes(true);
					titleMessageImageId.setVisibility(View.GONE);
				}else if(state == -1){
					mainMenu.setMessageButtonRes(false);
					titleMessageImageId.setVisibility(View.GONE);
				}
				break;
			}
		}

	};
	
	/**
	 * 评论消息回复结果解析
	 * @param jsonResult
	 */
	private void jsonCommentResult(byte[] data) {
		try {
			String str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			if (result != null) {
				if (result.equalsIgnoreCase("true")) {
					showCommentState("评论发布成功...");
					mHandler.sendEmptyMessage(112233);
				} else {
					String msg = jObject.getString("msg");
					if (msg != null) {
						showCommentState("评论发布失败!\n" + msg);
//						CommonUtil.ShowToast(MessageManagerActivity.this,
//								"发布失败!\n" + msg);
					} else {
						showCommentState("评论发布失败!");
//						CommonUtil.ShowToast(MessageManagerActivity.this,
//								"发布失败!");
					}
				}
			}
		} catch (Exception e) {

		}
	}

	private String msgString;
	private int input_cur;
	//评论消息回复对话框  都是多级评论
	public void buildCommentDialog(final int type, final int commentid,
			final String subjectid) {		
		input_cur = 100;
		LinearLayout l = (LinearLayout) getLayoutInflater().inflate(
				R.layout.comment_dialog, null);
		final EditText et = (EditText) l.findViewById(R.id.comment_dialog);
		final TextView input_num = (TextView) l.findViewById(R.id.input_num);
		et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {				
				input_num.setText("您还可输入" + input_cur + "字");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {				
				input_num.setText("您还可输入" + input_cur + "字");
			}

			@Override
			public void afterTextChanged(Editable s) {				
				if (et.getText().length() >= 100) {
					CommonUtil.ShowToast(MessageManagerActivityA.this,
							"主人，您写那么多字做咩？");
				}
				input_cur = 100 - et.getText().length();
				input_num.setText("您还可输入" + input_cur + "字");
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(
				MessageManagerActivityA.this);
		builder.setTitle("评论").setView(l).setPositiveButton("发布",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						showCommentState("评论发布中...");
						// 多级评论
						if (et.getText() == null) {
							msgString = "";
						} else {
							msgString = et.getText().toString();
						}
						addProgress();
						//nocache==1 不使用缓存  ； tag==1 刷新
						mConnectUtil = new ConnectUtil(
								MessageManagerActivityA.this, mHandler, 1, TAG_COMMENT_REPLY);						
						 mConnectUtil.connect(addUrlParam(URLUtil.URL_COMMENT_THIRDCOMMENT,
						 UserUtil.userid, subjectid, commentid, msgString),
						 HttpThread.TYPE_PAGE, TYPE_CENTER);

					}
				}).setNegativeButton("取消", null).show();
	}
	
	NotificationManager notificationManager;
	Notification notification;
	//显示发送状态
	private void showCommentState(String msg){
		if(notificationManager == null){
			notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		}
		notification = new Notification(R.drawable.notifytionicon, msg, System.currentTimeMillis());
		notification.defaults |= Notification.DEFAULT_VIBRATE;//震动通知
	    notification.flags |= Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
	    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
	    notification.setLatestEventInfo(this, null, msg, pendingIntent);
	    notificationManager.notify(0, notification);
	    notificationManager.cancel(0);
	}
	
  	/**
  	 * 多级评论
  	 * @param url
  	 * @param oid
  	 * @param cid
  	 * @return
  	 */
  	public String addUrlParam(String url,int oid,String subjectid, int commentid, String msg){		
		if(url.indexOf("?") > 0){
			return url+"&oid="+oid+"&subjectid="+subjectid + "&commentid=" + commentid + "&msg="+ msg;
		}
		return url+"?oid="+oid+"&subjectid="+subjectid + "&commentid=" + commentid + "&msg="+ msg;
	}  	

	
	/**
	 * 获得评论的个数
	 * @param data
	 * @return
	 */
	private int jsonCnt(byte[] data){
		try {
		String str = new String(data, "UTF-8");
		str = CommonUtil.formUrlEncode(str);			
		if(str.length() <= 2){
			return 0;
		}		
		JSONObject json = new JSONObject(str);
		CommentUtils.totalcount = json.getInt("totalcount");
		return CommentUtils.totalcount;			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	private boolean isNoResult(byte[] data){
		String str;		
		try {
			str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("lyb", "str = " + str);
			if(str.equals("[]")){	
				return true;
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 解析json数据
	 * @param data
	 */
	private ArrayList<Comment> Json(byte[] data, boolean isResetTotalPage) {
		String str;
		ArrayList<Comment> comments = new ArrayList<Comment>();
		try {
			
			str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);
			//没有数据，返回空json  {}
			if(str.length() <= 2){
				return comments;
			}
			JSONObject json = new JSONObject(str);
			if (isResetTotalPage) {
				int newCount = json.getInt("totalcount");
				CommentUtils.newCommCount = newCount - CommentUtils.totalcount;
				CommentUtils.totalcount = newCount;
				// 每页的条目数
				int pagesize = json.getInt("pagesize");
				//总页数
				totalPage = (CommentUtils.totalcount % pagesize == 0) ? (CommentUtils.totalcount / pagesize)
						: (CommentUtils.totalcount / pagesize) + 1;
				LogPrint.Print("lyb", "totalcount =" +CommentUtils. totalcount);
				LogPrint.Print("lyb", "totalPage =" + totalPage);				
			}
			JSONArray jsonArray = json.getJSONArray("comment");			
			//评论列表			
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject o = (JSONObject) jsonArray.get(i);
				Comment comment = new Comment();			
				comment.time = o.getString("time");
				comment.msg1 = o.getString("msg1");
				comment.msg2 = o.getString("msg2");
				comment.subjectid = o.getString("subjectid");
				comment.commentid = o.getString("commentid");
				//lyb 设置最新的评论id
				if(isResetTotalPage && i == 0){
					CommentUtils.newCommId = Integer.parseInt(comment.commentid);
				}
				comment.icon_src = o.getString("icon_src");
				comment.username = o.getString("username");
				comment.comment_to = o.getString("comment_to");
				comment.userpage = o.getString("userpage");
				comment.isSubject = o.getInt("is_subject");
				comment.cid = o.getString("cid");
				comment.userid = o.getInt("userid");
				comments.add(comment);
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return comments;
	}
	
	/**
	 * 获取评论消息的列表
	 * @param url
	 * @param oid
	 * @param pi
	 * @return
	 */
  	public String addUrlParam(String url,int oid,int pi){		
		if(url.indexOf("?") > 0){
			return url+"&oid="+oid+"&pi="+pi;
		}
		return url+"?oid="+oid+"&pi="+pi;
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
	
	public void buildExitDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(MessageManagerActivityA.this);
		builder.setTitle("确定要退出翠鸟吗?").setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				CommonUtil.isInMessageScreen = false;
				CommonUtil.isNormalInToApp = true;
				DownLoadPool.getInstance(MessageManagerActivityA.this).stop();
//				android.os.Process.killProcess(android.os.Process.myPid());
				MySoftReference.getInstance().clear();
				String cameraDirString = CommonUtil.getExtendsCardPath()+"iCuiniao/camera";
				CommonUtil.deleteAll(new File(cameraDirString));
				OfflineLog.writeExit();//写入离线日志
				finish();
				CommonUtil.saveLastExitTime(MessageManagerActivityA.this);//保存最后一次退出时间
			}
		}).setNegativeButton("取消", null).show();
	}
	
	/**
	 * 注册广播
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
 			String action = intent.getAction();
			if(action.equals(ActionID.ACTION_BROADCAST_MESSAGE_CHICK)){
				CommonUtil.ShowToast(MessageManagerActivityA.this, "主人，您有新的消息。");
				Intent intent2 = new Intent(MessageManagerActivityA.this,MessageManagerActivityA.class);
				startActivity(intent2);
				finish(true);
			}else if(action.equals(ActionID.ACTION_BROADCAST_COMMENT_COUNT)){
				message_centerbtn.setText("评论(" + CommentUtils.newCommCount + ")");
			}
			//接收好友消息广播-用户在消息中心
			else if(action.equals(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_MESSAGE)){
				messageCount = messageCount + 1;
				message_leftbtn.setText("消息(" + messageCount + ")");
				if(type!=1){
					entities = getMessages(userId,FRIENDTYPE);
					adapter = new MessageAdapter(MessageManagerActivityA.this, entities);
					listview.setAdapter(adapter);
				}
				sendMessageIntent(intent);
			}
			//接收系统消息广播-用户在消息中心
			else if(action.equals(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_SYSTEMMESSAGE)){
				notificationCount = notificationCount + 1;
				message_rightbtn.setText("通知(" + notificationCount + ")");
				if(type==1){
					entities = getMessages(userId,SYSTEMTYPE);
					adapter = new MessageAdapter(MessageManagerActivityA.this, entities);
					listview.setAdapter(adapter);
				}
				sendMessageIntent(intent);
			}
			//接收私信
			else if(action.equals(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_PRIVATEMESSAGE)){
				messageCount = messageCount + 1;
				message_leftbtn.setText("消息(" + messageCount + ")");
				if(type!=1){
					entities = getMessages(userId,FRIENDTYPE);
					adapter = new MessageAdapter(MessageManagerActivityA.this, entities);
					listview.setAdapter(adapter);
				}
				sendMessageIntent(intent);
			}
			//接收建立好友关系消息
			else if(action.equals(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE)){
				messageCount = messageCount + 1;
				message_leftbtn.setText("消息(" + messageCount + ")");
				if(type!=1){
					entities = getMessages(userId,FRIENDTYPE);
					adapter = new MessageAdapter(MessageManagerActivityA.this, entities);
					listview.setAdapter(adapter);
				}
				sendMessageIntent(intent);
			}
			//接收确认建立好友关系的消息
			else if(action.equals(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE_SUCCESS)){
				messageCount = messageCount + 1;
				message_leftbtn.setText("消息(" + messageCount + ")");
				if(type!=1){
					entities = getMessages(userId,FRIENDTYPE);
					adapter = new MessageAdapter(MessageManagerActivityA.this, entities);
					listview.setAdapter(adapter);
				}
				sendMessageIntent(intent);
			}
			//用户登出
			else if(ActionID.ACTION_BROADCAST_LOGOUT.equals(action)){
				
				sendMessageIntent(intent);
			}
		}
	};
	
	/**
	 * 注册广播接收器
	 */
	private final void registerReceiver() {
		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(ActionID.ACTION_BROADCAST_MESSAGE_CHICK);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_COMMENT_COUNT);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_MESSAGE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_SYSTEMMESSAGE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_PRIVATEMESSAGE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE_SUCCESS);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_LOGOUT);
		registerReceiver(receiver, intentFilter);
	}
	
	/**
	 * 卸载广播接收器
	 */
	private final void unRegisterReceiver() {
		if(receiver != null){
			try {
				unregisterReceiver(receiver);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unRegisterReceiver();
		super.onDestroy();
	}
	
	//code:9100
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 9100) {			
			boolean isSend = data.getExtras().getBoolean("issend");
			String msg = data.getExtras().getString("msg");
			int issubject = data.getExtras().getInt("issubject");
			int subjectid = data.getExtras().getInt("subjectid");
			int commentid = data.getExtras().getInt("commentid");
			if (isSend) {
				sendComment(issubject, msg, subjectid + "", commentid);
			}
		}
	}
	
	//只有多级评论
	public void sendComment(int issubject, String msgString, String subjectid,
			int commentid) {
		// nocache==1 不使用缓存 ；
		mConnectUtil = new ConnectUtil(MessageManagerActivityA.this, mHandler,
				1, TAG_COMMENT_REPLY);
		mConnectUtil.connect(addUrlParam(URLUtil.URL_COMMENT_THIRDCOMMENT,
				UserUtil.userid, subjectid, commentid, msgString),
				HttpThread.TYPE_PAGE, TYPE_CENTER);
		showCommentState("评论发布中...");

	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		gotoMenu();
		return true;
	}
	
	private void gotoMenu(){
		if(messagemanagerpage != null){
			isAnimationOpen = !isAnimationOpen;			
			if(isAnimationOpen){
				if(mainMenu != null){
					mainMenu.setIsCanClick(true);
				}
				mHandler.removeMessages(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.right_out);
	    		animation.setFillEnabled(true);
	    		animation.setFillAfter(true);
	    		animation.setAnimationListener(this);
	    		//动画播放，实际布局坐标不变
	    		messagemanagerpage.startAnimation(animation);
	    		OfflineLog.writeMainMenu();//写入离线日志
			}else{
				if(mainMenu != null){
					mainMenu.setIsCanClick(false);
				}
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.left_in);
				animation.setAnimationListener(this);
	    		animation.setFillEnabled(true);
	    		animation.setFillAfter(true);
	    		messagemanagerpage.startAnimation(animation);
			}						
		}		
	}
	
	private int getMainMenuAnimationPos(int dip){
		int result = CommonUtil.dip2px(this, dip);
		if(result%10 == 0){
			return result;
		}else{
			if(result%10 < 5){
				result -= result%10;
			}else{
				result += 10 - result%10;
			}
		}
		return result;
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if(isAnimationOpen){			
			titlebar_leftmenu.setVisibility(View.VISIBLE);			
			titlebar_menubutton.setClickable(false);
		}else{
			titlebar_menubutton.setClickable(true);
		}		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		if(!isAnimationOpen){			
			titlebar_leftmenu.setVisibility(View.INVISIBLE);		
		}
		
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int barY = 80 * CommonUtil.screen_width / 480 ;
//		LogPrint.Print("lyb", "barY =" + yb);
		if(isAnimationOpen){			
			if(ev.getY() <= barY+15){
				return super.dispatchTouchEvent(ev);
			}
//			if(ev.getAction() == MotionEvent.ACTION_DOWN || ev.getAction() == MotionEvent.ACTION_UP){
			final int offsetY = 40 * CommonUtil.screen_width / 480;
			ev.setLocation(ev.getX(), ev.getY() - offsetY);	
			return mainMenu.dispatchTouchEvent(ev);
//			}
		}
		return super.dispatchTouchEvent(ev);
	}
	
	public void buildUrl(int index){
		ArrayList<String> tmp = getCommodityIdList();
		if(tmp != null){
			MyPageActivityA.urls = null;
			MyPageActivityA.urls = new String[1];
			MyPageActivityA.urls[0] = URLUtil.URL_MYPAGE+"?cid="+tmp.get(index)+"&dpi="+URLUtil.dpi()+"&pi=0&plaid="+URLUtil.plaid;
		}
  	}
	
	
	/**
	 * 获取消息列表信息
	 * @param userId
	 * @return
	 */
	public List<Entity> getMessages(int userId,int type){
		entities = new ArrayList<Entity>();
		if(imDataBase != null){
			entities = imDataBase.getTheMessageByUserIdGroup(userId,type,up);
		}
		return entities;
	}
	
	/**
	 * 设置消息和系统消息未读总数
	 */
	public void setCount(){
		messageCount = 0;
		notificationCount = 0;
		if(entitys != null && entitys.size() > 0){
			for(int i=0;i<entitys.size();i++){
				Entity entity = entitys.get(i);
				//好友消息
				if(entity.getType()==0 || entity.getType()==1 || entity.getType()==3 || entity.getType()==4){
					//未读消息
					if(entity.getIsRead()==0){
						messageCount++;
					}
				}
				else if(entity.getType()==2){
					//未读消息
					if(entity.getIsRead()==0){
						notificationCount++;
					}
				}
			}
		}
		else{
			messageCount = 0;
			notificationCount = 0;
		}
	}
	/**
	 * 根据消息已读\未读状态
	 */
	private void getMessageByUserId(){
		
		ArrayList<Entity> entitys =  imDataBase.getTheMessageByUserId(UserUtil.userid);
		if(entitys != null){
			boolean isFind = false;
			for (Entity entity : entitys) {
				//未读
				if(entity.getIsRead() == 0 && entity.getFromId() != UserUtil.userid && UserUtil.userState == 1){
					isFind = true;
					break;
				}
			}
			if(isFind){
				mainMenu.setMessageButtonRes(true);
				titleMessageImageId.setVisibility(View.VISIBLE);
			}else{
				mainMenu.setMessageButtonRes(false);
				titleMessageImageId.setVisibility(View.GONE);
			}
			
		}else{
			mainMenu.setMessageButtonRes(false);
			titleMessageImageId.setVisibility(View.GONE);
		}
		
	}
	
	public void sendMessageIntent(Intent intent){
		
		Message message = new Message();
		message.what = 998877;
		Bundle bundle = new Bundle();
		if(intent.getExtras() != null){
			
			bundle.putLong("messageId", intent.getExtras().getLong("messageId"));
		}
		message.setData(bundle);
		mHandler.sendMessageDelayed(message, 1000);
	}
	
}
