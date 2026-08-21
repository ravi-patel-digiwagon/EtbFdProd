package com.suryoday.EtbFdOpening.Others;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.security.SecureRandom;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ResponseData {
	private static Logger logger = LoggerFactory.getLogger(ResponseData.class);
	public static JSONObject postResponseData(JSONObject parent, JSONObject sendAuthenticateResponse,
			HttpURLConnection con, String MethodType) throws IOException {

		con.setDoOutput(true);
		OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());
		os.write(parent.toString());
		os.flush();
		os.close();

		int responseCode = con.getResponseCode();
		logger.debug("POST Response Code :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			JSONObject sendauthenticateResponse1 = new JSONObject(response.toString());
			sendAuthenticateResponse = sendauthenticateResponse1;
		}
		else if(responseCode==HttpURLConnection.HTTP_INTERNAL_ERROR)
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			JSONObject sendauthenticateResponse1 = new JSONObject(response.toString());
			sendAuthenticateResponse = sendauthenticateResponse1;
		}
		else {
			logger.debug("POST request not worked");

			

			JSONObject errr = new JSONObject();
			errr.put("Description", "Server Error " + responseCode);

			JSONObject j = new JSONObject();
			j.put("Error", errr);

			
			sendAuthenticateResponse = j;
		}

		return sendAuthenticateResponse;

	}
	
	public static JSONObject postResponseDataXml(String parent, JSONObject sendauthenticateResponse,
			HttpURLConnection con,String MethodType) throws IOException {

			
			//logger.debug(con.toString());

			if(MethodType.equals("POST") || MethodType.equals("PUT"))
			{
			// For POST only - START
			con.setDoOutput(true);
			OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());
			os.write(parent.toString());
			os.flush();
			os.close();
			
			
			// For POST only - END
			}

			int responseCode = con.getResponseCode();
			logger.debug("POST Response Code :: " + responseCode);

			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				//logger.debug("RequestJson type is  ::" + response);
			   JSONObject 	sendauthenticateResponse1  = new JSONObject();
			   
			   org.json.JSONObject soapDatainJsonObject = new org.json.JSONObject();
				try {
					soapDatainJsonObject = XML.toJSONObject(response.toString());
					//System.out.println(soapDatainJsonObject);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					logger.debug("Error msg "+e.getMessage());
//					e.printStackTrace();
				}
				
			   sendauthenticateResponse = soapDatainJsonObject;
			 //  logger.debug("response added  ::");
				// print result
				//logger.debug(response.toString());
			}
			else if (responseCode == HttpURLConnection.HTTP_CREATED) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				//logger.debug("RequestJson type is  ::" + response);
			   JSONObject 	sendauthenticateResponse1  = new JSONObject();
			   
			   org.json.JSONObject soapDatainJsonObject = new org.json.JSONObject();
				try {
					soapDatainJsonObject = XML.toJSONObject(response.toString());
					//System.out.println(soapDatainJsonObject);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					logger.debug("Error msg "+e.getMessage());
				}
				
				JSONObject errr = new JSONObject();
				 errr.put("Description",soapDatainJsonObject.toString());
				 
				 JSONObject j = new JSONObject();
				 j.put("Error",errr);
				 
				 
			   sendauthenticateResponse = j;
			 //  logger.debug("response added  ::");
				// print result
				//logger.debug(response.toString());
			}
			else {
				logger.debug("POST request not worked");
				
				JSONObject 	sendauthenticateResponse1  = new JSONObject();
				 
				 JSONObject errr = new JSONObject();
				 errr.put("Description","Server Error "+responseCode);
				 
				 JSONObject j = new JSONObject();
				 j.put("Error",errr);
				 
				
				sendauthenticateResponse = j;
				
				//   logger.debug("response added  :");
			}
			return sendauthenticateResponse;

	}
	
	public static JSONObject getResponseData(String parent, JSONObject sendAuthenticateResponse,
			HttpURLConnection con, String MethodType) throws IOException {

		con.setDoOutput(true);
//		OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());
//		os.write(parent.toString());
//		os.flush();
//		os.close();

		int responseCode = con.getResponseCode();
		logger.debug("Get Response Code :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			JSONObject sendauthenticateResponse1 = new JSONObject(response.toString());
			sendAuthenticateResponse = sendauthenticateResponse1;
		} else {
			logger.debug("Get request not worked");

			

			JSONObject errr = new JSONObject();
			errr.put("Description", "Server Error " + responseCode);

			JSONObject j = new JSONObject();
			j.put("Error", errr);

			
			sendAuthenticateResponse = j;
		}

		return sendAuthenticateResponse;

	}
	
	public static String getXMLResponseData(String parent, String response2, HttpURLConnection con, String MethodType)
			throws IOException {

		con.setDoOutput(true);
		DataOutputStream os = new DataOutputStream(con.getOutputStream());
		os.writeBytes(parent);
		os.flush();
		os.close();

		int responseCode = con.getResponseCode();
		logger.debug("POST Response Code :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append(response.toString());
			response2 = stringBuffer.toString();

		} else {
			logger.debug("POST request not worked");
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("<Description><Error>");
			stringBuffer.append(" Server Error  " + responseCode + "</Error></Description>");
			response2 = stringBuffer.toString();

		}

		return response2;

	}
	
	public static long generateRandom(int length) {
//      Random random = new Random();
		SecureRandom  random = new SecureRandom();
		char[] digits = new char[length];
		digits[0] = (char) (random.nextInt(9) + '1');
		for (int i = 1; i < length; i++) {
			digits[i] = (char) (random.nextInt(10) + '0');
		}
		return Long.parseLong(new String(digits));
	}
}
