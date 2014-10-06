package com.gureen.commutetime;

import java.net.URLEncoder;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

/* AsyncTask which is launched on demand to request geo codes from Internet.
 * Thread exits as soon as work is done. This async task is used to verify user 
 * address input is valid. The thread posts either error back or the results back to
 * MainActivity, which are then forwarded to CommuteSetupActivity. 
 */
public class GeoCodeWWWFetcher extends AsyncTask<String, String, Pair<GMapsApiGeoCodingResponseJsonParser.GMapsGeoCodingResult, GMapsApiGeoCodingResponseJsonParser.GMapsGeoCodingResult>> {
	public static final String LOG_IDENTIFIER = "GeoCodeWWWFetcher";
	
	Handler mUIThreadHandler;   // Message handler for UI thread
	public void setHandler(Handler handler){
		mUIThreadHandler = handler;
	}
	
	/* Validates a given addresses */
	protected GMapsApiGeoCodingResponseJsonParser.GMapsGeoCodingResult validateAddress(String address) {
		String url = "";
	    String httpResponse = null;
	 
	    try {
	    	Uri.Builder builder = new Uri.Builder();
	        builder.scheme("https").authority("maps.googleapis.com")
	                               .appendEncodedPath("maps/api/geocode/json")
	                               .appendQueryParameter("address", URLEncoder.encode(address, "UTF-8"));
	        url = builder.build().toString();
	    	httpResponse = HttpRequestUtil.getHttpResponse(url);
	    	   
	   } catch(Exception e) {
	        e.printStackTrace();
	        return new GMapsApiGeoCodingResponseJsonParser.GMapsGeoCodingResult(false, "", false);
	   }
	    	
	   if(httpResponse != null) {
		   Log.i(LOG_IDENTIFIER, "GeoCode response : " + httpResponse);
		   return GMapsApiGeoCodingResponseJsonParser.parseGMapsGeoCodeApiResponse(httpResponse);
	   } 
	   
	   return new GMapsApiGeoCodingResponseJsonParser.GMapsGeoCodingResult(false, "", false);
	}
	
	/* This is executed when AsyncTask is started. Caller passes a string of
	 * addresses. When finished executing, commute times are calculated and
	 * and object with commute information is returned.*/
	protected Pair<GMapsApiGeoCodingResponseJsonParser.GMapsGeoCodingResult, GMapsApiGeoCodingResponseJsonParser.GMapsGeoCodingResult> doInBackground(String... addresses) {
    	String addressA = SetupDataModel.getInstance().getAddresses()[0];
    	String addressB = SetupDataModel.getInstance().getAddresses()[1];
    	GMapsApiGeoCodingResponseJsonParser.GMapsGeoCodingResult geoCodeA = validateAddress(addressA);
    	GMapsApiGeoCodingResponseJsonParser.GMapsGeoCodingResult geoCodeB = validateAddress(addressB);
    	return new Pair<GMapsApiGeoCodingResponseJsonParser.GMapsGeoCodingResult, GMapsApiGeoCodingResponseJsonParser.GMapsGeoCodingResult>(geoCodeA, geoCodeB);
    }
	 
	/* This is executed on main thread.  Message is posted to UI Thread to indicate
	 * data received from WWW.
	 */
    protected void onPostExecute(Pair<GMapsApiGeoCodingResponseJsonParser.GMapsGeoCodingResult, GMapsApiGeoCodingResponseJsonParser.GMapsGeoCodingResult> result) {
    	//mActivity.onReceivedAddressValidationResponse(result);
        Log.i("CommuteTimesWWWFetcher", "onPostExecute in CommuteTimesFetcher : " + result );
    	Message refreshGeoCodeMessage = Message.obtain();
    	refreshGeoCodeMessage.what = MainActivity.MessageID.ID_GEOCODE_DATA_RECEIVED_FROM_WWW;
    	Bundle bundle = new Bundle();
    	bundle.putString("addressAFormatted", result.first.mFormattedAddress);
    	bundle.putBoolean("addressAPartialMatch", result.first.mPartialMatch);
    	bundle.putBoolean("addressASuccess",result.first.mSuccess);
    	bundle.putString("addressBFormatted", result.second.mFormattedAddress);
    	bundle.putBoolean("addressBPartialMatch", result.second.mPartialMatch);
    	bundle.putBoolean("addressBSuccess",result.second.mSuccess);
    	refreshGeoCodeMessage.setData(bundle);
    	
    	refreshGeoCodeMessage.setTarget(mUIThreadHandler);
    	refreshGeoCodeMessage.sendToTarget();
    }
}
