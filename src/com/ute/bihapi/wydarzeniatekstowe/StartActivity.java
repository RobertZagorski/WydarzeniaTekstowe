package com.ute.bihapi.wydarzeniatekstowe;

import com.ute.bihapi.wydarzeniatekstowe.secondScreenActivities.AboutActivity;
import com.ute.bihapi.wydarzeniatekstowe.secondScreenActivities.CreateNewEventActivity;
import com.ute.bihapi.wydarzeniatekstowe.secondScreenActivities.IncomingEventsActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * Aplikacja napisana w ramach projektu UTE BiHapi
 * 
 * wykorzystywane API
 *
 *	ORANGE:
 *		send SMS
 *		send USSD
 *		SIM Geolocalisation
 *		getTime

 *	API WARSZAWA:
 *		swimming pools
 *		sport fields
 *		theatre
 *		squares
 * @author Robert2
 *
 */
public class StartActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		findViewById(R.id.optionbutton1).setOnClickListener(this);
		findViewById(R.id.optionbutton2).setOnClickListener(this);
		findViewById(R.id.optionbutton3).setOnClickListener(this);
		findViewById(R.id.optionbutton4).setOnClickListener(this);
		if (!isConnectingToInternet())
		{
			Toast.makeText(this, "No Internet Access", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
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
	public void onClick(View v) {
		//Sending data to another Activity
        //nextScreen.putExtra("name", inputName.getText().toString());
        //nextScreen.putExtra("email", inputEmail.getText().toString());

        //Log.e("n", inputName.getText()+"."+ inputEmail.getText());
		switch (v.getId()) 
		{
	    	case R.id.optionbutton1:
	    	{
	    		Intent nextScreen = new Intent(getApplicationContext(), CreateNewEventActivity.class);
	    		startActivity(nextScreen);
	    		break;
	    	}
	    	case R.id.optionbutton2:
	    	{
	    		Intent nextScreen = new Intent(getApplicationContext(), IncomingEventsActivity.class);
	    		startActivity(nextScreen);
	    		break;
	    	}
	    	case R.id.optionbutton3:
	    	{
	    		break;
	    	}
	    	case R.id.optionbutton4:
	    	{
	    		Intent nextScreen = new Intent(getApplicationContext(), AboutActivity.class);
	    		startActivity(nextScreen);
	    		break;
	    	}
	    }	
	}
	
	public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null)
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null)
                  for (int i = 0; i < info.length; i++)
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
  
          }
          return false;
    }
}
