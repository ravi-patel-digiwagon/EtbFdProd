package com.suryoday.EtbFdOpening.Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suryoday.EtbFdOpening.Others.Crypt;
import com.suryoday.EtbFdOpening.Others.EncrtionAngulurTest;
import com.suryoday.EtbFdOpening.Pojo.ErrorResponse;
import com.suryoday.EtbFdOpening.Pojo.FdOpening;
import com.suryoday.EtbFdOpening.Pojo.OtpValidation;
import com.suryoday.EtbFdOpening.Service.SendOtpService;

@Component
@RestController
@RequestMapping(value = "/fdOpening")
public class SendOtpEncyController extends OncePerRequestFilter {
	Logger logger = LoggerFactory.getLogger(SendOtpEncyController.class);
	@Autowired
	SendOtpService otpservice;
	
	private final Map<String, long[]> otpMap = new ConcurrentHashMap<>();
	private static final long TIME_WINDOW = 60 * 60 * 1000; // 60 minutes in ms

	
	private int updateAndGetOtpCount(String mobileNo) {
	    
				
		long now = System.currentTimeMillis();
	    long[] data = otpMap.get(mobileNo);

	    logger.debug("OTP check started for Mobile: {}", mobileNo);

	    // First request OR 60 minutes passed → reset
	    if (data == null || now - data[1] > TIME_WINDOW) {
	        logger.debug("Reset counter for Mobile: {} (first request OR >60 mins passed)", mobileNo);
	        data = new long[] {1, now}; // count, timestamp (long)
	    } else {
	        data[0]++; // increment count
	        logger.debug("Increment counter for Mobile: {} → New Count = {}", mobileNo, data[0]);
	    }

	    otpMap.put(mobileNo, data);

	    // Print map on every call
	    otpMap.forEach((key, value) ->
	        logger.debug("MAP → Mobile: {} | Count: {} | Timestamp(ms): {}", key, value[0], value[1])
	    );

	    return (int) data[0];
	}

	
	
