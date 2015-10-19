package com.cmmobi.icuiniao.onlineEngine.activity;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.Activity;
import com.cmmobi.icuiniao.Activity.SayhiPageActivity;
import com.cmmobi.icuiniao.entity.Friend;
import com.cmmobi.icuiniao.entity.FriendList;
import com.cmmobi.icuiniao.entity.Result;
import com.cmmobi.icuiniao.store.DBFriendList;
import com.cmmobi.icuiniao.ui.adapter.BlackListAdapter;
import com.cmmobi.icuiniao.ui.adapter.FriendListAdapter;
import com.cmmobi.icuiniao.ui.view.ListviewForRefresh;
import com.cmmobi.icuiniao.ui.view.MainMenu;
import com.cmmobi.icuiniao.ui.view.PopUserTitleMenu;
import com.cmmobi.icuiniao.ui.view.ListviewForRefresh.OnRefreshListener;
import com.cmmobi.icuiniao.util.ActionID;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MHandlerThread;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.PinyinUtils;
import com.cmmobi.icuiniao.util.ReplayPermit;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;
import com.icuiniao.plug.im.Entity;
import com.icuiniao.plug.im.IMDataBase;


public class FriendActivityA extends Activity implements View.OnClickListener, AnimationListener{

	
	private ProgressBar loadingBar;
	private ConnectUtil mConnectUtil;	
	private DBFriendList dbFriendList;
	private FriendListAdapter friendListAdapter; 
	private BlackListAdapter blackListAdapter;	
	private TextView titlebar_titletext;	//标题
	private ListviewForRefresh listview;
	private ImageView searchbox_left;
	private EditText searchbox_center;
	private ImageView searchbox_right;
	private TextView txtips;  //
	
	private LinearLayout friendLinear;  //好友或黑名单列表
	private RelativeLayout invateLinear;  //没有好友的邀请
	private Button invateBtn;
	private LinearLayout blackTipLinear; //没有黑名单的提示
	
	private LinearLayout titlebar_title;	
	private Button titlebar_menubutton;
	private Button titlebar_leftmenu;
	
	private LinearLayout linearPage;
	private MainMenu mainMenu;
	private boolean isAnimationOpen;//主菜单动画状态
	
	private ImageView messageImageId;
	
	//标题菜单的选中项  0:我的好友；1：黑名单； 2：我的好友
	private int index;	


	//联网的线程索引
	public static final int THREAD_FRIENDS = 0;
	public static final int THREAD_FRIENDS_NO_UI = 1;
	public static final int THREAD_BLACK = 2;
	public static final int THREAD_BLACK_NO_UI = 3;
	public static final int THREAD_DEL_BLACK = 4;
	public static final int THREAD_FRIEND_SINGLE = 5; //好友界面的单条刷新
	public static final int THREAD_BLACK_SINGLE = 6;  //黑名单界面的单条刷新
	private final int thread_is_addFriend = 7;
	private final int thread_addFried = 8;
	public static final int thread_is_refused = 9;
	
	
	private final int TAG_REFRESH = 1;   //刷新标志
	
	private ArrayList<Friend> arrFriend;
	private ArrayList<Friend> arrBlack;
	
	//启用数据库操作
	private IMDataBase imDataBase;
	
	OnTouchListener touchListener = new View.OnTouchListener() {
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
					CommonUtil.hideSoftInput(searchbox_center, FriendActivityA.this);
				}
				break;	
			}				
			return false;
		}
	};

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//从添加好友addFriendsAct界面返回的标题菜单选中项
		index = getIntent().getIntExtra("popIndex", -1);
		if(index > -1){
			setTheme(R.style.MessageActivityStyle);			
		}else{
			index = 0;
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendpagenew);
		if (UserUtil.userid == -1 || UserUtil.userState != 1) {
			finish();
			return;
		}		
		linearPage = (LinearLayout)findViewById(R.id.linearPage);		
		mainMenu = (MainMenu)findViewById(R.id.mainmenu);		
		listview = (ListviewForRefresh)findViewById(R.id.friendlist);
		//点击空白区，键盘消失
		listview.setOnTouchListener(touchListener);
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		searchbox_left = (ImageView)findViewById(R.id.searchbox_left);
		searchbox_center = (EditText)findViewById(R.id.searchbox_center);
		searchbox_right = (ImageView)findViewById(R.id.searchbox_right);
		txtips = (TextView)findViewById(R.id.txtips);
		titlebar_titletext = (TextView)findViewById(R.id.titlebar_titletext);
		titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
		titlebar_menubutton.setOnClickListener(this);
		titlebar_leftmenu = (Button)findViewById(R.id.titlebar_leftmenu); 
		titlebar_leftmenu.setOnClickListener(this);
		titlebar_title = (LinearLayout)findViewById(R.id.titlebar_title);
		titlebar_title.setOnClickListener(this);
		friendLinear = (LinearLayout)findViewById(R.id.friendLinear);
		invateLinear = (RelativeLayout)findViewById(R.id.invateLinear);
		invateBtn = (Button)findViewById(R.id.invateBtn);
		blackTipLinear  = (LinearLayout)findViewById(R.id.blackTipLinear);
		messageImageId = (ImageView)findViewById(R.id.messageImageId);
		newHandler();
		dbFriendList = new DBFriendList(this);
		dbFriendList.createBookIntoDB();
