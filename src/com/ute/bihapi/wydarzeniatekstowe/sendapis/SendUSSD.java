package com.ute.bihapi.wydarzeniatekstowe.sendapis;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.ute.bihapi.wydarzeniatekstowe.httpRequest.HttpRequest;

public class SendUSSD implements SendInterface {

	String To;
	String MessageBody;
	private String adres;
	Boolean sentSuccessfully;
	
	public SendUSSD(String To, String Message)
	{
		this.To = To;
		this.MessageBody = Message;
		this.sentSuccessfully = false;
	}
	
	@Override
	public Boolean send() {
		AsyncTask<Void, Integer, Boolean> task = new FetchData() { 
	        protected void onPostExecute(Boolean result) {
	        	sentSuccessfully = true;
	        }
	    }.execute();
	    return this.sentSuccessfully;
	}

	@Override
	public String constructRequest() {
	   	adres = "https://api.bihapi.pl/orange/oracle/sendussd?msg="+this.MessageBody+"&to="+this.To;
		return adres;
	}

	class FetchData extends AsyncTask<Void, Integer, Boolean> {
	    @Override
	    protected Boolean doInBackground(Void... arg0) {
	    	Log.i("SendUSSD:45","Sending USSD");
	    	String response = HttpRequest.get().execute( constructRequest() );
	    	try {
				JSONObject resp = new JSONObject(response);
				if (resp.getString("result").equals("OK"))
					return true;
				else
					return false;
				
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
	    }	
	}
}