	@RequestMapping(value = "/sendOtpEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> sendOtpAndEmail(@RequestBody String bm,
//			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("sendOtp start");
		logger.debug("sendOtp request" + bm);
		JSONObject Header = new JSONObject();
//		 Header.put("X-Correlation-ID",X_CORRELATION_ID );
		Header.put("X-From-ID", X_From_ID);
		Header.put("X-To-ID", X_To_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);
		Header.put("X-User-ID", "30639");
		Header.put("X-Request-ID", X_Request_ID);

		org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
		String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

		// logger.debug("start request" + bm.toString());

		String key = X_Session_ID;
		String encodeId = "c3VyeW9EYXlARGVwb3NpdA==";
		String decryptContainerString = Crypt.decrypt(encryptString, encodeId);

		String data = "";
		if (X_Request_ID.equals("NOVOPAY")) {
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			String mobileNo = jsonObject.getJSONObject("Data").getString("MobileNO");
			String callId = jsonObject.getJSONObject("Data").getString("X_Call_ID");
			 int callLimit = updateAndGetOtpCount(mobileNo);
			if (callLimit > 3) {
//				if (Integer.parseInt(callId) > 3) {
				long[] stored = otpMap.get(mobileNo);
				long firstAttemptTime = stored[1];
				long retryAllowedTime = firstAttemptTime + TIME_WINDOW;
				logger.debug("Max Limit Exceeded");

				org.json.JSONObject error = new org.json.JSONObject();
				error.put("StatusCode", "429");
				error.put("Description", "Max Limit Exceeded");
				error.put("RetryAllowedTime", retryAllowedTime);
				org.json.JSONObject errorResp = new org.json.JSONObject();
				errorResp.put("Error", error);

				data = errorResp.toString();
				String encryptString2 = Crypt.encrypt(data, encodeId);
				org.json.JSONObject data2 = new org.json.JSONObject();
				data2.put("value", encryptString2);
				org.json.JSONObject data3 = new org.json.JSONObject();
				data3.put("Data", data2);
				logger.debug("response : " + data3.toString());
				return new ResponseEntity<Object>(data3.toString(), HttpStatus.TOO_MANY_REQUESTS);
			}
			Optional<OtpValidation> otpOptional=otpservice.fetchOtpData(mobileNo);
			if(otpOptional.isPresent())
			{
				OtpValidation otpValidation = otpOptional.get();
				if(otpValidation.getTransactionId().equalsIgnoreCase(X_Transaction_ID)&&otpValidation.getCallId().equalsIgnoreCase(callId))
				{
					org.json.JSONObject error = new org.json.JSONObject();
					error.put("StatusCode", "400");
					error.put("Description", "Max Limit Exceeded");
					org.json.JSONObject errorResp = new org.json.JSONObject();
					errorResp.put("Error", error);

					data = errorResp.toString();
					String encryptString2 = Crypt.encrypt(data, encodeId);
					org.json.JSONObject data2 = new org.json.JSONObject();
					data2.put("value", encryptString2);
					org.json.JSONObject data3 = new org.json.JSONObject();
					data3.put("Data", data2);
					logger.debug("response : " + data3.toString());
					return new ResponseEntity<Object>(data3.toString(), HttpStatus.BAD_REQUEST);

				}
				else
				{
					otpValidation.setTransactionId(X_Transaction_ID);
					otpValidation.setCallId(callId);
					otpservice.saveValidateData(otpValidation);
				}
			}
			else
			{
				OtpValidation otpValidation=new OtpValidation();
				otpValidation.setMobileNo(mobileNo);
				otpValidation.setTransactionId(X_Transaction_ID);
				otpValidation.setCallId(callId);
				otpservice.saveValidateData(otpValidation);
				
			}
			
				JSONObject sendOtp = otpservice.sendOtp(mobileNo, Header);

				HttpStatus h = HttpStatus.BAD_GATEWAY;
				if (sendOtp != null) {
					String Data2 = sendOtp.getString("data");
					logger.debug("data2");
					JSONObject Data1 = new JSONObject(Data2);

					logger.debug(Data1.toString());

					if (Data1.has("Data")) {
						h = HttpStatus.OK;
						JSONObject resp = Data1.getJSONObject("Data");
						resp.put("StatusCode", "200");

					} else if (Data1.has("Error")) {
						h = HttpStatus.BAD_REQUEST;

					}
					data = Data1.toString();
					String encryptString2 = Crypt.encrypt(data, encodeId);
					org.json.JSONObject data2 = new org.json.JSONObject();
					data2.put("value", encryptString2);
					org.json.JSONObject data3 = new org.json.JSONObject();
					data3.put("Data", data2);
					logger.debug("response : " + data3.toString());
					return new ResponseEntity<Object>(data3.toString(), h);

				} else {
					logger.debug("timeout");
					return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
				}
			
		} else {
			logger.debug("Invalid Request");
			return new ResponseEntity<Object>("Invalid Request ", HttpStatus.BAD_REQUEST);

		}

	}
	
	

	

