/**
 * 
 */
package com.cmmobi.icuiniao.ui.view;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;

/**
 * @author hw
 *自定义动画效果
 */
public class MyAnimation extends Animation{

	//图片的中心点坐标
	private int centerX, centerY;
	//类型
	private int type;
	public final static int RIGHT_IN = 1;
	public final static int RIGHT_OUT = 2;
	public final static int LEFT_IN = 3;
	public final static int LEFT_OUT = 4;
	private static boolean isNext;
	public MyAnimation(int type,boolean isnext){
		this.type = type;
		centerX = CommonUtil.screen_width/2;
		centerY = CommonUtil.screen_height/2;
		isNext = isnext;
	}
	
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		// TODO Auto-generated method stub
		super.applyTransformation(interpolatedTime, t);
		LogPrint.Print("animation","interpolatedTime = "+interpolatedTime);
		Matrix matrix = t.getMatrix();
		Camera camera = new Camera();
		camera.save();
		switch (type) {
		case RIGHT_IN:
			LogPrint.Print("animation","right_in:rotateY = "+(180-interpolatedTime*180));
			camera.rotateY(180-interpolatedTime*180);
			t.setAlpha(interpolatedTime);
			break;
		case RIGHT_OUT:
			LogPrint.Print("animation","right_out:rotateY = "+(-interpolatedTime*180));
			camera.rotateY(-interpolatedTime*180);
			t.setAlpha((1.0f-interpolatedTime));
			break;
		case LEFT_IN:
			LogPrint.Print("animation","left_in:rotateY = "+(-(180-interpolatedTime*180)));
			camera.rotateY(-(180-interpolatedTime*180));
			t.setAlpha(interpolatedTime);
			break;
		case LEFT_OUT:
			LogPrint.Print("animation","left_out:rotateY = "+(interpolatedTime*90));
			camera.rotateY(interpolatedTime*180);
			t.setAlpha((1.0f-interpolatedTime));
			break;
		}
		camera.translate(0, 0, 0);
		camera.getMatrix(matrix);
		camera.restore();
		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
		camera.save();
	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		// TODO Auto-generated method stub
		super.initialize(width, height, parentWidth, parentHeight);
		setDuration(500);
//		setFillAfter(true);
//		setInterpolator(new LinearInterpolator());
		LogPrint.Print("animation","Animation init");
		LogPrint.Print("animation","type_before = "+type);
		if(isNext){
			if(type == LEFT_IN)type = RIGHT_IN;
			if(type == LEFT_OUT)type = RIGHT_OUT;
		}else{
			if(type == RIGHT_IN)type = LEFT_IN;
			if(type == RIGHT_OUT)type = LEFT_OUT;
		}
		LogPrint.Print("animation","type_after = "+type);
	}
	
	
}
