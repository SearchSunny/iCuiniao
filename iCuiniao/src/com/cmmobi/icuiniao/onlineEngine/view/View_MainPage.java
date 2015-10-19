/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Image;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_MainPage;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.DownImageManager;
import com.cmmobi.icuiniao.util.LogPrint;

/**
 * @author hw
 *
 */
public class View_MainPage extends XmlViewLayout{

	private Parser_MainPage parserMainPage;
	private ImageView[] imageViews;
	private Parser_Image[] parserImages;//图片数组
	
	private int[] modelIndex;//大图位置索引
	
	private final static int MODELTYPE_1_BIGIMAGEINDEX = 0;//模板1大图固定位置
	private final static int MODELTYPE_2_BIGIMAGEINDEX = 3;//模板2大图固定位置
	private final static int MODELTYPE_3_BIGIMAGEINDEX = 6;//模板3大图固定位置
	private final static int MODELTYPE_4_BIGIMAGEINDEX = 1;//模板4大图固定位置
	private final static int MODELTYPE_5_BIGIMAGEINDEX = 4;//模板5大图固定位置
	private final static int MODELTYPE_6_BIGIMAGEINDEX = 7;//模板6大图固定位置
	private int smallImageWH;//小图的尺寸
	private int bigImageWH;//大图的尺寸
	
	private String[] childUrls;//9个点击连接
	
