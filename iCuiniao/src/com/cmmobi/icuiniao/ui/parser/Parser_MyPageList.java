/**
 * 
 */
package com.cmmobi.icuiniao.ui.parser;

import org.json.JSONArray;

/**
 * @author hw
 * 单品页评论列表数据层数据
 */
public class Parser_MyPageList {

	private Parser_MyPageListItem[] listItems;
	
	public Parser_MyPageList(JSONArray jsonArray){
		try {
			listItems = new Parser_MyPageListItem[jsonArray.length()];
			for(int i = 0;i < jsonArray.length();i ++){
				listItems[i] = new Parser_MyPageListItem(jsonArray.getJSONObject(i));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public Parser_MyPageListItem[] getMyPageListItems(){
		return listItems;
	}
	
	public Parser_MyPageListItem getMyPageListItem(int index){
		return listItems[index];
	}
}
