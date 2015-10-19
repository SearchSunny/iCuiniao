/**
 * 
 */
package com.cmmobi.icuiniao.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.cmmobi.icuiniao.util.CommonUtil;

/**
 * @author hw
 *
 */
public class CropView extends LinearLayout{
	
	private int left,top;
	private Paint paint;
	
	public CropView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		paint = new Paint();
		setWillNotDraw(false);//自定义的view需要增加这句代码,否则onDraw不执行
	}

	public CropView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		paint = new Paint();
		setWillNotDraw(false);//自定义的view需要增加这句代码,否则onDraw不执行
	}
	@Override
	public void requestLayout() {
		// TODO Auto-generated method stub
		super.requestLayout();
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		int rectW = CommonUtil.screen_width;
		left = (CommonUtil.screen_width-rectW)/2;
		top = (CommonUtil.screen_height-rectW)/2;
		paint.setColor(0x99000000);
		paint.setStyle(Style.FILL);
		canvas.drawRect(0, 0, rectW, top, paint);
		canvas.drawRect(0, top+rectW, rectW, CommonUtil.screen_height, paint);
		paint.setColor(0xffffffff);
		paint.setStyle(Style.STROKE);
		canvas.drawRect(left, top, left+rectW-1, top+rectW-1, paint);
	}
	
}
