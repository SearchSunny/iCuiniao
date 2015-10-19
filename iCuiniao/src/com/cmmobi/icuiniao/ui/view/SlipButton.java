package com.cmmobi.icuiniao.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.cmmobi.icuiniao.R;

/**
 * 实现滚动开关按钮
 * @author Administrator
 *
 */
public class SlipButton extends View implements OnTouchListener{

	//记录当前按钮是否打开,true为打开,flase为关闭
    private boolean isOpen = false;
    private boolean isChecked;
    //记录用户是否在滑动的变量
    private boolean isSlip = false;
    //打开 状态下,游标的Rect
    private Rect btn_on;
    //关闭状态下,游标的Rect
    private Rect btn_off;
    private boolean isOnChangedListener = false;
    private OnChangedListener onChangedListener;
    private Bitmap bg_on; 
	private Bitmap bg_off;
	private Bitmap slip_btn;
	
	public SlipButton(Context context) {
		super(context);
		init();
	}
	
	public SlipButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init(){
        bg_on = BitmapFactory.decodeResource(getResources(), R.drawable.slipbuttonopen);
        bg_off = BitmapFactory.decodeResource(getResources(), R.drawable.slipbuttonclose);
        slip_btn = BitmapFactory.decodeResource(getResources(), R.drawable.slip);
        btn_on = new Rect(0, 0, slip_btn.getWidth(), slip_btn.getHeight());
        btn_off = new Rect(bg_off.getWidth() - slip_btn.getWidth(), 0, bg_off.getWidth(),slip_btn.getHeight());
        setOnTouchListener(this);
	}
    //绘图函数
    @Override
    protected void onDraw(Canvas canvas){

        super.onDraw(canvas);
        Matrix matrix = new Matrix();
        Paint paint = new Paint();
        float x;

        if(isChecked){
        	canvas.drawBitmap(bg_on, matrix, paint);// 画出打开时的背景 	
        	isOpen = true;
        	x = bg_off.getWidth() - slip_btn.getWidth();
        }
        else{
        	canvas.drawBitmap(bg_off, matrix, paint);// 画出关闭时的背景
        	isOpen = false;
        	x = 0;
        }
        //画出游标
        canvas.drawBitmap(slip_btn, x, 0, paint);//
    }	
	@Override
    public boolean onTouch(View v, MotionEvent event){
    	//根据动作来执行代码
    	switch (event.getAction()){
            //按下    
            case MotionEvent.ACTION_DOWN:
                if (event.getX() > bg_on.getWidth() || event.getY() > bg_on.getHeight()){
                    return false;
                }
                //打开状态
                if(isOpen){
                	isChecked = false;
                }
                //关闭状态
                else{
                	isChecked = true;
                }
                if(isOnChangedListener) // 如果设置了监听器,就调用其方法..
                	onChangedListener.onChanged(isChecked);
                break;
        }
    	//重画控件
        invalidate();
        return true;
    }

    public void setOnChangedListener(OnChangedListener l){
    	//设置监听器,当状态修改的时候
    	isOnChangedListener = true;
    	onChangedListener = l;
    }

    public void setCheck(boolean isChecked){
        this.isChecked = isChecked;
        isOpen = isChecked;
        //刷新控件
        postInvalidate();
    }
    public boolean getCheck(){
    	return isOpen;
    }
    //回调接口
	public interface OnChangedListener{
		public void onChanged(boolean checkState);
	}
}
