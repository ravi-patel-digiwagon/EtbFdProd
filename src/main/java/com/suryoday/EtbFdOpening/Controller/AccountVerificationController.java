package com.suryoday.EtbFdOpening.Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.suryoday.EtbFdOpening.Pojo.FdOpening;
import com.suryoday.EtbFdOpening.Service.FdRecieptService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suryoday.EtbFdOpening.Others.GenerateProperty;
import com.suryoday.EtbFdOpening.Pojo.FdOpeningNTB;
import com.suryoday.EtbFdOpening.Service.FdOpeningService;
import com.suryoday.EtbFdOpening.Service.SendOtpService;

@RestController
@RequestMapping(value = "/fdOpening")
@CrossOrigin(origins = "*")
public class AccountVerificationController {

	private static Logger logger = LoggerFactory.getLogger(AccountVerificationController.class);

	@Autowired
	SendOtpService otpservice;

	@Autowired
	FdOpeningService fdservice;

	@Autowired
	FdRecieptService fdRecieptService;

	// Disable SSL verification (for testing only)
	private void disableSSLVerification() throws Exception {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		} };

		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
	}


	@PostMapping("/saveUserAccountAndIfscdCodeEtb")
	public ResponseEntity<?> saveAccountETB(@RequestBody String bm,@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID) {
		try {
			logger.debug("Received request to save account info with session ID: {}", X_Session_ID);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(bm);

			String accountNumber = root.path("Data").path("AccountNo").asText();
			String ifsc = root.path("Data").path("Ifsc").asText().toUpperCase();
			String cifNo = root.path("Data").path("cifNo").asText();
			String productCode = root.path("Data").path("productCode").asText();

			String mobileNo = root.path("Data").path("MobileNo").asText();

			logger.debug("Parsed request data - AccountNo: {}, IFSC: {},  MobileNo: {}, ProductCode: {}",
					accountNumber, ifsc,  mobileNo, productCode);

			boolean sessionIdValid = otpservice.validateSessionId(X_Session_ID, mobileNo);
			logger.debug("Session ID validation result for mobile {}: {}", mobileNo, sessionIdValid);

			if (sessionIdValid) {
				FdOpening fdopening = fdRecieptService.fetchByMobNoAndSessionId(mobileNo, X_Session_ID);
				if (fdopening == null) {
					logger.debug("FdOpening not found for session ID: {}", mobileNo);
					Map<String, String> errorMap = new HashMap<>();
					errorMap.put("error", "Application not found");
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMap);
				}
				fdopening.setCifNo(cifNo);
				fdopening.setIsAccountVerify("Y");
				fdopening.setIfsc(ifsc);
				fdopening.setAccountNo(accountNumber);
				fdopening.setProductCode(productCode);
				fdservice.save(fdopening);

				logger.debug("Account details saved for mobileNo: {}", mobileNo);
				JSONObject response = new JSONObject();
				JSONObject subresponse = new JSONObject();
				subresponse.put("message", "Account information saved successfully");
				response.put("Data", subresponse);
				return ResponseEntity.ok(response.toMap());
			} else {
				JSONObject data2 = new JSONObject();
				data2.put("value", "SessionId is expired or Invalid sessionId");
				JSONObject data3 = new JSONObject();
				data3.put("Error", data2);

				logger.warn("Invalid or expired Session ID for mobileNo: {}", mobileNo);
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(data3.toString());
			}
		} catch (Exception e) {
			logger.error("Exception occurred while saving account information", e);
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put("error", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
		}
	}

	@PostMapping("/saveUserAccountAndIfscdCode")
	public ResponseEntity<?> saveAccount(@RequestBody String bm,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID) {
		try {
			logger.debug("Received request to save account info with session ID: {}", X_Session_ID);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(bm);

			String accountNumber = root.path("Data").path("AccountNo").asText();
			String ifsc = root.path("Data").path("Ifsc").asText().toUpperCase();
			String applicationNo = root.path("Data").path("applicationNo").asText();
			String mobileNo = root.path("Data").path("MobileNo").asText();

			logger.debug("Parsed request data - AccountNo: {}, IFSC: {}, ApplicationNo: {}, MobileNo: {}",
					accountNumber, ifsc, applicationNo, mobileNo);

			boolean sessionIdValid = otpservice.validateSessionId(X_Session_ID, mobileNo);
			logger.debug("Session ID validation result for mobile {}: {}", mobileNo, sessionIdValid);

			if (sessionIdValid) {
				FdOpeningNTB fdOpening = fdservice.fetchByApplicationNo(Long.parseLong(applicationNo));
				if (fdOpening == null) {
				    logger.warn("No application found for ApplicationNo: {}", applicationNo);
				    Map<String, String> errorMap = new HashMap<>();
				    errorMap.put("error", "Application not found");
				    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMap);
				}

				fdOpening.setIsAccountVerify("Y");
				fdOpening.setAccountNo(accountNumber);
				fdOpening.setIfsc(ifsc);
				otpservice.save(fdOpening);

				logger.debug("Account details saved for ApplicationNo: {}", applicationNo);
				JSONObject response = new JSONObject();
				JSONObject subresponse = new JSONObject();
				subresponse.put("message", "Account information saved successfully");
				response.put("Data", subresponse);
				return ResponseEntity.ok(response.toMap());
			} else {
				JSONObject data2 = new JSONObject();
				data2.put("value", "SessionId is expired or Invalid sessionId");
				JSONObject data3 = new JSONObject();
				data3.put("Error", data2);

				logger.warn("Invalid or expired Session ID for mobileNo: {}", mobileNo);
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(data3.toString());
			}
		} catch (Exception e) {
		    logger.error("Exception occurred while saving account information", e);
		    Map<String, String> errorMap = new HashMap<>();
		    errorMap.put("error", e.getMessage());
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
		}
	}

	@PostMapping("/accountverify")
	public ResponseEntity<?> verifyAccount(@RequestBody String bm) {
		logger.debug("accountverify API called");

//		String apiKey = "twkmgdbequkp827u8zdqe5bm";
		
		GenerateProperty x = GenerateProperty.getInstance();
		x.getappprop();
		

		try {
			logger.debug("Disabling SSL verification (for development use only)");
			disableSSLVerification(); // ⚠️ dev use only

			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(bm);

			String accountNumber = root.path("Data").path("AccountNo").asText();
			String ifsc = root.path("Data").path("Ifsc").asText();

			logger.debug("Parsed input - AccountNo: {}, IFSC: {}", accountNumber, ifsc);

//			String urlString = "https://intramashery.suryodaybank.com/ssfb/account/verification/" + accountNumber
//					+ "?consent=Y&ifsc=" + ifsc + "&api_key=" + apiKey;
			
//			String urlString = x.BASEURL +"account/verification/" + accountNumber+ "?consent=Y&ifsc=" + ifsc + "&api_key=" + x.api_key;

			String urlString = x.BASEURL +"benenamelookup/" + accountNumber+ "?consent=Y&ifsc=" + ifsc + "&api_key=" + x.api_key;

			logger.debug("Calling external API: {}", urlString);

			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("X-Request-ID", "TAB");

			int responseCode = conn.getResponseCode();
			logger.debug("Received response code: {}", responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder response = new StringBuilder();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			logger.debug("Raw response: {}", response.toString());

			// Convert JSON response to Map
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> responseMap = objectMapper.readValue(response.toString(), Map.class);

			logger.debug("Response successfully parsed to Map");
			logger.debug("Response Map: {}", responseMap);

			return ResponseEntity.status(responseCode).body(responseMap);

		} catch (Exception e) {
		    logger.error("Exception during account verification", e);
		    Map<String, String> errorMap = new HashMap<>();
		    errorMap.put("error", e.getMessage());
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
		}
	}

}