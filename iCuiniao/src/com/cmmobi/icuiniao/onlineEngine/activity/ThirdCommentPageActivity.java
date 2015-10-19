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
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.AbsCuiniaoMenu;
import com.cmmobi.icuiniao.Activity.Activity;
import com.cmmobi.icuiniao.Activity.LoginAndRegeditActivity;
import com.cmmobi.icuiniao.menuclick.ThirdComment_MenuClick;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ParserEngine;
import com.cmmobi.icuiniao.onlineEngine.view.MLinearLayout;
import com.cmmobi.icuiniao.onlineEngine.view.View_CommentU_Item;
import com.cmmobi.icuiniao.onlineEngine.view.XmlViewLayout;
import com.cmmobi.icuiniao.ui.view.MainMenu;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.DownImageManager;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.PageID;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

/**
 * @author hw
 *
 */
public class ThirdCommentPageActivity extends Activity{

	private ConnectUtil mConnectUtil;
	private XmlViewLayout xmlViewLayout;
	private MLinearLayout mPageLayout;
	private Parser_ParserEngine parserEngine;
	private String urlString;
	public static boolean isGotoClickMenu;//用于返回时,不进行页面刷新
	private int pageid;
	private boolean isAnimationOpen;//主菜单动画状态
	private int animationIndex;
	private MainMenu mainMenu;
	private LinearLayout thirdcommentpage;
	
