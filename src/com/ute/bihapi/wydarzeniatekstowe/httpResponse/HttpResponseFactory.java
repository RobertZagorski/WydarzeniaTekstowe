package com.ute.bihapi.wydarzeniatekstowe.httpResponse;

import java.util.ArrayList;
import java.util.TreeMap;

import org.json.*;

import android.util.Log;

public class HttpResponseFactory implements HttpResponseInterface {

	JSONArray mainJsonArray;
	JSONObject geometry;
	JSONArray property;
	JSONObject objects;
	TreeMap<String,String> map;
	
	@Override
	public void processResponse(String httpJsonResponse) {
		//JSONObject objects = mainJsonTokener.getJSONObject("geometry");
		try {
			mainJsonArray = new JSONObject(httpJsonResponse).getJSONArray("data");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		///Sprawdzenie, czy zwrocono poprawna odpowiedz
		///Metoda has z JSONObject
	}

	
	//returns map Object - property of the main set
	@Override
	public ArrayList<TreeMap<String, String>> getProperty(String propertyName) {
		ArrayList<TreeMap<String,String>> list = new ArrayList<TreeMap<String,String>>();
		try {
			for (int i=0; i < mainJsonArray.length(); i++) {
				JSONObject object=mainJsonArray.getJSONObject(i);
				
				//Parse geometry
				geometry = object.getJSONObject("geometry");
				String latitude = geometry.getJSONObject("coordinates").getString("lat");
				String longitude = geometry.getJSONObject("coordinates").getString("lon");
				
				//Parse properties
				property = object.getJSONArray("property");
				String name = property.getJSONObject(2).getString("value");
				
				TreeMap<String,String> map = new TreeMap<String,String>();
				map.put("name", name);
				map.put("lat", latitude);
				map.put("lon", longitude);
				
				list.add(map);				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * 	for (int i=0; i < jsonArray.length(); i++) {
      		JSONObject object=jsonArray.getJSONObject(i);
      		String id=object.getString("pk");
		 * 
		 * */
		return list;
	}

}