	@RequestMapping(value = "/validateOTPEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> validateOTP(@RequestBody String bm,
			@RequestHeader(name = "Type", required = true) String type,
			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest request)
			throws Exception {
		logger.debug("validateOTP start");
		JSONObject Header = new JSONObject();
		Header.put("X-Correlation-ID", X_CORRELATION_ID);
		Header.put("X-From-ID", X_From_ID);
		Header.put("X-To-ID", X_To_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);
		Header.put("X-User-ID", "30639");
		Header.put("X-Request-ID", X_Request_ID);
		org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
		String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

		// logger.debug("start request" + bm.toString());

		String key = X_Session_ID;
		String encodeId = "c3VyeW9EYXlARGVwb3NpdA==";
		String decryptContainerString = Crypt.decrypt(encryptString, encodeId);

		String data = "";

		if (X_Request_ID.equals("NOVOPAY")) {
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			String mobileNo = jsonObject.getJSONObject("Data").getString("MobileNo");
			String otp = jsonObject.getJSONObject("Data").getString("Otp");
			JSONObject sendsms = otpservice.validateOTP(otp, Header);
			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (sendsms != null) {
				String Data2 = sendsms.getString("data");
				logger.debug("data2");
				JSONObject Data1 = new JSONObject(Data2);

				logger.debug(Data1.toString());

				if (Data1.has("Data")) {
					h = HttpStatus.OK;
					String sessionid = otpservice.getRequestSession(request);
					otpservice.deleteAllSessions(mobileNo, sessionid);
					FdOpening fdopening = new FdOpening();
					fdopening.setMobileNo(mobileNo);
					fdopening.setSessionId(sessionid);
					LocalDateTime now = LocalDateTime.now();
					fdopening.setCreatedDate(now);
					fdopening.setStatus("Progress");
					fdopening.setCustType(type);
					String save = otpservice.saveData(fdopening);
					String applicationno = "";
					String applicationStatus = "";
					if (type.equalsIgnoreCase("NTB")) {
						JSONObject saveNtbFd = otpservice.saveNtbFd(mobileNo, type, "Progress");
						applicationno = saveNtbFd.getString("ApplicationNo");
						applicationStatus = saveNtbFd.getString("ApplicationStatus");
					}
//								String sessionId=otpservice.getSessionId(mobileNo);
					String substring = sessionid.substring(0, Math.min(sessionid.length(), 16));
					String encodeKey = EncrtionAngulurTest.encodeKey(substring);
					org.json.simple.JSONObject resp = new org.json.simple.JSONObject();
					org.json.simple.JSONObject response = new org.json.simple.JSONObject();
					response.put("Success", "Mobile No Validate Successfully");
					response.put("SessionId", sessionid);
					response.put("EncodeId", encodeKey);
					response.put("StatusCode", "200");
					response.put("ApplicationNo", applicationno);
					response.put("ApplicationStatus", applicationStatus);
					resp.put("Data", response);
					data = resp.toString();
					String encryptString2 = Crypt.encrypt(data, encodeId);
					org.json.JSONObject data2 = new org.json.JSONObject();
					data2.put("value", encryptString2);
					org.json.JSONObject data3 = new org.json.JSONObject();
					data3.put("Data", data2);
					logger.debug("response : " + data3.toString());
					return new ResponseEntity<Object>(data3.toString(), h);

				} else if (Data1.has("Error")) {
					h = HttpStatus.BAD_REQUEST;

				}
				data = Data1.toString();
				String encryptString2 = Crypt.encrypt(data, encodeId);
				org.json.JSONObject data2 = new org.json.JSONObject();
				data2.put("value", encryptString2);
				org.json.JSONObject data3 = new org.json.JSONObject();
				data3.put("Data", data2);
				logger.debug("response : " + data3.toString());
				return new ResponseEntity<Object>(data3.toString(), h);

			} else {
				logger.debug("timeout");
				return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
			}
		} else {
			logger.debug("Invalid Request");
			return new ResponseEntity<Object>("Invalid Request ", HttpStatus.BAD_REQUEST);

		}

	}

