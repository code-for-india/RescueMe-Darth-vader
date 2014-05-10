package com.nidl.shakti;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

public class ShaktiPreference extends Activity {
	
	ListView listView;
	Typeface typeface;
	Button quick_sms;
	Button emergencyContact;
	CheckBox playSound;
	SharedPreferences prefs;
	public static boolean playFlag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.prefs_list);
		prefs = getSharedPreferences("checkedState", 0);
		
		typeface = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
		TextView pref_option = (TextView)this.findViewById(R.id.prefs_option);
		pref_option.setTypeface(typeface);
		quick_sms = (Button)this.findViewById(R.id.quick_sms);
		quick_sms.setTypeface(typeface);
		quick_sms.setOnClickListener(listener);
		emergencyContact = (Button)this.findViewById(R.id.emergency_contact);
		emergencyContact.setTypeface(typeface);
		emergencyContact.setOnClickListener(listener);
		playSound = (CheckBox) findViewById(R.id.checkBox_playsound);
		playSound.setOnCheckedChangeListener(chkboxListener);
		playSound.setChecked(prefs.getBoolean("isChecked", false));
	}
	
	private OnCheckedChangeListener chkboxListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			
			SharedPreferences.Editor mEditor = prefs.edit();
			if(isChecked)
				mEditor.putBoolean("isChecked", true);
			else
				mEditor.putBoolean("isChecked", false);
			
			
			mEditor.commit();
		}
	};
	
	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.quick_sms:
				Intent intent1 = new Intent(ShaktiPreference.this, QuickSend.class);
				startActivity(intent1);
				break;
			case R.id.emergency_contact:
				Intent intent = new Intent(ShaktiPreference.this, AddViews.class);
				startActivity(intent);
				break;

			default:
				break;
			}
		}
	};
	

}
