package com.gureen.commutetime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/* Utility class for requesting resources through HTTP 
 */
public class HttpRequestUtil {
	public static final String LOG_IDENTIFIER = "HttpRequestUtil";

	// convert InputStream to String
	private static String getStringFromInputStream(InputStream is) throws Exception {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw e;
				}
			}
		}
 
		return sb.toString();
	}
 
	// Sync HTTP request and return response as a string
	public static String getHttpResponse(String url) throws Exception {
		  HttpClient httpclient = new DefaultHttpClient();

		  // Prepare a request object
		  HttpGet httpget = new HttpGet(url); 

		  // Execute the request
		  HttpResponse response;
		  response = httpclient.execute(httpget);
		  HttpEntity entity = response.getEntity();

		  if (entity != null) {
		       InputStream instream = entity.getContent();
		       String result= getStringFromInputStream(instream);
		       instream.close();
		       return result;
		   }
		  
		  return response.getStatusLine().getReasonPhrase();
	}
}
