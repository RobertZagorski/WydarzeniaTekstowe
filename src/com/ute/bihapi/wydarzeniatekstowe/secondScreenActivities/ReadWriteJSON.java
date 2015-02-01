package com.ute.bihapi.wydarzeniatekstowe.secondScreenActivities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class ReadWriteJSON {
	
	ReadWriteJSON rwJSON;
	public String[] layoutElements = {	"Event",
			  							"Date",
			  							"Hour",
			  							"Message",
			  							"Person",
			  							"Point",
			  							"WhenToSend"};
	private ReadWriteJSON(){}
	
	private static class ReadWriteJSONHolder { 
        private final static ReadWriteJSON instance = new ReadWriteJSON();
    }
	
	public static ReadWriteJSON get()
	{
		return ReadWriteJSONHolder.instance;
	}
	
	public void writeToFile(String data, Context context) {
		JSONObject eventsObject;
		JSONArray eventsArray;
		JSONObject obj = new JSONObject();
		JSONArray array = new JSONArray();
	    try {
	    	File file = new File(context.getFilesDir() + "/events.txt");
	    	if(file.exists())      
	    	{
	    		String alreadythere = readFromFile(context);
	    		Log.i("RWJSONwriteToFile:41",alreadythere);
	    		eventsObject = new JSONObject(alreadythere);
	    		if ((eventsArray = eventsObject.optJSONArray("events")) != null)
	    		{
	    			eventsArray.put(eventsArray.length(), new JSONObject(data).getJSONObject("events"));
	    			obj.put("events", eventsArray);
	    		}
	    		else
	    		{
	    			eventsObject = new JSONObject(alreadythere).getJSONObject("events");
	    			array.put(new JSONObject(data).getJSONObject("events"));
	    			array.put(eventsObject);
	    			obj.put("events", array);
	    		}
	    		Log.i("RWJSONwriteToFile:52",obj.toString(4));
	    		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("events.txt", Context.MODE_PRIVATE));
	    		outputStreamWriter.write(obj.toString(4));
	    		outputStreamWriter.close();
	    		
	    	}
	    	else
	    	{
	    		Log.i("RWJSONwriteToFile:66",data);
	    		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("events.txt", Context.MODE_PRIVATE));
	    		outputStreamWriter.write(data);
	    		outputStreamWriter.close();
	    	}
	    }
	    catch (IOException e) {
	        Log.e("Exception", "File write failed: " + e.toString());
	    } catch (JSONException e) {
			e.printStackTrace();
		} 
	}


	public String readFromFile(Context context) {

	    String ret = "";
	    try {
	        InputStream inputStream = context.openFileInput("events.txt");

	        if ( inputStream != null ) {
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	            String receiveString = "";
	            StringBuilder stringBuilder = new StringBuilder();

	            while ( (receiveString = bufferedReader.readLine()) != null ) {
	                stringBuilder.append(receiveString);
	            }

	            inputStream.close();
	            ret = stringBuilder.toString();
	        }
	    }
	    catch (FileNotFoundException e) {
	        Log.e("login activity", "File not found: " + e.toString());
	    } catch (IOException e) {
	        Log.e("login activity", "Can not read file: " + e.toString());
	    }

	    return ret;
	}
	
	public String createJSON(Bundle bundle)
	{
		JSONObject json = new JSONObject();
		Bundle mp = null;
		try {
			
			if(bundle != null) {
		    	
		    	for (String layoutElement : layoutElements)
		    	{
		    		if (bundle.get(layoutElement) != null)
			    	{
		    			if (layoutElement.equals("Person"))
				    	{
				    		mp = bundle.getBundle("Person");
				    		Log.i("ReadWriteJSON:131", "Got entry number to send message: "+ mp.keySet().toArray()[0].toString() + " "+mp.get(mp.keySet().toArray()[0].toString()).toString());
				    		json.put(layoutElement, mp);
				    	}
		    			else if (layoutElement.equals("Point"))
				    	{
				    		mp = bundle.getBundle("Point");
				    		Log.i("ReadWriteJSON:137", "Got entry to be sent: "+ mp.keySet().toArray()[0].toString() + " "+mp.get(mp.keySet().toArray()[0].toString()).toString());
				    		json.put(layoutElement, mp);
				    	}
		    			else
		    			{
		    				Log.i("ReadWriteJSON:142", "Got entry to be sent: "+ layoutElement +": "+bundle.get(layoutElement).toString());
		    				json.put(layoutElement, bundle.getString(layoutElement));
		    			}
			    	}
		    	}
		    }
			JSONObject js = new JSONObject();
			js.put("events", json);
			Log.i("createJSON",js.toString());
			return js.toString(4);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Bundle[] getBundleFromJSON(String JSONobject)
	{
		Bundle[] bundle = null;
		String name;
		Iterator<?> iterator;
		try {
			Log.i("getBundleFromJSON:135", JSONobject);
			JSONArray eventsArray;
			JSONObject eventsObject;
			eventsObject = new JSONObject(JSONobject);
			if ( (eventsArray = eventsObject.optJSONArray("events")) != null)
			{
				Log.i("getBundleFromJSON:140",eventsArray.toString(4));
				bundle = new Bundle[eventsArray.length()];
				for (int i = 0, size = eventsArray.length(); i < size; i++)
			    {
			      JSONObject objectInArray = eventsArray.getJSONObject(i);
			      iterator = objectInArray.keys();
			      bundle[i] = new Bundle();
			      while (iterator.hasNext())
			      {
			    	  name = (String) iterator.next();
			    	  if (name.equals("Person") || name.equals("Point"))
			    	  {

			    		  String[] temp = objectInArray.getString(name).replaceAll("Bundle\\[\\{", "").replaceAll("\\}\\]", "").split("=");
			    		  Bundle mp = new Bundle();
			    		  mp.putString(temp[0], temp[1]);
			    		  bundle[i].putBundle(name,mp);
			    	  }
			    	  else
			    	  {
			    		  bundle[i].putString(name,objectInArray.getString(name));
			    	  }
			    	  
			      }
			    }
			}
			else
			{
				eventsObject = eventsObject.getJSONObject("events");
				bundle = new Bundle[1];
				bundle[0] = new Bundle();
				iterator = eventsObject.keys();
			    while (iterator.hasNext())
			    {
			    	  name = (String) iterator.next();
			    	  if (name.equals("Person") || name.equals("Point"))
			    	  {			    		  
			    		  String[] temp = eventsObject.getString(name).replaceAll("Bundle\\[\\{", "").replaceAll("\\}\\]", "").split("=");
			    		  Bundle mp = new Bundle();
			    		  mp.putString(temp[0], temp[1]);
			    		  bundle[0].putBundle(name,mp);
			    	  }
			    	  else
			    	  {
			    		  bundle[0].putString(name,eventsObject.getString(name));
			    	  }
			    }
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bundle;
	}
	
	public Boolean ifEventsExist(Context context)
	{
		File file = new File(context.getFilesDir() + "/events.txt");
    	if(file.exists())      
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
	}
}
