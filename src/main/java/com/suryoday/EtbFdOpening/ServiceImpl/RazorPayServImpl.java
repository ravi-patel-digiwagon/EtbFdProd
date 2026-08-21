package com.suryoday.EtbFdOpening.ServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import com.suryoday.EtbFdOpening.Pojo.FdOpening;
import com.suryoday.EtbFdOpening.Service.FdOpeningService;
import com.suryoday.EtbFdOpening.Service.FdRecieptService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.suryoday.EtbFdOpening.Others.CheckSum;
import com.suryoday.EtbFdOpening.Others.GenerateProperty;
import com.suryoday.EtbFdOpening.Others.NoSuchElementException;
import com.suryoday.EtbFdOpening.Pojo.FdOpeningNTB;
import com.suryoday.EtbFdOpening.Repository.NtbFdRepo;
import com.suryoday.EtbFdOpening.Service.RazorPayService;

@Component
public class RazorPayServImpl implements RazorPayService {
	private static Logger logger = LoggerFactory.getLogger(RazorPayServImpl.class);
	@Autowired
	NtbFdRepo ntbFdRepo;

	@Autowired
	FdRecieptService fdRecieptService;

	@Autowired
	FdOpeningService fdservice;

	@Override
	public JSONObject sendPaymentLink(JSONObject jsonObject, JSONObject header) {
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
			// BASEURL =
			// https://intramashery.suryodaybank.com/ssfb/paymentLink?api_key=twkmgdbequkp827u8zdqe5bm
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			obj = new URL(x.BASEURL + "paymentLink?api_key=" + x.api_key);
			logger.debug(x.BASEURL + "paymentLink?api_key=" + x.api_key);
			;
//			 obj = new URL("https://brn.suryodaybank.co.in/BRConnectClientNew/v1/BrNetconnect");
//			logger.debug("https://brn.suryodaybank.co.in/BRConnectClientNew/v1/BrNetconnect");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("X-Request-ID", header.getString("X-Request-ID"));
			sendResponse = getResponse(jsonObject, sendResponse, con, "POST");
//				
//				getHeadersRequestInfo(con);

		} catch (Exception e) {

			e.getMessage();
		}

		return sendResponse;
	}

	private static JSONObject getResponse(JSONObject parent, JSONObject sendAuthenticateResponse, HttpURLConnection con,
			String MethodType) throws IOException {

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
	public JSONObject fetchPaymentLink(String orderId, JSONObject header) {
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
			obj = new URL(x.BASEURL + "fetch/paymentLink/" + orderId + "?api_key=" + x.api_key);
			logger.debug(x.BASEURL + "fetch/paymentLink/" + orderId + "?api_key=" + x.api_key);
			;
//			 obj = new URL("https://brn.suryodaybank.co.in/BRConnectClientNew/v1/BrNetconnect");
//			logger.debug("https://brn.suryodaybank.co.in/BRConnectClientNew/v1/BrNetconnect");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("X-Correlation-ID", header.getString("X-Correlation-ID"));
			con.setRequestProperty("X-User-ID", header.getString("X-User-ID"));
			con.setRequestProperty("X-Request-ID", header.getString("X-Request-ID"));
			sendResponse = getResponse2(orderId, sendResponse, con, "GET");
//				
//				getHeadersRequestInfo(con);

		} catch (Exception e) {

			e.getMessage();
		}

		return sendResponse;

	}

	private static JSONObject getResponse2(String parent, JSONObject sendAuthenticateResponse, HttpURLConnection con,
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

	@Override
	public JSONObject payuDetails(JSONObject jsonObject) {
		logger.debug("payu_merchant_details__req_json :: " + jsonObject.toString());
		JSONObject req = getPayuDetailsReq(jsonObject);
		logger.debug("payu_merchant_details_Req :: " + req.toString());
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

			obj = new URL(x.BASEURL + "payu/merchant/details");
			logger.debug(x.BASEURL + "payu/merchant/details");
//			 obj = new URL("https://brn.suryodaybank.co.in/BRConnectClientNew/v1/BrNetconnect");
//			logger.debug("https://brn.suryodaybank.co.in/BRConnectClientNew/v1/BrNetconnect");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("accept", "application/json");
			con.setRequestProperty("api_key", x.api_key);
			sendResponse = getResponse(req, sendResponse, con, "POST");
			logger.debug("payu_merchant_details_Response :: " + sendResponse.toString());
//				getHeadersRequestInfo(con);

		} catch (Exception e) {

			e.getMessage();
		}

		return sendResponse;
	}

	private JSONObject getPayuDetailsReq(JSONObject jsonObject) {
		String orderId = jsonObject.getJSONObject("Data").getString("OrderId");
		JSONObject parent = new JSONObject();
		JSONObject req = new JSONObject();
		req.put("Form", "2");
		req.put("Key", "bwYbd0");
		req.put("Command", "verify_payment");
		req.put("Var1", orderId);
		String payload = "bwYbd0|verify_payment|" + orderId + "|1rmv7S62JBSTQUHyQESTUg7oBWw4fvMe";
		String checksum = "";
		try {
			checksum = CheckSum.generateHash(payload);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		req.put("Hash", checksum);
		parent.put("Data", req);
		return parent;
	}

	@Override
	public JSONObject savePayuDetails(JSONObject jsonObject) {
		long appNo = Long.parseLong(jsonObject.getJSONObject("Data").getString("ApplicationNo"));
		String orderId = jsonObject.getJSONObject("Data").getString("OrderId");
		FdOpeningNTB fdOpeningNTB = ntbFdRepo.findById(appNo)
				.orElseThrow(() -> new NoSuchElementException("No record found"));
		JSONObject parent = new JSONObject();
		JSONObject data = new JSONObject();
		if (!"Y".equals(fdOpeningNTB.getIsPaymentDone())) {
			fdOpeningNTB.setPayuOrderId(orderId);
			ntbFdRepo.save(fdOpeningNTB);
			data.put("Message", "Success");
			data.put("Description", "Data saved successfully");
			logger.debug("savePayuDetails for Application No ::"+fdOpeningNTB.getApplicationNo() + "Data saved successfully");
		} else {
			data.put("Message", "Failure");
			data.put("Description", "Payment already done for this application.");
			logger.debug("savePayuDetails for Application No ::"+fdOpeningNTB.getApplicationNo() + "Payment already done for this application.");
		}

		parent.put("Data", data);
		return parent;
	}

	@Override
	public JSONObject savePayuDetailsEtb(JSONObject jsonObject,String mobileNo,String X_Session_ID) {
		long appNo = Long.parseLong(jsonObject.getJSONObject("Data").getString("ApplicationNo"));
		String orderId = jsonObject.getJSONObject("Data").getString("OrderId");

		FdOpening fdopening = fdRecieptService.fetchByMobNoAndSessionId(mobileNo, X_Session_ID);

		JSONObject parent = new JSONObject();
		JSONObject data = new JSONObject();
		if (!"Y".equals(fdopening.getIsPaymentDone())) {
			fdopening.setPayuOrderId(orderId);
			fdservice.save(fdopening);
			data.put("Message", "Success");
			data.put("Description", "Data saved successfully");
			logger.debug("savePayuDetailsEtb ::"+fdopening.getId() + "Data saved successfully");
		} else {
			data.put("Message", "Failure");
			data.put("Description", "Payment already done for this application.");
			logger.debug("savePayuDetailsEtb ::"+fdopening.getId() + "Payment already done for this application.");
		}

		parent.put("Data", data);
		return parent;
	}


}
