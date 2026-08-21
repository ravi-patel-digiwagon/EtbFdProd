package com.suryoday.EtbFdOpening.ServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.suryoday.EtbFdOpening.Others.GenerateProperty;
import com.suryoday.EtbFdOpening.Service.PennyDropService;


@Component
public class PennyDropServImpl implements PennyDropService  {
	private static Logger logger = LoggerFactory.getLogger(PennyDropServImpl.class);
	@Override
	public JSONObject pennyDrop(String accountNo,String ifsc,JSONObject header) {
JSONObject sendResponse = new JSONObject();
		URL obj = null;
		try {
			
			
			GenerateProperty x = GenerateProperty.getInstance();
			x.getappprop();
			//GenerateProperty x = GenerateProperty.getInstance();
			 x.bypassssl();
			// Create all-trusting host name verifier
				HostnameVerifier allHostsValid = new HostnameVerifier() {
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				};
				
			
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid); 
			logger.debug(x.BASEURL+"account/verification/"+accountNo+"?api_key="+x.api_key+"&consent=Y&ifsc="+ifsc);
	
			 obj =new URL(x.BASEURL+"account/verification/"+accountNo+"?api_key="+x.api_key+"&consent=Y&ifsc="+ifsc);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("Content-Type", "application/json");
				con.setRequestProperty("X-Correlation-ID",header.getString("X-Correlation-ID"));
				con.setRequestProperty("X-Request-ID", "MB");// not used in FD
				con.setRequestProperty("X-User-ID",header.getString("X-User-ID"));
				con.setRequestProperty("X-From-ID",header.getString("X-From-ID"));
				con.setRequestProperty("X-To-ID",header.getString("X-To-ID"));
				con.setRequestProperty("X-Transaction-ID",header.getString("X-Transaction-ID"));
				
				sendResponse = getResponseData(accountNo,sendResponse, con,"GET");

			
		} catch (Exception e) {
			
			e.getMessage();
		}
		
		return sendResponse;
	}
	
	private static JSONObject getResponseData(String parent,JSONObject sendAuthenticateResponse,
			HttpURLConnection con,String MethodType) throws IOException {
		
		con.setDoOutput(true);
//		OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());
//		os.write(parent.toString());
//		os.flush();
//		os.close();
		
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
			
			
			JSONObject 	sendauthenticateResponse1  = new JSONObject();
			   sendauthenticateResponse1.put("data", response.toString());
			   sendAuthenticateResponse = sendauthenticateResponse1;
		}
		else
		{
					logger.debug("POST request not worked");
			
			JSONObject 	sendauthenticateResponse1  = new JSONObject();
			 
			 JSONObject errr = new JSONObject();
			 errr.put("Description","Server Error "+responseCode);
			 
			 JSONObject j = new JSONObject();
			 j.put("Error",errr);
			 
			sendauthenticateResponse1.put("data", ""+j);
			sendAuthenticateResponse = sendauthenticateResponse1;
		}
		
				return sendAuthenticateResponse;
		
	}

	@Override
	public JSONObject searchIfsc(String ifscCode, JSONObject header) {
		JSONObject sendResponse = new JSONObject();

		URL obj = null;
		try {

			GenerateProperty x = GenerateProperty.getInstance();
			x.getappprop();
			x.bypassssl();
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			 obj = new URL(x.BASEURL+"transaction/ifsc/code?IFSCCode="+ifscCode+"&api_key="+x.api_key);
				logger.debug(x.BASEURL+"transaction/ifsc/code?IFSCCode="+ifscCode+"&api_key="+x.api_key);;
//			 obj = new URL("https://brn.suryodaybank.co.in/BRConnectClientNew/v1/BrNetconnect");
//			logger.debug("https://brn.suryodaybank.co.in/BRConnectClientNew/v1/BrNetconnect");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("X-Request-ID", header.getString("X-Request-ID"));
			con.setRequestProperty("X-Transaction-ID", header.getString("X-Transaction-ID"));
			con.setRequestProperty("X-From-ID", header.getString("X-From-ID"));
			con.setRequestProperty("X-To-ID", header.getString("X-To-ID"));
			sendResponse = getResponse(ifscCode, sendResponse, con, "GET");
//				
//				getHeadersRequestInfo(con);

		} catch (Exception e) {

			e.printStackTrace();
		}

		return sendResponse;
	}
	
	private static JSONObject getResponse(String parent, JSONObject sendAuthenticateResponse, HttpURLConnection con,
			String MethodType) throws IOException {

		con.setDoOutput(true);
//		 OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());
//		 os.write(parent.toString());
//		 os.flush();
//		 os.close();

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

			JSONObject sendauthenticateResponse1 = new JSONObject();
			sendauthenticateResponse1.put("data", response.toString());
			sendAuthenticateResponse = sendauthenticateResponse1;
		} else {
			logger.debug("GET request not worked");

			JSONObject sendauthenticateResponse1 = new JSONObject();

			JSONObject errr = new JSONObject();
			errr.put("Description", "Server Error " + responseCode);

			JSONObject j = new JSONObject();
			j.put("Error", errr);

			sendauthenticateResponse1.put("data", "" + j);
			sendAuthenticateResponse = sendauthenticateResponse1;
		}

		return sendAuthenticateResponse;

	}

}
