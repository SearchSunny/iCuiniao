/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Image;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.DownImageManager;

/**
 * @author lenovo
 *
 */
public class View_CommodityIcon extends XmlViewLayout{

	private Parser_Image parserImage;
	private ImageView imageView;
	
	private int commodityInfoIcon_width;
	private int commodityInfoIcon_height;
	
	public View_CommodityIcon(Context context, Parser_Image parserImage, Handler handler,int tag) {
		super(context, handler,null);
		// TODO Auto-generated constructor stub
		this.parserImage = parserImage;
		imageView = new ImageView(context);
		imageView.setBackgroundResource(R.drawable.commodityiconbg);
		if(parserImage.getClickable()){
			imageView.setOnClickListener(imageClickListener);
		}
		String url = parserImage.getSrc();
		if(parserImage.getSrc() == null||parserImage.getSrc().equalsIgnoreCase("null")||parserImage.getSrc().equalsIgnoreCase("")){
			url = null;
		}
		//压入下载队列
		DownImageItem downImageItem = new DownImageItem(Parser_Layout_AbsLayout.MODELTYPE_COMMODITYINFO, 1, url, parserImage.getPageId(),tag);
		DownImageManager.add(downImageItem);
		
		addView(imageView);
	}

	public void setImageView(final byte[] data,final String cacheUrl){
		try {
			//保存图片
			new Thread(){
				public void run(){
					CommonUtil.writeToFile(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(cacheUrl), data);
				}
			}.start();
		} catch (Exception e) {
		}
		Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
		if(temp == null)return;
		if(commodityInfoIcon_width == 147){
			temp = CommonUtil.resizeImage(temp, commodityInfoIcon_width+4, commodityInfoIcon_height+4);
		}else{
			temp = CommonUtil.resizeImage(temp, commodityInfoIcon_width+3, commodityInfoIcon_height+3);
		}
		Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.commodityiconbg);
		imageView.setImageBitmap(CommonUtil.mergerIcon(background, temp,15,5,4));
	}
	
	private OnClickListener imageClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onCommodityInfoIconClick(parserImage.getHref());
		}
	};
	
	public void initImageSize(int dpi){
		if(dpi <= 120){//qvga
			commodityInfoIcon_width = 46;
			commodityInfoIcon_height = 46;
		}else if(dpi <= 160){//hvga
			commodityInfoIcon_width = 62;
			commodityInfoIcon_height = 62;
		}else if(dpi <= 320){//wvga
			commodityInfoIcon_width = 92;
			commodityInfoIcon_height = 92;
			if(CommonUtil.screen_height >= 1280){
				commodityInfoIcon_width = 147;
				commodityInfoIcon_height = 147;
			}
		}else{//更大屏幕分辨率
			commodityInfoIcon_width = 92;
			commodityInfoIcon_height = 92;
		}
	}
	
	public int getCommodityInfoIcon_W(){return commodityInfoIcon_width;}
	public int getCommodityInfoIcon_H(){return commodityInfoIcon_height;}
}