//		dbBlackList = new DB_blackList(this);
		if (index == PopUserTitleMenu.INDEX_FRIENDS) {
			titlebar_titletext.setText("我的好友(?)");			
			initFriends();
//			connect200FriendsTest();
		} else if (index == PopUserTitleMenu.INDEX_BLACK) {
			titlebar_titletext.setText("黑名单(?)");			
			initBlackList();
		}		
				
		searchbox_left.setOnClickListener(this);
		searchbox_center.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if(s.length() > 0){
					searchbox_right.setBackgroundResource(R.drawable.searchbox_right2);
					searchbox_right.setOnClickListener(FriendActivityA.this);
				}else{
					searchbox_right.setBackgroundResource(R.drawable.searchbox_right1);
					searchbox_right.setOnClickListener(null);
				}
				//数据库查询
				if(index == PopUserTitleMenu.INDEX_FRIENDS){
					FriendList friendList = dbFriendList.searchFriendByWord(s.toString(), Friend.BOOL_FRIEND);
					if(friendList != null && friendList.arrUsers.size() != 0){
						txtips.setVisibility(View.GONE);
						listview.setVisibility(View.VISIBLE);
						friendListAdapter.setFriendList(friendList.arrUsers);
						friendListAdapter.notifyDataSetChanged();
						
					}else{
						txtips.setVisibility(View.VISIBLE);
						listview.setVisibility(View.GONE);
					}
				}else if(index == PopUserTitleMenu.INDEX_BLACK){
					FriendList friendList = dbFriendList.searchFriendByWord(s.toString(), Friend.BOOL_BLACK);
					if(friendList != null && friendList.arrUsers.size() != 0){
						txtips.setVisibility(View.GONE);
						listview.setVisibility(View.VISIBLE);
						blackListAdapter.setFriendList(friendList.arrUsers);
						blackListAdapter.notifyDataSetChanged();
						
					}else{
						txtips.setVisibility(View.VISIBLE);
						listview.setVisibility(View.GONE);
					}
				}
				
				
			}
			
		});
		imDataBase = new IMDataBase(this);
		OfflineLog.writeFriend();
	};	
	
	@Override
	protected void onResume() {
		super.onResume();
		getMessageByUserId();
		//注册广播
		registerReceiver();
		
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
		//关闭键盘
		if(isAnimationOpen){
			gotoMenu();
		}
	}
	
//	@Override   
//	public boolean onTouchEvent(MotionEvent event) {   
//        // TODO Auto-generated method stub    
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {   
//            LogPrint.Print("action", "focus = " + getCurrentFocus());
//            if (getCurrentFocus() != null) {   
//                if (getCurrentFocus().getWindowToken() != null) {   
//                	CommonUtil.hideSoftInput(searchbox_center, FriendActivityA.this);  
//                }   
//            }   
//        }   
//        return super.onTouchEvent(event);   
//    }  
  
	/**
	 * 初始化好友列表数据
	 */
	private void initFriends(){		
		listview.setPullHeadTips("下拉可以刷新好友", "松开刷新好友");
		friendListAdapter = new FriendListAdapter(this, mHandler);
		if(dbFriendList.tabbleIsExist() && dbFriendList.readFriendCount(Friend.BOOL_FRIEND) != 0){			
			initFriendsFromDB();
			
		}else{
			connectFriendList(true);
		}
	}
	
	/**
	 * 从数据库获取数据初始化好友列表
	 */
	private void initFriendsFromDB(){
		addProgress();
		FriendList list = dbFriendList.readFriendList(Friend.BOOL_FRIEND);
		titlebar_titletext.setText("我的好友(" + list.arrUsers.size() + ")");
		blackTipLinear.setVisibility(View.GONE);
		if(list.arrUsers.size() == 0){
			friendLinear.setVisibility(View.GONE);
			invateLinear.setVisibility(View.VISIBLE);
			invateBtn.setOnClickListener(iniviteListener);
		}else{
			friendLinear.setVisibility(View.VISIBLE);
			invateLinear.setVisibility(View.GONE);
			friendListAdapter.setFriendList(list.arrUsers);			
			listview.setAdapter(friendListAdapter);				
			
		}
		closeProgress();
	}	

	
	/**
	 * 初始化黑名单列表
	 */
	private void initBlackList(){	
		listview.setPullHeadTips("下拉可以刷新黑名单", "松开刷新黑名单");
		blackListAdapter = new BlackListAdapter(this, mHandler);
		if(dbFriendList.tabbleIsExist()&& dbFriendList.readFriendCount(Friend.BOOL_BLACK) != 0){
			initBlackListFromDB();
		}else{
			connectBlackList(true);
		}
	}
	
	/**
	 * 从数据库获取数据初始化黑名单列表
	 */
	private void initBlackListFromDB(){
		addProgress();
		FriendList list = dbFriendList.readFriendList(Friend.BOOL_BLACK);
		titlebar_titletext.setText("黑名单(" + list.arrUsers.size() + ")");
		invateLinear.setVisibility(View.GONE);
		if(list.arrUsers.size() == 0){
			friendLinear.setVisibility(View.GONE);				
			blackTipLinear.setVisibility(View.VISIBLE);
		}else{
			friendLinear.setVisibility(View.VISIBLE);				
			blackTipLinear.setVisibility(View.GONE);
			blackListAdapter.setFriendList(list.arrUsers);
			listview.setAdapter(blackListAdapter);
		}			
		closeProgress();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.titlebar_title:
			PopUserTitleMenu popTitleMenu = new PopUserTitleMenu(FriendActivityA.this, R.style.dialog, mHandler, index);
			Window win = popTitleMenu.getWindow();
			win.setGravity(Gravity.TOP);				
			WindowManager.LayoutParams p2 = win.getAttributes();				
			p2.y = 50;  //向下偏移				
			win.setAttributes(p2);				
			popTitleMenu.show();
			break;
		case R.id.searchbox_right:
			searchbox_center.setText("");
			break;
		case R.id.titlebar_leftmenu:
		case R.id.titlebar_menubutton:
			gotoMenu();
			break;
		}
		
	}
	
