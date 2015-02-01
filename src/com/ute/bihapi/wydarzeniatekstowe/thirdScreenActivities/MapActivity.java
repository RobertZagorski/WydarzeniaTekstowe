package com.ute.bihapi.wydarzeniatekstowe.thirdScreenActivities;

import java.util.ArrayList;



//Google Maps API
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
//Google Maps Markers API
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.ute.bihapi.wydarzeniatekstowe.R;
import com.ute.bihapi.wydarzeniatekstowe.thirdScreenActivities.util.GooglePlaces;
import com.ute.bihapi.wydarzeniatekstowe.thirdScreenActivities.util.SystemUiHider;
import com.ute.bihapi.wydarzeniatekstowe.thirdScreenActivities.util.Place;



//Android API
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;

//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapFragment;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MapActivity extends FragmentActivity implements OnMapLongClickListener, OnCameraChangeListener, OnMarkerClickListener {
	
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 5000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
	
	View controlsView;
	View contentView;
	
	LatLng point;
	String place;
	
	/**
	 * Obiekt mapy Google wykorzystywany w aplikacji.
	 */
	private GoogleMap googleMap = null;
	ListView listView;
	
	Bundle memory;
	public String[] layoutElements = {"Event",
									  "Date",
									  "Hour",
									  "Message",
									  "Person",
									  "WhenToSend"};
	
	public float[] markers = {	BitmapDescriptorFactory.HUE_AZURE,
								BitmapDescriptorFactory.HUE_BLUE,
								BitmapDescriptorFactory.HUE_CYAN,
								BitmapDescriptorFactory.HUE_GREEN,
								BitmapDescriptorFactory.HUE_MAGENTA,
								BitmapDescriptorFactory.HUE_ORANGE,
								BitmapDescriptorFactory.HUE_RED,
								BitmapDescriptorFactory.HUE_ROSE,
								BitmapDescriptorFactory.HUE_VIOLET,
								BitmapDescriptorFactory.HUE_YELLOW};
	public Marker[] markersHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_map);
		setupActionBar();

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_map);
		initilizeMap();		
		
		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider.setOnVisibilityChangeListener(
			new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							//contentView.setPadding(0, 0, 0, 0);
							contentView.animate().y(0).start();
							findViewById(R.id.listView).setVisibility(View.GONE);
							//contentView.animate().y(contentView.getMeasuredHeight()).start();
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							contentView.animate().y(-70).start();
							//contentView.animate().y(contentView.getMeasuredHeight()-70).start();
							//contentView.setPadding(0, 110, 0, 70);
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
							findViewById(R.id.listView).setVisibility(View.VISIBLE);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.map_button).setOnTouchListener(
				mDelayHideTouchListener);
		
		memory = new Bundle();
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
		    if(extras != null) {
		    	
		    	for (String layoutElement : layoutElements)
		    	{
		    		if (extras.get(layoutElement) != null)
			    	{
		    			if (layoutElement.equals("Person"))
				    	{
		    				Bundle mp = null;
				    		mp = extras.getBundle("Person");
				    		Log.i("Memory", "Got entry from previous activity: "+ mp.keySet().toArray()[0].toString() + " "+mp.get(mp.keySet().toArray()[0].toString()).toString());
				    		memory.putBundle("Person", mp);
				    	}
		    			else
		    			{
		    				memory.putString(layoutElement, extras.get(layoutElement).toString());
		    				Log.i("Memory", "Got entry from prevoius activity: "+ layoutElement +": "+extras.get(layoutElement).toString());
		    			}
			    	}
		    	}
		    }
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// TODO: If Settings has multiple levels, Up should navigate up
			// that hierarchy.
			returnToParentActivity();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			//contentView.setPadding(0, 30, 0, 30);
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			if (point != null)
			{
				Toast.makeText(MapActivity.this, "Chosen coordinates: "+ place +" (" + String.format( "%.3f", MapActivity.this.point.latitude )
																		+ ";" + String.format( "%.3f", MapActivity.this.point.longitude ) + ")", Toast.LENGTH_SHORT).show();
				returnToParentActivity();
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	/**
	 * Metoda uruchamiaj¹ca mapê.
	 */
	private void initilizeMap() {
		
		if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.fullscreen_map)).getMap();
 
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "B³¹d!. Nie mo¿na stworzyæ mapê.", Toast.LENGTH_SHORT)
                        .show();
            }
        }
		Location l;
		LocationManager lm=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		Criteria c=new Criteria();
		String provider=lm.getBestProvider(c, false);
		l=lm.getLastKnownLocation(provider);
		Log.i("MapActivity",l.toString());
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(l.getLatitude(),l.getLongitude()), 14));
		UiSettings mapSettings;
		mapSettings = googleMap.getUiSettings();
		mapSettings.setCompassEnabled(false);
		googleMap.setMyLocationEnabled(true);
		mapSettings.setMyLocationButtonEnabled(true);
		mapSettings.setZoomControlsEnabled(true);
		mapSettings.setZoomGesturesEnabled(true);
		mapSettings.setScrollGesturesEnabled(true);
		googleMap.setOnMapLongClickListener (this);
		googleMap.setOnCameraChangeListener(this);
		googleMap.setOnMarkerClickListener(this);
		googleMap.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() 
		{
		    @Override
		    public boolean onMyLocationButtonClick() 
		    {
		      Log.i("Map default location button","My location button clicked");
		      //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(, 20));
		      return true;
		    }
		});
	}

	@Override
	public void onMapLongClick(LatLng point) {
		// TODO Auto-generated method stub
		this.point = point;
		this.place = "Undefined";
		alertMessage();
	}
	
	public void alertMessage() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int which) {
                     switch (which) {
                     case DialogInterface.BUTTON_POSITIVE:
                            // Yes button clicked
                    	 	//returnToParentActivity();
                    	 ((Button)(findViewById(R.id.map_button))).setText(place + " (" + String.format( "%.3f", MapActivity.this.point.latitude )
                    			 											     + ";" + String.format( "%.3f", MapActivity.this.point.longitude ) + ")");
                    	 Toast.makeText(getBaseContext(),"Point Chosen: " + place
     							+ " (" + String.format( "%.3f", MapActivity.this.point.latitude ) 
     							+ ";" + String.format( "%.3f", MapActivity.this.point.longitude ) 
     							+ ")", Toast.LENGTH_SHORT).show();
                    	 break;

                     case DialogInterface.BUTTON_NEGATIVE:
                            break;
                     }
               }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Point was chosen: " + place
							+ " (" + String.format( "%.3f", this.point.latitude ) 
							+ ";" + String.format( "%.3f", this.point.longitude ) 
							+ ")")
                     .setPositiveButton("Ok", dialogClickListener)
                     .setNegativeButton("Once again", dialogClickListener).show();
    }
	
	private void returnToParentActivity()
	{
    	 Intent intent = NavUtils.getParentActivityIntent(MapActivity.this);
    	 for (String entry : memory.keySet())
    	 {
    		 if (entry == "Person")
    		 {
    			 intent.putExtra(entry, (Bundle)memory.get(entry));
    		 }
    		 else
    		 {
    			 intent.putExtra(entry, memory.getString(entry));
    		 }
    		 Log.i("Memory", "Sending entry to previous activity: "+entry +": "+memory.get(entry));
    	 }
    	 intent.putExtra("Point", point);
    	 intent.putExtra("PointName", place);
    	 NavUtils.navigateUpTo(MapActivity.this, intent);
	}

	@Override
	public void onCameraChange(CameraPosition arg0) {
		LatLng currentPosition = googleMap.getCameraPosition().target;
		new GooglePlaces(currentPosition,this);
		
	}
	
	public void returnPlacesArray(ArrayList<Place> places)
	{
		final ArrayList<Place> places2 = new ArrayList<Place>(places);
		markersHolder = new Marker[places2.size()];
		ArrayAdapter<Place> placeAdapter = new ArrayAdapter<Place>(this, R.layout.listrow, places2)
				{
					@Override
				    public View getView(int rowIndex, View convertView, ViewGroup parent) {
				        View row = convertView;
				        if(null == row) {
				            LayoutInflater layout = (LayoutInflater)getSystemService(
				                    Context.LAYOUT_INFLATER_SERVICE
				            );
				            row = layout.inflate(R.layout.listrow, null);
				        }
				        Place place = places2.get(rowIndex);
				        if(null != place) {
				            TextView name = (TextView) row.findViewById(R.id.htttptestrow_name);
				            TextView vicinity = (TextView) row.findViewById(
				                    R.id.httptestrow_vicinity);
				            if(null != name) {
				                name.setText(place.getName());
				            }
				            if(null != vicinity) {
				                vicinity.setText(place.getVicinity());
				            }
				            markersHolder[rowIndex] = googleMap.addMarker(new MarkerOptions()
																	.position( new LatLng(place.getGeometry()[0],place.getGeometry()[1]) )
																	.title(place.getName())
																	.snippet(place.getTypes()[0])
																	.icon(BitmapDescriptorFactory.defaultMarker( markers[ rowIndex % (markers.length) ] )));
				            Log.i("MapActivity:466","Added Marker: " + place.getName());
				        }
				        return row;
				    }
				};
		 listView = (ListView)findViewById(R.id.listView);
		 listView.setAdapter(placeAdapter);
		 listView.setOnItemClickListener(new OnItemClickListener() {
			 @Override
			 public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
			 {
				 markersHolder[position].showInfoWindow();
			 }			 
		 });
	}

	@Override
	public boolean onMarkerClick(Marker mark) {
		this.point = mark.getPosition();
		this.place = mark.getTitle();
		alertMessage();
		return false;
	}
}
