  /**
 * 
 */
package com.cmmobi.icuiniao.util;

/**
 * @author hw
 *
 *存储全部Message的ID
 */
public class MessageID {

	//============popupMenu的相关message
	public static final int MESSAGE_POPUPMENU_BUTTONCLICK = 1;
	public static final int MESSAGE_POPUPMENU_POPING_OPEN = 2;
	public static final int MESSAGE_POPUPMENU_POP_OPENOVER = 3;
	public static final int MESSAGE_POPUPMENU_POPING_CLOSE = 4;
	public static final int MESSAGE_POPUPMENU_POP_CLOSEOVER = 5;
	
	//============联网相关message
	//页面数据下载完成
	public static final int MESSAGE_CONNECT_DOWNLOADOVER = 6;
	//页面数据布局完成,不包含图片
	public static final int MESSAGE_CONNECT_LAYOUTOVER = 7;
	//连接开始
	public static final int MESSAGE_CONNECT_START = 8;
	//连接出错
	public static final int MESSAGE_CONNECT_ERROR = 9;
	//渲染
	public static final int MESSAGE_RENDER = 10;
	//关闭loading
	public static final int MESSAGE_CLOSEPROGRESS = 11;
	//下载图片总数值
	public static final int MESSAGE_DOWNLOAD_IMAGE_SIZE_VALUE = 12;
	//下载图片当前数值
	public static final int MESSAGE_DOWNLOAD_IMAGE_CURSIZE_VALUE = 13;
	//更新图片当前进度值
	public static final int MESSAGE_UPDATE_IMAGE_VALUE = 14;
	//下载图片超时记时
	public static final int MESSAGE_TIMEOUT = 15;
	//下载图片错误
	public static final int MESSAGE_DOWNLOAD_IMAGE_ERROR = 16;
	//重置监听事件
	public static final int MESSAGE_SET_LISTENER = 17;	


	
	//============按键响应相关message
	//titlebar/titlebarfloat中的左按钮被点击时
	public static final int MESSAGE_CLICK_TITLEBAR_LEFTBUTTON = 20;
	//titlebar/titlebarfloat中的中间按钮被点击时
	public static final int MESSAGE_CLICK_TITLEBAR_MIDDLEBUTTON = 21;
	//titlebar/titlebarfloat中的右按钮被点击时
	public static final int MESSAGE_CLICK_TITLEBAR_RIGHTBUTTON = 22;
	//titlebar/titlebarfloat中的顶部菜单被点击时
	public static final int MESSAGE_CLICK_TITLEBAR_MENU = 23;
	//mainpage中的图片被点击时
	public static final int MESSAGE_CLICK_MAINPAGE_ITEM = 24;
	//mypage中的卖家图标被点击时
	public static final int MESSAGE_CLICK_COMMODITYINFO_ICON = 25;
	//mypage中的晒单按钮被点击时
	public static final int MESSAGE_CLICK_COMMODITYINFO_FORMBUTTON = 26;
	//mypage中的喜欢按钮被点击时
	public static final int MESSAGE_CLICK_COMMODITYINFO_LIKEBUTTON = 27;
	//mypage中的like窗口grid被点击时
	public static final int MESSAGE_CLICK_POPLIKE_GRIDITEM = 28;
	//mypage中评论列表被点击时
	public static final int MESSAGE_CLICK_LISTITEM_COMMENTC = 29;
	//thirdcomment中评论列表被点击时
	public static final int MESSAGE_CLICK_LISTITEM_COMMENTU = 30;
	//friend列表被点击时
	public static final int MESSAGE_CLICK_LISTITEM_FRIEND = 31;
	//friend列表子组件被点击时
	public static final int MESSAGE_CLICK_LISTITEM_FRIENDCHILD = 32;
	//tabbutton被点击时
	public static final int MESSAGE_CLICK_FRIEND_TABBUTTON = 33;
	//inputline被点击时
	public static final int MESSAGE_CLICK_INPUTLINE = 34;
	//userbar中的推荐好友被点击时
	public static final int MESSAGE_CLICK_USERBAR_PUSHFRIEND = 35;
	//userbar中的发信息被点击时
	public static final int MESSAGE_CLICK_USERBAR_MESSAGE = 36;
	//userbar中的左侧按钮被点击时
	public static final int MESSAGE_CLICK_USERBAR_LEFTBUTTON = 37;
	//添加好友列表被点击时
	public static final int MESSAGE_CLICK_LISTITEM_ADDFRIENDS = 38;
	//单品页中商品简介区域被点击时
	public static final int MESSAGE_CLICK_COMMODITYINFO_BOX = 39;
	//他人主页中喜欢按钮被点击时
	public static final int MESSAGE_CLICK_USERINFO_LIKE = 40;
	//他人主页中晒单按钮被点击时
	public static final int MESSAGE_CLICK_USERINFO_FORM = 41;
	//我的撒娇中更多被点击时
	public static final int MESSAGE_CLICK_COQUETRY = 42;
	//瀑布流中子项被点击时
	public static final int MESSAGE_CLICK_STREAMPAGE = 43;
	//瀑布流中更多被点击时
	public static final int MESSAGE_CLICK_STREAMPAGE_MORE = 44;
	//单品页中视频区域被点击时
	public static final int MESSAGE_CLICK_MYPAGEPLAY = 45;
	//mypage中评论列表被点击时
	public static final int MESSAGE_CLICK_LISTITEM_COMMENT = 46;
	//mypage中评论列表用户图标被点击时
	public static final int MESSAGE_CLICK_LISTITEM_ICON = 47;
	//mypage中评论列表更多项被点击时
	public static final int MESSAGE_CLICK_LISTITEM_COMMENT_MORE = 48;
	
