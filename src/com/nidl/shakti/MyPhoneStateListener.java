package com.nidl.shakti;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.nidl.pservice.ScreenService;

public class MyPhoneStateListener extends PhoneStateListener {

	private Context context;

	public MyPhoneStateListener(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		// TODO Auto-generated method stub
		super.onCallStateChanged(state, incomingNumber);
		Intent intent;
		switch (state) {
		case TelephonyManager.CALL_STATE_IDLE:
			Log.d("DEBUG", "IDLE");
			intent = new Intent(ScreenService.ACTION_FOREGROUND);
			intent.setClass(context, ScreenService.class);
			context.startService(intent);
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			Log.d("DEBUG", "OFFHOOK");
			intent = new Intent(ScreenService.ACTION_FOREGROUND);
			intent.setClass(context, ScreenService.class);
			context.stopService(intent);
			break;
		case TelephonyManager.CALL_STATE_RINGING:
			Log.d("DEBUG", "RINGING");
			intent = new Intent(ScreenService.ACTION_FOREGROUND);
			intent.setClass(context, ScreenService.class);
			context.stopService(intent);
			break;
		}
	}

}
