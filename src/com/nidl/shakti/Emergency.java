package com.nidl.shakti;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.provider.ContactsContract;

public class Emergency extends Activity {

	private Typeface typeface;
	private String name = "";
	private String address;
	private String Number;
	private String Name;
	private String distressNumber;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emergency);

		SharedPreferences settings = getSharedPreferences("Prefences", 0);
		Map<String, ?> keys = settings.getAll();
		Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
		HashSet<String> numbers = new HashSet<String>();
//		String Number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Emergency");
//		System.out.println(Number);
//		query.whereEqualTo("phone", Number);
		List<ParseObject> locationList = new ArrayList<ParseObject>();
		try {
			locationList = query.find();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (locationList != null) {
			for (ParseObject locObject : locationList) {
				String number = locObject.getString("phone");
				name = locObject.getString("name");
				address = locObject.getString("location");
				System.out.println(name + " ::: " + address);
				numbers.add(number);
			}
		}

		TextView textView = (TextView) findViewById(R.id.help_text);
		textView.setTypeface(typeface);
		while (phones.moveToNext()) {
			Number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			Name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			if(numbers.contains(Number)){
				textView.setText("YOUR FRIEND " + Name.toUpperCase() + " \n NEEDS HELP !");
				distressNumber = Number;
			}
		}
		typeface = Typeface.createFromAsset(getAssets(), "Fonts/oswald.ttf");
		Button cancel_button = (Button) findViewById(R.id.cancel_help);
		Button help_button = (Button) findViewById(R.id.accept_help);
		cancel_button.getBackground().setAlpha(100);
		help_button.getBackground().setAlpha(100);
		cancel_button.setTypeface(typeface);
		help_button.setTypeface(typeface);
		
		help_button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Emergency.this, HelpActivity.class);
				intent.putExtra("number", distressNumber);
				intent.putExtra("address", address);
				startActivity(intent);
			}
			
		});
		ObjectAnimator anim = ObjectAnimator.ofInt(findViewById(R.id.anim_view), "backgroundColor", getResources()
				.getColor(R.color.body_bg), getResources().getColor(R.color.body_red));
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
