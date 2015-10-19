package com.cmmobi.icuiniao.onlineEngine.activity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.Activity;
import com.cmmobi.icuiniao.Activity.LoginAndRegeditActivity;
import com.cmmobi.icuiniao.Activity.PersonalSettingActivity;
import com.cmmobi.icuiniao.Activity.SayhiPageActivity;
import com.cmmobi.icuiniao.Activity.SendMessageActivity;
import com.cmmobi.icuiniao.entity.Friend;
import com.cmmobi.icuiniao.entity.ProductItem;
import com.cmmobi.icuiniao.entity.Products;
import com.cmmobi.icuiniao.entity.Result;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Button;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Image;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Page;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ParserEngine;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Text;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_TitleBarFloat;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_UserInfo_new;
import com.cmmobi.icuiniao.ui.adapter.UserPageNewAdapter;
import com.cmmobi.icuiniao.ui.view.ListviewForUserInfo;
import com.cmmobi.icuiniao.ui.view.MainMenu;
import com.cmmobi.icuiniao.ui.view.ListviewForUserInfo.OnContinusLoadListener;
import com.cmmobi.icuiniao.util.ActionID;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.ReplayPermit;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;
import com.icuiniao.plug.im.Entity;
import com.icuiniao.plug.im.IMDataBase;
/**
 * @author lyb
 *
 */
public class UserPageActivityA extends Activity implements AnimationListener {

	private ConnectUtil mConnectUtil;
	private String urlString = "";
	private Parser_UserInfo_new parser_UserInfo_new;
	private Parser_ParserEngine parserEngine;
	private Parser_Image parserImage;
	private Parser_Page parserPage;
	private Parser_Button leftParBtn;
	private Parser_Button rightParBtn;

	private TextView titlebar_titletext;
	private LinearLayout linebtnRight;
	private FrameLayout linebtnLeft;
	private ImageView btnRight;
	private ImageView btnLeft;
	private TextView friendCnt;
	private TextView textnickName;
	private TextView textPosition;
	private ImageView imgGender;
	private ImageView imgUser;

	private MainMenu mainMenu;
	private FrameLayout userPagelayout;
	private boolean isAnimationOpen;// 主菜单动画状态
	// add by lyb 小鱼loading
	private ProgressBar loadingBar;
	// 菜单按钮
	private Button titlebar_menubutton = null;
	// 左侧的隐藏菜单按钮，用于动画播放后的控制
	private Button titlebar_leftmenu = null;
	private Button titlebar_backbutton = null;
	DownImageItem downImageItem = null;
	
	private ListviewForUserInfo listView;
//	private View headView;
	private ProductItem productItem;
	private UserPageNewAdapter userPageNewAdapter;
	private int uid;
	private String nickname = "";
	private String gender = "";
	
	private TextView settips;
	//启用数据库操作
	private IMDataBase imDataBase;
	//消息点
	private ImageView messageImageId;
	private boolean messageImageState;
	private final int thread_userInfo_first = 0;    	//用户信息
	private final int thread_commodInfo = 1;  	//产品列表
	private final int thread_commodRefresh = 2;	//产品列表刷新
	private final int thread_addFried = 3;	//添加好友，黑名单
	private final int thread_addBlack = 4;
	private final int thread_delBlack = 5;
	private final int thread_user_refresh =6;
	private final int thread_is_addFriend = 7;
	private final int thread_is_refused = 8;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userpagenew);
//		if (UserUtil.userid == -1 || UserUtil.userState != 1) {
//			finish();
//			return;
//		}
		uid = getIntent().getIntExtra("userid", 0);
		nickname = getIntent().getStringExtra("nickname");
		urlString = getIntent().getExtras().getString("url");
		LayoutInflater inflater = LayoutInflater.from(this);
		listView = (ListviewForUserInfo)findViewById(R.id.userListView);
		listView.setScrollbarFadingEnabled(true);
		View headView = (LinearLayout) inflater.inflate(R.layout.userinfo_new, null);
		initView();
		initHeadView(headView);	
		
		connectUserInfo();
		connectCommodList();
		
		listView.addHeaderView(headView);
