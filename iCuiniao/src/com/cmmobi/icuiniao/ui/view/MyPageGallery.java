/**
 * 
 */
package com.cmmobi.icuiniao.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.activity.RockActivityA;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.PageID;

/**
 * @author hw
 *单品页用gallery
 */
public class MyPageGallery extends Gallery{

	private Context mContext;
	private boolean isLast;
	private MotionEvent e;
	private int max;
	private MyPageSoftReference reference;
	private int type;
	public int selectPosition;
	
	public MyPageGallery(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		selectPosition = 0;
	}
	
	public MyPageGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		selectPosition = 0;
	}
	
	public MyPageGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		selectPosition = 0;
	}
	
	public void setMax(int max){
		this.max = max;
	}
	
	public void setReference(MyPageSoftReference reference){
		this.reference = reference;
	}
	
	public void setType(int type){
		this.type = type;
	}
	/**
	 * 指在触摸屏上迅速移动，并松开的动作
	 * e1 .手势起点的移动事件
	 * e2 .当前手势点的移动事件
	 * velocityX 每秒x轴方向移动的像素
	 * velocityY 每秒y轴方向移动的像素
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		isMoved = false;
		if(e1.getRawX()-e2.getRawX() > 40){
			isMoved = true;
		}
		if(selectPosition == 0){
			selectPosition = getSelectedItemPosition();
		}
		//返回 int 值的绝对值。如果参数为非负数，则返回该参数。如果参数为负数，则返回该参数的相反数
		if(Math.abs(velocityX) >= 450){
			if(velocityX < 0){
				selectPosition ++;
				if(selectPosition >= max-1){
					selectPosition = max-1;
				}
			}else{
				selectPosition --;
				if(selectPosition <= 0){
					selectPosition = 0;
				}
			}
			if(reference.get(""+selectPosition) != null){
				reference.get(""+selectPosition).isFling = true;
			}
			int keycode = velocityX < 0?KeyEvent.KEYCODE_DPAD_RIGHT:KeyEvent.KEYCODE_DPAD_LEFT;
			onKeyDown(keycode, null);
		}else{
			if(reference.get(""+selectPosition) != null){
				reference.get(""+selectPosition).isFling = true;
			}
			if(selectPosition == max-1){
				if(!isLast){
					isLast = true;
					return false;
				}
				if(isMoved){
					isMoved = false;
					if(type == PageID.PAGEID_MAINPAGE_ALL){
						Intent intent = new Intent();
						intent.setClass(mContext, RockActivityA.class);
						intent.putExtra("fromscreen", 1);
						mContext.startActivity(intent);
						((Activity)mContext).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
					}else{
						CommonUtil.ShowToast(mContext, "别翻了，到头了！ ",false);
					}
				}
			}else{
				isLast = false;
			}
		}
		return false;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean bb = super.onInterceptTouchEvent(ev);
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			e = MotionEvent.obtain(ev);
			super.onTouchEvent(ev);
		} else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			// 手指触摸的大小
			if(getSelectedItemPosition() == 0){
				if(ev.getX()-e.getX() > CommonUtil.screen_width/6){
					CommonUtil.ShowToast(mContext, "别翻了，到头了！ ",false);
				}
			}
			if (Math.abs(ev.getX() - e.getX()) > CommonUtil.screen_width/6) {
				bb = true;
			}
		}
		return bb;
	}

	private boolean isMoved;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(selectPosition == max-1){
			if(!isLast){
				isLast = true;
				return super.onKeyDown(keyCode, event);
			}
			if(isMoved){
				isMoved = false;
				if(type == PageID.PAGEID_MAINPAGE_ALL){
					Intent intent = new Intent();
					intent.setClass(mContext, RockActivityA.class);
					intent.putExtra("fromscreen", 1);
					mContext.startActivity(intent);
					((Activity)mContext).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				}else{
					CommonUtil.ShowToast(mContext, "别翻了，到头了！ ",false);
				}
			}
		}else{
			isLast = false;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void setSelection(int index){
		selectPosition = index;
		if(selectPosition == max-1){
			isLast = true;
		}else{
			isLast = false;
		}
		super.setSelection(index);
	}
}
