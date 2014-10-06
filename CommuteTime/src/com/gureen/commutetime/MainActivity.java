package com.gureen.commutetime;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {
	ActionBar.Tab 				mCommuteTimesDisplayTab;
	ActionBar.Tab 				mSettingsTab;
	ActionBar.Tab 				mInfoTab;
	
	InfoFragment 				mInfoFragment;
	CommuteTimesDisplayFragment mCommuteTimesDisplayFragment;
	SettingsFragment 			mSettingsFragment;
	
	/* IDs for messages sent to this Activity */
	public class MessageID {
		final static int ID_REFRESH_COMMUTE_DATA_FROM_WWW = 96;
		final static int ID_SUCCESSFUL_COMMUTE_DATA_RECEIVED_FROM_WWW = 97;
		final static int ID_GEOCODE_DATA_RECEIVED_FROM_WWW = 98;
		final static int ID_FAILED_TO_RECEIVE_COMMUTE_DATA_FROM_WWW = 99;
	};
	
	/* Interface which needs to be implemented by class which
	 * will receive notification when commute data is obtained from WWW
	 */
	public interface receivedWWWCommuteDataListener {
		public void onReceiveCommuteDataFromWWW(Bundle bundle);
	}
	
	/* Action bar listener */
	public class ActionBarListener implements ActionBar.TabListener {
		Fragment mFragment;
		Context mContext;
		
	    public ActionBarListener(Fragment fragment, Context context) {
	    	mFragment = fragment;
	    	mContext  = context;
	    }
	      
	    /* Set Active icon for selected tab */
	    private void setActiveIcon(Tab tab) {
	    	if(tab == mCommuteTimesDisplayTab) {
	    		tab.setIcon(R.drawable.traffic_active);
	    	} else if( tab == mSettingsTab) {
	    		tab.setIcon(R.drawable.settings_active);
	    	} else if( tab == mInfoTab) {
	    		tab.setIcon(R.drawable.info_active);
	    	}
	    } 

	    /* Set InActive icon for Unselected tab */
	    private void setInActiveIcon(Tab tab) {
	    	if(tab == mCommuteTimesDisplayTab) {
	    		tab.setIcon(R.drawable.traffic_inactive);
	    	} else if( tab == mSettingsTab) {
	    		tab.setIcon(R.drawable.settings_inactive);
	    	} else if( tab == mInfoTab) {
	    		tab.setIcon(R.drawable.info_inactive);
	    	}
	    }

	    @Override
	    public void onTabReselected(Tab tab, FragmentTransaction ft) {
	    	setActiveIcon(tab);
	    }

	    @Override
	    public void onTabSelected(Tab tab, FragmentTransaction ft) {
	    	  setActiveIcon(tab);

	    	  getFragmentManager().beginTransaction()
	    	                      .replace(R.id.tabcontent, mFragment)
	    	                      .commit();
	    }

	    @Override
	    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	    	setInActiveIcon(tab);
	    }
	        
	}
	
	//public void switchToTab(Tab tab) {
	//	getActionBar().setSelectedNavigationItem(tab.getPosition());
	//}

	/* return handler for messages to this activity */
	public Handler getHandler() {
		return mCommuteTimesMessageHandler;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // ActionBar
        ActionBar action_bar = getActionBar();
        action_bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        action_bar.setDisplayShowHomeEnabled(false);
        action_bar.setDisplayShowTitleEnabled(false);
        
        //setSharedPreferences
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SetupDataModel.getInstance().setSharedPreferences(sharedPref);

        // create new tabs
        mCommuteTimesDisplayTab = action_bar.newTab().setIcon(R.drawable.traffic_inactive);
        mSettingsTab            = action_bar.newTab().setIcon(R.drawable.settings_inactive);
        mInfoTab                = action_bar.newTab().setIcon(R.drawable.info_inactive);
        
        // create the fragments
        mInfoFragment				 	= new InfoFragment();
        mCommuteTimesDisplayFragment 	= new CommuteTimesDisplayFragment();
        mSettingsFragment 				= new SettingsFragment();
        
        // Set tab listeners
        mCommuteTimesDisplayTab.setTabListener(new ActionBarListener(mCommuteTimesDisplayFragment,
                                getApplicationContext()));
        mInfoTab.setTabListener(new ActionBarListener(mInfoFragment,
                                getApplicationContext()));
        mSettingsTab.setTabListener(new ActionBarListener(mSettingsFragment,
                                getApplicationContext()));

        // add the tabs
        action_bar.addTab(mCommuteTimesDisplayTab);
        action_bar.addTab(mSettingsTab);
        action_bar.addTab(mInfoTab);
        action_bar.show();

        setContentView(R.layout.main);
    }
    
    /* Called when the user touches the Submit button. This needs to
     * be communicated down to CommuteDisplayTab fragment. */
    public void onFinishEnteringLocations(View view) {
    	mSettingsFragment.findIfInputDataIsValid();
    	getActionBar().setSelectedNavigationItem(mCommuteTimesDisplayTab.getPosition());
    }
    
    /* When commute times display data is received from CommuteTimesWWWFetcher */
    public void sendReceivedCommuteDisplayDataToFragment(Bundle bundle) {
    	mCommuteTimesDisplayFragment.onReceiveCommuteDataFromWWW(bundle);
    }
    

    /* When Geocode data is received from GMapsApiGeoCodingResponseJsonParser,
     * send it to settings fragment */
    public void sendReceivedGeoCodeDataToFragment(Bundle bundle) {
    	mSettingsFragment.onReceivedAddressValidationResponse(bundle);
    }
    
    /* This is a global message handler for UI thread. Messages are posted by
     *  both UI thread and  other threads in the Application. Messages are defined by
     * CommuteMessage class. */
    protected Handler mCommuteTimesMessageHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message inputMessage) {
			Bundle bundle = inputMessage.getData();
			Log.d(this.toString(), String.format("Message received in main activity: msg=%s", inputMessage));

			switch(inputMessage.what) {

			case MessageID.ID_SUCCESSFUL_COMMUTE_DATA_RECEIVED_FROM_WWW:
				sendReceivedCommuteDisplayDataToFragment(bundle);
				break;

			case MessageID.ID_GEOCODE_DATA_RECEIVED_FROM_WWW :
				sendReceivedGeoCodeDataToFragment(bundle);
				break;
			}
	        
			super.handleMessage(inputMessage);
		}
	};
}
