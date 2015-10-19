/**
 * 
 */
package com.cmmobi.icuiniao.ui.view;

import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;

/**
 * @author hw
 *补间平滑
 */
public class MyBothAnimation extends Animation{

	//类型
	private int type;
	public final static int RIGHT_IN = 1;
	public final static int RIGHT_OUT = 2;
	public final static int LEFT_IN = 3;
	public final static int LEFT_OUT = 4;
	private static boolean isNext;
	
	public MyBothAnimation(int type,boolean isnext){
		this.type = type;
		isNext = isnext;
	}
	
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		// TODO Auto-generated method stub
		super.applyTransformation(interpolatedTime, t);
		Matrix matrix = t.getMatrix();
		switch (type) {
		case RIGHT_IN:
			matrix.setTranslate(CommonUtil.screen_width-interpolatedTime*CommonUtil.screen_width, 0);
			break;
		case RIGHT_OUT:
			matrix.setTranslate(-interpolatedTime*CommonUtil.screen_width,0);
			break;
		case LEFT_IN:
			matrix.setTranslate(-CommonUtil.screen_width+interpolatedTime*CommonUtil.screen_width,0);
			break;
		case LEFT_OUT:
			matrix.setTranslate(interpolatedTime*CommonUtil.screen_width,0);
			break;
		}
	}
	
	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		// TODO Auto-generated method stub
		super.initialize(width, height, parentWidth, parentHeight);
		setDuration(500);
		LogPrint.Print("animation","isNext = "+isNext);
		LogPrint.Print("animation","type = "+type);
		if(isNext){
			if(type == LEFT_IN)type = RIGHT_IN;
			if(type == LEFT_OUT)type = RIGHT_OUT;
		}else{
			if(type == RIGHT_IN)type = LEFT_IN;
			if(type == RIGHT_OUT)type = LEFT_OUT;
		}
	}
}
