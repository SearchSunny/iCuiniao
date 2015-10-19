/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.util.CommonUtil;

/**
 * @author hw
 *兴趣
 */
public class InterestActivity extends Activity{

	private Button line11;
	private Button line12;
	private Button line21;
	private Button line22;
	private Button line31;
	private Button line32;
	private Button line41;
	private Button line42;
	private Button line51;
	private Button line52;
	private Button line53;
	private Button line61;
	
	private boolean bline11;
	private boolean bline12;
	private boolean bline21;
	private boolean bline22;
	private boolean bline31;
	private boolean bline32;
	private boolean bline41;
	private boolean bline42;
	private boolean bline51;
	private boolean bline52;
	private boolean bline53;
	private boolean bline61;
	
	private String interestStr;
	private List<Button> selectInterest;
	private Boolean[] selectBline;
	private EditText others;
	//其它兴趣的值
	String otherStr;
	StringBuffer otherStrBuffer = new StringBuffer();
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interest);
      
        interestStr = getIntent().getExtras().getString("interestStr");
        //其它标签
        others = (EditText)findViewById(R.id.others_input);
        Button titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
        line11 = (Button)findViewById(R.id.line11);
        line12 = (Button)findViewById(R.id.line12);
        line21 = (Button)findViewById(R.id.line21);
        line22 = (Button)findViewById(R.id.line22);
        line31 = (Button)findViewById(R.id.line31);
        line32 = (Button)findViewById(R.id.line32);
        line41 = (Button)findViewById(R.id.line41);
        line42 = (Button)findViewById(R.id.line42);
        line51 = (Button)findViewById(R.id.line51);
        line52 = (Button)findViewById(R.id.line52);
        line53 = (Button)findViewById(R.id.line53);
        line61 = (Button)findViewById(R.id.line61);
        
        bline11 = false;
        bline12 = false;
        bline21 = false;
        bline22 = false;
        bline31 = false;
        bline32 = false;
        bline41 = false;
        bline42 = false;
        bline51 = false;
        bline52 = false;
        bline53 = false;
        bline61 = false;
        
        selectBline = new Boolean[12];
        selectBline[0] = bline11;
        selectBline[1] = bline12;
        selectBline[2] = bline21;
        selectBline[3] = bline22;
        selectBline[4] = bline31;
        selectBline[5] = bline32;
        selectBline[6] = bline41;
        selectBline[7] = bline42;
        selectBline[8] = bline51;
        selectBline[9] = bline52;
        selectBline[10] = bline53;
        selectBline[11] = bline61;
        
        selectInterest = new ArrayList<Button>();
        selectInterest.add(line11);
        selectInterest.add(line12);
        selectInterest.add(line21);
        selectInterest.add(line22);
        selectInterest.add(line31);
        selectInterest.add(line32);
        selectInterest.add(line41);
        selectInterest.add(line42);
        selectInterest.add(line51);
        selectInterest.add(line52);
        selectInterest.add(line53);
        selectInterest.add(line61);
        
        if(interestStr.contains("#")){
        	
        	String[] interests = interestStr.split("#");
        	for (int i = 0; i < interests.length; i++) {
        		for (int j = 0; j < selectInterest.size(); j++) {
        			if(interests[i].trim().equals(selectInterest.get(j).getText().toString().trim()) ){
    					
        				selectInterest.get(j).setBackgroundResource(R.drawable.local_btn_f);
    					
    					selectBline[j] = true;
						
    				}
				}
        		
        		
			}
        	//取出其它兴趣标签的值
        	if(CommonUtil.getOtherInterest(InterestActivity.this).length() > 0){
        		
        		otherStr = CommonUtil.getOtherInterest(InterestActivity.this);
        		if(otherStr.contains("#")){
        			
        			String[] otherSplit = otherStr.split("#");
        			for (int i = 0; i < interests.length; i++) {
						
        				for (int j = 0; j < otherSplit.length; j++) {
							
        					if(interests[i].trim().equals(otherSplit[j].trim())){
        						otherStrBuffer.append(otherSplit[j]+" ");
        						others.setText(otherStrBuffer.toString());
        					}
						}
					}
        			
        		}else{
        			
        		   others.setText(otherStr);
        			
        		}
        	}
        }
        if(!interestStr.trim().equals("") && !interestStr.contains("#")){
        	
        	for (int j = 0; j < selectInterest.size(); j++) {
    			if(interestStr.trim().equals(selectInterest.get(j).getText().toString().trim()) ){
					
    				selectInterest.get(j).setBackgroundResource(R.drawable.local_btn_f);
					
					selectBline[j] = true;
					
				}
			}
        	//取出其它兴趣标签的值
        	if(CommonUtil.getOtherInterest(InterestActivity.this).length() > 0){
        		
        		otherStr = CommonUtil.getOtherInterest(InterestActivity.this);
        		others.setText(otherStr);
        	}
        }
        
        
        titlebar_backbutton.setOnClickListener(backClickListener);
        line11.setOnClickListener(line11ClickListener);
        line12.setOnClickListener(line12ClickListener);
        line21.setOnClickListener(line21ClickListener);
        line22.setOnClickListener(line22ClickListener);
        line31.setOnClickListener(line31ClickListener);
        line32.setOnClickListener(line32ClickListener);
        line41.setOnClickListener(line41ClickListener);
        line42.setOnClickListener(line42ClickListener);
        line51.setOnClickListener(line51ClickListener);
        line52.setOnClickListener(line52ClickListener);
        line53.setOnClickListener(line53ClickListener);
        line61.setOnClickListener(line61ClickListener);
	}
	
	public void finish(){
		Intent intent = new Intent();
		StringBuffer sBuffer = new StringBuffer();
		//存放其它标签
		StringBuffer otherBuffer = new StringBuffer();
		if(bline11 || selectBline[0]){
			sBuffer.append(line11.getText().toString()+"#");
		}
		if(bline12 || selectBline[1]){
			sBuffer.append(line12.getText().toString()+"#");
		}
		if(bline21 || selectBline[2]){
			sBuffer.append(line21.getText().toString()+"#");
		}
		if(bline22 || selectBline[3]){
			sBuffer.append(line22.getText().toString()+"#");
		}
		if(bline31 || selectBline[4]){
			sBuffer.append(line31.getText().toString()+"#");
		}
		if(bline32 || selectBline[5]){
			sBuffer.append(line32.getText().toString()+"#");
		}
		if(bline41 || selectBline[6]){
			sBuffer.append(line41.getText().toString()+"#");
		}
		if(bline42 || selectBline[7]){
			sBuffer.append(line42.getText().toString()+"#");
		}
		if(bline51 || selectBline[8]){
			sBuffer.append(line51.getText().toString()+"#");
		}
		if(bline52 || selectBline[9]){
			sBuffer.append(line52.getText().toString()+"#");
		}
		if(bline53 || selectBline[10]){
			sBuffer.append(line53.getText().toString()+"#");
		}
		if(bline61 || selectBline[11]){
			sBuffer.append(line61.getText().toString()+"#");
		}
		//其它标签
		if(!others.getText().toString().equals("") && others.getText().toString() != null){
			
			if(others.getText().toString().trim().contains(" ")){
				
				String[] enterStr = others.getText().toString().split(" ");
				for (int i = 0; i < enterStr.length; i++) {
					sBuffer.append(enterStr[i].trim()+"#");
					otherBuffer.append(enterStr[i].trim()+"#");
				}
			}
			else if(others.getText().toString().trim().contains(",")){
				
				String[] enterStr = others.getText().toString().split(",");
				for (int i = 0; i < enterStr.length; i++) {
					sBuffer.append(enterStr[i].trim()+"#");
					otherBuffer.append(enterStr[i].trim()+"#");
				}
			}
			else if(others.getText().toString().trim().contains("，")){
				
				String[] enterStr = others.getText().toString().split("，");
				for (int i = 0; i < enterStr.length; i++) {
					sBuffer.append(enterStr[i].trim()+"#");
					otherBuffer.append(enterStr[i].trim()+"#");
				}
			}else{
				
				sBuffer.append(others.getText().toString().trim()+"#");
				otherBuffer.append(others.getText().toString().trim()+"#");
			}
			
			String tmpOther = "";
			if(otherBuffer.length() > 0){
				if(otherBuffer.toString().indexOf("#") > 0){//将最后一个#去掉
					tmpOther = otherBuffer.toString().substring(0, otherBuffer.length()-1);
				}else{
					tmpOther = otherBuffer.toString();
				}
			}
			otherBuffer.delete(0, otherBuffer.length());
			otherBuffer = null;
			//将其它标签存储
			CommonUtil.saveOtherInterest(InterestActivity.this, tmpOther);
		}
		
		String tmp = "";
		if(sBuffer.length() > 0){
			if(sBuffer.toString().indexOf("#") > 0){//将最后一个#去掉
				tmp = sBuffer.toString().substring(0, sBuffer.length()-1);
			}else{
				tmp = sBuffer.toString();
			}
		}
		sBuffer.delete(0, sBuffer.length());
		sBuffer = null;
		intent.putExtra("result", tmp);
		setResult(903, intent);
		super.finish();
	}
	
	private OnClickListener backClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	};
	
	private OnClickListener line11ClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			selectBline[0] = !selectBline[0];
			if(selectBline[0]){
				line11.setBackgroundResource(R.drawable.local_btn_f);
			}else{
				line11.setBackgroundResource(R.drawable.local_btn);
			}
		}
	};
	
	private OnClickListener line12ClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			selectBline[1] = !selectBline[1];
			if(selectBline[1]){
				line12.setBackgroundResource(R.drawable.local_btn_f);
			}else{
				line12.setBackgroundResource(R.drawable.local_btn);
			}
		}
	};
	
	private OnClickListener line21ClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			selectBline[2] = !selectBline[2];
			if(selectBline[2]){
				line21.setBackgroundResource(R.drawable.local_btn_f);
			}else{
				line21.setBackgroundResource(R.drawable.local_btn);
			}
		}
	};
	
	private OnClickListener line22ClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			selectBline[3] = !selectBline[3];
			if(selectBline[3]){
				line22.setBackgroundResource(R.drawable.local_btn_f);
			}else{
				line22.setBackgroundResource(R.drawable.local_btn);
			}
		}
	};
	
	private OnClickListener line31ClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			selectBline[4] = !selectBline[4];
			if(selectBline[4]){
				line31.setBackgroundResource(R.drawable.local_btn_f);
			}else{
				line31.setBackgroundResource(R.drawable.local_btn);
			}
		}
	};
	
	private OnClickListener line32ClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			selectBline[5] = !selectBline[5];
			if(selectBline[5]){
				line32.setBackgroundResource(R.drawable.local_btn_f);
			}else{
				line32.setBackgroundResource(R.drawable.local_btn);
			}
		}
	};
	
	private OnClickListener line41ClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			selectBline[6] = !selectBline[6];
			if(selectBline[6]){
				line41.setBackgroundResource(R.drawable.local_btn_f);
			}else{
				line41.setBackgroundResource(R.drawable.local_btn);
			}
		}
	};
	
	private OnClickListener line42ClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			selectBline[7] = !selectBline[7];
			if(selectBline[7]){
				line42.setBackgroundResource(R.drawable.local_btn_f);
			}else{
				line42.setBackgroundResource(R.drawable.local_btn);
			}
		}
	};
	
	private OnClickListener line51ClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			selectBline[8] = !selectBline[8];
			if(selectBline[8]){
				line51.setBackgroundResource(R.drawable.local_btn_f);
			}else{
				line51.setBackgroundResource(R.drawable.local_btn);
			}
		}
	};
	
	private OnClickListener line52ClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			selectBline[9] = !selectBline[9];
			if(selectBline[9]){
				line52.setBackgroundResource(R.drawable.local_btn_f);
			}else{
				line52.setBackgroundResource(R.drawable.local_btn);
			}
		}
	};
	
	private OnClickListener line53ClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			selectBline[10] = !selectBline[10];
			if(selectBline[10]){
				line53.setBackgroundResource(R.drawable.local_btn_f);
			}else{
				line53.setBackgroundResource(R.drawable.local_btn);
			}
		}
	};
	
	private OnClickListener line61ClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			selectBline[11] = !selectBline[11];
			if(selectBline[11]){
				line61.setBackgroundResource(R.drawable.local_btn_f);
			}else{
				line61.setBackgroundResource(R.drawable.local_btn);
			}
		}
	};
}
