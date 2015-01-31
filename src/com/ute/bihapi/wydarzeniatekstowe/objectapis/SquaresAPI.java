package com.ute.bihapi.wydarzeniatekstowe.objectapis;

import java.util.ArrayList;
import java.util.TreeMap;

import android.os.AsyncTask;

import com.ute.bihapi.wydarzeniatekstowe.httpRequest.HttpRequest;
import com.ute.bihapi.wydarzeniatekstowe.httpResponse.SquaresResponse;

public class SquaresAPI implements ObjectObservatorInterface
{
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "latitude";
	public static final String NAME = "name";
	
	///Obiekty sluzace do konstrukcji HTTP GET
	public String centerLongitude;
	public String centerLatitude;
	public String radius;
	private String adres;
	
	//Elementy wewnetrzne do przechowywania danych
	SquaresResponse sqResponse;
	ArrayList<TreeMap<String, String>> squares;
	
	public SquaresAPI(String lat, String lon, String radius)
	{
		this.centerLatitude = lat;
		this.centerLongitude = lon;
		this.radius = radius;
	}
	
	@Override
	public void getObjects() {
		new FetchData() { 
	        protected void onPostExecute(Boolean result) {
	            ///Inform, data is loaded
	        }
	    }.execute();
	}
		
	private String constructRequest()
	{
		adres = "https://api.bihapi.pl/wfs/warszawa/squares?circle="	+this.centerLongitude
																		+","+this.centerLatitude
																		+","+this.radius;
		return adres;
	}

	@Override
	public ArrayList<String> extract(String property) {
		ArrayList<String> list = new ArrayList<String>();
		if (squares != null)
		{
			for (TreeMap<String, String> s : squares) {
				list.add(s.get(property));
			}
		}
		else 
			return null;
		return list;
		
	}	
	
	class FetchData extends AsyncTask<Void, Integer, Boolean> {
	    @Override
	    protected Boolean doInBackground(Void... arg0) {
	    	squares = sqResponse.getProperty(HttpRequest.get().execute( constructRequest() ));
	        return true;
	    }	
	}
}