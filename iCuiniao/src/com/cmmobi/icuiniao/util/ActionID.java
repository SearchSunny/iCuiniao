  /**
 * 
 */
package com.cmmobi.icuiniao.util;

/**
 * @author hw
 *
 *存储全部Action的ID
 */
public class ActionID {
	
	//==================手势广播动作
	//无匹配到任何手势
	public final static String ACTION_BROADCAST_GESTURE_NO_RESULT = "com.cmmobi.broadcast.GestureNoResult";
	//匹配到对钩手势
	public final static String ACTION_BROADCAST_GESTURE_RIGHT = "com.cmmobi.broadcast.GestureRight";
	//匹配到圆圈手势
	public final static String ACTION_BROADCAST_GESTURE_CIRCLE = "com.cmmobi.broadcast.GestureCircle";
	//匹配到三角手势
	public final static String ACTION_BROADCAST_GESTURE_TRIANGLE = "com.cmmobi.broadcast.GestureTriangle";
	
	//==================MediaPlayer手势广播动作
	//视频播放完成
	public final static String ACTION_BROADCAST_MEDIAPLAYER_FINISH = "com.cmmobi.broadcast.MediaPlayerFinish";
	//关闭手势面板
	public final static String ACTION_BROADCAST_MEDIAPLAYER_GESTURE_CLOSE = "com.cmmobi.broadcast.MediaPlayerGestureClose";
	//无匹配到任何手势
	public final static String ACTION_BROADCAST_MEDIAPLAYER_GESTURE_NO_RESULT = "com.cmmobi.broadcast.MediaPlayerGestureNoResult";
	//匹配到对钩手势
	public final static String ACTION_BROADCAST_MEDIAPLAYER_GESTURE_RIGHT = "com.cmmobi.broadcast.MediaPlayerGestureRight";
	//匹配到圆圈手势
	public final static String ACTION_BROADCAST_MEDIAPLAYER_GESTURE_CIRCLE = "com.cmmobi.broadcast.MediaPlayerGestureCircle";
	//匹配到三角手势
	public final static String ACTION_BROADCAST_MEDIAPLAYER_GESTURE_TRIANGLE = "com.cmmobi.broadcast.MediaPlayerGestureTriangle";
	//=================消息轮询结果动作
	public final static String ACTION_BROADCAST_MESSAGE_CHICK = "com.cmmobi.broadcast.messagechick";
	//下载迟完成
	public final static String ACTION_BROADCAST_DOWNLOADOVER = "com.cmmobi.broadcast.downloadover";
	
	//清空单品页适配器数据
	public final static String ACTION_BROADCAST_MYPAGE_CLEAR = "com.cmmobi.broadcast.clear";
	//apn改变
	public final static String ACTION_BROADCAST_APN_CHANGE = "com.cmmobi.broadcast.apnchange";
	
