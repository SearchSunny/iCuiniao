/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.activity.MyPageActivityA;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ParserEngine;
import com.cmmobi.icuiniao.onlineEngine.view.MLinearLayout;
import com.cmmobi.icuiniao.onlineEngine.view.View_CoquetryItem;
import com.cmmobi.icuiniao.onlineEngine.view.View_TitleBar;
import com.cmmobi.icuiniao.onlineEngine.view.XmlViewLayout;
import com.cmmobi.icuiniao.ui.view.MainMenu;
import com.cmmobi.icuiniao.util.ActionID;
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
import com.icuiniao.plug.im.Entity;
import com.icuiniao.plug.im.IMDataBase;

/**
 * @author hw
 *我的撒娇
 */
public class CoquetryActivity extends Activity implements AnimationListener{

	private ConnectUtil mConnectUtil;
	private XmlViewLayout xmlViewLayout;
	private MLinearLayout mPageLayout;
	private Parser_ParserEngine parserEngine;
	
	private int curIndex;
	
	private boolean isAnimationOpen;//主菜单动画状态
	private int animationIndex;
	private MainMenu mainMenu;
	private LinearLayout lcoquetrypage;
	private boolean hide;
	private int selectIndex;
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;	
	
	private IMDataBase imDataBase;
	private boolean messageImageState;
	private RelativeLayout rtitlebar;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//删除的跳转不显示动画
		hide = getIntent().getExtras().getBoolean("hide");
		if (!hide) {
			setTheme(R.style.MessageActivityStyle);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coquetrypage);
		if(!hide){ //删除刷新需要显示的假title
			Button titlebar_sendbtn = (Button)findViewById(R.id.titlebar_sendbtn);
			titlebar_sendbtn.setVisibility(View.VISIBLE);
			Button titlebar_editbtn = (Button)findViewById(R.id.titlebar_editbtn);
			titlebar_editbtn.setVisibility(View.INVISIBLE);
		}
		imDataBase = new IMDataBase(this);
		rtitlebar = (RelativeLayout)findViewById(R.id.rtitlebar);		
		isAnimationOpen = false;
		lcoquetrypage = (LinearLayout) findViewById(R.id.lcoquetrypage);
		mainMenu = (MainMenu) findViewById(R.id.mainmenu);
		loadingBar = (ProgressBar) findViewById(R.id.loading);
		if (UserUtil.userid == -1 || UserUtil.userState != 1) {
			finish();
			return;
		}
		init();		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(messageImageState){
			
			getMessageByUserId();
		}
		
