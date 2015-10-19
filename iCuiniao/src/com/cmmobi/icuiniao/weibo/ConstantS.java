package com.cmmobi.icuiniao.weibo;

public interface ConstantS {

	//应用的key
	public static final String APP_KEY="1506136685";
	
	//开发者REDIRECT_URL
	public static final String REDIRECT_URL = "http://jumpcn.icuiniao.com/Approve/login/redirect";
	
	//public static final String APP_Secret = "9bd78d02c45c62c8db975c56569c97c3";
	    
	//新支持scope 支持传入多个scope权限，用逗号分隔
	public static final String SCOPE = "email,direct_messages_read,direct_messages_write," +
			"friendships_groups_read,friendships_groups_write,statuses_to_me_read," +
				"follow_app_official_microblog";
	
	public static final String CLIENT_ID = "client_id";
	
	public static final String RESPONSE_TYPE = "response_type";
	
	public static final String USER_REDIRECT_URL = "redirect_uri";
	
	public static final String DISPLAY = "display";
	
	public static final String USER_SCOPE = "scope";
	
	public static final String PACKAGE_NAME = "com.cmmobi.icuiniao";
	
	public static final String KEY_HASH = "6de74be8d64a14b3379826a5953f4a85";

}
