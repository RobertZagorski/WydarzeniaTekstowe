package com.ute.bihapi.wydarzeniatekstowe.httpResponse;

import java.util.ArrayList;
import java.util.TreeMap;

public class SwimmingPoolsResponse extends HttpResponseFactory {
	
	//Zestaw String typu Final z mozliwymi do zwrocenia property
	//itp.
	
	public ArrayList<TreeMap<String, String>> getProperty(String JSONResponse)
	{
		if (super.getProperty(JSONResponse).isEmpty())
			return null;
		else
			return super.getProperty(JSONResponse);
		
	}

}
