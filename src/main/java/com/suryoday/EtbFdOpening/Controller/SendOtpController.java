package com.suryoday.EtbFdOpening.Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suryoday.EtbFdOpening.Others.EncrtionAngulurTest;
import com.suryoday.EtbFdOpening.Pojo.ErrorResponse;
import com.suryoday.EtbFdOpening.Pojo.FdOpening;
import com.suryoday.EtbFdOpening.Pojo.MerchantTCDetails;
import com.suryoday.EtbFdOpening.Service.CustomerDetailsService;
import com.suryoday.EtbFdOpening.Service.SendOtpService;

@Component
@RestController
@RequestMapping(value = "/fdOpening")
public class SendOtpController extends OncePerRequestFilter {
	Logger logger = LoggerFactory.getLogger(SendOtpController.class);
	@Autowired
	SendOtpService otpservice;
	@Autowired
	CustomerDetailsService customerservice;

	private final Map<String, long[]> otpMap = new ConcurrentHashMap<>();
	private static final long TIME_WINDOW = 60 * 60 * 1000; // 60 minutes in ms

	private int updateAndGetOtpCount(String mobileNo) {
		long now = System.currentTimeMillis();
		long[] data = otpMap.get(mobileNo);

		logger.debug("OTP check started for Mobile: {}", mobileNo);

		// First request OR 60 minutes passed → reset
		if (data == null || now - data[1] > TIME_WINDOW) {
			logger.debug("Reset counter for Mobile: {} (first request OR >60 mins passed)", mobileNo);
			data = new long[] { 1, now }; // count, timestamp (long)
		} else {
			data[0]++; // increment count
			logger.debug("Increment counter for Mobile: {} → New Count = {}", mobileNo, data[0]);
		}

		otpMap.put(mobileNo, data);

		// Print map on every call
		otpMap.forEach((key, value) -> logger.debug("MAP → Mobile: {} | Count: {} | Timestamp(ms): {}", key, value[0],
				value[1]));

		return (int) data[0];
	}

	@RequestMapping(value = "/sendOtp", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> sendOtpAndEmail(@RequestBody String bm,
//			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
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

		if (X_Request_ID.equals("NOVOPAY")) {
			JSONObject jsonObject = new JSONObject(bm);
			String mobileNo = jsonObject.getJSONObject("Data").getString("MobileNO");
			// RAVI START 30-11-2025
//			if (mobileNo.length() != 0) {
//				JSONObject responseObj = customerservice.validateCustomerMobileNumber(mobileNo, Header);
//				String matchFlag = new JSONObject(responseObj.optString("data", "{}")).optString("MatchFlag", "");
//				if (matchFlag != "") {
//					if ("100% Match".equals(matchFlag)) {
//						JSONObject resp = new JSONObject();
//						JSONObject respData = new JSONObject();
//						respData.put("StatusCode", "400");
//						respData.put("Description", "ETB Customer matched with provided mobile number.");
////						respData.put("MatchFlag", true);
//						resp.put("Data", respData);
//						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp.toMap());
//					}
//				}
//			}
			// RAVI END 30-11-2025
			String callId = jsonObject.getJSONObject("Data").getString("X_Call_ID");
			int callLimit = updateAndGetOtpCount(mobileNo);
			if (callLimit > 30) {
				long[] stored = otpMap.get(mobileNo);
				long firstAttemptTime = stored[1];
				long retryAllowedTime = firstAttemptTime + TIME_WINDOW;
				

				logger.debug("Max Limit Exceeded");

				org.json.JSONObject data2 = new org.json.JSONObject();
				data2.put("StatusCode", "429");
				data2.put("Description", "Max Limit Exceeded");
				data2.put("RetryAllowedTime", retryAllowedTime);
				org.json.JSONObject data3 = new org.json.JSONObject();
				data3.put("Error", data2);

				return new ResponseEntity<Object>(data3.toString(), HttpStatus.TOO_MANY_REQUESTS);
			}
//			Optional<OtpValidation> otpOptional = otpservice.fetchOtpData(mobileNo);
//			if (otpOptional.isPresent()) {
//				OtpValidation otpValidation = otpOptional.get();
//				if (otpValidation.getTransactionId().equalsIgnoreCase(X_Transaction_ID)
//						&& otpValidation.getCallId().equalsIgnoreCase(callId)) {
//					org.json.JSONObject data2 = new org.json.JSONObject();
//					data2.put("StatusCode", "400");
//					data2.put("Description", "Max Limit Exceeded");
//					org.json.JSONObject data3 = new org.json.JSONObject();
//					data3.put("Error", data2);

//					return new ResponseEntity<Object>(data3.toString(), HttpStatus.BAD_REQUEST);
//				} else {
//					otpValidation.setTransactionId(X_Transaction_ID);
//					otpalidation.setCallId(callId);
//					otpservice.saveValidateData(otpValidation);
//				}
//			} else {
//				OtpValidation otpValidation = new OtpValidation();
//				otpValidation.setMobileNo(mobileNo);
//				otpValidation.setTransactionId(X_Transaction_ID);
//				otpValidation.setCallId(callId);
//				otpservice.saveValidateData(otpValidation);

//			}

			JSONObject sendOtp = otpservice.sendOtpNew(mobileNo, Header);

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
				return new ResponseEntity<Object>(Data1.toString(), h);

			} else {
				logger.debug("timeout");
				return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
			}

		} else {
			logger.debug("Invalid Request");
			return new ResponseEntity<Object>("Invalid Request ", HttpStatus.BAD_REQUEST);

		}

	}

