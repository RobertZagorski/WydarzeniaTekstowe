package com.ute.bihapi.wydarzeniatekstowe.sendapis;

import android.os.AsyncTask;

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
	    while (this.sentSuccessfully == false) {}
	    return this.sentSuccessfully;
	}
	
	public String constructRequest()
	{
		adres = "https://api.bihapi.pl/sendsms/?from="+From
												+"&to="+To
												+"&msg="+MessageBody;
		return adres;
	}
	
	class FetchData extends AsyncTask<Void, Integer, Boolean> {
	    @Override
	    protected Boolean doInBackground(Void... arg0) {
	    	HttpRequest.get().execute( constructRequest() );
	        return true;
	    }	
	}
}
