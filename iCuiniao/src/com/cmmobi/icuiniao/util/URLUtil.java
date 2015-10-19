/**
 * 
 */
package com.cmmobi.icuiniao.util;

/**
 * @author hw
 *
 *定义使用的url地址
 */
public class URLUtil {

	public final static boolean IS_LOCAL_NETSTATE = false;//是否使用的是内网地址
	private static String getHost(){
		if(IS_LOCAL_NETSTATE){
			return "http://192.168.1.233:8080";
		}else{
			return "http://125.39.224.103:37001";//外网测试
//			return "http://a1.icuiniao.cn:7001";//新外网测试
//			return "http://a1.icuiniao.com:7001";//正式外网
		}
	}
	public static String userIconHost = "http://125.39.224.103:37000";//外网测试
//	public static String userIconHost = "http://image.icuiniao.cn:7000";//新外网测试
//	public static String userIconHost = "http://a1.icuiniao.com:7000";//正式外网
	
	//是否为本地链接
	private static boolean isLocalUrl;
	public static void setIsLocalUrl(boolean b){isLocalUrl = b;}
	public static boolean IsLocalUrl(){return isLocalUrl;}
	
	//版本号
	public final static int version = 220001;
	//更新平台使用的版本号样式
	public final static String str_version = "2_2_0001";
	//更新平台使用的system字段
	public final static int system = 101;
	//更新平台使用的productcode字段
	public final static int productcode = 8;
	//平台号
	public static final String plaid = "1016and";
	//分辨率
	public static String dpi(){return CommonUtil.screen_width+"x"+CommonUtil.screen_height;}
	//渠道号
	//channel0500	官网	cnm1		main
	//channel0502	新浪微博	cns1	sfan
	//channel0503	QQ空间	cnq1	qzone
	//channel0505	邮箱分享	cnm2	mail
	//channel0506	短信	cnm3		love
	//channel0507	撒娇	cns6		honey
	//channel0508	事件渠道	cne1	funny
	//channel0029	宝软网			channel0029
	/**channel0029	宝软网  需要将软件推荐去掉(2012.0.28安其拉需求)*/
	
	public static String himarket = "channel0008";  //安卓市场
	public static String xiaomi = "channel0140";	//小米
	public static String qihu360 = "channel0019";		//360
	public static String zs91 = "channel0001";      //91助手
	
//	public static String fid = "channel0509";
//	public static String fid = himarket; //"channel0008"; //安卓市场
//	public static String fid = qihu360; 
	public static String fid = zs91;
	//项目id
	public static final int pc = 0;
	//版本发布时间
	public static final String reletm = "20130624";
	
	public static String getKeyWord(String fid) {
		if(fid.equals("channel0500")){
			return "main";
		}else if(fid.equals("channel0502")){
			return "sfan";
		}else if(fid.equals("channel0503")){
			return "qzone";
		}else if(fid.equals("channel0505")){
			return "mail";
		}else if(fid.equals("channel0506")){
			return "love";
		}else if(fid.equals("channel0507")){
			return "honey";
		}else if(fid.equals("channel0508")){
			return "funny";
		}else{
			return fid;
		}
	}
	
	//================================本地地址=======================================
//	//首页地址
//	public final static String URL_MAINPAGE = "/sdcard/iCuiniao/cache/mainpage.xml";
//	//我的好友地址
//	public final static String URL_MYFRIEND = "/sdcard/iCuiniao/cache/friend.xml";
//	//搜索好友地址
//	public final static String URL_SEARCHFRIEND = "/sdcard/iCuiniao/cache/searchfriend.xml";
//	//推荐给友人的地址
//	public final static String URL_PUSHFRIEND = "/sdcard/iCuiniao/cache/pushfriend.xml";
//	//添加好友的地址
//	public final static String URL_ADDFRIENDS = "/sdcard/iCuiniao/cache/addfriends.xml";
//	//单品页更多
//	public final static String URL_MYPAGE_MORE_LISTITEM = "/sdcard/iCuiniao/cache/thirdcomment_3.xml";
	
