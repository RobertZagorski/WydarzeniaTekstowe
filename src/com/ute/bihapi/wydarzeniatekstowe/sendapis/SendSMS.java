package com.ute.bihapi.wydarzeniatekstowe.sendapis;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.ute.bihapi.wydarzeniatekstowe.httpRequest.HttpRequest;

public class SendSMS implements SendInterface {

	String To;
	String From;
	String MessageBody;
	private String adres;
	Boolean sentSuccessfully;
	
	public SendSMS(String To, String From, String Message)
	{
		this.To = To;
		this.From = From;
		this.MessageBody = Message;
		this.sentSuccessfully = false;
	}
	
	public Boolean send()
	{
		new FetchData() { 
	        protected void onPostExecute(Boolean result) {
	            sentSuccessfully = true;
	        }
	    }.execute();
	    return this.sentSuccessfully;
	}
	
	public String constructRequest()
	{
		adres = "https://api.bihapi.pl/orange/oracle/sendsms/?from="+From
												+"&to="+To
												+"&msg="+MessageBody;
		return adres;
	}
	
	class FetchData extends AsyncTask<Void, Integer, Boolean> {
	    @Override
	    protected Boolean doInBackground(Void... arg0) {
	    	Log.i("SendSMS:48","Sending SMS");
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
