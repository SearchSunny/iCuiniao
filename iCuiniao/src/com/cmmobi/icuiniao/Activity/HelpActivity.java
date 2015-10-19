/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.ui.adapter.HelpAdapter;
import com.cmmobi.icuiniao.ui.view.MyGallery;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.URLUtil;

/**
 * @author hw
 *帮助引导页
 */
public class HelpActivity extends Activity{

	private MyGallery gallery;
	private boolean isInSetting;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.helppage);
        CommonUtil.saveVersion(this, URLUtil.version);
        isInSetting = getIntent().getExtras().getBoolean("isInSetting");
        gallery = (MyGallery)findViewById(R.id.gallery);
        gallery.setSpacing(10);
        gallery.setAdapter(new HelpAdapter(this, isInSetting));
        if(isInSetting){
        	OfflineLog.writeMainMenu_Help((byte)1);//写入离线日志
        }
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			if(!isInSetting){
				Intent intent1 = new Intent();
				intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent1.setClass(this, FirstPageActivity.class);
				intent1.putExtra("type", 1);
				startActivity(intent1);
			}
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
