/**
 * 
 */
package com.icuiniao.plug.im;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.UserUtil;

/**
 * @author hw
 *消息数据库
 */
public class IMDataBase extends SQLiteOpenHelper{

	public final static String DB_NAME = "im.db";
	public final static int DB_VERSION = 1;//数据库版本
	
	public IMDataBase(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		createDB(db);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	/**who：记录用户id，标明这是哪个用户的聊天记录，用于用户切换时聊天记录的过滤（仅显示当前用户的聊天记录）<br>
	messageid：消息id<br>
	tempmessageid：消息流水号(毫秒级系统时间)<br>
	fromid：发出者id<br>
	toid：接收者id<br>
	fromname：发出者昵称<br>
	toname：接收者昵称<br>
	isread：未读已读状态（0未读，1已读）<br>
	message：消息<br>
	sendstate：发送状态（0默认，1发送中，2发送成功，3发送失败）<br>
	date：时间<br>
	remarks：备注<br>
	cid：商品id<br>
	type: 消息类型（0好友消息，1私信，2系统消息, 3申请建立好友，4确认建立好友关系）
	repetsend：首发或重发  1：重发，0：首发*/
	private void createDB(SQLiteDatabase db){
		String sql = "create table if not exists t_message(" +
		"_id integer primary key autoincrement," +
		"who integer," +
		"messageid long," +
		"tempmessageid long," +
		"fromid integer," +
		"toid integer," +
		"fromname varchar not null on conflict fail," +
		"toname varchar not null on conflict fail," +
		"isread integer," +
		"message varchar not null on conflict fail," +
		"sendstate integer," +
		"date varchar not null on conflict fail," +
		"remarks varchar not null on conflict fail," +
		"cid integer," +
		"type integer," +
		"repetsend integer)";
		db.execSQL(sql);
	}
	
	/**插入一条数据*/
	public boolean insertDB(int who,long messageid,long tempmessageid,int fromid,int toid,String fromname,String toname,int isread,String message,int sendstate,String date,String remarks,int cid,int type,int repetsend){
		LogPrint.Print("insert", "insert=====================");
		try {
			String sql = "insert into t_message(who,messageid,tempmessageid,fromid,toid,fromname,toname,isread,message,sendstate,date,remarks,cid,type,repetsend) values(" +
			who +"," +
			messageid +"," +
			tempmessageid + "," +
			fromid +"," +
			toid +"," +
			"'" +fromname+"'," +
			"'" +toname+"'," +
			isread+"," +
			"'" +message+"'," +
			sendstate +"," +
			"'" +date +"'," +
			"'" +remarks +"'," +
			cid +"," +
			type+"," +
			repetsend+")";
			SQLiteDatabase db = getWritableDatabase();
			if(db != null){
				db.execSQL(sql);
				db.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	
	/**更新消息id*/
	public boolean updataMessageId(long tempMessageId,long messageId){
		try {
			SQLiteDatabase db = getWritableDatabase();
			if(db != null){
				ContentValues values = new ContentValues();  
				values.put("messageid", messageId);  
				// 调用update方法  
				// 第一个参数String：表名  
				// 第二个参数ContentValues：ContentValues对象  
				// 第三个参数String：where字句，相当于sql语句where后面的语句，？号是占位符  
				// 第四个参数String[]：占位符的值  
				db.update("t_message", values, "tempmessageid = ?", new String[] {""+tempMessageId});
				db.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	
	/**更新首发重发状态(使用消息id为条件查找)*/
	public boolean updataRepetSendByMessageId(long messageId,int repetsend){
		try {
			SQLiteDatabase db = getWritableDatabase();
			if(db != null){
				ContentValues values = new ContentValues();  
				values.put("repetsend", repetsend);  
				// 调用update方法  
				// 第一个参数String：表名  
				// 第二个参数ContentValues：ContentValues对象  
				// 第三个参数String：where字句，相当于sql语句where后面的语句，？号是占位符  
				// 第四个参数String[]：占位符的值  
				db.update("t_message", values, "messageid = ?", new String[] {""+messageId});
				db.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	
	/**更新首发重发状态(使用流水号为条件查找)*/
	public boolean updataRepetSendByTempMessageId(long tempMessageId,int repetsend){
		try {
			SQLiteDatabase db = getWritableDatabase();
			if(db != null){
				ContentValues values = new ContentValues();  
				values.put("repetsend", repetsend);    
				// 调用update方法  
				// 第一个参数String：表名  
				// 第二个参数ContentValues：ContentValues对象  
				// 第三个参数String：where字句，相当于sql语句where后面的语句，？号是占位符  
				// 第四个参数String[]：占位符的值  
				db.update("t_message", values, "tempmessageid = ?", new String[] {""+tempMessageId});
				db.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	
	/**更新发送状态(使用消息id为条件查找)*/
	public boolean updataSendStateByMessageId(long messageId,int sendstate){
		try {
			SQLiteDatabase db = getWritableDatabase();
			if(db != null){
				ContentValues values = new ContentValues();  
				values.put("sendstate", sendstate);  
				// 调用update方法  
				// 第一个参数String：表名  
				// 第二个参数ContentValues：ContentValues对象  
				// 第三个参数String：where字句，相当于sql语句where后面的语句，？号是占位符  
				// 第四个参数String[]：占位符的值  
				db.update("t_message", values, "messageid = ?", new String[] {""+messageId});
				db.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	
	/**更新发送状态(使用流水号为条件查找)*/
	public boolean updataSendStateByTempMessageId(long tempMessageId,int sendstate){
		try {
			SQLiteDatabase db = getWritableDatabase();
			if(db != null){
				ContentValues values = new ContentValues();  
				values.put("sendstate", sendstate);  
				// 调用update方法  
				// 第一个参数String：表名  
				// 第二个参数ContentValues：ContentValues对象  
				// 第三个参数String：where字句，相当于sql语句where后面的语句，？号是占位符  
				// 第四个参数String[]：占位符的值  
				db.update("t_message", values, "tempmessageid = ?", new String[] {""+tempMessageId});
				db.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	
	/**更新已读未读状态*/
	public boolean updataIsReadState(long messageId,int isread){
		try {
			SQLiteDatabase db = getWritableDatabase();
			if(db != null){
				ContentValues values = new ContentValues();  
				values.put("isread", isread);  
				// 调用update方法  
				// 第一个参数String：表名  
				// 第二个参数ContentValues：ContentValues对象  
				// 第三个参数String：where字句，相当于sql语句where后面的语句，？号是占位符  
				// 第四个参数String[]：占位符的值  
				db.update("t_message", values, "messageid = ?", new String[] {""+messageId});
				db.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	
	/**获取已读未读状态<br>
	 * 0未读，1已读*/
	public int getIsReadState(long messageId){
		int state = -1;
		try {
			SQLiteDatabase db = getReadableDatabase();
			if(db != null){
				// 第一个参数String：表名  
				// 第二个参数String[]:要查询的列名  
				// 第三个参数String：查询条件  
				// 第四个参数String[]：查询条件的参数  
				// 第五个参数String:对查询的结果进行分组  
				// 第六个参数String：对分组的结果进行限制  
				// 第七个参数String：对查询的结果进行排序
				Cursor cursor = db.query("t_message", new String[]{"messageid","isread"}, "messageid = ?", new String[]{""+messageId}, null, null, null);
				if(cursor != null){
					if(cursor.getCount() > 0){
						cursor.moveToFirst();
						state = cursor.getInt(cursor.getColumnIndex("isread"));
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return state;
	}
	
	/**获取首发重发状态<br>
	 * 0首发，1重发*/
	public int getRepetSend(long tempMessageId){
		int state = 0;
		try {
			SQLiteDatabase db = getReadableDatabase();
			if(db != null){
				// 第一个参数String：表名  
				// 第二个参数String[]:要查询的列名  
				// 第三个参数String：查询条件  
				// 第四个参数String[]：查询条件的参数  
				// 第五个参数String:对查询的结果进行分组  
				// 第六个参数String：对分组的结果进行限制  
				// 第七个参数String：对查询的结果进行排序
				Cursor cursor = db.query("t_message", new String[]{"tempmessageid","repetsend"}, "tempmessageid = ?", new String[]{""+tempMessageId}, null, null, null);
				if(cursor != null){
					if(cursor.getCount() > 0){
						cursor.moveToFirst();
						state = cursor.getInt(cursor.getColumnIndex("repetsend"));
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return state;
	}
	
	/**获取发送状态<br>
	 * 0默认，1发送中，2发送成功，3发送失败*/
	public int getSendState(long tempMessageId){
		int state = 0;
		try {
			SQLiteDatabase db = getReadableDatabase();
			if(db != null){
				// 第一个参数String：表名  
				// 第二个参数String[]:要查询的列名  
				// 第三个参数String：查询条件  
				// 第四个参数String[]：查询条件的参数  
				// 第五个参数String:对查询的结果进行分组  
				// 第六个参数String：对分组的结果进行限制  
				// 第七个参数String：对查询的结果进行排序
				Cursor cursor = db.query("t_message", new String[]{"tempmessageid","sendstate"}, "tempmessageid = ?", new String[]{""+tempMessageId}, null, null, null);
				if(cursor != null){
					if(cursor.getCount() > 0){
						cursor.moveToFirst();
						state = cursor.getInt(cursor.getColumnIndex("sendstate"));
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return state;
	}
	
	/**取出一条数据*/
	public Entity getTheMessage(long tempMessageId){
		Entity entity = null;
		try {
			SQLiteDatabase db = getReadableDatabase();
			if(db != null){
				// 第一个参数String：表名  
				// 第二个参数String[]:要查询的列名  
				// 第三个参数String：查询条件  
				// 第四个参数String[]：查询条件的参数  
				// 第五个参数String:对查询的结果进行分组  
				// 第六个参数String：对分组的结果进行限制  
				// 第七个参数String：对查询的结果进行排序
				Cursor cursor = db.query("t_message", new String[]{"who","messageid","tempmessageid","fromid","toid","fromname","toname","isread","message","sendstate","date","remarks","cid","type","repetsend"}, "tempmessageid = ?", new String[]{""+tempMessageId}, null, null, null);
				if(cursor != null){
					if(cursor.getCount() > 0){
						cursor.moveToFirst();
						entity = new Entity();
						entity.setUserId(cursor.getInt(cursor.getColumnIndex("who")));
						entity.setMessageid(cursor.getLong(cursor.getColumnIndex("messageid")));
						entity.setTempMessageId(cursor.getLong(cursor.getColumnIndex("tempmessageid")));
						if(cursor.getInt(cursor.getColumnIndex("fromid")) == UserUtil.userid){//自己发送的消息
							entity.setRevicerUserId(cursor.getInt(cursor.getColumnIndex("toid")));
							entity.setUserName(cursor.getString(cursor.getColumnIndex("fromname")));
							entity.setNickName(cursor.getString(cursor.getColumnIndex("toname")));
						}else{//自己接收的消息
							entity.setRevicerUserId(cursor.getInt(cursor.getColumnIndex("fromid")));
							entity.setUserName(cursor.getString(cursor.getColumnIndex("toname")));
							entity.setNickName(cursor.getString(cursor.getColumnIndex("fromname")));
						}
						entity.setIsRead(cursor.getInt(cursor.getColumnIndex("isread")));
						String messageString = cursor.getString(cursor.getColumnIndex("message"));
						MessageBody messageBody = new MessageBody();
						messageBody.setActionType(0);
						messageBody.setMessage(messageString);
						entity.setMessageBodies(messageBody);
						entity.setSendState(cursor.getInt(cursor.getColumnIndex("sendstate")));
						entity.setCommenttime(cursor.getString(cursor.getColumnIndex("date")));
						entity.setRemarks(cursor.getString(cursor.getColumnIndex("remarks")));
						entity.setCid(cursor.getInt(cursor.getColumnIndex("cid")));
						entity.setType(cursor.getInt(cursor.getColumnIndex("type")));
						entity.setRepetSend((byte)cursor.getInt(cursor.getColumnIndex("repetsend")));
						entity.setFromId(cursor.getInt(cursor.getColumnIndex("fromid")));
						entity.setToId(cursor.getInt(cursor.getColumnIndex("toid")));
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return entity;
	}
	
	public ArrayList<Entity> getEntities(int userId){
		ArrayList<Entity> entities = null;
		Cursor cursor = null;
		SQLiteDatabase db = null;
		try{
			db = getReadableDatabase();
			if(db!=null){
				cursor = db.query("t_message", new String[]{"who","messageid","tempmessageid","fromid","toid","fromname","toname","isread","message","sendstate","date","remarks","cid","type","repetsend"}, "who = ?", new String[]{""+userId}, null, null, "_id desc");
				if(cursor.getCount() > 0){
					entities = new ArrayList<Entity>();
					while(cursor.moveToNext()){
						Entity entity = getEntity(cursor);
						entities.add(entity);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			if(db!=null){
				db.close();
			}
		}
		return entities;
	}
	
	/**
	 * 根据用户ID查询消息并且根据发送ID分组
	 * @param userId
	 * @return
	 */
	public ArrayList<Entity> getTheMessageByUserIdGroup(int userId,int type,int up){
		ArrayList<Entity> entities = null;
		Cursor cursor = null;
		SQLiteDatabase db = null;
		try{
			db = getReadableDatabase();
			if(db != null){
				// 第一个参数String：表名  
				// 第二个参数String[]:要查询的列名  
				// 第三个参数String：查询条件  
				// 第四个参数String[]：查询条件的参数  
				// 第五个参数String:对查询的结果进行分组  
				// 第六个参数String：对分组的结果进行限制  
				// 第七个参数String：对查询的结果进行排序
				String where = null;
				String[] whereValue = null;
				if(type != -1 ){
					if(type == 0){
						whereValue = new String[2];
						where = "who = ? and type <> ?";
						whereValue[0] = ""+userId;
						whereValue[1] = ""+2;
					}
					else if(type == 2){
						whereValue = new String[2];
						where = "who = ? and type = ?";
						whereValue[0] = ""+userId;
						whereValue[1] = ""+2;
					}
				}
				cursor = db.query("t_message", new String[]{"who","messageid","tempmessageid","fromid","toid","fromname","toname","isread","message","sendstate","date","remarks","cid","type","repetsend"}, where, whereValue, "fromid,toid", null, "_id desc");
				if(cursor != null){
					if(cursor.getCount() > 0){
						int i = 0;
						entities = new ArrayList<Entity>();
						while(cursor.moveToNext()){
							if(entities.size()==0){	
								Entity entity = getEntity(cursor);
								entities.add(entity);
							}
							else{
								Entity entity = getEntity(cursor);
								boolean b = false;
								for(int j=entities.size();j>0;j--){
									Entity previousEntity = entities.get(j-1);
									if(entity.getUserId()==previousEntity.getUserId()&&entity.getRevicerUserId()==previousEntity.getRevicerUserId()){
										b = true;
										break;
									}
								}
								if(!b){
									entities.add(entity);
								}
							}
						}
					}
				}
				//判断是否有置顶
				if(up!=-1){
					Entity upEntity = null;
					if(entities!=null&&entities.size()>0){
						for(int i=0;i<entities.size();i++){
							upEntity = entities.get(i);
							if(upEntity.getRevicerUserId()==up){
								entities.remove(i);
								break;
							}
						}
					}
					if(upEntity!=null){
						ArrayList<Entity> newEntitys = new ArrayList<Entity>();
						newEntitys.add(upEntity);
						if(entities!=null&&entities.size()>0){
							for(int i=0;i<entities.size();i++){
								Entity entity = entities.get(i);
								newEntitys.add(entity);
							}
						}
						return newEntitys;
					}
				}
				else{
					return entities;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			if(db!=null){
				db.close();
			}
		}
		return null;
	}
	
	public Entity getEntity(Cursor cursor){
		Entity entity = new Entity();
		entity.setUserId(cursor.getInt(cursor.getColumnIndex("who")));
		entity.setMessageid(cursor.getLong(cursor.getColumnIndex("messageid")));
		entity.setTempMessageId(cursor.getLong(cursor.getColumnIndex("tempmessageid")));
		if(cursor.getInt(cursor.getColumnIndex("fromid")) == UserUtil.userid){//自己发送的消息
			entity.setRevicerUserId(cursor.getInt(cursor.getColumnIndex("toid")));
			entity.setUserName(cursor.getString(cursor.getColumnIndex("fromname")));
			entity.setNickName(cursor.getString(cursor.getColumnIndex("toname")));
		}else{//自己接收的消息
			entity.setRevicerUserId(cursor.getInt(cursor.getColumnIndex("fromid")));
			entity.setUserName(cursor.getString(cursor.getColumnIndex("toname")));
			entity.setNickName(cursor.getString(cursor.getColumnIndex("fromname")));
		}
		entity.setIsRead(cursor.getInt(cursor.getColumnIndex("isread")));
		String messageString = cursor.getString(cursor.getColumnIndex("message"));
		MessageBody messageBody = new MessageBody();
		messageBody.setActionType(0);
		messageBody.setMessage(messageString);
		entity.setMessageBodies(messageBody);
		entity.setSendState(cursor.getInt(cursor.getColumnIndex("sendstate")));
		entity.setCommenttime(cursor.getString(cursor.getColumnIndex("date")));
		entity.setRemarks(cursor.getString(cursor.getColumnIndex("remarks")));
		entity.setCid(cursor.getInt(cursor.getColumnIndex("cid")));
		entity.setType(cursor.getInt(cursor.getColumnIndex("type")));
		entity.setRepetSend((byte)cursor.getInt(cursor.getColumnIndex("repetsend")));
		return entity;
	}
	
	/**取出一条数据*/
	public ArrayList<Entity> getTheMessageByUserId(int userId){
		ArrayList<Entity> entities = null;
		Cursor cursor = null;
		SQLiteDatabase db = null;
		try {
			db = getReadableDatabase();
			if(db != null){
				// 第一个参数String：表名  
				// 第二个参数String[]:要查询的列名  
				// 第三个参数String：查询条件  
				// 第四个参数String[]：查询条件的参数  
				// 第五个参数String:对查询的结果进行分组  
				// 第六个参数String：对分组的结果进行限制  
				// 第七个参数String：对查询的结果进行排序
				cursor = db.query("t_message", new String[]{"who","messageid","tempmessageid","fromid","toid","fromname","toname","isread","message","sendstate","date","remarks","cid","type","repetsend"}, "who = ?", new String[]{""+userId}, null, null, null);
				if(cursor != null){
					if(cursor.getCount() > 0){
						entities = new ArrayList<Entity>();
						while (cursor.moveToNext()) {
							Entity entity = new Entity();
							entity.setUserId(cursor.getInt(cursor.getColumnIndex("who")));
							entity.setMessageid(cursor.getLong(cursor.getColumnIndex("messageid")));
							entity.setTempMessageId(cursor.getLong(cursor.getColumnIndex("tempmessageid")));
							if(cursor.getInt(cursor.getColumnIndex("fromid")) == UserUtil.userid){//自己发送的消息
								entity.setRevicerUserId(cursor.getInt(cursor.getColumnIndex("toid")));
								entity.setUserName(cursor.getString(cursor.getColumnIndex("fromname")));
								entity.setNickName(cursor.getString(cursor.getColumnIndex("toname")));
							}else{//自己接收的消息
								entity.setRevicerUserId(cursor.getInt(cursor.getColumnIndex("fromid")));
								entity.setUserName(cursor.getString(cursor.getColumnIndex("toname")));
								entity.setNickName(cursor.getString(cursor.getColumnIndex("fromname")));
							}
							entity.setIsRead(cursor.getInt(cursor.getColumnIndex("isread")));
							String messageString = cursor.getString(cursor.getColumnIndex("message"));
							MessageBody messageBody = new MessageBody();
							messageBody.setActionType(0);
							messageBody.setMessage(messageString);
							entity.setMessageBodies(messageBody);
							entity.setSendState(cursor.getInt(cursor.getColumnIndex("sendstate")));
							entity.setCommenttime(cursor.getString(cursor.getColumnIndex("date")));
							entity.setRemarks(cursor.getString(cursor.getColumnIndex("remarks")));
							entity.setCid(cursor.getInt(cursor.getColumnIndex("cid")));
							entity.setType(cursor.getInt(cursor.getColumnIndex("type")));
							entity.setRepetSend((byte)cursor.getInt(cursor.getColumnIndex("repetsend")));
							entity.setFromId(cursor.getInt(cursor.getColumnIndex("fromid")));
							entity.setToId(cursor.getInt(cursor.getColumnIndex("toid")));
							entities.add(entity);
					}
				}
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cursor != null){
				cursor.close();
			}
			if(db != null){
				db.close();
			}
		}
		return entities;
	}
	
	
	//取得页数
	public int getMessageByUserIdByPageNum(int my,int other,int rowCount){
		int pageNum = 0;
		Cursor cursor = null;
		SQLiteDatabase db = null;
		try {
			db = getReadableDatabase();
			if(db != null){
				String sqlString = "select count(*) from t_message where who = "+my +" and (fromid = "+my+" or toid = "+my+")";
				cursor = db.rawQuery(sqlString, null);
				cursor.moveToLast();
				long recSize = cursor.getLong(0);//取得总数
				cursor.close();
				if(recSize%rowCount == 0){
					
					pageNum = (int)(recSize/rowCount);
				}else{
					
					pageNum = (int)(recSize/rowCount) + 1;//取得分页数
				}
				
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return pageNum;
	}
	/**
	 * 取出一条数据
	 * @param my
	 * @param other
	 * @param rowCount 每页显示行数
	 * @param pageId 第几页
	 * @return
	 */
	public ArrayList<Entity> getTheMessageByUserId(int my,int other,int rowCount,int pageId){
		ArrayList<Entity> entities = null;
		Cursor cursor = null;
		SQLiteDatabase db = null;
		try {
			db = getReadableDatabase();
			if(db != null){
				// 第一个参数String：表名  
				// 第二个参数String[]:要查询的列名  
				// 第三个参数String：查询条件  
				// 第四个参数String[]：查询条件的参数  
				// 第五个参数String:对查询的结果进行分组  
				// 第六个参数String：对分组的结果进行限制  
				// 第七个参数String：对查询的结果进行排序
					cursor = db.query("t_message", new String[]{"who","messageid","tempmessageid","fromid","toid","fromname","toname","isread","message","sendstate","date","remarks","cid","type","repetsend"}, "who = ? and (fromid = ? or toid = ?) ", new String[]{""+my,""+other,""+other}, null, null, "_id desc limit "+rowCount+" offset "+String.valueOf(pageId * rowCount)+"");
					//cursor = db.query("t_message", new String[]{"who","messageid","tempmessageid","fromid","toid","fromname","toname","isread","message","sendstate","date","remarks","cid","type","repetsend"}, "who = ? and (fromid = ? or toid = ?) ", new String[]{""+my,""+other,""+other}, null, null, "_id desc limit 0,100");
				if(cursor != null){
					if(cursor.getCount() > 0){
						entities = new ArrayList<Entity>();
						while (cursor.moveToNext()) {
							Entity entity = new Entity();
							entity.setUserId(cursor.getInt(cursor.getColumnIndex("who")));
							entity.setMessageid(cursor.getLong(cursor.getColumnIndex("messageid")));
							entity.setTempMessageId(cursor.getLong(cursor.getColumnIndex("tempmessageid")));
							if(cursor.getInt(cursor.getColumnIndex("fromid")) == UserUtil.userid){//自己发送的消息
								entity.setRevicerUserId(cursor.getInt(cursor.getColumnIndex("toid")));
								entity.setUserName(cursor.getString(cursor.getColumnIndex("fromname")));
								entity.setNickName(cursor.getString(cursor.getColumnIndex("toname")));
							}else{//自己接收的消息
								entity.setRevicerUserId(cursor.getInt(cursor.getColumnIndex("fromid")));
								entity.setUserName(cursor.getString(cursor.getColumnIndex("toname")));
								entity.setNickName(cursor.getString(cursor.getColumnIndex("fromname")));
							}
							entity.setIsRead(cursor.getInt(cursor.getColumnIndex("isread")));
							String messageString = cursor.getString(cursor.getColumnIndex("message"));
							MessageBody messageBody = new MessageBody();
							messageBody.setActionType(0);
							messageBody.setMessage(messageString);
							entity.setMessageBodies(messageBody);
							entity.setSendState(cursor.getInt(cursor.getColumnIndex("sendstate")));
							entity.setCommenttime(cursor.getString(cursor.getColumnIndex("date")));
							entity.setRemarks(cursor.getString(cursor.getColumnIndex("remarks")));
							entity.setCid(cursor.getInt(cursor.getColumnIndex("cid")));
							entity.setType(cursor.getInt(cursor.getColumnIndex("type")));
							entity.setRepetSend((byte)cursor.getInt(cursor.getColumnIndex("repetsend")));
							entity.setFromId(cursor.getInt(cursor.getColumnIndex("fromid")));
							entity.setToId(cursor.getInt(cursor.getColumnIndex("toid")));
							entities.add(entity);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cursor != null){
				cursor.close();
			}
			if(db != null){
				db.close();
			}
		}
		return entities;
	}
	
	/**删除数据库条目*/
	public boolean deleteByMessageId(long messageId){
		try {
			SQLiteDatabase db = getWritableDatabase();
			if(db != null){
				//第一个参数String：表名  
				//第二个参数String：条件语句  
				//第三个参数String[]：条件值 
				db.delete("t_message", "messageid = ?", new String[]{""+messageId});
				db.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	
	/**删除数据库条目*/
	public boolean deleteByTempMessageId(long tempmessageId){
		try {
			SQLiteDatabase db = getWritableDatabase();
			if(db != null){
				//第一个参数String：表名  
				//第二个参数String：条件语句  
				//第三个参数String[]：条件值 
				db.delete("t_message", "tempmessageid = ?", new String[]{""+tempmessageId});
				db.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	
	/**删除数据库条目*/
	public boolean deleteByTempUserId(int userId){
		try {
			SQLiteDatabase db = getWritableDatabase();
			if(db != null){
				//第一个参数String：表名  
				//第二个参数String：条件语句  
				//第三个参数String[]：条件值 
				db.delete("t_message", "who = ?", new String[]{""+userId});
				db.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	
	/**
	 * 删除数据信息
	 * @param who
	 * @param fromid
	 * @return
	 */
	public boolean deleteByFromid(int who,int fromid){
		try{
			SQLiteDatabase db = getWritableDatabase();
			if(db != null){
				db.delete("t_message", "who = ? and (fromid = ? or toid = ?)", new String[]{""+who,""+fromid,""+fromid});
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 删除系统消息
	 * @param who
	 * @param type
	 * @return
	 */
	public boolean deleteSystemMessage(int who,int type){
		try{
			SQLiteDatabase db = getWritableDatabase();
			if(db != null){
				db.delete("t_message", "who = ? and type = ?", new String[]{""+who,""+type});
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;		
	}
	
	/**更新发送时间*/
	public boolean updataCommentTimeByTempMessageId(long tempMessageId,String commentTime){
		try {
			SQLiteDatabase db = getWritableDatabase();
			if(db != null){
				ContentValues values = new ContentValues();  
				values.put("date", commentTime);  
				// 调用update方法  
				// 第一个参数String：表名  
				// 第二个参数ContentValues：ContentValues对象  
				// 第三个参数String：where字句，相当于sql语句where后面的语句，？号是占位符  
				// 第四个参数String[]：占位符的值  
				db.update("t_message", values, "tempmessageid = ?", new String[] {""+tempMessageId});
				db.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	/**
	 * 根据revicerUserId更新备注昵称
	 * @param revicerUserId
	 * @param remarks
	 * @return
	 */
	public boolean updateRemarkByRevicerUserId(int revicerUserId,String remarks){
		
		try {
			SQLiteDatabase db = getWritableDatabase();
			if(db != null){
				ContentValues values = new ContentValues();  
				values.put("remarks", remarks);  
				// 调用update方法  
				// 第一个参数String：表名  
				// 第二个参数ContentValues：ContentValues对象  
				// 第三个参数String：where字句，相当于sql语句where后面的语句，？号是占位符  
				// 第四个参数String[]：占位符的值  
				db.update("t_message", values, "toid = ?", new String[] {""+revicerUserId});
				db.close();
			}
		} catch (Exception e) {
			return false;
		}
		return true;
		
		
	}
	
	/**数据库中是否有这条消息*/
	public boolean isInDB(int messageId){
		boolean result = false;
		try {
			SQLiteDatabase db = getReadableDatabase();
			if(db != null){
				// 第一个参数String：表名  
				// 第二个参数String[]:要查询的列名  
				// 第三个参数String：查询条件  
				// 第四个参数String[]：查询条件的参数  
				// 第五个参数String:对查询的结果进行分组  
				// 第六个参数String：对分组的结果进行限制  
				// 第七个参数String：对查询的结果进行排序
				Cursor cursor = db.query("t_message", new String[]{"who","messageid","tempmessageid","fromid","toid","fromname","toname","isread","message","sendstate","date","remarks","cid","type","repetsend"}, "messageid = ?", new String[]{""+messageId}, null, null, null);
				if(cursor != null){
					if(cursor.getCount() > 0){
						cursor.moveToFirst();
						int index = cursor.getColumnIndex("messageid");
						String str = cursor.getString(index);
						if(str != null&&str.length() > 0){
							result = true;
						}
					}
					cursor.close();
				}
				db.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}

}
