  /**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;

/**
 * @author hw
 *
 *解析器的父类，定义共有属性
 */
public class Parser_Layout_AbsLayout {
	
	//页面id
	protected int mPageId;
	//组件类型ID
	private int mModelTypeId;
	public final static int MODELTYPE_TITLEBAR 					= 0;
	public final static int MODELTYPE_TITLEBARFLOAT 			= 1;
	public final static int MODELTYPE_MAINPAGE 					= 2;
	public final static int MODELTYPE_IMAGE 					= 3;
	public final static int MODELTYPE_TEXT 						= 4;
	public final static int MODELTYPE_BUTTON 					= 5;
	public final static int MODELTYPE_INPUTLINE 				= 6;
	public final static int MODELTYPE_USERINFO 					= 7;
	public final static int MODELTYPE_TABBUTTON 				= 8;
	public final static int MODELTYPE_USERBAR 					= 9;
	public final static int MODELTYPE_LIST 						= 10;
	public final static int MODELTYPE_LIKE 						= 11;
	public final static int MODELTYPE_COMMODITY 				= 12;
	public final static int MODELTYPE_COMMODITYINFO 			= 13;
	public final static int MODELTYPE_COMMODITYBAR 				= 14;
	public final static int MODELTYPE_LIKEBUTTON				= 15;
	public final static int MODELTYPE_FORMBUTTON				= 16;
	public final static int MODELTYPE_COMMODITYBUTTON			= 17;
	public final static int MODELTYPE_ACTIVITYBUTTON			= 18;
	public final static int MODELTYPE_LISTITEM					= 19;
	public final static int MODELTYPE_COQUETRY					= 20;
	public final static int MODELTYPE_COQUETRYITEM				= 21;
	public final static int MODELTYPE_STREAMPAGE				= 22;
	//lyb for userInfo_new
	public final static int MODELTYPE_USERINFO_new 				= 23;
	public final static int MODELTYPE_STYLEPAGE					= 24;
	public final static int MODELTYPE_STYLEITEM					= 25;

	//action
	public final static int ACTION_BACK 						= 100;
	public final static int ACTION_MAINMENU 					= 101;
	public final static int ACTION_PLAY 						= 102;
	public final static int ACTION_OK 							= 103;
	public final static int ACTION_SEND 						= 104;
	public final static int ACTION_COMMENT 						= 105;
	public final static int ACTION_EDIT							= 106;
	public final static int ACTION_DELETE						= 107;
	//lyb for userInfo
	public final static int ACTION_ADD 							= 110;
	public final static int ACTION_TO_DELETE					= 111;
	public final static int ACTION_DELETED 						= 112;
	public final static int ACTION_FRIEND						= 113;
	public final static int ACTION_SET							= 114;
	public final static int ACTION_DEL_BLACK					= 115;
	public final static int ACTION_ADD_FRIEND					= 116;
	
	//type
	public final static int TYPE_ADD 							= 200;
	public final static int TYPE_ADDEACHOTHER 					= 201;
	public final static int TYPE_ADDOVER 						= 202;
	public final static int TYPE_DISCOUNT 						= 203;
	public final static int TYPE_RECOMMEND 						= 204;
	public final static int TYPE_FORM 							= 205;
	public final static int TYPE_ACTIVITIES 					= 206;
	public final static int TYPE_ONLYLEFT 						= 207;
	public final static int TYPE_ONLYRIGHT 						= 208;
	public final static int TYPE_ITEM_ONLYTEXT 					= 209;
	public final static int TYPE_ITEM_FRIENDS 					= 210;
	public final static int TYPE_ITEM_COMMENT_C 				= 211;
	public final static int TYPE_ITEM_COMMENT_U 				= 212;
	public final static int TYPE_DIALOG 						= 213;
	public final static int TYPE_MULTI 							= 214;
	public final static int TYPE_CANCEL 						= 215;
	
	public final static int MODELTYPE_1 						= 300;
	public final static int MODELTYPE_2 						= 301;
	public final static int MODELTYPE_3 						= 302;
	public final static int MODELTYPE_4 						= 303;
	public final static int MODELTYPE_5 						= 304;
	public final static int MODELTYPE_6 						= 305;
	
	public final static int TRUE 								= 400;
	public final static int FALSE 								= 401;
	
	//用于默认
	public final static int DEFAULT 							= 999;
	//解析器版本号
	public final static int VERSION = 1000;
	
	public Parser_Layout_AbsLayout(){
		mModelTypeId = -1;
		mPageId = -1;
	}
	
	public void setModelType(int value){
		mModelTypeId = value;
	}
	
	public int getModelType(){
		return mModelTypeId;
	}
	
	public void setPageId(String value){
		mPageId = Integer.parseInt(value);
	}
	
	public int getPageId(){
		return mPageId;
	}
	
}
