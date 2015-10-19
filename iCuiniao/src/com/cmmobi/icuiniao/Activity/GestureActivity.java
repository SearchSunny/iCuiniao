  /**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import java.util.ArrayList;

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.os.Bundle;
import android.widget.Toast;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.util.ActionID;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.OfflineLog;

/**
 * @author hw
 *
 *手势操作逻辑
 */
  public class GestureActivity extends Activity implements OnGesturePerformedListener{
	
	//手势文件索引
	private final static String GESTURE_RIGHT = "ges_right";//对钩
	private final static String GESTURE_CIRCLE = "ges_circle";//圆圈
	private final static String GESTURE_TRIANGLE = "ges_triangle";//三角
	private final static String GESTURE_RIGHT_1 = "ges_right1";//反对钩
	private final static String GESTURE_CIRCLE_1 = "ges_circle1";//反圆圈
	private final static String GESTURE_TRIANGLE_1 = "ges_triangle1";//反三角
	private final static String GESTURE_TRIANGLE_2 = "ges_triangle2";//倒三角
	
	private final static String GESTURE_RIGHT_H = "ges_right_h";//对钩（横屏）
	private final static String GESTURE_CIRCLE_H = "ges_circle_h";//圆圈（横屏）
	private final static String GESTURE_TRIANGLE_H = "ges_triangle_h";//三角（横屏）
	private final static String GESTURE_RIGHT_1_H = "ges_right1_h";//反对钩（横屏）
	private final static String GESTURE_CIRCLE_1_H = "ges_circle1_h";//反圆圈（横屏）
	private final static String GESTURE_TRIANGLE_1_H = "ges_triangle1_h";//反三角（横屏）
	private final static String GESTURE_TRIANGLE_2_H = "ges_triangle2_h";//倒三角（横屏）
	
	private GestureLibrary gestureLibrary;
	//用于数据广播
	private Intent broadcastIntent;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gusture);
        if(loadGesture() == false){
        	Toast.makeText(this, R.string.gesture_load_false, Toast.LENGTH_LONG).show();
        }
    }

    //加载手势文件
    public boolean loadGesture(){
    	boolean isLoadSuccess = false;
    	
    	if(gestureLibrary == null){
    		gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.mygestures);
    	}
    	
    	isLoadSuccess = gestureLibrary.load();
    	if(isLoadSuccess){
    		GestureOverlayView gestureOverlayView = (GestureOverlayView)findViewById(R.id.gestureview);
    		//设置GesturePerformedListener事件
    		gestureOverlayView.addOnGesturePerformedListener(this);
    	}
    	
    	return isLoadSuccess;
    }

  	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		// TODO Auto-generated method stub
		LogPrint.Print("onGesturePerformed");
		//获得可能匹配的手势
		ArrayList<Prediction> predictions = gestureLibrary.recognize(gesture);
		//有可能匹配的手势
		if(predictions.size() > 0){
			//手势匹配记数
			int resultCount = 0;
			for(int i = 0;i < predictions.size();i ++){
				Prediction prediction = predictions.get(i);
				LogPrint.Print("score = "+prediction.score);
				if(prediction.score > 1.0){
					if(GESTURE_RIGHT.equals(prediction.name)||GESTURE_RIGHT_1.equals(prediction.name)||
							GESTURE_RIGHT_H.equals(prediction.name)||GESTURE_RIGHT_1_H.equals(prediction.name)){//匹配对钩
						LogPrint.Print("right");
						OfflineLog.writeGestureRight((byte)1);//写入离线日志
						broadcastIntent = new Intent(ActionID.ACTION_BROADCAST_GESTURE_RIGHT);
						sendBroadcast(broadcastIntent);
					}else if(GESTURE_CIRCLE.equals(prediction.name)||GESTURE_CIRCLE_1.equals(prediction.name)||
							GESTURE_CIRCLE_H.equals(prediction.name)||GESTURE_CIRCLE_1_H.equals(prediction.name)){//匹配圆圈
						LogPrint.Print("circle");
						OfflineLog.writeGestureCircle((byte)1);//写入离线日志
						broadcastIntent = new Intent(ActionID.ACTION_BROADCAST_GESTURE_CIRCLE);
						sendBroadcast(broadcastIntent);
  					}else if(GESTURE_TRIANGLE.equals(prediction.name)||GESTURE_TRIANGLE_1.equals(prediction.name)||GESTURE_TRIANGLE_2.equals(prediction.name)||
  							GESTURE_TRIANGLE_H.equals(prediction.name)||GESTURE_TRIANGLE_1_H.equals(prediction.name)||GESTURE_TRIANGLE_2_H.equals(prediction.name)){//匹配三角
						LogPrint.Print("triangle");
						OfflineLog.writeGestureTriangle((byte)1);//写入离线日志
						broadcastIntent = new Intent(ActionID.ACTION_BROADCAST_GESTURE_TRIANGLE);
						sendBroadcast(broadcastIntent);
					}
					
					resultCount ++;
					finish();
					break;
				}
			}
			
			//无匹配到任何手势的处理
			if(resultCount == 0){
				LogPrint.Print("no");
				broadcastIntent = new Intent(ActionID.ACTION_BROADCAST_GESTURE_NO_RESULT);
				sendBroadcast(broadcastIntent);
				finish();
			}
		}
	}
}
