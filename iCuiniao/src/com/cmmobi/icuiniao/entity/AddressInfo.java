package com.cmmobi.icuiniao.entity;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.cmmobi.icuiniao.util.CommonUtil;

public class AddressInfo implements Serializable {	
	
	public String addr;
	public String phone;
	public String name;
	public boolean isCheck;
	public String id;
	
	public static ArrayList<AddressInfo> changeStrToList(String str){
		ArrayList<AddressInfo> arrAddress = new ArrayList<AddressInfo>();
		try {
			JSONArray jsonArray = new JSONArray(str);
			for(int i=0; i< jsonArray.length(); i++){
				AddressInfo addrInfo = new AddressInfo();
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				addrInfo.id = jsonObj.getString("id");
				addrInfo.addr = jsonObj.getString("addr");
				addrInfo.name = jsonObj.getString("name");
				addrInfo.phone = jsonObj.getString("phone");
				arrAddress.add(addrInfo);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return arrAddress;
	}
	
	public static String changeListToString(ArrayList<AddressInfo> arrAddrInfo){
		JSONArray jsonArray = new JSONArray();
		String addrManager = "";
		try{
		for(int i = 0;i < arrAddrInfo.size();i++){
			JSONObject jsonObject = new JSONObject();
			AddressInfo addrInfo = arrAddrInfo.get(i);
			jsonObject.put("id", addrInfo.id);
			jsonObject.put("addr", addrInfo.addr);
			jsonObject.put("phone", addrInfo.phone);
			jsonObject.put("name", addrInfo.name);
			jsonArray.put(jsonObject);
		} 			
		addrManager = jsonArray.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return addrManager;
	}
	
	//获取当前收货地址
	public static AddressInfo getCurrAddressInfo(Context context) {
		String AddressMgr = CommonUtil.getReceiveAddrMgr(context);
		int currAddrIdx = CommonUtil.getCurReceiveAddr(context);
		if (AddressMgr.length() > 2) {
			ArrayList<AddressInfo> arrAddr = changeStrToList(AddressMgr);
			if(currAddrIdx <= arrAddr.size() -1 && currAddrIdx > -1){
				return arrAddr.get(currAddrIdx);
			}
		}
		return null;
	}
}
