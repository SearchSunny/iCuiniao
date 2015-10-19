/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Commodity;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_CommodityInfo;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Coquetry;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Image;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_InputLine;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Like;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_List;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_MainPage;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Page;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_StreamPage;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_TabButton;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_TitleBar;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_TitleBarFloat;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_UserBar;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_UserInfo;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.PageID;

/**
 * @author hw
 *
 *网络页面布局,view共三层
 */
public class XmlViewLayout extends View_AbsPage{

	private Parser_Layout_AbsLayout[] mAbsLayouts;
	private Parser_Page mPage;
	
	private FrameLayout view_pageLayout;//全部页面信息
	private LinearLayout view_floatLayout;//最上面的悬浮层,用于绘制titlebarfloat等不占排版的布局
	private LinearLayout view_iconLayout;//中间层,用于绘制主页中的商家图标
	private LinearLayout view_xmlLayout;//最下层,绘制页面
	
	private View_TitleBar viewTitleBar;
	private View_TitleBarFloat viewTitleBarFloat;
	private View_MainPage viewMainPage;
	private View_Image viewImage;
	private View_CommodityInfo viewCommodityInfo;
	private View_CommodityIcon viewCommodityIcon;
	private View_NoScrollList viewNoScrollList;
	private View_Commodity viewCommodity;
	private View_LikeDialog viewLikeDialog;
	private View_UserInfo viewUserInfo;
	private View_UserBar viewUserBar;
	private View_CommentU viewCommentU;
	private View_Tabbutton viewTabbutton;
	private View_FriendList viewFriendList;
	private View_Inputline viewInputline;
	private View_AddFriendsList viewAddFriendsList;
	private View_Coquetry viewCoquetry;
	private View_StreamPage viewStreamPage;
	
	private Parser_List parserList;//保存分页列表的数据
	private Parser_Coquetry parserCoquetry;//保存我的撒娇列表数据
	private Parser_StreamPage parserStreamPage;//保存瀑布流列表数据
	private boolean isPartRenderList;
	
	private Handler handler;
	private GestureDetector detector;
	
	private String titleString;
	
	private int tabbuttonIndex;
	
	private int flingX;
	private int flingY;
	
	private boolean isAddFlushBtn;//是否添加刷新评论按钮
	private int lastScrollY;//最后滚动条的位置
	private boolean hide;
	
	public XmlViewLayout(Context context, Handler handler, GestureDetector detector) {
		super(context);
		// TODO Auto-generated constructor stub
		this.handler = handler;
		this.detector = detector;
		hide = true;
	}
	
	public XmlViewLayout(Context context, Handler handler, GestureDetector detector,int tabbuttonIndex) {
		this(context, handler, detector);
		this.tabbuttonIndex = tabbuttonIndex;
		hide = true;
	}
	
	public void setData(Parser_Layout_AbsLayout[] absLayout){
		mAbsLayouts = absLayout;
	}
	
	public void setParserPage(Parser_Page parserPage){
		mPage = parserPage;
	}
	
	public Parser_Page getParserPage(){
		return mPage;
	}
	
	public String getTitleName(){
		return titleString;
	}
	
	public int getLastScrollY(){
		return lastScrollY;
	}
	
	public void setHideState(boolean hide){
		this.hide = hide;
	}
	
