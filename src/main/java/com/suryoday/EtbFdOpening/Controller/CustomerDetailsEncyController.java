package com.suryoday.EtbFdOpening.Controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
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
import com.suryoday.EtbFdOpening.Others.Crypt;
import com.suryoday.EtbFdOpening.Pojo.ErrorResponse;
import com.suryoday.EtbFdOpening.Pojo.FdOpeningNTB;
import com.suryoday.EtbFdOpening.Pojo.InterestRates;
import com.suryoday.EtbFdOpening.Service.CustomerDetailsService;
import com.suryoday.EtbFdOpening.Service.FdOpeningService;
import com.suryoday.EtbFdOpening.Service.SendOtpService;

@Component
@RestController
@RequestMapping(value = "/fdOpening")
public class CustomerDetailsEncyController extends OncePerRequestFilter {
	Logger logger = LoggerFactory.getLogger(CustomerDetailsEncyController.class);
	@Autowired
	CustomerDetailsService custsomerdetailsservice;
	@Autowired
	FdOpeningService fdservice;
	@Autowired
	SendOtpService otpservice;

	@RequestMapping(value = "/checkCustomerEtbOrNtbEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> checkCustomerEtbOrNtb(@RequestBody String bm,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("checkCustomerEtbOrNtb start");
		logger.debug("checkCustomerEtbOrNtb request" + bm);
		JSONObject Header = new JSONObject();

		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			JSONObject encryptJSONObject = new JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
//			String mobileno = jsonObject.getJSONObject("Data").getString("MobileNo");
			String AadhaarNo = jsonObject.getJSONObject("Data").optString("AadhaarNo", "");
			String PanNo = jsonObject.getJSONObject("Data").optString("PanNo", "");

			JSONObject sendOtp = custsomerdetailsservice.getCustomerDetailsEtbOrNtb(AadhaarNo, PanNo, Header);
//			JSONObject sendOtp = custsomerdetailsservice.getCustomerDetails(mobileno, Header);

			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (sendOtp != null) {
				String Data2 = sendOtp.getString("data");
				logger.debug("data2");
				JSONObject Data1 = new JSONObject(Data2);

				logger.debug(Data1.toString());

				if (Data1.has("Data")) {
					h = HttpStatus.OK;
					Data1.put("isEtb", true);

				} else if (Data1.has("Error")) {
					h = HttpStatus.OK;
					Data1.put("isEtb", false);
				}
				logger.debug("checkCustomerEtbOrNtb response" + Data1);
				data = Data1.toString();
				String encryptString2 = Crypt.encrypt(data, X_encode_ID);
				JSONObject data2 = new JSONObject();
				data2.put("value", encryptString2);
				JSONObject data3 = new JSONObject();
				data3.put("Data", data2);
				logger.debug("checkCustomerEtbOrNtb response : " + data3.toString());
				return new ResponseEntity<Object>(data3.toString(), h);

			} else {
				logger.debug("timeout");
				return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
			}
		} else {
			JSONObject data2 = new JSONObject();
			data2.put("value", "SessionId is expired or Invalid sessionId");
			JSONObject data3 = new JSONObject();
			data3.put("Error", data2);
			logger.debug("SessionId is expired or Invalid sessionId");
			return new ResponseEntity<Object>(data3.toString(), HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/getCustomerDetailsEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> getCustomerDetails(@RequestBody String bm,
//			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("getCustomerDetailsEncy start");
		logger.debug("getCustomerDetailsEncy request" + bm);
		JSONObject Header = new JSONObject();
//		 Header.put("X-Correlation-ID",X_CORRELATION_ID );
		Header.put("X-From-ID", X_From_ID);
		Header.put("X-User-ID", "30639");
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
			String mobileno = jsonObject.getJSONObject("Data").getString("MobileNo");

			JSONObject sendOtp = custsomerdetailsservice.getCustomerDetails(mobileno, Header);

			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (sendOtp != null) {
				String Data2 = sendOtp.getString("data");
				logger.debug("data2");
				JSONObject Data1 = new JSONObject(Data2);

				logger.debug(Data1.toString());

				if (Data1.has("Data")) {
					h = HttpStatus.OK;

				} else if (Data1.has("Error")) {
					h = HttpStatus.BAD_REQUEST;

				}
				logger.debug("response" + Data1);
				data = Data1.toString();
				String encryptString2 = Crypt.encrypt(data, X_encode_ID);
				JSONObject data2 = new JSONObject();
				data2.put("value", encryptString2);
				JSONObject data3 = new JSONObject();
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

	@RequestMapping(value = "/getDetailsByCustIdEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> getDetailsByCustIdEncy(@RequestBody String bm,
//			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("getCustomerDetailsEncy start");
		logger.debug("getCustomerDetailsEncy request" + bm);
		JSONObject Header = new JSONObject();
//		 Header.put("X-Correlation-ID",X_CORRELATION_ID );
		Header.put("X-From-ID", X_From_ID);
		Header.put("X-User-ID", "30639");
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
			String custId = jsonObject.getJSONObject("Data").getString("CustomerId");

			JSONObject getDetailsByCustId = custsomerdetailsservice.getDetailsByCustId(custId, Header);

			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (getDetailsByCustId != null) {
				String Data2 = getDetailsByCustId.getString("data");
				logger.debug("data2");
				JSONObject Data1 = new JSONObject(Data2);

				logger.debug(Data1.toString());

				if (Data1.has("Data")) {
					h = HttpStatus.OK;

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

	@RequestMapping(value = "/getAccountDetailsEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> getAccountDetails(@RequestBody String bm,
//			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("getAccountDetailsEncy start");
		logger.debug("getAccountDetailsEncy request" + bm);
		JSONObject Header = new JSONObject();
//		 Header.put("X-Correlation-ID",X_CORRELATION_ID );
		Header.put("X-From-ID", X_From_ID);
		Header.put("X-To-ID", X_To_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);
		Header.put("X-User-ID", "30639");
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
			String accountNo = jsonObject.getJSONObject("Data").getString("AccountNo");

			JSONObject accountDetails = custsomerdetailsservice.getAccountDetails(accountNo, Header);

			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (accountDetails != null) {
				String Data2 = accountDetails.getString("data");
				logger.debug("data2");
				JSONObject Data1 = new JSONObject(Data2);

				logger.debug(Data1.toString());

				if (Data1.has("Data")) {
					h = HttpStatus.OK;

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

	@RequestMapping(value = "/calculateDepositEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> calculateDeposit(@RequestBody String bm,
			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("calculateDeposit start");

		JSONObject Header = new JSONObject();
		Header.put("X-Correlation-ID", X_CORRELATION_ID);
		Header.put("X-From-ID", X_From_ID);
		Header.put("UserID", "30639");
		Header.put("X-To-ID", X_To_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);
		Header.put("X-Request-ID", X_Request_ID);

		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);
			logger.debug("calculateDeposit request" + decryptContainerString);
			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			JSONObject calculateDeposit = custsomerdetailsservice.calculateDeposit(jsonObject, Header);
			System.out.println(calculateDeposit);
			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (calculateDeposit != null) {
				String Data2 = calculateDeposit.getString("data");
				logger.debug("data2");
				JSONObject Data1 = new JSONObject(Data2);

				logger.debug(Data1.toString());

				if (Data1.has("Data")) {
					h = HttpStatus.OK;

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

	@RequestMapping(value = "/validateCustomerMobileNumberEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> validateCustomerMobileNumber(@RequestBody String bm,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID) throws Exception {

		logger.debug("validateCustomerMobileNumberEncy start");
		logger.debug("validateCustomerMobileNumberEncy request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("Content-Type", "application/json");
		Header.put("X-Request-ID", "WNT");
		Header.put("cache-control", "no-cache");
		Header.put("Postman-Token", "07932b6f-aa68-4fff-a885-88bf0b2083be");
		String description = "";
//		String encodeId = "bXVzdGJlMTZieXRlc2tleQ==";
//		String decryptContainerString = Crypt.decrypt(bm, encodeId);
		JSONObject encryptJSONObject = new JSONObject(bm);
		String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");
		String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

		logger.debug("decryptContainerString :: " + decryptContainerString);
		JSONObject jsonObject = new JSONObject(decryptContainerString);

		String mobileNo = jsonObject.getString("MobileNumber");
		if (mobileNo.length() != 0) {

			JSONObject responseObj = custsomerdetailsservice.validateCustomerMobileNumber(mobileNo, Header);
			logger.debug(" responseObj :: " + responseObj.toString());
			String matchFlag = new JSONObject(responseObj.optString("data", "{}")).optString("MatchFlag", "");
			logger.debug("matchFlag :: " + matchFlag);

			if (matchFlag != "") {
				JSONObject resp = new JSONObject();
				JSONObject respData = new JSONObject();
				if ("100% Match".equals(matchFlag)) {
					respData.put("StatusCode", "200");
					respData.put("Description", "Customer matched with provided mobile number.");
					respData.put("MatchFlag", true);
					resp.put("Data", respData);

				} else {
					respData.put("StatusCode", "200");
					respData.put("Description", "Unable to identify customer with the given mobile number.");
					respData.put("MatchFlag", false);
					resp.put("Data", respData);
				}
				return ResponseEntity.status(HttpStatus.OK).body(resp.toMap());
			} else {
				JSONObject inner = new JSONObject(responseObj.getString("data"));
				if (inner.has("Error")) {
					description = inner.getJSONObject("Error").getString("Description");
				}
			}
		}
		JSONObject response = new JSONObject();
		JSONObject error = new JSONObject();
		error.put("StatusCode", "400");
		error.put("Description", description.isEmpty() ? "Something went wrong." : description);
		response.put("Error", error);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.toMap());

	}

	@RequestMapping(value = "/validateCustomerDetailsEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> validateCustomerDetails(@RequestBody String bm,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("validateCustomerDetails start");
		logger.debug("validateCustomerDetails request" + bm);
		JSONObject Header = new JSONObject();
//		 Header.put("X-Correlation-ID",X_CORRELATION_ID );
		Header.put("X-From-ID", X_From_ID);
		Header.put("X-User-ID", "30639");
		Header.put("X-Request-ID", X_Request_ID);

		org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
		String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

		// logger.debug("start request" + bm.toString());

		String key = X_Session_ID;
		String encodeId = "bXVzdGJlMTZieXRlc2tleQ==";
		String decryptContainerString = Crypt.decrypt(encryptString, encodeId);

		String data = "";
		JSONObject jsonObject = new JSONObject(decryptContainerString);
		String mobileNo = jsonObject.getJSONObject("Data").getString("MobileNo");
		String dob = jsonObject.getJSONObject("Data").getString("Dob");
		String panNo = jsonObject.getJSONObject("Data").getString("PanNo");
		if (dob.length() != 0 || panNo.length() != 0) {
			JSONObject customerDetails = custsomerdetailsservice.getCustomerDetails(mobileNo, Header);

			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (customerDetails != null) {
				String Data2 = customerDetails.getString("data");
				logger.debug("data2");
				JSONObject Data1 = new JSONObject(Data2);

				logger.debug(Data1.toString());

				if (Data1.has("Data")) {
					h = HttpStatus.OK;
					JSONObject customerDetailsJson = Data1.getJSONObject("Data").getJSONArray("CustomerDetails")
							.getJSONObject(0);
//							   JSONObject accountDetailsJson = Data1.getJSONObject("Data").getJSONArray("CustomerDetails").getJSONObject(0);
					String dateOfBirth = customerDetailsJson.getString("DateOfBirth");
					String panNumber = "";
					if (customerDetailsJson.has("PANNumber")) {
						panNumber = customerDetailsJson.getString("PANNumber");
					}
//							   String panNumber="123";
					org.json.simple.JSONObject resp = new org.json.simple.JSONObject();
					org.json.simple.JSONObject respData = new org.json.simple.JSONObject();
					if (dob.equalsIgnoreCase(dateOfBirth)) {
						respData.put("StatusCode", "200");
						respData.put("Description", "The given details succesfully matches");
						resp.put("Data", respData);
						data = resp.toString();
						String encryptString2 = Crypt.encrypt(data, encodeId);
						org.json.JSONObject data2 = new org.json.JSONObject();
						data2.put("value", encryptString2);
						org.json.JSONObject data3 = new org.json.JSONObject();
						data3.put("Data", data2);
						logger.debug("response : " + data3.toString());
						return new ResponseEntity<Object>(data3.toString(), h);
					} else if (panNo.equalsIgnoreCase(panNumber)) {
						respData.put("StatusCode", "200");
						respData.put("Description", "The given details succesfully matches");
						resp.put("Data", respData);
						data = resp.toString();
						String encryptString2 = Crypt.encrypt(data, encodeId);
						org.json.JSONObject data2 = new org.json.JSONObject();
						data2.put("value", encryptString2);
						org.json.JSONObject data3 = new org.json.JSONObject();
						data3.put("Data", data2);
						logger.debug("response : " + data3.toString());
						return new ResponseEntity<Object>(data3.toString(), h);
					} else {
						respData.put("StatusCode", "400");
						respData.put("Description", "The given details did not matched");
						resp.put("Error", respData);
						data = resp.toString();
						String encryptString2 = Crypt.encrypt(data, encodeId);
						org.json.JSONObject data2 = new org.json.JSONObject();
						data2.put("value", encryptString2);
						org.json.JSONObject data3 = new org.json.JSONObject();
						data3.put("Data", data2);
						logger.debug("response : " + data3.toString());
						return new ResponseEntity<Object>(data3.toString(), h.BAD_REQUEST);
					}
				} else if (Data1.has("Error")) {
					h = HttpStatus.BAD_REQUEST;
					data = Data1.toString();
					String encryptString2 = Crypt.encrypt(data, encodeId);
					org.json.JSONObject data2 = new org.json.JSONObject();
					data2.put("value", encryptString2);
					org.json.JSONObject data3 = new org.json.JSONObject();
					data3.put("Data", data2);
					logger.debug("response : " + data3.toString());
					return new ResponseEntity<Object>(data3.toString(), h);
				}
				logger.debug("response" + Data1);

			} else {
				logger.debug("timeout");
				return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
			}
		}
		JSONObject response = new JSONObject();
		JSONObject error = new JSONObject();
		error.put("StatusCode", "400");
		error.put("Description", "Please enter dob or pan no");
		response.put("Error", error);
		data = response.toString();
		String encryptString2 = Crypt.encrypt(data, encodeId);
		org.json.JSONObject data2 = new org.json.JSONObject();
		data2.put("value", encryptString2);
		org.json.JSONObject data3 = new org.json.JSONObject();
		data3.put("Data", data2);
		logger.debug("response : " + data3.toString());
		return new ResponseEntity<Object>(data3.toString(), HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(value = "/fetchInterestRatesEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> fetchInterestRatesEncy(@RequestBody String jsonRequest,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID, HttpServletRequest req)
			throws Exception {
		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			org.json.JSONObject encryptJSONObject = new org.json.JSONObject(jsonRequest);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			logger.debug("start request :: " + jsonRequest.toString());
			logger.debug("X_encode_ID :: " + X_encode_ID);

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			logger.debug("fetchInterestRates start");
			logger.debug("request" + jsonRequest);

			logger.debug("db Call start");
			List<InterestRates> roaocpvProductType2 = custsomerdetailsservice.fetchInterestRates();
			JSONArray array = new JSONArray(roaocpvProductType2);
			logger.debug("db Call end" + roaocpvProductType2);
			org.json.simple.JSONObject response2 = new org.json.simple.JSONObject();
			response2.put("Data", array);
			data = response2.toString();
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

	@RequestMapping(value = "/CIfCreationEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> CIfCreationEncy(@RequestBody String bm,
			@RequestHeader(name = "X-Correlation-ID", required = true) String headerPersist,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "X-User-ID", required = true) String X_User_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID, HttpServletRequest req,
			HttpServletResponse res) throws Exception {

		JSONObject Header = new JSONObject();

		Header.put("X-From-ID", X_From_ID);
		Header.put("X-To-ID", X_To_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);
		Header.put("X-User-ID", "S7171");
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

			JSONObject cifCreation = custsomerdetailsservice.cifCreation(jsonObject, Header);
			if (cifCreation != null) {
				System.out.println("Api Response" + cifCreation);
				String Data2 = cifCreation.getString("data");
				JSONObject Data1 = new JSONObject(Data2);
				HttpStatus h = HttpStatus.BAD_GATEWAY;
				if (Data1.has("Data")) {
					h = HttpStatus.OK;

				} else if (Data1.has("Error")) {
					h = HttpStatus.BAD_REQUEST;
				}
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

	@RequestMapping(value = "/accountCreationEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> accountCreation(@RequestBody String bm,
			@RequestHeader(name = "X-Correlation-ID", required = true) String headerPersist,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req,
			HttpServletResponse res) throws Exception {

		JSONObject Header = new JSONObject();

		Header.put("X-From-ID", X_From_ID);
		Header.put("X-To-ID", X_To_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);
		Header.put("X-User-ID", "14508");
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
			System.out.println(jsonObject);
			JSONObject accountCreation = custsomerdetailsservice.accountCreation(jsonObject, Header);
			if (accountCreation != null) {
				System.out.println("Api Response" + accountCreation);
				String Data2 = accountCreation.getString("data");
				JSONObject Data1 = new JSONObject(Data2);
				HttpStatus h = HttpStatus.BAD_GATEWAY;
				if (Data1.has("Data")) {
					h = HttpStatus.OK;

				} else if (Data1.has("Error")) {
					h = HttpStatus.BAD_REQUEST;
				}
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

	@RequestMapping(value = "/nameMatchEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> nameMatch(@RequestBody String bm,
			@RequestHeader(name = "X-Correlation-ID", required = true) String headerPersist,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID, HttpServletRequest req,
			HttpServletResponse res) throws Exception {

		JSONObject Header = new JSONObject();

		Header.put("X-From-ID", X_From_ID);
		Header.put("X-To-ID", X_To_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);
		Header.put("X-User-ID", "S5050");
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
			JSONObject nameMatch = custsomerdetailsservice.nameMatch(jsonObject, Header);
			if (nameMatch != null) {
				System.out.println("Api Response" + nameMatch);
				String Data2 = nameMatch.getString("data");
				JSONObject Data1 = new JSONObject(Data2);
				HttpStatus h = HttpStatus.BAD_GATEWAY;
				if (Data1.has("Data")) {
					h = HttpStatus.OK;

				} else if (Data1.has("Error")) {
					JSONObject errorJson = Data1.getJSONObject("Error");
					if (errorJson.isEmpty()) {
						h = HttpStatus.OK;
					} else {
						h = HttpStatus.BAD_REQUEST;
					}
					h = HttpStatus.BAD_REQUEST;
				}
				logger.debug("Final Resp" + Data1.toString());
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

	@RequestMapping(value = "/saveRequestsEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> saveRequestsEncy(@RequestBody String jsonRequest,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID, HttpServletRequest req,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID) throws Exception {

		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			JSONObject encryptJSONObject = new JSONObject(jsonRequest);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			logger.debug("saveRequestsEncy start");
			logger.debug(" saveRequestsEncy request :: " + jsonObject.toString());
			JSONObject cifReq = jsonObject.getJSONObject("Data").getJSONObject("CifCreationRequest");
			JSONObject accReq = jsonObject.getJSONObject("Data").getJSONObject("AccCreationRequest");
			JSONObject fdReq = jsonObject.getJSONObject("Data").getJSONObject("FdCreationRequest");
			String applicationNo = jsonObject.getJSONObject("Data").getString("ApplicationNo");
			FdOpeningNTB fdOpening = fdservice.fetchByApplicationNo(Long.parseLong(applicationNo));

			logger.debug("setCifRequest :: " + cifReq.toString());
			logger.debug("setAccCreationRequest :: " + accReq.toString());

			fdOpening.setCifRequest(cifReq.toString());
			fdOpening.setAccCreationRequest(accReq.toString());

			if ("abcl".equalsIgnoreCase(fdOpening.getLgCode())) {
				// add "Agent" to fdReq
				JSONObject fdReqData = fdReq.getJSONObject("Data");
				JSONObject agent = new JSONObject();
				agent.put("AgentID", fdOpening.getLgCode());
				agent.put("AgentLatLong", "");
				fdReqData.put("Agent", agent);
				fdReq.put("Data", fdReqData);
				logger.debug("Added Agent to FdCreationRequest because lg code is abcl");
			}

			fdOpening.setFdOpeningReq(fdReq.toString());
			logger.debug("setFdOpeningReq :: " + fdReq.toString());
			otpservice.save(fdOpening);
			JSONObject response2 = new JSONObject();
			response2.put("Data", "Success");
			logger.debug("final response" + response2.toString());
			data = response2.toString();
			String encryptString2 = Crypt.encrypt(data, X_encode_ID);
			JSONObject data2 = new JSONObject();
			data2.put("value", encryptString2);
			JSONObject data3 = new JSONObject();
			data3.put("Data", data2);
			logger.debug("response : " + data3.toString());
			return new ResponseEntity<Object>(data3.toString(), HttpStatus.OK);
		} else {
			JSONObject data2 = new JSONObject();
			data2.put("value", "SessionId is expired or Invalid sessionId");
			JSONObject data3 = new JSONObject();
			data3.put("Error", data2);
			logger.debug("SessionId is expired or Invalid sessionId");
			return new ResponseEntity<Object>(data3.toString(), HttpStatus.UNAUTHORIZED);
		}
	}

	@PostMapping(value = "/checkAmlEncy", produces = "application/json")
	public ResponseEntity<Object> checkAmlEncy(@RequestBody String bm,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID, HttpServletRequest req)
			throws Exception {
		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			logger.debug("checkAmlEncy jsonObject :: " + jsonObject.toString());

			JSONObject checkAml = custsomerdetailsservice.checkAml(jsonObject);
			String applicationNo = jsonObject.getJSONObject("Data").optString("ApplicationNo", null);
			if (applicationNo != null && !applicationNo.isEmpty()) {
				FdOpeningNTB fdOpening = fdservice.fetchByApplicationNo(Long.parseLong(applicationNo));
				fdOpening.setAmlResp(checkAml.toString());
				otpservice.save(fdOpening);
			}

			logger.debug("checkAmlEncy Api Response :: " + checkAml);
			if (checkAml != null) {
				String Data2 = checkAml.getString("data");
				JSONObject Data1 = new JSONObject(Data2);
				data = Data1.toString();
				String encryptString2 = Crypt.encrypt(data, X_encode_ID);
				org.json.JSONObject data2 = new org.json.JSONObject();
				data2.put("value", encryptString2);
				org.json.JSONObject data3 = new org.json.JSONObject();
				data3.put("Data", data2);
				logger.debug("response : " + data3.toString());
				HttpStatus h = Data1.has("Data") ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
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
