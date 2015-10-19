/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_UserBar;

/**
 * @author hw
 *
 */
public class View_UserBar extends XmlViewLayout{

	private Parser_UserBar parserUserBar;
	private int userid;
	
	public View_UserBar(Context context, Parser_UserBar parserUserBar, Handler handler, int userid) {
		super(context, handler,null);
		// TODO Auto-generated constructor stub
		this.parserUserBar = parserUserBar;
		this.userid = userid;
		LinearLayout l = null;
		l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.userbar, null);
		Button userbar_addbutton = (Button)l.findViewById(R.id.userbar_addbutton);
		Button userbar_recommendbutton = (Button)l.findViewById(R.id.userbar_recommendbutton);
		ImageView userbar_messagebutton = (ImageView)l.findViewById(R.id.userbar_messagebutton);
		userbar_addbutton.setOnClickListener(addbtnClickListener);
		userbar_recommendbutton.setOnClickListener(recommendbtnClickListener);
		userbar_messagebutton.setOnClickListener(messagebtnClickListener);
		
		switch (parserUserBar.getLeftImage().getType()) {
		case Parser_Layout_AbsLayout.TYPE_ADD:
			userbar_addbutton.setBackgroundResource(R.drawable.addbtn_0);
			userbar_addbutton.setClickable(true);
			break;
		case Parser_Layout_AbsLayout.TYPE_ADDEACHOTHER:
			userbar_addbutton.setBackgroundResource(R.drawable.addeachotherbtn_f);
			userbar_addbutton.setClickable(false);
			break;
		case Parser_Layout_AbsLayout.TYPE_ADDOVER:
			userbar_addbutton.setBackgroundResource(R.drawable.addoverbtn_f);
			userbar_addbutton.setClickable(false);
			break;
		}
		
		addView(l);
	}
	
	private OnClickListener addbtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onUserBarAddButtonClick(parserUserBar.getLeftImage().getHref(), userid, parserUserBar.getLeftImage().getType());
		}
	};
	
	private OnClickListener messagebtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onUserBarMessageButtonClick(userid);
		}
	};
	
	private OnClickListener recommendbtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onUserBarRecommendButtonClick(userid);
		}
	};

}
