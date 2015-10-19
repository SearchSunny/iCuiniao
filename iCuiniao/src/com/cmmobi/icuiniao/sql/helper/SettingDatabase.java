package com.cmmobi.icuiniao.sql.helper;

import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.util.LogPrint;

public class SettingDatabase extends SQLiteOpenHelper {

	private final static String DB_NAME = "setting.db";
	public final static int DB_VERSION = 1;
	public final static String TABLE_NAME = "t_regional";
	private Context context = null;
	private final String TAG = "SettingDatabase";
	private RegionalsDomXml dom = null;
	
	public SettingDatabase(Context context){
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}
	
	public SettingDatabase(Context context, String name, CursorFactory factory,int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable(db);
		InputStream is = context.getResources().openRawResource(R.raw.db_city);
		//解析XML文件
		dom = new RegionalsDomXml();
		try {
			List<Regional> regionals =	dom.getRegionals(is);
			if(regionals!=null && regionals.size()>0){
				for(int i=0;i<regionals.size();i++){
					Regional regional = regionals.get(i);
					insertRegional(regional,db);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		//插入到数据库
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = " drop table " + TABLE_NAME;
		db.execSQL(sql);
	}
	
	/**
	 * 创建表
	 * @param db
	 */
	private void createTable(SQLiteDatabase db){
		String sql = "create table if not exists " + TABLE_NAME +
			" (id integer not null," +
			" regionalnum  varchar2(6)," +
			" regionalname varchar2(64)," +
			" fid integer," +
			" regionallevel	integer )";
		db.execSQL(sql);
	}
	
	/**
	 * 插入数据
	 * @param regional
	 */
	private void insertRegional(Regional regional,SQLiteDatabase db){
		String sql = "insert into " + TABLE_NAME + "(id, regionalnum, regionalname, fid, regionallevel) values("+regional.getId()+",'"+regional.getRegionalnum()+"','"+regional.getRegionalname()+"',"+regional.getFid()+","+regional.getRegionallevel()+")";
		try{
			db.execSQL(sql);
		}catch(Exception e){
			LogPrint.Print(TAG, e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 获得省信息
	 * @return
	 */
	public String[] getProvince(){
		String[] provinces = null;									    
		int i = 0;
		Cursor cursor = null;
		SQLiteDatabase db = null;
		try{
			db = getReadableDatabase();
			cursor = db.query(TABLE_NAME, new String[]{"regionalname"}, "regionallevel = ?", new String[]{String.valueOf(1)}, null, null, null);
			provinces = new String[cursor.getCount()];
			while(cursor.moveToNext()){
				provinces[i] = cursor.getString(cursor.getColumnIndex("regionalname"));
				i++;
			}
			return provinces;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			if(db!=null){
				db.close();
			}
		}
	}
	
	/**
	 * 获得城市信息
	 * @param province
	 * @return
	 */
	public String[] getCity(String province){
		String[] city = null;
		int i = 0;
		Cursor cursor = null;
		SQLiteDatabase db = null;
		int id = -1;
		try{
			db = getReadableDatabase();
			cursor = db.query(TABLE_NAME, new String[]{"id"}, "regionalname = ?", new String[]{province}, null, null, null);
			if(cursor.moveToNext()){
				id = cursor.getInt(cursor.getColumnIndex("id"));
			}
			cursor = db.query(TABLE_NAME, new String[]{"regionalname"}, "fid = ?", new String[]{String.valueOf(id)}, null, null, null);
			city = new String[cursor.getCount()];
			while(cursor.moveToNext()){
				city[i] = cursor.getString(cursor.getColumnIndex("regionalname"));
				i++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(db != null){
				db.close();
			}
		}
		return city;
	}
}
