package com.ute.bihapi.wydarzeniatekstowe.httpResponse;

import java.util.ArrayList;
import java.util.TreeMap;

public interface HttpResponseInterface {
	
	public void processResponse(String httpJsonResponse);
	public ArrayList<TreeMap<String, String>> getProperty(String propertyName);
}