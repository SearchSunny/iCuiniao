package com.cmmobi.icuiniao.onlineEngine.activity;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.Activity;
import com.cmmobi.icuiniao.Activity.SayhiPageActivity;
import com.cmmobi.icuiniao.entity.Friend;
import com.cmmobi.icuiniao.entity.FriendList;
import com.cmmobi.icuiniao.entity.Result;
import com.cmmobi.icuiniao.store.DBFriendList;
import com.cmmobi.icuiniao.ui.adapter.FindFriendAdapter;
import com.cmmobi.icuiniao.ui.adapter.ShareInFriendAdapter;
import com.cmmobi.icuiniao.ui.view.ListviewForUserInfo;
import com.cmmobi.icuiniao.ui.view.LitviewNoOverScroll;
import com.cmmobi.icuiniao.ui.view.MainMenu;
import com.cmmobi.icuiniao.ui.view.PopUserTitleMenu;
import com.cmmobi.icuiniao.ui.view.ListviewForUserInfo.OnContinusLoadListener;
import com.cmmobi.icuiniao.util.ActionID;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.ReplayPermit;
import com.cmmobi.icuiniao.util.ShareUtils;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;
import com.icuiniao.plug.im.Entity;
import com.icuiniao.plug.im.IMDataBase;
import com.tencent.mm.sdk.openapi.IWXAPI;
/**
 * @author lyb
 *
 */
public class AddFriendsActivityA extends Activity implements AnimationListener{
	
	private ProgressBar loadingBar;
	private ConnectUtil mConnectUtil;
	private LinearLayout titlebar_title;
	private DBFriendList dbFriendList;
	private ListviewForUserInfo listviewSearch;
	private LitviewNoOverScroll listviewInvite;
	private ImageView btn_find;
	private EditText et_find;	
	private ImageView overallFind;  //昵称搜索
	private ImageView invite;	   //邀请好友
	
	private LinearLayout lineSearch;  
	private LinearLayout linearInvite;
	
	private Button titlebar_menubutton;
	private Button titlebar_leftmenu;
	private LinearLayout linearPage;
	private MainMenu mainMenu;
	private boolean isAnimationOpen;//主菜单动画状态
	
	private FindFriendAdapter findFriendAdapter;
	private String searchUrl;
	
	//0代表全局搜索，1代表邀请好友
	private int uiIndex;	
	
	// IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    private ShareUtils shareUtils;
    
    private String[] itemStrings = {"邀请新浪微博好友","朋友圈邀請","邀请微信好友","邀请QQ空间好友","邀请手机好友"};
	
    private final int thread_search_first = 0;    	//搜索的第一页
	private final int thread_search_next = 1;  		//搜索的下一页
	private final int thread_is_addFriend = 2;	    //是否能加好友
	private final int thread_is_refused = 3;		//是否能发消息
	
