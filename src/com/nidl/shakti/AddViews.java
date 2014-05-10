package com.nidl.shakti;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class AddViews extends Activity {



	private Button mDoneButton;
	private Button mCancelButton;

	
	public static final String PREFS_NAME = "Prefences";
	AddViewAdapter adpater;
	SharedPreferences settings = null;
	SharedPreferences.Editor editor = null;
	ListView listView;
	ScrollView scroll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.row_container);
		scroll = (ScrollView)this.findViewById(R.id.scroll);
		listView = (ListView)this.findViewById(R.id.listView);
		adpater = new AddViewAdapter();
		listView.setAdapter(adpater);
		mDoneButton = (Button) findViewById(R.id.done);
		mDoneButton.setOnClickListener(listener);
		mCancelButton = (Button) findViewById(R.id.cancel);
		mCancelButton.setOnClickListener(listener);

		settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		Map<String,?> keys = settings.getAll();
		for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values",entry.getKey() + ": " + 
                                   entry.getValue().toString()); 
            mList.add(entry.getValue().toString());
            
            mEditText.add(entry.getKey().toString());
		}
		
	}
	

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.done:
				editor = settings.edit();
				for (int i = 0; i < mEditText.size(); i++) {
					editor.putString(mEditText.get(i)
							.toString(), mList.get(i)
							.toString());
				}

				// Commit the edits!
				editor.commit();

				
				finish();
				break;
			case R.id.cancel:
				finish();
				break;

			default:
				break;
			}
		}
	};

	public List<String> mEditText = new ArrayList<String>();
	public List<String> mList = new ArrayList<String>();

	

	// onClick handler for the "Add new" button;
	public void onAddNewClicked(View v) {
		// Inflate a new row and hide the button self.
		Intent intent = new Intent(Intent.ACTION_PICK,
				Contacts.CONTENT_URI);
				startActivityForResult(intent, 1); 

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1: {
			if (resultCode == Activity.RESULT_OK) {
				Uri contactData = data.getData();
				String id = contactData.getLastPathSegment();
				try {
					Cursor c = getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = ?", new String[] { id }, null);
					c.moveToFirst();
					
					String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					mList.add(name);
					String phone = c
							.getString(c
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					
					mEditText.add(phone);
					scroll.smoothScrollTo(0, listView.getBottom());
					adpater.notifyDataSetChanged();
//					inflateEditRow(phone);
				} catch (CursorIndexOutOfBoundsException ex) {
					Log.e(AddViews.class.getCanonicalName(), "error occured ",
							ex.fillInStackTrace());
					Toast.makeText(AddViews.this, "No phone number ",
							Toast.LENGTH_SHORT).show();
				}
			}
			break;
		}
		}
	}
	
	private class AddViewAdapter extends BaseAdapter{

		LayoutInflater inflater;
		
		public AddViewAdapter() {
			// TODO Auto-generated constructor stub
			inflater = LayoutInflater.from(AddViews.this);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mEditText.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mEditText;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if(convertView == null){
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.row, parent, false);
				holder.txt1 = (TextView)convertView.findViewById(R.id.editText);
				holder.txt1.setTypeface(Typeface.DEFAULT_BOLD);
				holder.txt1.setSelected(true);
				holder.txt = (TextView)convertView.findViewById(R.id.editText1);
				holder.txt1.setSelected(true);
				holder.img = (Button)convertView.findViewById(R.id.buttonDelete);
				convertView.setTag(holder);
				
			}else
				holder = (ViewHolder)convertView.getTag();
			
				holder.txt1.setText(mList.get(position));
				holder.txt.setText(mEditText.get(position));
				holder.img.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
//						Map<String,?> keys = settings.getAll();
						mList.remove(position);
						
							editor.remove(mEditText.get(position));

						mEditText.remove(position);
						editor.commit();
						adpater.notifyDataSetChanged();
					}
				});
				
				
			return convertView;
		}
		
		class ViewHolder{
			TextView txt;
			TextView txt1;
			Button img;
		}
		
	}

}
