package com.cmmobi.icuiniao.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cmmobi.icuiniao.entity.Friend;
import com.cmmobi.icuiniao.entity.FriendList;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.UserUtil;



public class DBFriendList {
	private SqlHelper sqlHelper ;
	private Context context;
	private String tableName;
	
	public static final String id = "id";
	public static final String userid = "userid";
	public static final String icon_src = "icon_src";
	public static final String username = "username";	
	public static final String userpage = "userpage";
	public static final String isblack = "isblack";
	public static final String firstletter = "firstletter";
	public static final String allpinyin = "allpinyin";
	
	public final static int idx_id = 0;
	public final static int idx_userid = 1;
	public final static int idx_icon_src = 2;
	public final static int idx_username = 3;
	public final static int idx_userpage = 4;
	public final static int idx_black = 5;
	public final static int idx_firstletter = 6;
	public final static int idx_allpinyin = 7;
	
	public DBFriendList(Context context){
		this.context = context;		
		tableName = "friends_" + UserUtil.userid;		
		sqlHelper = new SqlHelper(context);
//		createBookIntoDB();
	}
	
	/**
	 * �����?id������������ͬ��
	 */
	public void createBookIntoDB() {
		String sql = "create table if not exists " + (tableName) + "(" +
		
		"id INTEGER PRIMARY KEY," +
		
		"userid INTEGER  not null, " +
		
		"icon_src text  not null, " + 	
		
		"username text not null , " +			
		
		"userpage text not null,  " +	
		
		"isblack INTEGER not null," +
		
		"firstletter text not null," +
		
		"allpinyin text not null" +
		
		")";

		
		SQLiteDatabase database= sqlHelper.getWritableDatabase();
		database.execSQL(sql);
		database.close();		
	}
	
	public void reNewTable(){		
		if(tabbleIsExist()){
			SQLiteDatabase database = sqlHelper.getWritableDatabase();
			database.delete(tableName, "", null);
		}		
		createBookIntoDB();
	}
	
	public void update(FriendList friendlist){
		reNewTable();
		insert(friendlist);
	}
	
	/**
	 * ��������б���Ϣ
	 * @param bookInfo
	 */
	public void insert(FriendList friendlist) {		
		SQLiteDatabase database = sqlHelper.getWritableDatabase();		
		ContentValues value = new ContentValues();	
		for(int i=0; i< friendlist.arrUsers.size(); i++){
			Friend friend = friendlist.arrUsers.get(i);
			value.put(id, friend.id);
			value.put(icon_src, friend.icon_src);
			value.put(userid, friend.userid);
			LogPrint.Print("lyb", "save userid = " + friend.userid);
			value.put(username, friend.username);
			value.put(userpage, friendlist.userpage + "oid="+ UserUtil.userid + "&uid="+ friend.userid);
			value.put(isblack, friend.isblack);
			value.put(firstletter, friend.firstletter);
			value.put(allpinyin, friend.allpinyin);
			database.insert(tableName, null, value);
		}		
		database.close();
	}
	
	public void insert(Friend friend) {
		SQLiteDatabase database = sqlHelper.getWritableDatabase();
		ContentValues value = new ContentValues();
		value.put(id, friend.id);
		value.put(icon_src, friend.icon_src);
		value.put(userid, friend.userid);
		LogPrint.Print("lyb", "save userid = " + friend.userid);
		value.put(username, friend.username);
		value.put(userpage, friend.userPage);
		value.put(isblack, friend.isblack);
		value.put(firstletter, friend.firstletter);
		value.put(allpinyin, friend.allpinyin);
		database.insert(tableName, null, value);
		
	}
	
	/**
	 * ���µ����¼
	 * @param uid
	 */
	public int update(Friend friend){
		SQLiteDatabase database = sqlHelper.getWritableDatabase();
		ContentValues value = new ContentValues();		
		value.put(icon_src, friend.icon_src);
		value.put(username, friend.username);
		value.put(userpage, friend.userPage);
		value.put(isblack, friend.isblack);
		value.put(firstletter, friend.firstletter);
		value.put(allpinyin, friend.allpinyin);
		String[] args = {friend.userid + ""};
		int updateLine =  database.update(tableName, value, "userid = ?", args);
		database.close();
		return updateLine;
	}
	