	public ScrollView scrollView = null;
	/**创建页面*/
	public LinearLayout buildView(int tag){
		isAddFlushBtn = false;
		isPartRenderList = false;
		boolean canScroll = false;
		boolean hasIcon = false;
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		setOrientation(LinearLayout.VERTICAL);
		view_pageLayout = new FrameLayout(getContext());
		view_floatLayout = (LinearLayout)((Activity)getContext()).getLayoutInflater().inflate(R.layout.floatpage, null);
		LinearLayout float_top = (LinearLayout)view_floatLayout.findViewById(R.id.float_top);
		LinearLayout float_bottom = (LinearLayout)view_floatLayout.findViewById(R.id.float_bottom);
		view_iconLayout = new LinearLayout(getContext());
		view_xmlLayout = new LinearLayout(getContext());
		view_xmlLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		view_xmlLayout.setOrientation(LinearLayout.VERTICAL);
		int titleHeight = 66;
		
		if(mAbsLayouts != null&&mAbsLayouts.length > 0){
			for(int i = 0;i < mAbsLayouts.length; i ++){
				
				switch (mAbsLayouts[i].getModelType()) {
				case Parser_Layout_AbsLayout.MODELTYPE_TITLEBAR:
					titleString = ((Parser_TitleBar)mAbsLayouts[i]).getStr();
					viewTitleBar = new View_TitleBar(getContext(), (Parser_TitleBar)mAbsLayouts[i],handler,mPage.getEmpty());
					if(mPage.getPageId() != PageID.PAGEID_MAINPAGE_ALL&&mPage.getPageId() != PageID.PAGEID_OWN_MAINPAGE){
						view_xmlLayout.addView(viewTitleBar);
					}
					break;
				case Parser_Layout_AbsLayout.MODELTYPE_TITLEBARFLOAT:
					int dpi = getResources().getDisplayMetrics().densityDpi;
					if(dpi <= 120){//qvga
						titleHeight=33;
					}else if(dpi <= 160){//hvga
						titleHeight=44;
					}else if(dpi <= 240){//wvga
						titleHeight = 66;
						if(CommonUtil.screen_width > 700){
							titleHeight=85;
						}
					}else{//更大屏幕分辨率
						titleHeight=85;
					}
					if(mPage.getPageId() == PageID.PAGEID_OWN_MAINPAGE){
						LinearLayout view_titleback = new LinearLayout(getContext());
						view_titleback.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
						view_titleback.setBackgroundResource(R.drawable.titlebarfloatbg1);
						view_xmlLayout.addView(view_titleback);
					}else{
						view_xmlLayout.setPadding(0, CommonUtil.dip2px(getContext(), CommonUtil.px2dip(getContext(), titleHeight)), 0, 0);
					}
					titleString = ((Parser_TitleBarFloat)mAbsLayouts[i]).getStr();
					viewTitleBarFloat = new View_TitleBarFloat(getContext(), (Parser_TitleBarFloat)mAbsLayouts[i],handler);
					if(mPage.getPageId() != PageID.PAGEID_MAINPAGE_ALL&&mPage.getPageId() != PageID.PAGEID_OWN_MAINPAGE){
						float_top.addView(viewTitleBarFloat);
					}
					break;
				case Parser_Layout_AbsLayout.MODELTYPE_MAINPAGE:
					viewMainPage = new View_MainPage(getContext(), (Parser_MainPage)mAbsLayouts[i],handler,detector,tag);
					view_xmlLayout.addView(viewMainPage);
					break;
				case Parser_Layout_AbsLayout.MODELTYPE_IMAGE:
					viewImage = new View_Image(getContext(), (Parser_Image)mAbsLayouts[i], handler,tag);
					view_xmlLayout.addView(viewImage);
					break;
				case Parser_Layout_AbsLayout.MODELTYPE_TEXT:
					break;
				case Parser_Layout_AbsLayout.MODELTYPE_BUTTON:
					break;
				case Parser_Layout_AbsLayout.MODELTYPE_INPUTLINE:
					viewInputline = new View_Inputline(getContext(), (Parser_InputLine)mAbsLayouts[i], handler);
					view_xmlLayout.addView(viewInputline);
					break;
				case Parser_Layout_AbsLayout.MODELTYPE_USERINFO:
					viewUserInfo = new View_UserInfo(getContext(), (Parser_UserInfo)mAbsLayouts[i], handler, mPage.getUserId());
					view_xmlLayout.addView(viewUserInfo);
					canScroll = true;
					break;
				case Parser_Layout_AbsLayout.MODELTYPE_TABBUTTON:
					viewTabbutton = new View_Tabbutton(getContext(), (Parser_TabButton)mAbsLayouts[i], handler);
					viewTabbutton.setGravity(Gravity.CENTER_HORIZONTAL);
					viewTabbutton.init(tabbuttonIndex);
					view_xmlLayout.addView(viewTabbutton);
					break;
				case Parser_Layout_AbsLayout.MODELTYPE_USERBAR:
					viewUserBar = new View_UserBar(getContext(), (Parser_UserBar)mAbsLayouts[i], handler, mPage.getUserId());
					float_bottom.addView(viewUserBar);
					canScroll = true;
					break;
				case Parser_Layout_AbsLayout.MODELTYPE_LIST:
					if(mPage.getPartRender()){
						parserList = (Parser_List)mAbsLayouts[i];
						isPartRenderList = true;
						if(((Parser_List)mAbsLayouts[i]).getType() == Parser_Layout_AbsLayout.TYPE_ITEM_COMMENT_C){
							isAddFlushBtn = true;
						}
					}else{
						switch (((Parser_List)mAbsLayouts[i]).getType()) {
						case Parser_Layout_AbsLayout.TYPE_ITEM_COMMENT_C:
							viewNoScrollList = new View_NoScrollList(getContext(), (Parser_List)mAbsLayouts[i], handler,mPage.getTotalPage(),tag);
							view_xmlLayout.addView(viewNoScrollList);
//							isAddFlushBtn = viewNoScrollList.IsAddFlushBtn();
							isAddFlushBtn = true;
							if(viewCommodityInfo != null){
								viewCommodityInfo.changeBackGround();
							}
							break;
						case Parser_Layout_AbsLayout.TYPE_ITEM_COMMENT_U:
							viewCommentU = new View_CommentU(getContext(), (Parser_List)mAbsLayouts[i], handler,mPage.getTotalPage());
							view_xmlLayout.addView(viewCommentU);
							break;
						case Parser_Layout_AbsLayout.TYPE_ITEM_ONLYTEXT:
							viewFriendList = new View_FriendList(getContext(), (Parser_List)mAbsLayouts[i], handler, mPage.getTotalPage());
							view_xmlLayout.addView(viewFriendList);
							break;
						case Parser_Layout_AbsLayout.TYPE_ITEM_FRIENDS:
							viewAddFriendsList = new View_AddFriendsList(getContext(), (Parser_List)mAbsLayouts[i], handler, mPage.getTotalPage());
							view_xmlLayout.addView(viewAddFriendsList);
							break;
						}
					}
					break;
				case Parser_Layout_AbsLayout.MODELTYPE_LIKE:
					if(viewLikeDialog == null){
						viewLikeDialog = new View_LikeDialog(getContext(),R.style.FullScreenDialog,this);
					}
					viewLikeDialog.add(((Parser_Like)mAbsLayouts[i]).getLikeItem());
					break;
				case Parser_Layout_AbsLayout.MODELTYPE_COMMODITY:
					viewCommodity = new View_Commodity(getContext(), (Parser_Commodity)mAbsLayouts[i], handler);
					viewCommodity.initImageSize(getResources().getDisplayMetrics().densityDpi);
					view_xmlLayout.addView(viewCommodity);
					break;
				case Parser_Layout_AbsLayout.MODELTYPE_COMMODITYINFO:
					isAddFlushBtn = true;
					canScroll = true;
					hasIcon = true;
					viewCommodityInfo = new View_CommodityInfo(getContext(), (Parser_CommodityInfo)mAbsLayouts[i], handler);
					viewCommodityIcon = new View_CommodityIcon(getContext(), viewCommodityInfo.getParserImage(), handler,tag);
					viewCommodityIcon.initImageSize(getResources().getDisplayMetrics().densityDpi);
					view_xmlLayout.addView(viewCommodityInfo);
					break;
				case Parser_Layout_AbsLayout.MODELTYPE_COMMODITYBAR:
					
					break;
				case Parser_Layout_AbsLayout.MODELTYPE_COQUETRY:
					if(mPage.getPartRender()){
						parserCoquetry = (Parser_Coquetry)mAbsLayouts[i];
						isPartRenderList = true;
					}else{
						viewCoquetry = new View_Coquetry(getContext(), (Parser_Coquetry)mAbsLayouts[i], handler, mPage.getTotalPage(),hide);
						view_xmlLayout.addView(viewCoquetry);
					}
					break;
				case Parser_Layout_AbsLayout.MODELTYPE_STREAMPAGE:
					if(mPage.getPartRender()){
						parserStreamPage = (Parser_StreamPage)mAbsLayouts[i];
						isPartRenderList = true;
					}else{
						viewStreamPage = new View_StreamPage(getContext(), (Parser_StreamPage)mAbsLayouts[i], handler);
						view_xmlLayout.addView(viewStreamPage);
					}
					break;
				}
				
			}
		}
		
		if(isAddFlushBtn){//添加刷新评论的按钮
			int dpi = getResources().getDisplayMetrics().densityDpi;
			LinearLayout laddflush = new LinearLayout(getContext());
			laddflush.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			laddflush.setGravity(Gravity.CENTER);
			TextView textView = new TextView(getContext());
			textView.setTextColor(0xffacacac);
			
			if(dpi <= 120){//qvga 240x320
				laddflush.setPadding(0, 10, 0, 10);
				textView.setTextSize(dip2px(getContext(), 14));
				
			}else{
				laddflush.setPadding(0, 20, 0, 20);
				textView.setTextSize(dip2px(getContext(), 10));
			}
			textView.setText("点击刷新评论");
			laddflush.addView(textView);
			view_xmlLayout.addView(laddflush);
			laddflush.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					handler.sendEmptyMessage(MessageID.MESSAGE_FLUSH_PAGE);

				}
			});
