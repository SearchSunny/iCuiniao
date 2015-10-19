package com.cmmobi.icuiniao.Activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.entity.Friend;
import com.cmmobi.icuiniao.entity.FriendList;
import com.cmmobi.icuiniao.entity.Result;
import com.cmmobi.icuiniao.store.DBFriendList;
import com.cmmobi.icuiniao.ui.adapter.ShockFriendAdapter;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MHandlerThread;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.PinyinUtils;
import com.cmmobi.icuiniao.util.ReplayPermit;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

public class SendShockActivity extends Activity {
	
	private ConnectUtil mConnectUtil;
	private ProgressBar loadingBar;
	private ListView listView;
	private TextView noSearch;
	private RelativeLayout empLinear;
	private Button titlebar_backbutton;
	private ImageView searchbox_left;
	private EditText searchbox_center;
	private ImageView searchbox_right;
	private LinearLayout lineSearch;
	
	private ArrayList<Friend> arrFriend;
	private ShockFriendAdapter shockFriendAdapter;
	private DBFriendList dbFriendList;
	
	private final int message_json = 10001;	
	private final int message_updateUI = 10002;
	private final int message_add_database = 10003;
	
	public static final int thread_friend_list = 0;
	public static final int thread_is_refused = 1;
	public static final int thread_send_shock = 2;
	
