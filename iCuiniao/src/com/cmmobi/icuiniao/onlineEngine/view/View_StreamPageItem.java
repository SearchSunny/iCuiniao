/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Image;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.DownImageManager;
import com.cmmobi.icuiniao.util.LogPrint;

/**
 * @author hw
 *瀑布流子项
 */
public class View_StreamPageItem extends LinearLayout{

	private ImageView streampage_image;
	private ImageView streampage_mark;
	private LinearLayout l;
	
	private Parser_Image parserImage;
	
	public View_StreamPageItem(Context context,Parser_Image parserImage,int index) {
		super(context);
		// TODO Auto-generated constructor stub
		this.parserImage = parserImage;
		l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.streampageitem,null);
		streampage_image = (ImageView)l.findViewById(R.id.streampageitem);
		streampage_mark = (ImageView)l.findViewById(R.id.streampageitem_mark);
		
		String url = null;
		if(parserImage.getSrc()==null||parserImage.getSrc().equalsIgnoreCase("null")||parserImage.getSrc().equalsIgnoreCase("")){
			url = null;
		}else{
			url = parserImage.getSrc();
		}
		//压入下载队列
		DownImageItem downImageItem = new DownImageItem(Parser_Layout_AbsLayout.MODELTYPE_STREAMPAGE, index, url, parserImage.getPageId(),0);
		DownImageManager.add(downImageItem);
		
		addView(l);
	}
	
	public void setImageView(byte[] data){
		Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
		if(temp == null)return;
		
		temp = CommonUtil.resizeImage(temp, smallImageWH, smallImageWH);
		Bitmap playBitmap = null;
		if(parserImage.getPlayable()){
			playBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.playicon_s);
		}
		String price = parserImage.getPrice();
		String feature = parserImage.getFeature();
		int color = 0;
		switch (parserImage.getType()) {
		case Parser_Layout_AbsLayout.TYPE_DISCOUNT://折扣
			color = 0xff0000ff;
			break;
		case Parser_Layout_AbsLayout.TYPE_RECOMMEND://推荐
			color = 0xffff00ff;
			break;
		case Parser_Layout_AbsLayout.TYPE_FORM://晒单
			color = 0xffffff00;
			break;
		case Parser_Layout_AbsLayout.TYPE_ACTIVITIES://活动
			color = 0xff00ff00;
			break;
		default://异常情况,type为空
			color = 0x00ffffff;	
			break;
		}
		temp = CommonUtil.mergerBitmapForMainPage(getContext(),temp, color, playBitmap,price,feature);
		streampage_image.setBackgroundDrawable(new BitmapDrawable(temp));
		postInvalidate();
	}
	
	public Parser_Image getParserImage(){
		return parserImage;
	}
	
	int smallImageWH;
	public void initImageSize(int dpi){
		LogPrint.Print("dpi = "+dpi);
		if(dpi <= 120){//qvga
			smallImageWH = 60;
		}else if(dpi <= 160){//hvga
			smallImageWH = 95;
		}else if(dpi <= 320){//wvga
			smallImageWH = 140;
			if(CommonUtil.screen_width >= 640&&CommonUtil.screen_height >= 960){
				smallImageWH = 185;
			}
			else if(CommonUtil.screen_width >= 540&&CommonUtil.screen_height >= 960){
				smallImageWH = 160;
			}
			else if(CommonUtil.screen_height >= 1280){
				smallImageWH = 217;
				if(CommonUtil.screen_width >= 800){
					smallImageWH = 244;
				}
			}
		}else{//更大屏幕分辨率
			smallImageWH = 160;
		}
	}
	
	public void mark(boolean useMark){
		if(useMark){
			streampage_mark.setVisibility(VISIBLE);
		}else{
			streampage_mark.setVisibility(INVISIBLE);
		}
	}
	
	public boolean getMarkState(){
		return streampage_mark.isShown();
	}
	
	public int getId(){
		if(parserImage != null){
			return parserImage.getId();
		}
		return -1;
	}

}
