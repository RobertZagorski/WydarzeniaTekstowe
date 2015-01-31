package com.ute.bihapi.wydarzeniatekstowe.objectapis;

public interface ObjectObservableInterface {
	public void addObservator(ObjectObservatorInterface object);
    public void deleteObservator(ObjectObservatorInterface object);
    public void sendRequestObservator();
    public void extractData(String property);
}
