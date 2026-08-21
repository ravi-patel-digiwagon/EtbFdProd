package com.suryoday.EtbFdOpening.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
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
public class CustomerDetailsController extends OncePerRequestFilter {
	Logger logger = LoggerFactory.getLogger(CustomerDetailsController.class);
	@Autowired
	CustomerDetailsService custsomerdetailsservice;
	@Autowired
	FdOpeningService fdservice;
	@Autowired
	SendOtpService otpservice;
	
	
	@RequestMapping(value = "/checkCustomerEtbOrNtb", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> checkCustomerEtbOrNtb(@RequestBody String bm, HttpServletRequest req)
			throws Exception {
		logger.debug("checkCustomerEtbOrNtb start");
		logger.debug("checkCustomerEtbOrNtb request" + bm);
		JSONObject Header = new JSONObject();



			String data = "";
			JSONObject jsonObject = new JSONObject(bm);

			String AadhaarNo = jsonObject.getJSONObject("Data").optString("AadhaarNo", "");
			String PanNo = jsonObject.getJSONObject("Data").optString("PanNo", "");
			
			JSONObject sendOtp = custsomerdetailsservice.getCustomerDetailsEtbOrNtb(AadhaarNo, PanNo, Header);

			logger.debug("checkCustomerEtbOrNtb RES "+ sendOtp.toString());
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
				logger.debug("response" + Data1);
				data = Data1.toString();
//				String encryptString2 = Crypt.encrypt(data, X_encode_ID);
//				org.json.JSONObject data2 = new org.json.JSONObject();
//				data2.put("value", encryptString2);
//				org.json.JSONObject data3 = new org.json.JSONObject();
//				data3.put("Data", data2);
				logger.debug("response : " + data.toString());
				return new ResponseEntity<Object>(data.toString(), h);

			} else {
				logger.debug("timeout");
				return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
			}
		
	}
	
	

