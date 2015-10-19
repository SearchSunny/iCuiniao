/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
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
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.Activity;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ParserEngine;
import com.cmmobi.icuiniao.onlineEngine.view.MLinearLayout;
import com.cmmobi.icuiniao.onlineEngine.view.View_StreamPageItem;
import com.cmmobi.icuiniao.onlineEngine.view.View_TitleBar;
import com.cmmobi.icuiniao.onlineEngine.view.XmlViewLayout;
import com.cmmobi.icuiniao.ui.view.MainMenu;
import com.cmmobi.icuiniao.util.ActionID;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.DownImageManager;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MHandlerThread;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;
import com.icuiniao.plug.im.Entity;
import com.icuiniao.plug.im.IMDataBase;

/**
 * @author hw
 *瀑布流
 */
public class StreampageActivity extends Activity implements AnimationListener{

	private ConnectUtil mConnectUtil;
	private String urlString;
	private XmlViewLayout xmlViewLayout;
	private MLinearLayout mPageLayout;
	private Parser_ParserEngine parserEngine;
	private boolean isAnimationOpen;//主菜单动画状态
	private int animationIndex;
	private MainMenu mainMenu;
	private LinearLayout streampagelayout;
	IMDataBase imDataBase;
	private int curIndex;
	private ArrayList<DeleteDate> deleteDates;//需要删除的数据
	
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;
	