	//================================真实地址=====================================
	//首页地址
	public final static String URL_MAINPAGE = getHost()+"/terminalconnector/struts/Product!getProductListAll";
	//首页折扣
	public final static String URL_MAINPAGE_DISCOUNT = getHost()+"/terminalconnector/struts/Product!getProductDiscountList";
	//首页推荐
	public final static String URL_MAINPAGE_RECOMMEND = getHost()+"/terminalconnector/struts/Product!getProductRecommendList";
	//首页活动
	public final static String URL_MAINPAGE_ACTIVITIES = getHost()+"/terminalconnector/struts/Product!getProductActivitiesList";
	//首页晒单
	public final static String URL_MAINPAGE_FORM = getHost()+"/terminalconnector/struts/Form!getFormList";
	//我的喜欢
	public final static String URL_MAINPAGE_LIKE = getHost()+"/terminalconnector/struts/BaseBuyer!getProductLikeList";
	//我看过的
	public final static String URL_MAINPAGE_LOOKED = getHost()+"/terminalconnector/struts/BaseBuyer!getProductReadList";
	//单品页更多
	public final static String URL_MYPAGE_MORE_LISTITEM = getHost()+"/terminalconnector/struts/Product!getProductCommMore";
	//我的好友-关注更多
	public final static String URL_MYFRIEND_MORE_ADD_LISTITEM = getHost()+"/terminalconnector/struts/BaseBuyer!getAttenMoreList";
	//我的好友-相互关注更多
	public final static String URL_MYFRIEND_MORE_ADDEACHOTHER_LISTITEM = getHost()+"/terminalconnector/struts/BaseBuyer!getFriendMoreList";
	//我的好友-粉丝更多
	public final static String URL_MYFRIEND_MORE_FANS_LISTITEM = getHost()+"/terminalconnector/struts/BaseBuyer!getFansMoreList";
	//搜索好友-更多
	public final static String URL_SEARCHFRIEND_MORE_LISTITEM = getHost()+"/terminalconnector/struts/BaseBuyer!getSearchFriendMoreList";
	//添加好友-更多
	public final static String URL_ADDFRIEND_MORE_LISTITEM = getHost()+"/terminalconnector/struts/BaseBuyer!getAddFriendMoreList";
	//推荐好友-更多
	public final static String URL_PUSHFRIEND_MORE_LISTITEM = getHost()+"/terminalconnector/struts/BaseBuyer!getRecommendMore";
	//用户评论三级-更多
	public final static String URL_THIRDCOMMENT_MORE_LISTITEM = getHost()+"/terminalconnector/struts/BaseBuyer!getUserCommMore";
	//别人的主页
	public final static String URL_USERPAGE = getHost()+"/terminalconnector/struts/BaseBuyer!getOtherHomePage";
	//我的好友-关注地址
	public final static String URL_MYFRIEND_ADD = getHost()+"/terminalconnector/struts/BaseBuyer!getAtten";
	//我的好友-相互关注地址
	public final static String URL_MYFRIEND_EACHOTHER = getHost()+"/terminalconnector/struts/BaseBuyer!getMyFriend";
	//我的好友-粉丝地址
	public final static String URL_MYFRIEND_FANS = getHost()+"/terminalconnector/struts/BaseBuyer!getMyFans";
	//搜索好友地址
	public final static String URL_SEARCHFRIEND = getHost()+"/terminalconnector/struts/BaseBuyer!getSearchFriend";
	//推荐给友人的地址
	public final static String URL_PUSHFRIEND = getHost()+"/terminalconnector/struts/BaseBuyer!getRecommend";
	//添加好友的地址
	public final static String URL_ADDFRIENDS = getHost()+"/terminalconnector/struts/BaseBuyer!getAddFriend";
	//注册
//	public final static String URL_REGEDIT = getHost()+"/terminalconnector/struts/User!registered";
	public final static String URL_REGEDIT = getHost()+"/Approve/login/register";
	//登陆
//	public final static String URL_LOGIN = getHost()+"/terminalconnector/struts/User!login";
	public final static String URL_LOGIN = getHost()+"/Approve/login/login";
	//获取用户图标
//	public final static String URL_GET_USERICON = getHost()+"/terminalconnector/struts/User!getUserHeadPic";
	public final static String URL_GET_USERICON = userIconHost+"/terminalconnector/resource/user/pic/M/";
	//随便看看
	public final static String URL_MAINPAGE_RANDOM = getHost()+"/terminalconnector/struts/Product!getProductListAll";
	//图片上传
	public final static String URL_IMAGE_UPLOAD = getHost()+"/terminalconnector/servlet/upload/userImage";
	//添加商品评论
	public final static String URL_COMMENT_COMMODITY = getHost()+"/terminalconnector/struts/BaseBuyer!addSubject";
	//添加用户评论
	public final static String URL_COMMENT_USER = getHost()+"/terminalconnector/struts/BaseBuyer!addComment";
	//添加三级评论
	public final static String URL_COMMENT_THIRDCOMMENT = getHost()+"/terminalconnector/struts/BaseBuyer!addTwoComment";
	//关注页面状态改变
	public final static String URL_MYFRIEND_ADD_CHANGE = getHost()+"/terminalconnector/struts/BaseBuyer!getAtten";
	//相互关注页面状态改变
	public final static String URL_MYFRIEND_EACHOTHER_CHANGE = getHost()+"/terminalconnector/struts/BaseBuyer!getMyFriend";
	//粉丝页面状态改变
	public final static String URL_MYFRIEND_FANS_CHANGE = getHost()+"/terminalconnector/struts/BaseBuyer!getMyFans";
	//用户详细信息上传
	public final static String URL_SETTING_MORE = getHost()+"/terminalconnector/struts/User!userDetails";
	//第三方登陆
	public final static String URL_LOGIN_THIRDPLATFROM = getHost()+"/terminalconnector/struts/User!ThirdLogin";
	//晒单上传
	public final static String URL_FORM_UPLOAD = getHost()+"/terminalconnector/servlet/upload/formImage";
	//发消息
	public final static String URL_MESSAGE_SEND = getHost()+"/terminalconnector/struts/User!sendMessage";
	//消息轮询
	public final static String URL_MESSAGE_GET = getHost()+"/terminalconnector/struts/User!receiveMessages";
	//撒娇
	public final static String URL_SAJIAO = getHost()+"/terminalconnector/struts/Product!getCoquetry";
	//撒娇上传
	public final static String URL_SAJIAO_UPLOAD = getHost()+"/terminalconnector/struts/User!sendCoquetry";
	//修改密码
	public final static String URL_PASSWORD_RESET = getHost()+"/terminalconnector/struts/User!updatePwd";
	//找回密码
	public final static String URL_PASSWORD_FIND = getHost()+"/terminalconnector/struts/User!forgetPwd";
	//用户反馈
	public final static String URL_FEEDBACK = getHost()+"/terminalconnector/struts/System!userFeedback";
	//软件更新(自方后台)
	public final static String URL_SOFT_UPDATA = getHost()+"/terminalconnector/struts/System!updateSoftware";
	//软件更新(技术中心管理后台)
	public final static String URL_SOFT_UPDATA_CMMOBI = "http://channel.looklook.cn:8091/download/api/versionJson.do";
	//晒单更多
	public final static String URL_FORM_MORE_LISTITEM = getHost()+"/terminalconnector/struts/Form!getFormCommMore";
	//添加晒单商品评论
	public final static String URL_FORM_COMMODITY = getHost()+"/terminalconnector/struts/Form!addFormSubject";
	//添加晒单用户评论
	public final static String URL_FORM_USER = getHost()+"/terminalconnector/struts/Form!addFormComment";
	//添加晒单三级评论
	public final static String URL_FORM_THIRDCOMMENT = getHost()+"/terminalconnector/struts/Form!addFormTwoComment";
	//晒单评论三级-更多
	public final static String URL_FORM_THIRDCOMMENT_MORE_LISTITEM = getHost()+"/terminalconnector/struts/Form!getFormUserCommMore";
	//获得地址管理信息
	public final static String URL_ADDRMANAGER_GET = getHost()+"/terminalconnector/struts/User!getMailingAddr";
	//上传地址管理信息
	public final static String URL_ADDRMANAGER_UPDATA = getHost()+"/terminalconnector/struts/User!addMailingAddr";
//	//获得绑定信息
//	public final static String URL_BIND_GET = getHost()+"/terminalconnector/struts/User!getBinding";
	public final static String URL_BIND_GET = getHost()+"/Approve/agent/user!getBinding";
//	//绑定
//	public final static String URL_BIND_ADD = getHost()+"/terminalconnector/struts/User!addBinding";
	public final static String URL_BIND_ADD = getHost()+"/Approve/agent/user!binding";
//	//解除绑定
//	public final static String URL_BIND_REMOVE = getHost()+"/terminalconnector/struts/User!removeBinding";
	public final static String URL_BIND_REMOVE = getHost()+"/Approve/agent/user!unbinding";
	//完善翠鸟账号
	public final static String URL_COMPLETE_ADD = getHost()+"/Approve/agent/user!perfectCNloginInfo";
	//绑定页面
	public final static String URL_BIND = getHost()+"/Approve/agent/user!bindInfo";
	//我的撒娇
	public final static String URL_MY_CAQUETRY = getHost()+"/terminalconnector/struts/User!myCoquetry";
	//我的撒娇更多
	public final static String URL_MY_CAQUETRY_MORE = getHost()+"/terminalconnector/struts/User!myCoquetryMoreList";
	//进入三级评论
	public final static String URL_THIRDCOMMENT = getHost()+"/terminalconnector/struts/BaseBuyer!getThreeComments";
	//注销
	public final static String URL_LOGOUT = getHost()+"/Approve/login/login!logout";
	//登陆通知(获取游客用户ID)
	public final static String URL_LOGIN_INFORM = getHost()+"/Approve/agent/user!getVisitor";
	//获取系统时间
	public final static String URL_GET_SYSTIME = getHost()+"/terminalconnector/struts/resource!getServerCurrentTime";
	//其他软件
	public final static String URL_SOFTPUSH = getHost()+"/terminalconnector/struts/Apply!getRecommendList";
	//客户端下载地址
	public final static String URL_APPDOWNLOAD = "http://icuiniao.com/c?";
	//我的喜欢删除
	public final static String URL_MYLIKE_DELETE = getHost()+"/terminalconnector/struts/BaseBuyer!removeLike";
	//分享
	public final static String URL_SHARE = getHost()+"/Approve/weibo/share";
	//添加喜欢商品
	public final static String URL_ADDLIKE = getHost()+"/terminalconnector/struts/BaseBuyer!addLike";
	//删除喜欢商品(单个)
	public final static String URL_MYLIKE_DELETE_SINGLE = getHost()+"/terminalconnector/struts/BaseBuyer!removeLikeById";
	//获取短链接
	public final static String URL_GET_SHORTLINK = getHost()+"/terminalconnector/shortlink";
	//删除我的撒娇
	public final static String URL_COQUETRY_DELETE = getHost()+"/terminalconnector/struts/User!removeCoquetry";
	//离线日志
	public final static String URL_OFFLINE = getHost()+"/terminalconnector/receiveLogs";
	//评论列表
	public final static String URL_COMMENT_LIST = getHost()+ "/terminalconnector/struts/BaseBuyer!commentList";
	//评论消息
	public final static String URL_COMMENT_MSG = getHost()+ "/terminalconnector/struts/BaseBuyer!commentForMess";
	//获取评论列表（单品页）新增
	public final static String URL_MYPAGE_COMMENTLIST = getHost()+"/terminalconnector/struts/BaseBuyer!commentListForPro";
	//获取54个单品页的商品id
	public final static String URL_ALLCOMMDITYID = getHost()+"/terminalconnector/struts/Product!getProductListAll54";
	//单品页
	public final static String URL_MYPAGE = getHost()+"/terminalconnector/struts/Product!getProduct";
	