	//==================im通知
	//服务器回应客户端允许建立连接成功，tag:3
	public final static String ACTION_BROADCAST_CONNECT_SUCCESS_RESPONSE = "com.cmmobi.broadcast.connect.success.response";
	//服务器回应客户端拒绝建立连接，tag:4
	public final static String ACTION_BROADCAST_CONNECT_REFUSE_RESPONSE = "com.cmmobi.broadcast.connect.refuse.response";
	//服务器回应客户端建立连接失败，tag:5
	public final static String ACTION_BROADCAST_CONNECT_FAIL_RESPONSE = "com.cmmobi.broadcast.connect.fail.response";
	//服务器回应心跳请求，tag:202
	public final static String ACTION_BROADCAST_HEART_RESPONSE = "com.cmmobi.broadcast.heart.response";
	//io出现异常
	public final static String ACTION_BROADCAST_SOCKET_IOERROR = "com.cmmobi.broadcast.socket.ioerror";
	//发送消息
	public final static String ACTION_BROADCAST_REQUST_SEND_MESSAGE = "com.cmmobi.broadcast.requst.sendmessage";
	//发送私信
	public final static String ACTION_BROADCAST_REQUST_SEND_PRIVATEMESSAGE = "com.cmmobi.broadcast.requst.send.private.message";
	//发送成功（服务器反馈206）
	public final static String ACTION_BROADCAST_RESPONSE_SENDMESSAGE_SUCCESS = "com.cmmobi.broadcast.response.sendmessage.success";
	//发送失败
	public final static String ACTION_BROADCAST_RESPONSE_SENDMESSAGE_FAIL = "com.cmmobi.broadcast.response.sendmessage.fail";
	//接收到一条消息
	public final static String ACTION_BROADCAST_RESPONSE_RECEIVE_MESSAGE = "com.cmmobi.broadcast.response.receive.message";
	//接收到一条系统广播
	public final static String ACTION_BROADCAST_RESPONSE_RECEIVE_SYSTEMMESSAGE = "com.cmmobi.broadcast.response.receive.systemmessage";
	//接收到赠送好友摇一下机会
	public final static String ACTION_BROADCAST_RESPONSE_RECEIVE_GIVEROCKMESSAGE = "com.cmmobi.broadcast.response.receive.giverockmessage";
	//接收到一条私信
	public final static String ACTION_BROADCAST_RESPONSE_RECEIVE_PRIVATEMESSAGE = "com.cmmobi.broadcast.response.receive.privatemessage";
	//发送建立好友关系消息
	public final static String ACTION_BROADCAST_REQUST_SEND_FRIENDMESSAGE = "com.cmmobi.broadcast.requst.send.friend.message";
	//接收到一条建立好友关系消息
	public final static String ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE = "com.cmmobi.broadcast.response.receive.friendmessage";
	//发送确认建立好友关系的消息
	public final static String ACTION_BROADCAST_REQUST_SEND_FRIENDMESSAGE_SUCCESS = "com.cmmobi.broadcast.requst.send.friend.message.success";
	//接收到一条确认建立好友关系的消息
	public final static String ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE_SUCCESS = "com.cmmobi.broadcast.response.receive.friendmessage.success";
	//关闭获取评论的handler
	public final static String ACTION_BROADCAST_STOP_COMMENT_CONNECT = "com.cmmobi.broadcast.stop.comment";
	//开启获取评论的handler delay
	public final static String ACTION_BROADCAST_START_COMMENT_CONNECT = "com.cmmobi.broadcast.start.comment";
	//更新消息评论个数
	public final static String ACTION_BROADCAST_COMMENT_COUNT = "com.cmmobi.broadcast.COMMENT_COUNT";
	//断开连接重新连接（用于用户切换等时候）
	public final static String ACTION_BROADCAST_CUTCONNECT_AND_RECONNECT = "com.cmmobi.broadcast.cutconnect.and.reconnect";
	//摇一摇倒计时改变
	public final static String ACTION_BROADCAST_ROCK_TIME_CHANGE = "com.cmmobi.broadcast.rock.time.change";
	//用户登出
	public final static String ACTION_BROADCAST_LOGOUT = "com.cmmobi.broadcast.logout";
	//异地登录通知
	public final static String ACTION_BROADCAST_REMOTE_LOGIN = "com.cmmobi.broadcast.REMOTE_LOGIN";
	//发送加好友结果通知
	public final static String ACTION_BROADCAST_SEND_ADDFRIEND_FEEDBACK = "com.cmmobi.broadcast.send.addFriend_feedback";
	//收到加好友结果通知
	public final static String ACTION_BROADCAST_RESPONSE_ADDFRIEND_FEEDBACK = "com.cmmobi.broadcast.response.addFriend_feedback";
	//刷新好友列表
	public final static String ACTION_BROADCAST_REFRESH_FRIEND_LIST = "com.cmmobi.broadcast.refresh.friendList";
}
