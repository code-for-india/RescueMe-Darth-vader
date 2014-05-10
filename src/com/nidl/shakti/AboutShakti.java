package com.nidl.shakti;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class AboutShakti extends Activity {

	TextView about_us;
	Typeface typeface;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_iamshakti_layout);
		
		about_us = (TextView)this.findViewById(R.id.textView2);
		typeface = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
		about_us.setTypeface(typeface);
	}
}
