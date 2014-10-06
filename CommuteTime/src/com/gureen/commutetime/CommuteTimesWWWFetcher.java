package com.gureen.commutetime;

import java.net.URLEncoder;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

/* AsyncTask which is launched on demand to request commute times from Internet.
 * Thread exits as soon as work is done. Posts either error back or the commute times back to
 * CommuteTimesDisplayActivity.
 * 
 * eg. http://maps.googleapis.com/maps/api/directions/json?origin=4554%20Arce%20St%20Union%20City%20CA&destination=1950%20University%20Ave%20Palo%20Alto%20CA&sensor=false&mode=%22DRIVING%22  	 
	        
 */
public class CommuteTimesWWWFetcher extends AsyncTask<String, String, Pair<GMapsApiDirectionsResponseJsonParser.GMapsResult, GMapsApiDirectionsResponseJsonParser.GMapsResult>> {
	public static final String LOG_IDENTIFIER = "CommuteTimesWWWFetcher";
	
	Handler mUIThreadHandler;   // Message handler for UI thread
	public void setHandler(Handler handler){
		mUIThreadHandler = handler;
	}
	
	/* Get commute times between two given addresses */
	protected GMapsApiDirectionsResponseJsonParser.GMapsResult getCommuteTimes(String startAddress, String endAddress) {
		String url = "";
	    String httpResponse = null;
	 
	    try {
	    	Uri.Builder builder = new Uri.Builder();
	        builder.scheme("https").authority("maps.googleapis.com")
	                               .appendEncodedPath("maps/api/directions/json")
	                               .appendQueryParameter("origin", URLEncoder.encode(startAddress, "UTF-8"))
	                               .appendQueryParameter("destination", URLEncoder.encode(endAddress, "UTF-8"))
	                               .appendQueryParameter("sensor", "false")
	                               .appendQueryParameter("mode", "DRIVING");
	        url = builder.build().toString();
	    	httpResponse = HttpRequestUtil.getHttpResponse(url);
	    	   
	   } catch(Exception e) {
	        e.printStackTrace();
	        return new GMapsApiDirectionsResponseJsonParser.GMapsResult(GMapsApiDirectionsResponseJsonParser.GMapsResult.GMapsAPIError.ERROR_API_SPEC, 
	        		                                                     startAddress, 
	        		                                                     endAddress, 
	        		                                                     "-1", 
	        		                                                     "error");
	   }
	    	
	   if(httpResponse != null) {
		   Log.i(LOG_IDENTIFIER, "Directions response : " + httpResponse);
		   GMapsApiDirectionsResponseJsonParser.GMapsResult maps_result = GMapsApiDirectionsResponseJsonParser.parseGMapsApiResponse(httpResponse);
		   Log.i(LOG_IDENTIFIER, "Directions response Distance      : " + maps_result.mDistance);
		   Log.i(LOG_IDENTIFIER, "Directions response Duration      : " + maps_result.mDuration);
		   Log.i(LOG_IDENTIFIER, "Directions response Start Address : " + maps_result.mStartAddress);
		   Log.i(LOG_IDENTIFIER, "Directions response End Address   : " + maps_result.mEndAddress);
		   Log.i(LOG_IDENTIFIER, "Directions response summary       : " + maps_result.mSummary);
		   return maps_result;
	   } 
	   
       return new GMapsApiDirectionsResponseJsonParser.GMapsResult(GMapsApiDirectionsResponseJsonParser.GMapsResult.GMapsAPIError.ERROR_FAIL, 
    		   							                           startAddress,
       		                                                       endAddress, 
       		                                                       "-1", 
       		                                                       "error");
	}
	
	/* This is executed when AsyncTask is started. Caller passes a string of
	 * addresses. When finished executing, commute times are calculated and
	 * and object with commute information is returned.*/
	protected Pair<GMapsApiDirectionsResponseJsonParser.GMapsResult, GMapsApiDirectionsResponseJsonParser.GMapsResult> doInBackground(String... addresses) {
    	String addressA = SetupDataModel.getInstance().getAddresses()[0];
    	String addressB = SetupDataModel.getInstance().getAddresses()[1];
    	GMapsApiDirectionsResponseJsonParser.GMapsResult commuteA2B = getCommuteTimes(addressA, addressB);
    	GMapsApiDirectionsResponseJsonParser.GMapsResult commuteB2A = getCommuteTimes(addressB, addressA);
    	return new Pair<GMapsApiDirectionsResponseJsonParser.GMapsResult, GMapsApiDirectionsResponseJsonParser.GMapsResult>(commuteA2B, commuteB2A);
    }
	
	/* This is executed on main thread.  Message is posted to UI Thread to indicate
	 * data received from WWW.
	 */
    protected void onPostExecute(final Pair<GMapsApiDirectionsResponseJsonParser.GMapsResult, GMapsApiDirectionsResponseJsonParser.GMapsResult> result) {
         Log.i("CommuteTimesWWWFetcher", "onPostExecute in CommuteTimesFetcher : " + result );
     	 Message refreshCommuteTimes = Message.obtain();
     	 refreshCommuteTimes.what = MainActivity.MessageID.ID_SUCCESSFUL_COMMUTE_DATA_RECEIVED_FROM_WWW;
     	 Bundle bundle = new Bundle();
     	 bundle.putString("commuteTimesA2B", result.first.mDuration);
     	 bundle.putString("commuteTimesB2A", result.second.mDuration);
     	 bundle.putString("summaryA2B", result.first.mSummary);
     	 bundle.putString("summaryB2A", result.second.mSummary);
     	 bundle.putString("addressA", result.first.mStartAddress);
     	 bundle.putString("addressB", result.first.mEndAddress);
     	 refreshCommuteTimes.setData(bundle);
     	
     	 refreshCommuteTimes.setTarget(mUIThreadHandler);
     	 refreshCommuteTimes.sendToTarget();
    }
}