	//分享上传oid cid logintype
	public final static String URL_SHARE_UPLAOD = getHost() + "/terminalconnector/struts/User!upLoadShareMess";
	//新用户主页(oid==uid时为我的主页 )
	public final static String URL_USER_HOMEPAGE = getHost() + "/terminalconnector/struts/BaseBuyer!userHomePage";

	//用户商品列表
	public final static String URL_PRODUCET_LIST = getHost() + "/terminalconnector/struts/BaseBuyer!getUserProductList";
	//添加黑名单
	public final static String URL_ADD_BLACK = getHost() + "/terminalconnector/struts/BaseBuyer!addBlacklist";
	//添加好友
	public final static String Url_ADD_FRIEND = getHost() + "/terminalconnector/struts/BaseBuyer!addFriend";
	//好友列表（uid：本地的最后一条uid）
	public final static String Url_FRIEND_LIST = getHost() + "/terminalconnector/struts/BaseBuyer!getFriends3";
	//黑名单列表（uid：本地的最后一条uid）
	public final static String Url_BLACK_LIST = getHost() + "/terminalconnector/struts/BaseBuyer!getBlacklist2";
	//删除黑名单
	public final static String Url_DEL_BLACK = getHost() + "/terminalconnector/struts/BaseBuyer!removeBlacklist";
	//全站用户搜索
	public final static String Url_ALL_SEARCH = getHost() + "/terminalconnector/struts/BaseBuyer!getAllUserSearch";
	//判断好友还是黑名单
	public final static String Url_FRIEND_OR_BLACK = getHost() + "/terminalconnector/struts/BaseBuyer!isFriendOrBlack";
	//判断是否能加为好友
	public final static String Url_IS_ADD_FRIEND = getHost() + "/terminalconnector/struts/BaseBuyer!isAddFriend";
	//用户查询权限修改
	public final static String Url_SET_QUIRY_AUTHOR = getHost() + "/terminalconnector/struts/BaseBuyer!updatePermissionToView";
	//获取用户查询权限
	public final static String Url_GET_QUIRY_AUTHOR = getHost() + "/terminalconnector/struts/BaseBuyer!getPermissionToView";
	//设置允许任何人添加好友
	public final static String Url_SET_PERMIT_ADD_FRIEND = getHost() + "/terminalconnector/struts/BaseBuyer!updateAddFriendPermit";
	//获取允许任何人添加好友
	public final static String Url_GET_PERMIT_ADD_FRIEND = getHost() + "/terminalconnector/struts/BaseBuyer!getAddFriendPermit";
	//获取新评论数
	public final static String Url_NEW_COMMENT = getHost() + "/terminalconnector/struts/BaseBuyer!commentForMessCount";
	//是否能发私信
	public final static String Url_IS_PRIVATE_MSG = getHost() + "/terminalconnector/struts/BaseBuyer!isMayMessags";
	//好友相互关系
	public final static String Url_USER_RELATION = getHost() + "/terminalconnector/struts/BaseBuyer!userRelation";
	//赠送摇一摇
	public final static String Url_SEND_SHOCK = getHost() + "/terminalconnector/struts/resource!giveCount";
	//个人设置上传
	public final static String Url_USER_DETAIL_SET = getHost() + "/terminalconnector/struts/User!userDetailsSet";
	//详细设置上传
	public final static String Url_USER_OTHER_SET = getHost() + "/terminalconnector/struts/User!userOtherSet";
	//操作条上传
	public final static String URL_USER_OPERATE = getHost() + "/terminalconnector/struts/User!updateBuyerHandle";
	//新收货地址获取
	public final static String URL_RECEIVE_ADDR_GET = getHost()+"/terminalconnector/struts/User!getPostAddrList";
	//新收货地址上传
	public final static String URL_RECEIVE_ADDR_UPLOAD = getHost()+"/terminalconnector/struts/User!uploadAddrList";
	//新收货地址删除
	public final static String URL_RECEIVE_ADDR_DEL = getHost()+"/terminalconnector/struts/User!deleteAddrList";
	//新撒娇对象获取
	public final static String URL_SAJIAO_OBJ_GET = getHost()+"/terminalconnector/struts/User!getCoquetrySetList";
	//新撒娇对象上传
	public final static String URL_SAJIAO_OBJ_UPLOAD = getHost()+"/terminalconnector/struts/User!uploadCoquetrySet";
	//新撒娇对象删除
	public final static String URL_SAJIAO_OBJ_DEL = getHost()+"/terminalconnector/struts/User!deleteCoquetrySet";
	//权重商品上报
	public final static String URL_UPLOAD_WEIGHT_PRODUCT = getHost()+"//terminalconnector/struts/Product!uploadWeightProduct";
	//首页地址缓存信息地址 专用线程号60000
	public final static String URL_MAINPAGE_CACHE_INFO = getHost()+"/terminalconnector/struts/resource!getResource";
	//首页折扣缓存信息地址 专用线程号60001
	public final static String URL_MAINPAGE_DISCOUNT_CACHE_INFO = getHost()+"/terminalconnector/struts/resource!getResourceDiscountList";
	//首页推荐缓存信息地址 专用线程号60002
	public final static String URL_MAINPAGE_RECOMMEND_CACHE_INFO = getHost()+"/terminalconnector/struts/resource!getResourceRecommendList";
	//首页活动缓存信息地址 专用线程号60003
	public final static String URL_MAINPAGE_ACTIVITIES_CACHE_INFO = "";
	//首页晒单缓存信息地址 专用线程号60004
	public final static String URL_MAINPAGE_FORM_CACHE_INFO = "";
	//我的喜欢缓存信息地址 专用线程号60005
	public final static String URL_MAINPAGE_LIKE_CACHE_INFO = "";
	//我看过的缓存信息地址 专用线程号60006
	public final static String URL_MAINPAGE_LOOKED_CACHE_INFO = "";
	
