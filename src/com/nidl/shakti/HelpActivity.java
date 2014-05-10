package com.nidl.shakti;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Build;

public class HelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		ParseObject parse_object = new ParseObject("Count");
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Count");
		String number = getIntent().getStringExtra("number");
		String address = getIntent().getStringExtra("address");
		System.out.println(address + "address");
		int count;
		query.whereEqualTo("phone", number);

		try {
			List<ParseObject> results = query.find();
			if(results != null && !results.isEmpty()){
				
				ParseObject res = results.get(0);
				count = res.getInt("count") + 1;
				res.increment("count");
				List<String> names = res.getList("attendees");
				SharedPreferences settings = getSharedPreferences("details", 0); 
				names.add(settings.getString("name", ""));
				res.add("attendees", names);
				res.saveInBackground(new SaveCallback() {
					
					@Override
					public void done(ParseException arg0) {
						// TODO Auto-generated method stub
						System.out.println(arg0 + "  saved");
					}
				});
			} else {
				count = 1;
				parse_object.put("count", 1);
				List<String> names = new ArrayList<String>();
				SharedPreferences settings = getSharedPreferences("details", 0);
				names.add(settings.getString("name", ""));
				parse_object.put("attendees", names);
				parse_object.put("phone", number);
				parse_object.saveInBackground(new SaveCallback() {
					
					@Override
					public void done(ParseException arg0) {
						System.out.println("done :: " + arg0);
					}
				});
			}
			String lat = address.substring(address.indexOf("(") + 1, address.lastIndexOf(","));
			String lon = address.substring(address.lastIndexOf(",") + 1, address.indexOf(")"));
			TextView textView2 = (TextView) findViewById(R.id.textView2);
			textView2.setText("(" + count + " people including you in the mission)");
			 new DownloadImageTask((ImageView) findViewById(R.id.imageView1))
		        .execute("http://maps.googleapis.com/maps/api/staticmap?zoom=18&center=" + lat.trim() + "," + lon.trim() + "&size=800x400&sensor=false&markers=color:blue%7Clabel:S%7C" + lat.trim() + "," + lon.trim());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		if (id == R.id.action_settings) {
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