	@RequestMapping(value = "/saveNewJourneyEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> saveNewJourney(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("saveNewJourney start");
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);
		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			String applicationNo = otpservice.saveNewJourney(mobileNo);
			org.json.simple.JSONObject response = new org.json.simple.JSONObject();
			org.json.simple.JSONObject Data = new org.json.simple.JSONObject();
			Data.put("Success", "Data Saved Successfully");
			Data.put("ApplicationNo", applicationNo);
			response.put("Data", Data);
			data = response.toString();
			String encryptString2 = Crypt.encrypt(data, X_encode_ID);
			org.json.JSONObject data2 = new org.json.JSONObject();
			data2.put("value", encryptString2);
			org.json.JSONObject data3 = new org.json.JSONObject();
			data3.put("Data", data2);
			logger.debug("response : " + data3.toString());
			return new ResponseEntity<Object>(data3.toString(), HttpStatus.OK);
		} else {
			org.json.JSONObject data2 = new org.json.JSONObject();
			data2.put("value", "SessionId is expired or Invalid sessionId");
			org.json.JSONObject data3 = new org.json.JSONObject();
			data3.put("Error", data2);
			logger.debug("SessionId is expired or Invalid sessionId");
			return new ResponseEntity<Object>(data3.toString(), HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/validateOtpEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> validateOtpEncy(@RequestBody String bm,
			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest request)
			throws Exception {
		logger.debug("validateOTP start");
		JSONObject Header = new JSONObject();
		Header.put("X-Correlation-ID", X_CORRELATION_ID);
		Header.put("X-From-ID", X_From_ID);
		Header.put("X-To-ID", X_To_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);
		Header.put("X-User-ID", "30639");
		Header.put("X-Request-ID", X_Request_ID);
		org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
		String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

		// logger.debug("start request" + bm.toString());

		String key = X_Session_ID;
		String encodeId = "c3VyeW9EYXlARGVwb3NpdA==";
		String decryptContainerString = Crypt.decrypt(encryptString, encodeId);

		String data = "";

		if (X_Request_ID.equals("NOVOPAY")) {
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			String mobileNo = jsonObject.getJSONObject("Data").getString("MobileNo");
			String otp = jsonObject.getJSONObject("Data").getString("Otp");
			JSONObject sendsms = otpservice.validateOTP(otp, Header);
			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (sendsms != null) {
				String Data2 = sendsms.getString("data");
				logger.debug("data2");
				JSONObject Data1 = new JSONObject(Data2);

				logger.debug(Data1.toString());

				if (Data1.has("Data")) {
					h = HttpStatus.OK;
					JSONObject resp = Data1.getJSONObject("Data");
					resp.put("StatusCode", "200");

				} else if (Data1.has("Error")) {
					h = HttpStatus.BAD_REQUEST;

				}
				data = Data1.toString();
				String encryptString2 = Crypt.encrypt(data, encodeId);
				org.json.JSONObject data2 = new org.json.JSONObject();
				data2.put("value", encryptString2);
				org.json.JSONObject data3 = new org.json.JSONObject();
				data3.put("Data", data2);
				logger.debug("response : " + data3.toString());
				return new ResponseEntity<Object>(data3.toString(), h);

			} else {
				logger.debug("timeout");
				return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
			}
		} else {
			logger.debug("Invalid Request");
			return new ResponseEntity<Object>("Invalid Request ", HttpStatus.BAD_REQUEST);

		}

	}

	@RequestMapping(value = "/validateCallIdEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> validateCallId(@RequestBody String bm,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest request)
			throws Exception {
		logger.debug("validateOTP start");
		JSONObject Header = new JSONObject();
		Header.put("X-User-ID", "30639");
		Header.put("X-Request-ID", X_Request_ID);

		org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
		String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

		// logger.debug("start request" + bm.toString());

		String key = X_Session_ID;
		String encodeId = "c3VyeW9EYXlARGVwb3NpdA==";

		String decryptContainerString = Crypt.decrypt(encryptString, encodeId);

		String data = "";
		JSONObject jsonObject = new JSONObject(decryptContainerString);
		String callId = jsonObject.getJSONObject("Data").getString("X_Call_ID");
		if (Integer.parseInt(callId) > 3) {
			String dateTime = jsonObject.getJSONObject("Data").getString("DateTime");
			LocalDateTime dtTime = LocalDateTime.parse(dateTime);
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime trimNow = now.truncatedTo(ChronoUnit.MINUTES);
			LocalDateTime plusMinutes = dtTime.plusMinutes(15);
			if (plusMinutes.equals(trimNow)) {
				org.json.JSONObject data2 = new org.json.JSONObject();
				data2.put("StatusCode", "200");
				data2.put("Description", "Success");
				org.json.JSONObject data3 = new org.json.JSONObject();
				data3.put("Data", data2);
				data = data3.toString();
				String encryptString2 = Crypt.encrypt(data, encodeId);
				org.json.JSONObject data4 = new org.json.JSONObject();
				data4.put("value", encryptString2);
				org.json.JSONObject data5 = new org.json.JSONObject();
				data5.put("Data", data4);
				logger.debug("response : " + data5.toString());
				return new ResponseEntity<Object>(data5.toString(), HttpStatus.OK);
			} else {
				logger.debug("Max Limit Exceeded");

				org.json.JSONObject data2 = new org.json.JSONObject();
				data2.put("StatusCode", "400");
				data2.put("Description", "Max Limit Exceeded");
				org.json.JSONObject data3 = new org.json.JSONObject();
				data3.put("Error", data2);
				data = data3.toString();
				String encryptString2 = Crypt.encrypt(data, encodeId);
				org.json.JSONObject data4 = new org.json.JSONObject();
				data4.put("value", encryptString2);
				org.json.JSONObject data5 = new org.json.JSONObject();
				data5.put("Data", data4);
				logger.debug("response : " + data5.toString());
				return new ResponseEntity<Object>(data5.toString(), HttpStatus.BAD_REQUEST);
			}
		} else {
			org.json.JSONObject data2 = new org.json.JSONObject();
			data2.put("StatusCode", "200");
			data2.put("Description", "Success");
			org.json.JSONObject data3 = new org.json.JSONObject();
			data3.put("Data", data2);
			data = data3.toString();
			String encryptString2 = Crypt.encrypt(data, encodeId);
			org.json.JSONObject data4 = new org.json.JSONObject();
			data4.put("value", encryptString2);
			org.json.JSONObject data5 = new org.json.JSONObject();
			data5.put("Data", data4);
			logger.debug("response : " + data5.toString());
			return new ResponseEntity<Object>(data5.toString(), HttpStatus.OK);
		}

	}
	
	@RequestMapping(value = "/sendSmsEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> sendSms(@RequestBody String bm,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID) throws Exception {
		logger.debug("sendSms start");
		logger.debug("sendSms request" + bm);
		JSONObject Header = new JSONObject();
//		 Header.put("X-Correlation-ID",X_CORRELATION_ID );

		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			String mobileNo2 = jsonObject.getJSONObject("Data").getString("MobileNo");

		JSONObject sendOtp = otpservice.sendSms(mobileNo2, Header);

		HttpStatus h = HttpStatus.BAD_GATEWAY;
		if (sendOtp != null) {
			String Data2 = sendOtp.getString("data");
			logger.debug("data2");

			JSONObject Data1 = new JSONObject(Data2);

			logger.debug(Data1.toString());

			if (Data1.has("Data")) {
				h = HttpStatus.OK;
				JSONObject resp = Data1.getJSONObject("Data");
				resp.put("StatusCode", "200");

			} else if (Data1.has("Error")) {
				h = HttpStatus.BAD_REQUEST;

			}
			logger.debug("response" + Data1);
			data = Data1.toString();
			String encryptString2 = Crypt.encrypt(data, X_encode_ID);
			org.json.JSONObject data2 = new org.json.JSONObject();
			data2.put("value", encryptString2);
			org.json.JSONObject data3 = new org.json.JSONObject();
			data3.put("Data", data2);
			logger.debug("response : " + data3.toString());
			return new ResponseEntity<Object>(data3.toString(), h);

		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
		} else {
			org.json.JSONObject data2 = new org.json.JSONObject();
			data2.put("value", "SessionId is expired or Invalid sessionId");
			org.json.JSONObject data3 = new org.json.JSONObject();
			data3.put("Error", data2);
			logger.debug("SessionId is expired or Invalid sessionId");
			return new ResponseEntity<Object>(data3.toString(), HttpStatus.UNAUTHORIZED);
		}
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (request.getMethod().equals("OPTIONS")) {
			// response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			// throw new NoSuchElementException("You are Not authorized");
			org.json.JSONObject data2 = new org.json.JSONObject();
			data2.put("value", "unatharised Access");
			org.json.JSONObject data3 = new org.json.JSONObject();
			data3.put("Error", data2);

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setCode(401);
			errorResponse.setMessage("Unauthorized Access");

			byte[] responseToSend = restResponseBytes(errorResponse);
			((HttpServletResponse) response).setHeader("Content-Type", "application/json");
			((HttpServletResponse) response).setStatus(401);
			response.getOutputStream().write(responseToSend);
			return;
		} else {
			filterChain.doFilter(request, response);
		}
	}
	
	

	private byte[] restResponseBytes(ErrorResponse eErrorResponse) throws IOException {
		String serialized = new ObjectMapper().writeValueAsString(eErrorResponse);
		return serialized.getBytes();
	}

}
