/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.menuclick.Message_SendAndDelClick;
import com.cmmobi.icuiniao.onlineEngine.activity.MyPageActivityA;
import com.cmmobi.icuiniao.ui.adapter.MessageAdapter_A;
import com.cmmobi.icuiniao.ui.view.ListviewForRefresh;
import com.cmmobi.icuiniao.ui.view.ListviewForRefresh.OnRefreshLoadListener;
import com.cmmobi.icuiniao.util.ActionID;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.DownImageManager;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.PageID;
import com.cmmobi.icuiniao.util.UserUtil;
import com.icuiniao.plug.im.Entity;
import com.icuiniao.plug.im.IMDataBase;
import com.icuiniao.plug.im.MessageBody;

/**
 * @author hw
 *点对点的私信
 */
public class MessageActivity_A extends Activity{

	//接收人ID
	private int revicerUserId;
	//接收人昵称
	private String nickname;
	//备注昵称
	private String remarks;
	//临时消息ID 流水号--系统时间
//	final long tempmessageid =  System.currentTimeMillis(); 
	//消息内容
	private String msgString;
	//消息类型
	private int type;
	
	
	private Button titlebar_backbutton;
	private Button message_manage;
	private ListviewForRefresh  listview;
	//private ListView listview;
	private EditText message_input;
	private Button sendbtn;
	private TextView titlebar_titletext;
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;
	private LinearLayout rinput;
	private MessageAdapter_A adapter;
	//private ArrayList<MessageItem> items;
	//private ArrayList<MessageItem> messageItems;
	
	//用于接收私信数据封装
	private ArrayList<Entity> responseEntitys;
	//生序封装数据
	private ArrayList<Entity> ascEntitys;
	//消息数据库应用 
	private IMDataBase imDataBase;
	
