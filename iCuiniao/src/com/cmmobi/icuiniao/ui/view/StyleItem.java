/**
 * 
 */
package com.cmmobi.icuiniao.ui.view;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.util.CommonUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author hw
 *
 */
public class StyleItem extends LinearLayout{

	private ImageView styleitem_img;
	private TextView styleitem_str;
	public boolean isrenderover;
	private byte[] data;//图片数据
	
	public StyleItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		isrenderover = false;
		LinearLayout l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.styleitem,null);
		styleitem_img = (ImageView)l.findViewById(R.id.styleitem_img);
		styleitem_str = (TextView)l.findViewById(R.id.styleitem_str);
		addView(l);
	}
	
	//初次创建时使用
	public void setImage(byte[] data,String cacheUrl,boolean isSelected){
		if(data != null){
			if(this.data == null){
				this.data = data;
			}
			//写缓存
			try {
				if(!CommonUtil.exists(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(cacheUrl))){
					CommonUtil.writeToFile(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(cacheUrl), data);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			Bitmap temp = createRoundImage(BitmapFactory.decodeByteArray(data, 0, data.length),20);
			if(isSelected){
				temp = drawMark(temp, 20);
			}
			styleitem_img.setImageBitmap(temp);
			isrenderover = true;
		}
	}
	
	//状态改变时使用
	public void setImage(boolean isSelected){
		Bitmap temp = createRoundImage(BitmapFactory.decodeByteArray(data, 0, data.length),20);
		if(isSelected){
			temp = drawMark(temp, 20);
		}
		styleitem_img.setImageBitmap(temp);
		isrenderover = true;
	}
	
	public void setStr(String str){
		if(str != null){
			styleitem_str.setText(str);
		}
	}
	
	private Bitmap createRoundImage(Bitmap bitmap,int r){
		Bitmap out = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(out);  
  
        final int color = 0xff000000;  
        final Paint paint = new Paint();  
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
        final RectF rectF = new RectF(rect);  
        final float roundPx = r;  
  
        paint.setAntiAlias(true);  
        canvas.drawARGB(0, 0, 0, 0);  
        paint.setColor(color);  
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
  
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //画包边
        paint.setColor(0xffacacac);
    	paint.setStyle(Style.STROKE);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.save();
        
        return out;
	}
	
	private Bitmap drawMark(Bitmap src,int r){
		Canvas canvas = new Canvas(src);
		final int color = 0x99000000;  
        final Paint paint = new Paint();  
        final Rect rect = new Rect(0, 0, src.getWidth(), src.getHeight());  
        final RectF rectF = new RectF(rect);  
        final float roundPx = r;
        paint.setColor(color);
        paint.setStyle(Style.FILL);
    	canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
    	Bitmap right = BitmapFactory.decodeResource(getResources(), R.drawable.stylepage_right);
    	paint.setColor(0xffffffff);
    	canvas.drawBitmap(right, src.getWidth()-right.getWidth()-10, src.getHeight()-right.getHeight()-10, paint);
    	canvas.save();
    	
    	return src;
	}

}