	public int removeByUserid(int userid){
		SQLiteDatabase database = sqlHelper.getWritableDatabase();
		String[] args = {userid+""};
		int deleteLine = database.delete(tableName, "userid = ?", args);
		database.close();
		return deleteLine;
	}
	
	public boolean isUserExist(int uid){
		SQLiteDatabase readDatabase = sqlHelper.getReadableDatabase();
		String sql = "select * from " + tableName + " where userid = " + uid;
		Cursor cursor = readDatabase.rawQuery(sql, null);
		if(cursor.getCount() == 0){
			closeCursor(cursor);
			readDatabase.close();
			return false;
		}
		return true;
	}
	
	/**
	 * 获取好友或黑名单个数
	 * @param isblack
	 * @return
	 */
	public int readFriendCount(int isblack){		
		SQLiteDatabase readDatabase = sqlHelper.getReadableDatabase();
		String sql = "select * from " + tableName + " where isblack = " + isblack;
		Cursor cursor = readDatabase.rawQuery(sql, null);
		final int count = cursor.getCount();
		closeCursor(cursor);
		readDatabase.close();
		return count;
	}
	
	/**
	 * ��ȡ�����б�
	 * @return
	 */
	public FriendList readFriendList(int isblack){
		FriendList friendList = new FriendList();
		SQLiteDatabase readDatabase = sqlHelper.getReadableDatabase();
		String sql = "select * from " + tableName + " where isblack = " + isblack;
		Cursor cursor = readDatabase.rawQuery(sql, null);
		if(cursor.getCount() == 0){
			closeCursor(cursor);
			readDatabase.close();
			return friendList;
		}		
		cursor.moveToFirst();
		while(true){
			String iconSrc = cursor.getString(idx_icon_src);
			String username = cursor.getString(idx_username);
			int userid = cursor.getInt(idx_userid);
			String userpage = cursor.getString(idx_userpage);
			Friend friend = new Friend();
			friend.icon_src = iconSrc;
			friend.username = username;
			friend.userid = userid;
			friend.userPage = userpage;	
			friendList.arrUsers.add(friend);
			boolean isSuccess = cursor.moveToNext();
			if(!isSuccess){
				closeCursor(cursor);
				readDatabase.close();
				break;
			}
		}
		return friendList;
		
	}
	
	/**
	 * �ؼ����������
	 * @param keyWord
	 * @return
	 */
	public FriendList searchFriendByWord(String keyWord, int isBlack){
		SQLiteDatabase readDatabase = sqlHelper.getReadableDatabase();
		String[] args = {"%"+keyWord+"%", "%"+keyWord+"%", "%"+keyWord+"%"};
		String sql = "select * from " + tableName +  " where (username like ? or firstletter like ? or allpinyin like ? ) and isBlack = " + isBlack ;
//		String sql = "select * from " + tableName +  " where  firstletter like ?  and isBlack = " + isBlack ;
		Cursor cursor = readDatabase.rawQuery(sql, args);
		if(cursor.getCount() == 0){
			closeCursor(cursor);
			readDatabase.close();
			return null;
		}
		FriendList friendList = new FriendList();
		cursor.moveToFirst();
		while(true){
			String iconSrc = cursor.getString(idx_icon_src);
			String username = cursor.getString(idx_username);
			int userid = cursor.getInt(idx_userid);
			String userpage = cursor.getString(idx_userpage);
			Friend friend = new Friend();
			friend.icon_src = iconSrc;
			friend.username = username;
			friend.userid = userid;
			friend.userPage = userpage;	
			friendList.arrUsers.add(friend);
			boolean isSuccess = cursor.moveToNext();
			if(!isSuccess){
				closeCursor(cursor);
				readDatabase.close();
				break;
			}
		}
		return friendList;
	}
	
	/**
	 * �ر�cursor
	 * @param cursor
	 */
	private void closeCursor(Cursor cursor){
		if(cursor != null && !cursor.isClosed()){
			cursor.close();
		}
	}
	
	/**  
     * �ж�ĳ�ű��Ƿ����  
     * @param tabName ����  
     * @return  
     */  
	public boolean tabbleIsExist() {
		boolean result = false;
		if (tableName == null) {
			return false;
		}
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = sqlHelper.getReadableDatabase();
			String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='" + tableName.trim() + "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			if(cursor != null){
				cursor.close();
			}
			if(db != null){
				db.close();
			}			
		}
		return result;
	}	
	
	
}
