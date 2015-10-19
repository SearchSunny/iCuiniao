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
import com.cmmobi.icuiniao.Activity.AddAddressActivityA;
import com.cmmobi.icuiniao.entity.AddressInfo;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;

public class AddressMgrAdapter extends BaseAdapter{
	private ArrayList<AddressInfo> arrAddress;
	private LayoutInflater mInflater;
	private Handler handler;
	private Context context;
	private int currAddrIndex;
	
	public void setCurrAddrIndex(int currAddrIndex) {
		this.currAddrIndex = currAddrIndex;
	}

	public void setArrAddress(ArrayList<AddressInfo> arrAddress) {
		this.arrAddress = arrAddress;
	}	

	
	public AddressMgrAdapter(Context context, Handler handler){
		arrAddress = new ArrayList<AddressInfo>();
		mInflater = LayoutInflater.from(context);
		this.handler = handler;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrAddress.size();
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
		LogPrint.Print("list", "pos=" + position);
		Holder holder = null;
		if(convertView == null){
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.listitem_addrmgr, null);
			holder.check = (Button)convertView.findViewById(R.id.check);
			holder.delBtn = (Button)convertView.findViewById(R.id.delBtn);
			holder.modifyBtn = (Button)convertView.findViewById(R.id.modifyBtn);
			holder.address = (TextView)convertView.findViewById(R.id.address);
			holder.mobile =  (TextView)convertView.findViewById(R.id.mobile);
			holder.name = (TextView)convertView.findViewById(R.id.name);
			holder.line_addr = (LinearLayout)convertView.findViewById(R.id.line_addr);
			holder.line_phone = (LinearLayout)convertView.findViewById(R.id.line_phone);
			holder.line_name = (LinearLayout)convertView.findViewById(R.id.line_name);
			convertView.setTag(holder);
		}else{
			holder = (Holder)convertView.getTag();
		}
		final AddressInfo addrInfo = arrAddress.get(position);
		holder.address.setText(addrInfo.addr);
		holder.mobile.setText(addrInfo.phone);
		holder.name.setText(addrInfo.name);
		holder.check.setTag(position);
		holder.delBtn.setTag(position);
		holder.modifyBtn.setTag(position);
		holder.line_addr.setTag(position);
		holder.line_phone.setTag(position);
		holder.line_name.setTag(position);
		if(currAddrIndex == position){
			holder.check.setBackgroundResource(R.drawable.addrcheck1);							
		}else{
			holder.check.setBackgroundResource(R.drawable.addrcheck0);			
		}
		holder.check.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//只有一项时，不用取消
				if(arrAddress.size() == 1){
					return;
				}				
				if(currAddrIndex != position){
					v.setBackgroundResource(R.drawable.addrcheck1);
					currAddrIndex = position;
				}else{
					v.setBackgroundResource(R.drawable.addrcheck0);
					currAddrIndex = -1;					
				}
				CommonUtil.saveCurReceiveAddr(context, currAddrIndex);
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
		holder.line_addr.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoModify(v);
			}
		});
		holder.line_phone.setOnClickListener(new View.OnClickListener() {

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
		Intent intent = new Intent(context, AddAddressActivityA.class);				
		intent.putExtra("addressInfo", arrAddress.get((Integer)v.getTag()));
		intent.putExtra("index", ((Integer)v.getTag()).intValue());
		context.startActivity(intent);
	}
	
	public final class Holder{
		public Button check;
		public Button delBtn;
		public Button modifyBtn;
		public TextView address;
		public TextView mobile;
		public TextView name;
		public LinearLayout line_addr;
		public LinearLayout line_phone;
		public LinearLayout line_name;
	}	

}
