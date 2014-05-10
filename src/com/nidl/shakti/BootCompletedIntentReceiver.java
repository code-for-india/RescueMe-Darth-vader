package com.nidl.shakti;

import com.nidl.pservice.ScreenService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {  
			intent = new Intent(ScreenService.ACTION_FOREGROUND);
			intent.setClass(context, ScreenService.class);
			context.startService(intent);
		} 
	}

}
