package com.gureen.commutetime;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/* Fragment which displays commute times */
public class CommuteTimesDisplayFragment extends Fragment implements MainActivity.receivedWWWCommuteDataListener {
	private View mView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_commutes_times, container, false);
		fetchCommuteTimes();
		return mView;
	}
	   
    /* Displays "loading" transient screen to the user and launches
     * CommuteTimesWWWFetcher thread to fetch commute times from WWW */
    protected void fetchCommuteTimes() {
    	// Update text to show that we are in the process of getting data
    	((TextView) mView.findViewById(R.id.textViewA2B)).setText("Loading...");
    	((TextView) mView.findViewById(R.id.textViewB2A)).setText("");
    	mView.findViewById(R.id.layoutBothCommutes).invalidate();
        
		SetupDataModel settings = SetupDataModel.getInstance();
		CommuteTimesWWWFetcher commuteTimesFetcher = new CommuteTimesWWWFetcher();
		commuteTimesFetcher.setHandler(((MainActivity) getActivity()).getHandler());
		commuteTimesFetcher.execute(settings.getAddresses()[0], settings.getAddresses()[1]);
    }
    
    /* CommuteTimesWWWFetcher sends MainActivity a message to know when it is done
     * getting data from WWW. In turn, MainActivity calls this when
     * the message is received.
     */
    public void onReceiveCommuteDataFromWWW(Bundle bundle) {
		String commuteTimeA2BMin = bundle.getString("commuteTimesA2B");
        String A2BSummary = bundle.getString("summaryA2B");
		String commuteTimeB2AMin = bundle.getString("commuteTimesB2A");
        String B2ASummary = bundle.getString("summaryB2A");

        // These are the addresses for which Google maps returned commute times
        // The addresses might be spell-correct and different from what we requested for
		String addressA = bundle.getString("addressA");
		String addressB = bundle.getString("addressB");

		((TextView) mView.findViewById(R.id.textViewA2B)).setText( makeHtml(addressA, addressB, commuteTimeA2BMin, A2BSummary) );
		((TextView) mView.findViewById(R.id.textViewB2A)).setText( makeHtml(addressB, addressA, commuteTimeB2AMin, B2ASummary) );
		
		mView.findViewById(R.id.layoutBothCommutes).invalidate();
    }
    
    /* Creates a pretty HTML sentence from given parameters */
    protected Spanned makeHtml(String fromAddress,
    						  String toAddress,
    						  String commuteTime,
    						  String summary) {
    	return Html.fromHtml("<b>" + commuteTime + "</b>"
    						  + "<br/> from:  " + fromAddress
    						  + "<br/> to:  " + toAddress
    						  + "<br/> Via : " + summary);
    }
}
