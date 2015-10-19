package com.cmmobi.icuiniao.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class FriendList implements Serializable {
	public int totalcount;	//条目总数
	public int pagesize;   //每页的条目数
	public int totalPage;  //总页数
	public ArrayList<Friend> arrUsers = new ArrayList<Friend>();
	public String userpage;	
}
