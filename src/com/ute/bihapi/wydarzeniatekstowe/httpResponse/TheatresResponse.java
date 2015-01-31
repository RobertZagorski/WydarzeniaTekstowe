package com.ute.bihapi.wydarzeniatekstowe.httpResponse;

import java.util.ArrayList;
import java.util.TreeMap;

public class TheatresResponse extends HttpResponseFactory {
	
	//Zestaw String typu Final z mozliwymi do zwrocenia property
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "latitude";
	public static final String NAME = "Name";
	//itp.
	
	public ArrayList<TreeMap<String, String>> getProperty(String propertyName)
	{
		if (super.getProperty(propertyName).isEmpty())
			return null;
		else
			return super.getProperty(propertyName);
		
	}

}