	//============联网错误处理相关message
	//图片加载错误
	public static final int MESSAGE_IMAGE_LOAD_ERROR = 50;
	//页面加载失败
//	public static final int MESAGE_PAGE_LOAD_ERROR = 51;
	//显示页面加载失败提示
	public static final int MESSAGE_SHOW_WRONG_TIPS= 52;	
	
	//====================通知刷新的message
	//通知adapter刷新
	public static final int MESSAGE_FLUSH_ADAPTER = 101;
	//通知刷新页面
	public static final int MESSAGE_FLUSH_PAGE = 102;
	//进入更多评论列表 lyb
	public static final int MESSAGE_COMMENT_LIST = 103;
	
	//====================poptitlemenu的message
	public static final int MESSAGE_POPTITLEMENU = 110;
	
	//添加好友
	public static final int MESSAGE_ADD_FRIEND= 111;
	//打招呼或者发消息
	public static final int MESSAGE_SAYHI_MSG = 112;
	
	//====================menuclick的message
	//mypage中评论列表查看此人资料被点击
	public static final int MESSAGE_MENUCLICK_MYPAGE_COMMENTC_USERPAGE = 200;
	//mypage中评论列表回复被点击
	public static final int MESSAGE_MENUCLICK_MYPAGE_COMMENTC_COMMENT = 201;
	//setting中拍照被点击
	public static final int MESSAGE_MENUCLICK_SETTING_CAMERA = 202;
	//setting中用户相册被点击
	public static final int MESSAGE_MENUCLICK_SETTING_PHOTO = 203;
	//setting_more中教育程度点击
	public static final int MESSAGE_MENUCLICK_SETTING_MORE_EDUCATION = 204;
	//setting_more中月收入点击
	public static final int MESSAGE_MENUCLICK_SETTING_MORE_MONEY = 205;
	//setting_more中职业点击
	public static final int MESSAGE_MENUCLICK_SETTING_MORE_OCCUPTION = 206;
	//私信中的点击（撒娇删除）
	public static final int MESSAGE_MENUCLICK_MESSAGE = 207;
	//绑定中的点击
	public static final int MESSAGE_BIND_MENUCLICK = 208;
	//地址管理中的点击
	public static final int MESSAGE_MENUCLICK_ADDRMANAGER = 209;
	//撒娇删除lyb
	public static final int MESSAGE_COQUETRY_TOUCH_DEL = 210;
	//解绑中的取消
	public static final int MESSAGE_BIND_MENUCLICK_CANCEL = 211;
	