	private int curIndex;
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isGotoClickMenu = true;
		isAnimationOpen = false;
		setContentView(R.layout.thirdcommentpage);
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
		thirdcommentpage = (LinearLayout)findViewById(R.id.thirdcommentpage);
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
		if(!isGotoClickMenu){
			init();
		}else{
			isGotoClickMenu = false;
		}
	}

	public void init(){
		curIndex = 0;
		isGotoClickMenu = false;
		thirdcommentpage.removeAllViews();
		mPageLayout = new MLinearLayout(this,mHandler);
		mPageLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		mPageLayout.setOrientation(LinearLayout.VERTICAL);
		mPageLayout.setBackgroundColor(0xffffffff);
		thirdcommentpage.addView(mPageLayout);
		
		xmlViewLayout = new XmlViewLayout(this,mHandler,null);
		mConnectUtil = new ConnectUtil(this, mHandler,0);
		urlString = getIntent().getExtras().getString("url");
		if(urlString != null){
			mConnectUtil.connect(urlString, HttpThread.TYPE_PAGE,0);
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
				xmlViewLayout.getCommentU().insertList(xmlViewLayout.getParserList());
				DownImageManager.reset();
				mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
			}else{
				curIndex = 1;
				pageid = parserEngine.getPageObject().getPageId();
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
				if(downImageItem.getType() == Parser_Layout_AbsLayout.MODELTYPE_LISTITEM){
					View_CommentU_Item viewCommentUItem = xmlViewLayout.getCommentU().getListItem(threadindex);
					viewCommentUItem.setImageView((byte[])msg.obj);
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
							new ConnectUtil(ThirdCommentPageActivity.this, mHandler,0).connect(urlString, HttpThread.TYPE_IMAGE, downImageItem.getIndex());
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
				isGotoClickMenu = true;
				gotoMenu();
				break;
			case MessageID.MESSAGE_CLICK_LISTITEM_COMMENTU:
				String url = msg.getData().getString("url");
				boolean isMoreItem = msg.getData().getBoolean("isMoreItem");
//				int curIndex = msg.getData().getInt("curIndex");
				int totalpage = msg.getData().getInt("totalpage");
				int subjectid = msg.getData().getInt("subjectid");
				int commentid = msg.getData().getInt("commentid");
				if(isMoreItem){//查看更多
					if(curIndex < totalpage){
						if(URLUtil.IsLocalUrl()){
							if(curIndex == 1){
								new ConnectUtil(ThirdCommentPageActivity.this, mHandler,0).connect("/sdcard/iCuiniao/cache/thirdcomment_1.xml", HttpThread.TYPE_PAGE, 0);
							}else if(curIndex == 2){
								new ConnectUtil(ThirdCommentPageActivity.this, mHandler,0).connect("/sdcard/iCuiniao/cache/thirdcomment_2.xml", HttpThread.TYPE_PAGE, 0);
							}
						}else{
							if(pageid == PageID.PAGEID_COMMENT_USER_FORM){
								new ConnectUtil(ThirdCommentPageActivity.this, mHandler,0).connect(addUrlParam(URLUtil.URL_FORM_THIRDCOMMENT_MORE_LISTITEM, UserUtil.userid, subjectid,curIndex), HttpThread.TYPE_PAGE, 0);
							}else{
								new ConnectUtil(ThirdCommentPageActivity.this, mHandler,0).connect(addUrlParam(URLUtil.URL_THIRDCOMMENT_MORE_LISTITEM, UserUtil.userid, subjectid,curIndex), HttpThread.TYPE_PAGE, 0);
							}
						}
					}
				}else{//普通列表项
					isGotoClickMenu = true;
					ThirdComment_MenuClick menuClick = new ThirdComment_MenuClick(msg.getData().getInt("userid"),msg.getData().getInt("subjectid"),msg.getData().getInt("commentid"),mHandler);
					Intent intent3 = new Intent();
					intent3.setClass(ThirdCommentPageActivity.this, AbsCuiniaoMenu.class);
					intent3.putExtra("title", PageID.MYPAGE_MENU_TITLE);
					intent3.putExtra("items", PageID.MYPAGE_MENU_ITEM);
					startActivity(intent3);
					AbsCuiniaoMenu.set(menuClick);
				}
				break;
			case MessageID.MESSAGE_MENUCLICK_MYPAGE_COMMENTC_USERPAGE:
				if(msg.getData().getInt("userid") != -1){
					isGotoClickMenu = false;
					Intent intent4 = new Intent();
					intent4.putExtra("url", addUrlParam1(URLUtil.URL_USERPAGE, UserUtil.userid, msg.getData().getInt("userid")));
					intent4.setClass(ThirdCommentPageActivity.this, UserPageActivityA.class);
					startActivity(intent4);
				}
				break;
			case MessageID.MESSAGE_MENUCLICK_MYPAGE_COMMENTC_COMMENT:
				if(UserUtil.userid != -1&&UserUtil.userState == 1){
					buildCommentDialog(msg.getData().getInt("subjectid"),msg.getData().getInt("commentid"));
				}else{
					CommonUtil.ShowToast(ThirdCommentPageActivity.this, "小C的主人才能评论哦，登录或者注册成为小C的主人吧。");
					Intent intent11 = new Intent();
					intent11.setClass(ThirdCommentPageActivity.this, LoginAndRegeditActivity.class);
					startActivity(intent11);
				}
				break;
			case MessageID.MESSAGE_FLUSH_PAGE:
				Intent intent3 = new Intent();
				intent3.setClass(ThirdCommentPageActivity.this, ThirdCommentPageActivity.class);
				intent3.putExtra("url", urlString);
				startActivity(intent3);
				finish();
				break;
			case MessageID.MESSAGE_MAINMENU_OPEN:
				if(thirdcommentpage != null){
					if(thirdcommentpage.getRight() > getMainMenuAnimationPos(50)){
						thirdcommentpage.offsetLeftAndRight(animationIndex);
						thirdcommentpage.postInvalidate();
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_OPEN);
						animationIndex = -(CommonUtil.screen_width-getMainMenuAnimationPos(50))/5;
					}
				}
				break;
			case MessageID.MESSAGE_MAINMENU_CLOSE:
				if(thirdcommentpage != null){
					if(thirdcommentpage.getLeft() < 0){
						thirdcommentpage.offsetLeftAndRight(animationIndex);
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_CLOSE);
						thirdcommentpage.postInvalidate();
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
	
	private String msgString;
	private int input_cur;
	//评论对话框 
	public void buildCommentDialog(final int subjectid,final int commentid){
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
					CommonUtil.ShowToast(ThirdCommentPageActivity.this, "主人，您写那么多字做咩？");
				}
				input_cur = 100-et.getText().length();
				input_num.setText("您还可输入"+input_cur+"字");
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(ThirdCommentPageActivity.this);
		builder.setTitle("评论").setView(l).setPositiveButton("发布", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(et.getText() == null){
					msgString = "";
				}else{
					msgString = et.getText().toString();
				}
				mConnectUtil = new ConnectUtil(ThirdCommentPageActivity.this, mHandler,0);
				mConnectUtil.connect(addUrlParam(pageid==PageID.PAGEID_COMMENT_USER_FORM?URLUtil.URL_FORM_THIRDCOMMENT:URLUtil.URL_COMMENT_THIRDCOMMENT, UserUtil.userid, subjectid, commentid, msgString), HttpThread.TYPE_PAGE, 0);
			}
		}).setNegativeButton("取消", null).show();
	}
	
	private void Json(byte[] data){
		try{
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			if(result != null){
				if(result.equalsIgnoreCase("true")){
					CommonUtil.ShowToast(ThirdCommentPageActivity.this, "发布成功");
					mHandler.sendEmptyMessage(MessageID.MESSAGE_FLUSH_PAGE);
				}else{
					String msg = jObject.getString("msg");
					if(msg != null){
						CommonUtil.ShowToast(ThirdCommentPageActivity.this, "发布失败!\n"+msg);
					}else{
						CommonUtil.ShowToast(ThirdCommentPageActivity.this, "发布失败!");
					}
				}
			}
		}catch(Exception e){
			
		}
	}
	
	public String addUrlParam(String url,int oid,int subjectid,int commentid,String msg){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		if(msg == null)msg="";
		return url+"?oid="+oid+"&subjectid="+subjectid+"&commentid="+commentid+"&msg="+CommonUtil.toUrlEncode(msg);
	}
	
	public String addUrlParam(String url,int oid,int subjectid,int pi){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"?oid="+oid+"&subjectid="+subjectid+"&pi="+pi;
	}
	
	public String addUrlParam1(String url,int oid,int uid){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"?oid="+oid+"&uid="+uid;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		isGotoClickMenu = true;
		gotoMenu();
		return true;
	}
	
	private void gotoMenu(){
		if(mPageLayout != null){
//			Intent intent1 = new Intent();
//			intent1.setClass(ThirdCommentPageActivity.this, MainMenuActivity.class);
//			mPageLayout.setDrawingCacheEnabled(true);
//			intent1.putExtra("bitmapImage", CommonUtil.createScreenCache(mPageLayout.getDrawingCache(), XmlViewLayout.dip2px(this, 50)));
//			startActivity(intent1);
			animationIndex = 0;
			isAnimationOpen = !isAnimationOpen;
			if(isAnimationOpen){
				if(mainMenu != null){
					mainMenu.setIsCanClick(true);
				}
				mHandler.removeMessages(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
				mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_OPEN);
				OfflineLog.writeMainMenu();//写入离线日志
			}else{
				if(mainMenu != null){
					mainMenu.setIsCanClick(false);
				}
				mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_CLOSE);
				mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
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
}
