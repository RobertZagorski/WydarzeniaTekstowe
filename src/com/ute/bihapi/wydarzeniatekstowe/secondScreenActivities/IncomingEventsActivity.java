package com.ute.bihapi.wydarzeniatekstowe.secondScreenActivities;

import java.util.ArrayList;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ute.bihapi.wydarzeniatekstowe.R;
import com.ute.bihapi.wydarzeniatekstowe.thirdScreenActivities.MapActivity;
import com.ute.bihapi.wydarzeniatekstowe.thirdScreenActivities.util.Place;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class IncomingEventsActivity extends Activity implements OnItemClickListener {
	Bundle[] bundle;
	ArrayAdapter<Events> adapter;
	ListView list;
	ArrayList<Events> array;
	ArrayList<String> details;
	public String[] layoutElements = {"Event","Date","Hour","Message","Person","Point","WhenToSend"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*Bundle bu = new Bundle();
		bu.putString("Event","sth" );
		bu.putString("date","sth" );
		bu.putString("hour","sth" );
		bu.putString("message","sth" );
		bu.putString("people","sth" );
		bu.putString("place","sth" );
		Log.i("Events bundle",bu.toString());
		ReadWriteJSON.get().writeToFile( ReadWriteJSON.get().createJSON(bu), this);*/
		setContentView(R.layout.activity_incoming_events);
		if (ReadWriteJSON.get().ifEventsExist(this))
		{
			bundle = ReadWriteJSON.get().getBundleFromJSON(ReadWriteJSON.get().readFromFile(this));
			Log.i("Events bundle",bundle.toString());
			array = new ArrayList<Events>();
			for (Bundle bund : bundle)
			{
				array.add(new Events(bund.get("Event").toString(),
						bund.get("Hour").toString()+" "+bund.get("Date").toString()));	
			}
			adapter = new ArrayAdapter<Events>(this, R.layout.eventlist, array)
				{
					@Override
				    public View getView(int rowIndex, View convertView, ViewGroup parent) {
				        View row = convertView;
				        if(null == row) {
				            LayoutInflater layout = (LayoutInflater)getSystemService(
				                    Context.LAYOUT_INFLATER_SERVICE
				            );
				            row = layout.inflate(R.layout.eventlist, null);
				        }
				        Events oneEvent = array.get(rowIndex);
				        if(null != oneEvent) {
				            TextView name = (TextView) row.findViewById(R.id.ie_list_name);
				            TextView vicinity = (TextView) row.findViewById(R.id.ie_list_vicinity);
				            if(null != name) {
				                name.setText(oneEvent.Event);
				            }
				            if(null != vicinity) {
				                vicinity.setText(oneEvent.Date);
				            }
				        }
				        return row;
				    }
				};
			list = (ListView) findViewById(R.id.ie_list);
			list.setAdapter(adapter);
			list.setOnItemClickListener(this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.incoming_events, menu);
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (list != null)
		{
			String Event = array.get(position).Event;
			for (Bundle bund : bundle)
			{
				if (bund.get("Event").toString().equals(Event))
				{
			        String details = "";
			        Bundle mp = null;
			        for (String layoutElement : layoutElements)
			    	{
			    		if (bund.get(layoutElement) != null)
				    	{
			    			if (layoutElement.equals("Person"))
					    	{
					    		mp = bund.getBundle("Person");
					    		Log.i("IncomingEventsActivity:102", "Show details: "+ mp.keySet().toArray()[0].toString() + " "+mp.get(mp.keySet().toArray()[0].toString()).toString());
					    		details +="Person: " + mp.keySet().toArray()[0].toString() + " - " + mp.get(mp.keySet().toArray()[0].toString()).toString() + "\n";
					    	}
			    			else if (layoutElement.equals("Point"))
					    	{
					    		mp = bund.getBundle("Point");
					    		Log.i("IncomingEventsActivity:108", "Show details: "+ mp.keySet().toArray()[0].toString() + " "+mp.get(mp.keySet().toArray()[0].toString()).toString());
					    		details +="Place: "+ mp.get(mp.keySet().toArray()[0].toString()).toString() + "\n";
					    	}
			    			else
			    			{
			    				details += layoutElement + ": " + bund.get(layoutElement).toString() + "\n";
			    				Log.i("IncomingEventsActivity:113", "Show details: "+ layoutElement +": "+bund.get(layoutElement).toString());
			    			}
				    	}
			    	}
			        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int which) {
			                     /*(switch (which) {
			                     case DialogInterface.BUTTON_POSITIVE:
			                            // Yes button clicked
			                    	 	//returnToParentActivity();
			                    	 ((Button)(findViewById(R.id.map_button))).setText("(" + String.format( "%.3f", MapActivity.this.point.latitude )
			                    			 											     + ";" + String.format( "%.3f", MapActivity.this.point.longitude ) + ")");
			                    	 Toast.makeText(getBaseContext(),"Point Chosen: "
			     							+ "(" + String.format( "%.3f", MapActivity.this.point.latitude ) 
			     							+ ";" + String.format( "%.3f", MapActivity.this.point.longitude ) 
			     							+ ")", Toast.LENGTH_SHORT).show();
			                    	 break;

			                     case DialogInterface.BUTTON_NEGATIVE:
			                            break;
			                     }*/
			               }
			        };
			        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
			        builder.setTitle("Event details");
			        builder.setMessage(details)
			                     .setPositiveButton("Ok", dialogClickListener)
			                     /*.setNegativeButton("Once again", dialogClickListener)*/.show();
				}
			}
		}
	}
}
