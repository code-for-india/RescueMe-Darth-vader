package com.nidl.shakti;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nidl.gps.ShaktiLocation;
import com.nidl.pservice.ScreenService;

public class IndexScreen extends Activity {

	Button toggle_slint;
	Button battery_saving;
	Button sms_emergency;
	TextView quick_send;

	Typeface typeface;
	String SENT = "SMS_SENT";
	String DELIVERED = "SMS_DELIVERED";
	
	ShaktiLocation location;
	SendSms sendSms;
	public String mlocAddress;
	LinearLayout turn_layout;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.indexscreen);
		
		initialize();
		
		location = new ShaktiLocation(IndexScreen.this);
		location.setIndexActivity(this);

	}

	

	private void initialize() {
		
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		typeface = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
		sms_emergency = (Button) this.findViewById(R.id.sms_emergency);
		sms_emergency.setTypeface(typeface);
		sms_emergency.setOnClickListener(listener);
		
		battery_saving = (Button) this.findViewById(R.id.battery_options);
		battery_saving.setTypeface(typeface);
		battery_saving.setOnClickListener(listener);
		
		toggle_slint = (Button) this.findViewById(R.id.toggle_slient);
		toggle_slint.setTypeface(typeface);
		if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) 
			toggle_slint.setText("Disable\nSilent\nMode");
		else
			toggle_slint.setText(" Enable\nSilent\nMode");
		toggle_slint.setOnClickListener(listener);

		quick_send = (Button) this.findViewById(R.id.quick_send);
		quick_send.setTypeface(typeface);
		quick_send.setOnClickListener(listener);
		
		TextView turn_text = (TextView)this.findViewById(R.id.turn_on);
		turn_text.setTypeface(typeface);
		
		
		Button turn_on = (Button) this.findViewById(R.id.turnon);
		turn_on.setTypeface(typeface);
		turn_on.setOnClickListener(listener);
		
		Button emergency = (Button) this.findViewById(R.id.emergency);
		emergency.setOnClickListener(listener);
		emergency.setTypeface(typeface);
		
		turn_layout = (LinearLayout)this.findViewById(R.id.turn_layout);
		
		
	}

	

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			switch (v.getId()) {
			
			
			case R.id.sms_emergency:
				
				Log.d(IndexScreen.class.getCanonicalName(), "sms_emergency");
				Intent smsEmergency = new Intent();
				smsEmergency.setAction("com.nidl.shakti.SMS_EMERGENCY");
				startActivity(smsEmergency);
				break;
			
			case R.id.battery_options:
				
				Log.d(IndexScreen.class.getCanonicalName(), "battery options");
				Intent batteryIntent = new Intent();
				batteryIntent.setAction("com.nidl.shakti.BATTERY_OPTIONS");
				startActivity(batteryIntent);
				break;
			
			case R.id.toggle_slient:

				Log.d(IndexScreen.class.getCanonicalName(), "Toggle slient");
				if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
					audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
					Toast.makeText(IndexScreen.this, "Silent Mode Disabled",
							Toast.LENGTH_SHORT).show();
					toggle_slint.setText(" Enable\nSilent\nMode");

				} else {
					audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					Toast.makeText(IndexScreen.this, "Silent Mode Enabled",
							Toast.LENGTH_SHORT).show();
					toggle_slint.setText("Disable\nSilent\nMode");

				}
				
				break;
			case R.id.quick_send:
				
				if(mlocAddress == null)
					mlocAddress = "Location not found";
				if(sendSms.readStoreLocationValue() == null)
					sendSms.storeLocationPrefs(System.currentTimeMillis(), mlocAddress);
//					Log.d(IndexScreen.class.getCanonicalName(), "maddress "+mlocAddress);
				sendSms.data(mlocAddress, location);
				break;
			case R.id.turnon:
				enableLocationSettings();
			case R.id.emergency:
				Intent intent = new Intent(IndexScreen.this,Emergency.class);
				startActivity(intent);
			}
		}
	};

	
	private boolean isGpsEnabled(){
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean gpsEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		return gpsEnabled;
	}
	
	public void enableLocationSettings() {
		Intent settingsIntent = new Intent(
				Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(settingsIntent);
	}
	
	static boolean slientMode;
	AudioManager audioManager;

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//stop service
		/* 
		 * shared preference file is exists it's keeping writing a new file.
		 * if u don't want the new file creating every time
		 * please disable the below code 
		*/
		/*Intent intent = new Intent(ScreenService.ACTION_FOREGROUND);
		intent.setClass(IndexScreen.this, ScreenService.class);
		stopService(intent);*/
		
		Intent intent = new Intent(ScreenService.ACTION_FOREGROUND);
		intent.setClass(IndexScreen.this, ScreenService.class);
		startService(intent);
		
		location.onShaktiStart();
		
		sendSms = SendSms.getInstance(IndexScreen.this);
		
		registerReceiver(receiver, new IntentFilter(
				AudioManager.RINGER_MODE_CHANGED_ACTION));
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(isGpsEnabled())
			turn_layout.setVisibility(View.INVISIBLE);
		else
			turn_layout.setVisibility(View.VISIBLE);
		
	try{
		if (sendSms.readStoreLocationValue() == null)
			location.shaktiSetup();
		else if((System.currentTimeMillis() - Long.valueOf(sendSms.readStoreLocationKey()) > 300000))
			location.shaktiSetup();
	}catch(NullPointerException ex){
		location.shaktiSetup();
	}
		
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
			Intent intent = new Intent(IndexScreen.this, ShaktiPreference.class);
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		location = null;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		location.onShaktiStop();
		

		unregisterReceiver(receiver);
		sendSms = null;
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

			switch (am.getRingerMode()) {
			case AudioManager.RINGER_MODE_SILENT:
				toggle_slint.setText("Disable\nSilent\nMode");
				Log.i("MyApp", "Silent mode");
				break;
			case AudioManager.RINGER_MODE_VIBRATE:
				// toggle_slint.setText("");
				Log.i("MyApp", "Vibrate mode");
				break;
			case AudioManager.RINGER_MODE_NORMAL:
				toggle_slint.setText(" Enable\nSilent\nMode");
				Log.i("MyApp", "Normal mode");
				break;
			}
		}
	};
	
	

}