	//获取本地消息数据的xml
	public final static String URL_LOCALMESSAGE = getHost()+"/terminalconnector/struts/Message!getLocalMessage";
	//上报本地消息
	public final static String URL_UPLOADLOCALMESSAGE = getHost()+"/terminalconnector/struts/Message!uploadMessageRead";
	//批量下载单品页
	public final static String URL_BULKDOWNLOAD = getHost()+"/terminalconnector/struts/Product!getProductBatch";
	//自定义列表
	public final static String URL_STYLEPAGE = getHost()+"/terminalconnector/struts/DefinedSet!getStyleList";
	//上传用户自定义列表操作
	public final static String URL_STYLEPAGE_SELECT = getHost()+"/terminalconnector/struts/DefinedSet!addBuyerStyle";
	//删除用户自定义列表操作
	public final static String URL_STYLEPAGE_REMOVESELECT = getHost()+"/terminalconnector/struts/DefinedSet!deleteBuyerStyle";
	//呼叫小编
	public final static String URL_CALLEDITER = getHost()+"/terminalconnector/struts/Message!informAddProMess";
	

	//上传显示商品价格或商品特征
	public final static String URL_PRICE_FEATURE = getHost() + "/terminalconnector/struts/User!proShowSet";
	
	//新浪第三方客户端授权
	public final static String URL_SINA = getHost()+"/Approve/login/redirect";

	

