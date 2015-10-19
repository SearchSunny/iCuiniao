/**
 * 
 */
package com.cmmobi.icuiniao.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author hw
 *离线日志数据拼装
 */
public class OfflineLog {

	private static String Dir = CommonUtil.getExtendsCardPath()+"iCuiniao/offline/";
	private static String fileName = "offline_";
	private static RandomAccessFile rAccessFile;
	private static String mergerFileDir = Dir+"offline_merger";
	
	private final static int BOM = -2;
	private final static short END = -10;
	private final static short TAG_MAINPAGE_FLING = 			151;
	private final static short TAG_MYPAGE_FLING = 				152;
	private final static short TAG_GESTURE_RIGHT = 				155;
	private final static short TAG_GESTRUE_CIRCLE = 			156;
	private final static short TAG_GESTRUE_TRIANGLE = 			157;
	private final static short TAG_MAINMENU = 					158;
	private final static short TAG_MAINMENU_MAINPAGE = 			159;
	private final static short TAG_MAINMENU_SETTING = 			160;
	private final static short TAG_MAINMENU_ABOUT = 			161;
	private final static short TAG_MAINMENU_SOFTUPDATE = 		162;
	private final static short TAG_MAINMENU_CLEARCACHE = 		163;
	private final static short TAG_SHARE_SMS = 					164;
	private final static short TAG_SHARE_EMAIL = 				165;
	private final static short TAG_MAINMENU_HELP = 				166;
	private final static short TAG_MAINMENU_MESSAGEBUTTON = 	167;
	private final static short TAG_EXIT = 						168;
	private final static short TAG_MESSAGEMANAGER = 			169;
	private final static short TAG_MESSAGE = 					170;
	private final static short TAG_MEDIAPLAYER = 				171;
	private final static short TAG_MAINPAGE	=					172;
	private final static short TAG_MYPAGE = 					173;
	//2013.5.21新增 hw
	private final static short TAG_SHAREMENU_MYPAGE = 			174;
	private final static short TAG_GESTURE = 					175;
	private final static short TAG_FRIEND = 					176;
	private final static short TAG_BIND = 						177;
	private final static short TAG_SHARE_QQ = 					178;
	private final static short TAG_SHARE_WX = 					179;
	private final static short TAG_SAJIAO = 					180;
	private final static short TAG_DELETE_MESSAGE = 			181;
	private final static short TAG_EDIT_PASSWORD = 				182;
	private final static short TAG_EDIT_ICON = 					183;
	private final static short TAG_SETTING_MORE = 				184;//个人详细设置
	private final static short TAG_SETTING_OTHER = 				185;//其他信息界面
	private final static short TAG_ADDRMANAGER = 				186;
	private final static short TAG_FEEDBACK = 					187;
	private final static short TAG_SOFTUPDATE = 				188;
	private final static short TAG_LOGOUT = 					189;
	private final static short TAG_SHARE_WEIBO = 				191;
	
	
	private final static int MAX_FILELENGTH = 2*1024;//2k
	private static int time;
	private static boolean isInUploading;//是否正在上传
	
