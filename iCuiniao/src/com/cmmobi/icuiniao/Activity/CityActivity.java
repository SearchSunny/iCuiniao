package com.cmmobi.icuiniao.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.sql.helper.SettingDatabase;
import com.cmmobi.icuiniao.ui.adapter.ArrayWheelAdapter;
import com.cmmobi.icuiniao.ui.view.OnWheelScrollListener;
import com.cmmobi.icuiniao.ui.view.WheelView;

public class CityActivity extends Activity {
	
	private WheelView provinceWheelView = null;
	private WheelView cityWheelView = null;
	private Button okButton = null;
	private Button noButton = null;
	private SettingDatabase settingDatabase = null;
	private String province[] = null;
	private String city[] = null;
	private String area = null;
	private String[] areas = null;
	private int provinceIndex = -1;
	private int cityIndex = -1;
	private ArrayWheelAdapter<String> provinceAdapter = null;
	private ArrayWheelAdapter<String> cityAdapter = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.city);
        settingDatabase = new SettingDatabase(this);
        provinceWheelView = (WheelView)findViewById(R.id.provinceWheelView);
        cityWheelView = (WheelView)findViewById(R.id.cityWheelView);
        area = getIntent().getExtras().getString("area");
        okButton = (Button)findViewById(R.id.okButton);
        noButton = (Button)findViewById(R.id.noButton);       
        okButton.setOnClickListener(okListener);
        noButton.setOnClickListener(noListener); 
        provinceWheelView.setVisibleItems(5);  
        cityWheelView.setVisibleItems(5);
        provinceWheelView.addScrollingListener(scrollListener);
        
    	province = settingDatabase.getProvince();
    	provinceAdapter = new ArrayWheelAdapter<String>(province);
    	provinceWheelView.setAdapter(provinceAdapter);
        if(area!=null&&!area.equals("")){
        	try{
        		areas = area.split(" ");
        		city = settingDatabase.getCity(areas[0]);
        		if(city==null){
                	String provinceName = province[provinceWheelView.getCurrentItem()];
            		city = settingDatabase.getCity(provinceName);
            		cityAdapter = new ArrayWheelAdapter<String>(city);
            		cityWheelView.setAdapter(cityAdapter);
                	provinceWheelView.setCurrentItem(0);
                	cityWheelView.setCurrentItem(0);
        		}
        		else{
        			cityAdapter = new ArrayWheelAdapter<String>(city);
        			cityWheelView.setAdapter(cityAdapter);
        			provinceIndex = provinceWheelView.getItemIndex(province,areas[0]);
        			cityIndex = cityWheelView.getItemIndex(city,areas[1]);
        			provinceWheelView.setCurrentItem(provinceIndex);
        			cityWheelView.setCurrentItem(cityIndex);
        		}
        	}catch(Exception e){
            	String provinceName = province[provinceWheelView.getCurrentItem()];
        		city = settingDatabase.getCity(provinceName);
        		cityAdapter = new ArrayWheelAdapter<String>(city);
        		cityWheelView.setAdapter(cityAdapter);
            	provinceWheelView.setCurrentItem(0);
            	cityWheelView.setCurrentItem(0);
        	}
        }
        else{
        	String provinceName = province[provinceWheelView.getCurrentItem()];
    		city = settingDatabase.getCity(provinceName);
    		cityAdapter = new ArrayWheelAdapter<String>(city);
    		cityWheelView.setAdapter(cityAdapter);
        	provinceWheelView.setCurrentItem(0);
        	cityWheelView.setCurrentItem(0);
        }
        
    }
	
	private OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
		
		@Override
		public void onScrollingStarted(WheelView wheel) {
			
		}
		
		@Override
		public void onScrollingFinished(WheelView wheel) {
			String provinceName = province[wheel.getCurrentItem()];
			city = settingDatabase.getCity(provinceName);
			cityWheelView.setAdapter(new ArrayWheelAdapter<String>(city));
			cityWheelView.setCurrentItem(0);
		}
	};
	
	private OnClickListener okListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int provinceIndex = provinceWheelView.getCurrentItem();
			int cityIndex = cityWheelView.getCurrentItem();
			Intent intent = new Intent(CityActivity.this,PersonalSettingActivity.class);
			String provinceName = province[provinceIndex];
			String cityName = city[cityIndex];
			Bundle bundle = new Bundle();
			bundle.putString("cityName", provinceName+" "+cityName);
			bundle.putBoolean("isCity", true);
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
