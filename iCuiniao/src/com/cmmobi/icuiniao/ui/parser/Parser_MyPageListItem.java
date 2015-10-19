/**
 * 
 */
package com.cmmobi.icuiniao.ui.parser;

import org.json.JSONObject;

/**
 * @author hw
 * 单品页评论列表子项数据层数据
 */
public class Parser_MyPageListItem {

	private int is_subject;//0：商品评论，1：用户评论
	private String gender;//男/女
	private int subjectid;//主题id
	private int commentid;//评论id
	private int cid;//商品id
	private String icon_src;//图片下载地址
	private String userpage;//用户主页跳转地址
	private String username;//评论人
	private String comment_to;//回复评论对象
	private String time;//时间
	private String msg1;
	private String msg2;
	private int userid; //评论人ID
	
	public Parser_MyPageListItem(JSONObject jsonObject){
		try {
			is_subject = jsonObject.getInt("is_subject");
			gender = jsonObject.getString("gender");
			subjectid = jsonObject.getInt("subjectid");
			commentid = jsonObject.getInt("commentid");
			cid = jsonObject.getInt("cid");
			icon_src = jsonObject.getString("icon_src");
			userpage = jsonObject.getString("userpage");
			username = jsonObject.getString("username");
			comment_to = jsonObject.getString("comment_to");
			time = jsonObject.getString("time");
			msg1 = jsonObject.getString("msg1");
			msg2 = jsonObject.getString("msg2");
			userid = jsonObject.getInt("userid");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public int getIsSubject(){return is_subject;}
	public String getGender(){return gender;}
	public int getSubjectId(){return subjectid;}
	public int getCommentId(){return commentid;}
	public int getCid(){return cid;}
	public String getIconSrcUrl(){return icon_src;}
	public String getUserPageUrl(){return userpage;}
	public String getUserName(){return username;}
	public String getCommentTo(){return comment_to;}
	public String getTime(){return time;}
	public String getMsg1(){return msg1;}
	public String getMsg2(){return msg2;}
	public int getUserId(){return userid;}
	
}
