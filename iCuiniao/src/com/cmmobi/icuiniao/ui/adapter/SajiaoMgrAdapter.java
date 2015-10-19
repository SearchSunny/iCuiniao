package com.cmmobi.icuiniao.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.AddSajiaoObjActivity;
import com.cmmobi.icuiniao.entity.SajiaoObj;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.MessageID;

public class SajiaoMgrAdapter extends BaseAdapter{
	
	private ArrayList<SajiaoObj> arrSajiao;
	private LayoutInflater mInflater;
	private Handler handler;
	private Context context;
	private int currSajiao;//当前选中的撒娇对象	
	
	public void setCurrSajiao(int currSajiao) {
		this.currSajiao = currSajiao;
	}
	public void setArrSajiao(ArrayList<SajiaoObj> arrSajiao) {
		this.arrSajiao = arrSajiao;
	}

	public SajiaoMgrAdapter(Context context, Handler handler){
		arrSajiao = new ArrayList<SajiaoObj>();
		mInflater = LayoutInflater.from(context);
		this.handler = handler;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrSajiao.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
//		LogPrint.Print("list", "pos=" + position);
		Holder holder = null;
		if(convertView == null){
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.listitem_sajiaomgr, null);
			holder.check = (Button)convertView.findViewById(R.id.check);
			holder.delBtn = (Button)convertView.findViewById(R.id.delBtn);
			holder.modifyBtn = (Button)convertView.findViewById(R.id.modifyBtn);
			holder.emailAddr = (TextView)convertView.findViewById(R.id.emailAddr);
			holder.name = (TextView)convertView.findViewById(R.id.name);
			holder.line_email = (LinearLayout)convertView.findViewById(R.id.line_email);
			holder.line_name = (LinearLayout)convertView.findViewById(R.id.line_name);
			convertView.setTag(holder);
		}else{
			holder = (Holder)convertView.getTag();
		}
		final SajiaoObj sajiaoObj = arrSajiao.get(position);
		holder.emailAddr.setText(sajiaoObj.email);
		holder.name.setText(sajiaoObj.name);
		holder.check.setTag(position);
		holder.delBtn.setTag(position);
		holder.modifyBtn.setTag(position);
		holder.line_email.setTag(position);
		holder.line_name.setTag(position);
		if(currSajiao == position){
			holder.check.setBackgroundResource(R.drawable.addrcheck1);							
		}else{
			holder.check.setBackgroundResource(R.drawable.addrcheck0);			
		}
		holder.check.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//只有一项时，不用取消
				if(arrSajiao.size() == 1){
					return;
				}
				if(currSajiao != position){
					v.setBackgroundResource(R.drawable.addrcheck1);
					currSajiao = position;
				}else{
					v.setBackgroundResource(R.drawable.addrcheck0);
					currSajiao = -1;					
				}
				CommonUtil.saveCurSajiaoObj(context, currSajiao);
				notifyDataSetChanged();
			}
		});
		holder.delBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.arg1 = (Integer)v.getTag();
				msg.what = MessageID.MESSAGE_DEL_ADDRESS;				
				handler.sendMessage(msg);				
			}
		});
		holder.modifyBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				gotoModify(v);				
			}
		});
		holder.line_email.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				gotoModify(v);
				
			}
		});
		holder.line_name.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				gotoModify(v);
				
			}
		});
		return convertView;
	}
	
	private void gotoModify(View v){
		Intent intent = new Intent(context, AddSajiaoObjActivity.class);				
		intent.putExtra("SajiaoObj", arrSajiao.get((Integer)v.getTag()));
		intent.putExtra("index", ((Integer)v.getTag()).intValue());
		context.startActivity(intent);
	}
	
	public final class Holder{
		public Button check;
		public Button delBtn;
		public Button modifyBtn;
		public TextView emailAddr;
		public TextView name;
		public LinearLayout line_email;
		public LinearLayout line_name;
	}	

}
