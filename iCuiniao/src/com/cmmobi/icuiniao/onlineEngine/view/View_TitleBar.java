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
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_TitleBar;
import com.cmmobi.icuiniao.util.PageID;

/**
 * @author hw
 *
 */
public class View_TitleBar extends XmlViewLayout{

	private Parser_TitleBar parserTitleBar;
	private TextView textView;
	private ImageView imageView;
	private Button leftButton;
	private Button rightButton;
	//消息提醒
	private ImageView messageImageId;
	public final static int STATE_EDIT = 1000;
	public final static int STATE_DELETE = 1001;
	public final static int STATE_CANCEL = 1002;
	private int state = STATE_EDIT;
	private boolean empty;
	private boolean isVisibleImage;
	public View_TitleBar(Context context,Parser_TitleBar parserTitleBar,Handler handler,boolean empty){
		super(context,handler,null);
		this.parserTitleBar = parserTitleBar;
		this.empty = empty;
		LinearLayout l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.titlebar, null);
		
		messageImageId = (ImageView)l.findViewById(R.id.messageImageId);
		//左按钮
		leftButton = (Button)l.findViewById(R.id.titlebar_backbutton);
		if(parserTitleBar.getPageId() == PageID.PAGEID_MAINPAGE_MYLIKE){//我的喜欢进行特殊处理
			if(empty){
				leftButton.setVisibility(INVISIBLE);
			}else{
				state = STATE_EDIT;
				leftButton.setBackgroundResource(R.drawable.title_editbtn_0);
				leftButton.setOnClickListener(leftButtonClickListener);
			}
		}else if(parserTitleBar.getPageId() == PageID.PAGEID_COQUETRY){//我的撒娇进行特殊处理
			if(empty){
				leftButton.setVisibility(INVISIBLE);
			}else{
				state = STATE_EDIT;
				leftButton.setBackgroundResource(R.drawable.title_editbtn_0);
				leftButton.setOnClickListener(leftButtonClickListener);
			}
		}else{
			if(parserTitleBar.getLeftButton() == null){
				leftButton.setVisibility(INVISIBLE);
			}else{
				if(parserTitleBar.getLeftButton().getAction() == Parser_Layout_AbsLayout.TYPE_FORM){
					leftButton.setBackgroundResource(R.drawable.titlebar_formbtn1_0);
				}
				leftButton.setOnClickListener(leftButtonClickListener);
			}
		}
		//lyb leftmenu
		Button leftmenu = (Button)l.findViewById(R.id.titlebar_leftmenu);
		leftmenu.setOnClickListener(rightButtonClickListener);
		//右按钮
		rightButton = (Button)l.findViewById(R.id.titlebar_menubutton);
		if(parserTitleBar.getRightButton() == null){
			rightButton.setVisibility(INVISIBLE);
		}else{
			if(parserTitleBar.getRightButton().getAction() == Parser_Layout_AbsLayout.ACTION_OK){
				rightButton.setBackgroundResource(R.drawable.sendbtn_0);
			}
			rightButton.setOnClickListener(rightButtonClickListener);
		}
		//中间菜单
		textView = (TextView)l.findViewById(R.id.titlebar_titletext);
		imageView = (ImageView)l.findViewById(R.id.titlebar_titleimage);
		if(!parserTitleBar.getMenuable()){
			imageView.setVisibility(GONE);
			textView.setText(parserTitleBar.getStr());
		}else{
			switch (parserTitleBar.getPageId()) {
			case PageID.PAGEID_MAINPAGE_ALL:
				textView.setText(R.string.titlebar_text_all);
				break;
			case PageID.PAGEID_MAINPAGE_DISCOUNT:
				textView.setText(R.string.titlebar_text_discount);
				break;
			case PageID.PAGEID_MAINPAGE_RECOMMEND:
				textView.setText(R.string.titlebar_text_recommend);
				break;
			case PageID.PAGEID_MAINPAGE_MYFORM:
				textView.setText(R.string.titlebar_text_form);
				break;
			case PageID.PAGEID_MAINPAGE_ACTIVITIES:
				textView.setText(R.string.titlebar_text_activities);
				break;
			case PageID.PAGEID_MAINPAGE_MYLIKE:
				textView.setText(R.string.titlebar_text_mylike);
				break;
			case PageID.PAGEID_MAINPAGE_MYLOOKED:
				textView.setText(R.string.titlebar_text_mylooked);
				break;
			case PageID.PAGEID_MAINPAGE_USERLIKE:
				textView.setText(R.string.titlebar_text_userlike);
				break;
			case PageID.PAGEID_MAINPAGE_USERLOOKED:
				textView.setText(R.string.titlebar_text_userlooked);
				break;
			default:
				break;
			}
			textView.setOnClickListener(menuClickListener);
		}
		
