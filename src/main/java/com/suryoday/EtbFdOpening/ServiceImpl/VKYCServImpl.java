package com.suryoday.EtbFdOpening.ServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.suryoday.EtbFdOpening.Others.GenerateProperty;
import com.suryoday.EtbFdOpening.Pojo.FdOpeningNTB;
import com.suryoday.EtbFdOpening.Repository.NtbFdRepo;
import com.suryoday.EtbFdOpening.Service.VKYCService;

@Component
public class VKYCServImpl implements VKYCService {
	private static Logger logger = LoggerFactory.getLogger(VKYCServImpl.class);
	@Autowired
	NtbFdRepo fdrepo;
	
	
	@Override
	public JSONObject getHyperVergeVkycDetails(String trackingId, JSONObject header) {
		JSONObject sendResponse = new JSONObject();
		URL obj = null;
		try {

			GenerateProperty x = GenerateProperty.getInstance();
			x.getappprop();
			String url = x.BASEURL + "vkyc/v2/summary";

			logger.debug(" getHyperVergeVkycDetails :: {}", url);

			obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("userid", trackingId);
			con.setRequestProperty("appId", "9ktstz");
			con.setRequestProperty("appkey", "91cd221syoezr2eg42t8");
			con.setRequestProperty("X-Request-ID", "WNT");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("api_key", x.api_key);

			sendResponse = getResponseNew(trackingId, sendResponse, con, "GET");
			logger.debug(" getHyperVergeVkycDetails :: " + trackingId + " trackingId " + sendResponse.toString());

		} catch (Exception e) {

			e.getMessage();
		}

		return sendResponse;
	}
	
	
	private static JSONObject getResponseNew(String parent, JSONObject sendAuthenticateResponse, HttpURLConnection con,
			String MethodType) throws IOException {

		con.setDoOutput(true);
		// OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());
		// os.write(parent.toString());
		// os.flush();
		// os.close();

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
//			String str = response.toString();
//			logger.debug("PRE Response RRR :: " + response.toString());
//			String newstr = str.substring(1, str.length() - 1);
//			newstr = newstr.replace("\\", "");
////			String newstr=str.replaceAll("[\"'/]", "");
//			logger.debug("POST Response RRR :: " + newstr);
//			JSONObject resp = new JSONObject(newstr);
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
	
	@Override
	public JSONObject createHyperVergeVkyc(JSONObject jsonObject, JSONObject header) {
		JSONObject sendResponse = new JSONObject();
		 
		
		if (jsonObject != null && jsonObject.has("aadhaar")) {
			String createdDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
	        JSONObject aadhaarObject = jsonObject.getJSONObject("aadhaar");
	        aadhaarObject.put("createdDate", createdDate);
	    }
		logger.debug("createHyperVergeVkyc UPDATE  createdDate :: {}", jsonObject.toString());
		URL obj = null;
		try {

			GenerateProperty x = GenerateProperty.getInstance();
			x.getappprop();

			String url = x.BASEURL + "vkyc/v2/user/vwrap";
			logger.debug("createHyperVergeVkyc :: {}", url);
			obj = new URL(url);

			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			String formattedDateTime = now.format(dateTimeFormatter);

			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("appId", "9ktstz");
			con.setRequestProperty("appKey", "91cd221syoezr2eg42t8");
			con.setRequestProperty("transactionId", "HV-suryodayHVtesting8");
			con.setRequestProperty("Cookie",
					"AWSALB=DtWkd+pW883xiKTFQKUZuRtCSSNqm5FvQDS9u6dtamQc1UBDS2P7et+AIai9U/tTh103TUQz7RkPOW2bRUFf/ngItMmgfDVt3J6du6DyQwSt8Esby4AODgKyeUJr;AWSALBCORS=DtWkd+pW883xiKTFQKUZuRtCSSNqm5FvQDS9u6dtamQc1UBDS2P7et+AIai9U/tTh103TUQz7RkPOW2bRUFf/ngItMmgfDVt3J6du6DyQwSt8Esby4AODgKyeUJr; AWSALB=IYd/HnvHBn5tCbZ2YxM0jrvmFV2Y76vUzhv89XTFPwzYJNYKoNEyl9TtZEjJbJL8cme/eXJS91XumLP9oiZoLu3ssj+a+SB1Bt9y551cl7fPcZ9MJFMBa/jgQFSD; AWSALBCORS=IYd/HnvHBn5tCbZ2YxM0jrvmFV2Y76vUzhv89XTFPwzYJNYKoNEyl9TtZEjJbJL8cme/eXJS91XumLP9oiZoLu3ssj+a+SB1Bt9y551cl7fPcZ9MJFMBa/jgQFSD");
			con.setRequestProperty("X-Correlation-ID", formattedDateTime);
			con.setRequestProperty("X-Request-ID", "WNT");
			con.setRequestProperty("api_key", x.api_key);

			sendResponse = getResponseData(jsonObject, sendResponse, con, "POST");
			logger.debug("createHyperVergeVkyc sendResponse :: {}", sendResponse.toString());
		} catch (Exception e) {
			logger.error("Error in createHyperVergeVkyc", e);
			e.getMessage();
		}

		return sendResponse;
	}
	
	
	

	@Override
	public JSONObject getVkycDetails(String trackingId, JSONObject header) {
		JSONObject sendResponse = new JSONObject();
		URL obj = null;
		try {

			GenerateProperty x = GenerateProperty.getInstance();
			x.getappprop();
			// GenerateProperty x = GenerateProperty.getInstance();
			x.bypassssl();
			// Create all-trusting host name verifier
//			HostnameVerifier allHostsValid = new HostnameVerifier() {
//				public boolean verify(String hostname, SSLSession session) {
//					return true;
//				}
//			};

//			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			logger.debug(x.BASEURL + "ims/videokyc/trackingId/" + trackingId);

			obj = new URL(x.BASEURL + "ims/videokyc/trackingId/" + trackingId);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("client-id", header.getString("client-id"));
			con.setRequestProperty("timestamp", header.getString("timestamp"));
			con.setRequestProperty("x-request-id", "1168b770-5c69-40a0-a563-973797274e8d");
			con.setRequestProperty("api_key", x.api_key);
			con.setRequestProperty("Accept-Encoding", "*/*");

			sendResponse = getResponse(trackingId, sendResponse, con, "GET");
			logger.debug(" getVkycDetails :: " + trackingId + " trackingId " + sendResponse.toString());

		} catch (Exception e) {

			e.getMessage();
		}

		return sendResponse;
	}

	private static JSONObject getResponse(String parent, JSONObject sendAuthenticateResponse, HttpURLConnection con,
			String MethodType) throws IOException {

		con.setDoOutput(true);
		// OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());
		// os.write(parent.toString());
		// os.flush();
		// os.close();

		int responseCode = con.getResponseCode();
		logger.debug(MethodType+" Response Code :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			JSONObject sendauthenticateResponse1 = new JSONObject();
			String str = response.toString();
			logger.debug("PRE Response RRR :: " + response.toString());
			String newstr = str.substring(1, str.length() - 1);
			newstr = newstr.replace("\\", "");
//			String newstr=str.replaceAll("[\"'/]", "");
			logger.debug("POST Response RRR :: " + newstr);
			JSONObject resp = new JSONObject(newstr);
			sendauthenticateResponse1.put("data", resp.toString());
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
	
	
	

	@Override
	public JSONObject createVkyc(JSONObject jsonObject, JSONObject header) {
		JSONObject sendResponse = new JSONObject();
		URL obj = null;
		try {

			GenerateProperty x = GenerateProperty.getInstance();
			x.getappprop();
			// GenerateProperty x = GenerateProperty.getInstance();
			x.bypassssl();
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			logger.debug(x.BASEURL + "ims/videokyc/customer-data");

			obj = new URL(x.BASEURL + "ims/videokyc/customer-data");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("PUT");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("client-id", header.getString("client-id"));
			con.setRequestProperty("timestamp", header.getString("timestamp"));
			con.setRequestProperty("x-request-id", "63cba3fe-69a6-4bb1-8c85-7a518d7b3904");
			con.setRequestProperty("api_key", x.api_key);
			con.setRequestProperty("apikey", "61fd979ec7734056a0c3bc8cfbc762dd");
			con.setRequestProperty("Signature", "+OIgl9FVr76rBZCHHt73g7klfnbpvDfIvwmtm2SGCbA=");
			con.setRequestProperty("Accept-Encoding", "*/*");

			sendResponse = getResponseData(jsonObject, sendResponse, con, "PUT");

		} catch (Exception e) {

			e.getMessage();
		}

		return sendResponse;
	}

	private static JSONObject getResponseData(JSONObject parent, JSONObject sendAuthenticateResponse,
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

			JSONObject sendauthenticateResponse1 = new JSONObject();
			sendauthenticateResponse1.put("data", response.toString());
			sendAuthenticateResponse = sendauthenticateResponse1;
		} else {
			logger.debug("POST request not worked");

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

	@Override
	public List<String> getAllTrackingIds(String isActive) {
		return fdrepo.getAllTrackingIds(isActive);

	}

	@Override
	public List<FdOpeningNTB> findByIsPartialVkycAndIsFdCreated(String isActive, String isPartialVkyc,
			String isFdCreated) {
		return fdrepo.findActivePartialVkycWhereFdCreatedOrNull(isActive, isPartialVkyc, isFdCreated);

	}

	@Override
	public FdOpeningNTB fetchByCifCustomerId(String customerId) {

		Optional<FdOpeningNTB> optional = fdrepo.fetchByCifCustomerId(customerId);
		if (optional.isPresent()) {
			return optional.get();
		}
		throw new NoSuchElementException("No record found");
	}

	@Override
	public List<FdOpeningNTB> getAllDmsUploadList(String isDmsUpload) {
		return fdrepo.findActiveDmsCifEkyc("Y", isDmsUpload, "Y", "Y");
	}

}