	//总页数 
	private int totalPage;
	//每页显示条数
	private int rowCount = 15;
	ArrayList<Entity> entitys = new ArrayList<Entity>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_a);
		responseEntitys = new ArrayList<Entity>();
		ascEntitys = new ArrayList<Entity>();
		imDataBase = new IMDataBase(this);
		
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
		CommonUtil.isInMessageScreen = true;
		//隐藏软件键盘
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		revicerUserId = getIntent().getExtras().getInt("revicerUserId");
		nickname = getIntent().getExtras().getString("nickname");
		
		remarks = getIntent().getExtras().getString("remarks");
		
		type = getIntent().getExtras().getInt("type");
		
		if(getIntent().getExtras().getString("remarks") != null){
			
			remarks = getIntent().getExtras().getString("remarks");
			
		}else{
			
			remarks = nickname;
		}
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		message_manage = (Button)findViewById(R.id.titlebar_menubutton);
		listview = (ListviewForRefresh)findViewById(R.id.listview);
		listview.setScrollbarFadingEnabled(true);
		message_input = (EditText)findViewById(R.id.message_input);
		sendbtn = (Button)findViewById(R.id.sendbtn);
		titlebar_titletext = (TextView)findViewById(R.id.titlebar_titletext);
		titlebar_titletext.setText(nickname);
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		rinput = (LinearLayout)findViewById(R.id.rinput);
		if(revicerUserId == 0){
			//titlebar_titletext.setVisibility(View.GONE);
			//message_manage.setVisibility(View.GONE);
			rinput.setVisibility(View.GONE);
		}else{
			//titlebar_titletext.setVisibility(View.VISIBLE);
			//message_manage.setVisibility(View.VISIBLE);
			rinput.setVisibility(View.VISIBLE);
		}
		
		titlebar_backbutton.setOnClickListener(backClickListener);
		message_manage.setOnClickListener(manageClickListener);
		sendbtn.setOnClickListener(sendClickListener);
		//adapter = new MessageAdapter_A(ascEntitys,MessageActivity_A.this,mHandler);
		addProgress();
		mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_INIT,200);
		DownImageManager.clear();
	}
	
	public void finish(){
		Intent intent2 = new Intent();
		intent2.putExtra("type", type);//默认为用户消息		
		intent2.setClass(MessageActivity_A.this, MessageManagerActivityA.class);
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
			finish();
		}
	};
	
	private OnClickListener manageClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.putExtra("from", UserUtil.userid);
			intent.putExtra("revicerUserId", revicerUserId);
			intent.putExtra("nickname",titlebar_titletext.getText().toString());
			intent.putExtra("remarks", remarks);
			intent.putExtra("type", type);
			intent.setClass(MessageActivity_A.this, MessageSettingActivity.class);
			//startActivity(intent);
			startActivityForResult(intent,123456);
			//finish(true);
		}
	};
	
	private OnClickListener sendClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			if(imm != null){
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			if(message_input.getText() == null){
				msgString = "";
			}else{
				msgString = message_input.getText().toString();
			}
			send(msgString);//将方法单独抽出来，解决混淆后无法运行此类的问题，具体问题原因不明，暂时不做研究。按此方案执行。
			message_input.setText("");
			}
			
	};
	
	private void send(String msgString){
		if(!msgString.equals("")){
			
			Intent intent = null;
			//创建日期
			String date = createCommentTime();

			boolean isAddFriendMessage = false;//是否是建立好友关系的信息
			Entity entity = null;
			long tempmessageid =  System.currentTimeMillis(); 
			if(ascEntitys != null && ascEntitys.size() != 0){
				int type = ascEntitys.get(ascEntitys.size()-1).getType();
				if(type == 3){
					isAddFriendMessage = true;
					if(ascEntitys.get(ascEntitys.size()-1).getFromId() == UserUtil.userid){
						//向数据库添加消息数据
						imDataBase.insertDB(UserUtil.userid, 0L, tempmessageid, UserUtil.userid, revicerUserId, UserUtil.username, nickname, 1,msgString, 0, date, remarks, 0, 3, 0);
						entity = getLastSendEntity(UserUtil.userid, 0L, tempmessageid, UserUtil.userid, revicerUserId, UserUtil.username, nickname, 1,msgString, 0, date, remarks, 0, 3, 0);
						//申请发送消息
						intent = new Intent(ActionID.ACTION_BROADCAST_REQUST_SEND_FRIENDMESSAGE);
					}else{
						//向数据库添加消息数据
						imDataBase.insertDB(UserUtil.userid, 0L, tempmessageid, UserUtil.userid, revicerUserId, UserUtil.username, nickname, 1,msgString, 0, date, remarks, 0, 4, 0);
						entity = getLastSendEntity(UserUtil.userid, 0L, tempmessageid, UserUtil.userid, revicerUserId, UserUtil.username, nickname, 1,msgString, 0, date, remarks, 0, 4, 0);
						//申请发送消息
						intent = new Intent(ActionID.ACTION_BROADCAST_REQUST_SEND_FRIENDMESSAGE_SUCCESS);
					}
				}
			}
			if(!isAddFriendMessage){
				//向数据库添加消息数据
				imDataBase.insertDB(UserUtil.userid, 0L, tempmessageid, UserUtil.userid, revicerUserId, UserUtil.username, nickname, 1,msgString, 0, date, remarks, 0, 0, 0);
				entity = getLastSendEntity(UserUtil.userid, 0L, tempmessageid, UserUtil.userid, revicerUserId, UserUtil.username, nickname, 1,msgString, 0, date, remarks, 0, 0, 0);
				//申请发送消息
				intent = new Intent(ActionID.ACTION_BROADCAST_REQUST_SEND_MESSAGE);
			}

			
			//接收人id
			intent.putExtra("revicerUserId", revicerUserId);
			//昵称
			intent.putExtra("nickname", nickname);
			//备注名称
			intent.putExtra("remarks", remarks);
			//是否重发，1：重发，0：首发
			intent.putExtra("repetSend", 0);
			//消息id(流水号)
			intent.putExtra("messageId", tempmessageid); 
			//消息内容
			intent.putExtra("message", msgString);
			
			sendBroadcast(intent);
			//更新发送状态 0默认，1发送中，2发送成功，3发送失败
			imDataBase.updataSendStateByTempMessageId(tempmessageid, 1);
			
			if(entity != null){
				entity.setSendState(1);
				ascEntitys.add(entity);
				if(ascEntitys.size() > rowCount){
					ascEntitys.remove(0);
					
				}
				//mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_INIT,200);
				adapter.setListItem(ascEntitys);
				adapter.notifyDataSetChanged();
				listview.setSelection(ascEntitys.size()-1);//定位到最后
				setFootRefreshable();
			}
		  }
	}
	
	private Entity getLastSendEntity(int who,long messageid,long tempmessageid,int fromid,int toid,String fromname,String toname,int isread,String message,int sendstate,String date,String remarks,int cid,int type,int repetsend){
		Entity entity = new Entity();
		entity.setUserId(who);
		entity.setMessageid(messageid);
		entity.setTempMessageId(tempmessageid);
		entity.setFromId(fromid);
		entity.setToId(toid);
		entity.setRevicerUserId(toid);
		entity.setUserName(fromname);
		entity.setNickName(toname);
		entity.setIsRead(isread);
		MessageBody messageBody = new MessageBody();
		messageBody.setActionType(0);
		messageBody.setMessage(message);
		entity.setMessageBodies(messageBody);
		entity.setSendState(sendstate);
		entity.setCommenttime(date);
		entity.setRemarks(remarks);
		entity.setCid(cid);
		entity.setType(type);
		entity.setRepetSend((byte)repetsend);
		return entity;
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(final Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageID.MESSAGE_INIT:
			
				new Thread(){

					@Override
					public void run() {
						//总页数
						totalPage = imDataBase.getMessageByUserIdByPageNum(UserUtil.userid, revicerUserId, rowCount);

						//根据当前用户ID查询对应的消息数据
						responseEntitys = imDataBase.getTheMessageByUserId(UserUtil.userid,revicerUserId,rowCount,0);
						
						
						if(responseEntitys != null){
							ascEntitys = getAscEntity(responseEntitys);
							//更新消息状态为已读
							for (int i = 0; i < responseEntitys.size(); i++) {
								
								imDataBase.updataIsReadState(responseEntitys.get(i).getMessageid(), 1);
								
							}
							mHandler.sendEmptyMessage(1129);
						}else{
							//
							mHandler.sendEmptyMessage(1130);
						}
					}
					
				}.start();
				
				break;
				//叹号点击
			case 1120:
				
				Message_SendAndDelClick menuClick = new Message_SendAndDelClick(mHandler,msg.arg1);
				Intent intent = new Intent();
				intent.setClass(MessageActivity_A.this, AbsCuiniaoMenu.class);
				intent.putExtra("title", PageID.SETTING_MESSAGE_MENU_TITLE);
				
				intent.putExtra("items", PageID.SETTING_MESSAGE_TANHAO);
				
				startActivity(intent);
				AbsCuiniaoMenu.set(menuClick);
				break;
				//商品列点击
			case 1128:
				Intent intent1 = new Intent();
				intent1.setClass(MessageActivity_A.this, MyPageActivityA.class);
				intent1.putExtra("url", msg.obj.toString());
				intent1.putExtra("chickPos", 0);
				intent1.putExtra("type", PageID.PAGEID_MESSAGEMANAGER);
				startActivity(intent1);
				break;
			case 1129:
				if(ascEntitys != null){
					adapter = new MessageAdapter_A(ascEntitys,MessageActivity_A.this,mHandler);
					adapter.setListItem(ascEntitys);
					listview.setAdapter(adapter);
					listview.setSelection(ascEntitys.size()-1);//定位到最后
					listview.currPage = 0;
					listview.onHeadRefreshComplete();	
				}
				setFootRefreshable();
				closeProgress();
				break;
			case 1130:
				closeProgress();
				break;
				//下拉刷新
			case 9090:
				
				adapter.setListItem(ascEntitys);
				adapter.notifyDataSetChanged();
				listview.onHeadRefreshNextComplete(); //下拉刷新后的loding处理
				
				break;
			//重新发送、删除取消
			case MessageID.MESSAGE_SENDANDDEL_CANCEL:
				switch(msg.arg1){
				case 0:
					LogPrint.Print("console", "重新发送......"+ascEntitys.get(msg.arg2).getUserName());
					//获取首发重发状态
					   int repetState =  imDataBase.getRepetSend(ascEntitys.get(msg.arg2).getTempMessageId());
					 //重发
					   if(repetState == 1){
						   Intent intentRepet = null;
						   if(ascEntitys.get(msg.arg2).getType() == 3){//申请好友关系
								intentRepet = new Intent(ActionID.ACTION_BROADCAST_REQUST_SEND_FRIENDMESSAGE);
						   }else if(ascEntitys.get(msg.arg2).getType() == 4){//确认好友关系
								intentRepet = new Intent(ActionID.ACTION_BROADCAST_REQUST_SEND_FRIENDMESSAGE_SUCCESS);
						   }else if(ascEntitys.get(msg.arg2).getType() == 0){//好友消息
								intentRepet = new Intent(ActionID.ACTION_BROADCAST_REQUST_SEND_MESSAGE);
						   }else{//私信
							   intentRepet = new Intent(ActionID.ACTION_BROADCAST_REQUST_SEND_PRIVATEMESSAGE);
							   intentRepet.putExtra("cid", ascEntitys.get(msg.arg2).getCid());
						   }
							//接收人id
							intentRepet.putExtra("revicerUserId", revicerUserId);
							//昵称
							intentRepet.putExtra("nickname", nickname);
							//备注名称
							intentRepet.putExtra("remarks", remarks);
							//是否重发，1：重发，0：首发
							intentRepet.putExtra("repetSend", 1);
							//消息id(流水号)
							intentRepet.putExtra("messageId", ascEntitys.get(msg.arg2).getTempMessageId()); 
							ArrayList<MessageBody> messageBodys =  ascEntitys.get(msg.arg2).getMessageBodies();
							for (MessageBody messageBody : messageBodys) {

								//消息内容
								intentRepet.putExtra("message",messageBody.getMessage());
							}
							
							
							sendBroadcast(intentRepet);
							//更新状态为 发送中=1
							imDataBase.updataSendStateByTempMessageId(ascEntitys.get(msg.arg2).getTempMessageId(), 1);
							ascEntitys.get(msg.arg2).setSendState(1);
							adapter.setListItem(ascEntitys);
							adapter.notifyDataSetChanged();
							listview.setSelection(ascEntitys.size()-1);//定位到最后
					   }
					
					break;
				case 1:
					//删除当前数据
					LogPrint.Print("console", "删除取消......"+ascEntitys.get(msg.arg2).getUserName());
					if(imDataBase.deleteByTempMessageId(ascEntitys.get(msg.arg2).getTempMessageId())){
						ascEntitys.remove(msg.arg2);
						adapter.setListItem(ascEntitys);
						adapter.notifyDataSetChanged();
						listview.setSelection(ascEntitys.size()-1);//定位到最后
						OfflineLog.writeDeleteMessage();
					}
					break;
				
				}
				
			}
			


		}
		
	};
	

	
	public void addProgress(){
		try {
			if(loadingBar != null){
				loadingBar.setVisibility(View.VISIBLE);
			}
			
		} catch (Exception e) {
		}
	}
	
	public void closeProgress(){
		if(loadingBar != null){
			loadingBar.setVisibility(View.INVISIBLE);
		}
	}
	
	
	/**
	 * 注册广播接收器
	 */
	private final void registerReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		//接收到一条消息
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_MESSAGE);
		//接收到一条私信
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_PRIVATEMESSAGE);
		//接收到一条系统广播
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_SYSTEMMESSAGE);


		//接收到一条申请建立好友关系的消息
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE);
		//发送确认建立好友关系消息
		intentFilter.addAction(ActionID.ACTION_BROADCAST_REQUST_SEND_FRIENDMESSAGE_SUCCESS);
		//接收到确认好友关系
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE_SUCCESS);
		//发送成功（服务器反馈206）
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_SENDMESSAGE_SUCCESS);
		//发送失败
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_SENDMESSAGE_FAIL);
		registerReceiver(broadcastResponse, intentFilter);
	}
	
	/**
	 * 卸载广播接收器
	 */
	private final void unRegisterReceiver() {
		try {
			if(broadcastResponse != null){
				unregisterReceiver(broadcastResponse);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterReceiver();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		/*if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}*/
		registerReceiver();
		
	}
	
	/**
	 * 根据解析到的评论条数决定是否有上推加载旧评论功能
	 * @param commentSize
	 */
	private void setFootRefreshable() {
		if (listview.currPage < totalPage - 1) {

			listview.setonRefreshNextListener(new OnRefreshLoadListener() {

				@Override
				public void onRefreshNextPage(final int page) {
					new Thread() {

						public void run() {
							// 加载下一页数据
							responseEntitys = imDataBase.getTheMessageByUserId(UserUtil.userid, revicerUserId, rowCount,page);
							if (responseEntitys != null && responseEntitys.size() > 0) {

								// 更新消息状态为已读
								for (int i = 0; i < responseEntitys.size(); i++) {

									imDataBase.updataIsReadState(responseEntitys.get(i).getMessageid(), 1);

								}
								ArrayList<Entity> tempAscEntitys = new ArrayList<Entity>();
								tempAscEntitys = responseEntitys;
								ArrayList<Entity> ascs = getAscEntity(ascEntitys);
								for (int i = 0; i < tempAscEntitys.size(); i++) {
									ascs.add(tempAscEntitys.get(i));
								}
								ascEntitys = getAscEntity(ascs);
								mHandler.sendEmptyMessage(9090);

							} else {

								listview.currPage--;
								mHandler.sendEmptyMessage(9090);
							}
						};

					}.start();
				}
			});

		} else {
			listview.setHeadNextRefreshable(false);
		}

	}
	/**
	 * 收到好友消息广播
	 */
	private BroadcastReceiver broadcastResponse = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			//收到一条好友消息广播 type = 0
			if(intent.getAction().equals(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_MESSAGE)){
				
				Toast.makeText(MessageActivity_A.this, "收到一条好友消息", Toast.LENGTH_SHORT).show();
				
				int  reUserId = intent.getExtras().getInt("revicerUserId"); //接收人id
				String nickName = intent.getExtras().getString("nickname"); //昵称
				String remarks = intent.getExtras().getString("remarks"); //备注名称
				int repetSend = intent.getExtras().getInt("repetSend"); //是否重发，1：重发，0：首发
				long messageId = intent.getExtras().getLong("messageId"); //消息id
				String message = intent.getExtras().getString("message");
				int cid = intent.getExtras().getInt("cid"); //商品id
				String commentTime = intent.getExtras().getString("commentTime");//时间
				Entity entity = getLastSendEntity(UserUtil.userid, messageId, System.currentTimeMillis(), revicerUserId, UserUtil.userid, nickName, UserUtil.username, 1, message, 2, commentTime, remarks, cid, 0, repetSend);
				if(entity != null && revicerUserId == reUserId){
					imDataBase.updataIsReadState(messageId, 1);
					ascEntitys.add(entity);
					if(ascEntitys.size() > rowCount){
						ascEntitys.remove(0);
					}
					adapter.setListItem(ascEntitys);
					adapter.notifyDataSetChanged();
					listview.setSelection(ascEntitys.size()-1);//定位到最后
					setFootRefreshable();
				}
			}
			//接收到一条私信 type = 1
			else if(intent.getAction().equals(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_PRIVATEMESSAGE)){
				//添加数据库
				Toast.makeText(MessageActivity_A.this, "接收到一条私信", Toast.LENGTH_SHORT).show();
				int  reUserId = intent.getExtras().getInt("revicerUserId"); //接收人id
				String nickName = intent.getExtras().getString("nickname"); //昵称
				String remarks = intent.getExtras().getString("remarks"); //备注名称
				int repetSend = intent.getExtras().getInt("repetSend"); //是否重发，1：重发，0：首发
				long messageId = intent.getExtras().getLong("messageId"); //消息id
				String message = intent.getExtras().getString("message");
				int cid = intent.getExtras().getInt("cid"); //商品id
				String commentTime = intent.getExtras().getString("commentTime");//时间
				Entity entity = getLastSendEntity(UserUtil.userid, messageId, System.currentTimeMillis(), revicerUserId, UserUtil.userid, nickName, UserUtil.username, 1, message, 2, commentTime, remarks, cid, 1, repetSend);
				if(entity != null && revicerUserId == reUserId){
					imDataBase.updataIsReadState(messageId, 1);
					ascEntitys.add(entity);
					if(ascEntitys.size() > rowCount){
						ascEntitys.remove(0);
					}
					adapter.setListItem(ascEntitys);
					adapter.notifyDataSetChanged();
					listview.setSelection(ascEntitys.size()-1);//定位到最后
					setFootRefreshable();
				}
			}
			//接收到一条系统广播 type = 2
			else if(intent.getAction().equals(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_SYSTEMMESSAGE)){
				//添加数据库
				Toast.makeText(MessageActivity_A.this, "接收到一条系统广播", Toast.LENGTH_SHORT).show();
				long messageId = intent.getExtras().getLong("messageId");
				String commentTime =intent.getExtras().getString("commentTime");
				String message = intent.getExtras().getString("message");
				Entity entity = getLastSendEntity(UserUtil.userid, messageId, System.currentTimeMillis(), revicerUserId, UserUtil.userid, "翠鸟", UserUtil.username, 1, message, 2, commentTime, remarks, 0, 2, 0);
				if(entity != null && revicerUserId == 0){
					imDataBase.updataIsReadState(messageId, 1);
					ascEntitys.add(entity);
					if(ascEntitys.size() > rowCount){
						ascEntitys.remove(0);
					}
					adapter.setListItem(ascEntitys);
					adapter.notifyDataSetChanged();
					listview.setSelection(ascEntitys.size()-1);//定位到最后
					setFootRefreshable();
				}
				
			}
			//接收到申请建立好友关系
			else if(intent.getAction().equals(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE)){
				
				int  reUserId = intent.getExtras().getInt("revicerUserId"); //接收人id
				String nickName = intent.getExtras().getString("nickname"); //昵称
				String remarks = intent.getExtras().getString("remarks"); //备注名称
				int repetSend = intent.getExtras().getInt("repetSend"); //是否重发，1：重发，0：首发
				long messageId = intent.getExtras().getLong("messageId"); //消息id
				String message = intent.getExtras().getString("message");
				int cid = intent.getExtras().getInt("cid"); //商品id
				String commentTime = intent.getExtras().getString("commentTime");//时间
				Toast.makeText(MessageActivity_A.this, nickName+" 申请与您建立好友关系", Toast.LENGTH_SHORT).show();
				Entity entity = getLastSendEntity(UserUtil.userid, messageId, System.currentTimeMillis(), revicerUserId, UserUtil.userid, nickName, UserUtil.username, 1, message, 2, commentTime, remarks, cid, 3, repetSend);
				if(entity != null && revicerUserId == reUserId){
					imDataBase.updataIsReadState(messageId, 1);
					ascEntitys.add(entity);
					if(ascEntitys.size() > rowCount){
						ascEntitys.remove(0);
					}
					adapter.setListItem(ascEntitys);
					adapter.notifyDataSetChanged();
					listview.setSelection(ascEntitys.size()-1);//定位到最后
					setFootRefreshable();
				}
			}
			//接收到确认好友关系
			else if(intent.getAction().equals(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE_SUCCESS)){
				
				int  reUserId = intent.getExtras().getInt("revicerUserId"); //接收人id
				String nickName = intent.getExtras().getString("nickname"); //昵称
				String remarks = intent.getExtras().getString("remarks"); //备注名称
				int repetSend = intent.getExtras().getInt("repetSend"); //是否重发，1：重发，0：首发
				long messageId = intent.getExtras().getLong("messageId"); //消息id
				String message = intent.getExtras().getString("message");
				int cid = intent.getExtras().getInt("cid"); //商品id
				String commentTime = intent.getExtras().getString("commentTime");//时间
				Toast.makeText(MessageActivity_A.this, "与 "+nickName+" 建立好友关系成功", Toast.LENGTH_SHORT).show();
				Entity entity = getLastSendEntity(UserUtil.userid, messageId, System.currentTimeMillis(), revicerUserId, UserUtil.userid, nickName, UserUtil.username, 1, message, 2, commentTime, remarks, cid, 4, repetSend);
				if(entity != null && revicerUserId == reUserId){
					imDataBase.updataIsReadState(messageId, 1);
					ascEntitys.add(entity);
					if(ascEntitys.size() > rowCount){
						ascEntitys.remove(0);
					}
					adapter.setListItem(ascEntitys);
					adapter.notifyDataSetChanged();
					listview.setSelection(ascEntitys.size()-1);//定位到最后
					setFootRefreshable();
				}
			}
			//成功
			else if(intent.getAction().equals(ActionID.ACTION_BROADCAST_RESPONSE_SENDMESSAGE_SUCCESS)){
				
				//提示发送成功
				Toast.makeText(MessageActivity_A.this, "发送成功", Toast.LENGTH_SHORT).show();
				//获取消息Id\
				long messageId = intent.getExtras().getLong("messageId");
				long tempMessageId =  intent.getExtras().getLong("tempMessageId");
				String commenttime = intent.getExtras().getString("commentTime");
				//更新消息id
				imDataBase.updataMessageId(tempMessageId,messageId);
				//更新发送状态 发送成功 = 2
				imDataBase.updataSendStateByTempMessageId(tempMessageId, 2);
				
				for(int i = 0;i < ascEntitys.size(); i ++){
					if(ascEntitys.get(i).getTempMessageId() == tempMessageId){
						ascEntitys.get(i).setTempMessageId(tempMessageId);
						ascEntitys.get(i).setMessageid(messageId);
						ascEntitys.get(i).setSendState(2);
						ascEntitys.get(i).setCommenttime(commenttime);
						break;
					}
				}
				adapter.setListItem(ascEntitys);
				adapter.notifyDataSetChanged();
				adapter.notifyDataSetInvalidated();
				listview.setSelection(ascEntitys.size()-1);//定位到最后
				setFootRefreshable();
			}
			//失败
			else if(intent.getAction().equals(ActionID.ACTION_BROADCAST_RESPONSE_SENDMESSAGE_FAIL)){
				
				int code = intent.getExtras().getInt("code");
				switch (code) {
				case 0:
					//Toast.makeText(MessageActivity_A.this, "当前无网络,请确信网络是否正常", Toast.LENGTH_SHORT).show();
					
					for(int i = 0;i < ascEntitys.size(); i ++){
						if(ascEntitys.get(i).getSendState() == 1){
							//更新发送状态 发送失败 = 3
							imDataBase.updataSendStateByTempMessageId(ascEntitys.get(i).getTempMessageId(), 3);
							//更新重发状态1：重发，0：首发
							imDataBase.updataRepetSendByTempMessageId(ascEntitys.get(i).getTempMessageId(), 1);
							ascEntitys.get(i).setSendState(3);
							ascEntitys.get(i).setRepetSend((byte)1);
						}
					}
					adapter.setListItem(ascEntitys);
					adapter.notifyDataSetChanged();
					listview.setSelection(ascEntitys.size()-1);//定位到最后
					setFootRefreshable();
					break;
				case 1:
					//Toast.makeText(MessageActivity_A.this, "连接未准备好", Toast.LENGTH_SHORT).show();
					for(int i = 0;i < ascEntitys.size(); i ++){
						if(ascEntitys.get(i).getSendState() == 1){
							//更新发送状态 发送失败 = 3
							imDataBase.updataSendStateByTempMessageId(ascEntitys.get(i).getTempMessageId(), 3);
							//更新重发状态1：重发，0：首发
							imDataBase.updataRepetSendByTempMessageId(ascEntitys.get(i).getTempMessageId(), 1);
							ascEntitys.get(i).setSendState(3);
							ascEntitys.get(i).setRepetSend((byte)1);
						}
					}
					adapter.setListItem(ascEntitys);
					adapter.notifyDataSetChanged();
					listview.setSelection(ascEntitys.size()-1);//定位到最后
					setFootRefreshable();
					break;
				case 2:
					//Toast.makeText(CommentPageActivity_A.this, "服务器停止", Toast.LENGTH_SHORT).show();
					for(int i = 0;i < ascEntitys.size(); i ++){
						if(ascEntitys.get(i).getSendState() == 1){
							//更新发送状态 发送失败 = 3
							imDataBase.updataSendStateByTempMessageId(ascEntitys.get(i).getTempMessageId(), 3);
							//更新重发状态1：重发，0：首发
							imDataBase.updataRepetSendByTempMessageId(ascEntitys.get(i).getTempMessageId(), 1);
							ascEntitys.get(i).setSendState(3);
							ascEntitys.get(i).setRepetSend((byte)1);
						}
					}
					adapter.setListItem(ascEntitys);
					adapter.notifyDataSetChanged();
					listview.setSelection(ascEntitys.size()-1);//定位到最后
					setFootRefreshable();
					break;
				}
			}
		}
	};
	
	/**
	 * 生成毫秒级系统时间
	 * @return
	 */
	private String createCommentTime(){
		Date now=new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		String strNow=sdf.format(now);
		return strNow;
	}
	
	private ArrayList<Entity> getAscEntity(ArrayList<Entity> entitys){
		
		ArrayList<Entity> ascEntity = new ArrayList<Entity>();
		if(entitys != null){
			
			for (int i = entitys.size()-1; i >=0; i--) {
				
				ascEntity.add(entitys.get(i));
			}
		}
		
		return ascEntity;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 123456){
			
				ascEntitys.clear();
				adapter.setListItem(ascEntitys);
				adapter.notifyDataSetChanged();
			
		}else{
			adapter.setListItem(ascEntitys);
			adapter.notifyDataSetChanged();
		}
		
	}
	
}
