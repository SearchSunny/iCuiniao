/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.menuclick.AbsMenuClick;
import com.cmmobi.icuiniao.ui.adapter.CuiniaoListAdapter;

/**
 * @author hw
 *
 */
public class AbsCuiniaoMenu extends Activity{

	private static AbsMenuClick absMenuClick;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cuiniao_menu);
        TextView cuiniaoList_title = (TextView)findViewById(R.id.cuiniaoList_title);
        ListView cuiniao_list = (ListView)findViewById(R.id.cuiniao_list);
        String title = getIntent().getExtras().getString("title");
        String[] tmps = getIntent().getExtras().getStringArray("items");
        
        cuiniaoList_title.setText(title);
        ArrayList<String> items = new ArrayList<String>();
        for(int i = 0;i < tmps.length;i ++){
        	items.add(tmps[i]);
        }
        CuiniaoListAdapter adapter = new CuiniaoListAdapter(items, this);
        cuiniao_list.setAdapter(adapter);
        cuiniao_list.setOnItemClickListener(itemClickListener);
	}
	
	//按键监听
	public OnItemClickListener itemClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			absMenuClick.click(arg2);
			finish();
		}
		
	};
	
	public static void set(AbsMenuClick _absMenuClick){
		absMenuClick = _absMenuClick;
	}
	
	public void finish(){
		absMenuClick.cancel();
		super.finish();
	}
}
