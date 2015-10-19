package com.cmmobi.icuiniao.entity;

import java.io.Serializable;

public class Friend implements Serializable{
	
	public int id; //服务器主键
	public String icon_src = "";
	public String username = "";
	public int userid;
	public String userPage ="";
	public String firstletter = "";
	public String allpinyin = "";
	//是否是黑名单  0是黑名单 1好友 2.陌生人。3.自己 （表示他在我的黑名单或者好友列表里）
	public int isblack;  
	
	public static int BOOL_BLACK = 0;  //黑名单
	public static int BOOL_FRIEND = 1; //好友
	public static int BOOL_STRANGER =2; //陌生人
	public static int BOOL_SELF = 3; //陌生人
	
	
}
