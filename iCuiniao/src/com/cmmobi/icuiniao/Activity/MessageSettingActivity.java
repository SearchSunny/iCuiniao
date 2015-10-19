/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.menuclick.Message_ManageSettingClick;
import com.cmmobi.icuiniao.ui.view.SlipButton;
import com.cmmobi.icuiniao.ui.view.SlipButton.OnChangedListener;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.PageID;
import com.cmmobi.icuiniao.util.UserUtil;
import com.icuiniao.plug.im.IMDataBase;

/**
 * 个人私信管理
 * @author XP
 *
 */
public class MessageSettingActivity extends Activity{

	private int from;
	private int revicerUserId;
	private String nickname;
	private String remarks;
	private int type;
	private Button titlebar_backbutton;
	private Button clearMessage;
	private RelativeLayout remarkBtn;
	private EditText remark_str;
	private TextView titleStr;
	//消息置顶
	private SlipButton messageTop;
	//操作数据库
	IMDataBase imDataBase;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messagesetting);
	
		imDataBase = new IMDataBase(this);
		
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		clearMessage = (Button)findViewById(R.id.clearMessage);
		titleStr = (TextView)findViewById(R.id.titlebar_titletext);
		
		messageTop = (SlipButton)findViewById(R.id.messageTop);
		
		//备注
		remarkBtn = (RelativeLayout)findViewById(R.id.remarkBtn);
		remark_str = (EditText)findViewById(R.id.remark_str);
		
		clearMessage.setOnClickListener(clearMessageListener);
		titlebar_backbutton.setOnClickListener(backClickListener);
		remarkBtn.setOnClickListener(remarkBtnOnClick);
		
		messageTop.setOnChangedListener(messageTopListener);
		
		
		
		
		from = getIntent().getExtras().getInt("from");
		revicerUserId = getIntent().getExtras().getInt("revicerUserId");
		nickname = getIntent().getExtras().getString("nickname");
		remarks = getIntent().getExtras().getString("remarks");
		
		type = getIntent().getExtras().getInt("type");
		
		
		titleStr.setText(nickname);
		if(CommonUtil.getImTop(MessageSettingActivity.this) == revicerUserId){
			
			messageTop.setCheck(true);
			CommonUtil.saveImTop(MessageSettingActivity.this, revicerUserId);
			
		}else{
			
			messageTop.setCheck(false);
			CommonUtil.saveImTop(MessageSettingActivity.this, -1);
		}
		//从SharedPrences取出备注信息
//		if(CommonUtil.getRemark(MessageSettingActivity.this).length() > 0 && from == UserUtil.userid){
//			
//			remark_str.setText(CommonUtil.getRemark(MessageSettingActivity.this));
//			
//		}else{
			
			remark_str.setText(remarks);
		//}
	}
	
	
	//清空当前聊天记录
	private OnClickListener clearMessageListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			Message_ManageSettingClick menuClick = new Message_ManageSettingClick(mHandler,0);
			Intent intent = new Intent();
			intent.setClass(MessageSettingActivity.this, AbsCuiniaoMenu.class);
			intent.putExtra("title", PageID.SETTING_MESSAGE_MENU_TITLE_A);
			
			intent.putExtra("items", PageID.SETTING_MESSAGE_MENU_TITLE_A_SUB);
			
			startActivity(intent);
			AbsCuiniaoMenu.set(menuClick);
		}
	};
	
	//接收回调函数
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case MessageID.MESSAGE_DELRECORD:
				switch (msg.arg1) {
				case 0:
					try {
						
						if(imDataBase.deleteByFromid(UserUtil.userid, revicerUserId)){
							
							LogPrint.Print("console", "删除聊天记录成功......");
							delFinish();
							OfflineLog.writeDeleteMessage();
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;

				case 1:
					LogPrint.Print("console", "不删除聊天记录......");
					break;
				}
				break;
			}
		}
		
		
		
		
	};
	private OnClickListener backClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	public void finish(){
		//更新备注昵称
		imDataBase.updateRemarkByRevicerUserId(revicerUserId, remark_str.getText().toString());
		Intent intent2 = new Intent();
		intent2.putExtra("from", from);
		intent2.putExtra("revicerUserId", revicerUserId);
		intent2.putExtra("nickname", nickname);
		intent2.putExtra("remarks", remark_str.getText().toString());
		intent2.putExtra("type", type);
      	intent2.setClass(MessageSettingActivity.this, MessageActivity_A.class);
      	setResult(654321, intent2);
      	//startActivity(intent2);
		super.finish();
		//OfflineLog.writeMessageManager();//写入离线日志
	}
	
	public void delFinish() {
		
		Intent intent2 = new Intent();
		intent2.putExtra("from", from);
		intent2.putExtra("revicerUserId", revicerUserId);
		intent2.putExtra("nickname", nickname);
		intent2.putExtra("remarks", remark_str.getText().toString());
		intent2.putExtra("type", type);
      	intent2.setClass(MessageSettingActivity.this, MessageActivity_A.class);
      	setResult(123456, intent2);
      	super.finish();
	}
	//修改备注
	private OnClickListener remarkBtnOnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			if(imm != null){
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		}
	};
	
	//消息置顶
	private OnChangedListener messageTopListener = new OnChangedListener() {
		
		@Override
		public void onChanged(boolean checkState) {
			if(checkState){
				LogPrint.Print("console", "消息置顶......");
				CommonUtil.saveImTop(MessageSettingActivity.this, revicerUserId);
			}else{
				
				LogPrint.Print("console", "消息置顶取消.......");
				CommonUtil.saveImTop(MessageSettingActivity.this, -1);
			}
			
		}
	};
}
