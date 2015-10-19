/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.activity;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.Activity;
import com.cmmobi.icuiniao.Activity.LoginAndRegeditActivity;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ParserEngine;
import com.cmmobi.icuiniao.onlineEngine.view.MLinearLayout;
import com.cmmobi.icuiniao.onlineEngine.view.View_Image;
import com.cmmobi.icuiniao.onlineEngine.view.XmlViewLayout;
import com.cmmobi.icuiniao.ui.view.MainMenu;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.DownImageManager;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

/**
 * @author hw
 * 已废弃
 */
public class UserPageActivity extends Activity implements AnimationListener{

	private ConnectUtil mConnectUtil;
	private XmlViewLayout xmlViewLayout;
	private MLinearLayout mPageLayout;
	private Parser_ParserEngine parserEngine;
	private boolean isAnimationOpen;//主菜单动画状态
	private int animationIndex;
	private MainMenu mainMenu;
	private LinearLayout userpage;
	
	private int userid;
	private String userName;
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isAnimationOpen = false;
		setContentView(R.layout.userpage);
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
		userpage = (LinearLayout)findViewById(R.id.userpage);
		mainMenu = (MainMenu)findViewById(R.id.mainmenu);
		//lyb
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		init();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
		if(isAnimationOpen){
			gotoMenu();
		}
	}

	public void init(){
		userpage.removeAllViews();
		mPageLayout = new MLinearLayout(this,mHandler);
		mPageLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		mPageLayout.setOrientation(LinearLayout.VERTICAL);
		mPageLayout.setBackgroundColor(0xfffef9f9);
		userpage.addView(mPageLayout);
		
		xmlViewLayout = new XmlViewLayout(this,mHandler,null);
		mConnectUtil = new ConnectUtil(this, mHandler,0);
		String urlString = getIntent().getExtras().getString("url");
		if(urlString != null){
			mConnectUtil.connect(urlString, HttpThread.TYPE_PAGE,0);
		}
	}
	
	public void renderPage(Message msg,int threadindex,int type){
		switch (type) {
		case HttpThread.TYPE_PAGE:
			mPageLayout.removeAllViews();
			parserEngine = new Parser_ParserEngine(this);
//			LogPrint.Print("lyb", new String((byte[])msg.obj));
			parserEngine.parser((byte[])msg.obj);
			userid = parserEngine.getPageObject().getUserId();
			xmlViewLayout.setData(parserEngine.getLayouts());
			xmlViewLayout.setParserPage(parserEngine.getPageObject());
			userName = xmlViewLayout.getTitleName();
			mPageLayout.addView(xmlViewLayout.buildView(0));
			mPageLayout.postInvalidate();
			
			break;
		case HttpThread.TYPE_IMAGE:
			DownImageItem downImageItem = DownImageManager.get(threadindex);
			try {
				//保存图片
				CommonUtil.writeToFile(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(downImageItem.getUrl()), (byte[])msg.obj);
				if(downImageItem.getType() == Parser_Layout_AbsLayout.MODELTYPE_IMAGE){
					View_Image viewImage = xmlViewLayout.getViewImage();
					viewImage.setImageViewAddLine((byte[])msg.obj,null);
				}
			} catch (Exception e) {
			}
			break;
		}
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageID.MESSAGE_CONNECT_START:
				addProgress();
				break;
			case MessageID.MESSAGE_CONNECT_ERROR:
				closeProgress();
				CommonUtil.ShowToast(getApplicationContext(), (String)msg.obj);
				break;
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				if("text/json".equals(msg.getData().getString("content_type"))){
					Json((byte[])msg.obj);
					closeProgress();
				}else{
					mPageLayout.setIsDownLoadOver(true);
					Bundle bundle = msg.getData();
					renderPage(msg,msg.arg1,bundle.getInt("mType"));
				}
				break;
			case MessageID.MESSAGE_CONNECT_LAYOUTOVER:
				closeProgress();
				//取出一个可用的图片下载
				DownImageItem downImageItem = DownImageManager.get();
				if(downImageItem != null){
					LogPrint.Print("image downloading");
					//只有在同一页面中才发起图片下载
					if(parserEngine.getPageObject().getPageId() == downImageItem.getPageId()){
						String urlString = downImageItem.getUrl();
						LogPrint.Print("urlString =  "+urlString);
						if(urlString != null){
							new ConnectUtil(UserPageActivity.this, mHandler,0).connect(urlString, HttpThread.TYPE_IMAGE, downImageItem.getIndex());
						}
					}
					//发起下一个询问
					mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_CONNECT_LAYOUTOVER, 100);
				}else{
					LogPrint.Print("downImageItem = null");
				}
				break;
			case MessageID.MESSAGE_CLICK_TITLEBAR_LEFTBUTTON:
				finish();
				break;
			case MessageID.MESSAGE_CLICK_TITLEBAR_RIGHTBUTTON:
				gotoMenu();
				break;
			case MessageID.MESSAGE_CLICK_USERBAR_LEFTBUTTON:
				if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆
					CommonUtil.ShowToast(getApplicationContext(), "请登陆或者注册，变身为小C的主人！");
					Intent intent11 = new Intent();
					intent11.setClass(UserPageActivity.this, LoginAndRegeditActivity.class);
					startActivity(intent11);
				}else{
					String url = msg.getData().getString("url");
					int type = msg.getData().getInt("type");
					String stype = "";
					switch (type) {
					case Parser_Layout_AbsLayout.TYPE_ADD:
						stype = "add";
						break;
					case Parser_Layout_AbsLayout.TYPE_ADDEACHOTHER:
						stype = "addeachother";
						break;
					case Parser_Layout_AbsLayout.TYPE_ADDOVER:
						stype = "addove";
						break;
					}
					Intent intent = new Intent();
					intent.putExtra("url", addUrlParam(url, stype));
					intent.setClass(UserPageActivity.this, UserPageActivity.class);
					startActivity(intent);
					finish();
				}
				break;
			case MessageID.MESSAGE_CLICK_USERBAR_MESSAGE:
				if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆
					CommonUtil.ShowToast(getApplicationContext(), "请登陆或者注册，变身为小C的主人！");
					Intent intent11 = new Intent();
					intent11.setClass(UserPageActivity.this, LoginAndRegeditActivity.class);
					startActivity(intent11);
				}else{
					buildCommentDialog(UserUtil.userid, userid);
				}
				break;
			case MessageID.MESSAGE_CLICK_USERBAR_PUSHFRIEND:
				if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆
					CommonUtil.ShowToast(getApplicationContext(), "请登陆或者注册，变身为小C的主人！");
					Intent intent11 = new Intent();
					intent11.setClass(UserPageActivity.this, LoginAndRegeditActivity.class);
					startActivity(intent11);
				}else{
					Intent intent2 = new Intent();
					intent2.putExtra("url", URLUtil.URL_PUSHFRIEND);
					intent2.putExtra("fromid", userid);
					intent2.putExtra("username", userName);
					intent2.setClass(UserPageActivity.this, PushFriendActivity.class);
					startActivity(intent2);
					finish();
				}
				break;
			case MessageID.MESSAGE_CLICK_USERINFO_LIKE:
