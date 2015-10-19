  /**
 * 
 */
package com.cmmobi.icuiniao.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.GestureActivity;
import com.cmmobi.icuiniao.Activity.LoginAndRegeditActivity;
import com.cmmobi.icuiniao.Activity.MessageManagerActivityA;
import com.cmmobi.icuiniao.Activity.ShareActivity;
import com.cmmobi.icuiniao.onlineEngine.activity.WebviewActivity;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.UserUtil;

/**
 * @author hw
 *
 *向右弹出菜单
 */
public class PopupMenu extends FrameLayout{

	public ImageButton mPopupMenu_Button;
	public PopupFwRightView mPopupFwRightView;
	private LinearLayout lbutton;
	private ImageView backImageView;
	private ImageView shareImageView;
	private ImageView lookImageView;
	private ImageView messageImageView;
	private ImageView gestureImageView;
	
	private String wapUrl;
	private String titleString;
	private String commodityImageString;
	private int commodityid;
	private String commodityInfoString;
	private Activity activity;
	private boolean isOnlyBack;
	
	private PopupMenu popupMenu;
	
	public PopupMenu(Context context){
		super(context);
	}
	
	public PopupMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public PopupMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setActivity(Activity activity){
		this.activity = activity;
	}
	
	public void setWapUrl(String wapUrl){
		this.wapUrl = wapUrl;
	}
	
	public void setTitle(String title){
		this.titleString = title;
	}
	
	public void setCommodityImageString(String str){
		this.commodityImageString = str;
	}
	
	public void setCommodityId(int id){
		this.commodityid = id;
	}
	
	public void setCommodityInfoString(String str){
		this.commodityInfoString = str;
	}
	
	public void setIsOnlyBack(boolean b){
		isOnlyBack = b;
	}

  	public void init(PopupMenu popupMenu){
  		this.popupMenu = popupMenu;
  		wapUrl = null;
  		titleString = null;
		mPopupMenu_Button = (ImageButton)popupMenu.findViewById(R.id.popupmenu_button);
		mPopupMenu_Button.setOnClickListener(mPopupMenu_ButtonListener);
		
		mPopupFwRightView = (PopupFwRightView)popupMenu.findViewById(R.id.popupFwRightView);
		lbutton = (LinearLayout)popupMenu.findViewById(R.id.lbutton);
		backImageView = (ImageView)popupMenu.findViewById(R.id.backImageView);
		shareImageView = (ImageView)popupMenu.findViewById(R.id.shareImageView);
		lookImageView = (ImageView)popupMenu.findViewById(R.id.lookImageView);
		messageImageView = (ImageView)popupMenu.findViewById(R.id.messageImageView);
		gestureImageView = (ImageView)popupMenu.findViewById(R.id.gestureImageView);
		
		backImageView.setOnClickListener(mImgItemsListener_back);
		shareImageView.setOnClickListener(mImgItemsListener_share);
		lookImageView.setOnClickListener(mImgItemsListener_look);
		messageImageView.setOnClickListener(mImgItemsListener_message);
		gestureImageView.setOnClickListener(mImgItemsListener_gesture);
		mPopupFwRightView.setHandler(mHandler);
		
		mPopupFwRightView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				int keycode = event.getKeyCode();
				switch (event.getAction()) {
				case KeyEvent.ACTION_UP:
					if(keycode == KeyEvent.KEYCODE_BACK){
//						mHandler.sendEmptyMessage(MessageID.MESSAGE_POPUPMENU_POPING_CLOSE);
						if(activity != null&&!isOnlyBack){
							activity.finish();
						}
						isOnlyBack = false;
					}
					break;
				default:
					break;
				}
				return true;
			}
		});
		//初始化菜单状态为打开方式
		mHandler.sendEmptyMessage(MessageID.MESSAGE_POPUPMENU_BUTTONCLICK);
	}
  	
  	//分享功能
  	public void share(){
  		//分享的登录判断
  		if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆
			CommonUtil.ShowToast(getContext(), "请登陆或者注册，变身为小C的主人！");
			Intent intent11 = new Intent();
			intent11.setClass(getContext(), LoginAndRegeditActivity.class);
			getContext().startActivity(intent11);
			return;
  		}  		
  		Intent intent = new Intent();
  		intent.putExtra("url", wapUrl);
  		intent.putExtra("title", titleString);
  		intent.putExtra("image", commodityImageString);
  		intent.putExtra("commodityInfoString", commodityInfoString);
  		intent.putExtra("commodityid", commodityid);
  		intent.setClass(getContext(), ShareActivity.class);
  		getContext().startActivity(intent);
  	}
	
	//按钮监听
	public OnClickListener mPopupMenu_ButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			LogPrint.Print("onClick");
			if(mPopupFwRightView.isCloseAnimationOver()){
				mHandler.sendEmptyMessage(MessageID.MESSAGE_POPUPMENU_BUTTONCLICK);
			}
		}
	};
	
	//菜单上的五个按钮的监听
	//返回
	public OnClickListener mImgItemsListener_back = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			LogPrint.Print("back");
			if(mPopupFwRightView.isOpenAnimationOver()){
				mHandler.sendEmptyMessage(MessageID.MESSAGE_POPUPMENU_POPING_CLOSE);
			}
		}
	};
	
	//分享
	public OnClickListener mImgItemsListener_share = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			LogPrint.Print("share");
			isOnlyBack = true;
