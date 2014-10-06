package com.gureen.commutetime;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/*
 * Utility class to parse G! maps Directions API JSON response.
 * Parses JSON response from GMaps API Directions request
 * and provides utilities to extract duration and route information.
 */
public class GMapsApiDirectionsResponseJsonParser {
	
    /* JSON model - text-value tuple */
	@JsonIgnoreProperties(ignoreUnknown = true)
    public static class MapsDirectionsResponseTextValueEntry {
        @JsonProperty("text") public String text;
        @JsonProperty("value") public Integer value;
    }

    /* JSON model - legs */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class MapsDirectionsResponseLegs {
    	@JsonProperty("distance") public MapsDirectionsResponseTextValueEntry distance;
    	@JsonProperty("duration") public MapsDirectionsResponseTextValueEntry duration;
    	@JsonProperty("end_address") public String end_address;
    	@JsonProperty("start_address") public String start_address;
    }
 
    /* JSON model - routes */
	@JsonIgnoreProperties(ignoreUnknown = true)
    public static class MapsDirectionsResponseRoutes {
        @JsonProperty("legs") public List<MapsDirectionsResponseLegs> legs;
        @JsonProperty("summary") public String summary;
    }

    /* JSON model - G-Maps response  */
	@JsonIgnoreProperties(ignoreUnknown = true)
    public static class MapsDirectionsResponse {
        @JsonProperty("routes") public List<MapsDirectionsResponseRoutes> routes;
        @JsonProperty("status") public String status;
    }

    public static class GMapsResult {
        public enum GMapsAPIError {
            ERROR_SUCCESS(0),
            ERROR_FAIL(1),
            ERROR_UNREACHABLE(2),
            ERROR_API_SPEC(3);
            
            private final int m_error_code;
            GMapsAPIError(int code) { m_error_code = code;}
            public int getCode() { return m_error_code;}
        };

        public GMapsAPIError mError;    /* error code */
        public String mDuration;        /* Duration for commute */
        public String mDistance;        /* Distance for commute */
        public String mSummary;         /* Summary for route */
        public String mStartAddress;    /* Start address */
        public String mEndAddress;      /* End address */

        public GMapsResult() {
        	mError         = GMapsAPIError.ERROR_FAIL;
        	mDuration      = "0";
            mDistance      = "0";
            mSummary       = "";
            mStartAddress  = "";
            mEndAddress    = "";
        }
        
        public GMapsResult(GMapsAPIError error, String start, String end, String duration, String summary) {
        	mError         = error;
            mDuration      = duration;
            mDistance      = "0";
            mSummary       = summary;
            mStartAddress  = start;
            mEndAddress    = end;
        }
    }

    /*
     * Takes a JSON response from Gmaps server, parses it and returns a
     * GMapsResult with the commute information
     */
    public static GMapsResult parseGMapsApiResponse(String jsonResponse) {
        GMapsResult mapsResult = new GMapsResult();
        
        try {
        	ObjectMapper mapper = new ObjectMapper();
        	MapsDirectionsResponse model = mapper.readValue(jsonResponse, MapsDirectionsResponse.class);
        	
        	// If Status does not say "OK", return FAIL
        	if(model.status.compareTo("OK") != 0 ) {
        		mapsResult.mError = GMapsResult.GMapsAPIError.ERROR_FAIL;
        		return mapsResult;
        	}

        	// If no routes are present, return FAIL
        	if(model.routes.size() == 0 ) {
        		mapsResult.mError = GMapsResult.GMapsAPIError.ERROR_FAIL;
        		return mapsResult;
        	}

        	// (TODO) Always, take first route, we don't know
        	// what to do if there are multiple routes.
        	final int ROUTE_INDEX = 0;
        	MapsDirectionsResponseRoutes route = model.routes.get(ROUTE_INDEX);
        	mapsResult.mSummary                = route.summary;

        	// (TODO) Not sure why legs is an array, always take 
        	// first leg
        	final int LEG_INDEX = 0;
        	MapsDirectionsResponseLegs leg  = route.legs.get(LEG_INDEX);
        	mapsResult.mStartAddress        = leg.start_address;
        	mapsResult.mEndAddress          = leg.end_address;
        	mapsResult.mDistance            = leg.distance.text;
        	mapsResult.mDuration            = leg.duration.text;
        	mapsResult.mError               = GMapsResult.GMapsAPIError.ERROR_SUCCESS;

        } catch( Exception e) {
            // All exceptions are treated as failures
        	e.printStackTrace();
            mapsResult.mError = GMapsResult.GMapsAPIError.ERROR_FAIL;
        }

        return mapsResult;
    }
}