	private FriendList friendList;
	//启用数据库操作
	private IMDataBase imDataBase;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addfriend_new);
		if (UserUtil.userid == -1 || UserUtil.userState != 1) {
			finish();
			return;
		}
		uiIndex = getIntent().getIntExtra("uiIndex", 0);
		linearPage = (LinearLayout)findViewById(R.id.linearPage);
		lineSearch = (LinearLayout)findViewById(R.id.lineSearch);
		linearInvite = (LinearLayout)findViewById(R.id.linearInvite);
		overallFind = (ImageView)findViewById(R.id.overallFind);
		invite = (ImageView)findViewById(R.id.invite);	
		
		titlebar_title = (LinearLayout)findViewById(R.id.titlebar_title);
		titlebar_title.setOnClickListener(titleListener);		

		titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
		titlebar_menubutton.setOnClickListener(menuListener);
		titlebar_leftmenu = (Button)findViewById(R.id.titlebar_leftmenu); 
		titlebar_leftmenu.setOnClickListener(menuListener);
		mainMenu = (MainMenu)findViewById(R.id.mainmenu);
		
		changeButton(uiIndex);
		initSearch();
		initInvite();
		
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		imDataBase = new IMDataBase(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
		if(isAnimationOpen){
			gotoMenu();
		}
	}
	
	private void changeButton(int ui_index){
		if(ui_index == 1){  //邀请好友
			lineSearch.setVisibility(View.INVISIBLE);
			linearInvite.setVisibility(View.VISIBLE);
			overallFind.setBackgroundResource(R.drawable.overall_find2);
			invite.setBackgroundResource(R.drawable.invite1);
			invite.setOnClickListener(null);
			overallFind.setOnClickListener(overAllListener);
		}else if(ui_index == 0){ //昵称搜索
			lineSearch.setVisibility(View.VISIBLE);
			linearInvite.setVisibility(View.INVISIBLE);
			overallFind.setBackgroundResource(R.drawable.overall_find1);
			invite.setBackgroundResource(R.drawable.invite2);
			invite.setOnClickListener(InviteListener);
			overallFind.setOnClickListener(null);
		}
	}
	
	private void initSearch(){		
		listviewSearch = (ListviewForUserInfo)findViewById(R.id.findList);
		btn_find = (ImageView)findViewById(R.id.btn_find);
		et_find = (EditText)findViewById(R.id.et_find);			
		btn_find.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String str = et_find.getText().toString();
				if(str.equals("")){
					Toast.makeText(AddFriendsActivityA.this, "请输入查询关键字", Toast.LENGTH_SHORT).show();
				}else{
				CommonUtil.hideSoftInput(et_find, AddFriendsActivityA.this);
				connectFind(str.trim(), 0);
				}
			}
		});
		findFriendAdapter = new FindFriendAdapter(this, mHandler);
		//点击空白区，键盘消失
		listviewSearch.setOnTouchListener(new View.OnTouchListener() {
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
						CommonUtil.hideSoftInput(et_find, AddFriendsActivityA.this);
					}
					break;	
				}				
				return false;
			}
		});
	}
	
	private void initInvite(){		
		listviewInvite = (LitviewNoOverScroll)findViewById(R.id.inviteList);
		ShareInFriendAdapter shareAdapter = new ShareInFriendAdapter(this, itemStrings);
		listviewInvite.setAdapter(shareAdapter);
		listviewInvite.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
				Intent intent = new Intent(AddFriendsActivityA.this, ShareInviteAct.class);
				intent.putExtra("shareIndex", position);
				intent.putExtra("titleName", itemStrings[position]);
				AddFriendsActivityA.this.startActivity(intent);				
			}
		});
	}
	

	
	//全局搜索
	private View.OnClickListener overAllListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			changeButton(0);
			
		}
	};
	//邀请好友
	private View.OnClickListener InviteListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			changeButton(1);
			
		}
	};	

	
	private View.OnClickListener menuListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			gotoMenu();
			
		}
	};	
	
	private View.OnClickListener titleListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			PopUserTitleMenu popTitleMenu = new PopUserTitleMenu(AddFriendsActivityA.this, R.style.dialog, mHandler, PopUserTitleMenu.INDEX_ADD_FRIEND);
			Window win = popTitleMenu.getWindow();
			win.setGravity(Gravity.TOP);				
			WindowManager.LayoutParams p2 = win.getAttributes();				
			p2.y = 50;  //向下偏移				
			win.setAttributes(p2);				
			popTitleMenu.show();
			
		}
	};   
        
	
	private void connectFind(String keyWord, int pi){
		mConnectUtil = new ConnectUtil(this, mHandler, 0);
		searchUrl = URLUtil.Url_ALL_SEARCH + "?oid=" + UserUtil.userid
		+ "&msg=" + CommonUtil.toUrlEncode(keyWord);	
		mConnectUtil.connect(searchUrl + "&pi=" + pi , HttpThread.TYPE_PAGE, thread_search_first);
	}
	
	private void setFootRefreshable() {
		if (listviewSearch.currPage < friendList.totalPage - 1) {
			listviewSearch.setFootLoadListener(new OnContinusLoadListener() {

				@Override
				public void nextLoad(int page) {
					mConnectUtil = new ConnectUtil(AddFriendsActivityA.this,
							mHandler, 1, 0);
					mConnectUtil.setShowProgress(false);
					String urlProductList = searchUrl +  "&pi=" + page;							
					mConnectUtil.connect(urlProductList, HttpThread.TYPE_PAGE,
							thread_search_next);

				}

			});
		} else {
			listviewSearch.setFootRefreshable(false);
		}

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
		intent.putExtra("uid", uid);
		intent.putExtra("nickname", nickname);
		startActivityForResult(intent, MessageID.REQUEST_SAY_HI);	
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		final String localRemark = CommonUtil.getRemark(AddFriendsActivityA.this);
		String remarks = "";
		if(requestCode == MessageID.REQUEST_SAY_HI){
			boolean issend = data.getExtras().getBoolean("issend");			
			if(issend){
				String msg = data.getExtras().getString("msg");					
				int toUserId = data.getExtras().getInt("uid");
				String toName = data.getExtras().getString("nickname");
				final long tempmessageid = System.currentTimeMillis();
				final String date = CommonUtil.createCommentTime();
				Entity entity = imDataBase.getTheMessage(tempmessageid);
				int repeate = 0;				
				//取得备注名称
				if(localRemark.length() > 0){					
					remarks = localRemark;					
				}else{
					remarks = toName;
				}
				if(entity == null){//type=3, isread=1			
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
				final String date = CommonUtil.createCommentTime();
				Entity entity = imDataBase.getTheMessage(tempmessageid);
				int repeate = 0;
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
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				closeProgress();
				if ("text/json".equals(msg.getData().getString("content_type"))) {
					switch (msg.arg1) {
					case thread_search_first:
						friendList = jsonResultList((byte[]) msg.obj);
						findFriendAdapter.setFriendList(friendList);
						listviewSearch.setAdapter(findFriendAdapter);
						listviewSearch.currPage = 0;
						setFootRefreshable();
						break;
					case thread_search_next:
						JsonNextPage((byte[]) msg.obj);
						findFriendAdapter.setFriendList(friendList);
						findFriendAdapter.notifyDataSetChanged();
						listviewSearch.onFootRefreshComplete();
						setFootRefreshable();
						break;
					case thread_is_addFriend:
						Result result = jsonBoolResult((byte[]) msg.obj, "");
						if (result.boolResult) {
							int uid = msg.getData().getInt("tag");
							String url = msg.getData().getString("mUrl");
							String nickname = CommonUtil.getSubString(url,
									"?nickname=", "&uid=");
							LogPrint.Print("lyb", "nickname =" + nickname);
							addFrendButtonClick(uid, nickname);
						} else {
							Toast.makeText(AddFriendsActivityA.this,
									result.msgResult, Toast.LENGTH_SHORT)
									.show();
							ReplayPermit.isMayClick = true;
						}
						break;
					case thread_is_refused:
						ReplayPermit.replaySendMsg(AddFriendsActivityA.this, msg);						
						break;
					}

				}//if json end
				break;
			case MessageID.MESSAGE_SAYHI_MSG:				
				int uid = msg.getData().getInt("uid");
				int isblack = msg.getData().getInt("isblack");
				String nickname = msg.getData().getString("nickname");
				//好友：发消息
				if(isblack == Friend.BOOL_FRIEND){					
					connectIsRefused(uid, nickname);
				}else{  //陌生人和黑名单用户： 打招呼
					connectIsAddFriend(uid, nickname);
				}
				break;
//			case MessageID.MESSAGE_ADD_FRIEND:				
//				uid = msg.getData().getInt("uid");
//				nickname = msg.getData().getString("nickname");
//				connectIsAddFriend(uid, nickname);
//				break;
			case MessageID.MESSAGE_POPTITLEMENU:
				int index = msg.arg1;
				if (index < PopUserTitleMenu.INDEX_ADD_FRIEND) {
					Intent intent = new Intent(AddFriendsActivityA.this, FriendActivityA.class);
					//标题菜单的选项
					intent.putExtra("popIndex", index);
					AddFriendsActivityA.this.startActivity(intent);
					AddFriendsActivityA.this.finish();
				} 
				break;	
			}
		}
	};
	
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
	//发消息前的对方拒绝判断
	private void connectIsRefused(int uid, String nickname){
		if(!ReplayPermit.isMayClick){
			return;
		}
		ReplayPermit.isMayClick = false;
		String url = URLUtil.Url_USER_RELATION+ "?uid=" +
		uid + "&nickname=" + nickname + "&oid=" + UserUtil.userid;
		ConnectUtil connectUtil = new
		 ConnectUtil(this, mHandler, 1, 0);
		connectUtil.connect(url, HttpThread.TYPE_PAGE,
				thread_is_refused);
	}
	
	private FriendList jsonResultList(byte[] data) {
		friendList = new FriendList();
		try {			
			String str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("lybconnect", "button json = " + str);
			if (str.length() <= 2) {
				Toast.makeText(this, "主人，您没有匹配的好友！", Toast.LENGTH_SHORT).show();
				return friendList;
			}
			jsonUserList(str, friendList);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return friendList;
	}
	
	private void jsonUserList(String result, FriendList friendList){
		try{
		JSONObject json = new JSONObject(result);
		JSONArray jsonArray = json.getJSONArray("users");
		friendList.pagesize = json.getInt("pagesize");
		friendList.totalcount = json.getInt("totalcount");
		friendList.totalPage = (friendList.totalcount % friendList.pagesize == 0) ? (friendList.totalcount / friendList.pagesize)
				: (friendList.totalcount / friendList.pagesize) + 1;
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject userJson = jsonArray.getJSONObject(i);
			Friend friend = new Friend();
			friend.username = userJson.getString("username");
			friend.icon_src = userJson.getString("icon_src");
			friend.userid = Integer.parseInt(userJson.getString("userid"));
			friend.isblack = Integer.parseInt(userJson.getString("isblack"));
			friendList.arrUsers.add(friend);
		}
		friendList.userpage = json.getString("userpage");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
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
	
	/**
	 * 获取下一页合并列表产品信息
	 * @param data
	 */
	private void JsonNextPage(byte[] data){
		try {
			String str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);			
			LogPrint.Print("lybconnect", "userpage json = " + str);
			if(str.length() <= 2){
				listviewSearch.currPage --;
				return ;
			}
			jsonUserList(str, friendList);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data){		
//		if(requestCode == 100){	
//			mConnectUtil = new ConnectUtil(this, mHandler, 1, 0);					
//			mConnectUtil.connect(searchUrl, HttpThread.TYPE_PAGE, 0);
//		}
//	}
	
	public void addProgress() {
		if (loadingBar != null) {
			loadingBar.setVisibility(View.VISIBLE);
		}
		listviewSearch.setFootRefreshable(false);

	}

	public void closeProgress() {

		if (loadingBar != null) {
			loadingBar.setVisibility(View.INVISIBLE);
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
}
