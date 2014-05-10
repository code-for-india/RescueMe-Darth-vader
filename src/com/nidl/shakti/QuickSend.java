package com.nidl.shakti;

import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class QuickSend extends Activity {

	EditText mEditText;
	Button mButtonDone;
	Button mButtonCancel;
	
	SharedPreferences settings = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quick_send);
		
		settings = getSharedPreferences(PREFS_NAME, 0);
		mEditText = (EditText)this.findViewById(R.id.btnAddNewItem);
		mButtonDone = (Button)this.findViewById(R.id.done);
		mButtonDone.setOnClickListener(listener);
		mButtonCancel = (Button)this.findViewById(R.id.cancel);
		mButtonCancel.setOnClickListener(listener);
		
		Map<String,?> keys = settings.getAll();
		for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values",entry.getKey() + ": " + 
                                   entry.getValue().toString());
            String text = settings.getString("quick_send", null);
            text.trim().length();
            
            if(text == null || text.trim().length() == 0 ){
            	mEditText.setText(getResources().getString(R.string.call_myfamily));
            }
            else
            	mEditText.setText(text);
		}
		mEditText.setSelection(0,mEditText.getText().length());
	}
	
	
	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.done:
				if(mEditText.getText()!= null){
				    SharedPreferences.Editor editor = settings.edit();
				    editor.putString("quick_send", mEditText.getText().toString());
				    editor.commit();
				}
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
	
	public static final String PREFS_NAME = "quicksend";

	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
	}
}
