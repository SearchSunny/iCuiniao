/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Coquetryitem;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.DownImageManager;

/**
 * @author hw
 *我的撒娇子项
 */
public class View_CoquetryItem extends LinearLayout{

	private boolean isMoreItem;//是否为"更多"
	private TextView coquetry_time;
	private TextView coquetry_to;	
	private ImageView coquetry_image;
	private TextView listItem_more;
	private ImageView coquetry_cover;
	
	private Parser_Coquetryitem parserCoquetryitem;
	
	public View_CoquetryItem(Context context,boolean isMoreitem,Parser_Coquetryitem parserCoquetryitem,int index,boolean hide) {
		super(context);
		// TODO Auto-generated constructor stub
		this.isMoreItem = isMoreitem;
		this.parserCoquetryitem = parserCoquetryitem;
		String url = null;
		if(isMoreitem){
			LinearLayout l;
			l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.listitem_coquetry_more,null);
			l.setGravity(Gravity.CENTER_HORIZONTAL);
			listItem_more = (TextView)l.findViewById(R.id.listItem_coquetryitem);
			listItem_more.setGravity(Gravity.CENTER_HORIZONTAL);
			addView(l);
		}else{
			RelativeLayout r;
			r = (RelativeLayout)((Activity)context).getLayoutInflater().inflate(R.layout.listitem_coquetry,null);
			coquetry_time = (TextView)r.findViewById(R.id.coquetry_time);
			coquetry_to = (TextView)r.findViewById(R.id.coquetry_to);
			coquetry_image = (ImageView)r.findViewById(R.id.coquetry_image);
			coquetry_cover = (ImageView)r.findViewById(R.id.coquetry_cover);
			setState(hide);
//			coquetry_time.setSingleLine(true);
			coquetry_to.setSingleLine(true);
			if(parserCoquetryitem.getImage().getSrc()==null||parserCoquetryitem.getImage().getSrc().equalsIgnoreCase("null")||parserCoquetryitem.getImage().getSrc().equalsIgnoreCase("")){
				url = null;
			}else{
				url = parserCoquetryitem.getImage().getSrc();
			}
			//压入下载队列
			DownImageItem downImageItem = new DownImageItem(Parser_Layout_AbsLayout.MODELTYPE_COQUETRYITEM, index, url, parserCoquetryitem.getPageId(),0);
			DownImageManager.add(downImageItem);
			
			addView(r);
		}
		
	}
	
	public boolean getIsMoreItem(){
		return isMoreItem;
	}
	
	public LinearLayout addItem(){
		if(!isMoreItem){
			String temp = parserCoquetryitem.getTime().getStr();
			temp = temp.replaceAll("#", "\n");
			coquetry_time.setText(temp);
			coquetry_to.setText(parserCoquetryitem.getTo().getStr());
		}else{
			listItem_more.setText("查看更多");
		}
		return this;
	}
	
	public void setImageView(byte[] data){
		Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
		if(temp == null)return;
		coquetry_image.setImageBitmap(temp);
	}
	
	public Parser_Coquetryitem getParserCoquetryitem(){
		return parserCoquetryitem;
	}
	
	public void setState(boolean hide){
		if(coquetry_cover != null){
			if(hide){
				coquetry_cover.setVisibility(INVISIBLE);
			}else{
				coquetry_cover.setVisibility(VISIBLE);
			}
		}
	}
	
	public int getDeleteId(){
		return parserCoquetryitem.getImage().getId();
	}
	
}