//			case MessageID.MESSAGE_CLICK_USERINFO_FORM:
				Intent intent = new Intent();
				intent.putExtra("url", msg.getData().getString("url"));
				intent.setClass(UserPageActivity.this, StreampageActivity.class);
				startActivity(intent);
				break;
			case MessageID.MESSAGE_MAINMENU_OPEN:
				if(userpage != null){
					if(userpage.getRight() > getMainMenuAnimationPos(50)){
						userpage.offsetLeftAndRight(animationIndex);
						userpage.postInvalidate();
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_OPEN);
						animationIndex = -(CommonUtil.screen_width-getMainMenuAnimationPos(50))/5;
					}
				}
				break;
			case MessageID.MESSAGE_MAINMENU_CLOSE:
				if(userpage != null){
					if(userpage.getLeft() < 0){
						userpage.offsetLeftAndRight(animationIndex);
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_CLOSE);
						userpage.postInvalidate();
						animationIndex = (CommonUtil.screen_width-getMainMenuAnimationPos(50))/5;
					}
				}
				break;
			}
		}
		
	};
	
//	private SystemProgress progress;
	public void addProgress(){
		try {
//			if(progress != null)return;
//			progress = new SystemProgress(this, this, null){
//				public void cancelData(){
//					
//				}
//			};
			if(loadingBar != null){
				loadingBar.setVisibility(View.VISIBLE);
			}
			
		} catch (Exception e) {
		}
	}
	
	public void closeProgress(){
//		if(progress != null){
//			progress.close();
//			progress = null;
//		}
		if(loadingBar != null){
			loadingBar.setVisibility(View.INVISIBLE);
		}
	}
	
	public String addUrlParam(String url,String type){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"&type="+type;
	}
	
	public String addUrlParam(String url,int oid,int uid,String msg){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		if(msg == null)msg="";
		return url+"?oid="+oid+"&uid="+uid+"&msg="+CommonUtil.toUrlEncode(msg);
	}
	
	private String msgString;
	private int input_cur;
	//评论对话框 0:商品评论,1:用户评论
	public void buildCommentDialog(final int oid,final int uid){
		input_cur = 100;
		LinearLayout l = (LinearLayout)getLayoutInflater().inflate(R.layout.comment_dialog,null);
		final EditText et = (EditText)l.findViewById(R.id.comment_dialog);
		final TextView input_num = (TextView)l.findViewById(R.id.input_num);
		et.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				input_num.setText("您还可输入"+input_cur+"字");
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				input_num.setText("您还可输入"+input_cur+"字");
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if(et.getText().length() >= 100){
					CommonUtil.ShowToast(UserPageActivity.this, "主人，您写那么多字做咩？");
				}
				input_cur = 100-et.getText().length();
				input_num.setText("您还可输入"+input_cur+"字");
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(UserPageActivity.this);
		builder.setTitle("发消息").setView(l).setPositiveButton("发送", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(et.getText() == null){
					msgString = "";
				}else{
					msgString = et.getText().toString();
				}
				mConnectUtil = new ConnectUtil(UserPageActivity.this, mHandler,0);
				mConnectUtil.connect(addUrlParam(URLUtil.URL_MESSAGE_SEND, oid, uid, msgString), HttpThread.TYPE_PAGE, 0);
			}
		}).setNegativeButton("取消", null).show();
	}
	
	private void Json(byte[] data){
		try {
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = "+str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			if(result != null){
				if(result.equalsIgnoreCase("true")){
					CommonUtil.ShowToast(UserPageActivity.this, "发送成功");
					String to = jObject.getString("to");
					String to_name = jObject.getString("to_name");
					String msg = jObject.getString("msg");
					String time = jObject.getString("time");
					//写入本地文件
					CommonUtil.writeMessage(CommonUtil.dir_message_user+UserUtil.userid+CommonUtil.endName, ""+UserUtil.userid, UserUtil.username, to, to_name, msg, time);
				}else{
					String msg = jObject.getString("msg");
					if(msg != null){
						CommonUtil.ShowToast(UserPageActivity.this, "发送失败!\n"+msg);
					}else{
						CommonUtil.ShowToast(UserPageActivity.this, "发送失败!");
					}
				}
			}else{
				CommonUtil.ShowToast(UserPageActivity.this, "发送失败");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(mPageLayout.isDownLoadOver()){
			gotoMenu();
		}		
		return true;
	}
	
	private void gotoMenu(){
		if(mPageLayout != null){
			isAnimationOpen = !isAnimationOpen;			
			if(isAnimationOpen){
				if(mainMenu != null){
					mainMenu.setIsCanClick(true);
				}
				mHandler.removeMessages(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.right_out);
	    		animation.setFillEnabled(true);
	    		animation.setFillAfter(true);
	    		animation.setAnimationListener(this);
	    		//动画播放，实际布局坐标不变
	    		userpage.startAnimation(animation);
	    		OfflineLog.writeMainMenu();//写入离线日志
			}else{
				if(mainMenu != null){
					mainMenu.setIsCanClick(false);
				}
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.left_in);
				animation.setAnimationListener(this);
	    		animation.setFillEnabled(true);
	    		animation.setFillAfter(true);
	    		userpage.startAnimation(animation);
			}						
		}		
	}
	
	private int getMainMenuAnimationPos(int dip){
		int result = CommonUtil.dip2px(this, dip);
		if(result%10 == 0){
			return result;
		}else{
			if(result%10 < 5){
				result -= result%10;
			}else{
				result += 10 - result%10;
			}
		}
		return result;
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		Button btMenu = (Button) userpage.findViewById(R.id.titlebar_menubutton);
		if(isAnimationOpen){
			Button leftButton = (Button)userpage.findViewById(R.id.titlebar_backbutton);
			leftButton.setVisibility(View.GONE);
			Button leftMenu = (Button)userpage.findViewById(R.id.titlebar_leftmenu);
			leftMenu.setVisibility(View.VISIBLE);			
			btMenu.setClickable(false);
		}else{
			btMenu.setClickable(true);
		}
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		if (!isAnimationOpen) {
			Button leftMenu = (Button) userpage
					.findViewById(R.id.titlebar_leftmenu);
			leftMenu.setVisibility(View.GONE);
			Button leftButton = (Button) userpage
					.findViewById(R.id.titlebar_backbutton);
			leftButton.setVisibility(View.VISIBLE);
		}
		
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int barY = 80 * CommonUtil.screen_width / 480 ;
		if(isAnimationOpen){			
			if(ev.getY() <= barY+15){
				return super.dispatchTouchEvent(ev);
			}
			final int offsetY = 40 * CommonUtil.screen_width / 480;
			ev.setLocation(ev.getX(), ev.getY() - offsetY);		
			return mainMenu.dispatchTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
	}	
}
