package com.ute.bihapi.wydarzeniatekstowe.thirdScreenActivities.util;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
 


import com.google.android.gms.maps.model.LatLng;
import com.ute.bihapi.wydarzeniatekstowe.thirdScreenActivities.MapActivity;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
 
public class GooglePlaces {
    LatLng position;
    MapActivity mapActivity;
    private final String APIKEY = "AIzaSyCsiDrKmgKziPTwMLH6mK4qIL1rIRzrAcE"; //REPLACE WITH YOUR OWN GOOGLE PLACES API KEY
    private final int radius = 1000;
    private String type = "aquarium%7Cart_gallery%7Cestablishment%7Clibrary%7Cmuseum%7Cpark%7Cshopping_mall%7Cstadium";
    private StringBuilder query = new StringBuilder();
    private ArrayList<Place> places = new ArrayList<Place>();
    ProgressDialog progressDialog = null;

    public GooglePlaces(LatLng currentPosition, MapActivity mapActivity) {
    	this.position = currentPosition;
    	this.mapActivity = mapActivity;
        
        query.append("https://maps.googleapis.com/maps/api/place/nearbysearch/xml?");
        query.append("location=" +  position.latitude + "," + position.longitude + "&");
        query.append("radius=" + radius + "&");
        query.append("types=" + type + "&");
        query.append("sensor=true&"); //Must be true if queried from a device with GPS
        query.append("key=" + APIKEY);
        Log.i("GooglePlaces:66","query created: "+query.toString());
        new QueryGooglePlaces().execute(query.toString());
    }
 
    public static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
 
        InputSource is = new InputSource(new StringReader(xml));
 
        return builder.parse(is);
    }
 
    /**
     * Based on: http://stackoverflow.com/questions/3505930
     */
    private class QueryGooglePlaces extends AsyncTask<String, String, String> {
 
        @Override
        protected String doInBackground(String... args) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(args[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                	Log.i("GooglePlaces:83","Got Response: "+responseString);
                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                Log.e("ERROR", e.getMessage());
            } catch (IOException e) {
                Log.e("ERROR", e.getMessage());
            }
            return responseString;
        }
 
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                Document xmlResult = loadXMLFromString(result);
                NodeList nodeList =  xmlResult.getElementsByTagName("result");
                for(int i = 0, length = nodeList.getLength(); i < length; i++) {
                    Node node = nodeList.item(i);
                    //Log.i("GooglePlaces:118","Got list: "+node.toString());
                    if(node.getNodeType() == Node.ELEMENT_NODE) {
                        Element nodeElement = (Element) node;
                        Place place = new Place();
                        Node name = nodeElement.getElementsByTagName("name").item(0);
                        Node vicinity = nodeElement.getElementsByTagName("vicinity").item(0);
                        Node rating = nodeElement.getElementsByTagName("rating").item(0);
                        Node reference = nodeElement.getElementsByTagName("reference").item(0);
                        Node id = nodeElement.getElementsByTagName("id").item(0);
                        Node geometryElement = nodeElement.getElementsByTagName("geometry").item(0);
                        NodeList locationElement = geometryElement.getChildNodes();
                        Element latLngElem = (Element) locationElement.item(1);
                        Node lat = latLngElem.getElementsByTagName("lat").item(0);
                        Node lng = latLngElem.getElementsByTagName("lng").item(0);
                        float[] geometry =  {Float.valueOf(lat.getTextContent()),
                                Float.valueOf(lng.getTextContent())};
                        int typeCount = nodeElement.getElementsByTagName("type").getLength();
                        String[] types = new String[typeCount];
                        for(int j = 0; j < typeCount; j++) {
                            types[j] = nodeElement.getElementsByTagName("type").item(j).getTextContent();
                        }
                        place.setVicinity(vicinity.getTextContent());
                        place.setId(id.getTextContent());
                        place.setName(name.getTextContent());
                        if(null == rating) {
                            place.setRating(0.0f);
                        } else {
                            place.setRating(Float.valueOf(rating.getTextContent()));
                        }
                        place.setReference(reference.getTextContent());
                        place.setGeometry(geometry);
                        place.setTypes(types);
                        Log.i("GooglePlaces:151","Added Place: "+place.getName());
                        places.add(place);
                    }
                }
                mapActivity.returnPlacesArray(places);
 
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage());
            }
        }
    }
}