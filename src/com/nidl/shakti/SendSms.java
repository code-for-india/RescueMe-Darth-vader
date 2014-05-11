package com.nidl.shakti;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import android.content.ContentResolver;

import com.nidl.gps.ShaktiLocation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class SendSms {

	private Context context;
	static SendSms sendSms;
	private boolean smsconfirmation;
	private String google_key = "AIzaSyC-bdOlqg1mefy0759MKurJemag6hKWBMU";

	private SendSms(Context context) {
		this.context = context;
	}

	public void isSendSms(String address) {
		this.address = address;
		sent = false;
		smsconfirmation = false;
		((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).listen(phoneListener,
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}

	private void sendSms(String phoneNumber, String message) {
		int simState = isSIMExists();
		if (simState == 5) {

			String SENT = "SMS_SENT";
			String DELIVERED = "SMS_DELIVERED";

			PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);

			PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED), 0);

			context.registerReceiver(new BroadcastReceiver() {
				@Override
				public void onReceive(Context arg0, Intent arg1) {
					switch (getResultCode()) {
					case Activity.RESULT_OK:
						Toast.makeText(arg0, "SMS Sent (Awaiting Delivery)", Toast.LENGTH_SHORT).show();
						break;
					case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
						Toast.makeText(arg0, "Generic failure! Please check network settings!", Toast.LENGTH_SHORT).show();
						break;
					case SmsManager.RESULT_ERROR_NO_SERVICE:
						Toast.makeText(arg0, "No Signal! We will wait to acquire Signal and try again!", Toast.LENGTH_SHORT).show();
						Log.i("SendSms", "No Signal! We will wait to acquire Signal and try again!");
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

			// ---when the SMS has been delivered---
			context.registerReceiver(new BroadcastReceiver() {
				@Override
				public void onReceive(Context arg0, Intent arg1) {
					switch (getResultCode()) {
					case Activity.RESULT_OK:
						Toast.makeText(arg0, "SMS was delivered!", Toast.LENGTH_SHORT).show();
						smsconfirmation = true;
						break;
					case Activity.RESULT_CANCELED:
						Toast.makeText(arg0, "SMS was not delivered!", Toast.LENGTH_SHORT).show();
						break;
					}
				}
			}, new IntentFilter(DELIVERED));

			ParseObject emerg_obj = new ParseObject("Emergency");
			emerg_obj.put("location", address != null ? address : "");
			SharedPreferences mlocPrefs = context.getSharedPreferences("details", 0);
			String name = mlocPrefs.getString("name", "");
			emerg_obj.put("name", name);
			String phone = mlocPrefs.getString("phone", "");
			emerg_obj.put("phone", phone);
			emerg_obj.saveInBackground(new SaveCallback() {

				@Override
				public void done(ParseException arg0) {
					// TODO Auto-generated method stub
					System.out.println("from emerg" + arg0);
				}
			});
			SmsManager sms = SmsManager.getDefault();
			 sms.sendTextMessage(phoneNumber, null, message, sentPI,
			 deliveredPI);
		} else {
			Toast.makeText(context, "SIM CARD ERROR!", Toast.LENGTH_SHORT).show();
		}
	}

	private static final String TAG = "SENDSMS";

	private int isSIMExists() {
		TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		int simState = telMgr.getSimState();
		switch (simState) {
		case TelephonyManager.SIM_STATE_ABSENT:
			Log.e(TAG, "SIM STATE IS ABSENT");
			return simState;
		case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
			// do something
			Log.e(TAG, "SIM STATE NETWORK LOCKED");
			return simState;
		case TelephonyManager.SIM_STATE_PIN_REQUIRED:
			// do something
			Log.e(TAG, "SIM STATE PIN REQUIRED");
			return simState;
		case TelephonyManager.SIM_STATE_PUK_REQUIRED:
			// do something
			Log.e(TAG, "SIM STATE PUK REQUIRED");
			return simState;
		case TelephonyManager.SIM_STATE_READY:
			// do something
			Log.d(TAG, "SIM STATE READY");
			return simState;

		case TelephonyManager.SIM_STATE_UNKNOWN:
			// do something
			Log.e(TAG, "SIM STATE UNKNOWN");
			return simState;
		}

		return simState;
	}

	public static SendSms getInstance(Context context) {
		if (sendSms == null)
			sendSms = new SendSms(context);
		return sendSms;
	}

	private static final String loc = "locAddress";

	public void storeLocationPrefs(long mtimeInMillis, String address) {
		SharedPreferences mlocPrefs = context.getSharedPreferences(loc, 0);
		SharedPreferences.Editor mEditor = mlocPrefs.edit();
		mEditor.putString(String.valueOf(mtimeInMillis), address);
		mEditor.commit();
	}

	String mPrevTimeMillis;

	public String readStoreLocationKey() {
		SharedPreferences mlocPrefs = context.getSharedPreferences(loc, 0);
		if (mlocPrefs != null) {
			Map<String, ?> mPrevious = mlocPrefs.getAll();
			for (Map.Entry<String, ?> entry : mPrevious.entrySet()) {
				mPrevTimeMillis = entry.getKey().toString();
			}
			return mPrevTimeMillis;
		} else
			return null;
	}

	String mPrevKeyValue;

	public String readStoreLocationValue() {
		SharedPreferences mlocPrefs = context.getSharedPreferences(loc, 0);
		if (mlocPrefs != null) {
			Map<String, ?> mPrevious = mlocPrefs.getAll();
			for (Map.Entry<String, ?> entry : mPrevious.entrySet()) {
				if (entry.getValue().toString() != null)
					mPrevKeyValue = entry.getValue().toString();
				else
					mPrevKeyValue = null;
			}
			return mPrevKeyValue;
		} else
			return null;
	}

	public void deletelocFile() {
		SharedPreferences mlocPrefs = context.getSharedPreferences(loc, 0);
		SharedPreferences.Editor mEditor = mlocPrefs.edit();
		mEditor.clear();
		mEditor.commit();
	}

	Thread t1;
	String address;
	ShaktiLocation location;
	String oldAddress;

	public void data(String address, ShaktiLocation location) {
		this.address = address;
		this.location = location;

		isSendSms(readStoreLocationValue());
		t1 = new Thread(runnable);
		t1.start();

	}

	private Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			location.onShaktiStart();
			location.shaktiSetup();

			if (address != null && !address.equals(sendSms.readStoreLocationValue())) {
				deletelocFile();
				storeLocationPrefs(System.currentTimeMillis(), address);
				isSendSms(address);
			}
			location.onShaktiStop();
			return true;
		}
	});

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {

			try {
				Thread.sleep(300000);
				handler.sendEmptyMessage(0);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			} finally {
				if (t1 != null)
					t1 = null;
			}
		}
	};

	String addressService;

	public void serviceData(String addressService, ShaktiLocation location) {
		this.addressService = addressService;
		this.location = location;

	}

	private int sStrength;
	public boolean sent;

	private PhoneStateListener phoneListener = new PhoneStateListener() {

		@Override
		public void onSignalStrengthsChanged(android.telephony.SignalStrength signalStrength) {
			super.onSignalStrengthsChanged(signalStrength);
			sStrength = signalStrength.getGsmSignalStrength();

			if ((signalStrength.getGsmSignalStrength() != 0 || signalStrength.getGsmSignalStrength() != 99) && sent == false) {

				signalGood(address);
				sent = true;
			}

		};
	};
	private String data;

	private void signalGood(final String address) {
		SharedPreferences settings = context.getSharedPreferences("Prefences", 0);
		final HashSet<String> sentnums = new HashSet<String>();
		SharedPreferences quciksend = context.getSharedPreferences("quicksend", 0);
		data = quciksend.getString("quick_send", null);
		if (data == null)
			data = "I need help!Call my family!";

		Map<String, ?> keys = settings.getAll();
		if (keys.size() == 0) {
			Toast.makeText(context, "Please set the Emergency Contact Number in the Preferences", Toast.LENGTH_SHORT).show();
		} else {
			Log.i("Send sms ", "my signal strength" + sStrength);

			Toast.makeText(context, "Sending SMS ", Toast.LENGTH_LONG).show();

			if (address != null) {
				if (!address.equals("Location Not Found")) {
					Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
							null, null);

					while (phones.moveToNext()) {
						String Name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
						final String Number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						System.out.println(Name + ":" + Number);
						data += address;
						ParseQuery<ParseObject> query = ParseQuery.getQuery("Location");
						query.whereEqualTo("phone", Number);
						query.findInBackground(new FindCallback<ParseObject>() {

							public void done(List<ParseObject> locationList, ParseException e) {
								if (locationList != null) {
									for (ParseObject locObject : locationList) {
										System.out.println(" LOCC " + locObject);
										if (locObject.containsKey("location")) {
											System.out.println("From parse"+ locObject.getString("location"));
											String lat = address.substring(address.indexOf("(") + 1, address.lastIndexOf(","));
											String lon = address.substring(address.lastIndexOf(",") + 1, address.indexOf(")"));
											String[] contactLocation = locObject.getString("location").split(":");
											double k = distance(Double.valueOf(lat.trim()), Double.valueOf(lon.trim()),
													Double.valueOf(contactLocation[0].trim()), Double.valueOf(contactLocation[1].trim()),
													'K');
											System.out.println(k + " Kilometres");
//											if (k <= 1) {
												String localAddress = " http://maps.googleapis.com/maps/api/staticmap?center=" + lat.trim()
														+ "," + lon.trim() + "&size=400x400&sensor=false&markers=color:blue%7Clabel:S%7C"
														+ lat.trim() + "," + lon.trim();
												if (!sentnums.contains(Number) && !Number.isEmpty()) {
													System.out.println("lat :: " + Number + localAddress);
													sendSms(Number, data);
													sendSms(Number, localAddress);
													sentnums.add(Number);
												}
//											}
										}

									}
								}
							}
						});
					}
					// for (final Map.Entry<String, ?> entry : keys.entrySet())
					// {
					// data += address;
					// ParseQuery<ParseObject> query =
					// ParseQuery.getQuery("Location");
					// System.out.println(entry.getKey().toString());
					// query.whereEqualTo("phone", entry.getKey().toString());
					// query.findInBackground(new FindCallback<ParseObject>() {
					//
					// public void done(List<ParseObject> locationList,
					// ParseException e) {
					// for (ParseObject locObject : locationList) {
					//
					// if (locObject.containsKey("location")) {
					// String lat = address.substring(address.indexOf("(") + 1,
					// address.lastIndexOf(","));
					// String lon = address.substring(address.lastIndexOf(",") +
					// 1, address.indexOf(")"));
					// String[] contactLocation =
					// locObject.getString("location").split(":");
					// double k = distance(Double.valueOf(lat.trim()),
					// Double.valueOf(lon.trim()),
					// Double.valueOf(contactLocation[0].trim()),
					// Double.valueOf(contactLocation[1].trim()), 'K');
					// System.out.println(k + " Kilometres");
					// if (k <= 1) {
					// String localAddress =
					// " http://maps.googleapis.com/maps/api/staticmap?center="
					// + lat.trim()
					// + "," + lon.trim() +
					// "&size=400x400&sensor=false&markers=color:blue%7Clabel:S%7C"+lat.trim()
					// + "," + lon.trim();
					// if (!sentnums.contains(entry.getKey().toString()) &&
					// !entry.getKey().toString().isEmpty()) {
					// System.out.println("lat :: " + entry.getKey().toString()
					// + localAddress);
					// sendSms(entry.getKey().toString(), data);
					// sendSms(entry.getKey().toString(), localAddress);
					// sentnums.add(entry.getKey().toString());
					// }
					// }
					// }
					//
					// }
					// }
					// });
					//
					// // sendSms(entry.getKey().toString(), data +
					// // " I'm near " + address);
					//
					// }
				} else {
					for (Map.Entry<String, ?> entry : keys.entrySet()) {
						System.out.println(entry.getKey().toString() + "key");
						sendSms(entry.getKey().toString(), data);
					}

				}

			} else {
				for (Map.Entry<String, ?> entry : keys.entrySet()) {
					sendSms(entry.getKey().toString(), data);
				}
			}

		}
	}

	public static boolean distFrom(float lat1, float lng1, float lat2, float lng2, int distance) {
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
				* Math.sin(dLng / 2) * Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		int meterConversion = 1609;

		// return (float) (dist * meterConversion) < distance;
		return true;
	}

	private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == 'K') {
			dist = dist * 1.609344;
		} else if (unit == 'N') {
			dist = dist * 0.8684;
		}
		return (dist);
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts decimal degrees to radians : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts radians to decimal degrees : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

}