		addView(l);
	}
	
	private OnClickListener leftButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserTitleBar.getPageId() == PageID.PAGEID_MAINPAGE_MYLIKE){
				if(empty == false){
					if(state == STATE_EDIT){
						onTitleBarLeftButtonClick(null,parserTitleBar.ACTION_EDIT);
					}else if(state == STATE_DELETE){
						onTitleBarLeftButtonClick(null,parserTitleBar.ACTION_DELETE);
					}
				}
			}else if(parserTitleBar.getPageId() == PageID.PAGEID_COQUETRY){
				if(empty == false){
					if(state == STATE_EDIT){
						onTitleBarLeftButtonClick(null,parserTitleBar.ACTION_EDIT);
					}else if(state == STATE_CANCEL){
						onTitleBarLeftButtonClick(null,parserTitleBar.ACTION_BACK);
					}
				}
			}else{
				onTitleBarLeftButtonClick(parserTitleBar.getLeftButton().getHref(), parserTitleBar.getLeftButton().getAction());
			}
		}
	};
	
	private OnClickListener rightButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserTitleBar.getPageId() == PageID.PAGEID_MAINPAGE_MYLIKE){
				if(state == STATE_DELETE){
					if(empty == false){
						onTitleBarRightButtonClick(null, parserTitleBar.ACTION_BACK);
					}
				}else{
					onTitleBarRightButtonClick(parserTitleBar.getRightButton().getHref(), parserTitleBar.getRightButton().getAction());
				}
			}else{
				onTitleBarRightButtonClick(parserTitleBar.getRightButton().getHref(), parserTitleBar.getRightButton().getAction());
			}
		}
	};
	
	private OnClickListener menuClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onTitleBarMenuClick(parserTitleBar.getPageId());
		}
	};
	
	public void setState(int state){
		if(empty == false){
			this.state = state;
			if(state == STATE_EDIT){
				leftButton.setBackgroundResource(R.drawable.title_editbtn_0);
				if(!parserTitleBar.getMenuable()){
					imageView.setVisibility(GONE);
					textView.setText(parserTitleBar.getStr());
				}
				rightButton.setBackgroundResource(R.drawable.titlebar_menubutton1_0);
				if(isVisibleImage){
					
					messageImageId.setVisibility(VISIBLE);
				}else{
					
					messageImageId.setVisibility(GONE);
				}
			}else if(state == STATE_DELETE){
				leftButton.setBackgroundResource(R.drawable.title_deletebtn_0);
				if(!parserTitleBar.getMenuable()){
					imageView.setVisibility(GONE);
					textView.setText("选择删除");
					messageImageId.setVisibility(GONE);
				}
				rightButton.setBackgroundResource(R.drawable.title_canclebtn_0);
			}else if(state == STATE_CANCEL){
				leftButton.setBackgroundResource(R.drawable.sendbtn_0);
				if(!parserTitleBar.getMenuable()){
					imageView.setVisibility(GONE);
					textView.setText(parserTitleBar.getStr());
					
				}
			}
		}
	}
	
	public int getState(){
		return state;
	}
	
	/**
	 *  控制消息图片的显示/隐藏
	 * @param isVisible
	 */
	public void setMessageImageState(boolean isVisible){
		
		if(isVisible){
			
			messageImageId.setVisibility(VISIBLE);
			isVisibleImage = isVisible;
		}else{
			
			messageImageId.setVisibility(GONE);
			isVisibleImage = isVisible;
		}
	}
}
