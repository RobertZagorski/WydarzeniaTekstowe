package com.ute.bihapi.wydarzeniatekstowe.objectapis;

import java.util.ArrayList;

public class ObjectManager implements ObjectObservableInterface {
	
	private ArrayList<ObjectObservatorInterface> observators= new ArrayList<ObjectObservatorInterface>();
	@Override
	public void addObservator(ObjectObservatorInterface object) {
		if(object!=null)
            observators.add(object);
	}

	@Override
	public void deleteObservator(ObjectObservatorInterface object) {
		this.observators.remove(object);
	}

	@Override
	public void sendRequestObservator() {
		for(ObjectObservatorInterface ob : observators)
		{
	        ob.getObjects();
	        ///TODO rozbudowaæ i szeregowac obiekty, moze do jednej tablicy
	    }
	}

	@Override
	public void extractData(String property) {
		for(ObjectObservatorInterface ob : observators)
		{
	        ob.extract(property);
	        ///TODO rozbudowaæ i szeregowac obiekty, moze do jednej tablicy
	    }
	}

}
