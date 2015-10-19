package com.cmmobi.icuiniao.entity;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.cmmobi.icuiniao.util.CommonUtil;

public class SajiaoObj implements Serializable{
	public String id;
	public String email;
	public String name = "";
	public boolean isCheck;
	
	public static ArrayList<SajiaoObj> changeStrToList(String str){
		ArrayList<SajiaoObj> arrSajiao = new ArrayList<SajiaoObj>();
		try {
			JSONArray jsonArray = new JSONArray(str);
			for(int i=0; i< jsonArray.length(); i++){
				SajiaoObj sajiaoObj = new SajiaoObj();
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				sajiaoObj.id = jsonObj.getString("id");
				sajiaoObj.email = jsonObj.getString("email");
				if (!jsonObj.isNull("name")) {
					sajiaoObj.name = jsonObj.getString("name");
				}
				arrSajiao.add(sajiaoObj);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return arrSajiao;
	}
	
	public static String changeListToString(ArrayList<SajiaoObj> arrSajiao){
		JSONArray jsonArray = new JSONArray();
		String addrManager = "";
		try{
		for(int i = 0;i < arrSajiao.size();i++){
			JSONObject jsonObject = new JSONObject();
			SajiaoObj sajiaoObj = arrSajiao.get(i);
			jsonObject.put("id", sajiaoObj.id);
			jsonObject.put("email", sajiaoObj.email);			
			jsonObject.put("name", sajiaoObj.name);
			jsonArray.put(jsonObject);
		} 			
		addrManager = jsonArray.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return addrManager;
	}
	
	//获取当前撒娇对象
	public static SajiaoObj getCurrSajiaoObj(Context context) {
		String sajiaoMgr = CommonUtil.getSajiaoObjMgr(context);
		int currSajiaoIdx = CommonUtil.getCurSajiaoObj(context);
		if (sajiaoMgr.length() > 2) {
			ArrayList<SajiaoObj> arrSajiao = changeStrToList(sajiaoMgr);
			if(currSajiaoIdx <= arrSajiao.size() -1 && currSajiaoIdx > -1){
				return arrSajiao.get(currSajiaoIdx);
			}
		}
		return null;
	}
}