	public View_MainPage(Context context, Parser_MainPage parserMainPage,Handler handler,final GestureDetector detector,int tag) {
		super(context,handler,detector);
		// TODO Auto-generated constructor stub
		this.parserMainPage = parserMainPage;
		LinearLayout l = null;
		int bigPos = 0;
		switch (parserMainPage.getType()) {
		case Parser_Layout_AbsLayout.MODELTYPE_1:
			bigPos = MODELTYPE_1_BIGIMAGEINDEX;
			l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.mainpage_modeltype_1, null);
			break;
		case Parser_Layout_AbsLayout.MODELTYPE_2:
			bigPos = MODELTYPE_2_BIGIMAGEINDEX;
			l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.mainpage_modeltype_2, null);
			break;
		case Parser_Layout_AbsLayout.MODELTYPE_3:
			bigPos = MODELTYPE_3_BIGIMAGEINDEX;
			l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.mainpage_modeltype_3, null);
			break;
		case Parser_Layout_AbsLayout.MODELTYPE_4:
			bigPos = MODELTYPE_4_BIGIMAGEINDEX;
			l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.mainpage_modeltype_4, null);
			break;
		case Parser_Layout_AbsLayout.MODELTYPE_5:
			bigPos = MODELTYPE_5_BIGIMAGEINDEX;
			l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.mainpage_modeltype_5, null);
			break;
		case Parser_Layout_AbsLayout.MODELTYPE_6:
			bigPos = MODELTYPE_6_BIGIMAGEINDEX;
			l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.mainpage_modeltype_6, null);
			break;
		}
		
		ArrayList<Parser_Image> tmpArrayList = parserMainPage.getImages();
		parserImages = new Parser_Image[tmpArrayList.size()];
		for(int i = 0;i < parserImages.length;i ++){
			parserImages[i] = tmpArrayList.get(i);
		}
		modelIndex = parserMainPage.getModelIndexs();
		parserImages = order(modelIndex[0],bigPos);
		
		imageViews = new ImageView[parserImages.length];
		imageViews[0] = (ImageView)l.findViewById(R.id.mainpage_img0);
		imageViews[1] = (ImageView)l.findViewById(R.id.mainpage_img1);
		imageViews[2] = (ImageView)l.findViewById(R.id.mainpage_img2);
		imageViews[3] = (ImageView)l.findViewById(R.id.mainpage_img3);
		imageViews[4] = (ImageView)l.findViewById(R.id.mainpage_img4);
		imageViews[5] = (ImageView)l.findViewById(R.id.mainpage_img5);
		imageViews[6] = (ImageView)l.findViewById(R.id.mainpage_img6);
		imageViews[7] = (ImageView)l.findViewById(R.id.mainpage_img7);
		imageViews[8] = (ImageView)l.findViewById(R.id.mainpage_img8);
		
		for(int i = 0;i < imageViews.length;i ++){
			imageViews[i].setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					return detector.onTouchEvent(event);
				}
			});
		}
		
		childUrls = new String[9];
		childUrls[0] = parserImages[0].getHref();
		imageViews[0].setOnClickListener(imageViewClickListener0);
		childUrls[1] = parserImages[1].getHref();
		imageViews[1].setOnClickListener(imageViewClickListener1);
		childUrls[2] = parserImages[2].getHref();
		imageViews[2].setOnClickListener(imageViewClickListener2);
		childUrls[3] = parserImages[3].getHref();
		imageViews[3].setOnClickListener(imageViewClickListener3);
		childUrls[4] = parserImages[4].getHref();
		imageViews[4].setOnClickListener(imageViewClickListener4);
		childUrls[5] = parserImages[5].getHref();
		imageViews[5].setOnClickListener(imageViewClickListener5);
		childUrls[6] = parserImages[6].getHref();
		imageViews[6].setOnClickListener(imageViewClickListener6);
		childUrls[7] = parserImages[7].getHref();
		imageViews[7].setOnClickListener(imageViewClickListener7);
		childUrls[8] = parserImages[8].getHref();
		imageViews[8].setOnClickListener(imageViewClickListener8);
		
		for(int i = 0;i < imageViews.length;i ++){
			String url = parserImages[i].getSrc();
			if(parserImages[i].getSrc() == null||parserImages[i].getSrc().equalsIgnoreCase("null")||parserImages[i].getSrc().equalsIgnoreCase("")){
				url = null;
			}
			//压入下载队列
			DownImageItem tmpDownImageItem = new DownImageItem(Parser_Layout_AbsLayout.MODELTYPE_MAINPAGE, i, url, parserImages[i].getPageId(),tag);
			DownImageManager.add(tmpDownImageItem);
		}
		
		addView(l);
	}
	
	private OnClickListener imageViewClickListener0 = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserImages[0].getHref()!=null&&parserImages[0].getHref().length() > 0)
			onMainPageItemClick(parserImages[0].getHref(), parserImages[0].getType(),0);
		}
	};
	
	private OnClickListener imageViewClickListener1 = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserImages[1].getHref()!=null&&parserImages[1].getHref().length() > 0)
			onMainPageItemClick(parserImages[1].getHref(), parserImages[1].getType(),1);
		}
	};
	
	private OnClickListener imageViewClickListener2 = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserImages[2].getHref()!=null&&parserImages[2].getHref().length() > 0)
			onMainPageItemClick(parserImages[2].getHref(), parserImages[2].getType(),2);
		}
	};
	
	private OnClickListener imageViewClickListener3 = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserImages[3].getHref()!=null&&parserImages[3].getHref().length() > 0)
			onMainPageItemClick(parserImages[3].getHref(), parserImages[3].getType(),3);
		}
	};
	
	private OnClickListener imageViewClickListener4 = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserImages[4].getHref()!=null&&parserImages[4].getHref().length() > 0)
			onMainPageItemClick(parserImages[4].getHref(), parserImages[4].getType(),4);
		}
	};
	
	private OnClickListener imageViewClickListener5 = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserImages[5].getHref()!=null&&parserImages[5].getHref().length() > 0)
			onMainPageItemClick(parserImages[5].getHref(), parserImages[5].getType(),5);
		}
	};
	
	private OnClickListener imageViewClickListener6 = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserImages[6].getHref()!=null&&parserImages[6].getHref().length() > 0)
			onMainPageItemClick(parserImages[6].getHref(), parserImages[6].getType(),6);
		}
	};
	
	private OnClickListener imageViewClickListener7 = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserImages[7].getHref()!=null&&parserImages[7].getHref().length() > 0)
			onMainPageItemClick(parserImages[7].getHref(), parserImages[7].getType(),7);
		}
	};
	
	private OnClickListener imageViewClickListener8 = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserImages[8].getHref()!=null&&parserImages[8].getHref().length() > 0)
			onMainPageItemClick(parserImages[8].getHref(), parserImages[8].getType(),8);
		}
	};
	
	private Parser_Image[] order(int pos,int bigpos){
		int bigImageIndex = bigpos;//固定大图位置
		if(pos == bigImageIndex)return parserImages;
		
		Parser_Image[] tmpImages = new Parser_Image[parserImages.length];
		tmpImages[bigImageIndex] = parserImages[pos];
		for(int i = 0;i < parserImages.length;i ++){
			if(tmpImages[i] == null){
				tmpImages[i] = parserImages[i];
			}else{
				if(i == parserImages.length-1)break;
				tmpImages[i+1] = parserImages[i];
			}
		}
		return tmpImages;
	}
	
	public void setImageView(final byte[] data,int index,final String cacheUrl){
		try{
			new Thread(){
				public void run(){
					if(!CommonUtil.exists(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(cacheUrl))){
						CommonUtil.writeToFile(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(cacheUrl), data);
					}
				}
			}.start();
			
		}catch (Exception e) {
		}
		int bigImagePos = 0;
		switch (parserMainPage.getType()) {
		case Parser_Layout_AbsLayout.MODELTYPE_1:
			bigImagePos = MODELTYPE_1_BIGIMAGEINDEX;
			break;
		case Parser_Layout_AbsLayout.MODELTYPE_2:
			bigImagePos = MODELTYPE_2_BIGIMAGEINDEX;
			break;
		case Parser_Layout_AbsLayout.MODELTYPE_3:
			bigImagePos = MODELTYPE_3_BIGIMAGEINDEX;
			break;
		case Parser_Layout_AbsLayout.MODELTYPE_4:
			bigImagePos = MODELTYPE_4_BIGIMAGEINDEX;
			break;
		case Parser_Layout_AbsLayout.MODELTYPE_5:
			bigImagePos = MODELTYPE_5_BIGIMAGEINDEX;
			break;
		case Parser_Layout_AbsLayout.MODELTYPE_6:
			bigImagePos = MODELTYPE_6_BIGIMAGEINDEX;
			break;
		}
		
		Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
		if(temp == null)return;
		if(index == bigImagePos){
			if(temp.getWidth() != bigImageWH){
				temp = CommonUtil.resizeImage(temp, bigImageWH, bigImageWH);
			}
		}else{
			temp = CommonUtil.resizeImage(temp, smallImageWH, smallImageWH);
		}
		Bitmap playBitmap = null;
		if(parserImages[index].getPlayable()){
			if(index == bigImagePos){
				playBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.playicon_m);
			}else{
				playBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.playicon_s);
			}
		}
		String price = parserImages[index].getPrice();
		String feature = parserImages[index].getFeature();
		int color = 0;
		switch (parserImages[index].getType()) {
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
		imageViews[index].setBackgroundDrawable(new BitmapDrawable(temp));
		postInvalidate();
	}

	public void initImageSize(int dpi){
		LogPrint.Print("dpi = "+dpi);
		if(dpi <= 120){//qvga
			smallImageWH = 60;
			bigImageWH = 126;
		}else if(dpi <= 160){//hvga
			smallImageWH = 95;
			bigImageWH = 196;
		}else if(dpi <= 320){//wvga
			smallImageWH = 140;
			bigImageWH = 290;
			if(CommonUtil.screen_width >= 540&&CommonUtil.screen_height >= 960){
				smallImageWH = 160;
				bigImageWH = 330;
			}else if(CommonUtil.screen_height >= 1280){
				smallImageWH = 217;
				bigImageWH = 448;
				if(CommonUtil.screen_width >= 800){
					smallImageWH = 244;
					bigImageWH = 502;
				}
			}
		}else{//更大屏幕分辨率
			smallImageWH = 160;
			bigImageWH = 330;
		}
		LogPrint.Print("smallImageWH = "+smallImageWH);
		LogPrint.Print("bigImageWH = "+bigImageWH);
	}
	
	public String[] getChildUrls(){
		return childUrls;
	}
	
}
