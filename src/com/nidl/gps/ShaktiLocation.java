package com.nidl.gps;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.nidl.pservice.ScreenService;
import com.nidl.shakti.IndexScreen;
import com.nidl.shakti.R;
import com.nidl.shakti.SMSEmergency;
import com.parse.*;

public class ShaktiLocation {

	private Context context;
	private LocationManager mLocationManager;

	private boolean mGeocoderAvailable;
	private boolean mUseBoth;

	// Keys for maintaining UI states after rotation.
	/*
	 * private static final String KEY_FINE = "use_fine"; private static final
	 * String KEY_BOTH = "use_both";
	 */
	// UI handler codes.
	private static final int UPDATE_ADDRESS = 1;
	private static final int UPDATE_LATLNG = 2;

	private static final int TEN_SECONDS = 10000;
	private static final int TEN_METERS = 10;
	private static final int TWO_MINUTES = 1000 * 60 * 2;

	public String address;
	public String latlng;

	public ShaktiLocation(final Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		mGeocoderAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Geocoder.isPresent();
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		address = context.getString(R.string.address);
		latlng = context.getString(R.string.latlng);
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				Parse.initialize(context, "N4vwmDfHn5z6HyehLrkEy6B0Mk2c8QYERpMV7TvL", "vqUVRQmhdOWdxILIv6R2iAUffC7mDL5l0OsjaTzk");
				return null;
			}
		}.execute();
		
		
				
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_ADDRESS:

				address = (String) msg.obj;

				if (smsEmergency != null && !address.equals("Location Not Found") && smsEmergency.sendSms != null) {
					smsEmergency.sendSms.deletelocFile();
					smsEmergency.location_chk.setText(address);
					smsEmergency.sendSms.storeLocationPrefs(System.currentTimeMillis(), address);
					smsEmergency.location_chk.setEnabled(true);

				}

				if (indexScreen != null && !address.equals("Location Not Found")) {
					indexScreen.mlocAddress = address;
				}

				if (screenService != null && !address.equals("Location Not Found")) {
					screenService.address = address;
					screenService.setAddress(address);
				}
				break;
			case UPDATE_LATLNG:
				latlng = (String) msg.obj;
				if (smsEmergency != null && !address.equals("Location Not Found") && smsEmergency.sendSms != null) {
					smsEmergency.sendSms.deletelocFile();
					smsEmergency.location_chk.setText(latlng);
					smsEmergency.sendSms.storeLocationPrefs(System.currentTimeMillis(), latlng);
					smsEmergency.location_chk.setEnabled(true);

				}
				break;
			default:
				break;
			}

		};
	};

	public void onShaktiStart() {
		// Check if the GPS setting is currently enabled on the device.
		// This verification should be done during onStart() because the system
		// calls this method
		// when the user returns to the activity, which ensures the desired
		// location provider is
		// enabled each time the activity resumes from the stopped state.
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!gpsEnabled) {
			// Build an alert dialog here that requests that the user enable
			// the location services, then when the user clicks the "OK" button,
			// call enableLocationSettings()

			// important notice dude
			/*
			 * new EnableGpsDialogFragment().show(getSupportFragmentManager(),
			 * "enableGpsDialog");
			 */
		}
	}

	// Method to launch Settings
	public void enableLocationSettings() {
		Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		context.startActivity(settingsIntent);
	}

	public void onShaktiStop() {
		mLocationManager.removeUpdates(listener);
	}

	Location gpsLocation = null;
	Location networkLocation = null;

	// Set up fine and/or coarse location providers depending on whether the
	// fine provider or
	// both providers button is pressed.
	public void shaktiSetup() {
		mLocationManager.removeUpdates(listener);

		gpsLocation = requestUpdatesFromProvider(LocationManager.GPS_PROVIDER, R.string.not_support_gps);
		networkLocation = requestUpdatesFromProvider(LocationManager.NETWORK_PROVIDER, R.string.not_support_network);

		// If both providers return last known locations, compare the two and
		// use the better
		// one to update the UI. If only one provider returns a location, use
		// it.
		if (gpsLocation != null && networkLocation != null) {
			updateUILocation(getBetterLocation(gpsLocation, networkLocation));
		} else if (gpsLocation != null) {
			updateUILocation(gpsLocation);
		} else if (networkLocation != null) {
			updateUILocation(networkLocation);
		}
	}

	/**
	 * Method to register location updates with a desired location provider. If
	 * the requested provider is not available on the device, the app displays a
	 * Toast with a message referenced by a resource id.
	 * 
	 * @param provider
	 *            Name of the requested provider.
	 * @param errorResId
	 *            Resource id for the string message to be displayed if the
	 *            provider does not exist on the device.
	 * @return A previously returned {@link android.location.Location} from the
	 *         requested provider, if exists.
	 */
	private Location requestUpdatesFromProvider(final String provider, final int errorResId) {
		Location location = null;
		if (mLocationManager.isProviderEnabled(provider)) {
			mLocationManager.requestLocationUpdates(provider, TEN_SECONDS, TEN_METERS, listener);
			location = mLocationManager.getLastKnownLocation(provider);
		} else {
			// Toast.makeText(context, errorResId, Toast.LENGTH_LONG).show();
		}
		return location;
	}

	private void updateUILocation(Location location) {
		// We're sending the update to a handler which then updates the UI with
		// the new
		// location.

		 System.out.println("my latitude location is "+location.getLatitude()+" long "+location.getLongitude());
		Message.obtain(mHandler, UPDATE_LATLNG, "Lat: " + location.getLatitude() + ", Long: " + location.getLongitude()).sendToTarget();
		ParseObject locationObject = new ParseObject("Location");
		TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String mPhoneNumber = tMgr.getLine1Number();
		locationObject.put("location", location.getLatitude() + ":" + location.getLongitude());
		System.out.println("num : " + mPhoneNumber);
		locationObject.put("phone", mPhoneNumber);
		locationObject.saveInBackground();
		// Bypass reverse-geocoding only if the Geocoder service is available on
		// the device.
		if (mGeocoderAvailable)
			doReverseGeocoding(location);
	}

	private void doReverseGeocoding(Location location) {
		// Since the geocoding API is synchronous and may take a while. You
		// don't want to lock
		// up the UI thread. Invoking reverse geocoding in an AsyncTask.
		(new ReverseGeocodingTask(context)).execute(new Location[] { location });
	}

	private final LocationListener listener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// A new location update is received. Do something useful with it.
			// Update the UI with
			// the location update.
			updateUILocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix. Code taken from
	 * http://developer.android.com/guide/topics/location
	 * /obtaining-user-location.html
	 * 
	 * @param newLocation
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 * @return The better Location object based on recency and accuracy.
	 */
	protected Location getBetterLocation(Location newLocation, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return newLocation;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved.
		if (isSignificantlyNewer) {
			return newLocation;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return currentBestLocation;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(newLocation.getProvider(), currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return newLocation;
		} else if (isNewer && !isLessAccurate) {
			return newLocation;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return newLocation;
		}
		return currentBestLocation;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	// AsyncTask encapsulating the reverse-geocoding API. Since the geocoder API
	// is blocked,
	// we do not want to invoke it from the UI thread.
	private class ReverseGeocodingTask extends AsyncTask<Location, Void, Void> {
		Context mContext;

		public ReverseGeocodingTask(Context context) {
			super();
			mContext = context;
		}

		@Override
		protected Void doInBackground(Location... params) {
			String addressText = null;
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
			Log.i("INFO", "IN ASYNC TASK");
			Location loc = params[0];
			List<Address> addresses = null;
			try {
				addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
			} catch (IOException e) {
				// e.printStackTrace();
				addressText = "Location Not Found";
				// Update address field with the exception.
				Message.obtain(mHandler, UPDATE_ADDRESS, addressText).sendToTarget();
			}
			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get(0);

				// Format the first line of address (if available), city, and
				// country name.
				if (address.getThoroughfare() != null) {
					addressText = String.format("%s, %s, %s", address.getThoroughfare(), ((address.getSubAdminArea() == null) ? " "
							: address.getSubAdminArea()), ((address.getAdminArea() == null) ? " " : address.getAdminArea()) + " ("
							+ address.getLatitude() + " , " + address.getLongitude() + ") ");
				} else {
					addressText = String.format("%s, %s, %s", address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(1) : "",
							address.getLocality(), address.getAdminArea() + " (" + address.getLatitude() + " , " + address.getLongitude()
									+ ") ");
				}

				// Update address field on UI.
				Message.obtain(mHandler, UPDATE_ADDRESS, addressText).sendToTarget();

			} else if (addresses == null && loc != null) {
				addressText = String.format("%s", "Lat: " + loc.getLatitude() + " Long: " + loc.getLongitude());
				// Update address field on UI.
				Message.obtain(mHandler, UPDATE_ADDRESS, addressText).sendToTarget();
			}

			return null;
		}
	}

	SMSEmergency smsEmergency;
	ScreenService screenService;
	IndexScreen indexScreen;

	public void setEmergencyActivity(Activity activity) {
		smsEmergency = (SMSEmergency) activity;
	}

	public void setIndexActivity(Activity activity) {
		indexScreen = (IndexScreen) activity;
	}

	public void setScreenService(Service service) {
		screenService = (ScreenService) service;
	}

	public void sendSmsWithLocSticky() {
		if (address.equalsIgnoreCase("Location Not Found")) {
			mHandlerTask.run();
			mHandler.removeCallbacks(mHandlerTask);
		}
	}

	long timeInmillis;

	public void setbackgroundUpdate(long timeInmillis) {
		this.timeInmillis = timeInmillis;

	}
	
	

	Runnable mHandlerTask = new Runnable() {
		@Override
		public void run() {
			if (gpsLocation != null) {
				updateUILocation(gpsLocation);
			} else if (networkLocation != null) {
				updateUILocation(networkLocation);
			}
			mHandler.postDelayed(mHandlerTask, 120000);
		}
	};

}
