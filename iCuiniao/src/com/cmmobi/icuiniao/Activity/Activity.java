/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import android.content.Intent;
import android.os.Bundle;

import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.UserUtil;

/**
 * @author hw 抽出一个总的activity
 */
public class Activity extends android.app.Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt("userState", UserUtil.userState);
		outState.putInt("userid", UserUtil.userid);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		UserUtil.userState = savedInstanceState.getInt("userState");
		UserUtil.userid = savedInstanceState.getInt("userid");
		//关闭所有activity
		Intent intent1 = new Intent();
		intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent1.setClass(this, FirstPageActivity.class);
		intent1.putExtra("type", 2);
		startActivity(intent1);
		finish();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		CommonUtil.isAppOnForeground = CommonUtil.isAppOnForeground(this);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		CommonUtil.isAppOnForeground = CommonUtil.isAppOnForeground(this);
	}
	
}