		//注册广播
		registerReceiver();
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
		if(isAnimationOpen){
			gotoMenu();
		}
	}

	public void init(){
		lcoquetrypage.removeAllViews();
		curIndex = 0;
		mPageLayout = new MLinearLayout(this,mHandler);
		mPageLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		mPageLayout.setOrientation(LinearLayout.VERTICAL);
		mPageLayout.setBackgroundResource(R.drawable.mainpagebg);
		lcoquetrypage.addView(mPageLayout);
		
		xmlViewLayout = new XmlViewLayout(this,mHandler,null);
		mConnectUtil = new ConnectUtil(this, mHandler,0);
		mConnectUtil.connect(addUrlParam(URLUtil.URL_MY_CAQUETRY, UserUtil.userid, 0), HttpThread.TYPE_PAGE,0);
	}
	
	public void renderPage(Message msg,int threadindex,int type){
		switch (type) {
		case HttpThread.TYPE_PAGE:
			parserEngine = new Parser_ParserEngine(this);			
			parserEngine.parser((byte[])msg.obj);			
			xmlViewLayout.setHideState(hide);
			xmlViewLayout.setData(parserEngine.getLayouts());
			xmlViewLayout.setParserPage(parserEngine.getPageObject());
			LinearLayout tmplayout = xmlViewLayout.buildView(0);
			if(hide == false){
				xmlViewLayout.getViewTitleBar().setState(View_TitleBar.STATE_CANCEL);
				xmlViewLayout.getCoquetry().setState(false);
			}
			if(xmlViewLayout.isPartRenderList()){
				curIndex ++;
				xmlViewLayout.getCoquetry().insertList(xmlViewLayout.getParserCoquetry());
				DownImageManager.reset();
				mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
			}else{
				curIndex = 1;
				mPageLayout.removeAllViews();
				mPageLayout.addView(tmplayout);
				mPageLayout.postInvalidate();
			}
			break;
		case HttpThread.TYPE_IMAGE:
			DownImageItem downImageItem = DownImageManager.get(threadindex);
			try {
				//保存图片
				CommonUtil.writeToFile(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(downImageItem.getUrl()), (byte[])msg.obj);
				if(downImageItem.getType() == Parser_Layout_AbsLayout.MODELTYPE_COQUETRYITEM){
					View_CoquetryItem viewCoquetryItem = xmlViewLayout.getCoquetry().getCoquetryItem(threadindex);
					viewCoquetryItem.setImageView((byte[])msg.obj);
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
				if(msg.arg1 >= 10){  //删除失败，还原动画			
					final int itemIdx = msg.arg1 - 10;
					xmlViewLayout.getCoquetry().getCoquetryItem(itemIdx).clearAnimation();
					xmlViewLayout.getCoquetry().adapter.notifyDataSetChanged();
				}
				break;
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				if("text/json".equals(msg.getData().getString("content_type"))){
					closeProgress();
					Json((byte[])msg.obj);
				}else{
					mPageLayout.setIsDownLoadOver(true);
					Bundle bundle = msg.getData();
					renderPage(msg,msg.arg1,bundle.getInt("mType"));
					mHandler.sendEmptyMessage(9900);
				}
				break;
			case MessageID.MESSAGE_CONNECT_LAYOUTOVER:
				if(rtitlebar != null){
					if(rtitlebar.isShown()){
						rtitlebar.setVisibility(View.INVISIBLE);
					}
				}
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
							new ConnectUtil(CoquetryActivity.this, mHandler,0).connect(urlString, HttpThread.TYPE_IMAGE, downImageItem.getIndex());
						}
					}
					//发起下一个询问
					mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_CONNECT_LAYOUTOVER, 100);
				}else{
					LogPrint.Print("downImageItem = null");
				}
				break;
			case MessageID.MESSAGE_CLICK_TITLEBAR_LEFTBUTTON:
				if(xmlViewLayout != null){
					if(msg.getData().getInt("action") == Parser_Layout_AbsLayout.ACTION_EDIT){
						xmlViewLayout.getViewTitleBar().setState(View_TitleBar.STATE_CANCEL);
						xmlViewLayout.getCoquetry().setState(false);
					}else if(msg.getData().getInt("action") == Parser_Layout_AbsLayout.ACTION_BACK){
						xmlViewLayout.getViewTitleBar().setState(View_TitleBar.STATE_EDIT);
						xmlViewLayout.getCoquetry().setState(true);
					}
				}
				break;
			case MessageID.MESSAGE_CLICK_TITLEBAR_RIGHTBUTTON:
				gotoMenu();
				break;
			case MessageID.MESSAGE_CLICK_COQUETRY:
