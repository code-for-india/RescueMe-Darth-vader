package com.nidl.pservice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

import com.nidl.gps.ShaktiLocation;
import com.nidl.shakti.IndexScreen;
import com.nidl.shakti.R;
import com.nidl.shakti.SendSms;

public class ScreenService extends Service {

	public static final String ACTION_FOREGROUND = "com.nidl.shakti.SplashScreen";

	private static final Class<?>[] mSetForegroundSignature = new Class[] { boolean.class };
	private static final Class<?>[] mStartForegroundSignature = new Class[] {
			int.class, Notification.class };
	private static final Class<?>[] mStopForegroundSignature = new Class[] { boolean.class };
	private NotificationManager mNM;
	private Method mSetForeground;
	private Method mStartForeground;
	private Method mStopForeground;
	private Object[] mSetForegroundArgs = new Object[1];
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];
	SendSms sendSms;
	Timer timer;
	int delay = 1000; // delay for 1 sec. 
	int period = 10000; // repeat every 10 sec. 
	
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	void invokeMethod(Method method, Object[] args) {
		try {
			method.invoke(this, args);
		} catch (InvocationTargetException e) {
			Log.w("Shakti Service", "Unable to invoke method",
					e.fillInStackTrace());
		} catch (IllegalAccessException e) {
			Log.w("Shakti Service", "Unable to invoke method",
					e.fillInStackTrace());
		}
	}

	void startForegroundCompat(int id, Notification notification) {
		if (mStartForeground != null) {
			mStartForegroundArgs[0] = Integer.valueOf(id);
			mStartForegroundArgs[1] = notification;
			invokeMethod(mStartForeground, mStartForegroundArgs);
			return;
		}

		mSetForegroundArgs[0] = Boolean.TRUE;
		invokeMethod(mSetForeground, mSetForegroundArgs);
		mNM.notify(id, notification);
	}

	private ShaktiLocation location;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// System.out.println("iam in on create ra ");
		
		timer = new Timer();
		location = new ShaktiLocation(ScreenService.this);
		

		location.setScreenService(this);
		location.onShaktiStart();
		
		Thread hrlythread;
		hrlythread = new Thread(runnableService);
		hrlythread.start();
		
		
		sendSms = SendSms.getInstance(ScreenService.this);

		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		try {
			mStartForeground = getClass().getMethod("startForeground",
					mStartForegroundSignature);
			mStopForeground = getClass().getMethod("stopForeground",
					mStopForegroundSignature);

			IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
			filter.addAction(Intent.ACTION_SCREEN_OFF);
			registerReceiver(receiver, filter);
			return;
		} catch (NoSuchMethodException e) {
			// Running on an older platform.
			mStartForeground = mStopForeground = null;
		}
		try {
			mSetForeground = getClass().getMethod("setForeground",
					mSetForegroundSignature);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(
					"OS doesn't have Service.startForeground OR Service.setForeground!");
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try{
		if (sendSms.readStoreLocationValue() == null)
			location.shaktiSetup();
		else if((System.currentTimeMillis() - Long.valueOf(sendSms.readStoreLocationKey()) > 300000))
			location.shaktiSetup();
		}catch(NullPointerException ex){
			location.shaktiSetup();
		}
		
		if(intent != null)
			handleCommand(intent);
		
		
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if(intent != null)
			handleCommand(intent);
		
	}

	void handleCommand(Intent intent) {

		if (intent.getAction() != null && ACTION_FOREGROUND.equals(intent.getAction())) {
			// In this sample, we'll use the same text for the ticker and the
			// expanded notification
			CharSequence text = getText(R.string.foreground_service_started);

			// Set the icon, scrolling text and timestamp
			Notification notification = new Notification(R.drawable.i_m_shakti_icon, text,
					System.currentTimeMillis());

			// The PendingIntent to launch our activity if the user selects this
			// notification
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					new Intent(this, IndexScreen.class), 0);

			// Set the info for the views that show in the notification panel.
			notification.setLatestEventInfo(this, "I'm Shakti", text,
					contentIntent);

			startForegroundCompat(R.string.foreground_service_started,
					notification);
			
			
			
		}
	}

	
	public void setAddress(String address){
//		System.out.println("my service address is "+address);
		this.address = address;
		
		sendSms.serviceData(address, location);
//		System.out.println("my service po address is "+address);
	}
	
	private long mLastClickTime = 0;
	public String address ;

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Message msg = new Message();
			msg.arg1 = ++counter;
			msg.arg2 = (int) SystemClock.uptimeMillis();
			screenOFFHandler.sendMessage(msg);
		}
	};

	int start;
	int counter;
	 Vibrator vi;
	 AudioManager audioManager;
	 
	private Handler screenOFFHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			PowerManager powerManager = (PowerManager) ScreenService.this
					.getSystemService(Context.POWER_SERVICE);
			long l = SystemClock.uptimeMillis();
			vi = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
			audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sound); // in 2nd param u have to pass your desire ringtone
			int diff = 0;
			if (msg.arg1 == 1) {
				start = msg.arg2;
			}
			switch (msg.arg1) {
			case 1:
				break;
			case 2:
				break;
			case 3:
				diff = msg.arg2 - start;

				if ((msg.arg2 - start) > 2500) {
					counter = 0;
				} else if ((msg.arg2 - start) < 2500) {
	
					Log.i(ScreenService.class.getCanonicalName(), "my address is "+address);
					sendSms.isSendSms(address);
					SharedPreferences prefs = getSharedPreferences("checkedState", 0);
					vi.vibrate(100);
					Log.i("ScreenService ", "shared prefs are "+prefs.getBoolean("isChecked", false));
					if(prefs != null && prefs.getBoolean("isChecked", false) && 
							audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT){
					
						SoundPool sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
	
						int iTmp = sp.load(getApplicationContext(), R.raw.sound, 1); 
	
						sp.play(iTmp, 1, 1, 0, 0, 1);
						mPlayer.start();				
					}
				}
				counter = 0;
				break;
			case 4:
				counter = 0;
				break;
			case 5:
				counter = 0;
				break;
			case 6:
				counter = 0;
				break;
			case 7:
				counter = 0;
				break;
			case 8:
				counter = 0;
				break;
			case 9:
				counter = 0;
				break;
			default:
				break;
			}
			//mPlayer.release();//when to release?
			location.onShaktiStop();
			powerManager.userActivity(l, false);// false will bring the screen
												// back as bright as it was,
												// true - will dim it
		}
	};

	
	private OnKeyListener keyListener = new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sound);
			if(keyCode == KeyEvent.KEYCODE_POWER){
				SoundPool sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

				int iTmp = sp.load(getApplicationContext(), R.raw.sound, 1); 

				sp.play(iTmp, 1, 1, 0, 0, 1);
				mPlayer.start();	
			}
			return false;
		}
	};
	
	private Handler mServiceHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			location.onShaktiStart();
			location.shaktiSetup();
			String readValue = sendSms.readStoreLocationValue();

			if (readValue == null) {
				sendSms.storeLocationPrefs(System.currentTimeMillis(), address);
			}
			if (address != null && !address.equals(readValue)) {
				sendSms.deletelocFile();
				sendSms.storeLocationPrefs(System.currentTimeMillis(), address);
			}

			location.onShaktiStop();
			return true;
		}
	});

	private Runnable runnableService = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				try {
					Thread.sleep(600000);
//					Log.d(ScreenService.class.getCanonicalName(), "CHECKING "+Thread.currentThread());
					mServiceHandler.sendEmptyMessage(0);
				} catch (InterruptedException ex) {
					Log.e("SendSms", "error occured", ex.fillInStackTrace());
				} finally {

				}
			}
		}
	};
	
	
	
	

}
