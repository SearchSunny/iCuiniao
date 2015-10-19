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

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ReplayPermit;

public class SayhiPageActivity extends Activity{

	private EditText comment_edit;
	private TextView comment_num;
	private Button titlebar_backbutton;
	private Button titlebar_menubutton;
	private int input_cur;
	private boolean issend;//是点击否发送了
	
	private boolean isBlackList;	
	private String url;	
	private String message;
	private int uid;	
	private String nickname;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sayhilayout);
		issend = false;
		isBlackList = getIntent().getExtras().getBoolean("isBlackList");		
		url = getIntent().getExtras().getString("url");
		uid = getIntent().getExtras().getInt("uid");
		nickname = getIntent().getExtras().getString("nickname");
		String title = getIntent().getExtras().getString("title");
		TextView titlebar_titletext = (TextView)findViewById(R.id.titlebar_titletext);
		titlebar_titletext.setText(title);
		
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
		intent.putExtra("isBlackList", isBlackList);		
		intent.putExtra("url", url);
		intent.putExtra("uid", uid);
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
//			// TODO Auto-generated method stub
			if(CommonUtil.isNetWorkOpen(SayhiPageActivity.this)){
				if(comment_edit.getText().toString().trim().length() > 0){					
					message = comment_edit.getText().toString();
				}else{
					message = comment_edit.getHint().toString();
				}
				issend = true;
				finish();				
			}else{
				issend = false;
				CommonUtil.ShowToast(SayhiPageActivity.this, "杯具了- -!\n联网不给力啊");
				finish();
			}
		}
	};
}

