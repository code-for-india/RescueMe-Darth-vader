package com.nidl.shakti;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nidl.gps.ShaktiLocation;

public class SMSEmergency extends Activity {

	ShaktiLocation location;
	public CheckBox location_chk;
	CheckBox mSelectAll;
	CheckBox lowBattery;
	CheckBox pickMe;
	CheckBox dontCallBack;
	Typeface typeface;
	EditText sendMessage;
	Button quick_send;
	boolean smswithoutlocsent;
	public SendSms sendSms;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_multi_sms_layout);
		typeface = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
		
		initialize();
		
		location = new ShaktiLocation(SMSEmergency.this);
		location.setEmergencyActivity(this);
		smswithoutlocsent = false;
	}
	
	
	
	private void initialize(){
		
		TextView title_nouse = (TextView)this.findViewById(R.id.title_nouse);
		title_nouse.setTypeface(typeface);
		title_nouse.setText(R.string.quick_send);
		
		mSelectAll = (CheckBox)this.findViewById(R.id.chck_selectall);
		mSelectAll.setTypeface(typeface);
		mSelectAll.setOnCheckedChangeListener(changeListener);
		
		location_chk = (CheckBox)this.findViewById(R.id.checkBoxLocationsend);
		location_chk.setEnabled(false);
		location_chk.setTypeface(typeface);
		location_chk.setOnCheckedChangeListener(changeListener);
		
		dontCallBack = (CheckBox)this.findViewById(R.id.checkBoxDontCallSend);
		dontCallBack.setTypeface(typeface);
		dontCallBack.setOnCheckedChangeListener(changeListener);
		
		pickMe = (CheckBox)this.findViewById(R.id.checkBoxPickMeSend);
		pickMe.setTypeface(typeface);
		pickMe.setOnCheckedChangeListener(changeListener);
		
		lowBattery = (CheckBox)this.findViewById(R.id.checkBoxBatterySend);
		lowBattery.setTypeface(typeface);
		lowBattery.setOnCheckedChangeListener(changeListener);
		
		sendMessage = (EditText) this.findViewById(R.id.editTextMessagesend);
		
		Button sendsms = (Button)this.findViewById(R.id.button2CustomizeSend);
		sendsms.setTypeface(typeface);
		sendsms.setOnClickListener(listener);
		
		quick_send = (Button)this.findViewById(R.id.button1Quicksend);
		quick_send.setTypeface(typeface);
		quick_send.setOnClickListener(listener);
		
		TextView mTextOr =(TextView)this.findViewById(R.id.or);
		mTextOr.setTypeface(typeface);
		
			
		
		
	}
	
	String addText1 = "", addText3 = "", addText4 = "", addText5 = "";
	StringBuilder builder = new StringBuilder();
	
	private OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
		
			switch (buttonView.getId()) {
			case R.id.checkBoxLocationsend:
				
					if(isChecked )
						addText1 = "I'm near " + location_chk.getText().toString()+". " ;
					else
					{
						addText1 = "";
						smswithoutlocsent = true;
					}
				
				break;
			case R.id.checkBoxDontCallSend:
				if(isChecked)
					addText3 = "Don't call back, not safe. ";
				else
					addText3="";
				break;
			case R.id.checkBoxPickMeSend:
				if(isChecked)
					addText4 = "Pick me up, I'll wait here. ";
				else
					addText4="";
				break;
			case R.id.checkBoxBatterySend:
				if(isChecked)
					addText5 = "I'm very low on battery. ";
				else
					addText5 = "";
				break;
			
			}
			
			
			sendMessage.setText(addText1 + addText3 + addText4 + addText5);
		}
	};
	
	/*private void sendSms(String phoneNumber,String message){
		int simState = isSIMExists();
		Log.i("Sms State", "my sim state"+simState);
		if(simState == 5){
			
			String SENT = "SMS_SENT";
	        String DELIVERED = "SMS_DELIVERED";
	        
	        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
	                new Intent(SENT), 0);
	     
	            PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
	                new Intent(DELIVERED), 0);
	        
			registerReceiver(new BroadcastReceiver(){
	            @Override
	            public void onReceive(Context arg0, Intent arg1) {
	                switch (getResultCode())
	                {
	                case Activity.RESULT_OK:
//	        			Toast.makeText(arg0, "SMS sent", Toast.LENGTH_SHORT).show();
	        			break;
	        		case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
	        			Toast.makeText(arg0, "Generic failure! Please check network settings!", Toast.LENGTH_SHORT)
	        					.show();
	        			break;
	        		case SmsManager.RESULT_ERROR_NO_SERVICE:
	        			Toast.makeText(arg0, "No Signal! We will wait to acquire Signal and try again!", Toast.LENGTH_SHORT).show();
	        			break;
	        		case SmsManager.RESULT_ERROR_NULL_PDU:
	        			Toast.makeText(arg0, "Null PDU", Toast.LENGTH_SHORT).show();
	        			break;
	        		case SmsManager.RESULT_ERROR_RADIO_OFF:
	        			Toast.makeText(arg0, "No Signal! We will wait to acquire Signal and try again!", Toast.LENGTH_SHORT).show();
	        			break;
	                }
	            }
	        }, new IntentFilter(SENT));
	 
	        //---when the SMS has been delivered---
	        registerReceiver(new BroadcastReceiver(){
	            @Override
	            public void onReceive(Context arg0, Intent arg1) {
	                switch (getResultCode())
	                {
	                case Activity.RESULT_OK:
	        			Toast.makeText(arg0, "SMS was delivered!", Toast.LENGTH_SHORT).show();
	        			break;
	        		case Activity.RESULT_CANCELED:
	        			Toast.makeText(arg0, "SMS was not delivered!", Toast.LENGTH_SHORT)
	        					.show();
	        			break;                     
	                }
	            }
	        }, new IntentFilter(DELIVERED)); 
	        
	        SmsManager sms = SmsManager.getDefault();
	        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
	        
		}else{
			Toast.makeText(SMSEmergency.this, "SIM CARD ERROR!", Toast.LENGTH_SHORT).show();
		}
		sendMessage.setText("");
		mSelectAll.setChecked(false);
		location_chk.setChecked(false);
		dontCallBack.setChecked(false);
		lowBattery.setChecked(false);
		pickMe.setChecked(false);
	}
	
	private int isSIMExists(){
		TelephonyManager telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		int simState = telMgr.getSimState();
	            switch (simState) {
	                case TelephonyManager.SIM_STATE_ABSENT:
	                    Log.e(SMSEmergency.this.getCallingPackage(), "SIM STATE IS ABSENT");
	                    return simState;
	                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
	                	// do something
	                	Log.e(SMSEmergency.this.getCallingPackage(), "SIM STATE NETWORK LOCKED");
	                    return simState;
	                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
	                	// do something
	                	Log.e(SMSEmergency.this.getCallingPackage(), "SIM STATE PIN REQUIRED");
	                    return simState;
	                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
	                	// do something
	                	Log.e(SMSEmergency.this.getCallingPackage(), "SIM STATE PUK REQUIRED");
	                    return simState;
	                case TelephonyManager.SIM_STATE_READY:
	                	// do something
	                	Log.d(SMSEmergency.this.getCallingPackage(), "SIM STATE READY");
	                    return simState;
	                    
	                case TelephonyManager.SIM_STATE_UNKNOWN:
	                	// do something
	                	Log.e(SMSEmergency.this.getCallingPackage(), "SIM STATE UNKNOWN");
	                    return simState;
	            }
	            
	            return simState;
	}*/
	