	//commentMoreList中评论列表查看此人资料被点击
	public static final int MESSAGE_MENUCLICK_COMMENT_LIST_USERPAGE = 220;
	//commentMoreList评论列表回复被点击
	public static final int MESSAGE_MENUCLICK_COMMENTC_LIST_COMMENT = 221;
	//评论消息页面- 评论回复
	public static final int MESSAGE_MENUCLICK_COMMENTC_MSG_REPLY = 230;
	//评论消息页面- 进入商品
	public static final int MESSAGE_MENUCLICK_COMMENTC_MSG_COMMODITY = 231;
	

	
	//相册回调
	public static final int MESSAGE_PHOTO_RESULT = 300;
	//相机回调
	public static final int MESSAGE_CAMERA_RESULT = 301;
	
	//mediaplayer=========================
	//初始化视频
	public static final int MESSAGE_MEDIA_INIT = 400;
	//播放
	public static final int MESSAGE_MEDIA_PLAY = 401;
	//快进
	public static final int MESSAGE_MEDIA_NEXT = 402;
	//快退
	public static final int MESSAGE_MEDIA_PRE = 403;
	//当前播放时间
	public static final int MESSAGE_MEDIA_CURRENTTIME = 404;
	//跳转播放位置
	public static final int MESSAGE_MEDIA_SEEK = 405;
	//5秒后关闭视频操作控件
	public static final int MESSAGE_MEDIA_CLOSE = 406;
	
	//消息==================================
	//初始化
	public static final int MESSAGE_INIT = 500;
	//轮询
	public static final int MESSAGE_CHICK = 501;
	
	//滑动==================================
	//下一个
	public static final int MESSAGE_FLING_NEXT = 600;
	//上一个
	public static final int MESSAGE_FLING_PRE = 601;
	//滑动通知
	public static final int MESSAGE_START_FLING_NEXT = 602;
	public static final int MESSAGE_START_FLING_PRE = 603;
	
	//重力响应
	public static final int MESSAGE_SENSOR = 650;
	//缓存下载
	public static final int MESSAGE_DOWNLOAD_CACHE = 651;
	
	//主菜单打开动画
	public static final int MESSAGE_MAINMENU_OPEN = 652;
	//主菜单关闭动画
	public static final int MESSAGE_MAINMENU_CLOSE = 653;
	//摇一摇打开动画
	public static final int MESSAGE_ROCK_OPEN = 654;
	//摇一摇关闭动画
	public static final int MESSAGE_ROCK_CLOSE = 655;
	//摇一摇开始音乐播放完成
	public static final int MESSAGE_SENSOR_STARTROCK_PLAYOVER = 656;
	//摇一摇结束音乐播放完成
	public static final int MESSAGE_SENSOR_ENDROCK_PLAYOVER = 657;
	//进入摇一摇界面
	public static final int MESSAGE_IN_ROCK = 658;
	//首页未进入摇一摇界面
	public static final int MESSAGE_NOT_IN_ROCK = 659;
	
	//喜欢返回的刷新
	public static final int REQUESTCODE_LIKE_FLUSH = 800;
	//视频已达到可播放的长度
	public static final int MESSAGE_MEDIAPLAYER_READY = 900;
	//视频加载错误
	public static final int MESSAGE_MEDIAPLAYER_LOAD_ERROR = 901;
	//视频播放错误
	public static final int MESSAGE_MEDIAPLAYER_PLAY_ERROR = 902;
	//更新缓存进度
	public static final int MESSAGE_MEDIAPLAYER_BUFFERUPDATE = 903;
	

	//私信重新发送、删除取消
	public static final int MESSAGE_SENDANDDEL_CANCEL = 1010;
	//私信管理删除聊天记录
	public static final int MESSAGE_DELRECORD = 1011;
	
	//删除收货地址或撒娇对象 
	public static final int MESSAGE_DEL_ADDRESS= 2010;
	
	
	//REQUEST==================================
	//打招呼
	public static final int REQUEST_SAY_HI = 9001;
	//发送好友消息
	public static final int REQUEST_SEND_MSG = 9002;
	//进入用户设置
	public static final int REQUEST_GO_SET = 9003;

}
