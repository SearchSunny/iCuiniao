/**
 * 
 */
package com.cmmobi.icuiniao.util;

import android.os.HandlerThread;
import android.os.Message;
import android.os.Handler.Callback;

/**
 * @author hw
 *
 */
public class MHandlerThread extends HandlerThread implements Callback{

	public MHandlerThread(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		LogPrint.Print("handlerMessage","curHanderThread: "+Thread.currentThread().getName());
		return false;
	}

}