//	private void connect200FriendsTest(){
//		friendListAdapter = new FriendListAdapter(this, mHandler);
//		String url = "http://223.202.18.52:37001/terminalconnector/struts/BaseBuyer!getFriends_Test";
//		mConnectUtil = new ConnectUtil(this, mHandler, 0);		
//		mConnectUtil.connect(url, HttpThread.TYPE_PAGE, THREAD_FRIENDS);
//	}
	
	private void refreshFriendList(boolean isProgressShow) {
		final String url = URLUtil.Url_FRIEND_LIST + "?oid=" + UserUtil.userid
				+ "&maxid=-1";
		mConnectUtil = new ConnectUtil(this, mHandler, false, TAG_REFRESH);
		mConnectUtil.connect(url, HttpThread.TYPE_PAGE, THREAD_FRIENDS);
	}
	
	private void refreshBlackList(boolean isPogressShow){
		mConnectUtil = new ConnectUtil(this, mHandler, false,  TAG_REFRESH);		
		final String url = URLUtil.Url_FRIEND_LIST+ "?oid=" + UserUtil.userid
		+ "&maxid=-1";		
		mConnectUtil.connect(url, HttpThread.TYPE_PAGE, THREAD_BLACK);
	}
	
	private void connectFriendList(boolean isInfriend) {
		final String url = URLUtil.Url_FRIEND_LIST + "?oid=" + UserUtil.userid
		+ "&maxid=-1";
		if(isInfriend){
			mConnectUtil = new ConnectUtil(this, mHandler, 0);		
			mConnectUtil.connect(url, HttpThread.TYPE_PAGE, THREAD_FRIENDS);
		}else{
			//在其他界面更新好友数据
			mConnectUtil = new ConnectUtil(this, mHandler, false, 0);		
			mConnectUtil.connect(url, HttpThread.TYPE_PAGE, THREAD_FRIENDS_NO_UI);
		}
				
	}
	
	private void connectBlackList(boolean isInBlack){
		mConnectUtil = new ConnectUtil(this, mHandler, isInBlack,  0);		
		final String url = URLUtil.Url_FRIEND_LIST+ "?oid=" + UserUtil.userid
		+ "&maxid=-1";
		if(isInBlack){
			mConnectUtil.connect(url, HttpThread.TYPE_PAGE, THREAD_BLACK);
		}else{
			mConnectUtil.connect(url, HttpThread.TYPE_PAGE, THREAD_BLACK_NO_UI);
		}			
	}
	
	private void connectDelBlack(int uid){
		mConnectUtil = new ConnectUtil(this, mHandler, 0);
		final String url = URLUtil.Url_DEL_BLACK + "?uid=" + uid + "&oid=" + UserUtil.userid;			
		mConnectUtil.connect(url, HttpThread.TYPE_PAGE, THREAD_DEL_BLACK);
	}
	
	private void connectIsBlackOrFriend(int uid, int uiIndex){
		mConnectUtil = new ConnectUtil(this,  downHandler, 1, 0);
		final String url = URLUtil.Url_FRIEND_OR_BLACK + "?oid=" + UserUtil.userid
		+ "&uid=" + uid;
		LogPrint.Print("lyb", "isBorF url = " + url);
		if(uiIndex == PopUserTitleMenu.INDEX_FRIENDS){
			mConnectUtil.connect(url, HttpThread.TYPE_PAGE, THREAD_FRIEND_SINGLE);
		}else if(uiIndex == PopUserTitleMenu.INDEX_BLACK){
			mConnectUtil.connect(url, HttpThread.TYPE_PAGE, THREAD_BLACK_SINGLE);
		}
		
	}	

	/**
	 * 解析
	 * @param data
	 * @param splitChar
	 * @return Result
	 */
	private Result jsonBoolResult(byte[] data, String splitChar){
		Result result = new Result();
		try {			
			String str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);			
			LogPrint.Print("lybconnect", "button json = " + str);
			if(str.length() <= 2){				
				return result;
			}
			JSONObject json = new JSONObject(str);
			String boolStr = json.getString("result");			
			if (boolStr != null) {
				result.boolResult = Boolean.parseBoolean(boolStr);			
			}
			String msg = json.getString("msg");
			if(msg != null){
				result.msgResult = splitChar + msg;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	//呼叫好友
	private View.OnClickListener iniviteListener = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(FriendActivityA.this, AddFriendsActivityA.class);
			intent.putExtra("uiIndex", 1);
			FriendActivityA.this.startActivity(intent);
			finish();
		}
		
	};
	
	/**
	 * 拆分黑名单和好友
	 * @param allFriendList
	 */
	private void splitFriendAndBlack(FriendList allFriendList){
		arrFriend = new ArrayList<Friend>();
		arrBlack = new ArrayList<Friend>();
		for(int i=0;i<allFriendList.arrUsers.size(); i++){
			Friend friend = allFriendList.arrUsers.get(i);
			friend.userPage = allFriendList.userpage +  "oid="+ UserUtil.userid + "&uid="+ friend.userid;
			if(friend.isblack == Friend.BOOL_FRIEND){
				arrFriend.add(friend);	
			}else if(friend.isblack == Friend.BOOL_BLACK){
				arrBlack.add(friend);
			}
		}		
	}
	
	/**
	 * 返回userid的用户信息(0:黑名单/1:好友/2/陌生人)
	 * @param data
	 * @param threadIndex
	 */
	private void jsonSingle(byte[] data, int threadIndex) {
		try {
			String str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("lybconnect", "jsonSingle = " + str);
			if (str.length() <= 2) {
				return;
			}
			JSONObject json = new JSONObject(str);			
			Friend friend = new Friend();
			friend.isblack = Integer.parseInt(json.getString("isblack"));
			friend.userid = Integer.parseInt(json.getString("userid"));
			dbFriendList = new DBFriendList(this);
			if (friend.isblack == 2) {
				int delLine = dbFriendList.removeByUserid(friend.userid);
				LogPrint.Print("lyb", "del =" + delLine);
			} else {
				friend.id = Integer.parseInt(json.getString("id"));
				friend.username = json.getString("username");
				friend.icon_src = json.getString("icon_src");
				friend.userPage = json.getString("userpage") + "oid="
						+ UserUtil.userid + "&uid=" + friend.userid;
				if (friend.username != null && !friend.username.equals("")) {
					friend.allpinyin = PinyinUtils.getPinyin(friend.username);
					friend.firstletter = PinyinUtils
							.getHeadLetterByString(friend.username);
				}
				dbFriendList.update(friend);
			}			


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateSingleUser(int threadIndex){
		if (threadIndex == THREAD_FRIEND_SINGLE) {
			initFriendsFromDB();
		} else if (threadIndex == THREAD_BLACK_SINGLE) {
			initBlackListFromDB();
		}
	}
	
	/**
	 * 解析好友及黑名单列表（一套数据，用friend.isblack进行区分）
	 * @param data
	 * @return
	 */
	private FriendList jsonUserList(byte[] data) {
		FriendList allList = new FriendList();
		try {
			String str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);
			JSONObject json = new JSONObject(str);
			//数据异常的处理
			if(!json.isNull("result")){
				boolean result = json.getBoolean("result");
				arrFriend = new ArrayList<Friend>();
				arrBlack = new ArrayList<Friend>();
				return allList;
			}
			JSONArray jsonArray = json.getJSONArray("users");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject userJson = jsonArray.getJSONObject(i);
				Friend friend = new Friend();
				friend.id = Integer.parseInt(userJson.getString("id"));
				friend.username = userJson.getString("username");
				friend.icon_src = userJson.getString("icon_src");
				friend.userid = Integer.parseInt(userJson.getString("userid"));
				friend.isblack = Integer
						.parseInt(userJson.getString("isblack"));
				friend.allpinyin = PinyinUtils.getPinyin(friend.username);
				friend.firstletter = PinyinUtils
						.getHeadLetterByString(friend.username);
				allList.arrUsers.add(friend);
			}
			allList.userpage = json.getString("userpage");
			splitFriendAndBlack(allList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allList;
	}
	
	private boolean jsonUserListNoResult(byte[] data, int threadIndex) {
		try {
			String str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("lybconnect", "jsonResultList = " + str);
			//好友列表没有项目的布局
			if (str.length() <= 2) {
				if (threadIndex == THREAD_FRIENDS) {					
					friendLinear.setVisibility(View.GONE);
					invateLinear.setVisibility(View.VISIBLE);
					blackTipLinear.setVisibility(View.GONE);
					invateBtn.setOnClickListener(iniviteListener);
					titlebar_titletext.setText("我的好友(" + 0 + ")");
					dbFriendList.reNewTable();
				}else if(threadIndex == THREAD_BLACK){
					invateLinear.setVisibility(View.GONE);	
					friendLinear.setVisibility(View.GONE);
					blackTipLinear.setVisibility(View.VISIBLE);
					titlebar_titletext.setText("黑名单(" + 0 + ")");
					dbFriendList.reNewTable();					
				}
				mHandler.sendEmptyMessage(MessageID.MESSAGE_CLOSEPROGRESS);
				return false;
			}
			Message msg = new Message();			
			msg.obj = data;
			msg.arg1 = threadIndex;
			msg.what = message_json;			
			downHandler.sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void updateUI(FriendList allList, int threadIndex){
		if (threadIndex == THREAD_FRIENDS) {
			blackTipLinear.setVisibility(View.GONE);
			if (arrFriend.size() > 0) {
				friendLinear.setVisibility(View.VISIBLE);					
				invateLinear.setVisibility(View.GONE);					
			}else{
				friendLinear.setVisibility(View.GONE);
				invateLinear.setVisibility(View.VISIBLE);					
				invateBtn.setOnClickListener(iniviteListener);
				titlebar_titletext.setText("我的好友(" + 0 + ")");
			}
		}else if(threadIndex == THREAD_BLACK){
			invateLinear.setVisibility(View.GONE);
			if(arrBlack.size() > 0){
				blackTipLinear.setVisibility(View.GONE);
				friendLinear.setVisibility(View.VISIBLE);	
			}else{
				titlebar_titletext.setText("黑名单(" + 0 + ")");				
				blackTipLinear.setVisibility(View.VISIBLE);
				friendLinear.setVisibility(View.GONE);				
				dbFriendList.reNewTable();
			}				
		}
		
		if (threadIndex == THREAD_FRIENDS) {
			titlebar_titletext.setText("我的好友("
					+ arrFriend.size() + ")");
			friendListAdapter.setFriendList(arrFriend);
			listview.setAdapter(friendListAdapter);
			sendMsgAddData(allList);
//			dbFriendList.reNewTable();
//			dbFriendList.insert(allList);
		} else if (threadIndex == THREAD_BLACK) {
			titlebar_titletext.setText("黑名单(" + arrBlack.size()
					+ ")");
			blackListAdapter.setFriendList(arrBlack);
			listview.setAdapter(blackListAdapter);
			sendMsgAddData(allList);
//			dbFriendList.reNewTable();
//			dbFriendList.insert(allList);
		} else if (threadIndex == THREAD_FRIENDS_NO_UI) {
			sendMsgAddData(allList);
//			dbFriendList.reNewTable();
//			dbFriendList.insert(allList);
		} else if (threadIndex == THREAD_BLACK_NO_UI) {
			sendMsgAddData(allList);
//			dbFriendList.reNewTable();
//			dbFriendList.insert(allList);
		}
	}
	
	

	
	private void sendMsgAddData(FriendList allList){
		Message msg = new Message();
		msg.obj = allList;
		msg.what = message_add_database;
		downHandler.sendMessage(msg);
//		dbFriendList.reNewTable();
//		dbFriendList.insert(allList);
	}

	/**
	 * 添加陌生人为好友的逻辑
	 * @param action
	 */
	private void addFrendButtonClick(final int uid, final String nickname){
		Intent intent = new Intent(this, SayhiPageActivity.class);		
		String url = URLUtil.Url_ADD_FRIEND + "?oid=" +
		 UserUtil.userid + "&uid=" + uid;
		intent.putExtra("url", url);
		intent.putExtra("title", "跟TA打个招呼");
		intent.putExtra("uid", uid);  //add at 6.17
		intent.putExtra("nickname", nickname);
		startActivityForResult(intent, MessageID.REQUEST_SAY_HI);	
	}
	
	/**
	 * 判断是否能加为好友
	 */
	private void connectIsAddFriend(int uid, String nickname){
		if(!ReplayPermit.isMayClick){
			return;
		}
		ReplayPermit.isMayClick = false;
		String url = URLUtil.Url_IS_ADD_FRIEND + "?nickname=" + nickname +   "&uid=" + uid +  "&oid=" +
		 UserUtil.userid;
		ConnectUtil connectUtil = new
		 ConnectUtil(this, mHandler, 1, uid);
		connectUtil.connect(url, HttpThread.TYPE_PAGE,
				thread_is_addFriend);
	}
	
	private MHandlerThread mHandlerThread;
	public Handler downHandler;
	private void newHandler(){
		mHandlerThread = new MHandlerThread("MyPageActivityA");
		mHandlerThread.start();
		downHandler = new Handler(mHandlerThread.getLooper(), mHandlerThread){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case MessageID.MESSAGE_CONNECT_START:
					mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_START);
					break;
				case MessageID.MESSAGE_CONNECT_ERROR:
					mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_ERROR);
					break;
				case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
					if(msg.arg1 == THREAD_FRIEND_SINGLE
							|| msg.arg1 == THREAD_BLACK_SINGLE){
						final int threadIndex = msg.arg1;
						jsonSingle((byte[]) msg.obj, msg.arg1);						
						msg = new Message();
						msg.arg1 = threadIndex;
						msg.what = message_update_singleUI;
						mHandler.sendMessage(msg);
					}
					break;
				case message_add_database:
					FriendList allList = (FriendList)msg.obj;
					dbFriendList.reNewTable();
					dbFriendList.insert(allList);
					break;				
				case message_json:
					FriendList friendList = jsonUserList((byte[])msg.obj);
					int threadIndex = msg.arg1;
					msg = new Message();
					msg.arg1 = threadIndex;
					msg.what = message_updateUI;
					msg.obj = friendList;
					mHandler.sendMessage(msg);
					break;
				case message_json_single:
					
					break;
				
				}
			}
		};
	}
	
	private final int message_tag_refresh = 10000;
	private final int message_add_database = 10001;
	private final int message_json = 10002;	
	private final int message_updateUI = 10003;
	private final int message_update_singleUI= 10004;
	private final int message_json_single = 10005;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageID.MESSAGE_CONNECT_START:
				addProgress();
				break;
			case MessageID.MESSAGE_CONNECT_ERROR:
				if(msg.arg1 == thread_is_addFriend || msg.arg1 == thread_is_refused){
					ReplayPermit.isMayClick = true;
				}
				closeProgress();
				CommonUtil.ShowToast(getApplicationContext(), (String) msg.obj);
				break;
			case MessageID.MESSAGE_CLOSEPROGRESS:
				closeProgress();
				break;
			case message_tag_refresh:
				listview.onHeadRefreshComplete();
				break;
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:				
				if ("text/json".equals(msg.getData().getString("content_type"))) {
					//是否可以加好友
					if (msg.arg1 == thread_is_addFriend) {
						Result result = jsonBoolResult((byte[])msg.obj, "");						
						if(result.boolResult){
							int uid = msg.getData().getInt("tag");
							String url = msg.getData().getString("mUrl");
							String nickname = CommonUtil.getSubString(url, "?nickname=", "&uid=");
							LogPrint.Print("lyb", "nickname =" + nickname);
							addFrendButtonClick(uid, nickname);
						}else{
							Toast.makeText(FriendActivityA.this, result.msgResult, Toast.LENGTH_SHORT).show();
							ReplayPermit.isMayClick = true;
						}
						mHandler.sendEmptyMessage(MessageID.MESSAGE_CLOSEPROGRESS);
					}else if(msg.arg1 == thread_addFried){
						Result result = jsonBoolResult((byte[])msg.obj, "\n");
						if(result.boolResult){
							Toast.makeText(FriendActivityA.this, "发送成功", Toast.LENGTH_SHORT).show();							
						}else{
							Toast.makeText(FriendActivityA.this, "发送失败" + result.msgResult, Toast.LENGTH_SHORT).show();
						}
						mHandler.sendEmptyMessage(MessageID.MESSAGE_CLOSEPROGRESS);
//					} else if(msg.arg1 == THREAD_FRIEND_SINGLE
//							|| msg.arg1 == THREAD_BLACK_SINGLE){
//						jsonSingle((byte[]) msg.obj, msg.arg1);
//						downHandler.sendEmptyMessage(message_update_single);
					}else if(msg.arg1 == thread_is_refused){
						mHandler.sendEmptyMessage(MessageID.MESSAGE_CLOSEPROGRESS);
						ReplayPermit.replaySendMsg(FriendActivityA.this, msg);
					}else {
						jsonUserListNoResult((byte[]) msg.obj, msg.arg1);
						if (msg.getData().getInt("tag") == TAG_REFRESH) {
//							listview.onHeadRefreshComplete();
							mHandler.sendEmptyMessage(message_tag_refresh);
						}						
					}
					
				}
				break;
			case MessageID.MESSAGE_POPTITLEMENU:
				index = msg.arg1;
				if (index == PopUserTitleMenu.INDEX_FRIENDS) {					
					invateLinear.setVisibility(View.GONE);
					titlebar_titletext.setText("我的好友(?)");
					initFriends();
					searchbox_center.setText("");
				} else if (index == PopUserTitleMenu.INDEX_BLACK) {					
					invateLinear.setVisibility(View.GONE);	
					friendLinear.setVisibility(View.VISIBLE);
					titlebar_titletext.setText("黑名单(?)");
					initBlackList();
					searchbox_center.setText("");
				}else if(index == PopUserTitleMenu.INDEX_ADD_FRIEND){
					Intent intent = new Intent(FriendActivityA.this, AddFriendsActivityA.class);
					FriendActivityA.this.startActivity(intent);
					FriendActivityA.this.finish();
				}
				break;
			case MessageID.MESSAGE_ADD_FRIEND:				
				final int uid = msg.getData().getInt("uid");
				final String nickname = msg.getData().getString("nickname");
				connectIsAddFriend(uid, nickname);
				break;
			case message_updateUI:				
				updateUI((FriendList)msg.obj, msg.arg1);
				mHandler.sendEmptyMessage(MessageID.MESSAGE_CLOSEPROGRESS);
				break;
			case message_update_singleUI:				
				updateSingleUser(msg.arg1);
				mHandler.sendEmptyMessage(MessageID.MESSAGE_CLOSEPROGRESS);
				break;
			case 998877:
				//查询消息已读、未读
				long messageId = msg.getData().getLong("messageId");
				int state = imDataBase.getIsReadState(messageId);
				//已读
				if(state == 1){
					mainMenu.setMessageButtonRes(false);
					messageImageId.setVisibility(View.GONE);
					
				}else if(state == 0 && UserUtil.userState == 1){
					mainMenu.setMessageButtonRes(true);
					messageImageId.setVisibility(View.VISIBLE);
				}else if(state == 0 && UserUtil.userState != 1){
					mainMenu.setMessageButtonRes(true);
					messageImageId.setVisibility(View.GONE);
				}else if(state == -1){
					mainMenu.setMessageButtonRes(false);
					messageImageId.setVisibility(View.GONE);
				}
				break;
			}
		}
	};	

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){	
		final String localRemark = CommonUtil.getRemark(FriendActivityA.this);
		String remarks = "";
		if(requestCode == 100){
			final int userid = data.getIntExtra("userid", 0);
			connectIsBlackOrFriend(userid, index);				
		}else if(requestCode == MessageID.REQUEST_SAY_HI){
			boolean issend = data.getExtras().getBoolean("issend");			
			if(issend){
				String msg = data.getExtras().getString("msg");					
				int toUserId = data.getExtras().getInt("uid");
				String toName = data.getExtras().getString("nickname");
				//取得备注名称
				if(localRemark.length() > 0){					
					remarks = localRemark;					
				}else{
					remarks = toName;
				}
				long tempmessageid = System.currentTimeMillis();
				final String date = CommonUtil.createCommentTime();
				int repeate = 0;
				Entity entity = imDataBase.getTheMessage(tempmessageid);
				if(entity == null){//type=3 isread=1					
					imDataBase.insertDB(UserUtil.userid, 0L, tempmessageid, UserUtil.userid, toUserId, UserUtil.username, toName, 1,msg, 0, date, remarks, 0, 3, 0);
				}else{
					repeate = 1;
					imDataBase.updataSendStateByTempMessageId(tempmessageid, 1);
				}
				Intent intent = new Intent(ActionID.ACTION_BROADCAST_REQUST_SEND_FRIENDMESSAGE);
				intent.putExtra("revicerUserId", toUserId);
				intent.putExtra("nickname", toName);
				intent.putExtra("remarks", remarks);
				intent.putExtra("repetSend", repeate);
				intent.putExtra("messageId", tempmessageid);
				intent.putExtra("message", msg);
				sendBroadcast(intent);	
			}
		}else if(requestCode == MessageID.REQUEST_SEND_MSG){
			boolean issend = data.getExtras().getBoolean("issend");			
			if(issend){
				String msg = data.getExtras().getString("msg");	
				int toUserId = data.getExtras().getInt("uid");
				String toName = data.getExtras().getString("nickname");
				//取得备注名称
				if(localRemark.length() > 0){					
					remarks = localRemark;					
				}else{
					remarks = toName;
				}
				long tempmessageid = System.currentTimeMillis();
				int repeate = 0;
				final String date = CommonUtil.createCommentTime();
				Entity entity = imDataBase.getTheMessage(tempmessageid);
				if(entity == null){					
					imDataBase.insertDB(UserUtil.userid, 0L, tempmessageid, UserUtil.userid, toUserId, UserUtil.username, toName, 1,msg, 0, date, remarks, 0, 0, 0);
				}else{
					repeate = 1;
					imDataBase.updataSendStateByTempMessageId(tempmessageid, 1);
				}
				Intent intent = new Intent(ActionID.ACTION_BROADCAST_REQUST_SEND_MESSAGE);
				intent.putExtra("revicerUserId", toUserId);
				intent.putExtra("nickname", toName);
				intent.putExtra("remarks", remarks);
				intent.putExtra("repetSend", repeate);
				intent.putExtra("messageId", tempmessageid);
				intent.putExtra("message", msg);
				sendBroadcast(intent);	
			}
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		gotoMenu();
		return true;
	}
	
	private void gotoMenu(){
		if(linearPage != null){
			isAnimationOpen = !isAnimationOpen;			
			if(isAnimationOpen){
				if(mainMenu != null){
					mainMenu.setIsCanClick(true);
				}				
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.right_out);
	    		animation.setFillEnabled(true);
	    		animation.setFillAfter(true);
	    		animation.setAnimationListener(this);
	    		//动画播放，实际布局坐标不变
	    		linearPage.startAnimation(animation);
	    		OfflineLog.writeMainMenu();//写入离线日志
			}else{
				if(mainMenu != null){
					mainMenu.setIsCanClick(false);
				}
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.left_in);
				animation.setAnimationListener(this);
	    		animation.setFillEnabled(true);
	    		animation.setFillAfter(true);
	    		linearPage.startAnimation(animation);
			}						
		}		
	}

	public void addProgress() {

		if (loadingBar != null) {
			loadingBar.setVisibility(View.VISIBLE);
		}
		listview.setHeadRefreshable(false);

	}

	public void closeProgress() {
		if (loadingBar != null) {
			loadingBar.setVisibility(View.INVISIBLE);
		}
		listview.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (index == PopUserTitleMenu.INDEX_FRIENDS) {
					refreshFriendList(false);
				} else if (index == PopUserTitleMenu.INDEX_BLACK) {
					refreshBlackList(false);
				}
			}
		});
		
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
		if (!isAnimationOpen) {			
			titlebar_leftmenu.setVisibility(View.INVISIBLE);
		}
		
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {		
		int barY = 80 * CommonUtil.screen_width / 480 ;
		if(isAnimationOpen){			
			if(ev.getY() <= barY+15){
				return super.dispatchTouchEvent(ev);
			}
			final int offsetY = 40 * CommonUtil.screen_width / 480;
			ev.setLocation(ev.getX(), ev.getY() - offsetY);	
			return mainMenu.dispatchTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
	}
	
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			//新增消息广播
			if(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_MESSAGE.equals(action)||
					ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_PRIVATEMESSAGE.equals(action)||
					ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_SYSTEMMESSAGE.equals(action)||
					ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE.equals(action)||
					ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE_SUCCESS.equals(action) ||
					ActionID.ACTION_BROADCAST_LOGOUT.equals(action)){
				Message message = new Message();
				message.what = 998877;
				Bundle bundle = new Bundle();
				if(intent.getExtras() != null){
					
					bundle.putLong("messageId", intent.getExtras().getLong("messageId"));
				}
				
				message.setData(bundle);
				mHandler.sendMessageDelayed(message, 1000);
			}else if(ActionID.ACTION_BROADCAST_REFRESH_FRIEND_LIST.equals(action)){
				if(index == PopUserTitleMenu.INDEX_FRIENDS){
					updateSingleUser(THREAD_FRIEND_SINGLE);
				}else if(index == PopUserTitleMenu.INDEX_BLACK){
					updateSingleUser(THREAD_BLACK_SINGLE);					
				}
			}
		}
		
	};
	/**
	 * 注册广播接收器
	 */
	private final void registerReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		//新增消息广播
		//接收到一条消息
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_MESSAGE);
		//接收到一条私信
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_PRIVATEMESSAGE);
		//接收到一条系统广播
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_SYSTEMMESSAGE);
		//接收建立好友关系消息
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE_SUCCESS);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_LOGOUT);
		//通过消息加好友成功后刷新UI
		intentFilter.addAction(ActionID.ACTION_BROADCAST_REFRESH_FRIEND_LIST);
		registerReceiver(mReceiver, intentFilter);
	}
	
	/**
	 * 卸载广播接收器
	 */
	private final void unRegisterReceiver() {
		try {
			if(mReceiver != null){
				unregisterReceiver(mReceiver);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//销毁广播
		unRegisterReceiver();
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
				messageImageId.setVisibility(View.VISIBLE);
			}else{
				mainMenu.setMessageButtonRes(false);
				messageImageId.setVisibility(View.GONE);
			}
			
		}else{
			mainMenu.setMessageButtonRes(false);
			messageImageId.setVisibility(View.GONE);
		}
		
	}
}