//		SimpleAdapter adapter = new SimpleAdapter(this, getData(), R.layout.poptitlemenu_item, new String[]{"item"}, new int[]{R.id.poptitlemenu_item});
		userPageNewAdapter = new UserPageNewAdapter(this, uid, mHandler);
		listView.setAdapter(userPageNewAdapter);
		imDataBase = new IMDataBase(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(messageImageState){
			
			getMessageByUserId();
		}
		//注册广播
		registerReceiver();
//		if(UserUtil.userid == -1||UserUtil.userState != 1){
//			finish();
//			return;
//		}
		if(isAnimationOpen){
			gotoMenu();
		}
	}
	
	private void initView(){
		userPagelayout = (FrameLayout)findViewById(R.id.userPagelayout);
		mainMenu = (MainMenu)findViewById(R.id.mainmenu);
		titlebar_titletext = (TextView)findViewById(R.id.titlebar_titletext);
		titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
		titlebar_leftmenu = (Button)findViewById(R.id.titlebar_leftmenu);
		titlebar_menubutton.setOnClickListener(menuClickListener);
		titlebar_leftmenu.setOnClickListener(menuClickListener);
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		titlebar_backbutton.setOnClickListener(backClickListener);
		loadingBar = (ProgressBar)findViewById(R.id.loading);	
		settips = (TextView)findViewById(R.id.settips);
		
		
	}

	private void initHeadView(View headView) {		
		linebtnRight = (LinearLayout) headView.findViewById(R.id.linebtnRight);
		linebtnLeft = (FrameLayout) headView.findViewById(R.id.linebtnLeft);
		btnRight = (ImageView) headView.findViewById(R.id.btnRight);
		btnLeft = (ImageView) headView.findViewById(R.id.btnLeft);
		friendCnt  = (TextView) headView.findViewById(R.id.friendCnt);
		textnickName = (TextView) headView.findViewById(R.id.textnickName);
		textPosition = (TextView) headView.findViewById(R.id.textPosition);
		imgGender = (ImageView) headView.findViewById(R.id.imgGender);
		imgUser = (ImageView) headView.findViewById(R.id.iduserimg);
		messageImageId = (ImageView)findViewById(R.id.messageImageId);
		imgUser.setLayoutParams(new RelativeLayout.LayoutParams(CommonUtil.screen_width, CommonUtil.screen_width));
	}	
	

	private void connectUserInfo() {
		mConnectUtil = new ConnectUtil(this, mHandler, 1, 0);
		LogPrint.Print("lybconnect", "urlString =" + urlString);
		if (urlString != null) {
			mConnectUtil.connect(urlString, HttpThread.TYPE_PAGE, thread_userInfo_first);
		}
	}
	
	/**
	 * 刷新用户信息
	 */
	private void refreshUserInfo(){
		mConnectUtil = new ConnectUtil(this, mHandler, 1, 0);		
		if (urlString != null) {
			mConnectUtil.connect(urlString, HttpThread.TYPE_PAGE, thread_user_refresh);
		}
	}
	
	private void connectCommodList(){
		ConnectUtil connectUtil = new ConnectUtil(this, mHandler, 0);
		String urlProductList = URLUtil.URL_PRODUCET_LIST +  "?oid=" + UserUtil.userid
		 + "&uid=" + uid + "&pi=0" + "&plaid=" + URLUtil.plaid  + "&dpi=" + URLUtil.dpi();
		LogPrint.Print("lybconnect", "produList =" + urlProductList);
		connectUtil.connect(urlProductList, HttpThread.TYPE_PAGE, thread_commodInfo);
	}

	private String addUrlParam(String url, String oid, String uid) {
		if (url.indexOf("?") > 0) {
			return url + "oid=" + oid + "&uid=" + uid;
		}
		return url + "?oid=" + oid + "&uid=" + uid;
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
			case MessageID.MESSAGE_IMAGE_LOAD_ERROR:
				if (gender.equals("男")) {
					imgUser.setImageResource(R.drawable.local_icon_man_big);
				} else {
					imgUser.setImageResource(R.drawable.local_icon_women_big);
				}
				closeProgress();
				break;
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				closeProgress();
				if ("text/json".equals(msg.getData().getString("content_type"))) {
					final int threadIdx = msg.arg1;
					switch(threadIdx){
					case thread_commodInfo:
						JsonFirstPage((byte[]) msg.obj);
						setFootRefreshable();
						break;
					case thread_commodRefresh:
						JsonNextPage((byte[]) msg.obj);
						listView.onFootRefreshComplete();	
						setFootRefreshable();
						break;
					case thread_addBlack:
						Result result = jsonResult((byte[]) msg.obj, "\n");
						if(result.boolResult){
							Toast.makeText(UserPageActivityA.this, "成功加入黑名单", Toast.LENGTH_SHORT).show();
							refreshUserInfo();
						}else{
							Toast.makeText(UserPageActivityA.this, "加入黑名单失败" + result.msgResult, Toast.LENGTH_SHORT).show();
						}
						break;
					case thread_addFried:
						result = jsonResult((byte[]) msg.obj, "\n");
						if(result.boolResult){
							Toast.makeText(UserPageActivityA.this, "发送成功", Toast.LENGTH_SHORT).show();							
						}else{
							Toast.makeText(UserPageActivityA.this, "发送失败" + result.msgResult, Toast.LENGTH_SHORT).show();
						}											
						break;
					case thread_delBlack:
						result = jsonResult((byte[]) msg.obj, "\n");
						if(result.boolResult){
							Toast.makeText(UserPageActivityA.this, "移出黑名单成功",
									Toast.LENGTH_SHORT).show();
							refreshUserInfo();					
						}else{
							Toast.makeText(UserPageActivityA.this, "移出黑名单失败" + result.msgResult, Toast.LENGTH_SHORT).show();	
						}
						break;
					case thread_is_addFriend:
						result = jsonResult((byte[]) msg.obj, "");
						if(result.boolResult){
							addFrendButtonClick(1);
						}else{
							Toast.makeText(UserPageActivityA.this, result.msgResult, Toast.LENGTH_SHORT).show();
							ReplayPermit.isMayClick = true;
						}
						break;
					case thread_is_refused:
						int isOtherBlack = ReplayPermit.jsonRefused((byte[])msg.obj);
						if(isOtherBlack == Friend.BOOL_BLACK){
							Toast.makeText(UserPageActivityA.this, "由于对方设置，无法发送消息", Toast.LENGTH_SHORT).show();
							ReplayPermit.isMayClick = true;
						}else{
							Intent intent = new Intent(UserPageActivityA.this, SendMessageActivity.class);	
							intent.putExtra("uid", uid);
							intent.putExtra("nickname", nickname);
							UserPageActivityA.this.startActivityForResult(intent, MessageID.REQUEST_SEND_MSG);
						}						
							
						break;
					}
					
				} else {			
					try {
						String str = new String((byte[]) msg.obj, "UTF-8");
						str = CommonUtil.formUrlEncode(str);
						LogPrint.Print("lybjson", "str =" + str);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
					renderPage(msg, msg.arg1, msg.getData().getInt("mType"));
					mHandler.sendEmptyMessage(9900);
				}
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
					mainMenu.setMessageButtonRes(false);
					messageImageId.setVisibility(View.GONE);
				}
				else if(state == -1){
					mainMenu.setMessageButtonRes(false);
					messageImageId.setVisibility(View.GONE);
				}
				break;
			case 9900:
				getMessageByUserId();
				messageImageState = true;
			}

		}
	};
	
	private boolean readLocalExist(String url){
		String fileName = CommonUtil.dir_cache + "/"
		+ CommonUtil.urlToNum(url);
		File file = new File(fileName);
		if(file == null){
			return false;
		}
		if(file.exists() && file.length() > 0){
			Bitmap temp = BitmapFactory.decodeFile(fileName);
			imgUser.setImageBitmap(temp);
			closeProgress();
			return true;
		}
		return false;
	}

	private void renderPage(Message msg, int threadindex, int type) {		
		switch (type) {
		case HttpThread.TYPE_PAGE:

			parserEngine = new Parser_ParserEngine(this);
			parserEngine.parser((byte[]) msg.obj);
			parserPage = parserEngine.getPageObject();
			Parser_Layout_AbsLayout[] tmpAbsLayouts = parserEngine.getLayouts();
			for (int i = 0; tmpAbsLayouts != null && i < tmpAbsLayouts.length; i++) {
				if (tmpAbsLayouts[i].getModelType() == Parser_Layout_AbsLayout.MODELTYPE_TITLEBARFLOAT) {
					Parser_TitleBarFloat parserTitleBarFloat = (Parser_TitleBarFloat) tmpAbsLayouts[i];
					String title = parserTitleBarFloat.getStr();
					titlebar_titletext.setText(title);

				} else if (tmpAbsLayouts[i].getModelType() == Parser_Layout_AbsLayout.MODELTYPE_USERINFO_new) {
					parser_UserInfo_new = (Parser_UserInfo_new) tmpAbsLayouts[i];
					renderUserInfo(parser_UserInfo_new);
				} else if (tmpAbsLayouts[i].getModelType() == Parser_Layout_AbsLayout.MODELTYPE_IMAGE) {
					parserImage = (Parser_Image) tmpAbsLayouts[i];
					final String useImgString = parserImage.getSrc();					
					// 压入下载队列
					if (useImgString != null) {
//						if(readLocalExist(useImgString)){
//							LogPrint.Print("lybconnect", "local =");
//							continue;
//						}
						downImageItem = new DownImageItem(
								Parser_Layout_AbsLayout.MODELTYPE_IMAGE, 0,
								useImgString, parserImage.getPageId(), 0);
//						LogPrint.Print("lyb", "type 1=  " + downImageItem.getType());
//						LogPrint.Print("lybconnect", "image  url 1 = " + useImgString);
						addProgress();
						new ConnectUtil(UserPageActivityA.this, mHandler, 1, 0)
								.connect(useImgString, HttpThread.TYPE_IMAGE, 0);
					}
				}

			}
//			listView.addHeaderView(headView);			
//			SimpleAdapter adapter = new SimpleAdapter(this, getData(), R.layout.poptitlemenu_item, new String[]{"item"}, new int[]{R.id.poptitlemenu_item});
//			listView.setAdapter(adapter);
			
			break;
		case HttpThread.TYPE_IMAGE:
//			DownImageItem downImageItem = DownImageManagerUser.get(threadindex);			
			try {
				// 保存图片
//				CommonUtil.writeToFile(CommonUtil.dir_cache + "/"
//						+ CommonUtil.urlToNum(downImageItem.getUrl()),
//						(byte[]) msg.obj);
				final int imageType = downImageItem.getType();
				final String url = msg.getData().getString("mUrl");	
//				LogPrint.Print("lybconnect", "image url 2 = " + url);
				if (url.equals(downImageItem.getUrl()) && imageType == Parser_Layout_AbsLayout.MODELTYPE_IMAGE) {
					byte[] data = (byte[]) msg.obj;
					Bitmap temp = BitmapFactory.decodeByteArray(data, 0,
							data.length);
					if(temp == null){
						if (gender.equals("男")) {
							imgUser.setImageResource(R.drawable.local_icon_man_big);
						} else {
							imgUser.setImageResource(R.drawable.local_icon_women_big);
						}
					}else{ //2013.4.23 by lyb 
						imgUser.setImageBitmap(temp);
					}
					closeProgress();
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (gender.equals("男")) {
					imgUser.setImageResource(R.drawable.local_icon_man);
				} else {
					imgUser.setImageResource(R.drawable.local_icon_women);
				}
			}
			break;
		}

	}
	
	int[] RES = {R.drawable.titlebarmenu_all_0,R.drawable.titlebarmenu_discount_0,R.drawable.titlebarmenu_recommend_0,R.drawable.titlebarmenu_form_0};
	
	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		for(int i = 0;i < RES.length;i ++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("item", RES[i]);
			list.add(map);
		}
		
		return list;
	}

	private void renderUserInfo(Parser_UserInfo_new parser_UserInfo_new) {
		if (parser_UserInfo_new == null) {
			return;
		}
		Parser_Text addrParText = parser_UserInfo_new.getAddr();
		Parser_Text genderParText = parser_UserInfo_new.getGender();
		Parser_Text nameParText = parser_UserInfo_new.getName();
		leftParBtn = parser_UserInfo_new.getLeftButton();
		rightParBtn = parser_UserInfo_new.getRightButton();

		if (rightParBtn == null) {
			linebtnRight.setVisibility(View.GONE);
		} else {
			linebtnRight.setVisibility(View.VISIBLE);
			setButtonByAction(btnRight, rightParBtn);
			setButtonListener(btnRight, rightParBtn, leftParBtn);
		}

		if (leftParBtn == null) {
			linebtnLeft.setVisibility(View.GONE);
		} else {
			linebtnLeft.setVisibility(View.VISIBLE);
			setButtonByAction(btnLeft, leftParBtn);
			setButtonListener(btnLeft, leftParBtn, leftParBtn);
		}

		textnickName.setText(nameParText.getStr());
		textPosition.setText(addrParText.getStr());		
		gender = genderParText.getStr();
		if (gender.equals("男")) {
			imgGender.setBackgroundResource(R.drawable.manbtn);
		} else if (gender.equals("女")) {
			imgGender.setBackgroundResource(R.drawable.womenbtn);
		}

	}
	
	/**
	 * 添加陌生人为好友的逻辑
	 * @param action
	 */
	private void addFrendButtonClick(final int action){
		Intent intent = new Intent(this, SayhiPageActivity.class);		
		intent.putExtra("title", "跟TA打个招呼");
		intent.putExtra("uid", uid);
		intent.putExtra("nickname", nickname);
		startActivityForResult(intent, MessageID.REQUEST_SAY_HI);	
	}
	
	/**
	 * 判断是否能加为好友
	 */
	private void connectIsAddFriend(){
		if(!ReplayPermit.isMayClick){
			return;
		}
		ReplayPermit.isMayClick = false;
		String url = URLUtil.Url_IS_ADD_FRIEND + "?oid=" +
		 UserUtil.userid + "&uid=" + uid;
		ConnectUtil connectUtil = new
		 ConnectUtil(UserPageActivityA.this, mHandler, 1, 0);
		connectUtil.connect(url, HttpThread.TYPE_PAGE,
				thread_is_addFriend);
	}
	
	//判断是否消息将被拒绝
	private void connectIsRefused(){
		if(!ReplayPermit.isMayClick){
			return;
		}
		ReplayPermit.isMayClick = false;
		String url = URLUtil.Url_USER_RELATION+ "?oid=" +
		 UserUtil.userid + "&uid=" + uid;
		ConnectUtil connectUtil = new
		 ConnectUtil(UserPageActivityA.this, mHandler, 1, 0);
		connectUtil.connect(url, HttpThread.TYPE_PAGE,
				thread_is_refused);
	}
	
	private void delBlack (final int action){
		ConnectUtil connectUtil = new
		 ConnectUtil(UserPageActivityA.this, mHandler, action);
		String addfriend= URLUtil.Url_DEL_BLACK + "?oid=" +
		 UserUtil.userid + "&uid=" + uid;				
		 connectUtil.connect(addfriend, HttpThread.TYPE_PAGE,
				 thread_delBlack);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		final String localRemark = CommonUtil.getRemark(UserPageActivityA.this);
		String remarks = "";
		if(resultCode == RESULT_OK && requestCode== MessageID.REQUEST_SAY_HI){
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
				if(entity == null){	//type=3, isread=1				
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
		}else if(resultCode == RESULT_OK && requestCode == MessageID.REQUEST_SEND_MSG){
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
		}else if(requestCode == MessageID.REQUEST_GO_SET){
			connectUserInfo();
		}
	}
	
	/**
	 * 设置按钮的监听
	 * @param btn
	 * @param parser_button
	 */
	private void setButtonListener(ImageView btn, Parser_Button parser_button,
			final Parser_Button parser_buttonOther) {
		final int action = parser_button.getAction();
		switch (action) {
		case Parser_Layout_AbsLayout.ACTION_ADD_FRIEND:		
			btn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆
						CommonUtil.ShowToast(UserPageActivityA.this, "请登陆或者注册，变身为小C的主人！");
						Intent intent11 = new Intent();
						intent11.setClass(UserPageActivityA.this, LoginAndRegeditActivity.class);
						UserPageActivityA.this.startActivity(intent11);
						return;
					}
					connectIsAddFriend();					
				}

			});
			break;
		case Parser_Layout_AbsLayout.ACTION_SEND:
			btn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					connectIsRefused();
				}
			});
			break;
		case Parser_Layout_AbsLayout.ACTION_FRIEND:
			btn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(UserPageActivityA.this,
							FriendActivityA.class);
					UserPageActivityA.this.startActivity(intent);
				}

			});
			break;
		case Parser_Layout_AbsLayout.ACTION_DELETED:
			btn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					delBlack(action);
					
				}
			});
			
			break;
		case Parser_Layout_AbsLayout.ACTION_TO_DELETE:
			btn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆
						CommonUtil.ShowToast(UserPageActivityA.this, "请登陆或者注册，变身为小C的主人！");
						Intent intent11 = new Intent();
						intent11.setClass(UserPageActivityA.this, LoginAndRegeditActivity.class);
						UserPageActivityA.this.startActivity(intent11);
						return;
					}
					ConnectUtil connectUtil = new ConnectUtil(
							UserPageActivityA.this, mHandler, action);
					String addBlackList = URLUtil.URL_ADD_BLACK + "?oid="
							+ UserUtil.userid + "&uid=" + uid;
					connectUtil.connect(addBlackList, HttpThread.TYPE_PAGE,
							thread_addBlack);
				}

			});
			break;
		case Parser_Layout_AbsLayout.ACTION_SET:
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(UserPageActivityA.this,PersonalSettingActivity.class);
					UserPageActivityA.this.startActivityForResult(intent, MessageID.REQUEST_GO_SET);
				}
			});
			
			break;
