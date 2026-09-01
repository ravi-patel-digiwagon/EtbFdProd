package com.suryoday.EtbFdOpening.Controller;

import java.io.IOException;

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
import com.suryoday.EtbFdOpening.Pojo.ErrorResponse;
import com.suryoday.EtbFdOpening.Pojo.FdOpening;
import com.suryoday.EtbFdOpening.Pojo.FdOpeningNTB;
import com.suryoday.EtbFdOpening.Service.FdOpeningService;
import com.suryoday.EtbFdOpening.Service.FdRecieptService;
import com.suryoday.EtbFdOpening.Service.SendOtpService;

@Component
@RestController
@RequestMapping(value = "/fdOpening")
public class FdOpeningEncyController extends OncePerRequestFilter {
	Logger logger = LoggerFactory.getLogger(FdOpeningController.class);
	@Autowired
	FdOpeningService fdopeningservice;
	@Autowired
	SendOtpService otpservice;
	@Autowired
	FdRecieptService fdservice;

	@Autowired
	FdRecieptService fdRecieptService;

	@RequestMapping(value = "/createDepositEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> createDeposit(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("createDeposit start");
		logger.debug("createDeposit request" + bm);
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
			System.out.println("Request" + jsonObject);
			JSONObject createDeposit = fdopeningservice.createDeposit(jsonObject, Header);

			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (createDeposit != null) {
				String Data2 = createDeposit.getString("data");
				logger.debug("data2");
				JSONObject Data1 = new JSONObject(Data2);

				logger.debug(Data1.toString());

				if (Data1.has("Data")) {
					h = HttpStatus.OK;

				} else if (Data1.has("Error")) {
					h = HttpStatus.BAD_REQUEST;

				}
				logger.debug("response" + Data1);
				FdOpening fdopening = fdservice.fetchByMobNoAndSessionId(mobileNo, X_Session_ID);
				fdopening.setFdRequest(jsonObject.toString());
				fdopening.setFdResponse(Data1.toString());
				fdopeningservice.save(fdopening);
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

	@RequestMapping(value = "/createDepositNTBEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> createDepositNTB(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "ApplicationNo", required = true) String ApplicationNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("createDeposit start");
		logger.debug("createDeposit request" + bm);
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
			JSONObject createDeposit = fdopeningservice.createDeposit(jsonObject, Header);
			FdOpeningNTB fetchByApplicationNo = fdopeningservice.fetchByApplicationNo(Long.parseLong(ApplicationNo));
			fetchByApplicationNo.setFdOpeningReq(jsonObject.toString());
			System.out.println(createDeposit);
			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (createDeposit != null) {
				String Data2 = createDeposit.getString("data");
				logger.debug("data2");
				JSONObject Data1 = new JSONObject(Data2);

				logger.debug(Data1.toString());

				if (Data1.has("Data")) {
					h = HttpStatus.OK;
					fetchByApplicationNo.setIsFdCreated("Y");
					fetchByApplicationNo.setIsDmsUpload("N");
				} else if (Data1.has("Error")) {
					h = HttpStatus.BAD_REQUEST;
					fetchByApplicationNo.setIsFdCreated("N");
				}
				fetchByApplicationNo.setFdOpeningResp(Data1.toString());
				otpservice.save(fetchByApplicationNo);
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

	@RequestMapping(value = "/saveFdDataEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> saveFdDataEncy(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("saveFdData start");
		logger.debug("saveFdData request" + bm);
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
			String saveFdData = fdopeningservice.saveFdData(mobileNo, X_Session_ID, jsonObject);

			org.json.simple.JSONObject response = new org.json.simple.JSONObject();
			org.json.simple.JSONObject Data = new org.json.simple.JSONObject();
			Data.put("Success", saveFdData);
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

	@RequestMapping(value = "/saveNtbFdDataEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> saveNtbFdDataEncy(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("saveFdData start");
		logger.debug("saveFdData request" + bm);
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

			String saveFdData = fdopeningservice.saveNtbFdData(jsonObject);

			org.json.simple.JSONObject response = new org.json.simple.JSONObject();
			org.json.simple.JSONObject Data = new org.json.simple.JSONObject();
			Data.put("Success", saveFdData);
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

	@RequestMapping(value = "/CloseFdEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> CloseFdEncy(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("CloseFd start");
		logger.debug("CloseFd request" + bm);
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
			JSONObject json = new JSONObject(decryptContainerString);
			String accountId = json.getJSONObject("Data").getString("AccountId");
			String toaccountId = json.getJSONObject("Data").getString("ToAccountId");
			JSONObject jsonObject = new JSONObject();
			JSONObject Data = new JSONObject();
			Data.put("TypeOfClosure", "C");
			Data.put("AccountId", accountId);
			Data.put("ToAccountId", toaccountId);
			Data.put("Comments", "Yes");
			Data.put("Amount", "");
			jsonObject.put("Data", Data);
			JSONObject CloseFd = fdopeningservice.CloseFd(jsonObject, Header);
			System.out.println(CloseFd);
			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (CloseFd != null) {
				String Data2 = CloseFd.getString("data");
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

	@RequestMapping(value = "/FdMaturityChangeEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> FdMaturityChange(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("CloseFd start");
		logger.debug("CloseFd request" + bm);
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
			JSONObject json = new JSONObject(decryptContainerString);
			JSONObject FdMaturityChange = fdopeningservice.FdMaturityChange(json, Header);

			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (FdMaturityChange != null) {
				String Data2 = FdMaturityChange.getString("data");
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

//	public static void main(String[] args) throws Exception {
//		int i = 4;
//		String X_encode_ID = "";
//		String temp = "";
//		JSONObject root = null;
//		if (i == 1) {
//			X_encode_ID = "YTdiZGVmODctODA5ZS00ZA==";
//			temp = "{ \"Data\": { \"value\": \"UXInaQVNKNUCw9/dEHi4vC32JxUZB3ZfM5mxC/pIjIUvuTvJYB2OqZ2DQFAZL7qY0KsWwKBuu5omZ7qkVJmJ7asdJD7+QSsmo9ojW5e1gA0DXg0frphBMln2vpNkYfvET2zRfQgvWqBccaj51sApy2TJX93upFvXBlvE83j7pGHGs59BFiNtTD0bShzTqfX3ZHj0yReINxxm0SQPGGBEOnkt0jPKRSnoPssjiMmqGEQXdRUP8vyUTwPWwEtLFwfR6QMr4An7z6/0sWa1frgf1wA6ZO5wC5RJayvAaFZHdZd8BCderVtl/PuLAqCuZvHFsfXQ2WWrRosp0G4nemL+7junmeHz4fpagF5RZC0fQlw5NP8xKpCRWkJMMQ/19Zg1oAGZ8pN8uLtmMssB5VfCJj4quc8VxuzAeykbNEU4K7mKoTNTQXB39rHK47tvTZqt0+XhKISJA6jmnd1XhZv6EK1PaqQjXGzwmmNToPqPpNlp6M/9APcR0mzabC/W5Jog\" } }";
//			root = new JSONObject(temp);
//		}
//		
//		if (i == 4) {
//			X_encode_ID = "MzBmMTZjNjYtYWY5Ni00Yg==";
//			temp = "{\"Data\":{\"value\":\"yLdtdXYC/H0EjM/cpV5gxfSWRr5DKDDRhx4dQpDcdt0ZO6udWVZsDXluOjRVq7Vm\"}}";
//			root = new JSONObject(temp);
//		}
//		
//		if (i == 2) {
//			X_encode_ID = "ZjE3NzQwODUtNjQ3Yy00Ng==";
//			temp = "{ \"Data\": { \"value\": \"BfSz8MYn4KK7B7IkFqx1NXb6jrQQCQ66Uc6ykbVeGuLQd+L6QjxsRgEXtLqWEmYg\" } }";
//			root = new JSONObject(temp);
//		}
//
//
//		if (i == 3) {
//			// {"ApplicationNo":"2509160001"}
//			X_encode_ID = "MzBmMTZjNjYtYWY5Ni00Yg==";
//			String encryptString = Crypt.encrypt("{\"Data\":{\"ApplicationNo\":\"2511110023\"}}", X_encode_ID);
//			System.out.println("enc :: "+ encryptString);
//			JSONObject data = new JSONObject();
//			data.put("value", encryptString);
//			root = new JSONObject();
//			root.put("Data", data);
//		}
//		String encryptStringValue = root.getJSONObject("Data").getString("value");
//		String decryptContainerString = Crypt.decrypt(encryptStringValue, X_encode_ID);
//		System.out.println("dec :: " + decryptContainerString);
//		JSONObject jsonObject = new JSONObject(decryptContainerString);
//
//	}

	private Object nullSafe(Object value) {
		return value == null ? JSONObject.NULL : value;
	}

	private JSONObject buildFdOpeningData(FdOpening fdopening) {
		JSONObject data = new JSONObject();
		data.put("id", fdopening.getId());
		data.put("mobileNo", nullSafe(fdopening.getMobileNo()));
		data.put("sessionId", nullSafe(fdopening.getSessionId()));
		data.put("depositAccountNo", nullSafe(fdopening.getDepositAccountNo()));
		data.put("depositAmount", nullSafe(fdopening.getDepositAmount()));
		data.put("tenure", nullSafe(fdopening.getTenure()));
		data.put("maturityAmout", nullSafe(fdopening.getMaturityAmout()));
		data.put("interestEarned", nullSafe(fdopening.getInterestEarned()));
		data.put("cifNo", nullSafe(fdopening.getCifNo()));
		data.put("roi", nullSafe(fdopening.getRoi()));
		data.put("fromAccount", nullSafe(fdopening.getFromAccount()));
		data.put("maturityDate", nullSafe(fdopening.getMaturityDate()));
		data.put("createdDate", nullSafe(fdopening.getCreatedDate()));
		data.put("updatedDate", nullSafe(fdopening.getUpdatedDate()));
		data.put("status", nullSafe(fdopening.getStatus()));
		data.put("FdRequest", nullSafe(fdopening.getFdRequest()));
		data.put("FdResponse", nullSafe(fdopening.getFdResponse()));
		data.put("custType", nullSafe(fdopening.getCustType()));
		data.put("productCode", nullSafe(fdopening.getProductCode()));
		data.put("upiId", nullSafe(fdopening.getUpiId()));
		data.put("isUpiVerify", nullSafe(fdopening.getIsUpiVerify()));
		data.put("verifyUpiReq", nullSafe(fdopening.getVerifyUpiReq()));
		data.put("verifyUpiResp", nullSafe(fdopening.getVerifyUpiResp()));
		data.put("nomineeDetails", nullSafe(fdopening.getNomineeDetails()));
		data.put("isPaymentDone", nullSafe(fdopening.getIsPaymentDone()));
		data.put("createOrderResp", nullSafe(fdopening.getCreateOrderResp()));
		data.put("paymentDetails", nullSafe(fdopening.getPaymentDetails()));
		data.put("isAccountVerify", nullSafe(fdopening.getIsAccountVerify()));
		data.put("accountNo", nullSafe(fdopening.getAccountNo()));
		data.put("ifsc", nullSafe(fdopening.getIfsc()));
		data.put("payuOrderId", nullSafe(fdopening.getPayuOrderId()));
		data.put("paymentDate", nullSafe(fdopening.getPaymentDate()));
		data.put("mihPayid", nullSafe(fdopening.getMihPayid()));
		return data;
	}

	@RequestMapping(value = "/fetchByIdEtbEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> fetchByIdEtbEncy(@RequestBody String bm,
	                                                       @RequestHeader(name = "Accept", required = true) String accept,
	                                                       @RequestHeader(name = "Content-Type", required = true) String Content_Type,
	                                                       @RequestHeader(name = "MobileNo", required = true) String mobileNo,
	                                                       @RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
	                                                       @RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
	                                                       @RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
	                                                       @RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
	                                                       @RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
	                                                       @RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
	                                                       @RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
	                                                       @RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("fetchByIdEtbEncy start");
		logger.debug("fetchByIdEtbEncy request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);

		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			JSONObject encryptJSONObject = new JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			//String applicationNo = jsonObject.getJSONObject("Data").getString("ApplicationNo");
			//FdOpeningNTB fdOpeningNTB = fdopeningservice.fetchByApplicationNo(Long.parseLong(applicationNo));
			FdOpening fdopening = fdRecieptService.fetchByMobNoAndSessionId(mobileNo, X_Session_ID);
			logger.debug("fetchByIdEtbEncy response" + fdopening.toString());
			JSONObject Data = buildFdOpeningData(fdopening);
			JSONObject response = new JSONObject();
			response.put("Data", Data);
			data = response.toString();
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

	@RequestMapping(value = "/fetchByApplicationIdEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> fetchByApplicationIdEncy(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("fetchByApplicationId start");
		logger.debug("fetchByApplicationId request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);

		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			JSONObject encryptJSONObject = new JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			String applicationNo = jsonObject.getJSONObject("Data").getString("ApplicationNo");
			FdOpeningNTB fdOpeningNTB = fdopeningservice.fetchByApplicationNo(Long.parseLong(applicationNo));
			logger.debug("fetchByApplicationId response" + fdOpeningNTB.toString());
			JSONObject Data = new JSONObject(fdOpeningNTB);
			JSONObject response = new JSONObject();
			response.put("Data", Data);
			data = response.toString();
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


	@RequestMapping(value = "/saveNomineesDetailsEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> saveNomineesDetailsEncy(@RequestBody String bm,
	                                                     @RequestHeader(name = "Accept", required = true) String accept,
	                                                     @RequestHeader(name = "Content-Type", required = true) String Content_Type,
	                                                     @RequestHeader(name = "MobileNo", required = true) String mobileNo,
	                                                     @RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
	                                                     @RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
	                                                     @RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
	                                                     @RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
	                                                     @RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
	                                                     @RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
	                                                     @RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
	                                                     @RequestHeader(name = "Lg-Code", required = false) String lgcode,
	                                                     @RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("saveNomineesDetailsEncy start");

		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);

		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			JSONObject encryptJSONObject = new JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);
			logger.debug("saveNomineesDetailsEncy request" + decryptContainerString);
			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			String applicationNo = jsonObject.getJSONObject("Data").getString("ApplicationNo");
			JSONObject nomineeDetails = jsonObject.getJSONObject("Data").getJSONObject("NomineesDetails");
			FdOpeningNTB fdOpening = fdopeningservice.fetchByApplicationNo(Long.parseLong(applicationNo));
			fdOpening.setNomineesDetails(nomineeDetails.toString());
			fdOpening.setIsNomineeUpdate("Y");

			otpservice.save(fdOpening);
			JSONObject response = new JSONObject();
			JSONObject Data = new JSONObject();
			Data.put("Success", "Data Saved Successfully");
			response.put("Data", Data);
			data = response.toString();
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

	@RequestMapping(value = "/saveAccountDetailsEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> saveAccountDetailsEncy(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "Lg-Code", required = false) String lgcode,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("saveAccountDetailsEncy start");

		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);

		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			JSONObject encryptJSONObject = new JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);
			logger.debug("saveAccountDetailsEncy request" + decryptContainerString);
			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			String applicationNo = jsonObject.getJSONObject("Data").getString("ApplicationNo");
			JSONObject accDetails = jsonObject.getJSONObject("Data").getJSONObject("AccountDetails");
			FdOpeningNTB fdOpening = fdopeningservice.fetchByApplicationNo(Long.parseLong(applicationNo));
			fdOpening.setAccountDetails(accDetails.toString());
			fdOpening.setDepositAmount(accDetails.optString("depositAmount", ""));
//			fdOpening.setTenure(accDetails.getString("tenureMonth"));
			fdOpening.setMaturityAmout(accDetails.optString("maturityAmount", ""));
			fdOpening.setMaturityDate(accDetails.optString("maturityDate", ""));
			fdOpening.setInterestEarned(accDetails.optString("interestEarned", ""));
			fdOpening.setFlowStaus("AD");

			String year = accDetails.optString("tenureYear", "0");
			if (year.isEmpty()) {
				year = "0";
			}
			String month = accDetails.optString("tenureMonth", "0");
			if (month.isEmpty()) {
				month = "0";
			}
			String day = accDetails.optString("tenureDay", "0");
			if (day.isEmpty()) {
				day = "0";
			}

			if (lgcode != null && !lgcode.isEmpty()) {
				fdOpening.setLgCode(lgcode);
			}

			fdOpening.setTenure(year + "Y" + month + "M" + day + "D");
			otpservice.save(fdOpening);
			JSONObject response = new JSONObject();
			JSONObject Data = new JSONObject();
			Data.put("Success", "Data Saved Successfully");
			response.put("Data", Data);
			data = response.toString();
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

	@RequestMapping(value = "/createWorkItemEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> createWorkItemEncy(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("createWorkItem start");
		logger.debug("createWorkItem request" + bm);
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
			JSONObject createWorkItem = fdopeningservice.createWorkItem(jsonObject, Header);
			System.out.println(createWorkItem);
			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (createWorkItem != null) {
				String Data2 = createWorkItem.getString("data");
				logger.debug("data2");
				JSONObject Data1 = new JSONObject(Data2);

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

	@RequestMapping(value = "/createOrderEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> createOrder(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "ApplicationNo", required = true) String applicationNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "Authorization", required = true) String Authorization,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("createOrder start");
		logger.debug("createOrder request" + bm);
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
			JSONObject createOrder = fdopeningservice.createOrder(jsonObject, Header);
			System.out.println(createOrder);
			HttpStatus h = HttpStatus.OK;
			if (createOrder != null) {
				String Data2 = createOrder.getString("data");
				logger.debug("data2");
				JSONObject Data1 = new JSONObject(Data2);

				if (Data1.has("Data")) {
					h = HttpStatus.OK;

				} else if (Data1.has("error")) {
					h = HttpStatus.BAD_REQUEST;

				}
				FdOpeningNTB fdOpening = fdopeningservice.fetchByApplicationNo(Long.parseLong(applicationNo));
				fdOpening.setIsPaymentDone("N");// createOrderEncy
				if (Data1.has("id"))
					;
				fdOpening.setOrderId(Data1.getString("Id"));
				fdOpening.setCreateOrderResp(Data1.toString());
				otpservice.save(fdOpening);
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

	@RequestMapping(value = "/createOrderETBEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> createOrderETBEncy(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "Authorization", required = true) String Authorization,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("createOrder start");
		logger.debug("createOrder request" + bm);
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
			JSONObject createOrder = fdopeningservice.createOrder(jsonObject, Header);
			System.out.println(createOrder);
			HttpStatus h = HttpStatus.OK;
			if (createOrder != null) {
				String Data2 = createOrder.getString("data");
				logger.debug("data2");
				JSONObject Data1 = new JSONObject(Data2);

				if (Data1.has("Data")) {
					h = HttpStatus.OK;

				} else if (Data1.has("error")) {
					h = HttpStatus.BAD_REQUEST;

				}
				FdOpening fdOpening = fdservice.fetchByMobNoAndSessionId(mobileNo, X_Session_ID);
				fdOpening.setIsPaymentDone("N");// createOrderETBEncy
				fdOpening.setCreateOrderResp(Data1.toString());
				otpservice.save(fdOpening);
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

	@RequestMapping(value = "/fetchOrderEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> fetchOrder(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "ApplicationNo", required = true) String applicationNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "Authorization", required = true) String Authorization,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("fetchOrder start");
		logger.debug("fetchOrder request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);
		Header.put("Authorization", Authorization);
		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			JSONObject fetchOrder = fdopeningservice.fetchOrder(jsonObject, Header);
			System.out.println(fetchOrder);
			HttpStatus h = HttpStatus.OK;
			if (fetchOrder != null) {
				String Data2 = fetchOrder.getString("data");
				logger.debug("data2");
				JSONObject Data1 = new JSONObject(Data2);

				String status = Data1.getString("status");
				FdOpeningNTB fdOpening = fdopeningservice.fetchByApplicationNo(Long.parseLong(applicationNo));
				if (status.equalsIgnoreCase("paid")) {
					h = HttpStatus.OK;
					fdOpening.setIsPaymentDone("Y");// fetchOrderEncy
				} else {
					h = HttpStatus.OK;
					fdOpening.setIsPaymentDone("N");// fetchOrderEncy
				}
				fdOpening.setPaymentDetails(Data1.toString());
				otpservice.save(fdOpening);
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

	@RequestMapping(value = "/fetchOrderETBEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> fetchOrderETBEncy(@RequestBody String bm,
			@RequestHeader(name = "Accept", required = true) String accept,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "Authorization", required = true) String Authorization,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("fetchOrder start");
		logger.debug("fetchOrder request" + bm);
		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);
		Header.put("Authorization", Authorization);
		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			JSONObject fetchOrder = fdopeningservice.fetchOrder(jsonObject, Header);
			System.out.println(fetchOrder);
			HttpStatus h = HttpStatus.OK;
			if (fetchOrder != null) {
				String Data2 = fetchOrder.getString("data");
				logger.debug("data2");
				JSONObject Data1 = new JSONObject(Data2);

				String status = Data1.getString("status");
				FdOpening fdOpening = fdservice.fetchByMobNoAndSessionId(mobileNo, X_Session_ID);
				if (status.equalsIgnoreCase("paid")) {
					h = HttpStatus.OK;
					fdOpening.setIsPaymentDone("Y");// fetchOrderETBEncy
				} else {
					h = HttpStatus.OK;
					fdOpening.setIsPaymentDone("N");// fetchOrderETBEncy
				}
				fdOpening.setPaymentDetails(Data1.toString());
				otpservice.save(fdOpening);
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