//			MyPageActivity.isGotoClickMenu = true;
			share();
		}
	};
	
	//看看
	public OnClickListener mImgItemsListener_look = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			LogPrint.Print("look");
			if(wapUrl != null){
				isOnlyBack = true;
				Intent intent = new Intent();
				intent.setClass(getContext(), WebviewActivity.class);
				LogPrint.Print("==============kankan = "+wapUrl);
				intent.putExtra("url",wapUrl);
				intent.putExtra("title", titleString);
				intent.putExtra("commodityid", commodityid);
				intent.putExtra("commodityImageString", commodityImageString);
				intent.putExtra("commodityInfoString", commodityInfoString);
				getContext().startActivity(intent);
			}
		}
	};
	
	//信息
	public OnClickListener mImgItemsListener_message = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			LogPrint.Print("message");
			isOnlyBack = true;
			if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆
				CommonUtil.ShowToast(getContext(), "请登陆或者注册，变身为小C的主人！");
				Intent intent11 = new Intent();
				intent11.setClass(getContext(), LoginAndRegeditActivity.class);
				getContext().startActivity(intent11);
			}else{
				//消息点取消
				//popupMenu.findViewById(R.id.messageImageId).setVisibility(v.GONE);
				
				Intent intent2 = new Intent();
				intent2.putExtra("type", 0);//默认为用户消息
				intent2.setClass(getContext(), MessageManagerActivityA.class);
				getContext().startActivity(intent2);
				OfflineLog.writeMessageManager();//写入离线日志
			}
		}
	};
	
	//手势
	public OnClickListener mImgItemsListener_gesture = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			LogPrint.Print("gesture");
			isOnlyBack = true;
//			MyPageActivity.isGotoClickMenu = true;
			getContext().startActivity(new Intent(getContext(), GestureActivity.class));
		}
	};

  	public Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MessageID.MESSAGE_POPUPMENU_BUTTONCLICK:
				LogPrint.Print("handler-buttonclick");
				//1 按钮设置为隐藏
				mPopupMenu_Button.setVisibility(INVISIBLE);
				mPopupMenu_Button.setClickable(false);
				//2 显示PopupFwRightView，并发送消息到PopupFwRightView通知开始绘制
				mPopupFwRightView.setVisibility(VISIBLE);
				mPopupFwRightView.setPopupState(PopupFwRightView.POPUP_POPING_OPEN);
				mPopupFwRightView.mHandler.sendEmptyMessage(MessageID.MESSAGE_POPUPMENU_POPING_OPEN);
				break;
			case MessageID.MESSAGE_POPUPMENU_POP_OPENOVER:
				//展开动画完成,显示5个按钮
				lbutton.setVisibility(VISIBLE);
				backImageView.setClickable(true);
				shareImageView.setClickable(true);
				lookImageView.setClickable(true);
				messageImageView.setClickable(true);
				gestureImageView.setClickable(true);
				break;
			case MessageID.MESSAGE_POPUPMENU_POPING_CLOSE:
				//开始绘制关闭动画
				backImageView.setClickable(false);
				shareImageView.setClickable(false);
				lookImageView.setClickable(false);
				messageImageView.setClickable(false);
				gestureImageView.setClickable(false);
				mPopupFwRightView.setLButton(lbutton);
				mPopupFwRightView.setPopupState(PopupFwRightView.POPUP_POPING_CLOSE);
				mPopupFwRightView.mHandler.sendEmptyMessage(MessageID.MESSAGE_POPUPMENU_POPING_CLOSE);
				break;
			case MessageID.MESSAGE_POPUPMENU_POP_CLOSEOVER:
				//关闭动画执行完毕
				mPopupMenu_Button.setVisibility(VISIBLE);
				mPopupMenu_Button.setClickable(true);
				mPopupFwRightView.setVisibility(INVISIBLE);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	
	
	
	
//	public void closeMenu(){
//		if(mPopupFwRightView.isOpenAnimationOver()){
//			mHandler.sendEmptyMessage(MessageID.MESSAGE_POPUPMENU_POPING_CLOSE);
//		}
//	}
//	
//	public void openMenu(){
//		if(mPopupFwRightView.isCloseAnimationOver()){
//			mHandler.sendEmptyMessage(MessageID.MESSAGE_POPUPMENU_BUTTONCLICK);
//		}
//	}
}