	//专署线程号
	public final static int THREAD_MAINPAGE_CACHE_INFO = 60000;
	public final static int THREAD_MAINPAGE_DISCOUNT_CACHE_INFO = 60001;
	public final static int THREAD_MAINPAGE_RECOMMEND_CACHE_INFO = 60002;
	public final static int THREAD_MAINPAGE_ACTIVITIES_CACHE_INFO = 60003;
	public final static int THREAD_MAINPAGE_FORM_CACHE_INFO = 60004;
	public final static int THREAD_MAINPAGE_LIKE_CACHE_INFO = 60005;
	public final static int THREAD_MAINPAGE_LOOKED_CACHE_INFO = 60006;
	//类型
	public final static int TYPE_MAINPAGE_CACHE_INFO = 60100;
	public final static int TYPE_MAINPAGE_DISCOUNT_CACHE_INFO = 60101;
	public final static int TYPE_MAINPAGE_RECOMMEND_CACHE_INFO = 60102;
	public final static int TYPE_MAINPAGE_ACTIVITIES_CACHE_INFO = 60103;
	public final static int TYPE_MAINPAGE_FORM_CACHE_INFO = 60104;
	public final static int TYPE_MAINPAGE_LIKE_CACHE_INFO = 60105;
	public final static int TYPE_MAINPAGE_LOOKED_CACHE_INFO = 60106;
	//下载缓存的专署线程号
	public final static int THREAD_CACHE = 61000;
}
