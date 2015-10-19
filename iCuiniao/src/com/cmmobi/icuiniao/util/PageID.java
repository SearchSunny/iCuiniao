/**
 * 
 */
package com.cmmobi.icuiniao.util;

/**
 * @author hw
 *
 *存储全部页面的id及常量
 */
public class PageID {

	private static int mPageId;
	
	public final static int PAGEID_MAINPAGE_ALL = 0;//首页-全部
	public final static int PAGEID_MAINPAGE_DISCOUNT = 1;//首页-折扣
	public final static int PAGEID_MAINPAGE_RECOMMEND = 2;//首页-推荐
	public final static int PAGEID_MAINPAGE_MYFORM = 3;//首页-晒单
	public final static int PAGEID_MAINPAGE_ACTIVITIES = 4;//首页-活动
	public final static int PAGEID_MAINPAGE_MYLIKE = 5;//首页-我的喜欢
	public final static int PAGEID_MAINPAGE_MYLOOKED = 6;//首页-我看过的
	public final static int PAGEID_MAINPAGE_USERLIKE = 7;//首页-其他用户喜欢的
	public final static int PAGEID_MAINPAGE_USERLOOKED = 8;//首页-其他用户看过的
	public final static int PAGEID_USER_MAINPAGE = 9;//用户主页
	public final static int PAGEID_PUSH_FRIEDN = 10;//推荐给好友
	public final static int PAGEID_MAINMENU = 11;//主菜单
	public final static int PAGEID_MESSAGE = 12;//消息管理
	public final static int PAGEID_SETTING = 13;//设置
	public final static int PAGEID_SETTING_PASSWORD = 14;//密码设置
	public final static int PAGEID_SETTING_USERINFO = 15;//详细个人信息
	public final static int PAGEID_SETTING_FINDPASSWORD = 16;//找回密码
	public final static int PAGEID_FRIEND_LIST = 17;//我的好友
	public final static int PAGEID_FRIEND_SEARCH = 18;//搜索好友
	public final static int PAGEID_FRIEND_ADD = 19;//添加好友
	public final static int PAGEID_MESSAGE_PRIVATE = 20;//私信
	public final static int PAGEID_LOGIN = 21;//登陆界面
	public final static int PAGEID_REGEDIT = 22;//注册界面
	public final static int PAGEID_MEDIAPLAYER = 23;//播放器
	public final static int PAGEID_WEBVIEW = 24;//webview浏览器
	public final static int PAGEID_OWN_MAINPAGE = 25;//个人主页
	public final static int PAGEID_COMMENT_USER = 26;//用户评论页面
	public final static int PAGEID_FORM = 27;//晒单界面
	public final static int PAGEID_LIKE = 28;//喜欢窗口
	public final static int PAGEID_COMMODITY = 29;//品牌主页
	public final static int PAGEID_OWN_MAINPAGE_FORM = 30;//晒单的单品页
	public final static int PAGEID_COMMENT_USER_FORM = 31;//晒单用户评论页面
	public final static int PAGEID_COQUETRY = 32;//我的撒娇
	public final static int PAGEID_MESSAGEMANAGER = 33;//消息管理
	public final static int PAGEID_USERPAGE = 40;//用户主页
	public final static int PAGEID_STYLEPAGE = 41;//自定义商品页
	
	
	public final static String MYPAGE_MENU_TITLE = "选择操作";
	public final static String[] MYPAGE_MENU_ITEM = {"查看此人资料","回复"};
	
	public final static String SETTING_MENU_TITLE = "选择操作";
	public final static String[] SETTING_MENU_ITEM = {"拍照","用户相册"};
	
	public final static String SETTING_MORE_EDUCATION_MENU_TITLE = "教育程度";
	public final static String[] SETTING_MORE_EDUCATION_MENU_ITEM = {"大专以下","大专","本科","硕士","博士"};
	
	public final static String SETTING_MORE_MONEY_MENU_TITLE = "月收入";
	public final static String[] SETTING_MORE_MONEY_MENU_ITEM = {"2000以下","2000-5000","5000-10000","10000-20000","20000以上"};
	
	public final static String SETTING_MORE_OCCUPTION_MENU_TITLE = "职业";
	public final static String[] SETTING_MORE_OCCUPTION_MENU_ITEM = {"公务员/军队","企业主/合伙人","公司高级管理人员","部门主管","公司行政人员/职员","专业人士/医生/律师","会计/金融相关从业者","教育工作者","设计师/造型师/摄影师","演员/模特/音乐/艺术相关工作者","人力资源/培训/人事工作者","IT技术从业者","互联网专业人士","科研/研究/开发","作家/编辑/记者/媒体从业者","咨询行业从业者","销售/市场营销/商务工作者","自由职业者","学生","家庭主妇","其他"};
	
	public final static String SETTING_MESSAGE_MENU_TITLE = "选择操作";
	public final static String[] SETTING_MESSAGE_MENU_ITEM = {"删除"};
	public final static String[] SETTING_MESSAGE_MENU_ITEM1 = {"删除","推荐地址"};
	public final static String[] SETTING_MESSAGE_TANHAO = {"重新发送","删除消息"};
	public final static String SETTING_MESSAGE_MENU_TITLE_NEW = "将整个对话框删除";
	public final static String[] SETTING_MESSAGE_MENU_ITEM_NEW = {"确定","取消"};
	
	public final static String[] BIND_MENU_ITEM = {"取消绑定"};
	
	public final static String[] ADDRMANAGER_MENU_ITEM = {"删除","取消"};
	
	public final static String[] COMMENT_MSG_MENU_ITEM= {"回复评论","查看商品"};
	
	public final static String SETTING_MESSAGE_MENU_TITLE_A = "删除跟TA的聊天记录";
	public final static String[] SETTING_MESSAGE_MENU_TITLE_A_SUB = {"删除","不删了"};
	public static void setPageId(int value){
		mPageId = value;
	}
	
	public static int getPageId(){
		return mPageId;
	}
}
