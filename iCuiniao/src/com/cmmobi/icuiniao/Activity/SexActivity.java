package com.cmmobi.icuiniao.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.ui.adapter.ArrayWheelAdapter;
import com.cmmobi.icuiniao.ui.view.WheelView;

public class SexActivity extends Activity {

	private WheelView sexWheelView = null;
	private Button okButton = null;
	private Button noButton = null;
	private String sex[] = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		
		setContentView(R.layout.sex);
		sexWheelView = (WheelView)findViewById(R.id.sexWheelView);
		okButton = (Button)findViewById(R.id.okButton);
		noButton = (Button)findViewById(R.id.noButton);
		sex = new String[] {"男", "女"};
		sexWheelView.setVisibleItems(3);
		sexWheelView.setAdapter(new ArrayWheelAdapter<String>(sex));
		okButton.setOnClickListener(okListener);
		noButton.setOnClickListener(noListener);
		int sexInt = getIntent().getExtras().getInt("sex");
		sexWheelView.setCurrentItem(sexInt);
	}
	
	private OnClickListener okListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int index = sexWheelView.getCurrentItem();
			Intent intent = new Intent(SexActivity.this,PersonalSettingActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("sex", sex[index]);
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			finish();
		}
	};
	
	private OnClickListener noListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	
	
}
