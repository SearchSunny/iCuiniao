/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_UserInfo;

/**
 * @author hw
 *
 */
public class View_UserInfo extends XmlViewLayout{

	private Parser_UserInfo parserUserInfo;
	
	private LinearLayout userinfo_likebtn;
	private LinearLayout userinfo_formbtn;
	private int userid;
	
	public View_UserInfo(Context context, Parser_UserInfo parserUserInfo, Handler handler, int userid) {
		super(context, handler,null);
		// TODO Auto-generated constructor stub
		this.parserUserInfo = parserUserInfo;
		this.userid = userid;
		LinearLayout l = null;
		l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.userinfo, null);
		TextView userinfo_name = (TextView)l.findViewById(R.id.userinfo_name);
		ImageView userinfo_gender = (ImageView)l.findViewById(R.id.userinfo_gender);
		TextView userinfo_addr = (TextView)l.findViewById(R.id.userinfo_addr);
		userinfo_likebtn = (LinearLayout)l.findViewById(R.id.userinfo_likebtn);
		userinfo_formbtn = (LinearLayout)l.findViewById(R.id.userinfo_formbtn);
		TextView userinfo_likebtntext = (TextView)l.findViewById(R.id.userinfo_likebtntext);
		TextView userinfo_formbtntext = (TextView)l.findViewById(R.id.userinfo_formtext);
//		userinfo_likebtn.setOnClickListener(likebtnClickListener);
//		userinfo_formbtn.setOnClickListener(formbtnClickListener);
		
		userinfo_name.setText(parserUserInfo.getName().getStr());
		userinfo_gender.setImageResource(parserUserInfo.getGender().getStr().equalsIgnoreCase("男")?R.drawable.manbtn:R.drawable.womenbtn);
		userinfo_addr.setText(parserUserInfo.getAddr().getStr());
		userinfo_likebtntext.setText(parserUserInfo.getLikeButton().getStr());
		userinfo_formbtntext.setText(parserUserInfo.getFormButton().getStr());
		
		addView(l);
	}
	
	private OnClickListener likebtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onUserInfoLikeButtonClick(parserUserInfo.getLikeButton().getHref(), userid, parserUserInfo.getLikeButton().getAction());
		}
	};
	
	private OnClickListener formbtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onUserInfoFormButtonClick(parserUserInfo.getFormButton().getHref(), userid, parserUserInfo.getFormButton().getAction());
		}
	};

}
