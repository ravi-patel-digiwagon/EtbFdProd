package com.suryoday.EtbFdOpening.ServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.suryoday.EtbFdOpening.Others.GenerateProperty;
import com.suryoday.EtbFdOpening.Pojo.FdOpening;
import com.suryoday.EtbFdOpening.Pojo.FdOpeningNTB;
import com.suryoday.EtbFdOpening.Repository.FdOpeningRepository;
import com.suryoday.EtbFdOpening.Repository.NtbFdRepo;
import com.suryoday.EtbFdOpening.Service.FdOpeningService;

@Component
public class FdOpeningServImpl implements FdOpeningService {
	private static Logger logger = LoggerFactory.getLogger(FdOpeningServImpl.class);
	@Autowired
	FdOpeningRepository fdopeningrepo;
	@Autowired
	NtbFdRepo ntbfdrepo;

	@Override
	public JSONObject createDeposit(JSONObject jsonObject, JSONObject header) {
		JSONObject sendResponse = new JSONObject();
//		JSONObject request = getRequest();
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
			logger.debug(x.BASEURL + "account/TDRD?api_key=" + x.api_key);

			obj = new URL(x.BASEURL + "account/TDRD?api_key=" + x.api_key);
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			String formattedDateTime = now.format(dateTimeFormatter);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("X-Request-ID", "WET");
			con.setRequestProperty("X-Correlation-ID", formattedDateTime);
//			con.setRequestProperty("X-User-ID", header.getString("X-User-ID"));
//			con.setRequestProperty("X-From-ID", header.getString("X-From-ID"));

			sendResponse = getResponseData(jsonObject, sendResponse, con, "POST");

		} catch (Exception e) {

			e.printStackTrace();
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
	public String saveFdData(String mobileNo, String x_Session_ID, JSONObject jsonObject) {
		Optional<FdOpening> optional = fdopeningrepo.fetchBymobNo(mobileNo, x_Session_ID);
		if (optional.isPresent()) {
			FdOpening fdOpening = optional.get();
			System.out.println(fdOpening);
			String depositAccountNo = jsonObject.getJSONObject("Data").getString("DepositAccountNo");
			String depositAmount = jsonObject.getJSONObject("Data").getString("DepositAmount");
			String tenure = jsonObject.getJSONObject("Data").getString("Tenure");
			String maturityAmout = jsonObject.getJSONObject("Data").getString("MaturityAmout");
			String interestEarned = jsonObject.getJSONObject("Data").getString("InterestEarned");
			String roi = jsonObject.getJSONObject("Data").getString("Roi");
			String fromAccount = jsonObject.getJSONObject("Data").getString("FromAccount");
			String maturityDate = jsonObject.getJSONObject("Data").getString("MaturityDate");
			fdOpening.setDepositAccountNo(depositAccountNo);
			fdOpening.setDepositAmount(depositAmount);
			fdOpening.setTenure(tenure);
			fdOpening.setMaturityAmout(maturityAmout);
			fdOpening.setInterestEarned(interestEarned);
			fdOpening.setRoi(roi);
			fdOpening.setFromAccount(fromAccount);
			fdOpening.setMaturityDate(maturityDate);
			fdOpening.setUpdatedDate(LocalDateTime.now());
			fdOpening.setStatus("Completed");
			fdopeningrepo.save(fdOpening);
			return "Data Saved Successfully";
		} else {
			throw new NoSuchElementException("No record found");
		}
	}

	@Override
	public JSONObject CloseFd(JSONObject jsonObject, JSONObject header) {
		JSONObject sendResponse = new JSONObject();
//		JSONObject request = getRequest();
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
			logger.debug(x.BASEURL + "account/TD/close?api_key=" + x.api_key);

			obj = new URL(x.BASEURL + "account/TD/close?api_key=" + x.api_key);
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			String formattedDateTime = now.format(dateTimeFormatter);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("X-Request-ID", header.getString("X-Request-ID"));
			con.setRequestProperty("X-Correlation-ID", formattedDateTime);
			con.setRequestProperty("X-User-ID", "14508");
			con.setRequestProperty("X-From-ID", "MB");
			con.setRequestProperty("X-To-ID", "WNT");
			con.setRequestProperty("X-Transaction-ID", "EabeDcEE-db3c-BddD-CbD7-4bAA992c75d4");
			con.setConnectTimeout(90000);

			sendResponse = getResponseData(jsonObject, sendResponse, con, "POST");

		} catch (Exception e) {

			e.printStackTrace();
		}

		return sendResponse;
	}

	@Override
	public void save(FdOpening fdopening) {
		fdopeningrepo.save(fdopening);

	}

	@Override
	public JSONObject createWorkItem(JSONObject jsonObject, JSONObject header) {
		JSONObject sendResponse = new JSONObject();
//		JSONObject request = getRequest();
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
			logger.debug(x.BASEURL + "esb/workitem/CAO/createWorkitem?api_key=" + x.api_key);

			obj = new URL(x.BASEURL + "esb/workitem/CAO/createWorkitem?api_key=" + x.api_key);
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			String formattedDateTime = now.format(dateTimeFormatter);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("X-Correlation-ID", formattedDateTime);
			con.setRequestProperty("Authorization",
					"Basic dGF2YzBtRHRmUmR5ZlA1OVlROG1uMmpSY3RUU0RrbWZreUwwYmVEazpBTkxjT1JrcVNnUTAyUzE0NjBCSnFHVG5teDdHS0w5T1FVeHN5WWhBWVBpd1BBZ3BwUEw3UlE0WThOYktoeFhwZGNVc0RLMmh4dm96bmI3Y2ZMNlhvNlNCS3kyb1A3VVRTaVA2dDRERWFZZkZvQm5lekhlZjdmMGlYM3Z0WGlBaQ==");

			sendResponse = getResponseData(jsonObject, sendResponse, con, "POST");

		} catch (Exception e) {

			e.printStackTrace();
		}

