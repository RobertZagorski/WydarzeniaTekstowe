/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ute.bihapi.wydarzeniatekstowe.thirdScreenActivities;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.ute.bihapi.wydarzeniatekstowe.R;
/**
 * FragmentActivity to hold the main {@link ContactsListFragment}. On larger screen devices which
 * can fit two panes also load {@link ContactDetailFragment}.
 */
public class ContactsListActivity extends FragmentActivity implements
        ContactsListFragment.OnContactsInteractionListener {

    // Defines a tag for identifying log entries
    private static final String TAG = "ContactsListActivity";

    // True if this activity instance is a search result view (used on pre-HC devices that load
    // search results in a separate instance of the activity rather than loading results in-line
    // as the query is typed.
    private boolean isSearchResultView = false;
    
    Bundle memory;
    public String[] layoutElements = {"Event","Date","Hour","Message","Person","Point","WhenToSend"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*if (BuildConfig.DEBUG) {
            Utils.enableStrictMode();
        }*/
        super.onCreate(savedInstanceState);

        // Set main content view. On smaller screen devices this is a single pane view with one
        // fragment. One larger screen devices this is a two pane view with two fragments.
        setContentView(R.layout.activity_contacts_main);
        
        // Check if this activity instance has been triggered as a result of a search query. This
        // will only happen on pre-HC OS versions as from HC onward search is carried out using
        // an ActionBar SearchView which carries out the search in-line without loading a new
        // Activity.
        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {

            // Fetch query from intent and notify the fragment that it should display search
            // results instead of all contacts.
            String searchQuery = getIntent().getStringExtra(SearchManager.QUERY);
            ContactsListFragment mContactsListFragment = (ContactsListFragment)
                    getSupportFragmentManager().findFragmentById(R.id.contact_list);

            // This flag notes that the Activity is doing a search, and so the result will be
            // search results rather than all contacts. This prevents the Activity and Fragment
            // from trying to a search on search results.
            isSearchResultView = true;
            mContactsListFragment.setSearchQuery(searchQuery);

            // Set special title for search results
            String title = getString(R.string.contacts_list_search_results_title, searchQuery);
            setTitle(title);
        }
        
        memory = new Bundle();
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
		    if(extras != null) {
		    	
		    	for (String layoutElement : layoutElements)
		    	{
		    		if (extras.get(layoutElement) != null)
			    	{
		    			if (layoutElement.equals("Person"))
				    	{
		    				Bundle mp = null;
				    		mp = extras.getBundle("Person");
				    		Log.i("Memory", "Got entry from previous activity: "+ mp.keySet().toArray()[0].toString() + " "+mp.get(mp.keySet().toArray()[0].toString()).toString());
				    		memory.putBundle("Person", mp);
				    	}
		    			else if (layoutElement.equals("Point"))
				    	{
		    				Bundle mp = null;
				    		mp = extras.getBundle("Point");
				    		Log.i("Memory", "Got entry from previous activity: "+ mp.keySet().toArray()[0].toString() + " "+mp.get(mp.keySet().toArray()[0].toString()).toString());
				    		memory.putBundle("Point", mp);
				    	}
		    			else
		    			{
		    				memory.putString(layoutElement, extras.get(layoutElement).toString());
		    				Log.i("Memory", "Got entry from prevoius activity: "+ layoutElement +": "+extras.get(layoutElement).toString());
		    			}
			    	}
		    	}
		    }
		}
    }

    /**
     * This interface callback lets the main contacts list fragment notify
     * this activity that a contact has been selected.
     *
     * @param contactUri The contact Uri to the selected contact.
     */
    @Override
    public void onContactSelected(Uri contactUri) {
    	String id = null, name = null, phone = null, hasPhone = null;
    	int idx;
    	Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
    	if (cursor.moveToFirst()) {
    	    idx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
    	    id = cursor.getString(idx);

    	    idx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
    	    name = cursor.getString(idx);

    	    idx = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
    	    hasPhone = cursor.getString(idx);
    	}
    	// Get phone number - if they have one
    	if ("1".equalsIgnoreCase(hasPhone)) {
    	    cursor = getContentResolver().query(
    	            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
    	            null,
    	            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "+ id, 
    	            null, null);
    	    if (cursor.moveToFirst()) {
    	        int colIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
    	        phone = cursor.getString(colIdx);
    	    }
    	    cursor.close();
    	}
    	Log.i("Phone number", name + ": " + phone);
    	
    	Bundle mp = new Bundle();
		mp.putString(name, phone);
		Toast.makeText(getBaseContext(),"Wybrano osobê: " + name + " ( "+ phone +" )", Toast.LENGTH_SHORT).show();
		Bundle extras = new Bundle();
		extras.putBundle("Person",mp);

		returnToParentActivity(extras);
    		//Set up onClick - return to previous activity
    }

    /**
     * This interface callback lets the main contacts list fragment notify
     * this activity that a contact is no longer selected.
     */
    @Override
    public void onSelectionCleared() {
    }

    @Override
    public boolean onSearchRequested() {
        return !isSearchResultView && super.onSearchRequested();
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// TODO: If Settings has multiple levels, Up should navigate up
			// that hierarchy.
			returnToParentActivity(null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
    
    private void returnToParentActivity(Bundle bundle)
	{
    	Intent intent = NavUtils.getParentActivityIntent(this);
		for (String entry : memory.keySet())
		{
			if (entry == "Person")
    		{
    			 intent.putExtra(entry, (Bundle)memory.get(entry));
    		}
			else if (entry == "Point")
			{
				intent.putExtra(entry, (Bundle)memory.get(entry));
			}
    		else
    		{
    			intent.putExtra(entry, memory.getString(entry));
    		}
    		Log.i("Memory", "Sending entry to previous activity: "+entry +": "+memory.get(entry));
		}
		if (bundle != null)
		{
			intent.putExtras(bundle);
		}
		NavUtils.navigateUpTo(this, intent);
	}
}