	@RequestMapping(value = "/validateOTP", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> validateOTP(@RequestBody String bm,
			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "Type", required = true) String type,
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

		JSONObject jsonObject = new JSONObject(bm);
		if (X_Request_ID.equals("NOVOPAY")) {
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
					org.json.simple.JSONObject data = new org.json.simple.JSONObject();
					org.json.simple.JSONObject response = new org.json.simple.JSONObject();
					response.put("Success", "Mobile No Validate Successfully");
					response.put("SessionId", sessionid);
					response.put("EncodeId", encodeKey);
					response.put("StatusCode", "200");
					response.put("ApplicationNo", applicationno);
					response.put("ApplicationStatus", applicationStatus);
					data.put("Data", response);
					return new ResponseEntity<Object>(data.toString(), h);

				} else if (Data1.has("Error")) {
					h = HttpStatus.BAD_REQUEST;

				}
				logger.debug("response : " + Data1);
				return new ResponseEntity<Object>(Data1.toString(), h);

			} else {
				logger.debug("timeout");
				return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
			}
		} else {
			logger.debug("Invalid Request");
			return new ResponseEntity<Object>("Invalid Request ", HttpStatus.BAD_REQUEST);

		}

	}

	@RequestMapping(value = "/saveNewJourney", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> saveNewJourney(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("saveNewJourney start");
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);
		JSONObject jsonObject = new JSONObject(bm);
		String applicationNo = otpservice.saveNewJourney(mobileNo);
		org.json.simple.JSONObject response = new org.json.simple.JSONObject();
		org.json.simple.JSONObject data = new org.json.simple.JSONObject();
		data.put("Success", "Data Saved Successfully");
		data.put("ApplicationNo", applicationNo);
		response.put("Data", data);
		return new ResponseEntity<Object>(response, HttpStatus.OK);

	}

	@RequestMapping(value = "/validateOtp", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> validateOtp(@RequestBody String bm,
			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
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

		JSONObject jsonObject = new JSONObject(bm);
		if (X_Request_ID.equals("NOVOPAY")) {
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
				logger.debug("response : " + Data1);
				return new ResponseEntity<Object>(Data1.toString(), h);

			} else {
				logger.debug("timeout");
				return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
			}
		} else {
			logger.debug("Invalid Request");
			return new ResponseEntity<Object>("Invalid Request ", HttpStatus.BAD_REQUEST);

		}

	}

	@RequestMapping(value = "/validateCallId", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> validateCallId(@RequestBody String bm,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest request)
			throws Exception {
		logger.debug("validateOTP start");
		JSONObject Header = new JSONObject();
		Header.put("X-User-ID", "30639");
		Header.put("X-Request-ID", X_Request_ID);
		JSONObject jsonObject = new JSONObject(bm);
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
				data3.put("Success", data2);

				return new ResponseEntity<Object>(data3.toString(), HttpStatus.OK);
			} else {
				logger.debug("Max Limit Exceeded");

				org.json.JSONObject data2 = new org.json.JSONObject();
				data2.put("StatusCode", "400");
				data2.put("Description", "Max Limit Exceeded");
				org.json.JSONObject data3 = new org.json.JSONObject();
				data3.put("Error", data2);

				return new ResponseEntity<Object>(data3.toString(), HttpStatus.BAD_REQUEST);
			}
		} else {
			org.json.JSONObject data2 = new org.json.JSONObject();
			data2.put("StatusCode", "200");
			data2.put("Description", "Success");
			org.json.JSONObject data3 = new org.json.JSONObject();
			data3.put("Success", data2);

			return new ResponseEntity<Object>(data3.toString(), HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/sendsms", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> sendsms(@RequestBody String bm) {
		logger.debug("sendSms start");
		JSONObject response = otpservice.sendsms(new JSONObject(bm));
		return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);

	}

	@RequestMapping(value = "/saveConsentDetails", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> saveConsentDetails(@RequestBody MerchantTCDetails merchantTCDetails)
			throws Exception {
		logger.debug("saveConsentDetails start");
		JSONObject response = otpservice.saveConsentDetails(merchantTCDetails);
		return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);

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

	@RequestMapping(value = "/sendSms", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> sendSms(@RequestBody String bm) throws Exception {
		logger.debug("sendSms start");
		logger.debug("sendSms request" + bm);
		JSONObject Header = new JSONObject();
//		 Header.put("X-Correlation-ID",X_CORRELATION_ID );

		JSONObject jsonObject = new JSONObject(bm);
		String mobileNo = jsonObject.getJSONObject("Data").getString("MobileNo");

		JSONObject sendOtp = otpservice.sendSms(mobileNo, Header);

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
			return new ResponseEntity<Object>(Data1.toString(), h);

		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}

	}

	private byte[] restResponseBytes(ErrorResponse eErrorResponse) throws IOException {
		String serialized = new ObjectMapper().writeValueAsString(eErrorResponse);
		return serialized.getBytes();
	}

	@PostMapping(value = "/emailOtp", produces = "application/json")
	public ResponseEntity<Object> emailOtp(@RequestBody String req) throws Exception {
		JSONObject jsonObject = new JSONObject(req);

		JSONObject response = otpservice.emailOtp(jsonObject);
		return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
	}

}