	//创建离线日志文件,0:sd卡不存在或数据异常，1：新创建的文件，2：有数据的文件
	public static int creatFile(){
		int result = 0;
		if(isInUploading){
			LogPrint.Print("offline","正在上传中，放弃此次记录");
			return 0;
		}
		//sd卡不存在则退出函数
		if(!CommonUtil.getSDCardState()){
			return result;
		}
		
		try {
			time = (int)(UserUtil.sysTime/1000);
			File file = new File(Dir);
			if(!file.exists()){
				file.mkdirs();
			}
			//获取文件夹中所有的日志文件
			File[] files = file.listFiles();
			if(files == null){//文件夹中没有文件
				initAccessFile();
				File file1 = new File(Dir+fileName+"0");
				if(!file1.exists()){
					file1.createNewFile();
				}
				rAccessFile = new RandomAccessFile(file1, "rw");
				//写入bom头
				rAccessFile.writeInt(BOM);
				//写入用户id
				rAccessFile.writeInt(UserUtil.userid);
				//写入系统时间
				rAccessFile.writeInt(time);
				LogPrint.Print("offline","创建文件："+fileName+"0");
				LogPrint.Print("offline","写入bom："+BOM);
				LogPrint.Print("offline","写入用户id："+UserUtil.userid);
				LogPrint.Print("offline","写入系统时间："+time);
				result = 1;
			}else {//文件夹中有文件
				boolean isFind = false;//是否找到小于2k的可用文件
				for (int i = 0; i < files.length; i++) {
					//小于2k才继续
					if(files[i].length() < MAX_FILELENGTH){
						isFind = true;
						if(rAccessFile == null){
							rAccessFile = new RandomAccessFile(new File(Dir+fileName+i), "rw");
						}
						LogPrint.Print("offline","续写文件："+fileName+i);
						result = 2;
						break;
					}
				}
				if(isFind == false){//没有找到小于2k的可用文件
					initAccessFile();
					File file1 = new File(Dir+fileName+files.length);
					if(!file1.exists()){
						file1.createNewFile();
					}
					rAccessFile = new RandomAccessFile(file1, "rw");
					//写入bom头
					rAccessFile.writeInt(BOM);
					//写入用户id
					rAccessFile.writeInt(UserUtil.userid);
					//写入系统时间
					rAccessFile.writeInt(time);
					LogPrint.Print("offline","创建文件："+fileName+files.length);
					LogPrint.Print("offline","写入bom："+BOM);
					LogPrint.Print("offline","写入用户id："+UserUtil.userid);
					LogPrint.Print("offline","写入系统时间："+time);
					result = 1;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			result = 0;
		}
		return result;
	}
	
	public static void initAccessFile(){
		try {
			if(rAccessFile != null){
				rAccessFile.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			rAccessFile = null;
		}
	}
	
	/**
	 * 写入用户id,当系统时间改变的时候写入
	 * */
	public static void writeUserId(){
		try {
			if(creatFile() == 2){
				rAccessFile.seek(rAccessFile.length());
				//写入bom头
				rAccessFile.writeInt(BOM);
				//写入用户id
				rAccessFile.writeInt(UserUtil.userid);
				//写入系统时间
				rAccessFile.writeInt(time);
				LogPrint.Print("offline","写入用户信息：");
				LogPrint.Print("offline","写入bom："+BOM);
				LogPrint.Print("offline","写入用户id："+UserUtil.userid);
				LogPrint.Print("offline","写入系统时间："+time);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入商品列表滑动动作</br>
	 * 
	 * */
	public static void writeMainPageFling(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_MAINPAGE_FLING);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入商品列表滑动动作：");
				LogPrint.Print("offline","写入标识："+TAG_MAINPAGE_FLING);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 *写入单品页滑动动作</br>
	 *
	 *@param commodityid 商品id
	 * */
	public static void writeMyPageFling(int commodityid){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_MYPAGE_FLING);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				//写入商品id
				rAccessFile.writeInt(commodityid);
				LogPrint.Print("offline","写入单品页滑动动作：");
				LogPrint.Print("offline","写入标识："+TAG_MYPAGE_FLING);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入商品id："+commodityid);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 *写入手势画对勾动作</br>
	 *
	 *@param result 结果 0：false，1：true
	 * */
	public static void writeGestureRight(byte result){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_GESTURE_RIGHT);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				//写入结果
				rAccessFile.writeByte(result);
				LogPrint.Print("offline","写入手势画对勾动作：");
				LogPrint.Print("offline","写入标识："+TAG_GESTURE_RIGHT);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入结果："+result);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 *写入手势画圆圈动作</br>
	 *
	 *@param result 结果 0：false，1：true
	 * */
	public static void writeGestureCircle(byte result){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_GESTRUE_CIRCLE);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				//写入结果
				rAccessFile.writeByte(result);
				LogPrint.Print("offline","写入手势画圆圈动作：");
				LogPrint.Print("offline","写入标识："+TAG_GESTRUE_CIRCLE);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入结果："+result);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 *写入手势画三角动作</br>
	 *
	 *@param result 结果 0：false，1：true
	 * */
	public static void writeGestureTriangle(byte result){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_GESTRUE_TRIANGLE);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				//写入结果
				rAccessFile.writeByte(result);
				LogPrint.Print("offline","写入手势画三角动作：");
				LogPrint.Print("offline","写入标识："+TAG_GESTRUE_TRIANGLE);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入结果："+result);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入功能菜单动作</br>
	 * 
	 * */
	public static void writeMainMenu(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_MAINMENU);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入功能菜单动作：");
				LogPrint.Print("offline","写入标识："+TAG_MAINMENU);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入我的首页动作</br>
	 *
	 * */
	public static void writeMainMenu_MainPage(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_MAINMENU_MAINPAGE);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入我的首页动作：");
				LogPrint.Print("offline","写入标识："+TAG_MAINMENU_MAINPAGE);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入我的设置动作</br>
	 * 
	 * */
	public static void writeMainMenu_Setting(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_MAINMENU_SETTING);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入我的设置动作：");
				LogPrint.Print("offline","写入标识："+TAG_MAINMENU_SETTING);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入关于动作</br>
	 * 
	 * */
	public static void writeMainMenu_About(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_MAINMENU_ABOUT);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入关于动作：");
				LogPrint.Print("offline","写入标识："+TAG_MAINMENU_ABOUT);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入软件更新动作</br>
	 * 
	 * */
	public static void writeMainMenu_Softupdate(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_MAINMENU_SOFTUPDATE);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入软件更新动作：");
				LogPrint.Print("offline","写入标识："+TAG_MAINMENU_SOFTUPDATE);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入清除缓存动作</br>
	 * 
	 * */
	public static void writeMainMenu_ClearCache(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_MAINMENU_CLEARCACHE);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入清除缓存动作：");
				LogPrint.Print("offline","写入标识："+TAG_MAINMENU_CLEARCACHE);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 *写入短信分享动作</br>
	 *
	 *@param commodityid 商品id
	 * */
	public static void writeShareSms(int commodityid){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_SHARE_SMS);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				//写入商品id
				rAccessFile.writeInt(commodityid);
				LogPrint.Print("offline","写入短信分享动作：");
				LogPrint.Print("offline","写入标识："+TAG_SHARE_SMS);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入商品id："+commodityid);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 *写入邮件分享动作</br>
	 *
	 *@param commodityid 商品id
	 * */
	public static void writeShareEmail(int commodityid){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_SHARE_EMAIL);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				//写入商品id
				rAccessFile.writeInt(commodityid);
				LogPrint.Print("offline","写入邮件分享动作：");
				LogPrint.Print("offline","写入标识："+TAG_SHARE_EMAIL);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入商品id："+commodityid);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 *写入查看帮助动作</br>
	 *
	 *@param result 结果 0：false，1：true
	 * */
	public static void writeMainMenu_Help(byte result){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_MAINMENU_HELP);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				//写入结果
				rAccessFile.writeByte(result);
				LogPrint.Print("offline","写入查看帮助动作：");
				LogPrint.Print("offline","写入标识："+TAG_MAINMENU_HELP);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入结果："+result);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 *写入通知提醒动作</br>
	 *
	 *@param result 结果 0：false，1：true
	 * */
	public static void writeMainMenu_MessageButton(byte result){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_MAINMENU_MESSAGEBUTTON);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				//写入结果
				rAccessFile.writeByte(result);
				LogPrint.Print("offline","写入通知提醒动作：");
				LogPrint.Print("offline","写入标识："+TAG_MAINMENU_MESSAGEBUTTON);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入结果："+result);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入退出软件动作</br>
	 *
	 * */
	public static void writeExit(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_EXIT);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				rAccessFile.close();
				rAccessFile = null;
				LogPrint.Print("offline","写入退出软件动作：");
				LogPrint.Print("offline","写入标识："+TAG_EXIT);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入消息管理界面动作</br>
	 * 
	 * */
	public static void writeMessageManager(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_MESSAGEMANAGER);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入消息管理界面动作：");
				LogPrint.Print("offline","写入标识："+TAG_MESSAGEMANAGER);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入消息聊天界面动作</br>
	 * 
	 * */
	public static void writeMessage(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_MESSAGE);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入消息聊天界面动作：");
				LogPrint.Print("offline","写入标识："+TAG_MESSAGE);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 *写入视频播放动作</br>
	 *
	 *@param commodityid 商品id
	 * */
	public static void writeMediaPlayer(int commodityid){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_MEDIAPLAYER);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				//写入商品id
				rAccessFile.writeInt(commodityid);
				LogPrint.Print("offline","写入视频播放动作：");
				LogPrint.Print("offline","写入标识："+TAG_MEDIAPLAYER);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入商品id："+commodityid);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入商品列表动作</br>
	 * 
	 * */
	public static void writeMainPage(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_MAINPAGE);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入商品列表动作：");
				LogPrint.Print("offline","写入标识："+TAG_MAINPAGE);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 *写入单品页动作</br>
	 *
	 *@param commodityid 商品id
	 * */
	public static void writeMyPage(int commodityid){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_MYPAGE);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				//写入商品id
				rAccessFile.writeInt(commodityid);
				LogPrint.Print("offline","写入单品页动作：");
				LogPrint.Print("offline","写入标识："+TAG_MYPAGE);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入商品id："+commodityid);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 *写入单品页分享菜单动作</br>
	 *
	 *@param commodityid 商品id
	 * */
	public static void writeShareMenuMyPage(int commodityid){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_SHAREMENU_MYPAGE);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				//写入商品id
				rAccessFile.writeInt(commodityid);
				LogPrint.Print("offline","写入单品页分享菜单动作：");
				LogPrint.Print("offline","写入标识："+TAG_SHAREMENU_MYPAGE);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入商品id："+commodityid);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 *写入手势界面动作</br>
	 *
	 *@param commodityid 商品id
	 * */
	public static void writeGesture(int commodityid){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_GESTURE);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				//写入商品id
				rAccessFile.writeInt(commodityid);
				LogPrint.Print("offline","写入手势界面动作：");
				LogPrint.Print("offline","写入标识："+TAG_GESTURE);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入商品id："+commodityid);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入我的好友界面动作</br>
	 * 
	 * */
	public static void writeFriend(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_FRIEND);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入我的好友界面动作：");
				LogPrint.Print("offline","写入标识："+TAG_FRIEND);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入绑定界面动作</br>
	 * 
	 * */
	public static void writeBind(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_BIND);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入绑定界面动作：");
				LogPrint.Print("offline","写入标识："+TAG_BIND);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 *写入腾讯空间分享动作</br>
	 *
	 *@param commodityid 商品id
	 * */
	public static void writeShareQQ(int commodityid){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_SHARE_QQ);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				//写入商品id
				rAccessFile.writeInt(commodityid);
				LogPrint.Print("offline","写入腾讯空间分享动作：");
				LogPrint.Print("offline","写入标识："+TAG_SHARE_QQ);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入商品id："+commodityid);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 *写入微信分享动作</br>
	 *
	 *@param commodityid 商品id
	 * */
	public static void writeShareWX(int commodityid){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_SHARE_WX);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				//写入商品id
				rAccessFile.writeInt(commodityid);
				LogPrint.Print("offline","写入微信分享动作：");
				LogPrint.Print("offline","写入标识："+TAG_SHARE_WX);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入商品id："+commodityid);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 *写入撒娇界面动作</br>
	 *
	 *@param commodityid 商品id
	 * */
	public static void writeSajiao(int commodityid){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_SAJIAO);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				//写入商品id
				rAccessFile.writeInt(commodityid);
				LogPrint.Print("offline","写入撒娇界面动作：");
				LogPrint.Print("offline","写入标识："+TAG_SAJIAO);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入商品id："+commodityid);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入删除消息动作</br>
	 * 
	 * */
	public static void writeDeleteMessage(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_DELETE_MESSAGE);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入删除消息动作：");
				LogPrint.Print("offline","写入标识："+TAG_DELETE_MESSAGE);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入修改账户密码动作</br>
	 * 
	 * */
	public static void writeEditPassword(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_EDIT_PASSWORD);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入修改账户密码动作：");
				LogPrint.Print("offline","写入标识："+TAG_EDIT_PASSWORD);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入修改头像动作</br>
	 * 
	 * */
	public static void writeEditIcon(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_EDIT_ICON);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入修改头像动作：");
				LogPrint.Print("offline","写入标识："+TAG_EDIT_ICON);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入详细个人信息界面动作</br>
	 * 
	 * */
	public static void writeSettingMore(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_SETTING_MORE);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入详细个人信息界面动作：");
				LogPrint.Print("offline","写入标识："+TAG_SETTING_MORE);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入其他信息界面动作</br>
	 * 
	 * */
	public static void writeSettingOther(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_SETTING_OTHER);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入其他信息界面动作：");
				LogPrint.Print("offline","写入标识："+TAG_SETTING_OTHER);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入收货地址管理界面动作</br>
	 * 
	 * */
	public static void writeAddrManager(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_ADDRMANAGER);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入收货地址管理界面动作：");
				LogPrint.Print("offline","写入标识："+TAG_ADDRMANAGER);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入用户反馈界面动作</br>
	 * 
	 * */
	public static void writeFeedBack(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_FEEDBACK);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入用户反馈界面动作：");
				LogPrint.Print("offline","写入标识："+TAG_FEEDBACK);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入软件更新界面动作</br>
	 * 
	 * */
	public static void writeSoftUpdate(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_SOFTUPDATE);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入软件更新界面动作：");
				LogPrint.Print("offline","写入标识："+TAG_SOFTUPDATE);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 写入退出帐号界面动作</br>
	 * 
	 * */
	public static void writeLogout(){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_LOGOUT);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入退出帐号界面动作：");
				LogPrint.Print("offline","写入标识："+TAG_LOGOUT);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 *写入新浪微博分享动作</br>
	 *
	 *@param commodityid 商品id
	 * */
	public static void writeShareWeibo(int commodityid){
		try {
			if(creatFile() > 0){
				rAccessFile.seek(rAccessFile.length());
				//写入标识id
				rAccessFile.writeShort(TAG_SHARE_WEIBO);
				//写入时间差
				rAccessFile.writeInt(OfflineTimeService.getTimeDef());
				//写入商品id
				rAccessFile.writeInt(commodityid);
				LogPrint.Print("offline","写入新浪微博分享动作：");
				LogPrint.Print("offline","写入标识："+TAG_SHARE_WEIBO);
				LogPrint.Print("offline","写入时间差："+OfflineTimeService.getTimeDef());
				LogPrint.Print("offline","写入商品id："+commodityid);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	//当计数器到1分钟的时候调用
	public static void upload(final Context context){
		try {
			//sd卡不存在则退出函数
			if(!CommonUtil.getSDCardState()){
				return;
			}
			if(rAccessFile == null){
				return;
			}
			if(CommonUtil.isNetWorkOpen(context)){
				//在wifi和3g下可以上传
				if(CommonUtil.getApnType(context).toLowerCase().indexOf("wifi") >= 0||
						CommonUtil.getApnType(context).toLowerCase().indexOf("3g") >= 0){
					
					new Thread(){
						public void run(){
							//先判断当天的强制上传是否上传过
							if(!getForceUpload(context)){//没有上传过则合并文件，上传
								LogPrint.Print("offline","强制上传开始");
								saveForceUpload(context, true);
								isInUploading = true;
								try {
									rAccessFile.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								rAccessFile = null;
								//文件合并
								mergerFile();
								//上传文件
								uploadFile();
							}else{//上传过了需要判断文件是否大于2k
								File file = new File(Dir);
								if(file.exists()){
									//获取文件夹中所有的日志文件
									File[] files = file.listFiles();
									if(files != null&&files.length > 1){
										LogPrint.Print("offline","上传开始");
										isInUploading = true;
										try {
											rAccessFile.close();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										rAccessFile = null;
										//文件合并
										mergerFile();
										//上传文件
										uploadFile();
									}
								}
							}
						}
					}.start();
					
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private static void uploadFile(){
		String boundary = "*****";
		File mergerFile = null;
		try {
			mergerFile = new File(mergerFileDir);
			if(mergerFile.exists()){
				URL url = new URL(URLUtil.URL_OFFLINE);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				/* 允许Input、Output，不使用Cache */
				con.setDoInput(true);
				con.setDoOutput(true);
				con.setUseCaches(false);
				con.setConnectTimeout(10000);
				/* 设置传送的method=POST */
				con.setRequestMethod("POST");
				/* setRequestProperty */
				con.setRequestProperty("Connection", "Keep-Alive");
				con.setRequestProperty("Charset", "UTF-8");
				con.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				con.setRequestProperty("Content-Length", ""+mergerFile.length());
				LogPrint.Print("offline","Content-Length = "+mergerFile.length());
				
				FileInputStream fis = new FileInputStream(mergerFile);
				/* 设置DataOutputStream */
				DataOutputStream ds = new DataOutputStream(con.getOutputStream());
				/* 设置每次写入1024bytes */
				int bufferSize = 1024;
				byte[] buffer = new byte[bufferSize];
				int length = -1;
				int sum = 0;
				/* 从文件读取数据至缓冲区 */
				while ((length = fis.read(buffer)) != -1) {
					/* 将资料写入DataOutputStream中 */
					sum += length;
					ds.write(buffer, 0, length);
				}
				LogPrint.Print("offline","上传总数据："+sum);
				
				/* close streams */
				fis.close();
				ds.flush();
				/* 取得Response内容 */
				InputStream is = con.getInputStream();
				int ch;
				StringBuffer b = new StringBuffer();
				while ((ch = is.read()) != -1) {
					b.append((char) ch);
				}
				/* 将Response显示于Dialog */
				String resultString = b.toString().trim();
				LogPrint.Print("offline","result = "+resultString);
				if(resultString != null&&resultString.indexOf("ok") >= 0){//上传成功
					CommonUtil.deleteAll(new File(Dir));//删除离线文件
					isInUploading = false;
				}else{
					mergerFile.delete();
					isInUploading = false;
				}
				/* 关闭DataOutputStream */
				ds.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			if(mergerFile != null&&mergerFile.exists()){
				mergerFile.delete();
			}
			isInUploading = false;
		}
	}
	
	//当天是否强制上传过了
    public static void saveForceUpload(Context context, boolean bUpload){
    	try {
			SharedPreferences sharedPreferences = context.getSharedPreferences("com.cmmobi.icuiniao.offline", Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean("bUpload", bUpload);
			editor.commit();
		} catch (Exception e) {
		}
    }
    
    public static boolean getForceUpload(Context context){
    	try {
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.cmmobi.icuiniao.offline", Activity.MODE_PRIVATE);
    		return sharedPreferences.getBoolean("bUpload", false);
		} catch (Exception e) {
		}
		return false;
    }
    
    //文件合并
    private static boolean mergerFile(){
    	boolean result = false;
    	try {
    		File file = new File(Dir);
			if(file.exists()){
				//获取文件夹中所有的日志文件
				File[] files = file.listFiles();
				if(files != null&&files.length > 0){
					File mergerFile = new File(mergerFileDir);
					if(mergerFile.exists()){
						mergerFile.delete();
					}
					mergerFile.createNewFile();
					FileOutputStream mergerOutputStream = new FileOutputStream(mergerFile);
					DataOutputStream dos =new DataOutputStream(mergerOutputStream);
					byte[] buffer = new byte[2048];
					
					for (int i = 0; i < files.length; i++) {
						int len = -1;
						FileInputStream fis = new FileInputStream(files[i]);
						while((len = fis.read(buffer)) != -1){
							dos.write(buffer, 0, len);
						}
						fis.close();
						LogPrint.Print("offline","合并完成第"+i+"个文件");
					}
					dos.writeShort(END);//加入结束符号
					dos.close();
					mergerOutputStream.close();
					result = true;
					LogPrint.Print("offline","文件全部合并完成");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
    }
	
}
