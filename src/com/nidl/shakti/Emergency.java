package com.nidl.shakti;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.os.Build;

public class Emergency extends Activity {

	private Typeface typeface;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emergency);
		typeface = Typeface.createFromAsset(getAssets(), "Fonts/oswald.ttf");
		Button cancel_button = (Button) findViewById(R.id.cancel_help);
		Button help_button = (Button) findViewById(R.id.accept_help);
		cancel_button.getBackground().setAlpha(100);
		help_button.getBackground().setAlpha(100);
		cancel_button.setTypeface(typeface);
		help_button.setTypeface(typeface);
		TextView textView = (TextView) findViewById(R.id.help_text);
		textView.setTypeface(typeface);

		ObjectAnimator anim = ObjectAnimator.ofInt(findViewById(R.id.anim_view), "backgroundColor", getResources().getColor(R.color.body_bg), getResources().getColor(R.color.body_red));
		anim.setDuration(1000);
		anim.setEvaluator(new ArgbEvaluator());
		anim.setRepeatCount(ValueAnimator.INFINITE);
		anim.setRepeatMode(ValueAnimator.REVERSE);
		anim.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.emergency, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_emergency, container, false);
			rootView.findViewById(R.id.cancel_help).getBackground().setAlpha(4);
			rootView.findViewById(R.id.accept_help).getBackground().setAlpha(4);
			return rootView;
		}
	}

}