		return sendResponse;
	}

	@Override
	public JSONObject createOrder(JSONObject jsonObject, JSONObject header) {
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
			logger.debug(x.BASEURL + "generate/orders");

			obj = new URL(x.BASEURL + "generate/orders");

			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("api_key", x.api_key);
			con.setRequestProperty("Authorization", header.getString("Authorization"));

			sendResponse = getResponseData(jsonObject, sendResponse, con, "POST");

		} catch (Exception e) {

			e.printStackTrace();
		}

		return sendResponse;
	}

	@Override
	public JSONObject fetchOrder(JSONObject jsonObject, JSONObject header) {
		JSONObject sendResponse = new JSONObject();
		String orderId = jsonObject.getJSONObject("Data").getString("OrderId");
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
			logger.debug(x.BASEURL + "order/status/" + orderId);

			obj = new URL(x.BASEURL + "order/status/" + orderId);

			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
//			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("api_key", x.api_key);
			con.setRequestProperty("Authorization", header.getString("Authorization"));

			sendResponse = getResponseData2(orderId, sendResponse, con, "GET");

		} catch (Exception e) {

			e.printStackTrace();
		}

		return sendResponse;
	}

	private static JSONObject getResponseData2(String parent, JSONObject sendAuthenticateResponse,
			HttpURLConnection con, String MethodType) throws IOException {

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
	public FdOpeningNTB fetchByApplicationNo(long applicationNo) {
		Optional<FdOpeningNTB> optional = ntbfdrepo.fetchByApplicationNo(applicationNo);
		if (optional.isPresent()) {
			return optional.get();
		}
		throw new NoSuchElementException("No record found");
	}

	@Override
	public FdOpeningNTB fetchByTrackingId(String trackingId) {
		Optional<FdOpeningNTB> optional = ntbfdrepo.fetchByTrackingId(trackingId);
		if (optional.isPresent()) {
			return optional.get();
		}
		throw new NoSuchElementException("No record found");
	}

	@Override
	public String saveNtbFdData(JSONObject jsonObject) {
		String applicationNo = jsonObject.getJSONObject("Data").getString("ApplicationNo");
		Optional<FdOpeningNTB> fetchByApplicationNo = ntbfdrepo.fetchByApplicationNo(Long.parseLong(applicationNo));
		if (fetchByApplicationNo.isPresent()) {
			FdOpeningNTB fdOpening = fetchByApplicationNo.get();
			String depositAccountNo = jsonObject.getJSONObject("Data").getString("DepositAccountNo");
			String depositAmount = jsonObject.getJSONObject("Data").getString("DepositAmount");
			String tenure = jsonObject.getJSONObject("Data").getString("Tenure");
			String maturityAmout = jsonObject.getJSONObject("Data").getString("MaturityAmout");
			String interestEarned = jsonObject.getJSONObject("Data").getString("InterestEarned");
			String roi = jsonObject.getJSONObject("Data").getString("Roi");
			String fromAccount = jsonObject.getJSONObject("Data").getString("FromAccount");
			String maturityDate = jsonObject.getJSONObject("Data").getString("MaturityDate");
			fdOpening.setDepositAccountNo(depositAccountNo);
			fdOpening.setDepositAmount(depositAmount);
			fdOpening.setTenure(tenure);
			fdOpening.setMaturityAmout(maturityAmout);
			fdOpening.setInterestEarned(interestEarned);
			fdOpening.setRoi(roi);
			fdOpening.setFromAccount(fromAccount);
			fdOpening.setMaturityDate(maturityDate);
			fdOpening.setStatus("Completed");
			fdOpening.setUpdatedDate(LocalDateTime.now());
			ntbfdrepo.save(fdOpening);
			return "Data Saved Successfully";
		}
		throw new NoSuchElementException("No record found");
	}

	@Override
	public JSONObject FdMaturityChange(JSONObject jsonObject, JSONObject header) {
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
			logger.debug(x.BASEURL + "update/fd/maturity/instruction?api_key=zvhpsvsjgzghxz5gqb8ypp88");

			obj = new URL(x.BASEURL + "update/fd/maturity/instruction?api_key=zvhpsvsjgzghxz5gqb8ypp88");
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			String formattedDateTime = now.format(dateTimeFormatter);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("X-Request-ID", header.getString("X-Request-ID"));
			con.setRequestProperty("X-Correlation-ID", formattedDateTime);
			con.setRequestProperty("accept", "application/json");

			sendResponse = getResponseData(jsonObject, sendResponse, con, "POST");

		} catch (Exception e) {

			e.printStackTrace();
		}

		return sendResponse;
	}

	@Override
	public JSONObject createDepositNtb(JSONObject jsonObject, JSONObject header) {

		JSONObject sendResponse = new JSONObject();
//		JSONObject request = getRequest();
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
			logger.debug(x.BASEURL + "account/TDRD?api_key=" + x.api_key);

			obj = new URL(x.BASEURL + "account/TDRD?api_key=" + x.api_key);
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			String formattedDateTime = now.format(dateTimeFormatter);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("X-Request-ID", "WNT");
			con.setRequestProperty("X-Correlation-ID", formattedDateTime);
//						con.setRequestProperty("X-User-ID", header.getString("X-User-ID"));
//						con.setRequestProperty("X-From-ID", header.getString("X-From-ID"));

			sendResponse = getResponseData(jsonObject, sendResponse, con, "POST");

		} catch (Exception e) {

			e.printStackTrace();
		}

		return sendResponse;
	}

	

}
