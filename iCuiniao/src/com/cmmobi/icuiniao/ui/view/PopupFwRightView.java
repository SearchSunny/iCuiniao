  /**
 * 
 */
package com.cmmobi.icuiniao.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;

/**
 * @author hw
 *
 *向右弹出式菜单view的展开关闭逻辑实现
 */
  public class PopupFwRightView extends LinearLayout{
	
	public Paint mPaint;
	
	public boolean mIsAnimation;
	public Bitmap mBgLeft_Bitmap;
	public Bitmap mBgMiddle_Bitmap;
	public Bitmap mBgRight_Bitmap;
	public int mRenderSpeed;
	
	//绘制的实际尺寸，总宽度-left-right
	public int mDrawWidth;
	public int mBgLeftWidth;
	public int mBgRightWidth;
	
	//已绘制长度
	public int mDrawCurLength;
	
	//组建状态
	public int mPopupState;
	public final static int POPUP_POPING_OPEN = 1;//打开中
	public final static int POPUP_POPING_CLOSE = 2;//关闭中
	public final static int POPUP_OPEN_OVER = 3;//打开完成
	public final static int POPUP_CLOSE_OVER = 4;//关闭完成
	
	public Handler popupHandler;
	
	public PopupFwRightView(Context context){
		super(context);
	}

  	public PopupFwRightView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mPaint = new Paint();
		mPopupState = 0;
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PopupFwRightView);
		//获取相关属性
		int mBgLeftResID = array.getResourceId(R.styleable.PopupFwRightView_bg_leftResID, -1);
		int mBgMiddleResID = array.getResourceId(R.styleable.PopupFwRightView_bg_middleResID, -1);
		int mBgRightResID = array.getResourceId(R.styleable.PopupFwRightView_bg_rightResID, -1);
		mIsAnimation = array.getBoolean(R.styleable.PopupFwRightView_animation, true);
		mRenderSpeed = array.getInteger(R.styleable.PopupFwRightView_renderSpeed, 1);
  		mBgLeft_Bitmap = BitmapFactory.decodeResource(getResources(), mBgLeftResID);
		mBgMiddle_Bitmap = BitmapFactory.decodeResource(getResources(), mBgMiddleResID);
		mBgRight_Bitmap = BitmapFactory.decodeResource(getResources(), mBgRightResID);
		mBgLeftWidth = mBgLeft_Bitmap.getWidth();
		mBgRightWidth = mBgRight_Bitmap.getWidth();
		array.recycle();
		setWillNotDraw(false);//自定义的view需要增加这句代码,否则onDraw不执行
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		mDrawWidth = getWidth()-getPaddingLeft()-getPaddingRight()-mBgLeftWidth-mBgRightWidth;
		
		switch (mPopupState) {
		case POPUP_POPING_OPEN:
			//绘制左边图片
			canvas.drawBitmap(mBgLeft_Bitmap, 0, 0, mPaint);
			//绘制已有长度
			for(int i = 0;i < mDrawCurLength;i ++){
				canvas.drawBitmap(mBgMiddle_Bitmap, mBgLeftWidth+i, 0, mPaint);
			}
  			//绘制新增长度
			for(int i = 0;i < mRenderSpeed;i ++){
				if(mDrawCurLength >= mDrawWidth){
					break;
				}
				canvas.drawBitmap(mBgMiddle_Bitmap, mBgLeftWidth+mDrawCurLength, 0, mPaint);
				mDrawCurLength ++;
			}
			//绘制右边图片
			canvas.drawBitmap(mBgRight_Bitmap, mBgLeftWidth+mDrawCurLength, 0, mPaint);
			
			if(mDrawCurLength >= mDrawWidth){//动画结束
				mHandler.sendEmptyMessage(MessageID.MESSAGE_POPUPMENU_POP_OPENOVER);
			}else{//动画未结束
				mHandler.sendEmptyMessage(MessageID.MESSAGE_POPUPMENU_POPING_OPEN);
			}
			break;
  		case POPUP_POPING_CLOSE:
  			if(lbutton != null){
  				lbutton.setVisibility(INVISIBLE);
  			}
  			//绘制左边图片
			canvas.drawBitmap(mBgLeft_Bitmap, 0, 0, mPaint);
			if(mDrawCurLength < mRenderSpeed){
				mDrawCurLength = 0;
			}else{
				//计算已有长度
				mDrawCurLength -= mRenderSpeed;
				if(mDrawCurLength < 0){
					mDrawCurLength = mRenderSpeed + mDrawCurLength;
				}
			}
			//绘制已有长度
			for(int i = 0;mDrawCurLength > 0&i < mDrawCurLength;i ++){
				canvas.drawBitmap(mBgMiddle_Bitmap, mBgLeftWidth+i, 0, mPaint);
			}
			//绘制右边图片
			canvas.drawBitmap(mBgRight_Bitmap, mBgLeftWidth+mDrawCurLength, 0, mPaint);
			
			if(mDrawCurLength <= 0){//动画结束
				mHandler.sendEmptyMessage(MessageID.MESSAGE_POPUPMENU_POP_CLOSEOVER);
			}else{//动画未结束
				mHandler.sendEmptyMessage(MessageID.MESSAGE_POPUPMENU_POPING_CLOSE);
			}
			break;
		}
	}
	
	//获得handler
	public void setHandler(Handler handler){
		popupHandler = handler;
	}
	
	public void setPopupState(int state){
		mPopupState = state;
	}
	
	private LinearLayout lbutton;
	public void setLButton(LinearLayout lbutton){
		this.lbutton = lbutton;
	}
	
	public Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MessageID.MESSAGE_POPUPMENU_POPING_OPEN:
				mPopupState = POPUP_POPING_OPEN;
				postInvalidate();
				break;
			case MessageID.MESSAGE_POPUPMENU_POP_OPENOVER:
				popupHandler.sendEmptyMessage(MessageID.MESSAGE_POPUPMENU_POP_OPENOVER);
				break;
			case MessageID.MESSAGE_POPUPMENU_POPING_CLOSE:
				mPopupState = POPUP_POPING_CLOSE;
				postInvalidate();
				break;
			case MessageID.MESSAGE_POPUPMENU_POP_CLOSEOVER:
				popupHandler.sendEmptyMessage(MessageID.MESSAGE_POPUPMENU_POP_CLOSEOVER);
				mPopupState = POPUP_POPING_OPEN;
				mDrawCurLength = 0;
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	
	public boolean isOpenAnimationOver(){
		LogPrint.Print("============mDrawCurLength = "+mDrawCurLength);
		LogPrint.Print("============mDrawWidth = "+mDrawWidth);
		if(mDrawCurLength >= mDrawWidth){
			return true;
		}
		return false;
	}
	
	public boolean isCloseAnimationOver(){
		LogPrint.Print("============mDrawCurLength = "+mDrawCurLength);
		if(mDrawCurLength <= mRenderSpeed){
			return true;
		}
		return false;
	}
}
