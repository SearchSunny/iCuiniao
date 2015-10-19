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
import com.cmmobi.icuiniao.util.OfflineLog;

/**
 * @author hw
 *首页用gallery
 */
public class MainPageGallery extends Gallery{

	private Context mContext;
	private boolean isLast;
	private MotionEvent e;
	public int selectPosition;
	
	public MainPageGallery(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		selectPosition = 0;
	}
	
	public MainPageGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		selectPosition = 0;
	}
	
	public MainPageGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		selectPosition = 0;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		isMoved = false;
		if(e1.getRawX()-e2.getRawX() > 40){
			isMoved = true;
		}
		if(Math.abs(velocityX) >= 450){
			int keycode = velocityX < 0?KeyEvent.KEYCODE_DPAD_RIGHT:KeyEvent.KEYCODE_DPAD_LEFT;
			if(velocityX < 0){
				selectPosition ++;
				if(selectPosition >= 5){
					selectPosition = 5;
				}
			}else{
				selectPosition --;
				if(selectPosition <= 0){
					selectPosition = 0;
				}
			}
			onKeyDown(keycode, null);
		}else{
			OfflineLog.writeMainPageFling();//写入离线日志
			if(getSelectedItemPosition() == 5){
				if(!isLast){
					isLast = true;
					return false;
				}
				if(isMoved){
					isMoved = false;
					Intent intent = new Intent();
					intent.setClass(mContext, RockActivityA.class);
					intent.putExtra("fromscreen", 0);
					mContext.startActivity(intent);
					((Activity)mContext).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
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
		OfflineLog.writeMainPageFling();//写入离线日志
		if(selectPosition == 5){
			if(!isLast){
				isLast = true;
				return super.onKeyDown(keyCode, event);
			}
			if(isMoved){
				isMoved = false;
				Intent intent = new Intent();
				intent.setClass(mContext, RockActivityA.class);
				intent.putExtra("fromscreen", 0);
				mContext.startActivity(intent);
				((Activity)mContext).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		}else{
			isLast = false;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void setSelection(int index){
		selectPosition = index;
		if(selectPosition == 5){
			isLast = true;
		}else{
			isLast = false;
		}
		super.setSelection(index);
	}
}