//		case Parser_Layout_AbsLayout.ACTION_DEL_BLACK:
//			btn.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					delBlack(action);
//				}
//			});
//			break;
		}
	}

	/**
	 * 设置按钮图片
	 * @param btn
	 * @param parser_button
	 */
	private void setButtonByAction(ImageView btn, Parser_Button parser_button) {
		final int action = parser_button.getAction();
		if (action == Parser_Layout_AbsLayout.ACTION_ADD_FRIEND) {
			btn.setBackgroundResource(R.drawable.bt_add);
		}else if (action == Parser_Layout_AbsLayout.ACTION_DEL_BLACK) {
			btn.setBackgroundResource(R.drawable.bt_add);
		} else if (action == Parser_Layout_AbsLayout.ACTION_SEND) {
			btn.setBackgroundResource(R.drawable.bt_send);
		} else if (action == Parser_Layout_AbsLayout.ACTION_FRIEND) {
			btn.setBackgroundResource(R.drawable.bt_friend);
			friendCnt.setVisibility(View.VISIBLE);
			friendCnt.setText(parser_button.getStr());
		} else if (action == Parser_Layout_AbsLayout.ACTION_DELETED) {
			btn.setBackgroundResource(R.drawable.bt_deleted);
		} else if(action == Parser_Layout_AbsLayout.ACTION_TO_DELETE){
			btn.setBackgroundResource(R.drawable.bt_todelete);
		}else if(action == Parser_Layout_AbsLayout.ACTION_SET){
			btn.setBackgroundResource(R.drawable.bt_set);
		}
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
				listView.currPage --;
				return ;
			}
			JSONObject json = new JSONObject(str);
			JSONArray jsonArray = json.getJSONArray("products");
			getProductList(jsonArray);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 获取首页合并列表产品信息
	 * @param data
	 */
	private void JsonFirstPage(byte[] data) {
		try {
			String str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);
			
			LogPrint.Print("lybconnect", "userpage json = " + str);
			productItem = new ProductItem();
			if(str.length() <= 2){
				listView.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
				settips.setVisibility(View.VISIBLE);
				settips.setText("TA很懒，小C很无语...");
				return ;
			}			
			JSONObject json = new JSONObject(str);
			boolean isNoResult = json.isNull("result");
			if(!isNoResult){
				String boolResult = json.getString("result");
				listView.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
				String msg = json.getString("msg");
				settips.setVisibility(View.VISIBLE);
				settips.setText(msg);
				return;
			}			
			productItem.totalcount = json.getInt("totalcount");
			productItem.pagesize = json.getInt("pagesize");			
			productItem.totalPage = (productItem.totalcount % productItem.pagesize == 0) ? (productItem.totalcount / productItem.pagesize)
					: (productItem.totalcount / productItem.pagesize) + 1;
			productItem.propage = json.getString("propage");		
			JSONArray jsonArray = json.getJSONArray("products");
			getProductList(jsonArray);
			userPageNewAdapter.setProductItem(productItem);
			userPageNewAdapter.notifyDataSetChanged();
			LogPrint.Print("lyb", "productItem.arrProducts = "+ productItem.arrProducts.size());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 解析result结果
	 * @param data
	 * @return
	 */
	private Result jsonResult(byte[] data, String splitChar){
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
			if(!json.isNull("msg")){
				String msg = json.getString("msg");
				if(msg != null){
					result.msgResult = splitChar + msg;
				}
			}			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 从jsonarray中获取产品列表信息
	 * @param jsonArray
	 * @throws Exception
	 */
	private void getProductList(JSONArray jsonArray) throws Exception{
		for(int i=0; i< jsonArray.length(); i++){
			Products products = new Products();
			JSONObject o = (JSONObject) jsonArray.get(i);
			JSONArray cidArrayJson = o.getJSONArray("cids");
			JSONArray cidArrayProImg = o.getJSONArray("proImages");
			JSONArray playableArray = o.getJSONArray("playables");
			products.count = o.getInt("count");				
			products.findTime = o.getString("findtime");
			products.showTime = o.getString("showtime");
			for(int k=0; k< cidArrayJson.length(); k++){
				String cid = cidArrayJson.getString(k);					
				LogPrint.Print("lybjson", "cid =" + cid);//
				products.arrcid.add(cid);					
			}
			for (int k=0; k<cidArrayProImg.length(); k++){
				String proImg = cidArrayProImg.getString(k);
				LogPrint.Print("lybjson", "proImg = " + proImg);//
				products.arrProImg.add(proImg);
			}
			for(int k=0; k<playableArray.length(); k++){
				int playable = playableArray.getInt(k);
				products.arrPlayable.add(playable);
			}
			LogPrint.Print("lyb", "arrCommodity size = "+products.arrProImg.size());
			productItem.arrProducts.add(products);
		}
	}

	
	/**
	 * 根据解析到的分页信息进行判断，是否可以上推刷新
	 * @param commentSize
	 */
	private void setFootRefreshable() {
		if (listView.currPage < productItem.totalPage - 1) {
			listView.setFootLoadListener(new OnContinusLoadListener() {

				@Override
				public void nextLoad(int page) {
					mConnectUtil = new ConnectUtil(UserPageActivityA.this,
							mHandler, 1, 0);
					mConnectUtil.setShowProgress(false);
					String urlProductList = URLUtil.URL_PRODUCET_LIST + "?oid="
							+ UserUtil.userid + "&uid=" + uid + "&pi=" + page
							+ "&plaid=" + URLUtil.plaid + "&dpi="
							+ URLUtil.dpi();
					mConnectUtil.connect(urlProductList, HttpThread.TYPE_PAGE,
							thread_commodRefresh);

				}

			});
		} else {
			listView.setFootRefreshable(false);
		}

	}

	public void addProgress() {
		try {
			if (loadingBar != null) {
				loadingBar.setVisibility(View.VISIBLE);
			}

		} catch (Exception e) {
		}
	}

	public void closeProgress() {
		if (loadingBar != null) {
			loadingBar.setVisibility(View.INVISIBLE);
		}
	}

	public OnClickListener menuClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			gotoMenu();
		}
	};
	
	public OnClickListener backClickListener = new OnClickListener(){
		@Override
		public void onClick(View v){
			finish();
		}
	};
	
	public void finish(){
		Intent intent = new Intent();
		intent.putExtra("userid", uid);				
		setResult(RESULT_OK, intent);		
		super.finish();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		gotoMenu();
		return true;
	}

	private void gotoMenu() {
		if (userPagelayout != null) {
			isAnimationOpen = !isAnimationOpen;
			if (isAnimationOpen) {
				if (mainMenu != null) {
					mainMenu.setIsCanClick(true);
				}
				mHandler.removeMessages(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
				Animation animation = AnimationUtils.loadAnimation(this,
						R.anim.right_out);
				animation.setFillEnabled(true);
				animation.setFillAfter(true);
				animation.setAnimationListener(this);
				// 动画播放，实际布局坐标不变
				userPagelayout.startAnimation(animation);
				OfflineLog.writeMainMenu();// 写入离线日志
			} else {
				if (mainMenu != null) {
					mainMenu.setIsCanClick(false);
				}
				Animation animation = AnimationUtils.loadAnimation(this,
						R.anim.left_in);
				animation.setAnimationListener(this);
				animation.setFillEnabled(true);
				animation.setFillAfter(true);
				userPagelayout.startAnimation(animation);
			}
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if(isAnimationOpen){
			titlebar_backbutton.setVisibility(View.GONE);
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
			titlebar_leftmenu.setVisibility(View.GONE);		
			titlebar_backbutton.setVisibility(View.VISIBLE);
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

	
	/**
	 * 用于接收消息广播
	 */
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