	private boolean messageImageState;
	private RelativeLayout rtitlebar;
	private TextView titlebar_titletext;
	private boolean isFromLikeLimitDialog;//是否是从喜欢个数上限的弹出框过来的
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imDataBase = new IMDataBase(this);
		newHandler();
		isAnimationOpen = false;
		setContentView(R.layout.streampagelayout);
		urlString = getIntent().getExtras().getString("url");
		if(urlString.equals(URLUtil.URL_MAINPAGE_LIKE)){
			Button titlebar_editbtn = (Button)findViewById(R.id.titlebar_editbtn);
			titlebar_editbtn.setVisibility(View.VISIBLE);
		}
		streampagelayout = (LinearLayout)findViewById(R.id.streampagelayout);
		mainMenu = (MainMenu)findViewById(R.id.mainmenu);
		rtitlebar = (RelativeLayout)findViewById(R.id.rtitlebar);
		titlebar_titletext = (TextView)findViewById(R.id.titlebar_titletext);
		//lyb
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		if(deleteDates != null){
			deleteDates.clear();
		}
//		if(UserUtil.userid == -1||UserUtil.userState != 1){
//			finish();
//			return;
//		}
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
		//lyb 关闭菜单
		if(isAnimationOpen){
			gotoMenu();
		}
	}

	public void init(){
		streampagelayout.removeAllViews();
		curIndex = 0;
		mPageLayout = new MLinearLayout(this,mHandler);
		mPageLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		mPageLayout.setOrientation(LinearLayout.VERTICAL);
		mPageLayout.setBackgroundResource(R.drawable.mainpagebg);
		streampagelayout.addView(mPageLayout);
		
		xmlViewLayout = new XmlViewLayout(this,mHandler,null);
		mConnectUtil = new ConnectUtil(this, downHandler,0);
		urlString = getIntent().getExtras().getString("url");
		isFromLikeLimitDialog = getIntent().getExtras().getBoolean("fromdialog", false);
		if(urlString != null){
			if(urlString.equals(URLUtil.URL_MAINPAGE_LIKE)){
				titlebar_titletext.setText("我的喜欢");
			}else{
				titlebar_titletext.setText("我看过的");
			}
			mConnectUtil.connect(addUrlParam(urlString, URLUtil.dpi(),0,UserUtil.userid), HttpThread.TYPE_PAGE,0);
		}
	}
	
	private void buildPage(Message msg,int threadindex,int type){
		Message message = new Message();
		Bundle bundle = new Bundle();
		switch (type) {
		case HttpThread.TYPE_PAGE:
			parserEngine = new Parser_ParserEngine(this);
			parserEngine.parser((byte[])msg.obj);
			xmlViewLayout.setData(parserEngine.getLayouts());
			xmlViewLayout.setParserPage(parserEngine.getPageObject());
			break;
		case HttpThread.TYPE_IMAGE:
			DownImageItem downImageItem = DownImageManager.get(threadindex);
			try {
				//保存图片
				CommonUtil.writeToFile(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(downImageItem.getUrl()), (byte[])msg.obj);
			} catch (Exception e) {
			}
			break;
		}
		
		message.what = MessageID.MESSAGE_RENDER;
		message.obj = msg.obj;
		message.arg1 = msg.arg1;
		bundle.putInt("mType", type);
		message.setData(bundle);
		mHandler.sendMessage(message);
	}
	
	private void renderPage(Message msg,int threadindex,int type){
		switch (type) {
		case HttpThread.TYPE_PAGE:
			LinearLayout tmplayout = xmlViewLayout.buildView(0);
			if(xmlViewLayout.isPartRenderList()){
				curIndex ++;
				xmlViewLayout.getStreamPage().insertList(xmlViewLayout.getParserStreamPage());
				DownImageManager.reset();
				mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
			}else{
				curIndex = 1;
				mPageLayout.removeAllViews();
				mPageLayout.addView(tmplayout);
				mPageLayout.postInvalidate();
			}
			if(isFromLikeLimitDialog){
				xmlViewLayout.getViewTitleBar().setState(View_TitleBar.STATE_DELETE);
			}
			break;
		case HttpThread.TYPE_IMAGE:
			DownImageItem downImageItem = DownImageManager.get(threadindex);
			try {
				if(downImageItem.getType() == Parser_Layout_AbsLayout.MODELTYPE_STREAMPAGE){
					View_StreamPageItem viewStreamPageItem = xmlViewLayout.getStreamPage().getStreamPageItem(threadindex);
					viewStreamPageItem.initImageSize(getResources().getDisplayMetrics().densityDpi);
					viewStreamPageItem.setImageView((byte[])msg.obj);
				}
			} catch (Exception e) {
			}
			break;
		}
	}
	
	private MHandlerThread mHandlerThread;
	private Handler downHandler;
	//用于下载接收的异步handler
	private void newHandler(){
		mHandlerThread = new MHandlerThread("StreamPageActivity");
		mHandlerThread.start();
		downHandler = new Handler(mHandlerThread.getLooper(), mHandlerThread){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case MessageID.MESSAGE_CONNECT_START:
					mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_START);
					break;
				case MessageID.MESSAGE_CONNECT_ERROR:
					CommonUtil.ShowToast(getApplicationContext(), (String)msg.obj);
					mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_ERROR);
					break;
				case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
					if("text/json".equals(msg.getData().getString("content_type"))){
						mHandler.sendEmptyMessage(MessageID.MESSAGE_CLOSEPROGRESS);
						Json((byte[])msg.obj);
					}else{
						mPageLayout.setIsDownLoadOver(true);
						buildPage(msg,msg.arg1,msg.getData().getInt("mType"));
						mHandler.sendEmptyMessage(9900);
					}
					break;
				}
			}
			
		};
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
				break;
			case MessageID.MESSAGE_CLOSEPROGRESS:
				closeProgress();
				break;
			case MessageID.MESSAGE_RENDER:
				renderPage(msg,msg.arg1,msg.getData().getInt("mType"));
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
							new ConnectUtil(StreampageActivity.this, downHandler,0).connect(urlString, HttpThread.TYPE_IMAGE, downImageItem.getIndex());
						}
					}
					//发起下一个询问
					mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_CONNECT_LAYOUTOVER, 100);
				}else{
					LogPrint.Print("downImageItem = null");
				}
				break;
			case MessageID.MESSAGE_CLICK_TITLEBAR_LEFTBUTTON:
				if(xmlViewLayout!=null){
					if(msg.getData().getInt("action") == Parser_Layout_AbsLayout.ACTION_EDIT){
						xmlViewLayout.getViewTitleBar().setState(View_TitleBar.STATE_DELETE);
					}else if(msg.getData().getInt("action") == Parser_Layout_AbsLayout.ACTION_DELETE){
						if(deleteDates != null&&deleteDates.size() > 0){
							mConnectUtil.connect(addUrlParam(URLUtil.URL_MYLIKE_DELETE, deleteDates),HttpThread.TYPE_PAGE,0);
						}
					}
				}
				break;
			case MessageID.MESSAGE_CLICK_TITLEBAR_RIGHTBUTTON:
				if(msg.getData().getInt("action") == Parser_Layout_AbsLayout.ACTION_BACK){
					if(xmlViewLayout!=null){
						if(deleteDates != null){
							deleteDates.clear();
						}
						xmlViewLayout.getViewTitleBar().setState(View_TitleBar.STATE_EDIT);
						for(int i = 0;i < xmlViewLayout.getStreamPage().size();i ++){
							if(xmlViewLayout.getStreamPage().getStreamPageItem(i).getMarkState()){
								xmlViewLayout.getStreamPage().getStreamPageItem(i).mark(false);
							}
						}
					}
				}else if(msg.getData().getInt("action") == Parser_Layout_AbsLayout.ACTION_MAINMENU){
					gotoMenu();
				}
				break;
			case MessageID.MESSAGE_CLICK_STREAMPAGE:
				if(msg.getData().getString("url") != null&&msg.getData().getString("url").length() > 0){
					if(xmlViewLayout!=null){
						if(xmlViewLayout.getViewTitleBar().getState() == View_TitleBar.STATE_DELETE){
							int index = msg.getData().getInt("chickPos");
							boolean useMark = xmlViewLayout.getStreamPage().getStreamPageItem(index).getMarkState();
							//删除项的添加移除逻辑
							if(deleteDates == null){
								deleteDates = new ArrayList<DeleteDate>();
							}
							DeleteDate deleteDate;
							if(useMark){
								for(int i = 0;i < deleteDates.size();i ++){
									if(deleteDates.get(i).index == index){
										deleteDates.remove(i);
										break;
									}
								}
							}else{
								deleteDate = new DeleteDate();
								deleteDate.index = index;
								deleteDate.id = xmlViewLayout.getStreamPage().getStreamPageItem(index).getId();
								LogPrint.Print("add: index = "+deleteDate.index+" | id = "+deleteDate.id);
								deleteDates.add(deleteDate);
							}
							xmlViewLayout.getStreamPage().getStreamPageItem(index).mark(!useMark);
						}else{
							if(CommonUtil.isNetWorkOpen(StreampageActivity.this)){
								ArrayList<String> urls = xmlViewLayout.getStreamPage().getUrls();
								if(urls!=null&&urls.size() > 0){
									MyPageActivityA.urls = null;
									MyPageActivityA.urls = new String[urls.size()];
									for(int i = 0;i < urls.size();i ++){
										MyPageActivityA.urls[i] = urls.get(i);
									}
									Intent intent = new Intent();
									intent.setClass(StreampageActivity.this, MyPageActivityA.class);
									intent.putExtra("url", msg.getData().getString("url"));
									intent.putExtra("chickPos", msg.getData().getInt("chickPos"));
									intent.putExtra("type", parserEngine.getPageObject().getPageId());
									startActivity(intent);
									finish();
								}
							}else{
								CommonUtil.ShowToast(StreampageActivity.this, "杯具了- -!\n联网不给力啊");
							}
						}
					}
				}
				break;
			case MessageID.MESSAGE_CLICK_STREAMPAGE_MORE:
				if(xmlViewLayout!=null){
					if(xmlViewLayout.getViewTitleBar().getState() != View_TitleBar.STATE_DELETE){
						String nexturl = msg.getData().getString("nexturl");
						if(nexturl!=null&&nexturl.length() > 0){
							mConnectUtil.connect(nexturl+"&pi="+curIndex+"&uid="+UserUtil.uid, HttpThread.TYPE_PAGE,0);
						}else{
							CommonUtil.ShowToast(StreampageActivity.this, "别翻了，到头了！");
						}
					}
				}
				break;
			case MessageID.MESSAGE_MAINMENU_OPEN:
				if(streampagelayout != null){
					if(streampagelayout.getRight() > getMainMenuAnimationPos(50)){
						streampagelayout.offsetLeftAndRight(animationIndex);
						streampagelayout.postInvalidate();
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_OPEN);
						animationIndex = -(CommonUtil.screen_width-getMainMenuAnimationPos(50))/5;
					}
				}
				break;
			case MessageID.MESSAGE_MAINMENU_CLOSE:
				if(streampagelayout != null){
					if(streampagelayout.getLeft() < 0){
						streampagelayout.offsetLeftAndRight(animationIndex);
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_CLOSE);
						streampagelayout.postInvalidate();
						animationIndex = (CommonUtil.screen_width-getMainMenuAnimationPos(50))/5;
					}
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
	
	public String addUrlParam(String url,String dpi,int pi,int oid){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		if(url.indexOf("?") > 0){
			return url+"&dpi="+dpi+"&pi="+pi+"&plaid="+URLUtil.plaid+"&uid="+UserUtil.uid;
		}
		return url+"?dpi="+dpi+"&pi="+pi+"&oid="+oid+"&plaid="+URLUtil.plaid+"&uid="+UserUtil.uid;
	}
	
	public String addUrlParam(String url,ArrayList<DeleteDate> deleteDates){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		StringBuffer sb = new StringBuffer();
		for(int i = 0;i < deleteDates.size();i++){
			sb.append(deleteDates.get(i).id+",");
		}
		String str = sb.toString().substring(0,sb.length()-1);//去掉最后一个,号
		sb.delete(0, sb.length());
		sb = null;
		JSONObject jObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			jObject.put("id", str);
			jsonArray.put(jObject);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url+"?id="+jsonArray.toString();
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (xmlViewLayout != null) {
			//lyb 页面还没加载出来时，menu键，异常；
			if (xmlViewLayout.getViewTitleBar() != null) {
				if (xmlViewLayout.getViewTitleBar().getState() == View_TitleBar.STATE_EDIT) {
					gotoMenu();
				}
			}
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
	    		streampagelayout.startAnimation(animation);
	    		OfflineLog.writeMainMenu();//写入离线日志
			}else{
				if(mainMenu != null){
					mainMenu.setIsCanClick(false);
				}
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.left_in);
				animation.setAnimationListener(this);
	    		animation.setFillEnabled(true);
	    		animation.setFillAfter(true);
	    		streampagelayout.startAnimation(animation);
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
					CommonUtil.ShowToast(StreampageActivity.this, "删除成功");
					if(deleteDates != null){
						for(int i = 0;i < deleteDates.size();i ++){//删除单品页缓存
							String tempurl = buildUrl(deleteDates.get(i).id);
							CommonUtil.deleteCacheFile(tempurl, this);
						}
						deleteDates.clear();
					}
					if(isFromLikeLimitDialog){
						finish();
					}else{
						if(urlString != null){
							Intent intent = new Intent();
							intent.setClass(StreampageActivity.this, StreampageActivity.class);
							intent.putExtra("url", urlString);
							startActivity(intent);
							finish();
						}
					}
				}else{
					CommonUtil.ShowToast(StreampageActivity.this, "删除失败");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	protected class DeleteDate{
		public int index;
		public int id;
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if(streampagelayout == null){
			return;
		}
		Button btMenu = (Button) streampagelayout.findViewById(R.id.titlebar_menubutton);
		if(isAnimationOpen){
			//左侧按钮之前有无的判断
			Button titlebar_backbutton = (Button) streampagelayout.findViewById(R.id.titlebar_backbutton);
			if(titlebar_backbutton.getVisibility() == View.VISIBLE){
				titlebar_backbutton.setVisibility(View.GONE);
				titlebar_backbutton.setTag("VISIBLE");
			}else{
				titlebar_backbutton.setVisibility(View.GONE);
				titlebar_backbutton.setTag("GONE");
			}//
			Button leftMenu = (Button)streampagelayout.findViewById(R.id.titlebar_leftmenu);
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
			if(streampagelayout == null){
				return;
			}
			Button leftMenu = (Button)streampagelayout.findViewById(R.id.titlebar_leftmenu);
			leftMenu.setVisibility(View.GONE);
			//左侧按钮之前有无的判断
			Button titlebar_backbutton = (Button) streampagelayout.findViewById(R.id.titlebar_backbutton);
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
	
	//拼接单品页地址
	private String buildUrl(int cid){
		String url = "";
		url = URLUtil.URL_MYPAGE+"?cid="+cid+"&dpi="+URLUtil.dpi()+"&pi=0&plaid="+URLUtil.plaid;
		//拼接deviceid
		if(url.indexOf("?") >= 0){
			if(url.indexOf("deviceid=") < 0){
				url += "&deviceid="+CommonUtil.getIMEI(this);
			}
		}else{
			if(url.indexOf("deviceid=") < 0){
				url += "?deviceid="+CommonUtil.getIMEI(this);
			}
		}
		//拼接oid参数
		if(url.indexOf("?") >= 0){
			if(url.indexOf("oid=") < 0){
				url += "&oid="+UserUtil.userid;
			}
		}else{
			if(url.indexOf("oid=") < 0){
				url += "?oid="+UserUtil.userid;
			}
		}
		//拼接vid参数
		if(url.indexOf("?") >= 0){
			if(url.indexOf("vid=") < 0){
				url += "&vid="+UserUtil.vid;
			}
		}else{
			if(url.indexOf("vid=") < 0){
				url += "?vid="+UserUtil.vid;
			}
		}
		//拼接ver参数
		if(url.indexOf("?") >= 0){
			if(url.indexOf("ver=") < 0){
				url += "&ver="+URLUtil.version;
			}
		}else{
			if(url.indexOf("ver=") < 0){
				url += "?ver="+URLUtil.version;
			}
		}
		//拼接network_type参数
		if(url.indexOf("?") >= 0){
			if(url.indexOf("network_type=") < 0){
				url += "&network_type="+CommonUtil.getApnType(this);
			}
		}else{
			if(url.indexOf("network_type=") < 0){
				url += "?network_type="+CommonUtil.getApnType(this);
			}
		}
		return url;
	}
}
