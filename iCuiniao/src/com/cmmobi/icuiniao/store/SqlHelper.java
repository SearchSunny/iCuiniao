package com.cmmobi.icuiniao.store;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cmmobi.icuiniao.util.UserUtil;


public class SqlHelper extends SQLiteOpenHelper{	
	private static final int DATABASE_VERSION = 1;


	public SqlHelper(Context context) {		
		super(context, "DBFriends_" + UserUtil.userid + ".db", null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS notes");
		onCreate(db);
	}

}

