/**
 * 
 */
package com.cmmobi.icuiniao.util;

import android.content.Context;
/**
 * @author hw
 *
 */
public class UserUtil {

	//用户id
	public static int userid;
	//用户昵称
	public static String username;
	//登陆方式
	public static int logintype;
	//性别 0:男,1:女
	public static int gender;
	//游客id
	public static String vid;
	//uid,用于他人喜欢返回时的记录
	public static int uid;
	//系统时间
	public static long sysTime;
	//获取缓存的次数
	public static int callOnNum;
	//系统date时间
	public static String sysDate;
	//是否是新登陆或退出
	public static boolean isNewLoginOrExit;
	//用户状态,0为未注册（游客）,1为已注册（正式用户）,2为无效,3为登出
	public static int userState;
	//是否登出了，用于获取缓存时的参数
	public static boolean isLogout;
	//上一次的联网方式
	public static String preNetApn;	
	//是否接到了异地登录的通知
	public static boolean isRemoteLogin = false;
	//是否显示价格
	public static boolean isShowPrice = false;
	//是否显示商品特色
	public static boolean isShowFeature = true;
	//价格或特色的设置被改变了
	public static boolean isPriceOrFeatureChange;
	//是否呼叫过小编了
	public static boolean isCallEditerOver;
	//单品页总数量
	public static int cidMaxNum;
	
	public static String Encryption(String str){
		StringBuffer sb = new StringBuffer();
		String[] tmps = new String[str.length()];
		String tmp;
		for(int i = 0;i < str.length();i ++){
			tmps[i] = str.substring(i, i+1);
		}
		for(int i = 0;i < tmps.length;){
			if(i == tmps.length -1)break;
			tmp = tmps[i];
			tmps[i] = tmps[i+1];
			tmps[i+1] = tmp;
			i+=2;
		}
		for(int i = 0;i < tmps.length;i++){
			sb.append(tmps[i]);
		}
		return sb.toString();
	}
	
	public static String Decrypt(String str){
		return Encryption(str);
	}
	
	//登出时清除数据
	public static void clearSharePreference(Context context, String toast){
		CommonUtil.saveUserState(context, UserUtil.userState);
		CommonUtil.saveAddrManager(context, "");
		CommonUtil.saveCurAddr(context, -1);
		
		//新收货地址和撒娇对象清空
		CommonUtil.saveReceiveAddrMgr(context, "");
		CommonUtil.saveCurReceiveAddr(context, 0);
		CommonUtil.saveSajiaoObjMgr(context, "");
		CommonUtil.saveCurSajiaoObj(context, 0);
		
		CommonUtil.ShowToast(context, toast);
		//清空设置信息
		//清空通知提醒
		CommonUtil.saveMessageReceiverState(context, true);
		//清空所在地
		CommonUtil.saveArea(context, "");
		//清空好友可见
		CommonUtil.saveLikeFriendState(context, false);
		//清空分享公开
		CommonUtil.saveShareFriendState(context, false);
		//清空公开评论
		CommonUtil.saveBaskFriendState(context, false);
		//清空允许任何人加好友
		CommonUtil.saveAllAddFriendState(context, false);
		//清空真实姓名
		CommonUtil.saveRealName(context, "");
		//清空生日
		CommonUtil.saveBirthday(context, "");
		//清空兴趣
		CommonUtil.saveInterest(context, "");
		//清空职业
		CommonUtil.saveOccuption(context, "");
		//清空月收入
		CommonUtil.saveMoney(context, "");
		//清空教育程度
		CommonUtil.saveEducation(context, "");
		//清空婚姻状况
		CommonUtil.saveMarry(context, "");
		
		CommonUtil.saveCoqutryButtonState(context, false);
		//清空操作方式
		CommonUtil.saveOperateHand(context, "0");
		CommonUtil.saveOperateMode(context, "0");
		//清空商品价格和特征的显示设置
		CommonUtil.savePriceShow(context, false);
		CommonUtil.saveFeatureShow(context, true);
		UserUtil.isShowFeature = true;
		UserUtil.isShowPrice = false;
		CommonUtil.savePriceShow(context, UserUtil.isShowPrice);
		CommonUtil.savePriceShow(context, UserUtil.isShowFeature);
	}
}
