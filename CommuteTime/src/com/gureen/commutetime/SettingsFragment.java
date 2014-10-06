package com.gureen.commutetime;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class SettingsFragment extends Fragment {
	public static final String LOG_IDENTIFIER = "SettingsFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      	View settingsView = inflater.inflate(R.layout.fragment_setup, container, false);
      	
      	/* TextWatcher class to listen edits in EditTexts in this View */
      	final class SettingsViewTextWatcher implements TextWatcher {
        	public void beforeTextChanged(CharSequence s, int start,
           	     int count, int after) {
           	}

           	public void onTextChanged(CharSequence s, int start,
           	     int before, int count) {
           	}

   			@Override
   			public void afterTextChanged(Editable arg0) {
   				persistAddresses();
   			}
       };
		
        // Add Text changed handler for addressA
        EditText addressAEditText = (EditText) settingsView.findViewById(R.id.address_A);
        addressAEditText.setText(SetupDataModel.getInstance().getAddresses()[0]);
        addressAEditText.addTextChangedListener(new SettingsViewTextWatcher());

        // Add Text changed handler for addressB
        EditText addressBEditText = (EditText) settingsView.findViewById(R.id.address_B);
        addressBEditText.setText(SetupDataModel.getInstance().getAddresses()[1]);
        addressBEditText.addTextChangedListener(new SettingsViewTextWatcher());        
		return settingsView;
	}
	
	/* Save settings to SharedPreferences */
	private void persistAddresses() {
    	SetupDataModel.getInstance().setAddresses( ((EditText) getActivity().findViewById(R.id.address_A)).getText().toString(),
    			((EditText) getActivity().findViewById(R.id.address_B)).getText().toString() );
	}
	
	/* Starts a thread which will hit network to find out
	 * if input data is valid. The thread sends a message to MainActivity, which
	 * in turn calls onReceivedAddressValidationResponse() on this Fragment.
	 */
    public void findIfInputDataIsValid() {
	    GeoCodeWWWFetcher geoCodeFetcher = new GeoCodeWWWFetcher();
	    geoCodeFetcher.setHandler( ((MainActivity) getActivity()).getHandler() );
	    geoCodeFetcher.execute(SetupDataModel.getInstance().getAddresses()[0],
	    		SetupDataModel.getInstance().getAddresses()[1]);
    }
    
    /* 
     * TODO: Give UI feedback
     */
    public void onReceivedAddressValidationResponse(Bundle bundle) {
    	Log.i(LOG_IDENTIFIER, "is Address A valid: " + bundle.getBoolean("addressASuccess"));
    	Log.i(LOG_IDENTIFIER, "is Address B valid: " + bundle.getBoolean("addressBSuccess"));
    }
}