//				int curIndex = msg.getData().getInt("curIndex");
				int totalpage = msg.getData().getInt("totalpage");
				boolean ismore = msg.getData().getBoolean("ismore");
				selectIndex = msg.getData().getInt("selectIndex");
				String href = msg.getData().getString("href");				
				if(ismore){
					if(curIndex < totalpage){
						if(xmlViewLayout != null){
							if(xmlViewLayout.getViewTitleBar().getState() == View_TitleBar.STATE_EDIT){
								new ConnectUtil(CoquetryActivity.this, mHandler,0).connect(addUrlParam(URLUtil.URL_MY_CAQUETRY_MORE, UserUtil.userid, curIndex), HttpThread.TYPE_PAGE, 0);
							}
						}
					}
				}else{
					if(xmlViewLayout != null){
						if(xmlViewLayout.getViewTitleBar().getState() == View_TitleBar.STATE_CANCEL){
//							Message_MenuClick menuClick = new Message_MenuClick(mHandler,selectIndex);
//							Intent intent = new Intent();
//							intent.setClass(CoquetryActivity.this, AbsCuiniaoMenu.class);
//							intent.putExtra("title", PageID.SETTING_MESSAGE_MENU_TITLE);
//							intent.putExtra("items", PageID.SETTING_MESSAGE_MENU_ITEM);
//							startActivity(intent);
//							AbsCuiniaoMenu.set(menuClick);
						}else {//跳转到单品页
							if(CommonUtil.isNetWorkOpen(CoquetryActivity.this)){
								ArrayList<String> urls = xmlViewLayout.getCoquetry().getUrls();
								if(urls!=null&&urls.size() > 0&&href != null&&href.length() > 0){
									MyPageActivityA.urls = null;
									MyPageActivityA.urls = new String[urls.size()];
									for(int i = 0;i < urls.size();i ++){
										MyPageActivityA.urls[i] = urls.get(i);
									}
									Intent intent = new Intent();
									intent.setClass(CoquetryActivity.this, MyPageActivityA.class);
									intent.putExtra("url", href);
									intent.putExtra("chickPos", selectIndex);
									intent.putExtra("type", parserEngine.getPageObject().getPageId());
									startActivity(intent);
								}
							}
						}
					}
				}
				break;
			case MessageID.MESSAGE_MAINMENU_OPEN:
				if(lcoquetrypage != null){
					if(lcoquetrypage.getRight() > getMainMenuAnimationPos(50)){
						lcoquetrypage.offsetLeftAndRight(animationIndex);
						lcoquetrypage.postInvalidate();
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_OPEN);
						animationIndex = -(CommonUtil.screen_width-getMainMenuAnimationPos(50))/5;
					}
				}
				break;
			case MessageID.MESSAGE_MAINMENU_CLOSE:
				if(lcoquetrypage != null){
					if(lcoquetrypage.getLeft() < 0){
						lcoquetrypage.offsetLeftAndRight(animationIndex);
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_CLOSE);
						lcoquetrypage.postInvalidate();
						animationIndex = (CommonUtil.screen_width-getMainMenuAnimationPos(50))/5;
					}
				}
				break;
			case MessageID.MESSAGE_MENUCLICK_MESSAGE:
				switch (msg.arg1) {
				case 0:
					int id = -1;
					id = xmlViewLayout.getCoquetry().getCoquetryItem(selectIndex).getDeleteId();
					if(id != -1){
						new ConnectUtil(CoquetryActivity.this, mHandler,0).connect(URLUtil.URL_COQUETRY_DELETE+"?id="+id, HttpThread.TYPE_PAGE, 1);
					}
					break;
				}
				break;
				//lyb
			case MessageID.MESSAGE_COQUETRY_TOUCH_DEL:
				switch (msg.arg1) {
				case 0:
					int id = -1;
					int selectIdx = msg.arg2;	
					id = xmlViewLayout.getCoquetry().getCoquetryItem(selectIdx).getDeleteId();
					if(id != -1){
						new ConnectUtil(CoquetryActivity.this, mHandler,0).connect(URLUtil.URL_COQUETRY_DELETE+"?id="+id, HttpThread.TYPE_PAGE, 10+selectIdx);
					}
					break;
				}
				break;
				
				case 998877:
				//查询消息已读、未读
				long messageId = msg.getData().getLong("messageId");
				int state = imDataBase.getIsReadState(messageId);
				//已读
				if(state == 1){
					mainMenu.setMessageButtonRes(false);
					xmlViewLayout.getViewTitleBar().setMessageImageState(false);
				}else if(state == 0 && UserUtil.userState == 1){
					mainMenu.setMessageButtonRes(true);
					xmlViewLayout.getViewTitleBar().setMessageImageState(true);
				}else if(state == 0 && UserUtil.userState != 1){
					mainMenu.setMessageButtonRes(false);
					xmlViewLayout.getViewTitleBar().setMessageImageState(false);
				}
				else if(state == -1){
					mainMenu.setMessageButtonRes(false);
					xmlViewLayout.getViewTitleBar().setMessageImageState(false);
				}
				break;
				
			case 9900:
				getMessageByUserId();
				messageImageState = true;
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
	
	public String addUrlParam(String url,int oid,int pi){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"?oid="+oid+"&pi="+pi+"&dpi="+URLUtil.dpi()+"&plaid="+URLUtil.plaid;
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
	    		lcoquetrypage.startAnimation(animation);
	    		OfflineLog.writeMainMenu();//写入离线日志
			}else{
				if(mainMenu != null){
					mainMenu.setIsCanClick(false);
				}
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.left_in);
				animation.setAnimationListener(this);
	    		animation.setFillEnabled(true);
	    		animation.setFillAfter(true);
	    		lcoquetrypage.startAnimation(animation);
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
	
	private void Json(byte[] data){
		try {
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = "+str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			if(result != null){
				if(result.equalsIgnoreCase("true")){
					CommonUtil.ShowToast(CoquetryActivity.this, "删除成功");
					Intent intent = new Intent();
					intent.setClass(CoquetryActivity.this, CoquetryActivity.class);
					intent.putExtra("hide", false);
					startActivity(intent);
					finish();
				}else{
					CommonUtil.ShowToast(CoquetryActivity.this, "删除失败");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		Button btMenu = (Button) lcoquetrypage.findViewById(R.id.titlebar_menubutton);
		if(isAnimationOpen){
			//左侧按钮之前有无的判断
			Button titlebar_backbutton = (Button) lcoquetrypage.findViewById(R.id.titlebar_backbutton);
			if(titlebar_backbutton.getVisibility() == View.VISIBLE){
				titlebar_backbutton.setVisibility(View.GONE);
				titlebar_backbutton.setTag("VISIBLE");
			}else{
				titlebar_backbutton.setVisibility(View.GONE);
				titlebar_backbutton.setTag("GONE");
			}//
			Button leftMenu = (Button)lcoquetrypage.findViewById(R.id.titlebar_leftmenu);
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
			Button leftMenu = (Button) lcoquetrypage
					.findViewById(R.id.titlebar_leftmenu);
			leftMenu.setVisibility(View.GONE);
			//左侧按钮之前有无的判断
			Button titlebar_backbutton = (Button) lcoquetrypage.findViewById(R.id.titlebar_backbutton);
			if(titlebar_backbutton != null){
				if(titlebar_backbutton.getTag() != null){
					if(titlebar_backbutton.getTag().equals("VISIBLE")){
						titlebar_backbutton.setVisibility(View.VISIBLE);
					}
				}
			}
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

	
	/**
	 * 用于接收消息广播
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			//新增消息广播
			if(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_MESSAGE.equals(action)||
					ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_PRIVATEMESSAGE.equals(action)||
					ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_SYSTEMMESSAGE.equals(action)||
					ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE.equals(action)||
					ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE_SUCCESS.equals(action) ||
					ActionID.ACTION_BROADCAST_LOGOUT.equals(action)){
				Message message = new Message();
				message.what = 998877;
				Bundle bundle = new Bundle();
				if(intent.getExtras() != null){
					
					bundle.putLong("messageId", intent.getExtras().getLong("messageId"));
				}
				
				message.setData(bundle);
				mHandler.sendMessageDelayed(message, 1000);
			}
		}
		
	};
	
	
	/**
	 * 注册广播接收器
	 */
	private final void registerReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		//新增消息广播
		//接收到一条消息
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_MESSAGE);
		//接收到一条私信
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_PRIVATEMESSAGE);
		//接收到一条系统广播
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_SYSTEMMESSAGE);
		//接收建立好友关系消息
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE_SUCCESS);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_LOGOUT);
		registerReceiver(mReceiver, intentFilter);
	}
	
	/**
	 * 卸载广播接收器
	 */
	private final void unRegisterReceiver() {
		try {
			if(mReceiver != null){
				unregisterReceiver(mReceiver);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//销毁广播
		unRegisterReceiver();
	}
	
	/**
	 * 根据消息已读\未读状态
	 */
	private void getMessageByUserId(){
		
		ArrayList<Entity> entitys =  imDataBase.getTheMessageByUserId(UserUtil.userid);
		if(entitys != null){
			boolean isFind = false;
			for (Entity entity : entitys) {
				//未读
				if(entity.getIsRead() == 0 && entity.getFromId() != UserUtil.userid && UserUtil.userState == 1){
					isFind = true;
					break;
				}
			}
			if(isFind){
				mainMenu.setMessageButtonRes(true);
				xmlViewLayout.getViewTitleBar().setMessageImageState(true);
			}else{
				mainMenu.setMessageButtonRes(false);
				xmlViewLayout.getViewTitleBar().setMessageImageState(false);
			}
			
		}else{
			mainMenu.setMessageButtonRes(false);
			xmlViewLayout.getViewTitleBar().setMessageImageState(false);
		}
		
	}
	
}