//	StringBuilder quick_sendData = new StringBuilder();

	
	private OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SharedPreferences settings = getSharedPreferences("Prefences", 0);
			Map<String,?> keys = settings.getAll();
			switch (v.getId()) {
			case R.id.button2CustomizeSend:
				builder.append(sendMessage.getText().toString());
				if(keys.size()== 0){
					Toast.makeText(SMSEmergency.this, "Please set the Emergency Contact Number in the Preferences", Toast.LENGTH_SHORT).show();
				}
				
				if(sendMessage.getText().toString() != null && sendMessage.getText().toString().length() != 0){
//					Toast.makeText(SMSEmergency.this, "Sending SMS", Toast.LENGTH_SHORT).show();
					sendSms.data(builder.toString(), location);
				/*for(Map.Entry<String,?> entry : keys.entrySet()){
					
				            Log.d("nnn map values",entry.getKey() + ": " + 
				                                   entry.getValue().toString()); 
				            sendSms(entry.getKey().toString(), builder.toString());
				          
				 }*/
				}else{
					Toast.makeText(SMSEmergency.this, "Please select some options or write some text", Toast.LENGTH_SHORT).show();
				}
				
				builder.delete(0, builder.length());
				mSelectAll.setChecked(false);
				location_chk.setChecked(false);
				dontCallBack.setChecked(false);
				lowBattery.setChecked(false);
				pickMe.setChecked(false);
				sendMessage.setText("");
				break;
			case R.id.button1Quicksend:
				sendSms.data(location_chk.getText().toString(), location);
				break;
			default:
				break;
			}
			
		}
	};
	
	
	
	
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
			Intent intent = new Intent(SMSEmergency.this, ShaktiPreference.class);
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
		super.onStart();
		sendSms = SendSms.getInstance(SMSEmergency.this);
		location.onShaktiStart();
	};
	
	@Override
	protected void onStop() {
		super.onStop();
		sendSms = null;
		location.onShaktiStop();
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		location = null;
		
	};
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try{
		if (sendSms.readStoreLocationValue() == null)
			location.shaktiSetup();
		else if((System.currentTimeMillis() - Long.valueOf(sendSms.readStoreLocationKey()) > 300000))
			location.shaktiSetup();

		if(sendSms.readStoreLocationValue() != null){
		
			location_chk.setEnabled(true);
			location_chk.setText(sendSms.readStoreLocationValue());
		}
		}catch(NullPointerException ex){
			location.shaktiSetup();
		}
	}
	
}
