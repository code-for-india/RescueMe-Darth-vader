package com.nidl.shakti;

import java.util.List;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Window;

public class SplashScreen extends Activity {

	Thread mThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_splash);

		mThread = new Thread(mRunnable);
		mThread.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				Log.e(SplashScreen.class.getCanonicalName(), "error occured", ex.fillInStackTrace());
			} finally {
				Parse.initialize(SplashScreen.this, "N4vwmDfHn5z6HyehLrkEy6B0Mk2c8QYERpMV7TvL", "vqUVRQmhdOWdxILIv6R2iAUffC7mDL5l0OsjaTzk");
				Intent indexIntent = new Intent(SplashScreen.this, IndexScreen.class);
				startActivity(indexIntent);
				finish();
				if (mThread != null)
					mThread = null;
			}
		}
	};

}
