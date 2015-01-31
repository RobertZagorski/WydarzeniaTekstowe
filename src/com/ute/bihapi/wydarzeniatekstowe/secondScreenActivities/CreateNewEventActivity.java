package com.ute.bihapi.wydarzeniatekstowe.secondScreenActivities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.maps.model.LatLng;
import com.ute.bihapi.wydarzeniatekstowe.R;
import com.ute.bihapi.wydarzeniatekstowe.sendapis.SendSMS;
import com.ute.bihapi.wydarzeniatekstowe.thirdScreenActivities.ContactsListActivity;
import com.ute.bihapi.wydarzeniatekstowe.thirdScreenActivities.MapActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class CreateNewEventActivity extends Activity implements OnClickListener {

	Bundle mp = null;
	private LatLng mapPoint;
	Boolean BIHAPI;
	HashMap<String, TextView> layoutElements;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_event);
		this.findViewById(R.id.cne_includebutton).setOnClickListener(this);
		this.findViewById(R.id.cne_button_place).setOnClickListener(this);
		this.findViewById(R.id.cne_checkBox).setOnClickListener(this);
		this.findViewById(R.id.cne_sendbutton).setOnClickListener(this);
		Boolean BIHAPI = true;
		final String[] regex = {"\\d{2}\\-\\d{2}\\-\\d{4}",
		                        "\\d{2}\\:\\d{2}",
		                        "\\d{2}\\:\\d{2}"};  

		layoutElements = new HashMap<String, TextView>();
		layoutElements.put("EventName", (EditText) findViewById(R.id.cne_edit1));
		layoutElements.put("Date", (EditText) findViewById(R.id.cne_edit2));
		layoutElements.put("Hour", (EditText) findViewById(R.id.cne_edit3));
		layoutElements.put("Include", (EditText) findViewById(R.id.cne_edit4));
		layoutElements.put("Person", (MultiAutoCompleteTextView) findViewById(R.id.cne_edit5));
		layoutElements.put("WhenToSend", (EditText) findViewById(R.id.cne_edit6));
		layoutElements.put("Point", (TextView) findViewById(R.id.cne_text7));
		
		
		ArrayList<EditText> editFields = new ArrayList<EditText>();
		EditText dateField = (EditText)this.findViewById(R.id.cne_edit2);
		editFields.add(dateField);
		EditText HourField = (EditText)this.findViewById(R.id.cne_edit3);
		editFields.add(HourField);
		EditText HourField2 = (EditText)this.findViewById(R.id.cne_edit6);
		editFields.add(HourField2);
		int i=0;
		for (EditText editfield : editFields)
		{
			final int j = i;
			final EditText ef = editfield;
			editfield.setFilters(  
			    new InputFilter[] {  
			        new PartialRegexInputFilter(regex[i])
			    }  
			);  
			       
			editfield.addTextChangedListener(  
			    new TextWatcher(){  
			  
			            @Override  
			            public void afterTextChanged(Editable s) {  
			                String value  = s.toString();  
			                if(value.matches(regex[j]))  
			                	ef.setTextColor(Color.WHITE);  
			                else  
			                	ef.setTextColor(Color.BLACK);  
			            }  
			  
			            @Override  
			            public void beforeTextChanged(CharSequence s, int start,  
			                int count, int after) {}  
			  
			            @Override  
			            public void onTextChanged(CharSequence s, int start,  
			               int before, int count) {}  
			     }  
			);
			i++;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_new_event, menu);
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
	public void onPostCreate(Bundle savedInstanceState)
	{
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
		    if(extras != null) {
		    	
		    	for (Map.Entry<String, TextView> entry : layoutElements.entrySet())
	    		{
		    		if (extras.containsKey(entry.getKey()) /*&& extras.getString(entry.getKey()) != null /*&& extras.getString("EventName").length() > 0*/)
			    	{
		    			if (entry.getKey().equals("Person"))
		    			{
				    		mp = extras.getBundle("Person");
				    		Log.i("Memory", "Got entry from prevoius activity: "+ mp.keySet().toArray()[0].toString() + " "+mp.get( mp.keySet().toArray()[0].toString() ).toString());
				    		entry.getValue().setText( mp.keySet().toArray()[0].toString() );
		    			}
		    			else if (entry.getKey().equals("Point"))
		    			{
		    				this.mapPoint = (LatLng) extras.get("Point");
				    		Log.i("Memory", "Got entry from prevoius activity: Point: "+extras.get("Point").toString() );
				    		entry.getValue().setText( "(" + String.format( "%.3f", mapPoint.latitude ) 
																			  + ";" + String.format( "%.3f", mapPoint.longitude ) + ")" );
		    			}
		    			else
		    			{
		    				Log.i("Memory", "Got entry from prevoius activity: " + entry.getKey() + ": " + extras.get(entry.getKey()).toString() );
		    				entry.getValue().setText( extras.getString( entry.getKey() ) );
		    			}
			    	}
	    		}
		    }
		}
		super.onPostCreate(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) 
		{
		
			case R.id.cne_includebutton:
    		{
    			Intent nextScreen = new Intent(getApplicationContext(), ContactsListActivity.class);
    			for (Map.Entry<String, TextView> entry : layoutElements.entrySet())
	    		{
	    			if ( entry.getValue().getText().length() > 0)
		    		{
	    				if (entry.getKey().equalsIgnoreCase("Person"))
	    				{
	    					String personName = ( (MultiAutoCompleteTextView) findViewById(R.id.cne_edit5)).getText().toString();
	    	    			Log.i("CreateNewEventActivity:282","Person name: " + personName);
	    	    			Bundle bundle =  new Bundle();
	    	    			bundle.putString(personName,this.mp.get( personName ).toString() );
	    	    			nextScreen.putExtra("Person",bundle);
	    				}
	    				else if (entry.getKey().equalsIgnoreCase("Point"))
	    				{
	    					Bundle bund = new Bundle();
	    	    			bund.putString("Point","(" + String.format( "%.3f", mapPoint.latitude ) + ";" + String.format( "%.3f", mapPoint.longitude ) + ")" );
	    	    			nextScreen.putExtra("Point",bund);
	    				}
	    				else
	    				{
	    					nextScreen.putExtra(entry.getKey(), entry.getValue().getText().toString() );
	    				}
		    		}
	    		}
    			startActivity(nextScreen);
    			break;
    		}
	    	case R.id.cne_button_place:
	    	{
	    		Intent nextScreen = new Intent(getApplicationContext(), MapActivity.class);
	    		
	    		for (Map.Entry<String, TextView> entry : layoutElements.entrySet())
	    		{
	    			if ( entry.getValue().getText().length() > 0)
		    		{
	    				if (entry.getKey().equalsIgnoreCase("Person"))
	    				{
	    					String personName = ( (MultiAutoCompleteTextView) findViewById(R.id.cne_edit5)).getText().toString();
	    	    			Log.i("CreateNewEventActivity:282","Person name: " + personName);
	    	    			Bundle bundle =  new Bundle();
	    	    			bundle.putString(personName,this.mp.get( personName ).toString() );
	    	    			nextScreen.putExtra("Person",bundle);
	    				}
	    				else if (entry.getKey().equalsIgnoreCase("Point"))
	    				{
	    					Bundle bund = new Bundle();
	    	    			bund.putString("Point","(" + String.format( "%.3f", mapPoint.latitude ) + ";" + String.format( "%.3f", mapPoint.longitude ) + ")" );
	    	    			nextScreen.putExtra("Point",bund);
	    				}
	    				else
	    				{
	    					nextScreen.putExtra(entry.getKey(), entry.getValue().getText().toString() );
	    				}
		    		}
	    		}
	    		startActivity(nextScreen);
	    		break;
	    	}
	    	case R.id.cne_checkBox:
	    	{
	    		if ( ((CheckBox) findViewById(R.id.cne_checkBox)).isChecked() )
	    		{
	    			findViewById(R.id.cne_text6).setVisibility(View.VISIBLE);
		    		findViewById(R.id.cne_edit6).setVisibility(View.VISIBLE);
	    		}
	    		else
	    		{
	    			findViewById(R.id.cne_text6).setVisibility(View.GONE);
		    		findViewById(R.id.cne_edit6).setVisibility(View.GONE);
	    		}
	    		break;
	    	}
	    	case R.id.cne_sendbutton:
	    	{
	    		sendMessage();
	    		break;
	    	}
	    }
	}
	
	private void sendMessage()
	{
		//Zebraæ dane ze wszystkich pól i skonstruowaæ obiekt klasy zajmuj¹cej siê wys³aniem tego zdarzenia
		Bundle bundle = new Bundle();
		
		for (Map.Entry<String, TextView> entry : layoutElements.entrySet())
		{
			if ( entry.getValue().getText().length() > 0)
    		{
				if (entry.getKey().equalsIgnoreCase("Person"))
				{
					String personName = ( (MultiAutoCompleteTextView) findViewById(R.id.cne_edit5)).getText().toString();
	    			Log.i("CreateNewEventActivity:282","Person name: " + personName);
	    			Bundle bund =  new Bundle();
	    			bund.putString(personName,this.mp.get( personName ).toString() );
	    			bundle.putBundle("Person", bund);
				}
				else if (entry.getKey().equalsIgnoreCase("Point"))
				{
					Bundle bund = new Bundle();
	    			bund.putString("Point","(" + String.format( "%.3f", mapPoint.latitude ) + ";" + String.format( "%.3f", mapPoint.longitude ) + ")" );
	    			bundle.putBundle("Point", bund);
				}
				else
				{
					bundle.putString(entry.getKey(), entry.getValue().getText().toString() );
				}
    		}
		}
		((ProgressBar)findViewById(R.id.cne_progressBar)).setVisibility(View.VISIBLE);
		new SendData(this) 
		{ 
		        protected void onPostExecute(Boolean result) 
		        {
					((ProgressBar)findViewById(R.id.cne_progressBar)).setVisibility(View.GONE);
					Toast.makeText(getBaseContext(),"SMSs sent successfully", Toast.LENGTH_SHORT).show();
		        }
		}.execute(bundle);
	}
	
	class SendData extends AsyncTask<Bundle, Integer, Boolean> 
	{
		CreateNewEventActivity context;
		
		public SendData(CreateNewEventActivity context)
		{
			this.context = context;
		}
		
		@Override
		protected Boolean doInBackground(Bundle... bundle) {
			try {Thread.sleep(4000);} catch (InterruptedException e) {e.printStackTrace();}
			Bundle mp = null;
			String number = "";
			String getSimNumber = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
			String message = "";
			
			if(bundle[0] != null) {
		    	
		    	for (String layoutElement : layoutElements.keySet())
		    	{
		    		if (bundle[0].get(layoutElement) != null)
			    	{
		    			if (layoutElement.equals("Person"))
				    	{
				    		mp = bundle[0].getBundle("Person");
				    		Log.i("CreateNewActivity:307", "Got entry number to send message: "+ mp.keySet().toArray()[0].toString() + " "+mp.get(mp.keySet().toArray()[0].toString()).toString());
				    		number = mp.get(mp.keySet().toArray()[0].toString()).toString();
				    	}
		    			else if (layoutElement.equals("Point"))
				    	{
				    		mp = bundle[0].getBundle("Point");
				    		Log.i("CreateNewActivity:313", "Got entry to be sent: "+ mp.keySet().toArray()[0].toString() + " "+mp.get(mp.keySet().toArray()[0].toString()).toString());
				    		message += mp.keySet().toArray()[0].toString() + " " + mp.get(mp.keySet().toArray()[0].toString()).toString();
				    	}
		    			else
		    			{
		    				message += layoutElement + ": " + bundle[0].get(layoutElement).toString() + "\n";
		    				Log.i("CreateNewActivity:319", "Got entry to be sent: "+ layoutElement +": "+bundle[0].get(layoutElement).toString());
		    			}
			    	}
		    	}
		    }			
			
			Log.i("CreateNewEvent:273","Sending message");
			Log.i("CreateNewEvent:273","Number: " + number);
			Log.i("CreateNewEvent:274","Number: " + getSimNumber);
			Log.i("CreateNewEvent:275",message);
//			if (BIHAPI)
//			{
//				//if SMS
//				SendSMS sendSMS = new SendSMS(number,getSimNumber,message);
//				sendSMS.constructRequest();
//				return sendSMS.send();
//				//else if USSD
//			}
//			else
//			{
//				//send SMS using Android API
//			}
			
			ReadWriteJSON.get().writeToFile(ReadWriteJSON.get().createJSON(bundle[0]),this.context);
	    	return true;
		}	
	}
}

