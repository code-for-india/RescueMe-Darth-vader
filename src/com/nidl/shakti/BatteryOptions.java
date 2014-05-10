package com.nidl.shakti;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class BatteryOptions extends Activity {

	private CheckBox bluetooth;
	private CheckBox brightness;
	private CheckBox wifi;
	private CheckBox cellular_data;
	private CheckBox kill_application;
	private CheckBox select_all;
	private Typeface typeface;
	private TextView mheading;
	private TextView note;
	Button above_options;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enable_batterysaving_mode_layout);
		
		typeface = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
		initialize();
//		fetchAndSet();
		
	}
	
	
	
	private void initialize(){
		mheading = (TextView)this.findViewById(R.id.textView1);
		mheading.setTypeface(typeface);
		
		note = (TextView)this.findViewById(R.id.note);
		note.setTypeface(typeface);
		
		select_all = (CheckBox)this.findViewById(R.id.chk_selectall);
		select_all.setTypeface(typeface);
//		select_all.setOnCheckedChangeListener(changeListener);
		
		bluetooth  = (CheckBox)this.findViewById(R.id.checkBox2enablebluetooth);
		bluetooth.setTypeface(typeface);
//		bluetooth.setOnCheckedChangeListener(changeListener);
		
		brightness = (CheckBox)this.findViewById(R.id.checkBox1enablebrightness);
		brightness.setTypeface(typeface);
//		brightness.setOnCheckedChangeListener(changeListener);
		
		wifi = (CheckBox)this.findViewById(R.id.checkBox3enablewifi);
		wifi.setTypeface(typeface);
//		wifi.setOnCheckedChangeListener(changeListener);
		
		cellular_data = (CheckBox)this.findViewById(R.id.checkBox5enablecellulardata);
		cellular_data.setTypeface(typeface);
//		cellular_data.setOnCheckedChangeListener(changeListener);
		
		kill_application = (CheckBox)this.findViewById(R.id.checkBox8enablekillapp);
		kill_application.setTypeface(typeface);
//		kill_application.setOnCheckedChangeListener(changeListener);
		
		above_options = (Button)this.findViewById(R.id.button1enableSet);
		above_options.setTypeface(typeface);
		above_options.setOnClickListener(clickListener);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.item1:
			Intent intent = new Intent(BatteryOptions.this, ShaktiPreference.class);
			startActivity(intent);
			break;
		case R.id.item2:
			Intent aboutIntent = new Intent();
			aboutIntent.setAction("com.nidl.shakti.ABOUT_SHAKTI");
			startActivity(aboutIntent);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		/*this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));*/
		
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		unregisterReceiver(mConnReceiver);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	};
	
	private BluetoothAdapter mBluetoothAdapter;
	WifiManager wifiManager;
	
	private void isBluetoothON(){
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBluetoothAdapter.isEnabled())
			bluetooth.setEnabled(true);
		else
			bluetooth.setEnabled(false);
	}
	
	
	
	
	private void isMobileDataON(){
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mobileConnection = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileConnection.isConnected()) 
			cellular_data.setEnabled(true);
		else
			cellular_data.setEnabled(false);
	}
	
	/*private int brightnessValue(){
		int value = 0;
		try {
			value = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
			if((value/225) > 10)
				brightness.setEnabled(true);
			else
				brightness.setEnabled(false);
			return value;
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			Log.e(BatteryOptions.class.getCanonicalName(), "error in brightness", e.fillInStackTrace());
		}
		return value;
	}*/
	
	private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(bluetooth.isChecked()){
				mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				
				if(mBluetoothAdapter.isEnabled())
					mBluetoothAdapter.disable();
			}
			if(brightness.isChecked()){
				WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
				int brightnessMode;
				try {
					brightnessMode = Settings.System.getInt(getContentResolver(),
					        Settings.System.SCREEN_BRIGHTNESS_MODE);
					if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
						Settings.System.putInt(getContentResolver(),
								Settings.System.SCREEN_BRIGHTNESS_MODE,
								Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
					}
					
					layoutParams = getWindow().getAttributes();
					layoutParams.screenBrightness = 0.1F;
					getWindow().setAttributes(layoutParams);
					android.provider.Settings.System.putInt(getContentResolver(), 
	                        android.provider.Settings.System.SCREEN_BRIGHTNESS, 25);
				} catch (SettingNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(wifi.isChecked()){
				wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				if(wifiManager.isWifiEnabled()){
					wifi.setEnabled(false);
				    wifiManager.setWifiEnabled(false);
				  }else{
					  wifi.setEnabled(true);  
				  }
			}
			if(cellular_data.isChecked()){
				isMobileDataON();
			}
			if(kill_application.isChecked()){
				killApplications();
			}
			select_all.setChecked(false);
			bluetooth.setChecked(false);
			brightness.setChecked(false);
			wifi.setChecked(false);
			cellular_data.setChecked(false);
			kill_application.setChecked(false);
			above_options.setText("Going Back");
			bThread = new Thread(mRunnable);
			bThread.start();
		}
	};
	
	Thread bThread;
	
	private Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try{
				Thread.sleep(2000);
			}catch(InterruptedException ex){
				Log.e(BatteryOptions.class.getCanonicalName(), "error occured", ex.fillInStackTrace());
			}finally{
				finish();
				if(bThread != null)
					bThread =  null;
			}
		}
	};
	
	private void killApplications(){
		String nameOfProcess = "com.nidl.shakti";
		ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> listOfProcess = manager.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : listOfProcess) {
			if(runningAppProcessInfo.processName.contains(nameOfProcess)){
				Log.e("Proccess" , runningAppProcessInfo.processName + " : " + runningAppProcessInfo.pid);
			}else{
				Log.e("Proccess" , runningAppProcessInfo.processName + " : " + runningAppProcessInfo.pid);
				android.os.Process.killProcess(runningAppProcessInfo.pid);
                android.os.Process.sendSignal(runningAppProcessInfo.pid, android.os.Process.SIGNAL_KILL);
                manager.killBackgroundProcesses(runningAppProcessInfo.processName);
			}
		}
	}
	
	ContentResolver resolver;
	ContentResolver resolver1;
	ContentResolver resolver2;
	
	private void fetchAndSet() {
		// TODO Auto-generated method stub
		resolver=getContentResolver();
		
		try {
			
			int value=android.provider.Settings.System.getInt(resolver, android.provider.Settings.System.SCREEN_BRIGHTNESS);

		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resolver.registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, new BrightNessObserver(handler));
	}
	
	

	
	/*private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        @Override
		public void onReceive(Context context, Intent intent) {
           

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

            if(currentNetworkInfo.isConnected()){
            	wifi.setEnabled(true);
            }else{
            	wifi.setEnabled(false);
            }
        }
    };
	*/
    
    
	
	private Handler handler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				try {
					int value = android.provider.Settings.System.getInt(resolver, android.provider.Settings.System.SCREEN_BRIGHTNESS);
					
					if(value > 30)
						brightness.setEnabled(true);
					else
						brightness.setEnabled(false);
						
				} catch (SettingNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
			
			case 2:
				try {
					int value = android.provider.Settings.System.getInt(resolver2, android.provider.Settings.System.BLUETOOTH_ON);
					System.out.println("my bluetooth on is "+value);
					if(value == 0)
						bluetooth.setEnabled(true);
					else
						bluetooth.setEnabled(false);
						
				} catch (SettingNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
			return false;
		}
	});
	
	
	
	
	private  class BrightNessObserver extends ContentObserver{

		
		public BrightNessObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
			
		}
		
		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			Message msg = new Message();
			msg.what = 0;
			handler.sendMessage(msg);
		}
	}
	
	
	
	
	
	
}
