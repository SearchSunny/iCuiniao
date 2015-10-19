/**
 * 
 */
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

/**
 * @author hw
 *评论编辑界面
 */
public class CommentPageActivity extends Activity{

	private EditText comment_edit;
	private TextView comment_num;
	private Button titlebar_backbutton;
	private Button titlebar_menubutton;
	private int input_cur;
	private boolean isSend;//是点击否发送了
	private int issubject;//评论类型，-1：商品评论，0：1级评论，1：多级评论
	private int subjectid;
	private int commentid;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.commentlayout);
		isSend = false;
		issubject = getIntent().getExtras().getInt("issubject");
		subjectid = getIntent().getExtras().getInt("subjectid");
		commentid = getIntent().getExtras().getInt("commentid");
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
		Intent intent = new Intent();
		intent.putExtra("issend", isSend);
		intent.putExtra("msg", comment_edit.getText().toString().trim());
		intent.putExtra("issubject", issubject);
		intent.putExtra("subjectid", subjectid);
		intent.putExtra("commentid", commentid);
		setResult(RESULT_OK, intent);
		ReplayPermit.isMayClick = true;
		super.finish();
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
			if(CommonUtil.isNetWorkOpen(CommentPageActivity.this)){
				if(comment_edit.getText().toString().trim().length() > 0){
					isSend = true;
					finish();
				}else{
					CommonUtil.ShowToast(CommentPageActivity.this, "您未填写评论信息");
				}
			}else{
				isSend = false;
				CommonUtil.ShowToast(CommentPageActivity.this, "杯具了- -!\n联网不给力啊");
				finish();
			}
		}
	};
}
