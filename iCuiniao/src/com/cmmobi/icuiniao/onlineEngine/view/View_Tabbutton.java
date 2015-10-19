/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Button;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_TabButton;

/**
 * @author hw
 *
 */
public class View_Tabbutton extends XmlViewLayout{

	private ArrayList<Parser_Button> buttons;
	private Button tableft;
	private Button tabmiddle;
	private Button tabright;
	private int curIndex;
	
	public View_Tabbutton(Context context, Parser_TabButton parserTabButton, Handler handler) {
		super(context, handler, null);
		// TODO Auto-generated constructor stub
		buttons = parserTabButton.getButtons();
		LinearLayout l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.tabbutton, null);
		tableft = (Button)l.findViewById(R.id.tableft);
		tabmiddle = (Button)l.findViewById(R.id.tabmiddle);
		tabright = (Button)l.findViewById(R.id.tabright);
		tableft.setOnClickListener(tableftClickListener);
		tabmiddle.setOnClickListener(tabmiddleClickListener);
		tabright.setOnClickListener(tabrightClickListener);
		
		addView(l);
	}
	
	public void init(int initIndex){
		curIndex = initIndex;
		tableft.setBackgroundResource(R.drawable.tableft);
		tabmiddle.setBackgroundResource(R.drawable.tabmiddle);
		tabright.setBackgroundResource(R.drawable.tabright);
		tableft.setTextColor(0xffffffff);
		tabmiddle.setTextColor(0xffffffff);
		tabright.setTextColor(0xffffffff);
		switch (initIndex) {
		case 0://关注
			tableft.setBackgroundResource(R.drawable.tableft_f);
			tableft.setTextColor(0xcc000000);
			break;
		case 1://相互关注
			tabmiddle.setBackgroundResource(R.drawable.tabmiddle_f);
			tabmiddle.setTextColor(0xcc000000);
			break;
		case 2://粉丝
			tabright.setBackgroundResource(R.drawable.tabright_f);
			tabright.setTextColor(0xcc000000);
			break;
		}
	}
	
	private OnClickListener tableftClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(curIndex != 0){
				onFriendTabbuttonClick(buttons.get(0).getHref(), 0);
			}
			init(0);
		}
	};
	
	private OnClickListener tabmiddleClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(curIndex != 1){
				onFriendTabbuttonClick(buttons.get(1).getHref(), 1);
			}
			init(1);
		}
	};
	
	private OnClickListener tabrightClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(curIndex != 2){
				onFriendTabbuttonClick(buttons.get(2).getHref(), 2);
			}
			init(2);
		}
	};

}