	String dialogMsg = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.sendshock);
		initView();
		shockFriendAdapter = new ShockFriendAdapter(this, mHandler);
		dbFriendList = new DBFriendList(this);
		newHandler();
		connectFriendList();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 100){
			connectFriendList();
		}
	}
	
	
	private void initView(){
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		listView = (ListView)findViewById(R.id.friendlist);
		noSearch = (TextView)findViewById(R.id.noSearch);
		empLinear = (RelativeLayout)findViewById(R.id.empLinear);
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		noSearch = (TextView)findViewById(R.id.noSearch);
		titlebar_backbutton.setOnClickListener(backListener);	
		lineSearch = (LinearLayout)findViewById(R.id.lineSearch);
		searchbox_center = (EditText)findViewById(R.id.searchbox_center);
		searchbox_right = (ImageView)findViewById(R.id.searchbox_right);
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
					searchbox_right.setOnClickListener(rightClearListener);
					FriendList friendList = dbFriendList.searchFriendByWord(s.toString(), Friend.BOOL_FRIEND);
					if(friendList != null && friendList.arrUsers.size() != 0){
						noSearch.setVisibility(View.GONE);
						listView.setVisibility(View.VISIBLE);
						shockFriendAdapter.setFriendList(friendList.arrUsers);
						shockFriendAdapter.notifyDataSetChanged();
						
					}else{
						noSearch.setVisibility(View.VISIBLE);
						noSearch.setText("主人，您没有匹配的好友！");
						listView.setVisibility(View.GONE);
					}
				}else{
					searchbox_right.setBackgroundResource(R.drawable.searchbox_right1);
					searchbox_right.setOnClickListener(null);
					noSearch.setVisibility(View.GONE);
					listView.setVisibility(View.VISIBLE);
				}				
			}
			
		});
	}
	
	private View.OnClickListener backListener = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			finish();			
		}
		
	};
	
	private View.OnClickListener rightClearListener = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			searchbox_center.setText("");		
		}
		
	};
	
	private void connectFriendList() {
		final String url = URLUtil.Url_FRIEND_LIST + "?oid=" + UserUtil.userid
				+ "&maxid=-1";
		mConnectUtil = new ConnectUtil(this, mHandler, 0);
		mConnectUtil.connect(url, HttpThread.TYPE_PAGE, thread_friend_list);

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
			case MessageID.MESSAGE_CLOSEPROGRESS:
				closeProgress();
				break;
			case MessageID.MESSAGE_CONNECT_ERROR:				
				closeProgress();
				CommonUtil.ShowToast(getApplicationContext(), (String) msg.obj);
				break;
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				if ("text/json".equals(msg.getData().getString("content_type"))) {
					switch(msg.arg1){
					case thread_friend_list:
						jsonUserListNoResult((byte[])msg.obj);
						break;
					case thread_is_refused:
						final int isOtherBlack = ReplayPermit.jsonRefused((byte[])msg.obj);
						if(isOtherBlack == Friend.BOOL_BLACK){
							Toast.makeText(SendShockActivity.this, "由于对方设置，无法赠送摇一摇", Toast.LENGTH_SHORT).show();
							mHandler.sendEmptyMessage(MessageID.MESSAGE_CLOSEPROGRESS);
						}else{
							String url = msg.getData().getString("mUrl");
							String uid = CommonUtil.getSubString(url, "?uid=", "&nickname=");
							String nickname = CommonUtil.getSubString(url, "&nickname=", "&oid=");
							connectSendShock(uid, nickname);						}
						
						break;
					case thread_send_shock:
						Result result = jsonSendResult((byte[])msg.obj);						
						if(result.boolResult){
							String url = msg.getData().getString("mUrl");
							String nickname = CommonUtil.getSubString(url, "&nickname=", "&oid=");
							dialogMsg = "摇一下机会已经赠给" + "\n" + nickname;
//							CommonUtil.ShowToast(SendShockActivity.this, "摇一摇赠送成功", false);
						}else{
							dialogMsg = "摇一摇赠送失败 " + "\n"+ result.msgResult;
//							CommonUtil.ShowToast(SendShockActivity.this, "摇一摇赠送失败 " + "/"+ result.msgResult, false);
						}//										               
						mHandler.sendEmptyMessage(MessageID.MESSAGE_CLOSEPROGRESS);
						finish();
						break;
					}					
				}
				break;
			case message_updateUI:				
				updateUI();
				mHandler.sendEmptyMessage(MessageID.MESSAGE_CLOSEPROGRESS);
				sendMsgAddData((FriendList)msg.obj);
				break;
			}
		}
	};
	
	public void finish(){		
		Intent intent = new Intent();		
		intent.putExtra("msg", dialogMsg);
		setResult(RESULT_OK, intent);
		super.finish();
	}
	
	//送摇一摇
	private void connectSendShock(String uid, String nickname){
		String url = URLUtil.Url_SEND_SHOCK +  "?uid=" +
		uid +  "&nickname=" + nickname +  "&oid=" + UserUtil.userid + "&num=1";
		ConnectUtil connectUtil = new
		 ConnectUtil(this, mHandler, 1, 0);
		connectUtil.connect(url, HttpThread.TYPE_PAGE,
				thread_send_shock);
	}
	
	private Result jsonSendResult(byte[] data){
		Result result = new Result();
		try {
			String str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("lybjson", "str =" + str);
			JSONObject json = new JSONObject(str);
			result.boolResult = json.getBoolean("result");
			if(!json.isNull("msg")){
				result.msgResult = json.getString("msg");
			}
			//存入新的摇一摇个数
			if(!json.isNull("callonnum")){
				UserUtil.callOnNum = json.getInt("callonnum");
				CommonUtil.saveCallOnNum(this, UserUtil.callOnNum);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	private void sendMsgAddData(FriendList allList){
		Message msg = new Message();
		msg.obj = allList;
		msg.what = message_add_database;
		downHandler.sendMessage(msg);
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
				case message_add_database:
					FriendList allList = (FriendList)msg.obj;
					dbFriendList.reNewTable();
					dbFriendList.insert(allList);
					break;
					
				}
			}
		};
	}
	
	private void updateUI() {
		if (arrFriend.size() == 0) {  //有黑名单，没有好友
			empLinear.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			lineSearch.setVisibility(View.GONE);
		} else {
			empLinear.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			shockFriendAdapter.setFriendList(arrFriend);
			listView.setAdapter(shockFriendAdapter);
			lineSearch.setVisibility(View.VISIBLE);
		}
	}
	
	private boolean jsonUserListNoResult(byte[] data) {
		try {
			String str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("lybconnect", "jsonResultList = " + str);
			//好友列表没有项目的布局
			if (str.length() <= 2) {
				empLinear.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
				dbFriendList.createBookIntoDB();
				mHandler.sendEmptyMessage(MessageID.MESSAGE_CLOSEPROGRESS);
				return false;
			}
			Message msg = new Message();			
			msg.obj = data;			
			msg.what = message_json;			
			downHandler.sendMessage(msg);
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
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
	
	/**
	 * 拆分黑名单和好友
	 * @param allFriendList
	 */
	private void splitFriendAndBlack(FriendList allFriendList){
		arrFriend = new ArrayList<Friend>();
		for(int i=0;i<allFriendList.arrUsers.size(); i++){
			Friend friend = allFriendList.arrUsers.get(i);
			friend.userPage = allFriendList.userpage +  "oid="+ UserUtil.userid + "&uid="+ friend.userid;
			if(friend.isblack == Friend.BOOL_FRIEND){
				arrFriend.add(friend);	
			}
		}
	}
	
	
		
			
	
	public void addProgress() {
		if (loadingBar != null) {
			loadingBar.setVisibility(View.VISIBLE);
		}
	}
	
	public void closeProgress() {
		if (loadingBar != null) {
			loadingBar.setVisibility(View.INVISIBLE);
		}
	}
}
