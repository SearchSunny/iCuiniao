/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Image;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.DownImageManager;

/**
 * @author hw
 *
 */
public class View_Image extends XmlViewLayout{

	private LinearLayout l;
	private ProgressBar loading;
	private LinearLayout lprogress;
	private Parser_Image parserImage;
	private Button mypage_playbtn;
	private Handler mHandler;
	
	public View_Image(Context context, Parser_Image parserImage, Handler handler,int tag) {
		super(context, handler,null);
		// TODO Auto-generated constructor stub
		mHandler = handler;
		this.parserImage = parserImage;
		Bitmap tmp = BitmapFactory.decodeResource(getResources(), R.drawable.loading1);
		FrameLayout frameLayout = (FrameLayout)((Activity)context).getLayoutInflater().inflate(R.layout.imagelayout,null);
		LinearLayout imagelayout = (LinearLayout)frameLayout.findViewById(R.id.imagelayout);
		mypage_playbtn = (Button)frameLayout.findViewById(R.id.mypage_playbtn);
		if(parserImage.getPlayable()){
			mypage_playbtn.setVisibility(VISIBLE);
			mypage_playbtn.setOnClickListener(clickListener);
		}
		lprogress = (LinearLayout)frameLayout.findViewById(R.id.lprogress);
		lprogress.setPadding(CommonUtil.screen_width/2-tmp.getWidth()/2, CommonUtil.screen_width/2-tmp.getHeight()/2, getRight(), getBottom());
		loading = (ProgressBar)frameLayout.findViewById(R.id.loading);
		l = new LinearLayout(context);
		l.setLayoutParams(new LayoutParams(CommonUtil.screen_width, CommonUtil.screen_width));
		l.setBackgroundColor(0xc9c9c9);
		String url = parserImage.getSrc();
		if(parserImage.getSrc() == null||parserImage.getSrc().equalsIgnoreCase("null")||parserImage.getSrc().equalsIgnoreCase("")){
			url = null;
		}
		
		//压入下载队列
		if(parserImage != null&&parserImage.getValidable()){
			DownImageItem downImageItem = new DownImageItem(Parser_Layout_AbsLayout.MODELTYPE_IMAGE, 0, url, parserImage.getPageId(),tag);
			DownImageManager.add(downImageItem);
		}
		
		imagelayout.addView(l);
		addView(frameLayout);
	}

	public void setImageView(byte[] data){
		loading.setVisibility(GONE);
		Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
		if(temp != null){
			l.setBackgroundDrawable(new BitmapDrawable(temp));
			postInvalidate();
		}
	}
	
	public void setImageViewAddLine(final byte[] data,final String cacheUrl){
		if(cacheUrl != null){
			try {
				//保存图片
				new Thread(){
					public void run(){
						CommonUtil.writeToFile(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(cacheUrl), data);
					}
				}.start();
			} catch (Exception e) {
			}
		}
		loading.setVisibility(GONE);
		Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
		if(temp != null){
			Bitmap line = BitmapFactory.decodeResource(getResources(), R.drawable.share_line);
			l.setBackgroundDrawable(new BitmapDrawable(CommonUtil.mergerTwoBitmap(temp, line, 0, temp.getHeight()-line.getHeight())));
			postInvalidate();
		}
	}
	
	private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserImage != null){
				onMyPagePlayClick(parserImage.getHref());
			}
		}
	};
	
}
