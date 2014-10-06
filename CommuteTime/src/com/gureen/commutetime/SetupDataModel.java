package com.gureen.commutetime;

import android.content.SharedPreferences;

/* Data model for our application. This keeps track of
 * data which is accessed by both CommuteSetupActivity and
 * CommuteTimesDisplayActivity.
 */
public class SetupDataModel {
	    private static SetupDataModel 		mInstance;			// Singleton instance
	    private static SharedPreferences 	mSharedPreferences; // SharedPreferences used to persist settings

	    // Singleton
	    private SetupDataModel() {}
	    public static SetupDataModel getInstance() {
	        if(mInstance == null) {
	            mInstance = new SetupDataModel();
	        }
	        return mInstance;
	    }

	    public void setSharedPreferences(SharedPreferences preferences) {
	    	mSharedPreferences = preferences;
	    }
	    
	    /* Save addresses set by user to persistent storage
	     * using Android's sharedPreferences.
	     */
	    public void setAddresses(String A, String B) {
	    	SharedPreferences.Editor editor = mSharedPreferences.edit();
	    	editor.putString("addressA", A);
	    	editor.putString("addressB", B);
	    	editor.commit();
	    }
	    
	    /* Get addresses stored in sharedPreferences */
	    public String[] getAddresses() {
	    	if(mSharedPreferences == null) {
	    		return new String[]{null,null};
	    	}
	    	
	    	return new String[]{mSharedPreferences.getString("addressA", ""),
	    			mSharedPreferences.getString("addressB", "")};
	    }
}
