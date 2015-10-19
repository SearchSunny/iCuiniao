/**
 * 
 */
package com.cmmobi.icuiniao.util;

import android.content.Context;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Handler;

/**
 * @author hw
 *
 */
public class SensorUtil implements SensorListener{

	private Handler handler;
	private float x,y,z,last_x,last_y,last_z;
	private long lastUpdate;//记录最后一次判断的时间
	private final static int SHAKE_THRESHOLD = 1000;//控制精度,值越小灵敏度越高
	private final static long TIME = 100;//检测间隔
	
	private SensorManager sensorManager;
	
	public SensorUtil(Context context,Handler handler){
		this.handler = handler;
		sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
	}
	
	public void regeditListener(){
		lastUpdate = System.currentTimeMillis();
		sensorManager.registerListener(this, SensorManager.SENSOR_ACCELEROMETER,SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void unRegeditListener(){
		if(sensorManager != null){
			sensorManager.unregisterListener(this);
		}
	}
	
	@Override
	public void onAccuracyChanged(int sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(int sensor, float[] values) {
		// TODO Auto-generated method stub
		if(sensor == SensorManager.SENSOR_ACCELEROMETER){
			long curTime = System.currentTimeMillis();
			//100毫秒检测一次
			if(curTime-lastUpdate > TIME){
				long diffTime = curTime-lastUpdate;
				if(diffTime <= 0)diffTime = 1;
				lastUpdate = curTime;
				x = values[SensorManager.DATA_X];
				y = values[SensorManager.DATA_X];
				z = values[SensorManager.DATA_X];
				float speed = Math.abs(x+y+z-last_x-last_y-last_z)/diffTime*10000;
				if(speed > SHAKE_THRESHOLD){
					LogPrint.Print("speed = "+speed);
					lastUpdate += 10000;//判断成功的话,10秒内不再判断
					handler.sendEmptyMessage(MessageID.MESSAGE_SENSOR);
				}
				
				last_x = x;
				last_y = y;
				last_z = z;
			}
		}
	}

}
