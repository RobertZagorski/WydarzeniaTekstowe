package com.ute.bihapi.wydarzeniatekstowe.sendapis;

import android.os.AsyncTask;

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
		new FetchData() { 
	        protected void onPostExecute(Boolean result) {
	        	sentSuccessfully = true;
	        }
	    }.execute();
	    while (this.sentSuccessfully == false) {}
	    return this.sentSuccessfully;
	}

	@Override
	public String constructRequest() {
	   	adres = "https://api2.orange.pl/sendussd/?to="+this.To
	   													+"&msg="+this.MessageBody;
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
