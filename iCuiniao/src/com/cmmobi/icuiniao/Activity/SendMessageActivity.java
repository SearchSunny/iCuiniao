package com.cmmobi.icuiniao.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ReplayPermit;

public class SendMessageActivity extends Activity{

	private EditText comment_edit;
	private TextView comment_num;
	private Button titlebar_backbutton;
	private Button titlebar_menubutton;
	private int input_cur;
	private boolean issend;//是点击否发送了
	
		
	private int userid;	
	private String message;
	private String nickname;
		


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sendmessage);
		issend = false;			

		userid = getIntent().getIntExtra("uid", 0);
		nickname = getIntent().getStringExtra("nickname");
		comment_edit = (EditText)findViewById(R.id.comment_edit);
		comment_num = (TextView)findViewById(R.id.comment_num);
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
		titlebar_backbutton.setOnClickListener(backClickListener);
		titlebar_menubutton.setOnClickListener(sendClickListener);		
		
		input_cur = 140;
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
				// TODO Auto-generated method stub
				input_cur = 140-comment_edit.getText().length();
				comment_num.setText(""+input_cur);
			}
		});		
	}
	
	public void finish(){
		ReplayPermit.isMayClick = true;
		Intent intent = new Intent();
		intent.putExtra("issend", issend);
		intent.putExtra("msg", message);				
		intent.putExtra("uid", userid);
		intent.putExtra("nickname", nickname);
		setResult(RESULT_OK, intent);		
		super.finish();
	}
	
	private OnClickListener backClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			issend = false;
			finish();
		}
	};
	
	private OnClickListener sendClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (comment_edit.getText().toString().trim().length() == 0) {
				Toast.makeText(SendMessageActivity.this, "您还没有填写消息内容",
						Toast.LENGTH_LONG).show();
				return;
			}
			if (CommonUtil.isNetWorkOpen(SendMessageActivity.this)) {
				message = comment_edit.getText().toString();
				issend = true;
				finish();
			} else {
				issend = false;
				CommonUtil.ShowToast(SendMessageActivity.this,
						"杯具了- -!\n联网不给力啊");
				finish();
			}
		}
	};
}

