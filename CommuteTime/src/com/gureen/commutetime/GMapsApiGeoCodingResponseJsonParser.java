package com.gureen.commutetime;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/*
 * Utility class to parse G! maps JSON response for geocoding request.
 * Parses JSON response from GMaps Geocoding API and provides utilities
 * to extract if a particular address is a valid physical location
 * 
 * Ex: (invalid physical address)
 * https://maps.googleapis.com/maps/api/geocode/json?address=1600+Arceed+Parkway,+Union+City,+CA
 * 
 * Ex: (valid physical address)
 * https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheater+Pkwy,+Mountain+View,+CA
 * 
 */
public class GMapsApiGeoCodingResponseJsonParser {

	/* JSON model - routes */
	@JsonIgnoreProperties(ignoreUnknown = true)
    public static class MapsGeoCodeResponseResult {
        @JsonProperty("formatted_address") public String formatted_address;
        @JsonProperty("partial_match")     public boolean partial_match;
    }

    /* JSON model - G-Maps response  */
	@JsonIgnoreProperties(ignoreUnknown = true)
    public static class MapsGeoCodeResponse {
        @JsonProperty("results") 			public List<MapsGeoCodeResponseResult> results;
        @JsonProperty("status")             public String status;
    }

    public static class GMapsGeoCodingResult {
    	public boolean mSuccess;                 /* True or false */
        public String mFormattedAddress;         /* Formatted address */
        public boolean mPartialMatch;            /* Is the address only a partial match */

        public GMapsGeoCodingResult() {
        	mSuccess = false;
        	mFormattedAddress = "";
        	mPartialMatch = true;
        }
        
        public GMapsGeoCodingResult(boolean success, String formatted_address, boolean partial_match) {
        	mSuccess = success;
        	mFormattedAddress = formatted_address;
        	mPartialMatch = partial_match;
        }
    }

    /*
     * Takes a JSON response from Gmaps server, parses it and returns a
     * GMapsResult with the commute information
     */
    public static GMapsGeoCodingResult parseGMapsGeoCodeApiResponse(String jsonResponse) {
    	GMapsGeoCodingResult geoCodingResult = new GMapsGeoCodingResult();
        
        try {
        	ObjectMapper mapper = new ObjectMapper();
        	MapsGeoCodeResponse model = mapper.readValue(jsonResponse, MapsGeoCodeResponse.class);
        	
        	// If Status does not say "OK", return FAIL
        	if(model.status.compareTo("OK") != 0 ) {
        		geoCodingResult.mSuccess = false;
        		return geoCodingResult;
        	}

        	// If no routes are present, return FAIL
        	if(model.results.size() == 0 ) {
        		geoCodingResult.mSuccess = false;
        		return geoCodingResult;
        	}

        	// (TODO) Always, take first result, we don't know
        	// what to do if there are multiple routes.
        	final int RESULT_INDEX = 0;
        	MapsGeoCodeResponseResult mapsGeoCodeJsonResult = model.results.get(RESULT_INDEX);
        	geoCodingResult.mFormattedAddress       		= mapsGeoCodeJsonResult.formatted_address;
        	geoCodingResult.mPartialMatch           		= mapsGeoCodeJsonResult.partial_match;
        	geoCodingResult.mSuccess						= true;
        } catch( Exception e) {
            // All exceptions are treated as failures
        	e.printStackTrace();
        	geoCodingResult.mSuccess = false;
        }

        return geoCodingResult;
    }
}