//			// lyb 临时评论列表入口。
//			laddflush.setOnLongClickListener(new OnLongClickListener() {
//				
//				@Override
//				public boolean onLongClick(View v) {
//					handler.sendEmptyMessage(MessageID.MESSAGE_COMMENT_LIST);
//					return false;
//				}
//			});
		}
		
		if(hasIcon){
			LinearLayout layout = new LinearLayout(getContext());
			layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,dip2px(getContext(), 60)));
			view_xmlLayout.addView(layout);
		}
		
		scrollView = null;
		if(canScroll){
			scrollView = new ScrollView(getContext()){

				@Override
				protected void onScrollChanged(int l, int t, int oldl, int oldt) {
					// TODO Auto-generated method stub
					super.onScrollChanged(l, t, oldl, oldt);
					lastScrollY = getScrollY();
				}
				
			};
			scrollView.setVerticalScrollBarEnabled(false);
			scrollView.setHorizontalFadingEdgeEnabled(false);
			scrollView.setVerticalFadingEdgeEnabled(false);
			if(hasIcon){
				FrameLayout frameLayout = new FrameLayout(getContext());
				frameLayout.addView(view_xmlLayout);
				//如果页面中包含commodityInfo的话,需要将卖家图标绘制上去
				AbsoluteLayout absoluteLayout = new AbsoluteLayout(getContext());
				absoluteLayout.addView(viewCommodityIcon, new AbsoluteLayout.LayoutParams(viewCommodityIcon.getCommodityInfoIcon_W(), viewCommodityIcon.getCommodityInfoIcon_H(), dip2px(getContext(),20), CommonUtil.dip2px(getContext(), CommonUtil.px2dip(getContext(), titleHeight))+CommonUtil.screen_width-viewCommodityIcon.getCommodityInfoIcon_H()/2));
				view_iconLayout.addView(absoluteLayout);
				frameLayout.addView(view_iconLayout);
				scrollView.addView(frameLayout);
			}else{
				scrollView.addView(view_xmlLayout);
			}
			
			view_pageLayout.addView(scrollView);
			view_pageLayout.addView(view_floatLayout);
			
			scrollView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						flingX = (int)event.getRawX();
						flingY = (int)event.getRawY();
						break;
					case MotionEvent.ACTION_MOVE:
						
						break;
					case MotionEvent.ACTION_UP:
						int tmp = (int)event.getRawX();
						int tmpy = (int)event.getRawY();
						if(Math.abs(tmpy-flingY) <= 100){
							if(flingX - tmp > 100){
								handler.sendEmptyMessage(MessageID.MESSAGE_FLING_NEXT);
							}else if(tmp - flingX > 100){
								handler.sendEmptyMessage(MessageID.MESSAGE_FLING_PRE);
							}
						}
						break;
					}
					return onTouchEvent(event);
				}
			});
		}else{
			view_pageLayout.addView(view_xmlLayout);
			view_pageLayout.addView(view_iconLayout);
			view_pageLayout.addView(view_floatLayout);
		}
		addView(view_pageLayout);
		
		return this;
	}
	
	/** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }
    
    /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }
	
	public View_TitleBar getViewTitleBar(){return viewTitleBar;}	
	public View_TitleBarFloat getTitleBarFloat(){return viewTitleBarFloat;}
	public View_MainPage getViewMainPage(){return viewMainPage;}
	public View_Image getViewImage(){return viewImage;}
	public View_CommodityInfo getCommodityInfo(){return viewCommodityInfo;}
	public View_CommodityIcon getCommodityIcon(){return viewCommodityIcon;}
	public View_NoScrollList getNoScrollList(){return viewNoScrollList;}
	public View_Commodity getCommodity(){return viewCommodity;}
	public View_LikeDialog getViewLikeDialog(){return viewLikeDialog;}
	public View_UserInfo getViewUserInfo(){return viewUserInfo;}
	public View_UserBar getViewUserBar(){return viewUserBar;}
	public View_CommentU getCommentU(){return viewCommentU;}
	public View_Tabbutton getTabbutton(){return viewTabbutton;}
	public View_FriendList getFriendList(){return viewFriendList;}
	public View_Inputline getInputline(){return viewInputline;}
	public View_AddFriendsList getAddFriendsList(){return viewAddFriendsList;}
	public View_Coquetry getCoquetry(){return viewCoquetry;}
	public View_StreamPage getStreamPage(){return viewStreamPage;}
	
	
	public void clearViewLikeDialog(){viewLikeDialog = null;}
	public Parser_List getParserList(){return parserList;}
	public boolean isPartRenderList(){return isPartRenderList;}
	public Parser_Coquetry getParserCoquetry(){return parserCoquetry;}
	public Parser_StreamPage getParserStreamPage(){return parserStreamPage;}
	
	//=====================以下为实践的响应==========================
	/**titlebar/titlebarfloat的左按钮的响应*/
	public void onTitleBarLeftButtonClick(String url, int action){
		LogPrint.Print("=======onTitleBarLeftButtonClick=======");
		LogPrint.Print("url = "+url);
		LogPrint.Print("action = "+action);
		LogPrint.Print("=============over======================");
		
		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		bundle.putInt("action", action);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_TITLEBAR_LEFTBUTTON;
		handler.sendMessage(msg);
		
	}
	/**titlebar/titlebarfloat的中间按钮的响应*/
	public void onTitleBarMiddleButtonClick(String url, int action){
		LogPrint.Print("=======onTitleBarMiddleButtonClick=======");
		LogPrint.Print("url = "+url);
		LogPrint.Print("action = "+action);
		LogPrint.Print("=============over======================");

		if(url != null&&url.length() > 0){
			Bundle bundle = new Bundle();
			bundle.putString("url", url);
			bundle.putInt("action", action);
			Message msg = new Message();
			msg.setData(bundle);
			msg.what = MessageID.MESSAGE_CLICK_TITLEBAR_MIDDLEBUTTON;
			handler.sendMessage(msg);
		}
		
	}
	/**titlebar/titlebarfloat的右侧按钮的响应*/
	public void onTitleBarRightButtonClick(String url, int action){
		LogPrint.Print("=======onTitleBarRightButtonClick=======");
		LogPrint.Print("url = "+url);
		LogPrint.Print("action = "+action);
		LogPrint.Print("=============over======================");

		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		bundle.putInt("action", action);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_TITLEBAR_RIGHTBUTTON;
		handler.sendMessage(msg);
	}
	/**titlebar/titlebarfloat的中间菜单的响应*/
	public void onTitleBarMenuClick(int pageid){
		LogPrint.Print("=======onTitleBarMenuClick=======");
		LogPrint.Print("pageid = "+pageid);
		LogPrint.Print("=============over======================");

		Bundle bundle = new Bundle();
		bundle.putInt("pageid", pageid);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_TITLEBAR_MENU;
		handler.sendMessage(msg);
	}
	
	/**mainpage的图片响应*/
	public void onMainPageItemClick(String url, int type,int chickPos){
		LogPrint.Print("=======onMainPageItemClick=======");
		LogPrint.Print("url = "+url);
		LogPrint.Print("type = "+type);
		LogPrint.Print("chickPos = "+chickPos);
		LogPrint.Print("=============over======================");

		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		bundle.putInt("type", type);
		bundle.putInt("chickPos", chickPos);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_MAINPAGE_ITEM;
		handler.sendMessage(msg);
	}
	
	/**单品页中晒单按钮的响应*/
	public void onCommodityInfoFormButtonClick(String url,int action){
		LogPrint.Print("=======onCommodityInfoFormButtonClick=======");
		LogPrint.Print("url = "+url);
		LogPrint.Print("action = "+action);
		LogPrint.Print("=============over======================");
		
	}
	
	/**单品页中喜欢按钮的响应*/
	public void onCommodityInfoLikeButtonClick(String url, int action){
		LogPrint.Print("=======onCommodityInfoLikeButtonClick=======");
		LogPrint.Print("url = "+url);
		LogPrint.Print("action = "+action);
		LogPrint.Print("=============over======================");
		
		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		bundle.putInt("action", action);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_COMMODITYINFO_LIKEBUTTON;
		handler.sendMessage(msg);
	}
	
	/**单品页中商品简介区域的响应*/
	public void onCommodityInfoBoxClick(){
		LogPrint.Print("=======onCommodityInfoBoxClick=======");
		LogPrint.Print("还需要在activity中获得commodityid");
		LogPrint.Print("=============over======================");
		
		Message msg = new Message();
		msg.what = MessageID.MESSAGE_CLICK_COMMODITYINFO_BOX;
		handler.sendMessage(msg);
	}
	
	/**单品页中卖家图标的响应*/
	public void onCommodityInfoIconClick(String url){
		LogPrint.Print("=======onCommodityInfoIconClick=======");
		LogPrint.Print("url = "+url);
		LogPrint.Print("还需要在activity中获得commodityid");
		LogPrint.Print("=============over======================");
		
		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_COMMODITYINFO_ICON;
		handler.sendMessage(msg);
	}
	
	/**单品页中评论列表的响应*/
	public void onListItemCommentC_Click(String url, int userid,int action,int subjectid){
		LogPrint.Print("=======onListItemCommentC_Click=======");
		LogPrint.Print("url = "+url);
		LogPrint.Print("userid = "+userid);
		LogPrint.Print("action = "+action);
		LogPrint.Print("subjectid = "+subjectid);
		LogPrint.Print("=============over======================");
		
		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		bundle.putInt("userid", userid);
		bundle.putInt("action", action);
		bundle.putInt("subjectid", subjectid);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_LISTITEM_COMMENTC;
		handler.sendMessage(msg);
	}
	
	/**喜欢窗口中图标的响应*/
	public void onPopLikeGridItemClick(String url){
		LogPrint.Print("=======onPopLikeGridItemClick=======");
		LogPrint.Print("url = "+url);
		LogPrint.Print("=============over======================");
		
		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_POPLIKE_GRIDITEM;
		handler.sendMessage(msg);
	}
	
	/**用户主页中喜欢按钮的响应*/
	public void onUserInfoLikeButtonClick(String url, int userid, int action){
		LogPrint.Print("=======onUserInfoLikeButtonClick=======");
		LogPrint.Print("url = "+url);
		LogPrint.Print("userid = "+userid);
		LogPrint.Print("action = "+action);
		LogPrint.Print("=============over======================");
		
		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_USERINFO_LIKE;
		handler.sendMessage(msg);
	}
	
	/**用户主页中晒单按钮的响应*/
	public void onUserInfoFormButtonClick(String url, int userid, int action){
		LogPrint.Print("=======onUserInfoFormButtonClick=======");
		LogPrint.Print("url = "+url);
		LogPrint.Print("userid = "+userid);
		LogPrint.Print("action = "+action);
		LogPrint.Print("=============over======================");
		
		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_USERINFO_FORM;
		handler.sendMessage(msg);
	}
	
	/**用户主页中userbar的加关注按钮的响应*/
	public void onUserBarAddButtonClick(String url, int userid, int type){
		LogPrint.Print("=======onUserBarAddButtonClick=======");
		LogPrint.Print("url = "+url);
		LogPrint.Print("userid = "+userid);
		LogPrint.Print("type = "+type);
		LogPrint.Print("=============over======================");
		
		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		bundle.putInt("userid", userid);
		bundle.putInt("type", type);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_USERBAR_LEFTBUTTON;
		handler.sendMessage(msg);
	}
	
	/**用户主页中userbar的发消息按钮的响应*/
	public void onUserBarMessageButtonClick(int userid){
		LogPrint.Print("=======onUserBarMessageButtonClick=======");
		LogPrint.Print("userid = "+userid);
		LogPrint.Print("=============over======================");
		
		Bundle bundle = new Bundle();
		bundle.putInt("userid", userid);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_USERBAR_MESSAGE;
		handler.sendMessage(msg);
	}
	
	/**用户主页中userbar的推荐好友按钮的响应*/
	public void onUserBarRecommendButtonClick(int userid){
		LogPrint.Print("=======onUserBarRecommendButtonClick=======");
		LogPrint.Print("userid = "+userid);
		LogPrint.Print("=============over======================");
		
		Bundle bundle = new Bundle();
		bundle.putInt("userid", userid);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_USERBAR_PUSHFRIEND;
		handler.sendMessage(msg);
	}
	
	/**三级评论中的列表的响应*/
	public void onListItemCommentU_Click(String url, int userid,int action,boolean isMoreItem,int curIndex,int totalpage,int subjectid,int commentid){
		LogPrint.Print("=======onListItemCommentU_Click=======");
		LogPrint.Print("url = "+url);
		LogPrint.Print("userid = "+userid);
		LogPrint.Print("action = "+action);
		LogPrint.Print("isMoreItem = "+isMoreItem);
		LogPrint.Print("curIndex = "+curIndex);
		LogPrint.Print("totalpage = "+totalpage);
		LogPrint.Print("subjectid = "+subjectid);
		LogPrint.Print("commentid = "+commentid);
		LogPrint.Print("=============over======================");
		
		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		bundle.putInt("userid", userid);
		bundle.putInt("action", action);
		bundle.putBoolean("isMoreItem", isMoreItem);
		bundle.putInt("curIndex", curIndex);
		bundle.putInt("totalpage", totalpage);
		bundle.putInt("subjectid", subjectid);
		bundle.putInt("commentid", commentid);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_LISTITEM_COMMENTU;
		handler.sendMessage(msg);
	}
	
	/**单品页中评论列表查看更多的响应*/
	public void onListItemCommentC_Click(int curIndex,int totalpage){
		LogPrint.Print("=======onListItemCommentC_Click=======");
		LogPrint.Print("curIndex = "+curIndex);
		LogPrint.Print("totalpage = "+totalpage);
		LogPrint.Print("=============over======================");
		
		Bundle bundle = new Bundle();
		bundle.putInt("curIndex", curIndex);
		bundle.putInt("totalpage", totalpage);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_LISTITEM_COMMENTU;
		handler.sendMessage(msg);
	}
	
	/**评论对话的相应*/
	public void onCommentInputBoxClick(){
		LogPrint.Print("=======onCommentInputBoxClick=======");
		LogPrint.Print("还需要在activity中获得弹出输入窗口");
		LogPrint.Print("=============over======================");
	}
	
	/**我的好友中tabbutton的响应*/
	public void onFriendTabbuttonClick(String url,int curIndex){
		LogPrint.Print("=======onFriendTabbuttonClick=======");
		LogPrint.Print("url = "+url);
		LogPrint.Print("curIndex = "+curIndex);
		LogPrint.Print("=============over======================");
		
		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		bundle.putInt("curIndex", curIndex);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_FRIEND_TABBUTTON;
		handler.sendMessage(msg);
	}
	
	/**好友列表项的响应*/
	public void onFriendListItemClick(String url, int userid,int action,boolean isMoreItem,int curIndex,int totalpage){
		LogPrint.Print("=======onFriendListItemClick=======");
		LogPrint.Print("url = "+url);
		LogPrint.Print("userid = "+userid);
		LogPrint.Print("action = "+action);
		LogPrint.Print("isMoreItem = "+isMoreItem);
		LogPrint.Print("curIndex = "+curIndex);
		LogPrint.Print("totalpage = "+totalpage);
		LogPrint.Print("=============over======================");
		
		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		bundle.putInt("userid", userid);
		bundle.putInt("action", action);
		bundle.putBoolean("isMoreItem", isMoreItem);
		bundle.putInt("curIndex", curIndex);
		bundle.putInt("totalpage", totalpage);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_LISTITEM_FRIEND;
		handler.sendMessage(msg);
	}
	
	/**好友列表(onlytext)的子项的响应*/
	public void onFriendListItemChildClick(int userid,String flush,boolean isselected,int type,String name){
		LogPrint.Print("=======onFriendListItemChildClick=======");
		LogPrint.Print("userid = "+userid);
		LogPrint.Print("flush = "+flush);
		LogPrint.Print("isselected = "+isselected);
		LogPrint.Print("type = "+type);
		LogPrint.Print("name = "+name);
		LogPrint.Print("=============over======================");
		
		Bundle bundle = new Bundle();
		bundle.putInt("userid", userid);
		bundle.putString("flush", flush);
		bundle.putBoolean("isselected", isselected);
		bundle.putInt("type", type);
		bundle.putString("name", name);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_LISTITEM_FRIENDCHILD;
		handler.sendMessage(msg);
	}
	
	/**搜索的响应*/
	public void onInputlineClick(String url,String str){
		LogPrint.Print("=======onInputlineClick=======");
		LogPrint.Print("url = "+url);
		LogPrint.Print("str = "+str);
		LogPrint.Print("=============over======================");
		
		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		bundle.putString("str", str);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_INPUTLINE;
		handler.sendMessage(msg);
	};
	
	/**添加好友列表项的响应*/
	public void onAddFriendsListItemClick(String url, int userid,int action,boolean isMoreItem,int curIndex,int totalpage){
		LogPrint.Print("=======onAddFriendsListItemClick=======");
		LogPrint.Print("url = "+url);
		LogPrint.Print("userid = "+userid);
		LogPrint.Print("action = "+action);
		LogPrint.Print("isMoreItem = "+isMoreItem);
		LogPrint.Print("curIndex = "+curIndex);
		LogPrint.Print("totalpage = "+totalpage);
		LogPrint.Print("=============over======================");
		
		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		bundle.putInt("userid", userid);
		bundle.putInt("action", action);
		bundle.putBoolean("isMoreItem", isMoreItem);
		bundle.putInt("curIndex", curIndex);
		bundle.putInt("totalpage", totalpage);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_LISTITEM_ADDFRIENDS;
		handler.sendMessage(msg);
	}
	
	/**我的撒娇中更多的响应*/
	public void onCoquetryItemClick(int curIndex,int totalpage,boolean ismore,int selectIndex,String href){
		LogPrint.Print("=======onCoquetryItemClick=======");
		LogPrint.Print("curIndex = "+curIndex);
		LogPrint.Print("totalpage = "+totalpage);
		LogPrint.Print("ismore = "+ismore);
		LogPrint.Print("selectIndex = "+selectIndex);
		LogPrint.Print("href = "+href);
		LogPrint.Print("=============over======================");
		
		Bundle bundle = new Bundle();
		bundle.putInt("curIndex", curIndex);
		bundle.putInt("totalpage", totalpage);
		bundle.putBoolean("ismore", ismore);
		bundle.putInt("selectIndex", selectIndex);
		bundle.putString("href", href);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_COQUETRY;
		handler.sendMessage(msg);
	}
	
	/**瀑布流中子项的响应*/
	public void onStreamPageItemClick(String url,int chickPos){
		LogPrint.Print("=======onStreamPageItemClick=======");
		LogPrint.Print("url = "+url);
		LogPrint.Print("=============over======================");
		
		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		bundle.putInt("chickPos", chickPos);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_STREAMPAGE;
		handler.sendMessage(msg);
	}
	/**瀑布流中更多的响应*/
	public void onStreamPageMoreClick(String nexturl){
		LogPrint.Print("=======onCoquetryItemClick=======");
		LogPrint.Print("nexturl = "+nexturl);
		LogPrint.Print("=============over======================");
		
		Bundle bundle = new Bundle();
		bundle.putString("nexturl", nexturl);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_STREAMPAGE_MORE;
		handler.sendMessage(msg);
	}
	
	/**单品页中大图的响应*/
	public void onMyPagePlayClick(String url){
		LogPrint.Print("=======onMyPagePlayClick=======");
		LogPrint.Print("url = "+url);
		LogPrint.Print("=============over======================");
		
		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_MYPAGEPLAY;
		handler.sendMessage(msg);
	}
	
}
