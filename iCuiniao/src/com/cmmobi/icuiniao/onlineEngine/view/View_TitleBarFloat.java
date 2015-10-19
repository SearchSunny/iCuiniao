/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_TitleBarFloat;
import com.cmmobi.icuiniao.util.PageID;

/**
 * @author hw
 *
 */
public class View_TitleBarFloat extends XmlViewLayout{

	private Parser_TitleBarFloat parserTitleBarFloat;
	
	public View_TitleBarFloat(Context context, Parser_TitleBarFloat parserTitleBarFloat,Handler handler) {
		super(context,handler,null);
		// TODO Auto-generated constructor stub
		this.parserTitleBarFloat = parserTitleBarFloat;
		LinearLayout l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.titlebarfloat, null);
		RelativeLayout rtitlebar = (RelativeLayout)l.findViewById(R.id.rtitlebar);
		
		if(parserTitleBarFloat.getPageId() == PageID.PAGEID_OWN_MAINPAGE){
			rtitlebar.setBackgroundResource(R.drawable.titlebarfloatbg2);
		}
		
		//左按钮
		Button leftButton = (Button)l.findViewById(R.id.titlebar_backbutton);
		if(parserTitleBarFloat.getLeftButton() == null){
			leftButton.setVisibility(INVISIBLE);
		}else{
			if(parserTitleBarFloat.getLeftButton().getAction() == Parser_Layout_AbsLayout.TYPE_FORM){
				leftButton.setBackgroundResource(R.drawable.titlebar_formbtn1_0);
			}
			leftButton.setOnClickListener(leftButtonClickListener);
		}
		//中间按钮
		Button middleButton = (Button)l.findViewById(R.id.titlebar_playbutton);
		if(parserTitleBarFloat.getMiddleButton() == null){
			middleButton.setVisibility(GONE);
		}else{
			middleButton.setOnClickListener(middleButtonClickListener);
		}
		//右按钮
		Button rightButton = (Button)l.findViewById(R.id.titlebar_menubutton);
		if(parserTitleBarFloat.getRightButton() == null){
			rightButton.setVisibility(INVISIBLE);
		}else{
			rightButton.setOnClickListener(rightButtonClickListener);
		}
		//add by lyb 菜单动画的替换按钮
		Button leftMenu = (Button)l.findViewById(R.id.titlebar_leftmenu);
		leftMenu.setOnClickListener(rightButtonClickListener);
		//
		//中间文本
		TextView textView = (TextView)l.findViewById(R.id.titlebar_titletext);
		if(parserTitleBarFloat.getPageId() == PageID.PAGEID_OWN_MAINPAGE){
			textView.setText("商品详情");
		}else{
			textView.setText(parserTitleBarFloat.getStr());
		}
//		textView.setText(parserTitleBarFloat.getStr());		
		addView(l);
	}
	
	private OnClickListener leftButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onTitleBarLeftButtonClick(parserTitleBarFloat.getLeftButton().getHref(), parserTitleBarFloat.getLeftButton().getAction());
		}
	};
	
	private OnClickListener middleButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserTitleBarFloat.getMiddleButton() == null){
				onTitleBarMiddleButtonClick("", -1);
			}else{
				onTitleBarMiddleButtonClick(parserTitleBarFloat.getMiddleButton().getHref(), parserTitleBarFloat.getMiddleButton().getAction());
			}
		}
	};
	
	private OnClickListener rightButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onTitleBarRightButtonClick(parserTitleBarFloat.getRightButton().getHref(), parserTitleBarFloat.getRightButton().getAction());
		}
	};
	
}