	@RequestMapping(value = "/getCustomerDetails", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> getCustomerDetails(@RequestBody String bm,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("getCustomerDetails start");
		logger.debug("getCustomerDetails request" + bm);
		JSONObject Header = new JSONObject();
//		 Header.put("X-Correlation-ID",X_CORRELATION_ID );
		Header.put("X-From-ID", X_From_ID);
		Header.put("X-User-ID", "30639");
		Header.put("X-Request-ID", X_Request_ID);

		JSONObject jsonObject = new JSONObject(bm);
		String mobileNo = jsonObject.getJSONObject("Data").getString("MobileNo");
		JSONObject customerDetails = custsomerdetailsservice.getCustomerDetails(mobileNo, Header);

		HttpStatus h = HttpStatus.BAD_GATEWAY;
		if (customerDetails != null) {
			String Data2 = customerDetails.getString("data");
			logger.debug("data2");
			JSONObject Data1 = new JSONObject(Data2);

			logger.debug(Data1.toString());

			if (Data1.has("Data")) {
				h = HttpStatus.OK;

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

	@RequestMapping(value = "/getDetailsByCustId", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> getDetailsByCustId(@RequestBody String bm,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("getCustomerDetails start");
		logger.debug("getCustomerDetails request" + bm);
		JSONObject Header = new JSONObject();
//		 Header.put("X-Correlation-ID",X_CORRELATION_ID );
		Header.put("X-From-ID", X_From_ID);
		Header.put("X-User-ID", "30639");
		Header.put("X-Request-ID", X_Request_ID);

		JSONObject jsonObject = new JSONObject(bm);
		String custId = jsonObject.getJSONObject("Data").getString("CustomerId");
		JSONObject customerDetails = custsomerdetailsservice.getDetailsByCustId(custId, Header);

		HttpStatus h = HttpStatus.BAD_GATEWAY;
		if (customerDetails != null) {
			String Data2 = customerDetails.getString("data");
			logger.debug("data2");
			JSONObject Data1 = new JSONObject(Data2);

			logger.debug(Data1.toString());

			if (Data1.has("Data")) {
				h = HttpStatus.OK;

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

	@RequestMapping(value = "/getAccountDetails", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> getAccountDetails(@RequestBody String bm,
//			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("getAccountDetails start");
		logger.debug("getAccountDetails request" + bm);
		JSONObject Header = new JSONObject();
//		 Header.put("X-Correlation-ID",X_CORRELATION_ID );
		Header.put("X-From-ID", X_From_ID);
		Header.put("X-To-ID", X_To_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);
		Header.put("X-User-ID", "30639");
		Header.put("X-Request-ID", X_Request_ID);

		JSONObject jsonObject = new JSONObject(bm);
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
			return new ResponseEntity<Object>(Data1.toString(), h);

		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@RequestMapping(value = "/calculateDeposit", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> calculateDeposit(@RequestBody String bm,
			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("calculateDeposit start");
		logger.debug("calculateDeposit request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("X-Correlation-ID", X_CORRELATION_ID);
		Header.put("X-From-ID", X_From_ID);
		Header.put("UserID", "30639");
		Header.put("X-To-ID", X_To_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);
		Header.put("X-Request-ID", X_Request_ID);

		JSONObject jsonObject = new JSONObject(bm);
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
			return new ResponseEntity<Object>(Data1.toString(), h);

		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@RequestMapping(value = "/mobileLastFourDigitChecker", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> mobileLastFourDigitChecker(@RequestBody String bm) throws Exception {
		JSONObject inputJson = new JSONObject(bm);
		JSONObject data = inputJson.getJSONObject("Data");

		String adMobile = data.optString("adMobile");
		String cusMobile = data.optString("cusMobile");

		boolean isMatch = false;
		if (adMobile != null && cusMobile != null && adMobile.length() >= 4 && cusMobile.length() >= 4) {

			String lastFouradMobile = adMobile.substring(adMobile.length() - 4);
			String lastFourCusMobile = cusMobile.substring(cusMobile.length() - 4);
			isMatch = lastFouradMobile.equals(lastFourCusMobile);
		}

		Map<String, Object> response = new HashMap<>();
		response.put("match", isMatch);

		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/validateCustomerMobileNumber", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> validateCustomerMobileNumber(@RequestBody String bm) throws Exception {

		logger.debug("validateCustomerMobileNumber request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("Content-Type", "application/json");
		Header.put("X-Request-ID", "WNT");
		Header.put("Postman-Token", "07932b6f-aa68-4fff-a885-88bf0b2083be");
		Header.put("cache-control", "no-cache");
		String description = "";

		// not used in code it's just for convert
		String encryptStringDemo = Crypt.encrypt(bm, "bXVzdGJlMTZieXRlc2tleQ==");
		logger.debug(" enc :: " + encryptStringDemo);

		JSONObject jsonObject = new JSONObject(bm);
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
//			return new ResponseEntity<Object>(resp, h);
				logger.debug(":: Response :: " + resp.toMap());
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
		logger.debug(":: Response :: " + response.toMap());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.toMap());
		// return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);

	}

	@RequestMapping(value = "/validateCustomerDetails", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> validateCustomerDetails(@RequestBody String bm,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("validateCustomerDetails start");
		logger.debug("validateCustomerDetails request" + bm);
		JSONObject Header = new JSONObject();
//		 Header.put("X-Correlation-ID",X_CORRELATION_ID );
		Header.put("X-From-ID", X_From_ID);
		Header.put("X-User-ID", "30639");
		Header.put("X-Request-ID", X_Request_ID);

		JSONObject jsonObject = new JSONObject(bm);
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
						return new ResponseEntity<Object>(resp, h);
					} else if (panNo.equalsIgnoreCase(panNumber)) {
						respData.put("StatusCode", "200");
						respData.put("Description", "The given details succesfully matches");
						resp.put("Data", respData);
						return new ResponseEntity<Object>(resp, h);
					} else {
						respData.put("StatusCode", "400");
						respData.put("Description", "The given details did not matched");
						resp.put("Error", respData);
						return new ResponseEntity<Object>(resp, h.BAD_REQUEST);
					}
				} else if (Data1.has("Error")) {
					h = HttpStatus.BAD_REQUEST;
					return new ResponseEntity<Object>(Data1.toString(), h);
				}
				logger.debug("response" + Data1);

			} else {
				logger.debug("timeout");
				return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
			}
		}
		org.json.simple.JSONObject response = new org.json.simple.JSONObject();
		org.json.simple.JSONObject error = new org.json.simple.JSONObject();
		error.put("StatusCode", "400");
		error.put("Description", "Please enter dob or pan no");
		response.put("Error", error);
		return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(value = "/fetchInterestRates", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> fetchInterestRates(@RequestBody String jsonRequest,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID) {

		JSONObject jsonObject = new JSONObject(jsonRequest);
		logger.debug("fetchInterestRates start");
		logger.debug("request" + jsonRequest);

		logger.debug("db Call start");
		List<InterestRates> roaocpvProductType2 = custsomerdetailsservice.fetchInterestRates();
		JSONArray array = new JSONArray(roaocpvProductType2);
		logger.debug("db Call end" + roaocpvProductType2);
		org.json.simple.JSONObject response2 = new org.json.simple.JSONObject();
		response2.put("Data", array);
		logger.debug("final response" + response2.toString());
		return new ResponseEntity<Object>(response2.toString(), HttpStatus.OK);

	}

	@RequestMapping(value = "/CIfCreation", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> CIfCreation(@RequestBody String bm,
			@RequestHeader(name = "X-Correlation-ID", required = true) String headerPersist,
			@RequestHeader(name = "ApplicationNo", required = true) String applicationNo,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req,
			HttpServletResponse res) throws Exception {

		JSONObject Header = new JSONObject();

		Header.put("X-From-ID", X_From_ID);
		Header.put("X-To-ID", X_To_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);
		Header.put("X-User-ID", "S7171");
		Header.put("X-Request-ID", X_Request_ID);

		JSONObject jsonObject = new JSONObject(bm);
		FdOpeningNTB fdOpening = fdservice.fetchByApplicationNo(Long.parseLong(applicationNo));
		fdOpening.setCifRequest(jsonObject.toString());
		JSONObject cifCreation = custsomerdetailsservice.cifCreation(jsonObject, Header);
		if (cifCreation != null) {
			logger.debug("CIfCreation Response" + cifCreation);
			String Data2 = cifCreation.getString("data");
			JSONObject Data1 = new JSONObject(Data2);
			HttpStatus h = HttpStatus.BAD_GATEWAY;
			 String matchStatus = Data1.getJSONObject("Data").optString("MatchStatus",null);
	            
			if (Data1.has("Data") && "NoMatch".equalsIgnoreCase(matchStatus)) {
				h = HttpStatus.OK;
				fdOpening.setCifResponse(Data1.toString());
				String ucic = Data1.getJSONObject("Data").optString("UCIC",null);
				fdOpening.setCifCustomerId(ucic);
				fdOpening.setIsCifCreated("Y");

			} else if (Data1.has("Error")) {
				h = HttpStatus.BAD_REQUEST;
				fdOpening.setIsCifCreated("N");
			}
			otpservice.save(fdOpening);
			return new ResponseEntity<Object>(Data1.toString(), h);
		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@RequestMapping(value = "/accountCreation", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> accountCreation(@RequestBody String bm,
			@RequestHeader(name = "X-Correlation-ID", required = true) String headerPersist,
			@RequestHeader(name = "ApplicationNo", required = true) String applicationNo,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req,
			HttpServletResponse res) throws Exception {

		JSONObject Header = new JSONObject();

		Header.put("X-From-ID", X_From_ID);
		Header.put("X-To-ID", X_To_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);
		Header.put("X-User-ID", "14508");
		Header.put("X-Request-ID", X_Request_ID);

		JSONObject jsonObject = new JSONObject(bm);
		FdOpeningNTB fdOpening = fdservice.fetchByApplicationNo(Long.parseLong(applicationNo));
		fdOpening.setAccCreationRequest(jsonObject.toString());
		JSONObject accountCreation = custsomerdetailsservice.accountCreation(jsonObject, Header);
		if (accountCreation != null) {
			logger.debug("Api Response" + accountCreation);
			String Data2 = accountCreation.getString("data");
			JSONObject Data1 = new JSONObject(Data2);
			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (Data1.has("Data")) {
				h = HttpStatus.OK;
				fdOpening.setIsAccCreated("Y");

			} else if (Data1.has("Error")) {
				h = HttpStatus.BAD_REQUEST;
				fdOpening.setIsAccCreated("N");
			}
			fdOpening.setAccCreationResponse(Data1.toString());
			otpservice.save(fdOpening);
			return new ResponseEntity<Object>(Data1.toString(), h);
		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@RequestMapping(value = "/nameMatch", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> nameMatch(@RequestBody String bm,
			@RequestHeader(name = "X-Correlation-ID", required = true) String headerPersist,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req,
			HttpServletResponse res) throws Exception {

		JSONObject Header = new JSONObject();

		Header.put("X-From-ID", X_From_ID);
		Header.put("X-To-ID", X_To_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);
		Header.put("X-User-ID", "S5050");
		Header.put("X-Request-ID", X_Request_ID);

		JSONObject jsonObject = new JSONObject(bm);
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
			}
			return new ResponseEntity<Object>(Data1.toString(), h);
		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@RequestMapping(value = "/saveRequests", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> saveRequests(@RequestBody String jsonRequest,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID) {

		JSONObject jsonObject = new JSONObject(jsonRequest);

		JSONObject cifReq = jsonObject.getJSONObject("Data").getJSONObject("CifCreationRequest");
		JSONObject accReq = jsonObject.getJSONObject("Data").getJSONObject("AccCreationRequest");
		JSONObject fdReq = jsonObject.getJSONObject("Data").getJSONObject("FdCreationRequest");
		String applicationNo = jsonObject.getJSONObject("Data").getString("ApplicationNo");
		FdOpeningNTB fdOpening = fdservice.fetchByApplicationNo(Long.parseLong(applicationNo));
		logger.debug("saveRequests db Call start");

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
		logger.debug("saveRequests request :: " + fdOpening.getApplicationNo() + " || " + fdOpening.toString());
		otpservice.save(fdOpening);
		org.json.simple.JSONObject response2 = new org.json.simple.JSONObject();
		response2.put("Data", "Success");
		logger.debug("final response" + response2.toString());
		return new ResponseEntity<Object>(response2.toString(), HttpStatus.OK);

	}

	@PostMapping(value = "/checkAml", produces = "application/json")
	public ResponseEntity<Object> checkAml(@RequestBody String bm) {
		JSONObject jsonObject = new JSONObject(bm);
		JSONObject response = custsomerdetailsservice.checkAml(jsonObject);
//		JSONObject request = new JSONObject(bm);
//		String applicationno = request.getJSONObject("Data").getString("ApplicationNo");
		String applicationNo = jsonObject.getJSONObject("Data").optString("ApplicationNo", "");
		logger.debug("checkAml applicationNo " + applicationNo);
		if (applicationNo != null && !applicationNo.isEmpty()) {
			FdOpeningNTB fdOpening = fdservice.fetchByApplicationNo(Long.parseLong(applicationNo));
			fdOpening.setAmlResp(response.toString());
			otpservice.save(fdOpening);
		}
		
		return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
	}

	@RequestMapping(value = "/dmsUpload", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> dmsUpload(@RequestBody String bm) throws JSONException, IOException {
		logger.debug("dmsUpload start :: " + bm);
		JSONObject response = custsomerdetailsservice.dmsUpload(new JSONObject(bm));
		return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);

	}

	@RequestMapping(value = "/downloadPdf", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> downloadPdf(@RequestBody String bm) {
		logger.debug("downloadPdf start :: " + bm);
		JSONObject response = custsomerdetailsservice.downloadPdf(new JSONObject(bm));
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

	private byte[] restResponseBytes(ErrorResponse eErrorResponse) throws IOException {
		String serialized = new ObjectMapper().writeValueAsString(eErrorResponse);
		return serialized.getBytes();
	}

}
