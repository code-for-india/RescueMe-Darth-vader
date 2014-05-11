package com.nidl.shakti;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.renderscript.Font;
import android.renderscript.Font.Style;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class HelpActivity extends Activity {

	private LinearLayout messages;
	private String number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		ParseObject parse_object = new ParseObject("Count");
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Count");
		number = getIntent().getStringExtra("number");
		final String address = getIntent().getStringExtra("address");
		String name = getIntent().getStringExtra("name");
		System.out.println(address + "address");
		int count;
		query.whereEqualTo("phone", number);

		try {
			List<ParseObject> results = query.find();
			if (results != null && !results.isEmpty()) {

				ParseObject res = results.get(0);
				count = res.getInt("count");

//				List<String> names = res.getList("attendees");
				SharedPreferences settings = getSharedPreferences("details", 0);
//				if (!names.contains(settings.getString("name", ""))) {
					count  += 1;
					res.increment("count");
//					names.add(settings.getString("name", ""));
//					res.add("attendees", names);
					res.saveInBackground(new SaveCallback() {

						@Override
						public void done(ParseException arg0) {
							// TODO Auto-generated method stub
							System.out.println(arg0 + "  saved");
						}
					});
//				}

			} else {
				count = 1;
				parse_object.put("count", 1);
				List<String> names = new ArrayList<String>();
				SharedPreferences settings = getSharedPreferences("details", 0);
				names.add(settings.getString("name", ""));
//				parse_object.put("attendees", names);
				parse_object.put("phone", number);
				parse_object.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException arg0) {
						System.out.println("done :: " + arg0);
					}
				});
			}
			SharedPreferences settings = getSharedPreferences("details", 0);
			final String currentName = settings.getString("name", "");
			final String currentNumber = settings.getString("phone", "");
			final String lat = address.substring(address.indexOf("(") + 1, address.lastIndexOf(","));
			final String lon = address.substring(address.lastIndexOf(",") + 1, address.indexOf(")"));
			TextView textView2 = (TextView) findViewById(R.id.textView2);
			TextView textView1 = (TextView) findViewById(R.id.textView1);
			final EditText messageText = (EditText) findViewById(R.id.editText1);
			String text = "Mission  " + name;
			textView1.setText(text.toUpperCase());
			textView2.setText("(" + count + " people including you in this mission)");
			messages = (LinearLayout) findViewById(R.id.messages_layout);
			
			ImageView sendView = (ImageView) findViewById(R.id.imageView3);
			sendView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					TextView textView = new TextView(HelpActivity.this);
					textView.setText(currentName + " : " + messageText.getText().toString());
					messages.addView(textView);
					ParseObject messageObj = new ParseObject("EmergencyMessages");
					messageObj.put("name", currentName);
					messageObj.put("message", messageText.getText().toString());
					messageObj.put("distress", number);
					messageObj.put("user_number", currentNumber);
					messageObj.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException arg0) {
							Toast.makeText(HelpActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
							
							System.out.println("message sent with " + arg0);
						}
					});
				}
			});
			ImageView imageView = (ImageView) findViewById(R.id.imageView1);
			imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String url = "https://www.google.com/maps/place/" + address.substring(0, address.indexOf(")")) + "/@" + lat + "," + lon + ",17z/data=!3m1!4b1!4m2!3m1!1s0x3bae113618c9a9f7:0x43e9edd573db8110";
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(browserIntent);
				}
			});
			
			ParseQuery<ParseObject> query_message = ParseQuery.getQuery("EmergencyMessages");
			query_message.whereEqualTo("distress", number);
			List<ParseObject> resultSet = query_message.find();
			for(ParseObject object : resultSet){
				String message = object.getString("message");
				String getname = object.getString("name");
				final String getnumber = object.getString("user_number");
				TextView textView = new TextView(HelpActivity.this);
				textView.setTextSize(20);
				textView.setTextColor(Color.WHITE);
				textView.setText(getname + " says : " + message);
				textView.setCompoundDrawables(getResources().getDrawable(R.drawable.call), null, null, null);
				textView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getnumber));
						startActivity(intent);
					}
				});
				messages.addView(textView);
			}
//			query_message.findInBackground(new FindCallback<ParseObject>() {
//				
//				@Override
//				public void done(List<ParseObject> arg0, ParseException arg1) {
//					if(arg0 != null & !arg0.isEmpty()){
////						for(ParseObject object : arg0){
////							String message = object.getString("message");
////							String name = object.getString("name");
////							final String number = object.getString("user_number");
////							TextView textView = new TextView(HelpActivity.this);
//////							textView.setTextSize(20);
////							textView.setText(name + " says " + message);
////							textView.setCompoundDrawables(getResources().getDrawable(R.drawable.call), null, null, null);
////							textView.setOnClickListener(new OnClickListener() {
////								
////								@Override
////								public void onClick(View v) {
////									Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
////									startActivity(intent);
////								}
////							});
////							messages.addView(textView);
////						}
//					}
//				}
//			});
			new DownloadImageTask((ImageView) findViewById(R.id.imageView1))
					.execute("http://maps.googleapis.com/maps/api/staticmap?zoom=18&center=" + lat.trim() + "," + lon.trim()
							+ "&size=800x400&sensor=false&markers=size:mid%7Ccolor:red%7Clabel:S%7C" + lat.trim() + "," + lon.trim());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getCode());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.help, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.refresh) {
			ParseQuery<ParseObject> query_message = ParseQuery.getQuery("EmergencyMessages");
			query_message.whereEqualTo("distress", number);
			List<ParseObject> resultSet = new ArrayList<ParseObject>();
			try {
				resultSet = query_message.find();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(ParseObject object : resultSet){
				String message = object.getString("message");
				String getname = object.getString("name");
				final String getnumber = object.getString("user_number");
				TextView textView = new TextView(HelpActivity.this);
				textView.setTextSize(20);
				textView.setTextColor(Color.WHITE);
				textView.setText(getname + " says : " + message);
				textView.setCompoundDrawables(getResources().getDrawable(R.drawable.call), null, null, null);
				textView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getnumber));
						startActivity(intent);
					}
				});
				messages.addView(textView);
			}
			System.out.println("refreshed");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_help, container, false);
			return rootView;
		}
	}

}
