/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.Activity;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ParserEngine;
import com.cmmobi.icuiniao.onlineEngine.view.MLinearLayout;
import com.cmmobi.icuiniao.onlineEngine.view.View_FriendList_item;
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
public class FriendActivity extends Activity implements AnimationListener{

	private ConnectUtil mConnectUtil;
	private XmlViewLayout xmlViewLayout;
	private MLinearLayout mPageLayout;
	private Parser_ParserEngine parserEngine;
	
	private LinearLayout lfriendpage;
	private Button friendsearchbtn;
	private Button addfriendbtn;
	
	private int tabbuttonIndex;
	
	private int curIndex;
	
	private boolean isAnimationOpen;//主菜单动画状态
	private int animationIndex;
	private MainMenu mainMenu;
	private RelativeLayout friendpage;
	
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendpage);
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
		isAnimationOpen = false;
		friendpage = (RelativeLayout)findViewById(R.id.friendpage);
		mainMenu = (MainMenu)findViewById(R.id.mainmenu);
		tabbuttonIndex = getIntent().getExtras().getInt("tabbuttonIndex");
		lfriendpage = (LinearLayout)findViewById(R.id.lfriendpage);
		friendsearchbtn = (Button)findViewById(R.id.friendsearchbtn);
		//lyb
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		addfriendbtn = (Button)findViewById(R.id.addfriendbtn);		
		friendsearchbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("url", URLUtil.URL_SEARCHFRIEND);
				intent.putExtra("tabbuttonIndex", tabbuttonIndex);
				intent.setClass(FriendActivity.this, SearchFriendActivity.class);
				startActivity(intent);
			}
		});
		addfriendbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("url", URLUtil.URL_ADDFRIENDS);
				intent.setClass(FriendActivity.this, AddFriendsActivity.class);
				startActivity(intent);
			}
		});
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
		init();
		//lyb 关闭菜单
		if(isAnimationOpen){
			gotoMenu();
		}
	}

	public void init(){		
		curIndex = 0;
		mPageLayout = new MLinearLayout(this,mHandler);
		mPageLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		mPageLayout.setOrientation(LinearLayout.VERTICAL);
		mPageLayout.setBackgroundResource(R.drawable.mainpagebg);
		
		xmlViewLayout = new XmlViewLayout(this,mHandler,null,tabbuttonIndex);
		mConnectUtil = new ConnectUtil(this, mHandler,0);
		String urlString = getIntent().getExtras().getString("url");
		if(urlString != null){
			if(urlString.indexOf("&type") > 0){
				mConnectUtil.connect(urlString, HttpThread.TYPE_PAGE,0);
			}else{
				mConnectUtil.connect(addUrlParam(urlString, UserUtil.userid, 0), HttpThread.TYPE_PAGE,0);
			}
		}
		
	}
	
	public void init(String urlString){
		curIndex = 0;
		mPageLayout = new MLinearLayout(this,mHandler);
		mPageLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		mPageLayout.setOrientation(LinearLayout.VERTICAL);
		mPageLayout.setBackgroundResource(R.drawable.mainpagebg);
		
		xmlViewLayout = new XmlViewLayout(this,mHandler,null,tabbuttonIndex);
		mConnectUtil = new ConnectUtil(this, mHandler,0);
		if(urlString != null){
			mConnectUtil.connect(addUrlParam(urlString, UserUtil.userid, 0), HttpThread.TYPE_PAGE,0);
		}
	}
	
	public void renderPage(Message msg,int threadindex,int type){
		switch (type) {
		case HttpThread.TYPE_PAGE:
			parserEngine = new Parser_ParserEngine(this);
			parserEngine.parser((byte[])msg.obj);
			xmlViewLayout.setData(parserEngine.getLayouts());
			xmlViewLayout.setParserPage(parserEngine.getPageObject());
			LinearLayout tmplayout = xmlViewLayout.buildView(0);
			if(xmlViewLayout.isPartRenderList()){
				curIndex ++;
				xmlViewLayout.getFriendList().insertList(xmlViewLayout.getParserList());
				DownImageManager.reset();
				mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
			}else{
				curIndex = 1;
				mPageLayout.removeAllViews();
				lfriendpage.removeAllViews();
				mPageLayout.addView(tmplayout);
				lfriendpage.addView(mPageLayout);
				lfriendpage.postInvalidate();
			}
			break;
		case HttpThread.TYPE_IMAGE:
			DownImageItem downImageItem = DownImageManager.get(threadindex);
			try {
				//保存图片
				CommonUtil.writeToFile(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(downImageItem.getUrl()), (byte[])msg.obj);
				if(downImageItem.getType() == Parser_Layout_AbsLayout.MODELTYPE_LISTITEM){
					View_FriendList_item viewFriendListItem = xmlViewLayout.getFriendList().getListItem(threadindex);
					viewFriendListItem.setImageView((byte[])msg.obj);
				}
			} catch (Exception e) {
			}
			break;
		default:
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
				mPageLayout.setIsDownLoadOver(true);
				Bundle bundle = msg.getData();
				renderPage(msg,msg.arg1,bundle.getInt("mType"));				
				//lyb 恢复后菜单按钮状态	(onResume重新发起了连接)			
				if(isAnimationOpen){
					Button leftMenu = (Button)friendpage.findViewById(R.id.titlebar_leftmenu);
					Button btMenu = (Button) friendpage.findViewById(R.id.titlebar_menubutton);
					Button leftButton = (Button)friendpage.findViewById(R.id.titlebar_backbutton);
					leftButton.setVisibility(View.GONE);			
					leftMenu.setVisibility(View.VISIBLE);			
					btMenu.setClickable(false);
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
							new ConnectUtil(FriendActivity.this, mHandler,0).connect(urlString, HttpThread.TYPE_IMAGE, downImageItem.getIndex());
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
			case MessageID.MESSAGE_CLICK_LISTITEM_FRIEND:
				String url = msg.getData().getString("url");
				boolean isMoreItem = msg.getData().getBoolean("isMoreItem");
//				int curIndex = msg.getData().getInt("curIndex");
				int totalpage = msg.getData().getInt("totalpage");
				int userid = msg.getData().getInt("userid");
				if(isMoreItem){//查看更多
					if(curIndex < totalpage){
						if(URLUtil.IsLocalUrl()){
							
						}else{
							switch (tabbuttonIndex) {
							case 0://关注
								new ConnectUtil(FriendActivity.this, mHandler,0).connect(addUrlParam(URLUtil.URL_MYFRIEND_MORE_ADD_LISTITEM, UserUtil.userid, curIndex), HttpThread.TYPE_PAGE, 0);
								break;
							case 1://相互关注
								new ConnectUtil(FriendActivity.this, mHandler,0).connect(addUrlParam(URLUtil.URL_MYFRIEND_MORE_ADDEACHOTHER_LISTITEM, UserUtil.userid, curIndex), HttpThread.TYPE_PAGE, 0);
								break;
							case 2://粉丝
								new ConnectUtil(FriendActivity.this, mHandler,0).connect(addUrlParam(URLUtil.URL_MYFRIEND_MORE_FANS_LISTITEM, UserUtil.userid, curIndex), HttpThread.TYPE_PAGE, 0);
								break;
							}
						}
					}
				}else{//普通列表项
					Intent intent4 = new Intent();
					intent4.putExtra("url", addUrlParam1(URLUtil.URL_USERPAGE, UserUtil.userid, userid));
					intent4.setClass(FriendActivity.this, UserPageActivity.class);
					startActivity(intent4);
				}
				break;
			case MessageID.MESSAGE_CLICK_FRIEND_TABBUTTON:
//				String url1 = msg.getData().getString("url");
				tabbuttonIndex = msg.getData().getInt("curIndex");
				switch (tabbuttonIndex) {
				case 0://关注
					init(URLUtil.URL_MYFRIEND_ADD);
					break;
				case 1://相互关注
					init(URLUtil.URL_MYFRIEND_EACHOTHER);
					break;
				case 2://粉丝
					init(URLUtil.URL_MYFRIEND_FANS);
					break;
				}
				break;
			case MessageID.MESSAGE_CLICK_LISTITEM_FRIENDCHILD:
				int uid = msg.getData().getInt("userid");
//				String flush = msg.getData().getString("flush");
				int type = msg.getData().getInt("type");
				String stype = "";
				if(type == Parser_Layout_AbsLayout.TYPE_ADD){
					stype = "add";
				}else if(type == Parser_Layout_AbsLayout.TYPE_ADDEACHOTHER){
					stype = "addeachother";
				}else if(type == Parser_Layout_AbsLayout.TYPE_CANCEL){
					stype = "cancel";
				}
				
				Intent intent = new Intent();
				switch (tabbuttonIndex) {
				case 0://关注
					intent.putExtra("tabbuttonIndex", 0);
					intent.putExtra("url", addUrlParam(URLUtil.URL_MYFRIEND_ADD_CHANGE, UserUtil.userid, uid,stype));
					intent.setClass(FriendActivity.this, FriendActivity.class);
					startActivity(intent);
					finish();
					break;
				case 1://相互关注
					intent.putExtra("tabbuttonIndex", 1);
					intent.putExtra("url", addUrlParam(URLUtil.URL_MYFRIEND_EACHOTHER_CHANGE, UserUtil.userid, uid,stype));
					intent.setClass(FriendActivity.this, FriendActivity.class);
					startActivity(intent);
					finish();
					break;
				case 2://粉丝
					intent.putExtra("tabbuttonIndex", 2);
					intent.putExtra("url", addUrlParam(URLUtil.URL_MYFRIEND_FANS_CHANGE, UserUtil.userid, uid,stype));
					intent.setClass(FriendActivity.this, FriendActivity.class);
					startActivity(intent);
					finish();
					break;
				}
				break;
			case MessageID.MESSAGE_MAINMENU_OPEN:
				if(friendpage != null){
					if(friendpage.getRight() > getMainMenuAnimationPos(50)){
						friendpage.offsetLeftAndRight(animationIndex);
						friendpage.postInvalidate();
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_OPEN);
						animationIndex = -(CommonUtil.screen_width-getMainMenuAnimationPos(50))/5;
					}
				}
				break;
			case MessageID.MESSAGE_MAINMENU_CLOSE:
				if(friendpage != null){
					if(friendpage.getLeft() < 0){
						friendpage.offsetLeftAndRight(animationIndex);
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_CLOSE);
						friendpage.postInvalidate();
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
	
	public String addUrlParam(String url,int oid,int pi){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"?oid="+oid+"&pi="+pi;
	}
	
	public String addUrlParam(String url,int oid,int uid,String type){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"?oid="+oid+"&uid="+uid+"&type="+type;
	}
	
	public String addUrlParam1(String url,int oid,int uid){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"?oid="+oid+"&uid="+uid;
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
	    		friendpage.startAnimation(animation);
	    		OfflineLog.writeMainMenu();//写入离线日志
			}else{
				if(mainMenu != null){
					mainMenu.setIsCanClick(false);
				}
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.left_in);
				animation.setAnimationListener(this);
	    		animation.setFillEnabled(true);
	    		animation.setFillAfter(true);
	    		friendpage.startAnimation(animation);
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
		Button btMenu = (Button) friendpage.findViewById(R.id.titlebar_menubutton);
		if(isAnimationOpen){
			//左侧按钮之前有无的判断
			Button titlebar_backbutton = (Button) friendpage.findViewById(R.id.titlebar_backbutton);
			if(titlebar_backbutton.getVisibility() == View.VISIBLE){
				titlebar_backbutton.setVisibility(View.GONE);
				titlebar_backbutton.setTag("VISIBLE");
			}else{
				titlebar_backbutton.setVisibility(View.GONE);
				titlebar_backbutton.setTag("GONE");
			}//
			Button leftMenu = (Button)friendpage.findViewById(R.id.titlebar_leftmenu);
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
		if(!isAnimationOpen){
			Button leftMenu = (Button)friendpage.findViewById(R.id.titlebar_leftmenu);
			leftMenu.setVisibility(View.INVISIBLE);
			//左侧按钮之前有无的判断
			Button titlebar_backbutton = (Button) friendpage.findViewById(R.id.titlebar_backbutton);
			if(titlebar_backbutton.getTag().equals("VISIBLE")){
				titlebar_backbutton.setVisibility(View.VISIBLE);
			}
		}
		
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {		
		int barY = 80 * CommonUtil.screen_width / 480 ;
		if(isAnimationOpen){			
			if(ev.getY() <= barY+15 ){
				return super.dispatchTouchEvent(ev);
			}	
			final int offsetY = 40 * CommonUtil.screen_width / 480;
			ev.setLocation(ev.getX(), ev.getY() - offsetY);		
			return mainMenu.dispatchTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
	}
}
