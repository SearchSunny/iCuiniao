package com.cmmobi.icuiniao.Activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.entity.Result;
import com.cmmobi.icuiniao.util.ActionID;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.ReplayPermit;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;
import com.icuiniao.plug.im.Entity;
import com.icuiniao.plug.im.IMDataBase;
/**
 * 公开\私聊评论编辑页面
 * @author XP
 *
 */
public class CommentPageActivity_A extends Activity{

	private EditText comment_edit;
	private TextView comment_num;
	private Button titlebar_backbutton;
	private Button titlebar_menubutton;
	private int input_cur;
	private boolean isSend;//是否点击发送
	private int issubject;//评论类型，-1：商品评论，0：1级评论，1：多级评论
	private int subjectid;
	private int commentid;
	
	//商品ID
	private int cid;
	private int toUserId;
	private String toName;
	
	private Button gongkai_2;
	private Button siliao_1;
	//是否公开
	private boolean isGongKai;
	//启用数据库操作
	private IMDataBase imDataBase;
	//handler消息
	private final int thread_is_addFriend = 7;
	//判断是否为好友返回结果
	Result result = null;
	//临时消息ID 流水号--系统时间
	final Long tempmessageid =  System.currentTimeMillis(); 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.commentlayout_a);
		
		imDataBase = new IMDataBase(this);
		result = new Result();
		isSend = false;
		issubject = getIntent().getExtras().getInt("issubject");
		subjectid = getIntent().getExtras().getInt("subjectid");
		commentid = getIntent().getExtras().getInt("commentid");
		
		//接收商品ID、发出者昵称、接收者昵称
		cid = getIntent().getExtras().getInt("cid");
		toUserId = getIntent().getExtras().getInt("touserid");
		toName = getIntent().getExtras().getString("toname");
		
		comment_edit = (EditText)findViewById(R.id.comment_edit);
		comment_num = (TextView)findViewById(R.id.comment_num);
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
		titlebar_backbutton.setOnClickListener(backClickListener);
		titlebar_menubutton.setOnClickListener(sendClickListener);
		input_cur = 140;
		
		gongkai_2 = (Button)findViewById(R.id.gongkai_2);
        
        siliao_1 = (Button)findViewById(R.id.siliao_1);
        gongkai_2.setOnClickListener(onClickListener);
        siliao_1.setOnClickListener(onClickListener);
		comment_edit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				comment_num.setText(""+input_cur);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				comment_num.setText(""+input_cur);
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(isGongKai){
					
					input_cur = 500-comment_edit.getText().length();
					comment_num.setText(""+input_cur);
				}else{
					
					input_cur = 140-comment_edit.getText().length();
					comment_num.setText(""+input_cur);
				}
				
			}
		});		
	}
	
	public void finish(){		
		//如果是非私信
		if(!isGongKai){
			
			Intent intent = new Intent();
			intent.putExtra("issend", isSend);
			intent.putExtra("msg", comment_edit.getText().toString().trim());
			intent.putExtra("issubject", issubject);
			intent.putExtra("subjectid", subjectid);
			intent.putExtra("commentid", commentid);
			setResult(RESULT_OK, intent);
			
			super.finish();
		}else{
			
			super.finish();
		}
		ReplayPermit.isMayClick = true;
	}
	/**
	 * 私信使用
	 */
	public void imFinish(){
		
		super.finish();
		ReplayPermit.isMayClick = true;
	}
	
	private OnClickListener backClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			isSend = false;
			finish();
		}
	};
	
	private OnClickListener sendClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(CommonUtil.isNetWorkOpen(CommentPageActivity_A.this)){
				//公开
				if(!isGongKai){
					
					if(comment_edit.getText().toString().trim().length() > 0){
						isSend = true;
						finish();
					}else{
						CommonUtil.ShowToast(CommentPageActivity_A.this, "您未填写评论信息");
					}
					
				}//私信
				else{
					//发送消息.....
					LogPrint.Print("console", "评价私信信息......");
					//判断是否能加为好友
//					connectIsAddFriend();
					sendMessage();					
				}
				
			}else{
				isSend = false;
				CommonUtil.ShowToast(CommentPageActivity_A.this, "杯具了- -!\n联网不给力啊");
				finish();
			}
		}
	};
	//公开\私聊
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.siliao_1:
				isGongKai = true;
				gongkai_2.setBackgroundResource(R.drawable.gongkai_1);
				siliao_1.setBackgroundResource(R.drawable.siliao_2);
				comment_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});
				comment_num.setText("500");
				break;
			case R.id.gongkai_2:
				isGongKai = false;
				siliao_1.setBackgroundResource(R.drawable.siliao_1);
				gongkai_2.setBackgroundResource(R.drawable.gongkai_2);
				comment_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(140)});
				comment_num.setText("140");
			default:
				break;
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
	
	/**
	 * 判断是否能加为好友
	 */
	private void connectIsAddFriend(){
		String url = URLUtil.Url_IS_ADD_FRIEND + "?oid=" + UserUtil.userid + "&uid=" + toUserId;
		ConnectUtil connectUtil = new ConnectUtil(CommentPageActivity_A.this, mHandler, 1, 0);
		connectUtil.connect(url, HttpThread.TYPE_PAGE,thread_is_addFriend);
	}
	
	private void sendMessage(){
		if(comment_edit.getText().toString().trim().length() > 0){
			isSend = true;
			//消息内容
			String message = comment_edit.getText().toString();
			
			//创建日期
			String date = createCommentTime();
			String remarks = toName;
			//取得备注名称
			if(CommonUtil.getRemark(CommentPageActivity_A.this).length() > 0){
				
				remarks = CommonUtil.getRemark(CommentPageActivity_A.this);
				
			}
			//先从数据库查询有无当前数据
		   Entity entity = 	imDataBase.getTheMessage(tempmessageid);
		   if(entity == null){
			//向数据库添加私信消息数据
			//who-用户ID
			//messageid-消息ID
			//tempmessageid - 系统时间
			//fromid - 发出者id
			//toid - 接收者id
			//fromname - 发出者昵称
			//toname - 接收者昵称
			//isread - 未读已读状态（0未读，1已读）
			//message - 消息
			//sendstate - 发送状态（0默认，1发送中，2发送成功，3发送失败）
			//date - 时间
			//remarks - 备注
			//cid - 商品id
			//type - 消息类型（0好友消息，1私信，2系统消息
			//repetsend - 首发或重发  1：重发，0：首发
			imDataBase.insertDB(UserUtil.userid, 0L, tempmessageid, UserUtil.userid, toUserId, UserUtil.username, toName, 1,message, 0, date, remarks, cid, 1, 0);
			//申请发送私信广播
			Intent intent = new Intent(ActionID.ACTION_BROADCAST_REQUST_SEND_PRIVATEMESSAGE);
			//接收人id
			intent.putExtra("revicerUserId", toUserId);
			//昵称
			intent.putExtra("nickname", toName);
			//备注名称
			intent.putExtra("remarks", remarks);
			//是否重发，1：重发，0：首发
			intent.putExtra("repetSend", 0);
			//消息id(流水号)
			intent.putExtra("messageId", tempmessageid);
			//消息内容
			intent.putExtra("message", message);
			//商品id
			intent.putExtra("cid", cid);
			sendBroadcast(intent);
			//更新状态为 发送中=1
			imDataBase.updataSendStateByTempMessageId(tempmessageid, 1);
		   }
		   else{
			   //获取首发重发状态
			   int repetState =  imDataBase.getRepetSend(tempmessageid);
			   //重发
			   if(repetState == 1){
				   //重新申请发送私信广播
					Intent intent = new Intent(ActionID.ACTION_BROADCAST_REQUST_SEND_PRIVATEMESSAGE);
					//接收人id
					intent.putExtra("revicerUserId", toUserId);
					//昵称
					intent.putExtra("nickname", toName);
					//备注名称
					intent.putExtra("remarks", remarks);
					//是否重发，1：重发，0：首发
					intent.putExtra("repetSend", 1);
					//消息id(流水号)
					intent.putExtra("messageId", tempmessageid);
					//消息内容
					intent.putExtra("message", message);
					//商品id
					intent.putExtra("cid", cid);
					sendBroadcast(intent);
					//更新状态为 发送中=1
					imDataBase.updataSendStateByTempMessageId(tempmessageid, 1);
			   }
			  
		   }
//			finish();
		}else{
			CommonUtil.ShowToast(CommentPageActivity_A.this, "您未填写评论信息");
		}
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				if ("text/json".equals(msg.getData().getString("content_type"))) {
					final int threadIdx = msg.arg1;
//					switch(threadIdx){
//					case thread_is_addFriend:
//						result = jsonResult((byte[])msg.obj,"");
//						//是否能加为好友
//						if(true){
////						if(result.boolResult){
//							if(comment_edit.getText().toString().trim().length() > 0){
//								isSend = true;
//								//消息内容
//								String message = comment_edit.getText().toString();
//								
//								//创建日期
//								String date = createCommentTime();
//								String remarks = toName;
//								//取得备注名称
//								if(CommonUtil.getRemark(CommentPageActivity_A.this).length() > 0){
//									
//									remarks = CommonUtil.getRemark(CommentPageActivity_A.this);
//									
//								}
//								//先从数据库查询有无当前数据
//							   Entity entity = 	imDataBase.getTheMessage(tempmessageid);
//							   if(entity == null){
//								//向数据库添加私信消息数据
//								//who-用户ID
//								//messageid-消息ID
//								//tempmessageid - 系统时间
//								//fromid - 发出者id
//								//toid - 接收者id
//								//fromname - 发出者昵称
//								//toname - 接收者昵称
//								//isread - 未读已读状态（0未读，1已读）
//								//message - 消息
//								//sendstate - 发送状态（0默认，1发送中，2发送成功，3发送失败）
//								//date - 时间
//								//remarks - 备注
//								//cid - 商品id
//								//type - 消息类型（0好友消息，1私信，2系统消息
//								//repetsend - 首发或重发  1：重发，0：首发
//								imDataBase.insertDB(UserUtil.userid, 0L, tempmessageid, UserUtil.userid, toUserId, UserUtil.username, toName, 0,message, 0, date, remarks, cid, 1, 0);
//								//申请发送私信广播
//								Intent intent = new Intent(ActionID.ACTION_BROADCAST_REQUST_SEND_PRIVATEMESSAGE);
//								//接收人id
//								intent.putExtra("revicerUserId", toUserId);
//								//昵称
//								intent.putExtra("nickname", toName);
//								//备注名称
//								intent.putExtra("remarks", remarks);
//								//是否重发，1：重发，0：首发
//								intent.putExtra("repetSend", 0);
//								//消息id(流水号)
//								intent.putExtra("messageId", tempmessageid);
//								//消息内容
//								intent.putExtra("message", message);
//								//商品id
//								intent.putExtra("cid", cid);
//								sendBroadcast(intent);
//								//更新状态为 发送中=1
//								imDataBase.updataSendStateByTempMessageId(tempmessageid, 1);
//							   }
//							   else{
//								   //获取首发重发状态
//								   int repetState =  imDataBase.getRepetSend(tempmessageid);
//								   //重发
//								   if(repetState == 1){
//									   //重新申请发送私信广播
//										Intent intent = new Intent(ActionID.ACTION_BROADCAST_REQUST_SEND_PRIVATEMESSAGE);
//										//接收人id
//										intent.putExtra("revicerUserId", toUserId);
//										//昵称
//										intent.putExtra("nickname", toName);
//										//备注名称
//										intent.putExtra("remarks", remarks);
//										//是否重发，1：重发，0：首发
//										intent.putExtra("repetSend", 1);
//										//消息id(流水号)
//										intent.putExtra("messageId", tempmessageid);
//										//消息内容
//										intent.putExtra("message", message);
//										//商品id
//										intent.putExtra("cid", cid);
//										sendBroadcast(intent);
//										//更新状态为 发送中=1
//										imDataBase.updataSendStateByTempMessageId(tempmessageid, 1);
//								   }
//								  
//							   }
//								//finish();
//							}else{
//								CommonUtil.ShowToast(CommentPageActivity_A.this, "您未填写评论信息");
//							}
//						}else{
//							
//							Toast.makeText(CommentPageActivity_A.this, "对方不能添加为好友", Toast.LENGTH_SHORT).show();
//							
//						}
//						break;
//					
//					}
					
					
				}
			
			}
			
		}
	};
	
	
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
	 * 接收私信消息成功或失败
	 */
	private BroadcastReceiver successAndFaildBroadcast = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			//成功
			if(intent.getAction().equals(ActionID.ACTION_BROADCAST_RESPONSE_SENDMESSAGE_SUCCESS)){
				
				//提示发送成功
				Toast.makeText(CommentPageActivity_A.this, "发送成功", Toast.LENGTH_SHORT).show();
				//获取消息Id\
				Long messageId = intent.getExtras().getLong("messageId");
				Long tempMessageId =  intent.getExtras().getLong("tempMessageId");
				//更新消息id
				imDataBase.updataMessageId(tempMessageId,messageId);
				//更新发送状态 发送成功 = 2
				imDataBase.updataSendStateByTempMessageId(tempmessageid, 2);
				imFinish();
			}
			//失败
			else if(intent.getAction().equals(ActionID.ACTION_BROADCAST_RESPONSE_SENDMESSAGE_FAIL)){
				
				int code = intent.getExtras().getInt("code");
				switch (code) {
				case 0:
					Toast.makeText(CommentPageActivity_A.this, "当前无网络,请确信网络是否正常", Toast.LENGTH_SHORT).show();
					//更新发送状态 发送失败 = 3
					imDataBase.updataSendStateByTempMessageId(tempmessageid, 3);
					//更新重发状态1：重发，0：首发
					imDataBase.updataRepetSendByTempMessageId(tempmessageid, 1);
					break;
				case 1:
					Toast.makeText(CommentPageActivity_A.this, "连接未准备好", Toast.LENGTH_SHORT).show();
					//更新发送状态 发送失败 = 3
					imDataBase.updataSendStateByTempMessageId(tempmessageid, 3);
					//更新重发状态1：重发，0：首发
					imDataBase.updataRepetSendByTempMessageId(tempmessageid, 1);
					break;
				case 2:
					//Toast.makeText(CommentPageActivity_A.this, "服务器原因", Toast.LENGTH_SHORT).show();
					//更新发送状态 发送失败 = 3
					imDataBase.updataSendStateByTempMessageId(tempmessageid, 3);
					//更新重发状态1：重发，0：首发
					imDataBase.updataRepetSendByTempMessageId(tempmessageid, 1);
					break;
				}
			}
		}
	};
	/**
	 * 注销广播
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			if(successAndFaildBroadcast != null){
				unregisterReceiver(successAndFaildBroadcast);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 注册广播
	 */
	@Override
	protected void onResume() {
		super.onResume();
		//接收广播过滤
		IntentFilter filter = new IntentFilter();
		filter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_SENDMESSAGE_SUCCESS);
		filter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_SENDMESSAGE_FAIL);
		registerReceiver(successAndFaildBroadcast, filter);
	}
	
	
}
