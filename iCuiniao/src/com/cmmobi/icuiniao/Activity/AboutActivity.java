/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cmmobi.icuiniao.R;

/**
 * @author hw
 *关于
 */
public class AboutActivity extends Activity{

	private Button titlebar_backbutton;
	private Button helpbtn;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
        titlebar_backbutton.setOnClickListener(backClickListener);
        helpbtn = (Button)findViewById(R.id.helpbtn);
        helpbtn.setOnClickListener(helpbtnClickListener);
        
	}
	
	private OnClickListener backClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	};
	
	private OnClickListener helpbtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.putExtra("isInSetting", true);
			intent.setClass(AboutActivity.this, HelpActivity.class);
			startActivity(intent);
		}
	};
}
