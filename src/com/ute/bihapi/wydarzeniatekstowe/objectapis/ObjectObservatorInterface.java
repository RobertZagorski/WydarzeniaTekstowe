package com.ute.bihapi.wydarzeniatekstowe.objectapis;

import java.util.List;

/**
 * Observator programming pattern
 * Every each of them wil be sending a request for 
 * objects and receiving and parsing a response
 * @author Robert2
 *
 */
public interface ObjectObservatorInterface {
	//get list of Objects 
	public void getObjects();
	//extract specific list of properties from objects
	public List<String> extract(String property);
}
